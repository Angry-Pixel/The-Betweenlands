package thebetweenlands.client.model.definition;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import java.util.Set;

public class ContextCube extends ModelPart.Cube {

	public int texCoordU;
	public int texCoordV;
	public float originX;
	public float originY;
	public float originZ;
	public float dimensionX;
	public float dimensionY;
	public float dimensionZ;
	public float growX;
	public float growY;
	public float growZ;
	public float texScaleU;
	public float texScaleV;

	public ContextCube(int texCoordU, int texCoordV, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float growX, float growY, float growZ, boolean mirror, float texScaleU, float texScaleV, Set<Direction> visibleFaces) {
		super(texCoordU, texCoordV, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, growX, growY, growZ, mirror, texScaleU, texScaleV, visibleFaces);
		this.texCoordU = texCoordU;
		this.texCoordV = texCoordV;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		this.dimensionZ = dimensionZ;
		this.growX = growX;
		this.growY = growY;
		this.growZ = growZ;
		this.texScaleU = texScaleU;
		this.texScaleV = texScaleV;
	}
}
