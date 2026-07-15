package thebetweenlands.api.world.biome.layer.util;

import java.util.Map;
import java.util.Optional;

import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;

/**
 * Immutable {@link BiomeLayerChainState} implementation
 */
public record FrozenBiomeLayerChainState(Optional<BiomeLayerRef> previousLayer, Map<String, BiomeLayerRef> backwardRefs) implements BiomeLayerChainState {
	@Override
	public Optional<BiomeLayerRef> getPreviousLayer() {
		return this.previousLayer;
	}

	@Override
	public Optional<BiomeLayerRef> getBackwardRef(String name) {
		if(this.backwardRefs.containsKey(name)) {
			return Optional.of(this.backwardRefs.get(name));
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public Map<String, BiomeLayerRef> getAllBackwardRefs() {
		return this.backwardRefs;
	}
}
