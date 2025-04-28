package thebetweenlands.common.network.clientbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.gui.screen.ItemRenameScreen;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DataComponentRegistry;

public record OpenRenameScreenPacket(int maxLength) implements CustomPacketPayload {

	public static final Type<OpenRenameScreenPacket> TYPE = new Type<>(TheBetweenlands.prefix("open_rename_screen"));
	public static final StreamCodec<ByteBuf, OpenRenameScreenPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, OpenRenameScreenPacket::maxLength, OpenRenameScreenPacket::new);

	public OpenRenameScreenPacket(ItemStack stack) {
		this(stack.get(DataComponentRegistry.RENAMABLE).maxLength());
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(OpenRenameScreenPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft.getInstance().setScreen(new ItemRenameScreen(Component.empty(), packet.maxLength()));
				}
			});
		}
	}
}
