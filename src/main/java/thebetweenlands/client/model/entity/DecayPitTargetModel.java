package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DecayPitTargetModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var target_base = partDefinition.addOrReplaceChild("target_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, -4.0F, -9.0F, 18, 8, 18),
			PartPose.offset(0.0F, 8.0F, 0.0F));
		var target_base_upper = target_base.addOrReplaceChild("target_base_upper", CubeListBuilder.create()
				.texOffs(0, 58).addBox(-11.0F, -8.0F, -11.0F, 22, 8, 22),
			PartPose.offset(0.0F, -4.0F, 0.0F));
		var target_base_lower = target_base.addOrReplaceChild("target_base_lower", CubeListBuilder.create()
				.texOffs(0, 27).addBox(-11.0F, 0.0F, -11.0F, 22, 8, 22),
			PartPose.offset(0.0F, 4.0F, 0.0F));


		var cog2_1 = target_base.addOrReplaceChild("cog2_1", CubeListBuilder.create()
				.texOffs(89, 98).addBox(-1.49F, 4.5F, -2.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 13.5F, 0.0F, 0.0F, 0.0F, 1.5707963267948966F));
		var cog2_2 = cog2_1.addOrReplaceChild("cog2_2", CubeListBuilder.create()
				.texOffs(89, 107).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(-1.5F, 7.5F, -2.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_3 = cog2_2.addOrReplaceChild("cog2_3", CubeListBuilder.create()
				.texOffs(89, 115).addBox(0.01F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_4 = cog2_3.addOrReplaceChild("cog2_4", CubeListBuilder.create()
				.texOffs(74, 107).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_5 = cog2_4.addOrReplaceChild("cog2_5", CubeListBuilder.create()
				.texOffs(74, 115).addBox(0.01F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_6 = cog2_5.addOrReplaceChild("cog2_6", CubeListBuilder.create()
				.texOffs(104, 98).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_7 = cog2_6.addOrReplaceChild("cog2_7", CubeListBuilder.create()
				.texOffs(104, 107).addBox(0.01F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_8 = cog2_7.addOrReplaceChild("cog2_8", CubeListBuilder.create()
				.texOffs(104, 116).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_9 = cog2_8.addOrReplaceChild("cog2_9", CubeListBuilder.create()
				.texOffs(0, 27).addBox(0.01F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_10 = cog2_9.addOrReplaceChild("cog2_10", CubeListBuilder.create()
				.texOffs(0, 36).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_11 = cog2_10.addOrReplaceChild("cog2_11", CubeListBuilder.create()
				.texOffs(0, 58).addBox(0.01F, -3.0F, -4.0F, 3, 4, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_12 = cog2_11.addOrReplaceChild("cog2_12", CubeListBuilder.create()
				.texOffs(0, 67).addBox(0.0F, -3.0F, -4.0F, 3, 3, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.5235987755982988F, 0.0F, 0.0F));
		var cog2_fill = cog2_1.addOrReplaceChild("cog2_fill", CubeListBuilder.create()
				.texOffs(49, 0).addBox(-1.0F, -4.5F, -4.5F, 2, 9, 9),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		var target_front_base = target_base.addOrReplaceChild("target_front_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offset(0.0F, 0.0F, -11.0F));
		var front_plate_uppermid = target_front_base.addOrReplaceChild("front_plate_uppermid", CubeListBuilder.create()
				.texOffs(0, 89).addBox(-10.0F, -10.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var front_plate_upperright1 = front_plate_uppermid.addOrReplaceChild("front_plate_upperright1", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-4.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(-10.0F, 2.0F, -3.0F, 0.0F, 0.18203784098300857F, 0.0F));
		var front_plate_upperleft1 = front_plate_uppermid.addOrReplaceChild("front_plate_upperleft1", CubeListBuilder.create()
				.texOffs(89, 0).addBox(0.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(10.0F, 2.0F, -3.0F, 0.0F, -0.18203784098300857F, 0.0F));
		var front_plate_lower1 = target_front_base.addOrReplaceChild("front_plate_lower1", CubeListBuilder.create()
				.texOffs(0, 104).addBox(-10.0F, -2.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, 6.0F, -10.0F));
		var front_beam_top_right = target_front_base.addOrReplaceChild("front_beam_top_right", CubeListBuilder.create()
				.texOffs(47, 104).addBox(-4.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, -6.0F, 0.0F));
		var front_beam_bottom_left = target_front_base.addOrReplaceChild("front_beam_bottom_left", CubeListBuilder.create()
				.texOffs(89, 30).addBox(0.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, 6.0F, 0.0F));
		var front_beam_bottom_right = target_front_base.addOrReplaceChild("front_beam_bottom_right", CubeListBuilder.create()
				.texOffs(89, 45).addBox(-4.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, 6.0F, 0.0F));
		var front_beam_top_left = target_front_base.addOrReplaceChild("front_beam_top_left", CubeListBuilder.create()
				.texOffs(47, 89).addBox(0.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, -6.0F, 0.0F));
		var front_lense_main = target_front_base.addOrReplaceChild("front_lense_main", CubeListBuilder.create()
				.texOffs(89, 70).addBox(-8.0F, -6.0F, -3.0F, 16, 12, 3),
			PartPose.offset(0.0F, 0.0F, -13.0F));
		var front_lense_edge_corner2 = target_front_base.addOrReplaceChild("front_lense_edge_corner2", CubeListBuilder.create()
				.texOffs(73, 63).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, 8.0F, -13.0F));
		var front_lense_edge_left = target_front_base.addOrReplaceChild("front_lense_edge_left", CubeListBuilder.create()
				.texOffs(73, 0).addBox(0.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(8.0F, 0.0F, -13.0F));
		var front_lense_edge_right = target_front_base.addOrReplaceChild("front_lense_edge_right", CubeListBuilder.create()
				.texOffs(73, 21).addBox(-2.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(-8.0F, 0.0F, -13.0F));
		var front_lense_edge_lower = target_front_base.addOrReplaceChild("front_lense_edge_lower", CubeListBuilder.create()
				.texOffs(89, 65).addBox(-8.0F, 0.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, -15.0F, 0.091106186954104F, 0.0F, 0.0F));
		var front_lense_edge_corner1 = target_front_base.addOrReplaceChild("front_lense_edge_corner1", CubeListBuilder.create()
				.texOffs(73, 58).addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, -8.0F, -13.0F));
		var front_lense_edge_upper = target_front_base.addOrReplaceChild("front_lense_edge_upper", CubeListBuilder.create()
				.texOffs(89, 60).addBox(-8.0F, -2.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -8.0F, -15.0F, -0.091106186954104F, 0.0F, 0.0F));
		var front_lense_upper = target_front_base.addOrReplaceChild("front_lense_upper", CubeListBuilder.create()
				.texOffs(89, 86).addBox(-6.0F, -6.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, -2.0F, -13.0F));
		var front_lense_edge_corner4 = target_front_base.addOrReplaceChild("front_lense_edge_corner4", CubeListBuilder.create()
				.texOffs(73, 73).addBox(0.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, -8.0F, -13.0F));
		var front_lense_lower = target_front_base.addOrReplaceChild("front_lense_lower", CubeListBuilder.create()
				.texOffs(89, 92).addBox(-6.0F, 0.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, 6.0F, -13.0F));
		var front_lense_edge_corner3 = target_front_base.addOrReplaceChild("front_lense_edge_corner3", CubeListBuilder.create()
				.texOffs(73, 68).addBox(0.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, 8.0F, -13.0F));
		var arrow_pane1 = target_front_base.addOrReplaceChild("arrow_pane1", CubeListBuilder.create()
				.texOffs(11, -16).addBox(0.0F, -2.5F, -16.0F, 0, 5, 16),
			PartPose.offsetAndRotation(-4.0F, -5.0F, -15.0F, 0.136659280431156F, 0.4553564018453205F, 0.4553564018453205F));
		var arrow_pane2 = arrow_pane1.addOrReplaceChild("arrow_pane2", CubeListBuilder.create()
				.texOffs(-16, 0).addBox(-2.5F, 0.0F, -16.0F, 5, 0, 16),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var arrow_pane3 = arrow_pane1.addOrReplaceChild("arrow_pane3", CubeListBuilder.create()
				.texOffs(11, 7).addBox(-2.5F, -2.5F, -16.0F, 5, 5, 0),
			PartPose.offset(0.0F, 0.0F, 0.0F));


		var target_right_base = target_base.addOrReplaceChild("target_right_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offsetAndRotation(-11.0F, 0.0F, 0.0F, 0.0F, 1.5707963267948966F, 0.0F));
		var right_plate_uppermid = target_right_base.addOrReplaceChild("right_plate_uppermid", CubeListBuilder.create()
				.texOffs(0, 89).addBox(-10.0F, -10.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var right_plate_upperright1 = right_plate_uppermid.addOrReplaceChild("right_plate_upperright1", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-4.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(-10.0F, 2.0F, -3.0F, 0.0F, 0.18203784098300857F, 0.0F));
		var right_plate_upperleft1 = right_plate_uppermid.addOrReplaceChild("right_plate_upperleft1", CubeListBuilder.create()
				.texOffs(89, 0).addBox(0.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(10.0F, 2.0F, -3.0F, 0.0F, -0.18203784098300857F, 0.0F));
		var right_plate_lower1 = target_right_base.addOrReplaceChild("right_plate_lower1", CubeListBuilder.create()
				.texOffs(0, 104).addBox(-10.0F, -2.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, 6.0F, -10.0F));
		var right_beam_top_right = target_right_base.addOrReplaceChild("right_beam_top_right", CubeListBuilder.create()
				.texOffs(47, 104).addBox(-4.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, -6.0F, 0.0F));
		var right_beam_top_left = target_right_base.addOrReplaceChild("right_beam_top_left", CubeListBuilder.create()
				.texOffs(47, 89).addBox(0.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, -6.0F, 0.0F));
		var right_beam_bottom_left = target_right_base.addOrReplaceChild("right_beam_bottom_left", CubeListBuilder.create()
				.texOffs(89, 30).addBox(0.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, 6.0F, 0.0F));
		var right_beam_bottom_right = target_right_base.addOrReplaceChild("right_beam_bottom_right", CubeListBuilder.create()
				.texOffs(89, 45).addBox(-4.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, 6.0F, 0.0F));
		var right_lense_lower = target_right_base.addOrReplaceChild("right_lense_lower", CubeListBuilder.create()
				.texOffs(89, 92).addBox(-6.0F, 0.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, 6.0F, -13.0F));
		var right_lense_edge_upper = target_right_base.addOrReplaceChild("right_lense_edge_upper", CubeListBuilder.create()
				.texOffs(89, 60).addBox(-8.0F, -2.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -8.0F, -15.0F, -0.091106186954104F, 0.0F, 0.0F));
		var right_lense_edge_corner2 = target_right_base.addOrReplaceChild("right_lense_edge_corner2", CubeListBuilder.create()
				.texOffs(73, 63).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, 8.0F, -13.0F));
		var right_lense_edge_right = target_right_base.addOrReplaceChild("right_lense_edge_right", CubeListBuilder.create()
				.texOffs(73, 21).addBox(-2.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(-8.0F, 0.0F, -13.0F));
		var right_lense_edge_corner4 = target_right_base.addOrReplaceChild("right_lense_edge_corner4", CubeListBuilder.create()
				.texOffs(73, 73).addBox(0.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, -8.0F, -13.0F));
		var right_lense_main = target_right_base.addOrReplaceChild("right_lense_main", CubeListBuilder.create()
				.texOffs(89, 70).addBox(-8.0F, -6.0F, -3.0F, 16, 12, 3),
			PartPose.offset(0.0F, 0.0F, -13.0F));
		var right_lense_upper = target_right_base.addOrReplaceChild("right_lense_upper", CubeListBuilder.create()
				.texOffs(89, 86).addBox(-6.0F, -6.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, -2.0F, -13.0F));
		var right_lense_edge_left = target_right_base.addOrReplaceChild("right_lense_edge_left", CubeListBuilder.create()
				.texOffs(73, 0).addBox(0.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(8.0F, 0.0F, -13.0F));
		var right_lense_edge_corner3 = target_right_base.addOrReplaceChild("right_lense_edge_corner3", CubeListBuilder.create()
				.texOffs(73, 68).addBox(0.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, 8.0F, -13.0F));
		var right_lense_edge_corner1 = target_right_base.addOrReplaceChild("right_lense_edge_corner1", CubeListBuilder.create()
				.texOffs(73, 58).addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, -8.0F, -13.0F));
		var right_lense_edge_lower = target_right_base.addOrReplaceChild("right_lense_edge_lower", CubeListBuilder.create()
				.texOffs(89, 65).addBox(-8.0F, 0.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, -15.0F, 0.091106186954104F, 0.0F, 0.0F));


		var target_back_base = target_base.addOrReplaceChild("target_back_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 11.0F, 0.0F, 3.141592653589793F, 0.0F));
		var back_plate_lower1 = target_back_base.addOrReplaceChild("back_plate_lower1", CubeListBuilder.create()
				.texOffs(0, 104).addBox(-10.0F, -2.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, 6.0F, -10.0F));
		var back_plate_uppermid = target_back_base.addOrReplaceChild("back_plate_uppermid", CubeListBuilder.create()
				.texOffs(0, 89).addBox(-10.0F, -10.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var back_plate_upperright1 = back_plate_uppermid.addOrReplaceChild("back_plate_upperright1", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-4.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(-10.0F, 2.0F, -3.0F, 0.0F, 0.18203784098300857F, 0.0F));
		var back_plate_upperleft1 = back_plate_uppermid.addOrReplaceChild("back_plate_upperleft1", CubeListBuilder.create()
				.texOffs(89, 0).addBox(0.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(10.0F, 2.0F, -3.0F, 0.0F, -0.18203784098300857F, 0.0F));
		var back_beam_top_right = target_back_base.addOrReplaceChild("back_beam_top_right", CubeListBuilder.create()
				.texOffs(47, 104).addBox(-4.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, -6.0F, 0.0F));
		var back_beam_top_left = target_back_base.addOrReplaceChild("back_beam_top_left", CubeListBuilder.create()
				.texOffs(47, 89).addBox(0.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, -6.0F, 0.0F));
		var back_beam_bottom_right = target_back_base.addOrReplaceChild("back_beam_bottom_right", CubeListBuilder.create()
				.texOffs(89, 45).addBox(-4.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, 6.0F, 0.0F));
		var back_beam_bottom_left = target_back_base.addOrReplaceChild("back_beam_bottom_left", CubeListBuilder.create()
				.texOffs(89, 30).addBox(0.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, 6.0F, 0.0F));
		var back_lense_upper = target_back_base.addOrReplaceChild("back_lense_upper", CubeListBuilder.create()
				.texOffs(89, 86).addBox(-6.0F, -6.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, -2.0F, -13.0F));
		var back_lense_edge_corner4 = target_back_base.addOrReplaceChild("back_lense_edge_corner4", CubeListBuilder.create()
				.texOffs(73, 73).addBox(0.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, -8.0F, -13.0F));
		var back_lense_lower = target_back_base.addOrReplaceChild("back_lense_lower", CubeListBuilder.create()
				.texOffs(89, 92).addBox(-6.0F, 0.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, 6.0F, -13.0F));
		var back_lense_edge_right = target_back_base.addOrReplaceChild("back_lense_edge_right", CubeListBuilder.create()
				.texOffs(73, 21).addBox(-2.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(-8.0F, 0.0F, -13.0F));
		var back_lense_edge_lower = target_back_base.addOrReplaceChild("back_lense_edge_lower", CubeListBuilder.create()
				.texOffs(89, 65).addBox(-8.0F, 0.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, -15.0F, 0.091106186954104F, 0.0F, 0.0F));
		var back_lense_main = target_back_base.addOrReplaceChild("back_lense_main", CubeListBuilder.create()
				.texOffs(89, 70).addBox(-8.0F, -6.0F, -3.0F, 16, 12, 3),
			PartPose.offset(0.0F, 0.0F, -13.0F));
		var back_lense_edge_corner2 = target_back_base.addOrReplaceChild("back_lense_edge_corner2", CubeListBuilder.create()
				.texOffs(73, 63).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, 8.0F, -13.0F));
		var back_lense_edge_upper = target_back_base.addOrReplaceChild("back_lense_edge_upper", CubeListBuilder.create()
				.texOffs(89, 60).addBox(-8.0F, -2.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -8.0F, -15.0F, -0.091106186954104F, 0.0F, 0.0F));
		var back_lense_edge_corner1 = target_back_base.addOrReplaceChild("back_lense_edge_corner1", CubeListBuilder.create()
				.texOffs(73, 58).addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, -8.0F, -13.0F));
		var back_lense_edge_corner3 = target_back_base.addOrReplaceChild("back_lense_edge_corner3", CubeListBuilder.create()
				.texOffs(73, 68).addBox(0.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, 8.0F, -13.0F));
		var back_lense_edge_left = target_back_base.addOrReplaceChild("back_lense_edge_left", CubeListBuilder.create()
				.texOffs(73, 0).addBox(0.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(8.0F, 0.0F, -13.0F));


		var target_left_base = target_base.addOrReplaceChild("target_left_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0),
			PartPose.offsetAndRotation(11.0F, 0.0F, 0.0F, 0.0F, -1.5707963267948966F, 0.0F));
		var left_plate_uppermid = target_left_base.addOrReplaceChild("left_plate_uppermid", CubeListBuilder.create()
				.texOffs(0, 89).addBox(-10.0F, -10.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, -6.0F, -10.0F));
		var left_plate_upperleft1 = left_plate_uppermid.addOrReplaceChild("left_plate_upperleft1", CubeListBuilder.create()
				.texOffs(89, 0).addBox(0.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(10.0F, 2.0F, -3.0F, 0.0F, -0.18203784098300857F, 0.0F));
		var left_plate_upperright1 = left_plate_uppermid.addOrReplaceChild("left_plate_upperright1", CubeListBuilder.create()
				.texOffs(104, 0).addBox(-4.0F, -8.0F, 0.0F, 4, 24, 3),
			PartPose.offsetAndRotation(-10.0F, 2.0F, -3.0F, 0.0F, 0.18203784098300857F, 0.0F));
		var left_plate_lower1 = target_left_base.addOrReplaceChild("left_plate_lower1", CubeListBuilder.create()
				.texOffs(0, 104).addBox(-10.0F, -2.0F, -3.0F, 20, 12, 3),
			PartPose.offset(0.0F, 6.0F, -10.0F));
		var left_beam_bottom_right = target_left_base.addOrReplaceChild("left_beam_bottom_right", CubeListBuilder.create()
				.texOffs(89, 45).addBox(-4.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, 6.0F, 0.0F));
		var left_beam_bottom_left = target_left_base.addOrReplaceChild("left_beam_bottom_left", CubeListBuilder.create()
				.texOffs(89, 30).addBox(0.0F, 0.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, 6.0F, 0.0F));
		var left_beam_top_left = target_left_base.addOrReplaceChild("left_beam_top_left", CubeListBuilder.create()
				.texOffs(47, 89).addBox(0.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(3.0F, -6.0F, 0.0F));
		var left_beam_top_right = target_left_base.addOrReplaceChild("left_beam_top_right", CubeListBuilder.create()
				.texOffs(47, 104).addBox(-4.0F, -4.0F, -10.0F, 4, 4, 10),
			PartPose.offset(-3.0F, -6.0F, 0.0F));
		var left_lense_edge_upper = target_left_base.addOrReplaceChild("left_lense_edge_upper", CubeListBuilder.create()
				.texOffs(89, 60).addBox(-8.0F, -2.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, -8.0F, -15.0F, -0.091106186954104F, 0.0F, 0.0F));
		var left_lense_edge_right = target_left_base.addOrReplaceChild("left_lense_edge_right", CubeListBuilder.create()
				.texOffs(73, 21).addBox(-2.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(-8.0F, 0.0F, -13.0F));
		var left_lense_edge_left = target_left_base.addOrReplaceChild("left_lense_edge_left", CubeListBuilder.create()
				.texOffs(73, 0).addBox(0.0F, -8.0F, -2.0F, 2, 16, 4),
			PartPose.offset(8.0F, 0.0F, -13.0F));
		var left_lense_edge_corner3 = target_left_base.addOrReplaceChild("left_lense_edge_corner3", CubeListBuilder.create()
				.texOffs(73, 68).addBox(0.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, 8.0F, -13.0F));
		var left_lense_edge_lower = target_left_base.addOrReplaceChild("left_lense_edge_lower", CubeListBuilder.create()
				.texOffs(89, 65).addBox(-8.0F, 0.0F, 0.0F, 16, 2, 2),
			PartPose.offsetAndRotation(0.0F, 8.0F, -15.0F, 0.091106186954104F, 0.0F, 0.0F));
		var left_lense_edge_corner4 = target_left_base.addOrReplaceChild("left_lense_edge_corner4", CubeListBuilder.create()
				.texOffs(73, 73).addBox(0.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-8.0F, -8.0F, -13.0F));
		var left_lense_edge_corner1 = target_left_base.addOrReplaceChild("left_lense_edge_corner1", CubeListBuilder.create()
				.texOffs(73, 58).addBox(-2.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, -8.0F, -13.0F));
		var left_lense_edge_corner2 = target_left_base.addOrReplaceChild("left_lense_edge_corner2", CubeListBuilder.create()
				.texOffs(73, 63).addBox(-2.0F, -2.0F, -2.0F, 2, 2, 2),
			PartPose.offset(8.0F, 8.0F, -13.0F));
		var left_lense_main = target_left_base.addOrReplaceChild("left_lense_main", CubeListBuilder.create()
				.texOffs(89, 70).addBox(-8.0F, -6.0F, -3.0F, 16, 12, 3),
			PartPose.offset(0.0F, 0.0F, -13.0F));
		var left_lense_lower = target_left_base.addOrReplaceChild("left_lense_lower", CubeListBuilder.create()
				.texOffs(89, 92).addBox(-6.0F, 0.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, 6.0F, -13.0F));
		var left_lense_upper = target_left_base.addOrReplaceChild("left_lense_upper", CubeListBuilder.create()
				.texOffs(89, 86).addBox(-6.0F, -6.0F, -3.0F, 12, 2, 3),
			PartPose.offset(0.0F, -2.0F, -13.0F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
