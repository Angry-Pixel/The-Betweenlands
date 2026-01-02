package thebetweenlands.common.world.gen;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import thebetweenlands.api.world.biome.IBetweenlandsBiomeSource;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext.AreaFactoryContextSupplier;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.common.world.gen.layer.old.BetweenlandsBiomeLayerOld;
import thebetweenlands.common.world.gen.layer.old.ThinMaskLayer;
import thebetweenlands.common.world.gen.layer.old.ZoomIncrementLayer;
import thebetweenlands.common.world.gen.layer.old.util.BigContext;
import thebetweenlands.common.world.gen.layer.old.util.LazyAreaContextOld;
import thebetweenlands.common.world.gen.layer.util.BLWeightPoint;
import thebetweenlands.common.world.gen.layer.util.Layer;
import thebetweenlands.common.world.gen.layer.util.LazyArea;
import thebetweenlands.common.world.gen.layer.util.LazyAreaFactoryContext;
import thebetweenlands.common.world.gen.layer.util.LinearCongruentialRandomContext.LinearCongruentialRandomFactory;
import thebetweenlands.common.world.gen.warp.BLBiomeData;
import thebetweenlands.common.world.gen.warp.TerrainPoint;

public class BetweenlandsBiomeSource extends BiomeSource implements IBetweenlandsBiomeSource {

	public static final MapCodec<BetweenlandsBiomeSource> BL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		BLBiomeData.CODEC.listOf().fieldOf("biomes").forGetter((object) -> object.list),
		BiomeLayerConfigured.CODEC.fieldOf("biome_layers").forGetter((object) -> object.genBiomeLayers),
		Codec.floatRange(0.0F, 1.0F).fieldOf("surface_depth").forGetter((object) -> object.surfaceDepth),
		Codec.FLOAT.optionalFieldOf("global_factor", 1.0F).forGetter((object) -> object.globalFactor),
		ExtraCodecs.POSITIVE_INT.fieldOf("biome_size").forGetter((object) -> object.biomeSize),
		RegistryOps.retrieveGetter(Registries.BIOME)
	).apply(instance, BetweenlandsBiomeSource::new));

	private Layer genBiomes;
	private final List<BLBiomeData> list;
	private final BiomeLayerConfigured genBiomeLayers;
	private final float surfaceDepth; // The "depth" of the surface, as a factor of the world height: 0.46875 in a 256-high world means the surface is at y = 0.46875 * 256 = 120;
	private final float globalFactor; // Global multiplier that is used to multiply the output of the biome-related density
	private final int biomeSize;
	private final HolderGetter<Biome> registry;

	public BetweenlandsBiomeSource(List<BLBiomeData> list, BiomeLayerConfigured genBiomeLayers, float surfaceDepth, float globalFactor, int biomeSize, HolderGetter<Biome> registry) {
		this.list = list;
		this.genBiomeLayers = genBiomeLayers;
		this.surfaceDepth = surfaceDepth;
		this.globalFactor = globalFactor;
		this.biomeSize = biomeSize;
		this.registry = registry;
	}

	@Override
	protected MapCodec<? extends BiomeSource> codec() {
		return BL_CODEC;
	}

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return this.list.stream().map(BLBiomeData::biome);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
		this.lazyLoad();
		return this.genBiomes.get(registry, x, z);
	}

	@Override
	public float getSurfaceDepth() {
		return this.surfaceDepth;
	}

	@Override
	public float getGlobalFactor() {
		return this.globalFactor;
	}

	@Override
	public float getBiomeDepth(int x, int y, int z, Climate.Sampler sampler) {
		Holder<Biome> biome = this.getNoiseBiome(x, y, z, sampler);
		return this.getBiomeDepth(biome);
	}

	@Override
	public float getBiomeDepth(Holder<Biome> biome) {
		return this.getBiomeValue(biome, TerrainPoint::depth);
	}

	@Override
	public float getBiomeScale(int x, int y, int z, Climate.Sampler sampler) {
		Holder<Biome> biome = this.getNoiseBiome(x, y, z, sampler);
		return this.getBiomeScale(biome);
	}

	@Override
	public float getBiomeScale(Holder<Biome> biome) {
		return getBiomeValue(biome, TerrainPoint::scale);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> getBiomeGenerators(Holder<Biome> biome) {
		this.lazyLoad();
		return this.list.stream().filter(p -> p.biome().is(biome)).map(BLBiomeData::generators).findFirst().orElseGet(List::of);
	}

	@SuppressWarnings("deprecation")
	private float getBiomeValue(Holder<Biome> biome, Function<? super TerrainPoint, Float> function) {
		this.lazyLoad();
		return this.list.stream().filter(p -> p.biome().is(biome)).map(BLBiomeData::terrainPoint).map(function).findFirst().orElse(0.0F);
	}

	private void lazyLoad() {
		if (genBiomes == null) {
//			this.genBiomes = makeLayers(getSeed(), registry, list, biomeSize);
			this.genBiomes = this.makeLayers(getSeed());
		}
	}

	public Layer makeLayers(long worldSeed) {
		BiomeLayerConfigured configuredBiomeLayer = this.genBiomeLayers;
		BiomeLayer biomeLayer = configuredBiomeLayer.biomeLayer();
		BiomeLayerRandomContextResolver contextResolver = configuredBiomeLayer.contextResolver();

		// Area factory context
		AreaFactoryContextSupplier<LazyArea> areaContext = () -> new LazyAreaFactoryContext(25);
		
		// Random factory context generator
		LinearCongruentialRandomFactory randomFactory = new LinearCongruentialRandomFactory(worldSeed);
		
		// Random factory context
		BiomeLayerRandomContext randomContext = contextResolver.createContext(randomFactory);
		
		// Layer chain + full context
		BiomeLayerChain biomeLayerChain = new BiomeLayerChain();
		BiomeLayerContext<LazyArea> context = new BiomeLayerContext<LazyArea>(areaContext, randomContext);
		
		// Compose & create area factory
		biomeLayer.compose(context, biomeLayerChain);
		AreaFactory<LazyArea> areaFactory = biomeLayer.createAreaFactory(context, biomeLayerChain);
		
		return new Layer(areaFactory);
	}

	// TODO see how feasible it is to use holders instead of biome ids
	public static int getBiomeId(ResourceKey<Biome> biome, HolderGetter<Biome> registry) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.BIOME).getId(registry.get(biome).get().value());
	}

	// TODO see how feasible it is to use holders instead of biome ids
	public static int getBiomeId(Holder<Biome> biome) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.BIOME).getId(biome.value());
	}

	public static long getSeed() {
		return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getWorldData().worldGenOptions().seed();
	}

	public static Layer makeLayers(long seed, HolderGetter<Biome> registry, List<BLWeightPoint> biomes, int size) {
		AreaFactory<LazyArea> areaFactory = makeLayers((context) -> new LazyAreaContextOld(25, seed, context), biomes, registry, size);
		return new Layer(areaFactory);
	}

	public static <A extends Area, C extends BigContext<A>> AreaFactory<A> makeLayers(LongFunction<C> context, List<BLWeightPoint> biomes, HolderGetter<Biome> registry, int size) {
		AreaFactory<A> genLayer = new BetweenlandsBiomeLayerOld(registry, biomes).run(context.apply(100L));
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
