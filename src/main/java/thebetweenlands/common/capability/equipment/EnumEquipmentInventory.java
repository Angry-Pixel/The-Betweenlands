package thebetweenlands.common.capability.equipment;

import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.IInventory;

import javax.annotation.Nullable;

public enum EnumEquipmentInventory {
	MISC(3, 32), AMULET(1, 3), RING(2, 2);

	public static final ImmutableList<EnumEquipmentInventory> VALUES = ImmutableList.copyOf(EnumEquipmentInventory.values());

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
		for(EnumEquipmentInventory inv : EnumEquipmentInventory.VALUES) {
			if(inv.id == id) {
				return inv;
			}
		}
		return null;
	}
}
