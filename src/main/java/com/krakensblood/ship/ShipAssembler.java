package com.krakensblood.ship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShipAssembler {
    private static final int MAX_STRUCTURE_SIZE = 2000;

    public static List<BlockPos> getConnectedBlocks(Level level, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        List<BlockPos> connectedBlocks = new ArrayList<>();

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && connectedBlocks.size() < MAX_STRUCTURE_SIZE) {
            BlockPos currentPos = queue.poll();
            BlockState state = level.getBlockState(currentPos);

            visited.add(currentPos);

            if (state.isAir() || state.getFluidState().isEmpty()) {
                continue;
            }

            connectedBlocks.add(currentPos);

            for (BlockPos neighbor : getAdjacentPositions(currentPos)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        return connectedBlocks;
    }

    private static List<BlockPos> getAdjacentPositions(BlockPos pos) {
        return List.of(
            pos.north(),
            pos.south(),
            pos.east(),
            pos.west(),
            pos.above(),
            pos.below()
        );
    }

    public static void SpawnShip(Level level, BlockPos startPos) {
        List<BlockPos> blocks = getConnectedBlocks(level, startPos);
        Ship ship = new Ship();

        for (BlockPos block : blocks) {
            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, block, level.getBlockState(block));
            fallingBlock.noPhysics = true;
            fallingBlock.setNoGravity(true);
            fallingBlock.time = 1;

            level.destroyBlock(block, false);
            level.addFreshEntity(fallingBlock);

            ship.blocks.add(fallingBlock);
        }
    }
}
