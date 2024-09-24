package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.phys.Vec2;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;

public class AnadiaModel extends MowzieModelBase<Anadia> {

	private static final Vec2[] TAIL_ANIMS = new Vec2[]{
		new Vec2(0.5F, 0.5F),
		new Vec2(0.75F, 0.35F),
		new Vec2(1.0F, 0.25F)
	};
	private final ModelPart root;

	public AnadiaModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		makeHead1(partDefinition);
		makeBody1(partDefinition);
		makeTail1(partDefinition);

		makeHead2(partDefinition);
		makeBody2(partDefinition);
		makeTail2(partDefinition);

		makeHead3(partDefinition);
		makeBody3(partDefinition);
		makeTail3(partDefinition);

		return LayerDefinition.create(definition, 64, 64);
	}

	private static void makeHead1(PartDefinition root) {
		var base = root.addOrReplaceChild("set_1_head", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-2.5F, 0.0F, -4.0F, 5, 5, 4),
			PartPose.offsetAndRotation(0.0F, 15.0F, -3.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var head = base.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(22, 10)
				.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var leftBarbel = head.addOrReplaceChild("left_front_barbel", CubeListBuilder.create()
				.texOffs(22, 21)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.5F, 0.0F, -2.0F, 0.0F, -0.22759093446006054F, 0.0F));

		leftBarbel.addOrReplaceChild("left_front_barbel_2", CubeListBuilder.create()
				.texOffs(22, 24)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F, -0.22759093446006054F, 0.0F));

		var rightBarbel = head.addOrReplaceChild("right_front_barbel", CubeListBuilder.create()
				.texOffs(27, 21)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.5F, 0.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));

		rightBarbel.addOrReplaceChild("right_front_barbel_2", CubeListBuilder.create()
				.texOffs(27, 24)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));

		head.addOrReplaceChild("left_back_barbel", CubeListBuilder.create()
				.texOffs(22, 27)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.5F, 0.0F, -2.0F, 0.0F, -0.40980330836826856F, 0.0F));

		head.addOrReplaceChild("right_back_barbel", CubeListBuilder.create()
				.texOffs(27, 27)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.5F, 0.0F, -2.0F, 0.0F, 0.40980330836826856F, 0.0F));

		base.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(22, 17)
				.addBox(-2.0F, -1.0F, -4.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 5.0F, -4.0F, -0.6373942428283291F, 0.0F, 0.0F));
	}

	private static void makeHead2(PartDefinition root) {
		var base = root.addOrReplaceChild("set_2_head", CubeListBuilder.create()
				.texOffs(26, 0)
				.addBox(-2.5F, 0.0F, -5.0F, 5, 5, 5),
			PartPose.offsetAndRotation(0.0F, 15.0F, -3.0F, 0.36425021489121656F, 0.0F, 0.0F));

		base.addOrReplaceChild("left_crest", CubeListBuilder.create()
				.texOffs(26, 33)
				.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2),
			PartPose.offsetAndRotation(2.5F, 0.0F, -1.0F, 0.0F, 0.0F, 0.18203784098300857F));

		base.addOrReplaceChild("right_crest", CubeListBuilder.create()
				.texOffs(33, 33)
				.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2),
			PartPose.offsetAndRotation(-2.5F, 0.0F, -1.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var head = base.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(26, 11)
				.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.5009094953223726F, 0.0F, 0.0F));

		head.addOrReplaceChild("left_barbel", CubeListBuilder.create()
				.texOffs(26, 16)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.5F, 0.0F, -2.0F, 0.0F, -0.18203784098300857F, 0.0F));

		head.addOrReplaceChild("right_barbel", CubeListBuilder.create()
				.texOffs(31, 16)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.5F, 0.0F, -2.0F, 0.0F, 0.18203784098300857F, 0.0F));

		var jaw = base.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(26, 21)
				.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 1),
			PartPose.offsetAndRotation(0.0F, 5.0F, -5.0F, 0.8196066167365371F, 0.0F, 0.0F));

		var leftBarbel = jaw.addOrReplaceChild("left_jaw_barbel", CubeListBuilder.create()
				.texOffs(25, 27)
				.addBox(0.0F, 0.0F, -2.0F, 2, 0.001F, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.6373942428283291F, 0.0F, 0.0F));

		leftBarbel.addOrReplaceChild("left_jaw_barbel_2", CubeListBuilder.create()
				.texOffs(30, 27)
				.addBox(0.0F, 0.0F, -2.0F, 3, 0.001F, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.31869712141416456F, 0.0F, 0.0F));

		var rightBarbel = jaw.addOrReplaceChild("right_jaw_barbel", CubeListBuilder.create()
				.texOffs(25, 30)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 0.001F, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.5462880558742251F, 0.0F, 0.0F));

		rightBarbel.addOrReplaceChild("right_jaw_barbel_2", CubeListBuilder.create()
				.texOffs(30, 30)
				.addBox(-3.0F, 0.0F, -2.0F, 3, 0.001F, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.22759093446006054F, 0.0F, 0.0F));
	}

	private static void makeHead3(PartDefinition root) {
		var base = root.addOrReplaceChild("set_3_head", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-2.5F, 0.0F, -4.0F, 5, 5, 4),
			PartPose.offsetAndRotation(0.0F, 15.0F, -3.0F, 0.36425021489121656F, 0.0F, 0.0F));

		var head = base.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(22, 10)
				.addBox(-2.0F, -5.0F, -3.0F, 4, 5, 4),
			PartPose.offsetAndRotation(0.0F, 5.0F, -4.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var head2 = head.addOrReplaceChild("head_3", CubeListBuilder.create()
				.texOffs(22, 20)
				.addBox(-2.01F, 0.0F, -2.0F, 4, 4, 2),
			PartPose.offsetAndRotation(0.0F, -5.0F, -3.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var leftBarbel = head2.addOrReplaceChild("left_barbel", CubeListBuilder.create()
				.texOffs(22, 26)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 1, 1),
			PartPose.offsetAndRotation(2.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.8651597102135892F));

		leftBarbel.addOrReplaceChild("left_barbel_2", CubeListBuilder.create()
				.texOffs(22, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5462880558742251F));

		var rightBarbel = head2.addOrReplaceChild("right_barbel", CubeListBuilder.create()
				.texOffs(22, 26)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 1, 1),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.8651597102135892F));

		rightBarbel.addOrReplaceChild("right_barbel_2", CubeListBuilder.create()
				.texOffs(22, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.5462880558742251F));

		head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(22, 32)
				.addBox(-2.0F, -1.0F, -4.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.6829473363053812F, 0.0F, 0.0F));
	}

	private static void makeBody1(PartDefinition root) {
		var body = root.addOrReplaceChild("set_1_body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 6),
			PartPose.offset(0.0F, 15.0F, -3.0F));

		body.addOrReplaceChild("connection", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		var back = body.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4),
			PartPose.offset(0.0F, 0.0F, 6.0F));

		back.addOrReplaceChild("left_pelvic_fin", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.0F, 5.0F, 1.0F, 0.31869712141416456F, 0.0F, -0.27314402793711257F));

		back.addOrReplaceChild("right_pelvic_fin", CubeListBuilder.create()
				.texOffs(5, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 5.0F, 1.0F, 0.31869712141416456F, 0.0F, 0.27314402793711257F));

		var leftFin = body.addOrReplaceChild("left_pectoral_fin", CubeListBuilder.create()
				.texOffs(0, 33)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(2.0F, 4.0F, 1.0F, -0.5009094953223726F, 0.6373942428283291F, 0.0F));

		leftFin.addOrReplaceChild("left_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(0, 37)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.4553564018453205F, 0.0F));

		var rightFin = body.addOrReplaceChild("right_pectoral_fin", CubeListBuilder.create()
				.texOffs(7, 33)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(-2.0F, 4.0F, 1.0F, -0.5009094953223726F, -0.6373942428283291F, 0.0F));

		rightFin.addOrReplaceChild("right_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(7, 37)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.4553564018453205F, 0.0F));

		body.addOrReplaceChild("dorsal_fin", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(0.0F, -3.0F, 0.0F, 0.001F, 3, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.136659280431156F, 0.0F, 0.0F));
	}

	private static void makeBody2(PartDefinition root) {
		var body = root.addOrReplaceChild("set_2_body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 8),
			PartPose.offset(0.0F, 15.0F, -3.0F));

		body.addOrReplaceChild("connection", CubeListBuilder.create()
				.texOffs(13, 15)
				.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		var back = body.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 2),
			PartPose.offset(0.0F, 0.0F, 8.0F));

		back.addOrReplaceChild("left_pelvic_fin", CubeListBuilder.create()
				.texOffs(0, 21)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.0F, 5.0F, 0.0F, 0.36425021489121656F, 0.0F, -0.40980330836826856F));

		back.addOrReplaceChild("right_pelvic_fin", CubeListBuilder.create()
				.texOffs(5, 21)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 5.0F, 0.0F, 0.36425021489121656F, 0.0F, 0.40980330836826856F));

		var leftFin = body.addOrReplaceChild("left_pectoral_fin", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.0F, 5.0F, 1.0F, 0.22759093446006054F, -0.136659280431156F, -0.9105382707654417F));

		leftFin.addOrReplaceChild("left_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));

		var rightFin = body.addOrReplaceChild("right_pectoral_fin", CubeListBuilder.create()
				.texOffs(7, 27)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 5.0F, 1.0F, 0.22759093446006054F, 0.136659280431156F, 0.9105382707654417F));

		rightFin.addOrReplaceChild("right_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(7, 29)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.40980330836826856F));

		body.addOrReplaceChild("dorsal_fin", CubeListBuilder.create()
				.texOffs(0, 21)
				.addBox(0.0F, -2.0F, 0.0F, 0.001F, 2, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.136659280431156F, 0.0F, 0.0F));
	}

	private static void makeBody3(PartDefinition root) {
		var body = root.addOrReplaceChild("set_3_body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 2),
			PartPose.offset(0.0F, 15.0F, -3.0F));

		body.addOrReplaceChild("connection", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		var middle = body.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4),
			PartPose.offset(0.0F, 0.0F, 2.0F));

		var back = middle.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 19)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4),
			PartPose.offset(0.0F, 0.0F, 4.0F));

		back.addOrReplaceChild("left_pelvic_fin", CubeListBuilder.create()
				.texOffs(0, 30)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.0F, 4.0F, 0.0F, 0.5009094953223726F, 0.0F, -0.36425021489121656F));

		back.addOrReplaceChild("right_pelvic_fin", CubeListBuilder.create()
				.texOffs(5, 30)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 4.0F, 0.0F, 0.5009094953223726F, 0.0F, 0.36425021489121656F));

		var leftFin = body.addOrReplaceChild("left_pectoral_fin", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(2.0F, 5.0F, 1.0F, 0.4553564018453205F, 0.0F, -0.40980330836826856F));

		leftFin.addOrReplaceChild("left_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(5, 40)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 3, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.36425021489121656F));

		var rightFin = body.addOrReplaceChild("right_pectoral_fin", CubeListBuilder.create()
				.texOffs(0, 44)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 2, 2),
			PartPose.offsetAndRotation(-2.0F, 5.0F, 1.0F, 0.4553564018453205F, 0.0F, 0.40980330836826856F));

		rightFin.addOrReplaceChild("right_pectoral_fin_2", CubeListBuilder.create()
				.texOffs(5, 44)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 3, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		body.addOrReplaceChild("dorsal_fin", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(0.0F, -2.0F, -10.0F, 0.001F, 2, 10),
			PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.091106186954104F, 0.0F, 0.0F));
	}

	private static void makeTail1(PartDefinition root) {
		var tail = root.addOrReplaceChild("set_1_tail", CubeListBuilder.create()
				.texOffs(42, 0)
				.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 15.0F, 7.0F, -0.136659280431156F, 0.0F, 0.0F));

		var back = tail.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(42, 9)
				.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.091106186954104F, 0.0F, 0.0F));

		back.addOrReplaceChild("caudal_fin", CubeListBuilder.create()
				.texOffs(42, 12)
				.addBox(0.0F, -1.0F, 0.0F, 0.001F, 5, 5),
			PartPose.offset(0.0F, 0.0F, 3.0F));

		tail.addOrReplaceChild("anal_fin", CubeListBuilder.create()
				.texOffs(53, 13)
				.addBox(0.0F, -1.0F, 0.0F, 0.001F, 3, 4),
			PartPose.offsetAndRotation(0.0F, 4.0F, 2.0F, -0.22759093446006054F, 0.0F, 0.0F));
	}

	private static void makeTail2(PartDefinition root) {
		var tail = root.addOrReplaceChild("set_2_tail", CubeListBuilder.create()
				.texOffs(48, 0)
				.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, 15.0F, 7.0F, -0.136659280431156F, 0.0F, 0.0F));

		var back = tail.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(48, 8)
				.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));

		back.addOrReplaceChild("caudal_fin", CubeListBuilder.create()
				.texOffs(48, 9)
				.addBox(0.0F, -1.0F, 0.0F, 0.001F, 6, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.091106186954104F, 0.0F, 0.0F));

		tail.addOrReplaceChild("anal_fin", CubeListBuilder.create()
				.texOffs(48, 19)
				.addBox(0.0F, -1.0F, 0.0F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 1.0F, -0.27314402793711257F, 0.0F, 0.0F));
	}

	private static void makeTail3(PartDefinition root) {
		var tail = root.addOrReplaceChild("set_3_tail", CubeListBuilder.create()
				.texOffs(42, 0)
				.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 15.0F, 7.0F, -0.136659280431156F, 0.0F, 0.0F));

		var middle = tail.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(42, 9)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.136659280431156F, 0.0F, 0.0F));

		var back = middle.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(42, 17)
				.addBox(-0.5F, -3.0F, 0.0F, 1, 3, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 4.0F, 0.136659280431156F, 0.0F, 0.0F));

		back.addOrReplaceChild("caudal_fin", CubeListBuilder.create()
				.texOffs(42, 17)
				.addBox(0.0F, -4.0F, 0.0F, 0.001F, 5, 7),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));

		tail.addOrReplaceChild("anal_fin", CubeListBuilder.create()
				.texOffs(0, 30)
				.addBox(0.0F, 0.0F, 0.0F, 0.001F, 3, 5),
			PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, -0.136659280431156F, 0.0F, 0.0F));
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		//render default fishe
		this.getPartForType(AnadiaParts.AnadiaHeadParts.HEAD_1, "head").render(stack, consumer, light, overlay, color);
		this.getPartForType(AnadiaParts.AnadiaBodyParts.BODY_1, "body").render(stack, consumer, light, overlay, color);
		this.getPartForType(AnadiaParts.AnadiaTailParts.TAIL_1, "tail").render(stack, consumer, light, overlay, color);
	}

	public void renderPart(Enum<?> partType, String name, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.getPartForType(partType, name).render(stack, consumer, light, overlay, color);
	}

	@Override
	public void prepareMobModel(Anadia entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;

//		if (entity.isNoAi()) {
//			limbSwingAmount = 0;
//			frame = 0;
//		}

		this.walk(this.getPartForType(entity.getHeadType(), "head", "jaw"), (1.5F - entity.getFishSize()) * 0.25F, 0.35F, false, 0.0F, 0F, frame, 1F - limbSwingAmount);

		Vec2 tailAnims = TAIL_ANIMS[entity.getTailType().ordinal()];
		this.swing(this.getPartForType(entity.getTailType(), "tail"), tailAnims.x, tailAnims.y, false, 0.0F, 0F, frame, 0.0625F + limbSwingAmount);
		this.swing(this.getPartForType(entity.getTailType(), "tail", "back"), tailAnims.x, tailAnims.y, false, 1.0F, 0F, frame, 0.0625F + limbSwingAmount);
		this.swing(this.getPartForType(entity.getTailType(), "tail", "back", "caudal_fin"), tailAnims.x, tailAnims.y, false, 2.0F, 0F, frame, 0.0625F + limbSwingAmount);

		float bodySpeed = entity.getBodyType().ordinal() == 0 ? 0.25F : 0.5F;
		this.swing(this.getPartForType(entity.getBodyType(), "body", "back", "left_pelvic_fin"), bodySpeed, 0.5F, false, 2.0F, 0F, frame, 0.125F + limbSwingAmount);
		this.swing(this.getPartForType(entity.getBodyType(), "body", "back", "right_pelvic_fin"), bodySpeed, 0.5F, true, 2.0F, 0F, frame, 0.125F + limbSwingAmount);

		this.swing(this.getPartForType(entity.getBodyType(), "body", "left_pectoral_fin"), bodySpeed, 0.5F, true, 1.0F, 0F, frame, 0.125F + limbSwingAmount);
		this.swing(this.getPartForType(entity.getBodyType(), "body", "left_pectoral_fin", "left_pectoral_fin_2"), bodySpeed, 0.5F, true, 2.0F, 0F, frame, 0.125F + limbSwingAmount);

		this.swing(this.getPartForType(entity.getBodyType(), "body", "right_pectoral_fin"), bodySpeed, 0.5F, false, 1.0F, 0F, frame, 0.125F + limbSwingAmount);
		this.swing(this.getPartForType(entity.getBodyType(), "body", "right_pectoral_fin", "right_pectoral_fin_2"), bodySpeed, 0.5F, false, 2.0F, 0F, frame, 0.125F + limbSwingAmount);

		this.walk(this.getPartForType(entity.getBodyType(), "body", "left_pectoral_fin"), bodySpeed, 0.5F, false, 0.0F, 0F, frame, 0.125F + limbSwingAmount);
		this.walk(this.getPartForType(entity.getBodyType(), "body", "right_pectoral_fin"), bodySpeed, 0.5F, false, 0.0F, 0F, frame, 0.125F + limbSwingAmount);

		this.flap(this.getPartForType(entity.getBodyType(), "body", "left_pectoral_fin"), bodySpeed, 0.5F, false, 0.0F, 0F, frame, 0.125F + limbSwingAmount);
		this.flap(this.getPartForType(entity.getBodyType(), "body", "right_pectoral_fin"), bodySpeed, 0.5F, true, 0.0F, 0F, frame, 0.125F + limbSwingAmount);
	}

	@Override
	public void setupAnim(Anadia entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	private ModelPart getPartForType(Enum<?> en, String partName, String... childParts) {
		ModelPart part = this.root.getChild("set_" + (en.ordinal() + 1) + "_" + partName);
		for (String childName : childParts) {
			if (childName.equals("jaw") && en.ordinal() == 2) { //pain
				part = part.getChild("head_2").getChild(childName);
			} else if (childName.equals("back") && en.ordinal() == 2) { //even more pain
				part = part.getChild("middle").getChild(childName);
			} else {
				part = part.getChild(childName);
			}
		}
		return part;
	}
}
