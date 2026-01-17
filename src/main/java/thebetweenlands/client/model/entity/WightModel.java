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
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Wight;

public class WightModel<T extends Entity> extends MowzieModelBase<T> {
	public boolean renderHeadOnly = false;

	private final ModelPart base2;
	private final ModelPart torso3;
	private final ModelPart torso4;
	private final ModelPart chest2;
	private final ModelPart chest_left2;
	private final ModelPart arm_left3;
	private final ModelPart arm_left4;
	private final ModelPart cloak_left2;
	private final ModelPart chest_right2;
	private final ModelPart arm_right3;
	private final ModelPart arm_right4;
	private final ModelPart cloak_right2;
	private final ModelPart neck2;
	private final ModelPart head_base2;
	private final ModelPart head_top2;
	private final ModelPart jaw2;
	private final ModelPart hood2;
	private final ModelPart hood_l5;
	private final ModelPart hood_l9;
	private final ModelPart hood_l10;
	private final ModelPart hood_l11;
	private final ModelPart hood_r2;
	private final ModelPart hood_l12;
	private final ModelPart hood_l13;
	private final ModelPart hood_l14;
	private final ModelPart hood_tip2;
	private final ModelPart hood_tip_m2;
	private final ModelPart hood_tip_b2;
	private final ModelPart leg_left3;
	private final ModelPart leg_left4;
	private final ModelPart leg_right3;
	private final ModelPart leg_right4;
	private final ModelPart cloak_b3;
	private final ModelPart cloak_b4;
	private final ModelPart cloak_s_l3;
	private final ModelPart cloak_s_l4;
	private final ModelPart cloak_s_r3;
	private final ModelPart cloak_s_r4;

	private final ModelPart[] headPieces;

