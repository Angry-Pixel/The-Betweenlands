package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.PuppetData;
import thebetweenlands.common.component.entity.PuppeteerData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.ExtraCodecs;

import java.util.Optional;
import java.util.UUID;

public record UpdatePuppeteerPacket(int activatingId, int shieldRotation, int shieldData) implements CustomPacketPayload {

	public static final Type<UpdatePuppeteerPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_puppeteer"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePuppeteerPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, UpdatePuppeteerPacket::activatingId,
		ByteBufCodecs.INT, UpdatePuppeteerPacket::shieldRotation,
		ByteBufCodecs.INT, UpdatePuppeteerPacket::shieldData,
		UpdatePuppeteerPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdatePuppeteerPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.PUPPETEER, new PuppeteerData(message.activatingId(), message.shieldRotation(), message.shieldData())));
	}
}
