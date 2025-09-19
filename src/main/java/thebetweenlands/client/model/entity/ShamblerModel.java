package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Shambler;

public class ShamblerModel extends MowzieModelBase<Shambler> {

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart rightHindLeg1;
	private final ModelPart leftHindLeg1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart tail6;
	private final ModelPart rightHindLeg2;
	private final ModelPart rightHindLeg3;
	private final ModelPart rightFoot;
	private final ModelPart leftHindLeg2;
	private final ModelPart leftHindLeg3;
	private final ModelPart leftFoot;
	private final ModelPart mouthArm1;
	private final ModelPart mouthArm2;
	private final ModelPart mouthArm3;
	private final ModelPart mouthArm4;
	private final ModelPart mouthArm1ext;
	private final ModelPart mouthArm2ext;
	private final ModelPart mouthArm3ext;
	private final ModelPart mouthArm4ext;

	private final ModelPart tongue;
	private final ModelPart tongueEnd;

	public ShamblerModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body_base");
		var body2 = this.body.getChild("body2");
		this.rightHindLeg1 = body2.getChild("hindleg_right1");
		this.leftHindLeg1 = body2.getChild("hindleg_left1");
		this.tail2 = body2.getChild("body3").getChild("weird_butt").getChild("surprise_tail").getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.tail4 = this.tail3.getChild("tail4");
		this.tail5 = this.tail4.getChild("tail5");
		this.tail6 = this.tail5.getChild("tail6");
		this.head = this.body.getChild("head1");
		this.mouthArm1 = this.head.getChild("mouth_arm1a");
		this.mouthArm2 = this.head.getChild("mouth_arm2a");
		this.mouthArm3 = this.head.getChild("mouth_arm3a");
		this.mouthArm4 = this.head.getChild("mouth_arm4a");
		this.mouthArm1ext = this.mouthArm1.getChild("mouth_arm1b").getChild("mouth_arm1c");
		this.mouthArm2ext = this.mouthArm2.getChild("mouth_arm2b").getChild("mouth_arm2c");
		this.mouthArm3ext = this.mouthArm3.getChild("mouth_arm3b").getChild("mouth_arm3c");
		this.mouthArm4ext = this.mouthArm4.getChild("mouth_arm4b").getChild("mouth_arm4c");
		this.rightHindLeg2 = this.rightHindLeg1.getChild("hindleg_right2");
		this.rightHindLeg3 = this.rightHindLeg2.getChild("hindleg_right3");
		this.rightFoot = this.rightHindLeg3.getChild("foot_right1");
		this.leftHindLeg2 = this.leftHindLeg1.getChild("hindleg_left2");
		this.leftHindLeg3 = this.leftHindLeg2.getChild("hindleg_left3");
		this.leftFoot = this.leftHindLeg3.getChild("foot_left1");
		this.tongue = root.getChild("tongue_part");
		this.tongueEnd = root.getChild("tongue_end");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		PartDefinition body_base = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 8.0F, 7.0F),
			PartPose.offset(0.0F, 8.2F, -3.0F));

		PartDefinition body2 = body_base.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 8.0F, 5.0F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, -0.3643F, 0.0F, 0.0F));

		PartDefinition body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-4.01F, 0.0F, 0.0F, 8F, 8F, 3F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.2731F, 0.0F, 0.0F));

		PartDefinition weird_butt = body3.addOrReplaceChild("weird_butt", CubeListBuilder.create()
				.texOffs(0, 42).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 4.0F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.5009F, 0.0F, 0.0F));

		PartDefinition surprise_tail = weird_butt.addOrReplaceChild("surprise_tail", CubeListBuilder.create()
				.texOffs(0, 53).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 3.0F),
			PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, 0.6829F, 0.0F, 0.0F));

		PartDefinition tail2 = surprise_tail.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 61).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 5.0F),
			PartPose.offsetAndRotation(-0.015F, 0.0F, 3.0F, 0.5009F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(0, 71).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 5.0F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.7741F, 0.0F, 0.0F));

		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(0, 80).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 4.0F),
			PartPose.offsetAndRotation(-0.015F, 0.0F, 5.0F, 0.8652F, 0.0F, 0.0F));

		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(0, 88).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 4.0F),
			PartPose.offsetAndRotation(-0.015F, 0.0F, 4.0F, 0.8652F, 0.0F, 0.0F));

		tail5.addOrReplaceChild("tail6", CubeListBuilder.create()
				.texOffs(0, 96).addBox(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F),
			PartPose.offsetAndRotation(-0.015F, 0.0F, 4.0F, 0.8196F, 0.0F, 0.0F));

		PartDefinition hindleg_right1 = body2.addOrReplaceChild("hindleg_right1", CubeListBuilder.create()
				.texOffs(40, 0).addBox(-2.0F, -3.0F, -5.0F, 3.0F, 6.0F, 7.0F),
			PartPose.offsetAndRotation(-4.0F, 5.0F, 3.0F, 0.5235987755982988F, 0F, 0F));

		PartDefinition hindleg_right2 = hindleg_right1.addOrReplaceChild("hindleg_right2", CubeListBuilder.create()
				.texOffs(40, 14).addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 4.0F),
			PartPose.offsetAndRotation(-1.99F, 3.0F, -5.0F, 0.4363323129985824F, 0.0F, 0.0F));

		PartDefinition hindleg_right3 = hindleg_right2.addOrReplaceChild("hindleg_right3", CubeListBuilder.create()
				.texOffs(40, 25).addBox(0.02F, 0.0F, -4.0F, 3F, 4F, 4F),
			PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, -1.0471975511965976F, 0.0F, 0.0F));

		PartDefinition foot_right1 = hindleg_right3.addOrReplaceChild("foot_right1", CubeListBuilder.create()
				.texOffs(40, 34).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 4.0F),
			PartPose.offsetAndRotation(1.5F, 4.0F, -4.0F, 0.4886921905584123F, 0.0F, 0.0F));

		foot_right1.addOrReplaceChild("toe_right1", CubeListBuilder.create()
				.texOffs(40, 41).addBox(-2.0F, 0.0F, -4.0F, 2F, 2F, 5F),
			PartPose.offsetAndRotation(-0.5F, 0.0F, 2.0F, -0.045553093477052F, 0.18203784098300857F, -0.091106186954104F));

		foot_right1.addOrReplaceChild("toe_right2", CubeListBuilder.create()
				.texOffs(40, 49).addBox(-1.0F, 0.0F, -2.5F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		foot_right1.addOrReplaceChild("toe_right3", CubeListBuilder.create()
				.texOffs(40, 55).addBox(0.0F, 0.0F, -5.0F, 2F, 2F, 6F),
			PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F));

		PartDefinition hindleg_left1 = body2.addOrReplaceChild("hindleg_left1", CubeListBuilder.create()
				.texOffs(61, 0).addBox(-1.0F, -3.0F, -5.0F, 3F, 6F, 7F),
			PartPose.offsetAndRotation(4.0F, 5.0F, 3.0F, 0.5235987755982988F, 0.0F, 0.0F));

		PartDefinition hindleg_left2 = hindleg_left1.addOrReplaceChild("hindleg_left2", CubeListBuilder.create()
				.texOffs(61, 14).addBox(0.0F, 0.0F, 0.0F, 3F, 6F, 4F),
			PartPose.offsetAndRotation(-1.02F, 3.0F, -5.0F, 0.4363323129985824F, 0.0F, 0.0F));

		PartDefinition hindleg_left3 = hindleg_left2.addOrReplaceChild("hindleg_left3", CubeListBuilder.create()
				.texOffs(61, 25).addBox(-3.02F, 0.0F, -4.0F, 3F, 4F, 4F),
			PartPose.offsetAndRotation(3.0F, 6.0F, 4.0F, -1.0471975511965976F, 0.0F, 0.0F));

		PartDefinition foot_left1 = hindleg_left3.addOrReplaceChild("foot_left1", CubeListBuilder.create()
				.texOffs(61, 34).addBox(-1.5F, 0.0F, 0.0F, 3F, 2F, 4F),
			PartPose.offsetAndRotation(-1.5F, 4.0F, -4.0F, 0.4886921905584123F, 0.0F, 0.0F));

		foot_left1.addOrReplaceChild("toe_left1", CubeListBuilder.create()
				.texOffs(61, 41).addBox(0.0F, 0.0F, -4.0F, 2F, 2F, 5F),
			PartPose.offsetAndRotation(0.5F, 0.0F, 2.0F, -0.045553093477052F, -0.18203784098300857F, 0.091106186954104F));

		foot_left1.addOrReplaceChild("toe_left2", CubeListBuilder.create()
				.texOffs(61, 49).addBox(-1.0F, 0.0F, -2.5F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		foot_left1.addOrReplaceChild("toe_left3", CubeListBuilder.create()
				.texOffs(61, 55).addBox(-2.0F, 0.0F, -5.0F, 2F, 2F, 6F),
			PartPose.offsetAndRotation(-0.5F, 0.0F, 2.0F, -0.045553093477052F, 0.18203784098300857F, -0.091106186954104F));

		PartDefinition head1 = body_base.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(85, 0).addBox(-4.0F, -3.5F, -6.0F, 8F, 8F, 7F),
			PartPose.offset(0.0F, 1.0F, 2.0F));

		head1.addOrReplaceChild("mouth", CubeListBuilder.create()
				.texOffs(85, 16).addBox(-3.0F, 0.0F, -2.0F, 6F, 6F, 2F),
			PartPose.offset(0.0F, -2.5F, -6.0F));

		PartDefinition mouth_arm1a = head1.addOrReplaceChild("mouth_arm1a", CubeListBuilder.create()
				.texOffs(85, 25).addBox(0.0F, 0.0F, -4.0F, 3F, 3F, 5F),
			PartPose.offsetAndRotation(-3.9F, -3.3F, -5.0F, 0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm1b = mouth_arm1a.addOrReplaceChild("mouth_arm1b", CubeListBuilder.create()
				.texOffs(85, 34).addBox(0.0F, 0.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.5F, 0.5F, -4.0F, 0.36425021489121656F, -0.36425021489121656F, -0.045553093477052F));

		mouth_arm1b.addOrReplaceChild("mouth_arm1c", CubeListBuilder.create()
				.texOffs(85, 40).addBox(0.0F, 0.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, 0.01F, -3.0F, 0.0F, -0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm2a = head1.addOrReplaceChild("mouth_arm2a", CubeListBuilder.create()
				.texOffs(102, 25).addBox(-3.0F, 0.0F, -4.0F, 3F, 3F, 5F),
			PartPose.offsetAndRotation(3.9F, -3.3F, -5.0F, 0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm2b = mouth_arm2a.addOrReplaceChild("mouth_arm2b", CubeListBuilder.create()
				.texOffs(102, 34).addBox(-2.0F, 0.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(-0.5F, 0.5F, -4.0F, 0.36425021489121656F, 0.36425021489121656F, 0.045553093477052F));

		mouth_arm2b.addOrReplaceChild("mouth_arm2c", CubeListBuilder.create()
				.texOffs(102, 40).addBox(-2.0F, 0.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, 0.01F, -3.0F, 0.0F, 0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm3a = head1.addOrReplaceChild("mouth_arm3a", CubeListBuilder.create()
				.texOffs(85, 46).addBox(0.0F, -3.0F, -4.0F, 3F, 3F, 5F),
			PartPose.offsetAndRotation(-3.9F, 4.3F, -5.0F, -0.08726646259971647F, 0.0F, 0.0F));

		PartDefinition mouth_arm3b = mouth_arm3a.addOrReplaceChild("mouth_arm3b", CubeListBuilder.create()
				.texOffs(85, 55).addBox(0.0F, -2.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.5F, -0.5F, -4.0F, -0.36425021489121656F, -0.36425021489121656F,
				0.045553093477052F));

		mouth_arm3b.addOrReplaceChild("mouth_arm3c", CubeListBuilder.create()
				.texOffs(85, 61).addBox(0.0F, -2.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, -0.01F, -3.0F, 0.0F, -0.5462880558742251F, 0.0F));

		PartDefinition mouth_arm4a = head1.addOrReplaceChild("mouth_arm4a", CubeListBuilder.create()
				.texOffs(102, 46).addBox(-3.0F, -3.0F, -4.0F, 3F, 3F, 5F),
			PartPose.offsetAndRotation(3.9F, 4.3F, -5.0F, -0.0890117918517108F, 0.0F, 0.0F));

		PartDefinition mouth_arm4b = mouth_arm4a.addOrReplaceChild("mouth_arm4b", CubeListBuilder.create()
				.texOffs(102, 55).addBox(-2.0F, -2.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(-0.5F, -0.5F, -4.0F, -0.36425021489121656F, 0.36425021489121656F,
				-0.045553093477052F));

		mouth_arm4b.addOrReplaceChild("mouth_arm4c", CubeListBuilder.create()
				.texOffs(102, 61).addBox(-2.0F, -2.0F, -3.0F, 2F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, -0.01F, -3.0F, 0.0F, 0.5462880558742251F, 0.0F));

		PartDefinition cranialthing1 = head1.addOrReplaceChild("cranialthing1", CubeListBuilder.create()
				.texOffs(85, 74).addBox(-4.0F, -2.0F, 0.0F, 8F, 2F, 3F),
			PartPose.offsetAndRotation(0.0F, -1.5F, 1.0F, 0.18203784098300857F, 0.0F, 0.0F));

		cranialthing1.addOrReplaceChild("cranialthing2", CubeListBuilder.create()
				.texOffs(85, 80).addBox(-3.0F, -2.0F, 0.0F, 6F, 2F, 2F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("tongue_part", CubeListBuilder.create()
				.texOffs(85, 86).addBox(-1.0F, -1.0F, -2.0F, 2F, 2F, 4F),
			PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition tongue_end = partDefinition.addOrReplaceChild("tongue_end", CubeListBuilder.create()
				.texOffs(85, 86).addBox(-1.0F, -1.0F, -2.0F, 2F, 2F, 4F),
			PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition tongue1 = tongue_end.addOrReplaceChild("tongue1", CubeListBuilder.create()
				.texOffs(85, 93).addBox(-1.5F, 0.0F, -2.0F, 3F, 2F, 2F),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.5009094953223726F, 0.0F, 0.0F));
		tongue1.addOrReplaceChild("teeth1", CubeListBuilder.create()
				.texOffs(85, 98).addBox(-1.5F, -2.0F, -2.0F, 3F, 2F, 2F),
			PartPose.offset(0.0F, 2.0F, -2.0F));

		PartDefinition tongue2 = tongue_end.addOrReplaceChild("tongue2", CubeListBuilder.create()
				.texOffs(96, 93).addBox(-1.5F, -2.0F, -2.0F, 3F, 2F, 2F),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5009094953223726F, 0.0F, 0.0F));
		tongue2.addOrReplaceChild("teeth2", CubeListBuilder.create()
				.texOffs(96, 98).addBox(-1.5F, 0.0F, -2.0F, 3F, 2F, 2F),
			PartPose.offset(0.0F, -2.0F, -2.0F));

		return LayerDefinition.create(definition, 128, 128);
	}

	@Override
	public ModelPart root() {
		return this.body;
	}

	public void renderTonguePart(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		stack.mulPose(Axis.YP.rotationDegrees(180F));
		this.tongue.render(stack, consumer, light, overlay, colour);
	}

	public void renderTongueEnd(PoseStack stack, VertexConsumer consumer, int light, int overlay, int colour) {
		stack.mulPose(Axis.YP.rotationDegrees(180F));
		this.tongueEnd.render(stack, consumer, light, overlay, colour);
	}

	@Override
	public void setupAnim(Shambler entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float animation = (float) (Math.cos((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F);
		float animation2 = (float) (Math.sin((limbSwing * 1.2F) * 0.75F) * 0.3F * limbSwingAmount * 0.5F);
		float flap = (float) (Math.sin(ageInTicks * 0.3F) * 0.8F);
		float smoothedAngle = entity.smoothedAngle(partialTick);
		float headX = entity.getXRot() / Mth.RAD_TO_DEG;

		this.leftHindLeg1.xRot = 0.5235987755982988F - (animation2 * 14F) + flap * 0.1F - flap * 0.075F / Mth.RAD_TO_DEG;
		this.rightHindLeg1.xRot = 0.5235987755982988F - (animation * 14F) + flap * 0.1F - flap * 0.075F / Mth.RAD_TO_DEG;

		this.leftHindLeg2.xRot = 0.4363323129985824F + (animation2 * 8F) - flap * 0.05F + flap * 0.075F / Mth.RAD_TO_DEG;
		this.rightHindLeg2.xRot = 0.4363323129985824F + (animation * 8F) - flap * 0.05F + flap * 0.075F / Mth.RAD_TO_DEG;

		this.leftHindLeg3.xRot = -1.0471975511965976F + (animation2 * 4F) + flap * 0.05F - flap * 0.075F / Mth.RAD_TO_DEG;
		this.rightHindLeg3.xRot = -1.0471975511965976F + (animation * 4F) + flap * 0.05F - flap * 0.075F / Mth.RAD_TO_DEG;

		this.leftFoot.xRot = 0.4886921905584123F - (animation2 * 2F) - flap * 0.05F - flap * 0.075F / Mth.RAD_TO_DEG;
		this.rightFoot.xRot = 0.4886921905584123F - (animation * 2F) - flap * 0.05F - flap * 0.075F / Mth.RAD_TO_DEG;

		this.body.xRot = -(animation2 * 3F) - flap * 0.05F;
		this.head.xRot = headX + (animation2 * 4F) + flap * 0.1F;

		this.body.zRot = -(animation2 * 2F);
		this.head.zRot = (animation2 * 4F);

		this.leftHindLeg1.zRot = (animation2 * 2F);
		this.rightHindLeg1.zRot = (animation * 2F);

		this.mouthArm1.yRot = smoothedAngle / Mth.RAD_TO_DEG * 4F - (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		this.mouthArm1.xRot = 0.08726646259971647F - smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		this.mouthArm2.yRot = -smoothedAngle / Mth.RAD_TO_DEG * 4F + (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		this.mouthArm2.xRot = 0.08726646259971647F - smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		this.mouthArm3.yRot = smoothedAngle / Mth.RAD_TO_DEG * 4F - (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		this.mouthArm3.xRot = -0.08726646259971647F + smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		this.mouthArm4.yRot = -smoothedAngle / Mth.RAD_TO_DEG * 4F + (!entity.jawsAreOpen() ? 0F : flap * 0.3F);
		this.mouthArm4.xRot = -0.08726646259971647F + smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.1F);

		this.mouthArm1ext.yRot = -0.5462880558742251F + smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		this.mouthArm2ext.yRot = 0.5462880558742251F - smoothedAngle / Mth.RAD_TO_DEG * 3F + (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		this.mouthArm3ext.yRot = -0.5462880558742251F + smoothedAngle / Mth.RAD_TO_DEG * 3F - (!entity.jawsAreOpen() ? 0F : flap * 0.5F);
		this.mouthArm4ext.yRot = 0.5462880558742251F - smoothedAngle / Mth.RAD_TO_DEG * 3F + (!entity.jawsAreOpen() ? 0F : flap * 0.5F);

		this.tail2.xRot = 0.5009094953223726F - Mth.sin((animation) * 0.5009094953223726F) - 1F / 9 * entity.getTongueLength() * 0.5F;
		this.tail3.xRot = 0.7740535232594852F - Mth.sin((animation) * 0.7740535232594852F) - 1F / 9 * entity.getTongueLength() * 0.5F;
		this.tail4.xRot = 0.8651597102135892F - Mth.sin((animation) * 0.8651597102135892F) - 1F / 9 * entity.getTongueLength();
		this.tail5.xRot = 0.8651597102135892F - Mth.sin((animation) * 0.8651597102135892F) - 1F / 9 * entity.getTongueLength();
		this.tail6.xRot = 0.8196066167365371F - Mth.sin((animation) * 0.8196066167365371F) - 1F / 9 * entity.getTongueLength() * 0.75F;
	}
}