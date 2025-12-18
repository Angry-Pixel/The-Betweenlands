package thebetweenlands.common.world.gen.warp;

import java.util.stream.IntStream;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import thebetweenlands.api.world.biome.IBetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

public class TerrainWarper {
	protected static final float[] BIOME_WEIGHTS = Util.make(new float[25], (afloat) -> {
		for(int x = -2; x <= 2; ++x) {
			for(int z = -2; z <= 2; ++z) {
				float weight = 10.0F / Mth.sqrt((float)(x * x + z * z) + 0.2F);
				afloat[x + 2 + (z + 2) * 5] = weight;
			}
		}
	});
	
	private final TerrainWarperSettings settings;
	private final BLLegacyBlendedNoise blendedNoise;
	private final PerlinNoise depthNoise;
	private final long seed;

	public TerrainWarper(int cellWidth, int cellHeight, int cellCountY, BiomeSource biomeSource, NoiseSettings noiseSettings, NoiseSlider topSlide, NoiseSlider bottomSlide, NoiseModifier caveNoiseModifier, BLLegacyBlendedNoise blendedNoise) {
		// Fallbacks will never be used as this will crash to enforce correct source
		final double dimensionDensityFactor = biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getGlobalFactor() : 1.0F;
		final double dimensionDensityOffset = biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getSurfaceDepth() : 0.0F;
		
		this.settings = new TerrainWarperSettings(cellWidth, cellHeight, cellCountY, biomeSource, noiseSettings, topSlide, bottomSlide, dimensionDensityFactor, dimensionDensityOffset, caveNoiseModifier);
		this.blendedNoise = blendedNoise;
		this.depthNoise = null;
		this.seed = -1;
	}
	
	public TerrainWarper(TerrainWarperSettings settings, BLLegacyBlendedNoise blendedNoise, long seed) {
		this.settings = settings;
		
		LegacyRandomSource randomSource = new LegacyRandomSource(seed);
		
		this.blendedNoise = blendedNoise.withNewRandom(randomSource);
		
		// Advance the seed to line up with 1.12 depth pertubation noise
		randomSource.consumeCount(4 * 262 + 10 * 262);
		
		this.depthNoise = PerlinNoise.create(randomSource, IntStream.rangeClosed(-15, 0));
		this.seed = seed;
	}

	public TerrainWarper usingSeed(long seed) {
		if(this.seed == seed) {
			return this;
		}
		return new TerrainWarper(this.settings, this.blendedNoise, seed);
	}
	
