package thebetweenlands.common.biomes;

import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class SwamplandsNoiseGenerator extends BiomeNoiseGenerator {
	public PerlinNoise noise;

	public SwamplandsNoiseGenerator(PerlinNoise noise) {
		this.noise = noise;
	}

	public double sample(double x, double y, double z) {

		// Warping
		double warpx = noise.getValue(x * 8, 0, z * 8) * 0.25;
		double warpz = noise.getValue(x * 8, 32, z * 8) * 0.25;


		// base noise
		double outval = noise.getValue(x + warpx, y, z + warpz);

		// Clamping
		/*if (outval > 1) {
			outval = 1;
		}
		else if (outval < -1) {
			outval = -1;
		}*/

		return outval;
	}
}
