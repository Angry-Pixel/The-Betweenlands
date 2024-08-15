package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.Wight;
import thebetweenlands.common.world.event.WinterEvent;

public class RenderWight<T extends Wight> extends MobRenderer<T, ModelWight<T>> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/wight.png");
	public static final ResourceLocation TEXTURE_FROSTY =TheBetweenlands.prefix("textures/entity/wight_frosty.png");
	public static final ModelLayerLocation WIGHT_MODEL_LAYER = new ModelLayerLocation(TheBetweenlands.prefix("main"), "wight");

	public RenderWight(EntityRendererProvider.Context context) {
		super(context, new ModelWight<>(context.bakeLayer(WIGHT_MODEL_LAYER)), 0.5F);
	}

	@Override
	public void render(T entity, float yaw, float partialTicks, PoseStack stack, MultiBufferSource source, int light) {
		if(!entity.isVolatile()) {
			stack.pushPose();
			RenderSystem.disableBlend();
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.setShaderColor(1, 1, 1, 1);

			super.render(entity, yaw, partialTicks, stack, source, light);

			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F - entity.getHidingAnimation(partialTicks) * 0.5F);

			super.render(entity, yaw, partialTicks, stack, source, light);

			stack.popPose();
		} else {
			this.model.renderHeadOnly = true;

			stack.pushPose();
			RenderSystem.disableBlend();
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.setShaderColor(1, 1, 1, 1);

			if (entity.getVehicle() != null) {
				stack.scale(-1.0F, -1.0F, 1.0F);
				stack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) / 30.0F * 360.0F));
				stack.mulPose(Axis.YP.rotationDegrees(180));
				stack.translate(0, -entity.getVehicle().getEyeHeight() + 1.65D, 0.8D);
				stack.scale(0.5F, 0.5F, 0.5F);

				VertexConsumer consumer = source.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity), true));
				this.model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);

				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1F, 1F, 1F, 0.4F);

				this.model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);
			} else {
				super.render(entity, yaw, partialTicks, stack, source, light);

				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1F, 1F, 1F, 0.4F);

				super.render(entity, yaw, partialTicks, stack, source, light);
			}

			stack.popPose();
		}
	}

	public void scale(T entity, PoseStack stack, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(), 10.0f, -1, -1, -1));
		}

		float scale = 0.9F / 40F * (entity.getGrowthFactor(partialTicks));
		stack.scale(0.9F, scale, 0.9F);

		if (entity.isVolatile()) {
			stack.scale(0.5f, 0.5f, 0.5f);
			stack.translate(0, 1.0D, 0);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Wight entity) {
		if (WinterEvent.isFroooosty(entity.level())) {
			return TEXTURE_FROSTY;
		}
		return TEXTURE;
	}

}
