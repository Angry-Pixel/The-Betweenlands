package thebetweenlands.common.network.clientbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.gui.screen.ItemRenameScreen;
import thebetweenlands.common.TheBetweenlands;

public class OpenRenameScreenPacket implements CustomPacketPayload {

	public static final Type<OpenRenameScreenPacket> TYPE = new Type<>(TheBetweenlands.prefix("open_rename_screen"));
	public static final OpenRenameScreenPacket INSTANCE = new OpenRenameScreenPacket();
	public static final StreamCodec<ByteBuf, OpenRenameScreenPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft.getInstance().setScreen(new ItemRenameScreen(Component.empty()));
				}
			});
		}
	}
}
