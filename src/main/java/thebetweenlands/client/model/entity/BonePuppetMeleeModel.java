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
import thebetweenlands.common.entity.monster.BonePuppetMelee;

public class BonePuppetMeleeModel<T extends BonePuppetMelee> extends MowzieModelBase<T> {
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
	private final ModelPart shoulder_blade;
	private final ModelPart arm4;
	private final ModelPart arm_lower2;
	private final ModelPart arm5;
	private final ModelPart hand2;
	private final ModelPart finger_i3;
	private final ModelPart finger_i4;
	private final ModelPart fingers3;
	private final ModelPart fingers4;
	private final ModelPart thumb3;
	private final ModelPart thumb4;
	private final ModelPart bone_club;
	private final ModelPart arm6;
	private final ModelPart shoulder_blade2;
	private final ModelPart arm7;
	private final ModelPart arm_lower3;
	private final ModelPart arm8;
	private final ModelPart hand3;
	private final ModelPart finger_i5;
	private final ModelPart finger_i6;
	private final ModelPart fingers5;
	private final ModelPart fingers6;
	private final ModelPart thumb5;
	private final ModelPart thumb6;
	private final ModelPart arm9;
	private final ModelPart ribs4;
	private final ModelPart ribs4_l;
	private final ModelPart ribs4_r;
	private final ModelPart shoulder_blade3;
	private final ModelPart arm10;
	private final ModelPart arm_lower4;
	private final ModelPart arm11;
	private final ModelPart hand4;
	private final ModelPart finger_i7;
	private final ModelPart finger_i8;
	private final ModelPart fingers7;
	private final ModelPart fingers8;
	private final ModelPart thumb7;
	private final ModelPart thumb8;
	private final ModelPart arm12;
	private final ModelPart ribs3;
	private final ModelPart ribs2;
	private final ModelPart ribs1;
	private final ModelPart leg_right1;
	private final ModelPart leg_right_lower;
	private final ModelPart leg_right2;
	private final ModelPart foot_right;
	private final ModelPart toe_right1;
	private final ModelPart toe_right2;
	private final ModelPart toes_right1;
	private final ModelPart toes_right2;
	private final ModelPart leg_right3;
	private final ModelPart leg_left1;
	private final ModelPart leg_left_lower;
	private final ModelPart leg_left2;
	private final ModelPart foot_left;
	private final ModelPart toe_left1;
	private final ModelPart toe_left2;
	private final ModelPart toes_left1;
	private final ModelPart toes_left2;
	private final ModelPart leg_left3;

