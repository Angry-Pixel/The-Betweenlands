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
import thebetweenlands.api.entity.bossbar.BetweenlandsBossBar;
import thebetweenlands.api.entity.bossbar.BetweenlandsClientBossBar;
import thebetweenlands.common.TheBetweenlands;

import java.util.UUID;

public record AddBetweenlandsBossBarPacket(UUID owner, Component name, float progress, BetweenlandsBossBar.BossType bossType) implements CustomPacketPayload {

	public static final Type<AddBetweenlandsBossBarPacket> TYPE = new CustomPacketPayload.Type<>(TheBetweenlands.prefix("add_custom_boss_bar"));
	public static final StreamCodec<RegistryFriendlyByteBuf, AddBetweenlandsBossBarPacket> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddBetweenlandsBossBarPacket::owner,
		ComponentSerialization.STREAM_CODEC, AddBetweenlandsBossBarPacket::name,
		ByteBufCodecs.FLOAT, AddBetweenlandsBossBarPacket::progress,
		BetweenlandsBossBar.BossType.STREAM_CODEC, AddBetweenlandsBossBarPacket::bossType,
		AddBetweenlandsBossBarPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(AddBetweenlandsBossBarPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Minecraft minecraft = Minecraft.getInstance();
					minecraft.gui.getBossOverlay().events.put(packet.owner(), new BetweenlandsClientBossBar(packet.owner(), packet.name(), packet.progress(), packet.bossType()));
				}
			});
		}
	}
}
