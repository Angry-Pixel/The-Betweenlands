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
import thebetweenlands.api.world.biome.layer.context.BiomeLayerReferenceAcquirer;

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
	 * Reusable builder to make building biome layer chain states easier
	 */
	private final BiomeLayerChainStateBuilder chainStateBuilder = new BiomeLayerChainStateBuilder();
	
	/**
	 * An immutable copy of the backward refs, used to save memory in getAllBackwardRefs
	 */
	private Map<String, BiomeLayerRef> immutableBackwardRefs = Map.of();
	/**
	 * Have the backward refs changed since immutableBackwardRefs was last updated?
	 */
	private boolean backwardRefsChanged = false;
	
	/**
	 * If this is the first layer being processed
	 */
	private boolean first = true;
	
	/**
	 * If this chain is "finished" (meaning it can no longer be modified or accessed)
	 */
	private boolean finished = false;
	
	/**
	 * Used to close down old reference acquirers
	 */
	private long referenceStamp = 0;
	
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
	
	//#region reference getters
	
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
	 * {@return an immutable {@link BiomeLayerChainState} that contains all current references}
	 */
	public BiomeLayerChainState immutableCopy() {
		this.ensureOpen();
		return new FrozenBiomeLayerChainState(this.getPreviousLayer(), this.getAllBackwardRefs());
	}
	
	//#endregion
	
	//#region reference adders
	
//	/**
//	 * Adds a backward ref to {@code biomeLayer} using the specified {@code name}
//	 * @param name the name of the backward ref
//	 * @param biomeLayer the biome layer to reference
//	 */
//	public void addBackwardRef(String name, BiomeLayer biomeLayer, BiomeLayerRandomContext layerRandomContext) {
//		this.ensureOpen();
//		this.backwardRefs.put(name, new BiomeLayerRef(biomeLayer, layerRandomContext, this.immutableCopy()));
//		
//		this.backwardRefsChanged = true;
//	}
//	
//	/**
//	 * Adds a backward ref to {@code biomeLayer} using the specified {@code name}
//	 * @param name the name of the backward ref
//	 * @param biomeLayer the biome layer to reference
//	 */
//	public void addBackwardRef(String name, BiomeLayer biomeLayer, BiomeLayerContext<?> layerContext) {
//		this.addBackwardRef(name, biomeLayer, layerContext.randomContext());
//	}
	
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
	
	//#endregion

	//#region reference creation
	
	/**
	 * Creates a new reference acquirer for the currently processing biome layer
	 * @throws IllegalStateException if there is not a currently processing biome layer
	 * @return a new reference acquirer
	 */
	public BiomeLayerReferenceAcquirer createReferenceAcquirer() {
		this.ensureOpen();
		if(this.currentLayer == null) {
			throw new IllegalStateException("Cannot create a reference acquirer for a layer that doesn't exist");
		}
		return new ChainReferenceAcquirer();
	}
	
	/**
	 * Creates a new ref for the current layer
	 * @return an optional containing a new ref to the current layer, or empty if there is no current layer.
	 */
	protected Optional<BiomeLayerRef> buildCurrentRef() {
		if(this.currentLayer == null) {
			return Optional.empty();
		} else {
			// Builds a chain state and resets the chain state builder
			BiomeLayerChainState refState = this.chainStateBuilder.build();
			// Invalidate all outstanding reference acquirers
			this.referenceStamp++;
			
			return Optional.of(new BiomeLayerRef(this.currentLayer, this.currentLayerRandomContext, refState));
		}
	}
	
	//#endregion

	//#region moving along the chain
	
	/**
	 * Sets up the chain so that {@code nextLayer} is the current layer.
	 * @param nextLayer the layer to become the new current layer.
	 * @param nextLayerRandomContext the context of the layer to become the new current layer.
	 * @return an optional containing a reference to the layer being replaced, if it exists.
	 */
	public Optional<BiomeLayerRef> nextLayer(BiomeLayer nextLayer, BiomeLayerRandomContext nextLayerRandomContext) {
		this.ensureOpen();
		// Create the ref to the current layer, before it gets replaced
		final Optional<BiomeLayerRef> layerRef = this.buildCurrentRef();
		// Replace the current layer with the new layer
		this.currentLayer = nextLayer;
		this.currentLayerRandomContext = nextLayerRandomContext;
		// Replace the previous layer
		// If this is the first layer being processed (i.e. this is the first layer in a sequence), retain the previous layer
		// Without this, the first layer in a sequence will always see an empty optional for getPreviousLayer(), making it impossible to access the layers of the parent scope without a marker
		this.previousLayer = layerRef.isEmpty() && this.first ? this.previousLayer : layerRef;
		this.first = false;
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
		final Optional<BiomeLayerRef> layerRef = this.buildCurrentRef();
		
		this.finished = true;

		// Cleanup fields
		this.previousLayer = null;
		this.currentLayer = null;
		this.currentLayerRandomContext = null;
		this.immutableBackwardRefs = null;
		
		return layerRef;
	}
	
	//#endregion
	
	private class ChainReferenceAcquirer implements BiomeLayerReferenceAcquirer {
		private final long acquirerStamp;
		
		public ChainReferenceAcquirer() {
			this.acquirerStamp = BiomeLayerChain.this.referenceStamp;
		}
		
		private void validateStamp() {
			if(this.acquirerStamp != BiomeLayerChain.this.referenceStamp) {
				throw new IllegalStateException("Attempt to acquire/peek additional references for an already-built biome layer state");
			}
		}
		
		@Override
		public boolean acquirePreviousLayer() {
			this.validateStamp();
			Optional<BiomeLayerRef> previousLayer = BiomeLayerChain.this.previousLayer;
			if (previousLayer.isPresent()) {
				BiomeLayerChain.this.chainStateBuilder.setPreviousLayer(previousLayer);
				return true;
			}
			return false;
		}

		@Override
		public boolean acquireBackwardsRef(String name) {
			this.validateStamp();
			if (BiomeLayerChain.this.backwardRefs.containsKey(name)) {
				BiomeLayerChain.this.chainStateBuilder.addBackwardRef(name, BiomeLayerChain.this.backwardRefs.get(name));
				return true;
			}
			return false;
		}

		@Override
		public void acquireAllBackwardsRefs() {
			this.validateStamp();
			
			BiomeLayerChain.this.chainStateBuilder.addReferences(BiomeLayerChain.this.backwardRefs);
		}

		@Override
		public void acquireFullContext() {
			this.validateStamp();

			final BiomeLayerChainStateBuilder builder = BiomeLayerChain.this.chainStateBuilder;
			
			builder.setPreviousLayer(BiomeLayerChain.this.previousLayer);
			builder.addReferences(BiomeLayerChain.this.backwardRefs);
		}

		@Override
		public BiomeLayerChainState peekFullContext() {
			this.validateStamp();
			
			return BiomeLayerChain.this;
		}
	}
}
