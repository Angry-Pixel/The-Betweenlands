package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.DarkDruidModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.DarkDruid;

public class DarkDruidRenderer extends MobRenderer<DarkDruid, DarkDruidModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/dark_druid.png");

	public DarkDruidRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkDruidModel(context.bakeLayer(BLModelLayers.DARK_DRUID)), 0.7F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/dark_druid_glow.png")));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkDruid entity) {
		return TEXTURE;
	}
}
