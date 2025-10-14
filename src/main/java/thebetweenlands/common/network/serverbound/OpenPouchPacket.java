package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.equipment.LurkerSkinPouchItem;

public class OpenPouchPacket implements CustomPacketPayload {

	public static final Type<OpenPouchPacket> TYPE = new Type<>(TheBetweenlands.prefix("open_pouch"));
	public static final OpenPouchPacket INSTANCE = new OpenPouchPacket();
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenPouchPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(OpenPouchPacket packet, IPayloadContext context) {
		if (context.flow().isServerbound()) {
			context.enqueueWork(() -> {
				Player player = context.player();
				ItemStack stack = LurkerSkinPouchItem.getFirstPouch(player);
				if (!stack.isEmpty()) {
					LurkerSkinPouchItem.openMenu(player, stack, ((LurkerSkinPouchItem) stack.getItem()).getSlots());
				}
			});
		}
	}
}
