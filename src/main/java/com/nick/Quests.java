package com.nick;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Quests {
    public static final ItemStack TEST_QUEST = KrakensBlood.QUEST.create(
        "Stone Collector",
        "Collect 5 stones",
        (player) -> {
            return player.getInventory().countItem(Items.STONE) >= 5;
        }
    );
}