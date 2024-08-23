package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.common.entities.BubblerCrab;

public class BubblerCrabModel extends HierarchicalModel<BubblerCrab> {

	private final ModelPart root;

	private final ModelPart leftFrontLeg;
	private final ModelPart leftMiddleLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart leftArm;
	private final ModelPart rightFrontLeg;
	private final ModelPart rightMiddleLeg;
	private final ModelPart rightBackLeg;
	private final ModelPart rightArm;
	private final ModelPart leftClawTop;
	private final ModelPart leftClawBottom;
	private final ModelPart rightClawTop;
	private final ModelPart rightClawBottom;

	public BubblerCrabModel(ModelPart root) {
		this.root = root;
		var body = root.getChild("body_base").getChild("body");
		this.rightArm = body.getChild("right_arm_1");
		this.leftArm = body.getChild("left_arm_1");
		this.rightClawTop = this.rightArm.getChild("right_arm_2").getChild("right_claw_top");
		this.rightClawBottom = this.rightClawTop.getChild("right_claw_bottom");
		this.leftClawTop = this.leftArm.getChild("left_arm_2").getChild("left_claw_top");
		this.leftClawBottom = this.leftClawTop.getChild("left_claw_bottom");
		this.leftFrontLeg = body.getChild("left_front_leg");
		this.leftMiddleLeg = body.getChild("left_middle_leg");
		this.leftBackLeg = body.getChild("left_back_leg");
		this.rightFrontLeg = body.getChild("right_front_leg");
		this.rightMiddleLeg = body.getChild("right_middle_leg");
		this.rightBackLeg = body.getChild("right_back_leg");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.addBox(-2.0F, -3.0F, 0.0F, 4, 3, 2),
			PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, -0.40980330836826856F, 0.0F, 0.0F));

		var body = base.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.31869712141416456F, 0.0F, 0.0F));

		body.addOrReplaceChild("left_mouth", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(2.0F, 1.0F, -3.0F, 0.0F, -0.36425021489121656F, -0.091106186954104F));

		body.addOrReplaceChild("right_mouth", CubeListBuilder.create()
				.texOffs(7, 13)
				.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(-2.0F, 1.0F, -3.0F, 0.0F, 0.36425021489121656F, 0.091106186954104F));

		body.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.5F, 1.0F, -3.0F, 0.31869712141416456F, 0.0F, 0.22759093446006054F));

		body.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(5, 17)
				.addBox(-1.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-0.5F, 1.0F, -3.0F, 0.31869712141416456F, 0.0F, -0.22759093446006054F));

		var leftHorn = body.addOrReplaceChild("left_horn_1", CubeListBuilder.create()
				.texOffs(0, 21)
				.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.18203784098300857F));

		leftHorn.addOrReplaceChild("left_horn_2", CubeListBuilder.create()
				.texOffs(0, 25)
				.addBox(0.0F, -1.0F, 0.0F, 1, 1, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, -0.31869712141416456F, 0.0F));

		var rightHorn = body.addOrReplaceChild("right_horn_1", CubeListBuilder.create()
				.texOffs(7, 21)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rightHorn.addOrReplaceChild("right_horn_2", CubeListBuilder.create()
				.texOffs(5, 25)
				.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.31869712141416456F, 0.0F));

		var leftArm = body.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(1.5F, 3.0F, -2.0F, -0.9105382707654417F, -0.6373942428283291F, 0.0F));

		var leftArm2 = leftArm.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(25, 3)
				.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.0F, -0.5F, 0.22759093446006054F, 0.0F, 0.0F));

		var leftClawTop = leftArm2.addOrReplaceChild("left_claw_top", CubeListBuilder.create()
				.texOffs(15, 29)
				.addBox(-2.0F, 0.01F, -0.5F, 3, 2, 1),
			PartPose.offsetAndRotation(-0.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.36425021489121656F));

		var leftClawBottom = leftClawTop.addOrReplaceChild("left_claw_bottom", CubeListBuilder.create()
				.texOffs(25, 6)
				.addBox(-1.0F, -0.5F, 0.0F, 1, 2, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(1.0F, 0.5F, 0.5F, 0.0F, -0.36425021489121656F, 0.0F));

		leftClawBottom.addOrReplaceChild("left_claw_bottom_2", CubeListBuilder.create()
				.texOffs(25, 10)
				.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(-1.0F, 0.5F, 0.0F, 0.0F, 0.6829473363053812F, 0.0F));

		var rightArm = body.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(25, 14)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(-1.5F, 3.0F, -2.0F, -0.9105382707654417F, 0.6373942428283291F, 0.0F));

		var rightArm2 = rightArm.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(25, 17)
				.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.0F, -0.5F, 0.22759093446006054F, 0.0F, 0.0F));

		var rightClawTop = rightArm2.addOrReplaceChild("right_claw_top", CubeListBuilder.create()
				.texOffs(24, 29)
				.addBox(-1.0F, 0.01F, -0.5F, 3, 2, 1),
			PartPose.offsetAndRotation(0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		var rightClawBottom = rightClawTop.addOrReplaceChild("right_claw_bottom", CubeListBuilder.create()
				.texOffs(25, 6)
				.addBox(0.0F, -0.5F, 0.0F, 1, 2, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-1.0F, 0.5F, 0.5F, 0.0F, 0.36425021489121656F, 0.0F));

		rightClawBottom.addOrReplaceChild("right_claw_bottom_2", CubeListBuilder.create()
				.texOffs(25, 24)
				.addBox(0.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, -0.6829473363053812F, 0.0F));

		var leftFrontLeg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(15, 0)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.5F, 3.0F, -1.5F, -0.40980330836826856F, -0.045553093477052F, -1.6845917940249266F));

		leftFrontLeg.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(15, 4)
				.addBox(-1.0F, 0.0F, -0.5F, 1, 5, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.5F, 3.0F, 0.0F, 0.0F, 0.0F, 1.3203415791337103F));

		var leftMiddleLeg = body.addOrReplaceChild("left_middle_leg", CubeListBuilder.create()
				.texOffs(15, 10)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.5F, 3.0F, -1.0F, 0.0F, 0.0F, -1.4570008595648662F));

		leftMiddleLeg.addOrReplaceChild("left_middle_leg_2", CubeListBuilder.create()
				.texOffs(15, 14)
				.addBox(-1.0F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.5F, 3.0F, 0.0F, 0.0F, 0.0F, 1.1383037381507017F));

		var leftBackLeg = body.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
				.texOffs(15, 19)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.5F, 3.0F, 0.0F, 0.5009094953223726F, 0.5462880558742251F, -1.0016444577195458F));

		leftBackLeg.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(15, 23)
				.addBox(-1.0F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.5F, 3.0F, 0.0F, 0.0F, 0.0F, 1.3203415791337103F));

		var rightFrontLeg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(20, 0)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 3.0F, -1.5F, -0.40980330836826856F, 0.045553093477052F, 1.6845917940249266F));

		rightFrontLeg.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(15, 4)
				.addBox(0.0F, 0.0F, -0.5F, 1, 5, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-0.5F, 3.0F, 0.0F, 0.0F, 0.0F, -1.3203415791337103F));

		var rightMiddleLeg = body.addOrReplaceChild("right_middle_leg", CubeListBuilder.create()
				.texOffs(20, 10)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 3.0F, -1.0F, 0.0F, 0.0F, 1.4570008595648662F));

		rightMiddleLeg.addOrReplaceChild("right_middle_leg_2", CubeListBuilder.create()
				.texOffs(20, 14)
				.addBox(0.0F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-0.5F, 3.0F, 0.0F, 0.0F, 0.0F, -1.1383037381507017F));

		var rightBackLeg = body.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(20, 19)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 3.0F, 0.0F, 0.5009094953223726F, -0.5462880558742251F, 1.0016444577195458F));

		rightBackLeg.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(20, 23)
				.addBox(0.0F, 0.0F, -0.5F, 1, 4, 1, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(-0.5F, 3.0F, 0.0F, 0.0F, 0.0F, -1.3203415791337103F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		stack.pushPose();
		stack.mulPose(Axis.YN.rotationDegrees(90.0F));
		super.renderToBuffer(stack, consumer, light, overlay, color);
		stack.popPose();
	}

	public void renderCrabEating(PoseStack stack, VertexConsumer consumer, int light, int overlay, float animationTick) {
		this.playEatingAnimation(animationTick);
		stack.pushPose();
		stack.mulPose(Axis.YN.rotationDegrees(90.0F));
		this.root().render(stack, consumer, light, overlay);
		stack.popPose();
	}

	private void playEatingAnimation(float animationTick) {
		float flap2 = Mth.sin(animationTick * 0.15F) * 0.6F;
		float flap = Mth.cos(animationTick * 0.15F) * 0.6F;
		this.rightArm.xRot = flap * 0.5F - 0.9105382707654417F;
		this.leftArm.xRot = flap2 * 0.5F - 0.9105382707654417F;
		this.rightClawBottom.yRot = -flap * 0.5F;
		this.leftClawBottom.yRot = -flap2 * 0.5F;
		this.rightClawTop.yRot = 0.36425021489121656F * 0.5F + flap * 0.5F;
		this.leftClawTop.yRot = -0.36425021489121656F * 0.5F - flap2 * 0.5F;
	}

	@Override
	public void setupAnim(BubblerCrab entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float movement = Mth.cos(limbSwing * 1.5F + Mth.PI) * 1.5F * limbSwingAmount * 0.5F;
		this.rightArm.xRot = -movement * 0.2F - 0.9105382707654417F;
		this.leftArm.xRot = movement * 0.2F - 0.9105382707654417F;
		this.rightFrontLeg.zRot = movement + 1.6845917940249266F;
		this.rightMiddleLeg.zRot = -movement + 1.4570008595648662F;
		this.rightBackLeg.zRot = movement + 1.0016444577195458F;
		this.leftFrontLeg.zRot = movement - 1.6845917940249266F;
		this.leftMiddleLeg.zRot = -movement - 1.4570008595648662F;
		this.leftBackLeg.zRot = movement - 1.0016444577195458F;
		this.rightClawBottom.yRot = 0.36425021489121656F;
		this.leftClawBottom.yRot = -0.36425021489121656F;
		this.rightClawTop.yRot = 0.36425021489121656F;
		this.leftClawTop.yRot = -0.36425021489121656F;
	}
}
