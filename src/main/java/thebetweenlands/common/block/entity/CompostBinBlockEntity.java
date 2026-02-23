package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import thebetweenlands.common.block.container.CompostBinBlock;
import thebetweenlands.common.block.entity.util.SidedNoMenuContainerBlockEntity;
import thebetweenlands.common.capability.CompostBinWrapper;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CompostBinBlockEntity extends SidedNoMenuContainerBlockEntity {

	public static final int COMPOST_PER_ITEM = 25;
	public static final int MAX_COMPOST_AMOUNT = COMPOST_PER_ITEM * 16;
	public static final int MAX_COMPOSTING_ITEMS = 20;

	// Slot for compost items to go in
	public static final int MIN_COMPOSTING_SLOT = 0;
	public static final int MAX_COMPOSTING_SLOT = MAX_COMPOSTING_ITEMS - 1;
	public static final int COMPOST_SLOT = MAX_COMPOSTING_SLOT + 1;

	public static final float MAX_OPEN = 90.0F;
	public static final float MIN_OPEN = 0.0F;
	public static final float OPEN_SPEED = 10.0F;
	public static final float CLOSE_SPEED = 10.0F;

	private int compostedAmount;
	private int totalCompostAmount;
	private float lidAngle = 0.0F;
	private int[] processes = new int[MAX_COMPOSTING_ITEMS];
	private int[] compostAmounts = new int[MAX_COMPOSTING_ITEMS];
	private int[] compostTimes = new int[MAX_COMPOSTING_ITEMS];

	private NonNullList<ItemStack> items = NonNullList.withSize(MAX_COMPOSTING_ITEMS, ItemStack.EMPTY);

	public CompostBinBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.COMPOST_BIN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CompostBinBlockEntity entity) {
		entity.lidAngle = state.getValue(CompostBinBlock.OPEN) ? Math.min(entity.lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(entity.lidAngle - CLOSE_SPEED, MIN_OPEN);

		if (!level.isClientSide()) {
			if (!state.getValue(CompostBinBlock.OPEN)) {
				entity.tickComposting();
			}

			// Shift unfinished composting items into empty slots below them
			entity.compostingItemsFallDown();
		}
	}
	
	public void tickComposting() {
		boolean changed = false;
		for (int i = 0; i < this.getCompostingContainerSize(); i++) {
			if (!this.getCompostingItem(i).isEmpty()) {
				if (this.processes[i] >= this.compostTimes[i]) {
					this.compostedAmount += this.compostAmounts[i];
					this.setCompostingItem(i, ItemStack.EMPTY);
					this.processes[i] = 0;
					this.compostTimes[i] = 0;
					this.compostAmounts[i] = 0;
				} else {
					this.processes[i]++;
				}
				changed = true;
			}
		}
		
		if(changed) {
			this.setChanged();
		}
	}

	/**
	 * Removes the specified amount of compost and returns true if successful
	 *
	 * @param amount
	 * @return
	 */
	public boolean removeCompost(int amount) {
		if (this.compostedAmount != 0) {
			if (this.compostedAmount >= amount) {
				this.compostedAmount -= amount;
				this.totalCompostAmount -= amount;
			} else {
				this.compostedAmount = 0;
				this.totalCompostAmount = 0;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Adds an item to the compost bin
	 *
	 * @param stack
	 * @param compostAmount
	 * @param compostTime
	 * @param doSimulate
	 * @return
	 */
	public CompostResult addItemToBin(ItemStack stack, int compostAmount, int compostTime, boolean doSimulate) {
		int clampedAmount = this.getClampedCompostAmount(compostAmount);
		if (clampedAmount > 0) {
			for (int i = 0; i < this.getCompostingContainerSize(); i++) {
				if (this.getCompostingItem(i).isEmpty()) {
					if (!doSimulate) {
						ItemStack copy = stack.copyWithCount(1);
						this.setCompostingItem(i, copy);
						this.compostAmounts[i] = clampedAmount;
						this.compostTimes[i] = compostTime;
						this.processes[i] = 0;
						this.totalCompostAmount += clampedAmount;

						this.setChanged();
					}
					return CompostResult.ADDED;
				}
			}
			return CompostResult.NOT_ADDED;
		}
		return CompostResult.FULL;
	}

	public int getClampedCompostAmount(int compostAmount) {
		return this.getTotalCompostAmount() + compostAmount <= this.getMaximimumCompostAmount() ? compostAmount : this.getMaximimumCompostAmount() - this.getTotalCompostAmount();
	}
	
	public boolean canAddCompostToBin(int compostAmount) {
		return this.getClampedCompostAmount(compostAmount) > 0;
	}

	public boolean canAddItemToBin(int compostAmount, int index) {
		return this.getItem(index).isEmpty() && this.canAddCompostToBin(compostAmount);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putIntArray("processes", this.processes);
		tag.putIntArray("compost_amounts", this.compostAmounts);
		tag.putIntArray("compost_times", this.compostTimes);
		tag.putInt("total_compost_amount", this.totalCompostAmount);
		tag.putInt("composted_amount", this.compostedAmount);
		tag.putFloat("lid_angle", this.lidAngle);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items.clear();
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.processes = readIntArrayFixedSize("processes", this.items.size(), tag);
		this.compostAmounts = readIntArrayFixedSize("compost_amounts", this.items.size(), tag);
		this.compostTimes = readIntArrayFixedSize("compost_times", this.items.size(), tag);
		this.totalCompostAmount = tag.getInt("total_compost_amount");
		this.compostedAmount = tag.getInt("composted_amount");
		this.lidAngle = tag.getFloat("lid_angle");
	}

	public static int[] readIntArrayFixedSize(String id, int length, CompoundTag tag) {
		int[] array = tag.getIntArray(id);
		return array.length != length ? new int[length] : array;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	public boolean isCompostingSlot(int slot) {
		return MIN_COMPOSTING_SLOT <= slot && slot <= MAX_COMPOSTING_SLOT;
	}
	
	public void compostingItemsFallDown() {
		boolean changed = false;
		
		// Fall down
		for (int i = 1; i < this.getCompostingContainerSize(); i++) {
			if (this.getCompostingItem(i - 1).isEmpty() && !this.getCompostingItem(i).isEmpty()) {
				this.setCompostingItem(i - 1, this.getCompostingItem(i));
				this.setCompostingItem(i, ItemStack.EMPTY);
				this.processes[i - 1] = this.processes[i];
				this.processes[i] = 0;
				this.compostAmounts[i - 1] = this.compostAmounts[i];
				this.compostAmounts[i] = 0;
				this.compostTimes[i - 1] = this.compostTimes[i];
				this.compostTimes[i] = 0;
				changed = true;
			}
		}
		
		if(changed) {
			this.setChanged();
		}
	}
	
	public int getCompostingMaxStackSize() {
		return 1;
	}
	
	public int getCompostingMaxStackSize(ItemStack stack) {
		return Math.min(this.getCompostingMaxStackSize(), stack.getMaxStackSize());
	}
	
	public ItemStack getCompostingItem(int compostingIndex) {
		this.unpackLootTable(null);
		return this.items.get(compostingIndex);
	}
	
	public void setCompostingItem(int compostingIndex, ItemStack stack) {
		this.unpackLootTable(null);
		this.items.set(compostingIndex, stack);
		stack.limitSize(this.getCompostingMaxStackSize(stack));
		this.setChanged();
	}
	
	public int getCompostingContainerSize() {
		return MAX_COMPOSTING_ITEMS;
	}
	
	@Override
	public int getContainerSize() {
		return this.getCompostingContainerSize() + 1; // Compost slot
	}
	
	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public ItemStack getCompostStack() {
		int compostedAmount = this.getCompostedAmount();
		if(compostedAmount < COMPOST_PER_ITEM) {
			return ItemStack.EMPTY;
		}
		return ItemRegistry.COMPOST.toStack(compostedAmount / COMPOST_PER_ITEM);
	}
	
	public ItemStack removeCompostStack(int count, boolean simulate) {
		ItemStack compostStack = this.getCompostStack().copy();

		ItemStack removedCompost = compostStack.split(count);
		
		if(!simulate && !removedCompost.isEmpty()) {
			this.removeCompost(removedCompost.getCount() * COMPOST_PER_ITEM);
		}
		
		return removedCompost;
	}
	
	@Override
	public ItemStack getItem(int index) {
		if(index == COMPOST_SLOT) {
			return this.getCompostStack();
		}
		return super.getItem(index);
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		if(this.isCompostingSlot(index)) {
			int i = index;
			if(!stack.isEmpty() && this.compostAmounts[i] == 0) {
				CompostableItem compostData = stack.getItemHolder().getData(DataMapRegistry.COMPOSTABLE);
				
				// Can only insert compostable items
				if (compostData != null) {
					int compostAmount = compostData.amount();
					int compostTime = compostData.time();

					if(this.canAddItemToBin(compostAmount, index)) {
						int clampedAmount = this.getClampedCompostAmount(compostAmount);
						
						ItemStack copy = stack.copyWithCount(1);
						this.setCompostingItem(i, copy);
						this.compostAmounts[i] = clampedAmount;
						this.compostTimes[i] = compostTime;
						this.processes[i] = 0;
						this.totalCompostAmount += clampedAmount;

						this.setChanged();
						return;
					}
				}
			}
		}
		
		if(index == COMPOST_SLOT) {
			return;
		}
		
		super.setItem(index, stack);
	}
	
	@Override
	public ItemStack removeItem(int index, int count) {
		if(index == COMPOST_SLOT) {
			ItemStack removedCompost = this.removeCompostStack(count, false);
			if(!removedCompost.isEmpty()) {
				this.setChanged();
			}
			return removedCompost;
		}
		return super.removeItem(index, count);
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if(index == COMPOST_SLOT) {
			ItemStack removedCompost = this.getCompostStack();
			if(!removedCompost.isEmpty()) {
				this.removeCompost(removedCompost.getCount() * COMPOST_PER_ITEM);
			}
			return removedCompost;
		}
		return super.removeItemNoUpdate(index);
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		// Can only add items when the lid is open
		if(this.hasLevel()) {
			boolean isLidOpen = this.getLevel().getBlockState(this.getBlockPos()).getValue(CompostBinBlock.OPEN);
			if(!isLidOpen) {
				return false;
			}
		}
		
		// Can only insert to composting slots
		if(this.isCompostingSlot(slot)) {
			// Cannot insert into slot that is currently processing
			// Cannot insert empty stack
			if(!this.getItem(slot).isEmpty() || stack.isEmpty()) {
				return false;
			}
			
			CompostableItem compostData = stack.getItemHolder().getData(DataMapRegistry.COMPOSTABLE);
			
			// Can only insert compostable items
			if (compostData == null) {
				return false;
			}
			
			int compostAmount = compostData.amount();

			return this.canAddItemToBin(compostAmount, slot);
		}
		
		return false;
	}

	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		// Cannot take items when the lid is closed
		if(this.hasLevel()) {
			boolean isLidOpen = this.getLevel().getBlockState(this.getBlockPos()).getValue(CompostBinBlock.OPEN);
			if(!isLidOpen) {
				return false;
			}
		}
		
		// Only allow extracting items that shouldn't have been inserted
		if(this.isCompostingSlot(slot)) {
			return this.compostAmounts[slot] <= 0;
		}
		
		// Allow taking from the compost slot
		if(slot == COMPOST_SLOT) {
			return true;
		}
		
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if(side == Direction.DOWN) {
			return new int[] { COMPOST_SLOT };
		}
		return slotsBetweenInclusive(MIN_COMPOSTING_SLOT, MAX_COMPOSTING_SLOT);
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}
	
	/**
	 * Returns the lid angle
	 *
	 * @param partialTicks
	 * @return
	 */
	public float getLidAngle(BlockState state, float partialTicks) {
		return state.getValue(CompostBinBlock.OPEN) ? Math.min(this.lidAngle + OPEN_SPEED * partialTicks, MAX_OPEN) : Math.max(this.lidAngle - CLOSE_SPEED * partialTicks, MIN_OPEN);
	}

	/**
	 * Returns the total compost at the end of the process
	 *
	 * @return
	 */
	public int getTotalCompostAmount() {
		return this.totalCompostAmount;
	}

	/**
	 * Returns the current total amount of compost
	 *
	 * @return
	 */
	public int getCompostedAmount() {
		return this.compostedAmount;
	}
	
	/**
	 * Returns the maximum amount of compost that this bin can support at the end of the process
	 * 
	 * @return
	 */
	public int getMaximimumCompostAmount() {
		return MAX_COMPOST_AMOUNT;
	}

	public enum CompostResult {
		ADDED,
		NOT_ADDED,
		FULL
	}
	
	@Override
	public IItemHandler getItemHandlerCapability(Direction context) {
		return new CompostBinWrapper(this, context);
	}
}
