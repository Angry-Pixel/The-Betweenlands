package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.DreadfulPeatMummyModel;
import thebetweenlands.client.model.entity.PeatMummyModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;
import thebetweenlands.common.entity.monster.PeatMummy;

public class PeatMummyRenderer extends MobRenderer<PeatMummy, PeatMummyModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/peat_mummy.png");

	public PeatMummyRenderer(EntityRendererProvider.Context context) {
		super(context, new PeatMummyModel(context.bakeLayer(BLModelLayers.PEAT_MUMMY)), 0.7F);
	}

	@Override
	public void render(PeatMummy entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		if(entity.getSpawningProgress() >= 0.1F) {
			super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
		}
	}

	@Override
	protected void scale(PeatMummy entity, PoseStack stack, float partialTick) {
		stack.translate(0.0D, -entity.getInterpolatedSpawningOffset(partialTick), 0.0D);
	}

	@Override
	protected float getShadowRadius(PeatMummy entity) {
		return entity.getSpawningProgress() >= 0.1F ? super.getShadowRadius(entity) : 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(PeatMummy entity) {
		return TEXTURE;
	}
}
