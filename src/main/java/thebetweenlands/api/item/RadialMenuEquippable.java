package thebetweenlands.api.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;

import javax.annotation.Nullable;

public interface RadialMenuEquippable {

	/**
	 * Returns the equipment category
	 * @param stack Item
	 * @return
	 */
	EquipmentInventoryType getEquipmentCategory(ItemStack stack);

	/**
	 * Returns whether this item should be equipped on right click
	 * @param stack Item to equip
	 * @param player Player that is interacting
	 * @param target Entity that will be equipped
	 * @return
	 */
	boolean canEquipOnRightClick(ItemStack stack, Player player, Entity target);

	/**
	 * Returns whether this item can be equipped
	 * @param stack Item to equip
	 * @param player Player that is interacting
	 * @param target Entity that will be equipped
	 * @return
	 */
	boolean canEquip(ItemStack stack, @Nullable Player player, Entity target);

	/**
	 * Returns whether this item can be unequipped by a player
	 * @param stack Item to unequip
	 * @param player Player that is interacting
	 * @param target Entity that will be unequipped
	 * @param inventory Equipment inventory
	 * @return
	 */
	boolean canUnequip(ItemStack stack, @Nullable Player player, Entity target, Container inventory);

	/**
	 * Returns whether this item can drop on death
	 * @param stack Equipped item
	 * @param entity Entity that died
	 * @param inventory Equipment inventory
	 * @return
	 */
	boolean canDrop(ItemStack stack, Entity entity, Container inventory);

	/**
	 * Called when an item is equipped
	 * @param stack Equipped item
	 * @param entity Entity that is being equipped
	 * @param inventory Equipment inventory
	 */
	void onEquip(ItemStack stack, Entity entity, Container inventory);

	/**
	 * Called when an item is unequipped
	 * @param stack Equipped item
	 * @param entity Entity that is being unequipped
	 * @param inventory Equipment inventory
	 */
	void onUnequip(ItemStack stack, Entity entity, Container inventory);

	/**
	 * Called when the equipment is ticked
	 * @param stack
	 * @param entity
	 */
	void onEquipmentTick(ItemStack stack, Entity entity, Container inventory);
}
