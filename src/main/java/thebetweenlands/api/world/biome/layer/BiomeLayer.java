package thebetweenlands.api.world.biome.layer;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.api.BLRegistries;

public interface BiomeLayer {
	public static final Codec<BiomeLayer> CODEC = BLRegistries.BIOME_LAYER_TYPE.byNameCodec().dispatch(BiomeLayer::codec, Function.identity());
	
	/**
	 * @return a codec for this biome layer
	 */
	public MapCodec<? extends BiomeLayer> codec();
	
}
