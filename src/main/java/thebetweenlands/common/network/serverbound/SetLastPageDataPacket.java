package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.registries.DataComponentRegistry;

public record SetLastPageDataPacket(String category, int page) implements CustomPacketPayload {

	public static final Type<SetLastPageDataPacket> TYPE = new Type<>(TheBetweenlands.prefix("set_last_used_page"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SetLastPageDataPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SetLastPageDataPacket::category,
		ByteBufCodecs.INT, SetLastPageDataPacket::page,
		SetLastPageDataPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SetLastPageDataPacket message, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.flow().isServerbound()) {
				ItemStack stack = ctx.player().getInventory().getSelected();
				stack.set(DataComponentRegistry.DISCOVERY_DATA, stack.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY).setCurrentPage(message.category(), message.page()));
			}
		});
	}
}
