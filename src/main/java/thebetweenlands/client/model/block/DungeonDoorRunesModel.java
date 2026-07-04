package thebetweenlands.client.model.block;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.renderer.util.UVSquare;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.config.BetweenlandsConfig;

public class DungeonDoorRunesModel {

	public static final List<ResourceLocation> RUNES = List.of(
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_1.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_2.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_3.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_4.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_5.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_6.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_7.png"),
		TheBetweenlands.prefix("textures/entity/block/rune_overlay_8.png"));
	public static final ResourceLocation TEXTURE_RUNE_GLOW = TheBetweenlands.prefix("textures/entity/block/dungeon_runes_glow.png");

	// UVs for the rune cube textures
	private static final UVSquare TOP_RUNE_TOP_FACE_UVS   = UVSquare.of(5, 0,  19, 5,  32f); // [5, 0] to [19, 5] in a 32x32 image
	private static final UVSquare TOP_RUNE_FRONT_FACE_UVS = UVSquare.of(5, 5,  19, 10, 32f); // [5, 5] to [19, 10] in a 32x32 image
	
	private static final UVSquare MIDDLE_RUNE_TOP_FACE_UVS   = UVSquare.of(5, 11,  19, 15,  32f); // [5, 11] to [19, 15] in a 32x32 image
	private static final UVSquare MIDDLE_RUNE_FRONT_FACE_UVS = UVSquare.of(5, 15,  19, 19,  32f); // [5, 15] to [19, 19] in a 32x32 image

	private static final UVSquare BOTTOM_RUNE_TOP_FACE_UVS   = UVSquare.of(5, 20,  19, 25,  32f); // [5, 20] to [19, 25] in a 32x32 image
	private static final UVSquare BOTTOM_RUNE_FRONT_FACE_UVS = UVSquare.of(5, 25,  19, 30,  32f); // [5, 25] to [19, 30] in a 32x32 image

	// UVs for the rune glow texture
	private static final UVSquare TOP_RUNE_UVS    = UVSquare.of(16, 2,    48, 12,   64f, 32f); // [16, 2] to [48, 12] in a 64x32 image
	private static final UVSquare MIDDLE_RUNE_UVS = UVSquare.of(16, 12,   48, 20,   64f, 32f); // [16, 12] to [48, 20] in a 64x32 image
	private static final UVSquare BOTTOM_RUNE_UVS = UVSquare.of(16, 20,   48, 30,   64f, 32f); // [16, 20] to [48, 30] in a 64x32 image
	
	private final ModelPart top;
	private final ModelPart middle;
	private final ModelPart bottom;

