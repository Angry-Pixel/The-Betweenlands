package thebetweenlands.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.registries.ParticleRegistry;

public record SpikeParticleOptions(ResourceLocation texture, boolean playSound) implements ParticleOptions {

	public static final MapCodec<SpikeParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("texture").forGetter(SpikeParticleOptions::texture),
			Codec.BOOL.fieldOf("play_sound").forGetter(SpikeParticleOptions::playSound))
		.apply(instance, SpikeParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpikeParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC,
		SpikeParticleOptions::texture,
		ByteBufCodecs.BOOL,
		SpikeParticleOptions::playSound,
		SpikeParticleOptions::new
	);

	@Override
	public ParticleType<?> getType() {
		return ParticleRegistry.SPIKE.get();
	}
}
