package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.item.equipment.RingItem;
import thebetweenlands.common.registries.AttachmentRegistry;

public record UpdateRingStatePacket(int ringType, boolean active) implements CustomPacketPayload {

	public static final Type<UpdateRingStatePacket> TYPE = new Type<>(TheBetweenlands.prefix("update_ring_state"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRingStatePacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, UpdateRingStatePacket::ringType,
		ByteBufCodecs.BOOL, UpdateRingStatePacket::active,
		UpdateRingStatePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateRingStatePacket packet, IPayloadContext context) {
		if (context.flow().isServerbound()) {
			context.enqueueWork(() -> {
				Player player = context.player();

				EquipmentData data = player.getData(AttachmentRegistry.EQUIPMENT);
				Container inv = data.getContainer(player, EquipmentInventoryType.RING);

				if (packet.ringType() < inv.getContainerSize()) {
					int ringCount = 0;

					for (int i = 0; i < inv.getContainerSize(); i++) {
						ItemStack stack = inv.getItem(i);

						if (!stack.isEmpty() && stack.getItem() instanceof RingItem ring) {
							if (ringCount == packet.ringType()) {
								ring.onKeybindState(player, stack, inv, packet.active());
								break;
							}

							ringCount++;
						}
					}
				}
			});
		}
	}
}
