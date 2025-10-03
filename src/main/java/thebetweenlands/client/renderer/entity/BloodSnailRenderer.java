package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BloodSnailModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.BloodSnail;

public class BloodSnailRenderer extends MobRenderer<BloodSnail, BloodSnailModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/blood_snail.png");

	public BloodSnailRenderer(EntityRendererProvider.Context context) {
		super(context, new BloodSnailModel(context.bakeLayer(BLModelLayers.BLOOD_SNAIL)), 0.2F);
	}

	@Override
	public ResourceLocation getTextureLocation(BloodSnail entity) {
		return TEXTURE;
	}
}
