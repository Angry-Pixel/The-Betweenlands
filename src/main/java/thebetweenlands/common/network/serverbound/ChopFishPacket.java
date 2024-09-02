package thebetweenlands.common.network.serverbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.FishTrimmingTableMenu;

public class ChopFishPacket implements CustomPacketPayload {
	public static final Type<ChopFishPacket> TYPE = new Type<>(TheBetweenlands.prefix("chop_fish"));
	public static final ChopFishPacket INSTANCE = new ChopFishPacket();
	public static final StreamCodec<ByteBuf, ChopFishPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.flow().isServerbound() && ctx.player().containerMenu instanceof FishTrimmingTableMenu table) {
				table.chop((ServerPlayer) ctx.player());
			}
		});
	}
}
