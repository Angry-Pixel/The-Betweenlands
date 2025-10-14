package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import thebetweenlands.client.model.AnimationBlender;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;

public class ChiromawMatriarchModel extends MowzieModelBase<ChiromawMatriarch> {

	private final ModelPart body3;
	private final ModelPart leftArm1;
	private final ModelPart leftArm2;
	private final ModelPart leftArm3;
	private final ModelPart rightArm1;
	private final ModelPart rightArm2;
	private final ModelPart rightArm3;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;
	private final ModelPart head;
	private final ModelPart leftCrest;
	private final ModelPart rightCrest;
	private final ModelPart middleCrest;
	private final ModelPart jaw;

	private final ModelPart[] tailParts;
	private final ModelPart[] bodyAndTailParts;

	public ChiromawMatriarchModel(ModelPart root) {
		super(root);
		var body1 = root.getChild("body1");
		this.body3 = body1.getChild("body2").getChild("body3");
		this.leftArm1 = this.body3.getChild("left_arm1");
		this.leftArm2 = this.leftArm1.getChild("left_arm2");
		this.leftArm3 = this.leftArm2.getChild("left_arm3");
		this.rightArm1 = this.body3.getChild("right_arm1");
		this.rightArm2 = this.rightArm1.getChild("right_arm2");
		this.rightArm3 = this.rightArm2.getChild("right_arm3");
		this.leftLeg1 = body1.getChild("left_leg1");
		this.leftLeg2 = this.leftLeg1.getChild("left_leg2");
		this.rightLeg1 = body1.getChild("right_leg1");
		this.rightLeg2 = this.rightLeg1.getChild("right_leg2");
		this.head = this.body3.getChild("neck1").getChild("neck2").getChild("head1");
		this.leftCrest = this.head.getChild("left_crest1a");
		this.middleCrest = this.head.getChild("middle_crest1");
		this.rightCrest = this.head.getChild("right_crest1a");
		this.jaw = this.head.getChild("head2").getChild("jaw");

		this.tailParts = new ModelPart[]{
			body1.getChild("tail1"),
			body1.getChild("tail1").getChild("tail2"),
			body1.getChild("tail1").getChild("tail2").getChild("tail3"),
			body1.getChild("tail1").getChild("tail2").getChild("tail3").getChild("tail4"),
			body1.getChild("tail1").getChild("tail2").getChild("tail3").getChild("tail4").getChild("tail5")
		};
		this.bodyAndTailParts = ArrayUtils.addAll(new ModelPart[]{this.body3}, this.tailParts);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body1 = partDefinition.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 8, 6, 6),
			PartPose.offsetAndRotation(0.0F, 11.0F, 7.0F, -0.9560913642424937F, 0.0F, 0.0F));
		var body2 = body1.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-5.0F, 0.0F, -7.0F, 10, 7, 7),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));
		var body3 = body2.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-5.0F, 0.0F, -7.0F, 10, 7, 7),
			PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, 0.36425021489121656F, 0.0F, 0.0F));

		body3.addOrReplaceChild("vertebrate1", CubeListBuilder.create()
				.texOffs(0, 107).addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, -0.136659280431156F, 0.0F, 0.0F));
		body3.addOrReplaceChild("vertebrate2", CubeListBuilder.create()
				.texOffs(0, 111).addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.136659280431156F, 0.0F, 0.0F));
		body2.addOrReplaceChild("vertebrate3", CubeListBuilder.create()
				.texOffs(0, 115).addBox(-1.0F, -1.0F, 0.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.136659280431156F, 0.0F, 0.0F));

		var rightArm1 = body3.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(88, 0).addBox(-2.0F, -1.0F, -1.0F, 3, 4, 3),
			PartPose.offsetAndRotation(-4.5F, 2.0F, -6.5F, 0.4553564018453205F, 0.0F, 1.0016444577195458F));
		var rightArm2 = rightArm1.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(88, 8).addBox(-2.0F, 0.0F, -2.0F, 2, 12, 2),
			PartPose.offsetAndRotation(0.01F, 3.0F, 2.0F, -0.091106186954104F, 0.0F, 0.0F));
		var rightArm3 = rightArm2.addOrReplaceChild("right_arm3", CubeListBuilder.create()
				.texOffs(88, 23).addBox(-2.0F, 0.0F, -2.0F, 2, 18, 2),
			PartPose.offsetAndRotation(0.01F, 12.0F, 0.0F, -0.5918411493512771F, 0.0F, 0.0F));
		rightArm2.addOrReplaceChild("right_wing1", CubeListBuilder.create()
				.texOffs(112, 42).addBox(0.0F, -2.0F, 0.0F, 0, 14, 8),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.0F));
		rightArm3.addOrReplaceChild("right_wing2", CubeListBuilder.create()
				.texOffs(108, 15).addBox(0.0F, -4.0F, 0.0F, 0, 24, 10),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.0F));
		var rightClaw = rightArm3.addOrReplaceChild("right_claw1", CubeListBuilder.create()
				.texOffs(88, 44).addBox(-2.0F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.01F, 18.0F, 0.0F, -0.8196066167365371F, 0.0F, 0.0F));
		rightClaw.addOrReplaceChild("right_claw2", CubeListBuilder.create()
				.texOffs(88, 50).addBox(-2.0F, 0.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.01F, 3.0F, -2.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var leftArm1 = body3.addOrReplaceChild("left_arm1", CubeListBuilder.create()
				.texOffs(75, 0).addBox(-1.0F, -1.0F, -1.0F, 3, 4, 3),
			PartPose.offsetAndRotation(4.5F, 2.0F, -6.5F, 0.4553564018453205F, 0.0F, -1.0016444577195458F));
		var leftArm2 = leftArm1.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(75, 8).addBox(0.0F, 0.0F, -2.0F, 2, 12, 2),
			PartPose.offsetAndRotation(-0.01F, 3.0F, 2.0F, -0.091106186954104F, 0.0F, 0.0F));
		var leftArm3 = leftArm2.addOrReplaceChild("left_arm3", CubeListBuilder.create()
				.texOffs(75, 23).addBox(0.0F, 0.0F, -2.0F, 2, 18, 2),
			PartPose.offsetAndRotation(-0.01F, 12.0F, 0.0F, -0.5918411493512771F, 0.0F, 0.0F));
		leftArm2.addOrReplaceChild("left_wing1", CubeListBuilder.create()
				.texOffs(112, 57).addBox(0.0F, -2.0F, 0.0F, 0, 14, 8),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F, 0.0F));
		leftArm3.addOrReplaceChild("left_wing2", CubeListBuilder.create()
				.texOffs(108, -10).addBox(0.0F, -4.0F, 0.0F, 0, 24, 10),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F, 0.0F));
		var leftClaw = leftArm3.addOrReplaceChild("left_claw1", CubeListBuilder.create()
				.texOffs(75, 44).addBox(0.0F, 0.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.01F, 18.0F, 0.0F, -0.8196066167365371F, 0.0F, 0.0F));
		leftClaw.addOrReplaceChild("left_claw2", CubeListBuilder.create()
				.texOffs(75, 50).addBox(0.0F, 0.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(-0.01F, 3.0F, -2.0F, 0.6373942428283291F, 0.0F, 0.0F));

		var leftLeg1 = body1.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(25, 50).addBox(0.0F, -1.0F, -2.0F, 3, 6, 4),
			PartPose.offsetAndRotation(3.0F, 1.5F, 4.5F, -0.7285004297824331F, -0.136659280431156F, -0.27314402793711257F));
		var leftLeg2 = leftLeg1.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(25, 61).addBox(0.0F, 0.0F, 0.0F, 3, 5, 3),
			PartPose.offsetAndRotation(-0.01F, 5.0F, 2.0F, 2.367539130330308F, 0.0F, 0.0F));
		var leftLeg3 = leftLeg2.addOrReplaceChild("left_leg3", CubeListBuilder.create()
				.texOffs(25, 70).addBox(-1.0F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(1.5F, 5.0F, 3.0F, -0.9560913642424937F, 0.0F, 0.0F));
		leftLeg3.addOrReplaceChild("left_claw", CubeListBuilder.create()
				.texOffs(25, 79).addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, -2.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var rightLeg1 = body1.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(40, 50).addBox(-3.0F, -1.0F, -2.0F, 3, 6, 4),
			PartPose.offsetAndRotation(-3.0F, 1.5F, 4.5F, -0.7285004297824331F, 0.136659280431156F, 0.27314402793711257F));
		var rightLeg2 = rightLeg1.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(40, 61).addBox(-3.0F, 0.0F, 0.0F, 3, 5, 3),
			PartPose.offsetAndRotation(0.01F, 5.0F, 2.0F, 2.367539130330308F, 0.0F, 0.0F));
		var rightLeg3 = rightLeg2.addOrReplaceChild("right_leg3", CubeListBuilder.create()
				.texOffs(40, 70).addBox(-1.0F, 0.0F, -2.0F, 2, 6, 2),
			PartPose.offsetAndRotation(-1.5F, 5.0F, 3.0F, -0.9560913642424937F, 0.0F, 0.0F));
		rightLeg3.addOrReplaceChild("right_claw", CubeListBuilder.create()
				.texOffs(40, 79).addBox(-1.0F, 0.0F, -2.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, -2.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var neck1 = body3.addOrReplaceChild("neck1", CubeListBuilder.create()
				.texOffs(0, 43).addBox(-2.5F, 0.0F, -3.0F, 5, 5, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, 0.40980330836826856F, 0.0F, 0.0F));
		var neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create()
				.texOffs(0, 52).addBox(-2.5F, -5.0F, -4.0F, 5, 5, 4),
			PartPose.offsetAndRotation(0.0F, 5.0F, -3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		var head1 = neck2.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(35, 0).addBox(-4.0F, -5.0F, -9.0F, 8, 5, 9),
			PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, 0.136659280431156F, 0.0F, 0.0F));
		var head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(35, 15).addBox(-4.0F, 0.0F, -3.0F, 8, 3, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var leftCrest = head1.addOrReplaceChild("left_crest1a", CubeListBuilder.create()
				.texOffs(54, 15).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4),
			PartPose.offsetAndRotation(2.5F, -4.0F, -3.5F, 0.4553564018453205F, 0.091106186954104F, 0.6373942428283291F));
		leftCrest.addOrReplaceChild("left_crest1b", CubeListBuilder.create()
				.texOffs(55, 20).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.18203784098300857F, 0.0F, 0.0F));
		head1.addOrReplaceChild("left_crest2", CubeListBuilder.create()
				.texOffs(60, 27).addBox(0.0F, -4.0F, 0.0F, 0, 6, 3),
			PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, 0.27314402793711257F, 0.0F));
		var middleCrest = head1.addOrReplaceChild("middle_crest1", CubeListBuilder.create()
				.texOffs(55, 24).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3),
			PartPose.offsetAndRotation(0.0F, -5.0F, -3.0F, 0.36425021489121656F, 0.0F, 0.0F));
		middleCrest.addOrReplaceChild("middle_crest2", CubeListBuilder.create()
				.texOffs(62, 24).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var rightCrest = head1.addOrReplaceChild("right_crest1a", CubeListBuilder.create()
				.texOffs(61, 15).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4),
			PartPose.offsetAndRotation(-2.5F, -4.0F, -3.5F, 0.4553564018453205F, -0.091106186954104F, -0.6373942428283291F));
		rightCrest.addOrReplaceChild("right_crest1b", CubeListBuilder.create()
				.texOffs(62, 20).addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.18203784098300857F, 0.0F, 0.0F));
		head1.addOrReplaceChild("right_crest2", CubeListBuilder.create()
				.texOffs(60, 34).addBox(0.0F, -4.0F, 0.0F, 0, 6, 3),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, -0.27314402793711257F, 0.0F));

		head1.addOrReplaceChild("upper_left_teeth", CubeListBuilder.create()
				.texOffs(35, 37).addBox(0.0F, 0.0F, -2.0F, 0, 1, 5),
			PartPose.offsetAndRotation(4.0F, 0.0F, -6.0F, 0.0F, 0.0F, -0.091106186954104F));
		head1.addOrReplaceChild("upper_teeth", CubeListBuilder.create()
				.texOffs(35, 48).addBox(-3.0F, 0.0F, 0.0F, 6, 1, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, -0.091106186954104F, 0.0F, 0.0F));
		head1.addOrReplaceChild("upper_right_teeth", CubeListBuilder.create()
				.texOffs(46, 37).addBox(0.0F, 0.0F, -2.0F, 0, 1, 5),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -6.0F, 0.0F, 0.0F, 0.091106186954104F));

		var upperLeftCanine = head1.addOrReplaceChild("upper_left_canine1", CubeListBuilder.create()
				.texOffs(35, 44).addBox(-1.0F, -1.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(4.0F, 0.0F, -9.0F, -0.4553564018453205F, 0.0F, -0.18203784098300857F));
		upperLeftCanine.addOrReplaceChild("upper_left_canine2", CubeListBuilder.create()
				.texOffs(40, 44).addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));
		var upperRightCanine = head1.addOrReplaceChild("upper_right_canine1", CubeListBuilder.create()
				.texOffs(45, 44).addBox(0.0F, -1.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -9.0F, -0.4553564018453205F, 0.0F, 0.18203784098300857F));
		upperRightCanine.addOrReplaceChild("upper_right_canine2", CubeListBuilder.create()
				.texOffs(50, 44).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var jaw = head2.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(35, 22).addBox(-3.0F, 0.0F, -6.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, 1.0F, -3.0F, 0.8651597102135892F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("lower_left_teeth", CubeListBuilder.create()
				.texOffs(35, 26).addBox(0.0F, -2.0F, -2.0F, 0, 2, 5),
			PartPose.offsetAndRotation(3.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.136659280431156F));
		jaw.addOrReplaceChild("lower_teeth", CubeListBuilder.create()
				.texOffs(35, 39).addBox(-2.0F, -2.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.136659280431156F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("lower_right_teeth", CubeListBuilder.create()
				.texOffs(46, 26).addBox(0.0F, -2.0F, -2.0F, 0, 2, 5),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.136659280431156F));

		var lowerLeftCanine = jaw.addOrReplaceChild("lower_left_canine1", CubeListBuilder.create()
				.texOffs(35, 34).addBox(-1.0F, -2.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(3.0F, 0.0F, -6.0F, 0.22759093446006054F, 0.0F, 0.18203784098300857F));
		lowerLeftCanine.addOrReplaceChild("lower_left_canine2", CubeListBuilder.create()
				.texOffs(40, 34).addBox(-1.0F, -1.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));
		var lowerRightCanine = jaw.addOrReplaceChild("lower_right_canine1", CubeListBuilder.create()
				.texOffs(45, 34).addBox(0.0F, -2.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -6.0F, 0.22759093446006054F, 0.0F, -0.18203784098300857F));
		lowerRightCanine.addOrReplaceChild("lower_right_canine2", CubeListBuilder.create()
				.texOffs(50, 34).addBox(0.0F, -1.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var tail1 = body1.addOrReplaceChild("tail1", CubeListBuilder.create()
				.texOffs(0, 62).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 4),
			PartPose.offsetAndRotation(0.0F, 4.0F, 6.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create()
				.texOffs(0, 71).addBox(-2.0F, -4.0F, 0.0F, 4, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create()
				.texOffs(0, 80).addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5),
			PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, 0.22759093446006054F, 0.0F, 0.0F));
		var tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create()
				.texOffs(0, 89).addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.22759093446006054F, 0.0F, 0.0F));
		tail4.addOrReplaceChild("tail5", CubeListBuilder.create()
				.texOffs(0, 98).addBox(-1.5F, -3.0F, 0.0F, 3, 3, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.136659280431156F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}

	@Override
	public void setupAnim(ChiromawMatriarch entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float globalDegree = 0.5F;
		float rippleSpeed = 1F;

		float frame = entity.tickCount + partialTick;
		float flapFrame = entity.flapTicks + partialTick;

		float landingPercent = entity.landingTimer.getAnimationProgressSmooth(partialTick);
		float nestingPercent = entity.nestingTimer.getAnimationProgressSmooth(partialTick);
		float spinningPercent = entity.spinningTimer.getAnimationProgressSmooth(partialTick);
		float flyingPercent = Math.max(0, 1.0f - landingPercent - nestingPercent - spinningPercent);
		float featherControl = (Mth.sin(0.05F * frame - Mth.cos(0.05F * frame))) * 0.5F;

		AnimationBlender<ChiromawMatriarchModel> blender = new AnimationBlender<>(this);

		//Nesting animation state
		blender.addState(model -> {
			model.resetPose();
			model.jaw.xRot = 0.8651597102135892F;
			model.head.xRot = 0.136659280431156F;
			model.leftCrest.xRot -= 0.25F * featherControl * Mth.cos(0.5F * frame);
			model.rightCrest.xRot -= 0.25F * featherControl * Mth.cos(0.5F * frame);
			model.middleCrest.xRot -= 0.25F * featherControl * Mth.cos(0.5F * frame);
			walk(model.jaw, rippleSpeed * 0.125f, globalDegree * 0.5f, false, 2.0f, 0f, frame, 1F);
			walk(model.body3, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
			walk(model.rightArm3, rippleSpeed * 0.125f, globalDegree * 0.125f, false, 2.0f, 0f, frame, 1F);
			walk(model.leftArm3, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
			swing(model.rightArm3, rippleSpeed * 0.125f, globalDegree * 0.125f, true, 2.0f, 0f, frame, 1F);
			swing(model.leftArm3, rippleSpeed * 0.125f, globalDegree * 0.125f, false, 2.0f, 0f, frame, 1F);
			chainSwing(model.tailParts, rippleSpeed * 0.125f, globalDegree * 0.25f, 2f, frame, 1F);
		}, () -> nestingPercent);

		class FlyingPose {
			public void apply(ChiromawMatriarchModel model, float globalDegree, float rippleSpeed) {
				model.head.xRot = -0.398132F;
				if (!entity.isVehicle()) {
					model.rightLeg1.xRot = 0.7285004297824331F;
					model.leftLeg1.xRot = 0.7285004297824331F;
					model.rightLeg2.xRot = 1.367539130330308F;
					model.leftLeg2.xRot = 1.367539130330308F;
				}
				flap(model.rightArm1, entity.flapSpeed, globalDegree * 1.2f, false, 2.0f, 0f, flapFrame, 1F);
				swing(model.rightArm2, entity.flapSpeed, globalDegree * 1.5f, false, 2.8f, 0.5f, flapFrame, 1F);
				flap(model.rightArm3, entity.flapSpeed, globalDegree * 2.5f, false, 2.0f, 0f, flapFrame, 1F);

				flap(model.leftArm1, entity.flapSpeed, globalDegree * 1.2f, true, 2.0f, 0f, flapFrame, 1F);
				swing(model.leftArm2, entity.flapSpeed, globalDegree * 1.5f, true, 2.8f, -0.5f, flapFrame, 1F);
				flap(model.leftArm3, entity.flapSpeed, globalDegree * 2.5f, true, 2.0f, 0f, flapFrame, 1F);

				walk(model.rightArm2, entity.flapSpeed, globalDegree, true, 1.2f, 0.15f, flapFrame, 1F);
				walk(model.rightArm3, entity.flapSpeed, globalDegree * 1.2f, false, 1.2f, -0.9f, flapFrame, 1F);
				walk(model.leftArm2, entity.flapSpeed, globalDegree, true, 1.2f, 0.15f, flapFrame, 1F);
				walk(model.leftArm3, entity.flapSpeed, globalDegree * 1.2f, false, 1.2f, -0.9f, flapFrame, 1F);

				chainWave(model.bodyAndTailParts, rippleSpeed * 0.5f, globalDegree * 0.25f, 2f, frame, 1F);
				swing(model.head, rippleSpeed * 0.5f, globalDegree * 0.25f, false, 2.0f, 0f, frame, 1F);
				walk(model.head, rippleSpeed * 0.5f, globalDegree * 0.25f, true, 2.0f, 0f, frame, 1F);
				walk(model.jaw, rippleSpeed * 0.5f, globalDegree, true, 2.0f, 0f, frame, 1F);
			}
		}

		FlyingPose idlePose = new FlyingPose();

		//Idle (flying) animation state
		blender.addState(model -> {
			model.resetPose();

			idlePose.apply(model, globalDegree, rippleSpeed);
		}, () -> flyingPercent);

		//Landing animation state
		blender.addState(model -> {
			model.resetPose();
			model.rightArm1.xRot = 0.3553564018453205F;
			model.leftArm1.xRot = 0.3553564018453205F;

			idlePose.apply(model, 0.25f, 0);
		}, () -> landingPercent);

		//Spinning animation state
		blender.addState(model -> {
			model.resetPose();
			model.jaw.xRot = 0.8651597102135892F;
			model.head.xRot = 0.136659280431156F;
		}, () -> spinningPercent);

		blender.setAngles(false);
	}
}
