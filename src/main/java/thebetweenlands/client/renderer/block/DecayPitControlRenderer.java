package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.DecayPitPlugModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DecayPitControlBlockEntity;

public class DecayPitControlRenderer implements BlockEntityRenderer<DecayPitControlBlockEntity> {

	public static final ResourceLocation OUTER_RING_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_outer_ring.png");
	public static final ResourceLocation INNER_RING_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_inner_ring.png");
	public static final ResourceLocation OUTER_MASK_MUD_TILE_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_outer_gear_mask.png");
	public static final ResourceLocation INNER_MASK_MUD_TILE_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_inner_gear_mask.png");
	public static final ResourceLocation MASK_MUD_TILE_TEXTURE_HOLE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_gear_mask_hole.png");
	public static final ResourceLocation VERTICAL_RING_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_vertical_ring.png");
	public static final ResourceLocation DECAY_HOLE_TEXTURE_1 = TheBetweenlands.prefix("textures/entity/pit/decay_pit_hole_1.png");
	public static final ResourceLocation DECAY_HOLE_TEXTURE_2 = TheBetweenlands.prefix("textures/entity/pit/decay_pit_hole_2.png");
	public static final ResourceLocation DECAY_HOLE_TEXTURE_3 = TheBetweenlands.prefix("textures/entity/pit/decay_pit_hole_3.png");

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_plug.png");
	private final DecayPitPlugModel plug;

	public static final ResourceLocation TARGET_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_target.png");
	private final ModelPart targets;

	public DecayPitControlRenderer(BlockEntityRendererProvider.Context context) {
		this.plug = new DecayPitPlugModel(context.bakeLayer(BLModelLayers.DECAY_PIT_PLUG));
		this.targets = context.bakeLayer(BLModelLayers.DECAY_PIT_TARGET);
	}


	@Override
	public void render(DecayPitControlBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (entity.getLevel() == null) return;
		light = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above());
		float floor_fade = Mth.lerp(partialTick, entity.floorFadeTicksPrev, entity.floorFadeTicks);
		if (entity.getShowFloor()) {
			float ringRotation = Mth.lerp(partialTick, entity.animationTicksPrev, entity.animationTicks);

			stack.pushPose();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();

			Tesselator tessellator = Tesselator.getInstance();
			float alpha = 1.0F - floor_fade;

			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			if (entity.getShowFloor()) {
				RenderSystem.setShaderTexture(0, OUTER_MASK_MUD_TILE_TEXTURE);
				BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 3.001F, 0.5F, 15F * part, 7.5F, 7.5F, 4.25F, 4.25F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				RenderSystem.setShaderTexture(0, INNER_MASK_MUD_TILE_TEXTURE);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 2.001F, 0.5F, 15F * part, 4.25F, 4.25F, 2.75F, 2.75F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				RenderSystem.setShaderTexture(0, VERTICAL_RING_TEXTURE);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 3.001F, 0.5F, 15F * part, 7.5F, 7.5F, 4.25F, 4.25F, true, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				RenderSystem.setShaderTexture(0, DECAY_HOLE_TEXTURE_1);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 1.003F, 0.5F, 15F * part, 2.25F, 2.25F, 0.0F, 0.0F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				stack.pushPose();
				stack.translate(0.5F, 0.5F, 0.5F);
				stack.mulPose(Axis.YP.rotationDegrees(ringRotation));
				stack.translate(-0.5F, -0.5F, -0.5F);

				RenderSystem.setShaderTexture(0, OUTER_RING_TEXTURE);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 3.003F, 0.5F, 15F * part, 7.5F, 7.5F, 4.24F, 4.24F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());
				stack.popPose();

				stack.pushPose();
				stack.translate(0.5F, 0.5F, 0.5F);
				stack.mulPose(Axis.YN.rotationDegrees(ringRotation));
				stack.translate(-0.5F, -0.5F, -0.5F);

				RenderSystem.setShaderTexture(0, INNER_RING_TEXTURE);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 2.003F, 0.5F, 15F * part, 4.25F, 4.25F, 2.75F, 2.75F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());
				stack.popPose();

				RenderSystem.depthMask(false);

				stack.translate(0.5F, 0.5F, 0.5F);
				stack.mulPose(Axis.YP.rotationDegrees(ringRotation));
				stack.translate(-0.5F, -0.5F, -0.5F);

				RenderSystem.setShaderTexture(0, DECAY_HOLE_TEXTURE_2);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 1.5F + 0.003F, 0.5F, 15F * part, 2.0F, 2.0F, 0.0F, 0.0F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				stack.pushPose();
				stack.translate(0.5F, 0.5F, 0.5F);
				stack.mulPose(Axis.YP.rotationDegrees(ringRotation * 2.0F));
				stack.translate(-0.5F, -0.5F, -0.5F);

