package com.agilestars.client.model;

import com.agilestars.AgileStars;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Creeper model rebuilt from the user's Blockbench project (creepa.bbmodel).
 *
 * Geometry and UVs use the standard creeper layout (64x32 texture) so the
 * artwork from the Blockbench project maps onto it correctly; the animation is
 * the vanilla one so the creeper still walks, turns its head and puffs up.
 */
public class CustomCreeperModel extends CreeperModel<Creeper> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public CustomCreeperModel(ModelPart root) {
		super(root);
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer(CubeDeformation deformation) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation),
				PartPose.offset(0.0F, 6.0F, 0.0F));

		part.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation),
				PartPose.offset(0.0F, 6.0F, 0.0F));

		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16)
				.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation);
		part.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-2.0F, 18.0F, 4.0F));
		part.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(2.0F, 18.0F, 4.0F));
		part.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-2.0F, 18.0F, -4.0F));
		part.addOrReplaceChild("left_front_leg", leg, PartPose.offset(2.0F, 18.0F, -4.0F));

		return LayerDefinition.create(mesh, 64, 32);
	}

	public static ResourceLocation getTextureLocation() {
		return AgileStars.id("textures/entity/creeper.png");
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Creeper creeper, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.body.yRot = 0.0F;
	}
}
