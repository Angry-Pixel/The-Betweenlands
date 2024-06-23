package thebetweenlands.common.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayerBetweenlands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

// Uses vonoli cells at the moment (i think i got gen levels to work again)
public class BetweenlandsBiomeProvider extends LegacyBiomeSource {

	public List<Double> biomeDistMod;
	public boolean isPreset = false;
	public long seed = 0;

	// Noises
	public XoroshiroRandomSource source;
	public List<Integer> list = List.of(-1, 1);

	public final Optional<PresetInstance> preset;


	// Use genlayers for surface biomes (y50 and up)
	public GenLayer genBiomes;
	public GenLayer biomeIndexLayer;

	// Voronoi biomes with a bit of perlin noise to mix it up a bit
	// (was my old implementation to mimic genlayers)
	public VoronoiCellNoise biomenoise;
	public List<Integer> biomeweight = List.of(25, 12, 5, 10, 4, 20, 16, 16); // old test weight list

	// For a forge chunk generator factory
	public BetweenlandsBiomeProvider(RegistryAccess registryAccess, long seed) {
		super(new Climate.ParameterList<Holder<Biome>>(new ArrayList<>()), new LegacyBiomeSourceSettings(), new ProviderGenLayerBetweenlands());
		this.preset = Optional.of(new PresetInstance(Preset.BETWEENLANDS, registryAccess.registryOrThrow(Registry.BIOME_REGISTRY)));
		this.seed = seed;
	}

	// Preset constructor for data packs
	// I'm hoping this isn't called too quick for 3d party modders to add biomes
	// TODO: i also intend to make a json codec for adding biomes using datapacks
	public BetweenlandsBiomeProvider(Climate.ParameterList<Holder<Biome>> biomelist, Optional<PresetInstance> preset, ProviderGenLayer genLayerProvider) {
		super(biomelist, new LegacyBiomeSourceSettings(), genLayerProvider);
		this.preset = preset;
	}

	public BetweenlandsBiomeProvider(Climate.ParameterList<Holder<Biome>> biomelist, ProviderGenLayer genLayerProvider) {
		this(biomelist, Optional.empty(), genLayerProvider);
	}

	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	public BiomeSource withSeed(long p_48274_) {
		this.seed = p_48274_;
		return this;
	}

	public Holder<Biome> getNoiseBiome(int p_187083_, int p_187084_, int p_187085_, Climate.Sampler p_187086_) {
		return this.getBetweenlandsNoiseBiome(p_187083_, p_187084_, p_187085_);
	}

	@VisibleForDebug
	public Holder<Biome> getNoiseBiome(Climate.TargetPoint p_187062_, int x, int y, int z) {
		return this.getBetweenlandsNoiseBiome(x, y, z);
	}

	// TODO: check if the team want to make the underground oasis into a proper underground biome
	// Cave biomes will use standard perlin noise, if y is above 47 surface biomes are always chosen
	@VisibleForDebug
	public Holder<Biome> getBetweenlandsNoiseBiome(int x, int y, int z) {
		Holder<Biome> outbiome; //= new Holder.Direct<>(this.parameters.values().get(0).getSecond().value());

		// Here we sample our biome noise to find the biome to place at the xyz input
		// voronoi cell noise

		// Minecraft samples biomes in 4 by 4 chunks, to get true block pos multiply cords by 4

		// Genlayer noise
		int biome = this.biomeCache.getBiome(x * 4, z * 4, 0);

		// TODO: fix all issues with biome cache so i can remove these safety checks
		if (biome < 0) {
			biome = 0;
		}

		if (biome >= this.biomes.size()) {
			biome = 0;
		}

		outbiome = this.biomes.get(biome);


		// original betweenlands biome generation code
		//Interpolate biome weights
		// TODO: change some of the code to use block based cords that we get from input
		// probably interperting all this wrong
		/*
		float fractionZ = (z % 4) / 4.0F;
		float fractionX = (x % 4) / 4.0F;
		int biomeWeightZ = z / 4;
		int biomeWeightX = x / 4;

		float weightXCZC = this.terrainBiomeWeights[biomeWeightX + biomeWeightZ * 5];
		float weightXNZC = this.terrainBiomeWeights[biomeWeightX+1 + biomeWeightZ * 5];
		float weightXCZN = this.terrainBiomeWeights[biomeWeightX + (biomeWeightZ+1) * 5];
		float weightXNZN = this.terrainBiomeWeights[biomeWeightX+1 + (biomeWeightZ+1) * 5];

		float interpZAxisXC = weightXCZC + (weightXCZN - weightXCZC) * fractionZ;
		float interpZAxisXN = weightXNZC + (weightXNZN - weightXNZC) * fractionZ;
		float currentVal = interpZAxisXC + (interpZAxisXN - interpZAxisXC) * fractionX;

		this.interpolatedTerrainBiomeWeights[x + z * 16] = currentVal;
		*/


		// Test generator below
		// very bad
		// Multy noise (eatch biome has an indipendent perlin noise instance)
		/*
		int count = this.parameters.values().size();
		double value = -1;

		// setup biomes
		if (this.biomeNoiseGenerator.size() == 0) {
			for (int index = 0; index < count; index++) {
				this.biomeNoiseGenerator.add(new SwamplandsNoiseGenerator(this.perlinnoise));
			}
		}

		// Sample deflector to roughen biome borders
		//double tx = this.getBiomeShiftX(x, z) * 0.25d;
		//double tz = this.getBiomeShiftZ(x, z) * 0.25d;

		// biome sample scales
		//List<Double> scales = new ArrayList<Double>();

		// Find biome to exede cull plane and has higher value over compediters
		for (int read = 0; read < count; read ++) {
			double mod = this.biomeNoiseGenerator.get(read).sample(((x) * 0.01f), 32 * read, ((z) * 0.01f));

			//BetweenlandsPort.LOGGER.info(mod);

			if (mod > value) {
				value = mod;
				outbiome = new Holder.Direct<>(this.parameters.values().get(read).getSecond().value());
			}
		}

		*/

		// Set biome
		return outbiome;
	}

