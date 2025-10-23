package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DecayPitGroundChainBlockEntity;
import thebetweenlands.common.block.entity.DecayPitHangingChainBlockEntity;

public class DecayPitHangingChainRenderer implements BlockEntityRenderer<DecayPitHangingChainBlockEntity> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_chain.png");
	public final ModelPart model;

	public DecayPitHangingChainRenderer(BlockEntityRendererProvider.Context context) {
		this.model = context.bakeLayer(BLModelLayers.DECAY_PIT_CHAIN);
	}

	@Override
	public void render(DecayPitHangingChainBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		for (int len = 0; len <= 4 + Mth.floor(entity.getProgress() * 0.0078125F); len++) {
			this.renderChainPart(stack, buffer, light, overlay, 1.5D, len - entity.getProgress() * 0.0078125, 0.5D, 0F);
			this.renderChainPart(stack, buffer, light, overlay, -0.5D, len - entity.getProgress() * 0.0078125, 0.5D, 180F);
			this.renderChainPart(stack, buffer, light, overlay, 0.5D, len - entity.getProgress() * 0.0078125, 1.5D, 90F);
			this.renderChainPart(stack, buffer, light, overlay, 0.5D, len - entity.getProgress() * 0.0078125, -0.5D, 270F);
		}
	}

	private void renderChainPart(PoseStack stack, MultiBufferSource buffer, int light, int overlay, double x, double y, double z, float angle) {
		stack.pushPose();
		stack.translate(x, y - 2.5D, z);
		stack.scale(1.001F, -1.001F, -1.001F);
		stack.mulPose(Axis.YP.rotationDegrees(angle));
		this.model.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
		stack.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(DecayPitHangingChainBlockEntity entity) {
		return new AABB(entity.getBlockPos()).inflate(1.0D, 0.0D, 1.0D).expandTowards(0.0D, -4.0D, 0.0D);
	}
}
