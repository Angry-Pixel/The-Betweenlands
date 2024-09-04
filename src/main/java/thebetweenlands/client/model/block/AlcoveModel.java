package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AlcoveModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var backWall = partDefinition.addOrReplaceChild("back_wall", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8.0F, -16.0F, 0.0F, 16, 16, 6),
			PartPose.offset(0.0F, 0.0F, 2.0F));

		var top = backWall.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(0, 23)
				.addBox(-8.0F, 0.0F, -10.0F, 16, 4, 10),
			PartPose.offset(0.0F, -16.0F, 0.0F));

		var left = backWall.addOrReplaceChild("left_1", CubeListBuilder.create()
				.texOffs(0, 43)
				.addBox(-1.0F, -12.0F, -2.0F, 2, 12, 2),
			PartPose.offset(7.0F, 0.0F, 0.0F));

		var left2 = left.addOrReplaceChild("left_2", CubeListBuilder.create()
				.texOffs(9, 43)
				.addBox(-1.0F, 0.0F, -8.0F, 2, 2, 8),
			PartPose.offset(0.0F, -12.0F, -2.0F));

		left2.addOrReplaceChild("left_3", CubeListBuilder.create()
				.texOffs(30, 43)
				.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(0.0F, 2.0F, 0.0F));

		var right = backWall.addOrReplaceChild("right_1", CubeListBuilder.create()
				.texOffs(0, 60)
				.addBox(-1.0F, -12.0F, -2.0F, 2, 12, 2),
			PartPose.offset(-7.0F, 0.0F, 0.0F));

		var right2 = right.addOrReplaceChild("right_2", CubeListBuilder.create()
				.texOffs(9, 60)
				.addBox(-1.0F, 0.0F, -8.0F, 2, 2, 8),
			PartPose.offset(0.0F, -12.0F, -2.0F));

		right2.addOrReplaceChild("right_3", CubeListBuilder.create()
				.texOffs(30, 60)
				.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(0.0F, 2.0F, 0.0F));

		var back = top.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox(-6.0F, 0.0F, -2.0F, 12, 2, 2),
			PartPose.offset(0.0F, 4.0F, 0.0F));

		back.addOrReplaceChild("back_left", CubeListBuilder.create()
				.texOffs(29, 38)
				.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(5.0F, 2.0F, 0.0F));

		back.addOrReplaceChild("back_right", CubeListBuilder.create()
				.texOffs(38, 38)
				.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2),
			PartPose.offset(-5.0F, 2.0F, 0.0F));

		var outcrop = partDefinition.addOrReplaceChild("outcrop_a", CubeListBuilder.create()
			.texOffs(60, 0)
			.addBox(-1.5F, 0.0F, -2.0F, 3, 5, 2),
			PartPose.offsetAndRotation(0.0F, -16.0F, -8.0F, 0.091106186954104F, 0.0F, 0.0F));

		outcrop.addOrReplaceChild("outcrop_b", CubeListBuilder.create()
				.texOffs(71, 0)
				.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 1),
			PartPose.offsetAndRotation(0.0F, 5.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		var candle1 = partDefinition.addOrReplaceChild("candle_1", CubeListBuilder.create()
			.texOffs(60, 8)
			.addBox(-1.0F, -5.0F, -1.0F, 2, 5, 2),
			PartPose.offsetAndRotation(5.0F, 0.0F, -1.5F, 0.0F, -0.18203784098300857F, 0.0F));

		candle1.addOrReplaceChild("wick_1", CubeListBuilder.create()
			.texOffs(69, 8)
			.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.18203784098300857F, 0.5009094953223726F, -0.136659280431156F));

		candle1.addOrReplaceChild("drip_1", CubeListBuilder.create()
				.texOffs(72, 8)
				.addBox(-2.5F, 0.0F, -2.5F, 5, 0, 5),
			PartPose.offset(0.0F, -0.03F, 0.0F));

		var candle2 = partDefinition.addOrReplaceChild("candle_2", CubeListBuilder.create()
				.texOffs(60, 16)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(6.5F, 0.0F, -4.0F, 0.0F, -0.5918411493512771F, 0.0F));

		candle2.addOrReplaceChild("wick_2", CubeListBuilder.create()
				.texOffs(69, 16)
				.addBox(-0.5F, -1.0F, 0.0F, 1, 2, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, -0.31869712141416456F, 0.22759093446006054F));

		candle2.addOrReplaceChild("drip_2", CubeListBuilder.create()
				.texOffs(72, 16)
				.addBox(-2.0F, 0.0F, -2.0F, 4, 0, 4),
			PartPose.offset(0.0F, -0.02F, 0.0F));

		partDefinition.addOrReplaceChild("cobweb_1", CubeListBuilder.create()
				.texOffs(60, 22)
				.addBox(-3.0F, -6.0F, 0.0F, 8, 6, 0),
			PartPose.offsetAndRotation(-3.5F, 0.0F, -1.6F, -0.8196066167365371F, -0.4553564018453205F, 0.0F));

		var web = partDefinition.addOrReplaceChild("cobweb_2", CubeListBuilder.create()
				.texOffs(60, 29)
				.addBox(-4.0F, 0.0F, 0.0F, 7, 5, 0),
			PartPose.offsetAndRotation(-2.8F, -12.0F, -2.7F, 0.6373942428283291F, -0.18203784098300857F, -0.22759093446006054F));

		web.addOrReplaceChild("cobweb_2_2", CubeListBuilder.create()
				.texOffs(75, 29)
				.addBox(-4.0F, 0.0F, 0.0F, 7, 3, 0),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.091106186954104F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
