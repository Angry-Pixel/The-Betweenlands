package thebetweenlands.util;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FogGenerator {
	private int lastCX, lastCZ;
	private PerlinSimplexNoise fogNoiseGen;
	private double[] fogChunkNoise = new double[256];
	private double scaleX;
	private double scaleZ;
	private long seed;

	public FogGenerator(int octaves, double scaleX, double scaleZ, double octaveScale, long seed) {
		this.seed = seed;
		XoroshiroRandomSource source = new XoroshiroRandomSource(seed);
		this.fogNoiseGen = new PerlinSimplexNoise(source, List.of(0, 1, 2, 3));
		this.scaleX = scaleX;
		this.scaleZ = scaleZ;
		this.fogChunkNoise = new double[256];
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
		if(Minecraft.getInstance().player == null)
			return new float[]{min, max};
		double x = Minecraft.getInstance().player.xo;
		double z = Minecraft.getInstance().player.zo;
		int cx = (int)((x - ((int)(Math.floor(x)) & 15)) / 16) - 1;
		int cz = (int)((z - ((int)(Math.floor(z)) & 15)) / 16);
		if(this.lastCX != cx || this.lastCZ != cz) {
			this.lastCX = cx;
			this.lastCZ = cz;
			this.fogChunkNoise = sampleRegion(
				this.fogChunkNoise,
				cx * 16,
				cz * 16
			);
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

	private double[] sampleRegion(double[] buffer, double startX, double startZ) {
		if (buffer.length < 256) {
			buffer = new double[256];
		}

		int index = 0;
		for (int dz = 0; dz < 16; dz++) {
			for (int dx = 0; dx < 16; dx++) {
				double x = (startX + dx) * this.scaleX;
				double z = (startZ + dz) * this.scaleZ;

				buffer[index++] = Math.abs(this.fogNoiseGen.getValue(x, z, true));
			}
		}

		return buffer;
	}
}