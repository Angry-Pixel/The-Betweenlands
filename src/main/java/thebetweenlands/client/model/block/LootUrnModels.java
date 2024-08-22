package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class LootUrnModels {

	public static LayerDefinition makeUrn1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6),
			PartPose.ZERO);

		base.addOrReplaceChild("middle", CubeListBuilder.create()
			.texOffs(0, 10)
			.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var top = base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		top.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(25, 9)
				.addBox(-3.0F, -1.0F, 1.0F, 6, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(25, 13)
				.addBox(-3.0F, -1.0F, -3.0F, 6, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition makeUrn2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var stand = partDefinition.addOrReplaceChild("stand", CubeListBuilder.create()
				.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		stand.addOrReplaceChild("leg_1", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(0.0F, 0.0F, -2.0F, 2, 5, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, -2.0F, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F));

		stand.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(9, 11)
				.addBox(-2.0F, 0.0F, -2.0F, 2, 5, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -2.0F, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F));

		stand.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(18, 11)
				.addBox(-2.0F, 0.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 2.0F, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F));

		stand.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(27, 11)
				.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, 2.0F, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F));

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 19)
				.addBox(-3.0F, -4.0F, -3.0F, 6, 4, 6),
			PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.0F, 0.091106186954104F, 0.045553093477052F));

		base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(28, 17)
				.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 8),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(33, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition makeUrn3() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.ZERO);

		base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		var top = base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		top.addOrReplaceChild("top_stem", CubeListBuilder.create()
				.texOffs(25, 9)
				.addBox(-3.0F, -1.0F, -3.0F, 6, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("top_curl", CubeListBuilder.create()
				.texOffs(33, 13)
				.addBox(-3.0F, -3.0F, -3.0F, 6, 2, 4),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
