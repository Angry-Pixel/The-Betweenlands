package thebetweenlands.common.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateMudWalkerPacket(long reductionTime) implements CustomPacketPayload {

	public static final Type<UpdateMudWalkerPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_mud_walker"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMudWalkerPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, UpdateMudWalkerPacket::reductionTime,
		UpdateMudWalkerPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateMudWalkerPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.MUD_WALKER, new MudWalkerData(message.reductionTime())));
	}
}
