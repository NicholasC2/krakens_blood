package com.krakensblood.ship;

import java.util.List;

import net.minecraft.world.entity.item.FallingBlockEntity;

public class Ship {
    public List<FallingBlockEntity> blocks;

    public Ship(List<FallingBlockEntity> blocks) {
        this.blocks = blocks;
    }

    public Ship() {
        this.blocks = List.of();
    }
}
