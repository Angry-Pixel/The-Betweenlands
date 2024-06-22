package thebetweenlands.common.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import thebetweenlands.common.world.noisegenerators.genlayers.GenLayer;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayer;
import thebetweenlands.common.world.util.BiomeCache;

import javax.annotation.Nullable;
import java.util.List;

//import static thebetweenlands.common.world.LegacyBiomeSource.LegacyBiomeSourceSettings.SETTINGS_CODEC;

// A biome provider that use legacy genlayers
// No biome cache at the moment


public class LegacyBiomeSource extends BiomeSource {

	public final List<Holder<Biome>> biomes;
	public final BiomeCache biomeCache;
	// Climate is included for mod compatibility
	public final Climate.ParameterList<Holder<Biome>> parameters;

	public final LegacyBiomeSourceSettings settings;

	// Our GenLayers
	public ProviderGenLayer genLayerProvider;
	public GenLayer genBiomes;
	public GenLayer biomeIndexLayer;

	// GenLayer Supplier constructor
	public LegacyBiomeSource(Climate.ParameterList<Holder<Biome>> biomelist, LegacyBiomeSourceSettings settings, ProviderGenLayer genLayerProvider) {
		super(biomelist.values().stream().map(Pair::getSecond).toList());
		this.biomes = biomelist.values().stream().map(Pair::getSecond).toList();
		this.biomeCache = new BiomeCache(this);

		this.settings = settings;
		this.parameters = biomelist;

		this.genLayerProvider = genLayerProvider;

		// Call suppler to get GenLayers
		GenLayer[] genlayers = genLayerProvider.initialize(settings.seed);
		this.genBiomes = genlayers[0];
		this.biomeIndexLayer = genlayers[1];
	}

	// Climate default
	public LegacyBiomeSource(List<Holder<Biome>> biomelist, LegacyBiomeSourceSettings settings, ProviderGenLayer genLayerProvider) {
		this(new Climate.ParameterList<>(biomelist.stream().map(entry -> Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), entry)).toList()), settings, genLayerProvider);
	}

	public Holder<Biome> getNoiseBiome(int x, int y, int z) {
		return biomes.get(biomeCache.getBiome(x * 4, z * 4, 0));
	}

	public int[] legacyGetBiomesWithin(int[] biomes, int x, int y, int width, int height) {

		if (biomes == null || biomes.length < width * height) {
			biomes = new int[width * height];
		}

		int[] aint = this.genBiomes.getInts(x, y, width, height);

		if (width * height >= 0) System.arraycopy(aint, 0, biomes, 0, width * height);
		return biomes;
	}

	public int[] legacyGetBiomes(@Nullable int[] oldBiomeList, int x, int z, int width, int depth) {
		return this.legacyGetBiomes(oldBiomeList, x, z, width, depth, true);
	}

	public int[] legacyGetBiomes(@Nullable int[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new int[width * length];
		}

		if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
			int[] abiome = this.biomeCache.getCachedBiomes(x, z);
			System.arraycopy(abiome, 0, listToReuse, 0, width * length);
			return listToReuse;
		} else {
			int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

			if (width * length >= 0) System.arraycopy(aint, 0, listToReuse, 0, width * length);

			return listToReuse;
		}
	}

	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}

	@Override
	protected Codec<? extends BiomeSource> codec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	// we don't use Climate.Sampler so defalt back to
	@Override
	public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler p_204241_) {
		return this.getNoiseBiome(x, y, z);
	}

	public static class LegacyBiomeSourceSettings {
		public final long seed;

		// Settings codec
		public static final MapCodec<LegacyBiomeSourceSettings> SETTINGS_CODEC = RecordCodecBuilder.mapCodec((codec) -> {
			return codec.group(
					Codec.LONG.fieldOf("seed").forGetter(LegacyBiomeSourceSettings::getSeed)).
				apply(codec, LegacyBiomeSourceSettings::new);
		});

		public LegacyBiomeSourceSettings() {
			this(0);
		}

		public long getSeed() {
			return seed;
		}

		public LegacyBiomeSourceSettings(long seed) {
			this.seed = seed;
		}
	}

	// CODECS

	// Biome names and climate definition
	public static final MapCodec<LegacyBiomeSource> CLIMATE_DEFFINED_DIRECT_CODEC = RecordCodecBuilder.mapCodec((codec) -> {
		return codec.group(ExtraCodecs.<Pair<Climate.ParameterPoint, Holder<Biome>>>nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, Holder<Biome>>>create((parameter) -> {
			return parameter.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(parameter, Pair::of);
		}).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter((obj) -> {
			return obj.parameters;
		})).and(LegacyBiomeSourceSettings.SETTINGS_CODEC.fieldOf("settings").forGetter((obj) -> {
			return obj.settings;
		})).and(ProviderGenLayer.CODEC.fieldOf("provider").forGetter((obj) -> {
			return obj.genLayerProvider;
		})).apply(codec, LegacyBiomeSource::new);
	});

	// Genlayers must be registered before they can be found by codec, preferably using RegisterGenLayersEvent

	// Generator main codec
	public static final Codec<LegacyBiomeSource> CODEC = CLIMATE_DEFFINED_DIRECT_CODEC.codec();
}
