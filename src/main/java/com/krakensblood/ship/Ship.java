package com.krakensblood.ship;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class Ship {
    public Map<BlockPos, BlockState> blocks = new HashMap<>();
    public Map<BlockPos, CompoundTag> blockEntities = new HashMap<>();

    public ServerLevel level;

    public Ship(Map<BlockPos, BlockState> blocks, Map<BlockPos, CompoundTag> blockEntites, ServerLevel level) {
        this.blocks = blocks;
        this.blockEntities = blockEntites;
        
        this.level = level;
    }

    public void removeFromWorld() {
        for (Map.Entry<BlockPos, BlockState> block : blocks.entrySet()) {
            BlockPos pos = block.getKey();
    
            level.removeBlock(pos, false);
        }
    }

    public void tick() {
        for (Map.Entry<BlockPos, BlockState> block : blocks.entrySet()) {
            BlockPos pos = block.getKey();
            BlockState state = block.getValue();

            state.tick(level, pos, level.getRandom());
        }
    }
}
