package thebetweenlands.common.block.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import thebetweenlands.common.block.container.CompostBinBlock;
import thebetweenlands.common.block.entity.util.ItemHandlerProvidingBlockEntity;
import thebetweenlands.common.capability.CompostBinWrapper;
import thebetweenlands.common.capability.CompostContainer;
import thebetweenlands.common.capability.ICompostStorageHandler;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class CompostBinBlockEntity extends BlockEntity implements ItemHandlerProvidingBlockEntity, Clearable {

	public static final int COMPOST_PER_ITEM = 25;
	public static final int MAX_COMPOST_AMOUNT = COMPOST_PER_ITEM * 16;
	public static final int MAX_COMPOSTING_ITEMS = 20;

	public static final float MAX_OPEN = 90.0F;
	public static final float MIN_OPEN = 0.0F;
	public static final float OPEN_SPEED = 10.0F;
	public static final float CLOSE_SPEED = 10.0F;
	
	private final ICompostStorageHandler compostHandler;

	private boolean isLidOpen = false;
	private float lidAngle = 0.0F;

	public CompostBinBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.COMPOST_BIN.get(), pos, state);
		
		this.compostHandler = this.createCompostHandler();
		if(state.hasProperty(CompostBinBlock.OPEN)) {
			this.isLidOpen = state.getValue(CompostBinBlock.OPEN);
		}
	}
	
	/**
	 * Creates the compost handler
	 * 
	 * @return
	 */
	protected ICompostStorageHandler createCompostHandler() {
		return new CompostContainer(MAX_COMPOSTING_ITEMS, MAX_COMPOST_AMOUNT) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				
				// Don't mark dirty while loading chunk!
				if(CompostBinBlockEntity.this.hasLevel()) {
					CompostBinBlockEntity.this.setChanged();
				}
			}
			
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				// Do not allow insertions when lid is closed
				if(!isLidOpen()) {
					return stack;
				}
				return super.insertItem(slot, stack, simulate);
			}
			
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				// Do not allow extractions when lid is closed
				if(!isLidOpen()) {
					return ItemStack.EMPTY;
				}
				return super.extractItem(slot, amount, simulate);
			}
		};
	}

	public ICompostStorageHandler getCompostHandler() {
		return this.compostHandler;
	}
	
	public void setLidOpen(boolean isLidOpen) {
		this.isLidOpen = isLidOpen;
	}
	
	public boolean isLidOpen() {
		return this.isLidOpen;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, CompostBinBlockEntity entity) {
		boolean lidOpen = state.getValue(CompostBinBlock.OPEN);
		entity.lidAngle = lidOpen ? Math.min(entity.lidAngle + OPEN_SPEED, MAX_OPEN) : Math.max(entity.lidAngle - CLOSE_SPEED, MIN_OPEN);
		entity.setLidOpen(lidOpen);

		if (!level.isClientSide()) {
			if (!state.getValue(CompostBinBlock.OPEN)) {
				entity.tickComposting();
			}

			// Shift unfinished composting items into empty slots below them
			entity.tickNonComposting();
		}
	}

	public void tickComposting() {
		int compost = this.compostHandler.tickComposting(false);
		
		if(compost != 0) {
			this.setChanged();
		}
	}

	public void tickNonComposting() {
		this.compostHandler.tickNonComposting();
	}

	/**
	 * Drops all uncomposted items and all available compost
	 * @param level
	 * @param pos
	 */
	public void dropContents(Level level, BlockPos pos) {
		final double x = (double)pos.getX();
		final double y = (double)pos.getY();
		final double z = (double)pos.getZ();
		for(int i = 0; i < this.compostHandler.getSlots(); ++i) {
			Containers.dropItemStack(level, x, y, z, this.compostHandler.getStackInSlot(i));
		}
		Containers.dropItemStack(level, x, y, z, this.getCompostStack());
	}
	
	/**
	 * If this bin has no items that aren't finished composting
	 * @return
	 */
	public boolean isFinishedComposting() {
		for(int i = 0; i < this.compostHandler.getSlots(); ++i) {
			if(this.compostHandler.hasCompostItem(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes the specified amount of compost and returns true if successful
	 *
	 * @param amount
	 * @return
	 */
	public boolean removeCompost(int amount) {
		if(this.compostHandler.removeCompost(amount, true) >= amount) {
			this.compostHandler.removeCompost(amount, false);
			return true;
		}
		return false;
	}

	/**
	 * Tries to remove the specified amount of compost
	 * @param amount Amount to extract (may be greater than the total amount of compost)
	 * @param simulate If true, the extraction is only simulated
	 * @return Amount of compost extracted
	 */
	public int removeCompost(int amount, boolean simulate) {
		return this.compostHandler.removeCompost(amount, simulate);
	}
	
	/**
	 * Adds an item to the compost bin
	 *
	 * @param stack
	 * @param compostAmount
	 * @param compostTime
	 * @param simulate
	 * @return
	 */
	public CompostResult addItemToBin(ItemStack stack, boolean simulate) {
		if(!this.compostHandler.isValidCompost(stack)) {
			return CompostResult.NOT_COMPOSTABLE;
		}
		
		for (int i = 0; i < this.getMaxCompostItems(); i++) {
			ItemStack resultStack = this.compostHandler.insertItem(i, stack, true);
			
			if(resultStack.isEmpty()) {
				if(!simulate) {
					this.compostHandler.insertItem(i, stack, false);
				}
				return CompostResult.ADDED;
			}
		}
		
		return CompostResult.NOT_ADDED;
	}

	@Override
	public void clearContent() {
		this.compostHandler.clearContent();
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("compost", this.compostHandler.serializeNBT(registries));
		tag.putFloat("lid_angle", this.lidAngle);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("compost", Tag.TAG_COMPOUND)) {
			CompoundTag compostTag = tag.getCompound("compost");
			this.compostHandler.deserializeNBT(registries, compostTag);
		} else {
			this.compostHandler.clearContent();
		}
		this.lidAngle = tag.getFloat("lid_angle");
	}

	public ItemStack getCompostStack() {
		int compostedAmount = this.getCompostedAmount();
		if(compostedAmount < COMPOST_PER_ITEM) {
			return ItemStack.EMPTY;
		}
		return ItemRegistry.COMPOST.toStack(compostedAmount / COMPOST_PER_ITEM);
	}
	
	public ItemStack extractCompostStack(int count, boolean simulate) {
		final int compostAmount = this.getCompostedAmount();
		// If they try to extract nothing or we have no compost, return an empty item
		if(count <= 0 || compostAmount < COMPOST_PER_ITEM) {
			return ItemStack.EMPTY;
		}
		
		// Limit the upper bound on how much we can extract to avoid deleting compost
		// e.g. if we have 1.5 items worth of compost and we try to extract 2 items, this prevents us from deleting that 0.5 compost
		// It's not the most perfect solution, unfortunately
		count = Math.min(count, Math.floorDiv(compostAmount, COMPOST_PER_ITEM));
		
		// Figure out how much compost we should try to extract
		int toExtract = count * COMPOST_PER_ITEM;
		
		// Simulate extracting that compost
		int compostExtracted = this.removeCompost(toExtract, true);

		// If it's less than an item's worth, then return an empty item
		if(compostExtracted < COMPOST_PER_ITEM) {
			return ItemStack.EMPTY;
		}
		
		// Figure out how many items worth of compost actually got extracted
		int itemsExtracted = compostExtracted / COMPOST_PER_ITEM;
		
		// Actually remove the compost (if we're not simulated)
		if(!simulate) {
			this.removeCompost(toExtract, false);
		}

		// Return the extracted compost
		return ItemRegistry.COMPOST.toStack(itemsExtracted);
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
	 * Returns the maximum amount of items that can be composted at once
	 * 
	 * @return
	 */
	public int getMaxCompostItems() {
		return this.compostHandler.getSlots();
	}
	
	/**
	 * Returns the item being composted
	 * 
	 * @param slot the compost item slot
	 * @return the item being composted in slot {@code slot}
	 */
	public ItemStack getCompostItem(int slot) {
		return this.compostHandler.getStackInSlot(slot);
	}
	
	/**
	 * Returns the total compost at the end of the process
	 *
	 * @return
	 */
	public int getTotalCompostAmount() {
		return this.compostHandler.getTotalCompost();
	}

	/**
	 * Returns the current total amount of compost
	 *
	 * @return
	 */
	public int getCompostedAmount() {
		return this.compostHandler.getAvailableCompost();
	}
	
	/**
	 * Returns the maximum amount of compost that this bin can support at the end of the process
	 * 
	 * @return
	 */
	public int getMaximumCompostAmount() {
		return MAX_COMPOST_AMOUNT;
	}

	public enum CompostResult {
		ADDED,
		NOT_ADDED,
		NOT_COMPOSTABLE;
	}
	
	// Boring stuff

	@Override
	protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		// TODO
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		// TODO
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		// TODO
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}
	
	@Override
	public IItemHandler getItemHandlerCapability(Direction context) {
		if(context == Direction.DOWN) {
			return new CompostBinWrapper(this);
		} else {
			return this.compostHandler;
		}
	}
}
