package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.GreeblingCorpse;

public class GreeblingCorpseModel extends MowzieModelBase<GreeblingCorpse> {
	public GreeblingCorpseModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var waist = partDefinition.addOrReplaceChild("waist", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, 0.0F, -3.0F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, 23.5F, -2.0F, -1.4114477660878142F, 0.5462880558742251F, -0.045553093477052F));
		var body = waist.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 8).addBox(-2.5F, -3.0F, -3.5F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F, -0.18203784098300857F));
		waist.addOrReplaceChild("ribcage", CubeListBuilder.create()
				.texOffs(17, 0).addBox(0.0F, 0.0F, -3.0F, 1, 4, 3),
			PartPose.offset(1.0F, 0.0F, 0.0F));

		var head = body.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-2.0F, -4.0F, -4.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -2.5F, -1.0F, -0.36425021489121656F, 0.6373942428283291F, 0.22759093446006054F));
		var throat = head.addOrReplaceChild("throat", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-2.0F, -1.0F, -2.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(17, 26).addBox(-4.0F, -0.5F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(-1.0F, -3.5F, -1.0F, -0.27314402793711257F, -0.6829473363053812F, 0.7740535232594852F));
		head.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(17, 22).addBox(-1.0F, -0.5F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(1.0F, -3.5F, -1.0F, -0.091106186954104F, -0.5462880558742251F, -0.5009094953223726F));
		head.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(17, 18).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, -4.0F, -0.22759093446006054F, 0.0F, 0.0F));
		throat.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 29).addBox(-3.0F, 0.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(1.5F, -1.0F, -2.0F, 0.6829473363053812F, 0.136659280431156F, 0.0F));

		var rightArm = body.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(19, 10).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.0F, -2.5F, -2.0F, 0.5918411493512771F, 0.091106186954104F, 0.8651597102135892F));
		rightArm.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(24, 10).addBox(-1.0F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.51F, 3.5F, 0.5F, -0.6829473363053812F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}
}
