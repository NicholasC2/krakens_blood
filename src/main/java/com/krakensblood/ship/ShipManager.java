package com.krakensblood.ship;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ShipManager {

    private static final List<ShipPeice> SHIPS = new ArrayList<>();

    public static void addShip(ShipPeice ship) {
        if (ship == null || ship.blocks.isEmpty()) {
            return;
        }

        for (ShipPeice existing : SHIPS) {
            if (existing == ship || existing.helmPos.equals(ship.helmPos)) {
                return;
            }
        }

        SHIPS.add(ship);

        System.out.println(
            "[Kraken's Blood] Created ship with "
            + ship.blocks.size() + " blocks"
        );
    }

    public static void tick(ServerLevel level) {
        for (ShipPeice ship : new ArrayList<>(SHIPS)) {
            if (ship == null || !ship.active) {
                continue;
            }
            ship.tick(level);
        }
    }

    public static List<ShipPeice> getShips() {
        return new ArrayList<>(SHIPS);
    }

    public static ShipPeice findShipAtPos(Level level, BlockPos pos) {
        for (ShipPeice ship : new ArrayList<>(SHIPS)) {
            if (ship == null || !ship.active) {
                continue;
            }

            AABB shipBox = ship.getWorldAabb();
            if (shipBox.contains(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) {
                return ship;
            }
        }

        return null;
    }

    public static boolean hasShipAtHelm(BlockPos helmPos) {
        for (ShipPeice ship : SHIPS) {
            if (ship != null && ship.helmPos.equals(helmPos)) {
                return true;
            }
        }

        return false;
    }

    public static void removeShip(ShipPeice ship) {
        SHIPS.remove(ship);
    }
}