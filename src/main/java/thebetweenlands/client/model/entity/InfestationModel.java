package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.infestation.Infestation;

public class InfestationModel extends MowzieModelBase<Infestation> {

	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightElytra;
	private final ModelPart leftElytra;

	private final ModelPart rightMandible;
	private final ModelPart leftMandible;
	private final ModelPart rightAntenna;
	private final ModelPart leftAntenna;

	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightMiddleLeg;
	private final ModelPart leftMiddleLeg;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;

	public InfestationModel(ModelPart root) {
		super(root);
		var base = root.getChild("base");
		this.rightWing = base.getChild("right_wing");
		this.leftWing = base.getChild("left_wing");
		this.rightElytra = base.getChild("right_elytra");
		this.leftElytra = base.getChild("left_elytra");

		var head = base.getChild("head");
		this.rightAntenna = head.getChild("right_antenna");
		this.leftAntenna = head.getChild("left_antenna");
		this.rightMandible = head.getChild("right_mandible");
		this.leftMandible = head.getChild("left_mandible");

		this.rightFrontLeg = base.getChild("right_front_leg");
		this.leftFrontLeg = base.getChild("left_front_leg");
		this.rightMiddleLeg = base.getChild("right_middle_leg");
		this.leftMiddleLeg = base.getChild("left_middle_leg");
		this.rightBackLeg = base.getChild("right_back_leg");
		this.leftBackLeg = base.getChild("left_back_leg");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, -0.6829473363053812F, 0.0F, 0.0F));

		var head = base.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.01F, 0.0F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var mandible_right1a = head.addOrReplaceChild("right_mandible", CubeListBuilder.create()
				.texOffs(9, 15).addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3),
			PartPose.offsetAndRotation(-0.25F, 1.0F, -1.0F, 0.31869712141416456F, 0.36425021489121656F, -0.091106186954104F));
		var mandible_right1b = mandible_right1a.addOrReplaceChild("right_mandible2", CubeListBuilder.create()
				.texOffs(9, 20).addBox(0.0F, 0.0F, -2.0F, 1, 1, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, -2.0F, 0.0F, -0.36425021489121656F, 0.0F));
		mandible_right1b.addOrReplaceChild("right_mandible3", CubeListBuilder.create()
				.texOffs(9, 24).addBox(0.0F, -1.0F, -1.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.4553564018453205F, 0.0F, 0.0F));

		var rightAntenna = head.addOrReplaceChild("right_antenna", CubeListBuilder.create()
				.texOffs(-2, 30).addBox(-1.0F, 0.0F, -2.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.01F, -2.0F, 0.0F, 0.9105382707654417F, 0.0F));
		var rightAntenna2 = rightAntenna.addOrReplaceChild("right_antenna2", CubeListBuilder.create()
				.texOffs(1, 30).addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.9105382707654417F, 0.0F, 0.0F));
		rightAntenna2.addOrReplaceChild("right_antenna3", CubeListBuilder.create()
				.texOffs(6, 30).addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.9105382707654417F, 0.0F, 0.0F));

		var leftMandible = head.addOrReplaceChild("left_mandible", CubeListBuilder.create()
				.texOffs(0, 15).addBox(0.0F, 0.0F, -2.0F, 1, 1, 3),
			PartPose.offsetAndRotation(0.25F, 1.0F, -1.0F, 0.31869712141416456F, -0.36425021489121656F, 0.091106186954104F));
		var leftMandible2 = leftMandible.addOrReplaceChild("left_mandible2", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2),
			PartPose.offsetAndRotation(1.0F, 0.0F, -2.0F, 0.0F, 0.36425021489121656F, 0.0F));
		leftMandible2.addOrReplaceChild("left_mandible3", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.0F, -1.0F, -1.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.4553564018453205F, 0.0F, 0.0F));

		var leftAntenna = head.addOrReplaceChild("left_antenna", CubeListBuilder.create()
				.texOffs(-2, 27).addBox(0.0F, 0.0F, -2.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.01F, -2.0F, 0.0F, -0.9105382707654417F, 0.0F));
		var leftAntenna2 = leftAntenna.addOrReplaceChild("left_antenna2", CubeListBuilder.create()
				.texOffs(1, 27).addBox(0.0F, 0.0F, -2.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.9105382707654417F, 0.0F, 0.0F));
		leftAntenna2.addOrReplaceChild("left_antenna3", CubeListBuilder.create()
				.texOffs(6, 27).addBox(0.0F, 0.0F, -2.0F, 2, 0, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.9105382707654417F, 0.0F, 0.0F));

		var butt = base.addOrReplaceChild("butt", CubeListBuilder.create()
				.texOffs(0, 5).addBox(-1.01F, 0.0F, 0.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.5009094953223726F, 0.0F, 0.0F));
		var stinger = butt.addOrReplaceChild("stinger", CubeListBuilder.create()
				.texOffs(10, 0).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F, 0.27314402793711257F, 0.0F, 0.0F));
		stinger.addOrReplaceChild("right_stinger", CubeListBuilder.create()
				.texOffs(10, 2).addBox(0.0F, -1.0F, 0.0F, 0, 1, 3),
			PartPose.offsetAndRotation(-0.3F, 0.0F, 1.0F, 0.31869712141416456F, -0.31869712141416456F, 0.18203784098300857F));
		stinger.addOrReplaceChild("left_stinger", CubeListBuilder.create()
				.texOffs(10, 0).addBox(0.0F, -1.0F, 0.0F, 0, 1, 3),
			PartPose.offsetAndRotation(0.3F, 0.0F, 1.0F, 0.31869712141416456F, 0.31869712141416456F, -0.18203784098300857F));


		var rightFrontLeg = base.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(23, 11).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(-0.5F, 2.0F, 0.5F, 0.22759093446006054F, 0.7285004297824331F, 1.2292353921796064F));
		rightFrontLeg.addOrReplaceChild("right_front_leg2", CubeListBuilder.create()
				.texOffs(23, 14).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));

		var rightMiddleLeg = base.addOrReplaceChild("right_middle_leg", CubeListBuilder.create()
				.texOffs(23, 16).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(-0.5F, 2.0F, 1.0F, 1.1838568316277536F, -1.0471975511965976F, -0.091106186954104F));
		rightMiddleLeg.addOrReplaceChild("right_middle_leg2", CubeListBuilder.create()
				.texOffs(23, 19).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.22759093446006054F, 0.0F, 0.0F));

		var rightBackLeg = base.addOrReplaceChild("right_back_leg", CubeListBuilder.create()
				.texOffs(23, 21).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(-0.5F, 2.0F, 1.5F, 0.8651597102135892F, -0.22759093446006054F, 0.0F));
		rightBackLeg.addOrReplaceChild("right_back_leg2", CubeListBuilder.create()
				.texOffs(23, 24).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.5918411493512771F, 0.0F, 0.0F));


		var leftFrontLeg = base.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(20, 11).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.5F, 2.0F, 0.5F, 0.22759093446006054F, -0.7285004297824331F, -1.2292353921796064F));
		leftFrontLeg.addOrReplaceChild("left_front_leg2", CubeListBuilder.create()
				.texOffs(20, 14).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));

		var leftMiddleLeg = base.addOrReplaceChild("left_middle_leg", CubeListBuilder.create()
				.texOffs(20, 16).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.5F, 2.0F, 1.0F, 1.1838568316277536F, 1.0471975511965976F, 0.091106186954104F));
		leftMiddleLeg.addOrReplaceChild("left_middle_leg2", CubeListBuilder.create()
				.texOffs(20, 19).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.22759093446006054F, 0.0F, 0.0F));

		var leftBackLeg = base.addOrReplaceChild("left_back_leg", CubeListBuilder.create()
				.texOffs(20, 21).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.5F, 2.0F, 1.5F, 0.8651597102135892F, 0.22759093446006054F, 0.0F));
		leftBackLeg.addOrReplaceChild("left_back_leg2", CubeListBuilder.create()
				.texOffs(20, 24).addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.5918411493512771F, 0.0F, 0.0F));


		base.addOrReplaceChild("right_elytra", CubeListBuilder.create()
				.texOffs(33, 0).addBox(-2.0F, 0.0F, 0.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F, 0.9105382707654417F));
		base.addOrReplaceChild("left_elytra", CubeListBuilder.create()
				.texOffs(20, 0).addBox(0.0F, 0.0F, 0.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.40980330836826856F, -0.9105382707654417F));

		base.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(32, 7).addBox(-7.0F, 0.0F, 0.0F, 7, 0, 3),
			PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.136659280431156F, -0.18203784098300857F, -0.091106186954104F));
		base.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(17, 7).addBox(0.0F, 0.0F, 0.0F, 7, 0, 3),
			PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.136659280431156F, 0.18203784098300857F, 0.091106186954104F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void setupAnim(Infestation entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float speed = 2.5f;

		float flap = Mth.sin(ageInTicks * speed);
		float flap2 = Mth.sin(ageInTicks * speed - 1.72F);

		this.rightWing.xRot = 0.136659280431156F + 0.05F + flap2 * 0.4F;
		this.rightWing.zRot = -0.091106186954104F + flap * 0.2F;

		this.leftWing.xRot = 0.136659280431156F + 0.05F + flap2 * 0.4F;
		this.leftWing.zRot = 0.091106186954104F - flap * 0.2F;

		float crunch = (float) Math.pow(Mth.sin(ageInTicks * speed * 0.19F), 12);

		this.leftMandible.yRot = -0.36425021489121656F + 0.1F + crunch * 0.15F;
		this.leftMandible.zRot = 0.091106186954104F - 0.2F + crunch * 0.15F;

		this.rightMandible.yRot = 0.36425021489121656F - 0.1F - crunch * 0.15F;
		this.rightMandible.zRot = 0.091106186954104F + 0.2f - crunch * 0.15F;

		float flop = Mth.sin(ageInTicks * speed * 0.12F);

		this.leftElytra.zRot = -0.9105382707654417F + flop * 0.05F;
		this.rightElytra.zRot = 0.9105382707654417F - flop * 0.05F;

		this.leftAntenna.zRot = flop * 0.05F;
		this.rightAntenna.zRot = -flop * 0.05F;

		this.leftFrontLeg.zRot = -1.2292353921796064F - flop * 0.05F;
		this.leftMiddleLeg.zRot = 0.091106186954104F - flop * 0.05F;
		this.leftBackLeg.xRot = 0.8651597102135892F + flop * 0.05F;

		this.rightFrontLeg.zRot = 1.2292353921796064F + flop * 0.05F;
		this.rightMiddleLeg.zRot = 0.091106186954104F + flop * 0.05F;
		this.rightBackLeg.xRot = 0.8651597102135892F + flop * 0.05F;
	}
}
