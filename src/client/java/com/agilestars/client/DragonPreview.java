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
 * Deliberately avoids world/entity integration so the geometry pipeline
 * (raw mesh -> VertexConsumer, with the bone transform) can be verified alone.
 *
 * API note: GuiGraphics has no getMatrices()/getGuiScaledWidth() in 1.21.1, so
 * the screen size comes from Window (stable since 1.16) and the pose stack is
 * obtained defensively. Any failure is logged once instead of spamming.
 */
public class DragonPreview implements ClientModInitializer {

	private static boolean failed = false;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
			Minecraft mc = Minecraft.getInstance();
			if (failed || mc.level == null || mc.player == null) {
				return;
			}
			try {
				int width = mc.getWindow().getGuiScaledWidth();
				int height = mc.getWindow().getGuiScaledHeight();

				DragonHeadModel model = DragonHeadModel.get();
				PoseStack pose = graphics.pose();
				MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

				pose.pushPose();
				pose.translate(width / 2.0F, height / 2.0F, 0.0F);
				pose.scale(60.0F, -60.0F, 60.0F);

				model.render(pose, buffers.getBuffer(DragonHeadModel.renderType()), 0xF000F0, 0);

				pose.popPose();
				buffers.endBatch();
			} catch (Throwable t) {
				failed = true;
				com.agilestars.AgileStars.LOGGER.error(
						"dragon HUD preview disabled after an error", t);
			}
		});
	}
}
