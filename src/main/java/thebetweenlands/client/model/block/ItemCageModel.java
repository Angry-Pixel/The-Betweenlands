package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ItemCageModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6)
				.texOffs(0, 9)
				.addBox(-6.0F, -4.0F, -6.0F, 12, 2, 12)
				.texOffs(0, 34)
				.addBox(-7.5F, -10.5F, -7.5F, 15, 2, 15)
				.texOffs(61, 38)
				.addBox(-5.5F, -12.5F, -5.5F, 11, 2, 11)
				.texOffs(24, 0)
				.addBox(-1.5F, -14.5F, -1.5F, 3, 2, 3)
				.texOffs(37, 0)
				.addBox(-2.0F, -15.5F, -2.0F, 4, 1, 4),
			PartPose.ZERO);

		var bars = partDefinition.addOrReplaceChild("bars", CubeListBuilder.create(), PartPose.ZERO);

		bars.addOrReplaceChild("bar_1", CubeListBuilder.create()
			.texOffs(2, 24)
			.addBox(0.0F, -6.0F, 0.0F, 1, 7, 1),
			PartPose.offsetAndRotation(-6.0F, -4.0F, -6.0F, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F));

		bars.addOrReplaceChild("bar_2", CubeListBuilder.create()
				.texOffs(11, 24)
				.addBox(0.0F, -6.0F, -1.0F, 1, 7, 1),
			PartPose.offsetAndRotation(-6.0F, -4.0F, 6.0F, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F));

		bars.addOrReplaceChild("bar_3", CubeListBuilder.create()
				.texOffs(20, 24)
				.addBox(-1.0F, -6.0F, -1.0F, 1, 7, 1),
			PartPose.offsetAndRotation(6.0F, -4.0F, 6.0F, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F));

		bars.addOrReplaceChild("bar_4", CubeListBuilder.create()
				.texOffs(29, 24)
				.addBox(-1.0F, -6.0F, 0.0F, 1, 7, 1),
			PartPose.offsetAndRotation(6.0F, -4.0F, -6.0F, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F));

		bars.addOrReplaceChild("bar_5", CubeListBuilder.create()
				.texOffs(38, 24)
				.addBox(-0.5F, -6.0F, 0.0F, 1, 7, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, -6.0F, 0.18203784098300857F, 0.0F, 0.0F));

		bars.addOrReplaceChild("bar_6", CubeListBuilder.create()
				.texOffs(47, 24)
				.addBox(0.0F, -6.0F, -0.5F, 1, 7, 1),
			PartPose.offsetAndRotation(-6.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		bars.addOrReplaceChild("bar_7", CubeListBuilder.create()
				.texOffs(56, 24)
				.addBox(-0.5F, -6.0F, -1.0F, 1, 7, 1),
			PartPose.offsetAndRotation(0.0F, -4.0F, 6.0F, -0.18203784098300857F, 0.0F, 0.0F));

		bars.addOrReplaceChild("bar_8", CubeListBuilder.create()
				.texOffs(65, 24)
				.addBox(-1.0F, -6.0F, -0.5F, 1, 7, 1),
			PartPose.offsetAndRotation(6.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
