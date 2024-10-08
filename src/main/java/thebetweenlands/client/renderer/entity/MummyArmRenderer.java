package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.MummyArmModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.MummyArm;

public class MummyArmRenderer extends MobRenderer<MummyArm, MummyArmModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/peat_mummy.png");

	public MummyArmRenderer(EntityRendererProvider.Context context) {
		super(context, new MummyArmModel(context.bakeLayer(BLModelLayers.MUMMY_ARM)), 0.2F);
	}

	@Override
	protected void scale(MummyArm entity, PoseStack stack, float partialTickTime) {
		stack.scale(1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, entity.getYOffset() + 1.5D, 0.0D);
	}

	@Override
	public ResourceLocation getTextureLocation(MummyArm entity) {
		return TEXTURE;
	}
}
