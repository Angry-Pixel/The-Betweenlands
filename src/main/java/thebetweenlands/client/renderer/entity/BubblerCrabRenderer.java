package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BubblerCrabModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.fishing.BubblerCrab;

public class BubblerCrabRenderer extends MobRenderer<BubblerCrab, BubblerCrabModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bubbler_crab.png");

	public BubblerCrabRenderer(EntityRendererProvider.Context context) {
		super(context, new BubblerCrabModel(context.bakeLayer(BLModelLayers.BUBBLER_CRAB)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(BubblerCrab entity) {
		return TEXTURE;
	}
}
