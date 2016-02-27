package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;

public interface IEquippable {
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack);
	public boolean canEquip(ItemStack stack, Entity entity, EquipmentInventory inventory);
	public boolean canUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory);
	public boolean canDrop(ItemStack stack, Entity entity, EquipmentInventory inventory);
	public void onEquip(ItemStack stack, Entity entity, EquipmentInventory inventory);
	public void onUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory);
}
