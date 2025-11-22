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
//	private final BlendedNoise blendedNoise;
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

//	public TerrainWarper(int width, int height, int yCount, BiomeSource source, NoiseSettings settings, NoiseSlider topSlide, NoiseSlider bottomSlide, BlendedNoise blend, NoiseModifier modifier) {
	public TerrainWarper(int width, int height, int yCount, BiomeSource source, NoiseSettings settings, NoiseSlider topSlide, NoiseSlider bottomSlide, BLLegacyBlendedNoise blend, NoiseModifier modifier) {
		this.cellWidth = width;
		this.cellHeight = height;
		this.cellCountY = yCount;
		this.biomeSource = source;
		this.noiseSettings = settings;
		this.topSlide = topSlide;
		this.bottomSlide = bottomSlide;
		this.blendedNoise = blend;
		//Fallbacks will never be met as this will crash to enforce correct source
		this.dimensionDensityFactor = source instanceof BetweenlandsBiomeSource blsource ? blsource.getBaseFactor() : 1.0F;
		this.dimensionDensityOffset = source instanceof BetweenlandsBiomeSource blsource ? blsource.getBaseOffset() : 0.0F;
		this.caveNoiseModifier = modifier;
	}

	public void fillNoiseColumn(double[] adouble, int x, int z, Climate.Sampler sampler, int sealevel, int min, int max) {
		if (biomeSource instanceof BetweenlandsBiomeSource source) {
			float averageBiomeScale = 0.0F; // In 1.12: biomeVariation
			float averageBiomeDepth = 0.0F; // In 1.12: biomeDepth
			float totalBiomeWeight = 0.0F;
			float centerBiomeDepth = source.getBiomeDepth(x, sealevel, z, sampler);

			for (int offX = -2; offX <= 2; ++offX) {
				for (int offZ = -2; offZ <= 2; ++offZ) {
					Biome nearbyBiome = source.getNoiseBiome(x + offX, sealevel, z + offZ, sampler).value();
					float nearbyBiomeDepth = source.getBiomeDepth(nearbyBiome);
					float nearbyBiomeScale = source.getBiomeScale(nearbyBiome);

					// If the neighboring biome is higher than this one, then don't weight them as highly
					// This helps make sure the lower biome blends properly into the higher one, and doesn't rise too high
					float weightModifier = nearbyBiomeDepth > centerBiomeDepth ? 0.5F : 1.0F;
					float biomeWeight = weightModifier * BIOME_WEIGHTS[offX + 2 + (offZ + 2) * 5];
//					float adjustedWeight = biomeWeight / (nearbyBiomeDepth + 2.0F); // what is this? why is this? is it meant to blend mountains? it feels like it's meant to blend mountains.
					float adjustedWeight = biomeWeight;
					averageBiomeScale += nearbyBiomeScale * adjustedWeight;
					averageBiomeDepth += nearbyBiomeDepth * adjustedWeight;
					totalBiomeWeight += adjustedWeight;
				}
			}

			// convert biome depth and biome scale to averages
			averageBiomeDepth /= totalBiomeWeight;
			averageBiomeScale /= totalBiomeWeight;
			
			// normalize depths
//			double d6 = averageBiomeDepth * 0.5F - 0.125F;
//			double normalisedBiomeScale = averageBiomeScale * 0.9F + 0.1F;
//			final double d0 = d6 * 0.265625D;
//			final double d1 = 96.0D / normalisedBiomeScale;
//			final double density = -0.46875;

			for (int index = 0; index <= max; ++index) {
				int y = index + min;
				DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(x, index, z);
				final double noise = blendedNoise.compute(context) * 128.0D;
				// Biome scale multiplier: density multiplier, multiplies noise by a certain amount
				double biomeScaleDensityMultiplier = averageBiomeScale * 10.0D / 256.0;
				
				// Height offset: density modifier that decreases density as height increases
				double heightDensityOffset = ((double)y * 8.0F) / 256.0 / (512.0D / 32767.0D); // TODO move elsewhere
				
				// Biome depth offset: density modifier that 
				double biomeDepthDensityOffset = ((double)sealevel + averageBiomeDepth * 10.0D) / 256.0 / (512.0D / 32767.0D); // TODO move elsewhere
				
//				adouble[index] = noise - densityOffset;
//				double modifiedDensity = noise;
				double totalDensity = noise;
				totalDensity *= biomeScaleDensityMultiplier;
				totalDensity -= heightDensityOffset;
				totalDensity += biomeDepthDensityOffset;
//				totalDensity += this.computeInitialDensity(y, d0, d1, density);
				totalDensity = this.caveNoiseModifier.modifyNoise(totalDensity, y * this.cellHeight, z * this.cellWidth, x * this.cellWidth);
				totalDensity = this.applySlide(totalDensity, y);
				adouble[index] = totalDensity;
			}
		} else {
			throw new IllegalArgumentException("BiomeSource is not an instance of BetweenlandsBiomeSource");
		}
	}

	protected double computeInitialDensity(int y, double offset, double factor, double density) {
		double base = 1.0D - (double)y * 2.0D / 32.0D + density;
		double factored = base * this.dimensionDensityFactor + this.dimensionDensityOffset;
		double total = (factored + offset) * factor;
		return total * (double)(total > 0.0D ? 4 : 1);
	}

	protected double applySlide(double density, int height) {
		int i = Math.floorDiv(this.noiseSettings.minY(), this.cellHeight);
		int j = height - i;
		density = this.topSlide.applySlide(density, this.cellCountY - j);
		density = this.bottomSlide.applySlide(density, j);
		return density;
	}
}