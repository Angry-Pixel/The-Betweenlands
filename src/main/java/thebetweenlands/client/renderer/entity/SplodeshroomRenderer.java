package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SplodeshroomModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Splodeshroom;

public class SplodeshroomRenderer extends MobRenderer<Splodeshroom, SplodeshroomModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/splodeshroom.png");

	public SplodeshroomRenderer(EntityRendererProvider.Context context) {
		super(context, new SplodeshroomModel(context.bakeLayer(BLModelLayers.SPLODESHROOM)), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(Splodeshroom entity) {
		return TEXTURE;
	}
}
