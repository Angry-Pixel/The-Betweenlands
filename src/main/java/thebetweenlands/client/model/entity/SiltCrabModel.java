package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.common.entity.fishing.SiltCrab;

public class SiltCrabModel extends HierarchicalModel<SiltCrab> {

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
	private final ModelPart leftClawBase;
	private final ModelPart rightClawTop;
	private final ModelPart rightClawBase;

	public SiltCrabModel(ModelPart root) {
		this.root = root;
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
		this.rightClawBase = this.rightArm.getChild("right_claw_base");
		this.rightClawTop = this.rightClawBase.getChild("right_claw_top");
		this.leftClawBase = this.leftArm.getChild("left_claw_base");
		this.leftClawTop = this.leftClawBase.getChild("left_claw_top");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.leftMiddleLeg = root.getChild("left_middle_leg");
		this.leftBackLeg = root.getChild("left_back_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.rightMiddleLeg = root.getChild("right_middle_leg");
		this.rightBackLeg = root.getChild("right_back_leg");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 3),
			PartPose.offsetAndRotation(0.0F, 18.5F, -1.0F, -0.22759093446006054F, 0.0F, 0.0F));

		body.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 23)
				.addBox(-2.5F, -2.0F, 0.0F, 5, 2, 2),
			PartPose.offset(0.0F, 2.0F, 3.0F));

		var panser = body.addOrReplaceChild("panser_1", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-3.5F, -2.0F, 0.0F, 7, 2, 4),
			PartPose.offsetAndRotation(0.0F, 0.5F, 1.0F, -0.136659280431156F, 0.0F, 0.0F));

		panser.addOrReplaceChild("panser_2", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-3.5F, -2.0F, -2.0F, 7, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));

		panser.addOrReplaceChild("panser_3", CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-2.5F, 0.0F, 0.0F, 5, 3, 1),
			PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var leftArm = partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(15, 22)
				.addBox(-0.5F, -0.2F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(2.0F, 20.7F, -0.5F, -1.5025539530419183F, -0.4553564018453205F, -0.4553564018453205F));

		var leftClaw = leftArm.addOrReplaceChild("left_claw_base", CubeListBuilder.create()
				.texOffs(20, 22)
				.addBox(-1.0F, -0.5F, -1.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.31869712141416456F, 0.0F, 0.40980330836826856F));

		var leftClawTop = leftClaw.addOrReplaceChild("left_claw_top", CubeListBuilder.create()
				.texOffs(20, 26)
				.addBox(-1.0F, 0.0F, -1.5F, 1, 3, 3),
			PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.045553093477052F));

		leftClawTop.addOrReplaceChild("left_claw_point", CubeListBuilder.create()
				.texOffs(29, 22)
				.addBox(-1.0F, 0.0F, -1.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		leftClaw.addOrReplaceChild("left_snapper", CubeListBuilder.create()
				.texOffs(29, 26)
				.addBox(0.0F, 0.0F, -1.0F, 1, 3, 2),
			PartPose.offsetAndRotation(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		var rightArm = partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(36, 22)
				.addBox(-0.5F, -0.2F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-2.0F, 20.7F, -0.5F, -1.5025539530419183F, 0.4553564018453205F, 0.4553564018453205F));

		var rightClaw = rightArm.addOrReplaceChild("right_claw_base", CubeListBuilder.create()
				.texOffs(41, 22)
				.addBox(-1.0F, -0.5F, -1.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.31869712141416456F, 0.0F, -0.40980330836826856F));

		var rightClawTop = rightClaw.addOrReplaceChild("right_claw_top", CubeListBuilder.create()
				.texOffs(41, 26)
				.addBox(0.0F, 0.0F, -1.5F, 1, 3, 3),
			PartPose.offsetAndRotation(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.045553093477052F));

		rightClawTop.addOrReplaceChild("right_claw_point", CubeListBuilder.create()
				.texOffs(50, 22)
				.addBox(0.0F, 0.0F, -1.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		rightClaw.addOrReplaceChild("right_snapper", CubeListBuilder.create()
				.texOffs(50, 26)
				.addBox(-1.0F, 0.0F, -1.0F, 1, 3, 2),
			PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		var leftFrontLeg = partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(23, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1),
			PartPose.offsetAndRotation(2.0F, 21.0F, 0.0F, -0.36425021489121656F, 0.18203784098300857F, -0.31869712141416456F));

		leftFrontLeg.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(23, 3)
				.addBox(-1.0F, -0.5F, -1.0F, 2, 5, 2),
			PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		var leftMiddleLeg = partDefinition.addOrReplaceChild("left_middle_leg", CubeListBuilder.create()
				.texOffs(34, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 4, 1, 1),
			PartPose.offsetAndRotation(2.0F, 21.0F, 1.0F, 0.0F, -0.136659280431156F, -0.18203784098300857F));

		leftMiddleLeg.addOrReplaceChild("left_middle_leg_2", CubeListBuilder.create()
				.texOffs(34, 3)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.31869712141416456F));

		var leftBackLeg = partDefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
				.texOffs(45, 0)
				.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1),
			PartPose.offsetAndRotation(2.0F, 21.0F, 2.0F, 0.36425021489121656F, -0.40980330836826856F, -0.136659280431156F));

		leftBackLeg.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(45, 3)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.31869712141416456F));

		var rightFrontLeg = partDefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(23, 11)
				.addBox(-3.5F, -0.5F, -0.5F, 4, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 0.0F, -0.36425021489121656F, -0.18203784098300857F, 0.31869712141416456F));

		rightFrontLeg.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(23, 14)
				.addBox(-1.0F, -0.5F, -1.0F, 2, 5, 2),
			PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		var rightMiddleLeg = partDefinition.addOrReplaceChild("right_middle_leg", CubeListBuilder.create()
				.texOffs(34, 11)
				.addBox(-3.5F, -0.5F, -0.5F, 4, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 1.0F, 0.0F, 0.136659280431156F, 0.18203784098300857F));

		rightMiddleLeg.addOrReplaceChild("right_middle_leg_2", CubeListBuilder.create()
				.texOffs(34, 14)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.31869712141416456F));

		var rightBackLeg = partDefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(45, 11)
				.addBox(-2.5F, -0.5F, -0.5F, 3, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 21.0F, 2.0F, 0.36425021489121656F, 0.40980330836826856F, 0.136659280431156F));

		rightBackLeg.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(45, 14)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.31869712141416456F));

		return LayerDefinition.create(definition, 64, 32);
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
		this.rightArm.xRot = flap * 0.5F - 1.5025539530419183F;
		this.leftArm.xRot = flap2 * 0.5F - 1.5025539530419183F;
		this.leftClawTop.zRot = -0.045553093477052F * 5F + flap * 0.5F;
		this.rightClawTop.zRot = 0.045553093477052F * 5F + flap2 * 0.5F;
		this.leftClawBase.zRot = 0.40980330836826856F * 4F + flap * 1.5F;
		this.rightClawBase.zRot = -0.40980330836826856F* 4F + flap2 * 1.5F;
	}

	@Override
	public void setupAnim(SiltCrab entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float movement = Mth.cos(limbSwing * 1.5F + Mth.PI) * 1.5F * limbSwingAmount * 0.5F;
		this.rightArm.xRot = -movement * 0.2F - 1.5025539530419183F;
		this.leftArm.xRot = movement * 0.2F - 1.5025539530419183F;
		this.rightFrontLeg.zRot = movement;
		this.rightMiddleLeg.zRot = -movement;
		this.rightBackLeg.zRot = movement;
		this.leftFrontLeg.zRot = movement;
		this.leftMiddleLeg.zRot = -movement;
		this.leftBackLeg.zRot = movement;
		this.leftClawTop.zRot = -0.045553093477052F;
		this.rightClawTop.zRot = 0.045553093477052F;
		this.leftClawBase.zRot = 0.40980330836826856F;
		this.rightClawBase.zRot = -0.40980330836826856F;
	}
}
