package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.BonePuppetRanged;

public class BonePuppetRangedModel<T extends BonePuppetRanged> extends MowzieModelBase<T> {
	private final ModelPart hipbone;
	private final ModelPart hip_l1;
	private final ModelPart hip_l2;
	private final ModelPart hip_r1;
	private final ModelPart hip_r2;
	private final ModelPart spine1;
	private final ModelPart spine2;
	private final ModelPart spine3;
	private final ModelPart spine4;
	private final ModelPart spine5;
	private final ModelPart spine6;
	private final ModelPart spine7;
	private final ModelPart spine8;
	private final ModelPart ribs1;
	private final ModelPart spine9;
	private final ModelPart spine10;
	private final ModelPart shoulder_blade;
	private final ModelPart arm1;
	private final ModelPart arm_lower;
	private final ModelPart arm2;
	private final ModelPart hand;
	private final ModelPart weapon;
	private final ModelPart finger_i;
	private final ModelPart finger_i2;
	private final ModelPart fingers;
	private final ModelPart fingers2;
	private final ModelPart thumb;
	private final ModelPart thumb2;
	private final ModelPart arm3;
	private final ModelPart ribs7;
	private final ModelPart ribs2;
	private final ModelPart ribs3;
	private final ModelPart ribs5;
	private final ModelPart ribs4;
	private final ModelPart ribs6;
	private final ModelPart leg_left1;
	private final ModelPart leg_left_lower;
	private final ModelPart leg_left2;
	private final ModelPart foot_left;
	private final ModelPart toe_left1;
	private final ModelPart toe_left2;
	private final ModelPart toes_left1;
	private final ModelPart toes_left2;
	private final ModelPart leg_left3;
	private final ModelPart leg_right1;
	private final ModelPart leg_right2;


