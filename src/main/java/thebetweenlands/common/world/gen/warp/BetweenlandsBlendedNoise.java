package thebetweenlands.common.world.gen.warp;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class BetweenlandsBlendedNoise extends BlendedNoise {

	public BetweenlandsBlendedNoise(RandomSource random) {
		super(random, 1.0F, 1.0F, 80.0F, 160.0F, 0.0D); //todo
	}

	public double sampleAndClampNoise(FunctionContext context) {
		double d0 = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double scaleXZ = 684.412D * this.xzScale;
		double scaleY = 684.412D * this.yScale;
		double factorXZ = scaleXZ / this.xzFactor;
		double factorY = scaleY / this.yFactor;
		double scale = 1.0D;

		for(int oct = 0; oct < 8; ++oct) {
			ImprovedNoise improvednoise = this.mainNoise.getOctaveNoise(oct);
			if (improvednoise != null) {
				d2 += improvednoise.noise(
					PerlinNoise.wrap(context.blockX() * factorXZ * scale),
					PerlinNoise.wrap(context.blockY() * factorY * scale),
					PerlinNoise.wrap(context.blockZ() * factorXZ * scale),
					factorY * scale,
					context.blockY() * factorY * scale) / scale;
			}

			scale /= 2.0D;
		}

		double d8 = (d2 / 10.0D + 1.0D) / 2.0D;
		boolean flag1 = d8 >= 1.0D;
		boolean flag2 = d8 <= 0.0D;
		scale = 1.0D;

		for(int j = 0; j < 16; ++j) {
			double d4 = PerlinNoise.wrap(context.blockX() * scaleXZ * scale);
			double d5 = PerlinNoise.wrap(context.blockY() * scaleY * scale);
			double d6 = PerlinNoise.wrap(context.blockZ() * scaleXZ * scale);
			double d7 = scaleY * scale;
			if (!flag1) {
				ImprovedNoise minimumOct = this.minLimitNoise.getOctaveNoise(j);
				if (minimumOct != null) {
					d0 += minimumOct.noise(d4, d5, d6, d7, context.blockY() * d7) / scale;
				}
			}

			if (!flag2) {
				ImprovedNoise maximumOct = this.maxLimitNoise.getOctaveNoise(j);
				if (maximumOct != null) {
					d1 += maximumOct.noise(d4, d5, d6, d7, context.blockY() * d7) / scale;
				}
			}

			scale /= 2.0D;
		}

		return Mth.clampedLerp(d0 / 512.0D, d1 / 512.0D, d8);
	}
}
