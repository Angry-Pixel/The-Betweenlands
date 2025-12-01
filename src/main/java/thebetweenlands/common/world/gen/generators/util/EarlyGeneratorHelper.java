package thebetweenlands.common.world.gen.generators.util;

import net.minecraft.world.level.ChunkPos;
import thebetweenlands.util.legacy.BLLegacyPerlinSimplexNoise;

public class EarlyGeneratorHelper {

	public static double[] computeNoiseRaw(BLLegacyPerlinSimplexNoise noiseGenerator, ChunkPos pos, double scale) {
		double[] noise = new double[256];

		// TODO get rid of getRegion and stuff again
		
		// Note: 1.12.2 swaps X and Z when calculating noise for this, so we swap it here to line up with 1.12
		//       If worldgen ever changes in future, then swap this for:
		//       noise = noiseGenerator.getRegionTransposed(noise, (pos.x * 16), (pos.z * 16), 16, 16, scale, scale, 1.0D);
		noise = noiseGenerator.getRegion(noise, (pos.z * 16), (pos.x * 16), 16, 16, scale, scale, 1.0D);
		
		return noise;
	}
	
}
