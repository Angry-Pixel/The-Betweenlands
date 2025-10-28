package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DecayPitShieldModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();


		var base_mid = partDefinition.addOrReplaceChild("base_mid", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0F, -1.5F, -3.0F, 6, 3, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		base_mid.addOrReplaceChild("base_right", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-5.0F, -1.49F, 0.0F, 5, 3, 3),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -3.0F, 0.0F, 0.18203784098300857F, 0.0F));
		base_mid.addOrReplaceChild("base_left", CubeListBuilder.create()
				.texOffs(0, 7).addBox(0.0F, -1.49F, 0.0F, 5, 3, 3),
			PartPose.offsetAndRotation(3.0F, 0.0F, -3.0F, 0.0F, -0.18203784098300857F, 0.0F));

		var lowerplate_mid = base_mid.addOrReplaceChild("lowerplate_mid", CubeListBuilder.create()
				.texOffs(17, 21).addBox(-3.0F, 0.0F, 0.0F, 6, 7, 2),
			PartPose.offsetAndRotation(0.0F, 1.5F, -2.5F, 0.22759093446006054F, 0.0F, 0.0F));
		lowerplate_mid.addOrReplaceChild("lowerplate_left", CubeListBuilder.create()
				.texOffs(17, 31).addBox(0.0F, 0.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F));
		lowerplate_mid.addOrReplaceChild("lowerplate_right", CubeListBuilder.create()
				.texOffs(17, 39).addBox(-4.0F, 0.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F, 0.0F));

		var upperplate_mid = base_mid.addOrReplaceChild("upperplate_mid", CubeListBuilder.create()
				.texOffs(0, 21).addBox(-3.0F, -7.0F, 0.0F, 6, 7, 2),
			PartPose.offsetAndRotation(0.0F, -1.5F, -2.5F, -0.18203784098300857F, 0.0F, 0.0F));
		upperplate_mid.addOrReplaceChild("upperplate_left", CubeListBuilder.create()
				.texOffs(0, 31).addBox(0.0F, -5.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F));
		upperplate_mid.addOrReplaceChild("upperplate_right", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-4.0F, -5.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
