package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AnimatorModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("stone_base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8.0F, -4.0F, -8.0F, 16, 4, 16),
			PartPose.ZERO);

		partDefinition.addOrReplaceChild("stone_corner_1", CubeListBuilder.create()
				.texOffs(0, 21)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(-6.0F, -4.0F, -6.0F));

		partDefinition.addOrReplaceChild("stone_corner_2", CubeListBuilder.create()
				.texOffs(17, 21)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(-6.0F, -4.0F, 6.0F));

		partDefinition.addOrReplaceChild("stone_corner_3", CubeListBuilder.create()
				.texOffs(34, 21)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(6.0F, -4.0F, 6.0F));

		partDefinition.addOrReplaceChild("stone_corner_4", CubeListBuilder.create()
				.texOffs(51, 21)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(6.0F, -4.0F, -6.0F));

		partDefinition.addOrReplaceChild("wood_leg_1", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3),
			PartPose.offsetAndRotation(-6.0F, -6.1F, -6.0F, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F));

		partDefinition.addOrReplaceChild("wood_leg_2", CubeListBuilder.create()
				.texOffs(13, 28)
				.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3),
			PartPose.offsetAndRotation(-6.0F, -6.1F, 6.0F, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F));

		partDefinition.addOrReplaceChild("wood_leg_3", CubeListBuilder.create()
				.texOffs(26, 28)
				.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3),
			PartPose.offsetAndRotation(6.0F, -6.1F, 6.0F, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F));

		partDefinition.addOrReplaceChild("wood_leg_4", CubeListBuilder.create()
				.texOffs(39, 28)
				.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3),
			PartPose.offsetAndRotation(6.0F, -6.1F, -6.0F, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F));

		partDefinition.addOrReplaceChild("wood_plate_1", CubeListBuilder.create()
				.texOffs(0, 41)
				.addBox(-8.0F, -2.0F, -7.0F, 16, 2, 5),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		partDefinition.addOrReplaceChild("wood_plate_2", CubeListBuilder.create()
				.texOffs(0, 49)
				.addBox(-8.0F, -2.0F, -2.0F, 6, 2, 4),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		partDefinition.addOrReplaceChild("wood_plate_3", CubeListBuilder.create()
				.texOffs(0, 56)
				.addBox(-8.0F, -2.0F, 2.0F, 16, 2, 5),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		partDefinition.addOrReplaceChild("wood_plate_4", CubeListBuilder.create()
				.texOffs(21, 49)
				.addBox(2.0F, -2.0F, -2.0F, 6, 2, 4),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		partDefinition.addOrReplaceChild("wood_plate_5", CubeListBuilder.create()
				.texOffs(43, 41)
				.addBox(-7.0F, -2.0F, -8.0F, 14, 2, 1),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		partDefinition.addOrReplaceChild("wood_plate_6", CubeListBuilder.create()
				.texOffs(43, 45)
				.addBox(-7.0F, -2.0F, 7.0F, 14, 2, 1),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		var beam1 = partDefinition.addOrReplaceChild("beam_1", CubeListBuilder.create()
				.texOffs(43, 49)
				.addBox(-1.0F, -2.0F, -5.0F, 2, 2, 10),
			PartPose.offset(5.5F, -8.0F, 0.0F));

		beam1.addOrReplaceChild("board_1", CubeListBuilder.create()
				.texOffs(50, 0)
				.addBox(-1.0F, -2.0F, -4.0F, 1, 2, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var beam2 = partDefinition.addOrReplaceChild("beam_2", CubeListBuilder.create()
				.texOffs(68, 49)
				.addBox(-1.0F, -2.0F, -5.0F, 2, 2, 10),
			PartPose.offset(-5.5F, -8.0F, 0.0F));

		beam2.addOrReplaceChild("board_2", CubeListBuilder.create()
				.texOffs(69, 0)
				.addBox(0.0F, -2.0F, -4.0F, 1, 2, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var beam3 = partDefinition.addOrReplaceChild("beam_3", CubeListBuilder.create()
				.texOffs(93, 49)
				.addBox(-5.0F, -2.0F, -1.0F, 10, 2, 2),
			PartPose.offset(0.0F, -8.0F, 5.5F));

		beam3.addOrReplaceChild("board_3", CubeListBuilder.create()
				.texOffs(88, 0)
				.addBox(-4.0F, -2.0F, -1.0F, 8, 2, 1),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("board_4", CubeListBuilder.create()
				.texOffs(88, 4)
				.addBox(-4.0F, 0.0F, -1.0F, 8, 2, 1),
			PartPose.offset(0.0F, -13.0F, -4.0F));

		var scrollBase = partDefinition.addOrReplaceChild("scroll_1", CubeListBuilder.create()
				.texOffs(80, 15)
				.addBox(-3.5F, -0.5F, -0.5F, 7, 1, 1),
			PartPose.offsetAndRotation(5.0F, -16.0F, 3.0F, -0.136659280431156F, -1.0016444577195458F, 0.0F));

		var scroll = scrollBase.addOrReplaceChild("scroll_2", CubeListBuilder.create()
				.texOffs(80, 18)
				.addBox(-3.0F, -1.0F, -1.0F, 6, 2, 2),
			PartPose.ZERO);

		var scrollPiece1 = scroll.addOrReplaceChild("scroll_piece_1", CubeListBuilder.create()
				.texOffs(80, 23)
				.addBox(-3.0F, 0.0F, -3.0F, 6, 0, 4),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.136659280431156F, -0.27314402793711257F, 0.0F));

		var scrollPiece2 = scrollPiece1.addOrReplaceChild("scroll_piece_2", CubeListBuilder.create()
				.texOffs(82, 28)
				.addBox(-3.0F, 0.0F, -2.0F, 6, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 1.1838568316277536F, 0.0F, 0.0F));

		scrollPiece2.addOrReplaceChild("scroll_piece_3", CubeListBuilder.create()
				.texOffs(79, 31)
				.addBox(-3.0F, 0.0F, -5.0F, 6, 0, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.40980330836826856F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
