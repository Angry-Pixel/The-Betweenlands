package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.inventory.LurkerSkinPouchMenu;
import thebetweenlands.common.inventory.container.SecureItemContainer;
import thebetweenlands.common.item.equipment.LurkerSkinPouchItem;
import thebetweenlands.common.registries.AttachmentRegistry;

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
