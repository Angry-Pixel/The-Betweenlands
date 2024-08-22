package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CrabPotFilterModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(36, 20)
			.addBox(-8.0F, -1.0F, -8.0F, 16, 1, 16),
			PartPose.ZERO);

		partDefinition.addOrReplaceChild("mesh", CubeListBuilder.create()
				.texOffs(71, 0)
				.addBox(-6.0F, 0.0F, -6.0F, 12, 0.001F, 12),
			PartPose.offset(0.0F, -3.99F, 0.0F));

		partDefinition.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2),
			PartPose.offset(-6.0F, 0.0F, -6.0F));

		partDefinition.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
				.texOffs(9, 20)
				.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2),
			PartPose.offset(6.0F, 0.0F, -6.0F));

		partDefinition.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
				.texOffs(18, 20)
				.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2),
			PartPose.offset(-6.0F, 0.0F, 6.0F));

		partDefinition.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
				.texOffs(27, 20)
				.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2),
			PartPose.offset(6.0F, 0.0F, 6.0F));

		partDefinition.addOrReplaceChild("left_side_bottom", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox(-0.5F, -5.0F, -7.0F, 1, 5, 14),
			PartPose.offset(-7.5F, -1.0F, 0.0F));

		partDefinition.addOrReplaceChild("right_side_bottom", CubeListBuilder.create()
				.texOffs(66, 38)
				.addBox(-0.5F, -5.0F, -7.0F, 1, 5, 14),
			PartPose.offset(7.5F, -1.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_side_top", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-0.5F, -2.0F, -7.0F, 1, 4, 14),
			PartPose.offset(-7.5F, -14.0F, 0.0F));

		partDefinition.addOrReplaceChild("right_side_top", CubeListBuilder.create()
				.texOffs(64, 0)
				.addBox(-0.5F, -2.0F, -7.0F, 1, 4, 14),
			PartPose.offset(7.5F, -14.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_mesh_frame", CubeListBuilder.create()
				.texOffs(17, 0)
				.addBox(-0.5F, -1.0F, -5.0F, 1, 3, 10),
			PartPose.offsetAndRotation(-6.65F, -4.95F, 0.0F, 0.0F, 0.0F, -0.7853981633974483F));

		partDefinition.addOrReplaceChild("right_mesh_frame", CubeListBuilder.create()
				.texOffs(56, 0)
				.addBox(-0.5F, -1.0F, -5.0F, 1, 3, 10),
			PartPose.offsetAndRotation(6.65F, -4.95F, 0.0F, 0.0F, 0.0F, 0.7853981633974483F));

		partDefinition.addOrReplaceChild("front_mesh_frame", CubeListBuilder.create()
				.texOffs(36, 5)
				.addBox(-5.0F, -0.5F, 0.0F, 10, 1, 2),
			PartPose.offsetAndRotation(0.0F, -4.95F, -6.65F, -0.7853981633974483F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("back_mesh_frame", CubeListBuilder.create()
				.texOffs(35, 0)
				.addBox(-5.0F, -0.5F, -2.0F, 10, 1, 3),
			PartPose.offsetAndRotation(0.0F, -4.95F, 6.65F, 0.7853981633974483F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("front_left_bottom", CubeListBuilder.create()
				.texOffs(97, 51)
				.addBox(-1.5F, -6.0F, -0.5F, 3, 5, 1),
			PartPose.offset(-6.5F, 0.0F, -7.5F));

		partDefinition.addOrReplaceChild("front_right_bottom", CubeListBuilder.create()
				.texOffs(106, 51)
				.addBox(-1.5F, -6.0F, -0.5F, 3, 5, 1),
			PartPose.offset(6.5F, 0.0F, -7.5F));

		partDefinition.addOrReplaceChild("front_top", CubeListBuilder.create()
				.texOffs(94, 13)
				.addBox(-8.0F, -2.0F, -0.5F, 16, 4, 1),
			PartPose.offset(0.0F, -14.0F, -7.5F));

		partDefinition.addOrReplaceChild("back_top", CubeListBuilder.create()
				.texOffs(30, 13)
				.addBox(-8.0F, -2.0F, -0.5F, 16, 4, 1),
			PartPose.offset(0.0F, -14.0F, 7.5F));

		partDefinition.addOrReplaceChild("back_bottom", CubeListBuilder.create()
				.texOffs(31, 51)
				.addBox(-8.0F, -2.5F, -0.5F, 16, 5, 1),
			PartPose.offset(0.0F, -3.5F, 7.5F));

		partDefinition.addOrReplaceChild("left_mesh", CubeListBuilder.create()
				.texOffs(17, 28)
				.addBox(0.0F, -3.0F, -5.0F, 0, 6, 10),
			PartPose.offset(-7.0F, -9.0F, 0.0F));

		partDefinition.addOrReplaceChild("right_mesh", CubeListBuilder.create()
				.texOffs(59, 28)
				.addBox(0.0F, -3.0F, -5.0F, 0, 6, 10),
			PartPose.offset(7.0F, -9.0F, 0.0F));

		partDefinition.addOrReplaceChild("back_mesh", CubeListBuilder.create()
				.texOffs(38, 38)
				.addBox(-5.0F, -3.0F, 0.0F, 10, 6, 0),
			PartPose.offset(0.0F, -9.0F, 7.0F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
