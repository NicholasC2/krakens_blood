
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
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.biome.Climate.TargetPoint;

public final class IslandsBiomeSource extends BiomeSource {

    public static final MapCodec<IslandsBiomeSource> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.CODEC.fieldOf("ocean_biome")
                .forGetter(s -> s.ocean),
            Biome.CODEC.fieldOf("jungle_biome")
                .forGetter(s -> s.jungle),
            Biome.CODEC.fieldOf("mountain_biome")
                .forGetter(s -> s.mountain)
        ).apply(instance, IslandsBiomeSource::new));

    private final Holder<Biome> ocean;
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
        Holder<Biome> ocean,
        Holder<Biome> jungle,
        Holder<Biome> mountain
    ) {
        this.ocean = ocean;
        this.jungle = jungle;
        this.mountain = mountain;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(ocean, jungle, mountain).distinct();
    }

    @Override
    public BiomeResolver createResolver(Sampler sampler) {
        return (x, y, z) -> {
            float depth = Climate.unquantizeCoord(sampler.sample(x, y, z).continentalness());

            if (depth < 153) return ocean;
            if (depth < 200) return jungle;

            return mountain;
        };
    }
}
