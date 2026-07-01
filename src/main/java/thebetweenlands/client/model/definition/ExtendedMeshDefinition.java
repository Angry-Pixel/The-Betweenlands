package thebetweenlands.client.model.definition;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import thebetweenlands.util.RotationOrder;

public class ExtendedMeshDefinition extends MeshDefinition {

	private final ExtendedPartDefinition root = new ExtendedPartDefinition(ImmutableList.of(), PartPose.ZERO, RotationOrder.ZYX, ExtendedPartDefinition.Type.VANILLA);

	@Override
	public ExtendedPartDefinition getRoot() {
		return this.root;
	}
}
