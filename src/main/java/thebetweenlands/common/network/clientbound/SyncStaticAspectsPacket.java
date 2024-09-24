package thebetweenlands.common.network.clientbound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;

public record SyncStaticAspectsPacket(CompoundTag tag) implements CustomPacketPayload {
	public static final Type<SyncStaticAspectsPacket> TYPE = new Type<>(TheBetweenlands.prefix("sync_static_aspect"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncStaticAspectsPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, SyncStaticAspectsPacket::tag, SyncStaticAspectsPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncStaticAspectsPacket packet, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> AspectManager.get(ctx.player().level()).loadStaticAspects(packet.tag(), ctx.player().registryAccess()));
		}
	}
}
