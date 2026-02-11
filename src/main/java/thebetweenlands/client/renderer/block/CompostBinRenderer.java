package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.CompostBinBlock;
import thebetweenlands.common.block.entity.CompostBinBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class CompostBinRenderer implements BlockEntityRenderer<CompostBinBlockEntity> {
	public static final float COMPOST_BLOCK_SCALE = 0.8F;

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/compost_bin.png"));
	private final ModelPart bin;
	private final ModelPart lid;
	private final ItemRenderer itemRenderer;

	public CompostBinRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart part = context.bakeLayer(BLModelLayers.COMPOST_BIN);
		this.bin = part;
		this.lid = part.getChild("bin_top");
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(CompostBinBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		float compostHeight = Math.min((float) entity.getCompostedAmount() / (float) entity.getMaximimumCompostAmount() * 1.25F, 1.025F) * COMPOST_BLOCK_SCALE;

		if (compostHeight > 0.01F) {
			BlockState compost = BlockRegistry.COMPOST_BLOCK.get().defaultBlockState();

			stack.pushPose();
			stack.translate(0.5D - COMPOST_BLOCK_SCALE * 0.5D, 0.005D, 0.5D - COMPOST_BLOCK_SCALE * 0.5D);
			stack.scale(COMPOST_BLOCK_SCALE, compostHeight, COMPOST_BLOCK_SCALE);
			//TODO render block pixel perfect instead of just stretching the block
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(compost, stack, source, light, overlay);
			stack.popPose();
		}


		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(CompostBinBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.lid.zRot = entity.getLevel() != null ? (float) Math.toRadians(entity.getLidAngle(entity.getBlockState(), partialTicks)) : 0.0F;
		this.bin.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		for (int i = 0; i < entity.getContainerSize(); i++) {
			ItemStack item = entity.getItem(i);

			if (!item.isEmpty()) {
				stack.pushPose();

				// 0.4 for items, 0.5 for compost
				stack.translate(0.5F, 0.005F + compostHeight + i * 0.4f / entity.getContainerSize(), 0.5F + 0.08F);
				stack.scale(0.6F, 0.6F, 0.6F);
				stack.mulPose(Axis.YP.rotationDegrees(RandomSource.create(i * 12315L).nextFloat() * 360.0F));
				stack.mulPose(Axis.XP.rotationDegrees(90.0f));

				this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);

				stack.popPose();
			}
		}
	}
}
