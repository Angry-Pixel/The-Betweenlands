package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.InfestationModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.infestation.Infestation;

public class InfestationRenderer extends MultiPieceMobModelRenderer<Infestation, InfestationModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/infestation.png");

	public InfestationRenderer(EntityRendererProvider.Context context) {
		super(context, new InfestationModel(context.bakeLayer(BLModelLayers.INFESTATION)), 0.0F);
	}

	@Override
	protected void renderModel(Infestation entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state) {
		RenderType rendertype = this.getRenderType(entity, state.visible(), state.translucent(), state.glowing());
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);

			stack.pushPose();

			stack.scale(0.4f, 0.4f, 0.4f);

			int count = Math.round(entity.getSwarmSize() * 4);

			float offset = (float) Math.abs(Math.sin(state.ageInTicks() * 0.01f) * 0.05f) + 0.15f;

			float radius = count == 1 ? 0 : (count / 4.0f * 0.45f + 0.2f);

			float move = Math.max((1 - state.limbSwingAmount() / 0.3f), 0);

			for(int i = 0; i < count; i++) {
				float time1 = state.ageInTicks();

				float ox = Mth.cos(i / 4.0f * Mth.PI * 2) * radius + Mth.sin(time1 * 0.1f * (1 + i / 4.0f * 0.3f) + i * 0.34f) * offset;
				float oy = i / 4.0f * 0.5f + 1.5f + Mth.sin(time1 * 0.2f * (1 + i / 4.0f * 0.2f) + i * 1.5f) * offset;
				float oz = Mth.sin(i / 4.0f * Mth.PI * 2) * radius + Mth.sin(time1 * 0.05f * (1 + i / 4.0f * 0.5f) - i * 2.4f) * offset;

				stack.pushPose();
				stack.translate(ox, oy, oz);
				stack.mulPose(Axis.YP.rotationDegrees(state.yRot() + 180));
				stack.translate(0, 0, (1 - move) * 0.5f);

				this.model.renderToBuffer(stack, vertexconsumer, packedLight, overlay, color);

				stack.popPose();
			}

			stack.popPose();
		}
	}

	@Override
	protected float getFlipDegrees(Infestation livingEntity) {
		return 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(Infestation entity) {
		return TEXTURE;
	}
}
