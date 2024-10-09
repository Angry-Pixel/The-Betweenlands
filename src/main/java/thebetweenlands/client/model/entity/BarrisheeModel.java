package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.boss.Barrishee;

public class BarrisheeModel extends MowzieModelBase<Barrishee> {

	private final ModelPart root;
	private final ModelPart neck;
	private final ModelPart belly_1;
	private final ModelPart shoulder_right;
	private final ModelPart arm_right_2;
	private final ModelPart hand_right;
	private final ModelPart finger_right_mid;
	private final ModelPart finger_right_outer;
	private final ModelPart finger_right_inner;
	private final ModelPart finger_right_mid_1;
	private final ModelPart finger_right_mid_2;
	private final ModelPart finger_right_outer_1;
	private final ModelPart finger_right_outer_2;
	private final ModelPart finger_right_inner_1;
	private final ModelPart finger_right_inner_2;
	private final ModelPart shoulder_left;
	private final ModelPart arm_left_2;
	private final ModelPart hand_left;
	private final ModelPart finger_left_mid;
	private final ModelPart finger_left_outer;
	private final ModelPart finger_left_inner;
	private final ModelPart finger_left_mid_1;
	private final ModelPart finger_left_mid_2;
	private final ModelPart finger_left_outer_1;
	private final ModelPart finger_left_outer_2;
	private final ModelPart finger_left_inner_1;
	private final ModelPart finger_left_inner_2;
	private final ModelPart head_main;
	private final ModelPart jaw_back;
	private final ModelPart jaw;
	private final ModelPart cog1;
	private final ModelPart cogbeam;

