package thebetweenlands.api.world.biome.layer.util;

import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;

/**
 * Represents a chain of biome layers that is currently in-use
 */
@NotThreadSafe
public class BiomeLayerChain implements BiomeLayerChainState {

	/**
	 * The previous biome layer
	 */
	private Optional<BiomeLayerRef> previousLayer;
	/**
	 * A mutable map of all backward refs, which is updated as the chain moves
	 */
	private final Map<String, BiomeLayerRef> backwardRefs;
	
	/**
	 * The layer currently being processed
	 */
	private BiomeLayer currentLayer = null;
	private BiomeLayerRandomContext currentLayerRandomContext = null;
	
	/**
	 * Used to determine if the current layer is allowed to access the previous biome layer in its ref.
	 */
	private boolean previousLayerAccessible = true;
	
	/**
	 * An immutable copy of the backward refs, used to save memory in getAllBackwardRefs
	 */
	private Map<String, BiomeLayerRef> immutableBackwardRefs = Map.of();
	/**
	 * Have the backward refs changed since immutableBackwardRefs was last updated?
	 */
	private boolean backwardRefsChanged = false;
	
	/**
	 * If this chain is "finished" (meaning it can no longer be modified or accessed)
	 */
	private boolean finished = false;
	
	public BiomeLayerChain(Optional<BiomeLayerRef> previousLayer, Map<String, BiomeLayerRef> backwardRefs) {
		this.previousLayer = previousLayer;
		this.backwardRefs = new Object2ObjectArrayMap<>(backwardRefs);
		if(backwardRefs.size() != 0) {
			this.immutableBackwardRefs = Map.copyOf(backwardRefs);
		}
	}

	public BiomeLayerChain() {
		this(Optional.empty(), Map.of());
	}
	
	protected void ensureOpen() {
		if(this.finished) { throw new IllegalStateException("Attempt to access a finished BiomeLayerChain"); }
	}
	
	@Override
	public Optional<BiomeLayerRef> getPreviousLayer() {
		this.ensureOpen();
		return this.previousLayer;
	}

	@Override
	public Optional<BiomeLayerRef> getBackwardRef(String name) {
		this.ensureOpen();
		if(this.backwardRefs.containsKey(name)) {
			return Optional.of(this.backwardRefs.get(name));
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public Map<String, BiomeLayerRef> getAllBackwardRefs() {
		this.ensureOpen();
		if(this.backwardRefsChanged) {
			this.immutableBackwardRefs = Map.copyOf(this.backwardRefs);
			this.backwardRefsChanged = false;
		}
		
		return this.immutableBackwardRefs;
	}
	
	/**
	 * Adds a backward ref to {@code biomeLayer} using the specified {@code name}
	 * @param name the name of the backward ref
	 * @param biomeLayer the biome layer to reference
	 */
	public void addBackwardRef(String name, BiomeLayer biomeLayer, BiomeLayerRandomContext layerRandomContext) {
		this.ensureOpen();
		this.backwardRefs.put(name, new BiomeLayerRef(biomeLayer, layerRandomContext, this.immutableCopy()));
		
		this.backwardRefsChanged = true;
	}
	
	/**
	 * Adds a backward ref to {@code biomeLayer} using the specified {@code name}
	 * @param name the name of the backward ref
	 * @param biomeLayer the biome layer to reference
	 */
	public void addBackwardRef(String name, BiomeLayer biomeLayer, BiomeLayerContext<?> layerContext) {
		this.addBackwardRef(name, biomeLayer, layerContext.randomContext());
	}
	
	/**
	 * Sets the backward ref with the specified {@code name} to {@code biomeLayerRef}
	 * @param name the name of the backward ref
	 * @param biomeLayerRef the biome layer to reference
	 */
	public void addBackwardRef(String name, BiomeLayerRef biomeLayerRef) {
		this.ensureOpen();
		this.backwardRefs.put(name, biomeLayerRef);
		
		this.backwardRefsChanged = true;
	}
	
	/**
	 * Creates a new ref for the current layer
	 * @return an optional containing a new ref to the current layer, or empty if there is no current layer.
	 */
	protected Optional<BiomeLayerRef> calculateCurrentRef() {
		if(this.currentLayer == null) {
			return Optional.empty();
		} else {
			BiomeLayerChainState refState = this.immutableCopy(this.previousLayerAccessible);
			
			return Optional.of(new BiomeLayerRef(this.currentLayer, this.currentLayerRandomContext, refState));
		}
	}

	/**
	 * Sets up the chain so that {@code nextLayer} is the current layer.
	 * @param nextLayer the layer to become the new current layer.
	 * @param nextLayerRandomContext the context of the layer to become the new current layer.
	 * @return an optional containing a reference to the layer being replaced, if it exists.
	 */
	public Optional<BiomeLayerRef> nextLayer(BiomeLayer nextLayer, BiomeLayerRandomContext nextLayerRandomContext) {
		this.ensureOpen();
		// Create the ref to the current layer, before it gets replaced
		final Optional<BiomeLayerRef> layerRef = this.calculateCurrentRef();
		// Replace the current layer with the new layer
		this.currentLayer = nextLayer;
		this.currentLayerRandomContext = nextLayerRandomContext;
		this.previousLayer = layerRef;
		this.previousLayerAccessible = nextLayer.referencesPreviousLayer();
		return layerRef;
	}
	
	/**
	 * Sets up the chain so that {@code nextLayer} is the current layer.
	 * @param nextLayer the layer to become the new current layer.
	 * @param nextLayerContext the context of the layer to become the new current layer.
	 * @return an optional containing a reference to the layer being replaced, if it exists.
	 */
	public Optional<BiomeLayerRef> nextLayer(BiomeLayer nextLayer, BiomeLayerContext<?> nextLayerContext) {
		return this.nextLayer(nextLayer, nextLayerContext.randomContext());
	}
	
	/**
	 * "Finishes" the layer chain, causing any calls to it to throw errors
	 * @return an optional containing a reference to the last layer in the chain, if it exists.
	 */
	public Optional<BiomeLayerRef> finish() {
		// Create the ref to the current layer, before everything becomes inaccessible
		final Optional<BiomeLayerRef> layerRef = this.calculateCurrentRef();
		
		this.finished = true;
		
		return layerRef;
	}
	
	public FrozenBiomeLayerChainState immutableCopy() {
		return this.immutableCopy(true);
	}
	
	public FrozenBiomeLayerChainState immutableCopy(boolean includePreviousLayer) {
		if(this.finished) { throw new IllegalStateException("Attempt to access a finished BiomeLayerChain"); }
		Optional<BiomeLayerRef> previousLayer = includePreviousLayer ? this.getPreviousLayer() : Optional.empty();
		
		Map<String, BiomeLayerRef> backwardRefs = this.getAllBackwardRefs();
		
		return new FrozenBiomeLayerChainState(previousLayer, backwardRefs);
	}
	
	public static record FrozenBiomeLayerChainState(Optional<BiomeLayerRef> previousLayer, Map<String, BiomeLayerRef> backwardRefs) implements BiomeLayerChainState {
		
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
}
