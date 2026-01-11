package thebetweenlands.client.model.armor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;

public class SyrmoriteArmorModel extends BLArmorModel {

	private final EquipmentSlot slot;
	private final ModelPart belt;
	private final ModelPart leftBoot;
	private final ModelPart rightBoot;

	public SyrmoriteArmorModel(EquipmentSlot slot, ModelPart root) {
		super(root);
		this.slot = slot;
		this.belt = root.getChild("belt");
		this.leftBoot = root.getChild("left_boot");
		this.rightBoot = root.getChild("right_boot");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(34, 10).addBox(-5.0F, -8.25F, -5.0F, 10.0F, 4.0F, 4.0F, CubeDeformation.NONE)
			.texOffs(0, 27).addBox(-5.0F, -8.25F, -1.0F, 10.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.ZERO);
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition helmet_right_r1 = head.addOrReplaceChild("helmet_right_r1", CubeListBuilder.create().texOffs(10, 67).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -4.25F, -2.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition helmet_left_r1 = head.addOrReplaceChild("helmet_left_r1", CubeListBuilder.create().texOffs(0, 67).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -4.25F, -2.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition helmet_main_b_r1 = head.addOrReplaceChild("helmet_main_b_r1", CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, 0.0F, -7.0F, 10.0F, 3.0F, 7.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, -4.25F, 5.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition helmet_front_l_r1 = head.addOrReplaceChild("helmet_front_l_r1", CubeListBuilder.create().texOffs(20, 68).addBox(-5.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, CubeDeformation.NONE)
			.texOffs(28, 15).addBox(-2.0F, 1.0F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -4.25F, -5.0F, 0.0F, -0.1745F, 0.0F));

		PartDefinition top_f_l_r1 = head.addOrReplaceChild("top_f_l_r1", CubeListBuilder.create().texOffs(64, 48).addBox(-5.0F, 0.0F, 0.0F, 5.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -8.25F, -5.0F, 0.4363F, -0.1745F, 0.0F));

		PartDefinition helmet_front_r_r1 = head.addOrReplaceChild("helmet_front_r_r1", CubeListBuilder.create().texOffs(54, 69).addBox(0.0F, -4.0F, 0.0F, 5.0F, 5.0F, 1.0F, CubeDeformation.NONE)
			.texOffs(34, 25).addBox(0.0F, 1.0F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, -4.25F, -5.0F, 0.0F, 0.1745F, 0.0F));

		PartDefinition top_f_r_r1 = head.addOrReplaceChild("top_f_r_r1", CubeListBuilder.create().texOffs(64, 45).addBox(0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, -8.25F, -5.0F, 0.4363F, 0.1745F, 0.0F));

		PartDefinition nose_guard_r1 = head.addOrReplaceChild("nose_guard_r1", CubeListBuilder.create().texOffs(28, 10).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.5F, -6.25F, -0.0873F, 0.0F, 0.0F));

		PartDefinition guard_top1_r1 = head.addOrReplaceChild("guard_top1_r1", CubeListBuilder.create().texOffs(54, 63).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.4848F, -5.9014F, -1.1781F, 0.0F, 0.0F));

		PartDefinition guard_top2_r1 = head.addOrReplaceChild("guard_top2_r1", CubeListBuilder.create().texOffs(54, 75).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -9.6328F, -3.1297F, -1.5882F, 0.0F, 0.0F));

		PartDefinition guard_top2b_r1 = head.addOrReplaceChild("guard_top2b_r1", CubeListBuilder.create().texOffs(20, 46).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, -9.5979F, -1.13F, -1.5882F, 0.0F, 0.0F));

