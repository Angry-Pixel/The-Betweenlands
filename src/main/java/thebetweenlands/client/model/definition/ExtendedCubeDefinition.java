package thebetweenlands.client.model.definition;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

//[VanillaCopy] of CubeDefinition that exposes cube fields via a new ContextCube
public class ExtendedCubeDefinition extends CubeDefinition {
	public ExtendedCubeDefinition(@Nullable String comment, float texCoordU, float texCoordV, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, CubeDeformation grow, boolean mirror, float texScaleU, float texScaleV, Set<Direction> visibleFaces) {
		super(comment, texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, grow, mirror, texScaleU, texScaleV, visibleFaces);
	}

	@Override
	public ContextCube bake(int texWidth, int texHeight) {
		return new ContextCube(
			(int)this.texCoord.u(),
			(int)this.texCoord.v(),
			this.origin.x(),
			this.origin.y(),
			this.origin.z(),
			this.dimensions.x(),
			this.dimensions.y(),
			this.dimensions.z(),
			this.grow.growX,
			this.grow.growY,
			this.grow.growZ,
			this.mirror,
			(float)texWidth * this.texScale.u(),
			(float)texHeight * this.texScale.v(),
			this.visibleFaces
		);
	}
}
