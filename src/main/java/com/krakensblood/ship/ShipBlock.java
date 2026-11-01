package com.krakensblood.ship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3f;

import com.krakensblood.KrakensBloodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShipBlock {

    public BlockState state;
    public BlockPos localPos;
    public BlockPos currentWorldPos;
    public CompoundTag blockEntityData;
    public double health = 1.0;

    public ShipCollisionCallback collisionCallback;

    private final Set<BlockPos> previousBlockCollisions = new HashSet<>();
    private final Set<UUID> previousEntityCollisions = new HashSet<>();

    public ShipBlock(
        BlockState state,
        BlockPos localPos,
        CompoundTag blockEntityData
    ) {
        this.state = state;
        this.localPos = localPos.immutable();
        this.health = state.getDestroySpeed(null, null) > 0.0 ? 1.0 : 0.5;

        this.blockEntityData = blockEntityData == null
            ? null
            : blockEntityData.copy();
    }

    public boolean isHelm() {
        return state.is(KrakensBloodBlocks.SHIP_HELM_BLOCK);
    }

    public boolean isRudder() {
        return state.is(KrakensBloodBlocks.SHIP_RUDDER_BLOCK);
    }

    public boolean isSail() {
        return state.is(KrakensBloodBlocks.SHIP_SAIL_BLOCK);
    }

    public Direction getSailFacing() {
        if (!isSail()) {
            return Direction.NORTH;
        }

        if (state.hasProperty(ShipSailBlock.FACING)) {
            return state.getValue(ShipSailBlock.FACING);
        }

        return Direction.NORTH;
    }

    public void tick(Level level, ShipPeice ship) {
        AABB bounds = getWorldBounds(ship);

        Set<BlockPos> currentBlockCollisions = new HashSet<>();
        Set<UUID> currentEntityCollisions = new HashSet<>();

        int minX = (int) Math.floor(bounds.minX);
        int minY = (int) Math.floor(bounds.minY);
        int minZ = (int) Math.floor(bounds.minZ);

        int maxX = (int) Math.floor(bounds.maxX);
        int maxY = (int) Math.floor(bounds.maxY);
        int maxZ = (int) Math.floor(bounds.maxZ);

        Vec3 center = getWorldPosition(ship);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos.set(x, y, z);

                    BlockState hitState = level.getBlockState(pos);
                    FluidState fluid = level.getFluidState(pos);

                    if (hitState.isAir() && fluid.isEmpty()) {
                        continue;
                    }

                    boolean collided = false;
                    VoxelShape shape = hitState.getCollisionShape(level, pos);
                    for (AABB box : shape.toAabbs()) {
                        AABB worldBox = box.move(x, y, z);
                        if (bounds.intersects(worldBox)) {
                            collided = true;
                            break;
                        }
                    }

                    if (!fluid.isEmpty()) {
                        AABB fluidBox = new AABB(x, y, z, x + 1, y + 1, z + 1);
                        if (bounds.intersects(fluidBox)) {
                            collided = true;
                        }
                    }

                    if (!collided) {
                        continue;
                    }

                    BlockPos hitPos = pos.immutable();
                    currentBlockCollisions.add(hitPos);

                    if (!previousBlockCollisions.contains(hitPos)) {
                        Vec3 normal = center.subtract(new Vec3(x + 0.5, y + 0.5, z + 0.5));
                        if (normal.lengthSqr() > 0.0001) {
                            normal = normal.normalize();
                        } else {
                            normal = new Vec3(0, 1, 0);
                        }

                        onCollision(ship, hitPos, hitState, fluid, null, center, normal);
                    }
                }
            }
        }

        for (Entity entity : level.getEntities((Entity) null, bounds, e -> e.isAlive() && !e.isSpectator())) {
            UUID id = entity.getUUID();
            currentEntityCollisions.add(id);

            if (!previousEntityCollisions.contains(id)) {
                Vec3 normal = center.subtract(entity.position());
                if (normal.lengthSqr() > 0.0001) {
                    normal = normal.normalize();
                } else {
                    normal = new Vec3(0, 1, 0);
                }

                BlockPos entityPos = BlockPos.containing(entity.position());
                onCollision(ship, entityPos, level.getBlockState(entityPos), level.getFluidState(entityPos), entity, center, normal);
            }
        }

        previousBlockCollisions.clear();
        previousBlockCollisions.addAll(currentBlockCollisions);

        previousEntityCollisions.clear();
        previousEntityCollisions.addAll(currentEntityCollisions);
    }

    public Vec3 getWorldPosition(ShipPeice ship) {
        Vector3f local = new Vector3f(
            localPos.getX() + 0.5f,
            localPos.getY() + 0.5f,
            localPos.getZ() + 0.5f
        );

        ship.rotation.transform(local);

        return ship.position.add(
            local.x,
            local.y,
            local.z
        );
    }

    public AABB getWorldBounds(ShipPeice ship) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;

        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    Vector3f corner = new Vector3f(
                        localPos.getX() + x,
                        localPos.getY() + y,
                        localPos.getZ() + z
                    );

                    ship.rotation.transform(corner);

                    double wx = ship.position.x + corner.x;
                    double wy = ship.position.y + corner.y;
                    double wz = ship.position.z + corner.z;

                    minX = Math.min(minX, wx);
                    minY = Math.min(minY, wy);
                    minZ = Math.min(minZ, wz);

                    maxX = Math.max(maxX, wx);
                    maxY = Math.max(maxY, wy);
                    maxZ = Math.max(maxZ, wz);
                }
            }
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private void onCollision(
        ShipPeice ship,
        BlockPos worldPos,
        BlockState hitState,
        FluidState fluid,
        Entity entity,
        Vec3 hitPosition,
        Vec3 normal
    ) {
        if (collisionCallback != null) {
            collisionCallback.onCollision(this, worldPos, hitState, fluid, entity, hitPosition, normal);
        }

        if (fluid.isEmpty()) {
            ship.onCollision(this, hitPosition, normal, entity);
        }
    }

    public void damage(Level level, ShipPeice ship, double amount) {
        health -= amount;
        if (health <= 0.0) {
            if (ship.blocks.contains(this)) {
                ship.blocks.remove(this);
            }

            BlockPos dropPos = BlockPos.containing(ship.getBlockPosition(this));
            Block.popResource(level, dropPos, new ItemStack(state.getBlock()));
        }
    }
}