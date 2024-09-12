package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateInfestationPacket(long immunityTime) implements CustomPacketPayload {

	public static final Type<UpdateInfestationPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_infestation"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInfestationPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, UpdateInfestationPacket::immunityTime,
		UpdateInfestationPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateInfestationPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.INFESTATION_IGNORE, new InfestationIgnoreData(message.immunityTime())));
	}
}
