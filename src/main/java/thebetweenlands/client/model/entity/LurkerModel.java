package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Lurker;

public class LurkerModel extends MowzieModelBase<Lurker> {

	private final ModelPart root;
	private final ModelPart trunk;
	private final ModelPart head;
	private final ModelPart mandible;
	private final ModelPart forefinLeftProximal;
	private final ModelPart forefinRightProximal;
	private final ModelPart forefinLeftDistal;
	private final ModelPart forefinRightDistal;
	private final ModelPart hindfinLeft;
	private final ModelPart hindfinRight;
	private final ModelPart[] tail;

	public LurkerModel(ModelPart root) {
		this.root = root;
		this.trunk = root.getChild("trunk");
		this.head = this.trunk.getChild("head");
		this.mandible = this.head.getChild("mandible");
		this.forefinLeftProximal = this.trunk.getChild("left_forefin_proximal");
		this.forefinRightProximal = this.trunk.getChild("right_forefin_proximal");
		this.forefinLeftDistal = this.forefinLeftProximal.getChild("left_forefin_distal");
		this.forefinRightDistal = this.forefinRightProximal.getChild("right_forefin_distal");
		this.hindfinLeft = this.trunk.getChild("left_hindfin");
		this.hindfinRight = this.trunk.getChild("right_hindfin");
		var lumbar = this.trunk.getChild("lumbar_vertebrae");
		var tail1 = lumbar.getChild("tail_1");
		var tail2 = tail1.getChild("tail_2");
		var tail3 = tail2.getChild("tail_3");
		this.tail = new ModelPart[]{lumbar, tail1, tail2, tail3};
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var trunk = partDefinition.addOrReplaceChild("trunk", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, 0.0F, 0.0F, 10, 10, 22)
				.texOffs(0, 33).addBox(-5.5F, -0.5F, 3.0F, 11, 8, 16)
				.texOffs(0, 58).addBox(-3.5F, 1.5F, -1.5F, 7, 7, 2),
			PartPose.offset(0.0F, 11.0F, -11.0F));

		trunk.addOrReplaceChild("first_bump", CubeListBuilder.create()
				.texOffs(66, 0).addBox(-1.5F, -2.0F, 4.0F, 3, 2, 3),
			PartPose.rotation(-0.0940825F, 0.0F, 0.0F));

		trunk.addOrReplaceChild("second_bump", CubeListBuilder.create()
				.texOffs(66, 7).addBox(-1.0F, -2.0F, 10.0F, 2, 2, 2),
			PartPose.rotation(-0.0766374F, 0.0F, 0.0F));

		trunk.addOrReplaceChild("third_bump", CubeListBuilder.create()
				.texOffs(66, 13).addBox(-1.0F, -2.0F, 15.0F, 2, 2, 2),
			PartPose.rotation(-0.0591841F, 0.0F, 0.0F));

