package thebetweenlands.common.network.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

public record SoundRipplePacket(BlockPos pos, int delay) implements CustomPacketPayload {

	public static final Type<SoundRipplePacket> TYPE = new Type<>(TheBetweenlands.prefix("sound_ripple"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SoundRipplePacket> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SoundRipplePacket::pos,
		ByteBufCodecs.INT, SoundRipplePacket::delay,
		SoundRipplePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SoundRipplePacket packet, IPayloadContext context) {
		//context.enqueueWork(() -> BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.UNBATCHED, BLParticles.SOUND_RIPPLE.spawn(world, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, ParticleArgs.get().withData(this.delay))));
	}
}
