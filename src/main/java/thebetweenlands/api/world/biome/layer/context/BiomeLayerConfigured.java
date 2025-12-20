package thebetweenlands.api.world.biome.layer.context;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext.AreaFactoryContextSupplier;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver.LongBasedBiomeLayerContextResolver;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver.StringBasedBiomeLayerContextResolver;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record BiomeLayerConfigured(BiomeLayer biomeLayer, BiomeLayerRandomContextResolver contextResolver) {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Codec<BiomeLayerConfigured> CODEC = Codec.pair(
			BiomeLayer.CODEC,
			BiomeLayerRandomContextResolver.CODEC.fieldOf("random_seed").codec()
		).<BiomeLayerConfigured>xmap(BiomeLayerConfigured::fromPair, (config) -> (Pair)toPair(config));

	/**
	 * @see BiomeLayer#compose(BiomeLayerContext, BiomeLayerChain)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		this.compose(context.areaContext(), context.randomContext().getRandomFactory(), biomeLayerChain);
	}

	/**
	 * @see BiomeLayer#compose(BiomeLayerContext, BiomeLayerChain)
	 * @param <A>
	 * @param randomContext
	 * @param biomeLayerChain
	 */
	public <A extends Area> void compose(AreaFactoryContextSupplier<A> areaFactory, BiomeLayerRandomFactoryContext randomFactory, BiomeLayerChain biomeLayerChain) {
		BiomeLayerRandomContext randomState = this.contextResolver().createContext(randomFactory);
		this.biomeLayer().compose(new BiomeLayerContext<>(areaFactory, randomState), biomeLayerChain);
	}

	/**
	 * @see BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState)
	 * @param <A>
	 * @param context
	 * @param biomeLayerChain
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		return this.createAreaFactory(context.areaContext(), context.randomContext().getRandomFactory(), chainState);
	}

	/**
	 * @see BiomeLayer#createAreaFactory(BiomeLayerContext, BiomeLayerChainState)
	 * @param <A>
	 * @param randomContext
	 * @param biomeLayerChain
	 */
	public <A extends Area> AreaFactory<A> createAreaFactory(AreaFactoryContextSupplier<A> areaFactory, BiomeLayerRandomFactoryContext randomFactory, BiomeLayerChainState chainState) {
		BiomeLayerRandomContext randomState = this.contextResolver().createContext(randomFactory);
		return this.biomeLayer().createAreaFactory(new BiomeLayerContext<>(areaFactory, randomState), chainState);
	}
	
	
	public static <T extends BiomeLayerRandomContextResolver> BiomeLayerConfigured fromPair(Pair<BiomeLayer, T> pair) {
		return new BiomeLayerConfigured(pair.getFirst(), pair.getSecond());
	}

	public static Pair<BiomeLayer, ? extends BiomeLayerRandomContextResolver> toPair(BiomeLayerConfigured config) {
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
