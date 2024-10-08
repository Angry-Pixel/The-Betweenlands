package thebetweenlands.common.network.clientbound;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public record SummonPeatMummyParticlesPacket(int mummyID) implements CustomPacketPayload {
	public static final Type<SummonPeatMummyParticlesPacket> TYPE = new Type<>(TheBetweenlands.prefix("peat_mummy_particles"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SummonPeatMummyParticlesPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, SummonPeatMummyParticlesPacket::mummyID, SummonPeatMummyParticlesPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SummonPeatMummyParticlesPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = context.player().level().getEntity(packet.mummyID());
			if (entity != null) {
				for (int i = 0; i < 250; i++) {
					double px = entity.getX() - 0.75F + entity.level().getRandom().nextFloat() * 1.5F;
					double py = entity.getY() - 2.0F + entity.level().getRandom().nextFloat() * 4.0F;
					double pz = entity.getZ() - 0.75F + entity.level().getRandom().nextFloat() * 1.5F;
					Vec3 vec = new Vec3(px, py, pz).subtract(new Vec3(entity.getX() + 0.35F, entity.getY() + 1.1F, entity.getZ() + 0.35F)).normalize();
					entity.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.MUD.get().defaultBlockState()), px, py, pz, vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F);
				}
			}
		});
	}
}
