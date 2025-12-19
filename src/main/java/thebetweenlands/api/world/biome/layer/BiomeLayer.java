package thebetweenlands.api.world.biome.layer;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.util.RandomSource;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public interface BiomeLayer {
	public static final Codec<BiomeLayer> CODEC = BLRegistries.BIOME_LAYER_TYPE.byNameCodec().dispatch(BiomeLayer::codec, Function.identity());

    public default <A extends Area> AreaFactory<A> compose(BiomeLayerContext<A> context) {
        return () -> context.createResult((x, z) -> {
            return this.apply(context, context.createRandom(x, z), x, z);
        });
    }

    public <A extends Area> int apply(BiomeLayerContext<A> context, RandomSource random, int x, int z);
    
	/**
	 * @return a codec for this biome layer
	 */
	public MapCodec<? extends BiomeLayer> codec();
	
}
