package com.krakensblood.ship;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface ShipCollisionCallback {
    void onCollision(
        ShipBlock shipBlock,
        BlockPos worldPos,
        BlockState hitState,
        FluidState fluidState,
        Entity hitEntity,
        Vec3 hitPosition,
        Vec3 hitNormal
    );
}