				RenderSystem.setShaderTexture(0, DECAY_HOLE_TEXTURE_3);
				buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
				for (int part = 0; part < 24; part++) {
					buildRingQuads(buffer, stack.last(), 0.5F, 1.75F + 0.003F, 0.5F, 15F * part, 2.25F, 2.25F, 0.0F, 0.0F, false, alpha);
				}
				BufferUploader.drawWithShader(buffer.buildOrThrow());
				stack.popPose();

				RenderSystem.depthMask(true);

				RenderSystem.disableBlend();

				stack.popPose();
			}
		}

		if (entity.isPlugged()) {
			float fall = Mth.lerp(partialTick, entity.plugDropTicksPrev, entity.plugDropTicks);
			float jumpUP = Mth.lerp(partialTick, entity.plugJumpPrev, entity.plugJump) * 0.1F;

			stack.pushPose();
			stack.translate(0.5F, 4F - fall + jumpUP, 0.5F);
			stack.scale(1F, -1F, -1F);
			stack.mulPose(Axis.YP.rotationDegrees(entity.plugRotation));
			this.plug.renderPlug(stack, source.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay, -1);
			stack.popPose();

			if (entity.getShowFloor()) {
				stack.pushPose();
				stack.translate(0.5F, 4F - fall, 0.5F);
				stack.scale(1F, -1F, -1F);
				this.plug.renderChains(stack, source.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay, FastColor.ARGB32.colorFromFloat(1 - floor_fade, 1, 1, 1));
				stack.popPose();

				stack.pushPose();
				stack.translate(0.5F, 7F - fall, 0.5F);
				stack.scale(1F, -1F, -1F);
				this.targets.render(stack, source.getBuffer(RenderType.entityCutoutNoCull(TARGET_TEXTURE)), light, overlay, FastColor.ARGB32.colorFromFloat(1 - floor_fade, 1, 1, 1));
				stack.popPose();
			}
		}
	}

	public static void buildRingQuads(BufferBuilder buffer, PoseStack.Pose pose, float x, float y, float z, float angle, float offsetXOuter, float offsetZOuter, float offsetXInner, float offsetZInner, boolean innerWall, float alpha) {
		float startAngle = (float) Math.toRadians(angle);
		float endAngle = (float) Math.toRadians(angle + 15F);
		float offSetXOut1 = -Mth.sin(startAngle) * offsetXOuter;
		float offSetZOut1 = Mth.cos(startAngle) * offsetZOuter;
		float offSetXIn1 = -Mth.sin(startAngle) * offsetXInner;
		float offSetZIn1 = Mth.cos(startAngle) * offsetZInner;

		float offSetXOut2 = -Mth.sin(endAngle) * offsetXOuter;
		float offSetZOut2 = Mth.cos(endAngle) * offsetZOuter;
		float offSetXIn2 = -Mth.sin(endAngle) * offsetXInner;
		float offSetZIn2 = Mth.cos(endAngle) * offsetZInner;

		if (!innerWall) {
			buffer.addVertex(pose, x + offSetXOut1, y, z + offSetZOut1).setUv(offSetZOut1 / offsetZOuter / 2 + 0.5F, 1 - (offSetXOut1 / offsetXOuter / 2 + 0.5F)).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXIn1, y, z + offSetZIn1).setUv(offSetZIn1 / offsetZOuter / 2 + 0.5F, 1 - (offSetXIn1 / offsetXOuter / 2 + 0.5F)).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXIn2, y, z + offSetZIn2).setUv(offSetZIn2 / offsetZOuter / 2 + 0.5F, 1 - (offSetXIn2 / offsetXOuter / 2 + 0.5F)).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXOut2, y, z + offSetZOut2).setUv(offSetZOut2 / offsetZOuter / 2 + 0.5F, 1 - (offSetXOut2 / offsetXOuter / 2 + 0.5F)).setColor(1.0F, 1.0F, 1.0F, alpha);
		} else {
			buffer.addVertex(pose, x + offSetXIn1, y, z + offSetZIn1).setUv(1, 1).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXIn1, y - 1F, z + offSetZIn1).setUv(1, 0).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXIn2, y - 1F, z + offSetZIn2).setUv(0, 0).setColor(1.0F, 1.0F, 1.0F, alpha);
			buffer.addVertex(pose, x + offSetXIn2, y, z + offSetZIn2).setUv(0, 1).setColor(1.0F, 1.0F, 1.0F, alpha);
		}
	}

	@Override
	public AABB getRenderBoundingBox(DecayPitControlBlockEntity entity) {
		return new AABB(entity.getBlockPos()).inflate(10.0D, 0.0D, 10.0D).expandTowards(0.0D, 12.0D, 0.0D);
	}
}
