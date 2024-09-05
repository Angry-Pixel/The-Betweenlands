package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MossBedModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-7.0F, -4.0F, -7.0F, 14, 4, 30),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("moss", CubeListBuilder.create()
				.texOffs(0, 35)
				.addBox(-7.0F, -2.01F, -7.0F, 14, 2, 30),
			PartPose.offset(0.0F, -4.0F, 0.0F));


		base.addOrReplaceChild("front_stand_left", CubeListBuilder.create()
			.texOffs(59, 14)
			.addBox(-1.0F, -10.0F, 0.0F, 3, 10, 3),
			PartPose.offsetAndRotation(-6.95F, 2.0F, -8.0F, 0.0F, 0.0F, 0.045553093477052F));

		var frontStandR = base.addOrReplaceChild("front_stand_right", CubeListBuilder.create()
				.texOffs(59, 0)
				.addBox(-2.0F, -10.0F, 0.0F, 3, 10, 3),
			PartPose.offsetAndRotation(6.95F, 2.0F, -8.0F, 0.0F, 0.0F, -0.045553093477052F));

		var frontBeam = frontStandR.addOrReplaceChild("front_beam", CubeListBuilder.create()
				.texOffs(72, 13)
				.addBox(-15.0F, -2.8F, -1.51F, 15, 3, 3),
			PartPose.offsetAndRotation(1.0F, -10.0F, 1.5F, 0.0F, 0.0F, 0.045553093477052F));

		frontBeam.addOrReplaceChild("front_stand_middle", CubeListBuilder.create()
				.texOffs(72, 0)
				.addBox(-1.5F, 0.0F, -1.49F, 3, 10, 2),
			PartPose.offset(-7.5F, 0.0F, 0.0F));

		base.addOrReplaceChild("back_stand_left", CubeListBuilder.create()
				.texOffs(72, 35)
				.addBox(-1.0F, -12.0F, 0.0F, 3, 12, 3),
			PartPose.offsetAndRotation(-7.04F, 2.0F, 22.0F, 0.0F, 0.0F, 0.045553093477052F));

		var backStandR = base.addOrReplaceChild("back_stand_right", CubeListBuilder.create()
				.texOffs(59, 35)
				.addBox(-2.0F, -12.0F, 0.0F, 3, 12, 3),
			PartPose.offsetAndRotation(7.04F, 2.0F, 22.0F, 0.0F, 0.0F, -0.045553093477052F));

		var backBeam = backStandR.addOrReplaceChild("back_beam_1", CubeListBuilder.create()
				.texOffs(59, 51)
				.addBox(-15.0F, -2.8F, -1.49F, 15, 3, 3),
			PartPose.offsetAndRotation(1.0F, -12.0F, 1.5F, 0.0F, 0.0F, 0.045553093477052F));

		backBeam.addOrReplaceChild("back_beam_2", CubeListBuilder.create()
				.texOffs(59, 58)
				.addBox(-6.5F, -1.0F, -1.5F, 13, 1, 3),
			PartPose.offset(-7.5F, -2.8F, 0.0F));

		backBeam.addOrReplaceChild("back_stand_middle", CubeListBuilder.create()
				.texOffs(85, 35)
				.addBox(-1.5F, 0.0F, -0.59F, 3, 12, 2),
			PartPose.offset(-7.5F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
