package thebetweenlands.api.world.biome.layer.context;

import thebetweenlands.api.world.biome.layer.BiomeLayer;

/**
 * Used to make references to previous biome layers available to a biome layer's {@link BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState)}
 */
public interface BiomeLayerReferenceAcquirer {

	/**
	 * Attempts to acquire a reference to the previous biome layer in the chain (if available).
	 * <p>Fails if there does not exist a previous biome layer</p>
	 * @return whether the reference was acquired successfully
	 */
	public boolean acquirePreviousLayer();

	/**
	 * Attempts to acquire a named reference to a previous biome layer.
	 * <p>Fails if there does not exist a named reference with the specified name</p>
	 * @return whether the reference was acquired successfully
	 */
	public boolean acquireBackwardsRef(String name);

	/**
	 * Acquires all named backwards references for area factory creation.
	 */
	public void acquireAllBackwardsRefs();

	/**
	 * Acquires the entire available context for area factory creation.
	 */
	public void acquireFullContext();
	
	/**
	 * {@return the entire available context, without acquiring it}
	 */
	public BiomeLayerChainState peekFullContext();
	
}
