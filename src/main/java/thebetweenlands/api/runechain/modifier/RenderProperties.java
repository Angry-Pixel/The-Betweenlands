package thebetweenlands.api.runechain.modifier;

/**
 * Render properties that tell {@link RuneEffectModifier} renderers how the effects are supposed to be rendered.
 */
public class RenderProperties {
	/**
	 * Red color component multiplier
	 */
	public float red = 1;

	/**
	 * Green color component multiplier
	 */
	public float green = 1;

	/**
	 * Blue color component multiplier
	 */
	public float blue = 1;

	/**
	 * Alpha color component multiplier
	 */
	public float alpha = 1;

	/**
	 * X size the renderer should fit into, [-sizeX, sizeX]
	 */
	public float sizeX = 0.5f;

	/**
	 * Y size the renderer should fit into, [-sizeY, sizeY]
	 */
	public float sizeY = 0.5f;

	/**
	 * Z size the renderer should fit into, [-sizeZ, sizeZ]
	 */
	public float sizeZ = 0.5f;

	/**
	 * Whether the renderer should be rendered at a fixed position and not move
	 */
	public boolean fixed = false;
	
	/**
	 * Whether the renderer should be emissive/glow
	 */
	public boolean emissive = false;
}