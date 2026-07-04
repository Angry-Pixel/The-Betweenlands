package thebetweenlands.client.model.definition;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;

public class ExtendedLayerDefinition extends LayerDefinition {

	private ExtendedLayerDefinition(ExtendedMeshDefinition mesh, MaterialDefinition material) {
		super(mesh, material);
	}

	public static ExtendedLayerDefinition create(ExtendedMeshDefinition mesh, int texWidth, int texHeight) {
		return new ExtendedLayerDefinition(mesh, new MaterialDefinition(texWidth, texHeight));
	}
}
