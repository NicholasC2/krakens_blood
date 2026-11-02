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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Ship extends Entity {
    public Map<BlockPos, BlockState> blocks;
    public Map<BlockPos, CompoundTag> blockEntities;

    public Ship(EntityType<? extends Ship> type, Level level) {
        super(type, level);
        this.blocks = new HashMap<>();
        this.blockEntities = new HashMap<>();
    }

    public Ship(
        EntityType<? extends Ship> type,
        Level level,
        Map<BlockPos, BlockState> blocks,
        Map<BlockPos, CompoundTag> blockEntities
    ) {
        super(type, level);
        this.blocks = blocks;
        this.blockEntities = blockEntities;
    }

    public void removeFromWorld() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (Map.Entry<BlockPos, BlockState> block : blocks.entrySet()) {
                BlockPos pos = block.getKey();
                serverLevel.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel) {
            for (Map.Entry<BlockPos, BlockState> block : blocks.entrySet()) {
                BlockPos pos = block.getKey();
                BlockState state = block.getValue();
                state.tick(serverLevel, pos, serverLevel.getRandom());
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // TODO: You will need to serialize your 'blocks' and 'blockEntities' maps into NBT here
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        // TODO: You will need to deserialize your 'blocks' and 'blockEntities' maps from NBT here
    }

    @Override
    protected void defineSynchedData(Builder entityData) {
        
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }
}
