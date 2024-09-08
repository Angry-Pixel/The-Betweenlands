package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class InfuserModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var infuser = partDefinition.addOrReplaceChild("infuser", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -10.0F, -7.0F, 10, 10, 2)
				.texOffs(0, 13)
				.addBox(-7.0F, -10.0F, -5.0F, 2, 10, 10)
				.texOffs(25, 0)
				.addBox(-5.0F, -10.0F, 5.0F, 10, 10, 2)
				.texOffs(25, 13)
				.addBox(5.0F, -10.0F, -5.0F, 2, 10, 10)
				.texOffs(0, 34)
				.addBox(3.0F, -10.0F, -5.0F, 2, 9, 2)
				.texOffs(9, 34)
				.addBox(3.0F, -10.0F, 3.0F, 2, 9, 2)
				.texOffs(18, 34)
				.addBox(-5.0F, -10.0F, 3.0F, 2, 9, 2)
				.texOffs(27, 34)
				.addBox(-5.0F, -10.0F, -5.0F, 2, 9, 2)
				.texOffs(0, 46)
				.addBox(-5.0F, -1.0F, -5.0F, 10, 3, 10)
				.texOffs(65, 10)
				.addBox(-7.5F, -2.5F, -7.5F, 15, 3, 15),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		infuser.addOrReplaceChild("leg_1", CubeListBuilder.create()
				.texOffs(65, 0)
				.addBox(0.0F, -1.5F, -3.0F, 3, 6, 3),
			PartPose.offsetAndRotation(-6.5F, 0.0F, 6.5F, 0.22759093446006054F, -0.022689280275926284F, 0.22759093446006054F));

		infuser.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(78, 0)
				.addBox(-3.0F, -1.5F, -3.0F, 3, 6, 3),
			PartPose.offsetAndRotation(6.5F, 0.0F, 6.5F, 0.22759093446006054F, 0.022689280275926284F, -0.22759093446006054F));

		infuser.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(91, 0)
				.addBox(-3.0F, -1.5F, 0.0F, 3, 6, 3),
			PartPose.offsetAndRotation(6.5F, 0.0F, -6.5F, -0.22759093446006054F, 0.022689280275926284F, -0.22759093446006054F));

		infuser.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(104, 0)
				.addBox(0.0F, -1.5F, 0.0F, 3, 6, 3),
			PartPose.offsetAndRotation(-6.5F, 0.0F, -6.5F, -0.22759093446006054F, -0.022689280275926284F, 0.22759093446006054F));

		var spoon = partDefinition.addOrReplaceChild("spoon", CubeListBuilder.create()
				.texOffs(65, 29)
				.addBox(-1.5F, 0.0F, -1.0F, 3, 4, 1),
			PartPose.offsetAndRotation(-0.6F, -10.1F, 2.5F, -0.5918411493512771F, -0.5009094953223726F, -0.136659280431156F));

		spoon.addOrReplaceChild("handle", CubeListBuilder.create()
				.texOffs(74, 29)
				.addBox(-1.0F, -7.0F, -1.5F, 2, 8, 2)
				.texOffs(83, 29)
				.addBox(-1.5F, -9.0F, -2.0F, 3, 2, 3),
			PartPose.rotation(0.18203784098300857F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