	// TODO: add warning for people not using Betweenlands Biome instance
	// TODO: replace with legacyBiomeInstance in legacy biome provider
	public BiomeBetweenlands BiomeFromID(int id) {
		return BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY.get(id);
	}

	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z) {
		return this.getBetweenlandsNoiseBiome(x, y, z);
	}

	// Presets
	public static class Preset {
		static final Map<ResourceLocation, Preset> BY_NAME = Maps.newHashMap();
		// uses
		public static final Preset BETWEENLANDS = new Preset(TheBetweenlands.prefix("betweenlands_reg"), (registry) -> {

			return new Climate.ParameterList<>(BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY.stream().map((obj) -> {
				TheBetweenlands.LOGGER.info(obj.biome.getKey());
				return Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
					registry.getOrCreateHolder(obj.biome.getKey()));
			}).toList());
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.DEEPWATERS)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.SLUGEPLAINS)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.MARSH)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.ERODED_MARSH)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.PATCHY_ISLANDS)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.CORSE_ISLANDS)),
			//Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), registry.getOrCreateHolder(ResourceKeys.RASED_ISLES))));
		});
		final ResourceLocation name;
		private final Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> parameterSource;


		public Preset(ResourceLocation p_187090_, Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> p_187091_) {
			this.name = p_187090_;
			this.parameterSource = p_187091_;
			BY_NAME.put(p_187090_, this);
		}

		public BetweenlandsBiomeProvider biomeSource(PresetInstance preset, boolean usePreset) {
			Climate.ParameterList<Holder<Biome>> parameterlist = this.parameterSource.apply(preset.biomes());
			return new BetweenlandsBiomeProvider(parameterlist, usePreset ? Optional.of(preset) : Optional.empty(), new ProviderGenLayerBetweenlands());
		}

		public BetweenlandsBiomeProvider biomeSource(Registry<Biome> p_187105_, boolean p_187106_) {
			return this.biomeSource(new PresetInstance(this, p_187105_), p_187106_);
		}

		public BetweenlandsBiomeProvider biomeSource(Registry<Biome> p_187100_) {
			return this.biomeSource(p_187100_, true);
		}
	}

	public static record PresetInstance(Preset preset, Registry<Biome> biomes) {
		public static final MapCodec<PresetInstance> CODEC = RecordCodecBuilder.mapCodec((p_48558_) -> {
			return p_48558_.group(ResourceLocation.CODEC.flatXmap((p_151869_) -> {
				return Optional.ofNullable(Preset.BY_NAME.get(p_151869_)).map(DataResult::success).orElseGet(() -> {
					return DataResult.error("Unknown preset: " + p_151869_);
				});
			}, (p_151867_) -> {
				return DataResult.success(p_151867_.name);
			}).fieldOf("preset").stable().forGetter(PresetInstance::preset), RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(PresetInstance::biomes)).apply(p_48558_, p_48558_.stable(PresetInstance::new));
		});

		public BetweenlandsBiomeProvider biomeSource() {
			return this.preset.biomeSource(this, true);
		}
	}

	// Haven decided what to add as settings

	//public static final Codec<BetweenlandsBiomeProvider> CODEC = Codec.mapEither(BetweenlandsBiomeProvider.PresetInstance.CODEC, CLIMATE_DEFFINED_DIRECT_CODEC).xmap((codec) -> {
	//	return codec.map(BetweenlandsBiomeProvider.PresetInstance::biomeSource, Function.identity());
	//}, (biomeSource) -> {
	//	return biomeSource.preset.map(Either::<BetweenlandsBiomeProvider.PresetInstance, BetweenlandsBiomeProvider>left).orElseGet(() -> {
	//		return Either.right(biomeSource);
	//	});
	//}).codec();

	public static final MapCodec<BetweenlandsBiomeProvider> CLIMATE_DEFFINED_DIRECT_CODEC = RecordCodecBuilder.mapCodec((codec) -> {
		return codec.group(ExtraCodecs.<Pair<Climate.ParameterPoint, Holder<Biome>>>nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, Holder<Biome>>>create((obj) -> {
			return obj.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(obj, Pair::of);
		}).listOf()).xmap(Climate.ParameterList::new, (Function<Climate.ParameterList<Holder<Biome>>, List<Pair<Climate.ParameterPoint, Holder<Biome>>>>) Climate.ParameterList::values).fieldOf("biomes").forGetter((biomes) -> {
			return biomes.parameters;
		})).and(ProviderGenLayer.CODEC.fieldOf("provider").forGetter((obj2) -> {
			return obj2.genLayerProvider;
		})).apply(codec, BetweenlandsBiomeProvider::new);
	});

	public static final Codec<BetweenlandsBiomeProvider> CODEC = Codec.mapEither(PresetInstance.CODEC, CLIMATE_DEFFINED_DIRECT_CODEC).xmap((codec) -> {
		return codec.map(PresetInstance::biomeSource, Function.identity());
	}, (obj) -> {
		return obj.preset.map(Either::<PresetInstance, BetweenlandsBiomeProvider>left).orElseGet(() -> {
			return Either.right(obj);
		});
	}).codec();
	;
}
