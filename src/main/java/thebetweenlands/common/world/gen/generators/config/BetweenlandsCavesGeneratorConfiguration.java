package thebetweenlands.common.world.gen.generators.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import thebetweenlands.api.world.generator.EarlyGeneratorConfiguration;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors;
import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexCache;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;

public final class BetweenlandsCavesGeneratorConfiguration implements EarlyGeneratorConfiguration {
	
	public static final Codec<BetweenlandsCavesGeneratorConfiguration> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("cave_noise").forGetter(BetweenlandsCavesGeneratorConfiguration::caveNoiseSettings),
					FractalOpenSimplexNoiseSettings2D.CODEC.fieldOf("surface_opening_noise").forGetter(BetweenlandsCavesGeneratorConfiguration::surfaceOpeningNoiseSettings),
					FractalOpenSimplexNoiseSettings3D.CODEC.fieldOf("form_noise").forGetter(BetweenlandsCavesGeneratorConfiguration::formNoiseSettings),
					
					BlockHeightSelectors.codec().fieldOf("min_cave_height").forGetter(BetweenlandsCavesGeneratorConfiguration::minCaveHeight),
					Codec.INT.fieldOf("min_cave_height_taper_distance").forGetter(BetweenlandsCavesGeneratorConfiguration::minCaveHeightTaperDistance),
					BlockHeightSelectors.codec().fieldOf("max_cave_height").forGetter(BetweenlandsCavesGeneratorConfiguration::maxCaveHeight),
					Codec.INT.fieldOf("min_cave_height_taper_distance").forGetter(BetweenlandsCavesGeneratorConfiguration::maxCaveHeightTaperDistance),
					
					Codec.DOUBLE.fieldOf("default_noise_limit").forGetter(BetweenlandsCavesGeneratorConfiguration::defaultNoiseLimit),
					
					Codec.INT.fieldOf("cave_water_height").forGetter(BetweenlandsCavesGeneratorConfiguration::maxCaveHeightTaperDistance),
					
					BlockPredicate.CODEC.fieldOf("buffer_replace_predicate").forGetter(BetweenlandsCavesGeneratorConfiguration::bufferBlockReplacePredicate),
					Codec.DOUBLE.fieldOf("buffer_noise_limit").forGetter(BetweenlandsCavesGeneratorConfiguration::bufferNoiseLimit),
					
					Biome.LIST_CODEC.fieldOf("biomes_without_surface_openings").forGetter(BetweenlandsCavesGeneratorConfiguration::biomesWithoutSurfaceOpenings),
					Codec.DOUBLE.fieldOf("no_surface_opening_noise_offset").forGetter(BetweenlandsCavesGeneratorConfiguration::noSurfaceOpeningNoiseOffset)
			).apply(instance, BetweenlandsCavesGeneratorConfiguration::new));
	
	// Noise settings for the "cave" noise
	private final FractalOpenSimplexNoiseSettings3D caveNoiseSettings;
	// Noise settings for the "surface opening" noise (how close the caves can get to the surface)
	private final FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings;
	// Noise settings for the "form" noise (roughly controls the shape of the caves)
	private final FractalOpenSimplexNoiseSettings3D formNoiseSettings;
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
	// TODO replace BlockPredicate with something that is just state-in-boolean-out
	private final BlockPredicate bufferReplacePredicate;
	private final double bufferNoiseLimit;
	
	private final HolderSet<Biome> biomesWithoutSurfaceOpenings;
	private final double noSurfaceOpeningNoiseOffset;
	
	public BetweenlandsCavesGeneratorConfiguration(
			FractalOpenSimplexNoiseSettings3D caveNoiseSettings,
			FractalOpenSimplexNoiseSettings2D surfaceOpeningNoiseSettings,
			FractalOpenSimplexNoiseSettings3D formNoiseSettings,
			BlockHeightSelector minCaveHeight, int minCaveHeightTaperDistance,
			BlockHeightSelector maxCaveHeight, int maxCaveHeightTaperDistance,
			double defaultNoiseLimit,
			int caveWaterHeight,
			BlockPredicate bufferReplacePredicate, double bufferNoiseLimit,
			HolderSet<Biome> biomesWithoutSurfaceOpenings,
			double noSurfaceOpeningNoiseOffset
	) {
		this.caveNoiseSettings = caveNoiseSettings;
		this.formNoiseSettings = formNoiseSettings;
		this.surfaceOpeningNoiseSettings = surfaceOpeningNoiseSettings;
		this.noiseCache = new TriFractalOpenSimplexCache(caveNoiseSettings.octaves(), caveNoiseSettings.additiveSeed(), surfaceOpeningNoiseSettings.octaves(), surfaceOpeningNoiseSettings.additiveSeed(), formNoiseSettings.octaves(), formNoiseSettings.additiveSeed());

		this.minCaveHeight = minCaveHeight;
		this.minCaveHeightTaperDistance = minCaveHeightTaperDistance;
		
		this.maxCaveHeight = maxCaveHeight;
		this.maxCaveHeightTaperDistance = maxCaveHeightTaperDistance;
		
		this.defaultNoiseLimit = defaultNoiseLimit;
		this.caveWaterHeight = caveWaterHeight;
		
		this.bufferReplacePredicate = bufferReplacePredicate;
		this.bufferNoiseLimit = bufferNoiseLimit;
		
		this.biomesWithoutSurfaceOpenings = biomesWithoutSurfaceOpenings;
		this.noSurfaceOpeningNoiseOffset = noSurfaceOpeningNoiseOffset;
	}

	// Noise
	
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

	public BlockPredicate bufferBlockReplacePredicate() {
		return this.bufferReplacePredicate;
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
}