	public BarrisheeModel(ModelPart root) {
		this.root = root.getChild("base_rotation_bit");
		this.neck = this.root.getChild("neck");
		this.head_main = this.neck.getChild("head_main");
		this.jaw_back = this.head_main.getChild("jaw_back");
		this.jaw = this.jaw_back.getChild("jaw");

		this.belly_1 = this.root.getChild("belly_1");
		this.cog1 = this.belly_1.getChild("cog1_1");
		this.cogbeam = this.belly_1.getChild("belly_2").getChild("cogbeam");

		this.shoulder_right = this.root.getChild("chest_right").getChild("shoulder_right");
		this.arm_right_2 = this.shoulder_right.getChild("arm_right_1").getChild("arm_right_2");
		this.hand_right = this.arm_right_2.getChild("hand_right");
		this.finger_right_outer = this.hand_right.getChild("finger_right_outer");
		this.finger_right_outer_1 = this.finger_right_outer.getChild("finger_right_outer_1");
		this.finger_right_outer_2 = this.finger_right_outer_1.getChild("finger_right_outer_2");
		this.finger_right_mid = this.hand_right.getChild("finger_right_mid");
		this.finger_right_mid_1 = this.finger_right_mid.getChild("finger_right_mid_1");
		this.finger_right_mid_2 = this.finger_right_mid_1.getChild("finger_right_mid_2");
		this.finger_right_inner = this.hand_right.getChild("finger_right_inner");
		this.finger_right_inner_1 = this.finger_right_inner.getChild("finger_right_inner_1");
		this.finger_right_inner_2 = this.finger_right_inner_1.getChild("finger_right_inner_2");

		this.shoulder_left = this.root.getChild("chest_left").getChild("shoulder_left");
		this.arm_left_2 = this.shoulder_left.getChild("arm_left_1").getChild("arm_left_2");
		this.hand_left = this.arm_left_2.getChild("hand_left");
		this.finger_left_outer = this.hand_left.getChild("finger_left_outer");
		this.finger_left_outer_1 = this.finger_left_outer.getChild("finger_left_outer_1");
		this.finger_left_outer_2 = this.finger_left_outer_1.getChild("finger_left_outer_2");
		this.finger_left_mid = this.hand_left.getChild("finger_left_mid");
		this.finger_left_mid_1 = this.finger_left_mid.getChild("finger_left_mid_1");
		this.finger_left_mid_2 = this.finger_left_mid_1.getChild("finger_left_mid_2");
		this.finger_left_inner = this.hand_left.getChild("finger_left_inner");
		this.finger_left_inner_1 = this.finger_left_inner.getChild("finger_left_inner_1");
		this.finger_left_inner_2 = this.finger_left_inner_1.getChild("finger_left_inner_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base_rotation_bit = partDefinition.addOrReplaceChild("base_rotation_bit", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, 11.0F, -3.5F));
		var chest_left = base_rotation_bit.addOrReplaceChild("chest_left", CubeListBuilder.create()
				.texOffs(57, 0).addBox(-0.5F, -7.0F, -4.0F, 10, 12, 14),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.3490658503988659F, 0.0F, -0.18203784098300857F));
		var chest_right = base_rotation_bit.addOrReplaceChild("chest_right", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.5F, -7.0F, -4.0F, 10, 12, 14),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.3490658503988659F, 0.0F, 0.18203784098300857F));
		var belly_1 = base_rotation_bit.addOrReplaceChild("belly_1", CubeListBuilder.create()
				.texOffs(105, 0).addBox(-5.5F, -4.0F, -0.5F, 11, 8, 9),
			PartPose.offsetAndRotation(0.0F, 1.1F, 8.5F, -0.6457718232379019F, 0.0F, -0.08726646259971647F));
		var belly_2 = belly_1.addOrReplaceChild("belly_2", CubeListBuilder.create()
				.texOffs(146, 0).addBox(-7.5F, -4.5F, 0.0F, 15, 10, 11),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.5235987755982988F, 0.0F, -0.08726646259971647F));

		var shoulder_right = chest_right.addOrReplaceChild("shoulder_right", CubeListBuilder.create()
				.texOffs(0, 27).addBox(-6.0F, -4.5F, -4.5F, 7, 9, 9),
			PartPose.offsetAndRotation(-7.0F, -1.5F, 2.0F, 0.0F, -0.091106186954104F, -0.40980330836826856F));
		var arm_right_1 = shoulder_right.addOrReplaceChild("arm_right_1", CubeListBuilder.create()
				.texOffs(0, 46).addBox(-6.0F, -4.0F, -4.0F, 6, 8, 8),
			PartPose.offset(-6.0F, 0.0F, 0.0F));
		var arm_right_2 = arm_right_1.addOrReplaceChild("arm_right_2", CubeListBuilder.create()
				.texOffs(0, 63).addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.27314402793711257F));
		var hand_right = arm_right_2.addOrReplaceChild("hand_right", CubeListBuilder.create()
				.texOffs(0, 82).addBox(-4.0F, -1.5F, -8.0F, 8, 3, 8),
			PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var finger_right_outer = hand_right.addOrReplaceChild("finger_right_outer", CubeListBuilder.create()
				.texOffs(0, 102).addBox(-3.0F, -1.5F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(4.0F, 0.0F, -6.0F, -0.18203784098300857F, -0.4553564018453205F, 0.0F));
		var finger_right_outer_1 = finger_right_outer.addOrReplaceChild("finger_right_outer_1", CubeListBuilder.create()
				.texOffs(15, 102).addBox(-1.51F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(-1.5F, -1.5F, -4.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_right_outer_1.addOrReplaceChild("finger_right_outer_2", CubeListBuilder.create()
				.texOffs(28, 102).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_right_mid = hand_right.addOrReplaceChild("finger_right_mid", CubeListBuilder.create()
				.texOffs(0, 94).addBox(-1.5F, -1.5F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, -0.18203784098300857F, 0.0F, 0.0F));
		var finger_right_mid_1 = finger_right_mid.addOrReplaceChild("finger_right_mid_1", CubeListBuilder.create()
				.texOffs(17, 94).addBox(-1.51F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.5F, -5.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_right_mid_1.addOrReplaceChild("finger_right_mid_2", CubeListBuilder.create()
				.texOffs(30, 94).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_right_inner = hand_right.addOrReplaceChild("finger_right_inner", CubeListBuilder.create()
				.texOffs(0, 109).addBox(0.0F, -1.5F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -6.0F, -0.18203784098300857F, 0.4553564018453205F, 0.0F));
		var finger_right_inner_1 = finger_right_inner.addOrReplaceChild("finger_right_inner_1", CubeListBuilder.create()
				.texOffs(15, 109).addBox(-1.51F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(1.5F, -1.5F, -4.0F, 0.8196066167365371F, 0.0F, 0.0F));
		finger_right_inner_1.addOrReplaceChild("finger_right_inner_2", CubeListBuilder.create()
				.texOffs(28, 109).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_right_thumb = hand_right.addOrReplaceChild("finger_right_thumb", CubeListBuilder.create()
				.texOffs(0, 116).addBox(-3.0F, -1.5F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(4.0F, 0.0F, -0.5F, -0.18203784098300857F, -1.0471975511965976F, 0.0F));
		var finger_right_thumb_1 = finger_right_thumb.addOrReplaceChild("finger_right_thumb_1", CubeListBuilder.create()
				.texOffs(15, 116).addBox(-1.51F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(-1.5F, -1.5F, -4.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_right_thumb_1.addOrReplaceChild("finger_right_thumb_2", CubeListBuilder.create()
				.texOffs(28, 116).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var shoulder_left = chest_left.addOrReplaceChild("shoulder_left", CubeListBuilder.create()
				.texOffs(57, 27).addBox(-1.0F, -4.5F, -4.5F, 7, 9, 9),
			PartPose.offsetAndRotation(7.0F, -1.5F, 2.0F, 0.0F, 0.091106186954104F, 0.40980330836826856F));
		var arm_left_1 = shoulder_left.addOrReplaceChild("arm_left_1", CubeListBuilder.create()
				.texOffs(57, 46).addBox(0.0F, -4.0F, -4.0F, 6, 8, 8),
			PartPose.offset(6.0F, 0.0F, 0.0F));
		var arm_left_2 = arm_left_1.addOrReplaceChild("arm_left_2", CubeListBuilder.create()
				.texOffs(57, 63).addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F, -0.27314402793711257F));
		var hand_left = arm_left_2.addOrReplaceChild("hand_left", CubeListBuilder.create()
				.texOffs(57, 82).addBox(-4.0F, -1.5F, -8.0F, 8, 3, 8),
			PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var finger_left_outer = hand_left.addOrReplaceChild("finger_left_outer", CubeListBuilder.create()
				.texOffs(57, 102).addBox(-3.0F, -1.5F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(4.0F, 0.0F, -6.0F, -0.18203784098300857F, -0.4553564018453205F, 0.0F));
		var finger_left_outer_1 = finger_left_outer.addOrReplaceChild("finger_left_outer_1", CubeListBuilder.create()
				.texOffs(72, 102).addBox(-1.49F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(-1.5F, -1.5F, -4.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_left_outer_1.addOrReplaceChild("finger_left_outer_2", CubeListBuilder.create()
				.texOffs(85, 102).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_left_mid = hand_left.addOrReplaceChild("finger_left_mid", CubeListBuilder.create()
				.texOffs(57, 94).addBox(-1.5F, -1.5F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -7.0F, -0.18203784098300857F, 0.0F, 0.0F));
		var finger_left_mid_1 = finger_left_mid.addOrReplaceChild("finger_left_mid_1", CubeListBuilder.create()
				.texOffs(74, 94).addBox(-1.49F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.5F, -5.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_left_mid_1.addOrReplaceChild("finger_left_mid_2", CubeListBuilder.create()
				.texOffs(87, 94).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_left_inner = hand_left.addOrReplaceChild("finger_left_inner", CubeListBuilder.create()
				.texOffs(57, 109).addBox(0.0F, -1.5F, -4.0F, 3, 2, 4),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -6.0F, -0.18203784098300857F, 0.4553564018453205F, 0.0F));
		var finger_left_inner_1 = finger_left_inner.addOrReplaceChild("finger_left_inner_1", CubeListBuilder.create()
				.texOffs(72, 109).addBox(-1.49F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(1.5F, -1.5F, -4.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_left_inner_1.addOrReplaceChild("finger_left_inner_2", CubeListBuilder.create()
				.texOffs(85, 109).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var finger_left_thumb = hand_left.addOrReplaceChild("finger_left_thumb", CubeListBuilder.create()
				.texOffs(57, 116).addBox(0.0F, -1.5F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -0.5F, -0.18203784098300857F, 1.0471975511965976F, 0.0F));
		var finger_left_thumb_1 = finger_left_thumb.addOrReplaceChild("finger_left_thumb_1", CubeListBuilder.create()
				.texOffs(72, 116).addBox(-1.49F, 0.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(1.5F, -1.5F, -4.0F, 0.7285004297824331F, 0.0F, 0.0F));
		finger_left_thumb_1.addOrReplaceChild("finger_left_thumb_2", CubeListBuilder.create()
				.texOffs(85, 116).addBox(-2.0F, 0.0F, -2.0F, 4, 2, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.5009094953223726F, 0.0F, 0.0F));


		var neck = base_rotation_bit.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(0, 124).addBox(-4.0F, -4.0F, -7.0F, 8, 7, 8),
			PartPose.offset(0.0F, -1.0F, -1.0F));
		var head_main = neck.addOrReplaceChild("head_main", CubeListBuilder.create()
				.texOffs(0, 140).addBox(-7.0F, -8.0F, -14.0F, 14, 10, 16),
			PartPose.offset(0.0F, -4.0F, -7.0F));
		head_main.addOrReplaceChild("teeth_top", CubeListBuilder.create()
				.texOffs(0, 211).addBox(-5.5F, 1.9F, -8.5F, 11, 2, 8),
			PartPose.offset(0.0F, 0.0F, -5.0F));

		var headress_left1 = head_main.addOrReplaceChild("headress_left1", CubeListBuilder.create()
				.texOffs(61, 140).addBox(6.0F, -7.0F, -2.0F, 7, 7, 4),
			PartPose.offsetAndRotation(0.0F, 1.0F, -9.0F, 0.0F, -0.22759093446006054F, 0.091106186954104F));
		headress_left1.addOrReplaceChild("headress_left2", CubeListBuilder.create()
				.texOffs(61, 152).addBox(0.0F, -9.0F, 0.0F, 13, 9, 4, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, -7.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));
		headress_left1.addOrReplaceChild("headress_left3", CubeListBuilder.create()
				.texOffs(61, 166).addBox(0.0F, 0.0F, -1.99F, 7, 5, 4),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		var headress_right1 = head_main.addOrReplaceChild("headress_right1", CubeListBuilder.create()
				.texOffs(96, 140).addBox(-13.0F, -7.0F, -2.0F, 7, 7, 4),
			PartPose.offsetAndRotation(0.0F, 1.0F, -9.0F, 0.0F, 0.18203784098300857F, -0.091106186954104F));
		headress_right1.addOrReplaceChild("headress_right2", CubeListBuilder.create()
				.texOffs(96, 152).addBox(-13.0F, -9.0F, 0.0F, 13, 9, 4, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0.0F, -7.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));
		headress_right1.addOrReplaceChild("headress_right3", CubeListBuilder.create()
				.texOffs(96, 166).addBox(-7.0F, 0.0F, -1.99F, 7, 5, 4),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		var jaw_back = head_main.addOrReplaceChild("jaw_back", CubeListBuilder.create()
				.texOffs(0, 167).addBox(-6.0F, 0.0F, -3.0F, 12, 6, 6),
			PartPose.offset(0.0F, 1.0F, -2.0F));
		var jaw = jaw_back.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(0, 180).addBox(-5.0F, -1.0F, -11.0F, 10, 4, 12),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.7853981633974483F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("teeth_bottom", CubeListBuilder.create()
				.texOffs(0, 197).addBox(-4.5F, -2.0F, -11.0F, 9, 2, 11),
			PartPose.offset(0.0F, -1.0F, 0.5F));


		var greeble1_1 = belly_2.addOrReplaceChild("greeble1_1", CubeListBuilder.create()
				.texOffs(169, 22).addBox(-2.0F, -4.0F, 0.0F, 4, 5, 4),
			PartPose.offsetAndRotation(4.5F, -3.0F, 8.5F, -0.7740535232594852F, 0.0F, 0.22759093446006054F));
		greeble1_1.addOrReplaceChild("greeble1_2", CubeListBuilder.create()
				.texOffs(169, 32).addBox(-2.01F, -2.0F, 0.0F, 4, 2, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.136659280431156F, 0.0F, 0.0F));

		var greeble2_1 = belly_2.addOrReplaceChild("greeble2_1", CubeListBuilder.create()
				.texOffs(186, 22).addBox(-2.0F, -4.0F, 0.0F, 4, 6, 4),
			PartPose.offsetAndRotation(-4.5F, -3.0F, 8.5F, -0.7740535232594852F, 0.0F, -0.4553564018453205F));
		greeble2_1.addOrReplaceChild("greeble2_2", CubeListBuilder.create()
				.texOffs(186, 33).addBox(-2.01F, -2.0F, 0.0F, 4, 2, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));

		belly_2.addOrReplaceChild("greeble_back", CubeListBuilder.create()
				.texOffs(146, 22).addBox(-3.0F, 0.0F, -4.0F, 6, 9, 4),
			PartPose.offsetAndRotation(0.0F, -3.0F, 11.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var leg_1 = belly_2.addOrReplaceChild("leg_1", CubeListBuilder.create()
				.texOffs(203, 0).addBox(-6.0F, -3.0F, -1.0F, 7, 9, 9),
			PartPose.offsetAndRotation(-6.5F, -0.4F, 2.0F, 0.0F, 0.5235987755982988F, 0.08726646259971647F));
		var leg_2 = leg_1.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(203, 19).addBox(-6.0F, -3.0F, 0.0F, 6, 8, 7),
			PartPose.offsetAndRotation(-5.0F, 0.5F, 0.0F, -0.045553093477052F, -0.045553093477052F, 0.045553093477052F));
		var leg_3 = leg_2.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(203, 35).addBox(-6.0F, -3.0F, -2.0F, 8, 6, 7),
			PartPose.offsetAndRotation(-5.5F, 1.0F, 3.5F, 0.0F, 0.8196066167365371F, 0.0F));
		leg_3.addOrReplaceChild("rope", CubeListBuilder.create()
				.texOffs(146, 241).addBox(-1.5F, -3.5F, -2.5F, 2, 7, 8),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.045553093477052F, 0.0F, -0.091106186954104F));
		var ski_1 = leg_3.addOrReplaceChild("ski_1", CubeListBuilder.create()
				.texOffs(146, 165).addBox(-2.5F, 0.0F, -12.0F, 5, 3, 24),
			PartPose.offsetAndRotation(-1.0F, 1.5F, 0.0F, 0.136659280431156F, -1.3203415791337103F, 0.0F));
		ski_1.addOrReplaceChild("ski_2", CubeListBuilder.create()
				.texOffs(146, 193).addBox(-3.0F, 0.0F, -11.0F, 3, 2, 22),
			PartPose.offsetAndRotation(-2.5F, 1.0F, 0.0F, 0.0F, 0.045553093477052F, -0.045553093477052F));
		ski_1.addOrReplaceChild("ski_3", CubeListBuilder.create()
				.texOffs(146, 218).addBox(0.0F, 0.0F, -10.0F, 3, 2, 20),
			PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.0F, -0.045553093477052F, 0.091106186954104F));

		var cogbeam = belly_2.addOrReplaceChild("cogbeam", CubeListBuilder.create()
				.texOffs(146, 36).addBox(0.0F, -1.5F, -1.5F, 8, 3, 3),
			PartPose.offsetAndRotation(7.0F, 2.5F, 6.0F, 0.0F, 0.0F, -0.136659280431156F));
		var cog2_1 = cogbeam.addOrReplaceChild("cog2_1", CubeListBuilder.create()
				.texOffs(146, 44).addBox(-1.5F, 4.5F, -2.0F, 3, 4, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.18203784098300857F, 0.0F, -0.091106186954104F));
		var cog2_2 = cog2_1.addOrReplaceChild("cog2_2", CubeListBuilder.create()
				.texOffs(146, 53).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-1.49F, 7.5F, -2.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_3 = cog2_2.addOrReplaceChild("cog2_3", CubeListBuilder.create()
				.texOffs(146, 61).addBox(0.0F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_4 = cog2_3.addOrReplaceChild("cog2_4", CubeListBuilder.create()
				.texOffs(146, 70).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_5 = cog2_4.addOrReplaceChild("cog2_5", CubeListBuilder.create()
				.texOffs(146, 78).addBox(0.0F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_6 = cog2_5.addOrReplaceChild("cog2_6", CubeListBuilder.create()
				.texOffs(146, 87).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_7 = cog2_6.addOrReplaceChild("cog2_7", CubeListBuilder.create()
				.texOffs(146, 95).addBox(0.0F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_8 = cog2_7.addOrReplaceChild("cog2_8", CubeListBuilder.create()
				.texOffs(146, 104).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_9 = cog2_8.addOrReplaceChild("cog2_9", CubeListBuilder.create()
				.texOffs(146, 112).addBox(0.0F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_10 = cog2_9.addOrReplaceChild("cog2_10", CubeListBuilder.create()
				.texOffs(146, 121).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_11 = cog2_10.addOrReplaceChild("cog2_11", CubeListBuilder.create()
				.texOffs(146, 129).addBox(0.0F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		cog2_11.addOrReplaceChild("cog2_12", CubeListBuilder.create()
				.texOffs(146, 138).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-0.01F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		cog2_1.addOrReplaceChild("cog2_fill", CubeListBuilder.create()
				.texOffs(146, 146).addBox(-1.0F, -4.5F, -4.5F, 2, 9, 9),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		var cog1_1 = belly_1.addOrReplaceChild("cog1_1", CubeListBuilder.create()
				.texOffs(169, 39).addBox(-2.5F, 6.0F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -0.5F, 3.2F, 0.15707963267948966F, 0.0F, 0.0F));
		var cog1_2 = cog1_1.addOrReplaceChild("cog1_2", CubeListBuilder.create()
				.texOffs(169, 48).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(2.5F, 9.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_3 = cog1_2.addOrReplaceChild("cog1_3", CubeListBuilder.create()
				.texOffs(169, 56).addBox(0.0F, -7.0F, -2.0F, 5, 8, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_4 = cog1_3.addOrReplaceChild("cog1_4", CubeListBuilder.create()
				.texOffs(169, 69).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_5 = cog1_4.addOrReplaceChild("cog1_5", CubeListBuilder.create()
				.texOffs(169, 77).addBox(0.0F, -3.0F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_6 = cog1_5.addOrReplaceChild("cog1_6", CubeListBuilder.create()
				.texOffs(169, 86).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_7 = cog1_6.addOrReplaceChild("cog1_7", CubeListBuilder.create()
				.texOffs(169, 94).addBox(0.0F, -7.0F, -2.0F, 5, 8, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_8 = cog1_7.addOrReplaceChild("cog1_8", CubeListBuilder.create()
				.texOffs(169, 107).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_9 = cog1_8.addOrReplaceChild("cog1_9", CubeListBuilder.create()
				.texOffs(169, 115).addBox(0.0F, -3.0F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_10 = cog1_9.addOrReplaceChild("cog1_10", CubeListBuilder.create()
				.texOffs(169, 124).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		var cog1_11 = cog1_10.addOrReplaceChild("cog1_11", CubeListBuilder.create()
				.texOffs(169, 132).addBox(0.0F, -7.0F, -2.0F, 5, 8, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.5235987755982988F));
		cog1_11.addOrReplaceChild("cog1_12", CubeListBuilder.create()
				.texOffs(169, 145).addBox(0.0F, -3.0F, -2.0F, 5, 3, 4),
			PartPose.offsetAndRotation(5.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.5235987755982988F));


		return LayerDefinition.create(definition, 256, 256);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Barrishee entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(Barrishee entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float animation = limbSwing * 0.1F;
		float animation2 = Mth.sin((limbSwing) * 0.5F) * 0.4F * limbSwingAmount * 2.5F;
		float animation3 = Mth.sin((limbSwing) * 0.5F) * 0.4F * limbSwingAmount * 0.15707963267948966F* 0.5F;
		float standingAngle = entity.getSmoothedStandingAngle(partialTick);
		float flap = Mth.sin((entity.tickCount + partialTick) * 0.6F) * 0.8F;
		float flap2 = Mth.sin((entity.tickCount + partialTick) * 0.3F) * 0.8F;

		this.cog1.zRot = 0F + animation;
		this.cog1.xRot = 0 + animation3;

		if ((entity.standingAngle > 0)) {
			this.root.xRot = this.convertDegtoRad(-65F) + (this.convertDegtoRad(65F) * standingAngle);
			this.neck.xRot = this.convertDegtoRad(60F) - (this.convertDegtoRad(60F) * standingAngle);
			this.head_main.xRot = this.convertDegtoRad(5F) - (this.convertDegtoRad(5F) * standingAngle);
			this.belly_1.xRot = this.convertDegtoRad(30F) - (this.convertDegtoRad(70F) * standingAngle);
		}

		if (entity.isScreaming() && entity.getScreamTimer() >= 20 && entity.getScreamTimer() <= 30) {
			float fudge = entity.getScreamTimer() - 20 + partialTick;

			this.root.xRot = this.convertDegtoRad(0F) - (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.belly_1.xRot = this.convertDegtoRad(-40F) + (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.neck.xRot = this.convertDegtoRad(0F) - (this.convertDegtoRad(45F) * fudge * 0.1F);
			this.head_main.xRot = this.convertDegtoRad(0F) + (this.convertDegtoRad(25F) * fudge * 0.1F);

			this.jaw_back.xRot = this.convertDegtoRad(0F) + (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.jaw.xRot = this.convertDegtoRad(20F) + (this.convertDegtoRad(45F) * fudge * 0.1F);

			this.shoulder_right.xRot = this.convertDegtoRad(0F) + (this.convertDegtoRad(30F) * fudge * 0.1F);
			this.shoulder_right.zRot = this.convertDegtoRad(-25F) + (this.convertDegtoRad(-5F) * fudge * 0.1F);

			this.shoulder_left.xRot = this.convertDegtoRad(0F) + (this.convertDegtoRad(30F) * fudge * 0.1F);
			this.shoulder_left.zRot = this.convertDegtoRad(25F) + (this.convertDegtoRad(5F) * fudge * 0.1F);

			this.hand_right.xRot = convertDegtoRad(25F) - (this.convertDegtoRad(20F) * fudge * 0.1F);
			this.hand_left.xRot = this.convertDegtoRad(25F) - (this.convertDegtoRad(20F) * fudge * 0.1F);
		}

		if (entity.isScreaming() && entity.getScreamTimer() > 30 && entity.getScreamTimer() < 40) {
			this.root.xRot = this.convertDegtoRad(-10F);
			this.belly_1.xRot = this.convertDegtoRad(-30F);
			this.neck.xRot = this.convertDegtoRad(-45F);

			this.jaw_back.xRot = this.convertDegtoRad(10F);
			this.jaw.xRot = this.convertDegtoRad(65F);

			this.shoulder_right.xRot = this.convertDegtoRad(30F);
			this.shoulder_right.zRot = this.convertDegtoRad(-30F);

			this.shoulder_left.xRot = this.convertDegtoRad(30F);
			this.shoulder_left.zRot = this.convertDegtoRad(30F);

			this.hand_right.xRot = this.convertDegtoRad(5F);
			this.hand_left.xRot = this.convertDegtoRad(5F);

			this.head_main.zRot = 0F + animation2 * 0.25F + flap * 0.25F;
		}

		if (entity.isScreaming() && entity.getScreamTimer() >= 40) {
			float fudge = entity.getScreamTimer() - 40 + partialTick;

			this.root.xRot = this.convertDegtoRad(-10F) + (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.belly_1.xRot = this.convertDegtoRad(-30F) - (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.neck.xRot = this.convertDegtoRad(-45F) + (this.convertDegtoRad(45F) * fudge * 0.1F);
			this.head_main.xRot = this.convertDegtoRad(25F) - (this.convertDegtoRad(25F) * fudge * 0.1F);

			this.jaw_back.xRot = this.convertDegtoRad(10F) - (this.convertDegtoRad(10F) * fudge * 0.1F);
			this.jaw.xRot = this.convertDegtoRad(65F) - (this.convertDegtoRad(45F) * fudge * 0.1F);

			this.shoulder_right.xRot = this.convertDegtoRad(30F) - (this.convertDegtoRad(30F) * fudge * 0.1F);
			this.shoulder_right.zRot = this.convertDegtoRad(-30F) + (this.convertDegtoRad(5F) * fudge * 0.1F);

			this.shoulder_left.xRot = convertDegtoRad(30F) - (this.convertDegtoRad(30F) * fudge * 0.1F);
			this.shoulder_left.zRot = convertDegtoRad(30F) - (this.convertDegtoRad(5F) * fudge * 0.1F);

			this.hand_right.xRot = this.convertDegtoRad(5F) + (this.convertDegtoRad(20F) * fudge * 0.1F);
			this.hand_left.xRot = this.convertDegtoRad(5F) + (this.convertDegtoRad(20F) * fudge * 0.1F);
		}

		if (!entity.isAmbushSpawn() && !entity.isScreaming() && !entity.isSlamming() || entity.isAmbushSpawn() && entity.standingAngle == 1 && !entity.isScreaming() && !entity.isSlamming()) {
			this.cogbeam.xRot = 0F + animation;
			this.neck.xRot = 0F - flap2 * 0.0625F;
			this.head_main.xRot = 0F + flap2 * 0.0625F;
			this.head_main.zRot = 0F + animation2 * 0.25F;
			this.jaw_back.xRot = 0F + - flap2 * 0.125F;
			this.jaw.xRot = this.convertDegtoRad(20F) - flap2 * 0.0625F;

			this.shoulder_left.xRot = 0F + animation2 * 0.5F;
			this.shoulder_right.xRot = 0F - animation2 * 0.5F;
			this.arm_left_2.xRot = 0F + animation2;
			this.arm_right_2.xRot = 0F - animation2;

			this.hand_left.xRot = 0.36425021489121656F - this.shoulder_left.xRot - this.arm_left_2.xRot - animation2 * 0.5F;
			this.hand_right.xRot = 0.36425021489121656F - this.shoulder_right.xRot - this.arm_right_2.xRot + animation2 * 0.5F;

			this.finger_left_inner.xRot = -0.17453292519943295F + animation2;
			this.finger_left_inner_1.xRot = 0.7853981633974483F + animation2;
			this.finger_left_inner_2.xRot = -0.5235987755982988F - animation2;

			this.finger_left_mid.xRot = -0.17453292519943295F + animation2;
			this.finger_left_mid_1.xRot = 0.7853981633974483F + animation2;
			this.finger_left_mid_2.xRot = -0.5235987755982988F - animation2;

			this.finger_left_outer.xRot = -0.17453292519943295F + animation2;
			this.finger_left_outer_1.xRot = 0.7853981633974483F + animation2;
			this.finger_left_outer_2.xRot = -0.5235987755982988F - animation2;

			this.finger_right_inner.xRot = -0.17453292519943295F - animation2;
			this.finger_right_inner_1.xRot = 0.7853981633974483F - animation2;
			this.finger_right_inner_2.xRot = -0.5235987755982988F + animation2;

			this.finger_right_mid.xRot = -0.17453292519943295F - animation2;
			this.finger_right_mid_1.xRot = 0.7853981633974483F - animation2;
			this.finger_right_mid_2.xRot = -0.5235987755982988F + animation2;

			this.finger_right_outer.xRot = -0.17453292519943295F - animation2;
			this.finger_right_outer_1.xRot = 0.7853981633974483F - animation2;
			this.finger_right_outer_2.xRot = -0.5235987755982988F + animation2;

			this.root.xRot = 0F - animation2 * 0.125F;
			this.root.zRot = 0F - animation2 * 0.125F;

			this.belly_1.xRot = -0.6457718232379019F + animation2 * 0.125F;
			this.belly_1.zRot = -0.08726646259971647F + animation2 * 0.125F;
		}
	}
}
