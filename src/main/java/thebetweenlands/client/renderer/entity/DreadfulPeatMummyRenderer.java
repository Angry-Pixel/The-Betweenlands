package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.DreadfulPeatMummyModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;

public class DreadfulPeatMummyRenderer extends MobRenderer<DreadfulPeatMummy, DreadfulPeatMummyModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/dreadful_mummy.png");

	public DreadfulPeatMummyRenderer(EntityRendererProvider.Context context) {
		super(context, new DreadfulPeatMummyModel(context.bakeLayer(BLModelLayers.DREADFUL_PEAT_MUMMY)), 0.7F);
	}

	@Override
	public void render(DreadfulPeatMummy entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (entity.getSpawningProgress(packedLight) > 0) {
			this.shadowRadius = Math.max(0, entity.getSpawningProgress(partialTicks) * 1 - 0.3F);
			super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		}
	}

	@Override
	protected void scale(DreadfulPeatMummy entity, PoseStack stack, float partialTick) {
		stack.translate(0.0D, -entity.getInterpolatedYOffsetProgress(partialTick), 0.0D);
	}

	@Override
	public ResourceLocation getTextureLocation(DreadfulPeatMummy entity) {
		return TEXTURE;
	}
}
