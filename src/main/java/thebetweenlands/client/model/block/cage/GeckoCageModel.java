package thebetweenlands.client.model.block.cage;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class GeckoCageModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12),
			PartPose.offset(0.0F, -2.2F, 0.0F));

		partDefinition.addOrReplaceChild("corner_1", CubeListBuilder.create()
				.texOffs(60, 15)
				.addBox(-2.0F, -16.0F, -2.0F, 2, 16, 2),
			PartPose.offset(-6.0F, 0.0F, -6.0F));

		partDefinition.addOrReplaceChild("corner_2", CubeListBuilder.create()
				.texOffs(69, 15)
				.addBox(-2.0F, -16.0F, 0.0F, 2, 16, 2),
			PartPose.offset(-6.0F, 0.0F, 6.0F));

		partDefinition.addOrReplaceChild("corner_3", CubeListBuilder.create()
				.texOffs(78, 15)
				.addBox(0.0F, -16.0F, 0.0F, 2, 16, 2),
			PartPose.offset(6.0F, 0.0F, 6.0F));

		partDefinition.addOrReplaceChild("corner_4", CubeListBuilder.create()
				.texOffs(87, 15)
				.addBox(0.0F, -16.0F, -2.0F, 2, 16, 2),
			PartPose.offset(6.0F, 0.0F, -6.0F));

		var topBase = base.addOrReplaceChild("top_base", CubeListBuilder.create()
				.texOffs(49, 0)
				.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12),
			PartPose.offset(0.0F, -11.35F, 0.0F));

		var bottomEdge1 = base.addOrReplaceChild("bottom_edge_1", CubeListBuilder.create()
			.texOffs(0, 15)
			.addBox(-6.0F, 0.0F, -2.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, -6.0F, 0.27314402793711257F, 0.0F, 0.0F));

		bottomEdge1.addOrReplaceChild("bottom_raster_1", CubeListBuilder.create()
			.texOffs(0, 35)
			.addBox(-6.01F, -5.0F, 0.0F, 12, 5, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.136659280431156F, 0.0F, 0.0F));

		var topEdge1 = topBase.addOrReplaceChild("top_edge_1", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-6.0F, -2.0F, -2.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, -0.27314402793711257F, 0.0F, 0.0F));

		topEdge1.addOrReplaceChild("top_raster_1", CubeListBuilder.create()
				.texOffs(54, 35)
				.addBox(-5.99F, 0.0F, 0.0F, 12, 5, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.136659280431156F, 0.0F, 0.0F));

		var bottomEdge2 = base.addOrReplaceChild("bottom_edge_2", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-2.0F, 0.0F, -6.0F, 2, 2, 12),
			PartPose.offsetAndRotation(-6.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		bottomEdge2.addOrReplaceChild("bottom_raster_2", CubeListBuilder.create()
				.texOffs(0, 42)
				.addBox(0.0F, -5.0F, -6.01F, 1, 5, 12),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		var topEdge2 = topBase.addOrReplaceChild("top_edge_2", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-2.0F, -2.0F, -6.0F, 2, 2, 12),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		topEdge2.addOrReplaceChild("top_raster_2", CubeListBuilder.create()
				.texOffs(54, 42)
				.addBox(0.0F, 0.0F, -5.99F, 1, 5, 12),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		var bottomEdge3 = base.addOrReplaceChild("bottom_edge_3", CubeListBuilder.create()
				.texOffs(29, 15)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 6.0F, -0.27314402793711257F, 0.0F, 0.0F));

		bottomEdge3.addOrReplaceChild("bottom_raster_3", CubeListBuilder.create()
				.texOffs(27, 35)
				.addBox(-6.01F, -5.0F, -1.0F, 12, 5, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.136659280431156F, 0.0F, 0.0F));

		var topEdge3 = topBase.addOrReplaceChild("top_edge_3", CubeListBuilder.create()
				.texOffs(29, 15)
				.addBox(-6.0F, -2.0F, 0.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.27314402793711257F, 0.0F, 0.0F));

		topEdge3.addOrReplaceChild("top_raster_3", CubeListBuilder.create()
				.texOffs(81, 35)
				.addBox(-5.99F, 0.0F, -1.0F, 12, 5, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.136659280431156F, 0.0F, 0.0F));

		var bottomEdge4 = base.addOrReplaceChild("bottom_edge_4", CubeListBuilder.create()
				.texOffs(29, 20)
				.addBox(0.0F, 0.0F, -6.0F, 2, 2, 12),
			PartPose.offsetAndRotation(6.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		bottomEdge4.addOrReplaceChild("bottom_raster_4", CubeListBuilder.create()
				.texOffs(27, 42)
				.addBox(-1.0F, -5.0F, -6.01F, 1, 5, 12),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F));

		var topEdge4 = topBase.addOrReplaceChild("top_edge_4", CubeListBuilder.create()
				.texOffs(29, 20)
				.addBox(0.0F, -2.0F, -6.0F, 2, 2, 12),
			PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		topEdge4.addOrReplaceChild("top_raster_4", CubeListBuilder.create()
				.texOffs(81, 42)
				.addBox(-1.0F, 0.0F, -5.99F, 1, 5, 12),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
