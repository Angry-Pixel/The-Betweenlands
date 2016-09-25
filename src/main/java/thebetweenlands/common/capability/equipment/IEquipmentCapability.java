package thebetweenlands.common.capability.equipment;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import thebetweenlands.common.capability.base.ISerializableCapability;

public interface IEquipmentCapability extends ISerializableCapability {
	/**
	 * Returns the inventory for the specified equipment inventory.
	 * The inventory can be tickable if {@link ITickable} is implemented.
	 * @param inventory
	 * @return
	 */
	@Nonnull
	public IInventory getInventory(EnumEquipmentInventory inventory);
}
