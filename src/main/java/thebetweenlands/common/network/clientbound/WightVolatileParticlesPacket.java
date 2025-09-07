package thebetweenlands.common.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

public record WightVolatileParticlesPacket(int entityID) implements CustomPacketPayload {

	public static final Type<WightVolatileParticlesPacket> TYPE = new Type<>(TheBetweenlands.prefix("wight_volatile_particles"));

	public static final StreamCodec<RegistryFriendlyByteBuf, WightVolatileParticlesPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, WightVolatileParticlesPacket::entityID, WightVolatileParticlesPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(WightVolatileParticlesPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = context.player().level().getEntity(packet.entityID());
			if(entity != null) {
				for (int i = 0; i < 80; i++) {
					double px = entity.getX() + entity.level().getRandom().nextFloat() * 0.7F;
					double py = entity.getY() + entity.level().getRandom().nextFloat() * 2.2F;
					double pz = entity.getZ() + entity.level().getRandom().nextFloat() * 0.7F;
					Vec3 vec = new Vec3(px, py, pz).subtract(new Vec3(entity.getX() + 0.35F, entity.getY() + 1.1F, entity.getZ() + 0.35F)).normalize();
//					BLParticles.SWAMP_SMOKE.spawn(entity.level(), px, py, pz, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F));
				}
			}
		});
	}
}
