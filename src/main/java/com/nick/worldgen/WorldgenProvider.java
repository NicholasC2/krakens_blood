package com.nick.worldgen;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

public class WorldgenProvider extends FabricDynamicRegistryProvider {
	public WorldgenProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void configure(HolderLookup.Provider registries, @NonNull Entries entries) {
		entries.addAll(registries.lookupOrThrow(Registries.BIOME));
	}

	@Override
	public String getName() {
		return "Biomes";
	}
}