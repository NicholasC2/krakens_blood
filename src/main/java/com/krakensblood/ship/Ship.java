package com.krakensblood.ship;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Ship extends Entity {
    public Map<BlockPos, BlockState> blocks;
    public Map<BlockPos, CompoundTag> blockEntities;

    public ServerLevel level;

    public Ship(
        EntityType<?> type,
        ServerLevel level,
        Map<BlockPos, BlockState> blocks,
        Map<BlockPos, CompoundTag> blockEntities
    ) {
        super(type, level);

        this.blocks = blocks;
        this.blockEntities = blockEntities;
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

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAdditionalSaveData'");
    }

    @Override
    protected void defineSynchedData(Builder entityData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'defineSynchedData'");
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hurtServer'");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readAdditionalSaveData'");
    }
}
