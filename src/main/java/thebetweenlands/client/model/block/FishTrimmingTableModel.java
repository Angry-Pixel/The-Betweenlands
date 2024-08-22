package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class FishTrimmingTableModel {
	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-7.0F, -12.0F, -4.0F, 14, 12, 11),
			PartPose.ZERO);

		base.addOrReplaceChild("front", CubeListBuilder.create()
			.texOffs(0, 24)
			.addBox(-7.0F, -12.0F, -7.0F, 14, 12, 2),
			PartPose.ZERO);

		base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(7, 39)
				.addBox(-5.0F, -11.0F, -5.0F, 11, 11, 1),
			PartPose.ZERO);

		var side = base.addOrReplaceChild("side", CubeListBuilder.create()
				.texOffs(0, 39)
				.addBox(-7.0F, -12.0F, -5.0F, 2, 12, 1),
			PartPose.ZERO);

		side.addOrReplaceChild("bloodslide", CubeListBuilder.create()
				.texOffs(0, 53)
				.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1),
			PartPose.offsetAndRotation(-5.0F, -12.0F, -5.0F, 0.0F, 0.0F, 0.091106186954104F));

		var cleaver = partDefinition.addOrReplaceChild("cleaver_blade", CubeListBuilder.create()
			.texOffs(40, 24)
			.addBox(-2.0F, -3.0F, 0.0F, 5, 3, 0.001F),
			PartPose.offsetAndRotation(5.5F, -12.0F, 1.9F, 0.5918411493512771F, 1.7756979809790308F, 0.6373942428283291F));

		var handle1 = cleaver.addOrReplaceChild("cleaver_handle_1", CubeListBuilder.create()
				.texOffs(40, 28)
				.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1),
			PartPose.offset(-2.0F, -3.5F, 0.0F));

		var handle2 = handle1.addOrReplaceChild("cleaver_handle_2", CubeListBuilder.create()
				.texOffs(40, 31)
				.addBox(0.0F, -1.0F, -0.5F, 2, 1, 1),
			PartPose.offsetAndRotation(3.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		var handle3 = handle2.addOrReplaceChild("cleaver_handle_3", CubeListBuilder.create()
				.texOffs(40, 34)
				.addBox(0.0F, -1.0F, -0.5F, 2, 1, 1),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		handle3.addOrReplaceChild("cleaver_handle_4", CubeListBuilder.create()
				.texOffs(40, 37)
				.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1),
			PartPose.offsetAndRotation(2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		partDefinition.addOrReplaceChild("blood", CubeListBuilder.create()
				.texOffs(-5, 56)
				.addBox(0.0F, 0.0F, -2.4F, 5, 0, 5),
			PartPose.offset(6.0F, -0.02F, -4.5F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
