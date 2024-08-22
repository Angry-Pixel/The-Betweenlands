package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PurifierModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		base.addOrReplaceChild("filter", CubeListBuilder.create()
				.texOffs(50, 0)
				.addBox(-5.0F, 0.0F, -5.0F, 10, 0, 10),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		partDefinition.addOrReplaceChild("fire_plate", CubeListBuilder.create()
				.texOffs(50, 11)
				.addBox(-5.0F, -0.1F, -5.0F, 10, 0, 10),
			PartPose.ZERO);

		var side1 = base.addOrReplaceChild("side_1", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-5.0F, -12.0F, -2.0F, 10, 12, 2),
			PartPose.offset(0.0F, 0.0F, -5.0F));

		base.addOrReplaceChild("side_2", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-2.0F, -12.0F, -5.0F, 2, 12, 10),
			PartPose.offset(-5.0F, 0.0F, 0.0F));

		base.addOrReplaceChild("side_3", CubeListBuilder.create()
				.texOffs(25, 13)
				.addBox(-5.0F, -12.0F, 0.0F, 10, 12, 2),
			PartPose.offset(0.0F, 0.0F, 5.0F));

		base.addOrReplaceChild("side_4", CubeListBuilder.create()
				.texOffs(25, 28)
				.addBox(0.0F, -12.0F, -5.0F, 2, 12, 10),
			PartPose.offset(5.0F, 0.0F, 0.0F));

		base.addOrReplaceChild("corner_1", CubeListBuilder.create()
				.texOffs(50, 0)
				.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2),
			PartPose.offset(-4.0F, -2.0F, -4.0F));

		base.addOrReplaceChild("corner_2", CubeListBuilder.create()
				.texOffs(50, 13)
				.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2),
			PartPose.offset(-4.0F, -2.0F, 4.0F));

		base.addOrReplaceChild("corner_3", CubeListBuilder.create()
				.texOffs(50, 26)
				.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2),
			PartPose.offset(4.0F, -2.0F, 4.0F));

		base.addOrReplaceChild("corner_4", CubeListBuilder.create()
				.texOffs(50, 39)
				.addBox(-1.0F, -10.0F, -1.0F, 2, 10, 2),
			PartPose.offset(4.0F, -2.0F, -4.0F));

		var shutter = side1.addOrReplaceChild("shutter", CubeListBuilder.create()
				.texOffs(36, 51)
				.addBox(-0.5F, -4.0F, -1.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.136659280431156F, 0.0F, 0.0F));

		shutter.addOrReplaceChild("shutter_piece", CubeListBuilder.create()
				.texOffs(41, 51)
				.addBox(-1.5F, -1.0F, -1.0F, 3, 1, 1),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var tap = side1.addOrReplaceChild("tap_piece_1", CubeListBuilder.create()
			.texOffs(0, 51)
			.addBox(-1.5F, -3.0F, -2.0F, 1, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.136659280431156F, 0.0F, 0.0F));

		tap.addOrReplaceChild("tap_piece_2", CubeListBuilder.create()
				.texOffs(9, 51)
				.addBox(-0.5F, -3.0F, -2.0F, 1, 1, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		tap.addOrReplaceChild("tap_piece_3", CubeListBuilder.create()
				.texOffs(18, 51)
				.addBox(0.5F, -3.0F, -2.0F, 1, 3, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		tap.addOrReplaceChild("tap_piece_4", CubeListBuilder.create()
				.texOffs(27, 51)
				.addBox(-0.5F, -1.0F, -2.0F, 1, 1, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		base.addOrReplaceChild("leg_1", CubeListBuilder.create()
				.texOffs(81, 0)
				.addBox(-2.0F, -14.0F, -1.5F, 3, 14, 3),
			PartPose.offsetAndRotation(7.0F, 4.0F, -6.5F, 0.0F, 0.0F, -0.045553093477052F));

		base.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(81, 18)
				.addBox(-2.0F, -14.0F, -1.5F, 3, 14, 3),
			PartPose.offsetAndRotation(7.0F, 4.0F, 6.5F, 0.0F, 0.0F, -0.045553093477052F));

		var leg = base.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(81, 36)
				.addBox(-1.0F, -14.0F, -1.5F, 3, 14, 3),
			PartPose.offsetAndRotation(-7.0F, 4.0F, -6.5F, 0.0F, 0.0F, 0.045553093477052F));

		base.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(68, 36)
				.addBox(-1.0F, -14.0F, -1.5F, 3, 14, 3),
			PartPose.offsetAndRotation(-7.0F, 4.0F, 6.5F, 0.0F, 0.0F, 0.045553093477052F));

		var beam1 = base.addOrReplaceChild("beam_1", CubeListBuilder.create()
				.texOffs(95, 0)
				.addBox(-1.5F, -5.0F, -5.0F, 3, 2, 10),
			PartPose.offsetAndRotation(6.5F, 4.0F, 0.0F, 0.0F, 0.0F, -0.045553093477052F));

		var beam2 = base.addOrReplaceChild("beam_2", CubeListBuilder.create()
				.texOffs(95, 13)
				.addBox(-1.5F, -5.0F, -5.0F, 3, 2, 10),
			PartPose.offsetAndRotation(-6.5F, 4.0F, 0.0F, 0.0F, 0.0F, 0.045553093477052F));

		var beam3 = base.addOrReplaceChild("beam_3", CubeListBuilder.create()
				.texOffs(95, 26)
				.addBox(-5.0F, -5.0F, -1.51F, 10, 2, 3),
			PartPose.offset(0.0F, 4.0F, 6.5F));

		beam1.addOrReplaceChild("beam_1b", CubeListBuilder.create()
				.texOffs(95, 32)
				.addBox(-0.5F, -14.0F, -5.0F, 2, 2, 10),
			PartPose.ZERO);

		beam2.addOrReplaceChild("beam_2b", CubeListBuilder.create()
				.texOffs(95, 45)
				.addBox(-1.5F, -14.0F, -5.0F, 2, 2, 10),
			PartPose.ZERO);

		beam3.addOrReplaceChild("beam_3b", CubeListBuilder.create()
				.texOffs(95, 58)
				.addBox(-5.0F, -13.9F, -0.51F, 10, 2, 2),
			PartPose.ZERO);

		leg.addOrReplaceChild("beam_4b", CubeListBuilder.create()
				.texOffs(72, 58)
				.addBox(-5.0F, 0.0F, -1.49F, 10, 3, 1),
			PartPose.offsetAndRotation(6.5F, -14.2F, 0.0F, 0.0F, 0.0F, -0.045553093477052F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
