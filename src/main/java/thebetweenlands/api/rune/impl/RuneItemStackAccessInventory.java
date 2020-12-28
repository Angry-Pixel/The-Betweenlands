package thebetweenlands.api.rune.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thebetweenlands.api.rune.IRuneItemStackAccess;

public class RuneItemStackAccessInventory implements IInventory {
	private final IRuneItemStackAccess access;

	public RuneItemStackAccessInventory(IRuneItemStackAccess access) {
		this.access = access;
	}

	@Override
	public void clear() {
		this.access.set(ItemStack.EMPTY);
	}

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index == 0) {
			return this.access.remove(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public String getName() {
		return "Rune Item Stack Access";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if(index == 0) {
			this.access.set(stack);
		}
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(index == 0) {
			return this.access.get();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index == 0) {
			ItemStack result = this.access.get();

			if(this.access.set(ItemStack.EMPTY)) {
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return this.access.get().isEmpty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0) {
			return this.access.isItemValid(stack);
		}
		return false;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void markDirty() { }
}
