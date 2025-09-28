package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.chiromaw.Chiromaw;

public class ChiromawModel<T extends Chiromaw> extends MowzieModelBase<T> {

	protected final ModelPart body;
	private final ModelPart leftArm1;
	private final ModelPart leftArm2;
	private final ModelPart rightArm1;
	private final ModelPart rightArm2;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart head;
	private final ModelPart mouth;


	public ChiromawModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.leftArm1 = this.body.getChild("left_arm1");
		this.leftArm2 = this.leftArm1.getChild("left_arm2");
		this.rightArm1 = this.body.getChild("right_arm1");
		this.rightArm2 = this.rightArm1.getChild("right_arm2");
		var butt = this.body.getChild("butt");
		this.leftLeg = butt.getChild("left_leg1");
		this.rightLeg = butt.getChild("right_leg1");
		this.tail1 = butt.getChild("tail1");
		this.tail2 = this.tail1.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.head = this.body.getChild("neck").getChild("head");
		this.mouth = this.head.getChild("mouth");
	}

	public static LayerDefinition create() {
		return LayerDefinition.create(createBase(), 128, 64);
	}

	protected static MeshDefinition createBase() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0F, 0.0F, -2.0F, 6, 6, 4),
			PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.5462880558742251F, 0.0F, 0.0F));

		var rightArm1 = body.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(46, 29).addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 1.0F, -0.8196066167365371F, -0.091106186954104F, 0.5462880558742251F));
		var rightArm2 = rightArm1.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(55, 29).addBox(-1.01F, 0.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, -0.31869712141416456F, 0.0F, 0.0F));
		rightArm1.addOrReplaceChild("right_wing1", CubeListBuilder.create()
				.texOffs(46, 44).addBox(0.0F, 0.0F, 0.0F, 0, 7, 5),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 1.0F, 0.0F, 0.27314402793711257F, 0.0F));
		rightArm2.addOrReplaceChild("right_wing2", CubeListBuilder.create()
				.texOffs(57, 44).addBox(0.0F, -1.0F, 0.0F, 0, 8, 5),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 1.0F, 0.0F, 0.27314402793711257F, 0.0F));

		var leftArm1 = body.addOrReplaceChild("left_arm1", CubeListBuilder.create()
				.texOffs(46, 19).addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 1.0F, -0.8196066167365371F, 0.091106186954104F, -0.5462880558742251F));
		var leftArm2 = leftArm1.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(55, 19).addBox(-0.99F, 0.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, -0.31869712141416456F, 0.0F, 0.0F));
		leftArm1.addOrReplaceChild("left_wing1", CubeListBuilder.create()
				.texOffs(46, 34).addBox(0.0F, 0.0F, 0.0F, 0, 7, 5),
			PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, -0.27314402793711257F, 0.0F));
		leftArm2.addOrReplaceChild("left_wing2", CubeListBuilder.create()
				.texOffs(57, 34).addBox(0.0F, -1.0F, 0.0F, 0, 8, 5),
			PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, -0.27314402793711257F, 0.0F));

		var butt = body.addOrReplaceChild("butt", CubeListBuilder.create()
				.texOffs(0, 11).addBox(-2.0F, 0.0F, -3.0F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 2.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var rightLeg = butt.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(57, 0).addBox(-2.0F, -1.0F, -1.5F, 2, 5, 3),
			PartPose.offsetAndRotation(-1.5F, 2.0F, -1.0F, -2.276432943376204F, 0.5009094953223726F, -0.27314402793711257F));
		rightLeg.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(57, 9).addBox(-1.01F, 0.0F, -1.5F, 2, 7, 2),
			PartPose.offsetAndRotation(-1.0F, 4.0F, 0.0F, 2.1855012893472994F, 0.0F, 0.0F));
		var leftLeg = butt.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(46, 0).addBox(0.0F, -1.0F, -1.5F, 2, 5, 3),
			PartPose.offsetAndRotation(1.5F, 2.0F, -1.0F, -2.276432943376204F, -0.5009094953223726F, 0.27314402793711257F));
		leftLeg.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(46, 9).addBox(-0.99F, 0.0F, -1.5F, 2, 7, 2),
			PartPose.offsetAndRotation(1.0F, 4.0F, 0.0F, 2.1855012893472994F, 0.0F, 0.0F));
		var tail1 = butt.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.27314402793711257F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var neck = body.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.7285004297824331F, 0.0F, 0.0F));
		var head = neck.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(21, 7).addBox(-3.0F, -2.0F, -6.0F, 6, 4, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));
		head.addOrReplaceChild("head_connection", CubeListBuilder.create()
				.texOffs(21, 18).addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2),
			PartPose.offset(0.0F, 2.0F, 0.0F));
		head.addOrReplaceChild("left_fang", CubeListBuilder.create()
				.texOffs(21, 37).addBox(-1.0F, -1.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(3.0F, 2.0F, -6.0F, -0.091106186954104F, 0.0F, -0.091106186954104F));
		head.addOrReplaceChild("right_fang", CubeListBuilder.create()
				.texOffs(26, 37).addBox(0.0F, -1.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(-3.0F, 2.0F, -6.0F, -0.091106186954104F, 0.0F, 0.091106186954104F));
		head.addOrReplaceChild("teeth_upper_left", CubeListBuilder.create()
				.texOffs(21, 39).addBox(0.0F, 0.0F, -2.0F, 0, 1, 3),
			PartPose.offsetAndRotation(3.0F, 2.0F, -3.0F, 0.0F, 0.0F, -0.091106186954104F));
		head.addOrReplaceChild("upper_teeth", CubeListBuilder.create()
				.texOffs(21, 44).addBox(-2.0F, 0.0F, 0.0F, 4, 1, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, -6.0F, -0.091106186954104F, 0.0F, 0.0F));
		head.addOrReplaceChild("teeth_upper_right", CubeListBuilder.create()
				.texOffs(27, 39).addBox(0.0F, 0.0F, -2.0F, 0, 1, 3),
			PartPose.offsetAndRotation(-3.0F, 2.0F, -3.0F, 0.0F, 0.0F, 0.091106186954104F));

		var mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create()
				.texOffs(21, 23).addBox(-2.5F, -1.0F, -5.0F, 5, 2, 5),
			PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.9560913642424937F, 0.0F, 0.0F));
		mouth.addOrReplaceChild("teeth_lower_left", CubeListBuilder.create()
				.texOffs(21, 26).addBox(0.0F, -2.0F, -2.0F, 0, 2, 5),
			PartPose.offsetAndRotation(2.5F, -1.0F, -3.0F, 0.0F, 0.0F, 0.22759093446006054F));
		mouth.addOrReplaceChild("lower_teeth", CubeListBuilder.create()
				.texOffs(21, 34).addBox(-2.5F, -2.0F, 0.0F, 5, 2, 0),
			PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, 0.136659280431156F, 0.0F, 0.0F));
		mouth.addOrReplaceChild("teeth_lower_right", CubeListBuilder.create()
				.texOffs(32, 26).addBox(0.0F, -2.0F, -2.0F, 0, 2, 5),
			PartPose.offsetAndRotation(-2.5F, -1.0F, -3.0F, 0.0F, 0.0F, -0.22759093446006054F));

		return definition;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float flap = Mth.sin(ageInTicks * 0.5F) * 0.6F;
		if (entity.isHanging()) {
			this.rightArm1.zRot = 0.5462880558742251F;
			this.rightArm2.zRot = 0F;
			this.leftArm1.zRot = -0.5462880558742251F;
			this.leftArm2.zRot = 0F;

			this.rightArm1.yRot = 0.0091106186954104F;
			this.rightArm2.yRot = 0F;
			this.leftArm1.yRot = -0.0091106186954104F;
			this.leftArm2.yRot = 0;

			rightLeg.xRot = -2.276432943376204F;
			leftLeg.xRot = -2.276432943376204F;

			this.tail1.xRot = 0.27314402793711257F;
			this.tail2.xRot = 0.36425021489121656F;
			this.tail3.xRot = 0.40980330836826856F;

			this.tail1.xRot = 0.27314402793711257F;
			this.tail2.xRot = 0.36425021489121656F;
			this.tail3.xRot = 0.40980330836826856F;

			this.mouth.xRot = 0.9560913642424937F;
			this.head.xRot = 0.091106186954104F;
		} else {
			this.rightArm1.zRot = 0.5462880558742251F;
			this.rightArm2.zRot = 0F;
			this.leftArm1.zRot = -0.5462880558742251F;
			this.leftArm2.zRot = 0;

			this.rightArm1.yRot = 0.9091106186954104F;
			this.rightArm2.yRot = 0F;
			this.leftArm1.yRot = -0.9091106186954104F;
			this.leftArm2.yRot = 0;

			float globalSpeed = 1F;
			float globalDegree = 1F;

			this.swing(this.rightArm1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, ageInTicks, 1F);
			this.flap(this.rightArm2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, ageInTicks, 1F);
			this.swing(this.leftArm1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, ageInTicks, 1F);
			this.flap(this.leftArm2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, ageInTicks, 1F);

			this.walk(this.rightArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, ageInTicks, 1F);
			this.walk(this.rightArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, ageInTicks, 1F);
			this.walk(this.leftArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, ageInTicks, 1F);
			this.walk(this.leftArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, ageInTicks, 1F);

			this.rightLeg.xRot = -2.276432943376204F + flap * 0.5F;
			this.leftLeg.xRot = -2.276432943376204F + flap * 0.5F;

			this.tail1.xRot = 0.27314402793711257F + flap * 0.5F;
			this.tail2.xRot = 0.36425021489121656F + flap * 0.25F;
			this.tail3.xRot = 0.40980330836826856F + flap * 0.125F;

			this.mouth.xRot = 0.9560913642424937F - flap * 0.5F;
			this.head.xRot = -0.698132F;
		}
	}
}
