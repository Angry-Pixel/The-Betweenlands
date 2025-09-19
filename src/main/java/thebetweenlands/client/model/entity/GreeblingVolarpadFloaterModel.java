package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.GreeblingVolarpadFloater;

public class GreeblingVolarpadFloaterModel extends MowzieModelBase<GreeblingVolarpadFloater> {

	public GreeblingVolarpadFloaterModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(71, 9).addBox(-2.0F, -4.0F, -1.5F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 19.5F, 1.3F, 0.0F, 0.0F, 0.33161255787892263F));

		var chest = body.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(71, 0).addBox(-2.5F, -3.5F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -3.4F, 0.0F, 0.5462880558742251F, 0.0F, 0.0F));

		var arm_right_upper = chest.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(106, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, -2.7F, 0.5F, 2.882760325519034F, -0.2937389131106456F, -0.6033603224144397F));
		arm_right_upper.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(111, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.2284635990860578F, 0.045553093477052F, -0.8134979643545569F));

		var arm_left_upper = chest.addOrReplaceChild("left_arm1", CubeListBuilder.create()
				.texOffs(96, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, -2.7F, 0.5F, -0.3565707661824416F, 0.045553093477052F, -0.36425021489121656F));
		arm_left_upper.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(101, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.6829473363053812F, 0.0F, 0.0F));

		var legright1 = body.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(116, 5).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.7F, -0.4F, 0.0F, -0.1623156204354726F, -0.02426007660272119F, 0.2527236756887789F));
		legright1.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(121, 5).addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.42114794850623166F, 0.0F, 0.0F));

		var legleft1 = body.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(116, 0).addBox(-0.4F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.7F, -0.4F, 0.0F, -0.5462880558742251F, -0.06300638599699529F, -0.40980330836826856F));
		legleft1.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(121, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.6829473363053812F, -0.045553093477052F, 0.045553093477052F));

		var head_main = chest.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(71, 17).addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -3.8F, -0.5F, -0.5918411493512771F, 0.0F, 0.0F));
		head_main.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(71, 25).addBox(-1.5F, -0.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.5009094953223726F, 0.0F, 0.0F));
		head_main.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(82, 25).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		head_main.addOrReplaceChild("mouth", CubeListBuilder.create()
				.texOffs(88, 21).addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head_main.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(87, 25).addBox(-1.0F, -2.0F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(2.0F, -1.5F, -0.5F, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F));
		head_main.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(87, 29).addBox(-4.0F, -2.0F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(-2.0F, -1.5F, -0.5F, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
