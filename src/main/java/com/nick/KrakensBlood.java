package com.nick;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrakensBlood implements ModInitializer {
	public static final String MOD_ID = "krakens_blood";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

	}

	public static Identifier id(@NonNull String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
