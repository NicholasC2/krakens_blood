package com.krakensblood.ship.dock;

import com.krakensblood.KrakensBloodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DockBlockEntity extends BlockEntity {
    public DockBlockEntity(BlockPos pos, BlockState state) {
        super(KrakensBloodBlocks.DOCK_ENTITY_TYPE, pos, state);
    }
}