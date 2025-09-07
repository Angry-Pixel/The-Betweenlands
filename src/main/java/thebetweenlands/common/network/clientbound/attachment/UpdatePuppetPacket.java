package thebetweenlands.common.network.clientbound.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.PuppetData;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.ExtraCodecs;

import java.util.Optional;
import java.util.UUID;

public record UpdatePuppetPacket(Optional<UUID> puppeteerUUID, int remainingTicks, boolean stay, boolean guard, Optional<BlockPos> guardHome, Optional<UUID> ringUUID, int recruitmentCost) implements CustomPacketPayload {

	public static final Type<UpdatePuppetPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_puppet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePuppetPacket> STREAM_CODEC = ExtraCodecs.composite(
		ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), UpdatePuppetPacket::puppeteerUUID,
		ByteBufCodecs.INT, UpdatePuppetPacket::remainingTicks,
		ByteBufCodecs.BOOL, UpdatePuppetPacket::stay,
		ByteBufCodecs.BOOL, UpdatePuppetPacket::guard,
		ByteBufCodecs.optional(BlockPos.STREAM_CODEC), UpdatePuppetPacket::guardHome,
		ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), UpdatePuppetPacket::ringUUID,
		ByteBufCodecs.INT, UpdatePuppetPacket::recruitmentCost,
		UpdatePuppetPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdatePuppetPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.PUPPET, new PuppetData(message.puppeteerUUID(), message.remainingTicks(), message.stay(), message.guard(), message.guardHome(), message.ringUUID(), message.recruitmentCost())));
	}
}
