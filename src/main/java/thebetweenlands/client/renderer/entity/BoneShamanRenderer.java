package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BoneShamanModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.BoneShaman;

public class BoneShamanRenderer<T extends BoneShaman> extends MobRenderer<T, BoneShamanModel<T>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bone_shaman.png");

	public BoneShamanRenderer(EntityRendererProvider.Context context) {
		super(context, new BoneShamanModel<>(context.bakeLayer(BLModelLayers.BONE_SHAMAN)), 0.5F);
	}

	@Override
	protected void scale(BoneShaman entity, PoseStack stack, float partialTick) {
		shadowStrength = 0F;
		if (entity.getSpawningAnimation(partialTick) <= 0.5F) {
			stack.translate(0.0D, 3.2D - entity.getSpawningAnimation(partialTick) * 6.4D, 0.0D);
			stack.mulPose(Axis.YP.rotationDegrees(entity.getSpawningAnimation(partialTick) * 720F));
		}
		shadowRadius = (float) (entity.getSpawningAnimation(partialTick) * 0.5D);
		shadowStrength = 1F;
	}

	@Override
	protected float getFlipDegrees(BoneShaman livingEntity) {
		return 0F;
	}

	@Override
	public ResourceLocation getTextureLocation(BoneShaman entity) {
		return TEXTURE;
	}
}
