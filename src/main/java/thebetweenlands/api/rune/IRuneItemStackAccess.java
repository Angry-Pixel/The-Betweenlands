package thebetweenlands.api.rune;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public interface IRuneItemStackAccess extends IInventory {
	/**
	 * Returns the inventory slot that this access refers to. May be null if unknown or if no such inventory or slot exists.
	 * Access must not happen through the delegated inventory and may throw an exception.
	 * @return
	 */
	@Nullable
	public Pair<IInventory, Integer> getDelegatedSlot();

	/**
	 * Returns whether this access is valid.
	 * If false none of the other operations have an effect.
	 * @return
	 */
	public boolean isAccessValid();

	/**
	 * Returns the {@link ItemStack}.
	 * If {@link #isAccessValid()} is false an empty {@link ItemStack} is returned.
	 * @return
	 */
	public ItemStack get();

	/**
	 * Sets the {@link ItemStack}.
	 * If {@link #isAccessValid()} or {@link #isItemValid(ItemStack)} is false this has no effect and false is returned.
	 * @param stack
	 * @return
	 */
	public boolean set(ItemStack stack);

	/**
	 * Removes the given count from the {@link ItemStack}.
	 * If {@link #isAccessValid()} is false this has no effect and an empty {@link ItemStack} is returned.
	 * @param count
	 * @return
	 */
	public default ItemStack remove(int count) {
		ItemStack decreased = this.get().copy();
		ItemStack result = decreased.splitStack(count);
		if(this.set(decreased)) {
			return result;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Returns whether the given {@link ItemStack} is valid for this access.
	 * @param stack
	 * @return
	 */
	public boolean isItemValid(ItemStack stack);

	@Override
	public default void clear() {
		this.set(ItemStack.EMPTY);
	}

	@Override
	public default void closeInventory(EntityPlayer player) { }

	@Override
	public default void openInventory(EntityPlayer player) { }

	@Override
	public default ItemStack decrStackSize(int index, int count) {
		if(index == 0) {
			return this.remove(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public default String getName() {
		return "Rune Item Stack Access";
	}

	@Override
	public default ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public default boolean hasCustomName() {
		return false;
	}

	@Override
	public default int getField(int id) {
		return 0;
	}

	@Override
	public default int getFieldCount() {
		return 0;
	}

	@Override
	public default void setField(int id, int value) { }

	@Override
	public default int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public default int getSizeInventory() {
		return 1;
	}

	@Override
	public default void setInventorySlotContents(int index, ItemStack stack) {
		if(index == 0) {
			this.set(stack);
		}
	}

	@Override
	public default ItemStack getStackInSlot(int index) {
		if(index == 0) {
			return this.get();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public default ItemStack removeStackFromSlot(int index) {
		ItemStack result = this.get();
		if(this.set(ItemStack.EMPTY)) {
			return result;
		}
		return null;
	}

	@Override
	public default boolean isEmpty() {
		return this.get().isEmpty();
	}

	@Override
	public default boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == 0) {
			return this.isItemValid(stack);
		}
		return false;
	}

	@Override
	public default boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public default void markDirty() { }
}
