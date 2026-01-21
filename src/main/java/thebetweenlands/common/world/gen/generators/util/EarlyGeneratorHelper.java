package thebetweenlands.common.world.gen.generators.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings2D;
import thebetweenlands.common.world.gen.util.config.FractalOpenSimplexNoiseSettings3D;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseSettings;
import thebetweenlands.util.FractalOpenSimplexNoise;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public class EarlyGeneratorHelper {

	// ======== Compute Noise Raw with Size ========
	public static double[] computeNoiseRawWithSize(BLLegacyPerlinSimplexNoise noiseGenerator, int x, int z, double scale, int size) {
		if(size <= 0) {
			throw new IllegalArgumentException("\"size\" must be greater than 0");
		}
		
		double[] noise = new double[size * size];

		// TODO get rid of getRegion and stuff again
		
		// Note: 1.12.2 swaps X and Z when calculating noise for this, so we swap it here to line up with 1.12
		//       If worldgen ever changes in future, then swap this for:
		//       noise = noiseGenerator.getRegionTransposed(noise, x, z, size, size, scale, scale, 1.0D);
		noise = noiseGenerator.getRegion(noise, z, x, size, size, scale, scale, 1.0D);
		
		return noise;
	}

	// ======== Compute Noise Raw ========
	
	public static double[] computeNoiseRaw(BLLegacyPerlinSimplexNoise noiseGenerator, int x, int z, double scale) {
		double[] noise = new double[256];

		// TODO get rid of getRegion and stuff again
		
		// Note: 1.12.2 swaps X and Z when calculating noise for this, so we swap it here to line up with 1.12
		//       If worldgen ever changes in future, then swap this for:
		//       noise = noiseGenerator.getRegionTransposed(noise, x, z, 16, 16, scale, scale, 1.0D);
		noise = noiseGenerator.getRegion(noise, z, x, 16, 16, scale, scale, 1.0D);
		
		return noise;
	}
	
	public static double[] computeNoiseRaw(BLLegacyPerlinSimplexNoise noiseGenerator, BlockPos pos, double scale) {
		return computeNoiseRaw(noiseGenerator, pos.getX(), pos.getZ(), scale);
	}
	
	public static double[] computeNoiseRaw(BLLegacyPerlinSimplexNoise noiseGenerator, ChunkPos pos, double scale) {
		return computeNoiseRaw(noiseGenerator, (pos.x * 16), (pos.z * 16), scale);
	}

	// ======== Compute Noise from Settings ========

	@FunctionalInterface
	public static interface NoiseWeightGetter {
		/**
		 * Gets a weighting for the noise value at the offset x, z. Multiplies the noise value and noise multiplier, but not the noise offset.
		 * @param x
		 * @param z
		 * @return
		 */
		public float getWeight(int x, int z);
	}
	
	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, int x, int z, SimplexNoiseSettings settings, NoiseWeightGetter weightProvider) {
		double[] noise = computeNoiseRaw(noiseGenerator, x, z, settings.noiseScale());

		final double noiseValueMultiplier = settings.noiseValueMultiplier();
		final double noiseValueOffset = settings.noiseValueOffset();
		
		for(int xOffset = 0; xOffset < 16; ++xOffset) {
			for(int zOffset = 0; zOffset < 16; ++zOffset) {
				final int index = xOffset * 16 + zOffset;
				final float weight = weightProvider.getWeight(xOffset, zOffset);
				
				noise[index] = noise[index] * noiseValueMultiplier * weight + noiseValueOffset;
			}
		}
		
		return noise;
	}

	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, int x, int z, SimplexNoiseSettings settings) {
		return computeNoiseFromSettings(noiseGenerator, x, z, settings, (_x, _z) -> 1.0F);
	}
	
	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, BlockPos pos, SimplexNoiseSettings settings, NoiseWeightGetter weightProvider) {
		return computeNoiseFromSettings(noiseGenerator, pos.getX(), pos.getZ(), settings, weightProvider);
	}

	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, BlockPos pos, SimplexNoiseSettings settings) {
		return computeNoiseFromSettings(noiseGenerator, pos, settings, (_x, _z) -> 1.0F);
	}
	
	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, ChunkPos pos, SimplexNoiseSettings settings, NoiseWeightGetter weightProvider) {
		return computeNoiseFromSettings(noiseGenerator, (pos.x * 16), (pos.z * 16), settings, weightProvider);
	}

	public static double[] computeNoiseFromSettings(BLLegacyPerlinSimplexNoise noiseGenerator, ChunkPos pos, SimplexNoiseSettings settings) {
		return computeNoiseFromSettings(noiseGenerator, pos, settings, (_x, _z) -> 1.0F);
	}
	
	// ======== Compute Single Noise ========
	
	public static double computeSingleNoiseRaw(BLLegacyPerlinSimplexNoise noiseGenerator, int x, int z, double scale) {
		// Note: 1.12.2 swaps X and Z when calculating noise for this, so we swap it here to line up with 1.12
		//       If worldgen ever changes in future, then swap this for:
		//       return noiseGenerator.getValue(x * scale, z * scale, true);
		return noiseGenerator.getValue(z * scale, x * scale, true);
	}
	
	// ======== Fractal Open Simplex Noise Samplers ========

	@FunctionalInterface
	public static interface NoiseSampler2D {
		public double eval(double x, double y);
	}
	
	@FunctionalInterface
	public static interface NoiseSampler3D {
		public double eval(double x, double y, double z);
	}
	
	/**
	 * Creates a 2D sampler that returns values in the range [{@code (-noiseValueMultiplier) + noiseValueOffset}, {@code noiseValueMultiplier + noiseValueOffset}]
	 * @param noise
	 * @param settings
	 * @return
	 */
	public static NoiseSampler2D createConfiguredSampler2D(FractalOpenSimplexNoise noise, FractalOpenSimplexNoiseSettings2D settings) {
		final double noiseScaleX = settings.noiseScaleX();
		final double noiseScaleY = settings.noiseScaleY();
		final double noiseValueMultiplier = settings.noiseValueMultiplier();
		final double noiseValueOffset = settings.noiseValueOffset();
		
		return (double x, double y) -> noise.eval(x * noiseScaleX, y * noiseScaleY) * noiseValueMultiplier + noiseValueOffset;
	}

	/**
	 * Creates a 2D sampler that returns values in the range [{@code noiseValueOffset}, {@code noiseValueMultiplier + noiseValueOffset}]
	 * @param noise
	 * @param settings
	 * @return
	 */
	public static NoiseSampler2D createConfiguredSampler2DNormalized(FractalOpenSimplexNoise noise, FractalOpenSimplexNoiseSettings2D settings) {
		final double noiseScaleX = settings.noiseScaleX();
		final double noiseScaleY = settings.noiseScaleY();
		final double noiseValueMultiplier = settings.noiseValueMultiplier();
		final double noiseValueOffset = settings.noiseValueOffset();
		
		return (double x, double y) -> MathUtils.linearTransformd(noise.eval(x * noiseScaleX, y * noiseScaleY), -1, 1, 0, 1) * noiseValueMultiplier + noiseValueOffset;
	}

	/**
	 * Creates a 3D sampler that returns values in the range [{@code (-noiseValueMultiplier) + noiseValueOffset}, {@code noiseValueMultiplier + noiseValueOffset}]
	 * @param noise
	 * @param settings
	 * @return
	 */
	public static NoiseSampler3D createConfiguredSampler3D(FractalOpenSimplexNoise noise, FractalOpenSimplexNoiseSettings3D settings) {
		final double noiseScaleX = settings.noiseScaleX();
		final double noiseScaleY = settings.noiseScaleY();
		final double noiseScaleZ = settings.noiseScaleY();
		final double noiseValueMultiplier = settings.noiseValueMultiplier();
		final double noiseValueOffset = settings.noiseValueOffset();
		
		return (double x, double y, double z) -> noise.eval(x * noiseScaleX, y * noiseScaleY, z * noiseScaleZ) * noiseValueMultiplier + noiseValueOffset;
	}

	/**
	 * Creates a 3D sampler that returns values in the range [{@code noiseValueOffset}, {@code noiseValueMultiplier + noiseValueOffset}]
	 * @param noise
	 * @param settings
	 * @return
	 */
	public static NoiseSampler3D createConfiguredSampler3DNormalized(FractalOpenSimplexNoise noise, FractalOpenSimplexNoiseSettings3D settings) {
		final double noiseScaleX = settings.noiseScaleX();
		final double noiseScaleY = settings.noiseScaleY();
		final double noiseScaleZ = settings.noiseScaleY();
		final double noiseValueMultiplier = settings.noiseValueMultiplier();
		final double noiseValueOffset = settings.noiseValueOffset();
		
		return (double x, double y, double z) -> MathUtils.linearTransformd(noise.eval(x * noiseScaleX, y * noiseScaleY, z * noiseScaleZ), -1, 1, 0, 1) * noiseValueMultiplier + noiseValueOffset;
	}
}
