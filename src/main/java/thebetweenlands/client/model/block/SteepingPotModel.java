package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SteepingPotModel {

	public static LayerDefinition makeNormalModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("outer_frame_1", CubeListBuilder.create()
			.texOffs(66, 0)
			.addBox(-2.0F, -1.0F, -6.0F, 2, 1, 12),
			PartPose.offsetAndRotation(-6.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		partDefinition.addOrReplaceChild("outer_frame_2", CubeListBuilder.create()
				.texOffs(37, 0)
				.addBox(0.0F, -1.0F, -6.0F, 2, 1, 12),
			PartPose.offsetAndRotation(6.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		partDefinition.addOrReplaceChild("outer_frame_3", CubeListBuilder.create()
				.texOffs(38, 41)
				.addBox(-6.0F, 0.0F, -5.99F, 12, 1, 1),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("outer_frame_4", CubeListBuilder.create()
				.texOffs(38, 38)
				.addBox(-6.0F, 0.0F, 4.999F, 12, 1, 1),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("grate_1", CubeListBuilder.create()
				.texOffs(0, 74)
				.addBox(-0.5F, 0.0F, -5.0F, 1, 1, 10),
			PartPose.offset(4.5F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("grate_2", CubeListBuilder.create()
				.texOffs(0, 86)
				.addBox(-0.5F, 0.0F, -5.0F, 1, 1, 10),
			PartPose.offset(1.5F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("grate_3", CubeListBuilder.create()
				.texOffs(23, 74)
				.addBox(-0.5F, 0.0F, -5.0F, 1, 1, 10),
			PartPose.offset(-1.5F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("grate_4", CubeListBuilder.create()
				.texOffs(23, 86)
				.addBox(-0.5F, 0.0F, -5.0F, 1, 1, 10),
			PartPose.offset(-4.5F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("grate_5", CubeListBuilder.create()
				.texOffs(23, 14)
				.addBox(-6.5F, -0.025F, -0.5F, 13, 1, 1),
			PartPose.offset(0.0F, -4.1F, 4.0F));

		partDefinition.addOrReplaceChild("grate_6", CubeListBuilder.create()
				.texOffs(23, 17)
				.addBox(-6.5F, -0.025F, -0.5F, 13, 1, 1),
			PartPose.offset(0.0F, -4.1F, 2.0F));

		partDefinition.addOrReplaceChild("grate_7", CubeListBuilder.create()
				.texOffs(23, 20)
				.addBox(-6.5F, -0.025F, -0.5F, 13, 1, 1),
			PartPose.offset(0.0F, -4.1F, 0.0F));

		partDefinition.addOrReplaceChild("grate_8", CubeListBuilder.create()
				.texOffs(17, 23)
				.addBox(-6.5F, -0.025F, -0.5F, 13, 1, 1),
			PartPose.offset(0.0F, -4.1F, -2.0F));

		partDefinition.addOrReplaceChild("grate_9", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-6.5F, -0.025F, -0.5F, 13, 1, 1),
			PartPose.offset(0.0F, -4.1F, -4.0F));

		var front = partDefinition.addOrReplaceChild("front_leg_segment", CubeListBuilder.create()
				.texOffs(36, 60)
				.addBox(-3.0F, 0.0F, -1.001F, 6, 1, 2),
			PartPose.offset(0.0F, -3.5F, -6.0F));

		var frontLeftLeg = front.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
				.texOffs(77, 67)
				.addBox(-3.0F, 0.0F, -1.0F, 3, 1, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		frontLeftLeg.addOrReplaceChild("front_left_leg_2", CubeListBuilder.create()
				.texOffs(66, 67)
				.addBox(-3.0F, 0.0F, -0.999F, 3, 1, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		var frontRightLeg = front.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
				.texOffs(55, 67)
				.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		frontRightLeg.addOrReplaceChild("front_right_leg_2", CubeListBuilder.create()
				.texOffs(44, 67)
				.addBox(0.0F, 0.0F, -0.999F, 3, 1, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		var back = partDefinition.addOrReplaceChild("back_leg_segment", CubeListBuilder.create()
				.texOffs(36, 60)
				.addBox(-3.0F, 0.0F, -0.999F, 6, 1, 2),
			PartPose.offset(0.0F, -3.5F, 6.0F));

		var backLeftLeg = back.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
				.texOffs(77, 67)
				.addBox(-3.0F, 0.0F, -1.0F, 3, 1, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		backLeftLeg.addOrReplaceChild("back_left_leg_2", CubeListBuilder.create()
				.texOffs(66, 67)
				.addBox(-3.0F, 0.0F, -1.001F, 3, 1, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		var backRightLeg = back.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
				.texOffs(55, 67)
				.addBox(0.0F, 0.0F, -1.0F, 3, 1, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		backRightLeg.addOrReplaceChild("back_right_leg_2", CubeListBuilder.create()
				.texOffs(44, 67)
				.addBox(0.0F, 0.0F, -1.001F, 3, 1, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		var base = partDefinition.addOrReplaceChild("pot_base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -1.005F, -4.001F, 10, 1, 8),
			PartPose.offset(0.0F, -4.1F, 0.0F));

		var backSide = base.addOrReplaceChild("back_side_1", CubeListBuilder.create()
				.texOffs(19, 55)
				.addBox(-4.0F, -3.001F, 0.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, -0.0873F, 0.0F, 0.0F));

		backSide.addOrReplaceChild("back_side_fill", CubeListBuilder.create()
				.texOffs(57, 50)
				.addBox( -8.0F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 0.0F));

		var backSide2 = backSide.addOrReplaceChild("back_side_2", CubeListBuilder.create()
				.texOffs(95, 50)
				.addBox(-3.999F, -3.001F, -1.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.1745F, 0.0F, 0.0F));

		backSide2.addOrReplaceChild("back_side_fill_2", CubeListBuilder.create()
				.texOffs(38, 50)
				.addBox( -8.001F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, -1.0F));

		var frontSide = base.addOrReplaceChild("front_side_1", CubeListBuilder.create()
				.texOffs(0, 55)
				.addBox(-4.0F, -3.001F, -1.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

		frontSide.addOrReplaceChild("front_side_fill", CubeListBuilder.create()
				.texOffs(19, 50)
				.addBox( -8.0F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 1.0F));

		var frontSide2 = frontSide.addOrReplaceChild("front_side_2", CubeListBuilder.create()
				.texOffs(76, 50)
				.addBox(-3.999F, -3.001F, 0.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		frontSide2.addOrReplaceChild("front_side_fill_2", CubeListBuilder.create()
				.texOffs(0, 50)
				.addBox( -8.001F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 2.0F));

		var leftSide = base.addOrReplaceChild("left_side_1", CubeListBuilder.create()
				.texOffs(19, 38)
				.addBox( 0.0F, -3.001F, -4.0F, 1, 3, 8),
			PartPose.offsetAndRotation(4.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		leftSide.addOrReplaceChild("left_side_fill", CubeListBuilder.create()
				.texOffs(0, 26)
				.addBox( -1.0F, -3.0F, -8.0F, 1, 3, 8),
			PartPose.offset(0.0F, 0.0F, 4.0F));

		var leftSide2 = leftSide.addOrReplaceChild("left_side_2", CubeListBuilder.create()
				.texOffs(57, 26)
				.addBox(-1.0F, -3.001F, -4.001F, 1, 3, 8),
			PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		leftSide2.addOrReplaceChild("left_side_fill_2", CubeListBuilder.create()
				.texOffs(19, 26)
				.addBox( -1.0F, -3.0F, -7.99F, 1, 3, 8),
			PartPose.offset(-1.0F, 0.0F, 4.0F));

		var rightSide = base.addOrReplaceChild("right_side_1", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox( -1.0F, -3.001F, -4.0F, 1, 3, 8),
			PartPose.offsetAndRotation(-4.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		rightSide.addOrReplaceChild("right_side_fill", CubeListBuilder.create()
				.texOffs(71, 14)
				.addBox( -1.0F, -3.0F, -8.0F, 1, 3, 8),
			PartPose.offset(1.0F, 0.0F, 4.0F));

		var rightSide2 = rightSide.addOrReplaceChild("right_side_2", CubeListBuilder.create()
				.texOffs(38, 26)
				.addBox(0.0F, -3.001F, -4.001F, 1, 3, 8),
			PartPose.offsetAndRotation(-1.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		rightSide2.addOrReplaceChild("right_side_fill_2", CubeListBuilder.create()
				.texOffs(52, 14)
				.addBox( -1.0F, -3.0F, -7.99F, 1, 3, 8),
			PartPose.offset(2.0F, 0.0F, 4.0F));

		var handle1 = leftSide2.addOrReplaceChild("handle_1", CubeListBuilder.create()
			.texOffs(66, 60)
			.addBox(0.0F, 0.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		var rope2a = handle1.addOrReplaceChild("rope_2a", CubeListBuilder.create()
			.texOffs(82, 0)
			.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 4),
			PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, -1.3963F, 0.0F, -0.1309F));

		var rope2b = rope2a.addOrReplaceChild("rope_2b", CubeListBuilder.create()
				.texOffs(85, 4)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.3927F, 0.0F, 0.0F));

		var rope2c = rope2b.addOrReplaceChild("rope_2c", CubeListBuilder.create()
				.texOffs(85, 5)
				.addBox(-1.0F, 0.001F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, -0.2182F, 0.0F));

		var rope2d = rope2c.addOrReplaceChild("rope_2d", CubeListBuilder.create()
				.texOffs(85, 6)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.7854F, 0.0F, 0.0F));

		var rope2e = rope2d.addOrReplaceChild("rope_2e", CubeListBuilder.create()
				.texOffs(84, 7)
				.addBox(-1.0F, 0.001F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0436F, -0.6545F, 0.0F));

		var rope2f = rope2e.addOrReplaceChild("rope_2f", CubeListBuilder.create()
				.texOffs(85, 9)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.6981F, 0.0F));

		rope2f.addOrReplaceChild("rope_2g", CubeListBuilder.create()
				.texOffs(85, 10)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.48F, 0.0F, 0.0F));

		var handle2 = rightSide2.addOrReplaceChild("handle_2", CubeListBuilder.create()
				.texOffs(53, 60)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		var rope1a = handle2.addOrReplaceChild("rope_1a", CubeListBuilder.create()
				.texOffs(80, 0)
				.addBox(0.0F, 0.0F, 0.0F, 1, 0, 3),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, -1.309F, 0.0F, 0.1309F));

		var rope1b = rope1a.addOrReplaceChild("rope_1b", CubeListBuilder.create()
				.texOffs(82, 3)
				.addBox(0.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.3927F, 0.0F, 0.0F));

		var rope1c = rope1b.addOrReplaceChild("rope_1c", CubeListBuilder.create()
				.texOffs(81, 4)
				.addBox(0.0F, 0.001F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.3491F, 0.0F));

		var rope1d = rope1c.addOrReplaceChild("rope_1d", CubeListBuilder.create()
				.texOffs(82, 6)
				.addBox(0.0F, 0.0F, 0.0F, 1, 0, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

		var rope1e = rope1d.addOrReplaceChild("rope_1e", CubeListBuilder.create()
				.texOffs(81, 7)
				.addBox(0.0F, 0.001F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.5236F, 0.0F));

		var rope1f = rope1e.addOrReplaceChild("rope_1f", CubeListBuilder.create()
				.texOffs(81, 9)
				.addBox(0.0F, 0.0F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.7418F, 0.0F));

		rope1f.addOrReplaceChild("handle", CubeListBuilder.create()
				.texOffs(58, 1)
				.addBox(-1.0F, -0.5F, -0.5F, 2, 1, 6),
			PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.1139F, 0.1087F, -0.2225F));

		base.addOrReplaceChild("base_front_side", CubeListBuilder.create()
			.texOffs(0, 63)
			.addBox(-3.999F, -1.005F, 0.0F, 8, 1, 1),
			PartPose.offset(0.0F, 0.0F, 4.0F));

		base.addOrReplaceChild("base_back_side", CubeListBuilder.create()
				.texOffs(0, 60)
				.addBox(-3.999F, -1.005F, -1.0F, 8, 1, 1),
			PartPose.offset(0.0F, 0.0F, -4.0F));

		var frontTop = base.addOrReplaceChild("front_top", CubeListBuilder.create()
				.texOffs(95, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		frontTop.addOrReplaceChild("front_left_corner", CubeListBuilder.create()
				.texOffs(5, 71)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var leftTop = base.addOrReplaceChild("left_top", CubeListBuilder.create()
				.texOffs(76, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		leftTop.addOrReplaceChild("back_left_corner", CubeListBuilder.create()
				.texOffs(0, 71)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var backTop = base.addOrReplaceChild("back_top", CubeListBuilder.create()
				.texOffs(57, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		backTop.addOrReplaceChild("back_right_corner", CubeListBuilder.create()
				.texOffs(93, 67)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var rightTop = base.addOrReplaceChild("right_top", CubeListBuilder.create()
				.texOffs(38, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		rightTop.addOrReplaceChild("front_right_corner", CubeListBuilder.create()
				.texOffs(88, 67)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		return LayerDefinition.create(definition, 128, 128);
	}

	public static LayerDefinition makeHangingModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("pot_base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -1.005F, -4.001F, 10, 1, 8),
			PartPose.offset(0.0F, -0.1F, 0.0F));

		var backSide = base.addOrReplaceChild("back_side_1", CubeListBuilder.create()
				.texOffs(19, 55)
				.addBox(-4.0F, -3.001F, 0.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, -0.0873F, 0.0F, 0.0F));

		backSide.addOrReplaceChild("back_side_fill", CubeListBuilder.create()
				.texOffs(57, 50)
				.addBox( -8.0F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 0.0F));

		var backSide2 = backSide.addOrReplaceChild("back_side_2", CubeListBuilder.create()
				.texOffs(95, 50)
				.addBox(-3.999F, -3.001F, -1.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.1745F, 0.0F, 0.0F));

		backSide2.addOrReplaceChild("back_side_fill_2", CubeListBuilder.create()
				.texOffs(38, 50)
				.addBox( -8.001F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, -1.0F));

		var frontSide = base.addOrReplaceChild("front_side_1", CubeListBuilder.create()
				.texOffs(0, 55)
				.addBox(-4.0F, -3.001F, -1.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

		frontSide.addOrReplaceChild("front_side_fill", CubeListBuilder.create()
				.texOffs(19, 50)
				.addBox( -8.0F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 1.0F));

		var frontSide2 = frontSide.addOrReplaceChild("front_side_2", CubeListBuilder.create()
				.texOffs(76, 50)
				.addBox(-3.999F, -3.001F, 0.0F, 8, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		frontSide2.addOrReplaceChild("front_side_fill_2", CubeListBuilder.create()
				.texOffs(0, 50)
				.addBox( -8.001F, -3.0F, -1.0F, 8, 3, 1),
			PartPose.offset(4.0F, 0.0F, 2.0F));

		var leftSide = base.addOrReplaceChild("left_side_1", CubeListBuilder.create()
				.texOffs(19, 38)
				.addBox( 0.0F, -3.001F, -4.0F, 1, 3, 8),
			PartPose.offsetAndRotation(4.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		leftSide.addOrReplaceChild("left_side_fill", CubeListBuilder.create()
				.texOffs(0, 26)
				.addBox( -1.0F, -3.0F, -8.0F, 1, 3, 8),
			PartPose.offset(0.0F, 0.0F, 4.0F));

		var leftSide2 = leftSide.addOrReplaceChild("left_side_2", CubeListBuilder.create()
				.texOffs(57, 26)
				.addBox(-1.0F, -3.001F, -4.001F, 1, 3, 8),
			PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		leftSide2.addOrReplaceChild("left_side_fill_2", CubeListBuilder.create()
				.texOffs(19, 26)
				.addBox( -1.0F, -3.0F, -7.99F, 1, 3, 8),
			PartPose.offset(-1.0F, 0.0F, 4.0F));

		var rightSide = base.addOrReplaceChild("right_side_1", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox( -1.0F, -3.001F, -4.0F, 1, 3, 8),
			PartPose.offsetAndRotation(-4.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		rightSide.addOrReplaceChild("right_side_fill", CubeListBuilder.create()
				.texOffs(71, 14)
				.addBox( -1.0F, -3.0F, -8.0F, 1, 3, 8),
			PartPose.offset(1.0F, 0.0F, 4.0F));

		var rightSide2 = rightSide.addOrReplaceChild("right_side_2", CubeListBuilder.create()
				.texOffs(38, 26)
				.addBox(0.0F, -3.001F, -4.001F, 1, 3, 8),
			PartPose.offsetAndRotation(-1.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		rightSide2.addOrReplaceChild("right_side_fill_2", CubeListBuilder.create()
				.texOffs(52, 14)
				.addBox( -1.0F, -3.0F, -7.99F, 1, 3, 8),
			PartPose.offset(2.0F, 0.0F, 4.0F));

		var handle1 = leftSide2.addOrReplaceChild("handle_1", CubeListBuilder.create()
				.texOffs(66, 60)
				.addBox(0.0F, 0.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		var handle2 = rightSide2.addOrReplaceChild("handle_2", CubeListBuilder.create()
				.texOffs(53, 60)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		base.addOrReplaceChild("base_front_side", CubeListBuilder.create()
				.texOffs(0, 63)
				.addBox(-3.999F, -1.005F, 0.0F, 8, 1, 1),
			PartPose.offset(0.0F, 0.0F, 4.0F));

		base.addOrReplaceChild("base_back_side", CubeListBuilder.create()
				.texOffs(0, 60)
				.addBox(-3.999F, -1.005F, -1.0F, 8, 1, 1),
			PartPose.offset(0.0F, 0.0F, -4.0F));

		var frontTop = base.addOrReplaceChild("front_top", CubeListBuilder.create()
				.texOffs(95, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		frontTop.addOrReplaceChild("front_left_corner", CubeListBuilder.create()
				.texOffs(5, 71)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var leftTop = base.addOrReplaceChild("left_top", CubeListBuilder.create()
				.texOffs(76, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		leftTop.addOrReplaceChild("back_left_corner", CubeListBuilder.create()
				.texOffs(0, 71)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var backTop = base.addOrReplaceChild("back_top", CubeListBuilder.create()
				.texOffs(57, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		backTop.addOrReplaceChild("back_right_corner", CubeListBuilder.create()
				.texOffs(93, 67)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var rightTop = base.addOrReplaceChild("right_top", CubeListBuilder.create()
				.texOffs(38, 55)
				.addBox(-4.0F, -0.8F, 4.0F, 8, 1, 1),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		rightTop.addOrReplaceChild("front_right_corner", CubeListBuilder.create()
				.texOffs(88, 67)
				.addBox(3.0F, -0.8F, 3.0F, 1, 1, 1),
			PartPose.ZERO);

		var rope2a = handle1.addOrReplaceChild("rope_1b", CubeListBuilder.create()
			.texOffs(83, 6)
			.addBox(-1.0F, -5.0F, 0.0F, 1, 5, 0),
			PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, 0.4102F, 0.0F, -0.3491F));

		handle1.addOrReplaceChild("rope_2b", CubeListBuilder.create()
				.texOffs(83, 0)
				.addBox(-1.0F, -5.0F, 0.0F, 1, 5, 0),
			PartPose.offsetAndRotation(1.0F, 0.0F, -2.0F, -0.4102F, 0.0F, -0.3491F));

		rope2a.addOrReplaceChild("rope_2c", CubeListBuilder.create()
				.texOffs(88, 6)
				.addBox(-1.0F, -6.0F, 0.0F, 1, 6, 0),
			PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.4102F, 0.0F, 0.0F));

		var rope1a = handle2.addOrReplaceChild("rope_1a", CubeListBuilder.create()
				.texOffs(83, 6)
				.addBox(0.0F, -5.0F, 0.0F, 1, 5, 0),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, 0.4102F, 0.0F, 0.3491F));

		handle2.addOrReplaceChild("rope_1b", CubeListBuilder.create()
				.texOffs(83, 0)
				.addBox(0.0F, -5.0F, 0.0F, 1, 5, 0),
			PartPose.offsetAndRotation(-1.0F, 0.0F, -2.0F, -0.4102F, 0.0F, 0.3491F));

		rope1a.addOrReplaceChild("rope_1c", CubeListBuilder.create()
				.texOffs(88, 6)
				.addBox(0.0F, -6.0F, 0.0F, 1, 6, 0),
			PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.4102F, 0.0F, 0.0F));

		base.addOrReplaceChild("handle", CubeListBuilder.create()
				.texOffs(58, 1)
				.addBox(-1.0F, 0.0F, -3.0F, 2, 1, 6)
				.texOffs(93, 0)
				.addBox(-1.5F, -0.5F, -1.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, -15.9F, 0.0F, 0.0F, 1.5708F, 0.0F));


		return LayerDefinition.create(definition, 128, 128);
	}
}
