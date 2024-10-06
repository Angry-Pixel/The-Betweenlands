package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Stalker;

public class StalkerModel extends MowzieModelBase<Stalker> {

	private final ModelPart root;
	private final ModelPart bodyBase;
	private final ModelPart bodyMain;
	private final ModelPart abdomen;
	private final ModelPart arm_lefta;
	private final ModelPart arm_leftb;
	private final ModelPart arm_righta;
	private final ModelPart arm_rightb;
	private final ModelPart neck1a;
	private final ModelPart head_main;
	private final ModelPart jaw_lower_left1a;
	private final ModelPart jaw_lower_right1a;
	private final ModelPart jaw_lower_main;
	private final ModelPart bigeye;
	private final ModelPart bigEyelidTop;
	private final ModelPart bigEyelidBottom;
	private final ModelPart medEyelidTopRight;
	private final ModelPart medEyelidTopLeft;
	private final ModelPart medEyelidBottomRight;
	private final ModelPart medEyelidBottomLeft;
	private final ModelPart smallEyelidRight;
	private final ModelPart smallEyelidLeft;
	private final ModelPart midarm_righta;
	private final ModelPart midarm_rightb;
	private final ModelPart midarm_lefta;
	private final ModelPart midarm_leftb;
	private final ModelPart leg_right1a;
	private final ModelPart leg_right1b;
	private final ModelPart leg_left1a;
	private final ModelPart leg_left1b;

	public StalkerModel(ModelPart root) {
		this.root = root;
		this.bodyBase = root.getChild("body_base");
		this.bodyMain = this.bodyBase.getChild("body_main");
		this.abdomen = this.bodyMain.getChild("abdomen");
		this.arm_lefta = this.abdomen.getChild("chest_main").getChild("right_chest").getChild("left_arm_1");
		this.arm_leftb = this.arm_lefta.getChild("left_arm_2");
		this.arm_righta = this.abdomen.getChild("chest_main").getChild("left_chest").getChild("right_arm_1");
		this.arm_rightb = this.arm_righta.getChild("right_arm_2");
		this.neck1a = this.abdomen.getChild("chest_main").getChild("neck_1");
		this.head_main = this.neck1a.getChild("neck_2").getChild("head");
		this.jaw_lower_left1a = this.head_main.getChild("head_connector").getChild("lower_left_jaw_1");
		this.jaw_lower_right1a = this.head_main.getChild("head_connector").getChild("lower_right_jaw_1");
		this.jaw_lower_main = this.head_main.getChild("head_connector").getChild("lower_jaw");
		this.bigeye = this.head_main.getChild("big_eye");
		this.bigEyelidTop = this.bigeye.getChild("big_eyelid_top");
		this.bigEyelidBottom = this.bigeye.getChild("big_eyelid_bottom");
		this.medEyelidTopRight = this.head_main.getChild("medium_top_right_eyelid");
		this.medEyelidTopLeft = this.head_main.getChild("medium_top_left_eyelid");
		this.medEyelidBottomRight = this.head_main.getChild("medium_bottom_right_eyelid");
		this.medEyelidBottomLeft = this.head_main.getChild("medium_bottom_left_eyelid");
		this.smallEyelidRight = this.head_main.getChild("small_right_eyelid");
		this.smallEyelidLeft = this.head_main.getChild("small_left_eyelid");
		this.midarm_righta = this.abdomen.getChild("mid_right_arm_1");
		this.midarm_rightb = this.midarm_righta.getChild("mid_right_arm_2");
		this.midarm_lefta = this.abdomen.getChild("mid_left_arm_1");
		this.midarm_leftb = this.midarm_lefta.getChild("mid_left_arm_2");
		this.leg_right1a = this.bodyBase.getChild("right_leg_1");
		this.leg_right1b = this.leg_right1a.getChild("right_leg_2");
		this.leg_left1a = this.bodyBase.getChild("left_leg_1");
		this.leg_left1b = this.leg_left1a.getChild("left_leg_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var bodyBase = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4.5F, -2.5686F, -3.4697F, 9, 5, 6),
			PartPose.offsetAndRotation(0.0F, -9.0F, 6.0F, 1.3659F, 0.0F, 0.0F));

		var bodyMain = bodyBase.addOrReplaceChild("body_main", CubeListBuilder.create()
				.texOffs(0, 12)
				.addBox(-4.0F, -7.818F, -4.0083F, 8, 8, 6),
			PartPose.offsetAndRotation(0.0F, -2.5686F, 0.5303F, 0.0911F, 0.0F, 0.0F));

		var abdomen = bodyMain.addOrReplaceChild("abdomen", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(-4.5F, -3.4551F, -3.9627F, 9, 4, 6),
			PartPose.offsetAndRotation(0.0F, -7.818F, -0.0083F, -0.1367F, 0.0F, 0.0F));

		var chest = abdomen.addOrReplaceChild("chest_main", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0.0F, -3.4551F, -4.9627F, -0.3187F, 0.0F, 0.0F));

