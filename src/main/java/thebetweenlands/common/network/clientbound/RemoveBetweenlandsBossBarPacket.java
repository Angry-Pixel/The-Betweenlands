package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

import java.util.UUID;

public record RemoveBetweenlandsBossBarPacket(UUID owner) implements CustomPacketPayload {

	public static final Type<RemoveBetweenlandsBossBarPacket> TYPE = new Type<>(TheBetweenlands.prefix("remove_custom_boss_bar"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveBetweenlandsBossBarPacket> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, RemoveBetweenlandsBossBarPacket::owner,
		RemoveBetweenlandsBossBarPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(RemoveBetweenlandsBossBarPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft minecraft = Minecraft.getInstance();
					minecraft.gui.getBossOverlay().events.remove(packet.owner());
				}
			});
		}
	}
}
