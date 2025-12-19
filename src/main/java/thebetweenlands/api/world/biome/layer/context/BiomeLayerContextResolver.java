package thebetweenlands.api.world.biome.layer.context;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import thebetweenlands.api.world.biome.layer.Area;

@FunctionalInterface
public interface BiomeLayerContextResolver {
	public static final Codec<? extends BiomeLayerContextResolver> CODEC = NeoForgeExtraCodecs.withAlternative(
			LongBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerContextResolver.class, LongBasedBiomeLayerContextResolver.class)),
			StringBasedBiomeLayerContextResolver.CODEC.flatComapMap(Function.identity(), ensureClass(BiomeLayerContextResolver.class, StringBasedBiomeLayerContextResolver.class))
		);
	
	public static <T, U extends T> Function<? extends T, DataResult<U>> ensureClass(Class<T> parentClass, Class<U> childClass) {
		return (instance) -> childClass.isInstance(instance) ? DataResult.success(childClass.cast(instance)) : DataResult.error(() -> "Expected %s".formatted(childClass.getTypeName()));
	}
	
	public <A extends Area> BiomeLayerContext<A> createContext(BiomeLayerContextFactory<A> factory);

	public static record LongBasedBiomeLayerContextResolver(long seed) implements BiomeLayerContextResolver {
		public static final Codec<LongBasedBiomeLayerContextResolver> CODEC = Codec.LONG
				.xmap(LongBasedBiomeLayerContextResolver::new, LongBasedBiomeLayerContextResolver::seed)
				.validate(resolver -> !(resolver instanceof LongBasedBiomeLayerContextResolver) ? DataResult.error(() -> "") : DataResult.success(resolver));

		@Override
		public <A extends Area> BiomeLayerContext<A> createContext(BiomeLayerContextFactory<A> factory) {
			return factory.createContext(this.seed());
		}
	}

	public static record StringBasedBiomeLayerContextResolver(String seed) implements BiomeLayerContextResolver {
		public static final Codec<StringBasedBiomeLayerContextResolver> CODEC = Codec.STRING.xmap(StringBasedBiomeLayerContextResolver::new, StringBasedBiomeLayerContextResolver::seed);

		@Override
		public <A extends Area> BiomeLayerContext<A> createContext(BiomeLayerContextFactory<A> factory) {
			return factory.createContext(this.seed());
		}
	}
}