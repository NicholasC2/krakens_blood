package com.krakensblood;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krakensblood.items.Quest;
import com.krakensblood.ship.ShipManager;

public class KrakensBlood implements ModInitializer {
	public static final String MOD_ID = "krakensblood";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Quest QUEST = Quest.register();

	@Override
	public void onInitialize() {
		KrakensBloodCommands.register();
		KrakensBloodBlocks.register();

		ServerTickEvents.END_LEVEL_TICK.register(level -> {
			ShipManager.tick(level);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
