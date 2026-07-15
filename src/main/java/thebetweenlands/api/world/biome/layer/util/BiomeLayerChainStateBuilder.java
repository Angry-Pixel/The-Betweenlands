package thebetweenlands.api.world.biome.layer.util;

import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;

/**
 * Reusable biome layer builder that constructs immutable {@link BiomeLayerChainState} instances
 */
@NotThreadSafe
public class BiomeLayerChainStateBuilder {
	/**
	 * The previous biome layer
	 */
	private Optional<BiomeLayerRef> previousLayer = Optional.empty();
	/**
	 * A mutable map of all backward refs, which is updated as the chain moves
	 */
	private final Map<String, BiomeLayerRef> backwardRefs = new Object2ObjectArrayMap<>();

	/**
	 * Sets the builder's previous layer reference
	 * @param previousLayer
	 * @return {@code this}
	 */
	public BiomeLayerChainStateBuilder setPreviousLayer(BiomeLayerRef previousLayer) {
		return this.setPreviousLayer(Optional.of(previousLayer));
	}
	
	/**
	 * Sets the builder's previous layer reference
	 * @param previousLayer
	 * @return {@code this}
	 */
	public BiomeLayerChainStateBuilder setPreviousLayer(Optional<BiomeLayerRef> previousLayer) {
		this.previousLayer = previousLayer;
		return this;
	}

	/**
	 * Adds the specified backwards ref to the builder
	 * @param name
	 * @param biomeLayerRef
	 * @return {@code this}
	 */
	public BiomeLayerChainStateBuilder addBackwardRef(String name, BiomeLayerRef biomeLayerRef) {
		this.backwardRefs.put(name, biomeLayerRef);
		return this;
	}

	/**
	 * Adds all specified backwards ref to the builder
	 * @param biomeLayerRefs
	 * @return {@code this}
	 */
	public BiomeLayerChainStateBuilder addReferences(Map<String, BiomeLayerRef> biomeLayerRefs) {
		this.backwardRefs.putAll(biomeLayerRefs);
		return this;
	}

	/**
	 * Builds an immutable {@link BiomeLayerChainState}, and resets this builder for reuse
	 * @return
	 */
	public BiomeLayerChainState build() {
		BiomeLayerChainState ref = new FrozenBiomeLayerChainState(this.previousLayer, Map.copyOf(this.backwardRefs));
		this.previousLayer = Optional.empty();
		this.backwardRefs.clear();
		return ref;
	}
}
