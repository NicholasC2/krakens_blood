package com.nick.worldgen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public class IslandsChunkGenerator extends ChunkGenerator {
    public static final MapCodec<IslandsChunkGenerator> CODEC = BiomeSource.CODEC.fieldOf("biome_source").xmap(
        IslandsChunkGenerator::new, 
        IslandsChunkGenerator::getBiomeSource
    );

    public IslandsChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    public void addDebugScreenInfo(
        @NonNull List<String> result, 
        @NonNull RandomState randomState, 
        @NonNull BlockPos feetPos, 
        @NonNull SamplerContext samplerContext
    ) {
        throw new UnsupportedOperationException("Unimplemented method 'addDebugScreenInfo'");
    }

    @Override
    public @NonNull CompletableFuture<ChunkAccess> buildTerrain(
        @NonNull ChunkAccess arg0, 
        @NonNull Blender arg1, 
        @NonNull RandomState arg2, 
        @NonNull StructureManager arg3, 
        @NonNull BiomeManager arg4, 
        @Nullable WorldGenRegion arg5, 
        @NonNull Set<Holder<Biome>> arg6
    ) {
        throw new UnsupportedOperationException("Unimplemented method 'buildTerrain'");
    }

    @Override
    protected @NonNull MapCodec<? extends ChunkGenerator> codec() {
        if(CODEC == null) throw new Error("CODEC is null");
        return CODEC;
    }

    @Override
    public @NonNull NoiseColumn getBaseColumn(int x, int z, @NonNull LevelHeightAccessor heightAccessor, @NonNull RandomState randomState) {
        throw new UnsupportedOperationException("Unimplemented method 'getBaseColumn'");
    }

    @Override
    public int getBaseHeight(int x, int z, @NonNull Types type, @NonNull LevelHeightAccessor heightAccessor, @NonNull RandomState randomState) {
        throw new UnsupportedOperationException("Unimplemented method 'getBaseHeight'");
    }

    @Override
    public int getGenDepth() {
        throw new UnsupportedOperationException("Unimplemented method 'getGenDepth'");
    }

    @Override
    public int getMinY() {
        throw new UnsupportedOperationException("Unimplemented method 'getMinY'");
    }

    @Override
    public int getSeaLevel() {
        throw new UnsupportedOperationException("Unimplemented method 'getSeaLevel'");
    }

    @Override
    public void spawnOriginalMobs(@NonNull WorldGenRegion worldGenRegion) {
        throw new UnsupportedOperationException("Unimplemented method 'spawnOriginalMobs'");
    }
}