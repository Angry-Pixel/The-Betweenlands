package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.block.container.ItemShelfBlock;
import thebetweenlands.common.block.entity.ItemShelfBlockEntity;

public class ItemShelfRenderer implements BlockEntityRenderer<ItemShelfBlockEntity> {

	private final ItemRenderer itemRenderer;

	public ItemShelfRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(ItemShelfBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (entity.getLevel() != null) {
			Direction dir = entity.getBlockState().getValue(ItemShelfBlock.FACING);
			stack.pushPose();
			stack.translate(0.5F, 0.0F, 0.5F);
			stack.mulPose(Axis.YP.rotationDegrees(dir.toYRot() + 180));

			int index = 0;
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					ItemStack item = entity.getItem(index);
					if (!item.isEmpty()) {
						stack.pushPose();
						stack.translate(
							(dir.getAxis() == Direction.Axis.X ? (0.25F - i * 0.5F) : (-0.25F + i * 0.5F)),
							0.6625F - j * 0.5F,
							dir.getAxis() == Direction.Axis.X ? 0.25F : -0.25F
						);
						stack.scale(0.4F, 0.4F, 0.4F);
						this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
						stack.popPose();
					}
					index++;
				}
			}
			stack.popPose();
		}
	}
}
