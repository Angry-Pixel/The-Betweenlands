package thebetweenlands.common.world.gen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.gen.layer.*;
import thebetweenlands.common.world.gen.layer.util.*;
import thebetweenlands.common.world.gen.warp.TerrainPoint;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;

public class BetweenlandsBiomeSource extends BiomeSource {

	public static final MapCodec<BetweenlandsBiomeSource> BL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		RecordCodecBuilder.<Pair<TerrainPoint, Holder<Biome>>>create((pair) -> pair.group(
			TerrainPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst),
			Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)
		).apply(pair, Pair::of)).listOf().fieldOf("biomes").forGetter((object) -> object.list),
		Codec.FLOAT.fieldOf("base_offset").forGetter((object) -> object.offset),
		Codec.FLOAT.fieldOf("base_factor").forGetter((object) -> object.factor),
		ExtraCodecs.POSITIVE_INT.fieldOf("biome_size").forGetter((object) -> object.biomeSize),
		RegistryOps.retrieveGetter(Registries.BIOME)
	).apply(instance, BetweenlandsBiomeSource::new));

	private Layer genBiomes;
	private final List<Pair<TerrainPoint, Holder<Biome>>> list;
	private final float offset;
	private final float factor;
	private final int biomeSize;
	private final HolderGetter<Biome> registry;

	public BetweenlandsBiomeSource(List<Pair<TerrainPoint, Holder<Biome>>> list, float offset, float factor, int biomeSize, HolderGetter<Biome> registry) {
		this.list = list;
		this.offset = offset;
		this.factor = factor;
		this.biomeSize = biomeSize;
		this.registry = registry;
	}

	@Override
	protected MapCodec<? extends BiomeSource> codec() {
		return BL_CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return this.list.stream().map(Pair::getSecond);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		this.lazyLoad();
		return this.genBiomes.get(registry, x, z);
	}

	public float getBaseOffset() {
		return this.offset;
	}

	public float getBaseFactor() {
		return this.factor;
	}

	public float getBiomeDepth(int x, int y, int z, Climate.Sampler sampler) {
		Biome biome = this.getNoiseBiome(x, y, z, sampler).value();
		return this.getBiomeDepth(biome);
	}

	public float getBiomeDepth(Biome biome) {
		return this.getBiomeValue(biome, TerrainPoint::depth);
	}

	public float getBiomeScale(int x, int y, int z, Climate.Sampler sampler) {
		Biome biome = this.getNoiseBiome(x, y, z, sampler).value();
		return this.getBiomeScale(biome);
	}

	public float getBiomeScale(Biome biome) {
		return getBiomeValue(biome, TerrainPoint::scale);
	}

	private float getBiomeValue(Biome biome, Function<? super TerrainPoint, Float> function) {
		this.lazyLoad();
		return this.list.stream().filter(p -> p.getSecond().value().equals(biome)).map(Pair::getFirst).map(function).findFirst().orElse(0.0F);
	}

	private void lazyLoad() {
		if (genBiomes == null) {
			this.genBiomes = makeLayers(getSeed(), registry, list, biomeSize);
		}
	}

	public static int getBiomeId(ResourceKey<Biome> biome, HolderGetter<Biome> registry) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.BIOME).getId(registry.get(biome).get().value());
	}

	public static long getSeed() {
		return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getWorldData().worldGenOptions().seed();
	}

	public static Layer makeLayers(long seed, HolderGetter<Biome> registry, List<Pair<TerrainPoint, Holder<Biome>>> biomes, int size) {
		AreaFactory<LazyArea> areaFactory = makeLayers((context) -> new LazyAreaContext(25, seed, context), biomes, registry, size);
		return new Layer(areaFactory);
	}

	public static <A extends Area, C extends BigContext<A>> AreaFactory<A> makeLayers(LongFunction<C> context, List<Pair<TerrainPoint, Holder<Biome>>> biomes, HolderGetter<Biome> registry, int size) {
		AreaFactory<A> genLayer = new BetweenlandsBiomeLayer(registry, biomes).run(context.apply(100L));
		genLayer = BetweenlandsBiomeSource.repeatZoom(2000L, genLayer, 2, context);

//		AreaFactory<A> swamplandsClearingLayer = new SurroundedLayer(registry, BiomeRegistry.SWAMPLANDS, BiomeRegistry.SWAMPLANDS_CLEARING, 1, 1).run(context.apply(102L), genLayer);
//		swamplandsClearingLayer = new MaskLayer(registry, BiomeRegistry.SWAMPLANDS_CLEARING, BiomeRegistry.SWAMPLANDS_CLEARING).run(context.apply(102L), swamplandsClearingLayer);
//		swamplandsClearingLayer = BetweenlandsBiomeSource.repeatThin(105L, swamplandsClearingLayer, registry, BiomeRegistry.SWAMPLANDS_CLEARING, 3, 0.25F, 10, context);

		genLayer = BetweenlandsBiomeSource.repeatZoom(2345L, genLayer, 1, context);
//		swamplandsClearingLayer = BetweenlandsBiomeSource.repeatZoom(2345L, swamplandsClearingLayer, 1, context);

//		AreaFactory<A> sludgePlainsClearingLayer = new SurroundedLayer(registry, BiomeRegistry.SLUDGE_PLAINS, BiomeRegistry.SLUDGE_PLAINS_CLEARING, 2, 1).run(context.apply(351L), genLayer);
//		sludgePlainsClearingLayer = new MaskLayer(registry, BiomeRegistry.SWAMPLANDS_CLEARING, BiomeRegistry.SWAMPLANDS_CLEARING).run(context.apply(351L), sludgePlainsClearingLayer);
//		sludgePlainsClearingLayer = BetweenlandsBiomeSource.repeatThin(214L, sludgePlainsClearingLayer, registry, BiomeRegistry.SWAMPLANDS_CLEARING, 4, 0.15F, 20, context);

		genLayer = BetweenlandsBiomeSource.repeatZoom(2345L, genLayer, size - 1, context);
//		swamplandsClearingLayer = BetweenlandsBiomeSource.repeatZoom(2345L, swamplandsClearingLayer, size - 1, context);
//		sludgePlainsClearingLayer = BetweenlandsBiomeSource.repeatZoom(2345L, sludgePlainsClearingLayer, size - 1 - 2, context);

//		sludgePlainsClearingLayer = new CircleMaskLayer(registry, BiomeRegistry.SLUDGE_PLAINS_CLEARING, 10).run(context.apply(103L), sludgePlainsClearingLayer);
//		sludgePlainsClearingLayer = BetweenlandsBiomeSource.repeatZoom(2345L, sludgePlainsClearingLayer, 2, context);

//		swamplandsClearingLayer = new CircleMaskLayer(registry, BiomeRegistry.SWAMPLANDS_CLEARING, 10).run(context.apply(103L), swamplandsClearingLayer);

//		genLayer = MixerLayer.INSTANCE.run(context.apply(0L), genLayer, swamplandsClearingLayer);
//		genLayer = MixerLayer.INSTANCE.run(context.apply(0L), genLayer, sludgePlainsClearingLayer);

		return genLayer;
	}

	private static <T extends Area, C extends BigContext<T>> AreaFactory<T> repeatZoom(long seed, AreaFactory<T> layer, int count, LongFunction<C> contextFactory) {
		AreaFactory<T> iareafactory = layer;

		for(int i = 0; i < count; ++i) {
			iareafactory = new ZoomIncrementLayer().run(contextFactory.apply(seed + (long)i), iareafactory);
		}

		return iareafactory;
	}

	private static <T extends Area, C extends BigContext<T>> AreaFactory<T> repeatThin(long seed, AreaFactory<T> layer, HolderGetter<Biome> registry, ResourceKey<Biome> biome, int range, float chance, int count, LongFunction<C> contextFactory) {
		AreaFactory<T> iareafactory = layer;

		for (int i = 0; i < count; ++i) {
			iareafactory = new ThinMaskLayer(registry, biome, range, chance).run(contextFactory.apply(seed + 1), iareafactory);
		}

		return iareafactory;
	}
}
