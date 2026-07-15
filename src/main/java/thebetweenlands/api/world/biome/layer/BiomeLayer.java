package thebetweenlands.api.world.biome.layer;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerReferenceAcquirer;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public interface BiomeLayer {
	public static final Codec<BiomeLayer> CODEC = BLRegistries.BIOME_LAYER_TYPE.byNameCodec().dispatch(BiomeLayer::codec, Function.identity());
	
	/**
	 * Called once when biome layers are initially processed
	 * <p>Called before {@link #acquireReferences(BiomeLayerContext, BiomeLayerReferenceAcquirer)} and {@link #createAreaFactory(BiomeLayerContext, BiomeLayerChainState)}</p>
	 * @param <A>
	 * @param context the biome layer context
	 * @param biomeLayerChain the layer chain, which may be modified in this function
	 */
	public default <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		// NO-OP by default
	}

	/**
	 * Used to collect all references to previous biome layers that are necessary for {@link #createAreaFactory(BiomeLayerContext, BiomeLayerChainState)}.
	 * <p>Only references acquired in this method will be available to the {@link BiomeLayerChainState} used in area factory creation</p>
	 * <p>Called after {@link #compose(BiomeLayerContext, BiomeLayerChain)} and before {@link #createAreaFactory(BiomeLayerContext, BiomeLayerChainState)}</p>
	 * @param <A>
	 * @param context the biome layer context
	 * @param referenceAcquirer the reference acquirer, used to make references available to {@link #createAreaFactory(BiomeLayerContext, BiomeLayerChainState)}
	 */
	public default <A extends Area> void acquireReferences(BiomeLayerContext<A> context, BiomeLayerReferenceAcquirer referenceAcquirer) {
		// NO-OP by default
	}
	
	/**
	 * Called to create an AreaFactory for biome processing
	 * <p>Called after {@link #compose(BiomeLayerContext, BiomeLayerChain)} and {@link #acquireReferences(BiomeLayerContext, BiomeLayerReferenceAcquirer)}</p>
	 * @param <A>
	 * @param context the biome layer context
	 * @param chainState the state of the biome chain, containing all references from {@link #acquireReferences(BiomeLayerContext, BiomeLayerReferenceAcquirer)}
	 * @return this layer's area factory
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState);
	
	/**
	 * @return a codec for this biome layer
	 */
	public MapCodec<? extends BiomeLayer> codec();
	
}
