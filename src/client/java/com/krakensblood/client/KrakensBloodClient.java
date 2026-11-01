
package com.krakensblood.client;

import com.krakensblood.ship.ShipBlock;
import com.krakensblood.ship.ShipManager;
import com.krakensblood.ship.ShipPeice;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

public class KrakensBloodClient implements ClientModInitializer {
	private BlockModelResolver blockModelResolver;

    @Override
    public void onInitializeClient() {
        LevelRenderEvents.COLLECT_SUBMITS.register(this::renderShips);
    }

    private void renderShips(LevelRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        Vec3 camera = context.levelState().cameraRenderState.pos;

        var matrices = context.poseStack();

        var collector = context.submitNodeCollector();

        for (ShipPeice ship : new java.util.ArrayList<>(ShipManager.getShips())) {
            matrices.pushPose();

            matrices.translate(
                ship.position.x - camera.x,
                ship.position.y - camera.y,
                ship.position.z - camera.z
            );

            matrices.mulPose(new Matrix4f().rotation(ship.rotation));

            for (ShipBlock block : ship.blocks) {
                if (block.state.isAir()) {
                    continue;
                }

                matrices.pushPose();

                Vec3 renderPos = ship.getBlockPosition(block);
                matrices.translate(
                    renderPos.x - ship.position.x,
                    renderPos.y - ship.position.y,
                    renderPos.z - ship.position.z
                );

                BlockModelRenderState renderState = new BlockModelRenderState();

                if (blockModelResolver == null) {
                    blockModelResolver = new BlockModelResolver(
                        minecraft.getModelManager()
                    );
                }

                blockModelResolver.update(
                    renderState,
                    block.state,
                    BlockDisplayContext.create()
                );

                renderState.submit(
                    matrices,
                    collector,
                    15728880,
                    0,
                    0
                );

                matrices.popPose();
            }

            matrices.popPose();
        }
    }
}