	public WightModel(ModelPart root) {
		super(root, RenderType::entityTranslucent);
		this.base2 = root.getChild("base2");
		this.torso3 = this.base2.getChild("torso3");
		this.torso4 = this.torso3.getChild("torso4");
		this.chest2 = this.torso4.getChild("chest2");
		this.chest_left2 = this.chest2.getChild("chest_left2");
		this.arm_left3 = this.chest_left2.getChild("arm_left3");
		this.arm_left4 = this.arm_left3.getChild("arm_left4");
		this.cloak_left2 = this.chest_left2.getChild("cloak_left2");
		this.chest_right2 = this.chest2.getChild("chest_right2");
		this.arm_right3 = this.chest_right2.getChild("arm_right3");
		this.arm_right4 = this.arm_right3.getChild("arm_right4");
		this.cloak_right2 = this.chest_right2.getChild("cloak_right2");
		this.neck2 = this.torso4.getChild("neck2");
		this.head_base2 = this.neck2.getChild("head_base2");
		this.head_top2 = this.head_base2.getChild("head_top2");
		this.jaw2 = this.head_base2.getChild("jaw2");
		this.hood2 = this.head_base2.getChild("hood2");
		this.hood_l5 = this.hood2.getChild("hood_l5");
		this.hood_l9 = this.hood_l5.getChild("hood_l9");
		this.hood_l10 = this.hood_l9.getChild("hood_l10");
		this.hood_l11 = this.hood_l10.getChild("hood_l11");
		this.hood_r2 = this.hood2.getChild("hood_r2");
		this.hood_l12 = this.hood_r2.getChild("hood_l12");
		this.hood_l13 = this.hood_l12.getChild("hood_l13");
		this.hood_l14 = this.hood_l13.getChild("hood_l14");
		this.hood_tip2 = this.hood2.getChild("hood_tip2");
		this.hood_tip_m2 = this.hood_tip2.getChild("hood_tip_m2");
		this.hood_tip_b2 = this.hood_tip_m2.getChild("hood_tip_b2");
		this.leg_left3 = this.base2.getChild("leg_left3");
		this.leg_left4 = this.leg_left3.getChild("leg_left4");
		this.leg_right3 = this.base2.getChild("leg_right3");
		this.leg_right4 = this.leg_right3.getChild("leg_right4");
		this.cloak_b3 = this.base2.getChild("cloak_b3");
		this.cloak_b4 = this.cloak_b3.getChild("cloak_b4");
		this.cloak_s_l3 = this.base2.getChild("cloak_s_l3");
		this.cloak_s_l4 = this.cloak_s_l3.getChild("cloak_s_l4");
		this.cloak_s_r3 = this.base2.getChild("cloak_s_r3");
		this.cloak_s_r4 = this.cloak_s_r3.getChild("cloak_s_r4");

		this.headPieces = new ModelPart[]{
				this.neck2.getChild("head_base2"),
				this.head_base2.getChild("jaw2")
			};
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base2 = partdefinition.addOrReplaceChild("base2", CubeListBuilder.create().texOffs(0, 63).addBox(-3.5F, -2.0F, -4.0F, 7.0F, 3.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition hipbone_l_r1 = base2.addOrReplaceChild("hipbone_l_r1", CubeListBuilder.create().texOffs(20, 61).addBox(-3.0F, -1.0F, -4.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition hipbone_r_r1 = base2.addOrReplaceChild("hipbone_r_r1", CubeListBuilder.create().texOffs(20, 68).addBox(0.0F, -1.0F, -4.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition torso3 = base2.addOrReplaceChild("torso3", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition torso4 = torso3.addOrReplaceChild("torso4", CubeListBuilder.create().texOffs(0, 40).addBox(-4.0F, -7.0F, -5.0F, 8.0F, 7.0F, 5.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition ribs_r_r1 = torso4.addOrReplaceChild("ribs_r_r1", CubeListBuilder.create().texOffs(21, 53).addBox(0.0F, 0.0F, -5.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition ribs_l_r1 = torso4.addOrReplaceChild("ribs_l_r1", CubeListBuilder.create().texOffs(21, 47).addBox(-3.0F, 0.0F, -5.0F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition chest2 = torso4.addOrReplaceChild("chest2", CubeListBuilder.create(), PartPose.offset(0.0F, -2.25F, -6.0F));

		PartDefinition chest_left2 = chest2.addOrReplaceChild("chest_left2", CubeListBuilder.create().texOffs(20, 30).addBox(0.0F, -5.0F, 0.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0077F, -0.1744F, 0.0443F));

		PartDefinition arm_left3 = chest_left2.addOrReplaceChild("arm_left3", CubeListBuilder.create().texOffs(38, 38).addBox(0.0F, -1.0F, -2.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.5F, -3.5F, 3.0F, -0.3478F, 0.0298F, -0.0052F));

		PartDefinition arm_left4 = arm_left3.addOrReplaceChild("arm_left4", CubeListBuilder.create().texOffs(38, 51).addBox(0.0F, 0.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition cloak_left2 = chest_left2.addOrReplaceChild("cloak_left2", CubeListBuilder.create().texOffs(40, 17).addBox(-5.0F, 0.0F, -3.5F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.75F, -5.25F, 3.0F));

		PartDefinition chest_right2 = chest2.addOrReplaceChild("chest_right2", CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, -5.0F, 0.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0077F, 0.1744F, -0.0443F));

		PartDefinition arm_right3 = chest_right2.addOrReplaceChild("arm_right3", CubeListBuilder.create().texOffs(48, 38).addBox(-2.0F, -1.0F, -2.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.5F, -3.5F, 3.0F, -0.3478F, -0.0298F, 0.0052F));

		PartDefinition arm_right4 = arm_right3.addOrReplaceChild("arm_right4", CubeListBuilder.create().texOffs(48, 51).addBox(-2.0F, 0.0F, -3.0F, 2.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 1.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition cloak_right2 = chest_right2.addOrReplaceChild("cloak_right2", CubeListBuilder.create().texOffs(74, 17).addBox(0.0F, 0.0F, -3.5F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.75F, -5.25F, 3.0F));

		PartDefinition neck2 = torso4.addOrReplaceChild("neck2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition head_base2 = neck2.addOrReplaceChild("head_base2", CubeListBuilder.create().texOffs(4, 11).addBox(-3.0F, -3.0F, -2.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.01F))
		.texOffs(4, 24).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition head_top2 = head_base2.addOrReplaceChild("head_top2", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -7.0F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(19, 3).addBox(1.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 0).addBox(2.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(1, 3).addBox(-3.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(1, 0).addBox(-3.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.5F));

		PartDefinition jaw2 = head_base2.addOrReplaceChild("jaw2", CubeListBuilder.create().texOffs(2, 17).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 12).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 19).addBox(2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(1, 19).addBox(-3.0F, -1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.5F));

		PartDefinition hood2 = head_base2.addOrReplaceChild("hood2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -8.5F, 1.0F, 0.0611F, 0.0F, 0.0F));

		PartDefinition hood_l5 = hood2.addOrReplaceChild("hood_l5", CubeListBuilder.create().texOffs(60, 2).addBox(0.0F, 0.0F, -8.0F, 3.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0087F, 0.0F, 0.2618F));

		PartDefinition hood_l_b_r1 = hood_l5.addOrReplaceChild("hood_l_b_r1", CubeListBuilder.create().texOffs(68, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0087F, 0.0F, 0.0F));

		PartDefinition hood_l_f_r1 = hood_l5.addOrReplaceChild("hood_l_f_r1", CubeListBuilder.create().texOffs(68, 10).addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition hood_l9 = hood_l5.addOrReplaceChild("hood_l9", CubeListBuilder.create().texOffs(66, 2).addBox(0.0F, 0.0F, -8.0F, 1.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition hood_l_b_r2 = hood_l9.addOrReplaceChild("hood_l_b_r2", CubeListBuilder.create().texOffs(74, 1).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0087F, 0.0F, 0.0F));

		PartDefinition hood_l_f_r2 = hood_l9.addOrReplaceChild("hood_l_f_r2", CubeListBuilder.create().texOffs(74, 10).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0524F, 0.0F, 0.0F));

		PartDefinition hood_l10 = hood_l9.addOrReplaceChild("hood_l10", CubeListBuilder.create().texOffs(68, 2).addBox(0.0F, 0.0F, -8.0F, 4.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(76, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		PartDefinition hood_l_f_r3 = hood_l10.addOrReplaceChild("hood_l_f_r3", CubeListBuilder.create().texOffs(76, 12).addBox(0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition hood_l11 = hood_l10.addOrReplaceChild("hood_l11", CubeListBuilder.create().texOffs(76, 2).addBox(0.0F, 0.0F, -8.0F, 9.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0017F, 0.3491F, 0.0F));

		PartDefinition hood_r2 = hood2.addOrReplaceChild("hood_r2", CubeListBuilder.create().texOffs(54, 2).addBox(-3.0F, 0.0F, -8.0F, 3.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.05F, 0.0087F, 0.0F, -0.2618F));

		PartDefinition hood_r_b_r1 = hood_r2.addOrReplaceChild("hood_r_b_r1", CubeListBuilder.create().texOffs(62, 0).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0087F, 0.0F, 0.0F));

		PartDefinition hood_r_f_r1 = hood_r2.addOrReplaceChild("hood_r_f_r1", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition hood_l12 = hood_r2.addOrReplaceChild("hood_l12", CubeListBuilder.create().texOffs(52, 2).addBox(-1.0F, 0.0F, -8.0F, 1.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

		PartDefinition hood_r_f_r2 = hood_l12.addOrReplaceChild("hood_r_f_r2", CubeListBuilder.create().texOffs(60, 10).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0524F, 0.0F, 0.0F));

		PartDefinition hood_l13 = hood_l12.addOrReplaceChild("hood_l13", CubeListBuilder.create().texOffs(44, 2).addBox(-4.0F, 0.0F, -8.0F, 4.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(54, 0).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

		PartDefinition hood_r_f_r3 = hood_l13.addOrReplaceChild("hood_r_f_r3", CubeListBuilder.create().texOffs(52, 12).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition hood_l14 = hood_l13.addOrReplaceChild("hood_l14", CubeListBuilder.create().texOffs(26, 2).addBox(-9.0F, 0.0F, -8.0F, 9.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0017F, -0.3491F, 0.0F));

		PartDefinition hood_tip2 = hood2.addOrReplaceChild("hood_tip2", CubeListBuilder.create().texOffs(63, 14).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition hood_tip_m2 = hood_tip2.addOrReplaceChild("hood_tip_m2", CubeListBuilder.create().texOffs(63, 18).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition hood_tip_b2 = hood_tip_m2.addOrReplaceChild("hood_tip_b2", CubeListBuilder.create().texOffs(65, 30).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition leg_left3 = base2.addOrReplaceChild("leg_left3", CubeListBuilder.create().texOffs(10, 71).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 1.0F, 0.5F, -0.4333F, -0.0946F, -0.0238F));

		PartDefinition leg_left4 = leg_left3.addOrReplaceChild("leg_left4", CubeListBuilder.create().texOffs(10, 84).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 13.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -3.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition leg_right3 = base2.addOrReplaceChild("leg_right3", CubeListBuilder.create().texOffs(0, 71).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-2.0F, 1.0F, 0.5F, -0.4333F, 0.0946F, 0.0238F));

		PartDefinition leg_right4 = leg_right3.addOrReplaceChild("leg_right4", CubeListBuilder.create().texOffs(0, 84).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 13.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -3.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cloak_b3 = base2.addOrReplaceChild("cloak_b3", CubeListBuilder.create().texOffs(32, 13).addBox(-3.0F, 0.0F, 0.0F, 7.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, 1.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition cloak_b4 = cloak_b3.addOrReplaceChild("cloak_b4", CubeListBuilder.create().texOffs(32, 17).addBox(-3.0F, 0.0F, 0.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cloak_s_l3 = base2.addOrReplaceChild("cloak_s_l3", CubeListBuilder.create().texOffs(42, 25).addBox(0.0F, 0.0F, -4.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

		PartDefinition cloak_s_l4 = cloak_s_l3.addOrReplaceChild("cloak_s_l4", CubeListBuilder.create().texOffs(42, 28).addBox(0.0F, 0.0F, -4.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition cloak_s_r3 = base2.addOrReplaceChild("cloak_s_r3", CubeListBuilder.create().texOffs(52, 25).mirror().addBox(0.0F, 0.0F, -4.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

		PartDefinition cloak_s_r4 = cloak_s_r3.addOrReplaceChild("cloak_s_r4", CubeListBuilder.create().texOffs(52, 28).mirror().addBox(0.0F, 0.0F, -4.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		if (entity instanceof Wight wight) {
			// for testing static speed with no AIs active
			//float sin = Mth.sin(ageInTicks * 0.5F) * 0.8F;
			//float cos = Mth.cos(ageInTicks * 0.5F) * 0.8F;

			float sin = wight.isHiding() ? 0 : Mth.sin(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
			float cos = wight.isHiding() ? 0 : Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;

			base2.zRot = 0F + sin * 0.25F * wight.getHidingAnimation(partialTick);

			torso3.xRot = convertDegtoRad(7.5F) + convertDegtoRad(9.5F) * wight.getHidingAnimation(partialTick) + sin * 0.25F;
			torso3.yRot = 0F - cos * 0.25F;
			torso3.zRot = 0F - sin * 0.25F;

			arm_left3.xRot = convertDegtoRad(-19.9299F) + convertDegtoRad(-4.9299F) * wight.getHidingAnimation(partialTick) + sin * 0.5F;
			arm_left3.yRot = convertDegtoRad(1.7082F) + convertDegtoRad(-13.7082F) * wight.getHidingAnimation(partialTick);
			arm_left3.zRot = convertDegtoRad(0.3001F) + convertDegtoRad(-44.6999F) * wight.getHidingAnimation(partialTick) + cos * 0.25F;

			arm_left4.xRot = convertDegtoRad(-10F) + convertDegtoRad(-60F) * wight.getHidingAnimation(partialTick);

			arm_right3.xRot = convertDegtoRad(-19.9299F) + convertDegtoRad(-4.9299F) * wight.getHidingAnimation(partialTick) - sin * 0.75F;
			arm_right3.yRot = convertDegtoRad(-1.7082F) + convertDegtoRad(13.7082F) * wight.getHidingAnimation(partialTick);
			arm_right3.zRot = convertDegtoRad(-0.3001F) + convertDegtoRad(44.6999F) * wight.getHidingAnimation(partialTick) - cos * 0.5F;

			arm_right4.xRot = convertDegtoRad(-10F) + convertDegtoRad(-60F) * wight.getHidingAnimation(partialTick);

			leg_left3.xRot = -0.4333F - sin;
			leg_left3.yRot = convertDegtoRad(-5.4196F) + convertDegtoRad(-7.5804F) * wight.getHidingAnimation(partialTick);
			leg_left3.zRot = convertDegtoRad(1.3664F) + convertDegtoRad(-18.6336F) * wight.getHidingAnimation(partialTick);

			leg_left4.xRot = 0.6981F + cos * 0.5F;
			leg_left4.yRot = 0F + convertDegtoRad(5F) * wight.getHidingAnimation(partialTick);
			leg_left4.zRot = 0F + convertDegtoRad(15F) * wight.getHidingAnimation(partialTick);

			leg_right3.xRot = -0.4333F + sin;
			leg_right3.yRot = convertDegtoRad(5.4196F) + convertDegtoRad(7.5804F) * wight.getHidingAnimation(partialTick);
			leg_right3.zRot = convertDegtoRad(-1.3664F) + convertDegtoRad(18.6336F) * wight.getHidingAnimation(partialTick);

			leg_right4.xRot = 0.6981F - cos * 0.5F;
			leg_right4.yRot = 0F + convertDegtoRad(-5F) * wight.getHidingAnimation(partialTick);
			leg_right4.zRot = 0F + convertDegtoRad(-15F) * wight.getHidingAnimation(partialTick);

			neck2.xRot = convertDegtoRad(40F) + convertDegtoRad(-43F) * wight.getHidingAnimation(partialTick) - sin * 0.25F;
			neck2.yRot = 0F + cos * 0.25F;
			neck2.zRot = 0F + sin * 0.25F;

			hood_tip2.xRot = convertDegtoRad(7.5F) + convertDegtoRad(7.5F) * wight.getHidingAnimation(partialTick);

			cloak_s_l3.zRot = -0.0436F - convertDegtoRad(30F) * wight.getHidingAnimation(partialTick) - sin * 0.125F;
			cloak_s_r3.zRot = 0.0436F + convertDegtoRad(30F) * wight.getHidingAnimation(partialTick) + sin * 0.125F;

			jaw2.xRot = 0F + convertDegtoRad(30F) * wight.getHidingAnimation(partialTick) - sin * 0.25F;

		} else {
			neck2.xRot = convertDegtoRad(40F);
			neck2.yRot = 0F;
			neck2.zRot = 0F;
			jaw2.xRot = 0;
		}
	}

	public float convertDegtoRad(float angle) {
		return angle * Mth.DEG_TO_RAD;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		if (this.renderHeadOnly) {
			for (ModelPart part : this.headPieces) {
				part.render(stack, consumer, light, overlay, color);
			}
		} else {
			base2.render(stack, consumer, light, overlay, color);
		}
	}

	public WightModel<T> setRenderHeadOnly(boolean headOnly) {
		this.renderHeadOnly = headOnly;
		return this;
	}
}