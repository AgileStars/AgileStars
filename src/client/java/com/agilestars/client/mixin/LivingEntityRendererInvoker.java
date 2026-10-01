package com.agilestars.client.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes the protected setModel so custom renderers can install their own model.
 */
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererInvoker<T extends LivingEntity, M extends EntityModel<T>> {

	@Invoker("setModel")
	void agilestars$setModel(M model);
}
