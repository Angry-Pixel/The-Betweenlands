package thebetweenlands.common.capability.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;

import javax.annotation.Nullable;

public class EquipmentHelper {
	/**
	 * NBT containing whether the entity does *not* have any equipment.
	 * Using the inverse because old entities may not have this tag yet and so we can still do a quick check
	 * instead of having to do hasKey.
	 */
	public static final String NBT_HAS_NO_EQUIPMENT = "thebetweenlands.has_no_equipment";
	
	/**
	 * Returns the first item stack of the specified item, or empty if none is found
	 * @param entity
	 * @param item
	 * @return
	 */
	public static ItemStack getEquipment(EnumEquipmentInventory inventory, Entity entity, Item item) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			IInventory mainInv = cap.getInventory(inventory);
			for(int i = 0; i < mainInv.getSizeInventory(); i++) {
				ItemStack stack = mainInv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() == item) {
					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}
	
	/**
	 * Tries to equip the specified item, returns the leftover stack
	 * @param player
	 * @param target
	 * @param stack
	 * @return
	 */
	public static ItemStack equipItem(@Nullable EntityPlayer player, Entity target, ItemStack stack, boolean simulate) {
		if(stack.getItem() instanceof IEquippable) {
			IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				IEquippable equippable = (IEquippable) stack.getItem();

				if(equippable.canEquip(stack, player, target)) {
					EnumEquipmentInventory type = equippable.getEquipmentCategory(stack);

					stack = stack.copy();

					if (target instanceof EntityPlayerMP) {
						AdvancementCriterionRegistry.EQUIP.trigger((EntityPlayerMP) target, stack);
					}

					IInventory inv = cap.getInventory(type);
					IItemHandler wrapper = new InvWrapper(inv);

					ItemStack result = ItemHandlerHelper.insertItem(wrapper, stack, true);

					if(simulate) {
						return result;
					}

					if(result.isEmpty() || result.getCount() != stack.getCount()) {
						target.getEntityData().setBoolean(NBT_HAS_NO_EQUIPMENT, false);
						
						equippable.onEquip(stack, target, inv);
						
						return ItemHandlerHelper.insertItem(wrapper, stack, false);
					}
				}
			}
		}

		return stack;
	}

	/**
	 * Tries to equip the specified item in the specified slot, returns the leftover stack
	 * @param player
	 * @param target
	 * @param stack
	 * @param slot
	 * @param simulate
	 * @return
	 */
	public static ItemStack equipItem(@Nullable EntityPlayer player, Entity target, ItemStack stack, int slot, boolean simulate) {
		if(slot >= 0 && stack.getItem() instanceof IEquippable) {
			IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				IEquippable equippable = (IEquippable) stack.getItem();

				if(equippable.canEquip(stack, player, target)) {
					EnumEquipmentInventory type = equippable.getEquipmentCategory(stack);

					IInventory inv = cap.getInventory(type);
					if(slot < inv.getSizeInventory()) {
						IItemHandler wrapper = new InvWrapper(inv);

						stack = stack.copy();

						if (target instanceof EntityPlayerMP) {
							AdvancementCriterionRegistry.EQUIP.trigger((EntityPlayerMP) target, stack);
						}

						ItemStack result = wrapper.insertItem(slot, stack, simulate);

						if(simulate) {
							return result;
						}

						if(result.isEmpty() || result.getCount() != stack.getCount()) {
							target.getEntityData().setBoolean(NBT_HAS_NO_EQUIPMENT, false);
							
							equippable.onEquip(stack, target, inv);
							
							return ItemHandlerHelper.insertItem(wrapper, stack, false);
						}
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
	public static ItemStack unequipItem(@Nullable EntityPlayer player, Entity target, boolean simulate) {
		IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			for(EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
				IInventory inv = cap.getInventory(type);

				for(int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);

					if(!stack.isEmpty() && stack.getItem() instanceof IEquippable &&
							!((IEquippable) stack.getItem()).canUnequip(stack, player, target, cap.getInventory(((IEquippable) stack.getItem()).getEquipmentCategory(stack)))) {
						continue;
					}

					if(simulate) {
						return stack;
					}

					if(!stack.isEmpty()) {
						if(stack.getItem() instanceof IEquippable) {
							((IEquippable) stack.getItem()).onUnequip(stack, target, inv);
						}

						inv.setInventorySlotContents(i, ItemStack.EMPTY);
						return stack;
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Uneqips the item in the specified inventory and slot
	 * @param target
	 * @param type
	 * @param slot
	 * @return
	 */
	public static ItemStack unequipItem(@Nullable EntityPlayer player, Entity target, EnumEquipmentInventory type, int slot, boolean simulate) {
		IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			IInventory inv = cap.getInventory(type);

			if(slot >= 0 && slot < inv.getSizeInventory()) {
				ItemStack stack = inv.getStackInSlot(slot);

				if(!stack.isEmpty() && stack.getItem() instanceof IEquippable &&
						!((IEquippable) stack.getItem()).canUnequip(stack, player, target, cap.getInventory(((IEquippable) stack.getItem()).getEquipmentCategory(stack)))) {
					return stack;
				}
				
				if(simulate) {
					return stack;
				}

				if(!stack.isEmpty() && stack.getItem() instanceof IEquippable) {
					((IEquippable) stack.getItem()).onUnequip(stack, target, inv);
				}

				inv.setInventorySlotContents(slot, ItemStack.EMPTY);

				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Tries to unequip the first found item from the specified target
	 * @param player
	 * @param target
	 * @return True if successful
	 */
	public static boolean tryPlayerUnequip(EntityPlayer player, Entity target) {
		ItemStack unequipped = unequipItem(player, target, false);
		if(!unequipped.isEmpty()) {
			if(!player.inventory.addItemStackToInventory(unequipped)) {
				target.entityDropItem(unequipped, target.getEyeHeight());
			}

			return true;
		}
		return false;
	}
}
