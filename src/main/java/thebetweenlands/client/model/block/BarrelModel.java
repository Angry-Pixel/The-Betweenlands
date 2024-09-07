package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BarrelModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var bottomBase = partDefinition.addOrReplaceChild("bottom_base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-4.0F, -2.0F, -6.0F, 8, 2, 12),
			PartPose.ZERO);

		var bottomBaseLeft = bottomBase.addOrReplaceChild("bottom_base_left", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(0.0F, -2.0F, -4.0F, 2, 2, 8),
			PartPose.offset(4.0F, 0.0F, 0.0F));

		var bottomBaseRight = bottomBase.addOrReplaceChild("bottom_base_right", CubeListBuilder.create()
				.texOffs(0, 25)
				.addBox(-2.0F, -2.0F, -4.0F, 2, 2, 8),
			PartPose.offset(-4.0F, 0.0F, 0.0F));

		var frontSide1 = bottomBase.addOrReplaceChild("front_side_1", CubeListBuilder.create()
				.texOffs(0, 36)
				.addBox(-3.99F, -6.0F, -3.0F, 8, 6, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, 0.136659280431156F, 0.0F, 0.0F));

		var frontSide2 = frontSide1.addOrReplaceChild("front_side_2", CubeListBuilder.create()
				.texOffs(0, 45)
				.addBox(-3.98F, -6.0F, 0.0F, 8, 6, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, -3.0F, -0.27314402793711257F, 0.0F, 0.0F));

		frontSide1.addOrReplaceChild("left_side", CubeListBuilder.create()
			.texOffs(72, 30)
			.addBox(3.01F, -6.0F, -1.0F, 1, 6, 1),
			PartPose.ZERO);

		frontSide1.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(77, 30)
				.addBox(-4.01F, -6.0F, -1.0F, 1, 6, 1),
			PartPose.ZERO);

		frontSide2.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(62, 30)
				.addBox(3.02F, -6.0F, 2.0F, 1, 6, 1),
			PartPose.ZERO);

		frontSide2.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(67, 30)
				.addBox(-4.0F, -6.0F, 2.0F, 1, 6, 1),
			PartPose.ZERO);

		var topFront = frontSide2.addOrReplaceChild("top_front", CubeListBuilder.create()
				.texOffs(42, 49)
				.addBox(-3.99F, -2.0F, -2.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, 2.0F, 0.136659280431156F, 0.0F, 0.0F));

		topFront.addOrReplaceChild("top_front_corner", CubeListBuilder.create()
				.texOffs(74, 0)
				.addBox(3.0F, -2.0F, 0.0F, 1, 14, 1),
			PartPose.ZERO);

		var backSide1 = bottomBase.addOrReplaceChild("back_side_1", CubeListBuilder.create()
				.texOffs(0, 54)
				.addBox(-4.01F, -6.0F, 1.0F, 8, 6, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, -0.136659280431156F, 0.0F, 0.0F));

		var backSide2 = backSide1.addOrReplaceChild("back_side_2", CubeListBuilder.create()
				.texOffs(21, 45)
				.addBox(-4.02F, -6.0F, -2.0F, 8, 6, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, 3.0F, 0.27314402793711257F, 0.0F, 0.0F));

		backSide1.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(24, 15)
				.addBox(-4.01F, -6.0F, 0.0F, 1, 6, 1),
			PartPose.ZERO);

		backSide1.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(34, 15)
				.addBox(3.01F, -6.0F, 0.0F, 1, 6, 1),
			PartPose.ZERO);

		backSide2.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(14, 15)
				.addBox(-4.02F, -6.0F, -3.0F, 1, 6, 1),
			PartPose.ZERO);

		backSide2.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(19, 15)
				.addBox(3.0F, -6.0F, -3.0F, 1, 6, 1),
			PartPose.ZERO);

		var topBack = backSide2.addOrReplaceChild("top_back", CubeListBuilder.create()
				.texOffs(42, 44)
				.addBox(-4.01F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, -2.0F, -0.136659280431156F, 0.0F, 0.0F));

		topBack.addOrReplaceChild("top_back_corner", CubeListBuilder.create()
				.texOffs(79, 0)
				.addBox(-4.0F, -2.0F, -1.0F, 1, 14, 1),
			PartPose.ZERO);

		var leftSide1 = bottomBaseLeft.addOrReplaceChild("left_side_1", CubeListBuilder.create()
				.texOffs(21, 15)
				.addBox(1.0F, -6.0F, -3.99F, 2, 6, 8),
			PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		leftSide1.addOrReplaceChild("left_edge", CubeListBuilder.create()
			.texOffs(57, 30)
			.addBox(0.0F, -6.0F, 3.01F, 1, 6, 1),
			PartPose.ZERO);

		leftSide1.addOrReplaceChild("right_edge", CubeListBuilder.create()
				.texOffs(52, 30)
				.addBox(0.0F, -6.0F, -4.01F, 1, 6, 1),
			PartPose.ZERO);

		var leftSide2 = leftSide1.addOrReplaceChild("left_side_2", CubeListBuilder.create()
				.texOffs(21, 30)
				.addBox(-2.0F, -6.0F, -3.98F, 2, 6, 8),
			PartPose.offsetAndRotation(3.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		leftSide2.addOrReplaceChild("left_edge", CubeListBuilder.create()
				.texOffs(47, 30)
				.addBox(-3.0F, -6.0F, 3.02F, 1, 6, 1),
			PartPose.ZERO);

		leftSide2.addOrReplaceChild("right_edge", CubeListBuilder.create()
				.texOffs(42, 30)
				.addBox(-3.0F, -6.0F, -4.0F, 1, 6, 1),
			PartPose.ZERO);

		var topLeft = leftSide2.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(21, 54)
				.addBox(0.0F, -2.0F, -3.99F, 2, 2, 8),
			PartPose.offsetAndRotation(-2.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		topLeft.addOrReplaceChild("top_left_corner", CubeListBuilder.create()
				.texOffs(64, 0)
				.addBox(-1.0F, -2.0F, 3.0F, 1, 14, 1),
			PartPose.ZERO);

		leftSide2.addOrReplaceChild("left_handle", CubeListBuilder.create()
				.texOffs(0, 3)
				.addBox(0.0F, 0.0F, -2.0F, 0, 3, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		var rightSide1 = bottomBaseRight.addOrReplaceChild("right_side_1", CubeListBuilder.create()
				.texOffs(42, 0)
				.addBox(-3.0F, -6.0F, -4.01F, 2, 6, 8),
			PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		rightSide1.addOrReplaceChild("left_edge", CubeListBuilder.create()
				.texOffs(39, 0)
				.addBox(-1.0F, -6.0F, -4.01F, 1, 6, 1),
			PartPose.ZERO);

		rightSide1.addOrReplaceChild("right_edge", CubeListBuilder.create()
				.texOffs(44, 0)
				.addBox(-1.0F, -6.0F, 3.01F, 1, 6, 1),
			PartPose.ZERO);

		var rightSide2 = rightSide1.addOrReplaceChild("right_side_2", CubeListBuilder.create()
				.texOffs(42, 15)
				.addBox(0.0F, -6.0F, -4.02F, 2, 6, 8),
			PartPose.offsetAndRotation(-3.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		rightSide2.addOrReplaceChild("left_edge", CubeListBuilder.create()
				.texOffs(29, 0)
				.addBox(2.0F, -6.0F, -4.02F, 1, 6, 1),
			PartPose.ZERO);

		rightSide2.addOrReplaceChild("right_edge", CubeListBuilder.create()
				.texOffs(34, 0)
				.addBox(2.0F, -6.0F, 3.0F, 1, 6, 1),
			PartPose.ZERO);

		var topRight = rightSide2.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(42, 54)
				.addBox(-2.0F, -2.0F, -4.01F, 2, 2, 8),
			PartPose.offsetAndRotation(2.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		topRight.addOrReplaceChild("top_right_corner", CubeListBuilder.create()
				.texOffs(69, 0)
				.addBox(0.0F, -2.0F, -4.0F, 1, 14, 1),
			PartPose.ZERO);

		rightSide2.addOrReplaceChild("right_handle", CubeListBuilder.create()
				.texOffs(0, -2)
				.addBox(0.0F, 0.0F, -2.0F, 0, 3, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
