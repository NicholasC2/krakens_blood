package com.nick;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrakensBlood implements ModInitializer, DataGeneratorEntrypoint {
	public static final String MOD_ID = "krakens_blood";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Pack pack;

	@Override
	public void onInitialize() {
		
	}

	@Override
	public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
		pack = fabricDataGenerator.createPack();

		pack.addProvider(KrakensBloodWorldgenProvider::new);
	}

	@Override
	public void buildRegistry(@NonNull RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.BIOME, KrakensBloodWorldBiomes::configure);
	}

	public static Identifier id(@NonNull String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
