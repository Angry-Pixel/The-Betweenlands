package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.EntityWight;

public class RenderWight<T extends EntityWight> extends MobRenderer<T, ModelWight<T>> {

	private static final ResourceLocation WIGHT_TEXTURE = TheBetweenlands.prefix("textures/entity/wight.png");
	public static final ModelLayerLocation WIGHT_MODEL_LAYER = new ModelLayerLocation(TheBetweenlands.prefix("main"), "wight");
	public static final RenderType WIGHT_RENDER_TYPE = RenderType.entityTranslucent(WIGHT_TEXTURE, true);

	float ang;

	public RenderWight(EntityRendererProvider.Context p_174401_) {
		this(p_174401_, WIGHT_MODEL_LAYER);
		this.shadowRadius = 0.5F;
	}

	public RenderWight(EntityRendererProvider.Context p_174403_, ModelLayerLocation p_174404_) {
		super(p_174403_, new ModelWight<>(p_174403_.bakeLayer(p_174404_)), 0.8F);
	}

	@Override
	public void render(T entity, float p_115456_, float partialTicks, PoseStack pose, MultiBufferSource p_115459_,
					   int p_115460_) {

		model.alpha = 1F - entity.getHidingAnimation(partialTicks) * 0.5F;

		if (entity.isVolatile()) {
			this.model.renderHeadOnly = true;
			this.shadowRadius = 0;
			pose.scale(0.5f, 0.5f, 0.5f);
			pose.translate(0, 1.0D, 0);

			if (entity.getRidingEntity() != null) {
				// SPIiiEEENnnNNnn
				pose.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) / 30.0F * 360.0F));
				pose.translate(0, -entity.getRidingEntity().getEyeHeight() + 1.65D, 0.8D);
			}
			VertexConsumer vertexconsumer = p_115459_.getBuffer(WIGHT_RENDER_TYPE);

			model.getHead().render(pose, vertexconsumer, p_115460_, p_115460_);
			model.getHead2().render(pose, vertexconsumer, p_115460_, p_115460_);
			model.getHead3().render(pose, vertexconsumer, p_115460_, p_115460_);
			model.getJaw().render(pose, vertexconsumer, p_115460_, p_115460_);

			super.render(entity, p_115456_, partialTicks, pose, p_115459_, p_115460_);

			return;
		} else {
			this.model.renderHeadOnly = false;
		}

		this.shadowRadius = 0.5f;

		float scale = 0.9F / 40F * (entity.getGrowthFactor(partialTicks));
		pose.scale(0.9F, scale, 0.9F);

		model.renderHeadOnly = false;

		super.render(entity, p_115456_, partialTicks, pose, p_115459_, p_115460_);

		// just incase
		model.alpha = 1f;
	}

	public void scale(T entity, PoseStack pose, float partialTicks) {
		if (entity.isVolatile()) {
			pose.scale(0.5f, 0.5f, 0.5f);
			pose.translate(0, 1.0D, 0);
			return;
		}
		float scale = 0.9F / 40F * (entity.getGrowthFactor(partialTicks));
		pose.scale(0.9F, scale, 0.9F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityWight p_114482_) {
		return WIGHT_TEXTURE;
	}

	@Override
	protected RenderType getRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
		// TODO Auto-generated method stub
		return WIGHT_RENDER_TYPE;
	}
}
