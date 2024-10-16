package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Emberling;

public class EmberlingModel extends MowzieModelBase<Emberling> {

	private final ModelPart root;
	private final ModelPart body2;
	private final ModelPart body3;
	private final ModelPart body4;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;
	private final ModelPart leftArm;
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
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart tail6;
	private final ModelPart tail7;

	public EmberlingModel(ModelPart root) {
		this.root = root;
		this.body3 = root.getChild("body_3");
		this.body2 = this.body3.getChild("body_2");
		this.body4 = this.body3.getChild("body_4");
		this.leftLeg1 = this.body3.getChild("left_leg_1");
		this.leftLeg2 = this.leftLeg1.getChild("left_leg_2");
		this.rightLeg1 = this.body3.getChild("right_leg_1");
		this.rightLeg2 = this.rightLeg1.getChild("right_leg_2");
		this.leftArm = this.body2.getChild("body_base").getChild("left_arm_1");
		this.rightArm = this.body2.getChild("body_base").getChild("right_arm_1");
		this.head = this.body2.getChild("body_base").getChild("neck").getChild("head");
		this.jaw = this.head.getChild("jaw_connection").getChild("jaw");
		this.leftLowerGills1 = this.head.getChild("left_lower_gills_1");
		this.leftLowerGills2 = this.leftLowerGills1.getChild("left_lower_gills_2");
		this.rightLowerGills1 = this.head.getChild("right_lower_gills_1");
		this.rightLowerGills2 = this.rightLowerGills1.getChild("right_lower_gills_2");
		this.leftUpperGills1 = this.head.getChild("left_upper_gills_1");
		this.leftUpperGills2 = this.leftUpperGills1.getChild("left_upper_gills_2");
		this.rightUpperGills1 = this.head.getChild("right_upper_gills_1");
		this.rightUpperGills2 = this.rightUpperGills1.getChild("right_upper_gills_2");
		this.tail1 = this.body4.getChild("body_5").getChild("tail_1");
		this.tail2 = this.tail1.getChild("tail_2");
		this.tail3 = this.tail2.getChild("tail_3");
		this.tail4 = this.tail3.getChild("tail_4");
		this.tail5 = this.tail4.getChild("tail_5");
		this.tail6 = this.tail5.getChild("tail_6");
		this.tail7 = this.tail6.getChild("tail_7");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body3 = partDefinition.addOrReplaceChild("body_3", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-3.51F, -3.5F, 0.0F, 7, 7, 3),
			PartPose.offsetAndRotation(0.0F, 15.5F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var body4 = body3.addOrReplaceChild("body_4", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-3.0F, 0.0F, 0.0F, 6, 6, 3),
			PartPose.offsetAndRotation(0.0F, -3.5F, 3.0F, -0.18203784098300857F, 0.0F, 0.0F));
		var body5 = body4.addOrReplaceChild("body_5", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-3.01F, 0.0F, 0.0F, 6, 5, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var body2 = body3.addOrReplaceChild("body_2", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-3.5F, 0.0F, -3.0F, 7, 7, 3),
			PartPose.offset(0.0F, -3.5F, 0.0F));
		var body_base = body2.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, 0.0F, -1.0F, 8, 7, 6),
			PartPose.offsetAndRotation(0.0F, 1.1F, -7.9F, 0.22759093446006054F, 0.0F, 0.0F));

		body_base.addOrReplaceChild("sidefin_left", CubeListBuilder.create()
				.texOffs(42, 50).addBox(0.0F, 0.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(4.0F, 1.0F, 5.0F, 0.0F, 0.18203784098300857F, 0.0F));
		body_base.addOrReplaceChild("sidefin_right", CubeListBuilder.create()
				.texOffs(49, 50).addBox(0.0F, 0.0F, 0.0F, 0, 4, 3),
			PartPose.offsetAndRotation(-4.0F, 1.0F, 5.0F, 0.0F, -0.18203784098300857F, 0.0F));

		var neckpiece = body_base.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(44, 0).addBox(-3.0F, 0.0F, -2.0F, 6, 6, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var head_main = neckpiece.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 54).addBox(-3.0F, 0.0F, -6.0F, 6, 4, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.045553093477052F, 0.0F, 0.0F));
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
			PartPose.offsetAndRotation(4.0F, 5.0F, 2.0F, 0.22759093446006054F, -0.091106186954104F, -0.18203784098300857F));
		arm_left1.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(20, 21).addBox(-1.01F, 0.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, -0.31869712141416456F, 0.0F, 0.0F));

		var arm_right1 = body_base.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(20, 28).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-4.0F, 5.0F, 2.0F, 0.22759093446006054F, 0.091106186954104F, 0.18203784098300857F));
		arm_right1.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(20, 35).addBox(-0.99F, 0.0F, -2.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, -0.31869712141416456F, 0.0F, 0.0F));

		var hindleg_left1 = body3.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(42, 39).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(3.0F, 1.5F, 2.0F, -0.2617993877991494F, -0.3490658503988659F, -0.17453292519943295F));
		hindleg_left1.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(51, 39).addBox(-1.01F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 0.7740535232594852F, 0.0F, 0.0F));

		var hindleg_right1 = body3.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(42, 46).addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(-3.0F, 1.5F, 2.0F, -0.2617993877991494F, 0.3490658503988659F, 0.17453292519943295F));
		hindleg_right1.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(51, 46).addBox(-0.99F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 0.7740535232594852F, 0.0F, 0.0F));

		var tail1 = body5.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(29, 0).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(29, 8).addBox(-2.01F, -4.0F, 0.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(29, 16).addBox(-1.5F, -4.0F, 0.0F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
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
	public void setupAnim(Emberling entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float heady = Mth.sin((netHeadYaw / Mth.RAD_TO_DEG) * 0.5F);
		float headx = Mth.sin((headPitch / Mth.RAD_TO_DEG) * 0.5F);
		if (entity.isInSittingPose()) {
			this.head.yRot = 0F;
			this.head.xRot = 0F;
		} else {
			this.head.yRot = heady;
			this.head.xRot = 0.045553093477052F + headx + entity.animationTicks;
		}
	}

	@Override
	public void prepareMobModel(Emberling entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float animation = Mth.sin(limbSwing * 0.6F) * limbSwingAmount * 0.4F;
		float flap = Mth.sin((entity.tickCount + partialTick) * 0.2F) * 0.8F;
		float headFlap = Mth.sin((entity.tickCount + partialTick) * 0.6F) * 0.7F;
		this.leftUpperGills1.yRot = 0.5918411493512771F - flap * 0.125F;
		this.leftUpperGills2.yRot = 0.22759093446006054F - flap * 0.25F;

		this.rightUpperGills1.yRot = -0.5918411493512771F + flap * 0.125F;
		this.rightUpperGills2.yRot = -0.22759093446006054F + flap * 0.25F;

		this.leftLowerGills1.yRot = 0.8651597102135892F - flap * 0.125F;
		this.leftLowerGills2.yRot = 0.31869712141416456F - flap * 0.25F;

		this.rightLowerGills1.yRot = -0.8651597102135892F + flap * 0.125F;
		this.rightLowerGills2.yRot = -0.31869712141416456F + flap * 0.25F;

		if (entity.isShootingFlames()) {
			this.jaw.xRot = 1.0F;
			this.head.zRot = 0.0F + headFlap;
		} else {
			this.jaw.xRot = (!entity.isInSittingPose() ? 0.40980330836826856F : 0.2F) + flap * (!entity.isInSittingPose() ? 0.5F : 0.125F);
			this.head.zRot = 0.0F;
		}

		if (entity.isInSittingPose()) {
			this.body3.y = 20.5F;
			this.leftLeg1.xRot = -1.4F;
			this.rightLeg1.xRot = -1.4F;

			this.leftLeg2.xRot = 0.7740535232594852F;
			this.rightLeg2.xRot = 0.7740535232594852F;

			this.rightArm.xRot = -1.1F;
			this.rightArm.yRot = 0.091106186954104F;
			this.rightArm.zRot = 0.18203784098300857F;

			this.leftArm.xRot = -1.1F;
			this.leftArm.yRot = -0.091106186954104F;
			this.leftArm.zRot = -0.18203784098300857F;

			this.body2.yRot = 0F;
			this.body4.yRot = 0F;

			this.body2.xRot = 0F + flap * 0.0125F;
			this.body4.xRot = -0.18203784098300857F - flap * 0.025F;

			this.tail1.yRot = 0.4F;
			this.tail2.yRot = 0.4F;
			this.tail3.yRot = 0.4F;
			this.tail4.yRot = 0.6F;
			this.tail5.yRot = 0.8F;
			this.tail6.yRot = 0.4F;
			this.tail7.yRot = 0.2F;

			this.tail2.zRot = 0.3F;
			this.tail3.zRot = 0.3F;
			this.tail4.zRot = 0.3F;
			this.tail5.zRot = 0.1F;
		} else {
			this.body3.y = 15.5F;
			this.leftLeg1.xRot = -0.2617993877991494F + animation * 2F;
			this.rightLeg1.xRot = -0.2617993877991494F - animation * 2F;

			this.leftLeg2.xRot = 0.7740535232594852F;
			this.rightLeg2.xRot = 0.7740535232594852F;

			this.rightArm.xRot = 0.22759093446006054F + animation * 2F;
			this.rightArm.yRot = 0.091106186954104F + animation;
			this.rightArm.zRot = 0.18203784098300857F + entity.smoothedAngle(partialTick);

			this.leftArm.xRot = 0.22759093446006054F - animation * 2F;
			this.leftArm.yRot = -0.091106186954104F + animation;
			this.leftArm.zRot = -0.18203784098300857F - entity.smoothedAngle(partialTick);

			this.body2.yRot = 0F + animation * 0.8F + entity.smoothedAngle(partialTick) * 0.125F;
			this.body4.yRot = 0F - animation * 0.8F - entity.smoothedAngle(partialTick) * 0.125F;

			this.tail1.yRot = 0F + animation * 0.5F - flap * 0.25F + entity.smoothedAngle(partialTick) * 0.125F;
			this.tail2.yRot = 0F + animation * 0.5F - flap * 0.25F + entity.smoothedAngle(partialTick) * 0.125F;
			this.tail3.yRot = 0F - animation * 0.5F + flap * 0.25F - entity.smoothedAngle(partialTick) * 0.125F;
			this.tail4.yRot = 0F - animation * 0.5F + flap * 0.25F - entity.smoothedAngle(partialTick) * 0.125F;
			this.tail5.yRot = 0F - animation * 0.5F + flap * 0.25F - entity.smoothedAngle(partialTick) * 0.125F;
			this.tail6.yRot = 0F - animation * 0.5F + flap * 0.25F - entity.smoothedAngle(partialTick) * 0.125F;
			this.tail7.yRot = 0F - animation * 0.5F + flap * 0.25F - entity.smoothedAngle(partialTick) * 0.125F;

			this.tail2.zRot = 0F;
			this.tail3.zRot = 0F;
			this.tail4.zRot = 0F;
		}
	}
}
