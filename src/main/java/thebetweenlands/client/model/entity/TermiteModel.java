package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Termite;

public class TermiteModel extends MowzieModelBase<Termite> {

	private final ModelPart middleJaw;
	private final ModelPart rightJaw;
	private final ModelPart leftJaw;

	private final ModelPart frontRightLeg;
	private final ModelPart middleRightLeg;
	private final ModelPart backRightLeg;

	private final ModelPart frontLeftLeg;
	private final ModelPart middleLeftLeg;
	private final ModelPart backLeftLeg;

	private final ModelPart[] tail;

	public TermiteModel(ModelPart root) {
		super(root);
		var head = root.getChild("head");
		this.leftJaw = head.getChild("left_jaw");
		this.rightJaw = head.getChild("right_jaw");
		this.middleJaw = head.getChild("middle_jaw");

		this.frontRightLeg = root.getChild("front_right_leg");
		this.middleRightLeg = root.getChild("middle_right_leg");
		this.backRightLeg = root.getChild("back_right_leg");

		this.frontLeftLeg = root.getChild("front_left_leg");
		this.middleLeftLeg = root.getChild("middle_left_leg");
		this.backLeftLeg = root.getChild("back_left_leg");

		var body1 = root.getChild("base").getChild("body1");
		var body2 = body1.getChild("body2");
		var body3 = body2.getChild("body3");
		var body4 = body3.getChild("body4");
		var tail = body4.getChild("tail");
		this.tail = new ModelPart[]{tail, body4, body3, body2, body1};
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5F, -4.0F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, 21.0F, -3.5F, -0.091106186954104F, 0.0F, 0.0F));
		var body1 = base.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.31869712141416456F, 0.0F, 0.0F));
		var body2 = body1.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, 4.0F, 0.31869712141416456F, 0.0F, 0.0F));
		var body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(0, 27).addBox(-3.0F, -4.0F, 0.0F, 6, 4, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, 4.0F, 0.31869712141416456F, 0.0F, 0.0F));
		var body4 = body3.addOrReplaceChild("body4", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-2.0F, -3.0F, 0.0F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.7740535232594852F, 0.0F, 0.0F));
		var tail = body4.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 43).addBox(-1.0F, -2.0F, 0.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.31869712141416456F, 0.0F, 0.0F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-2.0F, -2.0F, -5.0F, 4, 3, 5),
			PartPose.offsetAndRotation(0.0F, 18.0F, -3.5F, 0.31869712141416456F, 0.0F, 0.0F));
		head.addOrReplaceChild("middle_jaw", CubeListBuilder.create()
				.texOffs(21, 21).addBox(-0.5F, -1.5F, -3.0F, 1, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, -4.5F, 0.136659280431156F, 0.0F, 0.0F));
		head.addOrReplaceChild("left_jaw", CubeListBuilder.create()
				.texOffs(21, 9).addBox(0.0F, -0.5F, -3.0F, 2, 1, 4),
			PartPose.offsetAndRotation(1.0F, 0.0F, -4.5F, -0.18203784098300857F, 0.22759093446006054F, 0.31869712141416456F));
		head.addOrReplaceChild("right_jaw", CubeListBuilder.create()
				.texOffs(21, 15).addBox(-2.0F, -0.5F, -3.0F, 2, 1, 4),
			PartPose.offsetAndRotation(-1.0F, 0.0F, -4.5F, -0.18203784098300857F, -0.22759093446006054F, -0.31869712141416456F));
		var leftSensor = head.addOrReplaceChild("left_sensor1", CubeListBuilder.create()
				.texOffs(36, 0).addBox(-0.5F, 0.0F, -3.0F, 1, 0, 4),
			PartPose.offsetAndRotation(2.0F, -2.0F, -2.0F, -0.7740535232594852F, -0.5009094953223726F, 0.36425021489121656F));
		leftSensor.addOrReplaceChild("left_sensor2", CubeListBuilder.create()
				.texOffs(39, 0).addBox(-0.5F, 0.0F, -4.0F, 4, 0, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.7285004297824331F, 0.0F, 0.0F));
		var rightSensor = head.addOrReplaceChild("right_sensor1", CubeListBuilder.create()
				.texOffs(36, 5).addBox(-0.5F, 0.0F, -3.0F, 1, 0, 4),
			PartPose.offsetAndRotation(-2.0F, -2.0F, -2.0F, -0.7740535232594852F, 0.5009094953223726F, -0.36425021489121656F));
		rightSensor.addOrReplaceChild("right_sensor2", CubeListBuilder.create()
				.texOffs(39, 5).addBox(-3.5F, 0.0F, -4.0F, 4, 0, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.7285004297824331F, 0.0F, 0.0F));

		var leaf1 = tail.addOrReplaceChild("leaf1", CubeListBuilder.create()
				.texOffs(-3, 48).addBox(-2.5F, 0.0F, 0.0F, 5, 0, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 1.0927506446736497F, 0.0F, 0.0F));
		leaf1.addOrReplaceChild("leaf2", CubeListBuilder.create()
				.texOffs(-4, 52).addBox(-2.5F, 0.0F, 0.0F, 5, 0, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));

		var headLeaf = head.addOrReplaceChild("head_leaf1", CubeListBuilder.create()
				.texOffs(37, 10).addBox(-3.0F, 0.0F, 0.0F, 6, 0, 3),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F, 0.0F));
		headLeaf.addOrReplaceChild("head_leaf2", CubeListBuilder.create()
				.texOffs(37, 14).addBox(-3.0F, 0.0F, 0.0F, 6, 0, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.31869712141416456F, 0.0F, 0.0F));


		var frontLeftLeg = partDefinition.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
				.texOffs(60, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, 20.0F, -4.5F, -0.7285004297824331F, -0.31869712141416456F, -1.6845917940249266F));
		frontLeftLeg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create()
				.texOffs(65, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 1.3658946726107624F));

		var middleLeftLeg = partDefinition.addOrReplaceChild("middle_left_leg", CubeListBuilder.create()
				.texOffs(60, 8).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, 20.1F, -3.5F, 0.18203784098300857F, 0.136659280431156F, -1.9123572614101867F));
		middleLeftLeg.addOrReplaceChild("middle_left_leg2", CubeListBuilder.create()
				.texOffs(65, 8).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 1.4114477660878142F));

		var backLeftLeg = partDefinition.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
				.texOffs(60, 16).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, 20.0F, -2.5F, 0.8196066167365371F, 0.40980330836826856F, -1.8212510744560826F));
		backLeftLeg.addOrReplaceChild("back_left_leg2", CubeListBuilder.create()
				.texOffs(65, 16).addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 1.3658946726107624F));


		var frontRightLeg = partDefinition.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
				.texOffs(70, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, 20.0F, -4.5F, -0.7285004297824331F, 0.31869712141416456F, 1.6845917940249266F));
		frontRightLeg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create()
				.texOffs(75, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, -1.3658946726107624F));

		var middleRightLeg = partDefinition.addOrReplaceChild("middle_right_leg", CubeListBuilder.create()
				.texOffs(70, 8).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, 20.1F, -3.5F, 0.18203784098300857F, -0.136659280431156F, 1.9123572614101867F));
		middleRightLeg.addOrReplaceChild("middle_right_leg2", CubeListBuilder.create()
				.texOffs(75, 8).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, -1.4114477660878142F));

		var backRightLeg = partDefinition.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
				.texOffs(70, 16).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, 20.0F, -2.5F, 0.8196066167365371F, -0.40980330836826856F, 1.8212510744560826F));
		backRightLeg.addOrReplaceChild("back_right_leg2", CubeListBuilder.create()
				.texOffs(75, 16).addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.0F, 0.0F, -1.3658946726107624F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void setupAnim(Termite entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float movement = Mth.cos(limbSwing * 1.25F + Mth.PI) * 2F * limbSwingAmount * 0.5F;
		this.frontRightLeg.zRot = movement + 1.95F;
		this.middleRightLeg.zRot = -movement + 1.95F;
		this.backRightLeg.zRot = movement + 1.95F;
		this.frontLeftLeg.zRot = movement - 1.95F;
		this.middleLeftLeg.zRot = -movement - 1.95F;
		this.backLeftLeg.zRot = movement - 1.95F;

		this.frontRightLeg.yRot = movement;
		this.middleRightLeg.yRot = -movement;
		this.backRightLeg.yRot = movement;
		this.frontLeftLeg.yRot = movement;
		this.middleLeftLeg.yRot = -movement;
		this.backLeftLeg.yRot = movement;


		float mandibleControl = (Mth.sin(0.1F * ageInTicks - Mth.cos(0.1F * ageInTicks)) + 1) / 2;
		this.chainWave(this.tail, 0.4f, 0.1f, 0, ageInTicks, 1);
		this.chainSwing(this.tail, 0.2f, 0.2f, 1, ageInTicks, 1);
		this.leftJaw.yRot -= 1 * mandibleControl * Mth.cos(2 * ageInTicks);
		this.rightJaw.yRot += 1 * mandibleControl * Mth.cos(2 * ageInTicks);
		this.middleJaw.xRot -= 1 * mandibleControl * Mth.cos(2 * ageInTicks);
	}
}
