package com.agilestars.client.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import com.agilestars.AgileStars;

/**
 * Minimal route-A pipeline: raw mesh geometry (from the Blockbench project)
 * drawn with a bone transform, no GeckoLib and no cube conversion.
 *
 * Data comes from assets/agilestars/dragon_head.json, generated from
 * D.bbmodel: 45 meshes / 1656 triangles on the "head" bone.
 */
public final class DragonHeadModel {

	public static final ResourceLocation TEXTURE =
			AgileStars.id("textures/entity/dragon.png");

	private static DragonHeadModel loaded;

	private final float[] vertices;   // x,y,z per vertex
	private final int[] faces;        // 3 indices, 6 uv, 1 texture per triangle
	private final float[] chainX;     // bone chain pivots, root -> leaf
	private final float[] chainY;
	private final float[] chainZ;
	private final float scale;
	private final float originX;
	private final float originY;
	private final float originZ;

	private DragonHeadModel(float[] vertices, int[] faces, List<float[]> chain, float scale) {
		this.vertices = vertices;
		this.faces = faces;
		this.chainX = new float[chain.size()];
		this.chainY = new float[chain.size()];
		this.chainZ = new float[chain.size()];
		for (int i = 0; i < chain.size(); i++) {
			this.chainX[i] = chain.get(i)[0];
			this.chainY[i] = chain.get(i)[1];
			this.chainZ[i] = chain.get(i)[2];
		}
		// model units are the same as Minecraft's (16 per block); the dragon is
		// ~120 units long, so it is scaled down to a sensible in-world size and
		// pulled down so the head sits at eye level for a quick visual check.
		this.scale = scale;
		float[] root = chain.isEmpty() ? new float[] { 0, 0, 0 } : chain.get(chain.size() - 1);
		this.originX = root[0];
		this.originY = root[1];
		this.originZ = root[2];
	}

	public static DragonHeadModel get() {
		if (loaded == null) {
			try {
				loaded = load();
			} catch (Exception e) {
				AgileStars.LOGGER.error("could not load dragon_head.json", e);
				loaded = new DragonHeadModel(new float[0], new int[0], List.of(), 1.0F);
			}
		}
		return loaded;
	}

	private static DragonHeadModel load() throws Exception {
		ResourceLocation id = AgileStars.id("dragon_head.json");
		Resource res = net.minecraft.client.Minecraft.getInstance()
				.getResourceManager().getResourceOrThrow(id);
		try (InputStream in = res.open()) {
			JsonObject root = JsonParser.parseReader(
					new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();

			JsonArray varr = root.getAsJsonArray("vertices");
			float[] verts = new float[varr.size()];
			for (int i = 0; i < verts.length; i++) {
				verts[i] = varr.get(i).getAsFloat();
			}

			JsonArray farr = root.getAsJsonArray("faces");
			int[] faces = new int[farr.size()];
			for (int i = 0; i < faces.length; i++) {
				faces[i] = farr.get(i).getAsInt();
			}

			List<float[]> chain = new ArrayList<>();
			for (JsonElement e : root.getAsJsonArray("chain")) {
				JsonArray p = e.getAsJsonObject().getAsJsonArray("pivot");
				chain.add(new float[] { p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat() });
			}
			AgileStars.LOGGER.info("dragon head: {} verts, {} tris, {} bones in chain",
					verts.length / 3, faces.length / 11, chain.size());
			return new DragonHeadModel(verts, faces, chain, 1.0F / 16.0F);
		}
	}

	/** Draws the geometry with the accumulated bone transform applied. */
	public void render(PoseStack pose, VertexConsumer buffer, int light, int overlay) {
		if (this.faces.length == 0) {
			return;
		}
		pose.pushPose();
		pose.translate(-this.originX * this.scale, -this.originY * this.scale, -this.originZ * this.scale);
		pose.scale(this.scale, this.scale, this.scale);

		// walk the bone chain: translate to each pivot, then translate back
		for (int i = chainX.length - 1; i >= 0; i--) {
			pose.translate(chainX[i], chainY[i], chainZ[i]);
			pose.translate(-chainX[i], -chainY[i], -chainZ[i]);
		}

		var matrix = pose.last().pose();
		for (int i = 0; i + 10 < this.faces.length; i += 11) {
			int a = this.faces[i] * 3;
			int b = this.faces[i + 1] * 3;
			int c = this.faces[i + 2] * 3;

			float ax = this.vertices[a], ay = this.vertices[a + 1], az = this.vertices[a + 2];
			float bx = this.vertices[b], by = this.vertices[b + 1], bz = this.vertices[b + 2];
			float cx = this.vertices[c], cy = this.vertices[c + 1], cz = this.vertices[c + 2];

			float e1x = bx - ax, e1y = by - ay, e1z = bz - az;
			float e2x = cx - ax, e2y = cy - ay, e2z = cz - az;
			float nx = e1y * e2z - e1z * e2y;
			float ny = e1z * e2x - e1x * e2z;
			float nz = e1x * e2y - e1y * e2x;
			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1.0E-6F) {
				nx /= len;
				ny /= len;
				nz /= len;
			}

			vertex(buffer, matrix, ax, ay, az, this.faces[i + 3], this.faces[i + 4], nx, ny, nz, light, overlay);
			vertex(buffer, matrix, bx, by, bz, this.faces[i + 5], this.faces[i + 6], nx, ny, nz, light, overlay);
			vertex(buffer, matrix, cx, cy, cz, this.faces[i + 7], this.faces[i + 8], nx, ny, nz, light, overlay);
		}
		pose.popPose();
	}

	/**
	 * 1.21.1 VertexConsumer method names, verified against the official
	 * mappings: addVertex / setColor / setUv / setOverlay / setUv2 / setNormal.
	 * (The older vertex()/color()/uv() names do not exist here.)
	 */
	private static void vertex(VertexConsumer buffer, org.joml.Matrix4f m,
			float x, float y, float z, float u, float v,
			float nx, float ny, float nz, int light, int overlay) {
		buffer.addVertex(m, x, y, z)
				.setColor(255, 255, 255, 255)
				.setUv(u / 1024.0F, v / 1024.0F)
				.setOverlay(overlay)
				.setUv2(light & 0xFFFF, (light >> 16) & 0xFFFF)
				.setNormal(nx, ny, nz);
	}

	public static RenderType renderType() {
		return RenderType.entityCutoutNoCull(TEXTURE);
	}
}
