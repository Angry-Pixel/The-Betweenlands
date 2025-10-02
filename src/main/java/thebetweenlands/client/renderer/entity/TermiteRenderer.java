package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.TermiteModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Termite;

public class TermiteRenderer extends MobRenderer<Termite, TermiteModel> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/termite.png");

	public TermiteRenderer(EntityRendererProvider.Context context) {
		super(context, new TermiteModel(context.bakeLayer(BLModelLayers.TERMITE)), 0.5F);
	}

	@Override
	protected void scale(Termite livingEntity, PoseStack poseStack, float partialTickTime) {
		super.scale(livingEntity, poseStack, partialTickTime);
	}

	@Override
	public ResourceLocation getTextureLocation(Termite entity) {
		return TEXTURE;
	}
}
