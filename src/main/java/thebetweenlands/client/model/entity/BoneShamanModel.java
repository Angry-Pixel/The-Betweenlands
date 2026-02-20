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
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.BoneShaman;

public class BoneShamanModel<T extends BoneShaman> extends MowzieModelBase<T> {

	private final ModelPart hipbone;
	private final ModelPart cloth_back_1a;
	private final ModelPart cloth_back_1b;
	private final ModelPart spine1a;
	private final ModelPart spine1b;
	private final ModelPart spine1c;
	private final ModelPart ribs3a;
	private final ModelPart spine1d;
	private final ModelPart ribs2a;
	private final ModelPart ribs2b_l;
	private final ModelPart ribs2b_r;
	private final ModelPart spine1e;
	private final ModelPart ribs1a;
	private final ModelPart ribs1b_r;
	private final ModelPart ribs1b_l;
	private final ModelPart neck;
	private final ModelPart head_base;
	private final ModelPart head_top;
	private final ModelPart jaw;
	private final ModelPart shoulder_blade_left;
	private final ModelPart arm1;
	private final ModelPart arm_lower;
	private final ModelPart arm2;
	private final ModelPart hand;
	private final ModelPart finger_i;
	private final ModelPart finger_i2;
	private final ModelPart fingers;
	private final ModelPart fingers2;
	private final ModelPart thumb;
	private final ModelPart thumb2;
	private final ModelPart arm3;
	private final ModelPart cloth_up_left1a;
	private final ModelPart cloth_up_left1b;
	private final ModelPart shoulder_blade_right;
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
	private final ModelPart staff1a;
	private final ModelPart staff1d;
	private final ModelPart staff1e;
	private final ModelPart staff1f;
	private final ModelPart staff1g;
	private final ModelPart staff1b;
	private final ModelPart staff1c;
	private final ModelPart staff_t_a;
	private final ModelPart staff_t_b;
	private final ModelPart staff_t_c;
	private final ModelPart staff_t_d;
	private final ModelPart staff_t_e1;
	private final ModelPart staff_t_f1;
	private final ModelPart staff_t_e2;
	private final ModelPart staff_t_f2;
	private final ModelPart staff_t_g;
	private final ModelPart staff_t_h;
	private final ModelPart staff_t_i;
	private final ModelPart staff_t_j;
	private final ModelPart staff_t_k;
	private final ModelPart arm6;
	private final ModelPart cloth_up_right1a;
	private final ModelPart cloth_up_right1b;
	private final ModelPart hip_l1;
	private final ModelPart hip_l2;
	private final ModelPart cloth_left_1a;
	private final ModelPart cloth_left_1b;
	private final ModelPart cloth_left_1c;
	private final ModelPart hip_r1;
	private final ModelPart hip_r2;
	private final ModelPart cloth_right_1a;
	private final ModelPart cloth_right_1b;
	private final ModelPart cloth_right_1c;
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
	private final ModelPart leg_right_lower;
	private final ModelPart leg_right2;
	private final ModelPart foot_right;
	private final ModelPart toe_right1;
	private final ModelPart toe_right2;
	private final ModelPart toes_right1;
	private final ModelPart toes_right2;
	private final ModelPart leg_right3;

