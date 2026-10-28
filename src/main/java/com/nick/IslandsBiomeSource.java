
package com.nick;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;

public final class IslandsBiomeSource extends BiomeSource {

    public static final MapCodec<IslandsBiomeSource> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.CODEC.fieldOf("cold_ocean_biome")
                .forGetter(s -> s.coldOcean),
            Biome.CODEC.fieldOf("ocean_biome")
                .forGetter(s -> s.ocean),
            Biome.CODEC.fieldOf("warm_ocean_biome")
                .forGetter(s -> s.warmOcean),
            Biome.CODEC.fieldOf("jungle_biome")
                .forGetter(s -> s.jungle),
            Biome.CODEC.fieldOf("mountain_biome")
                .forGetter(s -> s.mountain)
        ).apply(instance, IslandsBiomeSource::new));

    private final Holder<Biome> coldOcean;
    private final Holder<Biome> ocean;
    private final Holder<Biome> warmOcean;
    private final Holder<Biome> jungle;
    private final Holder<Biome> mountain;

    public static void register() {
        Registry.register(
            BuiltInRegistries.BIOME_SOURCE,
            KrakensBlood.id("islands"),
            CODEC
        );
    }

    public IslandsBiomeSource(
        Holder<Biome> coldOcean,
        Holder<Biome> ocean,
        Holder<Biome> warmOcean,
        Holder<Biome> jungle,
        Holder<Biome> mountain
    ) {
        this.coldOcean = coldOcean;
        this.ocean = ocean;
        this.warmOcean = warmOcean;
        this.jungle = jungle;
        this.mountain = mountain;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(coldOcean, ocean, warmOcean, jungle, mountain)
            .distinct();
    }

    @Override
    public BiomeResolver createResolver(Sampler sampler) {
        return (x, y, z) -> {
            int blockY = y * 4;

            if (blockY < 100) {
                return coldOcean;
            }

            if (blockY < 130) {
                return ocean;
            }

            if (blockY < 153) {
                return warmOcean;
            }

            if (blockY < 200) {
                return jungle;
            }

            return mountain;
        };
    }
}
