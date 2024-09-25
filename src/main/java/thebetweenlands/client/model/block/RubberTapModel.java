package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RubberTapModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12),
			PartPose.offsetAndRotation(0.0F, 1.0F, 6.0F, 0.091106186954104F, 0.0F, 0.0F));

		base.addOrReplaceChild("front_side", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-4.0F, -12.0F, -4.0F, 8, 12, 2),
			PartPose.offset(0.0F, -2.0F, -2.0F));

		base.addOrReplaceChild("back_side", CubeListBuilder.create()
				.texOffs(21, 40)
				.addBox(-4.0F, -12.0F, 0.0F, 8, 12, 2),
			PartPose.offset(0.0F, -2.0F, 4.0F));

		var left = base.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(29, 15)
				.addBox(0.0F, -12.0F, -6.0F, 2, 12, 12),
			PartPose.offset(4.0F, -2.0F, 0.0F));

		var bottomRope = left.addOrReplaceChild("bottom_rope_1", CubeListBuilder.create()
				.texOffs(60, 32)
				.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 9),
			PartPose.offsetAndRotation(2.0F, -1.0F, 6.0F, 0.0F, -0.136659280431156F, 0.0F));

		bottomRope.addOrReplaceChild("bottom_rope_2", CubeListBuilder.create()
				.texOffs(53, 3)
				.addBox(-9.3F, -1.0F, -0.8F, 9, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 9.0F, 0.0F, 0.136659280431156F, 0.0F));

		var leftHandle = left.addOrReplaceChild("left_handle", CubeListBuilder.create()
				.texOffs(29, 15)
				.addBox(-1.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offset(1.0F, -12.0F, 0.0F));

		var topRope = leftHandle.addOrReplaceChild("top_rope_1", CubeListBuilder.create()
				.texOffs(60, 0)
				.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 14),
			PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, 0.0F, -0.091106186954104F, 0.0F));

		topRope.addOrReplaceChild("top_rope_2", CubeListBuilder.create()
			.texOffs(53, 0)
			.addBox(-9.2F, -0.99F, -0.8F, 9, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 14.0F, 0.0F, 0.091106186954104F, 0.0F));

		var right = base.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-2.0F, -12.0F, -6.0F, 2, 12, 12),
			PartPose.offset(-4.0F, -2.0F, 0.0F));

		right.addOrReplaceChild("bottom_rope_3", CubeListBuilder.create()
				.texOffs(60, 43)
				.addBox(0.0F, -1.0F, 0.0F, 1, 1, 9),
			PartPose.offsetAndRotation(-2.0F, -1.0F, 6.0F, 0.0F, 0.136659280431156F, 0.0F));

		var rightHandle = right.addOrReplaceChild("right_handle", CubeListBuilder.create()
				.texOffs(16, 15)
				.addBox(-1.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offset(-1.0F, -12.0F, 0.0F));

		rightHandle.addOrReplaceChild("top_rope_3", CubeListBuilder.create()
			.texOffs(60, 16)
			.addBox(0.0F, -1.0F, 0.0F, 1, 1, 14),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, 0.0F, 0.091106186954104F, 0.0F));

		var tap = partDefinition.addOrReplaceChild("bottom_tap", CubeListBuilder.create()
				.texOffs(0, 55)
				.addBox(-1.5F, 0.0F, -5.0F, 3, 1, 5),
			PartPose.offsetAndRotation(0.0F, -16.0F, 12.0F, 0.091106186954104F, 0.0F, 0.0F));

		tap.addOrReplaceChild("left_tap", CubeListBuilder.create()
				.texOffs(17, 55)
				.addBox(0.0F, -1.0F, -2.5F, 1, 1, 6),
			PartPose.offsetAndRotation(0.5F, 0.0F, -2.5F, 0.0F, 0.0F, 0.136659280431156F));

		tap.addOrReplaceChild("right_tap", CubeListBuilder.create()
				.texOffs(32, 55)
				.addBox(-1.0F, -1.0F, -2.5F, 1, 1, 6),
			PartPose.offsetAndRotation(-0.5F, 0.0F, -2.5F, 0.0F, 0.0F, -0.091106186954104F));

		return LayerDefinition.create(definition, 128, 64);
	}

	public static LayerDefinition theRubberMustFlow() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var flow = partDefinition.addOrReplaceChild("flow_1", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-0.5F, -1.0F, -6.0F, 1, 1, 6),
			PartPose.offsetAndRotation(0.0F, -16.1F, 12.0F, 0.091106186954104F, 0.0F, 0.0F));

		flow.addOrReplaceChild("flow_2", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-0.51F, 0.0F, 0.0F, 1, 16, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, -6.0F, -0.091106186954104F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}
}
