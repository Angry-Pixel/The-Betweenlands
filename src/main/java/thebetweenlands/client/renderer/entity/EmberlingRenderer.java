package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.EmberlingModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Emberling;

public class EmberlingRenderer extends MobRenderer<Emberling, EmberlingModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/emberling.png");
	public static final ResourceLocation TEXTURE_SLEEPING = TheBetweenlands.prefix("textures/entity/emberling_sleeping.png");

	public EmberlingRenderer(EntityRendererProvider.Context context) {
		super(context, new EmberlingModel(context.bakeLayer(BLModelLayers.EMBERLING)), 0.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/emberling_glow.png")) {
			@Override
			public void render(PoseStack stack, MultiBufferSource buffer, int light, Emberling entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				if (!entity.isInSittingPose()) {
					super.render(stack, buffer, light, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
				}
			}
		});
	}

	@Override
	public ResourceLocation getTextureLocation(Emberling entity) {
		return entity.isInSittingPose() ? TEXTURE_SLEEPING : TEXTURE;
	}
}
