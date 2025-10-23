package thebetweenlands.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;

public class DungeonDoorModel extends Model {

	private final ModelPart root;
	private final ModelPart slate1;
	private final ModelPart slate2;
	private final ModelPart slate3;
	private final ModelPart top;
	private final ModelPart mid;
	private final ModelPart bottom;
	private final ModelPart behind;
	private final ModelPart middleFloor;
	private final ModelPart leftFloor;
	private final ModelPart rightFloor;
	private final ModelPart[] lockParts;

	public DungeonDoorModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
		this.slate1 = root.getChild("slate1");
		this.slate2 = root.getChild("slate2");
		this.slate3 = root.getChild("slate3");
		this.top = root.getChild("top");
		this.mid = root.getChild("mid");
		this.bottom = root.getChild("bottom");
		this.behind = root.getChild("behind");
		this.middleFloor = root.getChild("middle_floor");
		this.leftFloor = root.getChild("left_floor");
		this.rightFloor = root.getChild("right_floor");
		this.lockParts = new ModelPart[] {
			this.top, this.mid, this.bottom,
			root.getChild("frame_top"),
			root.getChild("frame_bottom"),
			root.getChild("frame_left"),
			root.getChild("frame_right")
		};
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("slate1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-8.0F, -48.0F, -3.0F, 16, 48, 6),
			PartPose.offset(0.0F, 24.0F, -4.0F));
		var slate2 = partDefinition.addOrReplaceChild("slate2", CubeListBuilder.create()
				.texOffs(0, 55).addBox(-16.0F, -48.0F, -1.0F, 16, 48, 4),
			PartPose.offsetAndRotation(-8.0F, 24.0F, -4.0F, 0.0F, 0.045553093477052F, 0.0F));
		slate2.addOrReplaceChild("slate2b", CubeListBuilder.create()
				.texOffs(0, 108).addBox(-16.0F, -48.0F, -3.0F, 16, 38, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		slate2.addOrReplaceChild("slate2c", CubeListBuilder.create()
				.texOffs(0, 149).addBox(-16.0F, -10.0F, -3.0F, 12, 10, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var slate3 = partDefinition.addOrReplaceChild("slate3", CubeListBuilder.create()
				.texOffs(45, 0).addBox(-8.0F, -40.0F, 2.0F, 16, 40, 4),
			PartPose.offsetAndRotation(16.0F, 24.0F, -7.0F, -0.017453292519943295F, 0.0F, 0.0F));
		slate3.addOrReplaceChild("slate3b", CubeListBuilder.create()
				.texOffs(86, 0).addBox(-5.0F, -8.0F, 0.0F, 13, 8, 6),
			PartPose.offset(0.0F, -40.0F, 0.0F));
		slate3.addOrReplaceChild("slate3c", CubeListBuilder.create()
				.texOffs(125, 0).addBox(-6.0F, -36.0F, 0.0F, 14, 36, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		slate3.addOrReplaceChild("slate3d", CubeListBuilder.create()
				.texOffs(158, 0).addBox(-3.0F, -40.0F, 0.0F, 11, 4, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("frame_top", CubeListBuilder.create()
				.texOffs(44, 116).addBox(-7.0F, -8.0F, -9.0F, 14, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("frame_bottom", CubeListBuilder.create()
				.texOffs(44, 138).addBox(-7.0F, 7.0F, -9.0F, 14, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("frame_right", CubeListBuilder.create()
				.texOffs(68, 121).addBox(6.9F, -7.0F, -9.0F, 1, 14, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("frame_left", CubeListBuilder.create()
				.texOffs(44, 121).addBox(-8.0F, -7.0F, -9.0F, 1, 14, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(41, 85).addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5),
			PartPose.offset(0.0F, -4.5F, -5.5F));
		partDefinition.addOrReplaceChild("mid", CubeListBuilder.create()
				.texOffs(41, 96).addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4),
			PartPose.offset(0.0F, 0.0F, -6.0F));
		partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create()
				.texOffs(41, 105).addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5),
			PartPose.offset(0.0F, 4.5F, -5.5F));
		partDefinition.addOrReplaceChild("behind", CubeListBuilder.create()
				.texOffs(81, 46).addBox(-24.0F, -24.0F, -1.0F, 48, 48, 9),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_floor", CubeListBuilder.create()
				.texOffs(0, 162).addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16),
			PartPose.offset(-16.0F, 24.0F, 0.0F));
		partDefinition.addOrReplaceChild("right_floor", CubeListBuilder.create()
				.texOffs(130, 162).addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16),
			PartPose.offset(16.0F, 24.0F, 0.0F));
		partDefinition.addOrReplaceChild("middle_floor", CubeListBuilder.create()
				.texOffs(65, 162).addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16),
			PartPose.offset(-0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 256);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}

	public void renderDoor(DungeonDoorRunesBlockEntity entity, float partialTick, PoseStack stack, VertexConsumer consumer, int light, int overlay) {
		float scale = 0.0625F;
		var topRotation = Mth.lerp(partialTick, entity.lastTickTopRotate, entity.top_rotate);
		var middleRotation = Mth.lerp(partialTick, entity.lastTickMidRotate, entity.mid_rotate);
		var bottomRotation = Mth.lerp(partialTick, entity.lastTickBottomRotate, entity.bottom_rotate);

		var slate1Rot = Mth.lerp(partialTick, entity.last_tick_slate_1_rotate, entity.slate_1_rotate);
		var slate2Rot = Mth.lerp(partialTick, entity.last_tick_slate_2_rotate, entity.slate_2_rotate);
		var slate3Rot = Mth.lerp(partialTick, entity.last_tick_slate_3_rotate, entity.slate_3_rotate);

		var doorRecession = Mth.lerp(partialTick, entity.last_tick_recess_pos, entity.recess_pos) * scale;

		this.top.xRot = topRotation * Mth.DEG_TO_RAD;
		this.mid.xRot = middleRotation * Mth.DEG_TO_RAD;
		this.bottom.xRot = bottomRotation * Mth.DEG_TO_RAD;

		if (entity.isMimic()) {
			this.slate1.xRot = slate1Rot * Mth.DEG_TO_RAD;
			this.slate2.xRot = slate2Rot * Mth.DEG_TO_RAD;
			this.slate3.xRot = slate3Rot * Mth.DEG_TO_RAD;
		} else {
			this.slate1.xRot = 0F;
			this.slate2.xRot = 0F;
			this.slate3.xRot = 0F;
		}
		if (!entity.hide_slate_1) {
			if (!entity.isMimic()) {
				stack.pushPose();
				stack.translate(0F, 0.1375F * slate1Rot * scale, 0.275F * doorRecession);
				this.slate1.render(stack, consumer, light, overlay);
				stack.popPose();
			} else
				this.slate1.render(stack, consumer, light, overlay);
		}
		if (!entity.hide_slate_2) {
			if (!entity.isMimic()) {
				stack.pushPose();
				stack.translate(0F, 0.1375F * slate2Rot * scale, 0.275F * doorRecession);
				this.slate2.render(stack, consumer, light, overlay);
				stack.popPose();
			} else
				this.slate2.render(stack, consumer, light, overlay);
		}
		if (!entity.hide_slate_3) {
			if (!entity.isMimic()) {
				stack.pushPose();
				stack.translate(0F, 0.1375F * slate3Rot * scale, 0.275F * doorRecession);
				this.slate3.render(stack, consumer, light, overlay);
				stack.popPose();
			} else
				this.slate3.render(stack, consumer, light, overlay);
		}
		if (!entity.hide_lock) {
			if (!entity.isMimic()) {
				stack.pushPose();
				stack.translate(0F, 0.1375F * slate1Rot * scale, 0.275F * doorRecession);
				this.renderLockParts(entity, stack, consumer, light, overlay);
				stack.popPose();
			} else {
				this.renderLockParts(entity, stack, consumer, light, overlay);
			}
		}
		if (!entity.hide_back_wall) {
			if (!entity.isMimic()) {
				stack.pushPose();
				stack.translate(0F, 0.1375F * slate1Rot * scale, 0.275F * doorRecession);
				stack.scale(0.99F, 1F, 1F);
				this.behind.render(stack, consumer, light, overlay);
				stack.popPose();
			} else {
				stack.pushPose();
				stack.scale(0.99F, 1F, 1F);
				this.behind.render(stack, consumer, light, overlay);
				stack.popPose();
			}
		}

		if (!entity.isMimic() && entity.is_in_dungeon) {
			if (entity.animate_tile_recess) {
				var slate1Recession = Mth.lerp(partialTick, entity.last_tick_recess_pos_tile_1, entity.tile_1_recess_pos) * scale;
				var slate2Recession = Mth.lerp(partialTick, entity.last_tick_recess_pos_tile_2, entity.tile_2_recess_pos) * scale;
				var slate3Recession = Mth.lerp(partialTick, entity.last_tick_recess_pos_tile_3, entity.tile_3_recess_pos) * scale;

				stack.pushPose();
				stack.translate(0F, 0F, 0.275F * slate1Recession);
				this.rightFloor.render(stack, consumer, light, overlay);
				stack.popPose();
				stack.pushPose();
				stack.translate(0F, 0F, 0.275F * slate2Recession);
				this.middleFloor.render(stack, consumer, light, overlay);
				stack.popPose();
				stack.pushPose();
				stack.translate(0F, 0F, 0.275F * slate3Recession);
				this.leftFloor.render(stack, consumer, light, overlay);
				stack.popPose();
			} else {
				this.leftFloor.render(stack, consumer, light, overlay);
				this.middleFloor.render(stack, consumer, light, overlay);
				this.rightFloor.render(stack, consumer, light, overlay);
			}
		}
	}

	private void renderLockParts(DungeonDoorRunesBlockEntity entity, PoseStack stack, VertexConsumer consumer, int light, int overlay) {
		if (!entity.is_gate_entrance) {
			for (ModelPart part : this.lockParts) {
				part.render(stack, consumer, light, overlay);
			}
		}
	}
}
