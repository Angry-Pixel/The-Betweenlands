package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.spirit_tree.LargeSpiritTreeFace;

public class LargeSpiritTreeFaceModel extends MowzieModelBase<LargeSpiritTreeFace> {

	public LargeSpiritTreeFaceModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create(boolean headModel) {
		MeshDefinition definition = headModel ? HumanoidArmorModel.createBodyLayer(CubeDeformation.NONE) : new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		if (headModel) partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		var mask = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, -8.0F, -3.0F, 18, 12, 3),
			PartPose.offset(0.0F, headModel ? -2.0F : 8.5F, headModel ? -4.0F : 0.0F));
		mask.addOrReplaceChild("head_lower", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-7.0F, 0.0F, 0.0F, 14, 8, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));

		var nose1 = mask.addOrReplaceChild("nose1", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-3.0F, -5.0F, -2.0F, 6, 5, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, -3.0F, 0.045553093477052F, 0.0F, 0.0F));
		var nose2 = nose1.addOrReplaceChild("nose2", CubeListBuilder.create()
				.texOffs(0, 37).addBox(-3.0F, -10.0F, 0.0F, 6, 10, 5),
			PartPose.offsetAndRotation(0.0F, -5.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		mask.addOrReplaceChild("cheekthingy_right", CubeListBuilder.create()
				.texOffs(9, 55).addBox(-2.0F, 0.0F, 0.0F, 2, 10, 2),
			PartPose.offset(-3.0F, 4.0F, -3.0F));
		mask.addOrReplaceChild("cheekthingy_rightupper", CubeListBuilder.create()
				.texOffs(9, 68).addBox(-1.0F, 0.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(-9.0F, -3.0F, -3.0F, 0.0F, 0.091106186954104F, 0.0F));

		mask.addOrReplaceChild("cheekthingy_left", CubeListBuilder.create()
				.texOffs(0, 55).addBox(0.0F, 0.0F, 0.0F, 2, 10, 2),
			PartPose.offset(3.0F, 4.0F, -3.0F));
		mask.addOrReplaceChild("cheekthingy_leftupper", CubeListBuilder.create()
				.texOffs(0, 68).addBox(0.0F, 0.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(9.0F, -3.0F, -3.0F, 0.0F, -0.091106186954104F, 0.0F));

		var brow_left = nose2.addOrReplaceChild("brow_left", CubeListBuilder.create()
				.texOffs(43, 0).addBox(0.0F, -5.0F, 0.0F, 12, 5, 5),
			PartPose.offsetAndRotation(3.0F, -5.0F, 0.02F, 0.0F, 0.0F, -0.22759093446006054F));
		brow_left.addOrReplaceChild("browpiece_left", CubeListBuilder.create()
				.texOffs(68, 45).addBox(0.0F, -4.0F, -2.0F, 4, 4, 4),
			PartPose.offsetAndRotation(4.0F, -5.0F, 2.02F, 0.0F, 0.0F, 0.4553564018453205F));
		brow_left.addOrReplaceChild("brow_leftpiece_a", CubeListBuilder.create()
				.texOffs(43, 11).addBox(0.0F, -5.0F, 0.0F, 6, 5, 5),
			PartPose.offsetAndRotation(12.0F, 0.0F, -0.02F, 0.0F, 0.0F, -0.5918411493512771F));
		var brow_leftpiece_b = brow_left.addOrReplaceChild("brow_leftpiece_b", CubeListBuilder.create()
				.texOffs(43, 22).addBox(-4.0F, 0.0F, 0.0F, 5, 5, 5),
			PartPose.offsetAndRotation(11.0F, 0.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));
		brow_leftpiece_b.addOrReplaceChild("brow_leftpiece_c", CubeListBuilder.create()
				.texOffs(43, 33).addBox(0.0F, 0.0F, 0.0F, 5, 6, 5),
			PartPose.offsetAndRotation(-4.0F, 5.0F, -0.02F, 0.0F, 0.0F, -0.22759093446006054F));

		nose2.addOrReplaceChild("browpiece_mid", CubeListBuilder.create()
				.texOffs(43, 45).addBox(-4.0F, -5.0F, -2.0F, 8, 5, 4),
			PartPose.offset(0.0F, -10.0F, 2.0F));

		var brow_right = nose2.addOrReplaceChild("brow_right", CubeListBuilder.create()
				.texOffs(78, 0).addBox(-12.0F, -5.0F, 0.0F, 12, 5, 5),
			PartPose.offsetAndRotation(-3.0F, -5.0F, 0.02F, 0.0F, 0.0F, 0.22759093446006054F));
		brow_right.addOrReplaceChild("browpiece_right", CubeListBuilder.create()
				.texOffs(85, 45).addBox(-4.0F, -4.0F, -2.0F, 4, 4, 4),
			PartPose.offsetAndRotation(-4.0F, -5.0F, 2.02F, 0.0F, 0.0F, -0.4553564018453205F));
		brow_right.addOrReplaceChild("brow_rightpiece_a", CubeListBuilder.create()
				.texOffs(78, 11).addBox(-6.0F, -5.0F, 0.0F, 6, 5, 5),
			PartPose.offsetAndRotation(-12.0F, 0.0F, -0.02F, 0.0F, 0.0F, 0.5918411493512771F));
		var brow_rightpiece_b = brow_right.addOrReplaceChild("brow_rightpiece_b", CubeListBuilder.create()
				.texOffs(78, 22).addBox(-1.0F, 0.0F, 0.0F, 5, 5, 5),
			PartPose.offsetAndRotation(-11.0F, 0.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));
		brow_rightpiece_b.addOrReplaceChild("brow_rightpiece_c", CubeListBuilder.create()
				.texOffs(78, 33).addBox(-5.0F, 0.0F, 0.0F, 5, 6, 5),
			PartPose.offsetAndRotation(4.0F, 5.0F, -0.02F, 0.0F, 0.0F, 0.22759093446006054F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
