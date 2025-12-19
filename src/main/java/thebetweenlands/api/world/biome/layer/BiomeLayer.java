package thebetweenlands.api.world.biome.layer;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChain;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public interface BiomeLayer {
	public static final Codec<BiomeLayer> CODEC = BLRegistries.BIOME_LAYER_TYPE.byNameCodec().dispatch(BiomeLayer::codec, Function.identity());

	/**
	 * Called once when biome layers are initially processed
	 * @param <A>
	 * @param context the biome layer context
	 * @param biomeLayerChain the layer chain, which may be modified in this function
	 */
    public default <A extends Area> AreaFactory<A> compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
    	return this.createAreaFactory(context, biomeLayerChain);
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
