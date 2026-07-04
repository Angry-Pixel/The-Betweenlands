package thebetweenlands.common.handler;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.registries.ItemRegistry;

public class MagicItemMagnetHandler {
	public static void onItemPickup(ItemEntityPickupEvent.Post event) {
		if(!event.getPlayer().level().isClientSide()) {
			ItemStack magnet = EquipmentHelper.getEquipment(EquipmentInventoryType.MISC, event.getPlayer(), ItemRegistry.MAGIC_ITEM_MAGNET.get());
			if(!magnet.isEmpty()) {
				//Damage magnet on pickup
				magnet.hurtAndBreak(1, event.getPlayer(), EquipmentSlot.MAINHAND);
			}
		}
	}
}
