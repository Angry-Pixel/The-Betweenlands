package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FractalOpenSimplexNoiseSettings3D(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY, double noiseScaleZ, double noiseValueMultiplier, double noiseValueOffset) {

	public static final double DEFAULT_NOISE_VALUE_MULTIPLIER = 1.0D;
	public static final double DEFAULT_NOISE_VALUE_OFFSET = 0.0D;
	
	public static final MapCodec<FractalOpenSimplexNoiseSettings3D> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("noise_octaves").forGetter(FractalOpenSimplexNoiseSettings3D::octaves),
//					Codec.DOUBLE.fieldOf("noise_octave_scale").forGetter(FractalOpenSimplexNoiseSettings3D::noiseOctaveScale),
					Codec.BOOL.optionalFieldOf("additive_octave_seed", false).forGetter(FractalOpenSimplexNoiseSettings3D::additiveSeed),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale_x").forGetter(FractalOpenSimplexNoiseSettings3D::noiseScaleX),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale_y").forGetter(FractalOpenSimplexNoiseSettings3D::noiseScaleY),
					thebetweenlands.util.ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale_z").forGetter(FractalOpenSimplexNoiseSettings3D::noiseScaleZ),
					Codec.DOUBLE.optionalFieldOf("noise_value_multiplier", DEFAULT_NOISE_VALUE_MULTIPLIER).forGetter(FractalOpenSimplexNoiseSettings3D::noiseValueMultiplier),
					Codec.DOUBLE.optionalFieldOf("noise_value_offset", DEFAULT_NOISE_VALUE_OFFSET).forGetter(FractalOpenSimplexNoiseSettings3D::noiseValueOffset)
			).apply(instance, FractalOpenSimplexNoiseSettings3D::new));

	public static final Codec<FractalOpenSimplexNoiseSettings3D> CODEC = MAP_CODEC.codec();

	public static FractalOpenSimplexNoiseSettings3D of(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY, double noiseScaleZ, double noiseValueMultiplier, double noiseValueOffset) {
		return new FractalOpenSimplexNoiseSettings3D(octaves, additiveSeed, noiseScaleX, noiseScaleY, noiseScaleZ, noiseValueMultiplier, noiseValueOffset);
	}
	
	public static FractalOpenSimplexNoiseSettings3D of(int octaves, boolean additiveSeed, double noiseScaleX, double noiseScaleY, double noiseScaleZ) {
		return new FractalOpenSimplexNoiseSettings3D(octaves, additiveSeed, noiseScaleX, noiseScaleY, noiseScaleZ, DEFAULT_NOISE_VALUE_MULTIPLIER, DEFAULT_NOISE_VALUE_OFFSET);
	}

	public static FractalOpenSimplexNoiseSettings3D of(int octaves, boolean additiveSeed, double noiseScaleXZ, double noiseScaleY, double noiseValueMultiplier, double noiseValueOffset) {
		return new FractalOpenSimplexNoiseSettings3D(octaves, additiveSeed, noiseScaleXZ, noiseScaleY, noiseScaleXZ, noiseValueMultiplier, noiseValueOffset);
	}

	public static FractalOpenSimplexNoiseSettings3D of(int octaves, boolean additiveSeed, double noiseScaleXZ, double noiseScaleY) {
		return new FractalOpenSimplexNoiseSettings3D(octaves, additiveSeed, noiseScaleXZ, noiseScaleY, noiseScaleXZ, DEFAULT_NOISE_VALUE_MULTIPLIER, DEFAULT_NOISE_VALUE_OFFSET);
	}
}
