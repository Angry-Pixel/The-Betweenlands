package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DecayPitGroundChainBlockEntity;
import thebetweenlands.common.block.structure.DecayPitGroundChainBlock;

public class DecayPitGroundChainRenderer implements BlockEntityRenderer<DecayPitGroundChainBlockEntity> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_chain.png");
	public final ModelPart model;

	public DecayPitGroundChainRenderer(BlockEntityRendererProvider.Context context) {
		this.model = context.bakeLayer(BLModelLayers.DECAY_PIT_CHAIN);
	}

	@Override
	public void render(DecayPitGroundChainBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float scroll = Mth.lerp(partialTick, entity.animationTicksChainPrev, entity.animationTicksChain) * 0.0078125F;
		Direction facing = entity.getBlockState().getValue(DecayPitGroundChainBlock.FACING);

		if (entity.isMoving()) {
			stack.pushPose();
			if (entity.isRaising()) {
				stack.translate(0.5F, 0.5F + scroll, 0.5F);
				stack.mulPose(facing.getRotation().rotateX(-Mth.HALF_PI));
				stack.scale(1.001F, -1.001F, -1.001F);
				this.model.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
			} else {
				stack.translate(0.5F, entity.getLength() + 1.5F - scroll, 0.5F);
				stack.mulPose(facing.getRotation().rotateX(-Mth.HALF_PI));
				stack.scale(1.001F, -1.001F, -1.001F);
				this.model.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
			}
			stack.popPose();
		}

		for (int len = 1; len <= entity.getLength(); len++) {
			stack.pushPose();
			stack.translate(0.5F, len + 0.5F + (entity.isRaising() ? scroll : -scroll), 0.5F);
			stack.mulPose(facing.getRotation().rotateX(-Mth.HALF_PI));
			stack.scale(1.001F, -1.001F, -1.001F);
			this.model.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, overlay);
			stack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(DecayPitGroundChainBlockEntity entity) {
		return new AABB(entity.getBlockPos()).expandTowards(0.0D, 4.0D, 0.0D);
	}
}
