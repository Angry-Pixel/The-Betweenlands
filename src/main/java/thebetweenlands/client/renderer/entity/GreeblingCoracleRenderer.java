package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.GreeblingCoracleModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.GreeblingCoracle;

public class GreeblingCoracleRenderer extends MobRenderer<GreeblingCoracle, GreeblingCoracleModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/greebling_coracle.png");

	public GreeblingCoracleRenderer(EntityRendererProvider.Context context) {
		super(context, new GreeblingCoracleModel(context.bakeLayer(BLModelLayers.GREEBLING_CORACLE)), 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(GreeblingCoracle entity) {
		return TEXTURE;
	}
}