	public BonePuppetMeleeModel(ModelPart root) {
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
		this.shoulder_blade = this.spine7.getChild("shoulder_blade");
		this.arm4 = this.shoulder_blade.getChild("arm4");
		this.arm_lower2 = this.arm4.getChild("arm_lower2");
		this.arm5 = this.arm_lower2.getChild("arm5");
		this.hand2 = this.arm5.getChild("hand2");
		this.finger_i3 = this.hand2.getChild("finger_i3");
		this.finger_i4 = this.finger_i3.getChild("finger_i4");
		this.fingers3 = this.hand2.getChild("fingers3");
		this.fingers4 = this.fingers3.getChild("fingers4");
		this.thumb3 = this.hand2.getChild("thumb3");
		this.thumb4 = this.thumb3.getChild("thumb4");
		this.bone_club = this.hand2.getChild("bone_club");
		this.arm6 = this.arm_lower2.getChild("arm6");
		this.shoulder_blade2 = this.spine7.getChild("shoulder_blade2");
		this.arm7 = this.shoulder_blade2.getChild("arm7");
		this.arm_lower3 = this.arm7.getChild("arm_lower3");
		this.arm8 = this.arm_lower3.getChild("arm8");
		this.hand3 = this.arm8.getChild("hand3");
		this.finger_i5 = this.hand3.getChild("finger_i5");
		this.finger_i6 = this.finger_i5.getChild("finger_i6");
		this.fingers5 = this.hand3.getChild("fingers5");
		this.fingers6 = this.fingers5.getChild("fingers6");
		this.thumb5 = this.hand3.getChild("thumb5");
		this.thumb6 = this.thumb5.getChild("thumb6");
		this.arm9 = this.arm_lower3.getChild("arm9");
		this.ribs4 = this.spine6.getChild("ribs4");
		this.ribs4_l = this.ribs4.getChild("ribs4_l");
		this.ribs4_r = this.ribs4.getChild("ribs4_r");
		this.shoulder_blade3 = this.spine6.getChild("shoulder_blade3");
		this.arm10 = this.shoulder_blade3.getChild("arm10");
		this.arm_lower4 = this.arm10.getChild("arm_lower4");
		this.arm11 = this.arm_lower4.getChild("arm11");
		this.hand4 = this.arm11.getChild("hand4");
		this.finger_i7 = this.hand4.getChild("finger_i7");
		this.finger_i8 = this.finger_i7.getChild("finger_i8");
		this.fingers7 = this.hand4.getChild("fingers7");
		this.fingers8 = this.fingers7.getChild("fingers8");
		this.thumb7 = this.hand4.getChild("thumb7");
		this.thumb8 = this.thumb7.getChild("thumb8");
		this.arm12 = this.arm_lower4.getChild("arm12");
		this.ribs3 = this.spine5.getChild("ribs3");
		this.ribs2 = this.spine4.getChild("ribs2");
		this.ribs1 = this.spine3.getChild("ribs1");
		this.leg_right1 = this.hipbone.getChild("leg_right1");
		this.leg_right_lower = this.leg_right1.getChild("leg_right_lower");
		this.leg_right2 = this.leg_right_lower.getChild("leg_right2");
		this.foot_right = this.leg_right2.getChild("foot_right");
		this.toe_right1 = this.foot_right.getChild("toe_right1");
		this.toe_right2 = this.toe_right1.getChild("toe_right2");
		this.toes_right1 = this.foot_right.getChild("toes_right1");
		this.toes_right2 = this.toes_right1.getChild("toes_right2");
		this.leg_right3 = this.leg_right_lower.getChild("leg_right3");
		this.leg_left1 = this.hipbone.getChild("leg_left1");
		this.leg_left_lower = this.leg_left1.getChild("leg_left_lower");
		this.leg_left2 = this.leg_left_lower.getChild("leg_left2");
		this.foot_left = this.leg_left2.getChild("foot_left");
		this.toe_left1 = this.foot_left.getChild("toe_left1");
		this.toe_left2 = this.toe_left1.getChild("toe_left2");
		this.toes_left1 = this.foot_left.getChild("toes_left1");
		this.toes_left2 = this.toes_left1.getChild("toes_left2");
		this.leg_left3 = this.leg_left_lower.getChild("leg_left3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hipbone = partdefinition.addOrReplaceChild("hipbone", CubeListBuilder.create().texOffs(0, 29).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.55F, 5.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition slime1_r1 = hipbone.addOrReplaceChild("slime1_r1", CubeListBuilder.create().texOffs(61, 2).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.0F, -3.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition hip_l1 = hipbone.addOrReplaceChild("hip_l1", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition slime1_r2 = hip_l1.addOrReplaceChild("slime1_r2", CubeListBuilder.create().texOffs(62, 3).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -0.05F, -1.0F, 0.0F, -0.6109F, 0.0F));

		PartDefinition hip_l2 = hip_l1.addOrReplaceChild("hip_l2", CubeListBuilder.create().texOffs(0, 40).addBox(-0.98F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition slime1_r3 = hip_l2.addOrReplaceChild("slime1_r3", CubeListBuilder.create().texOffs(61, 1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition hip_r1 = hipbone.addOrReplaceChild("hip_r1", CubeListBuilder.create().texOffs(10, 34).addBox(-1.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition hip_r2 = hip_r1.addOrReplaceChild("hip_r2", CubeListBuilder.create().texOffs(8, 40).addBox(-0.02F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition spine1 = hipbone.addOrReplaceChild("spine1", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition spine1_r1 = spine1.addOrReplaceChild("spine1_r1", CubeListBuilder.create().texOffs(1, 26).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition slime1_r4 = spine1.addOrReplaceChild("slime1_r4", CubeListBuilder.create().texOffs(61, 1).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.05F, 0.0F, -1.0908F, 0.0F, 0.0F));

		PartDefinition spine2 = spine1.addOrReplaceChild("spine2", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition slime1_r5 = spine2.addOrReplaceChild("slime1_r5", CubeListBuilder.create().texOffs(61, 2).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 2.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition spine3 = spine2.addOrReplaceChild("spine3", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(10, -1).addBox(0.0F, -2.0F, 0.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition spine4 = spine3.addOrReplaceChild("spine4", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.01F))
		.texOffs(8, 0).addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition slime1_r6 = spine4.addOrReplaceChild("slime1_r6", CubeListBuilder.create().texOffs(62, 3).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.05F, 0.0F, -2.5744F, 0.0F, 0.0F));

		PartDefinition spine5 = spine4.addOrReplaceChild("spine5", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 3).addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition spine6 = spine5.addOrReplaceChild("spine6", CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.01F))
		.texOffs(12, -3).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition spine7 = spine6.addOrReplaceChild("spine7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, -1).addBox(0.0F, -4.0F, 2.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition spine8_r1 = spine7.addOrReplaceChild("spine8_r1", CubeListBuilder.create().texOffs(1, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition shoulder_blade = spine7.addOrReplaceChild("shoulder_blade", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -0.05F, 1.25F, 0.3802F, 0.0356F, -0.199F));

		PartDefinition slime1_r7 = shoulder_blade.addOrReplaceChild("slime1_r7", CubeListBuilder.create().texOffs(61, 1).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.95F, 0.0F, 0.0F, 0.5672F, 0.0F));

		PartDefinition collar_bone_r1 = shoulder_blade.addOrReplaceChild("collar_bone_r1", CubeListBuilder.create().texOffs(32, 5).addBox(-5.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, -1.0F, 0.0F, -0.9163F, 0.0F));

		PartDefinition arm4 = shoulder_blade.addOrReplaceChild("arm4", CubeListBuilder.create().texOffs(32, 7).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.0F, 0.75F, -1.0F, -1.1937F, -0.2532F, -0.1885F));

		PartDefinition arm1_r1 = arm4.addOrReplaceChild("arm1_r1", CubeListBuilder.create().texOffs(38, 7).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -0.5F, 0.0F, 0.0F, -0.1745F));

		PartDefinition slime1_r8 = arm4.addOrReplaceChild("slime1_r8", CubeListBuilder.create().texOffs(62, 4).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.5F, 0.5F, -1.0036F, 0.0F, 0.0F));

		PartDefinition arm_lower2 = arm4.addOrReplaceChild("arm_lower2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition arm5 = arm_lower2.addOrReplaceChild("arm5", CubeListBuilder.create().texOffs(32, 15).addBox(0.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -0.5F, 0.0F, -0.0873F, 0.0F, 0.2182F));

		PartDefinition hand2 = arm5.addOrReplaceChild("hand2", CubeListBuilder.create().texOffs(32, 21).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 5.0F, -0.5F, 0.0F, 0.0F, 0.0873F));

		PartDefinition finger_i3 = hand2.addOrReplaceChild("finger_i3", CubeListBuilder.create().texOffs(38, 10).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, 0.0F, -0.0436F, 0.0F, 0.4363F));

		PartDefinition finger_i4 = finger_i3.addOrReplaceChild("finger_i4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition finger_i_r1 = finger_i4.addOrReplaceChild("finger_i_r1", CubeListBuilder.create().texOffs(38, 11).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition fingers3 = hand2.addOrReplaceChild("fingers3", CubeListBuilder.create().texOffs(40, 9).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, -0.5F, 0.0001F, -0.0433F, 0.4743F));

		PartDefinition fingers4 = fingers3.addOrReplaceChild("fingers4", CubeListBuilder.create().texOffs(40, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition thumb3 = hand2.addOrReplaceChild("thumb3", CubeListBuilder.create().texOffs(40, 14).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, -0.7239F, 0.3912F, 0.6818F));

		PartDefinition thumb4 = thumb3.addOrReplaceChild("thumb4", CubeListBuilder.create().texOffs(40, 15).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

		PartDefinition bone_club = hand2.addOrReplaceChild("bone_club", CubeListBuilder.create().texOffs(40, 52).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.5F, 2.25F, 0.25F, 3.1416F, 0.0F, -2.7489F));

		PartDefinition bone1b_r1 = bone_club.addOrReplaceChild("bone1b_r1", CubeListBuilder.create().texOffs(52, 57).addBox(0.0F, -1.0F, -4.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.5F, 8.0F, 0.0F, 0.2443F, 0.0F));

		PartDefinition bone1a_r1 = bone_club.addOrReplaceChild("bone1a_r1", CubeListBuilder.create().texOffs(53, 60).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.5F, 8.0F, 0.0F, -0.4363F, 0.0F));

		PartDefinition bone1a_r2 = bone_club.addOrReplaceChild("bone1a_r2", CubeListBuilder.create().texOffs(53, 58).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.5F, -3.0F, 0.0F, -0.3054F, 0.0F));

		PartDefinition slime1_r9 = bone_club.addOrReplaceChild("slime1_r9", CubeListBuilder.create().texOffs(61, 2).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, 7.0F, -0.5672F, 0.0F, 0.0F));

		PartDefinition slime1_r10 = bone_club.addOrReplaceChild("slime1_r10", CubeListBuilder.create().texOffs(61, 2).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.5F, 4.0F, 0.0F, -0.7418F, 0.0F));

		PartDefinition arm6 = arm_lower2.addOrReplaceChild("arm6", CubeListBuilder.create().texOffs(36, 15).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, 0.5F, -0.1308F, 0.0057F, -0.0004F));

		PartDefinition shoulder_blade2 = spine7.addOrReplaceChild("shoulder_blade2", CubeListBuilder.create().texOffs(46, 0).addBox(-3.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-1.0F, -0.05F, 1.25F, 0.3802F, -0.0356F, 0.199F));

		PartDefinition collar_bone_r2 = shoulder_blade2.addOrReplaceChild("collar_bone_r2", CubeListBuilder.create().texOffs(44, 5).addBox(0.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, -1.0F, 0.0F, 0.9163F, 0.0F));

		PartDefinition arm7 = shoulder_blade2.addOrReplaceChild("arm7", CubeListBuilder.create().texOffs(44, 7).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.0F, 0.75F, -1.0F, -1.4589F, 0.0798F, 0.1684F));

		PartDefinition arm1_r2 = arm7.addOrReplaceChild("arm1_r2", CubeListBuilder.create().texOffs(50, 7).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, -0.5F, 0.0F, 0.0F, 0.1745F));

		PartDefinition slime1_r11 = arm7.addOrReplaceChild("slime1_r11", CubeListBuilder.create().texOffs(61, 2).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 3.5F, -1.5F, -0.5672F, 0.0F, 0.0F));

		PartDefinition slime1_r12 = arm7.addOrReplaceChild("slime1_r12", CubeListBuilder.create().texOffs(61, 4).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.5F, -0.5F, 0.0F, 0.5672F, 0.0F));

		PartDefinition arm_lower3 = arm7.addOrReplaceChild("arm_lower3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition arm8 = arm_lower3.addOrReplaceChild("arm8", CubeListBuilder.create().texOffs(48, 15).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -0.5F, 0.0F, -0.0873F, 0.0F, -0.2182F));

		PartDefinition slime1_r13 = arm8.addOrReplaceChild("slime1_r13", CubeListBuilder.create().texOffs(61, 1).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.0F, -1.0F, 0.0F, -0.5672F, 0.0F));

		PartDefinition hand3 = arm8.addOrReplaceChild("hand3", CubeListBuilder.create().texOffs(44, 21).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -0.5F, 0.0F, 0.0F, -0.0873F));

