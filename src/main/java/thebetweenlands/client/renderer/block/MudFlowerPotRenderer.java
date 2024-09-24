package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.common.block.entity.MudFlowerPotBlockEntity;

public class MudFlowerPotRenderer implements BlockEntityRenderer<MudFlowerPotBlockEntity> {

	private final BlockRenderDispatcher renderer;

	public MudFlowerPotRenderer(BlockEntityRendererProvider.Context context) {
		this.renderer = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(MudFlowerPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (entity.hasFlowerBlock()) {
			stack.pushPose();
			stack.translate(0.25F, 0.375F, 0.25F);
			stack.scale(0.5F, 0.5F, 0.5F);
			if (entity.getFlowerBlock() == Blocks.CACTUS) {
				stack.translate(0.25F, 0.0F, 0.25F);
				stack.scale(0.5F, 0.75F, 0.5F);

			}
			this.renderer.renderSingleBlock(entity.getFlowerBlock().defaultBlockState(), stack, source, light, overlay, ModelData.EMPTY, null);
			stack.popPose();
		}
	}
}
