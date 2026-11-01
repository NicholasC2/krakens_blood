package com.krakensblood.ship;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShipPeice {

    public List<ShipBlock> blocks;

    public BlockPos helmPos;

    public Vec3 position;
    public Vec3 velocity = Vec3.ZERO;

    public Quaternionf rotation = new Quaternionf();

    public Vec3 angularVelocity = Vec3.ZERO;

    public double mass = 1.0;
    public double inertia = 1.0;
    public double floodedMass;

    public double waterDensity = 1000.0;
    public double gravity = 9.81;

    public double buoyancy = 0.85;
    public double waterDrag = 0.18;
    public double airDrag = 0.04;

    public Vec3 centerOfMass = Vec3.ZERO;
    public Vec3 centerOfBuoyancy = Vec3.ZERO;

    public double displacedWaterVolume;
    public double floodedVolume;
    public double enclosedAirVolume;
    public double rudderAngle;
    public double steeringInput;
    public Vec3 lastWind = Vec3.ZERO;
    public boolean active = true;

    public ShipPeice(List<ShipBlock> blocks, BlockPos helmPos) {
        this.blocks = blocks;
        this.helmPos = helmPos.immutable();
        this.position = new Vec3(
            helmPos.getX(),
            helmPos.getY(),
            helmPos.getZ()
        );
        recalculateMass();
    }

    public void recalculateMass() {
        double total = 0.0;

        for (ShipBlock block : blocks) {
            if (block == null || block.state == null || block.state.isAir()) {
                continue;
            }
            total += 1.0;
        }

        mass = Math.max(1.0, total);
        inertia = Math.max(1.0, mass * 1.75);
    }

    public Vec3 getBlockPosition(ShipBlock block) {
        Vector3f local = new Vector3f(
            block.localPos.getX() + 0.5f,
            block.localPos.getY() + 0.5f,
            block.localPos.getZ() + 0.5f
        );

        rotation.transform(local);

        return position.add(
            local.x,
            local.y,
            local.z
        );
    }

    public AABB getWorldAabb() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (ShipBlock block : blocks) {
            Vec3 center = getBlockPosition(block);
            minX = Math.min(minX, center.x - 0.6);
            minY = Math.min(minY, center.y - 0.6);
            minZ = Math.min(minZ, center.z - 0.6);
            maxX = Math.max(maxX, center.x + 0.6);
            maxY = Math.max(maxY, center.y + 0.6);
            maxZ = Math.max(maxZ, center.z + 0.6);
        }

        if (Double.isInfinite(minX)) {
            return new AABB(position, position);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void tick(Level level) {
        if (!active || blocks.isEmpty()) {
            return;
        }

        recalculateMass();
        updateBuoyancy(level);
        updateWindAndSteering(level);

        for (ShipBlock block : new ArrayList<>(blocks)) {
            block.tick(level, this);
        }

        position = position.add(velocity);

        double speed = angularVelocity.length();
        if (speed > 0.00001) {
            Vector3f axis = new Vector3f(
                (float) angularVelocity.x,
                (float) angularVelocity.y,
                (float) angularVelocity.z
            ).normalize();

            rotation.mul(
                new Quaternionf().rotationAxis(
                    (float) speed,
                    axis
                )
            ).normalize();
        }

        if (velocity.lengthSqr() > 0.0001) {
            velocity = velocity.scale(1.0 - airDrag);
            if (velocity.lengthSqr() < 0.0000001) {
                velocity = Vec3.ZERO;
            }
        }

        if (Math.abs(angularVelocity.y) > 0.000001) {
            angularVelocity = angularVelocity.scale(1.0 - 0.06);
        }

        syncBlocksToWorld(level);
        collideWithOtherShips(level);
        applyStandingPlayers(level);
    }

    public void syncBlocksToWorld(Level level) {
        for (ShipBlock block : blocks) {
            if (block == null || block.state == null || block.state.isAir()) {
                continue;
            }

            BlockPos targetPos = BlockPos.containing(getBlockPosition(block));
            if (block.currentWorldPos != null && block.currentWorldPos.equals(targetPos)) {
                continue;
            }

            if (block.currentWorldPos != null) {
                BlockState oldState = level.getBlockState(block.currentWorldPos);
                if (oldState.is(block.state.getBlock()) || oldState.isAir()) {
                    level.setBlock(block.currentWorldPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }

            if (level.getBlockState(targetPos).isAir() || level.getBlockState(targetPos).is(block.state.getBlock())) {
                level.setBlock(targetPos, block.state, Block.UPDATE_ALL);
            }

            block.currentWorldPos = targetPos;
        }
    }

    private void updateBuoyancy(Level level) {
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (ShipBlock block : blocks) {
            if (block == null || block.state.isAir()) {
                continue;
            }
            Vec3 blockPos = getBlockPosition(block);
            minY = Math.min(minY, blockPos.y);
            maxY = Math.max(maxY, blockPos.y);
        }

        if (Double.isInfinite(minY) || Double.isInfinite(maxY)) {
            return;
        }

        double hullHeight = Math.max(1.0, maxY - minY + 1.0);
        double localSeaLevel = level.getSeaLevel();
        double draft = Math.max(0.0, localSeaLevel - (minY + hullHeight * 0.5));
        double submergedFraction = Mth.clamp(draft / Math.max(1.0, hullHeight), 0.0, 1.0);

        if (submergedFraction > 0.0) {
            double liftForce = submergedFraction * waterDensity * gravity * buoyancy * Math.max(1.0, blocks.size() / 4.0);
            velocity = velocity.add(0.0, ((liftForce / mass) - gravity * 0.2), 0.0);
        } else {
            velocity = velocity.add(0.0, -gravity * 0.16, 0.0);
        }
    }

    private void updateWindAndSteering(Level level) {
        Vec3 wind = getWindVector(level);
        lastWind = wind;

        if (Math.abs(steeringInput) > 0.0001) {
            angularVelocity = angularVelocity.add(0.0, steeringInput * 0.015, 0.0);
            steeringInput *= 0.86;
        } else {
            rudderAngle *= 0.88;
        }

        Vector3f forwardLocal = new Vector3f(0.0f, 0.0f, 1.0f);
        rotation.transform(forwardLocal);
        Vec3 forward = new Vec3(forwardLocal.x, forwardLocal.y, forwardLocal.z).normalize();

        double sailPull = 0.0;
        for (ShipBlock block : blocks) {
            if (!block.isSail()) {
                continue;
            }

            Direction facing = block.getSailFacing();
            Vector3f normalLocal = new Vector3f(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
            );
            rotation.transform(normalLocal);
            Vec3 normal = new Vec3(normalLocal.x, normalLocal.y, normalLocal.z);

            double windAlignment = Math.max(0.0, wind.dot(normal));
            if (windAlignment > 0.0) {
                sailPull += windAlignment * 0.02;
            }
        }

        if (sailPull > 0.0) {
            Vec3 pull = forward.scale(sailPull);
            velocity = velocity.add(pull.scale(1.0 / Math.max(1.0, mass)));
        }
    }

    public Vec3 getWindVector(Level level) {
        long time = level.getGameTime() % 24000L;
        double cyc = (time / 24000.0) * (Math.PI * 2.0) + (Math.PI * 0.5);
        double weatherBoost = level.isRaining() ? 0.8 : (level.isThundering() ? 1.2 : 0.35);
        double magnitude = 0.02 + weatherBoost * 0.025;

        double x = Math.cos(cyc) * magnitude;
        double z = Math.sin(cyc) * magnitude;

        return new Vec3(x, 0.0, z);
    }

    public void applyStandingPlayers(Level level) {
        AABB shipBox = getWorldAabb().inflate(0.25);

        for (var player : level.players()) {
            if (player == null || !player.isAlive()) {
                continue;
            }

            if (!shipBox.intersects(player.getBoundingBox())) {
                continue;
            }

            double top = shipBox.maxY + 0.05;
            if (player.getY() < top - 1.1) {
                player.setDeltaMovement(player.getDeltaMovement().add(velocity.x * 0.15, 0.0, velocity.z * 0.15));
            }

            player.setPos(
                player.getX(),
                Math.min(player.getY(), top - 1.0),
                player.getZ()
            );
            player.fallDistance = 0.0F;
        }
    }

    public void collideWithOtherShips(Level level) {
        for (ShipPeice other : ShipManager.getShips()) {
            if (other == this || !other.active) {
                continue;
            }

            AABB thisBox = getWorldAabb();
            AABB otherBox = other.getWorldAabb();
            if (!thisBox.intersects(otherBox)) {
                continue;
            }

            Vec3 normal = new Vec3(
                position.x - other.position.x,
                0.0,
                position.z - other.position.z
            );

            double length = normal.length();
            if (length < 0.0001) {
                normal = new Vec3(1.0, 0.0, 0.0);
            } else {
                normal = normal.scale(1.0 / length);
            }

            double impact = Math.max(0.0, (velocity.length() + other.velocity.length()) * 0.5);
            velocity = velocity.add(normal.scale(impact * 0.02));
            other.velocity = other.velocity.add(normal.scale(-impact * 0.02));
        }
    }

    public void onCollision(
        ShipBlock block,
        Vec3 hitPosition,
        Vec3 normal,
        Entity entity
    ) {
        Vec3 r = hitPosition.subtract(position);

        Vec3 pointVelocity = velocity.add(
            angularVelocity.cross(r)
        );

        double normalVelocity = pointVelocity.dot(normal);

        if (normalVelocity >= 0) {
            return;
        }

        double effectiveMass =
            (1.0 / mass) +
            r.cross(normal).lengthSqr() / inertia;

        double impulseMagnitude =
            -(1.0 + 0.2) * normalVelocity / Math.max(0.0001, effectiveMass);

        Vec3 impulse = normal.scale(impulseMagnitude);

        velocity = velocity.add(impulse.scale(1.0 / mass));

        angularVelocity = angularVelocity.add(
            r.cross(impulse).scale(1.0 / inertia)
        );
    }

    public void damageBlock(Level level, ShipBlock block, double amount) {
        if (block == null || !blocks.contains(block)) {
            return;
        }

        block.health -= amount;
        if (block.health <= 0.0) {
            destroyBlock(level, block);
        }
    }

    public void destroyBlock(Level level, ShipBlock block) {
        if (block == null || !blocks.remove(block)) {
            return;
        }

        if (block.currentWorldPos != null) {
            level.setBlock(block.currentWorldPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        BlockPos dropPos = BlockPos.containing(getBlockPosition(block));
        Block.popResource(level, dropPos, block.state.getBlock().asItem().getDefaultInstance());

        if (blocks.isEmpty()) {
            ShipManager.removeShip(this);
            active = false;
        }
    }
}