package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.DungeonDoorRunesModel;
import thebetweenlands.common.block.entity.DungeonDoorCombinationBlockEntity;
import thebetweenlands.common.block.structure.DungeonDoorCombinationBlock;

public class DungeonDoorCombinationRenderer implements BlockEntityRenderer<DungeonDoorCombinationBlockEntity> {

	private final DungeonDoorRunesModel model;

	public DungeonDoorCombinationRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new DungeonDoorRunesModel(context.bakeLayer(BLModelLayers.DUNGEON_DOOR_RUNES));
	}

	@Override
	public void render(DungeonDoorCombinationBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		if (entity.getLevel() != null) {
			Direction facing = entity.getBlockState().getValue(DungeonDoorCombinationBlock.FACING);

			stack.pushPose();
			stack.translate(0.5F, 0.5F, 0.5F);
			stack.mulPose(facing.getRotation().rotateX(-Mth.HALF_PI));
			stack.scale(1.0F, -1.0F, -1.0F);
			this.model.renderTopLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.topCode), entity.renderTicks, partialTick, stack, buffer, light, overlay);
			this.model.renderMiddleLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.midCode), entity.renderTicks, partialTick, stack, buffer, light, overlay);
			this.model.renderBottomLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.bottomCode), entity.renderTicks, partialTick, stack, buffer, light, overlay);
			stack.popPose();
		}
	}
}
