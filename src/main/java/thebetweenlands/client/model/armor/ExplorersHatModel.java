package thebetweenlands.client.model.armor;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ExplorersHatModel extends BLArmorModel {

	public ExplorersHatModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = HumanoidArmorModel.createBodyLayer(CubeDeformation.NONE);
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		var hatrim = head.addOrReplaceChild("hatrim", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12, 2, 12),
			PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));

		hatrim.addOrReplaceChild("hatcup", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-4.5F, -11.4F, -6.3F, 9, 4, 9),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.091106186954104F, 0.0F, 0.0F));

		hatrim.addOrReplaceChild("rimjobback", CubeListBuilder.create()
				.texOffs(37, 16).addBox(-5.0F, 0.0F, 0.0F, 10, 2, 1),
			PartPose.offsetAndRotation(0.0F, -8.0F, 6.0F, -0.18203784098300857F, 0.0F, 0.0F));

		hatrim.addOrReplaceChild("rimjobfront", CubeListBuilder.create()
				.texOffs(37, 16).addBox(-5.0F, 0.0F, -1.0F, 10, 2, 1),
			PartPose.offsetAndRotation(0.0F, -8.0F, -6.0F, 0.136659280431156F, 0.0F, 0.0F));

		hatrim.addOrReplaceChild("rimjobleft", CubeListBuilder.create()
				.texOffs(37, 0).addBox(0.0F, -2.0F, -5.0F, 1, 2, 10),
			PartPose.offsetAndRotation(6.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		hatrim.addOrReplaceChild("rimjobright", CubeListBuilder.create()
				.texOffs(37, 0).addBox(-1.0F, -2.0F, -5.0F, 1, 2, 10),
			PartPose.offsetAndRotation(-6.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
