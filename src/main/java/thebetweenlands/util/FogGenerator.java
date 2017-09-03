package thebetweenlands.util;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class FogGenerator {
	private int lastCX, lastCZ;
	private NoiseGeneratorPerlin fogNoiseGen;
	private double[] fogChunkNoise = new double[256];
	private double scaleX;
	private double scaleZ;
	private double octaveScale;
	private long seed;

	public FogGenerator(int octaves, double scaleX, double scaleZ, double octaveScale, long seed) {
		this.seed = seed;
		Random rnd = new Random();
		rnd.setSeed(seed);
		this.fogNoiseGen = new NoiseGeneratorPerlin(rnd, octaves);
		this.scaleX = scaleX;
		this.scaleZ = scaleZ;
		this.octaveScale = octaveScale;
	}

	public FogGenerator(long seed) {
		this(4, 0.003D, 0.003D, 0.003D, seed);
	}

	public long getSeed() {
		return this.seed;
	}

	/**
	 * Returns the fog range based on the player position.
	 * @return
	 */
	public float[] getFogRange(float min, float max) {
		if(Minecraft.getMinecraft().player == null)
			return new float[]{min, max};
		double x = Minecraft.getMinecraft().player.posX;
		double z = Minecraft.getMinecraft().player.posZ;
		int cx = (int)((x - ((int)(Math.floor(x)) & 15)) / 16) - 1;
		int cz = (int)((z - ((int)(Math.floor(z)) & 15)) / 16);
		if(this.fogChunkNoise == null || this.lastCX != cx || this.lastCZ != cz) {
			this.lastCX = cx;
			this.lastCZ = cz;
			this.fogChunkNoise = this.fogNoiseGen.getRegion(
					this.fogChunkNoise, 
					(double) (cx * 16), (double) (cz * 16), 
					16, 16, this.scaleX, this.scaleZ, this.octaveScale);
		}
		int ix = (int)(Math.floor(x)) & 15;
		int iz = (int)(Math.floor(z)) & 15;

		double noise = (Math.abs(this.fogChunkNoise[iz * 16 + ix]));

		float diff = max - min;

		float newMax = (float) Math.max(min, min + diff * (1.0F - noise));

		diff = (newMax - min) / 2.0F;

		float newMin = (float) (noise < 0.75F ? min + diff / 2.0F : (min + diff / 2.0F * (1.0F - (noise - 0.75F) * 2.0F)));

		return new float[]{newMin, newMax};
	}
}
