package thebetweenlands.common.capability.equipment;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.item.equipment.IEquippable;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EquipmentHelper {
	/**
	 * Tries to equip the specified item, returns the leftover stack
	 * @param player
	 * @param target
	 * @param stack
	 * @return
	 */
	@Nullable
	public static ItemStack equipItem(@Nullable EntityPlayer player, Entity target, ItemStack stack, boolean simulate) {
		if(stack.getItem() instanceof IEquippable) {
			if(target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

				IEquippable equippable = (IEquippable) stack.getItem();

				if(equippable.canEquip(stack, player, target, cap.getInventory(equippable.getEquipmentCategory(stack)))) {
					EnumEquipmentInventory type = equippable.getEquipmentCategory(stack);

					stack = stack.copy();

					IInventory inv = cap.getInventory(type);
					IItemHandler wrapper = new InvWrapper(inv);

					ItemStack result = ItemHandlerHelper.insertItem(wrapper, stack, true);

					if(simulate) {
						return result;
					}

					if(result == null || result.stackSize != stack.stackSize) {
						equippable.onEquip(stack, target, inv);
						return ItemHandlerHelper.insertItem(wrapper, stack, false);
					}
				}
			}
		}

		return stack;
	}

	/**
	 * Tries to unequip the first item found
	 * @param player
	 * @param target
	 * @return
	 */
	@Nullable
	public static ItemStack unequipItem(@Nullable EntityPlayer player, Entity target, boolean simulate) {
		if(target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

			for(EnumEquipmentInventory type : EnumEquipmentInventory.values()) {
				IInventory inv = cap.getInventory(type);

				for(int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);

					if(stack != null && stack.getItem() instanceof IEquippable && 
							!((IEquippable) stack.getItem()).canUnequip(stack, player, target, cap.getInventory(((IEquippable) stack.getItem()).getEquipmentCategory(stack)))) {
						continue;
					}

					if(simulate) {
						return stack;
					}

					if(stack != null) {
						if(stack.getItem() instanceof IEquippable) {
							((IEquippable) stack.getItem()).onUnequip(stack, target, inv);
						}

						inv.setInventorySlotContents(i, null);
						return stack;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Uneqips the item in the specified inventory and slot
	 * @param target
	 * @param type
	 * @param slot
	 * @return
	 */
	@Nullable
	public static ItemStack unequipItem(Entity target, EnumEquipmentInventory type, int slot, boolean simulate) {
		if(target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory inv = cap.getInventory(type);

			if(slot >= 0 && slot < inv.getSizeInventory()) {
				ItemStack stack = inv.getStackInSlot(slot);

				if(simulate) {
					return stack;
				}
				
				if(stack.getItem() instanceof IEquippable) {
					((IEquippable) stack.getItem()).onUnequip(stack, target, inv);
				}
				
				inv.setInventorySlotContents(slot, null);

				return stack;
			}
		}

		return null;
	}

	/**
	 * Tries to unequip the first found item from the specified target
	 * @param player
	 * @param target
	 * @return True if successful
	 */
	public static boolean tryPlayerUnequip(EntityPlayer player, Entity target) {
		ItemStack unequipped = unequipItem(player, target, false);
		if(unequipped != null) {
			if(!player.inventory.addItemStackToInventory(unequipped)) {
				target.entityDropItem(unequipped, target.getEyeHeight());
			}

			return true;
		}
		return false;
	}
}
