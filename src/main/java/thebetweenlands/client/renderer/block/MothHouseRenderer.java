package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.MothHouseBlock;
import thebetweenlands.common.block.entity.MothHouseBlockEntity;

import java.util.List;

public class MothHouseRenderer implements BlockEntityRenderer<MothHouseBlockEntity> {

	private static final List<RenderType> TEXTURES = List.of(
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mothhouse_0.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mothhouse_1.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mothhouse_2.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mothhouse_3.png")));

	private final ModelPart house;
	private final ItemRenderer itemRenderer;

	public MothHouseRenderer(BlockEntityRendererProvider.Context context) {
		this.house = context.bakeLayer(BLModelLayers.MOTH_HOUSE);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(MothHouseBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		int stage = Mth.clamp(entity.getSilkRenderStage(), 0, 3);
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(MothHouseBlock.FACING).toYRot()));
		if (entity.getBlockState().getValue(MothHouseBlock.ON_WALL)) {
			stack.translate(0.0F, 0.01F, -0.38F);
		}
		stack.scale(1.0F, -1.0F, -1.0F);
		this.house.render(stack, source.getBuffer(TEXTURES.get(stage)), light, overlay);

		if (entity.getLevel() != null && !entity.isSilkProductionFinished()) {
			ItemStack grubs = entity.getItem(MothHouseBlockEntity.SLOT_GRUBS);

			if (!grubs.isEmpty()) {
				this.renderGrubs(grubs, stack, source, light, overlay);
			}
		}
		stack.popPose();
	}

	@SuppressWarnings("fallthrough") //intended fallthrough here
	private void renderGrubs(ItemStack item, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (item.getCount() > 0) {
			switch (item.getCount()) {
				default:
				case 6:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(0.5D, -4.25D, 0.4D);
					stack.mulPose(Axis.ZP.rotationDegrees(10));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				case 5:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(-0.5D, -3.75D, 0.4D);
					stack.mulPose(Axis.ZN.rotationDegrees(80));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				case 4:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(1.1D, -3.3D, 0.4D);
					stack.mulPose(Axis.ZN.rotationDegrees(8));
					stack.mulPose(Axis.YP.rotationDegrees(180));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				case 3:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(0.3D, -1.4D, 0.4D);
					stack.mulPose(Axis.ZN.rotationDegrees(75));
					stack.mulPose(Axis.YP.rotationDegrees(180));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				case 2:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(-1.0D, -2.0D, 0.4D);
					stack.mulPose(Axis.ZP.rotationDegrees(5));
					stack.mulPose(Axis.YP.rotationDegrees(180));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
				case 1:
					stack.pushPose();
					stack.scale(0.15F, 0.15F, 0.15F);
					stack.translate(0.5D, -2.5D, 0.4D);
					stack.mulPose(Axis.ZN.rotationDegrees(20));
					stack.mulPose(Axis.XP.rotationDegrees(180));
					this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
					stack.popPose();
			}
		}
	}
}
