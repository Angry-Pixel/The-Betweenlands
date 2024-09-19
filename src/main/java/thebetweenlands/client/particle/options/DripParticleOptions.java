package thebetweenlands.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thebetweenlands.common.registries.ParticleRegistry;

public record DripParticleOptions(boolean spawnSplashes, boolean spawnRipples) implements ParticleOptions {

	public static final MapCodec<DripParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("spawn_splashes").forGetter(DripParticleOptions::spawnSplashes),
			Codec.BOOL.fieldOf("spawn_ripples").forGetter(DripParticleOptions::spawnRipples))
		.apply(instance, DripParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DripParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		DripParticleOptions::spawnSplashes,
		ByteBufCodecs.BOOL,
		DripParticleOptions::spawnRipples,
		DripParticleOptions::new
	);

	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.FANCY_DRIP.get();
	}
}
