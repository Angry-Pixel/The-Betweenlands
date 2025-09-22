package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.registries.AttachmentRegistry;

public record EquipItemPacket(int entityID, int sourceSlot, int mode, EquipmentInventoryType inventory) implements CustomPacketPayload {

	public static final int EQUIP_MODE = 0;
	public static final int UNEQUIP_MODE = 1;

	public static final Type<EquipItemPacket> TYPE = new Type<>(TheBetweenlands.prefix("equip_item"));
	public static final StreamCodec<RegistryFriendlyByteBuf, EquipItemPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, EquipItemPacket::entityID,
		ByteBufCodecs.VAR_INT, EquipItemPacket::sourceSlot,
		ByteBufCodecs.VAR_INT, EquipItemPacket::mode,
		EquipmentInventoryType.STREAM_CODEC, EquipItemPacket::inventory,
		EquipItemPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(EquipItemPacket packet, IPayloadContext context) {
		if (context.flow().isServerbound()) {
			context.enqueueWork(() -> {
				Player sender = context.player();
				Entity target = sender.level().getEntity(packet.entityID());

				if (target != null && target.hasData(AttachmentRegistry.EQUIPMENT)) {
					switch (packet.mode()) {
						case EQUIP_MODE -> {
							//Equip
							if (packet.sourceSlot() >= -1 && packet.sourceSlot() < sender.getInventory().getContainerSize()) {
								ItemStack stack = packet.sourceSlot() == -1 ? sender.getItemInHand(InteractionHand.OFF_HAND) : sender.getInventory().getItem(packet.sourceSlot());
								ItemStack result = EquipmentHelper.equipItem(sender, target, stack, false);
								if (!sender.isCreative()) {
									if (packet.sourceSlot() == -1) {
										sender.setItemInHand(InteractionHand.OFF_HAND, result);
									} else {
										sender.getInventory().setItem(packet.sourceSlot(), result);
									}
								}
								if (result.isEmpty() || result.getCount() != stack.getCount()) {
									sender.displayClientMessage(Component.translatable("equipment.thebetweenlands.equipped", stack.getHoverName()), true);
								}
							}
						}
						case UNEQUIP_MODE -> {
							//Unequip
							if (packet.sourceSlot() >= 0) {
								ItemStack stack = EquipmentHelper.unequipItem(sender, target, packet.inventory(), packet.sourceSlot(), false);
								if (!stack.isEmpty()) {
									sender.displayClientMessage(Component.translatable("equipment.thebetweenlands.unequipped", stack.getHoverName()), true);
									if (!sender.getInventory().add(stack)) {
										target.spawnAtLocation(stack, target.getEyeHeight());
									}
								}
							}
						}
						default -> {
						}
					}
				}
			});
		}
	}
}
