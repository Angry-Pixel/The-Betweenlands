package thebetweenlands.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.registries.AttachmentRegistry;

public class AmuletInventory extends EquipmentInventory {

	public AmuletInventory(Entity attached, NonNullList<ItemStack> inventory) {
		super(attached, inventory);
	}

	@Override
	public int getContainerSize() {
		EquipmentData data = this.entity.getData(AttachmentRegistry.EQUIPMENT);
		return Math.min(data.getAmuletSlots(), data.getMazSizeForType(EquipmentInventoryType.AMULET));
	}
}
