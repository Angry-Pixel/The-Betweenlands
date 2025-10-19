package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WaterFilterModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var barrelcon1a = partDefinition.addOrReplaceChild("barrelcon1a", CubeListBuilder.create()
				.texOffs(68, 65).addBox(-3.0F, -1.0F, -1.0F, 6, 2, 2),
			PartPose.offset(0.0F, 0.0F, -3.0F));
		var barrelcon1b = barrelcon1a.addOrReplaceChild("barrelcon1b", CubeListBuilder.create()
				.texOffs(34, 65).addBox(-3.001F, -2.0F, -2.0F, 6, 2, 2),
			PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.1745F, 0.0F, 0.0F));

		var barrelcon2a = partDefinition.addOrReplaceChild("barrelcon2a", CubeListBuilder.create()
				.texOffs(51, 65).addBox(-3.0F, -1.0F, -1.0F, 6, 2, 2),
			PartPose.offset(0.0F, 0.0F, 3.0F));
		var barrelcon2b = barrelcon1b.addOrReplaceChild("barrelcon2b", CubeListBuilder.create()
				.texOffs(17, 65).addBox(-3.001F, -2.0F, 0.0F, 6, 2, 2),
			PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -0.1745F, 0.0F, 0.0F));

		var barrelcon3a = partDefinition.addOrReplaceChild("barrelcon3a", CubeListBuilder.create()
				.texOffs(51, 56).addBox(-1.0F, -1.0F, -3.0F, 2, 2, 6),
			PartPose.offset(3.0F, 0.0F, 0.0F));
		var barrelcon3b = barrelcon3a.addOrReplaceChild("barrelcon3b", CubeListBuilder.create()
				.texOffs(17, 56).addBox(0.0F, -2.0F, -3.0F, 2, 2, 6),
			PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		var barrelcon4a = partDefinition.addOrReplaceChild("barrelcon4a", CubeListBuilder.create()
				.texOffs(34, 56).addBox(-1.0F, -1.0F, -3.0F, 2, 2, 6),
			PartPose.offset(-3.0F, 0.0F, 0.0F));
		var barrelcon4b = barrelcon4a.addOrReplaceChild("barrelcon4b", CubeListBuilder.create()
				.texOffs(0, 56).addBox(-2.0F, -2.0F, -3.0F, 2, 2, 6),
			PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		var support1a = partDefinition.addOrReplaceChild("support1a", CubeListBuilder.create()
				.texOffs(63, 70).addBox(-2.0F, -4.0F, -1.001F, 2, 4, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -3.0F, 0.0F, -0.7854F, 0.0F));
		var support1b = support1a.addOrReplaceChild("support1b", CubeListBuilder.create()
				.texOffs(18, 84).addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support1c = support1b.addOrReplaceChild("support1c", CubeListBuilder.create()
				.texOffs(9, 84).addBox(-2.0F, -3.0F, -1.001F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support1d = support1c.addOrReplaceChild("support1d", CubeListBuilder.create()
				.texOffs(0, 84).addBox(0.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support1e = support1d.addOrReplaceChild("support1e", CubeListBuilder.create()
				.texOffs(27, 70).addBox(0.0F, -5.0F, -1.001F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		var support2a = partDefinition.addOrReplaceChild("support2a", CubeListBuilder.create()
				.texOffs(54, 70).addBox(0.0F, -4.0F, -1.001F, 2, 4, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, -3.0F, 0.0F, 0.7854F, 0.0F));
		var support2b = support2a.addOrReplaceChild("support2b", CubeListBuilder.create()
				.texOffs(72, 78).addBox(0.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support2c = support2b.addOrReplaceChild("support2c", CubeListBuilder.create()
				.texOffs(63, 78).addBox(0.0F, -3.0F, -1.001F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support2d = support2c.addOrReplaceChild("support2d", CubeListBuilder.create()
				.texOffs(54, 78).addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(2.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support2e = support2d.addOrReplaceChild("support2e", CubeListBuilder.create()
				.texOffs(18, 70).addBox(-2.0F, -5.0F, -1.001F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		var support3a = partDefinition.addOrReplaceChild("support3a", CubeListBuilder.create()
				.texOffs(45, 70).addBox(-2.0F, -4.0F, -0.999F, 2, 4, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 3.0F, 0.0F, 0.7854F, 0.0F));
		var support3b = support3a.addOrReplaceChild("support3b", CubeListBuilder.create()
				.texOffs(45, 78).addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support3c = support3b.addOrReplaceChild("support3c", CubeListBuilder.create()
				.texOffs(36, 78).addBox(-2.0F, -3.0F, -0.999F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support3d = support3c.addOrReplaceChild("support3d", CubeListBuilder.create()
				.texOffs(27, 78).addBox(0.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support3e = support3d.addOrReplaceChild("support3e", CubeListBuilder.create()
				.texOffs(9, 70).addBox(0.0F, -5.0F, -0.999F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		var support4a = partDefinition.addOrReplaceChild("support4a", CubeListBuilder.create()
				.texOffs(36, 70).addBox(0.0F, -4.0F, -0.999F, 2, 4, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 3.0F, 0.0F, -0.7854F, 0.0F));
		var support4b = support4a.addOrReplaceChild("support4b", CubeListBuilder.create()
				.texOffs(18, 78).addBox(0.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support4c = support4b.addOrReplaceChild("support4c", CubeListBuilder.create()
				.texOffs(9, 78).addBox(0.0F, -3.0F, -0.999F, 2, 3, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
		var support4d = support4c.addOrReplaceChild("support4d", CubeListBuilder.create()
				.texOffs(0, 78).addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(2.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
		var support4e = support4d.addOrReplaceChild("support4e", CubeListBuilder.create()
				.texOffs(0, 70).addBox(-2.0F, -5.0F, -0.999F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		var filterbase = partDefinition.addOrReplaceChild("filterbase", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));
		var filterbase1 = filterbase.addOrReplaceChild("filterbase1", CubeListBuilder.create()
				.texOffs(0, 65).addBox(-3.0F, 0.0F, 0.0F, 6, 2, 2),
			PartPose.offset(0.0F, 0.0F, -4.0F));
		var filterbase2 = filterbase.addOrReplaceChild("filterbase2", CubeListBuilder.create()
				.texOffs(68, 56).addBox(-3.0F, 0.0F, -2.0F, 6, 2, 2),
			PartPose.offset(0.0F, 0.0F, 4.0F));
		var filterbase3 = filterbase.addOrReplaceChild("filterbase3", CubeListBuilder.create()
				.texOffs(67, 47).addBox(-2.0F, 0.0F, -3.0F, 2, 2, 6),
			PartPose.offset(4.0F, 0.0F, 0.0F));
		var filterbase4 = filterbase.addOrReplaceChild("filterbase4", CubeListBuilder.create()
				.texOffs(50, 47).addBox(0.0F, 0.0F, -3.0F, 2, 2, 6),
			PartPose.offset(-4.0F, 0.0F, 0.0F));

		var basin = partDefinition.addOrReplaceChild("basin", CubeListBuilder.create(), PartPose.ZERO);

		var basinside1a = basin.addOrReplaceChild("basinside1a", CubeListBuilder.create()
				.texOffs(50, 35).addBox(-6.0F, 0.0F, -1.0F, 12, 5, 1),
			PartPose.offset(0.0F, -16.0F, -6.0F));
		var basinside1b = basinside1a.addOrReplaceChild("basinside1b", CubeListBuilder.create()
				.texOffs(0, 47).addBox(-6.0F, 0.0F, 0.0F, 12, 4, 1),
			PartPose.offsetAndRotation(0.0F, 5.0F, -1.0F, 0.2618F, 0.0F, 0.0F));
		var plank1 = basinside1a.addOrReplaceChild("plank1", CubeListBuilder.create()
				.texOffs(27, 47).addBox(-5.0F, -1.0F, -1.0F, 10, 1, 1),
			PartPose.offsetAndRotation(0.0F, 8.75F, 1.0F, 0.7854F, 0.0F, 0.0F));

		var basinside2a = basin.addOrReplaceChild("basinside2a", CubeListBuilder.create()
				.texOffs(23, 35).addBox(-6.0F, 0.0F, 0.0F, 12, 5, 1),
			PartPose.offset(0.0F, -16.0F, 6.0F));
		var basinside2b = basinside2a.addOrReplaceChild("basinside2b", CubeListBuilder.create()
				.texOffs(77, 35).addBox(-6.0F, 0.0F, -1.0F, 12, 4, 1),
			PartPose.offsetAndRotation(0.0F, 5.0F, 1.0F, -0.2618F, 0.0F, 0.0F));
		var plank2 = basinside2a.addOrReplaceChild("plank2", CubeListBuilder.create()
				.texOffs(0, 53).addBox(-5.0F, -1.0F, 0.0F, 10, 1, 1),
			PartPose.offsetAndRotation(0.0F, 8.75F, -1.0F, -0.7854F, 0.0F, 0.0F));

		var basinside3a = basin.addOrReplaceChild("basinside3a", CubeListBuilder.create()
				.texOffs(72, 0).addBox(-1.0F, 0.0F, -6.0F, 1, 5, 12),
			PartPose.offset(-6.0F, -16.0F, 0.0F));
		var basinside3b = basinside3a.addOrReplaceChild("basinside3b", CubeListBuilder.create()
				.texOffs(27, 18).addBox(0.0F, 0.0F, -6.0F, 1, 4, 12),
			PartPose.offsetAndRotation(-1.0F, 5.0F, 0.0F, 0.0F, 0.0F, -0.2618F));
		var plank3 = basinside3a.addOrReplaceChild("plank3", CubeListBuilder.create()
				.texOffs(0, 35).addBox(-1.0F, -1.0F, -5.0F, 1, 1, 10),
			PartPose.offsetAndRotation(1.0F, 8.75F, 0.0F, 0.0F, 0.0F, -0.7854F));

		var basinside4a = basin.addOrReplaceChild("basinside4a", CubeListBuilder.create()
				.texOffs(45, 0).addBox(0.0F, 0.0F, -6.0F, 1, 5, 12),
			PartPose.offset(6.0F, -16.0F, 0.0F));
		var basinside4b = basinside4a.addOrReplaceChild("basinside4b", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-1.0F, 0.0F, -6.0F, 1, 4, 12),
			PartPose.offsetAndRotation(1.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
		var plank4 = basinside4a.addOrReplaceChild("plank4", CubeListBuilder.create()
				.texOffs(54, 18).addBox(0.0F, -1.0F, -5.0F, 1, 1, 10),
			PartPose.offsetAndRotation(-1.0F, 8.75F, 0.0F, 0.0F, 0.0F, 0.7854F));


		var cornerpiece1 = basin.addOrReplaceChild("cornerpiece1", CubeListBuilder.create()
				.texOffs(32, 87).addBox(0.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offset(-6.0F, -16.0F, -6.0F));

		var cornerpiece2 = basin.addOrReplaceChild("cornerpiece2", CubeListBuilder.create()
				.texOffs(32, 84).addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1),
			PartPose.offset(6.0F, -16.0F, -6.0F));

		var cornerpiece3 = basin.addOrReplaceChild("cornerpiece3", CubeListBuilder.create()
				.texOffs(27, 87).addBox(-1.0F, 0.0F, -1.0F, 1, 1, 1),
			PartPose.offset(6.0F, -16.0F, 6.0F));

		var cornerpiece4 = basin.addOrReplaceChild("cornerpiece4", CubeListBuilder.create()
				.texOffs(27, 84).addBox(0.0F, 0.0F, -1.0F, 1, 1, 1),
			PartPose.offset(-6.0F, -16.0F, 6.0F));

		var basin_bottom = basin.addOrReplaceChild("basin_bottom", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.5F, -1.0F, -5.5F, 11, 1, 11),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		var basin_opening = basin_bottom.addOrReplaceChild("basin_opening", CubeListBuilder.create()
				.texOffs(85, 65).addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
