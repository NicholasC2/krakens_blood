package com.krakensblood.items;

import java.util.List;
import java.util.function.Predicate;

import com.krakensblood.KrakensBlood;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

public class Quest extends Item {
    public static final ResourceKey<Item> KEY = ResourceKey.create(
        BuiltInRegistries.ITEM.key(),
        KrakensBlood.id("quest")
    );

    public Quest(Properties properties) {
        super(properties);
    }

    public static Quest register() {
        return Registry.register(
            BuiltInRegistries.ITEM,
            KEY,
            new Quest(new Item.Properties().setId(KEY).stacksTo(1))
        );
    }

    public ItemStack create(
        String name,
        String description,
        Predicate<ServerPlayer> check
    ) {
        ItemStack stack = new ItemStack(this);

        stack.set(
            DataComponents.LORE,
            new ItemLore(List.of(
                Component.literal(name).withStyle(ChatFormatting.LIGHT_PURPLE),
                Component.literal(description).withStyle(ChatFormatting.GRAY)
            ))
        );

        return stack;
    }
}