package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.FallDamageReductionData;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateFallReductionPacket(long immunityTime) implements CustomPacketPayload {

	public static final Type<UpdateFallReductionPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_fall_reduction"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFallReductionPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, UpdateFallReductionPacket::immunityTime,
		UpdateFallReductionPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateFallReductionPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.FALL_DAMAGE_REDUCTION, new FallDamageReductionData(message.immunityTime())));
	}
}
