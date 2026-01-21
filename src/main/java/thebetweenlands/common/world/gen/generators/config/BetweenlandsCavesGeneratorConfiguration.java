package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexCache;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;

public final record BetweenlandsCavesGeneratorConfiguration(
		// Predicate of which blocks can even be replaced
		HolderSet<Block> replaceable,
		// caveNoiseSettings - Noise settings for the "cave" noise
		// surfaceOpeningNoiseSettings - Noise settings for the "surface opening" noise (how close the caves can get to the surface)
		// formNoiseSettings - Noise settings for the "form" noise (roughly controls the shape of the caves)
		// noiseSettings - Thread-safe cache for noise
		BLCaveNoiseSettings noiseSettings,
		// "Bottom" of the caves
		BlockHeightSelector minCaveHeight,
		// How far from the bottom should the caves start tapering off
		int minCaveHeightTaperDistance,
		// Provides the world surface
		BlockHeightSelector maxCaveHeight,
		// How far from the surface should the caves start tapering off (unless a surface opening should be present)
		int maxCaveHeightTaperDistance,
		// Default limit (gets tapered near the min/max cave height)
		double defaultNoiseLimit,
		// Cave water height
		int caveWaterHeight,
		// Blocks in bufferReplaceable will get replaced with terrain if they're within bufferNoiseLimit from a cave
		HolderSet<Block> bufferReplaceable, double bufferNoiseLimit,
		// Data for biomes
		HolderSet<Biome> biomesWithoutSurfaceOpenings,
		double noSurfaceOpeningNoiseOffset
	) implements EarlyGeneratorConfiguration {
	
	public static final Codec<BetweenlandsCavesGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter(BetweenlandsCavesGeneratorConfiguration::replaceable),
					
					BLCaveNoiseSettings.CODEC.forGetter(BetweenlandsCavesGeneratorConfiguration::noiseSettings),
					
					BlockHeightSelectors.codec().fieldOf("min_cave_height").forGetter(BetweenlandsCavesGeneratorConfiguration::minCaveHeight),
					Codec.INT.fieldOf("min_cave_height_taper_distance").forGetter(BetweenlandsCavesGeneratorConfiguration::minCaveHeightTaperDistance),
					
					BlockHeightSelectors.codec().fieldOf("max_cave_height").forGetter(BetweenlandsCavesGeneratorConfiguration::maxCaveHeight),
					Codec.INT.fieldOf("max_cave_height_taper_distance").forGetter(BetweenlandsCavesGeneratorConfiguration::maxCaveHeightTaperDistance),
					
					Codec.DOUBLE.fieldOf("default_noise_limit").forGetter(BetweenlandsCavesGeneratorConfiguration::defaultNoiseLimit),
					
					Codec.INT.fieldOf("cave_water_height").forGetter(BetweenlandsCavesGeneratorConfiguration::caveWaterHeight),

					RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("buffer_replaceable").forGetter(BetweenlandsCavesGeneratorConfiguration::bufferReplaceable),
					Codec.DOUBLE.fieldOf("buffer_noise_limit").forGetter(BetweenlandsCavesGeneratorConfiguration::bufferNoiseLimit),
					
					Biome.LIST_CODEC.fieldOf("biomes_without_surface_openings").forGetter(BetweenlandsCavesGeneratorConfiguration::biomesWithoutSurfaceOpenings),
					Codec.DOUBLE.fieldOf("no_surface_opening_noise_offset").forGetter(BetweenlandsCavesGeneratorConfiguration::noSurfaceOpeningNoiseOffset)
			).apply(instance, BetweenlandsCavesGeneratorConfiguration::new));

	public BetweenlandsCavesGeneratorConfiguration(
			HolderSet<Block> replaceable,
			FractalOpenSimplexNoiseSettings3D caveNoiseSettings,
			FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings,
			FractalOpenSimplexNoiseSettings3D formNoiseSettings,
			BlockHeightSelector minCaveHeight, int minCaveHeightTaperDistance,
			BlockHeightSelector maxCaveHeight, int maxCaveHeightTaperDistance,
			double defaultNoiseLimit,
			int caveWaterHeight,
			HolderSet<Block> bufferReplaceable, double bufferNoiseLimit,
			HolderSet<Biome> biomesWithoutSurfaceOpenings,
			double noSurfaceOpeningNoiseOffset
	) {
		this(
				replaceable,
				new BLCaveNoiseSettings(caveNoiseSettings, surfaceOpeningNoiseSettings, formNoiseSettings),
				minCaveHeight, minCaveHeightTaperDistance,
				maxCaveHeight, maxCaveHeightTaperDistance,
				defaultNoiseLimit,
				caveWaterHeight,
				bufferReplaceable, bufferNoiseLimit,
				biomesWithoutSurfaceOpenings, noSurfaceOpeningNoiseOffset
			);
	}
	
	// Noise accessors
	
	public FractalOpenSimplexNoiseSettings3D caveNoiseSettings() {
		return this.noiseSettings.caveNoiseSettings();
	}

	public FractalOpenSimplexNoiseSettings3D formNoiseSettings() {
		return this.noiseSettings.formNoiseSettings();
	}

	public FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings() {
		return this.noiseSettings.surfaceOpeningNoiseSettings();
	}
	
	public TriFractalOpenSimplexCache noiseCache() {
		return this.noiseSettings.noiseCache();
	}
	
	public static class BLCaveNoiseSettings {
		public static final MapCodec<BLCaveNoiseSettings> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("cave_noise").forGetter(BLCaveNoiseSettings::caveNoiseSettings),
						FractalOpenSimplexNoiseSettings2D.CODEC.fieldOf("surface_opening_noise").forGetter(BLCaveNoiseSettings::surfaceOpeningNoiseSettings),
						FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("form_noise").forGetter(BLCaveNoiseSettings::formNoiseSettings)
				).apply(instance, BLCaveNoiseSettings::new));

		// Noise settings for the "cave" noise
		private final FractalOpenSimplexNoiseSettings3D caveNoiseSettings;
		// Noise settings for the "surface opening" noise (how close the caves can get to the surface)
		private final FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings;
		// Noise settings for the "form" noise (roughly controls the shape of the caves)
		private final FractalOpenSimplexNoiseSettings3D formNoiseSettings;
		// Thread-safe cache for noise
		private final TriFractalOpenSimplexCache noiseCache;
		
		public BLCaveNoiseSettings(
				FractalOpenSimplexNoiseSettings3D caveNoiseSettings,
				FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings,
				FractalOpenSimplexNoiseSettings3D formNoiseSettings
			) {
			this.caveNoiseSettings = caveNoiseSettings;
			this.surfaceOpeningNoiseSettings = surfaceOpeningNoiseSettings;
			this.formNoiseSettings = formNoiseSettings;
			this.noiseCache = new TriFractalOpenSimplexCache(caveNoiseSettings.octaves(), caveNoiseSettings.additiveSeed(), surfaceOpeningNoiseSettings.octaves(), surfaceOpeningNoiseSettings.additiveSeed(), formNoiseSettings.octaves(), formNoiseSettings.additiveSeed());
		}

		public FractalOpenSimplexNoiseSettings3D caveNoiseSettings() {
			return this.caveNoiseSettings;
		}

		public FractalOpenSimplexNoiseSettings3D formNoiseSettings() {
			return this.formNoiseSettings;
		}

		public FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings() {
			return this.surfaceOpeningNoiseSettings;
		}

		public TriFractalOpenSimplexCache noiseCache() {
			return this.noiseCache;
		}
	}
}