		PartDefinition nose_guard_b_r1 = head.addOrReplaceChild("nose_guard_b_r1", CubeListBuilder.create().texOffs(24, 38).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -4.5F, -6.25F, 0.0873F, 0.0F, 0.0F));

		PartDefinition top_l_r1 = head.addOrReplaceChild("top_l_r1", CubeListBuilder.create().texOffs(28, 0).addBox(-5.0F, 0.0F, -9.0F, 5.0F, 0.0F, 10.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -8.25F, 4.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition top_r_r1 = head.addOrReplaceChild("top_r_r1", CubeListBuilder.create().texOffs(32, 27).addBox(0.0F, 0.0F, -9.0F, 5.0F, 0.0F, 10.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, -8.25F, 4.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition top_b_r1 = head.addOrReplaceChild("top_b_r1", CubeListBuilder.create().texOffs(32, 37).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.25F, 5.0F, -0.3054F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F, CubeDeformation.NONE), PartPose.ZERO);

		PartDefinition chest_left_r1 = body.addOrReplaceChild("chest_left_r1", CubeListBuilder.create().texOffs(62, 24).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.011F, -3.4978F, 0.1496F, 0.1295F, 0.0195F));

		PartDefinition chest_left_s_r1 = body.addOrReplaceChild("chest_left_s_r1", CubeListBuilder.create().texOffs(32, 68).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.9658F, -0.0662F, -2.9814F, 0.1366F, 0.0555F, -0.0797F));

		PartDefinition chest_right_r1 = body.addOrReplaceChild("chest_right_r1", CubeListBuilder.create().texOffs(40, 63).addBox(0.0F, 0.0F, 0.0F, 4.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.011F, -3.4978F, 0.1496F, -0.1295F, -0.0195F));

		PartDefinition chest_right_s_r1 = body.addOrReplaceChild("chest_right_s_r1", CubeListBuilder.create().texOffs(66, 69).addBox(0.0F, 0.0F, 0.0F, 1.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.9658F, -0.0662F, -2.9814F, 0.1366F, -0.0555F, 0.0797F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 53).addBox(-1.5F, -2.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition shoulder_plate_left_r1 = left_arm.addOrReplaceChild("shoulder_plate_left_r1", CubeListBuilder.create().texOffs(60, 53).addBox(0.0F, -1.0F, -4.0F, 3.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.25F, 1.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition shoulder_plate_left_2_r1 = left_arm.addOrReplaceChild("shoulder_plate_left_2_r1", CubeListBuilder.create().texOffs(60, 61).addBox(0.0F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.6049F, -2.8499F, 1.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 57).addBox(-3.5F, -2.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition shoulder_plate_right_r1 = right_arm.addOrReplaceChild("shoulder_plate_right_r1", CubeListBuilder.create().texOffs(62, 8).addBox(-3.0F, -1.0F, -4.0F, 3.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.25F, 1.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition shoulder_plate_right_2_r1 = right_arm.addOrReplaceChild("shoulder_plate_right_2_r1", CubeListBuilder.create().texOffs(62, 16).addBox(-3.0F, 0.0F, -4.0F, 3.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.6049F, -2.8499F, 1.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition belt = partdefinition.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(34, 18).addBox(-4.5F, 10.5F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.ZERO);

		PartDefinition belt_left_r1 = belt.addOrReplaceChild("belt_left_r1", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.9564F, 10.5009F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition belft_left2_r1 = belt.addOrReplaceChild("belft_left2_r1", CubeListBuilder.create().texOffs(62, 34).addBox(-2.0F, 0.0F, -3.0F, 2.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.9962F, 11.3255F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition belt_right_r1 = belt.addOrReplaceChild("belt_right_r1", CubeListBuilder.create().texOffs(0, 38).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(1.0F, 11.5F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(20, 53).addBox(-2.5F, -0.5F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.03F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, -0.5F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.02F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition left_boot = partdefinition.addOrReplaceChild("left_boot", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition plate_top_l_r1 = left_boot.addOrReplaceChild("plate_top_l_r1", CubeListBuilder.create().texOffs(24, 41).mirror().addBox(-3.0F, -2.0F, -3.0F, 5.0F, 7.0F, 5.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.4777F, 7.25F, 0.5213F, 0.0F, -0.0436F, 0.0F));

		PartDefinition plate_f_l_r1 = left_boot.addOrReplaceChild("plate_f_l_r1", CubeListBuilder.create().texOffs(20, 74).mirror().addBox(-2.0F, -2.0F, 0.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.109F, 7.25F, -2.4976F, 0.1745F, -0.0436F, 0.0F));

		PartDefinition plate_f_r2_r1 = left_boot.addOrReplaceChild("plate_f_r2_r1", CubeListBuilder.create().texOffs(0, 75).mirror().addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.1242F, 5.2804F, -2.8446F, -0.1309F, -0.0436F, 0.0F));

		PartDefinition sole_l_r1 = left_boot.addOrReplaceChild("sole_l_r1", CubeListBuilder.create().texOffs(40, 73).mirror().addBox(-2.0F, -2.0F, -2.0F, 5.0F, 2.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-0.3905F, 12.25F, -2.5194F, 0.0F, -0.0436F, 0.0F));

		PartDefinition f_t_l_r1 = left_boot.addOrReplaceChild("f_t_l_r1", CubeListBuilder.create().texOffs(20, 64).mirror().addBox(-2.0F, 0.0F, 0.0F, 5.0F, 1.0F, 3.0F, new CubeDeformation(-0.02F)).mirror(false), PartPose.offsetAndRotation(-0.3032F, 10.25F, -4.5175F, 0.3491F, -0.0436F, 0.0F));

		PartDefinition right_boot = partdefinition.addOrReplaceChild("right_boot", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition plate_top_r_r1 = right_boot.addOrReplaceChild("plate_top_r_r1", CubeListBuilder.create().texOffs(24, 41).addBox(-2.0F, -2.0F, -3.0F, 5.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.4777F, 7.25F, 0.5213F, 0.0F, 0.0436F, 0.0F));

		PartDefinition plate_f_r_r1 = right_boot.addOrReplaceChild("plate_f_r_r1", CubeListBuilder.create().texOffs(20, 74).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.109F, 7.25F, -2.4976F, 0.1745F, 0.0436F, 0.0F));

		PartDefinition plate_f_r2_r2 = right_boot.addOrReplaceChild("plate_f_r2_r2", CubeListBuilder.create().texOffs(0, 75).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.1242F, 5.2804F, -2.8446F, -0.1309F, 0.0436F, 0.0F));

		PartDefinition sole_r_r1 = right_boot.addOrReplaceChild("sole_r_r1", CubeListBuilder.create().texOffs(40, 73).addBox(-3.0F, -2.0F, -2.0F, 5.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.3905F, 12.25F, -2.5194F, 0.0F, 0.0436F, 0.0F));

		PartDefinition f_t_r_r1 = right_boot.addOrReplaceChild("f_t_r_r1", CubeListBuilder.create().texOffs(20, 64).addBox(-3.0F, 0.0F, 0.0F, 5.0F, 1.0F, 3.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.3032F, 10.25F, -4.5175F, 0.3491F, 0.0436F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.belt, this.leftBoot, this.rightBoot));
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		switch (this.slot) {
			case HEAD -> {
				this.head.visible = true;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case CHEST -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = true;
				this.rightArm.visible = true;
				this.leftArm.visible = true;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case LEGS -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = true;
				this.rightLeg.visible = true;
				this.leftLeg.visible = true;
				this.leftBoot.visible = false;
				this.rightBoot.visible = false;
			}
			case FEET -> {
				this.head.visible = false;
				this.hat.visible = false;
				this.body.visible = false;
				this.rightArm.visible = false;
				this.leftArm.visible = false;
				this.belt.visible = false;
				this.rightLeg.visible = false;
				this.leftLeg.visible = false;
				this.leftBoot.visible = true;
				this.rightBoot.visible = true;
			}
			default -> { }
		}
		this.rightBoot.copyFrom(this.rightLeg);
		this.leftBoot.copyFrom(this.leftLeg);
		super.renderToBuffer(stack, consumer, light, overlay, color);
	}
}