		var chestRight = chest.addOrReplaceChild("right_chest", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox(-2.7988F, -2.0F, -5.6716F, 6, 6, 7),
			PartPose.offsetAndRotation(2.0F, -3.0F, 6.0F, 0.0F, -0.1367F, 0.0F));

		var leftArm1 = chestRight.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-1.0F, 0.0F, -1.0F, 2, 10, 2),
			PartPose.offsetAndRotation(3.2012F, -1.0F, -0.6716F, -0.5463F, 0.0F, -0.6374F));

		var leftArm2 = leftArm1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(40, 13)
				.addBox(-1.01F, 0.0F, -2.0F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, 1.0F, -1.2748F, 0.0F, 0.0F));

		var leftElbowHair = leftArm2.addOrReplaceChild("left_elbow_hair_1", CubeListBuilder.create()
				.texOffs(105, 0)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 1),
			PartPose.rotation(-0.8652F, 0.0F, 0.0F));

		leftElbowHair.addOrReplaceChild("left_elbow_hair_2", CubeListBuilder.create()
				.texOffs(105, 2)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.3187F, 0.0F, 0.0F));

		var leftShoulderHair = leftArm1.addOrReplaceChild("left_shoulder_hair_1", CubeListBuilder.create()
				.texOffs(95, 0)
				.addBox(0.0F, 0.0F, -1.0F, 0, 2, 2),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		leftShoulderHair.addOrReplaceChild("left_shoulder_hair_2", CubeListBuilder.create()
				.texOffs(95, 3)
				.addBox(0.0F, 0.0F, -1.0F, 0, 2, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		var rightChestHair = chestRight.addOrReplaceChild("right_chest_hair_1", CubeListBuilder.create()
				.texOffs(0, 52)
				.addBox(0.0F, 0.0F, 0.0F, 6, 2, 0),
			PartPose.offsetAndRotation(-2.7988F, -1.0F, -5.6716F, -0.3643F, 0.0F, 0.0F));

		rightChestHair.addOrReplaceChild("right_chest_hair_2", CubeListBuilder.create()
				.texOffs(0, 55)
				.addBox(0.0F, 0.0F, 0.0F, 6, 3, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.3643F, 0.0F, 0.0F));


		var chestLeft = chest.addOrReplaceChild("left_chest", CubeListBuilder.create()
				.texOffs(27, 38)
				.addBox(-3.2012F, -1.99F, -5.6716F, 6, 6, 7),
			PartPose.offsetAndRotation(-2.0F, -3.0F, 6.0F, 0.0F, 0.1367F, 0.0F));

		var rightArm1 = chestLeft.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(49, 0)
				.addBox(-1.0F, 0.0F, -1.0F, 2, 10, 2),
			PartPose.offsetAndRotation(-3.2012F, -1.0F, -0.6716F, -0.5463F, 0.0F, 0.6374F));

		var rightArm2 = rightArm1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(49, 13)
				.addBox(-0.99F, 0.0F, -2.0F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, 1.0F, -1.2748F, 0.0F, 0.0F));

		var rightElbowHair = rightArm2.addOrReplaceChild("right_elbow_hair_1", CubeListBuilder.create()
				.texOffs(110, 0)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 1),
			PartPose.rotation(-0.8652F, 0.0F, 0.0F));

		rightElbowHair.addOrReplaceChild("right_elbow_hair_2", CubeListBuilder.create()
				.texOffs(110, 2)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.3187F, 0.0F, 0.0F));

		var rightShoulderHair = rightArm1.addOrReplaceChild("right_shoulder_hair_1", CubeListBuilder.create()
				.texOffs(100, 0)
				.addBox(0.0F, 0.0F, -1.0F, 0, 2, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		rightShoulderHair.addOrReplaceChild("right_shoulder_hair_2", CubeListBuilder.create()
				.texOffs(100, 3)
				.addBox(0.0F, 0.0F, -1.0F, 0, 2, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		var leftChestHair = chestLeft.addOrReplaceChild("left_chest_hair_1", CubeListBuilder.create()
				.texOffs(27, 52)
				.addBox(0.0F, 0.0F, 0.0F, 6, 2, 0),
			PartPose.offsetAndRotation(2.7988F, -1.0F, -5.6716F, -0.3643F, 0.0F, 0.0F));

		leftChestHair.addOrReplaceChild("left_chest_hair_2", CubeListBuilder.create()
				.texOffs(27, 55)
				.addBox(0.0F, 0.0F, 0.0F, 6, 3, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.3643F, 0.0F, 0.0F));


		var neck1 = chest.addOrReplaceChild("neck_1", CubeListBuilder.create()
				.texOffs(0, 59)
				.addBox(-2.0F, -3.1446F, -2.5932F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 6.0F, 0.6829F, 0.0F, 0.0F));

		var neck2 = neck1.addOrReplaceChild("neck_2", CubeListBuilder.create()
				.texOffs(17, 59)
				.addBox(-2.0F, -2.6379F, -1.967F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -3.1446F, -0.5932F, -0.182F, 0.0F, 0.0F));

		var head = neck2.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 67)
				.addBox(-4.0F, -4.9977F, -6.9318F, 8, 6, 8),
			PartPose.offsetAndRotation(0.0F, -2.6379F, 0.533F, -1.5026F, 0.0F, 0.0F));

		var headConnection = head.addOrReplaceChild("head_connector", CubeListBuilder.create()
				.texOffs(0, 82)
				.addBox(-4.0F, 0.0F, -2.0F, 8, 2, 2),
			PartPose.offset(0.0F, 1.0023F, 1.0682F));

		var lowerLeftJaw1 = headConnection.addOrReplaceChild("lower_left_jaw_1", CubeListBuilder.create().mirror()
				.texOffs(0, 87)
				.addBox(-3.0F, 0.0F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(-1.5F, 0.0F, -2.0F, 0.3643F, 0.2276F, -0.0911F));

		var lowerLeftJaw2 = lowerLeftJaw1.addOrReplaceChild("lower_left_jaw_2", CubeListBuilder.create().mirror()
				.texOffs(17, 87)
				.addBox(-3.0F, -2.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, -5.0F, -0.3187F, 0.0F, 0.0F));

		lowerLeftJaw2.addOrReplaceChild("lower_left_teeth", CubeListBuilder.create()
				.texOffs(30, 87)
				.addBox(-3.0F, -1.0F, 0.0F, 3, 1, 3),
			PartPose.offset(0.0F, -2.0F, -3.0F));

		lowerLeftJaw1.addOrReplaceChild("left_mutton_chops", CubeListBuilder.create()
				.texOffs(44, 72)
				.addBox(0.0F, 0.0F, -5.0F, 0, 2, 5),
			PartPose.offsetAndRotation(-3.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.2276F));

		var lowerRightJaw1 = headConnection.addOrReplaceChild("lower_right_jaw_1", CubeListBuilder.create().mirror()
				.texOffs(0, 95)
				.addBox(0.0F, 0.0F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(1.5F, 0.0F, -2.0F, 0.3643F, -0.2276F, 0.0911F));

		var lowerRightJaw2 = lowerRightJaw1.addOrReplaceChild("lower_right_jaw_2", CubeListBuilder.create().mirror()
				.texOffs(17, 95)
				.addBox(0.0F, -2.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, -5.0F, -0.3187F, 0.0F, 0.0F));

		lowerRightJaw2.addOrReplaceChild("lower_right_teeth", CubeListBuilder.create()
				.texOffs(30, 95)
				.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3),
			PartPose.offset(0.0F, -2.0F, -3.0F));

		lowerRightJaw1.addOrReplaceChild("right_mutton_chops", CubeListBuilder.create()
				.texOffs(33, 72)
				.addBox(0.0F, 0.0F, -5.0F, 0, 2, 5),
			PartPose.offsetAndRotation(3.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.2276F));

		var lowerJaw = headConnection.addOrReplaceChild("lower_jaw", CubeListBuilder.create()
				.texOffs(0, 103)
				.addBox(-2.0F, 0.0F, -6.0F, 4, 2, 6),
			PartPose.offsetAndRotation(0.0F, 0.5F, -2.0F, 0.5463F, 0.0F, 0.0F));

		lowerJaw.addOrReplaceChild("lower_teeth", CubeListBuilder.create()
				.texOffs(21, 103)
				.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 4),
			PartPose.offset(0.0F, 0.0F, -6.0F));

		var leftEar = head.addOrReplaceChild("left_ear_1", CubeListBuilder.create()
				.texOffs(33, 64)
				.addBox(0.0F, -1.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(-4.0F, -3.9977F, -0.9318F, 0.0F, -0.4098F, 0.0F));

		leftEar.addOrReplaceChild("left_ear_2", CubeListBuilder.create()
				.texOffs(40, 63)
				.addBox(0.0F, 0.0F, 0.0F, 0, 4, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.0F, 0.2276F, 0.0F));

		var rightEar = head.addOrReplaceChild("right_ear_1", CubeListBuilder.create()
				.texOffs(33, 69)
				.addBox(0.0F, -1.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(4.0F, -3.9977F, -0.9318F, 0.0F, 0.4098F, 0.0F));

		rightEar.addOrReplaceChild("right_ear_2", CubeListBuilder.create()
				.texOffs(40, 68)
				.addBox(0.0F, 0.0F, 0.0F, 0, 4, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.0F, -0.2276F, 0.0F));

		head.addOrReplaceChild("upper_teeth", CubeListBuilder.create()
				.texOffs(0, 112)
				.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 5),
			PartPose.offset(0.0F, 1.0023F, -6.9318F));

		head.addOrReplaceChild("left_facial_hair", CubeListBuilder.create()
				.texOffs(49, 65)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(-4.0F, -2.4977F, -1.9318F, -0.4098F, -0.4098F, 0.0F));

		head.addOrReplaceChild("right_facial_hair", CubeListBuilder.create()
				.texOffs(49, 70)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(4.0F, -2.4977F, -1.9318F, -0.4098F, 0.4098F, 0.0F));

		var bigEye = head.addOrReplaceChild("big_eye", CubeListBuilder.create()
				.texOffs(54, 67)
				.addBox(-2.0F, -2.1737F, -2.1903F, 4, 4, 4),
			PartPose.offsetAndRotation(0.0F, -2.9977F, -5.9318F, -0.0911F, 0.0F, 0.0F));

		bigEye.addOrReplaceChild("big_eyelid_top", CubeListBuilder.create()
				.texOffs(34, 58)
				.addBox(-2.0F, -2.1737F, -2.1902F, 4, 2, 5, new CubeDeformation(0.02F)),
			PartPose.ZERO);

		bigEye.addOrReplaceChild("big_eyelid_bottom", CubeListBuilder.create()
				.texOffs(58, 58)
				.addBox(-2.0F, -0.1737F, -2.1902F, 4, 2, 5, new CubeDeformation(0.02F)),
			PartPose.ZERO);

		head.addOrReplaceChild("medium_top_right_eyelid", CubeListBuilder.create()
			.texOffs(0, 4)
			.addBox(-4.07F, -4.0825F, -7.0625F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.ZERO);

		head.addOrReplaceChild("medium_top_left_eyelid", CubeListBuilder.create().mirror()
				.texOffs(0, 4)
				.addBox(3.07F, -4.0825F, -7.0625F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.ZERO);

		head.addOrReplaceChild("medium_bottom_right_eyelid", CubeListBuilder.create()
				.texOffs(0, 2)
				.addBox(-3.05F, -4.055F, -6.0325F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.offset(-1.0F, 1.0F, -1.0F));

		head.addOrReplaceChild("medium_bottom_left_eyelid", CubeListBuilder.create().mirror()
				.texOffs(0, 2)
				.addBox(2.07F, -4.055F, -6.0625F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.offset(1.0F, 1.0F, -1.0F));

		head.addOrReplaceChild("small_right_eyelid", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-4.07F, -4.08F, -7.05F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, 0.0F, 2.0F));

		head.addOrReplaceChild("small_left_eyelid", CubeListBuilder.create().mirror()
				.texOffs(0, 11)
				.addBox(3.07F, -4.08F, -7.05F, 1, 1, 1, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, 0.0F, 2.0F));


		var midRightArm = abdomen.addOrReplaceChild("mid_right_arm_1", CubeListBuilder.create()
			.texOffs(58, 0)
			.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2),
			PartPose.offsetAndRotation(4.5F, -1.4551F, -1.9627F, -0.0456F, -0.2731F, -0.4098F));

		var midRightArm2 = midRightArm.addOrReplaceChild("mid_right_arm_2", CubeListBuilder.create()
			.texOffs(58, 11)
			.addBox(-0.99F, 0.0F, -2.0F, 2, 9, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 1.0F, -1.0472F, 0.0F, 0.0F));

		var midRightElbowHair = midRightArm2.addOrReplaceChild("right_elbow_hair_1", CubeListBuilder.create()
			.texOffs(105, 5)
			.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 1),
			PartPose.rotation(-1.1383F, 0.0F, 0.0F));

		midRightElbowHair.addOrReplaceChild("right_elbow_hair_2", CubeListBuilder.create()
			.texOffs(105, 7)
			.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.3187F, 0.0F, 0.0F));

		var midLeftArm = abdomen.addOrReplaceChild("mid_left_arm_1", CubeListBuilder.create()
				.texOffs(67, 0)
				.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2),
			PartPose.offsetAndRotation(-4.5F, -1.4551F, -1.9627F, -0.0456F, 0.2731F, 0.4098F));

		var midLeftArm2 = midLeftArm.addOrReplaceChild("mid_left_arm_2", CubeListBuilder.create()
				.texOffs(67, 11)
				.addBox(-1.01F, 0.0F, -2.0F, 2, 9, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 1.0F, -1.0472F, 0.0F, 0.0F));

		var midLeftElbowHair = midLeftArm2.addOrReplaceChild("left_elbow_hair_1", CubeListBuilder.create()
				.texOffs(110, 5)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 1),
			PartPose.rotation(-1.1383F, 0.0F, 0.0F));

		midLeftElbowHair.addOrReplaceChild("left_elbow_hair_2", CubeListBuilder.create()
				.texOffs(110, 7)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.3187F, 0.0F, 0.0F));

		var rightLeg = bodyBase.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
			.texOffs(76, 0)
			.addBox(-1.0F, -1.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(3.5F, 1.9314F, 0.5303F, -0.6829F, 0.2731F, -1.0472F));

		rightLeg.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(76, 12)
				.addBox(-2.0F, 0.0F, -0.99F, 2, 9, 2),
			PartPose.offsetAndRotation(1.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.8652F));

		var leftLeg = bodyBase.addOrReplaceChild("left_leg_1", CubeListBuilder.create().mirror()
				.texOffs(85, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(-3.5F, 1.9314F, 0.5303F, -0.6829F, -0.2731F, 1.0472F));

		leftLeg.addOrReplaceChild("left_leg_2", CubeListBuilder.create().mirror()
				.texOffs(85, 12)
				.addBox(0.0F, 0.0F, -1.01F, 2, 9, 2),
			PartPose.offsetAndRotation(-1.0F, 8.0F, 0.0F, 0.0F, 0.0F, -0.8652F));

		return LayerDefinition.create(definition, 128, 128);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Stalker entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.neck1a.zRot -= (netHeadYaw / Mth.RAD_TO_DEG) / 2.0F;
		this.head_main.zRot -= (netHeadYaw / Mth.RAD_TO_DEG) / 2.0F;
		this.neck1a.xRot += (headPitch / Mth.RAD_TO_DEG) / 2.0F;
		this.head_main.xRot += (headPitch / Mth.RAD_TO_DEG) / 2.0F;
	}

	@Override
	public void prepareMobModel(Stalker entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;

		this.medEyelidTopRight.x += 0.07F;
		this.medEyelidTopRight.z += 0.13F;
		this.medEyelidTopRight.y += 0.085F;
		this.medEyelidTopLeft.x -= 0.07F;
		this.medEyelidTopLeft.z += 0.13F;
		this.medEyelidTopLeft.y += 0.085F;
		this.medEyelidBottomRight.x += 0.05F;
		this.medEyelidBottomRight.z += 0.1F;
		this.medEyelidBottomLeft.x -= 0.07F;
		this.medEyelidBottomLeft.z += 0.13F;
		this.medEyelidBottomLeft.y += 0.06F;
		this.medEyelidBottomRight.y += 0.06F;
		this.smallEyelidLeft.x -= 0.07F;
		this.smallEyelidLeft.z += 0.12F;
		this.smallEyelidLeft.y += 0.08F;
		this.smallEyelidRight.x += 0.07F;
		this.smallEyelidRight.z += 0.12F;
		this.smallEyelidRight.y += 0.08F;

		float blinkFrame = frame * Mth.TWO_PI;
		float blinkSmallRight = 0.0F;
		float blinkSmallLeft = 0.0F;
		float blinkMedRight = 0.0F;
		float blinkMedLeft = 0.0F;
		float blinkBig = 0.0F;
		if ((int) (frame * 0.04F) % 5 == 0) {
			blinkSmallRight = (float) Math.pow(Math.sin(blinkFrame * 0.02F + 0.5F), 10.0F);
			blinkSmallLeft = (float) Math.pow(Math.sin(blinkFrame * 0.02F + 0.7F), 10.0F);
			blinkMedRight = (float) Math.pow(Math.sin(blinkFrame * 0.02F - 0.2F), 10.0F);
			blinkMedLeft = (float) Math.pow(Math.sin(blinkFrame * 0.02F + 0.1F), 10.0F);
			blinkBig = (float) Math.pow(Math.sin(blinkFrame * 0.02F + 0.35F), 20.0F);
		}
		this.smallEyelidLeft.y -= (blinkSmallLeft);
		this.smallEyelidRight.y -= (blinkSmallRight);
		this.medEyelidTopRight.y -= (blinkMedRight);
		this.medEyelidTopLeft.y -= (blinkMedLeft);
		this.medEyelidBottomRight.y += (blinkMedRight);
		this.medEyelidBottomLeft.y += (blinkMedLeft);

		float blinkAnim1 = (float) Math.pow(2.0F * (1.0F - blinkBig) - 1.0F, 2.0F);
		if (1.0F - blinkBig < 0.5F) blinkAnim1 *= -1.0F;
		float blinkAnim2 = 1 - (float) Math.pow(2.0F * (1.0F - blinkBig) - 1.0F, 6.0F);
		float blinkAnim3 = (float) Math.pow(Math.sin((1.0F - blinkBig) - 1.8F), 10.0F) - 1.0F;
		float blinkAnim4 = (float) Math.pow(1 - blinkBig, 20.0F);
		this.bigEyelidTop.xRot -= 0.5F * (blinkAnim1 + 1.0F) + 0.4F * blinkAnim4;
		this.bigEyelidTop.y -= 0.9F * blinkAnim2 - 0.3F * blinkAnim4;
		this.bigEyelidTop.z -= 2 * blinkAnim3 + 0.6F;
		this.bigEyelidTop.zScale -= 0.4F * blinkAnim4;
		this.bigEyelidBottom.xRot += 0.5F * (blinkAnim1 + 1.0F) + 0.4F * blinkAnim4;
		this.bigEyelidBottom.y += 0.9F * blinkAnim2 - 0.2F * blinkAnim4;
		this.bigEyelidBottom.z -= blinkAnim3 + 0.6F;
		this.bigEyelidBottom.zScale -= 0.45F * blinkAnim4;

		this.root.y -= 2.2F;

//        swing = frame;
//        limbSwingAmount = 1.0F;

		float newf1 = limbSwingAmount;
		if (newf1 > 0.4F) newf1 = 0.4F;
		float newf12 = limbSwingAmount;
		if (newf12 > 0.7F) newf12 = 0.7F;

		float globalDegree = 1.4F;
		float wiggleDegree = 2.0F;
		float globalSpeed = 0.8F;
		float globalHeight = 1.0F;

		this.bodyBase.x -= wiggleDegree * globalDegree * newf1 * 3.0F * Mth.cos(globalSpeed * limbSwing);
		this.swing(this.bodyBase, globalSpeed, 0.2F * globalDegree * wiggleDegree, false, 1.6F, 0.0F, limbSwing, newf1);
		this.flap(this.bodyMain, globalSpeed, 0.2F * globalDegree * wiggleDegree, false, 0.8F, 0.0F, limbSwing, newf1);
		this.flap(this.abdomen, globalSpeed, 0.3F * globalDegree * wiggleDegree, false, 0.0F, 0.0F, limbSwing, newf1);
		this.flap(this.neck1a, globalSpeed, 0.7F * globalDegree * wiggleDegree, true, -0.5F, 0.0F, limbSwing, newf1);
		this.flap(this.head_main, globalSpeed, 0.35F * globalDegree * wiggleDegree, false, -1.0F, 0.0F, limbSwing, newf1);
		this.swing(this.head_main, globalSpeed, 0.2F * globalDegree * wiggleDegree, true, 0.0F, 0.0F, limbSwing, newf1);
		this.head_main.z -= 1.0F * Mth.cos(2.0F * limbSwing * globalSpeed - 0.5F) * newf1 * globalDegree;

		this.walk(this.bodyMain, 2.0F * globalSpeed, 0.1F * globalHeight, true, -1.5F, 0.1F, limbSwing, limbSwingAmount);
		this.walk(this.neck1a, 2.0F * globalSpeed, 0.1F * globalHeight, false, -1.5F, -0.1F, limbSwing, limbSwingAmount);
		this.walk(this.jaw_lower_main, 2.0F * globalSpeed, 0.15F * globalHeight, false, -0.7F, -0.1F, limbSwing, limbSwingAmount);
		this.walk(this.jaw_lower_left1a, 2.0F * globalSpeed, 0.1F * globalHeight, false, -0.7F, -0.1F, limbSwing, limbSwingAmount);
		this.walk(this.jaw_lower_right1a, 2.0F * globalSpeed, 0.1F * globalHeight, false, -0.7F, -0.1F, limbSwing, limbSwingAmount);
		this.bob(this.bodyBase, 2.0F * globalSpeed, 0.5F * globalHeight, false, limbSwing, limbSwingAmount);

		float legOffset = -0.4F;
		this.flap(this.leg_left1a, globalSpeed, 0.6F * globalDegree, false, 0.0F - 0.8F + legOffset, 0.6F, limbSwing, newf12);
		this.walk(this.leg_left1a, globalSpeed, 0.2F * globalDegree, true, 0.0F - 2.0F + legOffset, 0.5F, limbSwing, newf12);
		this.walk(this.leg_left1b, globalSpeed, 0.3F * globalDegree, true, -1.5F + 0.2F + legOffset, -0.6F, limbSwing, newf12);
		this.flap(this.leg_left1b, globalSpeed, 0.5F * globalDegree, true, -1.5F + 0.7F + legOffset, 0.0F, limbSwing, newf12);

		this.flap(this.leg_right1a, globalSpeed, 0.6F * globalDegree, false, 0.0F - 0.8F + legOffset, -0.6F, limbSwing, newf12);
		this.walk(this.leg_right1a, globalSpeed, 0.2F * globalDegree, false, 0.0F - 2.0F + legOffset, 0.5F, limbSwing, newf12);
		this.walk(this.leg_right1b, globalSpeed, 0.3F * globalDegree, false, -1.5F + 0.2F + legOffset, -0.6F, limbSwing, newf12);
		this.flap(this.leg_right1b, globalSpeed, 0.5F * globalDegree, true, -1.5F + 0.7F + legOffset, 0.0F, limbSwing, newf12);

		this.walk(this.arm_lefta, globalSpeed, 0.5F * globalDegree, true, -1.6F - 0.4F, -0.3F, limbSwing, newf12);
		this.walk(this.arm_leftb, globalSpeed, 0.5F * globalDegree, true, -0.1F - 0.4F, -0.2F, limbSwing, newf12);
		this.swing(this.arm_leftb, globalSpeed, 0.3F * globalDegree, false, -0.1F - 0.4F, 0.0F, limbSwing, newf12);

		this.walk(this.arm_righta, globalSpeed, 0.5F * globalDegree, false, -1.6F - 0.4F, -0.3F, limbSwing, newf12);
		this.walk(this.arm_rightb, globalSpeed, 0.5F * globalDegree, false, -0.1F - 0.4F, -0.2F, limbSwing, newf12);
		this.swing(this.arm_rightb, globalSpeed, 0.3F * globalDegree, false, -0.1F - 0.4F, 0.0F, limbSwing, newf12);

		float midLegOffset = Mth.PI - 3.0F;
		float midLegDegree = 0.5F;
		this.walk(this.midarm_lefta, globalSpeed, 0.7F * globalDegree * midLegDegree, true, -1.6F - 0.4F + midLegOffset, 0.1F, limbSwing, newf12);
		this.swing(this.midarm_lefta, globalSpeed, 0.2F * globalDegree * midLegDegree, false, -1.6F + 2.4f + midLegOffset, 0.3F, limbSwing, newf12);
		this.flap(this.midarm_lefta, globalSpeed, 0.3F * globalDegree * midLegDegree, false, -1.6F + 0.4F + midLegOffset, 0.5F, limbSwing, newf12);
		this.walk(this.midarm_leftb, globalSpeed, 0.5F * globalDegree * midLegDegree, true, -0.1F - 0.4F + midLegOffset, 0.2F, limbSwing, newf12);
		this.swing(this.midarm_leftb, globalSpeed, 0.3F * globalDegree * midLegDegree, false, -0.1F - 0.4F + midLegOffset, 0.0F, limbSwing, newf12);

		this.walk(this.midarm_righta, globalSpeed, 0.7F * globalDegree * midLegDegree, false, -1.6F - 0.4F + midLegOffset, 0.1F, limbSwing, newf12);
		this.swing(this.midarm_righta, globalSpeed, 0.2F * globalDegree * midLegDegree, false, -1.6F + 2.4f + midLegOffset, -0.3F, limbSwing, newf12);
		this.flap(this.midarm_righta, globalSpeed, 0.3F * globalDegree * midLegDegree, false, -1.6F + 0.4F + midLegOffset, -0.5F, limbSwing, newf12);
		this.walk(this.midarm_rightb, globalSpeed, 0.5F * globalDegree * midLegDegree, false, -0.1F - 0.4F + midLegOffset, 0.2F, limbSwing, newf12);
		this.swing(this.midarm_rightb, globalSpeed, 0.3F * globalDegree * midLegDegree, false, -0.1F - 0.4F + midLegOffset, 0.0F, limbSwing, newf12);

		Vec3 eyeRot = entity.prevEyeRotation.add(entity.eyeRotation.subtract(entity.prevEyeRotation).scale(partialTick));
		eyeRot = eyeRot.scale(0.5F);
		this.bigeye.xRot += (float) (eyeRot.x * 0.5F);
		this.bigeye.yRot += (float) eyeRot.y;
		this.bigeye.zRot += (float) eyeRot.z;

		this.walk(this.head_main, 4.0F, 0.01F, false, 0.0F, 0.0F, frame, 1.0F);
		this.swing(this.head_main, 3.0F, 0.01F, false, 0.0F, 0.0F, frame, 1.0F);
		this.flap(this.head_main, 5.0F, 0.01F, false, 0.0F, 0.0F, frame, 1.0F);

		this.walk(this.jaw_lower_main, 0.4F, 0.2F, false, 0.0F, 0.0F, frame, 1.0F);
		this.walk(this.jaw_lower_right1a, 0.4F, 0.1F, false, -0.35F, 0.0F, frame, 1.0F);
		this.walk(this.jaw_lower_left1a, 0.4F, 0.1F, false, -0.35F, 0.0F, frame, 1.0F);
		this.swing(this.jaw_lower_right1a, 0.4F, 0.1F, true, -0.7F, 0.0F, frame, 1.0F);
		this.swing(this.jaw_lower_left1a, 0.4F, 0.1F, false, -0.7F, 0.0F, frame, 1.0F);

		float screechTicks = entity.prevScreechingTicks + (entity.screechingTicks - entity.prevScreechingTicks) * partialTick;

		float upright = easeInOutBack(Math.min(screechTicks / 40.0F, 1.0F));
		this.bodyMain.xRot -= upright * 0.5F;
		this.head_main.xRot -= upright;

		this.arm_lefta.xRot -= upright * 0.7F;
		this.arm_righta.xRot -= upright * 0.7F;

		this.arm_lefta.yRot -= upright * 0.7F;
		this.arm_righta.yRot += upright * 0.7F;

		this.arm_lefta.zRot += upright * 0.7F;
		this.arm_righta.zRot -= upright * 0.7F;

		this.midarm_lefta.xRot -= upright * 0.4F;
		this.midarm_righta.xRot -= upright * 0.4F;
		this.midarm_lefta.yRot -= upright * 0.4F;
		this.midarm_righta.yRot += upright * 0.4F;
		this.midarm_leftb.xRot += upright * 0.4F;
		this.midarm_rightb.xRot += upright * 0.4F;
		this.midarm_leftb.zRot -= upright * 0.1F;
		this.midarm_rightb.zRot += upright * 0.1F;

		float screeching = easeInOutBack(Mth.clamp((screechTicks - 20.0F) / 30.0F, 0.0F, 1.0F));
		float screechingHeadRotationStrength = Mth.sin(frame * 0.4F) * screeching;
		this.head_main.yRot += screechingHeadRotationStrength * 0.13F;
		this.head_main.zRot += screechingHeadRotationStrength * 0.13F;

		this.abdomen.yRot += screechingHeadRotationStrength * 0.04F;
		this.abdomen.zRot += screechingHeadRotationStrength * 0.04F;

		this.midarm_lefta.zRot -= screechingHeadRotationStrength * 0.04F;
		this.midarm_righta.zRot -= screechingHeadRotationStrength * 0.04F;
		this.midarm_lefta.yRot -= screechingHeadRotationStrength * 0.04F;
		this.midarm_righta.yRot -= screechingHeadRotationStrength * 0.04F;

		this.midarm_lefta.xRot -= screechingHeadRotationStrength * 0.04F;
		this.midarm_leftb.xRot += screechingHeadRotationStrength * 0.075F;

		this.midarm_righta.xRot += screechingHeadRotationStrength * 0.04F;
		this.midarm_rightb.xRot -= screechingHeadRotationStrength * 0.075F;

		float armShakeStrength = Mth.sin(frame * 4.0F) * screeching;
		this.arm_lefta.zRot += armShakeStrength * 0.025F;
		this.arm_righta.zRot -= armShakeStrength * 0.025F;
	}

	private static float easeInOutBack(float x) {
		float c1 = 1.70158F;
		float c2 = c1 * 1.525F;

		return x < 0.5F
			? ((float) Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
			: ((float) Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
	}
}
