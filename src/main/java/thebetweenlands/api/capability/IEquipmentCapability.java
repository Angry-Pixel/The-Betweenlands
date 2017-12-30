package thebetweenlands.api.capability;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;

public interface IEquipmentCapability {
	/**
	 * Returns the inventory for the specified equipment inventory.
	 * The inventory can be tickable if {@link ITickable} is implemented.
	 * Following requirements should be fulfilled:
	 * <p>
	 * <ul>
	 * <li>For any item that extends {@link IEquippable}:
	 * <ul>
	 * <li>If an item that extends {@link IEquippable}
	 * is added, {@link IEquippable#onEquip(ItemStack, Entity, IInventory)} should be invoked.
	 * </li>
	 * <li>
	 * If an item that extends {@link IEquippable}
	 * is removed, {@link IEquippable#onUnequip(ItemStack, Entity, IInventory)} should be invoked.
	 * </li>
	 * <li>
	 * {@link IEquippable#onEquipmentTick(ItemStack, Entity, IInventory)} should be invoked every tick
	 * for items in an equipment inventory.
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * {@link EquipmentHelper} provides some helper functions:
	 * {@link EquipmentHelper#equipItem(EntityPlayer, Entity, ItemStack)}, {@link EquipmentHelper#unequipItem(EntityPlayer, Entity)} and
	 * {@link EquipmentHelper#unequipItem(Entity, EnumEquipmentInventory, int)}
	 * @param inventory
	 * @return
	 */
	@Nonnull
	public IInventory getInventory(EnumEquipmentInventory inventory);

	/**
	 * Returns how many amulets can be equipped
	 * @return
	 */
	public int getAmuletSlots();

	/**
	 * Sets how many amulets can be equipped
	 * @param slots
	 */
	public void setAmuletSlots(int slots);
}
