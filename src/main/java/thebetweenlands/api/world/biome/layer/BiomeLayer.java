package thebetweenlands.api.world.biome.layer;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public interface BiomeLayer {
	public static final Codec<BiomeLayer> CODEC = BLRegistries.BIOME_LAYER_TYPE.byNameCodec().dispatch(BiomeLayer::codec, Function.identity());

	/**
	 * Whether this biome layer's {@link BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState) createAreaFactory(BiomeLayerContext, BiomeLayerChainState)} method requires a reference to the previous biome layer.
	 * @return whether this biome layer's area factory will use a reference to the previous biome layer
	 */
	public boolean referencesPreviousLayer();
	
	/**
	 * Called once when biome layers are initially processed
	 * @param <A>
	 * @param context the biome layer context
	 * @param biomeLayerChain the layer chain, which may be modified in this function
	 */
	public default <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		// NO-OP by default
	}
	
	/**
	 * Called to create an AreaFactory for biome processing
	 * @param <A>
	 * @param context the biome layer context
	 * @param chainState the state of the biome chain after {@link BiomeLayer#compose(BiomeLayerContext, BiomeLayerChain) compose()} was called
	 * @return this layer's area factory
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState);
	
	/**
	 * @return a codec for this biome layer
	 */
	public MapCodec<? extends BiomeLayer> codec();
	
}
