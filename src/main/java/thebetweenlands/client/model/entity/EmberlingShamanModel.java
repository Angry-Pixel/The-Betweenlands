package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.EmberlingShaman;

public class EmberlingShamanModel extends MowzieModelBase<EmberlingShaman> {

	private final ModelPart root;
	private final ModelPart bodyBase;
	private final ModelPart body3;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;
	private final ModelPart leftArm1;
	private final ModelPart leftArm2;
	private final ModelPart rightArm;
	private final ModelPart head;
	private final ModelPart leftUpperGills1;
	private final ModelPart leftUpperGills2;
	private final ModelPart leftLowerGills1;
	private final ModelPart leftLowerGills2;
	private final ModelPart rightUpperGills1;
	private final ModelPart rightUpperGills2;
	private final ModelPart rightLowerGills1;
	private final ModelPart rightLowerGills2;
	private final ModelPart jaw;

	public EmberlingShamanModel(ModelPart root) {
		this.root = root;
		this.bodyBase = root.getChild("body_base");
		this.body3 = this.bodyBase.getChild("body_2").getChild("body_3");
		this.leftLeg1 = this.body3.getChild("left_leg_1");
		this.leftLeg2 = this.leftLeg1.getChild("left_leg_2");
		this.rightLeg1 = this.body3.getChild("right_leg_1");
		this.rightLeg2 = this.rightLeg1.getChild("right_leg_2");
		this.leftArm1 = this.bodyBase.getChild("left_arm_1");
		this.leftArm2 = this.leftArm1.getChild("left_arm_2");
		this.rightArm = this.bodyBase.getChild("right_arm_1");
		this.head = this.bodyBase.getChild("neck").getChild("head");
		this.jaw = this.head.getChild("jaw_connection").getChild("jaw");
		this.leftLowerGills1 = this.head.getChild("left_lower_gills_1");
		this.leftLowerGills2 = this.leftLowerGills1.getChild("left_lower_gills_2");
		this.rightLowerGills1 = this.head.getChild("right_lower_gills_1");
		this.rightLowerGills2 = this.rightLowerGills1.getChild("right_lower_gills_2");
		this.leftUpperGills1 = this.head.getChild("left_upper_gills_1");
		this.leftUpperGills2 = this.leftUpperGills1.getChild("left_upper_gills_2");
		this.rightUpperGills1 = this.head.getChild("right_upper_gills_1");
		this.rightUpperGills2 = this.rightUpperGills1.getChild("right_upper_gills_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body_base = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, 0.0F, -1.0F, 8, 7, 6),
			PartPose.offsetAndRotation(0.0F, 9.0F, -6.0F, -0.18203784098300857F, 0.0F, 0.0F));
		var body2 = body_base.addOrReplaceChild("body_2", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-3.5F, 0.0F, 0.0F, 7, 7, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var body3 = body2.addOrReplaceChild("body_3", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-3.51F, 0.0F, 0.0F, 7, 7, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var body4 = body3.addOrReplaceChild("body_4", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-3.0F, 0.0F, 0.0F, 6, 6, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var body5 = body4.addOrReplaceChild("body_5", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-3.01F, 0.0F, 0.0F, 6, 5, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));

		body_base.addOrReplaceChild("sidefin_left", CubeListBuilder.create()
				.texOffs(42, 50).addBox(0.0F, 0.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(4.0F, 1.0F, 5.0F, 0.0F, 0.18203784098300857F, 0.0F));
		body_base.addOrReplaceChild("sidefin_right", CubeListBuilder.create()
				.texOffs(49, 50).addBox(0.0F, 0.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(-4.0F, 1.0F, 5.0F, 0.0F, -0.18203784098300857F, 0.0F));

		var neckpiece = body_base.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(44, 0).addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var head_main = neckpiece.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 54).addBox(-3.0F, 0.0F, -6.0F, 6, 4, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var jaw_connection = head_main.addOrReplaceChild("jaw_connection", CubeListBuilder.create()
				.texOffs(44, 9).addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2),
			PartPose.offset(0.0F, 4.0F, 0.0F));
		jaw_connection.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(44, 14).addBox(-3.01F, 0.0F, -4.0F, 6, 2, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var gillthingy_left_upper1 = head_main.addOrReplaceChild("left_upper_gills_1", CubeListBuilder.create()
				.texOffs(44, 18).addBox(0.0F, -4.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(3.0F, 2.0F, -1.0F, 0.27314402793711257F, 0.5918411493512771F, 0.091106186954104F));
		gillthingy_left_upper1.addOrReplaceChild("left_upper_gills_2", CubeListBuilder.create()
				.texOffs(51, 18).addBox(0.0F, -4.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.22759093446006054F, 0.0F));

		var gillthingy_right_upper1 = head_main.addOrReplaceChild("right_upper_gills_1", CubeListBuilder.create()
				.texOffs(44, 23).addBox(0.0F, -4.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(-3.0F, 2.0F, -1.0F, 0.27314402793711257F, -0.5918411493512771F, -0.091106186954104F));
		gillthingy_right_upper1.addOrReplaceChild("right_upper_gills_2", CubeListBuilder.create()
				.texOffs(51, 23).addBox(0.0F, -4.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.22759093446006054F, 0.0F));

		var gillthingy_left_lower1 = head_main.addOrReplaceChild("left_lower_gills_1", CubeListBuilder.create()
				.texOffs(44, 29).addBox(0.0F, -3.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(3.0F, 5.0F, -1.0F, -0.22759093446006054F, 0.8651597102135892F, -0.18203784098300857F));
		gillthingy_left_lower1.addOrReplaceChild("left_lower_gills_2", CubeListBuilder.create()
				.texOffs(49, 28).addBox(0.0F, -3.0F, 0.0F, 0, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.31869712141416456F, 0.0F));

		var gillthingy_right_lower1 = head_main.addOrReplaceChild("right_lower_gills_1", CubeListBuilder.create()
				.texOffs(44, 33).addBox(0.0F, -3.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(-3.0F, 5.0F, -1.0F, -0.22759093446006054F, -0.8651597102135892F, 0.18203784098300857F));
		gillthingy_right_lower1.addOrReplaceChild("right_lower_gills_2", CubeListBuilder.create()
				.texOffs(49, 32).addBox(0.0F, -3.0F, 0.0F, 0, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.31869712141416456F, 0.0F));

		var arm_left1 = body_base.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(20, 14).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(4.0F, 5.0F, 2.0F, 0.36425021489121656F, -0.27314402793711257F, -0.22759093446006054F));
		arm_left1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(20, 21).addBox(-1.01F, 0.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, -1.1383037381507017F, 0.0F, 0.0F));
		var arm_right1 = body_base.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(20, 28).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-4.0F, 5.0F, 2.0F, -0.4553564018453205F, -0.136659280431156F, 1.3658946726107624F));
		var arm_right2 = arm_right1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(20, 35).addBox(-0.99F, 0.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, -0.5918411493512771F, 0.0F, 0.0F));

		var charstick1 = arm_right2.addOrReplaceChild("charstick_1", CubeListBuilder.create()
				.texOffs(25, 62).addBox(-3.0F, 0.0F, -1.0F, 18, 1, 1),
			PartPose.offsetAndRotation(6.0F, 3.5F, -1.5F, -1.5707963267948966F, 3.141592653589793F, 0.0F));
		var charstick2 = charstick1.addOrReplaceChild("charstick_2", CubeListBuilder.create()
				.texOffs(25, 59).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(15.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));
		charstick2.addOrReplaceChild("charstick_3", CubeListBuilder.create()
				.texOffs(32, 59).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		var hindleg_left1 = body3.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(42, 39).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(3.0F, 5.0F, 2.0F, 0.5009094953223726F, -0.18203784098300857F, -0.18203784098300857F));
		hindleg_left1.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(51, 39).addBox(-1.01F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 0.31869712141416456F, 0.0F, 0.0F));

		var hindleg_right1 = body3.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(42, 46).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-3.0F, 5.0F, 2.0F, 0.5009094953223726F, 0.18203784098300857F, 0.18203784098300857F));
		hindleg_right1.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(51, 46).addBox(-0.99F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 0.31869712141416456F, 0.0F, 0.0F));