	public void fillNoiseColumn(double[] adouble, int x, int z, Climate.Sampler sampler, int sealevel, int worldHeight, int min, int max) {
		if (this.settings.biomeSource() instanceof IBetweenlandsBiomeSource biomeSource) {
			float averageBiomeScale = 0.0F; // In 1.12: biomeVariation
			float averageBiomeDepth = 0.0F; // In 1.12: biomeDepth
			float totalBiomeWeight = 0.0F;
			float centerBiomeDepth = biomeSource.getBiomeDepth(x, sealevel, z, sampler);

			for (int offsetX = -2; offsetX <= 2; ++offsetX) {
				for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
					Holder<Biome> nearbyBiome = biomeSource.getNoiseBiome(x + offsetX, sealevel, z + offsetZ, sampler);
					float nearbyBiomeDepth = biomeSource.getBiomeDepth(nearbyBiome);
					float nearbyBiomeScale = biomeSource.getBiomeScale(nearbyBiome);

					// If the neighboring biome is higher than this one, then don't weight them as highly
					// This helps make sure the lower biome blends properly into the higher one, and doesn't rise too high
					float weightModifier = nearbyBiomeDepth > centerBiomeDepth ? 0.5F : 1.0F;
					float biomeWeight = weightModifier * BIOME_WEIGHTS[offsetX + 2 + (offsetZ + 2) * 5];
					float adjustedWeight = biomeWeight;
					
					// Add values to average
					averageBiomeScale += nearbyBiomeScale * adjustedWeight;
					averageBiomeDepth += nearbyBiomeDepth * adjustedWeight;
					totalBiomeWeight += adjustedWeight;
				}
			}

			// convert biome depth and biome scale to averages
			averageBiomeDepth /= totalBiomeWeight;
			averageBiomeScale /= totalBiomeWeight;

			final NoiseModifier caveNoiseModifier = this.settings.caveNoiseModifier();
			final int cellHeight = this.settings.cellHeight();
			final int cellWidth = this.settings.cellWidth();
			
			final double depthPerturbation = computeDepthPerturbation(x, z, averageBiomeScale);
			
			for (int index = 0; index <= max; ++index) {
				int y = index + min;
				DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(x, index, z);
				final double noise = this.blendedNoise.compute(context) * 128.0D;
				
				double totalDensity = this.computeInitialDensity(averageBiomeDepth, averageBiomeScale, noise);
				totalDensity = this.applyHeightDensityModifiers(y, worldHeight, totalDensity);
				totalDensity -= depthPerturbation;
				totalDensity = caveNoiseModifier.modifyNoise(totalDensity, y * cellHeight, z * cellWidth, x * cellWidth);
				totalDensity = this.applySlide(totalDensity, y);
				adouble[index] = totalDensity;
			}
		} else {
			throw new IllegalArgumentException("BiomeSource is not an instance of BetweenlandsBiomeSource");
		}
	}

	protected double computeInitialDensity(double averageBiomeDepth, double averageBiomeScale, double noise) {
		// TODO move some of these hardcoded values to a settings object from the ChunkGenerator

		double totalDensity = noise;
		
		// Biome scale multiplier: density multiplier, multiplies noise by a certain amount
		// Effectively, limits noise to the biome's height variation
		double biomeScaleDensityMultiplier = averageBiomeScale * 10.0D / 256.0; // Note: 1 / 256.0 is a weighting from 1.12
		totalDensity *= biomeScaleDensityMultiplier;
		
		// Biome depth offset: density modifier that controls biome height
		double biomeDepthDensity = averageBiomeDepth * 10.0D;

		totalDensity += biomeDepthDensity / 256.0 / (512.0D / 32767.0D);
		totalDensity *= this.settings.dimensionDensityFactor();
		
		return totalDensity;
	}

	protected double applyHeightDensityModifiers(int y, int worldHeight, double totalDensity) {
		// Base biome height offset: density modifier that decreases density as height increases
		final double baseBiomeHeight = (double)worldHeight * this.settings.dimensionDensityOffset();

		// Height offset: density modifier that decreases density as height increases
		// TODO the 8.0D could be made into a setting
		final double heightDensity = -((double)y) * 8.0D;
		
		// Note: 1 / 256.0 is a weighting from 1.12
		// Note: 1 / (512.0D / 32767.0D) converts from 1.12 numbers to 1.21 numbers
		return totalDensity + (baseBiomeHeight + heightDensity) / 256.0 / (512.0D / 32767.0D);
	}
	
	@SuppressWarnings("deprecation")
	protected double computeDepthPerturbation(int x, int z, double averageBiomeScale) {
		double depthPerturbation = 0.0;
		double scale = 1.0;
		
		for (int i = 0; i < 16; i++) {
			ImprovedNoise improvednoise = this.depthNoise.getOctaveNoise(i);
			if (improvednoise != null) {
				depthPerturbation += improvednoise.noise(PerlinNoise.wrap(200.0D * x * scale), PerlinNoise.wrap(1.0D * 10.0D * scale), PerlinNoise.wrap(200.0D * z * scale), 1.0D, -1.0D) / scale;
			}
		
			scale /= 2.0;
		}
		
		depthPerturbation /= 8000.0;
		
		if (depthPerturbation < 0.0D) {
			depthPerturbation = -depthPerturbation * 0.3D;
		}
		
		depthPerturbation = depthPerturbation * 3.0D - 2.0D;
		
		if (depthPerturbation < 0.0D) {
			depthPerturbation = depthPerturbation / 2.0D;
			
			if (depthPerturbation < -1.0D)
			{
				depthPerturbation = -1.0D;
			}
			
			depthPerturbation = depthPerturbation / 1.4D;
			depthPerturbation = depthPerturbation / 2.0D;
		} else {
			if (depthPerturbation > 1.0D) {
				depthPerturbation = 1.0D;
			}
			
			depthPerturbation = depthPerturbation / 8.0D;
		}
		
		return depthPerturbation * (averageBiomeScale * 10.0D / 256.0) / 256.0 / (512.0D / 32767.0D);
	}
	
	protected double applySlide(double density, int height) {
		int i = Math.floorDiv(this.settings.noiseSettings().minY(), this.settings.cellHeight());
		int j = height - i;
		density = this.settings.topSlide().applySlide(density, this.settings.cellCountY() - j);
		density = this.settings.bottomSlide().applySlide(density, j);
		return density;
	}
}