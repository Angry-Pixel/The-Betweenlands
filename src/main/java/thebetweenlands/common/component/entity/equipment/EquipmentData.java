package thebetweenlands.common.component.entity.equipment;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.inventory.AmuletInventory;
import thebetweenlands.common.inventory.EquipmentInventory;

import java.util.EnumMap;
import java.util.Map;

public class EquipmentData {

	final Map<EquipmentInventoryType, NonNullList<ItemStack>> allInventoryStacks = new EnumMap<>(EquipmentInventoryType.class);
	private final Map<EquipmentInventoryType, EquipmentInventory> inventories = new EnumMap<>(EquipmentInventoryType.class);
	private int amuletSlots = 1;
	int maxAmuletSlots = 3;
	int maxRingSlots = 2;
	int maxMiscSlots = 32;

	public EquipmentData() {
		for (EquipmentInventoryType inventory : EquipmentInventoryType.values()) {
			this.allInventoryStacks.put(inventory, NonNullList.withSize(this.getMazSizeForType(inventory), ItemStack.EMPTY));
		}
	}

	public int getMazSizeForType(EquipmentInventoryType type) {
		return switch (type) {
			case AMULET -> this.maxAmuletSlots;
			case RING -> this.maxRingSlots;
			case MISC -> this.maxMiscSlots;
		};
	}

	public EquipmentInventory getContainer(Entity entity, EquipmentInventoryType inventoryType) {
		EquipmentInventory inventory = this.inventories.get(inventoryType);
		if (inventory == null) {
			if (inventoryType == EquipmentInventoryType.AMULET) {
				inventory = new AmuletInventory(entity, this.allInventoryStacks.get(inventoryType));
			} else {
				inventory = new EquipmentInventory(entity, this.allInventoryStacks.get(inventoryType));
			}
			this.inventories.put(inventoryType, inventory);
		}
		return inventory;
	}


	public int getAmuletSlots() {
		return this.amuletSlots;
	}

	public void setAmuletSlots(int slots) {
		this.amuletSlots = slots;
	}
}
