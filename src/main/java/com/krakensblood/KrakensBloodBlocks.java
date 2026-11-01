package com.krakensblood;

import com.krakensblood.ship.ShipHelmBlock;
import com.krakensblood.ship.ShipRudderBlock;
import com.krakensblood.ship.ShipSailBlock;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class KrakensBloodBlocks {
    public static final ResourceKey<CreativeModeTab> KRAKENS_BLOOD_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, KrakensBlood.id("krakens_blood_tab"));

    public static final CreativeModeTab KRAKENS_BLOOD_TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        KRAKENS_BLOOD_TAB_KEY,
        FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(SHIP_HELM_ITEM))
            .title(Component.translatable("itemGroup.krakensblood.tab"))
            .displayItems((parameters, output) -> {
                output.accept(SHIP_HELM_ITEM);
            })
            .build()
    );

    public static void register() {
        KrakensBlood.LOGGER.info("Registering blocks for Krakens Blood");
    }
}
