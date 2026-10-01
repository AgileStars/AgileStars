package com.agilestars.client.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Gives access to the renderer's model field.
 *
 * This is deliberately an @Accessor on the field rather than an @Invoker on
 * setModel: the field's type is EntityModel after generic erasure, so the
 * generated accessor signature is exact, whereas an @Invoker on a generic
 * method forces Mixin to synthesise a bridge that can fail to resolve.
 */
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {

	@Accessor("model")
	void agilestars$setModel(EntityModel<?> model);
}
