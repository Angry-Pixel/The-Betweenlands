package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.CenserModel;
import thebetweenlands.client.model.block.CompostBinModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.CenserBlock;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.block.entity.CompostBinBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class CompostBinRenderer implements BlockEntityRenderer<CompostBinBlockEntity> {

	private final ModelPart bin;
	private final ModelPart lid;

	public CompostBinRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart part = context.bakeLayer(BLModelLayers.COMPOST_BIN);
		this.bin = part;
		this.lid = part.getChild("bin_top");
	}

	@Override
	public void render(CompostBinBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		float compostHeight = Math.min(entity.getCompostedAmount() / (float) CompostBinBlockEntity.MAX_COMPOST_AMOUNT, 0.82F);

		if (compostHeight > 0.01F) {
			BlockState compost = BlockRegistry.COMPOST_BLOCK.get().defaultBlockState();

			stack.pushPose();
			RenderSystem.disableCull();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			stack.translate(0.5D, 0.005D, 0.5D);
			stack.scale(0.8f, compostHeight, 0.8f);
			//TODO render block pixel perfect instead of just stretching the block
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(compost, stack, source, light, overlay);
			RenderSystem.enableCull();
			stack.popPose();
		}


		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, -0.5F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(CenserBlock.FACING).toYRot()));
		this.lid.zRot = entity.getLevel() != null ? (float) Math.toRadians(entity.getLidAngle(entity.getBlockState(), partialTicks)) : 0.0F;
		this.bin.render(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/compost_bin.png"))), light, overlay);
		stack.popPose();

		for (int i = 0; i < entity.getContainerSize(); i++) {
			ItemStack item = entity.getItem(i);

			if (!item.isEmpty()) {
				stack.pushPose();

				// 0.4 for items, 0.5 for compost
				stack.translate(0, 0.005F + compostHeight + i * 0.4f / entity.getContainerSize(), 0.08F);
				stack.scale(0.6F, 0.6F, 0.6F);
				stack.mulPose(Axis.YP.rotationDegrees(RandomSource.create(i * 12315L).nextFloat() * 360.0F));
				stack.mulPose(Axis.XP.rotationDegrees(90.0f));

				Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);

				stack.popPose();
			}
		}
	}
}
