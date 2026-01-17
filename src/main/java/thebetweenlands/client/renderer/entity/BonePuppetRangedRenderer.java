package thebetweenlands.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BonePuppetRangedModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.BonePuppetRanged;

public class BonePuppetRangedRenderer<T extends BonePuppetRanged> extends MobRenderer<T, BonePuppetRangedModel<T>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bone_puppet_ranged.png");

	public BonePuppetRangedRenderer(EntityRendererProvider.Context context) {
		super(context, new BonePuppetRangedModel<>(context.bakeLayer(BLModelLayers.BONE_PUPPET_RANGED)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(BonePuppetRanged entity) {
		return TEXTURE;
	}
}
