package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.FrogModel;
import thebetweenlands.common.entity.creature.frog.Frog;

public class FrogRenderer extends MobRenderer<Frog, FrogModel> {
	public FrogRenderer(EntityRendererProvider.Context context) {
		super(context, new FrogModel(context.bakeLayer(BLModelLayers.FROG)), 0.2F);
	}

	@Override
	public ResourceLocation getTextureLocation(Frog entity) {
		return entity.getVariant().value().texture();
	}
}
