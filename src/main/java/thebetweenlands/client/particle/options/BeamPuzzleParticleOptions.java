package thebetweenlands.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import thebetweenlands.common.registries.ParticleRegistry;

/**
 * @param maxAlpha   the maximum particle alpha after fading in/out
 * @param fadeTime   the time (in ticks) it takes to fade to an alpha of {@code maxAlpha}
 * @param useShaders whether to add a shader light source to this beam
 */
public record BeamPuzzleParticleOptions(float maxAlpha, int fadeTime, boolean useShaders) implements ParticleOptions {

	public static final MapCodec<BeamPuzzleParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("max_alpha", 1.0F).validate(BeamPuzzleParticleOptions::validateMaxAlpha).forGetter(BeamPuzzleParticleOptions::maxAlpha),
			ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("fade_time", 10).forGetter(BeamPuzzleParticleOptions::fadeTime),
			Codec.BOOL.fieldOf("use_shaders").forGetter(BeamPuzzleParticleOptions::useShaders)
		).apply(instance, BeamPuzzleParticleOptions::new));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, BeamPuzzleParticleOptions> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, BeamPuzzleParticleOptions::maxAlpha,
			ByteBufCodecs.INT,   BeamPuzzleParticleOptions::fadeTime,
			ByteBufCodecs.BOOL,  BeamPuzzleParticleOptions::useShaders,
			BeamPuzzleParticleOptions::new
		);
	
	private static DataResult<Float> validateMaxAlpha(float maxAlpha) {
		if (0.0F <= maxAlpha && maxAlpha <= 1.0F) {
			return DataResult.success(maxAlpha, Lifecycle.stable());
		} else {
			return DataResult.error(() -> "max_alpha must be a float in range [0.0, 1.0]", Lifecycle.stable());
		}
	}
	
	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.BEAM_PUZZLE_BEAM.get();
	}

}
