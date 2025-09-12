package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SludgeWormEggSacModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.SludgeWormEggSac;

public class SludgeWormEggSacRenderer extends MobRenderer<SludgeWormEggSac, SludgeWormEggSacModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/worm_egg_sac.png");

	public SludgeWormEggSacRenderer(EntityRendererProvider.Context context) {
		super(context, new SludgeWormEggSacModel(context.bakeLayer(BLModelLayers.SLUDGE_WORM_EGG_SAC)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(SludgeWormEggSac entity) {
		return TEXTURE;
	}
}