		PartDefinition finger_i5 = hand3.addOrReplaceChild("finger_i5", CubeListBuilder.create().texOffs(50, 10).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 0.0F, -0.125F, 0.056F, 0.211F));

		PartDefinition finger_i6 = finger_i5.addOrReplaceChild("finger_i6", CubeListBuilder.create().texOffs(50, 11).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition fingers5 = hand3.addOrReplaceChild("fingers5", CubeListBuilder.create().texOffs(52, 9).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, -0.5F, 0.218F, 0.0483F, -0.1213F));

		PartDefinition fingers6 = fingers5.addOrReplaceChild("fingers6", CubeListBuilder.create().texOffs(52, 10).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition thumb5 = hand3.addOrReplaceChild("thumb5", CubeListBuilder.create().texOffs(52, 14).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -1.0F, -0.3054F, 0.0F, -0.3054F));

		PartDefinition thumb6 = thumb5.addOrReplaceChild("thumb6", CubeListBuilder.create().texOffs(52, 15).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition arm9 = arm_lower3.addOrReplaceChild("arm9", CubeListBuilder.create().texOffs(44, 15).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, 0.5F, -0.1308F, -0.0057F, 0.0004F));

		PartDefinition ribs4 = spine6.addOrReplaceChild("ribs4", CubeListBuilder.create().texOffs(8, 4).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.0F));

		PartDefinition slime1_r14 = ribs4.addOrReplaceChild("slime1_r14", CubeListBuilder.create().texOffs(61, 1).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, -1.8762F, 0.0F, 0.0F));

		PartDefinition ribs4_l = ribs4.addOrReplaceChild("ribs4_l", CubeListBuilder.create().texOffs(26, -4).addBox(0.0F, -3.0F, -4.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -4.0F, 0.0F, 1.0908F, 0.0F));

		PartDefinition slime1_r15 = ribs4_l.addOrReplaceChild("slime1_r15", CubeListBuilder.create().texOffs(61, 3).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, 0.48F, 0.0F, 1.5708F));

		PartDefinition slime1_r16 = ribs4_l.addOrReplaceChild("slime1_r16", CubeListBuilder.create().texOffs(61, 3).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.48F, 0.0F, 1.5708F));

		PartDefinition ribs4_r = ribs4.addOrReplaceChild("ribs4_r", CubeListBuilder.create().texOffs(18, -4).addBox(0.0F, -3.0F, -4.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -4.0F, 0.0F, -1.0908F, 0.0F));

		PartDefinition shoulder_blade3 = spine6.addOrReplaceChild("shoulder_blade3", CubeListBuilder.create().texOffs(32, 26).addBox(-3.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -0.05F, -0.75F, -0.4074F, 0.6625F, -0.0722F));

		PartDefinition arm10 = shoulder_blade3.addOrReplaceChild("arm10", CubeListBuilder.create().texOffs(32, 31).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.0F, 0.75F, -1.0F, -1.0534F, 0.3702F, -0.1794F));

		PartDefinition arm1_r3 = arm10.addOrReplaceChild("arm1_r3", CubeListBuilder.create().texOffs(38, 31).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, -0.5F, 0.0F, 0.0F, 0.1745F));

		PartDefinition slime1_r17 = arm10.addOrReplaceChild("slime1_r17", CubeListBuilder.create().texOffs(61, 1).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 4.5F, 0.5F, 0.0F, 0.5672F, 0.0F));

		PartDefinition arm_lower4 = arm10.addOrReplaceChild("arm_lower4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.5F, 0.0F, -0.9926F, -0.1836F, -0.1186F));

		PartDefinition arm11 = arm_lower4.addOrReplaceChild("arm11", CubeListBuilder.create().texOffs(32, 39).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -0.5F, 0.0F, -0.0873F, 0.0F, -0.2182F));

		PartDefinition slime1_r18 = arm11.addOrReplaceChild("slime1_r18", CubeListBuilder.create().texOffs(61, 4).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.0F, -1.0F, 0.0F, 0.0F, 0.6109F));

		PartDefinition hand4 = arm11.addOrReplaceChild("hand4", CubeListBuilder.create().texOffs(32, 45).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -0.5F, 0.0F, 0.0F, -0.0873F));

		PartDefinition finger_i7 = hand4.addOrReplaceChild("finger_i7", CubeListBuilder.create().texOffs(38, 34).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 0.0F, -0.125F, 0.056F, 0.211F));

		PartDefinition finger_i8 = finger_i7.addOrReplaceChild("finger_i8", CubeListBuilder.create().texOffs(38, 35).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition fingers7 = hand4.addOrReplaceChild("fingers7", CubeListBuilder.create().texOffs(40, 33).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, -0.5F, 0.218F, 0.0483F, -0.1213F));

		PartDefinition fingers8 = fingers7.addOrReplaceChild("fingers8", CubeListBuilder.create().texOffs(40, 34).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition thumb7 = hand4.addOrReplaceChild("thumb7", CubeListBuilder.create().texOffs(40, 38).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -1.0F, -0.3054F, 0.0F, -0.3054F));

		PartDefinition thumb8 = thumb7.addOrReplaceChild("thumb8", CubeListBuilder.create().texOffs(40, 39).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition arm12 = arm_lower4.addOrReplaceChild("arm12", CubeListBuilder.create().texOffs(36, 39).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, 0.5F, -0.1308F, -0.0057F, 0.0004F));

		PartDefinition ribs3 = spine5.addOrReplaceChild("ribs3", CubeListBuilder.create().texOffs(8, 13).addBox(-4.0F, -2.75F, -3.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, -1.0F, -1.0F));

		PartDefinition slime1_r19 = ribs3.addOrReplaceChild("slime1_r19", CubeListBuilder.create().texOffs(61, 2).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.75F, 0.0F, 0.0F, -0.7418F, 0.0F));

		PartDefinition ribs2 = spine4.addOrReplaceChild("ribs2", CubeListBuilder.create().texOffs(8, 19).addBox(-4.0F, -2.0F, -2.0F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -2.0F, -1.0F));

		PartDefinition ribs1 = spine3.addOrReplaceChild("ribs1", CubeListBuilder.create().texOffs(8, 24).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.0F));

		PartDefinition leg_right1 = hipbone.addOrReplaceChild("leg_right1", CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.0F, 0.5F, -1.0F, -0.5233F, 0.0131F, -0.0893F));

		PartDefinition leg_right1_r1 = leg_right1.addOrReplaceChild("leg_right1_r1", CubeListBuilder.create().texOffs(6, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition slime1_r20 = leg_right1.addOrReplaceChild("slime1_r20", CubeListBuilder.create().texOffs(62, 1).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.95F, 0.0F, 0.0F, -0.5236F, 0.0F));

		PartDefinition leg_right_lower = leg_right1.addOrReplaceChild("leg_right_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, 6.0F, -0.5F, 1.0881F, -0.0447F, 0.1231F));

		PartDefinition leg_right2 = leg_right_lower.addOrReplaceChild("leg_right2", CubeListBuilder.create().texOffs(0, 52).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition slime1_r21 = leg_right2.addOrReplaceChild("slime1_r21", CubeListBuilder.create().texOffs(61, 2).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.5F, 0.0F, 0.0F, 0.6545F, 0.0F));

		PartDefinition foot_right = leg_right2.addOrReplaceChild("foot_right", CubeListBuilder.create().texOffs(0, 60).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 6.5F, 0.5F, -0.3926F, 0.0057F, 0.0433F));

		PartDefinition heel_r1 = foot_right.addOrReplaceChild("heel_r1", CubeListBuilder.create().texOffs(7, 60).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, 1.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition toe_right1 = foot_right.addOrReplaceChild("toe_right1", CubeListBuilder.create().texOffs(5, 47).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.079F, -0.1775F, 0.0288F));

		PartDefinition toe_right2 = toe_right1.addOrReplaceChild("toe_right2", CubeListBuilder.create().texOffs(4, 48).addBox(0.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toes_right1 = foot_right.addOrReplaceChild("toes_right1", CubeListBuilder.create().texOffs(7, 47).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, 0.1F, -2.0F, 0.0704F, 0.09F, -0.0381F));

		PartDefinition toes_right2 = toes_right1.addOrReplaceChild("toes_right2", CubeListBuilder.create().texOffs(6, 48).addBox(-2.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition leg_right3 = leg_right_lower.addOrReplaceChild("leg_right3", CubeListBuilder.create().texOffs(4, 52).addBox(0.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0436F, -0.0019F, 0.1745F));

		PartDefinition slime1_r22 = leg_right3.addOrReplaceChild("slime1_r22", CubeListBuilder.create().texOffs(61, 3).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition leg_left1 = hipbone.addOrReplaceChild("leg_left1", CubeListBuilder.create().texOffs(13, 44).addBox(0.0F, 0.0F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.0F, 0.5F, -1.0F, -0.5233F, -0.0131F, 0.0893F));

		PartDefinition leg_left1_r1 = leg_left1.addOrReplaceChild("leg_left1_r1", CubeListBuilder.create().texOffs(19, 44).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition leg_left_lower = leg_left1.addOrReplaceChild("leg_left_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 6.0F, -0.5F, 1.0881F, 0.0447F, -0.1231F));

		PartDefinition leg_left2 = leg_left_lower.addOrReplaceChild("leg_left2", CubeListBuilder.create().texOffs(13, 52).addBox(0.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition foot_left = leg_left2.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(13, 60).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 6.5F, 0.5F, -0.3926F, -0.0057F, -0.0433F));

		PartDefinition heel_r2 = foot_left.addOrReplaceChild("heel_r2", CubeListBuilder.create().texOffs(20, 60).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, 1.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition slime1_r23 = foot_left.addOrReplaceChild("slime1_r23", CubeListBuilder.create().texOffs(61, 1).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.6109F));

		PartDefinition toe_left1 = foot_left.addOrReplaceChild("toe_left1", CubeListBuilder.create().texOffs(18, 47).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.079F, 0.1775F, -0.0288F));

		PartDefinition toe_left2 = toe_left1.addOrReplaceChild("toe_left2", CubeListBuilder.create().texOffs(17, 48).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toes_left1 = foot_left.addOrReplaceChild("toes_left1", CubeListBuilder.create().texOffs(20, 47).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 0.1F, -2.0F, 0.0704F, -0.09F, 0.0381F));

		PartDefinition toes_left2 = toes_left1.addOrReplaceChild("toes_left2", CubeListBuilder.create().texOffs(19, 48).addBox(0.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition leg_left3 = leg_left_lower.addOrReplaceChild("leg_left3", CubeListBuilder.create().texOffs(17, 52).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0436F, 0.0019F, -0.1745F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float sin = Mth.sin(ageInTicks * 0.25F) * 0.8F;
		float cos = Mth.cos(ageInTicks * 0.25F) * 0.8F;
		float sinWalk = Mth.sin(limbSwing * 0.6662F) * 2F * limbSwingAmount;
		float sinWalkFaster = Mth.sin(limbSwing * 1.3324F) * 1F * limbSwingAmount;

		spine2.xRot = -0.1309F + cos * 0.0625F;
		arm10.yRot = 0.3702F + cos * 0.125F; // back right arm
		arm4.yRot = -0.2532F - sin * 0.125F; // left arm
		arm7.yRot = 0.0798F + sin * 0.125F; // front right arm

		hipbone.y = Math.min(12.55F, 12.55F - sinWalkFaster * 2F);
		hipbone.z = 3F - sinWalkFaster * 0.5F;
		hipbone.yRot = 0F - sinWalk * 0.125F;
		spine1.yRot = 0F + sinWalk * 0.125F;

		leg_left1.yRot = -0.0131F + sinWalk * 0.125F;
		leg_left1.xRot = -0.5233F + sinWalk * 0.5F;
		leg_left_lower.xRot = 1.0881F - sinWalk * 0.25F;
		foot_left.xRot = -0.3926F - sinWalk * 0.125F;

		leg_right1.yRot = 0.0131F + sinWalk * 0.125F;
		leg_right1.xRot = -0.5233F - sinWalk * 0.5F;
		leg_right_lower.xRot = 1.0881F + sinWalk * 0.25F;
		foot_right.xRot = -0.3926F + sinWalk * 0.125F;

		if (entity.getAttackTimer() > 0) {
			float attackProgress = (float)entity.getAttackTimer() / 20.0f + ((float)entity.getAttackTimer() / 20.0f - (float)entity.prevAttackTimer / 20F) * partialTick;
		    arm4.xRot = -1.1937F - 1F * (float)Math.sin(attackProgress * Math.PI);
		    arm4.yRot = -0.2532F - 1F * (float)Math.sin(attackProgress * Math.PI);
		    if (entity.getAttackTimer() <= 5) 
		    	spine1.yRot = 0F - 1F * (float)Math.sin(attackProgress * Math.PI);
		    if (entity.getAttackTimer() > 5 && entity.getAttackTimer() <= 10)
		    	spine1.yRot = -0.70710677F + 1F * (float)Math.sin(attackProgress * Math.PI);
		    if (entity.getAttackTimer() > 10)
		    	spine1.yRot = 0F + 1F * (float)Math.sin(attackProgress * Math.PI);

		    arm_lower2.yRot = 0F - 0.5F * (float)Math.sin(attackProgress * Math.PI);
		    hand2.yRot = 0F - 1F * (float)Math.sin(attackProgress * Math.PI);
		    hand2.zRot = 0.0873F + 0.5F * (float)Math.sin(attackProgress * Math.PI);
		    arm10.yRot = 0.3702F + 1F * (float)Math.sin(attackProgress * Math.PI);
			arm7.yRot = 0.0798F + 1F * (float)Math.sin(attackProgress * Math.PI);
		}

	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		hipbone.render(stack, consumer, light, overlay, color);
	}
}