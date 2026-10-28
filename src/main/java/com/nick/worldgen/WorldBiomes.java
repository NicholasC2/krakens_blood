package com.nick.worldgen;

import com.nick.KrakensBlood;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class WorldBiomes {
    public static void configure(BootstrapContext<Biome> context) {
        context.register(ResourceKey.create(
            Registries.BIOME,
            KrakensBlood.id("jungle_island")
        ), createKrakenBiome());
    }

    private static Biome createKrakenBiome() {
        return new Biome.BiomeBuilder()
            .build();
    }
}
