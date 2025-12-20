package thebetweenlands.api.world.biome.layer.context;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextResolver.LongBasedBiomeLayerContextResolver;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextResolver.StringBasedBiomeLayerContextResolver;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record BiomeLayerConfigured(BiomeLayer biomeLayer, BiomeLayerContextResolver contextResolver) {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Codec<BiomeLayerConfigured> CODEC = Codec.pair(
			BiomeLayer.CODEC,
			BiomeLayerContextResolver.CODEC.fieldOf("random_seed").codec()
		).<BiomeLayerConfigured>xmap(BiomeLayerConfigured::fromPair, (config) -> (Pair)toPair(config));

	/**
	 * @see BiomeLayer#compose(BiomeLayerContext, BiomeLayerChain)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		this.compose(context.getContextFactory(), biomeLayerChain);
	}

	/**
	 * @see BiomeLayer#compose(BiomeLayerContext, BiomeLayerChain)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> void compose(BiomeLayerContextFactory<A> context, BiomeLayerChain biomeLayerChain) {
		this.biomeLayer().compose(this.contextResolver().createContext(context), biomeLayerChain);
	}

	/**
	 * @see BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		return this.createAreaFactory(context.getContextFactory(), chainState);
	}

	/**
	 * @see BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContextFactory<A> context, BiomeLayerChainState chainState) {
		return this.biomeLayer().createAreaFactory(this.contextResolver().createContext(context), chainState);
	}
	
	
	public static <T extends BiomeLayerContextResolver> BiomeLayerConfigured fromPair(Pair<BiomeLayer, T> pair) {
		return new BiomeLayerConfigured(pair.getFirst(), pair.getSecond());
	}

	public static Pair<BiomeLayer, ? extends BiomeLayerContextResolver> toPair(BiomeLayerConfigured config) {
		return Pair.of(config.biomeLayer(), config.contextResolver());
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, long seed) {
		return new BiomeLayerConfigured(biomeLayer, new LongBasedBiomeLayerContextResolver(seed));
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, String seed) {
		return new BiomeLayerConfigured(biomeLayer, new StringBasedBiomeLayerContextResolver(seed));
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, ResourceLocation seed) {
		return BiomeLayerConfigured.of(biomeLayer, seed.toString());
	}
	
	// TODO proper support for layers without context
	public static BiomeLayerConfigured unconfigured(BiomeLayer biomeLayer) {
		return new BiomeLayerConfigured(biomeLayer, new LongBasedBiomeLayerContextResolver(0L));
	}
	
}
