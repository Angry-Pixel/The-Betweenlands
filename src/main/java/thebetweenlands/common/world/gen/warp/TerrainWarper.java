package thebetweenlands.common.world.gen.warp;

import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseSettings;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

public class TerrainWarper {
	private final int cellWidth;
	private final int cellHeight;
	private final int cellCountY;
	private final BiomeSource biomeSource;
	private final NoiseSettings noiseSettings;
	private final NoiseSlider topSlide;
	private final NoiseSlider bottomSlide;
	private final BLLegacyBlendedNoise blendedNoise;
	private final double dimensionDensityFactor;
	private final double dimensionDensityOffset;
	public final NoiseModifier caveNoiseModifier;
	protected static final float[] BIOME_WEIGHTS = Util.make(new float[25], (afloat) -> {
		for(int x = -2; x <= 2; ++x) {
			for(int z = -2; z <= 2; ++z) {
				float weight = 10.0F / Mth.sqrt((float)(x * x + z * z) + 0.2F);
				afloat[x + 2 + (z + 2) * 5] = weight;
			}
		}
	});

	public TerrainWarper(int cellWidth, int cellHeight, int cellCountY, BiomeSource biomeSource, NoiseSettings noiseSettings, NoiseSlider topSlide, NoiseSlider bottomSlide, BLLegacyBlendedNoise blendedNoise, NoiseModifier caveNoiseModifier) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.cellCountY = cellCountY;
		this.biomeSource = biomeSource;
		this.noiseSettings = noiseSettings;
		this.topSlide = topSlide;
		this.bottomSlide = bottomSlide;
		this.blendedNoise = blendedNoise;
		// Fallbacks will never be used as this will crash to enforce correct source
		this.dimensionDensityFactor = biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getBaseFactor() : 1.0F;
		this.dimensionDensityOffset = biomeSource instanceof BetweenlandsBiomeSource blBiomeSource ? blBiomeSource.getBaseOffset() : 0.0F;
		this.caveNoiseModifier = caveNoiseModifier;
	}

	public void fillNoiseColumn(double[] adouble, int x, int z, Climate.Sampler sampler, int sealevel, int worldHeight, int min, int max) {
		if (biomeSource instanceof BetweenlandsBiomeSource source) {
			float averageBiomeScale = 0.0F; // In 1.12: biomeVariation
			float averageBiomeDepth = 0.0F; // In 1.12: biomeDepth
			float totalBiomeWeight = 0.0F;
			float centerBiomeDepth = source.getBiomeDepth(x, sealevel, z, sampler);

			for (int offsetX = -2; offsetX <= 2; ++offsetX) {
				for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
					Biome nearbyBiome = source.getNoiseBiome(x + offsetX, sealevel, z + offsetZ, sampler).value();
					float nearbyBiomeDepth = source.getBiomeDepth(nearbyBiome);
					float nearbyBiomeScale = source.getBiomeScale(nearbyBiome);

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

			double baseBiomeHeight = (double)worldHeight * this.dimensionDensityOffset;
			
			for (int index = 0; index <= max; ++index) {
				int y = index + min;
				DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(x, index, z);
				final double noise = blendedNoise.compute(context) * 128.0D;
				
				double totalDensity = this.computeInitialDensity(y, averageBiomeDepth, averageBiomeScale, baseBiomeHeight, noise);
				
				totalDensity = this.caveNoiseModifier.modifyNoise(totalDensity, y * this.cellHeight, z * this.cellWidth, x * this.cellWidth);
				totalDensity = this.applySlide(totalDensity, y);
				adouble[index] = totalDensity;
			}
		} else {
			throw new IllegalArgumentException("BiomeSource is not an instance of BetweenlandsBiomeSource");
		}
	}

	protected double computeInitialDensity(int y, double averageBiomeDepth, double averageBiomeScale, double baseBiomeHeight, double noise) {
		// TODO move some of these hardcoded values to a settings object from the ChunkGenerator

		double totalDensity = noise;
		
		// Biome scale multiplier: density multiplier, multiplies noise by a certain amount
		// Effectively, limits noise to the biome's height variation
		double biomeScaleDensityMultiplier = averageBiomeScale * 10.0D / 256.0; // Note: 1 / 256.0 is a weighting from 1.12
		totalDensity *= biomeScaleDensityMultiplier;
		
		// Biome depth offset: density modifier that controls biome height
		double biomeDepthDensityOffset = averageBiomeDepth * 10.0D;

		totalDensity += biomeDepthDensityOffset / 256.0 / (512.0D / 32767.0D);
		totalDensity *= this.dimensionDensityFactor;
				
		// Height offset: density modifier that decreases density as height increases
		// TODO the 8.0D could be made into a setting
		double heightDensityOffset = -((double)y) * 8.0D;
		
		// Note: 1 / 256.0 is a weighting from 1.12
		// Note: 1 / (512.0D / 32767.0D) converts from 1.12 numbers to 1.21 numbers
		totalDensity += (baseBiomeHeight + heightDensityOffset) / 256.0 / (512.0D / 32767.0D);
		
		return totalDensity;
	}
	
	protected double applySlide(double density, int height) {
		int i = Math.floorDiv(this.noiseSettings.minY(), this.cellHeight);
		int j = height - i;
		density = this.topSlide.applySlide(density, this.cellCountY - j);
		density = this.bottomSlide.applySlide(density, j);
		return density;
	}
}