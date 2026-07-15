package thebetweenlands.api.world.biome.layer.context;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext.AreaFactoryContextSupplier;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver.ContextResolverHolder;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record BiomeLayerConfigured(BiomeLayer biomeLayer, ContextResolverHolder contextResolver) {
	
	public static final Codec<BiomeLayerConfigured> CODEC = Codec.pair(
			BiomeLayer.CODEC,
			BiomeLayerRandomContextResolver.ContextResolverHolder.CODEC.optionalFieldOf("random_seed", ContextResolverHolder.unconfigured()).codec()
		).<BiomeLayerConfigured>xmap(BiomeLayerConfigured::fromPair, BiomeLayerConfigured::toPair);

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
	 * @see BiomeLayer#acquireReferences(BiomeLayerContext, BiomeLayerReferenceAcquirer)
	 * @param <A>
	 * @param context
	 * @param referenceAcquirer
	 */
	public <A extends Area> void acquireReferences(BiomeLayerContext<A> context, BiomeLayerReferenceAcquirer referenceAcquirer) {
		this.acquireReferences(context.areaContext(), context.randomContext().getRandomFactory(), referenceAcquirer);
	}

	/**
	 * @see BiomeLayer#acquireReferences(BiomeLayerContext, BiomeLayerReferenceAcquirer)
	 * @param <A>
	 * @param randomContext
	 * @param referenceAcquirer
	 */
	public <A extends Area> void acquireReferences(AreaFactoryContextSupplier<A> areaFactory, BiomeLayerRandomFactoryContext randomFactory, BiomeLayerReferenceAcquirer referenceAcquirer) {
		BiomeLayerRandomContext randomState = this.contextResolver().createContext(randomFactory);
		this.biomeLayer().acquireReferences(new BiomeLayerContext<>(areaFactory, randomState), referenceAcquirer);
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
	
	
	public static BiomeLayerConfigured fromPair(Pair<BiomeLayer, ContextResolverHolder> pair) {
		return new BiomeLayerConfigured(pair.getFirst(), pair.getSecond());
	}

	public static Pair<BiomeLayer, ContextResolverHolder> toPair(BiomeLayerConfigured config) {
		return Pair.of(config.biomeLayer(), config.contextResolver());
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, long seed) {
		return new BiomeLayerConfigured(biomeLayer, ContextResolverHolder.ofLong(seed));
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, String seed) {
		return new BiomeLayerConfigured(biomeLayer,  ContextResolverHolder.ofString(seed));
	}

	public static BiomeLayerConfigured of(BiomeLayer biomeLayer, ResourceLocation seed) {
		return BiomeLayerConfigured.of(biomeLayer, seed.toString());
	}
	
	public static BiomeLayerConfigured unconfigured(BiomeLayer biomeLayer) {
		return new BiomeLayerConfigured(biomeLayer, ContextResolverHolder.unconfigured());
	}
	
}
