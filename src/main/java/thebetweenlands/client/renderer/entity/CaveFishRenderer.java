package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.CaveFishModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.CaveFish;

public class CaveFishRenderer extends MobRenderer<CaveFish, CaveFishModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/cave_fish.png");

	public CaveFishRenderer(EntityRendererProvider.Context context) {
		super(context, new CaveFishModel(context.bakeLayer(BLModelLayers.CAVE_FISH)), 0.25F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/cave_fish_leader_glow.png")) {
			@Override
			public void render(PoseStack stack, MultiBufferSource buffer, int light, CaveFish entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				if (entity.isLeader()) {
					super.render(stack, buffer, light, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
				}
			}
		});
	}

	@Override
	protected void scale(CaveFish entity, PoseStack stack, float partialTick) {
		if(!entity.isLeader()) {
			stack.scale(0.5F, 0.5F, 0.5F);
		}
		stack.translate(0, -0.25F, 0);
		float smoothedPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
		stack.mulPose(Axis.XP.rotationDegrees(smoothedPitch));
		stack.translate(0, 0.75F, 0);
	}

	@Override
	public ResourceLocation getTextureLocation(CaveFish entity) {
		return TEXTURE;
	}
}
