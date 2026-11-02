package com.krakensblood.ship.dock;

import com.krakensblood.KrakensBloodBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DockBlockEntity extends BlockEntity {
    private BlockPos offset = BlockPos.ZERO;
    private BlockPos size = new BlockPos(10, 10, 10);

    public DockBlockEntity(BlockPos pos, BlockState state) {
        super(KrakensBloodBlocks.DOCK_ENTITY_TYPE, pos, state);
    }

    public BlockPos getOffset() {
        return offset;
    }

    public BlockPos getSize() {
        return size;
    }

    public void setOffset(BlockPos offset) {
        this.offset = offset.immutable();
        setChanged();
    }

    public void setSize(BlockPos size) {
        this.size = size;
        setChanged();
    }
}