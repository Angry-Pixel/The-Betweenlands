package thebetweenlands.api.world.biome.layer.context;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

@FunctionalInterface
public interface BiomeLayerRandomContextResolver {
	public static final Codec<? extends BiomeLayerRandomContextResolver> CODEC = NeoForgeExtraCodecs.withAlternative(
			LongBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerRandomContextResolver.class, LongBasedBiomeLayerContextResolver.class)),
			StringBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerRandomContextResolver.class, StringBasedBiomeLayerContextResolver.class))
		);
	
	public static <T, U extends T> Function<? extends T, DataResult<U>> ensureClass(Class<T> parentClass, Class<U> childClass) {
		return (instance) -> childClass.isInstance(instance) ? DataResult.success(childClass.cast(instance)) : DataResult.error(() -> "Expected %s".formatted(childClass.getTypeName()));
	}
	
	public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory);

	public static record LongBasedBiomeLayerContextResolver(long seed) implements BiomeLayerRandomContextResolver {
		public static final Codec<LongBasedBiomeLayerContextResolver> CODEC = Codec.LONG
				.xmap(LongBasedBiomeLayerContextResolver::new, LongBasedBiomeLayerContextResolver::seed)
				.validate(resolver -> !(resolver instanceof LongBasedBiomeLayerContextResolver) ? DataResult.error(() -> "") : DataResult.success(resolver));

		@Override
		public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory) {
			return factory.createContext(this.seed());
		}
	}

	public static record StringBasedBiomeLayerContextResolver(String seed) implements BiomeLayerRandomContextResolver {
		public static final Codec<StringBasedBiomeLayerContextResolver> CODEC = Codec.STRING.xmap(StringBasedBiomeLayerContextResolver::new, StringBasedBiomeLayerContextResolver::seed);

		@Override
		public BiomeLayerRandomContext createContext(BiomeLayerRandomFactoryContext factory) {
			return factory.createContext(this.seed());
		}
	}
}