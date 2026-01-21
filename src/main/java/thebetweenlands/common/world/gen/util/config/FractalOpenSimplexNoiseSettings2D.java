package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FractalOpenSimplexNoiseSettings2D(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY, double noiseValueMultiplier, double noiseValueOffset) {

	public static final double DEFAULT_NOISE_VALUE_MULTIPLIER = 1.0D;
	public static final double DEFAULT_NOISE_VALUE_OFFSET = 0.0D;
	
	public static final MapCodec<FractalOpenSimplexNoiseSettings2D> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("noise_octaves").forGetter(FractalOpenSimplexNoiseSettings2D::octaves),
//					Codec.DOUBLE.fieldOf("noise_octave_scale").forGetter(FractalOpenSimplexNoiseSettings2D::noiseOctaveScale),
					Codec.BOOL.optionalFieldOf("additive_octave_seed", false).forGetter(FractalOpenSimplexNoiseSettings2D::additiveSeed),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale_x").forGetter(FractalOpenSimplexNoiseSettings2D::noiseScaleX),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale_y").forGetter(FractalOpenSimplexNoiseSettings2D::noiseScaleY),
					Codec.DOUBLE.optionalFieldOf("noise_value_multiplier", DEFAULT_NOISE_VALUE_MULTIPLIER).forGetter(FractalOpenSimplexNoiseSettings2D::noiseValueMultiplier),
					Codec.DOUBLE.optionalFieldOf("noise_value_offset", DEFAULT_NOISE_VALUE_OFFSET).forGetter(FractalOpenSimplexNoiseSettings2D::noiseValueOffset)
			).apply(instance, FractalOpenSimplexNoiseSettings2D::new));

	public static final Codec<FractalOpenSimplexNoiseSettings2D> CODEC = MAP_CODEC.codec();

	public static FractalOpenSimplexNoiseSettings2D of(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY, double noiseValueMultiplier, double noiseValueOffset) {
		return new FractalOpenSimplexNoiseSettings2D(octaves, additiveSeed, noiseScaleX, noiseScaleY, noiseValueMultiplier, noiseValueOffset);
	}
	
	public static FractalOpenSimplexNoiseSettings2D of(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY) {
		return new FractalOpenSimplexNoiseSettings2D(octaves, additiveSeed, noiseScaleX, noiseScaleY, DEFAULT_NOISE_VALUE_MULTIPLIER, DEFAULT_NOISE_VALUE_OFFSET);
	}

	public static FractalOpenSimplexNoiseSettings2D of(int octaves, boolean additiveSeed, double noiseScaleXZ, double noiseValueMultiplier, double noiseValueOffset) {
		return new FractalOpenSimplexNoiseSettings2D(octaves, additiveSeed, noiseScaleXZ, noiseScaleXZ, noiseValueMultiplier, noiseValueOffset);
	}

	public static FractalOpenSimplexNoiseSettings2D of(int octaves, boolean additiveSeed, double noiseScaleXZ) {
		return new FractalOpenSimplexNoiseSettings2D(octaves, additiveSeed, noiseScaleXZ, noiseScaleXZ, DEFAULT_NOISE_VALUE_MULTIPLIER, DEFAULT_NOISE_VALUE_OFFSET);
	}
}
