package com.krakensblood.ship;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ShipHelmBlock extends Block {
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

    public ShipHelmBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void onPlace(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState oldState,
        boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide() || oldState.is(this)) {
            return;
        }

        if (ShipManager.hasShipAtHelm(pos)) {
            return;
        }

        List<BlockPos> connected = ShipAssembler.getConnectedBlocks(level, pos);
        if (connected.isEmpty()) {
            return;
        }

        for (BlockPos worldPos : connected) {
            if (ShipManager.findShipAtPos(level, worldPos) != null) {
                return;
            }
        }

        List<ShipBlock> shipBlocks = new ArrayList<>();
        for (BlockPos worldPos : connected) {
            BlockState blockState = level.getBlockState(worldPos);
            if (blockState.isAir()) {
                continue;
            }

            var blockEntity = level.getBlockEntity(worldPos);
            var nbt = blockEntity != null
                ? blockEntity.saveWithFullMetadata(level.registryAccess()).copy()
                : null;

            BlockPos localPos = worldPos.subtract(pos);
            shipBlocks.add(new ShipBlock(blockState, localPos, nbt));
        }

        if (shipBlocks.isEmpty()) {
            return;
        }

        ShipPeice ship = new ShipPeice(shipBlocks, pos);
        ship.position = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        ShipManager.addShip(ship);
    }

    @Override
    public InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ShipPeice ship = ShipManager.findShipAtPos(level, pos);
        if (ship == null) {
            return InteractionResult.PASS;
        }

        double steer = player.isShiftKeyDown() ? -0.22 : 0.22;
        ship.steeringInput = steer;
        ship.rudderAngle = steer;
        ship.angularVelocity = ship.angularVelocity.add(0.0, steer * 0.1, 0.0);
        return InteractionResult.SUCCESS;
    }
}