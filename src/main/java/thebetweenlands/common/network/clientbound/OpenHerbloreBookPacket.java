package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.client.gui.screen.ItemRenameScreen;
import thebetweenlands.common.TheBetweenlands;

public record OpenHerbloreBookPacket(ItemStack book) implements CustomPacketPayload {

	public static final Type<OpenHerbloreBookPacket> TYPE = new Type<>(TheBetweenlands.prefix("open_herblore_book"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenHerbloreBookPacket> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, OpenHerbloreBookPacket::book, OpenHerbloreBookPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(OpenHerbloreBookPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft.getInstance().setScreen(new HerbloreManualScreen(packet.book()));
				}
			});
		}
	}
}
