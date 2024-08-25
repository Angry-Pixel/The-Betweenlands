package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SiltCrabModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.fishing.SiltCrab;

public class SiltCrabRenderer extends MobRenderer<SiltCrab, SiltCrabModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/silt_crab.png");

	public SiltCrabRenderer(EntityRendererProvider.Context context) {
		super(context, new SiltCrabModel(context.bakeLayer(BLModelLayers.SILT_CRAB)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(SiltCrab entity) {
		return TEXTURE;
	}
}
