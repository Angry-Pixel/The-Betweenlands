package thebetweenlands.common.network.serverbound;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;

public record RenameItemPacket(String name) implements CustomPacketPayload {

	public static final Type<RenameItemPacket> TYPE = new Type<>(TheBetweenlands.prefix("rename_item"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RenameItemPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, RenameItemPacket::name, RenameItemPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RenameItemPacket message, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.flow().isServerbound()) {
				ItemStack stack = ctx.player().getInventory().getSelected();
				if (message.name().isEmpty() || Component.translatable(stack.getDescriptionId()).getString().equals(message.name())) {
					stack.remove(DataComponents.CUSTOM_NAME);
				} else {
					stack.set(DataComponents.CUSTOM_NAME, Component.literal(message.name()));
				}
			}
		});
	}
}
