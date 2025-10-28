package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@SuppressWarnings("unused") //I cant be bothered
public class DecayPitPlugModel {

	//plug parts
	private final ModelPart[] plugParts;
	private final ModelPart[] chainParts;

	public DecayPitPlugModel(ModelPart root) {
		this.plugParts = new ModelPart[] {
			root.getChild("base_mid"),
			root.getChild("base_top"),
			root.getChild("base_bottom"),
			root.getChild("lid_base_mid"),
			root.getChild("lid_base_front"),
			root.getChild("lid_base_back")
		};
		this.chainParts = new ModelPart[] {
			root.getChild("topconnection_left1"),
			root.getChild("topconnection_front1"),
			root.getChild("topconnection_back1"),
			root.getChild("topconnection_right1"),
			root.getChild("chainconnection_upper_right"),
			root.getChild("chainconnection_upper_back"),
			root.getChild("chainconnection_upper_left"),
			root.getChild("chainconnection_upper_front")
		};
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("base_mid", CubeListBuilder.create()
				.texOffs(0, 51).addBox(-20.0F, 0.0F, -20.0F, 40, 6, 40),
			PartPose.offset(0.0F, 13.0F, 0.0F));

		var topBase = partDefinition.addOrReplaceChild("base_top", CubeListBuilder.create()
				.texOffs(0, 97).addBox(-24.0F, 0.0F, -22.0F, 48, 5, 46),
			PartPose.offset(0.0F, 8.0F, 0.0F));
		var topBase1 = topBase.addOrReplaceChild("base_top_edge1", CubeListBuilder.create()
				.texOffs(188, 16).addBox(-16.0F, 0.0F, -2.0F, 28, 5, 2),
			PartPose.offset(0.0F, 0.0F, -22.0F));
		var topBase2 = topBase1.addOrReplaceChild("base_top_edge2", CubeListBuilder.create()
				.texOffs(16, 7).addBox(0.0F, 0.0F, -2.0F, 8, 3, 2),
			PartPose.offset(12.0F, 0.0F, 0.0F));
		topBase2.addOrReplaceChild("base_top_edge3", CubeListBuilder.create()
				.texOffs(241, 5).addBox(-5.0F, 0.0F, -2.0F, 5, 4, 2),
			PartPose.offset(-16.0F, 0.0F, 0.0F));

		var bottomBase = partDefinition.addOrReplaceChild("base_bottom", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-24.0F, 0.0F, -24.0F, 46, 5, 46),
			PartPose.offset(0.0F, 19.0F, 0.0F));
		var bottomBase1 = bottomBase.addOrReplaceChild("base_bottom_edge1", CubeListBuilder.create()
				.texOffs(147, 14).addBox(0.0F, 0.0F, -24.0F, 2, 5, 37),
			PartPose.offset(22.0F, 0.0F, 0.0F));
		var bottomBase2 = bottomBase1.addOrReplaceChild("base_bottom_edge2", CubeListBuilder.create()
				.texOffs(0, 5).addBox(0.0F, 0.0F, 0.0F, 2, 3, 6),
			PartPose.offset(0.0F, 2.0F, 13.0F));
		var bottomBase3 = bottomBase.addOrReplaceChild("base_bottom_edge3", CubeListBuilder.create()
				.texOffs(138, 7).addBox(-24.0F, 0.0F, 0.0F, 44, 3, 2),
			PartPose.offset(0.0F, 0.0F, 22.0F));
		bottomBase3.addOrReplaceChild("base_bottom_edge4", CubeListBuilder.create()
				.texOffs(188, 12).addBox(-24.0F, 0.0F, 0.0F, 31, 2, 2),
			PartPose.offset(0.0F, 3.0F, 0.0F));

