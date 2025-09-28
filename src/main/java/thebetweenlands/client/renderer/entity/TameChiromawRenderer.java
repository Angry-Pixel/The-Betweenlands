package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.TameChiromawModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.TameChiromaw;

public class TameChiromawRenderer extends MobRenderer<TameChiromaw, TameChiromawModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/chiromaw/tame_chiromaw.png");
	public static final ResourceLocation TEXTURE_LIGHTNING = TheBetweenlands.prefix("textures/entity/chiromaw/tame_lightning_chiromaw.png");

	public TameChiromawRenderer(EntityRendererProvider.Context context) {
		super(context, new TameChiromawModel(context.bakeLayer(BLModelLayers.CHIROMAW)), 0.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_glow.png")) {
			@Override
			public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, TameChiromaw entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				if (!entity.getElectricBoogaloo()) {
					super.render(stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
				}
			}
		});
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/chiromaw/tame_lightning_chiromaw_glow.png")) {
			@Override
			public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, TameChiromaw entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				if (entity.getElectricBoogaloo()) {
					super.render(stack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
				}
			}
		});
	}

	@Override
	protected void scale(TameChiromaw entity, PoseStack stack, float partialTick) {
		if (!entity.isInSittingPose() && !entity.isPassenger()) {
			float flap = Mth.sin((entity.tickCount + partialTick) * 0.5F) * 0.6F;
			stack.translate(0.0F, 0F - flap * 0.5F, 0.0F);
		}

		if (entity.isInSittingPose()) {
			stack.translate(0.0F, 2.125F, 0.0F);
			stack.mulPose(Axis.XP.rotationDegrees(180));
		} else if (entity.isPassenger()) {
			//something else
		} else {
			stack.mulPose(Axis.XP.rotationDegrees(40));
			stack.translate(0.0F, 0.0F, 0.4F);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(TameChiromaw entity) {
		return entity.getElectricBoogaloo()? TEXTURE_LIGHTNING : TEXTURE;
	}
}
