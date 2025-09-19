package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.GasCloud;

public class GasCloudModel extends MowzieModelBase<GasCloud> {

	public GasCloudModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		return LayerDefinition.create(definition, 0, 0);
	}
}
