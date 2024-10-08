package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.MireSnailModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.MireSnail;

public class MireSnailRenderer extends MobRenderer<MireSnail, MireSnailModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/mire_snail.png");

	public MireSnailRenderer(EntityRendererProvider.Context context) {
		super(context, new MireSnailModel(context.bakeLayer(BLModelLayers.MIRE_SNAIL)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(MireSnail entity) {
		return TEXTURE;
	}
}
