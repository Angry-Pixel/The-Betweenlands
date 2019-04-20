package thebetweenlands.common.capability.equipment;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;

public enum EnumEquipmentInventory {
	MISC(3, 32), AMULET(1, 3), RING(2, 2);

	public static final EnumEquipmentInventory[] VALUES = EnumEquipmentInventory.values();

	/**
	 * The ID of this inventory
	 */
	public final int id;

	/**
	 * The maximum stack size (check {@link IInventory#getSizeInventory()} for the actual size!)
	 */
	public final int maxSize;

	private EnumEquipmentInventory(int id, int maxSize) {
		this.id = id;
		this.maxSize = maxSize;
	}

	@Nullable
	public static EnumEquipmentInventory fromID(int id) {
		for(EnumEquipmentInventory inv : EnumEquipmentInventory.values()) {
			if(inv.id == id) {
				return inv;
			}
		}
		return null;
	}
}
