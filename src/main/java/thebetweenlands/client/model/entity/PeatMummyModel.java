package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.PeatMummy;

public class PeatMummyModel extends MowzieModelBase<PeatMummy> {

	private final ModelPart root;
	private final ModelPart bodyBase;
	private final ModelPart butt;
	private final ModelPart legleft;
	private final ModelPart legright;
	private final ModelPart legleft2;
	private final ModelPart legright2;
	private final ModelPart shoulderBase;
	private final ModelPart neck;
	private final ModelPart armright;
	private final ModelPart armleft;
	private final ModelPart armright2;
	private final ModelPart armleft2;
	private final ModelPart jaw;
	private final ModelPart cheektissueright;
	private final ModelPart cheektissueleft;

	public PeatMummyModel(ModelPart root) {
		this.root = root;
		this.bodyBase = root.getChild("body_base");
		this.butt = this.bodyBase.getChild("butt_joint").getChild("butt");
		this.legleft = this.butt.getChild("left_leg_1");
		this.legleft2 = this.legleft.getChild("left_leg_2");
		this.legright = this.butt.getChild("right_leg_1");
		this.legright2 = this.legright.getChild("right_leg_2");
		this.shoulderBase = this.bodyBase.getChild("shoulder_joint");
		this.neck = this.shoulderBase.getChild("neck");
		this.armright = this.shoulderBase.getChild("right_arm_1");
		this.armright2 = this.armright.getChild("right_arm_2");
		this.armleft = this.shoulderBase.getChild("left_arm_1");
		this.armleft2 = this.armleft.getChild("left_arm_2");
		this.jaw = this.neck.getChild("head_1").getChild("jaw");
		this.cheektissueright = this.jaw.getChild("right_cheek_tissue");
		this.cheektissueleft = this.jaw.getChild("left_cheek_tissue");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var bodyBase = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -9.68799815159352F, -4.678445443159999F, 8, 10, 6),
			PartPose.offsetAndRotation(0.0F, 10.0F, 6.0F, 1.3203415791337103F, 0.0F, 0.0F));

		var shoulderJoint = bodyBase.addOrReplaceChild("shoulder_joint", CubeListBuilder.create()
				.addBox(0.0F, -7.5F, 2.5F, 0, 0, 0),
			PartPose.offsetAndRotation(0.0F, -9.026879981515934F, 0.2252155455684F, -1.3203415791337103F, 0.0F, 0.0F));

		var neck = shoulderJoint.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(55, 0).addBox(-1.5F, -3.8F, -1.5F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, -1.5F, -5.02F, 0.9105382707654417F, 0.0F, 0.0F));

		var head1 = neck.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(55, 9).addBox(-4.0F, -5.0F, -8.0F, 8, 5, 8),
			PartPose.offsetAndRotation(0.0F, -2.6F, 0.0F, -0.9105382707654417F, 0.0F, 0.0F));

		head1.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(55, 23).addBox(-3.5F, 0.0F, -3.0F, 7, 3, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		head1.addOrReplaceChild("hair", CubeListBuilder.create()
				.texOffs(40, 42).addBox(-4.5F, -5.1F, -8.5F, 9, 12, 9),
			PartPose.offset(0.0F, -0.0F, 0.0F));

		var jaw = head1.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(55, 30).addBox(-3.0F, -1.0F, -8.0F, 6, 2, 7),
			PartPose.offsetAndRotation(0.0F, 1.1F, 0.0F, 0.5918411493512771F, 0.0F, 0.18203784098300857F));

		jaw.addOrReplaceChild("teeth_1", CubeListBuilder.create()
				.texOffs(82, 30).addBox(-2.5F, -2.0F, -7.7F, 5, 1, 6),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		jaw.addOrReplaceChild("right_cheek_tissue", CubeListBuilder.create()
				.texOffs(82, 34).addBox(-2.4F, -4.3F, -7.0F, 0, 5, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.27314402793711257F, 0.0F, -0.27314402793711257F));

		jaw.addOrReplaceChild("left_cheek_tissue", CubeListBuilder.create()
				.texOffs(92, 34).addBox(2.8F, -4.9F, -6.0F, 0, 5, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.091106186954104F));

		head1.addOrReplaceChild("teeth_2", CubeListBuilder.create()
				.texOffs(82, 44).addBox(-3.0F, 0.0F, -7.8F, 6, 1, 5),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		shoulderJoint.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
				.texOffs(25, 17).addBox(-0.2F, -5.0F, -3.0F, 5, 6, 7),
			PartPose.offsetAndRotation(0.0F, 2.5F, -1.5F, 1.230231460770601F, -0.02255085846982904F, -0.08827863759542955F));

		shoulderJoint.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-4.8F, -5.0F, -3.0F, 5, 6, 7),
			PartPose.offsetAndRotation(0.0F, 2.5F, -1.5F, 1.230231460770601F, 0.02255085846982904F, 0.08827863759542955F));

		var armleft = shoulderJoint.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(19, 32).addBox(0.0F, -1.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(4.0F, -0.13F, -2.89F, 0.13735741213195374F, 0.7311184236604247F, -0.4316199240181977F));

		armleft.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(28, 32).addBox(0.0F, 0.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, -0.5918411493512771F, 0.0F, 0.0F));

		var armright = shoulderJoint.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-2.0F, -1.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(-4.0F, -0.13F, -2.89F, 0.13735741213195374F, -0.7311184236604247F, 0.4316199240181977F));

		armright.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(9, 32).addBox(-2.0F, 0.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, -0.5918411493512771F, 0.0F, 0.0F));

		var joint = bodyBase.addOrReplaceChild("butt_joint", CubeListBuilder.create(),
			PartPose.offsetAndRotation(0.0F, 0.5420018484064801F, 1.5215545568400013F, -1.3203415791337103F, 0.0F, 0.0F));

		var butt = joint.addOrReplaceChild("butt", CubeListBuilder.create()
				.texOffs(90, 0).addBox(-4.5F, -1.4F, -2.0F, 9, 5, 6),
			PartPose.offsetAndRotation(0.0F, 3.9F, -2.0F, 0.7740535232594852F, 0.0F, 0.0F));

		var legleft = butt.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(108, 12).addBox(0.0F, -1.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(4.0F, 3.5F, 1.5F, -1.1838568316277536F, -1.1383037381507017F, 0.091106186954104F));

		var legleft2 = legleft.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(117, 12).addBox(0.0F, 0.0F, -1.0F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.2292353921796064F, 0.0F, 0.0F));

		var legright = butt.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(90, 12).addBox(-2.0F, -1.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(-4.0F, 3.5F, 1.5F, -1.1838568316277536F, 1.1383037381507017F, -0.091106186954104F));

		legright.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(99, 12).addBox(-2.0F, 0.0F, -1.0F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.2292353921796064F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(PeatMummy entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.neck.yRot = Mth.sin((netHeadYaw / Mth.RAD_TO_DEG));
		this.neck.xRot = 0.9105382707654417F + Mth.sin((headPitch / Mth.RAD_TO_DEG));
		float newf1 = limbSwingAmount;
		if (newf1 > 0.4) newf1 = 0.4f;
		float newf12 = limbSwingAmount;
		if (newf12 > 0.7) newf12 = 0.7f;

		float globalDegree = 1.5f;
		float wiggleDegree = 1.5f;
		float globalSpeed = 0.6f;
		float globalHeight = 1.5f;

		//this.bodyBase.xRot -= wiggleDegree * globalDegree * newf1 * 3.0F * Mth.cos(globalSpeed * limbSwing);
		this.swing(this.butt, globalSpeed, 0.2f * globalDegree * wiggleDegree, true, -1.6f, 0, limbSwing, newf1);
		this.swing(this.bodyBase, globalSpeed, 0.3f * globalDegree * wiggleDegree, true, -0.8f, 0, limbSwing, newf1);
		this.swing(this.shoulderBase, globalSpeed, 0.4f * globalDegree * wiggleDegree, true, 0, 0, limbSwing, newf1);
		this.swing(this.neck, globalSpeed, 0.6f * globalDegree * wiggleDegree, false, -0.5f, 0, limbSwing, newf1);

		this.walk(this.bodyBase, 2 * globalSpeed, 0.1f * globalHeight, true, -1.5f, 0.1f, limbSwing, limbSwingAmount);
		this.walk(this.neck, 2 * globalSpeed, 0.1f * globalHeight, false, -1f, -0.1f, limbSwing, limbSwingAmount);
		this.walk(this.jaw, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, limbSwing, limbSwingAmount);
		this.bob(this.bodyBase, 2 * globalSpeed, 0.5f * globalHeight, false, limbSwing, limbSwingAmount);

		this.flap(this.legleft, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.3f, limbSwing, newf12);
		this.walk(this.legleft, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.5f, limbSwing, newf12);
		this.walk(this.legleft2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, limbSwing, newf12);
		this.swing(this.legleft2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, limbSwing, newf12);

		this.flap(this.legright, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, 0.3f, limbSwing, newf12);
		this.walk(this.legright, globalSpeed, 0.3f * globalDegree, true, 0 - 0.8f, -0.5f, limbSwing, newf12);
		this.walk(this.legright2, globalSpeed, 0.3f * globalDegree, true, -1.5f - 0.8f, 0.3f, limbSwing, newf12);
		this.swing(this.legright2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, -0.3f, limbSwing, newf12);

		this.walk(this.armleft, globalSpeed, 0.5f * globalDegree, true, -1.6f - 0.4f, 0.3f, limbSwing, newf12);
		this.walk(this.armleft2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, limbSwing, newf12);
		this.swing(this.armleft2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, limbSwing, newf12);

		this.walk(this.armright, globalSpeed, 0.5f * globalDegree, false, -1.6f - 0.4f, 0.3f, limbSwing, newf12);
		this.walk(this.armright2, globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, -0.4f, limbSwing, newf12);
		this.swing(this.armright2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, 0.4f, limbSwing, newf12);
	}

	@Override
	public void prepareMobModel(PeatMummy entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float spawningProgress = entity.getInterpolatedSpawningProgress(partialTick);
		this.bodyBase.xRot -= 1.0F - spawningProgress;
		this.armleft.xRot -= 1.5F * (1.0F - spawningProgress);
		this.armright.xRot -= 1.5F * (1.0F - spawningProgress);
		this.root.z += 20.0F * (1.0F - spawningProgress);
		this.root.y += 10.0F * (1.0F - spawningProgress);

		float globalDegree = 1.5f;
		float wiggleDegree = 1.5f;
		float globalSpeed = 1.3f;
		float globalHeight = 1.5f;

		float f = spawningProgress * 10.0F;
		float f1 = (float) (0.6F * (1.0F / (1.0F + Math.pow(2.0F, 100.0F * (spawningProgress - 0.9F)))));
		if (spawningProgress != 1) {

			this.bodyBase.x -= wiggleDegree * globalDegree * f1 * 3f * Mth.cos(globalSpeed * f);
			this.swing(this.butt, globalSpeed, 0.2f * globalDegree * wiggleDegree, true, -1.6f, 0, f, f1);
			this.swing(this.bodyBase, globalSpeed, 0.3f * globalDegree * wiggleDegree, true, -0.8f, 0, f, f1);
			this.swing(this.shoulderBase, globalSpeed, 0.4f * globalDegree * wiggleDegree, true, 0, 0, f, f1);
			this.swing(this.neck, globalSpeed, 0.6f * globalDegree * wiggleDegree, false, -0.5f, 0, f, f1);

			this.walk(this.bodyBase, 2 * globalSpeed, 0.1f * globalHeight, true, -1.5f, 0.1f, f, f1);
			this.walk(this.neck, 2 * globalSpeed, 0.1f * globalHeight, false, -1f, -0.1f, f, f1);
			this.walk(this.jaw, 2 * globalSpeed, 0.1f * globalHeight, false, -0.7f, -0.1f, f, f1);
			this.bob(this.bodyBase, 2 * globalSpeed, 0.5f * globalHeight, false, f, f1);

			this.flap(this.legleft, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.3f, f, f1);
			this.walk(this.legleft, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, -0.5f, f, f1);
			this.walk(this.legleft2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, f1);
			this.swing(this.legleft2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, 0.3f, f, f1);

			this.flap(this.legright, globalSpeed, 0.3f * globalDegree, false, 0 - 0.8f, 0.3f, f, f1);
			this.walk(this.legright, globalSpeed, 0.3f * globalDegree, true, 0 - 0.8f, -0.5f, f, f1);
			this.walk(this.legright2, globalSpeed, 0.3f * globalDegree, true, -1.5f - 0.8f, 0.3f, f, f1);
			this.swing(this.legright2, globalSpeed, 0.3f * globalDegree, false, -1.5f - 0.8f, -0.3f, f, f1);

			this.walk(this.armleft, globalSpeed, 0.5f * globalDegree, true, -1.6f - 0.4f, 0.3f, f, f1);
			this.walk(this.armleft2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, f1);
			this.swing(this.armleft2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, -0.4f, f, f1);

			this.walk(this.armright, globalSpeed, 0.5f * globalDegree, false, -1.6f - 0.4f, 0.3f, f, f1);
			this.walk(this.armright2, globalSpeed, 0.3f * globalDegree, false, -0.1f - 0.4f, -0.4f, f, f1);
			this.swing(this.armright2, globalSpeed, 0.3f * globalDegree, true, -0.1f - 0.4f, 0.4f, f, f1);
		}

		float screamProgress = entity.getScreamingProgress();
		if (screamProgress != 0.0F) {
			if (screamProgress > 1.0F) screamProgress = 1.0F;
			float controller = 40.0F * (-screamProgress * (screamProgress - 1.0F) * (screamProgress - 0.1F));
			if (controller > 0.2f) controller = 0.2f;
			float controller2 = controller;
			if (controller2 < 0.0F) controller2 = 0.0F;

			this.bodyBase.xRot -= 1.6f * controller;
			this.bodyBase.y -= 1.6f * controller;
			this.bodyBase.z -= 1.6f * controller;
			this.legleft.xRot += 1.6f * controller;
			this.legright.xRot += 1.6f * controller;
			this.armleft.xRot += 1.6f * controller;
			this.armright.xRot += 1.6f * controller;
			this.armleft.y += 10.0F * controller;
			this.armright.y += 10.0F * controller;
			this.armleft.z += 5.0F * controller;
			this.armright.z += 5.0F * controller;
			this.armleft.zRot += 0.5F * controller;
			this.armright.zRot -= 0.5F * controller;
			this.armleft2.xRot += controller;
			this.armright2.xRot += controller;
			this.jaw.xRot += 2.4F * controller + controller2 * 0.5F * Mth.cos(4.0F * (entity.tickCount + partialTick));
			this.cheektissueleft.xRot -= 2.4F * controller + controller2 * 0.5F * Mth.cos(4.0F * (entity.tickCount + partialTick));
			this.cheektissueleft.y += 10.0F * controller;
			this.cheektissueright.xRot -= 2.4F * controller + controller2 * 0.5F * Mth.cos(4.0F * (entity.tickCount + partialTick));
			this.cheektissueright.y += 10.0F * controller;
		}
	}
}