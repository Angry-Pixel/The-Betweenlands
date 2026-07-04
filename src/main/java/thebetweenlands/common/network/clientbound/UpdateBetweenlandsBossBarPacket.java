package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

import java.util.UUID;

public record UpdateBetweenlandsBossBarPacket(UUID owner, float progress, Component name) implements CustomPacketPayload {

	public static final Type<UpdateBetweenlandsBossBarPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_custom_boss_bar"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBetweenlandsBossBarPacket> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, UpdateBetweenlandsBossBarPacket::owner,
		ByteBufCodecs.FLOAT, UpdateBetweenlandsBossBarPacket::progress,
		ComponentSerialization.TRUSTED_STREAM_CODEC, UpdateBetweenlandsBossBarPacket::name,
		UpdateBetweenlandsBossBarPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(UpdateBetweenlandsBossBarPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft minecraft = Minecraft.getInstance();
					minecraft.gui.getBossOverlay().events.get(packet.owner()).setProgress(packet.progress());
					minecraft.gui.getBossOverlay().events.get(packet.owner()).setName(packet.name());
				}
			});
		}
	}
}
