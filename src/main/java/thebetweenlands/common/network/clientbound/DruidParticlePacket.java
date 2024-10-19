package thebetweenlands.common.network.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;

public record DruidParticlePacket(BlockPos pos) implements CustomPacketPayload {

	public static final Type<DruidParticlePacket> TYPE = new Type<>(TheBetweenlands.prefix("druid_particles"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DruidParticlePacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, DruidParticlePacket::pos, DruidParticlePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(DruidParticlePacket message, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				Level level = ctx.player().level();
				for (int a = 0; a < 360; a += 4) {
					double rad = a * Mth.DEG_TO_RAD;
					TheBetweenlands.createParticle(ParticleTypes.WHITE_SMOKE, level, message.pos().getX() - Mth.sin((float) rad) * 0.25D, message.pos().getY(), message.pos().getZ() + Mth.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-Mth.sin((float) rad) * 0.1D, 0.01D, Mth.cos((float) rad) * 0.1));
					TheBetweenlands.createParticle(ParticleTypes.WHITE_SMOKE, level, message.pos().getX() - Mth.sin((float) rad) * 0.25D, message.pos().getY() + 0.5D, message.pos().getZ() + Mth.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-Mth.sin((float) rad) * 0.1D, 0.01D, Mth.cos((float) rad) * 0.1));
					TheBetweenlands.createParticle(ParticleTypes.WHITE_SMOKE, level, message.pos().getX() - Mth.sin((float) rad) * 0.25D, message.pos().getY() + 1D, message.pos().getZ() + Mth.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-Mth.sin((float) rad) * 0.1D, 0.01D, Mth.cos((float) rad) * 0.1));
				}
			});
		}
	}
}
