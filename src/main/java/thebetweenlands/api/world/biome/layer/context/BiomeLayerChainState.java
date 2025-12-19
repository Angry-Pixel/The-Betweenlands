package thebetweenlands.api.world.biome.layer.context;

import java.util.Optional;

public interface BiomeLayerChainState {
	/**
	 * @return a ref to the layer immediately before this one
	 */
	public Optional<BiomeLayerRef> getPreviousLayer();

	/**
	 * Gets the backwards ref with the specified {@code name}, if it is present.
	 * @param name the name of the backwards ref
	 * @return an optional containing the ref, or an empty optional if no ref exists
	 */
	public Optional<BiomeLayerRef> getBackwardsRef(String name);
}
