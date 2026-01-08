package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.wall.WallLamprey;

public class WallLampreyModel extends MowzieModelBase<WallLamprey> {

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart midHead;
	private final ModelPart frontHead;
	private final ModelPart leftFlap;
	private final ModelPart rightFlap;
	private final ModelPart leftNostril;
	private final ModelPart rightNostril;
	private final ModelPart teeth1;
	private final ModelPart teeth2;
	private final ModelPart teeth3;
	private final ModelPart teeth4;

	public WallLampreyModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body_base");
		this.head = this.body.getChild("head_base");
		this.midHead = this.head.getChild("head_mid");
		this.frontHead = this.midHead.getChild("head_front");
		this.rightFlap = this.midHead.getChild("right_flap");
		this.leftFlap = this.midHead.getChild("left_flap");
		this.rightNostril = this.frontHead.getChild("right_nostril1");
		this.leftNostril = this.frontHead.getChild("left_nostril1");
		var mouth = this.frontHead.getChild("mouth_top");
		this.teeth1 = mouth.getChild("teeth1");
		this.teeth2 = mouth.getChild("teeth2");
		this.teeth3 = mouth.getChild("teeth3");
		this.teeth4 = this.frontHead.getChild("teeth4");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body_base = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8, 8, 8),
			PartPose.offsetAndRotation(0.0F, 22.0F, 1.0F, -0.31869712141416456F, 0.0F, 0.0F));

		var head_base = body_base.addOrReplaceChild("head_base", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3),
			PartPose.offsetAndRotation(0.01F, -6.0F, -4.0F, 0.136659280431156F, 0.0F, 0.0F));
		head_base.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(7, 53).addBox(-1.0F, 0.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 3.0F, -3.0F, 0.0F, 0.091106186954104F, -0.136659280431156F));
		head_base.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(0, 53).addBox(0.0F, 0.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(4.0F, 3.0F, -3.0F, 0.0F, -0.091106186954104F, 0.136659280431156F));

		var head_mid = head_base.addOrReplaceChild("head_mid", CubeListBuilder.create()
				.texOffs(0, 29).addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3),
			PartPose.offsetAndRotation(0.01F, 0.0F, -3.0F, 0.136659280431156F, 0.0F, 0.0F));
		head_mid.addOrReplaceChild("right_flap", CubeListBuilder.create()
				.texOffs(38, 54).addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, 0.0F, -1.1838568316277536F));
		head_mid.addOrReplaceChild("left_flap", CubeListBuilder.create()
				.texOffs(33, 54).addBox(0.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, 0.0F, 1.1838568316277536F));

		var head_front = head_mid.addOrReplaceChild("head_front", CubeListBuilder.create()
				.texOffs(0, 41).addBox(-4.0F, 0.0F, -3.0F, 8, 8, 3),
			PartPose.offsetAndRotation(0.01F, 0.0F, -3.0F, 0.136659280431156F, 0.0F, 0.0F));

		var nostril_right1 = head_front.addOrReplaceChild("right_nostril1", CubeListBuilder.create()
				.texOffs(33, 50).addBox(-1.0F, 0.0F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.9105382707654417F));
		var nostril_right2 = nostril_right1.addOrReplaceChild("right_nostril2", CubeListBuilder.create()
				.texOffs(36, 50).addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));
		nostril_right2.addOrReplaceChild("right_nostril3", CubeListBuilder.create()
				.texOffs(40, 50).addBox(-2.0F, 0.0F, 0.0F, 2, 0, 3),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));

		var nostril_left1 = head_front.addOrReplaceChild("left_nostril1", CubeListBuilder.create()
				.texOffs(33, 46).addBox(0.0F, 0.0F, 0.0F, 1, 0, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.9105382707654417F));
		var nostril_left2 = nostril_left1.addOrReplaceChild("left_nostril2", CubeListBuilder.create()
				.texOffs(36, 46).addBox(0.0F, 0.0F, 0.0F, 2, 0, 2),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F, 0.0F));
		nostril_left2.addOrReplaceChild("left_nostril3", CubeListBuilder.create()
				.texOffs(40, 46).addBox(0.0F, 0.0F, 0.0F, 2, 0, 3),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F, 0.0F));

		var mouthpart_top = head_front.addOrReplaceChild("mouth_top", CubeListBuilder.create()
				.texOffs(33, 0).addBox(-4.0F, -1.0F, -2.0F, 8, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.045553093477052F, 0.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_left", CubeListBuilder.create()
				.texOffs(33, 12).addBox(0.0F, -3.0F, -2.0F, 2, 6, 3),
			PartPose.offset(3.0F, 4.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_right", CubeListBuilder.create()
				.texOffs(44, 12).addBox(-2.0F, -3.0F, -2.0F, 2, 6, 3),
			PartPose.offset(-3.0F, 4.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_bottom", CubeListBuilder.create()
				.texOffs(33, 6).addBox(-4.0F, 0.0F, -2.0F, 8, 2, 3),
			PartPose.offset(0.0F, 7.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_corner1", CubeListBuilder.create()
				.texOffs(33, 22).addBox(0.0F, -1.0F, -2.0F, 1, 1, 3),
			PartPose.offset(4.0F, 1.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_corner2", CubeListBuilder.create()
				.texOffs(42, 22).addBox(0.0F, 0.0F, -2.0F, 1, 1, 3),
			PartPose.offset(4.0F, 7.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_corner3", CubeListBuilder.create()
				.texOffs(33, 27).addBox(-1.0F, -1.0F, -2.0F, 1, 1, 3),
			PartPose.offset(-4.0F, 1.0F, 0.0F));
		mouthpart_top.addOrReplaceChild("mouth_corner4", CubeListBuilder.create()
				.texOffs(42, 27).addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3),
			PartPose.offset(-4.0F, 7.0F, 0.0F));

		mouthpart_top.addOrReplaceChild("teeth1", CubeListBuilder.create()
				.texOffs(33, 32).addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0),
			PartPose.offset(0.0F, 1.0F, -0.5F));
		mouthpart_top.addOrReplaceChild("teeth2", CubeListBuilder.create()
				.texOffs(46, 32).addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0),
			PartPose.offset(0.0F, 1.0F, -1.0F));
		mouthpart_top.addOrReplaceChild("teeth3", CubeListBuilder.create()
				.texOffs(33, 39).addBox(-3.0F, 0.0F, 0.0F, 6, 6, 0),
			PartPose.offset(0.0F, 1.0F, -1.5F));
		head_front.addOrReplaceChild("teeth4", CubeListBuilder.create()
				.texOffs(46, 39).addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1),
			PartPose.offset(0.0F, 4.0F, -3.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void setupAnim(WallLamprey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {

		float hidePercent = entity.getLampreyHiddenPercent(partialTick);

		float[] relHeadLook = entity.getRelativeHeadLookAngles(partialTick);

		float relYaw = (float) (Math.toRadians(relHeadLook[0]) * (1 - hidePercent));
		float relPitch = (float) (Math.toRadians(relHeadLook[1]) * (1 - hidePercent));

		relYaw = this.easeOutCubic(Math.min(Mth.PI * 0.7F, Math.abs(relYaw)) / (Mth.PI * 0.7F)) * 0.61F * Math.signum(relYaw) / 4.0F;
		relPitch = this.easeOutCubic(Math.min(Mth.PI * 0.7F, Math.abs(relPitch)) / (Mth.PI * 0.7F)) * 0.61F * Math.signum(relPitch) / 4.0F;

		this.body.yRot += relYaw;
		this.head.yRot += relYaw;
		this.midHead.yRot += relYaw;
		this.frontHead.yRot += relYaw;

		this.body.xRot += relPitch;
		this.head.xRot += relPitch;
		this.midHead.xRot += relPitch;
		this.frontHead.xRot += relPitch;

		this.bob(this.body, 0.07f, 0.1f, false, ageInTicks, 1);

		float walkDrive = ageInTicks * 0.15F + limbSwing * limbSwingAmount * 0.25F;
		float walkDegree = 0.05F * (1 - hidePercent);

		float swingDrive = walkDrive * 0.9F + Mth.HALF_PI;
		float swingDegree = walkDegree * (1 - hidePercent);

		this.swing(this.body, 1, swingDegree, false, swingDrive, 0, 0, 1);
		this.swing(this.head, 1, swingDegree, false, swingDrive, 0, 0, 1);
		this.swing(this.midHead, 1, swingDegree, false, swingDrive, 0, 0, 1);
		this.swing(this.frontHead, 1, swingDegree, false, swingDrive, 0, 0, 1);

		this.walk(this.body, 1, walkDegree, false, walkDrive, 0, 0, 1);
		this.walk(this.head, 1, walkDegree, false, walkDrive, 0, 0, 1);
		this.walk(this.midHead, 1, walkDegree, false, walkDrive, 0, 0, 1);
		this.walk(this.frontHead, 1, walkDegree, false, walkDrive, 0, 0, 1);

		float flapDrive = ageInTicks * 0.15F * 3;
		float flapDegree = 0.1F * (1 - hidePercent);

		this.flap(this.leftFlap, 1, flapDegree, false, flapDrive, 0, 0, 1);
		this.flap(this.rightFlap, 1, flapDegree, true, flapDrive, 0, 0, 1);

		this.flap(this.leftNostril, 1, flapDegree, false, flapDrive + 1.1F, 0, 0, 1);
		this.flap(this.rightNostril, 1, flapDegree, true, flapDrive + 1.1F, 0, 0, 1);

		this.walk(this.body, 1, (float)Math.pow(hidePercent, 0.9F) * -Mth.HALF_PI, false, 0, 0, 0, 1);
		this.body.y += (float) (Math.pow(hidePercent, 1.5F) * 16.0F);

		this.teeth1.z += Mth.sin(ageInTicks) * 0.1F;
		this.teeth2.z += Mth.sin(ageInTicks + 0.8F) * 0.1F;
		this.teeth3.z += Mth.sin(ageInTicks + 1.6F) * 0.1F;
		this.teeth4.z += Mth.sin(ageInTicks + 2.4F) * 0.1F;
	}

	private float easeOutCubic(float t) {
		t -= 1;
		return (t * t * t + 1);
	}
}
