package com.agilestars.client.mixin;

import com.agilestars.client.model.DragonHeadModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Route-A step 2: draw the custom dragon geometry where the ender dragon is.
 *
 * EnderDragonRenderer is not a LivingEntityRenderer and its DragonModel is a
 * private inner class, so the model cannot simply be swapped. Instead this
 * injects into the renderer's own render method and draws our raw mesh geometry
 * with the same pose stack the vanilla model would have used.
 *
 * Step 2 draws the head geometry on top of the vanilla dragon; suppressing the
 * vanilla model comes next.
 */
@Mixin(EnderDragonRenderer.class)
public class EnderDragonRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("RETURN"))
	private void agilestars$drawCustomDragon(EnderDragon dragon, float yaw, float partialTick,
			PoseStack pose, MultiBufferSource buffers, int light, CallbackInfo ci) {
		try {
			DragonHeadModel model = DragonHeadModel.get();
			VertexConsumer consumer = buffers.getBuffer(DragonHeadModel.renderType());

			pose.pushPose();
			// the vanilla dragon renderer scales its model by 0.25-ish and sits
			// the body near the entity origin; match that scale for the head.
			pose.scale(1.0F, 1.0F, 1.0F);

			model.render(pose, consumer, light, 0);

			pose.popPose();
		} catch (Throwable t) {
			com.agilestars.AgileStars.LOGGER.error("dragon model render failed", t);
		}
	}
}
