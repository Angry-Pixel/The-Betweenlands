package thebetweenlands.client.model.armor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;

public class BoneArmorModel extends BLArmorModel {
	
	private final EquipmentSlot slot;
	private final ModelPart belt;
	private final ModelPart leftBoot;
	private final ModelPart rightBoot;

	public BoneArmorModel(EquipmentSlot slot, ModelPart root) {
		super(root);
		this.slot = slot;
		this.belt = root.getChild("belt");
		this.leftBoot = root.getChild("left_boot");
		this.rightBoot = root.getChild("right_boot");
	}

	// children that are expected always:
	// "head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"
	public static LayerDefinition makeModel() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition armour = partdefinition;

		// =========== HELMET ===========
		
		// Originally "helmet"
		PartDefinition head = armour.addOrReplaceChild("head", CubeListBuilder.create().texOffs(29, 0).addBox(-5.0F, -8.999F, -1.0436F, 10.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

		// -------- HELMET PARTS --------
		
		PartDefinition helmet_main_r1 = head.addOrReplaceChild("helmet_main_r1", CubeListBuilder.create().texOffs(1, 12).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.0F, -1.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition helmet_back_r_r1 = head.addOrReplaceChild("helmet_back_r_r1", CubeListBuilder.create().texOffs(0, 6).addBox(-5.0F, -1.0F, 0.0F, 5.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -7.999F, 0.9564F, 0.1762F, -0.036F, -0.043F));

		PartDefinition helmet_back_l_r1 = head.addOrReplaceChild("helmet_back_l_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -1.0F, 0.0F, 5.0F, 1.0F, 5.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.0F, -7.999F, 0.9564F, 0.1762F, 0.036F, 0.043F));

		PartDefinition helmet_bottomv_r1 = head.addOrReplaceChild("helmet_bottomv_r1", CubeListBuilder.create().texOffs(29, 10).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 3.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -4.999F, -1.0436F, 0.1309F, 0.0F, 0.0F));

		PartDefinition helmet_front1_r1 = head.addOrReplaceChild("helmet_front1_r1", CubeListBuilder.create().texOffs(1, 17).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.8682F, -4.0408F, 0.2182F, 0.0F, 0.0F));

		PartDefinition helmet_front1_r2 = head.addOrReplaceChild("helmet_front1_r2", CubeListBuilder.create().texOffs(1, 19).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.6518F, -5.0171F, 0.6545F, 0.0F, 0.0F));

		PartDefinition helmet_front1_r3 = head.addOrReplaceChild("helmet_front1_r3", CubeListBuilder.create().texOffs(4, 28).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.043F, -5.8104F, 1.6581F, 0.0F, 0.0F));

