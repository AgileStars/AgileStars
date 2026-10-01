package com.agilestars.client.mixin;

import com.agilestars.client.model.CustomCreeperModel;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the vanilla creeper model with the one built from the Blockbench
 * project (creepa.bbmodel) and points the renderer at the matching texture.
 */
@Mixin(CreeperRenderer.class)
public abstract class CreeperRendererMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	private void agilestars$useCustomModel(EntityRendererProvider.Context context, CallbackInfo ci) {
		CustomCreeperModel model = new CustomCreeperModel(
				context.getModelSet().bakeLayer(ModelLayers.CREEPER));
		((LivingEntityRendererInvoker<Creeper, CustomCreeperModel>) this).agilestars$setModel(model);
	}

	/**
	 * The custom model was built against the 64x32 creeper layout, so it needs
	 * our own copy of the texture rather than the vanilla one.
	 */
	@Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
	private void agilestars$customTexture(Creeper creeper, CallbackInfoReturnable<ResourceLocation> cir) {
		cir.setReturnValue(CustomCreeperModel.getTextureLocation());
	}
}
