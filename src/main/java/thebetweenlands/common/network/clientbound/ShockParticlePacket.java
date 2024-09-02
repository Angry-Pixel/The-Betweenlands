package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

public record ShockParticlePacket(int entityID) implements CustomPacketPayload {

	public static final Type<ShockParticlePacket> TYPE = new Type<>(TheBetweenlands.prefix("shock_particle"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ShockParticlePacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ShockParticlePacket::entityID, ShockParticlePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ShockParticlePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			Entity victim = level.getEntity(packet.entityID());
			Entity view = Minecraft.getInstance().getCameraEntity();

			if (view != null) {
				float dst = view.distanceTo(victim);

				if (dst < 100) {
					float ox = (level.getRandom().nextFloat() - 0.5f) * 4;
					float oy = (level.getRandom().nextFloat() - 0.5f) * 4;
					float oz = (level.getRandom().nextFloat() - 0.5f) * 4;
					//TODO
//					ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(entity.world, entity.posX, entity.posY, entity.posZ,
//						ParticleArgs.get()
//							.withColor(0.5f, 0.4f, 1.0f, 0.9f)
//							.withData(new Vec3d(entity.posX + ox, entity.posY + oy, entity.posZ + oz)));
//
//					if (dst > 30) {
//						//lower quality
//						particle.setBaseSize(0.1f);
//						particle.setSubdivs(2, 1);
//						particle.setSplits(2);
//					}
//
//					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);

					if (dst < 16) {
						level.playSound(null, victim.blockPosition(), SoundRegistry.ZAP.get(), SoundSource.AMBIENT, 0.85F, 1.0F);
					}
				}
			}
		});
	}
}
