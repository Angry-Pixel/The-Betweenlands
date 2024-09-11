package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.client.gui.screen.LoreScrapScreen;
import thebetweenlands.common.TheBetweenlands;

public record OpenLoreScrapPacket(ResourceLocation itemName) implements CustomPacketPayload {

	public static final Type<OpenLoreScrapPacket> TYPE = new Type<>(TheBetweenlands.prefix("open_lore_scrap_screen"));

	public static final StreamCodec<RegistryFriendlyByteBuf, OpenLoreScrapPacket> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, OpenLoreScrapPacket::itemName, OpenLoreScrapPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(OpenLoreScrapPacket message, IPayloadContext ctx) {
		//ensure this is only done on clients as this uses client only code
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft.getInstance().setScreen(new LoreScrapScreen(message.itemName()));
				}
			});
		}
	}
}
