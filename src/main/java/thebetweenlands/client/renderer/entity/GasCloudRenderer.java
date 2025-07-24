package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.GasCloudModel;
import thebetweenlands.client.shader.postprocessing.Warp;
import thebetweenlands.common.entity.monster.GasCloud;

public class GasCloudRenderer extends MobRenderer<GasCloud, GasCloudModel> {
	public GasCloudRenderer(EntityRendererProvider.Context context) {
		super(context, new GasCloudModel(context.bakeLayer(BLModelLayers.GAS_CLOUD)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(GasCloud gasCloud) {
		return Warp.GAS_PARTICLE_TEXTURE;
	}
}
