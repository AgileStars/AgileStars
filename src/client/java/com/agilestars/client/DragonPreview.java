package com.agilestars.client;

import com.agilestars.client.model.DragonHeadModel;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Route-A step 1: draw the dragon head geometry on the HUD.
 *
 * This deliberately avoids world/entity integration so that the geometry
 * pipeline (raw mesh -> VertexConsumer, with the bone transform) can be
 * verified on its own before anything is wired to the ender dragon.
 */
public class DragonPreview implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null || mc.player == null) {
				return;
			}
			DragonHeadModel model = DragonHeadModel.get();
			PoseStack pose = context.getMatrices();
			MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

			pose.pushPose();
			// centre-ish of the screen, in GUI space
			pose.translate(context.getGuiScaledWidth() / 2.0F, context.getGuiScaledHeight() / 2.0F, 0.0F);
			pose.scale(60.0F, -60.0F, 60.0F);

			model.render(pose, buffers.getBuffer(DragonHeadModel.renderType()),
					0xF000F0, 0);

			pose.popPose();
			buffers.endBatch();
		});
	}
}
