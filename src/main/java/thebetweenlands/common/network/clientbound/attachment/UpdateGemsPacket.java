package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.component.entity.circlegem.CircleGem;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.List;

public record UpdateGemsPacket(List<CircleGem> gems) implements CustomPacketPayload {

	public static final Type<UpdateGemsPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_circle_gems"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateGemsPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.fromCodec(CircleGem.CODEC).apply(ByteBufCodecs.list()), UpdateGemsPacket::gems, UpdateGemsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateGemsPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.CIRCLE_GEM, new CircleGemData(message.gems())));
	}
}
