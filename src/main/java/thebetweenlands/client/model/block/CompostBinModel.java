package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class CompostBinModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var left = partDefinition.addOrReplaceChild("bin_left", CubeListBuilder.create()
				.texOffs(37, 0)
				.addBox(0.0F, 1.0F, -8.0F, 2, 12, 16),
			PartPose.offsetAndRotation(4.5F, -15.5F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		var right = partDefinition.addOrReplaceChild("bin_right", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 1.0F, -8.0F, 2, 12, 16),
			PartPose.offsetAndRotation(-4.5F, -15.5F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		partDefinition.addOrReplaceChild("bin_back", CubeListBuilder.create()
				.texOffs(75, 0)
				.addBox(-6.0F, -0.6F, 0.0F, 12, 12, 2),
			PartPose.offset(0.0F, -14.0F, 5.0F));

		partDefinition.addOrReplaceChild("bin_top", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox(-15.0F, -1.0F, -8.01F, 16, 2, 16),
			PartPose.offset(7.0F, -15.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_support", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 6, 4),
			PartPose.offsetAndRotation(8.0F, -14.0F, -5.0F, 0.0F, 0.0F, 0.136659280431156F));

		partDefinition.addOrReplaceChild("right_support", CubeListBuilder.create()
				.texOffs(35, 0)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 6, 4),
			PartPose.offsetAndRotation(8.0F, -14.0F, 5.0F, 0.0F, 0.0F, 0.136659280431156F));

		var mainNet = partDefinition.addOrReplaceChild("net_1", CubeListBuilder.create()
				.texOffs(75, 15)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0),
			PartPose.offsetAndRotation(0.0F, -14.0F, -7.0F, -0.045553093477052F, 0.091106186954104F, 0.0F));

		var subNet = mainNet.addOrReplaceChild("net_2", CubeListBuilder.create()
				.texOffs(75, 21)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 0),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.091106186954104F, 0.0F, 0.0F));

		subNet.addOrReplaceChild("net_3", CubeListBuilder.create()
				.texOffs(75, 27)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 4, 0),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, -0.31869712141416456F, 0.0F, 0.0F));

		left.addOrReplaceChild("leg_1", CubeListBuilder.create()
			.texOffs(30, 29)
			.addBox(-2.0F, 0.0F, -1.99F, 2, 3, 5),
			PartPose.offsetAndRotation(2.0F, 13.0F, -6.0F, 0.0F, 0.0F, 0.091106186954104F));

		left.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(45, 29)
				.addBox(-2.0F, 0.0F, -3.01F, 2, 3, 5),
			PartPose.offsetAndRotation(2.0F, 13.0F, 6.0F, 0.0F, 0.0F, 0.091106186954104F));

		right.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(0.0F, 0.0F, -1.99F, 2, 3, 5),
			PartPose.offsetAndRotation(-2.0F, 13.0F, -6.0F, 0.0F, 0.0F, -0.091106186954104F));

		right.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(15, 29)
				.addBox(0.0F, 0.0F, -3.01F, 2, 3, 5),
			PartPose.offsetAndRotation(-2.0F, 13.0F, 6.0F, 0.0F, 0.0F, -0.091106186954104F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
