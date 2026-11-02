package com.krakensblood;

import java.util.Set;

import com.krakensblood.ship.ShipHelmBlock;
import com.krakensblood.ship.dock.DockBlock;
import com.krakensblood.ship.dock.DockBlockEntity;

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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class KrakensBloodBlocks {
    private static final Identifier SHIP_HELM_ID = KrakensBlood.id("ship_helm");

    public static final Block SHIP_HELM_BLOCK = Registry.register(
        BuiltInRegistries.BLOCK,
        SHIP_HELM_ID,
        new ShipHelmBlock(Properties.of().setId(ResourceKey.create(Registries.BLOCK, SHIP_HELM_ID)).strength(3.0f))
    );

    public static final Item SHIP_HELM_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        SHIP_HELM_ID,
        new BlockItem(SHIP_HELM_BLOCK, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, SHIP_HELM_ID)).stacksTo(1))
    );

    private static final Identifier DOCK_ID = KrakensBlood.id("dock");

    public static final Block DOCK_BLOCK = Registry.register(
        BuiltInRegistries.BLOCK,
        DOCK_ID,
        new DockBlock(
            Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, DOCK_ID))
                .strength(3.0f)
        )
    );

    public static final Item DOCK_ITEM = Registry.register(
        BuiltInRegistries.ITEM,
        DOCK_ID,
        new BlockItem(
            DOCK_BLOCK,
            new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, DOCK_ID))
                .stacksTo(64)
        )
    );

    
    public static final BlockEntityType<DockBlockEntity> DOCK_ENTITY_TYPE = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        KrakensBlood.id("dock"),
        new BlockEntityType<>(
            DockBlockEntity::new,
            Set.of(DOCK_BLOCK)
        )
    );

    public static final ResourceKey<CreativeModeTab> KRAKENS_BLOOD_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, KrakensBlood.id("krakens_blood_tab"));

    public static final CreativeModeTab KRAKENS_BLOOD_TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        KRAKENS_BLOOD_TAB_KEY,
        FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(SHIP_HELM_ITEM))
            .title(Component.translatable("itemGroup.krakensblood.tab"))
            .displayItems((parameters, output) -> {
                output.accept(SHIP_HELM_ITEM);
                output.accept(DOCK_ITEM);
            })
            .build()
    );

    public static void register() {
        KrakensBlood.LOGGER.info("Registering blocks for Krakens Blood");
    }
}
