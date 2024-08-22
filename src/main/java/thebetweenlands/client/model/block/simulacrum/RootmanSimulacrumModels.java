package thebetweenlands.client.model.block.simulacrum;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RootmanSimulacrumModels {

	public static LayerDefinition makeSimulacrum1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.5F, -4.0F, 0.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.091106186954104F, 0.0F, 0.0F));

		var mid = base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-2.5F, -4.0F, -4.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 4.0F, 0.091106186954104F, 0.0F, 0.0F));

		base.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(37, 12)
				.addBox(0.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, 0.136659280431156F));

		base.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(37, 20)
				.addBox(-4.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, -0.136659280431156F));

		var arms = mid.addOrReplaceChild("arms_1", CubeListBuilder.create()
				.texOffs(32, 0)
				.addBox(-3.5F, 0.0F, -1.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, -1.5F, -0.40980330836826856F, 0.0F, 0.0F));

		arms.addOrReplaceChild("arms_2", CubeListBuilder.create()
				.texOffs(32, 6)
				.addBox(-3.5F, 0.0F, -2.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, -0.6373942428283291F, 0.0F, 0.0F));

		var head = mid.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-2.0F, -4.0F, -4.0F, 4, 4, 4),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var maskMain = head.addOrReplaceChild("mask_main", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-2.0F, -6.0F, -2.0F, 4, 12, 2),
			PartPose.offset(0.0F, -3.0F, -3.0F));

		maskMain.addOrReplaceChild("mask_left", CubeListBuilder.create()
				.texOffs(19, 15)
				.addBox(0.0F, -4.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, -2.0F, 0.0F, -0.22759093446006054F, 0.0F));

		maskMain.addOrReplaceChild("mask_right", CubeListBuilder.create()
				.texOffs(28, 15)
				.addBox(-2.0F, -4.0F, 0.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public static LayerDefinition makeSimulacrum2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.5F, -4.0F, 0.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.091106186954104F, 0.0F, 0.0F));

		var mid = base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-2.5F, -4.0F, -4.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 4.0F, 0.091106186954104F, 0.0F, 0.0F));

		base.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(37, 12)
				.addBox(0.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, 0.136659280431156F));

		base.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(37, 20)
				.addBox(-4.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, -0.136659280431156F));

		var arms = mid.addOrReplaceChild("arms_1", CubeListBuilder.create()
				.texOffs(32, 0)
				.addBox(-3.5F, 0.0F, -1.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, -1.5F, -0.40980330836826856F, 0.0F, 0.0F));

		arms.addOrReplaceChild("arms_2", CubeListBuilder.create()
				.texOffs(32, 6)
				.addBox(-3.5F, 0.0F, -2.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, -0.6373942428283291F, 0.0F, 0.0F));

		var head = mid.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-2.0F, -4.0F, -4.0F, 4, 4, 4),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var maskMain = head.addOrReplaceChild("mask_main", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-2.0F, -4.0F, -2.0F, 4, 8, 2),
			PartPose.offset(0.0F, -2.0F, -3.0F));

		maskMain.addOrReplaceChild("mask_left", CubeListBuilder.create()
				.texOffs(19, 12)
				.addBox(0.0F, -2.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(2.0F, -1.0F, -2.0F, 0.0F, -0.22759093446006054F, 0.0F));

		maskMain.addOrReplaceChild("mask_right", CubeListBuilder.create()
				.texOffs(28, 12)
				.addBox(-2.0F, -2.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(-2.0F, -1.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));

		maskMain.addOrReplaceChild("mask_left_2", CubeListBuilder.create()
				.texOffs(19, 20)
				.addBox(0.0F, 0.0F, -1.99F, 2, 2, 2),
			PartPose.offsetAndRotation(2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));

		maskMain.addOrReplaceChild("mask_right_2", CubeListBuilder.create()
				.texOffs(28, 20)
				.addBox(-2.0F, 0.0F, -1.99F, 2, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public static LayerDefinition makeSimulacrum3() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.5F, -4.0F, 0.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.091106186954104F, 0.0F, 0.0F));

		var mid = base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-2.5F, -4.0F, -4.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 4.0F, 0.091106186954104F, 0.0F, 0.0F));

		base.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(37, 12)
				.addBox(0.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, 0.136659280431156F));

		base.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(37, 20)
				.addBox(-4.0F, -2.0F, -1.5F, 4, 2, 5),
			PartPose.rotation(0.091106186954104F, 0.0F, -0.136659280431156F));

		var arms = mid.addOrReplaceChild("arms_1", CubeListBuilder.create()
				.texOffs(32, 0)
				.addBox(-3.5F, 0.0F, -1.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, -1.5F, -0.40980330836826856F, 0.0F, 0.0F));

		arms.addOrReplaceChild("arms_2", CubeListBuilder.create()
				.texOffs(32, 6)
				.addBox(-3.5F, 0.0F, -2.0F, 7, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, -0.6373942428283291F, 0.0F, 0.0F));

		var head = mid.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-2.0F, -4.0F, -4.0F, 4, 4, 4),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var maskMain = head.addOrReplaceChild("mask_main", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-2.0F, -4.0F, -2.0F, 4, 7, 2),
			PartPose.offset(0.0F, -1.0F, -3.0F));

		var left = maskMain.addOrReplaceChild("mask_left", CubeListBuilder.create()
				.texOffs(19, 12)
				.addBox(0.0F, -2.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(2.0F, -1.0F, -2.0F, 0.0F, -0.22759093446006054F, 0.0F));

		var right = maskMain.addOrReplaceChild("mask_right", CubeListBuilder.create()
				.texOffs(28, 12)
				.addBox(-2.0F, -2.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(-2.0F, -1.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));

		left.addOrReplaceChild("mask_left_2", CubeListBuilder.create()
				.texOffs(19, 20)
				.addBox(0.0F, -3.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(2.0F, 1.0F, 0.01F, 0.0F, 0.0F, -0.22759093446006054F));

		right.addOrReplaceChild("mask_right_2", CubeListBuilder.create()
				.texOffs(28, 20)
				.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-2.0F, 1.0F, 0.01F, 0.0F, 0.0F, 0.22759093446006054F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
