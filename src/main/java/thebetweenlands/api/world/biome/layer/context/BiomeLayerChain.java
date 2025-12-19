package thebetweenlands.api.world.biome.layer.context;

import java.util.Map;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import thebetweenlands.api.world.biome.layer.BiomeLayer;

/**
 * Represents a chain of biome layers that is currently in-use
 */
public class BiomeLayerChain implements BiomeLayerChainState {

	/**
	 * The previous biome layer
	 */
	private Optional<BiomeLayerRef> previousLayer;
	/**
	 * A mutable map of all backwards refs, which is updated as the chain moves
	 */
	private final Map<String, BiomeLayerRef> backwardsRefs;
	
	/**
	 * The layer currently being processed
	 */
	private BiomeLayer currentLayer = null;
	
	/**
	 * Used to check if the previous layer was accessed by the currently processing layer
	 * <br/>
	 * If the previous layer was not accessed by the time the layer is finished, then it is not necessary to store it in this layer's ref.
	 */
	private boolean previousLayerUsed = false;
	
	/**
	 * An immutable copy of the backward refs, used to save memory in immutableCopy
	 */
	private Map<String, BiomeLayerRef> immutableBackwardsRefs = Map.of();
	/**
	 * Have the backward refs changed since immutableBackwardsRefs was last updated?
	 */
	private boolean backwardRefsChanged = false;
	
	public BiomeLayerChain(Optional<BiomeLayerRef> previousLayer, Map<String, BiomeLayerRef> backwardsRefs) {
		this.previousLayer = previousLayer;
		this.backwardsRefs = new Object2ObjectArrayMap<>(backwardsRefs);
	}

	public BiomeLayerChain() {
		this(Optional.empty(), Map.of());
	}
	
	@Override
	public Optional<BiomeLayerRef> getPreviousLayer() {
		this.previousLayerUsed = true;
		return this.previousLayer;
	}

	@Override
	public Optional<BiomeLayerRef> getBackwardsRef(String name) {
		if(this.backwardsRefs.containsKey(name)) {
			return Optional.of(this.backwardsRefs.get(name));
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Adds a backwards ref to {@code biomeLayer} using the specified {@code name}
	 * @param name the name of the backwards ref
	 * @param biomeLayer the biome layer to reference
	 */
	public void addBackwardsRef(String name, BiomeLayer biomeLayer) {
		this.backwardsRefs.put(name, new BiomeLayerRef(biomeLayer, this.immutableCopy()));
		
		this.backwardRefsChanged = true;
	}
	
	/**
	 * Sets up the chain so that {@code nextLayer} is the current layer.
	 * @param nextLayer the layer to become the new current layer
	 */
	public void nextLayer(BiomeLayer nextLayer) {
		if(this.currentLayer == null) {
			this.previousLayer = Optional.empty();
		} else {
			final boolean previousLayerUsed = this.previousLayerUsed;
			BiomeLayerChainState refState = this.immutableCopy(previousLayerUsed);
			
			this.previousLayer = Optional.of(new BiomeLayerRef(this.currentLayer, refState));
		}
		this.currentLayer = nextLayer;
		this.previousLayerUsed = false;
	}
	
	public FrozenBiomeLayerChainState immutableCopy() {
		return this.immutableCopy(true);
	}
	
	public FrozenBiomeLayerChainState immutableCopy(boolean includePreviousLayer) {
		if(this.backwardRefsChanged) {
			this.immutableBackwardsRefs = Map.copyOf(this.backwardsRefs);
			this.backwardRefsChanged = false;
		}
		
		Optional<BiomeLayerRef> previousLayer = includePreviousLayer ? this.getPreviousLayer() : Optional.empty();
		
		return new FrozenBiomeLayerChainState(previousLayer, this.immutableBackwardsRefs);
	}
	
	public static record FrozenBiomeLayerChainState(Optional<BiomeLayerRef> previousLayer, Map<String, BiomeLayerRef> backwardsRefs) implements BiomeLayerChainState {
		
		@Override
		public Optional<BiomeLayerRef> getPreviousLayer() {
			return this.previousLayer;
		}

		@Override
		public Optional<BiomeLayerRef> getBackwardsRef(String name) {
			if(this.backwardsRefs.containsKey(name)) {
				return Optional.of(this.backwardsRefs.get(name));
			} else {
				return Optional.empty();
			}
		}
		
	}
}
