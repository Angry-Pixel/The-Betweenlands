package thebetweenlands.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateRotSmellPacket(long smellStamp, long immuneStamp) implements CustomPacketPayload {

	public static final Type<UpdateRotSmellPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_rot_smell"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRotSmellPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, UpdateRotSmellPacket::smellStamp,
		ByteBufCodecs.VAR_LONG, UpdateRotSmellPacket::immuneStamp,
		UpdateRotSmellPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateRotSmellPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.ROT_SMELL, new RotSmellData(message.smellStamp(), message.immuneStamp())));
	}
}
