package thebetweenlands.common.world.noisegenerators;

import net.minecraft.util.Mth;

import java.util.Random;

public class NoiseGeneratorOctaves {
	private final NoiseGeneratorImproved[] generatorCollection;
	private final int octaves;

	public NoiseGeneratorOctaves(Random seed, int octavesIn) {
		this.octaves = octavesIn;
		this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

		for (int i = 0; i < octavesIn; ++i) {
			this.generatorCollection[i] = new NoiseGeneratorImproved(seed);
		}
	}

	public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
		if (noiseArray == null) {
			noiseArray = new double[xSize * ySize * zSize];
		} else {
			for (int i = 0; i < noiseArray.length; ++i) {
				noiseArray[i] = 0.0D;
			}
		}

		double d3 = 1.0D;

		for (int j = 0; j < this.octaves; ++j) {
			double d0 = (double) xOffset * d3 * xScale;
			double d1 = (double) yOffset * d3 * yScale;
			double d2 = (double) zOffset * d3 * zScale;
			long k = Mth.lfloor(d0);
			long l = Mth.lfloor(d2);
			d0 = d0 - (double) k;
			d2 = d2 - (double) l;
			k = k % 16777216L;
			l = l % 16777216L;
			d0 = d0 + (double) k;
			d2 = d2 + (double) l;
			this.generatorCollection[j].populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3);
			d3 /= 2.0D;
		}

		return noiseArray;
	}

	public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double p_76305_10_) {
		return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0D, zScale);
	}
}
