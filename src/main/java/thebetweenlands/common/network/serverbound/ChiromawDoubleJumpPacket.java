package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.chiromaw.TameChiromaw;

public record ChiromawDoubleJumpPacket(int entityID) implements CustomPacketPayload {

	public static final Type<ChiromawDoubleJumpPacket> TYPE = new Type<>(TheBetweenlands.prefix("chiromaw_double_jump"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChiromawDoubleJumpPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, ChiromawDoubleJumpPacket::entityID,
		ChiromawDoubleJumpPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ChiromawDoubleJumpPacket packet, IPayloadContext context) {
		if (context.flow().isServerbound()) {
			context.enqueueWork(() -> {
				Player sender = context.player();
				Entity target = sender.level().getEntity(packet.entityID());

				if (target instanceof TameChiromaw chiromaw) {
					if (sender.getFirstPassenger() == chiromaw) {
						chiromaw.performDoubleJump(sender);
					}
				}
			});
		}
	}
}
