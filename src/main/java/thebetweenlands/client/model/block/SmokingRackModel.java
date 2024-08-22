package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SmokingRackModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var frontLeft1 = partDefinition.addOrReplaceChild("front_left_support_1", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-2.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offset(7.0F, 0.0F, -8.0F));

		var frontLeft2 = frontLeft1.addOrReplaceChild("front_left_support_2", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-2.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var frontLeft3 = frontLeft2.addOrReplaceChild("front_left_support_3", CubeListBuilder.create()
				.texOffs(0, 22)
				.addBox(-2.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		frontLeft3.addOrReplaceChild("front_left_support_4", CubeListBuilder.create()
				.texOffs(0, 33)
				.addBox(-2.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var frontRight1 = partDefinition.addOrReplaceChild("front_right_support_1", CubeListBuilder.create()
				.texOffs(18, 0)
				.addBox(0.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offset(-7.0F, 0.0F, -8.0F));

		var frontRight2 = frontRight1.addOrReplaceChild("front_right_support_2", CubeListBuilder.create()
				.texOffs(18, 11)
				.addBox(0.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var frontRight3 = frontRight2.addOrReplaceChild("front_right_support_3", CubeListBuilder.create()
				.texOffs(18, 22)
				.addBox(0.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		frontRight3.addOrReplaceChild("front_right_support_4", CubeListBuilder.create()
				.texOffs(18, 33)
				.addBox(0.0F, -8.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var backLeft1 = partDefinition.addOrReplaceChild("back_left_support_1", CubeListBuilder.create()
				.texOffs(9, 0)
				.addBox(-2.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offset(7.0F, 0.0F, 8.0F));

		var backLeft2 = backLeft1.addOrReplaceChild("back_left_support_2", CubeListBuilder.create()
				.texOffs(9, 11)
				.addBox(-2.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		var backLeft3 = backLeft2.addOrReplaceChild("back_left_support_3", CubeListBuilder.create()
				.texOffs(9, 22)
				.addBox(-2.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		backLeft3.addOrReplaceChild("back_left_support_4", CubeListBuilder.create()
				.texOffs(9, 33)
				.addBox(-2.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		var backRight1 = partDefinition.addOrReplaceChild("back_right_support_1", CubeListBuilder.create()
				.texOffs(27, 0)
				.addBox(0.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offset(-7.0F, 0.0F, 8.0F));

		var backRight2 = backRight1.addOrReplaceChild("back_right_support_2", CubeListBuilder.create()
				.texOffs(27, 11)
				.addBox(0.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		var backRight3 = backRight2.addOrReplaceChild("back_right_support_3", CubeListBuilder.create()
				.texOffs(27, 22)
				.addBox(0.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		backRight3.addOrReplaceChild("back_right_support_4", CubeListBuilder.create()
				.texOffs(27, 33)
				.addBox(0.0F, -8.0F, -2.0F, 2, 8, 2),
			PartPose.offsetAndRotation(0.01F, -8.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		var topBeam = partDefinition.addOrReplaceChild("top_beam", CubeListBuilder.create()
				.texOffs(0, 44)
				.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -30.5F, 0.0F, 0.7853981633974483F, 0.0F, 0.0F));

		topBeam.addOrReplaceChild("top_left_knot", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F, 0.091106186954104F));

		topBeam.addOrReplaceChild("top_right_knot", CubeListBuilder.create()
				.texOffs(49, 0)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.091106186954104F, -0.045553093477052F, 0.0F));

		var topConnector = topBeam.addOrReplaceChild("top_hook_connector", CubeListBuilder.create()
				.texOffs(36, 21)
				.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, -0.7853981633974483F, 0.0F, 0.0F));

		topConnector.addOrReplaceChild("top_hook", CubeListBuilder.create()
				.texOffs(41, 18)
				.addBox(0.0F, 0.0F, -1.5F, 0.001F, 3, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -1.2292353921796064F, 0.0F));

		var frontBeam = partDefinition.addOrReplaceChild("front_beam", CubeListBuilder.create()
				.texOffs(0, 49)
				.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -16.0F, -6.0F, -0.22759093446006054F, 0.0F, 0.0F));

		frontBeam.addOrReplaceChild("front_left_knot", CubeListBuilder.create()
				.texOffs(36, 7)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, -0.045553093477052F, 0.045553093477052F));

		frontBeam.addOrReplaceChild("front_right_knot", CubeListBuilder.create()
				.texOffs(49, 7)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, -0.045553093477052F, 0.045553093477052F, 0.0F));

		var frontConnector = frontBeam.addOrReplaceChild("front_hook_connector", CubeListBuilder.create()
				.texOffs(36, 25)
				.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.22759093446006054F, 0.0F, 0.0F));

		frontConnector.addOrReplaceChild("front_hook", CubeListBuilder.create()
				.texOffs(48, 18)
				.addBox(0.0F, 0.0F, -1.5F, 0.001F, 3, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.5F, 0.0F, 0.5009094953223726F, 0.0F));

		var backBeam = partDefinition.addOrReplaceChild("back_beam", CubeListBuilder.create()
				.texOffs(0, 54)
				.addBox(-8.0F, -1.0F, -1.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -16.0F, 6.0F, 0.22759093446006054F, 0.0F, 0.0F));

		backBeam.addOrReplaceChild("back_left_knot", CubeListBuilder.create()
				.texOffs(36, 14)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F, 0.091106186954104F));

		backBeam.addOrReplaceChild("back_right_knot", CubeListBuilder.create()
				.texOffs(49, 14)
				.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, -0.045553093477052F, -0.045553093477052F, -0.045553093477052F));

		var backConnector = backBeam.addOrReplaceChild("back_hook_connector", CubeListBuilder.create()
				.texOffs(36, 28)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.22759093446006054F, 0.0F, 0.0F));

		backConnector.addOrReplaceChild("back_hook", CubeListBuilder.create()
				.texOffs(55, 18)
				.addBox(0.0F, 0.0F, -1.5F, 0.001F, 3, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, -0.5F, 0.0F, -0.31869712141416456F, 0.0F));

		partDefinition.addOrReplaceChild("left_netting", CubeListBuilder.create()
				.texOffs(37, 19)
				.addBox(0.0F, 0.0F, -6.0F, 0.001F, 14, 12),
			PartPose.offset(6.5F, -17.0F, 0.0F));

		partDefinition.addOrReplaceChild("right_netting", CubeListBuilder.create()
				.texOffs(37, 34)
				.addBox(0.0F, 0.0F, -6.0F, 0.001F, 14, 12),
			PartPose.offset(-6.5F, -17.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
