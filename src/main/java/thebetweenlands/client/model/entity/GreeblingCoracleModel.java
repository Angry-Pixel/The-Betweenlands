package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.GreeblingCoracle;

public class GreeblingCoracleModel extends MowzieModelBase<GreeblingCoracle> {

	private final ModelPart root;
	private final ModelPart coracle_base;
	private final ModelPart body_base;
	private final ModelPart chest;
	private final ModelPart head_connect;
	private final ModelPart jaw;
	private final ModelPart ear_left;
	private final ModelPart ear_right;
	private final ModelPart cloth1a;
	private final ModelPart cloth1b;
	private final ModelPart arm_left1a;
	private final ModelPart arm_left1b;
	private final ModelPart arm_right1a;
	private final ModelPart arm_right1b;
	private final ModelPart leg_left1a;
	private final ModelPart leg_right1a;
	private final ModelPart paddle_main;
	private final ModelPart side_rope1;
	private final ModelPart hook_front;
	private final ModelPart hook_left;
	private final ModelPart netrope1a;
	private final ModelPart net_weightline;
	private final ModelPart[] netting;

	public GreeblingCoracleModel(ModelPart root) {
		this.root = root;
		this.coracle_base = root.getChild("coracle_base");
		this.body_base = this.coracle_base.getChild("body_base");
		this.chest = this.body_base.getChild("chest");
		this.head_connect = this.chest.getChild("head_connect");
		this.jaw = this.head_connect.getChild("jaw");
		this.ear_left = this.head_connect.getChild("head_main").getChild("left_ear");
		this.ear_right = this.head_connect.getChild("head_main").getChild("right_ear");
		this.cloth1a = this.head_connect.getChild("head_main").getChild("cloth_1");
		this.cloth1b = this.cloth1a.getChild("cloth_2");
		this.arm_left1a = this.chest.getChild("left_arm_1");
		this.arm_left1b = this.arm_left1a.getChild("left_arm_2");
		this.arm_right1a = this.chest.getChild("right_arm_1");
		this.arm_right1b = this.arm_right1a.getChild("right_arm_2");
		this.leg_left1a = this.body_base.getChild("left_leg_1");
		this.leg_right1a = this.body_base.getChild("right_leg_1");
		this.paddle_main = this.arm_left1b.getChild("paddle_handle");
		this.side_rope1 = this.coracle_base.getChild("side_left").getChild("top_left").getChild("side_rope_1");
		this.hook_front = this.coracle_base.getChild("left_front_corner_1").getChild("front_left_rope").getChild("hook_front");
		this.hook_left = this.coracle_base.getChild("left_back_corner_1").getChild("back_rope_1").getChild("hook_left");
		this.netrope1a = this.coracle_base.getChild("net_rope_1");
		var netrope1b = this.netrope1a.getChild("net_rope_2");
		var netting1 = netrope1b.getChild("net_ring").getChild("net_netting_1");
		var netting2 = netting1.getChild("net_netting_2");
		var netting3 = netting2.getChild("net_netting_3");
		this.net_weightline = netrope1b.getChild("net_ring").getChild("net_weightline");
		this.netting = new ModelPart[]{this.netrope1a, netrope1b, netting1, netting2, netting3};
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var coracle_base = partDefinition.addOrReplaceChild("coracle_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-6.0F, 0.0F, -6.0F, 12, 1, 12),
			PartPose.offsetAndRotation(0.0F, 23.5F, 0.0F, 0.0F, 0.7853981633974483F, 0.0F));

		var side_front = coracle_base.addOrReplaceChild("side_front", CubeListBuilder.create()
				.texOffs(54, 14).addBox(-6.0F, -3.0F, -1.0F, 12, 3, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.18203784098300857F, 0.0F, 0.0F));
		side_front.addOrReplaceChild("top_front", CubeListBuilder.create()
				.texOffs(54, 19).addBox(-7.0F, -2.0F, -1.0F, 14, 2, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));
		var side_left = coracle_base.addOrReplaceChild("side_left", CubeListBuilder.create()
				.texOffs(0, 14).addBox(0.0F, -3.0F, -6.0F, 1, 3, 12),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));
		var top_left = side_left.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(0, 30).addBox(0.0F, -2.0F, -7.0F, 1, 2, 14),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));
		var side_back = coracle_base.addOrReplaceChild("side_back", CubeListBuilder.create()
				.texOffs(54, 23).addBox(-6.0F, -3.0F, 0.0F, 12, 3, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, -0.18203784098300857F, 0.0F, 0.0F));
		var top_back = side_back.addOrReplaceChild("top_back", CubeListBuilder.create()
				.texOffs(54, 28).addBox(-7.0F, -2.0F, 0.0F, 14, 2, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));
		var side_right = coracle_base.addOrReplaceChild("side_right", CubeListBuilder.create()
				.texOffs(27, 14).addBox(-1.0F, -3.0F, -6.0F, 1, 3, 12),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));
		side_right.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(31, 30).addBox(-1.0F, -2.0F, -7.0F, 1, 2, 14),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		var corner_front_left1a = coracle_base.addOrReplaceChild("left_front_corner_1", CubeListBuilder.create()
				.texOffs(0, 52).addBox(-1.5F, -3.0F, -0.5F, 2, 4, 2),
			PartPose.offsetAndRotation(6.0F, 0.0F, -6.0F, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F));
		corner_front_left1a.addOrReplaceChild("left_front_corner_2", CubeListBuilder.create()
				.texOffs(0, 59).addBox(0.0F, -3.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-1.5F, -3.0F, 1.5F, 0.27314402793711257F, -0.027401669256310976F, 0.27314402793711257F));
		var corner_front_right1a = coracle_base.addOrReplaceChild("right_front_corner_1", CubeListBuilder.create()
				.texOffs(9, 52).addBox(-0.5F, -3.0F, -0.5F, 2, 4, 2),
			PartPose.offsetAndRotation(-6.0F, 0.0F, -6.0F, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F));
		corner_front_right1a.addOrReplaceChild("right_front_corner_2", CubeListBuilder.create()
				.texOffs(9, 59).addBox(-2.0F, -3.0F, -2.0F, 2, 3, 2),
			PartPose.offsetAndRotation(1.5F, -3.0F, 1.5F, 0.27314402793711257F, 0.027401669256310976F, -0.27314402793711257F));
		var corner_back_left1a = coracle_base.addOrReplaceChild("left_back_corner_1", CubeListBuilder.create()
				.texOffs(18, 52).addBox(-1.5F, -3.0F, -1.5F, 2, 4, 2),
			PartPose.offsetAndRotation(6.0F, 0.0F, 6.0F, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F));
		corner_back_left1a.addOrReplaceChild("left_back_corner_2", CubeListBuilder.create()
				.texOffs(18, 59).addBox(0.0F, -3.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-1.5F, -3.0F, -1.5F, -0.27314402793711257F, 0.027401669256310976F, 0.27314402793711257F));
		var corner_back_right1a = coracle_base.addOrReplaceChild("right_back_corner_1", CubeListBuilder.create()
				.texOffs(27, 52).addBox(-0.5F, -3.0F, -1.5F, 2, 4, 2),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 6.0F, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F));
		corner_back_right1a.addOrReplaceChild("right_back_corner_2", CubeListBuilder.create()
				.texOffs(27, 59).addBox(-2.0F, -3.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(1.5F, -3.0F, -1.5F, -0.27314402793711257F, -0.027401669256310976F, -0.27314402793711257F));

		corner_front_right1a.addOrReplaceChild("front_right_rope", CubeListBuilder.create()
				.texOffs(13, 65).addBox(-1.0F, -1.0F, -1.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, 0.136659280431156F, 0.01361356816555577F, -0.136659280431156F));
		var rope_fl1 = corner_front_left1a.addOrReplaceChild("front_left_rope", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-2.0F, -1.0F, -1.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, 0.136659280431156F, -0.01361356816555577F, 0.136659280431156F));
		var rope_br1 = corner_back_left1a.addOrReplaceChild("back_rope_1", CubeListBuilder.create()
				.texOffs(26, 65).addBox(-2.0F, -1.0F, -2.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -0.136659280431156F, 0.01361356816555577F, 0.136659280431156F));
		corner_back_right1a.addOrReplaceChild("back_rope_2", CubeListBuilder.create()
				.texOffs(39, 65).addBox(-1.0F, -1.0F, -2.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -0.136659280431156F, -0.01361356816555577F, -0.136659280431156F));


		var bench = coracle_base.addOrReplaceChild("bench", CubeListBuilder.create()
				.texOffs(0, 75).addBox(-5.5F, -1.0F, 0.0F, 11, 1, 3),
			PartPose.offsetAndRotation(-0.5F, -2.5F, 0.5F, 0.0F, -0.7853981633974483F, 0.0F));
		bench.addOrReplaceChild("bench_conn_left", CubeListBuilder.create()
				.texOffs(29, 75).addBox(0.0F, -1.0F, 0.0F, 2, 1, 1),
			PartPose.offset(5.4F, 0.0F, 0.0F));
		bench.addOrReplaceChild("bench_conn_right", CubeListBuilder.create()
				.texOffs(29, 78).addBox(-2.0F, -1.0F, 0.0F, 2, 1, 1),
			PartPose.offset(-5.5F, 0.0F, 0.0F));

		var gourdbag_base = coracle_base.addOrReplaceChild("gourdbag_base", CubeListBuilder.create()
				.texOffs(0, 80).addBox(0.0F, -3.0F, 0.0F, 3, 3, 3),
			PartPose.offsetAndRotation(3.0F, 0.0F, -0.5F, -0.18203784098300857F, -0.5462880558742251F, 0.18203784098300857F));
		var gourdbag_mid = gourdbag_base.addOrReplaceChild("gourdbag_mid", CubeListBuilder.create()
				.texOffs(0, 87).addBox(-0.01F, -3.0F, 0.0F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));
		gourdbag_mid.addOrReplaceChild("gourdbag_top", CubeListBuilder.create()
				.texOffs(0, 94).addBox(0.0F, -1.0F, 0.0F, 2, 1, 2),
			PartPose.offsetAndRotation(0.5F, -3.0F, 0.5F, -0.091106186954104F, 0.0F, 0.0F));
		top_left.addOrReplaceChild("side_rope_1", CubeListBuilder.create()
				.texOffs(0, 37).addBox(0.0F, 0.0F, -5.0F, 0, 4, 10),
			PartPose.offsetAndRotation(1.0F, -1.5F, 0.0F, 0.0F, 0.0F, -0.4553564018453205F));
		top_back.addOrReplaceChild("side_rope_2", CubeListBuilder.create()
				.texOffs(54, 32).addBox(-5.0F, 0.0F, 0.0F, 10, 4, 0),
			PartPose.offsetAndRotation(0.0F, -1.5F, 1.0F, 0.4553564018453205F, 0.0F, 0.0F));

		var netrope1a = coracle_base.addOrReplaceChild("net_rope_1", CubeListBuilder.create()
				.texOffs(0, 98).addBox(-0.5F, -1.0F, 0.0F, 1, 6, 0),
			PartPose.offsetAndRotation(-6.5F, -2.0F, 6.5F, 0.5918411493512771F, -0.7740535232594852F, 0.0F));
		var netrope1b = netrope1a.addOrReplaceChild("net_rope_2", CubeListBuilder.create()
				.texOffs(3, 98).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 0),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.31869712141416456F, 0.0F, 0.0F));
		var net_ring = netrope1b.addOrReplaceChild("net_ring", CubeListBuilder.create()
				.texOffs(6, 98).addBox(-2.5F, -0.5F, -5.0F, 5, 1, 5),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));
		var net_netting1 = net_ring.addOrReplaceChild("net_netting_1", CubeListBuilder.create()
				.texOffs(27, 98).addBox(-2.0F, 0.0F, -4.0F, 4, 5, 4),
			PartPose.offsetAndRotation(0.0F, 0.5F, -0.5F, -0.18203784098300857F, 0.0F, 0.0F));
		var net_netting2 = net_netting1.addOrReplaceChild("net_netting_2", CubeListBuilder.create()
				.texOffs(44, 98).addBox(-1.5F, -0.5F, -3.0F, 3, 5, 3),
			PartPose.offsetAndRotation(0.0F, 5.0F, -0.5F, -0.045553093477052F, 0.091106186954104F, 0.0F));
		net_netting2.addOrReplaceChild("net_netting_3", CubeListBuilder.create()
				.texOffs(57, 98).addBox(-1.0F, -0.5F, -2.0F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, -0.5F, 0.045553093477052F, 0.136659280431156F, 0.0F));
		var net_weightline = net_ring.addOrReplaceChild("net_weightline", CubeListBuilder.create()
				.texOffs(60, 98).addBox(-0.5F, 0.0F, -6.0F, 1, 0, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, -0.091106186954104F, 0.0F, 0.0F));
		net_weightline.addOrReplaceChild("net_weight", CubeListBuilder.create()
				.texOffs(69, 98).addBox(-0.5F, -1.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.091106186954104F, 0.0F, 0.0F));

		rope_fl1.addOrReplaceChild("hook_front", CubeListBuilder.create()
				.texOffs(0, 68).addBox(0.0F, 0.0F, -2.0F, 0, 3, 3),
			PartPose.offsetAndRotation(0.5F, 0.0F, -1.0F, -0.27314402793711257F, 0.9560913642424937F, -0.6373942428283291F));
		rope_br1.addOrReplaceChild("hook_left", CubeListBuilder.create()
				.texOffs(7, 68).addBox(0.0F, 0.0F, -2.0F, 0, 3, 3),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.5F, 0.31869712141416456F, -0.31869712141416456F, -0.31869712141416456F));

		var body_base = coracle_base.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(90, 0).addBox(-2.0F, -3.5F, -2.0F, 4, 4, 3),
			PartPose.offsetAndRotation(-3.3F, -4.2F, -0.8F, -0.18203784098300857F, -Mth.PI / 4.0f, -0.091106186954104F));
		var chest = body_base.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(90, 8).addBox(-2.5F, -3.0F, -2.5F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.136659280431156F, 0.0F, 0.091106186954104F));

		var head_connect = chest.addOrReplaceChild("head_connect", CubeListBuilder.create()
				.texOffs(90, 17).addBox(-2.0F, -1.0F, -1.5F, 4, 1, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));
		var head_main = head_connect.addOrReplaceChild("head_main", CubeListBuilder.create()
				.texOffs(90, 21).addBox(-2.0F, -3.0F, -3.5F, 4, 3, 4),
			PartPose.offset(0.0F, -1.0F, 0.0F));
		head_main.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(90, 29).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, -0.22759093446006054F, 0.0F, 0.0F));
		head_connect.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(107, 21).addBox(-1.5F, 0.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, -1.0F, -1.5F, 0.22759093446006054F, 0.0F, 0.0F));
		head_main.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(95, 29).addBox(-1.0F, -0.5F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(1.0F, -2.5F, -1.5F, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F));
		head_main.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(106, 29).addBox(-4.0F, -0.5F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(-1.0F, -2.5F, -1.5F, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F));
		var cloth1a = head_main.addOrReplaceChild("cloth_1", CubeListBuilder.create()
				.texOffs(90, 33).addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.5F, 0.5462880558742251F, 0.0F, 0.0F));
		cloth1a.addOrReplaceChild("cloth_2", CubeListBuilder.create()
				.texOffs(90, 36).addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var arm_left1a = chest.addOrReplaceChild("left_arm_1", CubeListBuilder.create()
				.texOffs(90, 39).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(2.5F, -2.0F, -1.0F, -1.4114477660878142F, 0.0F, -0.40980330836826856F));
		var arm_left1b = arm_left1a.addOrReplaceChild("left_arm_2", CubeListBuilder.create()
				.texOffs(90, 45).addBox(-0.49F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.5F, -1.0471975511965976F, 0.0F, 0.0F));

		var arm_right1a = chest.addOrReplaceChild("right_arm_1", CubeListBuilder.create()
				.texOffs(95, 39).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-2.5F, -2.0F, -1.0F, -0.6829473363053812F, -0.18203784098300857F, 0.40980330836826856F));
		arm_right1a.addOrReplaceChild("right_arm_2", CubeListBuilder.create()
				.texOffs(95, 45).addBox(-0.51F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 3.5F, 0.5F, -0.6829473363053812F, 0.0F, 0.0F));

		var leg_left1a = body_base.addOrReplaceChild("left_leg_1", CubeListBuilder.create()
				.texOffs(90, 51).addBox(-0.5F, -0.5F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(1.3F, 0.0F, -1.0F, -1.1383037381507017F, -0.36425021489121656F, 0.045553093477052F));
		leg_left1a.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(90, 56).addBox(-0.49F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.1838568316277536F, 0.0F, 0.0F));

		var leg_right1a = body_base.addOrReplaceChild("right_leg_1", CubeListBuilder.create()
				.texOffs(95, 51).addBox(-0.5F, -0.5F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.3F, 0.0F, -1.0F, -1.2747884856566583F, 0.5462880558742251F, 0.0F));
		leg_right1a.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(95, 56).addBox(-0.51F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 1.4114477660878142F, 0.0F, 0.0F));

		var paddle_handle = arm_left1b.addOrReplaceChild("paddle_handle", CubeListBuilder.create()
				.texOffs(118, 3).addBox(-0.5F, 0.0F, -0.5F, 1, 14, 1),
			PartPose.offsetAndRotation(-0.5F, 4.0F, 0.0F, 1.8212510744560826F, -1.2292353921796064F, 0.18203784098300857F));
		paddle_handle.addOrReplaceChild("paddle_knob", CubeListBuilder.create()
				.texOffs(118, 0).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, -0.36425021489121656F, 0.0F, 0.0F));
		var paddle_blade1 = paddle_handle.addOrReplaceChild("paddle_blade_1", CubeListBuilder.create()
				.texOffs(118, 19).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1),
			PartPose.offsetAndRotation(0.0F, 14.0F, -0.5F, 0.091106186954104F, 0.0F, 0.0F));
		paddle_blade1.addOrReplaceChild("paddle_blade_2", CubeListBuilder.create()
				.texOffs(118, 24).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(GreeblingCoracle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.faceTarget(this.head_connect, 1.0F, netHeadYaw, headPitch);
	}

	@Override
	public void prepareMobModel(GreeblingCoracle entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float frame = entity.tickCount + partialTick;

		this.body_base.visible = entity.getSinkingTicks() <= 10;

		if (entity.isGreeblingAboveWater()) {
			// Boat idle
			this.bob(this.coracle_base, 0.1f, 0.2f, false, frame, 1f);
			this.walk(this.coracle_base, 0.06f, 0.05f, false, 0, 0, frame, 1f);
			this.swing(this.coracle_base, 0.08f, 0.05f, false, 0.3f, 0, frame, 1f);
			this.flap(this.coracle_base, 0.04f, 0.05f, false, 1.2f, 0, frame, 1f);
			this.walk(this.netrope1a, 0.06f, 0.05f, true, 0, -0.025f, frame, 1f);
			this.swing(this.netrope1a, 0.08f, 0.05f, true, 0.3f, 0, frame, 1f);
			this.flap(this.netrope1a, 0.04f, 0.05f, true, 1.2f, 0, frame, 1f);
			this.walk(this.body_base, 0.06f, 0.05f, true, 0, 0, frame, 1f);
			this.swing(this.body_base, 0.08f, 0.05f, true, 0.3f, 0, frame, 1f);
			this.flap(this.body_base, 0.04f, 0.05f, true, 1.2f, 0, frame, 1f);

			float offsetAmount = 1.2f;
			for (int i = 0; i < this.netting.length; i++) {
				this.walk(this.netting[i], 0.08f, 0.05f, false, 2 - offsetAmount * i, 0, frame, 1f);
				this.swing(this.netting[i], 0.06f, 0.05f, false, 1.8f - offsetAmount * i, 0, frame, 1f);
				this.flap(this.netting[i], 0.05f, 0.05f, false, 1.3f - offsetAmount * i, 0, frame, 1f);
			}
			this.walk(this.net_weightline, 0.08f, 0.15f, false, 2 - offsetAmount * 2, 0, frame, 1f);
			this.swing(this.net_weightline, 0.06f, 0.15f, false, 1.8f - offsetAmount * 2, 0, frame, 1f);
			this.flap(this.net_weightline, 0.05f, 0.15f, false, 1.3f - offsetAmount * 2, 0, frame, 1f);

			this.walk(this.hook_front, 0.16f, 0.1f + (float) Math.sin(frame * 0.04f) * 0.1f, false, 0, 0, frame, 1f);
			this.walk(this.hook_left, 0.16f, 0.1f + (float) Math.sin(frame * 0.04f + 0.6) * 0.1f, false, 1.7f, 0, frame, 1f);
			this.flap(this.side_rope1, 0.08f, 0.15f, false, 2.3f, 0, frame, 1f);
			// Greebling idle
			this.walk(this.chest, 0.1f, 0.05f, false, 1, 0, frame, 1f);
			this.walk(this.head_connect, 0.1f, 0.05f, true, 1, 0, frame, 1f);
			this.walk(this.jaw, 0.1f, 0.05f, true, 1, -0.2f, frame, 1f);
			this.walk(this.cloth1a, 0.1f, 0.05f, false, 1.5f, 0.025f, frame, 1f);
			this.walk(this.cloth1b, 0.1f, 0.05f, false, 1f, 0.025f, frame, 1f);

			// Paddling
			float globalDegree = 0.6f;
			float globalSpeed = 1.4f;
			this.walk(this.body_base, 0.15f * globalSpeed, 0.3f * globalDegree, false, 0, 0.9f, limbSwing, limbSwingAmount);
			this.walk(this.leg_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.leg_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.arm_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.arm_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, 0, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.head_connect, 0.15f * globalSpeed, 0.25f * globalDegree, true, 0, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.chest, 0.15f * globalSpeed, 0.3f * globalDegree, false, -1f, 0, limbSwing, limbSwingAmount);
			this.walk(this.arm_right1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, -1f, 0, limbSwing, limbSwingAmount);
			this.walk(this.arm_left1a, 0.15f * globalSpeed, 0.3f * globalDegree, true, -1f, 0, limbSwing, limbSwingAmount);
			this.walk(this.head_connect, 0.15f * globalSpeed, 0.25f * globalDegree, true, -1f, 0, limbSwing, limbSwingAmount);

			this.walk(this.arm_left1a, 0.15f * globalSpeed, 0.8f * globalDegree, false, 2.5f, 0.4f, limbSwing, limbSwingAmount);
			this.walk(this.arm_left1b, 0.15f * globalSpeed, 0.8f * globalDegree, true, 2.5f - 0.8f, 0.2f, limbSwing, limbSwingAmount);

			float paddleDelay = -0.4f;
			this.flap(this.paddle_main, 0.15f * globalSpeed, 0.45f * globalDegree, false, 2.5f - 0.8f + paddleDelay, -0.1f, limbSwing, limbSwingAmount);
			this.flap(this.arm_left1a, 0.15f * globalSpeed, 0.4f * globalDegree, false, 2.5f - 0.8f + paddleDelay, 0.3f, limbSwing, limbSwingAmount);
			this.flap(this.chest, 0.15f * globalSpeed, 0.4f * globalDegree, false, 2.5f - 1.3f + paddleDelay, 0f, limbSwing, limbSwingAmount);
			this.flap(this.head_connect, 0.15f * globalSpeed, 0.4f * globalDegree, true, 2.5f - 1.3f + paddleDelay, 0f, limbSwing, limbSwingAmount);
			this.swing(this.paddle_main, 0.15f * globalSpeed, 0.2f * globalDegree, true, -0.5f + paddleDelay, -0.2f, limbSwing, limbSwingAmount);

			swing(this.chest, 0.15f * globalSpeed, 0.6f * globalDegree, false, 2.5f + paddleDelay, -0.2f, limbSwing, limbSwingAmount);
			swing(this.head_connect, 0.15f * globalSpeed, 0.6f * globalDegree, true, 2.5f + paddleDelay, -0.2f, limbSwing, limbSwingAmount);

			this.flap(this.body_base, 0.15f * globalSpeed, 0.1f * globalDegree, false, 2.5f + paddleDelay, -0.7f, limbSwing, limbSwingAmount);
			this.flap(this.chest, 0.15f * globalSpeed, 0.1f * globalDegree, false, 2.5f + paddleDelay, 0.4f, limbSwing, limbSwingAmount);
			this.flap(this.leg_left1a, 0.15f * globalSpeed, 0.1f * globalDegree, true, 2.5f + paddleDelay, 0.4f, limbSwing, limbSwingAmount);
			this.flap(this.leg_right1a, 0.15f * globalSpeed, 0.1f * globalDegree, true, 2.5f + paddleDelay, 0.4f, limbSwing, limbSwingAmount);

			this.walk(this.arm_right1a, 0.15f * globalSpeed, 0.7f * globalDegree, false, 2.5f + paddleDelay, 0.25f, limbSwing, limbSwingAmount);
			this.walk(this.arm_right1b, 0.15f * globalSpeed, globalDegree, true, 2.5f - 0.9f + paddleDelay, -0.2f, limbSwing, limbSwingAmount);
			this.walk(this.arm_right1b, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f + paddleDelay, 0f, limbSwing, limbSwingAmount);
			this.swing(this.arm_right1a, 0.15f * globalSpeed, 0.5f * globalDegree, true, 2.5f + paddleDelay, 0.4f, limbSwing, limbSwingAmount);
			this.flap(this.arm_right1a, 0.15f * globalSpeed, 0.6f * globalDegree, false, 2.5f - 0.8f + paddleDelay, 0f, limbSwing, limbSwingAmount);
			this.flap(this.arm_right1b, 0.15f * globalSpeed, 0.4f * globalDegree, true, 2.5f + paddleDelay, -0.2f, limbSwing, limbSwingAmount);
			this.arm_right1a.z += (float) Math.cos((limbSwing + 2.5f + 0.8f + paddleDelay) * 0.15 * globalSpeed) * globalDegree * limbSwingAmount + 0.2f;

			this.walk(this.cloth1a, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f + paddleDelay, 0.3f, limbSwing, limbSwingAmount);
			this.walk(this.cloth1b, 0.15f * globalSpeed, 0.3f * globalDegree, false, 2.5f - 1f + paddleDelay, 0.25f, limbSwing, limbSwingAmount);

			this.flap(this.ear_left, 0.3f * globalSpeed, 0.1f * globalDegree, false, 2.5f - 1f + paddleDelay, 0f, limbSwing, limbSwingAmount);
			this.flap(this.ear_right, 0.3f * globalSpeed, 0.1f * globalDegree, true, 2.5f - 1f + paddleDelay, 0f, limbSwing, limbSwingAmount);

			this.body_base.z -= 1 * limbSwingAmount;
		}
	}
}