		var midLid = partDefinition.addOrReplaceChild("lid_base_mid", CubeListBuilder.create()
				.texOffs(0, 148).addBox(-28.5F, 1.0F, -9.5F, 57, 3, 19),
			PartPose.offset(0.0F, 4.0F, 0.0F));
		midLid.addOrReplaceChild("lid_mid_edge1", CubeListBuilder.create()
				.texOffs(120, 56).addBox(-20.5F, 0.0F, -9.5F, 49, 1, 17),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		midLid.addOrReplaceChild("lid_mid_edge2", CubeListBuilder.create()
				.texOffs(5, 12).addBox(-6.0F, 0.0F, -9.5F, 6, 1, 11),
			PartPose.offset(-20.5F, 0.0F, 0.0F));
		midLid.addOrReplaceChild("lid_mid_edge3", CubeListBuilder.create()
				.texOffs(28, 12).addBox(-5.0F, 0.0F, 0.0F, 5, 1, 4),
			PartPose.offset(-20.5F, 0.0F, 1.5F));
		midLid.addOrReplaceChild("lid_mid_edge4", CubeListBuilder.create()
				.texOffs(138, 12).addBox(0.0F, 0.0F, 0.0F, 12, 1, 2),
			PartPose.offset(16.5F, 0.0F, 7.5F));

		var backLid = partDefinition.addOrReplaceChild("lid_base_back", CubeListBuilder.create()
				.texOffs(0, 191).addBox(-26.5F, 0.0F, 2.0F, 55, 4, 17),
			PartPose.offsetAndRotation(0.0F, 4.5F, 9.5F, -0.045553093477052F, 0.022863813201125717F, 0.0F));
		backLid.addOrReplaceChild("lid_back_edge1", CubeListBuilder.create()
				.texOffs(120, 80).addBox(0.0F, 0.0F, 0.0F, 34, 4, 2),
			PartPose.offset(-5.51F, 0.0F, 0.0F));
		backLid.addOrReplaceChild("lid_back_edge2", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-18.0F, 0.0F, 0.0F, 18, 2, 2),
			PartPose.offset(-5.5F, 2.0F, 0.0F));
		backLid.addOrReplaceChild("lid_back_edge3", CubeListBuilder.create()
				.texOffs(143, 26).addBox(-2.0F, 0.0F, 0.0F, 2, 4, 15),
			PartPose.offset(-26.5F, 0.0F, 2.0F));
		var chainConBackRight = backLid.addOrReplaceChild("chainconnection_lower_back_right", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.offset(-18.0F, 0.0F, 10.0F));
		chainConBackRight.addOrReplaceChild("chainconnection_lower_back_righttop", CubeListBuilder.create()
				.texOffs(162, 26).addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5),
			PartPose.offset(0.0F, -4.0F, 0.0F));
		var chainConBackLeft = backLid.addOrReplaceChild("chainconnection_lower_back_left", CubeListBuilder.create()
				.texOffs(188, 35).addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.offset(18.0F, 0.0F, 10.0F));
		chainConBackLeft.addOrReplaceChild("chainconnection_lower_back_lefttop", CubeListBuilder.create()
				.texOffs(24, 28).addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var frontLid = partDefinition.addOrReplaceChild("lid_base_front", CubeListBuilder.create()
				.texOffs(0, 170).addBox(-28.5F, 0.0F, -17.0F, 55, 4, 17),
			PartPose.offsetAndRotation(0.0F, 4.5F, -9.5F, 0.045553093477052F, 0.0F, 0.0F));
		frontLid.addOrReplaceChild("lid_front_edge1", CubeListBuilder.create()
				.texOffs(120, 74).addBox(-28.5F, 0.0F, -2.0F, 50, 4, 2),
			PartPose.offset(0.0F, 0.0F, -17.0F));
		frontLid.addOrReplaceChild("lid_front_edge2", CubeListBuilder.create()
				.texOffs(156, 12).addBox(0.0F, 0.0F, -10.0F, 2, 4, 10),
			PartPose.offset(26.5F, 0.0F, 0.0F));
		frontLid.addOrReplaceChild("lid_front_edge3", CubeListBuilder.create()
				.texOffs(34, 3).addBox(0.0F, 0.0F, -4.0F, 2, 2, 4),
			PartPose.offset(26.5F, 0.0F, -10.0F));
		var chainConFrontLeft = frontLid.addOrReplaceChild("chainconnection_lower_front_left", CubeListBuilder.create()
				.texOffs(188, 23).addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.offset(18.0F, 0.0F, -10.0F));
		chainConFrontLeft.addOrReplaceChild("chainconnection_lower_front_lefttop", CubeListBuilder.create()
				.texOffs(138, 15).addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5),
			PartPose.offset(0.0F, -4.0F, 0.0F));
		var chainConFrontRight = frontLid.addOrReplaceChild("chainconnection_lower_front_right", CubeListBuilder.create()
				.texOffs(220, 23).addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.offset(-18.0F, 0.0F, -10.0F));
		chainConFrontRight.addOrReplaceChild("chainconnection_lower_front_righttop", CubeListBuilder.create()
				.texOffs(138, 22).addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		var topconnection_left1 = partDefinition.addOrReplaceChild("topconnection_left1", CubeListBuilder.create()
				.texOffs(238, 0).addBox(-5.0F, 0.01F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(14.5F, -24.0F, 2.0F, 0.0F, 0.2617993877991494F, 0.0F));
		var topconnection_left2 = topconnection_left1.addOrReplaceChild("topconnection_left2", CubeListBuilder.create()
				.texOffs(243, 86).addBox(-2.0F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_left3 = topconnection_left2.addOrReplaceChild("topconnection_left3", CubeListBuilder.create()
				.texOffs(228, 88).addBox(-5.0F, 0.01F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_left4 = topconnection_left2.addOrReplaceChild("topconnection_left4", CubeListBuilder.create()
				.texOffs(160, 86).addBox(-1.0F, -1.0F, 0.0F, 1, 1, 3),
			PartPose.offsetAndRotation(-2.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));
		var topconnection_left5 = topconnection_left4.addOrReplaceChild("topconnection_left5", CubeListBuilder.create()
				.texOffs(22, 87).addBox(-2.0F, -0.99F, 0.0F, 2, 1, 3),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F, 0.0F));
		var topconnection_left6 = topconnection_left2.addOrReplaceChild("topconnection_left6", CubeListBuilder.create()
				.texOffs(199, 88).addBox(0.0F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.0F));

		var topconnection_back1 = partDefinition.addOrReplaceChild("topconnection_back1", CubeListBuilder.create()
				.texOffs(227, 0).addBox(-3.0F, 0.01F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(-2.0F, -24.0F, 14.5F, 0.0F, 0.2617993877991494F, 0.0F));
		var topconnection_back2 = topconnection_back1.addOrReplaceChild("topconnection_back2", CubeListBuilder.create()
				.texOffs(184, 86).addBox(-3.0F, 0.0F, -9.0F, 3, 2, 9),
			PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_back3 = topconnection_back2.addOrReplaceChild("topconnection_back3", CubeListBuilder.create()
				.texOffs(212, 86).addBox(-3.0F, 0.01F, -5.0F, 3, 2, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_back7 = topconnection_left6.addOrReplaceChild("topconnection_back7", CubeListBuilder.create()
				.texOffs(241, 91).addBox(0.0F, 0.0F, -3.0F, 3, 1, 3),
			PartPose.offsetAndRotation(2.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.27314402793711257F));

		var topconnection_front1 = partDefinition.addOrReplaceChild("topconnection_front1", CubeListBuilder.create()
				.texOffs(21, 0).addBox(0.0F, 0.01F, 0.0F, 3, 2, 5),
			PartPose.offsetAndRotation(2.0F, -24.0F, -14.5F, 0.0F, 0.2617993877991494F, 0.0F));
		var topconnection_front2 = topconnection_front1.addOrReplaceChild("topconnection_front2", CubeListBuilder.create()
				.texOffs(160, 86).addBox(0.0F, 0.0F, 0.0F, 3, 2, 9),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_front3 = topconnection_front2.addOrReplaceChild("topconnection_front3", CubeListBuilder.create()
				.texOffs(175, 86).addBox(0.0F, 0.0F, 0.0F, 3, 2, 5),
			PartPose.offsetAndRotation(0.0F, 0.01F, 9.0F, 0.0F, 0.5235987755982988F, 0.0F));

		var topconnection_right1 = partDefinition.addOrReplaceChild("topconnection_right1", CubeListBuilder.create()
				.texOffs(216, 0).addBox(0.0F, 0.01F, -3.0F, 5, 2, 3),
			PartPose.offsetAndRotation(-14.5F, -24.0F, -2.0F, 0.0F, 0.2617993877991494F, 0.0F));
		var topconnection_right2 = topconnection_right1.addOrReplaceChild("topconnection_right2", CubeListBuilder.create()
				.texOffs(120, 86).addBox(0.0F, 0.0F, -3.0F, 9, 2, 3),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.0F, 0.5235987755982988F, 0.0F));
		var topconnection_right3 = topconnection_right2.addOrReplaceChild("topconnection_right3", CubeListBuilder.create()
				.texOffs(144, 86).addBox(0.0F, 0.01F, -3.0F, 5, 2, 3),
			PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.5235987755982988F, 0.0F));

		createFrontChains(partDefinition);
		createBackChains(partDefinition);
		createleftChains(partDefinition);
		createRightChains(partDefinition);

		return LayerDefinition.create(definition, 256, 256);
	}

	private static void createFrontChains(PartDefinition partDefinition) {
		var chainconnection_upper_front = partDefinition.addOrReplaceChild("chainconnection_upper_front", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, 0.0F, -1.5F, 10, 2, 3),
			PartPose.offset(0.0F, -24.0F, -16.0F));
		var chainconnection_upper_front_piece1a = chainconnection_upper_front.addOrReplaceChild("chainconnection_upper_front_piece1a", CubeListBuilder.create()
				.texOffs(170, 12).addBox(0.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(2.0F, 2.0F, 0.0F));
		var chainconnection_upper_front_piece1b = chainconnection_upper_front_piece1a.addOrReplaceChild("chainconnection_upper_front_piece1b", CubeListBuilder.create()
				.texOffs(28, 17).addBox(0.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, -0.5918411493512771F));
		var chainconnection_upper_front_piece2a = chainconnection_upper_front.addOrReplaceChild("chainconnection_upper_front_piece2a", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-3.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(-2.0F, 2.0F, 0.0F));
		var chainconnection_upper_front_piece2b = chainconnection_upper_front_piece2a.addOrReplaceChild("chainconnection_upper_front_piece2b", CubeListBuilder.create()
				.texOffs(212, 23).addBox(-3.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, 0.5918411493512771F));


		var chain_front_left1a = chainconnection_upper_front_piece1b.addOrReplaceChild("chain_front_left1a", CubeListBuilder.create()
				.texOffs(32, 0).addBox(0.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));
		var chain_front_left1b = chain_front_left1a.addOrReplaceChild("chain_front_left1b", CubeListBuilder.create()
				.texOffs(36, 0).addBox(0.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(1.0F, 2.0F, 0.0F));
		var chain_front_left1c = chain_front_left1b.addOrReplaceChild("chain_front_left1c", CubeListBuilder.create()
				.texOffs(158, 15).addBox(0.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(1.0F, 0.0F, 0.01F));
		var chain_front_left2a = chain_front_left1a.addOrReplaceChild("chain_front_left2a", CubeListBuilder.create()
				.texOffs(10, 5).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(1.5F, 2.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.6829473363053812F));
		var chain_front_left2b = chain_front_left2a.addOrReplaceChild("chain_front_left2b", CubeListBuilder.create()
				.texOffs(42, 0).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_left2c = chain_front_left2a.addOrReplaceChild("chain_front_left2c", CubeListBuilder.create()
				.texOffs(0, 5).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_left2d = chain_front_left2a.addOrReplaceChild("chain_front_left2d", CubeListBuilder.create()
				.texOffs(230, 7).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_left3a = chain_front_left2a.addOrReplaceChild("chain_front_left3a", CubeListBuilder.create()
				.texOffs(235, 8).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3203415791337103F, 0.0F));
		var chain_front_left3b = chain_front_left3a.addOrReplaceChild("chain_front_left3b", CubeListBuilder.create()
				.texOffs(42, 9).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_left3c = chain_front_left3a.addOrReplaceChild("chain_front_left3c", CubeListBuilder.create()
				.texOffs(12, 14).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_left3d = chain_front_left3a.addOrReplaceChild("chain_front_left3d", CubeListBuilder.create()
				.texOffs(170, 17).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_left4a = chain_front_left3a.addOrReplaceChild("chain_front_left4a", CubeListBuilder.create()
				.texOffs(175, 18).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.1838568316277536F, 0.0F));
		var chain_front_left4b = chain_front_left4a.addOrReplaceChild("chain_front_left4b", CubeListBuilder.create()
				.texOffs(138, 15).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_left4c = chain_front_left4a.addOrReplaceChild("chain_front_left4c", CubeListBuilder.create()
				.texOffs(153, 15).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_left4d = chain_front_left4a.addOrReplaceChild("chain_front_left4d", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_left5a = chain_front_left4a.addOrReplaceChild("chain_front_left5a", CubeListBuilder.create()
				.texOffs(8, 19).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3658946726107624F, 0.0F));
		var chain_front_left5b = chain_front_left5a.addOrReplaceChild("chain_front_left5b", CubeListBuilder.create()
				.texOffs(248, 16).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_left5c = chain_front_left5a.addOrReplaceChild("chain_front_left5c", CubeListBuilder.create()
				.texOffs(40, 17).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_left5d = chain_front_left5a.addOrReplaceChild("chain_front_left5d", CubeListBuilder.create()
				.texOffs(245, 21).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_left6a = chain_front_left5a.addOrReplaceChild("chain_front_left6a", CubeListBuilder.create()
				.texOffs(38, 22).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));
		var chain_front_left6b = chain_front_left6a.addOrReplaceChild("chain_front_left6b", CubeListBuilder.create()
				.texOffs(138, 22).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_left6c = chain_front_left6a.addOrReplaceChild("chain_front_left6c", CubeListBuilder.create()
				.texOffs(180, 22).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_left6d = chain_front_left6a.addOrReplaceChild("chain_front_left6d", CubeListBuilder.create()
				.texOffs(188, 23).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));


		var chain_front_right1a = chainconnection_upper_front_piece2b.addOrReplaceChild("chain_front_right1a", CubeListBuilder.create()
				.texOffs(28, 12).addBox(-1.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.5918411493512771F));
		var chain_front_right1b = chain_front_right1a.addOrReplaceChild("chain_front_right1b", CubeListBuilder.create()
				.texOffs(161, 0).addBox(-1.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(-1.0F, 2.0F, 0.0F));
		var chain_front_right1c = chain_front_right1b.addOrReplaceChild("chain_front_right1c", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-1.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(-1.0F, 0.0F, 0.01F));
		var chain_front_right2a = chain_front_right1a.addOrReplaceChild("chain_front_right2a", CubeListBuilder.create()
				.texOffs(244, 25).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(-1.5F, 2.0F, 0.0F, -0.18203784098300857F, 0.0F, -0.6829473363053812F));
		var chain_front_right2b = chain_front_right2a.addOrReplaceChild("chain_front_right2b", CubeListBuilder.create()
				.texOffs(224, 23).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_right2c = chain_front_right2a.addOrReplaceChild("chain_front_right2c", CubeListBuilder.create()
				.texOffs(40, 26).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_right2d = chain_front_right2a.addOrReplaceChild("chain_front_right2d", CubeListBuilder.create()
				.texOffs(138, 29).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_right3a = chain_front_right2a.addOrReplaceChild("chain_front_right3a", CubeListBuilder.create()
				.texOffs(146, 29).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.593485607070823F, 0.0F));
		var chain_front_right3b = chain_front_right3a.addOrReplaceChild("chain_front_right3b", CubeListBuilder.create()
				.texOffs(162, 26).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_right3c = chain_front_right3a.addOrReplaceChild("chain_front_right3c", CubeListBuilder.create()
				.texOffs(177, 26).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_right3d = chain_front_right3a.addOrReplaceChild("chain_front_right3d", CubeListBuilder.create()
				.texOffs(138, 33).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_right4a = chain_front_right3a.addOrReplaceChild("chain_front_right4a", CubeListBuilder.create()
				.texOffs(146, 33).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3203415791337103F, 0.0F));
		var chain_front_right4b = chain_front_right4a.addOrReplaceChild("chain_front_right4b", CubeListBuilder.create()
				.texOffs(24, 28).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_right4c = chain_front_right4a.addOrReplaceChild("chain_front_right4c", CubeListBuilder.create()
				.texOffs(154, 29).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_right4d = chain_front_right4a.addOrReplaceChild("chain_front_right4d", CubeListBuilder.create()
				.texOffs(162, 33).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_right5a = chain_front_right4a.addOrReplaceChild("chain_front_right5a", CubeListBuilder.create()
				.texOffs(170, 33).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_front_right5b = chain_front_right5a.addOrReplaceChild("chain_front_right5b", CubeListBuilder.create()
				.texOffs(178, 33).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_right5c = chain_front_right5a.addOrReplaceChild("chain_front_right5c", CubeListBuilder.create()
				.texOffs(154, 34).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_right5d = chain_front_right5a.addOrReplaceChild("chain_front_right5d", CubeListBuilder.create()
				.texOffs(32, 35).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_front_right6a = chain_front_right5a.addOrReplaceChild("chain_front_right6a", CubeListBuilder.create()
				.texOffs(188, 35).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));
		var chain_front_right6b = chain_front_right6a.addOrReplaceChild("chain_front_right6b", CubeListBuilder.create()
				.texOffs(251, 34).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_front_right6c = chain_front_right6a.addOrReplaceChild("chain_front_right6c", CubeListBuilder.create()
				.texOffs(40, 35).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_front_right6d = chain_front_right6a.addOrReplaceChild("chain_front_right6d", CubeListBuilder.create()
				.texOffs(212, 35).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
	}

	private static void createBackChains(PartDefinition partDefinition) {
		var chainconnection_upper_back = partDefinition.addOrReplaceChild("chainconnection_upper_back", CubeListBuilder.create()
				.texOffs(164, 0).addBox(-5.0F, 0.0F, -1.5F, 10, 2, 3),
			PartPose.offsetAndRotation(0.0F, -24.0F, 16.0F, 0.0F, 3.141592653589793F, 0.0F));
		var chainconnection_upper_back_piece1a = chainconnection_upper_back.addOrReplaceChild("chainconnection_upper_back_piece1a", CubeListBuilder.create()
				.texOffs(230, 51).addBox(0.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(2.0F, 2.0F, 0.0F));
		var chainconnection_upper_back_piece1b = chainconnection_upper_back_piece1a.addOrReplaceChild("chainconnection_upper_back_piece1b", CubeListBuilder.create()
				.texOffs(1, 55).addBox(0.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, -0.5918411493512771F));
		var chainconnection_upper_back_piece2a = chainconnection_upper_back.addOrReplaceChild("chainconnection_upper_back_piece2a", CubeListBuilder.create()
				.texOffs(239, 53).addBox(-3.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(-2.0F, 2.0F, 0.0F));
		var chainconnection_upper_back_piece2b = chainconnection_upper_back_piece2a.addOrReplaceChild("chainconnection_upper_back_piece2b", CubeListBuilder.create()
				.texOffs(235, 62).addBox(-3.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, 0.5918411493512771F));

		var chain_back_left1a = chainconnection_upper_back_piece1b.addOrReplaceChild("chain_back_left1a", CubeListBuilder.create()
				.texOffs(180, 42).addBox(0.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));
		var chain_back_left1b = chain_back_left1a.addOrReplaceChild("chain_back_left1b", CubeListBuilder.create()
				.texOffs(251, 0).addBox(0.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(1.0F, 2.0F, 0.0F));
		var chain_back_left1c = chain_back_left1b.addOrReplaceChild("chain_back_left1c", CubeListBuilder.create()
				.texOffs(25, 55).addBox(0.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(1.0F, 0.0F, 0.01F));
		var chain_back_left2a = chain_back_left1a.addOrReplaceChild("chain_back_left2a", CubeListBuilder.create()
				.texOffs(13, 55).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(1.5F, 2.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.6829473363053812F));
		var chain_back_left2b = chain_back_left2a.addOrReplaceChild("chain_back_left2b", CubeListBuilder.create()
				.texOffs(140, 51).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_left2c = chain_back_left2a.addOrReplaceChild("chain_back_left2c", CubeListBuilder.create()
				.texOffs(251, 53).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_left2d = chain_back_left2a.addOrReplaceChild("chain_back_left2d", CubeListBuilder.create()
				.texOffs(120, 55).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_left3a = chain_back_left2a.addOrReplaceChild("chain_back_left3a", CubeListBuilder.create()
				.texOffs(125, 56).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.593485607070823F, 0.0F));
		var chain_back_left3b = chain_back_left3a.addOrReplaceChild("chain_back_left3b", CubeListBuilder.create()
				.texOffs(33, 55).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_left3c = chain_back_left3a.addOrReplaceChild("chain_back_left3c", CubeListBuilder.create()
				.texOffs(21, 56).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_left3d = chain_back_left3a.addOrReplaceChild("chain_back_left3d", CubeListBuilder.create()
				.texOffs(235, 58).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_left4a = chain_back_left3a.addOrReplaceChild("chain_back_left4a", CubeListBuilder.create()
				.texOffs(243, 58).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.2747884856566583F, 0.0F));
		var chain_back_left4b = chain_back_left4a.addOrReplaceChild("chain_back_left4b", CubeListBuilder.create()
				.texOffs(133, 56).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_left4c = chain_back_left4a.addOrReplaceChild("chain_back_left4c", CubeListBuilder.create()
				.texOffs(251, 58).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_left4d = chain_back_left4a.addOrReplaceChild("chain_back_left4d", CubeListBuilder.create()
				.texOffs(10, 59).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_left5a = chain_back_left4a.addOrReplaceChild("chain_back_left5a", CubeListBuilder.create()
				.texOffs(120, 59).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_back_left5b = chain_back_left5a.addOrReplaceChild("chain_back_left5b", CubeListBuilder.create()
				.texOffs(36, 59).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_left5c = chain_back_left5a.addOrReplaceChild("chain_back_left5c", CubeListBuilder.create()
				.texOffs(18, 60).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_left5d = chain_back_left5a.addOrReplaceChild("chain_back_left5d", CubeListBuilder.create()
				.texOffs(125, 60).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_left6a = chain_back_left5a.addOrReplaceChild("chain_back_left6a", CubeListBuilder.create()
				.texOffs(0, 61).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));
		var chain_back_left6b = chain_back_left6a.addOrReplaceChild("chain_back_left6b", CubeListBuilder.create()
				.texOffs(32, 60).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_left6c = chain_back_left6a.addOrReplaceChild("chain_back_left6c", CubeListBuilder.create()
				.texOffs(22, 61).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_left6d = chain_back_left6a.addOrReplaceChild("chain_back_left6d", CubeListBuilder.create()
				.texOffs(5, 62).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));


		var chain_back_right1a = chainconnection_upper_back_piece2b.addOrReplaceChild("chain_back_right1a", CubeListBuilder.create()
				.texOffs(26, 61).addBox(-1.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.5918411493512771F));
		var chain_back_right1b = chain_back_right1a.addOrReplaceChild("chain_back_right1b", CubeListBuilder.create()
				.texOffs(15, 5).addBox(-1.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(-1.0F, 2.0F, 0.0F));
		var chain_back_right1c = chain_back_right1b.addOrReplaceChild("chain_back_right1c", CubeListBuilder.create()
				.texOffs(13, 63).addBox(-1.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(-1.0F, 0.0F, 0.01F));
		var chain_back_right2a = chain_back_right1a.addOrReplaceChild("chain_back_right2a", CubeListBuilder.create()
				.texOffs(27, 62).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(-1.5F, 2.0F, 0.0F, -0.18203784098300857F, 0.0F, -0.6829473363053812F));
		var chain_back_right2b = chain_back_right2a.addOrReplaceChild("chain_back_right2b", CubeListBuilder.create()
				.texOffs(133, 61).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_right2c = chain_back_right2a.addOrReplaceChild("chain_back_right2c", CubeListBuilder.create()
				.texOffs(247, 62).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_right2d = chain_back_right2a.addOrReplaceChild("chain_back_right2d", CubeListBuilder.create()
				.texOffs(120, 63).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_right3a = chain_back_right2a.addOrReplaceChild("chain_back_right3a", CubeListBuilder.create()
				.texOffs(125, 64).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.593485607070823F, 0.0F));
		var chain_back_right3b = chain_back_right3a.addOrReplaceChild("chain_back_right3b", CubeListBuilder.create()
				.texOffs(251, 63).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_right3c = chain_back_right3a.addOrReplaceChild("chain_back_right3c", CubeListBuilder.create()
				.texOffs(35, 64).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_right3d = chain_back_right3a.addOrReplaceChild("chain_back_right3d", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_right4a = chain_back_right3a.addOrReplaceChild("chain_back_right4a", CubeListBuilder.create()
				.texOffs(18, 66).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.2747884856566583F, 0.0F));
		var chain_back_right4b = chain_back_right4a.addOrReplaceChild("chain_back_right4b", CubeListBuilder.create()
				.texOffs(26, 66).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_right4c = chain_back_right4a.addOrReplaceChild("chain_back_right4c", CubeListBuilder.create()
				.texOffs(30, 66).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_right4d = chain_back_right4a.addOrReplaceChild("chain_back_right4d", CubeListBuilder.create()
				.texOffs(5, 67).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_right5a = chain_back_right4a.addOrReplaceChild("chain_back_right5a", CubeListBuilder.create()
				.texOffs(120, 67).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.1838568316277536F, 0.0F));
		var chain_back_right5b = chain_back_right5a.addOrReplaceChild("chain_back_right5b", CubeListBuilder.create()
				.texOffs(133, 66).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_back_right5c = chain_back_right5a.addOrReplaceChild("chain_back_right5c", CubeListBuilder.create()
				.texOffs(246, 67).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_right5d = chain_back_right5a.addOrReplaceChild("chain_back_right5d", CubeListBuilder.create()
				.texOffs(125, 68).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_right6a = chain_back_right5a.addOrReplaceChild("chain_back_right6a", CubeListBuilder.create()
				.texOffs(235, 68).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));
		var chain_back_right6d = chain_back_right6a.addOrReplaceChild("chain_back_right6d", CubeListBuilder.create()
				.texOffs(10, 69).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_back_right6c = chain_back_right6a.addOrReplaceChild("chain_back_right6c", CubeListBuilder.create()
				.texOffs(0, 69).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_back_right6b = chain_back_right6a.addOrReplaceChild("chain_back_right6b", CubeListBuilder.create()
				.texOffs(250, 68).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
	}

	private static void createleftChains(PartDefinition partDefinition) {
		var chainconnection_upper_left = partDefinition.addOrReplaceChild("chainconnection_upper_left", CubeListBuilder.create()
				.texOffs(190, 0).addBox(-5.0F, 0.0F, -1.5F, 10, 2, 3),
			PartPose.offsetAndRotation(16.0F, -24.0F, 0.0F, 0.0F, -1.5707963267948966F, 0.0F));
		var chainconnection_upper_left_piece1a = chainconnection_upper_left.addOrReplaceChild("chainconnection_upper_left_piece1a", CubeListBuilder.create()
				.texOffs(1, 71).addBox(0.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(2.0F, 2.0F, 0.0F));
		var chainconnection_upper_left_piece1b = chainconnection_upper_left_piece1a.addOrReplaceChild("chainconnection_upper_left_piece1b", CubeListBuilder.create()
				.texOffs(27, 71).addBox(0.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, -0.5918411493512771F));
		var chainconnection_upper_left_piece2a = chainconnection_upper_left.addOrReplaceChild("chainconnection_upper_left_piece2a", CubeListBuilder.create()
				.texOffs(15, 71).addBox(-3.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(-2.0F, 2.0F, 0.0F));
		var chainconnection_upper_left_piece2b = chainconnection_upper_left_piece2a.addOrReplaceChild("chainconnection_upper_left_piece2b", CubeListBuilder.create()
				.texOffs(192, 80).addBox(-3.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, 0.5918411493512771F));


		var chain_left_left1a = chainconnection_upper_left_piece1b.addOrReplaceChild("chain_left_left1a", CubeListBuilder.create()
				.texOffs(36, 69).addBox(0.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));
		var chain_left_left1b = chain_left_left1a.addOrReplaceChild("chain_left_left1b", CubeListBuilder.create()
				.texOffs(42, 5).addBox(0.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(1.0F, 2.0F, 0.0F));
		var chain_left_left1c = chain_left_left1b.addOrReplaceChild("chain_left_left1c", CubeListBuilder.create()
				.texOffs(10, 74).addBox(0.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(1.0F, 0.0F, 0.01F));
		var chain_left_left2a = chain_left_left1a.addOrReplaceChild("chain_left_left2a", CubeListBuilder.create()
				.texOffs(240, 69).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(1.5F, 2.0F, 0.0F, -0.091106186954104F, 0.136659280431156F, 0.5918411493512771F));
		var chain_left_left2b = chain_left_left2a.addOrReplaceChild("chain_left_left2b", CubeListBuilder.create()
				.texOffs(251, 73).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_left2c = chain_left_left2a.addOrReplaceChild("chain_left_left2c", CubeListBuilder.create()
				.texOffs(224, 74).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_left2d = chain_left_left2a.addOrReplaceChild("chain_left_left2d", CubeListBuilder.create()
				.texOffs(228, 74).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_left3a = chain_left_left2a.addOrReplaceChild("chain_left_left3a", CubeListBuilder.create()
				.texOffs(236, 74).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.8651597102135892F, 0.0F));
		var chain_left_left3b = chain_left_left3a.addOrReplaceChild("chain_left_left3b", CubeListBuilder.create()
				.texOffs(244, 74).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_left3c = chain_left_left3a.addOrReplaceChild("chain_left_left3c", CubeListBuilder.create()
				.texOffs(0, 76).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_left3d = chain_left_left3a.addOrReplaceChild("chain_left_left3d", CubeListBuilder.create()
				.texOffs(18, 76).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_left4a = chain_left_left3a.addOrReplaceChild("chain_left_left4a", CubeListBuilder.create()
				.texOffs(245, 76).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3658946726107624F, 0.0F));
		var chain_left_left4b = chain_left_left4a.addOrReplaceChild("chain_left_left4b", CubeListBuilder.create()
				.texOffs(4, 76).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_left4c = chain_left_left4a.addOrReplaceChild("chain_left_left4c", CubeListBuilder.create()
				.texOffs(26, 77).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_left4d = chain_left_left4a.addOrReplaceChild("chain_left_left4d", CubeListBuilder.create()
				.texOffs(30, 77).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_left5a = chain_left_left4a.addOrReplaceChild("chain_left_left5a", CubeListBuilder.create()
				.texOffs(5, 78).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3658946726107624F, 0.0F));
		var chain_left_left5b = chain_left_left5a.addOrReplaceChild("chain_left_left5b", CubeListBuilder.create()
				.texOffs(227, 78).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_left5c = chain_left_left5a.addOrReplaceChild("chain_left_left5c", CubeListBuilder.create()
				.texOffs(231, 78).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_left5d = chain_left_left5a.addOrReplaceChild("chain_left_left5d", CubeListBuilder.create()
				.texOffs(235, 78).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_left6a = chain_left_left5a.addOrReplaceChild("chain_left_left6a", CubeListBuilder.create()
				.texOffs(240, 79).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_left_left6b = chain_left_left6a.addOrReplaceChild("chain_left_left6b", CubeListBuilder.create()
				.texOffs(223, 79).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_left6c = chain_left_left6a.addOrReplaceChild("chain_left_left6c", CubeListBuilder.create()
				.texOffs(13, 80).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_left6d = chain_left_left6a.addOrReplaceChild("chain_left_left6d", CubeListBuilder.create()
				.texOffs(17, 80).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));


		var chain_left_right1a = chainconnection_upper_left_piece2b.addOrReplaceChild("chain_left_right1a", CubeListBuilder.create()
				.texOffs(204, 80).addBox(-1.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.5918411493512771F));
		var chain_left_right1b = chain_left_right1a.addOrReplaceChild("chain_left_right1b", CubeListBuilder.create()
				.texOffs(138, 5).addBox(-1.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(-1.0F, 2.0F, 0.0F));
		var chain_left_right1c = chain_left_right1b.addOrReplaceChild("chain_left_right1c", CubeListBuilder.create()
				.texOffs(245, 80).addBox(-1.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(-1.0F, 0.0F, 0.01F));
		var chain_left_right2a = chain_left_right1a.addOrReplaceChild("chain_left_right2a", CubeListBuilder.create()
				.texOffs(208, 80).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(-1.5F, 2.0F, 0.0F, -0.091106186954104F, 0.0F, -0.5918411493512771F));
		var chain_left_right2b = chain_left_right2a.addOrReplaceChild("chain_left_right2b", CubeListBuilder.create()
				.texOffs(216, 80).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_right2c = chain_left_right2a.addOrReplaceChild("chain_left_right2c", CubeListBuilder.create()
				.texOffs(0, 81).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_right2d = chain_left_right2a.addOrReplaceChild("chain_left_right2d", CubeListBuilder.create()
				.texOffs(27, 81).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_right3a = chain_left_right2a.addOrReplaceChild("chain_left_right3a", CubeListBuilder.create()
				.texOffs(4, 82).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.593485607070823F, 0.0F));
		var chain_left_right3b = chain_left_right3a.addOrReplaceChild("chain_left_right3b", CubeListBuilder.create()
				.texOffs(35, 81).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_right3c = chain_left_right3a.addOrReplaceChild("chain_left_right3c", CubeListBuilder.create()
				.texOffs(234, 82).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_right3d = chain_left_right3a.addOrReplaceChild("chain_left_right3d", CubeListBuilder.create()
				.texOffs(22, 82).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_right4a = chain_left_right3a.addOrReplaceChild("chain_left_right4a", CubeListBuilder.create()
				.texOffs(217, 82).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_left_right4b = chain_left_right4a.addOrReplaceChild("chain_left_right4b", CubeListBuilder.create()
				.texOffs(226, 83).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_right4c = chain_left_right4a.addOrReplaceChild("chain_left_right4c", CubeListBuilder.create()
				.texOffs(230, 83).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_right4d = chain_left_right4a.addOrReplaceChild("chain_left_right4d", CubeListBuilder.create()
				.texOffs(238, 83).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_right5a = chain_left_right4a.addOrReplaceChild("chain_left_right5a", CubeListBuilder.create()
				.texOffs(201, 84).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_left_right5b = chain_left_right5a.addOrReplaceChild("chain_left_right5b", CubeListBuilder.create()
				.texOffs(209, 84).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_right5c = chain_left_right5a.addOrReplaceChild("chain_left_right5c", CubeListBuilder.create()
				.texOffs(213, 84).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_right5d = chain_left_right5a.addOrReplaceChild("chain_left_right5d", CubeListBuilder.create()
				.texOffs(9, 85).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_left_right6a = chain_left_right5a.addOrReplaceChild("chain_left_right6a", CubeListBuilder.create()
				.texOffs(17, 85).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.8196066167365371F, 0.0F));
		var chain_left_right6b = chain_left_right6a.addOrReplaceChild("chain_left_right6b", CubeListBuilder.create()
				.texOffs(29, 85).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_left_right6c = chain_left_right6a.addOrReplaceChild("chain_left_right6c", CubeListBuilder.create()
				.texOffs(0, 86).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_left_right6d = chain_left_right6a.addOrReplaceChild("chain_left_right6d", CubeListBuilder.create()
				.texOffs(4, 86).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
	}

	private static void createRightChains(PartDefinition partDefinition) {
		var chainconnection_upper_right = partDefinition.addOrReplaceChild("chainconnection_upper_right", CubeListBuilder.create()
				.texOffs(138, 0).addBox(-5.0F, 0.0F, -1.5F, 10, 2, 3),
			PartPose.offsetAndRotation(-16.0F, -24.0F, 0.0F, 0.0F, 1.5707963267948966F, 0.0F));
		var chainconnection_upper_right_piece1a = chainconnection_upper_right.addOrReplaceChild("chainconnection_upper_right_piece1a", CubeListBuilder.create()
				.texOffs(220, 35).addBox(0.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(2.0F, 2.0F, 0.0F));
		var chainconnection_upper_right_piece1b = chainconnection_upper_right_piece1a.addOrReplaceChild("chainconnection_upper_right_piece1b", CubeListBuilder.create()
				.texOffs(241, 37).addBox(0.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, -0.5918411493512771F));
		var chainconnection_upper_right_piece2a = chainconnection_upper_right.addOrReplaceChild("chainconnection_upper_right_piece2a", CubeListBuilder.create()
				.texOffs(232, 35).addBox(-3.0F, 0.0F, -1.5F, 3, 2, 3),
			PartPose.offset(-2.0F, 2.0F, 0.0F));
		var chainconnection_upper_right_piece2b = chainconnection_upper_right_piece2a.addOrReplaceChild("chainconnection_upper_right_piece2b", CubeListBuilder.create()
				.texOffs(217, 44).addBox(-3.0F, 0.0F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.01F, 0.0F, 0.0F, 0.5918411493512771F));


		var chain_right_left1a = chainconnection_upper_right_piece1b.addOrReplaceChild("chain_right_left1a", CubeListBuilder.create()
				.texOffs(180, 17).addBox(0.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));
		var chain_right_left1b = chain_right_left1a.addOrReplaceChild("chain_right_left1b", CubeListBuilder.create()
				.texOffs(187, 0).addBox(0.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(1.0F, 2.0F, 0.0F));
		var chain_right_left1c = chain_right_left1b.addOrReplaceChild("chain_right_left1c", CubeListBuilder.create()
				.texOffs(29, 39).addBox(0.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(1.0F, 0.0F, 0.01F));
		var chain_right_left2a = chain_right_left1a.addOrReplaceChild("chain_right_left2a", CubeListBuilder.create()
				.texOffs(138, 37).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(1.5F, 2.0F, 0.0F, -0.091106186954104F, 0.31869712141416456F, 0.5918411493512771F));
		var chain_right_left2b = chain_right_left2a.addOrReplaceChild("chain_right_left2b", CubeListBuilder.create()
				.texOffs(177, 38).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_left2c = chain_right_left2a.addOrReplaceChild("chain_right_left2c", CubeListBuilder.create()
				.texOffs(37, 39).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_left2d = chain_right_left2a.addOrReplaceChild("chain_right_left2d", CubeListBuilder.create()
				.texOffs(146, 37).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_left3a = chain_right_left2a.addOrReplaceChild("chain_right_left3a", CubeListBuilder.create()
				.texOffs(162, 37).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.3203415791337103F, 0.0F));
		var chain_right_left3b = chain_right_left3a.addOrReplaceChild("chain_right_left3b", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_left3c = chain_right_left3a.addOrReplaceChild("chain_right_left3c", CubeListBuilder.create()
				.texOffs(4, 40).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_left3d = chain_right_left3a.addOrReplaceChild("chain_right_left3d", CubeListBuilder.create()
				.texOffs(188, 39).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_left4a = chain_right_left3a.addOrReplaceChild("chain_right_left4a", CubeListBuilder.create()
				.texOffs(8, 40).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.1838568316277536F, 0.0F));
		var chain_right_left4b = chain_right_left4a.addOrReplaceChild("chain_right_left4b", CubeListBuilder.create()
				.texOffs(16, 40).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_left4c = chain_right_left4a.addOrReplaceChild("chain_right_left4c", CubeListBuilder.create()
				.texOffs(251, 20).addBox(-0.5F, -3.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(0.01F, 4.5F, 0.5F, -0.22759093446006054F, 0.0F, 0.0F));
		var chain_right_left4d = chain_right_left4a.addOrReplaceChild("chain_right_left4d", CubeListBuilder.create()
				.texOffs(20, 40).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_left5a = chain_right_left4a.addOrReplaceChild("chain_right_left5a", CubeListBuilder.create()
				.texOffs(220, 40).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0927506446736497F, 0.0F));
		var chain_right_left5b = chain_right_left5a.addOrReplaceChild("chain_right_left5b", CubeListBuilder.create()
				.texOffs(41, 40).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_left5c = chain_right_left5a.addOrReplaceChild("chain_right_left5c", CubeListBuilder.create()
				.texOffs(228, 40).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_left5d = chain_right_left5a.addOrReplaceChild("chain_right_left5d", CubeListBuilder.create()
				.texOffs(232, 40).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_left6a = chain_right_left5a.addOrReplaceChild("chain_right_left6a", CubeListBuilder.create()
				.texOffs(138, 42).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.9560913642424937F, 0.0F));
		var chain_right_left6b = chain_right_left6a.addOrReplaceChild("chain_right_left6b", CubeListBuilder.create()
				.texOffs(239, 43).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_left6c = chain_right_left6a.addOrReplaceChild("chain_right_left6c", CubeListBuilder.create()
				.texOffs(243, 43).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_left6d = chain_right_left6a.addOrReplaceChild("chain_right_left6d", CubeListBuilder.create()
				.texOffs(247, 43).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));


		var chain_right_right1a = chainconnection_upper_right_piece2b.addOrReplaceChild("chain_right_right1a", CubeListBuilder.create()
				.texOffs(170, 37).addBox(-1.0F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.5918411493512771F));
		var chain_right_right1b = chain_right_right1a.addOrReplaceChild("chain_right_right1b", CubeListBuilder.create()
				.texOffs(213, 0).addBox(-1.0F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(-1.0F, 2.0F, 0.0F));
		var chain_right_right1c = chain_right_right1b.addOrReplaceChild("chain_right_right1c", CubeListBuilder.create()
				.texOffs(234, 45).addBox(-1.0F, -2.0F, -1.5F, 1, 3, 3),
			PartPose.offset(-1.0F, 0.0F, 0.01F));
		var chain_right_right2a = chain_right_right1a.addOrReplaceChild("chain_right_right2a", CubeListBuilder.create()
				.texOffs(229, 44).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(-1.5F, 2.0F, 0.0F, -0.091106186954104F, -0.27314402793711257F, -0.5918411493512771F));
		var chain_right_right2b = chain_right_right2a.addOrReplaceChild("chain_right_right2b", CubeListBuilder.create()
				.texOffs(229, 48).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_right2c = chain_right_right2a.addOrReplaceChild("chain_right_right2c", CubeListBuilder.create()
				.texOffs(242, 48).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_right2d = chain_right_right2a.addOrReplaceChild("chain_right_right2d", CubeListBuilder.create()
				.texOffs(188, 47).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_right3a = chain_right_right2a.addOrReplaceChild("chain_right_right3a", CubeListBuilder.create()
				.texOffs(196, 47).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.8212510744560826F, 0.0F));
		var chain_right_right3b = chain_right_right3a.addOrReplaceChild("chain_right_right3b", CubeListBuilder.create()
				.texOffs(246, 48).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_right3c = chain_right_right3a.addOrReplaceChild("chain_right_right3c", CubeListBuilder.create()
				.texOffs(250, 48).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_right3d = chain_right_right3a.addOrReplaceChild("chain_right_right3d", CubeListBuilder.create()
				.texOffs(204, 47).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_right4a = chain_right_right3a.addOrReplaceChild("chain_right_right4a", CubeListBuilder.create()
				.texOffs(212, 47).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.1383037381507017F, 0.0F));
		var chain_right_right4b = chain_right_right4a.addOrReplaceChild("chain_right_right4b", CubeListBuilder.create()
				.texOffs(225, 50).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_right4c = chain_right_right4a.addOrReplaceChild("chain_right_right4c", CubeListBuilder.create()
				.texOffs(0, 51).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_right4d = chain_right_right4a.addOrReplaceChild("chain_right_right4d", CubeListBuilder.create()
				.texOffs(4, 51).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_right5a = chain_right_right4a.addOrReplaceChild("chain_right_right5a", CubeListBuilder.create()
				.texOffs(12, 51).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0016444577195458F, 0.0F));
		var chain_right_right5b = chain_right_right5a.addOrReplaceChild("chain_right_right5b", CubeListBuilder.create()
				.texOffs(20, 51).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_right5c = chain_right_right5a.addOrReplaceChild("chain_right_right5c", CubeListBuilder.create()
				.texOffs(24, 51).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_right5d = chain_right_right5a.addOrReplaceChild("chain_right_right5d", CubeListBuilder.create()
				.texOffs(28, 51).addBox(-0.5F, 0.0F, -1.5F, 1, 1, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
		var chain_right_right6a = chain_right_right5a.addOrReplaceChild("chain_right_right6a", CubeListBuilder.create()
				.texOffs(120, 51).addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 1.0016444577195458F, 0.0F));
		var chain_right_right6b = chain_right_right6a.addOrReplaceChild("chain_right_right6b", CubeListBuilder.create()
				.texOffs(36, 51).addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, -1.5F));
		var chain_right_right6c = chain_right_right6a.addOrReplaceChild("chain_right_right6c", CubeListBuilder.create()
				.texOffs(128, 51).addBox(-0.5F, 0.0F, -1.0F, 1, 4, 1),
			PartPose.offset(0.0F, 0.5F, 1.5F));
		var chain_right_right6d = chain_right_right6a.addOrReplaceChild("chain_right_right6d", CubeListBuilder.create()
				.texOffs(132, 51).addBox(-0.5F, 0.0F, -1.5F, 1, 2, 3),
			PartPose.offset(0.0F, 4.5F, 0.0F));
	}

	public void renderPlug(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		for (ModelPart part : this.plugParts) {
			part.render(stack, consumer, light, overlay, color);
		}
	}

	public void renderChains(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		for (ModelPart part : this.chainParts) {
			part.render(stack, consumer, light, overlay, color);
		}
	}
}
