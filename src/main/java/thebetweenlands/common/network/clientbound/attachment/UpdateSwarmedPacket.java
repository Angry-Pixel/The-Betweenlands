package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateSwarmedPacket(float strength, int hurtTimer, int damageTimer, float damage) implements CustomPacketPayload {

	public static final Type<UpdateSwarmedPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_swarmed"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSwarmedPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, UpdateSwarmedPacket::strength,
		ByteBufCodecs.INT, UpdateSwarmedPacket::hurtTimer,
		ByteBufCodecs.INT, UpdateSwarmedPacket::damageTimer,
		ByteBufCodecs.FLOAT, UpdateSwarmedPacket::damage,
		UpdateSwarmedPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateSwarmedPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.SWARMED, new SwarmedData(message.strength(), message.hurtTimer(), message.damageTimer(), message.damage())));
	}
}
