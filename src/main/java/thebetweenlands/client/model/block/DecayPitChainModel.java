package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DecayPitChainModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("fancy_bottomchain_front_left", CubeListBuilder.create()
				.texOffs(0, 38).addBox(0.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(1.5F, 24.0F, -5.0F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_topchain_back_right", CubeListBuilder.create()
				.texOffs(15, 42).addBox(-1.0F, 0.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-1.5F, 8.0F, 5.0F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_top_cornerpiece_bottomback", CubeListBuilder.create()
				.texOffs(41, 27).addBox(-1.5F, -3.0F, 2.0F, 3, 1, 2),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_rightpiece", CubeListBuilder.create()
				.texOffs(13, 0).addBox(2.0F, -5.0F, -1.5F, 3, 10, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_top_cornerpiece_bottomfront", CubeListBuilder.create()
				.texOffs(41, 23).addBox(-1.5F, -3.0F, -4.0F, 3, 1, 2),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_bottomchain_back_left", CubeListBuilder.create()
				.texOffs(10, 38).addBox(0.0F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(1.5F, 24.0F, 5.0F, 0.0F, 0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_top_backpiece", CubeListBuilder.create()
				.texOffs(28, 23).addBox(-1.5F, -8.0F, 2.0F, 3, 5, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_cornerpiece_topleft", CubeListBuilder.create()
				.texOffs(41, 0).addBox(2.0F, -6.0F, -1.5F, 2, 1, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_cornerpiece_topright", CubeListBuilder.create()
				.texOffs(52, 0).addBox(-4.0F, -6.0F, -1.5F, 2, 1, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_cornerpiece_bottomleft", CubeListBuilder.create()
				.texOffs(41, 5).addBox(2.0F, 5.0F, -1.5F, 2, 1, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_top_bottompiece", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-1.5F, -4.0F, -2.0F, 3, 3, 4),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_topchain_front_right", CubeListBuilder.create()
				.texOffs(5, 42).addBox(-1.0F, 0.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-1.5F, 8.0F, -5.0F, 0.0F, 0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_bottom_cornerpiece_topfront", CubeListBuilder.create()
				.texOffs(41, 14).addBox(-1.5F, 2.0F, -4.0F, 3, 1, 2),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_midchain_left_front", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.0F, -2.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(5.0F, 16.0F, -1.5F, 0.0F, 0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_bottomchain_back_right", CubeListBuilder.create()
				.texOffs(15, 38).addBox(-1.0F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-1.5F, 24.0F, 5.0F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_cornerpiece_bottomright", CubeListBuilder.create()
				.texOffs(52, 5).addBox(-4.0F, 5.0F, -1.5F, 2, 1, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_bottom_cornerpiece_topback", CubeListBuilder.create()
				.texOffs(41, 18).addBox(-1.5F, 2.0F, 2.0F, 3, 1, 2),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_leftpiece", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, -5.0F, -1.5F, 3, 10, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_bottompiece", CubeListBuilder.create()
				.texOffs(26, 7).addBox(-2.0F, 4.0F, -1.5F, 4, 3, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_topchain_front_left", CubeListBuilder.create()
				.texOffs(0, 42).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(1.5F, 8.0F, -5.0F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_bottom_frontpiece", CubeListBuilder.create()
				.texOffs(15, 14).addBox(-1.5F, 3.0F, 2.0F, 3, 5, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_bottomchain_front_right", CubeListBuilder.create()
				.texOffs(5, 38).addBox(-1.0F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(-1.5F, 24.0F, -5.0F, 0.0F, 0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_bottom_backpiece", CubeListBuilder.create()
				.texOffs(28, 14).addBox(-1.5F, 3.0F, -5.0F, 3, 5, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("chain_bottom_toppiece", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-1.5F, 1.0F, -2.0F, 3, 3, 4),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_midchain_left_back", CubeListBuilder.create()
				.texOffs(5, 32).addBox(-1.0F, -2.0F, 0.0F, 1, 4, 1),
			PartPose.offsetAndRotation(5.0F, 16.0F, 1.5F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_midchain_right_back", CubeListBuilder.create()
				.texOffs(15, 32).addBox(0.0F, -2.0F, 0.0F, 1, 4, 1),
			PartPose.offsetAndRotation(-5.0F, 16.0F, 1.5F, 0.0F, 0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_top_frontpiece", CubeListBuilder.create()
				.texOffs(15, 23).addBox(-1.5F, -8.0F, -5.0F, 3, 5, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_midchain_right_front", CubeListBuilder.create()
				.texOffs(10, 32).addBox(0.0F, -2.0F, -1.0F, 1, 4, 1),
			PartPose.offsetAndRotation(-5.0F, 16.0F, -1.5F, 0.0F, -0.091106186954104F, 0.0F));
		partDefinition.addOrReplaceChild("chain_mid_toppiece", CubeListBuilder.create()
				.texOffs(26, 0).addBox(-2.0F, -7.0F, -1.5F, 4, 3, 3),
			PartPose.offset(0.0F, 16.0F, 0.0F));
		partDefinition.addOrReplaceChild("fancy_topchain_back_left", CubeListBuilder.create()
				.texOffs(10, 42).addBox(0.0F, 0.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(1.5F, 8.0F, 5.0F, 0.0F, 0.091106186954104F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
