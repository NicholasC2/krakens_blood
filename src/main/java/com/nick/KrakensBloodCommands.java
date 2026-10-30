package com.nick;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class KrakensBloodCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(
                Commands.literal("krakens_blood")
                    .then(Commands.literal("quests")
                        .then(Commands.literal("give")
                            .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("description", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");
                                        String description = StringArgumentType.getString(context, "description");

                                        ServerPlayer player = context.getSource().getPlayer();

                                        if (player != null) {
                                            player.getInventory().add(
                                                KrakensBlood.QUEST.create(
                                                    name,
                                                    description,
                                                    player1 -> false
                                                )
                                            );
                                        }

                                        return Command.SINGLE_SUCCESS;
                                    })
                                )
                            )
                        )
                    )
            )
        );
    }
}