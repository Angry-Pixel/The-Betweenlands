package thebetweenlands.common.capability;

import java.util.Arrays;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.registries.DataMapRegistry;

public class CompostHandler implements ICompostHandler, ICompostHandlerModifiable, INBTSerializable<CompoundTag>, Clearable {
	public static final int STACK_CHANGED = 1;
	public static final int PROGRESS_CHANGED = 2;
	public static final int DURATION_CHANGED = 4;
	public static final int AMOUNT_CHANGED = 8;
	public static final int ALL_CHANGED = STACK_CHANGED | PROGRESS_CHANGED | DURATION_CHANGED | AMOUNT_CHANGED;
	
	private final int size;
	private final NonNullList<ItemStack> compostingItems;
	private final int[] compostProgress;
	private final int[] compostDuration;
	private final int[] compostAmount;
	
	public CompostHandler(int size) {
		this.size = size;
		this.compostingItems = NonNullList.withSize(size, ItemStack.EMPTY);
		this.compostProgress = new int[size];
		this.compostDuration = new int[size];
		this.compostAmount = new int[size];
	}

	protected int getSize() {
		return this.size;
	}
	
	protected NonNullList<ItemStack> getItems() {
		return this.compostingItems;
	}
	
	protected int[] getProgressArray() {
		return this.compostProgress;
	}
	
	protected int[] getDurationArray() {
		return this.compostDuration;
	}
	
	protected int[] getAmountArray() {
		return this.compostAmount;
	}

	@Override
	public int tickComposting(boolean simulate) {
		int compostedAmount = 0;
		
		final int size = this.size;
		for (int i = 0; i < size; i++) {
			if (this.hasCompostItem(i)) {
				if (this.compostProgress[i] >= this.compostDuration[i]) {
					compostedAmount += this.compostAmount[i];
					
					if(!simulate) {
						this.compostingItems.set(i, ItemStack.EMPTY);
						this.compostProgress[i] = 0;
						this.compostDuration[i] = 0;
						this.compostAmount[i] = 0;
						
						this.onCompostChanged(i, ALL_CHANGED);
					}
				} else if(!simulate) {
					this.compostProgress[i]++;
					
					this.onCompostChanged(i, PROGRESS_CHANGED);
				}
			}
		}
		
		return compostedAmount;
	}
	
	@Override
	public void tickNonComposting() {
		// Items fall down
		final int size = this.size;
		for (int i = 1; i < size; i++) {
			if (!this.hasCompostItem(i - 1) && this.hasCompostItem(i)) {
				this.compostingItems.set(i - 1, this.compostingItems.get(i));
				this.compostingItems.set(i, ItemStack.EMPTY);
				this.compostProgress[i - 1] = this.compostProgress[i];
				this.compostProgress[i] = 0;
				this.compostDuration[i - 1] = this.compostDuration[i];
				this.compostDuration[i] = 0;
				this.compostAmount[i - 1] = this.compostAmount[i];
				this.compostAmount[i] = 0;

				this.onCompostChanged(i - 1, ALL_CHANGED);
				this.onCompostChanged(i, ALL_CHANGED);
			}
		}
	}

	@Override
	public boolean hasCompostItem(int slot) {
		this.validateSlotIndex(slot);
		return !this.getStackInSlot(slot).isEmpty();
	}

	@Override
	public int getCompostProgress(int slot) {
		this.validateSlotIndex(slot);
		if(!this.hasCompostItem(slot)) return -1;
		return this.compostProgress[slot];
	}

	@Override
	public int getTotalCompostProgress(int slot) {
		this.validateSlotIndex(slot);
		if(!this.hasCompostItem(slot)) return -1;
		return this.compostDuration[slot];
	}

	@Override
	public int getCompostAmount(int slot) {
		this.validateSlotIndex(slot);
		if(!this.hasCompostItem(slot)) return -1;
		return this.compostAmount[slot];
	}
	
	public CompostableItem getCompostData(ItemStack stack) {
		return stack.getItemHolder().getData(DataMapRegistry.COMPOSTABLE);
	}

	@Override
	public void clearContent() {
		this.compostingItems.clear();
		Arrays.fill(this.compostProgress, 0);
		Arrays.fill(this.compostDuration, 0);
		Arrays.fill(this.compostAmount, 0);
	}
	
	@Override
	public boolean isValidCompost(ItemStack stack) {
		if(stack.isEmpty()) {
			return false;
		}
		
		CompostableItem compostData = this.getCompostData(stack);
		
		// Can only insert compostable items
		if (compostData == null) {
			return false;
		}
		
		return compostData.amount() > 0;
	}

