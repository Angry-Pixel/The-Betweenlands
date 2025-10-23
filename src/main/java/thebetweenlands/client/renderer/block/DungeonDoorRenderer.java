package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.DungeonDoorModel;
import thebetweenlands.client.model.block.DungeonDoorRunesModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.block.structure.DungeonDoorCombinationBlock;
import thebetweenlands.common.block.structure.DungeonDoorRunesBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class DungeonDoorRenderer implements BlockEntityRenderer<DungeonDoorRunesBlockEntity> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/block/dungeon_door.png");
	private final DungeonDoorModel model;
	private final DungeonDoorRunesModel runes;
	private final ItemRenderer itemRenderer;

	public DungeonDoorRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new DungeonDoorModel(context.bakeLayer(BLModelLayers.DUNGEON_DOOR));
		this.runes = new DungeonDoorRunesModel(context.bakeLayer(BLModelLayers.DUNGEON_DOOR_RUNES));
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(DungeonDoorRunesBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float scale = 0.0625F;

		Direction facing = entity.getBlockState().getValue(DungeonDoorRunesBlock.FACING);
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(facing.getRotation().rotateX(-Mth.HALF_PI));
		stack.scale(1F, -1F, -1F);
		this.model.renderDoor(entity, partialTick, stack, buffer.getBuffer(this.model.renderType(TEXTURE)), light, overlay);
		if (!entity.isMimic())
			stack.translate(0F, 0.1375F * Mth.lerp(partialTick, entity.last_tick_slate_1_rotate, entity.slate_1_rotate) * scale, 0F + 0.275F * Mth.lerp(partialTick, entity.last_tick_recess_pos, entity.recess_pos) * scale);
		RenderSystem.enableBlend();
		if (!entity.is_gate_entrance) {
			this.runes.renderTopLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.top_state_prev), entity.renderTicks, partialTick, stack, buffer, light, overlay);
			this.runes.renderMiddleLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.mid_state_prev), entity.renderTicks, partialTick, stack, buffer, light, overlay);
			this.runes.renderBottomLayer(entity, DungeonDoorRunesModel.RUNES.get(entity.bottom_state_prev), entity.renderTicks, partialTick, stack, buffer, light, overlay);
		}

		if (entity.is_gate_entrance && entity.slate_1_rotate <= 270) {
			stack.pushPose();
			stack.scale(1.03125F, 1.03125F, 1.03125F);
			this.itemRenderer.renderStatic(entity.cachedStack(), ItemDisplayContext.NONE, light, overlay, stack, buffer, null, 0);
			stack.popPose();
		}
		RenderSystem.disableBlend();
		stack.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(DungeonDoorRunesBlockEntity entity) {
		return new AABB(entity.getBlockPos()).inflate(1.0D);
	}
}
