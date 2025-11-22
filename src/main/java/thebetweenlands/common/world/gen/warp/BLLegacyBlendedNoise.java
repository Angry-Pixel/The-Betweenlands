package thebetweenlands.common.world.gen.warp;

import java.util.Locale;
import java.util.stream.IntStream;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

// Vanilla C&P of BlendedNoise, with a few changes to match 1.12
public class BLLegacyBlendedNoise implements DensityFunction.SimpleFunction {
    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001, 1000.0);
    private static final MapCodec<BLLegacyBlendedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec(
        p_230486_ -> p_230486_.group(
                    SCALE_RANGE.fieldOf("xz_scale").forGetter(p_230497_ -> p_230497_.xzScale),
                    SCALE_RANGE.fieldOf("y_scale").forGetter(p_230495_ -> p_230495_.yScale),
                    SCALE_RANGE.fieldOf("xz_factor").forGetter(p_230493_ -> p_230493_.xzFactor),
                    SCALE_RANGE.fieldOf("y_factor").forGetter(p_230490_ -> p_230490_.yFactor),
                    Codec.doubleRange(1.0, 8.0).fieldOf("smear_scale_multiplier").forGetter(p_230488_ -> p_230488_.smearScaleMultiplier)
                )
                .apply(p_230486_, BLLegacyBlendedNoise::createUnseeded)
    );
    public static final KeyDispatchDataCodec<BLLegacyBlendedNoise> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
    private final PerlinNoise minLimitNoise;
    private final PerlinNoise maxLimitNoise;
    private final PerlinNoise mainNoise;
    private final double xzMultiplier;
    private final double yMultiplier;
    private final double xzFactor;
    private final double yFactor;
    private final double smearScaleMultiplier;
    private final double maxValue;
    private final double xzScale;
    private final double yScale;

    public static BLLegacyBlendedNoise createUnseeded(double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
        return new BLLegacyBlendedNoise(new LegacyRandomSource(0L), xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier);
    }

    private BLLegacyBlendedNoise(
        PerlinNoise minLimitNoise,
        PerlinNoise maxLimitNoise,
        PerlinNoise mainNoise,
        double xzScale,
        double yScale,
        double xzFactor,
        double yFactor,
        double smearScaleMultiplier
    ) {
        this.minLimitNoise = minLimitNoise;
        this.maxLimitNoise = maxLimitNoise;
        this.mainNoise = mainNoise;
        this.xzScale = xzScale;
        this.yScale = yScale;
        this.xzFactor = xzFactor;
        this.yFactor = yFactor;
        this.smearScaleMultiplier = smearScaleMultiplier;
        this.xzMultiplier = 684.412 * this.xzScale;
        this.yMultiplier = 684.412 * this.yScale;
        this.maxValue = minLimitNoise.maxBrokenValue(this.yMultiplier);
    }

    public BLLegacyBlendedNoise(RandomSource random, double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
        this(
            PerlinNoise.createLegacyForBlendedNoise(random, IntStream.rangeClosed(-15, 0)),
            PerlinNoise.createLegacyForBlendedNoise(random, IntStream.rangeClosed(-15, 0)),
            PerlinNoise.createLegacyForBlendedNoise(random, IntStream.rangeClosed(-7, 0)),
            xzScale,
            yScale,
            xzFactor,
            yFactor,
            smearScaleMultiplier
        );
    }

    public BLLegacyBlendedNoise withNewRandom(RandomSource random) {
        return new BLLegacyBlendedNoise(random, this.xzScale, this.yScale, this.xzFactor, this.yFactor, this.smearScaleMultiplier);
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        double d0 = (double)context.blockX() * this.xzMultiplier;
        double d1 = (double)context.blockY() * this.yMultiplier;
        double d2 = (double)context.blockZ() * this.xzMultiplier;
        double d3 = d0 / this.xzFactor;
        double d4 = d1 / this.yFactor;
        double d5 = d2 / this.xzFactor;
        double d6 = this.yMultiplier * this.smearScaleMultiplier;
        double d7 = d6 / this.yFactor;
        double minLimitNoiseValue = 0.0;
        double maxLimitNoiseValue = 0.0;
        double mainNoiseValue = 0.0;
        double scale = 1.0;

        for (int i = 0; i < 8; i++) {
            ImprovedNoise improvednoise = this.mainNoise.getOctaveNoise(i);
            if (improvednoise != null) {
                mainNoiseValue += improvednoise.noise(PerlinNoise.wrap(d3 * scale), PerlinNoise.wrap(d4 * scale), PerlinNoise.wrap(d5 * scale), d7 * scale, d4 * scale) / scale;
            }

            scale /= 2.0;
        }

        /*
         * Biggest change is here to match with 1.12.2 generation:
         *     mainDensity = mainNoiseValue / 127.0;
         * instead of
         *     mainDensity = (mainNoiseValue / 10.0 + 1.0) / 2.0;
         */
        double mainDensity = mainNoiseValue / 127.0;
        boolean maxLimitOnly = mainDensity >= 1.0;
        boolean minLimitOnly = mainDensity <= 0.0;
        scale = 1.0;

        for (int j = 0; j < 16; j++) {
            double d12 = PerlinNoise.wrap(d0 * scale);
            double d13 = PerlinNoise.wrap(d1 * scale);
            double d14 = PerlinNoise.wrap(d2 * scale);
            double d15 = d6 * scale;
            if (!maxLimitOnly) {
                ImprovedNoise improvednoise1 = this.minLimitNoise.getOctaveNoise(j);
                if (improvednoise1 != null) {
                    minLimitNoiseValue += improvednoise1.noise(d12, d13, d14, d15, d1 * scale) / scale;
                }
            }

            if (!minLimitOnly) {
                ImprovedNoise improvednoise2 = this.maxLimitNoise.getOctaveNoise(j);
                if (improvednoise2 != null) {
                    maxLimitNoiseValue += improvednoise2.noise(d12, d13, d14, d15, d1 * scale) / scale;
                }
            }

            scale /= 2.0;
        }

        return Mth.clampedLerp(minLimitNoiseValue / 512.0, maxLimitNoiseValue / 512.0, mainDensity) / 128.0;
    }
    
    @Override
    public double minValue() {
        return -this.maxValue();
    }

    @Override
    public double maxValue() {
        return this.maxValue;
    }

    // Can probably remove this
    @VisibleForTesting
    public void parityConfigString(StringBuilder builder) {
        builder.append("BlendedNoise{minLimitNoise=");
        this.minLimitNoise.parityConfigString(builder);
        builder.append(", maxLimitNoise=");
        this.maxLimitNoise.parityConfigString(builder);
        builder.append(", mainNoise=");
        this.mainNoise.parityConfigString(builder);
        builder.append(
                String.format(
                    Locale.ROOT,
                    ", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=4, cellHeight=8",
                    684.412,
                    684.412,
                    8.555150000000001,
                    4.277575000000001
                )
            )
            .append('}');
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
