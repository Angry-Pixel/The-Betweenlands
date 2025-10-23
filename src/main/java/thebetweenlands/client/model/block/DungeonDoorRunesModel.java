package thebetweenlands.client.model.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;

import java.util.List;

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
	private static final ResourceLocation TEXTURE_RUNE_GLOW = TheBetweenlands.prefix("textures/entity/block/dungeon_runes_glow.png");

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
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.top.xRot = Mth.lerp(partialTick, door.lastTickTopRotate, door.top_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
		} else {
			this.top.xRot = 0;
		}
		this.renderRune(this.top, stack, buffer, texture, ticks, partialTick, light, overlay);
	}

	public void renderMiddleLayer(BlockEntity entity, ResourceLocation texture, int ticks, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.middle.xRot = Mth.lerp(partialTick, door.lastTickMidRotate, door.mid_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
		} else {
			this.middle.xRot = 0;
		}
		this.renderRune(this.middle, stack, buffer, texture, ticks, partialTick, light, overlay);
	}

	public void renderBottomLayer(BlockEntity entity, ResourceLocation texture, int ticks, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		if (entity instanceof DungeonDoorRunesBlockEntity door) {
			this.bottom.xRot = Mth.lerp(partialTick, door.lastTickBottomRotate, door.bottom_rotate) / Mth.RAD_TO_DEG;
			if(door.hide_lock)
				return;
		} else {
			this.bottom.xRot = 0;
		}
		this.renderRune(this.bottom, stack, buffer, texture, ticks, partialTick, light, overlay);
	}

	private void renderRune(ModelPart box, PoseStack stack, MultiBufferSource buffer, ResourceLocation texture, int ticks, float partialTick, int light, int overlay) {
		//GlStateManager.enablePolygonOffset();
		//GlStateManager.doPolygonOffset(-0.01F, -3F);

		box.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay);

		//TODO AAAAAAAAAA
//		try(Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget())) {
//			if(stencil.isValid()) {
//				GL11.glEnable(GL11.GL_STENCIL_TEST);
//
//				stencil.clear(false);
//
//				stencil.func(GL11.GL_ALWAYS, true);
//				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//
//				RenderSystem.enableBlend();
//				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//
//				RenderSystem.depthMask(false);
//
//				//Render rune mask
//				box.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay);
//
//				RenderSystem.depthMask(true);
//
//				stencil.func(GL11.GL_EQUAL, true);
//				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
//
//				//Render glowy stuff
//				this.renderRuneGlow(box, stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_RUNE_GLOW)), ticks, partialTick, overlay);
//
//				GL11.glDisable(GL11.GL_STENCIL_TEST);
//			} else {
//				//No fancy runes for toasters
//				box.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay);
//			}
//		}

		//GlStateManager.disablePolygonOffset();
	}

	private void renderRuneGlow(ModelPart box, PoseStack stack, VertexConsumer consumer, int ticks, float partialTick, int overlay) {
		float renderTicks = ticks + partialTick;
		float texOffset = renderTicks * 0.0015F;
		int passes = 3;

		for(int i = 0; i < passes; i++) {
			RenderSystem.depthMask(i == passes - 1);
			if(i == passes - 1) {
				RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			}

			float alpha = 0.3f + (Mth.sin(renderTicks / 10.0f + i * Mth.PI * 2.0f / passes) + 1) / 2.0f * 0.3f;

			float dirU = Mth.cos(i * Mth.PI * 2.0f / passes);
			float dirV = Mth.sin(i * Mth.PI * 2.0f / passes);

			stack.translate(dirU * texOffset, dirV * texOffset, 0);
			stack.scale(1.0F, 2.0F, 1.0F); //V needs to be scaled x2 because texture is not square
			stack.scale(passes - i, passes - i, 1);
			stack.mulPose(Axis.ZP.rotationDegrees(renderTicks / 30.0f));
			box.render(stack, consumer, LightTexture.FULL_BRIGHT, overlay, FastColor.ARGB32.color(FastColor.as8BitChannel(alpha), -1));
			RenderSystem.defaultBlendFunc();
		}
	}
}
