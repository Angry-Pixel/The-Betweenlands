package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.TarBeastModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.TarBeast;

public class TarBeastRenderer extends MobRenderer<TarBeast, TarBeastModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/tar_beast.png");

	public TarBeastRenderer(EntityRendererProvider.Context context) {
		super(context, new TarBeastModel(context.bakeLayer(BLModelLayers.TAR_BEAST)), 0.7F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/tar_beast_eyes.png")));
		this.addLayer(new TarBeastEffectOverlay(this));
	}

	@Override
	protected void scale(TarBeast entity, PoseStack stack, float partialTick) {
		float scale = 1.0F / 40F * (entity.getGrowthFactor(partialTick));
		stack.scale(1.0F, scale, 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(TarBeast entity) {
		return TEXTURE;
	}

	public static class TarBeastEffectOverlay extends RenderLayer<TarBeast, TarBeastModel> {

		public static final ResourceLocation OVERLAY_TEXTURE = TheBetweenlands.prefix("textures/entity/tar_beast_overlay.png");

		public TarBeastEffectOverlay(RenderLayerParent<TarBeast, TarBeastModel> renderer) {
			super(renderer);
		}

		@Override
		public void render(PoseStack stack, MultiBufferSource source, int packedLight, TarBeast entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
			float f = (float) entity.tickCount + partialTick;
			TarBeastModel entitymodel = this.getParentModel();
			entitymodel.jawTar.visible = false;
			entitymodel.waistTar.visible = false;
			entitymodel.teeth.visible = false;
			entitymodel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			this.getParentModel().copyPropertiesTo(entitymodel);
			VertexConsumer vertexconsumer = source.getBuffer(BLRenderTypes.animatedLayer(OVERLAY_TEXTURE, 0, -f * 0.002F % 1.0F));
			entitymodel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			entitymodel.renderToBuffer(stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
			entitymodel.jawTar.visible = true;
			entitymodel.waistTar.visible = true;
			entitymodel.teeth.visible = true;

			vertexconsumer = source.getBuffer(RenderType.entityTranslucent(TEXTURE));

			if (entity.isShedding()) {
				float sheddingScale = (entity.getSheddingProgress() * entity.getSheddingProgress() * 0.002F) + 1.0F;

				stack.pushPose();
				stack.scale(sheddingScale, sheddingScale, sheddingScale);
				entitymodel.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.6F, 1.0F, 1.0F, 1.0F));
				stack.popPose();

				sheddingScale = (entity.getSheddingProgress() * entity.getSheddingProgress() * 0.004F) + 1.0F;

				stack.pushPose();
				stack.scale(sheddingScale, sheddingScale, sheddingScale);
				entitymodel.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.3F, 1.0F, 1.0F, 1.0F));
				stack.popPose();
			}

			if (entity.isPreparing()) {
				float sheddingScale = (1.0F - (entity.tickCount % 8) / 8.0F) * 0.15F + 1.0F;

				stack.pushPose();
				stack.scale(sheddingScale, sheddingScale, sheddingScale);
				entitymodel.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.6F, 1.0F, 1.0F, 1.0F));
				stack.popPose();

				sheddingScale = (1.0F - (entity.tickCount % 8) / 8.0F) * 0.2F + 1.05F;

				stack.pushPose();
				stack.scale(sheddingScale, sheddingScale, sheddingScale);
				entitymodel.renderToBuffer(stack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.6F, 1.0F, 1.0F, 1.0F));
				stack.popPose();
			}
		}
	}
}
