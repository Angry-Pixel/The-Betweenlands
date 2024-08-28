package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MobSpawnerCrystalModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("crystal", CubeListBuilder.create().mirror()
				.addBox(-16.0F, -16.0F, 0.0F, 16, 16, 16),
			PartPose.offsetAndRotation(0.0F, 32.0F, 0.0F, 0.7071F, 0.0F, 0.7071F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
