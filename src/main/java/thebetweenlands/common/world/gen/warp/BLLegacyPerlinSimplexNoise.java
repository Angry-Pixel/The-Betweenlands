package thebetweenlands.common.world.gen.warp;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

// Recreation of 1.12 NoiseGeneratorPerlin
public class BLLegacyPerlinSimplexNoise {
	private final SimplexNoise[] noiseLevels;

	public BLLegacyPerlinSimplexNoise(RandomSource random, int octaves) {
		this.noiseLevels = new SimplexNoise[octaves];
		
		for(int i = 0; i < octaves; ++i) {
			this.noiseLevels[i] = new SimplexNoise(random);
		}
	}

	public SimplexNoise getNoiseLevel(int index) {
		return this.noiseLevels[index];
	}

	public double getValue(double x, double y)
	{
		double value = 0.0D;
		double scale = 1.0D;

		for (SimplexNoise simplexnoise : this.noiseLevels)
		{
			value += simplexnoise.getValue(x * scale, y * scale) / scale;
			scale /= 2.0D;
		}

		return value;
	}
	
	public double getValue(double x, double y, boolean useNoiseOffsets) {
		double noiseValue = 0.0;
		double scale = 1.0F;
		double amplifier = 1.0F;
		
		for (SimplexNoise simplexnoise : this.noiseLevels) {
			if (simplexnoise != null) {
				noiseValue += simplexnoise.getValue(x * scale + (useNoiseOffsets ? simplexnoise.xo : 0.0), y * scale + (useNoiseOffsets ? simplexnoise.yo : 0.0)) * 0.55 * amplifier;
			}

			scale /= 2.0;
			amplifier *= 2.0;
		}

		return noiseValue;
	}

	public double getValue(double x, double y, boolean useNoiseOffsets, double octaveScaleFactor, double octiveAmplifierFactor, double initialScale, double initialAmplifier) {
		double noiseValue = 0.0;
		double scale = initialScale;
		double amplifier = initialAmplifier;

		for (SimplexNoise simplexnoise : this.noiseLevels) {
			if (simplexnoise != null) {
				noiseValue += simplexnoise.getValue(x * scale + (useNoiseOffsets ? simplexnoise.xo : 0.0), y * scale + (useNoiseOffsets ? simplexnoise.yo : 0.0)) * amplifier;
			}

			scale *= octaveScaleFactor;
			amplifier *= octiveAmplifierFactor;
		}

		return noiseValue;
	}
	
	@Deprecated
	public double[] getRegion(double[] out, double x, double y, int sizeX, int sizeY, double xScale, double yScale, double octaveScaleFactor)
	{
		return this.getRegion(out, x, y, sizeX, sizeY, xScale, yScale, octaveScaleFactor, 0.5D);
	}

	@Deprecated
	public double[] getRegion(double[] out, double x, double y, int sizeX, int sizeY, double xScale, double yScale, double octaveScaleFactor, double octaveAmplifierFactor)
	{
		if (out != null && out.length >= sizeX * sizeY)
		{
			for (int i = 0; i < out.length; ++i)
			{
				out[i] = 0.0D;
			}
		}
		else
		{
			out = new double[sizeX * sizeY];
		}

		double amplifier = 1.0D;
		double scale = 1.0D;

		for (SimplexNoise simplexnoise : this.noiseLevels) {
			int i = 0;
			for (int yOffset = 0; yOffset < sizeY; ++yOffset) {
				double noiseY = (y + (double)yOffset) * (yScale * scale * amplifier) + simplexnoise.yo;

				for (int xOffset = 0; xOffset < sizeX; ++xOffset) {
					double noiseX = (x + (double)xOffset) * (xScale * scale * amplifier) + simplexnoise.xo;
					
					out[i++] = simplexnoise.getValue(noiseX, noiseY) * 0.55D / amplifier;
				}
			}
			
			scale *= octaveScaleFactor;
			amplifier *= octaveAmplifierFactor;
		}

		return out;
	}
	
	@Deprecated
	public double[] getRegionTransposed(double[] out, double x, double y, int sizeX, int sizeY, double xScale, double yScale, double octaveScaleFactor)
	{
		return this.getRegionTransposed(out, x, y, sizeX, sizeY, xScale, yScale, octaveScaleFactor, 0.5D);
	}

	@Deprecated
	public double[] getRegionTransposed(double[] out, double x, double y, int sizeX, int sizeY, double xScale, double yScale, double octaveScaleFactor, double octaveAmplifierFactor)
	{
		if (out != null && out.length >= sizeX * sizeY)
		{
			for (int i = 0; i < out.length; ++i)
			{
				out[i] = 0.0D;
			}
		}
		else
		{
			out = new double[sizeX * sizeY];
		}

		double amplifier = 1.0D;
		double scale = 1.0D;

		for (SimplexNoise simplexnoise : this.noiseLevels) {
			int i = 0;
			for (int xOffset = 0; xOffset < sizeX; ++xOffset) {
				double noiseX = (x + (double)xOffset) * (xScale * scale * amplifier) + simplexnoise.xo;
				
				for (int yOffset = 0; yOffset < sizeY; ++yOffset) {
					double noiseY = (y + (double)yOffset) * (yScale * scale * amplifier) + simplexnoise.yo;

					out[i++] = simplexnoise.getValue(noiseX, noiseY) * 0.55D / amplifier;
				}
			}
			
			scale *= octaveScaleFactor;
			amplifier *= octaveAmplifierFactor;
		}

		return out;
	}
	
	// 1.12.2 version:
//  public double[] getRegion(double[] out, double x, double z, int width, int height, double scaleX, double scaleZ, double octaveScaleFactor, double octiveAmplifierFactor)
//  {
//      if (out != null && out.length >= width * height)
//      {
//          for (int i = 0; i < out.length; ++i)
//          {
//              out[i] = 0.0D;
//          }
//      }
//      else
//      {
//          out = new double[width * height];
//      }
//
//      double amplifier = 1.0D;
//      double scale = 1.0D;
//
//      for (int j = 0; j < this.levels; ++j)
//      {
//          this.noiseLevels[j].add(out, x, z, width, height, scaleX * scale * amplifier, scaleZ * scale * amplifier, 0.55D / amplifier);
//          scale *= octaveScaleFactor;
//          amplifier *= octiveAmplifierFactor;
//      }
//
//      return out;
//  }
}
