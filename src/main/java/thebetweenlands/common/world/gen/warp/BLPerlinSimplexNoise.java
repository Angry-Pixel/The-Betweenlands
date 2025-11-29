package thebetweenlands.common.world.gen.warp;

import java.util.List;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

// C&P of Vanilla PerlinSimplexNoise with extra options
public class BLPerlinSimplexNoise {
    private final SimplexNoise[] noiseLevels;
    private final double highestFreqValueFactor;
    private final double highestFreqInputFactor;

    public BLPerlinSimplexNoise(RandomSource random, List<Integer> octaves) {
        this(random, new IntRBTreeSet(octaves));
    }

    private BLPerlinSimplexNoise(RandomSource random, IntSortedSet octaves) {
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -octaves.firstInt();
            int j = octaves.lastInt();
            int k = i + j + 1;
            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                SimplexNoise simplexnoise = new SimplexNoise(random);
                int l = j;
                this.noiseLevels = new SimplexNoise[k];
                if (j >= 0 && j < k && octaves.contains(0)) {
                    this.noiseLevels[j] = simplexnoise;
                }

                for (int i1 = j + 1; i1 < k; i1++) {
                    if (i1 >= 0 && octaves.contains(l - i1)) {
                        this.noiseLevels[i1] = new SimplexNoise(random);
                    } else {
                        random.consumeCount(262);
                    }
                }

                if (j > 0) {
                    long k1 = (long)(simplexnoise.getValue(simplexnoise.xo, simplexnoise.yo, simplexnoise.zo) * 9.223372E18F);
                    RandomSource randomsource = new WorldgenRandom(new LegacyRandomSource(k1));

                    for (int j1 = l - 1; j1 >= 0; j1--) {
                        if (j1 < k && octaves.contains(l - j1)) {
                            this.noiseLevels[j1] = new SimplexNoise(randomsource);
                        } else {
                            randomsource.consumeCount(262);
                        }
                    }
                }

                this.highestFreqInputFactor = Math.pow(2.0, (double)j);
                this.highestFreqValueFactor = 1.0 / (Math.pow(2.0, (double)k) - 1.0);
            }
        }
    }

    public double getValue(double x, double y, boolean useNoiseOffsets) {
        double d0 = 0.0;
        double d1 = this.highestFreqInputFactor;
        double d2 = this.highestFreqValueFactor;

        for (SimplexNoise simplexnoise : this.noiseLevels) {
            if (simplexnoise != null) {
                d0 += simplexnoise.getValue(x * d1 + (useNoiseOffsets ? simplexnoise.xo : 0.0), y * d1 + (useNoiseOffsets ? simplexnoise.yo : 0.0)) * d2;
            }

            d1 /= 2.0;
            d2 *= 2.0;
        }

        return d0;
    }

    public double getValue(double x, double y, boolean useNoiseOffsets, double octaveScaleFactor, double octiveAmplifierFactor) {
    	return this.getValue(x, y, useNoiseOffsets, octaveScaleFactor, octiveAmplifierFactor, this.highestFreqInputFactor, this.highestFreqValueFactor);
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
}
