package thebetweenlands.common.capability.equipment;

import net.minecraft.inventory.IInventory;
import thebetweenlands.common.capability.base.ISerializableCapability;

public interface IEquipmentCapability extends ISerializableCapability {
	/**
	 * Returns the inventory for the specified equipment inventory
	 * @param inventory
	 * @return
	 */
	public IInventory getInventory(EnumEquipmentInventory inventory);
}
