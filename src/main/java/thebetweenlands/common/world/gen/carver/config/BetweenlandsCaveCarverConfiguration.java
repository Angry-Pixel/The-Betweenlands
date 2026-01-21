package thebetweenlands.common.world.gen.carver.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexCache;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;

public final class BetweenlandsCaveCarverConfiguration extends CarverConfiguration {
	
	public static final Codec<BetweenlandsCaveCarverConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					CarverConfiguration.CODEC.forGetter(obj -> obj),
					BLCaveNoiseSettings.CODEC.forGetter(BetweenlandsCaveCarverConfiguration::noiseSettings),
					
					BlockHeightSelectors.codec().fieldOf("min_cave_height").forGetter(BetweenlandsCaveCarverConfiguration::minCaveHeight),
					Codec.INT.fieldOf("min_cave_height_taper_distance").forGetter(BetweenlandsCaveCarverConfiguration::minCaveHeightTaperDistance),
					BlockHeightSelectors.codec().fieldOf("max_cave_height").forGetter(BetweenlandsCaveCarverConfiguration::maxCaveHeight),
					Codec.INT.fieldOf("min_cave_height_taper_distance").forGetter(BetweenlandsCaveCarverConfiguration::maxCaveHeightTaperDistance),
					
					Codec.DOUBLE.fieldOf("default_noise_limit").forGetter(BetweenlandsCaveCarverConfiguration::defaultNoiseLimit),
					
					Codec.INT.fieldOf("cave_water_height").forGetter(BetweenlandsCaveCarverConfiguration::caveWaterHeight),
					
					RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("buffer_replaceable").forGetter(BetweenlandsCaveCarverConfiguration::bufferReplaceable),
					Codec.DOUBLE.fieldOf("buffer_noise_limit").forGetter(BetweenlandsCaveCarverConfiguration::bufferNoiseLimit),
					
