package thebetweenlands.utils;

import java.util.Random;

import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class FogGenerator {
	public static final FogGenerator INSTANCE = new FogGenerator();
	
	private int lastCX, lastCZ;
	private NoiseGeneratorPerlin fogNoiseGen;
	private double[] fogChunkNoise = new double[256];
	
	/**
	 * Returns the fog range based on a noise generator and the given position.
	 * @param x
	 * @param z
	 * @param farPlane
	 * @param rng
	 * @return
	 */
	public float[] getFogRange(double x, double z, float farPlane, long seed) {
		int cx = (int)((x - ((int)(Math.floor(x)) & 15)) / 16) - 1;
		int cz = (int)((z - ((int)(Math.floor(z)) & 15)) / 16);
		if(this.fogNoiseGen == null) {
			Random rnd = new Random();
			rnd.setSeed(seed);
			this.fogNoiseGen = new NoiseGeneratorPerlin(rnd, 4);
		}
		if(this.fogChunkNoise == null || this.lastCX != cx || this.lastCZ != cz) {
			this.lastCX = cx;
			this.lastCZ = cz;
			this.fogChunkNoise = this.fogNoiseGen.func_151599_a(
					this.fogChunkNoise, 
					(double) (cx * 16), (double) (cz * 16), 
					16, 16, 0.005D, 0.005D, 0.005D);
		}
		int ix = (int)(Math.floor(x)) & 15;
		int iz = (int)(Math.floor(z)) & 15;
		double fogDist = Math.abs(this.fogChunkNoise[iz * 16 + ix]) * 100;
		if(fogDist > farPlane - 60) {
			fogDist = farPlane - 60;
		} else if(fogDist < 1.0f) {
			fogDist = 1.0f;
		}
		return new float[]{(float)fogDist, (float)fogDist + 60};
	}
}
