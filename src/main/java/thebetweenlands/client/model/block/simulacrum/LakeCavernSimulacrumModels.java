package thebetweenlands.client.model.block.simulacrum;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LakeCavernSimulacrumModels {

	public static LayerDefinition makeSimulacrum1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -3.0F, -2.0F, 6, 3, 4),
			PartPose.ZERO);

		var main = base.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-2.0F, -12.0F, -1.0F, 4, 12, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		main.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(0, 23)
				.addBox(-2.0F, -3.0F, -1.0F, 3, 1, 2),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public static LayerDefinition makeSimulacrum2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -2.0F, -2.0F, 10, 2, 4),
			PartPose.ZERO);

		var middle = base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 7)
				.addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2),
			PartPose.offset(0.0F, -2.0F, -0.25F));

		var left = middle.addOrReplaceChild("left", CubeListBuilder.create()
				.texOffs(9, 7)
				.addBox(0.0F, -14.0F, 0.0F, 2, 14, 2),
			PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, 0.0F, -0.22759093446006054F, 0.0F));

		var right = middle.addOrReplaceChild("right", CubeListBuilder.create()
				.texOffs(18, 7)
				.addBox(-2.0F, -14.0F, 0.0F, 2, 14, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 0.0F, 0.22759093446006054F, 0.0F));

		left.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(9, 24)
				.addBox(0.0F, 0.0F, 0.0F, 1, 4, 2, new CubeDeformation(0.01F)),
			PartPose.offset(2.0F, -14.0F, 0.0F));

		right.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(18, 24)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 2, new CubeDeformation(0.00F)),
			PartPose.offset(-2.0F, -14.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	public static LayerDefinition makeSimulacrum3() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -3.0F, -5.0F, 10, 3, 10),
			PartPose.ZERO);

		var main = base.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		main.addOrReplaceChild("edge_front", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 2),
			PartPose.offset(0.0F, -5.9F, -2.0F));

		main.addOrReplaceChild("edge_back", CubeListBuilder.create()
				.texOffs(21, 29)
				.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offset(0.0F, -6.0F, 2.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