	public DungeonDoorRunesModel(ModelPart root) {
		this.top = root.getChild("top");
		this.middle = root.getChild("middle");
		this.bottom = root.getChild("bottom");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("top", CubeListBuilder.create().addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5), PartPose.offset(0.0F, -4.5F, -5.51F));
		partDefinition.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(1, 11).addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4), PartPose.offset(0.0F, 0.0F, -6.01F));
		partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 20).addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5), PartPose.offset(0.0F, 4.5F, -5.51F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public void renderTopLayer(BlockEntity entity, ResourceLocation texture, int ticks, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		boolean hideTopFace = false;
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.top.xRot = Mth.lerp(partialTick, door.lastTickTopRotate, door.top_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
			
			if(door.top_rotate == 0 && door.lastTickTopRotate == 0) {
				hideTopFace = true;
			}
		} else {
			this.top.xRot = 0;
			hideTopFace = true;
		}
		this.renderRune(this.top, stack, buffer, texture, ticks, partialTick, light, overlay, TOP_RUNE_FRONT_FACE_UVS, hideTopFace ? null : TOP_RUNE_TOP_FACE_UVS, TOP_RUNE_UVS);
	}

	public void renderMiddleLayer(BlockEntity entity, ResourceLocation texture, int ticks, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		boolean hideTopFace = false;
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.middle.xRot = Mth.lerp(partialTick, door.lastTickMidRotate, door.mid_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
			
			if(door.mid_rotate == 0 && door.lastTickMidRotate == 0) {
				hideTopFace = true;
			}
		} else {
			this.middle.xRot = 0;
			hideTopFace = true;
		}
		this.renderRune(this.middle, stack, buffer, texture, ticks, partialTick, light, overlay, MIDDLE_RUNE_FRONT_FACE_UVS, hideTopFace ? null : MIDDLE_RUNE_TOP_FACE_UVS, MIDDLE_RUNE_UVS);
	}

	public void renderBottomLayer(BlockEntity entity, ResourceLocation texture, int ticks, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		boolean hideTopFace = false;
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.bottom.xRot = Mth.lerp(partialTick, door.lastTickBottomRotate, door.bottom_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
			
			if(door.bottom_rotate == 0 && door.lastTickBottomRotate == 0) {
				hideTopFace = true;
			}
		} else {
			this.bottom.xRot = 0;
			hideTopFace = true;
		}
		this.renderRune(this.bottom, stack, buffer, texture, ticks, partialTick, light, overlay, BOTTOM_RUNE_FRONT_FACE_UVS, hideTopFace ? null : BOTTOM_RUNE_TOP_FACE_UVS, BOTTOM_RUNE_UVS);
	}

	private static final int RUNE_GLOW_PASSES = 3;
	
	private void renderRune(ModelPart box, PoseStack stack, MultiBufferSource buffer, ResourceLocation texture,
			int ticks, float partialTick, int light, int overlay,
			@Nullable UVSquare frontFaceUVs, @Nullable UVSquare topFaceUVs, UVSquare initialGlowUVs) {
		// Render outer box
		box.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay);

		if(BetweenlandsConfig.debug && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			return;
		}
		
		// No glow would render, so don't try to render the glow
		if(frontFaceUVs == null && topFaceUVs == null) {
			return;
		}
		
		// Setup stack for box
		stack.pushPose();

		stack.scale(box.xScale, box.yScale, box.zScale);
		
		// Render cube layers
		float renderTicks = ticks + partialTick;

		// Render bottom rune layers (translucent transparency, no depth writes)
		RenderType normalGlowType = BLRenderTypes.dungeonDoorRunes(texture);
		VertexConsumer normalConsumer = buffer.getBuffer(normalGlowType);
		
		box.visit(stack, (pose, path, index, cube) -> {
			AABB aabb = new AABB(cube.minX / 16.0, cube.minY / 16.0, cube.minZ / 16.0, cube.maxX / 16.0, cube.maxY / 16.0, cube.maxZ / 16.0);
			
			for(int i = 0; i < RUNE_GLOW_PASSES - 1; ++i) {
				this.renderCubeGlowLayer(pose, cube, normalConsumer, renderTicks, frontFaceUVs, topFaceUVs, initialGlowUVs, i, aabb);
			}
		});
		
		// Render top rune layer (additive transparency, depth writes enabled)
		RenderType additiveGlowType = BLRenderTypes.dungeonDoorRunesAdditive(texture);
		VertexConsumer additiveConsumer = buffer.getBuffer(additiveGlowType);

		box.visit(stack, (pose, path, index, cube) -> {
			AABB aabb = new AABB(cube.minX / 16.0, cube.minY / 16.0, cube.minZ / 16.0, cube.maxX / 16.0, cube.maxY / 16.0, cube.maxZ / 16.0);
			
			this.renderCubeGlowLayer(pose, cube, additiveConsumer, renderTicks, frontFaceUVs, topFaceUVs, initialGlowUVs, RUNE_GLOW_PASSES - 1, aabb);
		});
		
		stack.popPose();
	}
	
	private void renderCubeGlowLayer(PoseStack.Pose pose, ModelPart.Cube cube,
			VertexConsumer consumer,
			float renderTicks,
			@Nullable UVSquare frontFaceUVs, @Nullable UVSquare topFaceUVs, UVSquare initialGlowUVs,
			int i, AABB aabb) {
		float texOffset = renderTicks * 0.0015F;

		float alpha = 0.3f + (Mth.sin(renderTicks / 10.0f + i * Mth.PI * 2.0f / (float)RUNE_GLOW_PASSES) + 1) / 2.0f * 0.3f;

		float dirU = Mth.cos(i * Mth.PI * 2.0f / (float)RUNE_GLOW_PASSES);
		float dirV = Mth.sin(i * Mth.PI * 2.0f / (float)RUNE_GLOW_PASSES);

		float uTranslation = dirU * texOffset;
		float vTranslation = dirV * texOffset;
		
		// This could be made cleaner using abstractions like Matrix2f and Vector2f, but I don't feel like it right now.
		
		float scaleU = RUNE_GLOW_PASSES - i;
		float scaleV = scaleU;
		
		double rotationRadians = (renderTicks / 30.0) * (Math.PI / 180);
		
		float centreU = 0.5f;
		float centreV = 0.5f;

		// Min/Max U/V values (before transformation)
		float baseMinU = initialGlowUVs.start().u();
		float baseMaxU = initialGlowUVs.end().u();
		float baseMinV = initialGlowUVs.start().v();
		float baseMaxV = initialGlowUVs.end().v();
		
		
		// Center UVs on 0, 0
		baseMinU -= centreU;
		baseMaxU -= centreU;
		baseMinV -= centreV;
		baseMaxV -= centreV;
		
		centreU = 0.0f;
		centreV = 0.0f;

		// Scale V by 2x because it's a 64x32 image (not square)
		baseMinV = (baseMinV - centreU) / 2.0f + centreU;
		baseMaxV = (baseMaxV - centreV) / 2.0f + centreV;
		scaleV = scaleV * 2.0f;
		
		// Split into separate UVs for each vertex, so they can be properly rotated
		float topLeftU = baseMinU;
		float topLeftV = baseMinV;
		
		float topRightU = baseMinU;
		float topRightV = baseMaxV;

		float bottomRightU = baseMaxU;
		float bottomRightV = baseMaxV;

		float bottomLeftU = baseMaxU;
		float bottomLeftV = baseMinV;



		// Apply rotation
		// -----------------       -----       --------------------------- 
		// | cos θ, -sin θ |       | u |       | u * cos(θ) - v * sin(θ) | 
		// |               |   X   |   |   =   |                         | 
		// | sin θ,  cos θ |       | v |       | u * sin(θ) + v * cos(θ) | 
		// -----------------       -----       --------------------------- 
		final float cosPart = (float)Math.cos(rotationRadians);
		final float sinPart = (float)Math.sin(rotationRadians);

		// Note: Buffer variables are there to prevent the U assignment messing with the V calculation

		// Rotate top left vertex
		final float rotatedTopLeftU = ((topLeftU - centreU) * cosPart - (topLeftV - centreV) * sinPart) + centreU;
		final float rotatedTopLeftV = ((topLeftU - centreU) * sinPart + (topLeftV - centreV) * cosPart) + centreV;
		topLeftU = rotatedTopLeftU;
		topLeftV = rotatedTopLeftV;

		// Rotate top right vertex
		final float rotatedTopRightU = ((topRightU - centreU) * cosPart - (topRightV - centreV) * sinPart) + centreU;
		final float rotatedTopRightV = ((topRightU - centreU) * sinPart + (topRightV - centreV) * cosPart) + centreV;
		topRightU = rotatedTopRightU;
		topRightV = rotatedTopRightV;

		// Rotate bottom right vertex
		final float rotatedBottomRightU = ((bottomRightU - centreU) * cosPart - (bottomRightV - centreV) * sinPart) + centreU;
		final float rotatedBottomRightV = ((bottomRightU - centreU) * sinPart + (bottomRightV - centreV) * cosPart) + centreV;
		bottomRightU = rotatedBottomRightU;
		bottomRightV = rotatedBottomRightV;

		// Rotate bottom left vertex
		final float rotatedBottomLeftU = ((bottomLeftU - centreU) * cosPart - (bottomLeftV - centreV) * sinPart) + centreU;
		final float rotatedBottomLeftV = ((bottomLeftU - centreU) * sinPart + (bottomLeftV - centreV) * cosPart) + centreV;
		bottomLeftU = rotatedBottomLeftU;
		bottomLeftV = rotatedBottomLeftV;

		
		
		// Apply translation
		final float uOffset = uTranslation;
		final float vOffset = vTranslation;

		topLeftU     += uOffset;
		topRightU    += uOffset;
		bottomRightU += uOffset;
		bottomLeftU  += uOffset;
		centreU      += uOffset;

		topLeftV     += vOffset;
		topRightV    += vOffset;
		bottomRightV += vOffset;
		bottomLeftV  += vOffset;
		centreV      += vOffset;



		// Apply scale
		topLeftU     = (topLeftU - centreU) * scaleU + centreU;
		topRightU    = (topRightU - centreU) * scaleU + centreU;
		bottomRightU = (bottomRightU - centreU) * scaleU + centreU;
		bottomLeftU  = (bottomLeftU - centreU) * scaleU + centreU;
		
		topLeftV     = (topLeftV - centreV) * scaleV + centreV;
		topRightV    = (topRightV - centreV) * scaleV + centreV;
		bottomRightV = (bottomRightV - centreV) * scaleV + centreV;
		bottomLeftV  = (bottomLeftV - centreV) * scaleV + centreV;
		
		
		
		// Reset UV center
		centreU += 0.5f;
		centreV += 0.5f;
		
		topLeftU     += 0.5f;
		topRightU    += 0.5f;
		bottomRightU += 0.5f;
		bottomLeftU  += 0.5f;

		topLeftV     += 0.5f;
		topRightV    += 0.5f;
		bottomRightV += 0.5f;
		bottomLeftV  += 0.5f;

		
		
		// Render cube
		this.renderCube(pose, aabb, consumer,
				topLeftU, topLeftV,
				topRightU, topRightV,
				bottomRightU, bottomRightV,
				bottomLeftU, bottomLeftV,
				
				// Colour
				// Note: 1.12's runes were affected by a constant lightmap UV of [238.0f, 238.0f], replicate that here with a set colour
				196f / 256f, 196f / 256f, 196f / 256f, alpha,
				// Mask texture UVs
				frontFaceUVs, topFaceUVs);
	}
	
	private void renderCube(PoseStack.Pose pose, AABB aabb, VertexConsumer buffer,
			float topLeftU,     float topLeftV,
			float topRightU,    float topRightV,
			float bottomRightU, float bottomRightV,
			float bottomLeftU,  float bottomLeftV,
			
			float r, float g, float b, float a,
			@Nullable UVSquare frontFaceUVs, @Nullable UVSquare topFaceUVs) {

		final float minX = (float)aabb.minX;
		final float minY = (float)aabb.minY;
		final float minZ = (float)aabb.minZ;
		
		final float maxX = (float)aabb.maxX;
		final float maxY = (float)aabb.maxY;
		final float maxZ = (float)aabb.maxZ;

		// setUv1(frontFaceMinU2, frontFaceMinV2)
		// setUv1(frontFaceMinU2, frontFaceMaxV2)
		// setUv1(frontFaceMaxU2, frontFaceMaxV2)
		// setUv1(frontFaceMaxU2, frontFaceMinV2)

		// setUv1(topFaceMinU2, topFaceMinV2)
		// setUv1(topFaceMinU2, topFaceMaxV2)
		// setUv1(topFaceMaxU2, topFaceMaxV2)
		// setUv1(topFaceMaxU2, topFaceMinV2)

		// Render top rune
		if(topFaceUVs != null) {
			final int topFaceMinU2 = Math.round(topFaceUVs.start().u() * 256.0f);
			final int topFaceMaxU2 = Math.round(topFaceUVs.end().u()   * 256.0f);
			final int topFaceMinV2 = Math.round(topFaceUVs.start().v() * 256.0f);
			final int topFaceMaxV2 = Math.round(topFaceUVs.end().v()   * 256.0f);
			
			// Down face (which is the top due to the model's transformations)
			buffer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a).setUv(topLeftU,     topLeftV)    .setUv1(topFaceMinU2, topFaceMinV2).setNormal(pose, 0.0f, -1.0f, 0.0f);
			buffer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setUv(topRightU,    topRightV)   .setUv1(topFaceMinU2, topFaceMaxV2).setNormal(pose, 0.0f, -1.0f, 0.0f);
			buffer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setUv(bottomRightU, bottomRightV).setUv1(topFaceMaxU2, topFaceMaxV2).setNormal(pose, 0.0f, -1.0f, 0.0f);
			buffer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a).setUv(bottomLeftU,  bottomLeftV) .setUv1(topFaceMaxU2, topFaceMinV2).setNormal(pose, 0.0f, -1.0f, 0.0f);
		}
		
		// Render front rune
		if(frontFaceUVs != null) {
			final int frontFaceMinU2 = Math.round(frontFaceUVs.start().u() * 256.0f);
			final int frontFaceMaxU2 = Math.round(frontFaceUVs.end().u()   * 256.0f);
			final int frontFaceMinV2 = Math.round(frontFaceUVs.start().v() * 256.0f);
			final int frontFaceMaxV2 = Math.round(frontFaceUVs.end().v()   * 256.0f);

			// North face (which is the front due to the model's transformations)
			buffer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a).setUv(topLeftU,     topLeftV)    .setUv1(frontFaceMinU2, frontFaceMinV2).setNormal(pose, 0.0f, 0.0f, -1.0f);
			buffer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a).setUv(topRightU,    topRightV)   .setUv1(frontFaceMinU2, frontFaceMaxV2).setNormal(pose, 0.0f, 0.0f, -1.0f);
			buffer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a).setUv(bottomRightU, bottomRightV).setUv1(frontFaceMaxU2, frontFaceMaxV2).setNormal(pose, 0.0f, 0.0f, -1.0f);
			buffer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a).setUv(bottomLeftU,  bottomLeftV) .setUv1(frontFaceMaxU2, frontFaceMinV2).setNormal(pose, 0.0f, 0.0f, -1.0f);
		}
	}
}
