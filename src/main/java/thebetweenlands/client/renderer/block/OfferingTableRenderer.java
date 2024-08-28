package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.CenserBlock;
import thebetweenlands.common.block.OfferingTableBlock;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.block.entity.OfferingTableBlockEntity;

public class OfferingTableRenderer implements BlockEntityRenderer<OfferingTableBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/offering_table.png"));
	private final ModelPart table;

	public OfferingTableRenderer(BlockEntityRendererProvider.Context context) {
		this.table = context.bakeLayer(BLModelLayers.OFFERING_TABLE);
	}

	@Override
	public void render(OfferingTableBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(OfferingTableBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		this.table.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (!entity.getTheItem().isEmpty()) {
			stack.pushPose();
			stack.translate(0.5F, 0.5F, 0.5F);
			stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(OfferingTableBlock.FACING).toYRot()));
			stack.scale(0.5F, 0.5F, 0.5F);

			if (!Minecraft.getInstance().getItemRenderer().getModel(entity.getTheItem(), null, null, 0).isGui3d()) {
				stack.mulPose(Axis.YP.rotationDegrees(180.0F));
			}

			Minecraft.getInstance().getItemRenderer().renderStatic(entity.getTheItem(), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}
}