	@Override
	public int getSlots() {
		return this.size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		this.validateSlotIndex(slot);
		return this.compostingItems.get(slot);
	}

	@Override
	public void setCompostInSlot(int slot, ItemStack stack, int compostAmount, int compostProgress, int compostDuration) {
		this.compostingItems.set(slot, stack);
		this.compostProgress[slot] = compostProgress;
		this.compostDuration[slot] = compostDuration; 
		this.compostAmount[slot] = compostAmount;
		
		this.onCompostChanged(slot, ALL_CHANGED);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// If they insert nothing, do nothing
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		
		// If the item doesn't fit, don't insert it
		if(!this.isItemValid(slot, stack) || this.hasCompostItem(slot)) {
			return stack;
		}

		this.validateSlotIndex(slot);
		
		// Get compost data of item
		CompostableItem compostData = this.getCompostData(stack);
		
		// Can only insert compostable items
		if (compostData == null) {
			return stack;
		}
		
		// Only insert items one at a time
		ItemStack copyStack = stack.copy();
		ItemStack compostStack = copyStack.split(1);
		
		if(!simulate) {
			int compostAmount = compostData.amount();
			int compostDuration = compostData.time();
			
			this.compostingItems.set(slot, compostStack);
			this.compostProgress[slot] = 0;
			this.compostDuration[slot] = compostDuration;
			this.compostAmount[slot] = compostAmount;
			
			this.onCompostChanged(slot, ALL_CHANGED);
		}
		
		return copyStack.isEmpty() ? ItemStack.EMPTY : copyStack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		// If they're taking nothing, do nothing
		if(amount == 0) {
			return ItemStack.EMPTY;
		}
		
		this.validateSlotIndex(slot);
		
		// Get the stack in that slot
		ItemStack existingStack = this.compostingItems.get(slot);
		
		// If there's nothing in this slot, then don't do anything
		if(existingStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		
		// Figure out how much we're allowed to extract
		int extractedAmount = Math.min(amount, existingStack.getMaxStackSize());
		
		// Extract the whole stack
		if(existingStack.getCount() < extractedAmount) {
			if(!simulate) {
				this.compostingItems.set(slot, ItemStack.EMPTY);
				this.compostProgress[slot] = 0;
				this.compostDuration[slot] = 0;
				this.compostAmount[slot] = 0;
				this.onCompostChanged(slot, ALL_CHANGED);
				return existingStack;
			}
			return existingStack.copy();
		} else {
			// Not extracting the whole stack
			
			if(!simulate) {
				// newStack shouldn't be empty
				ItemStack newStack = existingStack.copyWithCount(existingStack.getCount() - extractedAmount);
				this.compostingItems.set(slot, newStack);
				// TODO it may be possible that the CompostableItem has changed
				this.onContentsChanged(slot);
			}
			
			return existingStack.copyWithCount(extractedAmount);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(Provider provider) {
		CompoundTag tag = new CompoundTag(4);
		ContainerHelper.saveAllItems(tag, this.compostingItems, provider);
		tag.putIntArray("compost_progress", this.compostProgress);
		tag.putIntArray("compost_duration", this.compostDuration);
		tag.putIntArray("compost_amount", this.compostAmount);
		return tag;
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag tag) {
		this.compostingItems.clear();
		ContainerHelper.loadAllItems(tag, this.compostingItems, provider);
		readIntArrayInto(tag, "compost_progress", this.compostProgress);
		readIntArrayInto(tag, "compost_duration", this.compostDuration);
		readIntArrayInto(tag, "compost_amount", this.compostAmount);
		this.onLoad();
	}
	
	public static void readIntArrayInto(CompoundTag tag, String key, int[] array) {
		int[] storedArray = tag.getIntArray(key);
		if(storedArray == null || storedArray.length != array.length) {
			Arrays.fill(array, 0);
			return;
		}
		System.arraycopy(storedArray, 0, array, 0, array.length);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		this.validateSlotIndex(slot);
		this.compostingItems.set(slot, stack);
		this.onContentsChanged(slot);
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.size) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size + ")");
		}
	}
	
	protected void onLoad() {}

	protected void onContentsChanged(int slot) {}

	protected void onCompostChanged(int slot, int flags) {
		if((flags & STACK_CHANGED) != 0) {
			this.onContentsChanged(slot);
		}
	}

}