					Biome.LIST_CODEC.fieldOf("biomes_without_surface_openings").forGetter(BetweenlandsCaveCarverConfiguration::biomesWithoutSurfaceOpenings),
					Codec.DOUBLE.fieldOf("no_surface_opening_noise_offset").forGetter(BetweenlandsCaveCarverConfiguration::noSurfaceOpeningNoiseOffset)
			).apply(instance, BetweenlandsCaveCarverConfiguration::new));
	
	// caveNoiseSettings - Noise settings for the "cave" noise
	// surfaceOpeningNoiseSettings - Noise settings for the "surface opening" noise (how close the caves can get to the surface)
	// formNoiseSettings - Noise settings for the "form" noise (roughly controls the shape of the caves)
	private final BLCaveNoiseSettings noiseSettings;
	// Thread-safe cache for noise
	private final TriFractalOpenSimplexCache noiseCache;
	
	// "Bottom" of the caves
	private final BlockHeightSelector minCaveHeight;
	// How far from the bottom should the caves start tapering off
	private final int minCaveHeightTaperDistance;
	
	// Provides the world surface
	private final BlockHeightSelector maxCaveHeight;
	// How far from the surface should the caves start tapering off (unless a surface opening should be present)
	private final int maxCaveHeightTaperDistance;
	
	// Default limit (gets tapered near the min/max cave height)
	private final double defaultNoiseLimit;
	
	// Cave water height
	private final int caveWaterHeight;
	
	// Blocks in the replace predicate near the caves will get replaced with terrain
	private final HolderSet<Block> bufferReplaceable;
	private final double bufferNoiseLimit;
	
	private final HolderSet<Biome> biomesWithoutSurfaceOpenings;
	private final double noSurfaceOpeningNoiseOffset;
	
	public BetweenlandsCaveCarverConfiguration(
			CarverConfiguration config,
			BLCaveNoiseSettings noiseSettings,
			BlockHeightSelector minCaveHeight, int minCaveHeightTaperDistance,
			BlockHeightSelector maxCaveHeight, int maxCaveHeightTaperDistance,
			double defaultNoiseLimit,
			int caveWaterHeight,
			HolderSet<Block> bufferReplaceable, double bufferNoiseLimit,
			HolderSet<Biome> biomesWithoutSurfaceOpenings,
			double noSurfaceOpeningNoiseOffset
	) {
		super(config.probability, config.y, config.yScale, config.lavaLevel, config.debugSettings, config.replaceable);
		this.noiseSettings = noiseSettings;

		FractalOpenSimplexNoiseSettings3D caveNoiseSettings = noiseSettings.caveNoiseSettings();
		FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings = noiseSettings.surfaceOpeningNoiseSettings();
		FractalOpenSimplexNoiseSettings3D formNoiseSettings = noiseSettings.formNoiseSettings();
		
		this.noiseCache = new TriFractalOpenSimplexCache(caveNoiseSettings.octaves(), caveNoiseSettings.additiveSeed(), surfaceOpeningNoiseSettings.octaves(), surfaceOpeningNoiseSettings.additiveSeed(), formNoiseSettings.octaves(), formNoiseSettings.additiveSeed());

		this.minCaveHeight = minCaveHeight;
		this.minCaveHeightTaperDistance = minCaveHeightTaperDistance;
		
		this.maxCaveHeight = maxCaveHeight;
		this.maxCaveHeightTaperDistance = maxCaveHeightTaperDistance;
		
		this.defaultNoiseLimit = defaultNoiseLimit;
		this.caveWaterHeight = caveWaterHeight;
		
		this.bufferReplaceable = bufferReplaceable;
		this.bufferNoiseLimit = bufferNoiseLimit;
		
		this.biomesWithoutSurfaceOpenings = biomesWithoutSurfaceOpenings;
		this.noSurfaceOpeningNoiseOffset = noSurfaceOpeningNoiseOffset;
	}

	// Noise
	
	public FractalOpenSimplexNoiseSettings3D caveNoiseSettings() {
		return this.noiseSettings.caveNoiseSettings();
	}

	public FractalOpenSimplexNoiseSettings3D formNoiseSettings() {
		return this.noiseSettings.formNoiseSettings();
	}

	public FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings() {
		return this.noiseSettings.surfaceOpeningNoiseSettings();
	}

	public BLCaveNoiseSettings noiseSettings() {
		return this.noiseSettings;
	}
	
	public TriFractalOpenSimplexCache noiseCache() {
		return this.noiseCache;
	}
	
	// Min height
	
	public BlockHeightSelector minCaveHeight() {
		return this.minCaveHeight;
	}
	
	public int minCaveHeightTaperDistance() {
		return this.minCaveHeightTaperDistance;
	}
	
	// Max height
	
	public BlockHeightSelector maxCaveHeight() {
		return this.maxCaveHeight;
	}
	
	public int maxCaveHeightTaperDistance() {
		return this.maxCaveHeightTaperDistance;
	}
	
	// Default noise limit
	
	public double defaultNoiseLimit() {
		return this.defaultNoiseLimit;
	}
	
	public int caveWaterHeight() {
		return this.caveWaterHeight;
	}
	
	// Buffer

	public HolderSet<Block> bufferReplaceable() {
		return this.bufferReplaceable;
	}
	
	public double bufferNoiseLimit() {
		return this.bufferNoiseLimit;
	}

	// Biome surface openings
	
	public HolderSet<Biome> biomesWithoutSurfaceOpenings() {
		return this.biomesWithoutSurfaceOpenings;
	}
	
	public double noSurfaceOpeningNoiseOffset() {
		return this.noSurfaceOpeningNoiseOffset;
	}
	
	public static record BLCaveNoiseSettings(
			// Noise settings for the "cave" noise
			FractalOpenSimplexNoiseSettings3D caveNoiseSettings,
			// Noise settings for the "surface opening" noise (how close the caves can get to the surface)
			FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings,
			// Noise settings for the "form" noise (roughly influences the shape of the caves)
			FractalOpenSimplexNoiseSettings3D formNoiseSettings
	) {
		public static final MapCodec<BLCaveNoiseSettings> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("cave_noise").forGetter(BLCaveNoiseSettings::caveNoiseSettings),
						FractalOpenSimplexNoiseSettings2D.CODEC.fieldOf("surface_opening_noise").forGetter(BLCaveNoiseSettings::surfaceOpeningNoiseSettings),
						FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("form_noise").forGetter(BLCaveNoiseSettings::formNoiseSettings)
				).apply(instance, BLCaveNoiseSettings::new));
	}
}
