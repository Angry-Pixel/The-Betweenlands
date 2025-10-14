package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ChiromawMatriarchModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;

public class ChiromawMatriarchRenderer extends MobRenderer<ChiromawMatriarch, ChiromawMatriarchModel> {

	protected static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_matriarch.png");

	public ChiromawMatriarchRenderer(EntityRendererProvider.Context context) {
		super(context, new ChiromawMatriarchModel(context.bakeLayer(BLModelLayers.CHIROMAW_MATRIARCH)), 1.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_matriarch_glow.png")));
	}

	@Override
	protected void scale(ChiromawMatriarch entity, PoseStack stack, float partialTick) {
		stack.scale(1.5F, 1.5F, 1.5F);

		float flyingPercent = Math.max(0, 1.0f - entity.landingTimer.getAnimationProgressSmooth(partialTick) - entity.nestingTimer.getAnimationProgressSmooth(partialTick) - entity.spinningTimer.getAnimationProgressSmooth(partialTick));

		float flap = Mth.sin((entity.tickCount + partialTick) * 0.5F) * 0.6F;
		stack.translate(0.0F, -flap * 0.5F * flyingPercent, 0.0F);

		stack.mulPose(Axis.XP.rotationDegrees(20.0F * flyingPercent));

		if (entity.isSpinning()) {
			float spinningRotation = Mth.lerp(partialTick, entity.previousSpinAngle, entity.spinAngle);
			stack.mulPose(Axis.YP.rotationDegrees(spinningRotation));
		}
	}

	@Override
	public ResourceLocation getTextureLocation(ChiromawMatriarch entity) {
		return TEXTURE;
	}
}
