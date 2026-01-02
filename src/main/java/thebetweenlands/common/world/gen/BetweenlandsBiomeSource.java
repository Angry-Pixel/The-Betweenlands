package thebetweenlands.common.world.gen;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext.AreaFactoryContextSupplier;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
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
		BiomeLayerRandomContextResolver contextResolver = configuredBiomeLayer.contextResolver().value();

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
}
