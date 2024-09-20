package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.container.CompostBinBlock;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class CompostBinBlockEntity extends NoMenuContainerBlockEntity {

	public static final int COMPOST_PER_ITEM = 25;
	public static final int MAX_COMPOST_AMOUNT = COMPOST_PER_ITEM * 16;
	public static final int MAX_ITEMS = 20;

	public static final float MAX_OPEN = 90.0F;
	public static final float MIN_OPEN = 0.0F;
	public static final float OPEN_SPEED = 10.0F;
	public static final float CLOSE_SPEED = 10.0F;

	private int compostedAmount;
	private int totalCompostAmount;
	private float lidAngle = 0.0F;
	private int[] processes = new int[MAX_ITEMS];
	private int[] compostAmounts = new int[MAX_ITEMS];
	private int[] compostTimes = new int[MAX_ITEMS];

	private NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);

	public CompostBinBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.COMPOST_BIN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CompostBinBlockEntity entity) {
		entity.lidAngle = state.getValue(CompostBinBlock.OPEN) ? Math.min(entity.lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(entity.lidAngle - CLOSE_SPEED, MIN_OPEN);

		if (!level.isClientSide()) {
			if (!state.getValue(CompostBinBlock.OPEN)) {
				for (int i = 0; i < entity.getContainerSize(); i++) {
					if (!entity.getItem(i).isEmpty()) {
						if (entity.processes[i] >= entity.compostTimes[i]) {
							entity.compostedAmount += entity.compostAmounts[i];
							entity.setItem(i, ItemStack.EMPTY);
							entity.processes[i] = 0;
							entity.compostTimes[i] = 0;
							entity.compostAmounts[i] = 0;

							level.sendBlockUpdated(pos, state, state, 2);
						} else {
							entity.processes[i]++;
						}
					}
				}
			}

			// Fall down
			for (int i = 1; i < entity.getContainerSize(); i++) {
				if (entity.getItem(i - 1).isEmpty() && !entity.getItem(i).isEmpty()) {
					entity.setItem(i - 1, entity.getItem(i));
					entity.setItem(i, ItemStack.EMPTY);
					entity.processes[i - 1] = entity.processes[i];
					entity.processes[i] = 0;
					entity.compostAmounts[i - 1] = entity.compostAmounts[i];
					entity.compostAmounts[i] = 0;
					entity.compostTimes[i - 1] = entity.compostTimes[i];
					entity.compostTimes[i] = 0;
				}
			}
		}
	}

	/**
	 * Removes the specified amount of compost and returns true if successful
	 *
	 * @param amount
	 * @return
	 */
	public boolean removeCompost(Level level, BlockPos pos, BlockState state, int amount) {
		if (this.compostedAmount != 0) {
			if (this.compostedAmount >= amount) {
				this.compostedAmount -= amount;
				this.totalCompostAmount -= amount;
			} else {
				this.compostedAmount = 0;
				this.totalCompostAmount = 0;
			}
			level.sendBlockUpdated(pos, state, state, 2);
			this.setChanged();
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
	public CompostResult addItemToBin(Level level, BlockPos pos, BlockState state, ItemStack stack, int compostAmount, int compostTime, boolean doSimulate) {
		int clampedAmount = this.getTotalCompostAmount() + compostAmount <= MAX_COMPOST_AMOUNT ? compostAmount : MAX_COMPOST_AMOUNT - this.getTotalCompostAmount();
		if (clampedAmount > 0) {
			for (int i = 0; i < this.getContainerSize(); i++) {
				if (this.getItem(i).isEmpty()) {
					if (!doSimulate) {
						ItemStack copy = stack.copy();
						copy.setCount(1);
						this.setItem(i, copy);
						this.compostAmounts[i] = clampedAmount;
						this.compostTimes[i] = compostTime;
						this.processes[i] = 0;
						this.totalCompostAmount += clampedAmount;

						level.sendBlockUpdated(pos, state, state, 2);
					}
					return CompostResult.ADDED;
				}
			}
			return CompostResult.NOT_ADDED;
		}
		return CompostResult.FULL;
	}

	private boolean canAddItemToBin(int compostAmount, int index) {
		return this.getItem(index).isEmpty() && (this.getTotalCompostAmount() + compostAmount <= MAX_COMPOST_AMOUNT ? compostAmount : MAX_COMPOST_AMOUNT - this.getTotalCompostAmount()) > 0;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
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

	@Override
	public int getContainerSize() {
		return MAX_ITEMS;
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

	public enum CompostResult {
		ADDED,
		NOT_ADDED,
		FULL
	}
}
