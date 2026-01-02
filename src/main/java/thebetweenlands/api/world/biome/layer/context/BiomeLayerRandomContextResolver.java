package thebetweenlands.api.world.biome.layer.context;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

// TODO clean all of this mess up
@FunctionalInterface
public interface BiomeLayerRandomContextResolver {
	public static final Codec<BiomeLayerRandomContextResolver> CODEC = NeoForgeExtraCodecs.withAlternative(
			LongBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerRandomContextResolver.class, LongBasedBiomeLayerContextResolver.class)),
			StringBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerRandomContextResolver.class, StringBasedBiomeLayerContextResolver.class))
		);
	
	/**
	 * Create a random context from a random factory
	 * @param factory the random factory
	 * @return a random context
	 */
	public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory);

	// Makes sure we don't get crashes from class casting
	public static <T, U extends T> Function<? extends T, DataResult<U>> ensureClass(Class<T> parentClass, Class<U> childClass) {
		return (instance) -> childClass.isInstance(instance) ? DataResult.success(childClass.cast(instance)) : DataResult.error(() -> "Expected %s".formatted(childClass.getTypeName()));
	}
	
	// Because generics
	public static record ContextResolverHolder(BiomeLayerRandomContextResolver value) {
		public static final Codec<ContextResolverHolder> CODEC = BiomeLayerRandomContextResolver.CODEC.xmap(ContextResolverHolder::new, ContextResolverHolder::value);

		public static ContextResolverHolder ofLong(long seed) {
			return new ContextResolverHolder(new LongBasedBiomeLayerContextResolver(seed));
		}

		public static ContextResolverHolder ofString(String seed) {
			return new ContextResolverHolder(new StringBasedBiomeLayerContextResolver(seed));
		}
	}
	
	// Creates a random context from a long-based seed
	public static record LongBasedBiomeLayerContextResolver(long seed) implements BiomeLayerRandomContextResolver {
		public static final Codec<LongBasedBiomeLayerContextResolver> CODEC = Codec.LONG.xmap(LongBasedBiomeLayerContextResolver::new, LongBasedBiomeLayerContextResolver::seed);

		@Override
		public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory) {
			return factory.createContext(this.seed());
		}
	}

	// Creates a random context from a string-based seed
	public static record StringBasedBiomeLayerContextResolver(String seed) implements BiomeLayerRandomContextResolver {
		public static final Codec<StringBasedBiomeLayerContextResolver> CODEC = Codec.STRING.xmap(StringBasedBiomeLayerContextResolver::new, StringBasedBiomeLayerContextResolver::seed);

		@Override
		public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory) {
			return factory.createContext(this.seed());
		}
	}
}