package thebetweenlands.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

public class InventoryEquipmentAmulets extends InventoryEquipment {
	public InventoryEquipmentAmulets(EquipmentEntityCapability capability, NonNullList<ItemStack> inventory) {
		super(capability, inventory);
	}

	@Override
	public int getSizeInventory() {
		return Math.min(capability.getAmuletSlots(), EnumEquipmentInventory.AMULET.maxSize);
	}
}