	public BonePuppetRangedModel(ModelPart root) {
		super(root, RenderType::entityCutoutNoCull);
		this.hipbone = root.getChild("hipbone");
		this.hip_l1 = this.hipbone.getChild("hip_l1");
		this.hip_l2 = this.hip_l1.getChild("hip_l2");
		this.hip_r1 = this.hipbone.getChild("hip_r1");
		this.hip_r2 = this.hip_r1.getChild("hip_r2");
		this.spine1 = this.hipbone.getChild("spine1");
		this.spine2 = this.spine1.getChild("spine2");
		this.spine3 = this.spine2.getChild("spine3");
		this.spine4 = this.spine3.getChild("spine4");
		this.spine5 = this.spine4.getChild("spine5");
		this.spine6 = this.spine5.getChild("spine6");
		this.spine7 = this.spine6.getChild("spine7");
		this.spine8 = this.spine7.getChild("spine8");
		this.ribs1 = this.spine8.getChild("ribs1");
		this.spine9 = this.spine8.getChild("spine9");
		this.spine10 = this.spine9.getChild("spine10");
		this.shoulder_blade = this.spine9.getChild("shoulder_blade");
		this.arm1 = this.shoulder_blade.getChild("arm1");
		this.arm_lower = this.arm1.getChild("arm_lower");
		this.arm2 = this.arm_lower.getChild("arm2");
		this.hand = this.arm2.getChild("hand");
		this.weapon = this.hand.getChild("weapon");
		this.finger_i = this.hand.getChild("finger_i");
		this.finger_i2 = this.finger_i.getChild("finger_i2");
		this.fingers = this.hand.getChild("fingers");
		this.fingers2 = this.fingers.getChild("fingers2");
		this.thumb = this.hand.getChild("thumb");
		this.thumb2 = this.thumb.getChild("thumb2");
		this.arm3 = this.arm_lower.getChild("arm3");
		this.ribs7 = this.spine8.getChild("ribs7");
		this.ribs2 = this.spine7.getChild("ribs2");
		this.ribs3 = this.spine6.getChild("ribs3");
		this.ribs5 = this.spine6.getChild("ribs5");
		this.ribs4 = this.spine4.getChild("ribs4");
		this.ribs6 = this.spine4.getChild("ribs6");
		this.leg_left1 = this.hipbone.getChild("leg_left1");
		this.leg_left_lower = this.leg_left1.getChild("leg_left_lower");
		this.leg_left2 = this.leg_left_lower.getChild("leg_left2");
		this.foot_left = this.leg_left2.getChild("foot_left");
		this.toe_left1 = this.foot_left.getChild("toe_left1");
		this.toe_left2 = this.toe_left1.getChild("toe_left2");
		this.toes_left1 = this.foot_left.getChild("toes_left1");
		this.toes_left2 = this.toes_left1.getChild("toes_left2");
		this.leg_left3 = this.leg_left_lower.getChild("leg_left3");
		this.leg_right1 = this.hipbone.getChild("leg_right1");
		this.leg_right2 = this.leg_right1.getChild("leg_right2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hipbone = partdefinition.addOrReplaceChild("hipbone", CubeListBuilder.create().texOffs(0, 24).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.5F, 2.0F, 0.0869F, 0.0076F, -0.0869F));

		PartDefinition slime6_r1 = hipbone.addOrReplaceChild("slime6_r1", CubeListBuilder.create().texOffs(8, 2).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -3.0F, -0.0873F, 0.0F, 0.0873F));

		PartDefinition slime8_r1 = hipbone.addOrReplaceChild("slime8_r1", CubeListBuilder.create().texOffs(7, 1).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition hip_l1 = hipbone.addOrReplaceChild("hip_l1", CubeListBuilder.create().texOffs(10, 29).addBox(0.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition hip_l2 = hip_l1.addOrReplaceChild("hip_l2", CubeListBuilder.create().texOffs(8, 35).addBox(-0.98F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition hip_r1 = hipbone.addOrReplaceChild("hip_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition hip_r2 = hip_r1.addOrReplaceChild("hip_r2", CubeListBuilder.create().texOffs(0, 35).addBox(-0.02F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition spine1 = hipbone.addOrReplaceChild("spine1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition spine1_r1 = spine1.addOrReplaceChild("spine1_r1", CubeListBuilder.create().texOffs(1, 22).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition slime7_r1 = spine1.addOrReplaceChild("slime7_r1", CubeListBuilder.create().texOffs(7, 1).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition spine2 = spine1.addOrReplaceChild("spine2", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition spine3 = spine2.addOrReplaceChild("spine3", CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition spine4 = spine3.addOrReplaceChild("spine4", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition slime4_r1 = spine4.addOrReplaceChild("slime4_r1", CubeListBuilder.create().texOffs(8, 2).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 2.0F, 0.0F, 0.4363F, 0.0F));

		PartDefinition spine5 = spine4.addOrReplaceChild("spine5", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -1.0F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition slime5_r1 = spine5.addOrReplaceChild("slime5_r1", CubeListBuilder.create().texOffs(7, 2).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 2.0F, -0.48F, 0.0F, 0.0F));

		PartDefinition spine6 = spine5.addOrReplaceChild("spine6", CubeListBuilder.create().texOffs(0, 8).addBox(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition spine7 = spine6.addOrReplaceChild("spine7", CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition spine8 = spine7.addOrReplaceChild("spine8", CubeListBuilder.create().texOffs(0, 4).addBox(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition ribs1 = spine8.addOrReplaceChild("ribs1", CubeListBuilder.create().texOffs(8, 1).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition ribs1_r1 = ribs1.addOrReplaceChild("ribs1_r1", CubeListBuilder.create().texOffs(15, -1).addBox(0.0F, -2.0F, -3.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -3.0F, 0.0F, -1.0908F, 0.0F));

		PartDefinition spine9 = spine8.addOrReplaceChild("spine9", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition spine10 = spine9.addOrReplaceChild("spine10", CubeListBuilder.create().texOffs(0, 1).addBox(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F))
		.texOffs(1, 0).addBox(0.0F, -3.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition shoulder_blade = spine9.addOrReplaceChild("shoulder_blade", CubeListBuilder.create().texOffs(22, 0).addBox(0.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, -0.0532F, -0.1264F, -0.264F));

		PartDefinition collar_bone_r1 = shoulder_blade.addOrReplaceChild("collar_bone_r1", CubeListBuilder.create().texOffs(21, 5).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, -1.0F, 0.0F, -0.6981F, 0.0F));

		PartDefinition arm1 = shoulder_blade.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(31, 2).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.0F, 0.0F, -1.0F, -0.4349F, -0.0368F, -0.5155F));

		PartDefinition arm1_r1 = arm1.addOrReplaceChild("arm1_r1", CubeListBuilder.create().texOffs(31, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -0.5F, 0.0F, 0.0F, -0.1745F));

		PartDefinition slime12_r1 = arm1.addOrReplaceChild("slime12_r1", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 4.25F, -0.5F, 0.0F, 0.829F, 0.0F));

		PartDefinition arm_lower = arm1.addOrReplaceChild("arm_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition arm2 = arm_lower.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(34, 9).addBox(0.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -0.5F, 0.0F, -0.0873F, 0.0F, 0.2182F));

		PartDefinition hand = arm2.addOrReplaceChild("hand", CubeListBuilder.create().texOffs(31, 15).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 5.0F, -0.5F, 0.0F, 0.0F, 0.0873F));

		PartDefinition weapon = hand.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(30, 41).addBox(-1.0F, -0.6F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 3.5F, -2.5F, 2.9196F, -0.0253F, 1.6518F));

		PartDefinition slime1_r1 = weapon.addOrReplaceChild("slime1_r1", CubeListBuilder.create().texOffs(29, 27).addBox(-1.0F, 0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1742F, -1.0F, 2.2963F, 0.6545F, 0.4363F, 0.0F));

		PartDefinition slime1_r2 = weapon.addOrReplaceChild("slime1_r2", CubeListBuilder.create().texOffs(29, 27).addBox(-1.0F, -0.6F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2961F, 0.0F, 4.1563F, -0.1309F, -0.3054F, 0.0F));

		PartDefinition bone_r1 = weapon.addOrReplaceChild("bone_r1", CubeListBuilder.create().texOffs(27, 33).addBox(-2.0F, -0.6F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5968F, 0.0F, 3.2026F, 0.0F, -0.3054F, 0.0F));

		PartDefinition bone_r2 = weapon.addOrReplaceChild("bone_r2", CubeListBuilder.create().texOffs(24, 48).addBox(0.0F, -0.6F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

		PartDefinition finger_i = hand.addOrReplaceChild("finger_i", CubeListBuilder.create().texOffs(29, 15).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, 0.0F, -0.125F, -0.056F, -0.211F));

		PartDefinition finger_i2 = finger_i.addOrReplaceChild("finger_i2", CubeListBuilder.create().texOffs(29, 16).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition fingers = hand.addOrReplaceChild("fingers", CubeListBuilder.create().texOffs(25, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, -0.5F, 0.218F, -0.0483F, 0.1213F));

		PartDefinition fingers2 = fingers.addOrReplaceChild("fingers2", CubeListBuilder.create().texOffs(25, 15).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition thumb = hand.addOrReplaceChild("thumb", CubeListBuilder.create().texOffs(23, 16).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, -1.0F, -0.3054F, 0.0F, 0.3054F));

		PartDefinition thumb2 = thumb.addOrReplaceChild("thumb2", CubeListBuilder.create().texOffs(23, 17).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition arm3 = arm_lower.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(30, 9).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, 0.5F, -0.1308F, 0.0057F, -0.0004F));

		PartDefinition ribs7 = spine8.addOrReplaceChild("ribs7", CubeListBuilder.create().texOffs(20, 19).addBox(0.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -1.0F, 0.0F, -0.1309F, 0.0F));

		PartDefinition ribs2 = spine7.addOrReplaceChild("ribs2", CubeListBuilder.create().texOffs(8, 6).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.0F, 0.1309F, 0.0F));

		PartDefinition slime2_r1 = ribs2.addOrReplaceChild("slime2_r1", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -3.0F, -0.7652F, -0.876F, 0.8962F));

		PartDefinition slime1_r3 = ribs2.addOrReplaceChild("slime1_r3", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -2.0F, 0.0F, 0.7418F, 0.0F));

		PartDefinition ribs1_r2 = ribs2.addOrReplaceChild("ribs1_r2", CubeListBuilder.create().texOffs(15, 3).addBox(0.0F, -2.0F, -3.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -3.0F, 0.0F, -1.0908F, 0.0F));

		PartDefinition ribs3 = spine6.addOrReplaceChild("ribs3", CubeListBuilder.create().texOffs(8, 12).addBox(-2.0F, -2.0F, -3.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.1309F, 0.0F));

		PartDefinition slime3_r1 = ribs3.addOrReplaceChild("slime3_r1", CubeListBuilder.create().texOffs(8, 1).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, -0.6981F, 0.0F));

		PartDefinition ribs5 = spine6.addOrReplaceChild("ribs5", CubeListBuilder.create().texOffs(15, 10).addBox(0.0F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -1.0F, 0.0F, -0.1309F, 0.0F));

		PartDefinition ribs4 = spine4.addOrReplaceChild("ribs4", CubeListBuilder.create().texOffs(8, 18).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 1.0F, 0.0F, 0.1309F, 0.0F));

		PartDefinition ribs6 = spine4.addOrReplaceChild("ribs6", CubeListBuilder.create().texOffs(14, 18).addBox(0.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.0F, -0.1309F, 0.0F));

		PartDefinition leg_left1 = hipbone.addOrReplaceChild("leg_left1", CubeListBuilder.create().texOffs(12, 39).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.0F, 0.5F, -1.0F, -0.3043F, 0.0262F, 0.2141F));

		PartDefinition leg_left1_r1 = leg_left1.addOrReplaceChild("leg_left1_r1", CubeListBuilder.create().texOffs(5, 35).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition slime13_r1 = leg_left1.addOrReplaceChild("slime13_r1", CubeListBuilder.create().texOffs(8, 2).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 4.0F, -1.0F, 0.0F, 0.6981F, 0.0F));

		PartDefinition leg_left_lower = leg_left1.addOrReplaceChild("leg_left_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 6.0F, -0.5F, 0.3463F, 0.0447F, -0.1231F));

		PartDefinition leg_left2 = leg_left_lower.addOrReplaceChild("leg_left2", CubeListBuilder.create().texOffs(15, 46).addBox(0.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition slime14_r1 = leg_left2.addOrReplaceChild("slime14_r1", CubeListBuilder.create().texOffs(7, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.5F, 0.0F, 0.0F, 0.0F, 0.829F));

		PartDefinition foot_left = leg_left2.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(3, 46).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 6.5F, 0.5F, -0.1308F, -0.0057F, -0.0433F));

		PartDefinition heel_r1 = foot_left.addOrReplaceChild("heel_r1", CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, 1.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition toe_left1 = foot_left.addOrReplaceChild("toe_left1", CubeListBuilder.create().texOffs(16, 35).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.079F, 0.1775F, -0.0288F));

		PartDefinition toe_left2 = toe_left1.addOrReplaceChild("toe_left2", CubeListBuilder.create().texOffs(15, 36).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toes_left1 = foot_left.addOrReplaceChild("toes_left1", CubeListBuilder.create().texOffs(12, 35).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 0.1F, -2.0F, 0.0704F, -0.09F, 0.0381F));

		PartDefinition toes_left2 = toes_left1.addOrReplaceChild("toes_left2", CubeListBuilder.create().texOffs(11, 36).addBox(0.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition leg_left3 = leg_left_lower.addOrReplaceChild("leg_left3", CubeListBuilder.create().texOffs(11, 46).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0436F, 0.0019F, -0.1745F));

		PartDefinition leg_right1 = hipbone.addOrReplaceChild("leg_right1", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.0F, 0.25F, -1.0F, -0.0922F, -0.365F, -0.0124F));

		PartDefinition leg_right1_r1 = leg_right1.addOrReplaceChild("leg_right1_r1", CubeListBuilder.create().texOffs(6, 29).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition slime9_r1 = leg_right1.addOrReplaceChild("slime9_r1", CubeListBuilder.create().texOffs(8, 2).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, 0.0F, 0.829F, 0.0F));

		PartDefinition slime10_r1 = leg_right1.addOrReplaceChild("slime10_r1", CubeListBuilder.create().texOffs(7, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 5.0F, 1.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition leg_right2 = leg_right1.addOrReplaceChild("leg_right2", CubeListBuilder.create().texOffs(6, 39).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition leg_right2_r1 = leg_right2.addOrReplaceChild("leg_right2_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_right2_r2 = leg_right2.addOrReplaceChild("leg_right2_r2", CubeListBuilder.create().texOffs(0, 31).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition slime11_r1 = leg_right2.addOrReplaceChild("slime11_r1", CubeListBuilder.create().texOffs(7, 2).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float sin = Mth.sin(ageInTicks * 0.25F) * 0.8F;
		float cos = Mth.cos(ageInTicks * 0.25F) * 0.8F;
		float sinWalk = Mth.sin(limbSwing * 0.6662F) * 2F * limbSwingAmount;
		float sinWalkFaster = Mth.sin(limbSwing * 1.3324F) * 1F * limbSwingAmount;

		spine2.xRot = -0.1309F + cos * 0.0625F;
		hipbone.y = Math.min(10.5F, 10.5F - sinWalkFaster * 2F);
		hipbone.z = 3F - sinWalkFaster * 0.5F;
		hipbone.yRot = 0.0076F - sinWalk * 0.125F;
		spine1.yRot = 0F + sinWalk * 0.125F;

		leg_left1.yRot = 0.0262F + sinWalk * 0.125F;
		leg_left1.xRot = -0.3043F + sinWalk * 0.5F;
		leg_left_lower.xRot = 0.3463F - sinWalk * 0.25F;
		foot_left.xRot = -0.1308F - sinWalk * 0.125F;

		leg_right1.yRot = -0.365F + sinWalk * 0.125F;
		leg_right1.xRot = -0.0922F - sinWalk * 0.25F;
		
		if(entity.isEmerging()) {
			spine1.xRot = convertDegtoRad(27.5F) + convertDegtoRad(-30F) * entity.getSpawningAnimation(partialTick);
			spine6.xRot = convertDegtoRad(45F) + convertDegtoRad(-32.5F) * entity.getSpawningAnimation(partialTick);
			spine7.xRot = convertDegtoRad(20F) + convertDegtoRad(-20F) * entity.getSpawningAnimation(partialTick);
			arm_lower.xRot = convertDegtoRad(-57.5F) + convertDegtoRad(40F) * entity.getSpawningAnimation(partialTick);
			leg_right1.xRot = convertDegtoRad(-122.7847F) + convertDegtoRad(117.5F) * entity.getSpawningAnimation(partialTick);
			leg_right2.xRot = convertDegtoRad(155F) + convertDegtoRad(-155F) * entity.getSpawningAnimation(partialTick);
			leg_left1.xRot = convertDegtoRad(-127.4374F) + convertDegtoRad(110F) * entity.getSpawningAnimation(partialTick);
			leg_left_lower.xRot = convertDegtoRad(159.8423F) + convertDegtoRad(-140F) * entity.getSpawningAnimation(partialTick);
			foot_left.xRot = convertDegtoRad(-39.9929F) + convertDegtoRad(32.5F) * entity.getSpawningAnimation(partialTick);
		}
		
		if (entity.getReloadTimer() > 0) {
			float reloadProgress = Mth.lerp(partialTick, entity.prevReloadTimer / 20.0f, entity.getReloadTimer() / 20.0f);
			arm1.xRot = convertDegtoRad(-24.9164F) + convertDegtoRad(-40F) * (float)Math.sin(reloadProgress * Math.PI);
			arm1.zRot = convertDegtoRad(-29.5336F) + convertDegtoRad(22.5F) * (float)Math.sin(reloadProgress * Math.PI); 
			arm_lower.xRot = convertDegtoRad(-17.5F) + convertDegtoRad(32.5F) * (float)Math.sin(reloadProgress * Math.PI);
			arm_lower.yRot = convertDegtoRad(0F) + convertDegtoRad(47.5F) * (float)Math.sin(reloadProgress * Math.PI);
			arm_lower.zRot = convertDegtoRad(0F) + convertDegtoRad(102.5F) * (float)Math.sin(reloadProgress * Math.PI);
			hand.yRot = convertDegtoRad(0F) + convertDegtoRad(90F) * (float)Math.sin(reloadProgress * Math.PI);
			spine1.yRot = convertDegtoRad(0F) + convertDegtoRad(-20F) * (float)Math.sin(reloadProgress * Math.PI);
			if (entity.getReloadTimer() < 12)
				weapon.visible = false;
			else
				weapon.visible = true;
		}

		if (entity.getAttackTimer() > 0) {
			float attackProgress = Mth.lerp(partialTick, entity.prevAttackTimer / 20.0f, entity.getAttackTimer() / 20.0f);
			arm1.xRot = convertDegtoRad(-24.9164F) + convertDegtoRad(-112.5F) * (float)Math.sin(attackProgress * Math.PI);
			arm1.zRot = convertDegtoRad(-29.5336F) + convertDegtoRad(22.5F) * (float)Math.sin(attackProgress * Math.PI); 
			arm_lower.xRot = convertDegtoRad(-17.5F) + convertDegtoRad(-75F) * (float)Math.sin(attackProgress * Math.PI);
			spine1.xRot = convertDegtoRad(-2.5F) + convertDegtoRad(-15F) * (float)Math.sin(attackProgress * Math.PI);
			spine6.xRot = convertDegtoRad(12.5F) + convertDegtoRad(-2.5F) * (float)Math.sin(attackProgress * Math.PI);
			//spine1.yRot = convertDegtoRad(0F) + convertDegtoRad(20F) * (float)Math.sin(attackProgress * Math.PI);
			if (entity.getAttackTimer() > 17)
				weapon.visible = false;
			else
				weapon.visible = true;
		}
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		hipbone.render(stack, consumer, light, overlay, color);
	}
}