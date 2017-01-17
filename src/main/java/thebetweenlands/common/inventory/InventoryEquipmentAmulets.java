package thebetweenlands.common.inventory;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

public class InventoryEquipmentAmulets extends InventoryEquipment {
	public InventoryEquipmentAmulets(EquipmentEntityCapability capability, ItemStack[] inventory) {
		super(capability, inventory);
	}

	@Override
	public int getSizeInventory() {
		return Math.min(capability.getAmuletSlots(), EnumEquipmentInventory.AMULET.maxSize);
	}
}
