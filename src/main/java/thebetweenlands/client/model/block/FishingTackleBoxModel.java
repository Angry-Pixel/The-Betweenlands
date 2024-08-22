package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FishingTackleBoxModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.addBox(-7.0F, -2.0F, -5.0F, 14, 2, 10),
			PartPose.ZERO);

		var frontLeftSupport = base.addOrReplaceChild("front_left_support_1", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-2.01F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(7.0F, -2.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));

		var topSupport = frontLeftSupport.addOrReplaceChild("front_left_support_2", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-2.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var frontRightSupport = base.addOrReplaceChild("front_right_support_1", CubeListBuilder.create()
				.texOffs(9, 13)
				.addBox(0.01F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-7.0F, -2.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));

		frontRightSupport.addOrReplaceChild("front_right_support_2", CubeListBuilder.create()
				.texOffs(9, 20)
				.addBox(0.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var backLeftSupport = base.addOrReplaceChild("back_left_support_1", CubeListBuilder.create()
				.texOffs(18, 13)
				.addBox(-2.01F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(7.0F, -2.0F, 3.0F, -0.091106186954104F, 0.0F, 0.0F));

		backLeftSupport.addOrReplaceChild("back_left_support_2", CubeListBuilder.create()
				.texOffs(18, 20)
				.addBox(-2.0F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var backRightSupport = base.addOrReplaceChild("back_right_support_1", CubeListBuilder.create()
				.texOffs(27, 13)
				.addBox(0.01F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-7.0F, -2.0F, 3.0F, -0.091106186954104F, 0.0F, 0.0F));

		backRightSupport.addOrReplaceChild("front_right_support_2", CubeListBuilder.create()
				.texOffs(9, 20)
				.addBox(0.0F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var front = base.addOrReplaceChild("front_1", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(-5.0F, -4.0F, 0.0F, 10, 4, 0.01F),
			PartPose.offsetAndRotation(0.0F, -2.0F, -4.5F, 0.091106186954104F, 0.0F, 0.0F));

		front.addOrReplaceChild("front_2", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-5.0F, -4.0F, 0.0F, 10, 4, 0.01F),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var back = base.addOrReplaceChild("back_1", CubeListBuilder.create()
				.texOffs(21, 27)
				.addBox(-5.0F, -4.0F, 0.0F, 10, 4, 0.01F),
			PartPose.offsetAndRotation(0.0F, -2.0F, 4.5F, -0.091106186954104F, 0.0F, 0.0F));

		back.addOrReplaceChild("back_2", CubeListBuilder.create()
				.texOffs(21, 32)
				.addBox(-5.0F, -4.0F, 0.0F, 10, 4, 0.01F),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var left = base.addOrReplaceChild("left_1", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(0.0F, -4.0F, -4.0F, 0.01F, 4, 8),
			PartPose.offsetAndRotation(6.5F, -2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		left.addOrReplaceChild("left_2", CubeListBuilder.create()
				.texOffs(0, 34)
				.addBox(0.0F, -4.0F, -4.0F, 0.01F, 4, 8),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var right = base.addOrReplaceChild("right_1", CubeListBuilder.create()
				.texOffs(17, 29)
				.addBox(0.0F, -4.0F, -4.0F, 0.01F, 4, 8),
			PartPose.offsetAndRotation(-6.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		right.addOrReplaceChild("right_2", CubeListBuilder.create()
				.texOffs(17, 34)
				.addBox(0.0F, -4.0F, -4.0F, 0.01F, 4, 8),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		var topLeft = topSupport.addOrReplaceChild("top_left", CubeListBuilder.create()
			.texOffs(40, 3)
			.addBox(-2.0F, -1.0F, -2.0F, 2, 1, 10, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.091106186954104F, 0.0F, 0.0F));

		topLeft.addOrReplaceChild("left_handle", CubeListBuilder.create()
				.texOffs(40, 37)
				.addBox(0.0F, 0.0F, -3.0F, 0.01F, 3, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.22759093446006054F));

		var topFront = topLeft.addOrReplaceChild("top_front", CubeListBuilder.create()
				.texOffs(40, 15)
				.addBox(-10.0F, -1.0F, 0.0F, 10, 1, 2, new CubeDeformation(-0.001F)),
			PartPose.offset(-2.0F, 0.0F, -2.0F));

		var topRight = topFront.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(40, 27)
				.addBox(-2.0F, -1.0F, 0.0F, 2, 1, 10, new CubeDeformation(-0.001F)),
			PartPose.offset(-10.0F, 0.0F, 0.0F));

		topRight.addOrReplaceChild("right_handle", CubeListBuilder.create()
				.texOffs(40, 33)
				.addBox(0.0F, 0.0F, -3.0F, 0.01F, 3, 6),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 5.0F, 0.0F, 0.0F, 0.22759093446006054F));

		topRight.addOrReplaceChild("top_back", CubeListBuilder.create()
				.texOffs(40, 19)
				.addBox(0.0F, -1.0F, 0.0F, 10, 1, 2, new CubeDeformation(-0.001F)),
			PartPose.offset(0.0F, 0.0F, 8.0F));

		var lid = partDefinition.addOrReplaceChild("lid", CubeListBuilder.create()
			.texOffs(0, 47)
			.addBox(-7.0F, -1.0F, -9.0F, 14, 1, 10, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, -10.5F, 4.0F));

		var strap = lid.addOrReplaceChild("strap_1", CubeListBuilder.create()
				.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0.01F),
			PartPose.offsetAndRotation(0.0F, -1.0F, -9.0F, -0.045553093477052F, 0.0F, 0.0F));

		strap.addOrReplaceChild("strap_2", CubeListBuilder.create()
				.texOffs(0, 3)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 0.01F),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.136659280431156F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}