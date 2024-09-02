package thebetweenlands.common.network.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

public record WeedwoodBushRustlePacket(BlockPos bushPos, float strength) implements CustomPacketPayload {

	public static final Type<WeedwoodBushRustlePacket> TYPE = new Type<>(TheBetweenlands.prefix("weedwood_bush_rustle"));

	public static final StreamCodec<RegistryFriendlyByteBuf, WeedwoodBushRustlePacket> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, WeedwoodBushRustlePacket::bushPos,
		ByteBufCodecs.FLOAT, WeedwoodBushRustlePacket::strength,
		WeedwoodBushRustlePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(WeedwoodBushRustlePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			int leafCount = (int) (47 * packet.strength()) + 1;
			float x = packet.bushPos().getX() + 0.5F;
			float y = packet.bushPos().getY() + 0.5F;
			float z = packet.bushPos().getZ() + 0.5F;
			for (int i = 0; i < leafCount; i++) {
				float dx = level.getRandom().nextFloat() * 2 - 1;
				float dy = level.getRandom().nextFloat() * 2 - 0.5F;
				float dz = level.getRandom().nextFloat() * 2 - 1;
				float mag = 0.01F + level.getRandom().nextFloat() * 0.07F;
//				BLParticles.WEEDWOOD_LEAF.spawn(level, x, y, z, ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		});
	}
}
