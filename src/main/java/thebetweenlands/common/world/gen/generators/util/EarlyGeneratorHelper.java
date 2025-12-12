package thebetweenlands.common.world.gen.generators.util;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseSettings;
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
	
}
