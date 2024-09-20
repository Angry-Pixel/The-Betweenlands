package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.GroundFog;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.CenserBlock;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.util.RenderUtils;

public class CenserRenderer implements BlockEntityRenderer<CenserBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/censer.png"));
	private final ModelPart censer;
	private static final TextureAtlasSprite FOG = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TheBetweenlands.prefix("entity/block/censer_fog"));

	public CenserRenderer(BlockEntityRendererProvider.Context context) {
		this.censer = context.bakeLayer(BLModelLayers.CENSER);
	}

	@Override
	public void render(CenserBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(CenserBlock.FACING).toYRot() + 180));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.censer.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(CenserBlock.FACING).toYRot() + 90));
		if (entity.getLevel() != null && entity.getFuelTicks() > 0) {
			if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();

				float strength = entity.getDungeonFogStrength(partialTicks);

				float fogBrightness = 0.85F;
				float inScattering = 0.025F * strength;
				float extinction = 2.5F + 5.0F * (1 - strength);

				AABB fogArea = entity.getFogRenderArea();

				ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFog.GroundFogVolume(new Vec3(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
			}

			CenserRecipe<Object> recipe = entity.getCurrentRecipe();
			if (recipe != null) {
				int effectColor = recipe.getEffectColor(entity.getCurrentRecipeContext(), entity, CenserRecipe.EffectColorType.FOG);
				float effectStrength = entity.getEffectStrength(partialTicks);

				stack.pushPose();
				stack.translate(0.0F, 0.7F, 0.0F);
				//top fog strips
				stack.pushPose();
				stack.translate(0.0F, 0.18F, 0.18F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.86f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.translate(0.03F, 0.18F, 0.15F);
				stack.mulPose(Axis.YP.rotationDegrees(90));
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.94f, stack, source, effectColor, effectStrength, true);
				stack.popPose();

				stack.pushPose();
				stack.translate(-0.03F, 0.18F, 0.15F);
				stack.mulPose(Axis.YN.rotationDegrees(90));
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.92f, stack, source, effectColor, effectStrength, true);
				stack.popPose();

				//bottom fog strips
				stack.pushPose();
				stack.translate(0.114F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.9f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.translate(-0.135F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 1.1f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(90));
				stack.translate(0.114F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.85f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(90));
				stack.translate(-0.135F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 1.15f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(180));
				stack.translate(0.114F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.95f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(180));
				stack.translate(-0.135F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 1.08f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(270));
				stack.translate(0.114F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 0.88f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(270));
				stack.translate(-0.135F, 0.0F, 0.235F);
				this.renderFogFlow((RenderUtils.getRenderTickCounter() + partialTicks) * 1.05f, stack, source, effectColor, effectStrength, false);
				stack.popPose();

				stack.popPose();

				recipe.render(entity.getCurrentRecipeContext(), entity, entity.getBlockPos(), partialTicks);
			}
		}

		stack.popPose();
	}

	private void renderFogFlow(float animationTicks, PoseStack stack, MultiBufferSource source, int color, float alpha, boolean flatSlope) {
//		color = FastColor.ARGB32.colorFromFloat(alpha, FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
		float minU = FOG.getU0();
		float minV = FOG.getV0();
		float maxU = FOG.getU1();
		float maxV = FOG.getV1();

		//texture is 3px wide
		maxU = minU + (maxU - minU) * (3.0f / 16.0f);

		PoseStack.Pose pose = stack.last();
		VertexConsumer consumer = source.getBuffer(RenderType.entityTranslucent(FOG.atlasLocation()));

		float halfWidth = 0.13f / 2.0f;

		float zOff = 0.0f;
		float prevZOff = zOff;
		float vOff = minV;

		float onePx = 1 / 16.0f;
		float oneTx = (maxV - minV) / 16.0f;

		float stripY = 0.0f;
		float prevStripY = stripY;

		for (int i = 0; i < 16; i++) {
			float slopeIndex = i + 1;

			float slope = 0.001f;

			if (flatSlope && (i == 3 || i == 4)) {
				slope = 0.00019f;
			}

			stripY -= slope + Math.min(slopeIndex * slopeIndex * slopeIndex * slope, onePx);
			zOff += onePx / ((i + 1) * slope / 0.001f);

			float wavyZOff = zOff + (float) Math.sin((i - (animationTicks) * 0.1f) * 1.1f) * 0.015f;

			consumer.addVertex(pose.pose(), -halfWidth, prevStripY, prevZOff).setColor(color).setUv(minU, vOff + oneTx).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BLOCK).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose.pose(), -halfWidth, stripY, wavyZOff).setColor(color).setUv(minU, vOff).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BLOCK).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose.pose(), halfWidth, stripY, wavyZOff).setColor(color).setUv(maxU, vOff).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BLOCK).setNormal(pose, 0.0F, -1.0F, 0.0F);
			consumer.addVertex(pose.pose(), halfWidth, prevStripY, prevZOff).setColor(color).setUv(maxU, vOff + oneTx).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BLOCK).setNormal(pose, 0.0F, -1.0F, 0.0F);

			vOff += oneTx;

			prevStripY = stripY;
			prevZOff = wavyZOff;
		}
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public AABB getRenderBoundingBox(CenserBlockEntity entity) {
		return AABB.INFINITE;
	}
}
