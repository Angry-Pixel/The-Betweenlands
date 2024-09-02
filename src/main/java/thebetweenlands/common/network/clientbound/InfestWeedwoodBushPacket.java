package thebetweenlands.common.network.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.GrubHubBlockEntity;

public record InfestWeedwoodBushPacket(BlockPos tilePos, BlockPos bushPos) implements CustomPacketPayload {

	public static final Type<InfestWeedwoodBushPacket> TYPE = new Type<>(TheBetweenlands.prefix("infest_weedwood_bush"));
	public static final StreamCodec<RegistryFriendlyByteBuf, InfestWeedwoodBushPacket> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, InfestWeedwoodBushPacket::tilePos,
		BlockPos.STREAM_CODEC, InfestWeedwoodBushPacket::bushPos,
		InfestWeedwoodBushPacket::new);

	public InfestWeedwoodBushPacket(GrubHubBlockEntity entity, BlockPos bushPos) {
		this(entity.getBlockPos(), bushPos);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(InfestWeedwoodBushPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			if (level.getBlockEntity(packet.tilePos()) instanceof GrubHubBlockEntity hub) {
				hub.switchTextureCount = 10;

				Vec3 dir = new Vec3((packet.bushPos().getX() + 0.5D) - (packet.tilePos().getX() + 0.5D), (packet.bushPos().getY() + 1D) - (packet.tilePos().getY() + 0.325D), (packet.bushPos().getZ() + 0.5D) - (packet.tilePos().getZ() + 0.5D));

				for (int i = 0; i < 20 + level.getRandom().nextInt(5); i++) {
//					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, packet.tilePos().getX() + 0.5F, packet.tilePos().getY() + 0.325F, packet.tilePos().getZ() + 0.5F,
//						ParticleArgs.get()
//							.withMotion(dir.x * 0.08f, dir.y * 0.08F, dir.z * 0.08F)
//							.withScale(0.6f + level.getRandom().nextFloat() * 5.0F)
//							.withColor(0.45f, 0.1f, 0.6f, 0.08f)
//							.withData(80, true, 0.01F, true)));
				}
			}
		});
	}
}
