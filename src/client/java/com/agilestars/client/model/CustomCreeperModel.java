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
 * The project's texture is 40x32 and lays the head out at 2 pixels per unit
 * (the 8x8x8 head occupies u 16..32 for the face plus u 32..40 for the ears,
 * v 0..21), which is why the vanilla 64x32 UV layout stretched the face.
 * UVs below are expressed in that 40x32 layout.
 *
 * The vanilla animation is kept, so the creeper still walks and turns its head.
 */
public class CustomCreeperModel extends CreeperModel<Creeper> {

	/** The project's texture is 40x32, not the vanilla 64x32. */
	public static final int TEXTURE_WIDTH = 40;
	public static final int TEXTURE_HEIGHT = 32;

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

	private static CubeListBuilder legCubes(CubeDeformation deformation) {
		return CubeListBuilder.create().texOffs(0, 0)
				.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation)
				.uv(0, 0, 8, 8)      // up
				.uv(8, 0, 16, 8)     // down
				.uv(0, 8, 8, 20)     // east
				.uv(8, 8, 16, 20)    // west
				.uv(16, 8, 24, 20)   // north
				.uv(24, 8, 32, 20);  // south
	}

	public static LayerDefinition createBodyLayer(CubeDeformation deformation) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		// head: 8x8x8 drawn at 2 px per unit; its front face holds the artwork
		part.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation)
						.uv(0, 0, 16, 16)     // up
						.uv(16, 0, 32, 16)    // down
						.uv(32, 0, 40, 16)    // east (ears)
						.uv(32, 16, 40, 32)   // west
						.uv(16, 16, 32, 32)   // north (the face)
						.uv(0, 16, 16, 32),   // south
				PartPose.offset(0.0F, 6.0F, 0.0F));

		// body: the project uses 7 wide x 10 tall x 3 deep
		part.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-3.5F, -5.0F, -1.5F, 7.0F, 10.0F, 3.0F, deformation)
						.uv(0, 0, 7, 3)      // up
						.uv(7, 0, 14, 3)     // down
						.uv(14, 0, 17, 10)   // east
						.uv(17, 0, 20, 10)   // west
						.uv(20, 0, 27, 10)   // north
						.uv(27, 0, 34, 10),  // south
				PartPose.offset(0.0F, 6.0F, 0.0F));

		part.addOrReplaceChild("right_hind_leg", legCubes(deformation), PartPose.offset(-2.0F, 18.0F, 4.0F));
		part.addOrReplaceChild("left_hind_leg", legCubes(deformation), PartPose.offset(2.0F, 18.0F, 4.0F));
		part.addOrReplaceChild("right_front_leg", legCubes(deformation), PartPose.offset(-2.0F, 18.0F, -4.0F));
		part.addOrReplaceChild("left_front_leg", legCubes(deformation), PartPose.offset(2.0F, 18.0F, -4.0F));

		return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