	public BoneShamanModel(ModelPart root) {
		super(root, RenderType::entityCutoutNoCull);
		this.hipbone = root.getChild("hipbone");
		this.cloth_back_1a = this.hipbone.getChild("cloth_back_1a");
		this.cloth_back_1b = this.cloth_back_1a.getChild("cloth_back_1b");
		this.spine1a = this.hipbone.getChild("spine1a");
		this.spine1b = this.spine1a.getChild("spine1b");
		this.spine1c = this.spine1b.getChild("spine1c");
		this.ribs3a = this.spine1c.getChild("ribs3a");
		this.spine1d = this.spine1c.getChild("spine1d");
		this.ribs2a = this.spine1d.getChild("ribs2a");
		this.ribs2b_l = this.ribs2a.getChild("ribs2b_l");
		this.ribs2b_r = this.ribs2a.getChild("ribs2b_r");
		this.spine1e = this.spine1d.getChild("spine1e");
		this.ribs1a = this.spine1e.getChild("ribs1a");
		this.ribs1b_r = this.ribs1a.getChild("ribs1b_r");
		this.ribs1b_l = this.ribs1a.getChild("ribs1b_l");
		this.neck = this.spine1e.getChild("neck");
		this.head_base = this.neck.getChild("head_base");
		this.head_top = this.head_base.getChild("head_top");
		this.jaw = this.head_base.getChild("jaw");
		this.shoulder_blade_left = this.spine1e.getChild("shoulder_blade_left");
		this.arm1 = this.shoulder_blade_left.getChild("arm1");
		this.arm_lower = this.arm1.getChild("arm_lower");
		this.arm2 = this.arm_lower.getChild("arm2");
		this.hand = this.arm2.getChild("hand");
		this.finger_i = this.hand.getChild("finger_i");
		this.finger_i2 = this.finger_i.getChild("finger_i2");
		this.fingers = this.hand.getChild("fingers");
		this.fingers2 = this.fingers.getChild("fingers2");
		this.thumb = this.hand.getChild("thumb");
		this.thumb2 = this.thumb.getChild("thumb2");
		this.arm3 = this.arm_lower.getChild("arm3");
		this.cloth_up_left1a = this.shoulder_blade_left.getChild("cloth_up_left1a");
		this.cloth_up_left1b = this.cloth_up_left1a.getChild("cloth_up_left1b");
		this.shoulder_blade_right = this.spine1e.getChild("shoulder_blade_right");
		this.arm4 = this.shoulder_blade_right.getChild("arm4");
		this.arm_lower2 = this.arm4.getChild("arm_lower2");
		this.arm5 = this.arm_lower2.getChild("arm5");
		this.hand2 = this.arm5.getChild("hand2");
		this.finger_i3 = this.hand2.getChild("finger_i3");
		this.finger_i4 = this.finger_i3.getChild("finger_i4");
		this.fingers3 = this.hand2.getChild("fingers3");
		this.fingers4 = this.fingers3.getChild("fingers4");
		this.thumb3 = this.hand2.getChild("thumb3");
		this.thumb4 = this.thumb3.getChild("thumb4");
		this.staff1a = this.hand2.getChild("staff1a");
		this.staff1d = this.staff1a.getChild("staff1d");
		this.staff1e = this.staff1d.getChild("staff1e");
		this.staff1f = this.staff1e.getChild("staff1f");
		this.staff1g = this.staff1d.getChild("staff1g");
		this.staff1b = this.staff1a.getChild("staff1b");
		this.staff1c = this.staff1b.getChild("staff1c");
		this.staff_t_a = this.staff1c.getChild("staff_t_a");
		this.staff_t_b = this.staff_t_a.getChild("staff_t_b");
		this.staff_t_c = this.staff_t_b.getChild("staff_t_c");
		this.staff_t_d = this.staff_t_c.getChild("staff_t_d");
		this.staff_t_e1 = this.staff_t_d.getChild("staff_t_e1");
		this.staff_t_f1 = this.staff_t_e1.getChild("staff_t_f1");
		this.staff_t_e2 = this.staff_t_d.getChild("staff_t_e2");
		this.staff_t_f2 = this.staff_t_e2.getChild("staff_t_f2");
		this.staff_t_g = this.staff1c.getChild("staff_t_g");
		this.staff_t_h = this.staff_t_g.getChild("staff_t_h");
		this.staff_t_i = this.staff_t_h.getChild("staff_t_i");
		this.staff_t_j = this.staff_t_i.getChild("staff_t_j");
		this.staff_t_k = this.staff_t_j.getChild("staff_t_k");
		this.arm6 = this.arm_lower2.getChild("arm6");
		this.cloth_up_right1a = this.shoulder_blade_right.getChild("cloth_up_right1a");
		this.cloth_up_right1b = this.cloth_up_right1a.getChild("cloth_up_right1b");
		this.hip_l1 = this.hipbone.getChild("hip_l1");
		this.hip_l2 = this.hip_l1.getChild("hip_l2");
		this.cloth_left_1a = this.hip_l1.getChild("cloth_left_1a");
		this.cloth_left_1b = this.cloth_left_1a.getChild("cloth_left_1b");
		this.cloth_left_1c = this.cloth_left_1b.getChild("cloth_left_1c");
		this.hip_r1 = this.hipbone.getChild("hip_r1");
		this.hip_r2 = this.hip_r1.getChild("hip_r2");
		this.cloth_right_1a = this.hip_r1.getChild("cloth_right_1a");
		this.cloth_right_1b = this.cloth_right_1a.getChild("cloth_right_1b");
		this.cloth_right_1c = this.cloth_right_1b.getChild("cloth_right_1c");
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
		this.leg_right_lower = this.leg_right1.getChild("leg_right_lower");
		this.leg_right2 = this.leg_right_lower.getChild("leg_right2");
		this.foot_right = this.leg_right2.getChild("foot_right");
		this.toe_right1 = this.foot_right.getChild("toe_right1");
		this.toe_right2 = this.toe_right1.getChild("toe_right2");
		this.toes_right1 = this.foot_right.getChild("toes_right1");
		this.toes_right2 = this.toes_right1.getChild("toes_right2");
		this.leg_right3 = this.leg_right_lower.getChild("leg_right3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition hipbone = partdefinition.addOrReplaceChild("hipbone", CubeListBuilder.create().texOffs(0, 39).addBox(-3.5F, -1.0F, -3.0F, 7.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, 0.0873F, 0.0F, -0.0873F));

		PartDefinition cloth_back_1a = hipbone.addOrReplaceChild("cloth_back_1a", CubeListBuilder.create().texOffs(0, 110).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cloth_back_1b = cloth_back_1a.addOrReplaceChild("cloth_back_1b", CubeListBuilder.create().texOffs(0, 116).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition spine1a = hipbone.addOrReplaceChild("spine1a", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.01F))
		.texOffs(1, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition slime1_r1 = spine1a.addOrReplaceChild("slime1_r1", CubeListBuilder.create().texOffs(33, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.7418F, 0.0F, 0.0F));

		PartDefinition spine1b = spine1a.addOrReplaceChild("spine1b", CubeListBuilder.create().texOffs(0, 31).addBox(0.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -4.0F, -1.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition spine1c = spine1b.addOrReplaceChild("spine1c", CubeListBuilder.create().texOffs(0, 28).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -1.0F, 1.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition slime8_r1 = spine1c.addOrReplaceChild("slime8_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.6981F, 0.0F));

		PartDefinition ribs3a = spine1c.addOrReplaceChild("ribs3a", CubeListBuilder.create().texOffs(8, 31).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition spine1d = spine1c.addOrReplaceChild("spine1d", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition ribs2a = spine1d.addOrReplaceChild("ribs2a", CubeListBuilder.create().texOffs(8, 24).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition rib_slime2_l_r1 = ribs2a.addOrReplaceChild("rib_slime2_l_r1", CubeListBuilder.create().texOffs(30, 22).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition rib_slime2_r_r1 = ribs2a.addOrReplaceChild("rib_slime2_r_r1", CubeListBuilder.create().texOffs(36, 22).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition slime10_r1 = ribs2a.addOrReplaceChild("slime10_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.0F, -2.0F, 0.0F, 2.1817F, 0.0F));

		PartDefinition ribs2b_l = ribs2a.addOrReplaceChild("ribs2b_l", CubeListBuilder.create().texOffs(30, 26).addBox(-4.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -3.0F, 0.0F, -0.4363F, 0.0F));

		PartDefinition slime2s_r1 = ribs2b_l.addOrReplaceChild("slime2s_r1", CubeListBuilder.create().texOffs(33, -1).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition slime1_r2 = ribs2b_l.addOrReplaceChild("slime1_r2", CubeListBuilder.create().texOffs(34, -1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition rib_slime1_l_r1 = ribs2b_l.addOrReplaceChild("rib_slime1_l_r1", CubeListBuilder.create().texOffs(30, 29).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition ribs2b_r = ribs2a.addOrReplaceChild("ribs2b_r", CubeListBuilder.create().texOffs(38, 26).addBox(0.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -3.0F, 0.0F, 0.4363F, 0.0F));

		PartDefinition rib_slime1_r_r1 = ribs2b_r.addOrReplaceChild("rib_slime1_r_r1", CubeListBuilder.create().texOffs(38, 29).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition slime9_r1 = ribs2b_r.addOrReplaceChild("slime9_r1", CubeListBuilder.create().texOffs(33, -1).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 0.0F, -0.6981F, 0.0F, 0.0F));

		PartDefinition spine1e = spine1d.addOrReplaceChild("spine1e", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -4.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition ribs1a = spine1e.addOrReplaceChild("ribs1a", CubeListBuilder.create().texOffs(8, 17).addBox(-4.5F, -4.0F, -3.0F, 9.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition ribs1b_r = ribs1a.addOrReplaceChild("ribs1b_r", CubeListBuilder.create().texOffs(32, 20).addBox(0.0F, -4.0F, 0.0F, 4.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 0.0F, -3.0F, 0.0F, 0.48F, 0.0F));

		PartDefinition ribs1b_l = ribs1a.addOrReplaceChild("ribs1b_l", CubeListBuilder.create().texOffs(40, 20).addBox(-4.0F, -4.0F, 0.0F, 4.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 0.0F, -3.0F, 0.0F, -0.48F, 0.0F));

		PartDefinition ribs1c_r1 = ribs1b_l.addOrReplaceChild("ribs1c_r1", CubeListBuilder.create().texOffs(48, 20).addBox(-2.0F, -4.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.95F, 0.0F, 0.0F, 0.0F, 0.48F, 0.0F));

		PartDefinition slime3_r1 = ribs1b_l.addOrReplaceChild("slime3_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition neck = spine1e.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition head_base = neck.addOrReplaceChild("head_base", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, -2.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition head_top = head_base.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -7.0F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(11, 11).addBox(1.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(17, 11).addBox(-3.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 3).addBox(-3.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.5F));

		PartDefinition jaw = head_base.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(23, 11).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.5F, 0.2618F, 0.0F, 0.0F));

		PartDefinition shoulder_blade_left = spine1e.addOrReplaceChild("shoulder_blade_left", CubeListBuilder.create().texOffs(15, 79).addBox(0.0F, -1.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, -0.1745F, -0.1309F, 0.0F));

		PartDefinition slime4_r1 = shoulder_blade_left.addOrReplaceChild("slime4_r1", CubeListBuilder.create().texOffs(34, -1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.0F, -1.0F, 0.7399F, 0.5148F, 1.0762F));

		PartDefinition collar_bone_r1 = shoulder_blade_left.addOrReplaceChild("collar_bone_r1", CubeListBuilder.create().texOffs(13, 84).addBox(-5.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -1.0F, 0.0F, -0.6981F, 0.0F));

		PartDefinition arm1 = shoulder_blade_left.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(19, 86).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.143F));

		PartDefinition arm1_r1 = arm1.addOrReplaceChild("arm1_r1", CubeListBuilder.create().texOffs(13, 86).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -0.5F, 0.0F, 0.0F, -0.1745F));

		PartDefinition slime5_r1 = arm1.addOrReplaceChild("slime5_r1", CubeListBuilder.create().texOffs(33, 3).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 4.5F, -1.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition arm_lower = arm1.addOrReplaceChild("arm_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.5F, 0.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition arm2 = arm_lower.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(21, 96).addBox(0.0F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -0.5F, 0.0F, -0.0869F, -0.0076F, 0.1312F));

		PartDefinition slime6_r1 = arm2.addOrReplaceChild("slime6_r1", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, -1.0F, 0.0F, -0.7418F, 0.0F));

		PartDefinition hand = arm2.addOrReplaceChild("hand", CubeListBuilder.create().texOffs(19, 105).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 8.0F, -0.5F, 0.0F, 0.0F, 0.0873F));

		PartDefinition finger_i = hand.addOrReplaceChild("finger_i", CubeListBuilder.create().texOffs(13, 89).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, 0.0F, -0.125F, -0.056F, -0.211F));

		PartDefinition finger_i2 = finger_i.addOrReplaceChild("finger_i2", CubeListBuilder.create().texOffs(13, 90).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition fingers = hand.addOrReplaceChild("fingers", CubeListBuilder.create().texOffs(15, 88).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, -0.5F, 0.218F, -0.0483F, 0.1213F));

		PartDefinition fingers2 = fingers.addOrReplaceChild("fingers2", CubeListBuilder.create().texOffs(15, 89).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition thumb = hand.addOrReplaceChild("thumb", CubeListBuilder.create().texOffs(17, 93).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, -1.0F, -0.3054F, 0.0F, 0.3054F));

		PartDefinition thumb2 = thumb.addOrReplaceChild("thumb2", CubeListBuilder.create().texOffs(17, 94).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition arm3 = arm_lower.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(17, 96).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, 0.5F, -0.1308F, 0.0057F, -0.0004F));

		PartDefinition slime7_r1 = arm3.addOrReplaceChild("slime7_r1", CubeListBuilder.create().texOffs(33, 2).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.0F, 0.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition cloth_up_left1a = shoulder_blade_left.addOrReplaceChild("cloth_up_left1a", CubeListBuilder.create().texOffs(26, 82).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cloth_up_left1b = cloth_up_left1a.addOrReplaceChild("cloth_up_left1b", CubeListBuilder.create().texOffs(26, 88).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition shoulder_blade_right = spine1e.addOrReplaceChild("shoulder_blade_right", CubeListBuilder.create().texOffs(0, 79).addBox(-4.0F, -1.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-1.0F, -3.0F, 0.0F, -0.1745F, 0.1309F, 0.0F));

		PartDefinition collar_bone_r2 = shoulder_blade_right.addOrReplaceChild("collar_bone_r2", CubeListBuilder.create().texOffs(0, 84).addBox(0.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -1.0F, 0.0F, 0.6981F, 0.0F));

		PartDefinition arm4 = shoulder_blade_right.addOrReplaceChild("arm4", CubeListBuilder.create().texOffs(0, 86).addBox(-0.5F, -0.5F, -1.5F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -1.0F, -0.0452F, 0.2615F, 0.1314F));

		PartDefinition arm1_r2 = arm4.addOrReplaceChild("arm1_r2", CubeListBuilder.create().texOffs(6, 86).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, -0.5F, 0.0F, 0.0F, 0.1745F));

		PartDefinition arm_lower2 = arm4.addOrReplaceChild("arm_lower2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 7.5F, 0.0F, -0.6545F, 0.0F, 0.0F));

		PartDefinition arm5 = arm_lower2.addOrReplaceChild("arm5", CubeListBuilder.create().texOffs(0, 96).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -0.5F, 0.0F, -0.0869F, 0.0076F, -0.1312F));

		PartDefinition slime11_r1 = arm5.addOrReplaceChild("slime11_r1", CubeListBuilder.create().texOffs(34, 2).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.0F, -1.0F, 0.0F, 0.9163F, 0.0F));

		PartDefinition slime12_r1 = arm5.addOrReplaceChild("slime12_r1", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 7.0F, 0.0F, 0.0F, 0.0F, -0.6545F));

		PartDefinition hand2 = arm5.addOrReplaceChild("hand2", CubeListBuilder.create().texOffs(0, 105).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 8.0F, -0.5F, -0.3491F, 0.0F, -0.0873F));

		PartDefinition finger_i3 = hand2.addOrReplaceChild("finger_i3", CubeListBuilder.create().texOffs(10, 89).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, 0.0F, -0.3421F, -0.0623F, -0.1176F));

		PartDefinition finger_i4 = finger_i3.addOrReplaceChild("finger_i4", CubeListBuilder.create().texOffs(10, 90).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition fingers3 = hand2.addOrReplaceChild("fingers3", CubeListBuilder.create().texOffs(6, 88).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, -0.5F, -0.0432F, -0.0359F, -0.1516F));

		PartDefinition fingers4 = fingers3.addOrReplaceChild("fingers4", CubeListBuilder.create().texOffs(6, 89).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition thumb3 = hand2.addOrReplaceChild("thumb3", CubeListBuilder.create().texOffs(6, 93).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -0.25F, -3.0915F, -1.2098F, 1.3934F));

		PartDefinition thumb4 = thumb3.addOrReplaceChild("thumb4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.4835F, 0.0F, 0.0F));

		PartDefinition thumb_r1 = thumb4.addOrReplaceChild("thumb_r1", CubeListBuilder.create().texOffs(6, 94).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition staff1a = hand2.addOrReplaceChild("staff1a", CubeListBuilder.create().texOffs(26, 55).addBox(0.0F, -4.0F, -2.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 2.25F, 0.5F, 1.2489F, -0.0485F, -0.399F));

		PartDefinition slime18_r1 = staff1a.addOrReplaceChild("slime18_r1", CubeListBuilder.create().texOffs(33, 3).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 5.0F, -2.0F, -0.8727F, 0.0F, 0.0F));

		PartDefinition slime17_r1 = staff1a.addOrReplaceChild("slime17_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -2.0F, 0.0F, 2.3562F, 0.0F));

		PartDefinition slime16_r1 = staff1a.addOrReplaceChild("slime16_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 5.0F, 0.0F, 0.0F, -0.48F, 0.0F));

		PartDefinition slime15_r1 = staff1a.addOrReplaceChild("slime15_r1", CubeListBuilder.create().texOffs(33, 1).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition staff1d = staff1a.addOrReplaceChild("staff1d", CubeListBuilder.create().texOffs(26, 69).addBox(0.0F, 0.0F, -2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition staff1e = staff1d.addOrReplaceChild("staff1e", CubeListBuilder.create().texOffs(27, 77).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -1.0F));

		PartDefinition staff1f = staff1e.addOrReplaceChild("staff1f", CubeListBuilder.create().texOffs(27, 79).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition staff1g = staff1d.addOrReplaceChild("staff1g", CubeListBuilder.create().texOffs(34, 77).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition staff1b = staff1a.addOrReplaceChild("staff1b", CubeListBuilder.create().texOffs(26, 48).addBox(0.0F, -7.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition slime13_r1 = staff1b.addOrReplaceChild("slime13_r1", CubeListBuilder.create().texOffs(34, -1).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.9163F, 0.0F));

		PartDefinition slime14_r1 = staff1b.addOrReplaceChild("slime14_r1", CubeListBuilder.create().texOffs(32, 3).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 2.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition staff1c = staff1b.addOrReplaceChild("staff1c", CubeListBuilder.create().texOffs(26, 41).addBox(0.0F, -7.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition nub_r1 = staff1c.addOrReplaceChild("nub_r1", CubeListBuilder.create().texOffs(35, 72).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 2.0F, 0.7586F, 0.3706F, 0.7825F));

		PartDefinition staff_slime1_r1 = staff1c.addOrReplaceChild("staff_slime1_r1", CubeListBuilder.create().texOffs(35, 41).addBox(0.0F, -9.0F, -2.0F, 0.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition staff_slime4_r1 = staff1c.addOrReplaceChild("staff_slime4_r1", CubeListBuilder.create().texOffs(35, 52).addBox(0.0F, -6.0F, -1.0F, 0.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition slime19_r1 = staff1c.addOrReplaceChild("slime19_r1", CubeListBuilder.create().texOffs(33, 2).addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition staff_t_a = staff1c.addOrReplaceChild("staff_t_a", CubeListBuilder.create().texOffs(34, 41).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -7.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition staff_t_b = staff_t_a.addOrReplaceChild("staff_t_b", CubeListBuilder.create().texOffs(34, 40).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition staff_t_c = staff_t_b.addOrReplaceChild("staff_t_c", CubeListBuilder.create().texOffs(34, 38).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition staff_t_d = staff_t_c.addOrReplaceChild("staff_t_d", CubeListBuilder.create().texOffs(34, 37).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition staff_t_e1 = staff_t_d.addOrReplaceChild("staff_t_e1", CubeListBuilder.create().texOffs(34, 36).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition staff_t_f1 = staff_t_e1.addOrReplaceChild("staff_t_f1", CubeListBuilder.create().texOffs(34, 34).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition staff_slime2_r1 = staff_t_f1.addOrReplaceChild("staff_slime2_r1", CubeListBuilder.create().texOffs(35, 55).addBox(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.5F, 0.0F, -0.5642F, 0.0283F, 0.2164F));

		PartDefinition staff_t_e2 = staff_t_d.addOrReplaceChild("staff_t_e2", CubeListBuilder.create().texOffs(38, 36).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition staff_t_f2 = staff_t_e2.addOrReplaceChild("staff_t_f2", CubeListBuilder.create().texOffs(38, 35).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition staff_slime3_r1 = staff_t_f2.addOrReplaceChild("staff_slime3_r1", CubeListBuilder.create().texOffs(35, 59).addBox(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, 0.0F, -0.5558F, -0.1016F, -0.1703F));

		PartDefinition staff_t_g = staff1c.addOrReplaceChild("staff_t_g", CubeListBuilder.create().texOffs(27, 40).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.0F, -7.0F, 1.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition staff_t_h = staff_t_g.addOrReplaceChild("staff_t_h", CubeListBuilder.create().texOffs(27, 38).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition staff_t_i = staff_t_h.addOrReplaceChild("staff_t_i", CubeListBuilder.create().texOffs(27, 35).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition staff_t_j = staff_t_i.addOrReplaceChild("staff_t_j", CubeListBuilder.create().texOffs(27, 33).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition staff_t_k = staff_t_j.addOrReplaceChild("staff_t_k", CubeListBuilder.create().texOffs(27, 32).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition arm6 = arm_lower2.addOrReplaceChild("arm6", CubeListBuilder.create().texOffs(4, 96).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.5F, 0.5F, -0.1308F, -0.0057F, 0.0004F));

		PartDefinition cloth_up_right1a = shoulder_blade_right.addOrReplaceChild("cloth_up_right1a", CubeListBuilder.create().texOffs(35, 82).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cloth_up_right1b = cloth_up_right1a.addOrReplaceChild("cloth_up_right1b", CubeListBuilder.create().texOffs(35, 89).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition hip_l1 = hipbone.addOrReplaceChild("hip_l1", CubeListBuilder.create().texOffs(11, 44).addBox(0.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition hip_l2 = hip_l1.addOrReplaceChild("hip_l2", CubeListBuilder.create().texOffs(11, 50).addBox(-0.98F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition cloth_left_1a = hip_l1.addOrReplaceChild("cloth_left_1a", CubeListBuilder.create().texOffs(15, 106).addBox(0.0F, 0.0F, -3.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.5F, 0.0F, 0.0F, 0.0F, -0.829F));

		PartDefinition cloth_left_1b = cloth_left_1a.addOrReplaceChild("cloth_left_1b", CubeListBuilder.create().texOffs(15, 108).addBox(0.0F, 0.0F, -3.0F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cloth_left_1c = cloth_left_1b.addOrReplaceChild("cloth_left_1c", CubeListBuilder.create().texOffs(15, 114).addBox(0.0F, 0.0F, -3.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition hip_r1 = hipbone.addOrReplaceChild("hip_r1", CubeListBuilder.create().texOffs(1, 44).addBox(-1.0F, -2.0F, -2.98F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -0.5F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition hip_r2 = hip_r1.addOrReplaceChild("hip_r2", CubeListBuilder.create().texOffs(3, 50).addBox(-0.02F, 0.0F, -2.98F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 1.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition cloth_right_1a = hip_r1.addOrReplaceChild("cloth_right_1a", CubeListBuilder.create().texOffs(24, 106).addBox(0.0F, 0.0F, -3.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.5F, 0.0F, 0.0F, 0.0F, 0.829F));

		PartDefinition cloth_right_1b = cloth_right_1a.addOrReplaceChild("cloth_right_1b", CubeListBuilder.create().texOffs(24, 108).addBox(0.0F, 0.0F, -3.0F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition cloth_right_1c = cloth_right_1b.addOrReplaceChild("cloth_right_1c", CubeListBuilder.create().texOffs(24, 114).addBox(0.0F, 0.0F, -3.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition leg_left1 = hipbone.addOrReplaceChild("leg_left1", CubeListBuilder.create().texOffs(19, 55).addBox(0.0F, 0.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(3.5F, -0.5F, -1.0F, -0.3105F, -0.2179F, 0.3366F));

		PartDefinition leg_left1_r1 = leg_left1.addOrReplaceChild("leg_left1_r1", CubeListBuilder.create().texOffs(13, 55).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

		PartDefinition leg_left_lower = leg_left1.addOrReplaceChild("leg_left_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 8.0F, -0.5F, 0.6954F, 0.0447F, -0.1231F));

		PartDefinition leg_left2 = leg_left_lower.addOrReplaceChild("leg_left2", CubeListBuilder.create().texOffs(21, 65).addBox(0.0F, -0.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.25F, 0.0F, 0.0F));

		PartDefinition foot_left = leg_left2.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(15, 74).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 7.5F, 0.5F, 0.7419F, -0.0057F, -0.0433F));

		PartDefinition heel_r1 = foot_left.addOrReplaceChild("heel_r1", CubeListBuilder.create().texOffs(15, 59).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, 1.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition slime20_r1 = foot_left.addOrReplaceChild("slime20_r1", CubeListBuilder.create().texOffs(34, 0).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9599F));

		PartDefinition toe_left1 = foot_left.addOrReplaceChild("toe_left1", CubeListBuilder.create().texOffs(12, 62).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.079F, 0.1775F, -0.0288F));

		PartDefinition toe_left2 = toe_left1.addOrReplaceChild("toe_left2", CubeListBuilder.create().texOffs(11, 63).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toes_left1 = foot_left.addOrReplaceChild("toes_left1", CubeListBuilder.create().texOffs(14, 62).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 0.1F, -2.0F, 0.2886F, -0.09F, 0.0381F));

		PartDefinition toes_left2 = toes_left1.addOrReplaceChild("toes_left2", CubeListBuilder.create().texOffs(13, 63).addBox(0.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition leg_left3 = leg_left_lower.addOrReplaceChild("leg_left3", CubeListBuilder.create().texOffs(17, 65).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 0.0F, 0.0F, 0.0436F, 0.0F, -0.1309F));

		PartDefinition slime21_r1 = leg_left3.addOrReplaceChild("slime21_r1", CubeListBuilder.create().texOffs(34, 1).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 5.5F, 1.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition leg_right1 = hipbone.addOrReplaceChild("leg_right1", CubeListBuilder.create().texOffs(0, 55).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.5F, -0.5F, -1.0F, -0.1754F, 0.1363F, -0.1553F));

		PartDefinition leg_right1_r1 = leg_right1.addOrReplaceChild("leg_right1_r1", CubeListBuilder.create().texOffs(6, 55).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition leg_right_lower = leg_right1.addOrReplaceChild("leg_right_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, 10.0F, -0.5F, 0.3463F, -0.0447F, 0.1231F));

		PartDefinition leg_right2 = leg_right_lower.addOrReplaceChild("leg_right2", CubeListBuilder.create().texOffs(0, 65).addBox(-1.0F, -0.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, 0.0F, 0.0F));

		PartDefinition foot_right = leg_right2.addOrReplaceChild("foot_right", CubeListBuilder.create().texOffs(0, 74).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 10.5F, 0.5F, 0.6546F, 0.0057F, 0.0433F));

		PartDefinition heel_r2 = foot_right.addOrReplaceChild("heel_r2", CubeListBuilder.create().texOffs(6, 59).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, 1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toe_right1 = foot_right.addOrReplaceChild("toe_right1", CubeListBuilder.create().texOffs(9, 62).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, -3.0F, 0.3844F, -0.1775F, 0.0288F));

		PartDefinition toe_right2 = toe_right1.addOrReplaceChild("toe_right2", CubeListBuilder.create().texOffs(8, 63).addBox(0.0F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition toes_right1 = foot_right.addOrReplaceChild("toes_right1", CubeListBuilder.create().texOffs(5, 62).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, 0.1F, -2.0F, 0.2886F, 0.09F, -0.0381F));

		PartDefinition toes_right2 = toes_right1.addOrReplaceChild("toes_right2", CubeListBuilder.create().texOffs(4, 63).addBox(-2.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition leg_right3 = leg_right_lower.addOrReplaceChild("leg_right3", CubeListBuilder.create().texOffs(4, 65).addBox(0.0F, -0.5F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -1.0F, 0.0F, 0.0436F, 0.0F, 0.1309F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float reloadProgress = Mth.lerp(partialTick, entity.prevReloadTimer / 40.0f, entity.getReloadTimer() / 40.0f);
		float castingProgress = Mth.lerp(partialTick, entity.prevCastingTimer / 40.0f, entity.getCastingTimer() / 40.0f);
		float attackProgress = Mth.lerp(partialTick, entity.prevAttackTimer / 20.0f, entity.getAttackTimer() / 20.0f);
		float spawnProgressDelay = Mth.lerp(partialTick, entity.lastSpawningAnimationTicks / 30.0f, entity.getSpawnTimer() / 30.0f);
		Vec3 movement = entity.getDeltaMovement();
		float sin = Mth.sin(ageInTicks * 0.25F) * 0.8F;

		head_base.yRot += netHeadYaw / Mth.RAD_TO_DEG;
		head_base.xRot += headPitch / Mth.RAD_TO_DEG;
		jaw.xRot = convertDegtoRad(15F) + Mth.cos(ageInTicks * 0.25F) * 0.125F;

		if(!entity.isEmerging())
			hipbone.y = -2.0F + sin * 2F;

		if(movement.x == 0 && movement.z == 0) {
			arm4.zRot = convertDegtoRad(7.5261F) + sin * 0.0625F;
			arm1.zRot = convertDegtoRad(-8.1958F) - sin * 0.125F;
		}
		else {
			arm4.zRot = convertDegtoRad(7.5261F) + convertDegtoRad(30F) * limbSwingAmount;
			arm1.zRot = convertDegtoRad(-8.1958F) + convertDegtoRad(-30F) * limbSwingAmount;
		}

		if(entity.isEmerging()) {

			if(entity.getSpawnTimer() < 10) {
				arm4.visible = false;
				staff1a.visible = false;
			}
			else
				arm4.visible = true;

			if(entity.getSpawnTimer() < 15) {
				arm4.xRot = 3.0527F;
				arm4.yRot = -0.1415F;
				arm4.zRot = -0.0136F;
				arm_lower2.xRot = 0.2182F;
				hand2.xRot = 0.1309F;
				arm1.xRot = -3.1416F;
				arm1.yRot = -0.1745F;
				arm1.zRot = -0.0121F;
				arm_lower.xRot = -0.0436F;
			}
			else {
				if (entity.getSpawnTimer() > 20)
					staff1a.visible = true;

				arm4.xRot = -3.0527F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm4.yRot = -0.1415F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm4.zRot = -0.0136F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm_lower2.xRot = 0.2182F * (float) Math.sin(spawnProgressDelay * Math.PI);
				hand2.xRot = 0.1309F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm1.xRot = -3.1416F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm1.yRot = -0.1745F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm1.zRot = -0.0121F * (float) Math.sin(spawnProgressDelay * Math.PI);
				arm_lower.xRot = -0.0436F * (float) Math.sin(spawnProgressDelay * Math.PI);

			}
		}

		if (entity.getReloadTimer() > 0) {
			if(!entity.isShootingSpikes()) {
				arm4.xRot = convertDegtoRad(-2.5881F) + convertDegtoRad(0.5546F) * (float)Math.sin(reloadProgress * Math.PI);
				arm4.yRot = convertDegtoRad(14.9854F) + convertDegtoRad(57.1991F) * (float)Math.sin(reloadProgress * Math.PI);
				arm4.zRot = convertDegtoRad(7.5261F) + convertDegtoRad(103.5949F) * (float)Math.sin(reloadProgress * Math.PI);
				hand2.xRot = convertDegtoRad(-20F) + convertDegtoRad(40F) * (float)Math.sin(reloadProgress * Math.PI);
				hand2.yRot = convertDegtoRad(0F) + convertDegtoRad(15F) * (float)Math.sin(reloadProgress * Math.PI);
				hand2.zRot = convertDegtoRad(-5F) + convertDegtoRad(25F) * (float)Math.sin(reloadProgress * Math.PI);
				arm1.xRot = convertDegtoRad(0F) + convertDegtoRad(-150F) * (float)Math.sin(reloadProgress * Math.PI);
				arm1.yRot = convertDegtoRad(0F) + convertDegtoRad(-40F) * (float)Math.sin(reloadProgress * Math.PI);
				arm1.zRot = convertDegtoRad(-8.1958F) + convertDegtoRad(88.1958F) * (float)Math.sin(reloadProgress * Math.PI);
				arm_lower.xRot = convertDegtoRad(-17.5F) + convertDegtoRad(2.5F) * (float)Math.sin(reloadProgress * Math.PI);
				arm_lower.yRot = convertDegtoRad(0F) + convertDegtoRad(10F) * (float)Math.sin(reloadProgress * Math.PI);
				arm_lower.zRot = convertDegtoRad(0F) + convertDegtoRad(40F) * (float)Math.sin(reloadProgress * Math.PI);
				head_base.xRot = convertDegtoRad(-12.5F) + convertDegtoRad(-27.5F) * (float)Math.sin(reloadProgress * Math.PI);
				jaw.xRot = convertDegtoRad(15F) + convertDegtoRad(30F) * (float)Math.sin(reloadProgress * Math.PI);
	
				if(entity.isCasting()) {
					hand2.xRot = convertDegtoRad(20F) + convertDegtoRad(12.5F) * (float)Math.sin(castingProgress * Math.PI * 4);
					arm1.zRot = convertDegtoRad(80F) + convertDegtoRad(-35F) * (float)Math.sin(castingProgress * Math.PI);
				}
			}
			else {
				//Not sure if anything really needs to be added for the spike 'reload'
			}
		}

		if (entity.getAttackTimer() > 0) {
			if(!entity.isShootingSpikes()) {
				arm1.xRot = convertDegtoRad(0F) + convertDegtoRad(-77.5F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.xRot = convertDegtoRad(-2.5881F) + convertDegtoRad(4.0881F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.yRot = convertDegtoRad(14.9854F) + convertDegtoRad(-40F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.zRot = convertDegtoRad(7.5261F) + convertDegtoRad(17.4739F) * (float)Math.sin(attackProgress * Math.PI);
				hand.yRot = convertDegtoRad(0F) + convertDegtoRad(77.5F) * (float)Math.sin(attackProgress * Math.PI); 
				spine1a.xRot = convertDegtoRad(-10F) + convertDegtoRad(15F) * (float)Math.sin(attackProgress * Math.PI);
			}
			else {
				arm1.zRot = convertDegtoRad(-8.1958F) + convertDegtoRad(-41.8042F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.xRot = convertDegtoRad(-2.5881F) + convertDegtoRad(-9.4119F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.yRot = convertDegtoRad(14.9854F) + convertDegtoRad(-24.9854F) * (float)Math.sin(attackProgress * Math.PI);
				arm4.zRot = convertDegtoRad(7.5261F) + convertDegtoRad(62.4739F) * (float)Math.sin(attackProgress * Math.PI);
				hand2.xRot = convertDegtoRad(-20F) + convertDegtoRad(-7.5F) * (float)Math.sin(attackProgress * Math.PI);
				spine1a.xRot = convertDegtoRad(-10F) + convertDegtoRad(25F) * (float)Math.sin(attackProgress * Math.PI);
				spine1a.yRot = convertDegtoRad(0F) + convertDegtoRad(60F) * (float)Math.sin(attackProgress * Math.PI * 2);
				hipbone.yRot = convertDegtoRad(0F) + convertDegtoRad(45F) * (float)Math.sin(attackProgress * Math.PI * 2);
			}
		}
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		hipbone.render(stack, consumer, light, overlay, color);
	}
}