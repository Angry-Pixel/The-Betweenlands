package thebetweenlands.common.capability.equipment;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;

public interface IEquipmentCapability {
	/**
	 * Returns the inventory for the specified equipment inventory.
	 * The inventory can be tickable if {@link ITickable} is implemented.
	 * @param inventory
	 * @return
	 */
	@Nonnull
	public IInventory getInventory(EnumEquipmentInventory inventory);
}
