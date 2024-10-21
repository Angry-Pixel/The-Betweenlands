package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class DraetonModel extends Model {

	private final ModelPart root;

	public DraetonModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition createCarriage() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var keel_mid = partDefinition.addOrReplaceChild("keel_mid", CubeListBuilder.create()
				.texOffs(180, 0).addBox(-2.0F, -4.0F, -14.0F, 4, 2, 28),
			PartPose.offset(0.0F, 28.0F, 0.0F));

		var keel_mid_back = keel_mid.addOrReplaceChild("keel_mid_back", CubeListBuilder.create()
				.texOffs(128, 5).addBox(-2.01F, -4.0F, 0.0F, 4, 4, 6),
			PartPose.offsetAndRotation(0.0F, -2.0F, 14.0F, 0.40980330836826856F, 0.0F, 0.0F));
		var keel_bow_main = keel_mid_back.addOrReplaceChild("keel_bow_main", CubeListBuilder.create()
				.texOffs(172, 6).addBox(-2.0F, -16.0F, -1.0F, 4, 16, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.36425021489121656F, 0.0F, 0.0F));
		var keel_bow_detail1 = keel_bow_main.addOrReplaceChild("keel_bow_detail1", CubeListBuilder.create()
				.texOffs(64, 5).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 2),
			PartPose.offset(0.0F, -14.0F, 2.0F));
		keel_bow_detail1.addOrReplaceChild("keel_bow_detail1b", CubeListBuilder.create()
				.texOffs(226, 10).addBox(-2.01F, 0.0F, 0.0F, 4, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.136659280431156F, 0.0F, 0.0F));
		var keel_bow_detail2 = keel_bow_main.addOrReplaceChild("keel_bow_detail2", CubeListBuilder.create()
				.texOffs(0, 8).addBox(-2.0F, -4.0F, -1.0F, 4, 4, 4),
			PartPose.offset(0.0F, -16.0F, 0.0F));
		var keel_bow_detail3 = keel_bow_detail2.addOrReplaceChild("keel_bow_detail3", CubeListBuilder.create()
				.texOffs(16, 8).addBox(-2.01F, -4.0F, 0.0F, 4, 4, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.136659280431156F, 0.0F, 0.0F));
		var keel_bow_detail4l = keel_bow_detail3.addOrReplaceChild("keel_bow_detail4l", CubeListBuilder.create()
				.texOffs(32, 8).addBox(0.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.136659280431156F, 0.0F));
		var keel_bow_detail4r = keel_bow_detail3.addOrReplaceChild("keel_bow_detail4r", CubeListBuilder.create()
				.texOffs(80, 5).addBox(-2.0F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, -0.136659280431156F, 0.0F));
		keel_bow_detail4l.addOrReplaceChild("keel_bow_detail5l", CubeListBuilder.create()
				.texOffs(216, 10).addBox(0.01F, -4.0F, 0.0F, 2, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.136659280431156F, 0.0F, 0.0F));
		keel_bow_detail4r.addOrReplaceChild("keel_bow_detail5r", CubeListBuilder.create()
				.texOffs(40, 8).addBox(-2.01F, -4.0F, 0.0F, 2, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.136659280431156F, 0.0F, 0.0F));

		var keel_mid_front = keel_mid.addOrReplaceChild("keel_mid_front", CubeListBuilder.create()
				.texOffs(152, 6).addBox(-2.01F, -4.0F, -6.0F, 4, 4, 6),
			PartPose.offsetAndRotation(0.0F, -2.0F, -14.0F, -0.40980330836826856F, 0.0F, 0.0F));
		var keel_stern_main = keel_mid_front.addOrReplaceChild("keel_stern_main", CubeListBuilder.create()
				.texOffs(241, 27).addBox(-2.0F, -16.0F, -2.0F, 4, 16, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.36425021489121656F, 0.0F, 0.0F));
		keel_stern_main.addOrReplaceChild("keel_stern_detail1", CubeListBuilder.create()
				.texOffs(11, 13).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 5),
			PartPose.offset(0.0F, -14.0F, -2.0F));
		var keel_stern_detail2 = keel_stern_main.addOrReplaceChild("keel_stern_detail2", CubeListBuilder.create()
				.texOffs(186, 12).addBox(-2.0F, -4.0F, -3.0F, 4, 4, 4),
			PartPose.offset(0.0F, -16.0F, 0.0F));
		var keel_stern_connection_l = keel_stern_main.addOrReplaceChild("keel_stern_connection_l", CubeListBuilder.create()
				.texOffs(104, 14).addBox(0.0F, -3.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(2.0F, -14.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F));
		var keel_stern_connection_r = keel_stern_main.addOrReplaceChild("keel_stern_connection_r", CubeListBuilder.create()
				.texOffs(92, 14).addBox(-4.0F, -3.0F, 0.0F, 4, 5, 2),
			PartPose.offsetAndRotation(-2.0F, -14.0F, -2.0F, 0.0F, 0.136659280431156F, 0.0F));
		var keel_stern_detail3 = keel_stern_detail2.addOrReplaceChild("keel_stern_detail3", CubeListBuilder.create()
				.texOffs(116, 14).addBox(-2.01F, -4.0F, -3.0F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.091106186954104F, 0.0F, 0.0F));

		var hull_mid_left_upper = partDefinition.addOrReplaceChild("hull_mid_left_upper", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, -6.0F, -14.0F, 2, 6, 28),
			PartPose.offset(8.0F, 16.0F, 0.0F));
		var ropeconnection_left1a = hull_mid_left_upper.addOrReplaceChild("ropeconnection_left1a", CubeListBuilder.create()
				.texOffs(54, 0).addBox(0.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var ropeconnection_left1b = ropeconnection_left1a.addOrReplaceChild("ropeconnection_left1b", CubeListBuilder.create()
				.texOffs(92, 0).addBox(0.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));
		ropeconnection_left1b.addOrReplaceChild("balloon_rope_left1", CubeListBuilder.create()
				.texOffs(78, 0).addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offset(1.0F, -1.0F, 0.0F));
		var ropeconnection_left2a = hull_mid_left_upper.addOrReplaceChild("ropeconnection_left2a", CubeListBuilder.create()
				.texOffs(66, 0).addBox(0.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(0.0F, -6.0F, 10.0F));
		var ropeconnection_left2b = ropeconnection_left2a.addOrReplaceChild("ropeconnection_left2b", CubeListBuilder.create()
				.texOffs(104, 0).addBox(0.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));
		ropeconnection_left2b.addOrReplaceChild("balloon_rope_left2", CubeListBuilder.create()
				.texOffs(116, 0).addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offset(1.0F, -1.0F, 0.0F));

		var hull_mid_right_upper = partDefinition.addOrReplaceChild("hull_mid_right_upper", CubeListBuilder.create()
				.texOffs(120, 0).addBox(-2.0F, -6.0F, -14.0F, 2, 6, 28),
			PartPose.offset(-8.0F, 16.0F, 0.0F));
		var ropeconnection_right1a = hull_mid_right_upper.addOrReplaceChild("ropeconnection_right1a", CubeListBuilder.create()
				.texOffs(126, 0).addBox(-2.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var ropeconnection_right1b = ropeconnection_right1a.addOrReplaceChild("ropeconnection_right1b", CubeListBuilder.create()
				.texOffs(164, 0).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));
		ropeconnection_right1b.addOrReplaceChild("balloon_rope_right1", CubeListBuilder.create()
				.texOffs(138, 0).addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offset(-1.0F, -1.0F, 0.0F));
		var ropeconnection_right2a = hull_mid_right_upper.addOrReplaceChild("ropeconnection_right2a", CubeListBuilder.create()
				.texOffs(152, 0).addBox(-2.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(0.0F, -6.0F, 10.0F));
		var ropeconnection_right2b = ropeconnection_right2a.addOrReplaceChild("ropeconnection_right2b", CubeListBuilder.create()
				.texOffs(176, 0).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 4),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));
		ropeconnection_right2b.addOrReplaceChild("balloon_rope_right2", CubeListBuilder.create()
				.texOffs(188, 0).addBox(-1.5F, -1.5F, -1.0F, 3, 3, 2),
			PartPose.offset(-1.0F, -1.0F, 0.0F));

		partDefinition.addOrReplaceChild("hull_mid_left_lower", CubeListBuilder.create()
				.texOffs(0, 34).addBox(0.0F, -8.0F, -14.0F, 2, 10, 28),
			PartPose.offset(6.0F, 22.0F, 0.0F));
		partDefinition.addOrReplaceChild("hull_mid_right_lower", CubeListBuilder.create()
				.texOffs(60, 0).addBox(-2.0F, -8.0F, -14.0F, 2, 10, 28),
			PartPose.offset(-6.0F, 22.0F, 0.0F));

		var bow_bottom = partDefinition.addOrReplaceChild("bow_bottom", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8, 2, 6),
			PartPose.offsetAndRotation(0.0F, 22.0F, 14.0F, 0.045553093477052F, 0.0F, 0.0F));
		bow_bottom.addOrReplaceChild("bow_upper", CubeListBuilder.create()
				.texOffs(92, 6).addBox(-8.0F, -6.0F, 0.0F, 16, 6, 2),
			PartPose.offset(0.0F, -8.0F, 4.0F));
		bow_bottom.addOrReplaceChild("bow_lower", CubeListBuilder.create()
				.texOffs(216, 0).addBox(-6.0F, -8.0F, 0.0F, 12, 8, 2),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		bow_bottom.addOrReplaceChild("hull_bow_left_upper", CubeListBuilder.create()
				.texOffs(70, 5).addBox(0.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(6.0F, -8.0F, 0.0F));
		bow_bottom.addOrReplaceChild("hull_bow_right_upper", CubeListBuilder.create()
				.texOffs(192, 0).addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(-6.0F, -8.0F, 0.0F));
		bow_bottom.addOrReplaceChild("hull_bow_left_lower", CubeListBuilder.create()
				.texOffs(54, 5).addBox(0.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(4.0F, -2.0F, 0.0F));
		bow_bottom.addOrReplaceChild("hull_bow_right_lower", CubeListBuilder.create()
				.texOffs(238, 4).addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(-4.0F, -2.0F, 0.0F));

		var stern_bottom = partDefinition.addOrReplaceChild("stern_bottom", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-4.0F, -2.0F, -4.0F, 8, 2, 6),
			PartPose.offsetAndRotation(0.0F, 22.0F, -14.0F, -0.045553093477052F, 0.0F, 0.0F));
		stern_bottom.addOrReplaceChild("stern_lower", CubeListBuilder.create()
				.texOffs(134, 41).addBox(-6.0F, -8.0F, -2.0F, 12, 8, 2),
			PartPose.offset(0.0F, 0.0F, -4.0F));
		stern_bottom.addOrReplaceChild("stern_upper", CubeListBuilder.create()
				.texOffs(42, 45).addBox(-8.0F, -6.0F, -2.0F, 16, 6, 2),
			PartPose.offset(0.0F, -8.0F, -4.0F));
		stern_bottom.addOrReplaceChild("hull_stern_left_upper", CubeListBuilder.create()
				.texOffs(108, 39).addBox(0.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(6.0F, -8.0F, -2.0F));
		stern_bottom.addOrReplaceChild("hull_stern_right_upper", CubeListBuilder.create()
				.texOffs(162, 35).addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6),
			PartPose.offset(-6.0F, -8.0F, -2.0F));
		stern_bottom.addOrReplaceChild("hull_stern_left_lower", CubeListBuilder.create()
				.texOffs(224, 37).addBox(0.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(4.0F, -2.0F, -2.0F));
		stern_bottom.addOrReplaceChild("hull_stern_right_lower", CubeListBuilder.create()
				.texOffs(92, 38).addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6),
			PartPose.offset(-4.0F, -2.0F, -2.0F));

		var boat_base = partDefinition.addOrReplaceChild("boat_base", CubeListBuilder.create()
				.texOffs(152, 30).addBox(-6.0F, -2.0F, -14.0F, 12, 2, 28),
			PartPose.offset(0.0F, 24.0F, 0.0F));
		boat_base.addOrReplaceChild("bench_front", CubeListBuilder.create()
				.texOffs(134, 34).addBox(-6.0F, 0.0F, -2.5F, 12, 2, 5),
			PartPose.offset(0.0F, -10.0F, -4.0F));
		boat_base.addOrReplaceChild("bench_back", CubeListBuilder.create()
				.texOffs(58, 38).addBox(-6.0F, 0.0F, -2.5F, 12, 2, 5),
			PartPose.offset(0.0F, -10.0F, 8.0F));

		var mountpole_left_base1 = keel_stern_connection_l.addOrReplaceChild("mountpole_left_base1", CubeListBuilder.create()
				.texOffs(32, 34).addBox(0.0F, -3.0F, 0.0F, 4, 11, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F, 0.0F));
		var mountpole_left_base2 = mountpole_left_base1.addOrReplaceChild("mountpole_left_base2", CubeListBuilder.create()
				.texOffs(7, 21).addBox(0.01F, 0.0F, 0.0F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, -0.18203784098300857F, 0.0F, 0.0F));
		mountpole_left_base2.addOrReplaceChild("mountpole_left_base3", CubeListBuilder.create()
				.texOffs(104, 21).addBox(0.0F, 0.0F, 0.0F, 3, 3, 3),
			PartPose.offsetAndRotation(1.0F, 0.0F, 3.0F, -0.18203784098300857F, 0.0F, 0.0F));
		mountpole_left_base1.addOrReplaceChild("mountpole_left_base4", CubeListBuilder.create()
				.texOffs(46, 18).addBox(-2.0F, 0.0F, 0.0F, 2, 3, 3),
			PartPose.offsetAndRotation(4.0F, -1.0F, 2.0F, 0.0F, -0.091106186954104F, 0.0F));

		var mountpole_left_c1 = mountpole_left_base1.addOrReplaceChild("mountpole_left_c1", CubeListBuilder.create()
				.texOffs(56, 21).addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));
		mountpole_left_base1.addOrReplaceChild("mountpole_left_c2", CubeListBuilder.create()
				.texOffs(92, 21).addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offset(2.0F, 2.0F, 0.0F));

		var mountpole_main_left1 = mountpole_left_c1.addOrReplaceChild("mountpole_main_left1", CubeListBuilder.create()
				.texOffs(44, 34).addBox(-2.0F, -5.0F, -3.0F, 4, 8, 3),
			PartPose.offset(0.0F, 0.0F, -2.0F));
		mountpole_main_left1.addOrReplaceChild("mountpole_main_left2", CubeListBuilder.create()
				.texOffs(120, 34).addBox(-2.0F, 0.0F, 0.0F, 4, 8, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));
		mountpole_main_left1.addOrReplaceChild("mountpole_left_rope", CubeListBuilder.create()
				.texOffs(116, 21).addBox(-2.5F, 0.0F, -2.0F, 5, 2, 4),
			PartPose.offsetAndRotation(0.0F, 2.0F, -1.5F, 0.045553093477052F, 0.0F, 0.045553093477052F));

		var mountpole_right_base1 = keel_stern_connection_r.addOrReplaceChild("mountpole_right_base1", CubeListBuilder.create()
				.texOffs(218, 30).addBox(-4.0F, -3.0F, 0.0F, 4, 11, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F, 0.0F));
		var mountpole_right_base2 = mountpole_right_base1.addOrReplaceChild("mountpole_right_base2", CubeListBuilder.create()
				.texOffs(152, 16).addBox(-4.01F, 0.0F, 0.0F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, -0.18203784098300857F, 0.0F, 0.0F));
		mountpole_right_base2.addOrReplaceChild("mountpole_right_base3", CubeListBuilder.create()
				.texOffs(186, 20).addBox(-3.0F, 0.0F, 0.0F, 3, 3, 3),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 3.0F, -0.18203784098300857F, 0.0F, 0.0F));
		mountpole_right_base1.addOrReplaceChild("mountpole_right_base4", CubeListBuilder.create()
				.texOffs(0, 16).addBox(0.0F, 0.0F, 0.0F, 2, 3, 3),
			PartPose.offsetAndRotation(-4.0F, -1.0F, 2.0F, 0.0F, 0.091106186954104F, 0.0F));

		var mountpole_right_c1 = mountpole_right_base1.addOrReplaceChild("mountpole_right_c1", CubeListBuilder.create()
				.texOffs(224, 16).addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));
		mountpole_right_base1.addOrReplaceChild("mountpole_right_c2", CubeListBuilder.create()
				.texOffs(70, 17).addBox(-1.5F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offset(-2.0F, 2.0F, 0.0F));

		var mountpole_main_right1 = mountpole_right_c1.addOrReplaceChild("mountpole_main_right1", CubeListBuilder.create()
				.texOffs(0, 34).addBox(-2.0F, -5.0F, -3.0F, 4, 8, 3),
			PartPose.offset(0.0F, 0.0F, -2.0F));
		mountpole_main_right1.addOrReplaceChild("mountpole_main_right2", CubeListBuilder.create()
				.texOffs(14, 34).addBox(-2.0F, 0.0F, 0.0F, 4, 8, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));
		mountpole_main_right1.addOrReplaceChild("mountpole_right_rope", CubeListBuilder.create()
				.texOffs(232, 20).addBox(-2.5F, 0.0F, -2.0F, 5, 2, 4),
			PartPose.offsetAndRotation(0.0F, 2.0F, -1.5F, 0.045553093477052F, 0.0F, -0.091106186954104F));

		var mountpole_mid1 = keel_stern_detail3.addOrReplaceChild("mountpole_mid1", CubeListBuilder.create()
				.texOffs(32, 15).addBox(-2.0F, -6.0F, -3.0F, 4, 9, 3),
			PartPose.offset(0.0F, 0.0F, -3.0F));
		mountpole_mid1.addOrReplaceChild("mountpole_mid2", CubeListBuilder.create()
				.texOffs(204, 30).addBox(-2.0F, 0.0F, 0.0F, 4, 12, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, -3.0F, 0.091106186954104F, 0.0F, 0.0F));
		mountpole_mid1.addOrReplaceChild("mountpole_mid_rope", CubeListBuilder.create()
				.texOffs(130, 15).addBox(-2.5F, 0.0F, -2.0F, 5, 2, 4),
			PartPose.offsetAndRotation(0.0F, 2.0F, -1.5F, 0.045553093477052F, 0.0F, 0.045553093477052F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public static LayerDefinition createBalloon() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var balloon_mainrotation = partDefinition.addOrReplaceChild("balloon_mainrotation", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, -22.0F, 0.0F));

		var balloon_main1 = balloon_mainrotation.addOrReplaceChild("balloon_main1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, 0.0F, 0.0F, 18, 16, 20),
			PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		balloon_main1.addOrReplaceChild("bone1", CubeListBuilder.create()
				.texOffs(0, 122).addBox(-3.0F, -1.0F, 0.0F, 6, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 14.0F, -0.045553093477052F, 0.0F, 0.0F));
		var balloon_side1l = balloon_main1.addOrReplaceChild("balloon_side1l", CubeListBuilder.create()
				.texOffs(132, 28).addBox(0.0F, 0.0F, 0.0F, 10, 16, 20),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_side1l.addOrReplaceChild("skin1l", CubeListBuilder.create()
				.texOffs(0, 149).addBox(0.0F, 0.0F, 0.0F, 0, 20, 20),
			PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		var balloon_side1r = balloon_main1.addOrReplaceChild("balloon_side1r", CubeListBuilder.create()
				.texOffs(192, 8).addBox(-10.0F, 0.0F, 0.0F, 10, 16, 20),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));
		balloon_side1r.addOrReplaceChild("skin1r", CubeListBuilder.create()
				.texOffs(0, 128).addBox(0.0F, 0.0F, 0.0F, 0, 20, 20),
			PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		var balloon_main2 = balloon_mainrotation.addOrReplaceChild("balloon_main2", CubeListBuilder.create()
				.texOffs(76, 0).addBox(-9.0F, 0.0F, -20.0F, 18, 16, 20),
			PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, 0.045553093477052F, 0.0F, 0.0F));
		balloon_main2.addOrReplaceChild("bone2", CubeListBuilder.create()
				.texOffs(0, 128).addBox(-3.0F, -1.0F, -3.0F, 6, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -14.0F, 0.045553093477052F, 0.0F, 0.0F));
		var balloon_side2l = balloon_main2.addOrReplaceChild("balloon_side2l", CubeListBuilder.create()
				.texOffs(120, 64).addBox(0.0F, 0.0F, -20.0F, 10, 16, 20),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_side2l.addOrReplaceChild("skin2l", CubeListBuilder.create()
				.texOffs(41, 149).addBox(0.01F, 0.0F, -20.0F, 0, 20, 20),
			PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		var balloon_side2r = balloon_main2.addOrReplaceChild("balloon_side2r", CubeListBuilder.create()
				.texOffs(60, 64).addBox(-10.0F, 0.0F, -20.0F, 10, 16, 20),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));
		balloon_side2r.addOrReplaceChild("skin2r", CubeListBuilder.create()
				.texOffs(41, 128).addBox(-0.01F, 0.0F, -20.0F, 0, 20, 20),
			PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		var balloon_main3 = balloon_main1.addOrReplaceChild("balloon_main3", CubeListBuilder.create()
				.texOffs(152, 0).addBox(-9.0F, 0.0F, 0.0F, 18, 16, 12),
			PartPose.offsetAndRotation(0.0F, 0.0F, 20.0F, -0.091106186954104F, 0.0F, 0.0F));
		balloon_main3.addOrReplaceChild("bone3", CubeListBuilder.create()
				.texOffs(19, 122).addBox(-2.0F, -1.0F, 0.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, -0.045553093477052F, 0.0F, 0.0F));
		var balloon_side3l = balloon_main3.addOrReplaceChild("balloon_side3l", CubeListBuilder.create()
				.texOffs(44, 36).addBox(0.0F, 0.0F, 0.0F, 10, 16, 12),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_side3l.addOrReplaceChild("skin3l", CubeListBuilder.create()
				.texOffs(82, 149).addBox(0.01F, 0.0F, 0.0F, 0, 20, 12),
			PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		var balloon_side3r = balloon_main3.addOrReplaceChild("balloon_side3r", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-10.0F, 0.0F, 0.0F, 10, 16, 12),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));
		balloon_side3r.addOrReplaceChild("skin3r", CubeListBuilder.create()
				.texOffs(82, 128).addBox(-0.01F, 0.0F, 0.0F, 0, 20, 12),
			PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		var balloon_main4 = balloon_main2.addOrReplaceChild("balloon_main4", CubeListBuilder.create()
				.texOffs(0, 64).addBox(-9.0F, 0.0F, -12.0F, 18, 16, 12),
			PartPose.offsetAndRotation(0.0F, 0.0F, -20.0F, 0.091106186954104F, 0.0F, 0.0F));
		balloon_main4.addOrReplaceChild("bone4", CubeListBuilder.create()
				.texOffs(19, 127).addBox(-2.0F, -1.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.045553093477052F, 0.0F, 0.0F));
		var balloon_side4l = balloon_main4.addOrReplaceChild("balloon_side4l", CubeListBuilder.create()
				.texOffs(168, 88).addBox(0.0F, 0.0F, -12.0F, 10, 16, 12),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_side4l.addOrReplaceChild("skin4l", CubeListBuilder.create()
				.texOffs(107, 149).addBox(0.0F, 0.0F, -12.0F, 0, 20, 12),
			PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		var balloon_side4r = balloon_main4.addOrReplaceChild("balloon_side4r", CubeListBuilder.create()
				.texOffs(200, 68).addBox(-10.0F, 0.0F, -12.0F, 10, 16, 12),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));
		balloon_side4r.addOrReplaceChild("skin4r", CubeListBuilder.create()
				.texOffs(107, 128).addBox(0.0F, 0.0F, -12.0F, 0, 20, 12),
			PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		balloon_mainrotation.addOrReplaceChild("bonemidr", CubeListBuilder.create()
				.texOffs(34, 128).addBox(-2.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(-4.0F, -16.0F, 0.0F));
		balloon_mainrotation.addOrReplaceChild("bonemidl", CubeListBuilder.create()
				.texOffs(34, 122).addBox(0.0F, -1.0F, -2.0F, 2, 1, 4),
			PartPose.offset(4.0F, -16.0F, 0.0F));

		var balloon_front = balloon_main4.addOrReplaceChild("balloon_front", CubeListBuilder.create()
				.texOffs(0, 92).addBox(-9.0F, 0.0F, -8.015F, 18, 16, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, 0.18203784098300857F, 0.0F, 0.0F));
		balloon_front.addOrReplaceChild("balloon_front_l", CubeListBuilder.create()
				.texOffs(52, 100).addBox(0.0F, 0.0F, -8.0F, 4, 16, 8),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_front.addOrReplaceChild("balloon_front_r", CubeListBuilder.create()
				.texOffs(212, 96).addBox(-4.0F, 0.0F, -8.0F, 4, 16, 8),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));
		var balloon_back = balloon_main3.addOrReplaceChild("balloon_back", CubeListBuilder.create()
				.texOffs(192, 44).addBox(-9.0F, 0.0F, 0.02F, 18, 16, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, 12.0F, -0.18203784098300857F, 0.0F, 0.0F));
		balloon_back.addOrReplaceChild("balloon_back_l", CubeListBuilder.create()
				.texOffs(104, 52).addBox(0.0F, 0.0F, 0.0F, 4, 16, 8),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		balloon_back.addOrReplaceChild("balloon_back_r", CubeListBuilder.create()
				.texOffs(88, 36).addBox(-4.0F, 0.0F, 0.0F, 4, 16, 8),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		var base_main = balloon_mainrotation.addOrReplaceChild("base_main", CubeListBuilder.create()
				.texOffs(0, 190).addBox(-4.0F, 0.0F, -5.0F, 8, 2, 10),
			PartPose.offset(0.0F, 9.0F, 0.0F));
		var base_edge1a = base_main.addOrReplaceChild("base_edge1a", CubeListBuilder.create()
				.texOffs(37, 190).addBox(-4.01F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, 5.0F, 0.27314402793711257F, 0.0F, 0.0F));
		base_edge1a.addOrReplaceChild("base_edge1b", CubeListBuilder.create()
				.texOffs(37, 195).addBox(-3.0F, -2.0F, 0.0F, 6, 2, 1),
			PartPose.offset(0.0F, 0.0F, 2.0F));
		var base_edge2a = base_main.addOrReplaceChild("base_edge2a", CubeListBuilder.create()
				.texOffs(58, 190).addBox(-4.01F, -2.0F, -2.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, 2.0F, -5.0F, -0.31869712141416456F, 0.0F, 0.0F));
		base_edge2a.addOrReplaceChild("base_edge2b", CubeListBuilder.create()
				.texOffs(58, 195).addBox(-3.0F, -2.0F, -1.0F, 6, 2, 1),
			PartPose.offset(0.0F, 0.0F, -2.0F));

		var supportbeam_front1m = balloon_main2.addOrReplaceChild("supportbeam_front1m", CubeListBuilder.create()
				.texOffs(160, 68).addBox(-12.0F, 0.0F, -1.0F, 24, 2, 2),
			PartPose.offset(0.0F, 17.0F, -12.0F));
		var supportbeam_front1l = supportbeam_front1m.addOrReplaceChild("supportbeam_front1l", CubeListBuilder.create()
				.texOffs(0, 12).addBox(0.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(12.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));
		supportbeam_front1l.addOrReplaceChild("ropeconnection_f1l", CubeListBuilder.create()
				.texOffs(139, 11).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(5.0F, 1.0F, 0.0F));
		var supportbeam_front1r = supportbeam_front1m.addOrReplaceChild("supportbeam_front1r", CubeListBuilder.create()
				.texOffs(66, 9).addBox(-7.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));
		supportbeam_front1r.addOrReplaceChild("ropeconnection_f1r", CubeListBuilder.create()
				.texOffs(84, 9).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(-5.0F, 1.0F, 0.0F));

		var supportbeam_front2m = balloon_main4.addOrReplaceChild("supportbeam_front2m", CubeListBuilder.create()
				.texOffs(160, 72).addBox(-12.0F, 0.0F, -1.0F, 24, 2, 2),
			PartPose.offset(0.0F, 17.0F, -7.0F));
		var supportbeam_front2l = supportbeam_front2m.addOrReplaceChild("supportbeam_front2l", CubeListBuilder.create()
				.texOffs(232, 8).addBox(0.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(12.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));
		supportbeam_front2l.addOrReplaceChild("ropeconnection_f2l", CubeListBuilder.create()
				.texOffs(56, 9).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(5.0F, 1.0F, 0.0F));
		var supportbeam_front2r = supportbeam_front2m.addOrReplaceChild("supportbeam_front2r", CubeListBuilder.create()
				.texOffs(0, 8).addBox(-7.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));
		supportbeam_front2r.addOrReplaceChild("ropeconnection_f2r", CubeListBuilder.create()
				.texOffs(132, 8).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(-5.0F, 1.0F, 0.0F));

		var supportbeam_back1m = balloon_main1.addOrReplaceChild("supportbeam_back1m", CubeListBuilder.create()
				.texOffs(200, 0).addBox(-12.0F, 0.0F, -1.0F, 24, 2, 2),
			PartPose.offset(0.0F, 17.0F, 12.0F));
		var supportbeam_back1l = supportbeam_back1m.addOrReplaceChild("supportbeam_back1l", CubeListBuilder.create()
				.texOffs(132, 4).addBox(0.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(12.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));
		supportbeam_back1l.addOrReplaceChild("ropeconnection_b1l", CubeListBuilder.create()
				.texOffs(147, 6).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(5.0F, 1.0F, 0.0F));
		var supportbeam_back1r = supportbeam_back1m.addOrReplaceChild("supportbeam_back1r", CubeListBuilder.create()
				.texOffs(0, 4).addBox(-7.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));
		supportbeam_back1r.addOrReplaceChild("ropeconnection_b1r", CubeListBuilder.create()
				.texOffs(86, 3).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(-5.0F, 1.0F, 0.0F));

		var supportbeam_back2m = balloon_main3.addOrReplaceChild("supportbeam_back2m", CubeListBuilder.create()
				.texOffs(200, 4).addBox(-12.0F, 0.0F, -1.0F, 24, 2, 2),
			PartPose.offset(0.0F, 17.0F, 7.0F));
		var supportbeam_back2l = supportbeam_back2m.addOrReplaceChild("supportbeam_back2l", CubeListBuilder.create()
				.texOffs(132, 0).addBox(0.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(12.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));
		supportbeam_back2l.addOrReplaceChild("ropeconnection_b2l", CubeListBuilder.create()
				.texOffs(150, 0).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(5.0F, 1.0F, 0.0F));var supportbeam_back2r = supportbeam_back2m.addOrReplaceChild("supportbeam_back2r", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-7.0F, 0.0F, -1.0F, 7, 2, 2),
			PartPose.offsetAndRotation(-12.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));
		supportbeam_back2r.addOrReplaceChild("ropeconnection_b2r", CubeListBuilder.create()
				.texOffs(79, 0).addBox(-1.0F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offset(-5.0F, 1.0F, 0.0F));

		base_main.addOrReplaceChild("rope_front_left1", CubeListBuilder.create()
				.texOffs(48, 203).addBox(0.0F, -16.0F, -1.0F, 1, 16, 1),
			PartPose.offsetAndRotation(3.0F, 0.0F, -4.0F, 1.2292353921796064F, -1.0471975511965976F, 0.0F));
		base_main.addOrReplaceChild("rope_front_right1", CubeListBuilder.create()
				.texOffs(53, 203).addBox(-1.0F, -16.0F, -1.0F, 1, 16, 1),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -4.0F, 1.2292353921796064F, 1.0471975511965976F, 0.0F));
		base_main.addOrReplaceChild("rope_back_left1", CubeListBuilder.create()
				.texOffs(38, 203).addBox(0.0F, -16.0F, 0.0F, 1, 16, 1),
			PartPose.offsetAndRotation(3.0F, 0.0F, 4.0F, -1.2292353921796064F, 1.0471975511965976F, 0.0F));
		base_main.addOrReplaceChild("rope_back_right1", CubeListBuilder.create()
				.texOffs(43, 203).addBox(-1.0F, -16.0F, 0.0F, 1, 16, 1),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 4.0F, -1.2292353921796064F, -1.0471975511965976F, 0.0F));

		var vialholder_main = base_main.addOrReplaceChild("vialholder_main", CubeListBuilder.create()
				.texOffs(79, 190).addBox(0.0F, 0.0F, -2.0F, 1, 1, 4),
			PartPose.offset(4.0F, 0.0F, -1.5F));
		var vial_base = vialholder_main.addOrReplaceChild("vial_base", CubeListBuilder.create()
				.texOffs(0, 203).addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2),
			PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));
		vialholder_main.addOrReplaceChild("vialholder1", CubeListBuilder.create()
				.texOffs(79, 196).addBox(0.0F, 0.0F, 0.0F, 3, 1, 1),
			PartPose.offset(1.0F, 0.0F, 1.0F));
		vialholder_main.addOrReplaceChild("vialholder2", CubeListBuilder.create()
				.texOffs(79, 199).addBox(0.0F, 0.0F, -1.0F, 3, 1, 1),
			PartPose.offset(1.0F, 0.0F, -1.0F));
		vial_base.addOrReplaceChild("vial_top", CubeListBuilder.create()
				.texOffs(0, 216).addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));
		vial_base.addOrReplaceChild("vial_bottom", CubeListBuilder.create()
				.texOffs(0, 207).addBox(-2.0F, 0.0F, -2.0F, 4, 4, 4),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		var burner_main = base_main.addOrReplaceChild("burner_main", CubeListBuilder.create()
				.texOffs(17, 203).addBox(-2.0F, -5.0F, -3.0F, 4, 5, 6),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		burner_main.addOrReplaceChild("burner_top", CubeListBuilder.create()
				.texOffs(17, 225).addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(0.0F, -5.0F, 0.0F));
		var burner_vialconnection1 = burner_main.addOrReplaceChild("burner_vialconnection1", CubeListBuilder.create()
				.texOffs(17, 215).addBox(0.0F, 0.0F, -1.0F, 3, 2, 2),
			PartPose.offset(2.0F, -3.0F, -2.0F));
		burner_vialconnection1.addOrReplaceChild("burner_vialconnection2", CubeListBuilder.create()
				.texOffs(17, 220).addBox(0.0F, 0.01F, 0.0F, 3, 2, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, -1.0F, 0.0F, -0.27314402793711257F, 0.0F));

		var heatvent1 = balloon_mainrotation.addOrReplaceChild("heatvent1", CubeListBuilder.create()
				.texOffs(56, 0).addBox(-4.0F, -2.0F, 0.0F, 8, 2, 7),
			PartPose.offset(0.0F, -15.5F, -4.0F));
		heatvent1.addOrReplaceChild("heatvent2", CubeListBuilder.create()
				.texOffs(232, 12).addBox(-3.0F, 0.0F, 0.0F, 6, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 7.0F, -0.091106186954104F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 265);
	}

	public static LayerDefinition createAnchor() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var anchor_main = partDefinition.addOrReplaceChild("anchor_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		anchor_main.addOrReplaceChild("anchor_rope", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-1.5F, -1.0F, -1.5F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.091106186954104F, 0.136659280431156F, 0.0F));
		anchor_main.addOrReplaceChild("anchor_topsplit", CubeListBuilder.create()
				.texOffs(9, 0).addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(0.0F, 0.0F, 0.0F));


		var anchor_hook1a = anchor_main.addOrReplaceChild("anchor_hook1a", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, 0.7285004297824331F, 0.0F, 0.0F));
		var anchor_hook1b = anchor_hook1a.addOrReplaceChild("anchor_hook1b", CubeListBuilder.create()
				.texOffs(9, 13).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.02F, 3.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));
		var anchor_hook1c = anchor_hook1b.addOrReplaceChild("anchor_hook1c", CubeListBuilder.create()
				.texOffs(18, 13).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.01F, 3.0F, 0.0F, 0.9105382707654417F, 0.0F, 0.0F));
		var anchor_hook1d = anchor_hook1c.addOrReplaceChild("anchor_hook1d", CubeListBuilder.create()
				.texOffs(27, 13).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.01F, 3.0F, 0.0F, 1.1383037381507017F, 0.0F, 0.0F));
		anchor_hook1d.addOrReplaceChild("anchor_hook1e", CubeListBuilder.create()
				.texOffs(36, 13).addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var anchor_hook2a = anchor_main.addOrReplaceChild("anchor_hook2a", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-1.0F, 8.0F, 0.0F, 0.7285004297824331F, 1.5707963267948966F, 0.0F));
		var anchor_hook2b = anchor_hook2a.addOrReplaceChild("anchor_hook2b", CubeListBuilder.create()
				.texOffs(9, 19).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.02F, 3.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));
		var anchor_hook2c = anchor_hook2b.addOrReplaceChild("anchor_hook2c", CubeListBuilder.create()
				.texOffs(18, 19).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.01F, 3.0F, 0.0F, 0.9105382707654417F, 0.0F, 0.0F));
		var anchor_hook2d = anchor_hook2c.addOrReplaceChild("anchor_hook2d", CubeListBuilder.create()
				.texOffs(27, 19).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.01F, 3.0F, 0.0F, 1.1383037381507017F, 0.0F, 0.0F));
		anchor_hook2d.addOrReplaceChild("anchor_hook2e", CubeListBuilder.create()
				.texOffs(36, 19).addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var anchor_hook3a = anchor_main.addOrReplaceChild("anchor_hook3a", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, 1.0F, 0.7285004297824331F, 3.141592653589793F, 0.0F));
		var anchor_hook3b = anchor_hook3a.addOrReplaceChild("anchor_hook3b", CubeListBuilder.create()
				.texOffs(9, 25).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.02F, 3.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));
		var anchor_hook3c = anchor_hook3b.addOrReplaceChild("anchor_hook3c", CubeListBuilder.create()
				.texOffs(18, 25).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.01F, 3.0F, 0.0F, 0.9105382707654417F, 0.0F, 0.0F));
		var anchor_hook3d = anchor_hook3c.addOrReplaceChild("anchor_hook3d", CubeListBuilder.create()
				.texOffs(27, 25).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.01F, 3.0F, 0.0F, 1.1383037381507017F, 0.0F, 0.0F));
		anchor_hook3d.addOrReplaceChild("anchor_hook3e", CubeListBuilder.create()
				.texOffs(36, 25).addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var anchor_hook4a = anchor_main.addOrReplaceChild("anchor_hook4a", CubeListBuilder.create()
				.texOffs(9, 7).addBox(-1.01F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(1.0F, 8.0F, 0.0F, 0.7285004297824331F, -1.5707963267948966F, 0.0F));
		var anchor_hook4b = anchor_hook4a.addOrReplaceChild("anchor_hook4b", CubeListBuilder.create()
				.texOffs(18, 7).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.02F, 3.0F, 0.0F, 0.8651597102135892F, 0.0F, 0.0F));
		var anchor_hook4c = anchor_hook4b.addOrReplaceChild("anchor_hook4c", CubeListBuilder.create()
				.texOffs(27, 7).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-0.01F, 3.0F, 0.0F, 0.9105382707654417F, 0.0F, 0.0F));
		var anchor_hook4d = anchor_hook4c.addOrReplaceChild("anchor_hook4d", CubeListBuilder.create()
				.texOffs(36, 7).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.01F, 3.0F, 0.0F, 1.1383037381507017F, 0.0F, 0.0F));
		anchor_hook4d.addOrReplaceChild("anchor_hook4e", CubeListBuilder.create()
				.texOffs(45, 7).addBox(-1.01F, 0.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 2.0F, -0.27314402793711257F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition createCraftingUpgrade() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var crafting_mainrotation = partDefinition.addOrReplaceChild("crafting_mainrotation", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var craftingbench = crafting_mainrotation.addOrReplaceChild("craftingbench", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, -5.0F, 10, 2, 10),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		var support1a = craftingbench.addOrReplaceChild("support1a", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2),
			PartPose.offsetAndRotation(8.0F, 1.0F, 3.0F, 0.0F, 0.0F, -0.091106186954104F));
		var support1b = support1a.addOrReplaceChild("support1b", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-4.0F, 0.0F, -0.99F, 4, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));
		support1b.addOrReplaceChild("support1c", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));
		var support2a = craftingbench.addOrReplaceChild("support2a", CubeListBuilder.create()
				.texOffs(15, 13).addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2),
			PartPose.offsetAndRotation(8.0F, 1.0F, -3.0F, 0.0F, 0.0F, -0.091106186954104F));
		var support2b = support2a.addOrReplaceChild("support2b", CubeListBuilder.create()
				.texOffs(15, 18).addBox(-4.0F, 0.0F, -1.01F, 4, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));
		support2b.addOrReplaceChild("support2c", CubeListBuilder.create()
				.texOffs(15, 23).addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));

		var sawhandle = craftingbench.addOrReplaceChild("sawhandle", CubeListBuilder.create()
				.texOffs(41, 0).addBox(-1.5F, 0.0F, 0.0F, 3, 2, 1),
			PartPose.offsetAndRotation(3.0F, 0.5F, 5.0F, 0.0F, 0.045553093477052F, -0.136659280431156F));
		sawhandle.addOrReplaceChild("sawblade", CubeListBuilder.create()
				.texOffs(41, 4).addBox(-1.5F, 0.0F, 0.0F, 3, 6, 0),
			PartPose.offset(0.0F, 2.0F, 0.5F));

		var thonghandle1 = craftingbench.addOrReplaceChild("thonghandle1", CubeListBuilder.create()
				.texOffs(41, 16).addBox(-0.5F, -1.5F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(7.5F, 1.0F, -5.0F, 0.0F, 0.0F, -0.27314402793711257F));
		var thonghandle2 = thonghandle1.addOrReplaceChild("thonghandle2", CubeListBuilder.create()
				.texOffs(46, 16).addBox(-1.0F, -4.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(-0.5F, 2.5F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		thonghandle1.addOrReplaceChild("thonghead1", CubeListBuilder.create()
				.texOffs(41, 22).addBox(-1.0F, 0.0F, 0.0F, 2, 3, 0),
			PartPose.offset(-0.5F, 2.5F, -0.5F));
		thonghandle2.addOrReplaceChild("thonghead2", CubeListBuilder.create()
				.texOffs(46, 22).addBox(-1.0F, 0.0F, -0.1F, 2, 3, 0),
			PartPose.offset(0.0F, 0.0F, -0.5F));

		var hammerhandle = craftingbench.addOrReplaceChild("hammerhandle", CubeListBuilder.create()
				.texOffs(50, 0).addBox(0.0F, -1.0F, -1.0F, 1, 6, 1),
			PartPose.offsetAndRotation(3.0F, 1.0F, -5.0F, 0.0F, -0.18203784098300857F, -0.136659280431156F));
		hammerhandle.addOrReplaceChild("hammerhead", CubeListBuilder.create()
				.texOffs(50, 8).addBox(-1.5F, 0.0F, -1.0F, 3, 2, 2),
			PartPose.offset(0.5F, 5.0F, -0.5F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition createFurnaceUpgrade() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var furnace_mainrotation = partDefinition.addOrReplaceChild("furnace_mainrotation", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var furnace_base = furnace_mainrotation.addOrReplaceChild("furnace_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, 0.0F, -4.0F, 8, 4, 8),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.045553093477052F));

		var support1a = furnace_base.addOrReplaceChild("support1a", CubeListBuilder.create()
				.texOffs(33, 0).addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2),
			PartPose.offsetAndRotation(5.0F, 3.0F, 2.99F, 0.0F, 0.0F, -0.091106186954104F));
		support1a.addOrReplaceChild("support1b", CubeListBuilder.create()
				.texOffs(33, 5).addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));
		var support2a = furnace_base.addOrReplaceChild("support2a", CubeListBuilder.create()
				.texOffs(33, 10).addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2),
			PartPose.offsetAndRotation(5.0F, 3.0F, -2.99F, 0.0F, 0.0F, -0.091106186954104F));
		support2a.addOrReplaceChild("support2b", CubeListBuilder.create()
				.texOffs(33, 15).addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.40980330836826856F));

		var furnace_topside1 = furnace_base.addOrReplaceChild("furnace_topside1", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-6.0F, -4.0F, 0.0F, 6, 4, 2),
			PartPose.offsetAndRotation(6.0F, 0.0F, 1.99F, 0.0F, 0.0F, -0.091106186954104F));
		furnace_topside1.addOrReplaceChild("cornerpiece1", CubeListBuilder.create()
				.texOffs(17, 13).addBox(-1.0F, 0.0F, -1.0F, 1, 1, 1),
			PartPose.offset(0.0F, -4.0F, 0.0F));
		furnace_topside1.addOrReplaceChild("connection1", CubeListBuilder.create()
				.texOffs(22, 13).addBox(-1.0F, -1.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-5.0F, -2.0F, 2.0F, 0.0F, -0.22759093446006054F, 0.0F));


		var furnace_topside2 = furnace_base.addOrReplaceChild("furnace_topside2", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-6.0F, -4.0F, -2.0F, 6, 4, 2),
			PartPose.offsetAndRotation(6.0F, 0.0F, -1.99F, 0.0F, 0.0F, -0.091106186954104F));
		furnace_topside2.addOrReplaceChild("cornerpiece2", CubeListBuilder.create()
				.texOffs(17, 16).addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offset(0.0F, -4.0F, 0.0F));
		furnace_topside2.addOrReplaceChild("connection2", CubeListBuilder.create()
				.texOffs(22, 17).addBox(-1.0F, -1.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-5.0F, -2.0F, -2.0F, 0.0F, 0.22759093446006054F, 0.0F));
		var top = furnace_topside2.addOrReplaceChild("furnace_top", CubeListBuilder.create()
				.texOffs(34, 23).addBox(-6.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offset(0.0F, -4.0F, 2.0F));
		top.addOrReplaceChild("furnace_backplate", CubeListBuilder.create()
				.texOffs(0, 23).addBox(0.0F, 0.0F, -2.0F, 0, 4, 4),
			PartPose.offset(-5.8F, 0.0F, 0.0F));

		var poker1 = furnace_topside2.addOrReplaceChild("poker1", CubeListBuilder.create()
				.texOffs(27, 13).addBox(-0.5F, -2.0F, -1.0F, 1, 6, 1),
			PartPose.offsetAndRotation(-3.0F, -1.5F, -2.0F, 0.0F, 0.31869712141416456F, 0.0F));
		poker1.addOrReplaceChild("poker2", CubeListBuilder.create()
				.texOffs(27, 21).addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.31869712141416456F, 0.0F, 0.0F));

		furnace_base.addOrReplaceChild("burnhatch", CubeListBuilder.create()
				.texOffs(11, 22).addBox(-0.5F, -3.0F, -3.0F, 1, 3, 6),
			PartPose.offsetAndRotation(6.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition createPulley() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var pulley_main = partDefinition.addOrReplaceChild("pulley_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4, 3, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		pulley_main.addOrReplaceChild("guidingedge", CubeListBuilder.create()
				.texOffs(0, 21).addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 2.0F, -0.136659280431156F, 0.0F, 0.0F));
		pulley_main.addOrReplaceChild("sideconnectionleft", CubeListBuilder.create()
				.texOffs(0, 24).addBox(0.0F, -4.0F, 0.0F, 2, 4, 3),
			PartPose.offset(4.0F, -10.0F, -1.0F));
		pulley_main.addOrReplaceChild("sideconnection_right", CubeListBuilder.create()
				.texOffs(11, 24).addBox(-2.0F, -4.0F, -1.0F, 2, 4, 3),
			PartPose.offset(-4.0F, -10.0F, 0.0F));

		var rotatingbeam = pulley_main.addOrReplaceChild("rotatingbeam", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-4.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offset(0.0F, -12.0F, 0.5F));
		rotatingbeam.addOrReplaceChild("rope1", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-2.0F, -1.5F, -1.5F, 4, 3, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));

		var handle1a = rotatingbeam.addOrReplaceChild("handle1a", CubeListBuilder.create()
				.texOffs(19, 0).addBox(-1.0F, 0.88F, -0.5F, 2, 2, 1),
			PartPose.offset(3.0F, 0.0F, 0.0F));
		var handle1b = handle1a.addOrReplaceChild("handle1b", CubeListBuilder.create()
				.texOffs(26, 0).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(-1.0F, 1.88F, -0.5F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1c = handle1b.addOrReplaceChild("handle1c", CubeListBuilder.create()
				.texOffs(19, 4).addBox(0.0F, -1.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1d = handle1c.addOrReplaceChild("handle1d", CubeListBuilder.create()
				.texOffs(26, 3).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1e = handle1d.addOrReplaceChild("handle1e", CubeListBuilder.create()
				.texOffs(19, 8).addBox(0.0F, -1.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1f = handle1e.addOrReplaceChild("handle1f", CubeListBuilder.create()
				.texOffs(26, 6).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1g = handle1f.addOrReplaceChild("handle1g", CubeListBuilder.create()
				.texOffs(19, 12).addBox(0.0F, -1.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1h = handle1g.addOrReplaceChild("handle1h", CubeListBuilder.create()
				.texOffs(26, 9).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1i = handle1h.addOrReplaceChild("handle1i", CubeListBuilder.create()
				.texOffs(19, 16).addBox(0.0F, -1.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1j = handle1i.addOrReplaceChild("handle1j", CubeListBuilder.create()
				.texOffs(26, 12).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var handle1k = handle1j.addOrReplaceChild("handle1k", CubeListBuilder.create()
				.texOffs(19, 20).addBox(0.0F, -1.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));
		handle1k.addOrReplaceChild("handle1l", CubeListBuilder.create()
				.texOffs(26, 15).addBox(0.0F, -1.0F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.5235987755982988F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public static LayerDefinition createStorageCompartment() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var storage_main = partDefinition.addOrReplaceChild("storage_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, -2.0F, -4.0F, 6, 4, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));
		storage_main.addOrReplaceChild("storage_bottom", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-6.0F, 0.0F, -4.0F, 6, 5, 8),
			PartPose.offsetAndRotation(6.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));
		storage_main.addOrReplaceChild("storage_lid", CubeListBuilder.create()
				.texOffs(0, 13).addBox(0.0F, -1.0F, -4.0F, 6, 1, 8),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		storage_main.addOrReplaceChild("pouch1", CubeListBuilder.create()
				.texOffs(21, 13).addBox(-1.5F, 0.0F, 0.0F, 3, 5, 2),
			PartPose.offsetAndRotation(3.0F, 1.0F, 4.0F, -0.136659280431156F, 0.0F, 0.0F));
		storage_main.addOrReplaceChild("pouch2", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-1.5F, 0.0F, -2.0F, 3, 4, 2),
			PartPose.offsetAndRotation(3.0F, 1.0F, -4.0F, 0.091106186954104F, 0.0F, 0.0F));

		storage_main.addOrReplaceChild("rope_top1", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-7.0F, -1.0F, 0.0F, 7, 1, 0),
			PartPose.offsetAndRotation(6.0F, 0.0F, 4.0F, 0.0F, 0.18203784098300857F, 0.0F));
		storage_main.addOrReplaceChild("rope_top2", CubeListBuilder.create()
				.texOffs(0, 30).addBox(-7.0F, -1.0F, 0.0F, 7, 1, 0),
			PartPose.offsetAndRotation(6.0F, 0.0F, -4.0F, 0.0F, -0.18203784098300857F, 0.0F));


		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
