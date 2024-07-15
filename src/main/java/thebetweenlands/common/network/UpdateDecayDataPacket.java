package thebetweenlands.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateDecayDataPacket(int level, int prevLevel, float saturation, float acceleration) implements CustomPacketPayload {

	public static final Type<UpdateDecayDataPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_decay_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateDecayDataPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, UpdateDecayDataPacket::level,
		ByteBufCodecs.INT, UpdateDecayDataPacket::prevLevel,
		ByteBufCodecs.FLOAT, UpdateDecayDataPacket::saturation,
		ByteBufCodecs.FLOAT, UpdateDecayDataPacket::acceleration,
		UpdateDecayDataPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateDecayDataPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.DECAY, new DecayData(message.level(), message.prevLevel(), message.saturation(), message.acceleration())));
	}
}
