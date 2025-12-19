package thebetweenlands.api.world.biome.layer.context;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import thebetweenlands.api.world.biome.layer.BiomeLayer;

public record BiomeLayerConfigured(BiomeLayer biomeLayer, BiomeLayerContextResolver contextResolver) {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Codec<BiomeLayerConfigured> CODEC = Codec.pair(
			BiomeLayer.CODEC,
			BiomeLayerContextResolver.CODEC.fieldOf("random").codec()
		).<BiomeLayerConfigured>xmap(BiomeLayerConfigured::fromPair, (config) -> (Pair)toPair(config));

	
	public static <T extends BiomeLayerContextResolver> BiomeLayerConfigured fromPair(Pair<BiomeLayer, T> pair) {
		return new BiomeLayerConfigured(pair.getFirst(), pair.getSecond());
	}

	public static Pair<BiomeLayer, ? extends BiomeLayerContextResolver> toPair(BiomeLayerConfigured config) {
		return Pair.of(config.biomeLayer(), config.contextResolver());
	}
	
}
