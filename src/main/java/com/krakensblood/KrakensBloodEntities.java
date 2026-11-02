package com.krakensblood;

import com.krakensblood.ship.Ship;
import com.krakensblood.ship.dock.DockBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.EntityFactory;

public class KrakensBloodEntities {
    private static final Identifier SHIP_ID = KrakensBlood.id("ship");

    public static final EntityType<Ship> SHIP_ENTITY = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        SHIP_ID,
        EntityType.Builder.createNothing(MobCategory.WATER_AMBIENT).build(new ResourceKey<EntityType<Ship>>(Identifier.E, SHIP_ID))
    );

    public static void register() {
        KrakensBlood.LOGGER.info("Registering blocks for Krakens Blood");
    }
}
