package thebetweenlands.common.capability.equipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.item.equipment.IEquippable;
import thebetweenlands.common.registries.CapabilityRegistry;

public interface IEquipmentCapability {
	/**
	 * Returns the inventory for the specified equipment inventory.
	 * The inventory can be tickable if {@link ITickable} is implemented.
	 * @param inventory
	 * @return
	 */
	@Nonnull
	public IInventory getInventory(EnumEquipmentInventory inventory);

	/**
	 * Tries to equip the specified item, returns the inserted stack
	 * @param player
	 * @param target
	 * @param stack
	 * @return
	 */
	@Nullable
	public static ItemStack equipItem(EntityPlayer player, Entity target, ItemStack stack) {
		if(stack.getItem() instanceof IEquippable) {
			if(target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

				IEquippable equippable = (IEquippable) stack.getItem();

				if(equippable.canEquip(stack, player, target, cap.getInventory(equippable.getEquipmentCategory(stack)))) {
					EnumEquipmentInventory type = equippable.getEquipmentCategory(stack);

					stack = stack.copy();

					IInventory inv = cap.getInventory(type);
					IItemHandler wrapper = new InvWrapper(inv);

					ItemStack insertable = ItemHandlerHelper.insertItem(wrapper, stack, true);

					if(insertable == null || insertable.stackSize != stack.stackSize) {
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
	public static ItemStack unequipItem(EntityPlayer player, Entity target) {
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
}
