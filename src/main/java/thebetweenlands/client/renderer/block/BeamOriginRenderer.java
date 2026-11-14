package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.handler.BeamOriginMirrorWorldRenderHandler;
import thebetweenlands.client.renderer.BeamOriginMirrorWorldRenderer;
import thebetweenlands.common.block.entity.BeamOriginBlockEntity;

public class BeamOriginRenderer implements BlockEntityRenderer<BeamOriginBlockEntity> {

	public BeamOriginRenderer(BlockEntityRendererProvider.Context context) {
		// NO-OP
	}
	
	@Override
	public AABB getRenderBoundingBox(BeamOriginBlockEntity blockEntity) {
		return new AABB(-2.0D / 16.0, -8.0D / 16.0, -2.0D / 16.0, 18.0D / 16.0, 19.0D / 16.0, 18.0D / 16.0).move(blockEntity.getBlockPos());
	}

	@Override
	public void render(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

		Minecraft.getInstance().getProfiler().push("thebetweenlands:beam_origin");

		poseStack.pushPose();
		poseStack.translate(0.5F, -1.5F, 0.5F);

		float visibility = Mth.lerp(partialTick, blockEntity.prevVisibility, blockEntity.visibility);

		float rotation = Mth.lerp(partialTick, blockEntity.prevRotation, blockEntity.rotation);

		boolean renderDeferred = false;
		// If we're in-world, submit this to the mirror world renderer to render the mirror world
		if (blockEntity.hasLevel() && blockEntity.getLevel() != null) {
			renderDeferred = BeamOriginMirrorWorldRenderHandler.submitBeamOrigin(
					poseStack.last(), visibility, rotation,
					packedLight, packedOverlay,
					blockEntity.clientMirrorEntities, blockEntity.getBlockPos());
		}
		
		// Render the beam origin prism
		if (!renderDeferred) {
			BeamOriginMirrorWorldRenderer.INSTANCE.renderPrism(poseStack, bufferSource, rotation, visibility);
		}

		poseStack.popPose();

		Minecraft.getInstance().getProfiler().pop();
	}

}
