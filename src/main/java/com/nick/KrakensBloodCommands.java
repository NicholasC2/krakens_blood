package com.nick;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;

public class KrakensBloodCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> 
			dispatcher.register(Commands.literal("krakens_blood")
				.then(Commands.literal("quests").executes(context -> {return 1;})))
		);
    }
}
