package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.StalkerModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Stalker;

public class StalkerRenderer extends ClimberRenderer<Stalker, StalkerModel> {

	public StalkerRenderer(EntityRendererProvider.Context context) {
		super(context, new StalkerModel(context.bakeLayer(BLModelLayers.STALKER)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Stalker entity) {
		return TheBetweenlands.prefix("textures/entity/stalker.png");
	}
}
