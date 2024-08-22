package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class CrabPotModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(-12, 0)
			.addBox(-5.0F, -0.5F, -6.0F, 10, 0, 12),
			PartPose.ZERO);

		var frameFront = base.addOrReplaceChild("base_frame_front", CubeListBuilder.create()
				.texOffs(21, 0)
				.addBox(-7.0F, -2.0F, -2.0F, 14, 2, 2),
			PartPose.offset(0.0F, 0.0F, -6.0F));

		var archFrontLeft = frameFront.addOrReplaceChild("arch_front_left_1", CubeListBuilder.create()
				.texOffs(21, 10)
				.addBox(0.0F, -7.0F, 0.0F, 2, 7, 2),
			PartPose.offset(5.0F, -2.0F, -2.0F));

		archFrontLeft.addOrReplaceChild("wicker_front_left_1", CubeListBuilder.create()
			.texOffs(0, 27)
			.addBox(0.0F, -7.0F, 0.0F, 6, 8, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archFrontLeft.addOrReplaceChild("wicker_ext_front_left_1", CubeListBuilder.create()
				.texOffs(13, 27)
				.addBox(0.0F, -7.0F, 0.0F, 1, 8, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archFrontLeft2 = archFrontLeft.addOrReplaceChild("arch_front_left_2", CubeListBuilder.create()
				.texOffs(21, 20)
				.addBox(0.0F, -4.0F, 0.0F, 2, 4, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontLeft2.addOrReplaceChild("wicker_front_left_2", CubeListBuilder.create()
				.texOffs(0, 22)
				.addBox(0.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archFrontLeft2.addOrReplaceChild("wicker_ext_front_left_2", CubeListBuilder.create()
				.texOffs(13, 22)
				.addBox(0.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archFrontLeft3 = archFrontLeft2.addOrReplaceChild("arch_front_left_3", CubeListBuilder.create()
				.texOffs(21, 27)
				.addBox(0.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontLeft3.addOrReplaceChild("wicker_front_left_3", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archFrontLeft3.addOrReplaceChild("wicker_ext_front_left_3", CubeListBuilder.create()
				.texOffs(13, 17)
				.addBox(0.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archFrontLeft4 = archFrontLeft3.addOrReplaceChild("arch_front_left_4", CubeListBuilder.create()
				.texOffs(21, 34)
				.addBox(0.0F, -3.0F, 0.0F, 2, 3, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontLeft4.addOrReplaceChild("wicker_front_left_4", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(0.0F, -3.0F, 0.0F, 6, 3, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archFrontLeft4.addOrReplaceChild("wicker_ext_front_left_4", CubeListBuilder.create()
				.texOffs(13, 13)
				.addBox(0.0F, -3.0F, 0.0F, 1, 3, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archFrontRight = frameFront.addOrReplaceChild("arch_front_right_1", CubeListBuilder.create()
				.texOffs(30, 10)
				.addBox(-2.0F, -7.0F, 0.0F, 2, 7, 2),
			PartPose.offset(-5.0F, -2.0F, -2.0F));

		archFrontRight.addOrReplaceChild("wicker_front_right_1", CubeListBuilder.create().mirror()
				.texOffs(0, 46)
				.addBox(-6.0F, -7.0F, 0.0F, 6, 8, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archFrontRight.addOrReplaceChild("wicker_ext_front_right_1", CubeListBuilder.create()
				.texOffs(13, 46)
				.addBox(-1.0F, -7.0F, 0.0F, 1, 8, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archFrontRight2 = archFrontRight.addOrReplaceChild("arch_front_right_2", CubeListBuilder.create()
				.texOffs(30, 20)
				.addBox(-2.0F, -4.0F, 0.0F, 2, 4, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontRight2.addOrReplaceChild("wicker_front_right_2", CubeListBuilder.create().mirror()
				.texOffs(0, 41)
				.addBox(-6.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archFrontRight2.addOrReplaceChild("wicker_ext_front_right_2", CubeListBuilder.create()
				.texOffs(13, 41)
				.addBox(-1.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archFrontRight3 = archFrontRight2.addOrReplaceChild("arch_front_right_3", CubeListBuilder.create()
				.texOffs(30, 27)
				.addBox(-2.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontRight3.addOrReplaceChild("wicker_front_right_3", CubeListBuilder.create().mirror()
				.texOffs(0, 36)
				.addBox(-6.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archFrontRight3.addOrReplaceChild("wicker_ext_front_right_3", CubeListBuilder.create()
				.texOffs(13, 36)
				.addBox(-1.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archFrontRight4 = archFrontRight3.addOrReplaceChild("arch_front_right_4", CubeListBuilder.create()
				.texOffs(30, 34)
				.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.4553564018453205F, 0.0F, 0.0F));

		archFrontRight4.addOrReplaceChild("wicker_front_right_4", CubeListBuilder.create()
				.texOffs(16, 44)
				.addBox(-6.0F, -3.0F, 0.0F, 6, 3, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archFrontRight4.addOrReplaceChild("wicker_ext_front_right_4", CubeListBuilder.create()
				.texOffs(16, 17)
				.addBox(-1.0F, -3.0F, 0.0F, 1, 3, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var frameLeft = base.addOrReplaceChild("base_frame_left", CubeListBuilder.create()
				.texOffs(18, 50)
				.addBox(0.0F, -2.0F, -6.0F, 2, 2, 12),
			PartPose.offset(5.0F, 0.0F, 0.0F));

		frameLeft.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(35, 28)
				.addBox(0.0F, -10.0F, -6.0F, 0.001F, 10, 12)
				.texOffs(0, 50)
				.addBox(0.0F, -12.0F, -4.0F, 0.001F, 2, 8),
			PartPose.offset(1.0F, -2.0F, 0.0F));

		frameLeft.addOrReplaceChild("top_rope_left", CubeListBuilder.create()
				.texOffs(18, 57)
				.addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offsetAndRotation(1.0F, -14.5F, 0.0F, 00.091106186954104F, 0.045553093477052F, -0.045553093477052F));

		var frameBack = base.addOrReplaceChild("base_frame_back", CubeListBuilder.create()
				.texOffs(21, 5)
				.addBox(-7.0F, -2.0F, 0.0F, 14, 2, 2),
			PartPose.offset(0.0F, 0.0F, 6.0F));

		var archBackLeft = frameBack.addOrReplaceChild("arch_back_left_1", CubeListBuilder.create()
				.texOffs(39, 10)
				.addBox(0.0F, -7.0F, -2.0F, 2, 7, 2, new CubeDeformation(0.001F)),
			PartPose.offset(5.0F, -2.0F, 2.0F));

		archBackLeft.addOrReplaceChild("wicker_back_left_1", CubeListBuilder.create()
				.texOffs(0, 46)
				.addBox(0.0F, -7.0F, 0.0F, 6, 8, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archBackLeft.addOrReplaceChild("wicker_ext_back_left_1", CubeListBuilder.create()
				.texOffs(13, 46)
				.addBox(0.0F, -7.0F, 0.0F, 1, 8, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archBackLeft2 = archBackLeft.addOrReplaceChild("arch_back_left_2", CubeListBuilder.create()
				.texOffs(39, 20)
				.addBox(0.0F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackLeft2.addOrReplaceChild("wicker_back_left_2", CubeListBuilder.create()
				.texOffs(0, 41)
				.addBox(0.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archBackLeft2.addOrReplaceChild("wicker_ext_back_left_2", CubeListBuilder.create()
				.texOffs(13, 41)
				.addBox(0.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archBackLeft3 = archBackLeft2.addOrReplaceChild("arch_back_left_3", CubeListBuilder.create()
				.texOffs(39, 27)
				.addBox(0.0F, -4.0F, -2.0F, 2, 4, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackLeft3.addOrReplaceChild("wicker_back_left_3", CubeListBuilder.create()
				.texOffs(0, 36)
				.addBox(0.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archBackLeft3.addOrReplaceChild("wicker_ext_back_left_3", CubeListBuilder.create()
				.texOffs(13, 36)
				.addBox(0.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archBackLeft4 = archBackLeft3.addOrReplaceChild("arch_back_left_4", CubeListBuilder.create()
				.texOffs(39, 34)
				.addBox(0.0F, -3.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackLeft4.addOrReplaceChild("wicker_back_left_4", CubeListBuilder.create()
				.texOffs(16, 48)
				.addBox(0.0F, -3.0F, 0.0F, 6, 3, 0.001F),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.46F, 0.0F, 0.091106186954104F, 0.0F));

		archBackLeft4.addOrReplaceChild("wicker_ext_back_left_4", CubeListBuilder.create()
				.texOffs(16, 13)
				.addBox(0.0F, -3.0F, 0.0F, 1, 3, 0.001F),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var archBackRight = frameBack.addOrReplaceChild("arch_back_right_1", CubeListBuilder.create()
				.texOffs(48, 10)
				.addBox(-2.0F, -7.0F, -2.0F, 2, 7, 2, new CubeDeformation(0.001F)),
			PartPose.offset(-5.0F, -2.0F, 2.0F));

		archBackRight.addOrReplaceChild("wicker_back_right_1", CubeListBuilder.create().mirror()
				.texOffs(0, 27)
				.addBox(-6.0F, -7.0F, 0.0F, 6, 8, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archBackRight.addOrReplaceChild("wicker_ext_back_right_1", CubeListBuilder.create()
				.texOffs(13, 27)
				.addBox(-1.0F, -7.0F, 0.0F, 1, 8, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archBackRight2 = archBackRight.addOrReplaceChild("arch_back_right_2", CubeListBuilder.create()
				.texOffs(48, 20)
				.addBox(-2.0F, -4.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackRight2.addOrReplaceChild("wicker_back_right_2", CubeListBuilder.create().mirror()
				.texOffs(0, 22)
				.addBox(-6.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archBackRight2.addOrReplaceChild("wicker_ext_back_right_2", CubeListBuilder.create()
				.texOffs(13, 22)
				.addBox(-1.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archBackRight3 = archBackRight2.addOrReplaceChild("arch_back_right_3", CubeListBuilder.create()
				.texOffs(48, 27)
				.addBox(-2.0F, -4.0F, -2.0F, 2, 4, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackRight3.addOrReplaceChild("wicker_back_right_3", CubeListBuilder.create().mirror()
				.texOffs(0, 17)
				.addBox(-6.0F, -4.0F, 0.0F, 6, 4, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archBackRight3.addOrReplaceChild("wicker_ext_back_right_3", CubeListBuilder.create()
				.texOffs(13, 17)
				.addBox(-1.0F, -4.0F, 0.0F, 1, 4, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var archBackRight4 = archBackRight3.addOrReplaceChild("arch_back_right_4", CubeListBuilder.create()
				.texOffs(48, 34)
				.addBox(-2.0F, -3.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.4553564018453205F, 0.0F, 0.0F));

		archBackRight4.addOrReplaceChild("wicker_back_right_4", CubeListBuilder.create()
				.texOffs(16, 40)
				.addBox(-6.0F, -3.0F, 0.0F, 6, 3, 0.001F),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.46F, 0.0F, -0.091106186954104F, 0.0F));

		archBackRight4.addOrReplaceChild("wicker_ext_back_right_4", CubeListBuilder.create()
				.texOffs(13, 13)
				.addBox(-1.0F, -3.0F, 0.0F, 1, 3, 0.001F),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var frameRight = base.addOrReplaceChild("base_frame_right", CubeListBuilder.create()
				.texOffs(18, 50)
				.addBox(-2.0F, -2.0F, -6.0F, 2, 2, 12),
			PartPose.offset(-5.0F, 0.0F, 0.0F));

		frameRight.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(35, 39)
				.addBox(0.0F, -10.0F, -6.0F, 0.001F, 10, 12)
				.texOffs(0, 47)
				.addBox(0.0F, -12.0F, -4.0F, 0.001F, 2, 8),
			PartPose.offset(-1.0F, -2.0F, 0.0F));

		frameRight.addOrReplaceChild("top_rope_right", CubeListBuilder.create()
				.texOffs(18, 52)
				.addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offsetAndRotation(-1.0F, -14.5F, 0.0F, 0.091106186954104F, -0.045553093477052F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