		var head = trunk.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(95, 0).addBox(-4.0F, -2.0F, -6.0F, 8, 7, 6),
			PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.0743572F, 0.0F, 0.0F));

		head.addOrReplaceChild("maxilla", CubeListBuilder.create()
				.texOffs(95, 36).addBox(-3.5F, 0.0F, 0.0F, 7, 3, 12)
				.texOffs(129, 53).addBox(-1.0F, -0.5F, 3.0F, 2, 1, 2)
				.texOffs(129, 57).addBox(-0.5F, -0.5F, 8.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -0.2F, -16.5F, 0.1115297F, 0.0F, 0.0F));

		var mandible = head.addOrReplaceChild("mandible", CubeListBuilder.create()
				.texOffs(95, 15).addBox(-4.5F, -2.0F, -15F, 9, 4, 16),
			PartPose.offset(0.0F, 3.0F, -2.0F));

		mandible.addOrReplaceChild("right_front_tooth", CubeListBuilder.create()
				.texOffs(95, 53).addBox(3.5F, -7.0F, -15.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.4F, 0.0F, 0.1487144F));

		mandible.addOrReplaceChild("left_front_tooth", CubeListBuilder.create()
				.texOffs(101, 53).addBox(-4.5F, -7.0F, -15.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.4F, 0.0F, -0.1487144F));

		mandible.addOrReplaceChild("right_middle_tooth", CubeListBuilder.create()
				.texOffs(107, 53).addBox(2.5F, -2.0F, -14.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.2230717F, -0.0743572F, 0.1487144F));

		mandible.addOrReplaceChild("left_middle_tooth", CubeListBuilder.create()
				.texOffs(112, 53).addBox(-3.5F, -2.0F, -14.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.2230717F, 0.0743572F, -0.1487144F));

		mandible.addOrReplaceChild("right_back_tooth", CubeListBuilder.create()
				.texOffs(118, 53).addBox(3.5F, -2.0F, -11.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.2602503F, 0.0F, 0.1487144F));

		mandible.addOrReplaceChild("left_middle_tooth", CubeListBuilder.create()
				.texOffs(123, 53).addBox(-4.5F, -2.0F, -11.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 2.0F, 0.2602503F, 0.0F, -0.1487144F));

		var vertebrae = trunk.addOrReplaceChild("lumbar_vertebrae", CubeListBuilder.create()
				.texOffs(20, 58).addBox(-4.0F, -4.0F, 0.0F, 8, 8, 6),
			PartPose.offsetAndRotation(0.0F, 4.0F, 21.75F, -0.0371786F, 0.0F, 0.0F));

		var tail1 = vertebrae.addOrReplaceChild("tail_1", CubeListBuilder.create()
				.texOffs(0, 74).addBox(-3.0F, -3.5F, 0.0F, 6, 7, 7),
			PartPose.offsetAndRotation(0.0F, -0.5F, 5.75F, -0.0371786F, 0.0F, 0.0F));

		var tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create()
				.texOffs(0, 90).addBox(-2.0F, -2.5F, 0.0F, 4, 5, 8),
			PartPose.offsetAndRotation(0.0F, -0.25F, 6.75F, -0.0743572F, 0.0F, 0.0F));

		var tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create()
				.texOffs(0, 105).addBox(-1.5F, -2.0F, 0.0F, 3, 4, 9),
			PartPose.offset(0.0F, -0.1F, 7.75F));

		tail3.addOrReplaceChild("tail_fin_1", CubeListBuilder.create()
				.texOffs(28, 74).addBox(-0.5F, -9.0F, -4.0F, 1, 9, 4),
			PartPose.offsetAndRotation(0.0F, 1.0F, 9.5F, -0.7807509F, 0.0F, 0.0F));

		tail3.addOrReplaceChild("tail_fin_2", CubeListBuilder.create()
				.texOffs(40, 74).addBox(-0.5F, 0.0F, 0.0F, 1, 7, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 0.6320364F, 0.0F, 0.0F));

		tail3.addOrReplaceChild("tail_fin_3", CubeListBuilder.create()
				.texOffs(49, 74).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 1.0F, 0.4461433F, 0.0F, 0.0F));

		var leftProximal = trunk.addOrReplaceChild("left_forefin_proximal", CubeListBuilder.create()
				.texOffs(155, 0).addBox(0F, 0F, -1F, 5, 2, 3),
			PartPose.offsetAndRotation(4F, 7F, 3F, -0.2602503F, 0.2230717F, 0.4461433F));

		var rightProximal = trunk.addOrReplaceChild("right_forefin_proximal", CubeListBuilder.create()
				.texOffs(172, 0).addBox(0F, 0F, -2F, 5, 2, 3),
			PartPose.offsetAndRotation(-4F, 7F, 3F, 0.260246F, 2.918522F, -0.4461411F));

		leftProximal.addOrReplaceChild("left_forefin_distal", CubeListBuilder.create()
				.texOffs(155, 7).addBox(-1F, 0.5F, -1F, 5, 1, 8)
				.texOffs(155, 30).addBox(2.5F, 0.3F, 6F, 1, 1, 2)
				.texOffs(170, 30).addBox(0.5F, 0.3F, 6.5F, 1, 1, 1),
			PartPose.offsetAndRotation(3, 0, 0, 0, 0.2F, 0));

		rightProximal.addOrReplaceChild("right_forefin_distal", CubeListBuilder.create()
				.texOffs(182, 7).addBox(-4F, 0.5F, -1F, 5, 1, 8)
				.texOffs(162, 30).addBox(-3.5F, 0.3F, 6F, 1, 1, 2)
				.texOffs(176, 30).addBox(-1.5F, 0.3F, 6.5F, 1, 1, 1),
			PartPose.offsetAndRotation(3, 0, 0, 0, -0.2F + Mth.PI, 0));

		trunk.addOrReplaceChild("left_hindfin", CubeListBuilder.create()
				.texOffs(155, 18).addBox(0F, -1F, -0.5F, 4, 1, 8),
			PartPose.offsetAndRotation(4F, 9F, 16F, -0.260246F, 0.4461411F, 0.4461411F));

		trunk.addOrReplaceChild("right_hindfin", CubeListBuilder.create()
				.texOffs(181, 18).addBox(-4F, -1F, -0.5F, 4, 1, 8),
			PartPose.offsetAndRotation(-4F, 9F, 16F, -0.260246F, -0.4461411F, -0.4461411F));

		return LayerDefinition.create(definition, 256, 128);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Lurker entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		netHeadYaw = Mth.clamp(netHeadYaw, -60, 60);
		this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
		this.head.xRot += headPitch * Mth.DEG_TO_RAD;
	}

	@Override
	public void prepareMobModel(Lurker entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float mouthOpen = entity.getMouthOpen(partialTick);
		float yaw = entity.getTailYaw(partialTick) * Mth.DEG_TO_RAD * 0.2F;
		float pitch = entity.getTailPitch(partialTick) * Mth.DEG_TO_RAD * 0.2F;
		this.forefinLeftProximal.xRot = Mth.cos(limbSwing * 0.8F - Mth.PI / 8) * limbSwingAmount * 1.5F - 0.2602503F;
		this.forefinLeftProximal.yRot = Mth.cos(limbSwing * 0.8F) * limbSwingAmount * 1.7F + 0.2230717F;
		this.forefinLeftProximal.zRot = Mth.sin(limbSwing * 0.8F) * limbSwingAmount * 0.7F + 0.4461433F - limbSwingAmount * 0.7F;
		this.forefinLeftDistal.yRot = Mth.sin(limbSwing * 0.8F + Mth.PI / 4) * limbSwingAmount * 0.7F + 0.2F;
		this.hindfinLeft.xRot = Mth.cos(limbSwing * 0.8F - Mth.PI / 2) * limbSwingAmount * 1.0F - 0.260246F + limbSwingAmount * 0.8F;
		this.hindfinLeft.yRot = Mth.sin(limbSwing * 0.8F - Mth.PI / 2) * limbSwingAmount * 1.2F + 0.4461411F;
		this.forefinRightProximal.xRot = Mth.cos(limbSwing * 0.8F - Mth.PI / 8) * limbSwingAmount * 1.5F + 0.2602503F;
		this.forefinRightProximal.yRot = Mth.cos(limbSwing * 0.8F) * limbSwingAmount * 1.7F + 2.918522F;
		this.forefinRightProximal.zRot = Mth.sin(limbSwing * 0.8F) * limbSwingAmount * 0.7F - 0.4461433F + limbSwingAmount * 0.7F;
		this.forefinRightDistal.yRot = Mth.sin(limbSwing * 0.8F + Mth.PI / 4) * limbSwingAmount * 0.7F - 0.2F + Mth.PI;
		this.hindfinRight.xRot = Mth.sin(limbSwing * 0.8F + Mth.PI / 2) * limbSwingAmount * 1.0F - 0.260246F + limbSwingAmount * 0.8F;
		this.hindfinRight.yRot = Mth.cos(limbSwing * 0.8F + Mth.PI / 2) * limbSwingAmount * 1.2F - 0.4461411F;
		this.trunk.zRot = Mth.sin(limbSwing * 0.8F) * limbSwingAmount * 0.1F;
		this.head.zRot = -this.trunk.zRot;
		this.head.xRot = -mouthOpen * 0.4F + 0.0743572F;
		this.mandible.xRot = mouthOpen * 0.4F;
		for (int i = 0; i < this.tail.length; i++) {
			ModelPart segment = this.tail[i];
			segment.xRot = segment.getInitialPose().xRot + pitch;
			segment.yRot = yaw + Mth.sin(limbSwing * 0.4F - i * 1.6F) * limbSwingAmount * ((i / (float) this.tail.length * 2 + 0.1F)) * 0.6F;
			segment.zRot = -this.trunk.zRot / this.tail.length;
		}
	}
}
