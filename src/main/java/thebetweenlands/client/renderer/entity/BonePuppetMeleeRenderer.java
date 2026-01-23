package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BonePuppetMeleeModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.BonePuppetMelee;

public class BonePuppetMeleeRenderer<T extends BonePuppetMelee> extends MobRenderer<T, BonePuppetMeleeModel<T>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/bone_puppet_melee.png");

	public BonePuppetMeleeRenderer(EntityRendererProvider.Context context) {
		super(context, new BonePuppetMeleeModel<>(context.bakeLayer(BLModelLayers.BONE_PUPPET_MELEE)), 0.5F);
	}
	
	@Override
	protected void scale(BonePuppetMelee entity, PoseStack stack, float partialTick) {
		stack.translate(0.0D, 1.25D - entity.getSpawningAnimation(partialTick) * 1.25D, 0.0D);
	}

	@Override
	public ResourceLocation getTextureLocation(BonePuppetMelee entity) {
		return TEXTURE;
	}
}
