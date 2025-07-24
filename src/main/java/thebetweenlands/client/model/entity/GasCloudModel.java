package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.GasCloud;

public class GasCloudModel extends MowzieModelBase<GasCloud> {

	private final ModelPart root;

	public GasCloudModel(ModelPart root) {
		this.root = root;
	}

	@Override
	public ModelPart root() {
		return root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		return LayerDefinition.create(definition, 0, 0);
	}

	@Override
	public void setupAnim(GasCloud gasCloud, float v, float v1, float v2, float v3, float v4) {

	}
}
