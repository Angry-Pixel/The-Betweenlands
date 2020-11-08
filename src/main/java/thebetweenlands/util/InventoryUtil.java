package thebetweenlands.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtil {
	public static void addItemToInventory(IInventory inv, ItemStack stack) {
		if(stack.isItemDamaged()) {
			int index = getFirstEmptyStack(inv);

			if(index >= 0) {
				inv.setInventorySlotContents(index, stack.copy());
				inv.getStackInSlot(index).setAnimationsToGo(5);
				stack.setCount(0);
			}
		} else {
			while(true) {
				int count = stack.getCount();

				stack.setCount(storePartialItemStack(inv, stack));

				if(stack.isEmpty() || stack.getCount() >= count) {
					break;
				}
			}
		}
	}

	private static int getFirstEmptyStack(IInventory inv) {
		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			if(inv.getStackInSlot(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	private static int storePartialItemStack(IInventory inv, ItemStack stack) {
		int index = storeItemStack(inv, stack);

		if(index == -1) {
			index = getFirstEmptyStack(inv);
		}

		return index == -1 ? stack.getCount() : addResource(inv, index, stack);
	}

	private static int storeItemStack(IInventory inv, ItemStack stack) {
		for(int i = 0; i < inv.getSizeInventory(); ++i) {
			if(canMergeStacks(inv, inv.getStackInSlot(i), stack)) {
				return i;
			}
		}

		return -1;
	}

	private static boolean canMergeStacks(IInventory inv, ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < inv.getInventoryStackLimit();
	}

	private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	private static int addResource(IInventory inv, int index, ItemStack stack) {
		int count = stack.getCount();
		ItemStack invStack = inv.getStackInSlot(index);

		if(invStack.isEmpty()) {
			invStack = stack.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
			invStack.setCount(0);

			if(stack.hasTagCompound()) {
				invStack.setTagCompound(stack.getTagCompound().copy());
			}

			inv.setInventorySlotContents(index, invStack);
		}

		int add = count;

		if(count > invStack.getMaxStackSize() - invStack.getCount()) {
			add = invStack.getMaxStackSize() - invStack.getCount();
		}

		if(add > inv.getInventoryStackLimit() - invStack.getCount()) {
			add = inv.getInventoryStackLimit() - invStack.getCount();
		}

		if(add == 0) {
			return count;
		} else {
			count = count - add;
			invStack.grow(add);
			invStack.setAnimationsToGo(5);
			return count;
		}
	}
}