		PartDefinition helmet_front_l_r1 = head.addOrReplaceChild("helmet_front_l_r1", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -5.871F, -3.9099F, 0.0569F, 0.6973F, 0.0366F));

		PartDefinition helmet_front_l2_r1 = head.addOrReplaceChild("helmet_front_l2_r1", CubeListBuilder.create().texOffs(10, 21).addBox(-1.0F, -2.0F, -4.0F, 1.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.3572F, -5.8376F, -4.6752F, 0.2463F, 1.3909F, 0.2425F));

		PartDefinition helmet_front_l_r2 = head.addOrReplaceChild("helmet_front_l_r2", CubeListBuilder.create().texOffs(20, 0).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -2.8739F, -3.779F, 0.0569F, 0.6973F, 0.0366F));

		PartDefinition slime1_r1 = head.addOrReplaceChild("slime1_r1", CubeListBuilder.create().texOffs(21, 5).addBox(0.0F, 0.0F, -1.0F, 0.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -0.8758F, -3.6918F, 0.0569F, 0.6973F, 0.0366F));

		PartDefinition helmet_front_r_r1 = head.addOrReplaceChild("helmet_front_r_r1", CubeListBuilder.create().texOffs(16, 0).mirror().addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-5.0F, -5.871F, -3.9099F, 0.0569F, -0.6973F, -0.0366F));

		PartDefinition helmet_front_l2_r2 = head.addOrReplaceChild("helmet_front_l2_r2", CubeListBuilder.create().texOffs(0, 21).mirror().addBox(0.0F, -2.0F, -4.0F, 1.0F, 3.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-4.3572F, -5.8376F, -4.6752F, 0.2463F, -1.3909F, -0.2425F));

		PartDefinition helmet_front_r_r2 = head.addOrReplaceChild("helmet_front_r_r2", CubeListBuilder.create().texOffs(26, 0).mirror().addBox(0.0F, -1.0F, -2.0F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-5.0F, -2.8739F, -3.779F, 0.0569F, -0.6973F, -0.0366F));

		PartDefinition h_main_l_r1 = head.addOrReplaceChild("h_main_l_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 7.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(10, 29).mirror().addBox(-10.0F, 0.0F, 0.0F, 1.0F, 7.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(5.0F, -7.8691F, -3.9971F, 0.0436F, 0.0F, 0.0F));

		PartDefinition slime2_r1 = head.addOrReplaceChild("slime2_r1", CubeListBuilder.create().texOffs(23, 5).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -0.8758F, -3.6918F, 0.0436F, 0.0F, 0.0F));

		PartDefinition slime2_r2 = head.addOrReplaceChild("slime2_r2", CubeListBuilder.create().texOffs(23, 7).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -0.9194F, -2.6928F, 0.0645F, -0.828F, -0.0476F));

		PartDefinition slime2_r3 = head.addOrReplaceChild("slime2_r3", CubeListBuilder.create().texOffs(23, 8).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, -2.8739F, -3.779F, 0.1136F, -1.1758F, -0.1049F));

		PartDefinition slime3_r1 = head.addOrReplaceChild("slime3_r1", CubeListBuilder.create().texOffs(22, 7).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -8.8682F, -4.0408F, 0.6545F, 0.0F, 0.0F));

		// ==============================

		// ============= HAT ============
		// Note: the "hat" group has to exist for the model to parse, so we create an empty group here

		PartDefinition hat = armour.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		
		// ==============================
		
		
		
		// ========= CHESTPLATE =========
		
		// Originally "chest"
		PartDefinition body = armour.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 41).addBox(-1.0F, 1.0F, -4.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(22, 26).addBox(-4.5F, 1.02F, -1.25F, 9.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

		// ------ CHESTPLATE PARTS ------
		
		PartDefinition slime5_r1 = body.addOrReplaceChild("slime5_r1", CubeListBuilder.create().texOffs(23, 7).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.5F, 4.0F, -0.25F, 0.0F, -0.8727F, 0.0F));

		PartDefinition slime5_r2 = body.addOrReplaceChild("slime5_r2", CubeListBuilder.create().texOffs(21, 5).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.5F, 6.0F, 0.75F, 0.0F, -0.6545F, 0.0F));

		PartDefinition slime5_r3 = body.addOrReplaceChild("slime5_r3", CubeListBuilder.create().texOffs(23, 6).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.5F, 7.0F, -1.25F, 0.0F, -0.48F, 0.0F));

		PartDefinition chest_bottom_r1 = body.addOrReplaceChild("chest_bottom_r1", CubeListBuilder.create().texOffs(6, 42).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, -4.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition chest_l2_r1 = body.addOrReplaceChild("chest_l2_r1", CubeListBuilder.create().texOffs(12, 40).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 5.9624F, -3.4989F, 0.1691F, -0.2653F, -0.002F));

		PartDefinition chest_r2_r1 = body.addOrReplaceChild("chest_r2_r1", CubeListBuilder.create().texOffs(12, 46).mirror().addBox(-4.0F, 0.0F, 0.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-1.0F, 5.9624F, -3.4989F, 0.1691F, 0.2653F, 0.002F));

		PartDefinition chest_l1_r1 = body.addOrReplaceChild("chest_l1_r1", CubeListBuilder.create().texOffs(24, 38).addBox(0.0F, 0.0F, 0.0F, 4.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 1.0F, -3.5F, 0.0F, -0.2618F, 0.0F));

		PartDefinition chest_r1_r1 = body.addOrReplaceChild("chest_r1_r1", CubeListBuilder.create().texOffs(24, 45).mirror().addBox(-4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-1.0F, 1.0F, -3.5F, 0.0F, 0.2618F, 0.0F));

		// ==============================
		
		

		// ========== LEFT ARM ==========
		
		// Originally "chest" -> "left_sleeve", now "root" -> "left_arm"
		PartDefinition left_arm = armour.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		// ------- LEFT ARM PARTS -------
		
		PartDefinition pad_l1a_r1 = left_arm.addOrReplaceChild("pad_l1a_r1", CubeListBuilder.create().texOffs(12, 53).addBox(0.0F, 0.0F, -3.0F, 3.0F, 3.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -3.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition pad_l1b_r1 = left_arm.addOrReplaceChild("pad_l1b_r1", CubeListBuilder.create().texOffs(37, 73).addBox(0.0F, -2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.3801F, -0.7917F, 0.0F, 0.0F, 0.0F, 0.1309F));

		// ==============================
		
		
		// ========== RIGHT ARM =========

		// Originally "chest" -> "right_sleeve", now "root" -> "right_arm"
		PartDefinition right_arm = armour.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 5).addBox(-3.363F, -0.5306F, -3.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));

		// ------- RIGHT ARM PARTS ------
		
		PartDefinition pad_r1a_r1 = right_arm.addOrReplaceChild("pad_r1a_r1", CubeListBuilder.create().texOffs(0, 47).mirror().addBox(-3.0F, 0.0F, -3.0F, 3.0F, 3.0F, 6.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(1.0F, -3.5F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition pad_r1b_r1 = right_arm.addOrReplaceChild("pad_r1b_r1", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(-2.0F, -2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-1.3801F, -0.7917F, 0.0F, 0.0F, 0.0F, -0.1309F));

		// ==============================
		
		
		
		// ========== LEFT LEG ==========
		
		// Originally "leggings" -> "left_pants", now "root" -> "left_leg"
		PartDefinition left_leg = armour.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

		// ------- LEFT LEG PARTS -------
		
		PartDefinition plate_r1_r1 = left_leg.addOrReplaceChild("plate_r1_r1", CubeListBuilder.create().texOffs(1, 86).addBox(-2.0F, 0.0F, -3.0F, 3.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.75F, -2.0F, 0.0F, -0.0114F, 0.0865F, -0.2187F));

		PartDefinition plate_r2_r1 = left_leg.addOrReplaceChild("plate_r2_r1", CubeListBuilder.create().texOffs(27, 79).addBox(0.0F, 0.0F, -3.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(1.6412F, 2.1215F, 0.0409F, 0.0F, 0.0873F, -0.0873F));

		PartDefinition slime7_r1 = left_leg.addOrReplaceChild("slime7_r1", CubeListBuilder.create().texOffs(23, 8).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.7648F, -1.2246F, -2.0902F, -0.0161F, 0.7846F, -0.229F));

		PartDefinition slime7_r2 = left_leg.addOrReplaceChild("slime7_r2", CubeListBuilder.create().texOffs(23, 5).addBox(0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.4895F, -1.9772F, -2.9886F, -0.0131F, -0.5243F, -0.2111F));

		PartDefinition kneecap_l_r1 = left_leg.addOrReplaceChild("kneecap_l_r1", CubeListBuilder.create().texOffs(19, 79).addBox(-1.0F, -1.0F, -3.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE)
		.texOffs(14, 72).addBox(-2.0F, -3.0F, -3.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.5F, 6.5F, 0.5F, 0.0F, -0.0436F, 0.0F));

		// ==============================
		
		
		// ========== RIGHT LEG =========
		
		// Originally "leggings" -> "right_pants", now "root" -> "right_leg"
		PartDefinition right_leg = armour.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

		// ------- RIGHT LEG PARTS -------
		
		PartDefinition kneecap_r_r1 = right_leg.addOrReplaceChild("kneecap_r_r1", CubeListBuilder.create().texOffs(19, 64).mirror().addBox(-3.0F, -1.0F, -3.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false)
		.texOffs(19, 86).mirror().addBox(-3.0F, -3.0F, -3.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)).mirror(false), PartPose.offsetAndRotation(0.5F, 6.5F, 0.5F, 0.0F, 0.0436F, 0.0F));

		PartDefinition slime6_r1 = right_leg.addOrReplaceChild("slime6_r1", CubeListBuilder.create().texOffs(21, 6).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.628F, 6.5F, -2.3663F, 0.0F, -0.7418F, 0.0F));

		PartDefinition slime6_r2 = right_leg.addOrReplaceChild("slime6_r2", CubeListBuilder.create().texOffs(23, 8).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.628F, 7.5F, -2.3663F, 0.0F, 0.0436F, 0.0F));

		PartDefinition plate_r1_r2 = right_leg.addOrReplaceChild("plate_r1_r2", CubeListBuilder.create().texOffs(1, 76).mirror().addBox(-1.0F, 0.0F, -3.0F, 3.0F, 4.0F, 6.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-1.75F, -2.0F, 0.0F, -0.0114F, -0.0865F, 0.2187F));

		PartDefinition plate_r2_r2 = right_leg.addOrReplaceChild("plate_r2_r2", CubeListBuilder.create().texOffs(27, 79).mirror().addBox(-2.0F, 0.0F, -3.0F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-1.6412F, 2.1215F, 0.0409F, 0.0F, -0.0873F, 0.0873F));

		// ==============================

		
		
		// ============ BELT ============

		// Originally "leggings" -> "belt", now "root" -> "belt"
		PartDefinition belt = armour.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(11, 72).addBox(-3.0F, 0.5F, 2.5F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.02F))
		.texOffs(3, 72).addBox(-3.0F, 0.5F, -2.5F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.02F))
		.texOffs(0, 65).addBox(-5.0F, -1.5F, -2.5F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)), PartPose.offset(1.0F, 11.5F, 0.0F));

		// ========== BELT END ==========

		
		
		// ========== LEFT BOOT =========

		// Originally "boots" -> "foot_left", now "root" -> "left_boot"
		PartDefinition left_boot = armour.addOrReplaceChild("left_boot", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition sole_l_r1 = left_boot.addOrReplaceChild("sole_l_r1", CubeListBuilder.create().texOffs(32, 54).addBox(-2.0F, -6.0F, -3.0F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.02F))
		.texOffs(34, 47).addBox(-1.0F, -8.0F, -3.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.4995F, 12.0F, 0.4782F, 0.0F, -0.0436F, 0.0F));

		PartDefinition front_l1_r1 = left_boot.addOrReplaceChild("front_l1_r1", CubeListBuilder.create().texOffs(35, 69).addBox(-2.0F, -2.0F, -2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.3687F, 12.0F, -2.519F, 0.0F, -0.0436F, 0.0F));

		PartDefinition front_l2_r1 = left_boot.addOrReplaceChild("front_l2_r1", CubeListBuilder.create().texOffs(34, 65).addBox(-2.0F, 0.0F, 0.0F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.2814F, 10.0F, -4.5171F, 0.3927F, -0.0436F, 0.0F));

		PartDefinition slime8_r1 = left_boot.addOrReplaceChild("slime8_r1", CubeListBuilder.create().texOffs(25, 7).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.6754F, 9.6173F, -3.4632F, -0.2182F, -0.0436F, 0.0F));

		PartDefinition slime8_r2 = left_boot.addOrReplaceChild("slime8_r2", CubeListBuilder.create().texOffs(25, 5).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.7157F, 12.0F, -4.3862F, 0.0F, 0.3927F, 0.0F));

		// ==============================
		

		// ========= RIGHT BOOT =========

		// Originally "boots" -> "foot_right", now "root" -> "right_boot"
		PartDefinition right_boot = armour.addOrReplaceChild("right_boot", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition sole_r_r1 = right_boot.addOrReplaceChild("sole_r_r1", CubeListBuilder.create().texOffs(53, 54).mirror().addBox(-3.0F, -6.0F, -3.0F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.02F)).mirror(false)
		.texOffs(55, 47).mirror().addBox(-3.0F, -8.0F, -3.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.4995F, 12.0F, 0.4782F, 0.0F, 0.0436F, 0.0F));

		PartDefinition front_r1_r1 = right_boot.addOrReplaceChild("front_r1_r1", CubeListBuilder.create().texOffs(56, 69).mirror().addBox(-3.0F, -2.0F, -2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(0.3687F, 12.0F, -2.519F, 0.0F, 0.0436F, 0.0F));

		PartDefinition front_r2_r1 = right_boot.addOrReplaceChild("front_r2_r1", CubeListBuilder.create().texOffs(55, 65).mirror().addBox(-3.0F, 0.0F, 0.0F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(0.2814F, 10.0F, -4.5171F, 0.3927F, 0.0436F, 0.0F));

		PartDefinition slime9_r1 = right_boot.addOrReplaceChild("slime9_r1", CubeListBuilder.create().texOffs(23, 8).addBox(0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5849F, 6.0F, -1.389F, 0.0F, -0.7418F, 0.0F));

		// ==============================
		
		
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