package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class GlassJarModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("jar", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-5.0F, -12.1F, -5.0F, 10, 12, 10)
			.texOffs(40, 24)
			.addBox(-3.0F, -14.0F, -3.0F, 6, 2, 6)
			.texOffs( 0, 22)
			.addBox(-4.0F, -16.0F, -4.0F, 8, 2, 8),
			PartPose.ZERO);

		return LayerDefinition.create(definition, 64, 32);
	}
}
