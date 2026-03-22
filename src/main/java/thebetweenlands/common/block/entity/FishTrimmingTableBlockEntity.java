package thebetweenlands.common.block.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.recipes.TrimmingTableRecipe;
import thebetweenlands.common.inventory.FishTrimmingTableMenu;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.util.BooleanContainerData;

public class FishTrimmingTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	public static final int FISH_SLOT = 0;
	public static final int OUTPUT_SLOT_1 = 1;
	public static final int OUTPUT_SLOT_2 = 2;
	public static final int OUTPUT_SLOT_3 = 3;
	public static final int CHOPPER_SLOT = 4;
	public static final int SLOT_COUNT = 5;

	public static final int FIELD_CHOPPER_VALID = 0;
	public static final int FIELD_HAS_RECIPE = 1;
	public static final int FIELD_CAN_CHOP = 2;
	public static final int DATA_FIELD_COUNT = 3;

	private static final int[] SLOTS_FOR_SIDES = new int[] {FISH_SLOT, CHOPPER_SLOT};
	
	private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
	
	private ItemStack remainsItem = ItemStack.EMPTY;
	// We need to track how many remains we have left even after clearing the output slots
	private int remainsCount = 0;
	
	/**
	 * Current trimming table recipe
	 */
	@Nullable
	protected TrimmingTableRecipe recipe;
	/**
	 * Does the triming table recipe need to be updated?
	 */
	protected boolean recipeDirty = false;
	
	// Yummy menu fields for the client
	protected final BooleanContainerData containerData = new BooleanContainerData() {
		private boolean isChopperValid = false;
		private boolean hasRecipe = false;
		private boolean canChop = false;

		@Override
		public boolean getBoolean(int index) {
			return switch(index) {
				case FIELD_CHOPPER_VALID -> this.isChopperValid;
				case FIELD_HAS_RECIPE -> this.hasRecipe;
				case FIELD_CAN_CHOP -> this.canChop;
				default -> false;
			};
		}
		
		@Override
		public void set(int index, boolean value) {
			switch(index) {
				case FIELD_CHOPPER_VALID -> this.isChopperValid = value;
				case FIELD_HAS_RECIPE -> this.hasRecipe = value;
				case FIELD_CAN_CHOP -> this.canChop = value;
			}
		}
		
		@Override
		public int getCount() {
			return DATA_FIELD_COUNT;
		}
	};
	
	public final RecipeManager.CachedCheck<SingleRecipeInput, TrimmingTableRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.TRIMMING_TABLE_RECIPE.get());
	
	public FishTrimmingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.FISH_TRIMMING_TABLE.get(), pos, state);
	}

	@Nullable
	public TrimmingTableRecipe getStoredRecipe() {
		return this.recipe;
	}

	public boolean isChopper(ItemStack stack) {
		return stack.is(ItemRegistry.BONE_AXE);
	}

	public boolean hasChopper() {
		return this.isChopper(this.getItem(CHOPPER_SLOT));
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.fish_trimming_table");
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
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new FishTrimmingTableMenu(containerId, inventory, this, this.containerData);
	}

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		if (slot == FISH_SLOT) {
			// Defer updating the recipe until setChanged()
			// This is for the purposes of getting output items:
			//     If the fish slot gets replaced (see getSlotResult(Level, int, int)) before the output slots do,
			//         the recipe won't be set to null immediately and the output slots will still get their items
			this.recipeDirty = true;
		}
		if(slot == CHOPPER_SLOT) {
			this.updateChopperValid();
			this.updateCanChop();
		}
	}
	
	@Override
	public void setChanged() {
		if(this.recipeDirty) {
			this.updateRecipe();
		}
		super.setChanged();
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		// Ensure recipe is loaded
		this.updateRecipe();
	}
	
	/**
	 * Updates the {@code recipe} if the world is loaded
	 */
	public void updateRecipe() {
		if(!this.hasLevel()) {
			return;
		}
		
		ItemStack fishStack = this.getItem(FISH_SLOT);
		if(!fishStack.isEmpty()) {
			Level level = this.getLevel();
			SingleRecipeInput recipeInput = new SingleRecipeInput(fishStack);
			this.recipe = this.quickCheck.getRecipeFor(recipeInput, level).map(RecipeHolder::value).orElse(null);
		} else {
			this.recipe = null;
		}
		
		this.recipeDirty = false;
		
		this.containerData.set(FIELD_HAS_RECIPE, this.recipe != null);
		this.updateCanChop();
	}
	
	public void updateChopperValid() {
		this.containerData.set(FIELD_CHOPPER_VALID, this.hasChopper());
	}
	
	public void updateCanChop() {
		this.containerData.set(FIELD_CAN_CHOP, this.hasChopper() && !this.getItem(FISH_SLOT).isEmpty() && this.allResultSlotsEmpty());
	}

	// ======== CONTAINER INSERTION/EXTRACTION RULES START ========
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		// No inserting into output slots
		if(slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2 || slot == OUTPUT_SLOT_3) {
			return false;
		} else if(slot == CHOPPER_SLOT) {
			return this.isChopper(stack);
		}
		return super.canPlaceItem(slot, stack);
	}
	
	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		// No extracting from output slots
		if(slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2 || slot == OUTPUT_SLOT_3) {
			return false;
		}
		return super.canTakeItem(target, slot, stack);
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS_FOR_SIDES;
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	// ========= CONTAINER INSERTION/EXTRACTION RULES END =========
	
	@Nullable
	public Entity getInputEntity(Level level) {
		ItemStack stack = this.getItems().getFirst();
		if(!stack.isEmpty() && stack.getItem() instanceof MobItem<?> mob) {
			return mob.createCapturedEntity(level, 0, 0, 0, stack, false);
		}
		return null;
	}

	public ItemStack getSlotResult(Level level, int slot, int numItems) {
		if (this.recipe != null) {
			switch (slot) {
				case 0:
					return ItemStack.EMPTY;
				case 1, 2, 3:
					return this.recipe.assembleRecipe(new SingleRecipeInput(this.getItem(0)), level).get(slot - 1);
				case 4:
					return this.recipe.getRemains().copyWithCount(numItems);
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Set the remains for the trimming table
	 * @param remainsItem
	 * @param remainsCount
	 */
	public void setRemains(ItemStack remainsItem, int remainsCount) {
		if(remainsItem.isEmpty()) remainsItem = ItemStack.EMPTY;
		this.remainsItem = remainsItem;
		this.remainsCount = remainsCount;
	}
	
	/**
	 * Gets the item used for the remains, without scaling from {@linkplain #getRemainsCount()}
	 * @return
	 */
	public ItemStack getRemainsItem() {
		return this.remainsItem;
	}

	/**
	 * Gets count multiplier for the remains item, used in {@linkplain #getRemainsStack()}
	 * @return
	 */
	public int getRemainsCount() {
		return this.remainsCount;
	}

	/**
	 * Gets the remains stack for recycling.
	 * 
	 * <p>The remains stack is found by multiplying the count of {@linkplain #getRemainsItem()} by the value of {@linkplain #getRemainsCount()}</p>
	 * @return
	 */
	public ItemStack getRemainsStack() {
		int remainsCount = this.getRemainsCount();
		if(remainsCount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack remainsItem = this.getRemainsItem();
		return remainsItem.copyWithCount(Math.min(remainsItem.getCount() * remainsCount, Item.ABSOLUTE_MAX_STACK_SIZE));
	}
	
	public boolean allResultSlotsEmpty() {
		return this.getItems().subList(1, 4).stream().allMatch(ItemStack::isEmpty) && (this.remainsCount == 0 || this.remainsItem.isEmpty());
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		
		CompoundTag remainsTag = new CompoundTag(2);
		if(!this.remainsItem.isEmpty()) {
			remainsTag.put("item", this.remainsItem.save(registries));
		}
		remainsTag.putInt("count", this.remainsCount);
		tag.put("remains", remainsTag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		
		if(tag.contains("remains", Tag.TAG_COMPOUND)) {
			CompoundTag remainsTag = tag.getCompound("remains");
			this.remainsItem = ItemStack.parseOptional(registries, remainsTag.getCompound("item"));
			this.remainsCount = remainsTag.getInt("count");
		} else {
			this.remainsItem = ItemStack.EMPTY;
			this.remainsCount = 0;
		}
		
		// Update recipe if the world is loaded
		this.updateRecipe();
		this.updateChopperValid();
		this.updateCanChop();
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
}
