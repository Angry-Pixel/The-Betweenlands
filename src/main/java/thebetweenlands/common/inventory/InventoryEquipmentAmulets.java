package thebetweenlands.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

public class InventoryEquipmentAmulets extends InventoryEquipment {
	public InventoryEquipmentAmulets(EquipmentEntityCapability capability, ItemStack[] inventory) {
		super(capability, inventory);
	}

	@Override
	public int getSizeInventory() {
		int amuletSlots = 1;
		IInventory amuletSlotInventory = this.capability.getInventory(EnumEquipmentInventory.AMULET_SLOT);
		for(int i = 0; i < amuletSlotInventory.getInventoryStackLimit(); i++) {
			ItemStack stack = amuletSlotInventory.getStackInSlot(i);
			if(stack != null /*&& stack.getItem() == ItemRegistry.AMULET_SLOT*/) { //TODO Requires amulet slots
				amuletSlots++;
			}
		}
		return Math.min(amuletSlots, EnumEquipmentInventory.AMULET.maxSize);
	}
}
