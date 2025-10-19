package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.PossessedBlockModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.PossessedBlockEntity;
import thebetweenlands.common.block.structure.PossessedBlock;

public class PossessedBlockRenderer implements BlockEntityRenderer<PossessedBlockEntity> {

	private static final ResourceLocation TEXTURE =TheBetweenlands.prefix("textures/entity/block/possessed_block.png");
	private final PossessedBlockModel ghost;

	public PossessedBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.ghost = new PossessedBlockModel(context.bakeLayer(BLModelLayers.POSSESSED_BLOCK));
	}

	@Override
	public void render(PossessedBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		Direction dir = entity.getBlockState().getValue(PossessedBlock.FACING);
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		stack.pushPose();
		stack.scale(1.0F, -1.0F, -1.0F);
		this.ghost.animate(entity.active, entity.animationTicks, entity.moveProgress, partialTick);
		this.ghost.renderToBuffer(stack, buffer.getBuffer(BLRenderTypes.translucentCulling(TEXTURE)), LightTexture.FULL_BRIGHT, overlay);
		this.ghost.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(TEXTURE)), LightTexture.FULL_BRIGHT, overlay);
		stack.popPose();
		stack.popPose();
	}
}
