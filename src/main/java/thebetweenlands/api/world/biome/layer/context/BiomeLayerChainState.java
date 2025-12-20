package thebetweenlands.api.world.biome.layer.context;

import java.util.Map;
import java.util.Optional;

public interface BiomeLayerChainState {
	public static final BiomeLayerChainState EMPTY = new BiomeLayerChainState() {
		@Override
		public Optional<BiomeLayerRef<?>> getPreviousLayer() {
			return Optional.empty();
		}
		
		@Override
		public Optional<BiomeLayerRef<?>> getBackwardRef(String name) {
			return Optional.empty();
		}

		@Override
		public Map<String, BiomeLayerRef<?>> getAllBackwardRefs() {
			return Map.of();
		}
	};
	
	/**
	 * @return a ref to the layer immediately before this one
	 */
	public Optional<BiomeLayerRef<?>> getPreviousLayer();

	/**
	 * Gets the backward ref with the specified {@code name}, if it is present.
	 * @param name the name of the backward ref
	 * @return an optional containing the ref, or an empty optional if no ref exists
	 */
	public Optional<BiomeLayerRef<?>> getBackwardRef(String name);
	
	/**
	 * @return an immutable map of all present backward refs
	 */
	public Map<String, BiomeLayerRef<?>> getAllBackwardRefs();
}
