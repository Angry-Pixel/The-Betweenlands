package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.RockSnotModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.RockSnot;

public class RockSnotRenderer extends MobRenderer<RockSnot, RockSnotModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/rock_snot.png");

	public RockSnotRenderer(Context context) {
		super(context, new RockSnotModel(context.bakeLayer(BLModelLayers.ROCK_SNOT)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(RockSnot entity) {
		return TEXTURE;
	}
}
