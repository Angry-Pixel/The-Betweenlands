package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RepellerModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var repeller = partDefinition.addOrReplaceChild("repeller", CubeListBuilder.create(), PartPose.ZERO);

		var base = repeller.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(17, 0)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.ZERO);

		var pole1 = base.addOrReplaceChild("pole_1", CubeListBuilder.create()
				.texOffs(17, 7)
				.addBox(-1.0F, -10.0F, -1.0F, 2, 8, 2),
			PartPose.ZERO);

		var pole2 = pole1.addOrReplaceChild("pole_2", CubeListBuilder.create()
				.texOffs(26, 7)
				.addBox(-1.01F, -6.0F, 0.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, -10.0F, -1.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var pole3 = pole2.addOrReplaceChild("pole_3", CubeListBuilder.create()
				.texOffs(35, 7)
				.addBox(-1.02F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var pole4 = pole3.addOrReplaceChild("pole_4", CubeListBuilder.create()
				.texOffs(17, 18)
				.addBox(-0.99F, -6.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.7285004297824331F, 0.0F, 0.0F));

		pole4.addOrReplaceChild("pole_5", CubeListBuilder.create()
				.texOffs(26, 15)
				.addBox(-1.0F, -2.0F, -6.0F, 2, 2, 9),
			PartPose.offsetAndRotation(0.0F, -6.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		var jar = repeller.addOrReplaceChild("jar", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 0.0F, -2.0F, 4, 4, 4)
				.texOffs(0, 9)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2)
				.texOffs(0, 13)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 1, 3),
			PartPose.offsetAndRotation(0.0F, -19.5F, -2.5F, 0.4553564018453205F, 0.045553093477052F, 0.091106186954104F));

		var rope1 = jar.addOrReplaceChild("rope_1", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-0.5F, -2.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, 0.091106186954104F, -0.08080874436733745F, -0.061959188445798695F));

		rope1.addOrReplaceChild("rope_2", CubeListBuilder.create()
				.texOffs(1, 18)
				.addBox(-0.515F, -1.0F, 1.0F, 1, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0471975511965976F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("jar_liquid", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, -19.0F, -2.5F, 0.4553564018453205F, 0.045553093477052F, 0.091106186954104F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