		var tail1 = body5.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(29, 0).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(29, 8).addBox(-2.01F, -4.0F, 0.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(29, 16).addBox(-1.5F, -4.0F, 0.0F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create()
				.texOffs(29, 24).addBox(-1.51F, -3.0F, 0.0F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.40980330836826856F, 0.0F, 0.0F));
		var tail5 = tail4.addOrReplaceChild("tail_5", CubeListBuilder.create()
				.texOffs(29, 31).addBox(-1.0F, -3.0F, 0.0F, 2, 3, 4),
			PartPose.offset(0.0F, 0.0F, 3.0F));
		var tail6 = tail5.addOrReplaceChild("tail_6", CubeListBuilder.create()
				.texOffs(29, 39).addBox(-1.01F, -3.0F, 0.0F, 2, 3, 4),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		var tail7 = tail6.addOrReplaceChild("tail_7", CubeListBuilder.create()
				.texOffs(29, 47).addBox(-1.02F, -3.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.40980330836826856F, 0.0F, 0.0F));
		tail7.addOrReplaceChild("tail_fin", CubeListBuilder.create()
				.texOffs(25, 45).addBox(0.0F, -5.0F, -1.0F, 0, 5, 8),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(EmberlingShaman entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float heady = Mth.sin((netHeadYaw / Mth.RAD_TO_DEG) * 0.5F);
		float headx = Mth.sin((headPitch / Mth.RAD_TO_DEG) * 0.5F);

		this.head.yRot = heady;
		this.head.xRot = 0.18203784098300857F + headx + entity.animationTicks;
	}

	@Override
	public void prepareMobModel(EmberlingShaman entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float animation = Mth.sin(limbSwing * 0.6F) * limbSwingAmount * 0.5F;

		float flap = Mth.sin((entity.tickCount + partialTick) * 0.2F) * 0.8F;

		this.leftLeg1.xRot = 0.5009094953223726F + animation * 2F;
		this.rightLeg1.xRot = 0.5009094953223726F - animation * 2F;

		this.leftLeg2.xRot = 0.31869712141416456F;
		this.rightLeg2.xRot = 0.31869712141416456F;

		this.rightArm.xRot = -0.4553564018453205F - animation;
		this.rightArm.yRot = -0.136659280431156F - animation * 2F;
		this.rightArm.zRot = 1.3658946726107624F + entity.smoothedAngle(partialTick);

		this.leftArm1.xRot = 0.36425021489121656F - animation * 2F;
		this.leftArm2.xRot = -1.1383037381507017F + animation;
		this.leftArm1.yRot = -0.27314402793711257F + animation;
		this.leftArm1.zRot = -0.22759093446006054F - entity.smoothedAngle(partialTick);

		this.bodyBase.xRot = -0.18203784098300857F - animation * 0.5F + flap * 0.025F - entity.smoothedAngle(partialTick) * 0.125F;

		this.body3.xRot = -0.22759093446006054F + animation * 0.5F - flap * 0.05F + entity.smoothedAngle(partialTick) * 0.125F;

		this.jaw.xRot = 0.40980330836826856F + flap * 0.5F;

		this.leftUpperGills1.yRot = 0.5918411493512771F - flap * 0.125F;
		this.leftUpperGills2.yRot = 0.22759093446006054F - flap * 0.25F;

		this.rightUpperGills1.yRot = -0.5918411493512771F + flap * 0.125F;
		this.rightUpperGills2.yRot = -0.22759093446006054F + flap * 0.25F;

		this.leftLowerGills1.yRot = 0.8651597102135892F - flap * 0.125F;
		this.leftLowerGills2.yRot = 0.31869712141416456F - flap * 0.25F;

		this.rightLowerGills1.yRot = -0.8651597102135892F + flap * 0.125F;
		this.rightLowerGills2.yRot = -0.31869712141416456F + flap * 0.25F;
	}
}
