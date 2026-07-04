package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.rowboat.WeedwoodRowboat;

public record RowboatRowPacket(boolean starboard, boolean port, float progressStarboard, float progressPort) implements CustomPacketPayload {

	public static final Type<RowboatRowPacket> TYPE = new Type<>(TheBetweenlands.prefix("set_rowboat_rowing_status"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RowboatRowPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, RowboatRowPacket::starboard,
		ByteBufCodecs.BOOL, RowboatRowPacket::port,
		ByteBufCodecs.FLOAT, RowboatRowPacket::progressStarboard,
		ByteBufCodecs.FLOAT, RowboatRowPacket::progressPort,
		RowboatRowPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RowboatRowPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.flow().isServerbound()) {
				Player player = context.player();
				if (player.getVehicle() instanceof WeedwoodRowboat rowboat) {
					rowboat.setOarStates(packet.starboard(), packet.port(), packet.progressStarboard(), packet.progressPort());
				}
			}
		});
	}
}
