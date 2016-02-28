package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;

public interface IEquippable {
	/**
	 * Returns the equipment category
	 * @param stack Item
	 * @return
	 */
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack);

	/**
	 * Returns whether this item can be equipped
	 * @param stack Item to equip
	 * @param player Player that is interacting
	 * @param entity Entity that will be equipped with this item
	 * @param inventory Equipment inventory
	 * @return
	 */
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory);

	/**
	 * Returns whether this item can be unequipped by a player
	 * @param stack Item to unequip
	 * @param player Player that is interacting
	 * @param entity Entity that will be unequipped
	 * @param inventory Equipment inventory
	 * @return
	 */
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory);

	/**
	 * Returns whether this item can drop on death
	 * @param stack Equipped item
	 * @param entity Entity that died
	 * @param inventory Equipment inventory
	 * @return
	 */
	public boolean canDrop(ItemStack stack, Entity entity, EquipmentInventory inventory);

	/**
	 * Called when an item is equipped
	 * @param stack Equipped item
	 * @param entity Entity that is being equipped
	 * @param inventory Equipment inventory
	 */
	public void onEquip(ItemStack stack, Entity entity, EquipmentInventory inventory);

	/**
	 * Called when an item is unequipped
	 * @param stack Equipped item
	 * @param entity Entity that is being unequipped
	 * @param inventory Equipment inventory
	 */
	public void onUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory);
}
