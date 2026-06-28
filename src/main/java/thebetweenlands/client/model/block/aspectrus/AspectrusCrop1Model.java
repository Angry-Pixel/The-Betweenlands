package thebetweenlands.client.model.block.aspectrus;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AspectrusCrop1Model {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var crop = partDefinition.addOrReplaceChild("crop", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5F, -8.0F, -2.5F, 5, 8, 5),
			PartPose.offset(0.0F, 24.0F, 0.0F));

		var leaf1 = crop.addOrReplaceChild("leaf1", CubeListBuilder.create()
				.texOffs(18, 0).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(-1.5F, -2.0F, -2.5F, -0.40980330836826856F, 0.36425021489121656F, 0.0F));
		var leaf1b = leaf1.addOrReplaceChild("leaf1b", CubeListBuilder.create()
				.texOffs(21, 5).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.5462880558742251F, 0.0F, 0.0F));

		var leaf2 = crop.addOrReplaceChild("leaf2", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-2.5F, 0.0F, -3.5F, 5, 0, 4),
			PartPose.offsetAndRotation(2.5F, -4.0F, 1.5F, -0.136659280431156F, -2.0488420089161434F, 0.0F));
		var leaf2b = leaf2.addOrReplaceChild("leaf2b", CubeListBuilder.create()
				.texOffs(36, 5).addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.31869712141416456F, 0.0F, 0.0F));

		var leaf3 = crop.addOrReplaceChild("leaf3", CubeListBuilder.create()
				.texOffs(17, 11).addBox(-2.0F, 0.0F, -3.5F, 4, 0, 4),
			PartPose.offsetAndRotation(-2.0F, -3.0F, 2.5F, -0.27314402793711257F, 2.5497515042385164F, 0.0F));
		var leaf3b = leaf3.addOrReplaceChild("leaf3b", CubeListBuilder.create()
				.texOffs(21, 16).addBox(-2.0F, 0.0F, -4.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.5462880558742251F, 0.0F, 0.0F));

		var leaf4 = crop.addOrReplaceChild("leaf4", CubeListBuilder.create()
				.texOffs(33, 11).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(1.0F, -6.0F, -2.5F, -0.36425021489121656F, -0.27314402793711257F, 0.0F));
		var leaf5 = crop.addOrReplaceChild("leaf5", CubeListBuilder.create()
				.texOffs(33, 15).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(-2.5F, -5.0F, 1.0F, -0.31869712141416456F, 1.7756979809790308F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
