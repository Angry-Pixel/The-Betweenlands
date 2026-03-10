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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.capability.BLCapabilities;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandler;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.AnimatorSoundInstance;
import thebetweenlands.common.datamap.item.AnimatorFuel;
import thebetweenlands.common.inventory.AnimatorMenu;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class AnimatorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	/**
	 * Slot that contains the item to animate + recipe output once animation is finished
	 */
	public static final int FOCAL_SLOT = 0;
	/**
	 * Slot that contains the life crystal
	 */
	public static final int LIFE_CRYSTAL_SLOT = 1;
	/**
	 * Slot that contains the fuel (sulphur)
	 */
	public static final int FUEL_SLOT = 2;

	private static final int[] SLOTS_FOR_VERTICAL = new int[] {FOCAL_SLOT};
	private static final int[] SLOTS_FOR_HORIZONTAL = new int[] {LIFE_CRYSTAL_SLOT, FUEL_SLOT};
	
	private static final int DEFAULT_MAX_LIFE = 128;
	
	// If a recipe finished and the focal slot now contains the output items for it
	// Once all the output items have been extracted, this is set back to false
	public boolean hasOutputItems = false;
	
	// If we have a valid animator recipe that we could use
	public boolean hasRecipe = false;
	public ItemStack itemToAnimate = ItemStack.EMPTY;
	public int fuelBurnProgress = 0;
	public int fuelBurnDuration = 42;
	public int fuelValue = 1;
	public int lifeCrystalLife = 0;
	public int lifeCrystalMaxLife = DEFAULT_MAX_LIFE;
	public int fuelConsumed = 0;
	public int requiredFuelCount = 32;
	public int requiredLifeCount = 32;
	public boolean itemAnimated = false; // TODO try to remove in favour of hasOutputItems

	public float oRot;
	public float rot;
	public float tRot;

	private boolean running = false;

	private boolean soundPlaying = false;
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress;
				case 1 -> AnimatorBlockEntity.this.fuelBurnDuration;
				case 2 -> AnimatorBlockEntity.this.lifeCrystalLife;
				case 3 -> AnimatorBlockEntity.this.lifeCrystalMaxLife;
				case 4 -> AnimatorBlockEntity.this.itemAnimated ? 1 : 0;
				case 5 -> AnimatorBlockEntity.this.fuelConsumed;
				case 6 -> AnimatorBlockEntity.this.requiredFuelCount;
				case 7 -> AnimatorBlockEntity.this.requiredLifeCount;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress = value;
				case 1 -> AnimatorBlockEntity.this.fuelBurnDuration = value;
				case 2 -> AnimatorBlockEntity.this.lifeCrystalLife = value;
				case 3 -> AnimatorBlockEntity.this.lifeCrystalMaxLife = value;
				case 4 -> AnimatorBlockEntity.this.itemAnimated = value == 1;
				case 5 -> AnimatorBlockEntity.this.fuelConsumed = value;
				case 6 -> AnimatorBlockEntity.this.requiredFuelCount = value;
				case 7 -> AnimatorBlockEntity.this.requiredLifeCount = value;
			}
		}

		public int getCount() {
			return 8;
		}
	};

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	public final RecipeManager.CachedCheck<SingleRecipeInput, AnimatorRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.ANIMATOR_RECIPE.get());

	// Constructor
	public AnimatorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ANIMATOR.get(), pos, state);
	}
	
	// ======== Filters for slots ========
	
	@Nullable
	public static AnimatorFuel getAnimatorFuel(ItemStack stack) {
		return stack.getItemHolder().getData(DataMapRegistry.ANIMATOR_FUEL);
	}
	
	@Nullable
	public static ILifeCrystalHandler getLifeCrystalHandler(ItemStack stack) {
		return stack.getCapability(BLCapabilities.LifeCrystalHandler.ITEM);
	}
	
	public boolean isValidLifeCrystal(ItemStack stack) {
		return !stack.isEmpty() && getLifeCrystalHandler(stack) != null;
	}

	public boolean isValidFuel(ItemStack stack) {
		return !stack.isEmpty() && getAnimatorFuel(stack) != null;
	}

	public boolean isValidFocalItem(Level level, ItemStack stack) {
		if (!stack.isEmpty()) {
			SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
			return this.quickCheck.getRecipeFor(recipeInput, level).isPresent();
		}
		return false;
	}

	public boolean hasValidLifeCrystal() {
		return this.isValidLifeCrystal(this.getItem(LIFE_CRYSTAL_SLOT));
	}

	public boolean hasValidFuel() {
		return this.isValidFuel(this.getItem(FUEL_SLOT));
	}
	
	public boolean hasValidFocalItem(Level level) {
		return this.isValidFocalItem(level, this.getItem(FOCAL_SLOT));
	}

	// ======== Tick Loop ========
	
	public static void tick(Level level, BlockPos pos, BlockState state, AnimatorBlockEntity entity) {
		if (!level.isClientSide()) {
			// Update the container fields and anything that doesn't have to do with crafting
			entity.updateFields();
			
			// Don't do any crafting if there are still output items from the last recipe to be extracted
			if(!entity.hasOutputItems) { 
				entity.tickCrafting(level, pos, state);
			}
		} else {
			entity.updateEntityRotation();
			if (entity.isRunning() && !entity.soundPlaying) {
				BetweenlandsClient.playLocalSound(new AnimatorSoundInstance(SoundRegistry.ANIMATOR.get(), SoundSource.BLOCKS, entity));
				entity.soundPlaying = true;
			} else if (!entity.isRunning()) {
				entity.soundPlaying = false;
			}
		}
	}

	private void updateEntityRotation() {
		this.oRot = this.rot;
		double d0 = BetweenlandsClient.getClientPlayer().getX() - this.getBlockPos().getX() - 0.5D;
		double d1 = BetweenlandsClient.getClientPlayer().getZ() - this.getBlockPos().getZ() - 0.5D;
		this.tRot = (float) Mth.atan2(d1, d0);

		while (this.rot >= Mth.PI) {
			this.rot -= Mth.TWO_PI;
		}

		while (this.rot < -Mth.PI) {
			this.rot += Mth.TWO_PI;
		}

		while (this.tRot >= Mth.PI) {
			this.tRot -= Mth.TWO_PI;
		}

		while (this.tRot < -Mth.PI) {
			this.tRot += Mth.TWO_PI;
		}

		float f2 = this.tRot - this.rot;

		while (f2 >= Mth.PI) {
			f2 -= Mth.TWO_PI;
		}

		while (f2 < -Mth.PI) {
			f2 += Mth.TWO_PI;
		}

		this.rot += f2 * 0.4F;
	}

	/**
	 * Updates life crystal fields
	 */
	public void updateCrystalPowerFields() {
		final int lifePower, maxLifePower;

		// Get the current life power and max life power of this crystal
		if(this.hasValidLifeCrystal()) {
			// Get life crystal handler
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(this.getItem(LIFE_CRYSTAL_SLOT));
			lifePower = lifeCrystalHandler.getLifePower();
			maxLifePower = lifeCrystalHandler.getMaxLifePower();
		} else {
			lifePower = 0;
			maxLifePower = DEFAULT_MAX_LIFE;
		}

		// Update the fields and setChanged if crystal power has changed
		if(this.lifeCrystalLife != lifePower || this.lifeCrystalMaxLife != maxLifePower) {
			this.lifeCrystalLife = lifePower;
			this.lifeCrystalMaxLife = maxLifePower;
			this.setChanged();
		}
	}

	/**
	 * Updates fuel burn duration
	 */
	public void updateFuelFields() {
		final int fuelBurnTime, fuelValue;
		
		// Get the current fuel values
		if(this.hasValidFuel()) {
			AnimatorFuel animatorFuel = getAnimatorFuel(this.getItem(FUEL_SLOT));
			fuelBurnTime = animatorFuel.fuelBurnTime();
			fuelValue = animatorFuel.fuelValue();
		} else {
			fuelBurnTime = 42;
			fuelValue = 0;
		}
		
		// Update fields and setChanged
		if(this.fuelBurnDuration != fuelBurnTime || this.fuelValue != fuelValue) {
			this.fuelBurnDuration = fuelBurnTime;
			this.fuelValue = fuelValue;
			this.setChanged();
		}
	}
	
	/**
	 * Updates values that don't require crafting to be in progress
	 */
	public void updateFields() {
		// Update life crystal values
		this.updateCrystalPowerFields();
		
		// Update burn duration and value
		this.updateFuelFields();
		
		if (this.hasOutputItems || this.getItems().subList(0, 3).stream().anyMatch(ItemStack::isEmpty)) {
			this.fuelBurnProgress = 0;
			this.fuelConsumed = 0;
		}
		
		if (this.hasOutputItems && this.running) {
			this.running = false;
			this.setChanged();
		}
		
		if(this.hasOutputItems) {
			if(this.getItem(FOCAL_SLOT).isEmpty()) {
				this.setItem(FOCAL_SLOT, ItemStack.EMPTY);
				this.hasOutputItems = false;
				this.itemAnimated = false;
				this.setChanged();
			}
		}
	}
	
	/**
	 * Updates the current animator recipe based on the focal item
	 * 
	 * Returns true if the recipe changed in any way
	 * @param level
	 * @return if the recipe changed
	 */
	public boolean updateRecipe(Level level) {
		final boolean hadRecipe = this.hasRecipe;
		
		ItemStack focalItem = this.getItem(FOCAL_SLOT);
		if(focalItem.isEmpty() || !this.isValidFocalItem(level, focalItem)) {
			this.itemToAnimate = ItemStack.EMPTY;
			this.hasRecipe = false;
			return hadRecipe;
		}

		SingleRecipeInput recipeInput = new SingleRecipeInput(focalItem);
		RecipeHolder<AnimatorRecipe> recipe = this.quickCheck.getRecipeFor(recipeInput, level).orElse(null);
		if (recipe == null) {
			this.itemToAnimate = ItemStack.EMPTY;
			this.hasRecipe = false;
			return hadRecipe;
		}

		ItemStack previousItem = this.itemToAnimate;
		boolean focalItemChanged = (
			previousItem.isEmpty() ||
			previousItem.getCount() != focalItem.getCount() |
			!ItemStack.isSameItemSameComponents(focalItem, previousItem)
		);

		if(focalItemChanged) {
			this.itemToAnimate = focalItem.copy();
		}
		
		this.hasRecipe = true;
		this.requiredFuelCount = recipe.value().getRequiredFuel(recipeInput);
		this.requiredLifeCount = recipe.value().getRequiredLife(recipeInput);
		
		return !hadRecipe || focalItemChanged;
	}
	
	public boolean resetCraftingProgress() {
		// Consider changed whenever one of the fields updates
		// Maybe, or also maybe not and this is wrong
		boolean changed = this.fuelBurnProgress != 0 || this.fuelConsumed != 0 || this.running;
		this.fuelBurnProgress = 0;
		this.fuelConsumed = 0;
		this.running = false;
		return changed;
	}
	
	public void updateRunning() {
		boolean shouldBeRunning = this.hasRecipe && this.hasValidLifeCrystal() && this.hasValidFuel() && this.fuelConsumed < this.requiredFuelCount && this.lifeCrystalLife >= this.requiredLifeCount;
		if (this.running != shouldBeRunning) {
			this.running = shouldBeRunning;
			this.setChanged();
		}
	}
	
	public boolean burnFuel() {
		if(this.fuelConsumed < this.requiredFuelCount) {
			this.fuelBurnProgress++;
			if (this.fuelBurnProgress >= this.fuelBurnDuration) {
				this.fuelBurnProgress = 0;
				this.getItem(FUEL_SLOT).shrink(1);
				this.fuelConsumed += this.fuelValue;
			}
			return true;
		}
		return false;
	}
	
	public boolean completeRecipe(Level level, BlockPos pos) {
		if(this.fuelConsumed >= this.requiredFuelCount) {
			// Get the recipe
			ItemStack focalItem = this.getItem(FOCAL_SLOT);
			SingleRecipeInput recipeInput = new SingleRecipeInput(focalItem);
			RecipeHolder<AnimatorRecipe> recipe = this.quickCheck.getRecipeFor(recipeInput, level).orElse(null);
			if (recipe == null) {
				return false;
			}
			
			// Get the result item
			ItemStack resultStack = recipe.value().onAnimated((ServerLevel) level, pos, recipeInput);
			if (resultStack.isEmpty()) {
				// It may require custom handling
				resultStack = recipe.value().assemble(recipeInput, level.registryAccess());
			}
			
			// If the result item exists
			if (!resultStack.isEmpty()) {
				this.setItem(FOCAL_SLOT, resultStack.copy());
				this.hasOutputItems = true;

				// Animate item trigger
				for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(12), EntitySelector.NO_SPECTATORS)) {
					if (player.distanceToSqr(Vec3.atCenterOf(pos)) <= 12 * 12) {
						AdvancementCriteriaRegistry.ANIMATE.get().trigger(player, focalItem.copy(), resultStack.copy());
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public void drainLifeCrystalPower(Level level, BlockPos pos) {
		// Maybe we could have a data component or capability for life crystal power instead...
		
		ItemStack lifeCrystalItem = this.getItem(LIFE_CRYSTAL_SLOT);
		
		if(this.isValidLifeCrystal(lifeCrystalItem) && lifeCrystalItem.isDamageableItem()) {
			// The life crystal item prevents itself from breaking
			// So I'm leaving it up to the target item to determine what to do for custom implementations
			// This is a good candidate for a true capability to determine how much power a crystal has, and how to handle draining it
			
			lifeCrystalItem.hurtAndBreak(this.requiredLifeCount, (ServerLevel) level, (LivingEntity) null, null);
		}
	}
	
	public void tickCrafting(Level level, BlockPos pos, BlockState state) {
		boolean changed = false;
		
		// Check if the recipe has changed or if the item we're animating has changed since last tick
		// (Also is a partial check for if the item we're animated has changed, TODO see if the two checks can be merged)
		boolean recipeChanged = this.updateRecipe(level);
		changed = changed || recipeChanged; // Avoid short circuits
		
		if(recipeChanged) {
			// Reset crafting progress if the recipe changed
			this.resetCraftingProgress();
		}
		
		if(
			!this.hasRecipe
			|| !this.hasValidLifeCrystal()
			|| (this.fuelConsumed < this.requiredFuelCount && !this.hasValidFuel())
		) {
			if(this.resetCraftingProgress()) {
				changed = true;
			}
		} else {
			// Burn fuel if fuel still needs burning
			boolean fuelItemBurned = this.burnFuel();
			if(fuelItemBurned) {
				this.itemAnimated = false;
				changed = true;
//				this.setChanged();
//				changed = false;
			}
			
			if(this.completeRecipe(level, pos)) {
				this.drainLifeCrystalPower(level, pos);
				this.itemAnimated = true;
				changed = true;
			}
		}
		
		this.updateRunning();
		
		if(changed) {
			this.setChanged();
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.animator");
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
	public ItemStack removeItem(int slot, int amount) {
		// hasOutputItems is checked in setChanged(), so we don't need an explicit check here
		//    because super.removeItem(...) calls setChanged() if an item was extracted
		return super.removeItem(slot, amount);
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		// No update for hasOutputItems
		return super.removeItemNoUpdate(slot);
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack prevItem = this.getItem(slot);
		super.setItem(slot, stack);
		if (slot == LIFE_CRYSTAL_SLOT) {
			this.updateCrystalPowerFields();
		}
		if(slot == FOCAL_SLOT && this.hasOutputItems) {
			// Note: AbstractFurnaceBlockEntity does something very similar
			if(stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, prevItem)) {
				this.hasOutputItems = false;
				this.itemAnimated = false;
				this.setChanged();
			}
		}
	}

	@Override
	public void setChanged() {
		if(this.hasOutputItems && this.getItem(FOCAL_SLOT).isEmpty()) {
			this.hasOutputItems = false;
			this.itemAnimated = false;
		}
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new AnimatorMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return 3;
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if(slot == LIFE_CRYSTAL_SLOT) {
			// Only allow inserting life crystals
			return this.isValidLifeCrystal(stack);
		} else if(slot == FUEL_SLOT) {
			// Only allow inserting fuel
			return this.isValidFuel(stack);
		}
		
		if(slot == FOCAL_SLOT) {
			// Do not allow placing items into the focal slot if it's working as an output right now
			if(this.hasOutputItems) {
				return false;
			}
			// Only allow inserting a single item to the focal slot
			// (Our item handler also ensures that mods never insert a stack with a count more than 1)
			else {
				return this.getItem(slot).isEmpty();
			}
		}
		return super.canPlaceItem(slot, stack);
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		if(side.getAxis().isVertical()) {
			return SLOTS_FOR_VERTICAL;
		} else {
			return SLOTS_FOR_HORIZONTAL;
		}
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		if(index == FOCAL_SLOT) {
			// Deny the top face access to the focal slot if it's an output right now
			if(direction == Direction.UP && this.hasOutputItems) {
				return false;
			}
			// Don't allow inserting through the bottom face
			else if(direction == Direction.DOWN) {
				return false;
			}
		}
		return this.canPlaceItem(index, itemStack);
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if(index == FOCAL_SLOT) {
			// Deny the top face access to the focal slot if it's an output right now
			if(direction == Direction.UP && this.hasOutputItems) {
				return false;
			}
			// Deny the bottom face access to the focal slot if it's an input right now
			else if(direction == Direction.DOWN && !this.hasOutputItems) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("life", this.lifeCrystalLife);
		tag.putInt("progress", this.fuelBurnProgress);
		tag.putInt("items_consumed", this.fuelConsumed);
		tag.putBoolean("life_depleted", this.itemAnimated);
		tag.putBoolean("has_output_items", this.hasOutputItems);
		tag.putBoolean("has_recipe", this.hasRecipe);
		if (!this.itemToAnimate.isEmpty()) {
			tag.put("to_animate", this.itemToAnimate.save(registries));
		}
		tag.putBoolean("running", this.running);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.lifeCrystalLife = tag.getInt("life");
		this.fuelBurnProgress = tag.getInt("progress");
		this.fuelConsumed = tag.getInt("items_consumed");
		this.itemAnimated = tag.getBoolean("life_depleted");
		this.hasOutputItems = tag.getBoolean("has_output_items");
		this.hasRecipe = tag.getBoolean("has_recipe");
		if (tag.contains("to_animate", Tag.TAG_COMPOUND))
			this.itemToAnimate = ItemStack.parseOptional(registries, tag.getCompound("to_animate"));
		else
			this.itemToAnimate = ItemStack.EMPTY;
		this.running = tag.getBoolean("running");
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
