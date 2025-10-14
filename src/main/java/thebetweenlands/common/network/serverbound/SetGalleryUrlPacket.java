package thebetweenlands.common.network.serverbound;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.GalleryFrame;

public record SetGalleryUrlPacket(int frameID, String url) implements CustomPacketPayload {
	public static final Type<SetGalleryUrlPacket> TYPE = new Type<>(TheBetweenlands.prefix("set_gallery_url"));
	public static final StreamCodec<ByteBuf, SetGalleryUrlPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, SetGalleryUrlPacket::frameID,
		ByteBufCodecs.STRING_UTF8, SetGalleryUrlPacket::url,
		SetGalleryUrlPacket::new
	);

	public SetGalleryUrlPacket(GalleryFrame frame, String url) {
		this(frame.getId(), url);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SetGalleryUrlPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity targetEntity = context.player().level().getEntity(packet.frameID());
			if (!packet.url().isBlank() && targetEntity instanceof GalleryFrame frame && context.player().distanceTo(frame) < 6.0D && !packet.url().isEmpty() && packet.url().length() <= 256) {
				if (!packet.url().startsWith("https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery")) {
					TheBetweenlands.LOGGER.warn("Malicious Packet detected: SetGalleryUrlPacket was sent with the following link by {} ({}): {}", context.player().getDisplayName(), context.player().getStringUUID(), packet.url());
					return;
				}
				frame.setUrl(packet.url());
			}
		});
	}
}
