package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.spirit_tree.AbstractSmallSpritTreeFace;

public class SmallSpiritTreeFaceModel<T extends AbstractSmallSpritTreeFace> extends MowzieModelBase<T> {

	public SmallSpiritTreeFaceModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createFace1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, -4.0F, -2.0F, 10, 8, 2, new CubeDeformation(0.0F)),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(0, 11).addBox(-3.0F, 0.0F, 0.0F, 6, 4, 2, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.091106186954104F, 0.0F, 0.0F));

		var nose1 = head.addOrReplaceChild("nose1", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.045553093477052F, 0.0F, 0.0F));
		var nose2 = nose1.addOrReplaceChild("nose2", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.5F, -6.0F, 0.0F, 3, 6, 3, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var leftBrow = nose2.addOrReplaceChild("left_brow", CubeListBuilder.create()
				.texOffs(25, 0).addBox(0.0F, -3.0F, 0.0F, 5, 3, 3, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(1.5F, -3.0F, 0.02F, 0.0F, 0.0F, -0.091106186954104F));
		var leftBrow2 = leftBrow.addOrReplaceChild("left_brow2", CubeListBuilder.create()
				.texOffs(25, 7).addBox(0.0F, -3.0F, 0.0F, 3, 3, 3, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.02F, 0.0F, 0.0F, -0.6373942428283291F));
		leftBrow2.addOrReplaceChild("left_brow3", CubeListBuilder.create()
				.texOffs(25, 14).addBox(-3.0F, -3.0F, 0.0F, 3, 3, 6, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(2.98F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var rightBrow = nose2.addOrReplaceChild("right_brow", CubeListBuilder.create()
				.texOffs(42, 0).addBox(-5.0F, -3.0F, 0.0F, 5, 3, 3, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(-1.5F, -3.0F, 0.02F, 0.0F, 0.0F, 0.091106186954104F));
		var rightBrow2 = rightBrow.addOrReplaceChild("right_brow2", CubeListBuilder.create()
				.texOffs(42, 7).addBox(-3.0F, -3.0F, 0.0F, 3, 3, 3, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -0.02F, 0.0F, 0.0F, 0.6373942428283291F));
		rightBrow2.addOrReplaceChild("right_brow3", CubeListBuilder.create()
				.texOffs(44, 14).addBox(0.0F, -3.0F, 0.0F, 3, 3, 6, new CubeDeformation(0.0F)),
			PartPose.offsetAndRotation(-2.98F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public static LayerDefinition createFace2(boolean headModel) {
		MeshDefinition definition = headModel ? HumanoidArmorModel.createMesh(CubeDeformation.NONE, 0.0F) : new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		if (headModel) partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -4.0F, -2.0F, 10, 8, 2),
			PartPose.offset(0.0F, 0.0F, headModel ? -2.0F : 0.0F));

		head.addOrReplaceChild("chin", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-3.0F, 0.0F, 0.0F, 6, 4, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.091106186954104F, 0.0F, 0.0F));

		var nose = head.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.045553093477052F, 0.0F, 0.0F));

		nose.addOrReplaceChild("nose2", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-1.5F, -6.0F, 0.0F, 3, 6, 3),
			PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
