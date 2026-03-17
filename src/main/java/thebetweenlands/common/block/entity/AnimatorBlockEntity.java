package thebetweenlands.common.block.entity;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
	
	public static final int DEFAULT_MAX_LIFE = 128;
	
	// Persistent data (save in saveAdditional; setChanged after updating)
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY); // Accessible to client (setChangedAndSync)
	/**
	 * The current time in ticks the current fuel item has been burning.
	 * Field (automatically updated to client if they have a screen open).
	 */
	public int fuelBurnProgress = 0; // Field
	/**
	 * How many fuel items have been .
	 * Field (automatically updated to client if they have a screen open).
	 */
	public int recipeFuelConsumed = 0; // Field
	/**
	 * Information about the previous recipe, so {@linkplain AnimatorRecipe#onRetrieved(net.minecraft.world.entity.player.Player, BlockPos, SingleRecipeInput)} can be called.
	 */
	protected Optional<AnimatorRecipeData> lastRecipeData = Optional.empty();
	/**
	 * If true:
	 * 1. The focal slot cannot be inserted/extracted from until a player opens the animator
	 * 2. After a player opens the animator, will be set to false
	 */
	protected boolean lastRecipeRequiresPlayerRetrieval = false;
	/**
	 * If the focal slot contains output items instead of items to be animated.
	 * While true, the animator will not attempt to animate the item in the focal slot.
	 * This flag is set to false if the items in the focal slot change or are emptied.
	 */
	protected boolean lastRecipeHasOutputItems = false;

	// Fields (automatically synced to any clients if they have the screen open; setChanged not necessary after changing)
	// Fields that aren't defined here: fuelBurnProgress, recipeFuelConsumed, running
	/**
	 * How many ticks this fuel item will take to finish burning
	 */
	public int fuelBurnDuration = 42;
	/**
	 * How much progress this fuel item will add after it finishes burning
	 */
	public int fuelBurnValue = 0;
	
	/**
	 * How much life power the current life crystal currently has
	 */
	public int lifeCrystalCurrentLife = 0;
	/**
	 * The max amount of life power the current life crystal can hold
	 */
	public int lifeCrystalMaxLife = DEFAULT_MAX_LIFE;
	/**
	 * How much life power will be drained from the life crystal after this recipe completes
	 * This may be greater than recipeRequiredLifeCount.
	 * This is 0 if {@linkplain #canExtractRequiredLife} is false
	 */
	public int lifeCrystalSimulatedDrain = 0;

	/**
	 * How much fuel need to be consumed for the recipe to complete
	 */
	public int recipeRequiredFuelCount = 0;
	/**
	 * How much life need to be consumed for the recipe to complete
	 */
	public int recipeRequiredLifeCount = 0;
	
	
	// Transient data (not stored directly, but is inferred from the persistent data after being loaded)
	/**
	 * Is the animator currently running?
	 * Accessible to client (sent in getUpdatePacket(), but not saved on the server)
	 */
	protected boolean running = false; // Accessible to client (setChangedAndSync)

	/**
	 * Is there a recipe available for the current focal item
	 */
	protected boolean hasRecipe = false;
	
	/**
	 * If there is a recipe available for the current focal item, true if the required amount of life power can be drained from the current life crystal.
	 * false if there is not a recipe available for the current focal item.
	 */
	protected boolean canExtractRequiredLife = false;
	

	public float oRot;
	public float rot;
	public float tRot;

	private boolean soundPlaying = false;
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress;
				case 1 -> AnimatorBlockEntity.this.fuelBurnDuration;
				case 2 -> AnimatorBlockEntity.this.fuelBurnValue;
				case 3 -> AnimatorBlockEntity.this.lifeCrystalCurrentLife;
				case 4 -> AnimatorBlockEntity.this.lifeCrystalMaxLife;
				case 5 -> AnimatorBlockEntity.this.lifeCrystalSimulatedDrain;
				case 6 -> AnimatorBlockEntity.this.recipeFuelConsumed;
				case 7 -> AnimatorBlockEntity.this.recipeRequiredFuelCount;
				case 8 -> AnimatorBlockEntity.this.recipeRequiredLifeCount;
				case 9 -> AnimatorBlockEntity.this.running ? 1 : 0;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> AnimatorBlockEntity.this.fuelBurnProgress = value;
				case 1 -> AnimatorBlockEntity.this.fuelBurnDuration = value;
				case 2 -> AnimatorBlockEntity.this.fuelBurnValue = value;
				case 3 -> AnimatorBlockEntity.this.lifeCrystalCurrentLife = value;
				case 4 -> AnimatorBlockEntity.this.lifeCrystalMaxLife = value;
				case 5 -> AnimatorBlockEntity.this.lifeCrystalSimulatedDrain = value;
				case 6 -> AnimatorBlockEntity.this.recipeFuelConsumed = value;
				case 7 -> AnimatorBlockEntity.this.recipeRequiredFuelCount = value;
				case 8 -> AnimatorBlockEntity.this.recipeRequiredLifeCount = value;
				case 9 -> AnimatorBlockEntity.this.running = value != 0;
			}
		}

		public int getCount() {
			return 10;
		}
	};

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
		if (level.isClientSide()) {
			entity.updateEntityRotation();
			if (entity.isRunning() && !entity.soundPlaying) {
				BetweenlandsClient.playLocalSound(new AnimatorSoundInstance(SoundRegistry.ANIMATOR.get(), SoundSource.BLOCKS, entity));
				entity.soundPlaying = true;
			} else if (!entity.isRunning()) {
				entity.soundPlaying = false;
			}
		} else {
			entity.updateFuelFields();
			entity.updateLifeCrystalFields();
			if(entity.lastRecipeHasOutputItems && entity.getItem(FOCAL_SLOT).isEmpty()) {
				entity.lastRecipeHasOutputItems = false;
				entity.setChangedNoUpdate();
			}
			entity.updateRecipeTransientData(level);
			if(entity.canProcess()) {
				boolean recipeCompleted = entity.tickCrafting(level, pos, state);
				entity.updateCurrentlyProcessing(!recipeCompleted);
			} else {
				entity.resetCraftingProgress();
				entity.updateCurrentlyProcessing(false);
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

	public boolean hasRecipe() {
		return this.hasRecipe;
	}
	
	public boolean lastRecipeHasOutput() {
		return this.lastRecipeHasOutputItems || this.lastRecipeRequiresPlayerRetrieval;
	}
	
	public boolean canProcess() {
		return !this.lastRecipeHasOutput() && this.hasRecipe && this.canExtractRequiredLife;
	}
	
	/**
	 * Updates fuel burn fields
	 */
	public void updateFuelFields() {
		if(this.hasValidFuel()) {
			// Update fuel fields
			AnimatorFuel animatorFuel = getAnimatorFuel(this.getItem(FUEL_SLOT));
			this.fuelBurnDuration = animatorFuel.fuelBurnTime();
			this.fuelBurnValue = animatorFuel.fuelValue();
		} else {
			// Reset fuel fields
			this.fuelBurnDuration = 42;
			this.fuelBurnValue = 0;
		}
	}
	
	/**
	 * Updates the fields and transient data for life power drain
	 */
	public void updateLifeCrystalDrainData() {
		// If we do not have a valid crystal or recipe, reset life power drain data
		if(!this.hasValidLifeCrystal() || !this.hasRecipe()) {
			this.canExtractRequiredLife = false;
			this.lifeCrystalSimulatedDrain = 0;
			return;
		}
		
		// If we have a valid crystal and recipe
		ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(this.getItem(LIFE_CRYSTAL_SLOT));
		int simulatedDrain = lifeCrystalHandler.drainLifePower(this.recipeRequiredLifeCount, true);
		
		if(simulatedDrain >= this.recipeRequiredLifeCount) {
			// If we can extract the required amount, set life power drain fields
			this.canExtractRequiredLife = true;
			this.lifeCrystalSimulatedDrain = simulatedDrain;
		} else {
			// If we cannot extract the required amount, reset life power drain fields
			this.canExtractRequiredLife = false;
			this.lifeCrystalSimulatedDrain = 0;
		}
	}

	/**
	 * Drains the amount of power required by the current recipe from the current life crystal
	 * @param level
	 * @param pos
	 */
	public void drainLifeCrystalPower(Level level, BlockPos pos) {
		if(this.hasValidLifeCrystal() && this.hasRecipe()) {
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(this.getItem(LIFE_CRYSTAL_SLOT));
			lifeCrystalHandler.drainLifePower(this.recipeRequiredLifeCount, false);
		}
	}
	
	/**
	 * Updates life crystal fields, and life crystal drain data
	 */
	public void updateLifeCrystalFields() {
		if(this.hasValidLifeCrystal()) {
			// Update life crystal life fields
			ILifeCrystalHandler lifeCrystalHandler = getLifeCrystalHandler(this.getItem(LIFE_CRYSTAL_SLOT));
			this.lifeCrystalCurrentLife = lifeCrystalHandler.getLifePower();
			this.lifeCrystalMaxLife = lifeCrystalHandler.getMaxLifePower();
		} else {
			this.lifeCrystalCurrentLife = 0;
			this.lifeCrystalMaxLife = DEFAULT_MAX_LIFE;
		}
	}
	
	/**
	 * Updates "running" field / transient data
	 */
	public void updateCurrentlyProcessing(boolean isRunning) {
		if(this.running != isRunning) {
			this.running = isRunning;
			this.markUpdated();
		}
	}
	
	/**
	 * Resets all data relating to the current animator recipe
	 */
	public void resetRecipeTransientData() {
		this.hasRecipe = false;
		this.recipeRequiredFuelCount = 0;
		this.recipeRequiredLifeCount = 0;
		this.canExtractRequiredLife = false;
		this.lifeCrystalSimulatedDrain = 0;
		// Mandatory update running due to the order in which menu sync and player actions are handled each tick
		this.updateCurrentlyProcessing(false);
	}
	
	/**
	 * Updates the data for the current animator recipe, based on the focal item
	 * @param level
	 */
	public void updateRecipeTransientData(Level level) {
		ItemStack focalItem = this.getItem(FOCAL_SLOT);
		// Ensure we have a valid focal item
		if(focalItem.isEmpty() || !this.isValidFocalItem(level, focalItem)) {
			this.resetRecipeTransientData();
			return;
		}
		
		SingleRecipeInput recipeInput = new SingleRecipeInput(focalItem);
		RecipeHolder<AnimatorRecipe> recipe = this.quickCheck.getRecipeFor(recipeInput, level).orElse(null);
		// Ensure we have a valid recipe for this item
		if (recipe == null) {
			this.resetRecipeTransientData();
			return;
		}
		
		this.hasRecipe = true;
		this.recipeRequiredFuelCount = recipe.value().getRequiredFuel(recipeInput);
		this.recipeRequiredLifeCount = recipe.value().getRequiredLife(recipeInput);
		this.updateLifeCrystalDrainData();
		// Mandatory update running due to the order in which menu sync and player actions are handled each tick
		this.updateCurrentlyProcessing(this.canProcess());
	}
	
	/**
	 * Resets the crafting progress fields
	 */
	public void resetCraftingProgress() {
		if(this.fuelBurnProgress != 0 || this.recipeFuelConsumed != 0) {
			this.fuelBurnProgress = 0;
			this.recipeFuelConsumed = 0;
			this.setChangedNoUpdate();
		}
	}
	
	/**
	 * Burns fuel items if the recipe has not completed yet
	 */
	public void burnFuel() {
		if(this.recipeFuelConsumed < this.recipeRequiredFuelCount) {
			this.fuelBurnProgress++;
			if (this.fuelBurnProgress >= this.fuelBurnDuration) {
				this.fuelBurnProgress = 0;
				this.getItem(FUEL_SLOT).shrink(1);
				this.recipeFuelConsumed += this.fuelBurnValue;
				// set changed and sync, because items are accessible to the client
				this.setChanged();
			}
		}
	}
	
	/**
	 * Updates the info about the last recipe.
	 * Does not mark this block entity as changed.
	 * @param level
	 * @param pos
	 * @param recipeInput
	 * @param recipe
	 */
	public void updateLastRecipe(Level level, BlockPos pos, SingleRecipeInput recipeInput, RecipeHolder<AnimatorRecipe> recipe) {
		this.lastRecipeRequiresPlayerRetrieval = recipe.value().requiresPlayerRetrieval(level, pos, recipeInput);
		
		this.lastRecipeData = Optional.of(new AnimatorRecipeData(recipe.id(), recipeInput.item()));
	}
	
	/**
	 * Attempts to complete the current recipe and updates the data for the last completed recipe
	 * 
	 * Does not mark this block entity as changed.
	 * It is up to the caller to mark this block entity as changed if this method returns true.
	 * @param level
	 * @param pos
	 * @return true if the recipe was completed
	 */
	public boolean completeRecipe(Level level, BlockPos pos) {
		// If the recipe hasn't completed yet
		if(this.recipeFuelConsumed < this.recipeRequiredFuelCount) {
			return false;
		}
		
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
			this.setItemInternal(FOCAL_SLOT, resultStack.copy());
			this.lastRecipeHasOutputItems = true;

			// Animate item trigger
			for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(12), EntitySelector.NO_SPECTATORS)) {
				if (player.distanceToSqr(Vec3.atCenterOf(pos)) <= 12 * 12) {
					AdvancementCriteriaRegistry.ANIMATE.get().trigger(player, focalItem.copy(), resultStack.copy());
				}
			}
		}
		
		// Updates "last recipe" data
		this.updateLastRecipe(level, pos, recipeInput, recipe);
		
		return true;
	}
	
	/**
	 * Ticks the current crafting recipe
	 * 
	 * @param level
	 * @param pos
	 * @param state
	 * @return true if the recipe completed this tick
	 */
	public boolean tickCrafting(Level level, BlockPos pos, BlockState state) {
		// Burn fuel
		this.burnFuel();
		
		// Maybe complete recipe if it's done
		if(this.completeRecipe(level, pos)) {
			// If the recipe has completed, drain life crystal power
			this.drainLifeCrystalPower(level, pos);
			
			// If the recipe has completed, reset fields
			this.fuelBurnProgress = 0;
			this.recipeFuelConsumed = 0;
			this.recipeRequiredFuelCount = 0;
			this.recipeRequiredLifeCount = 0;
			this.hasRecipe = false;
			this.canExtractRequiredLife = false;
			this.lifeCrystalSimulatedDrain = 0;
			this.running = false;

			// Set changed and sync
			this.setChanged();
			
			return true;
		}

		return false;
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
		// No update
		return super.removeItemNoUpdate(slot);
	}
	
	protected void setItemInternal(int slot, ItemStack stack) {
		super.setItem(slot, stack);
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack prevItem = this.getItem(slot);
		super.setItem(slot, stack);
		if(slot == FOCAL_SLOT) {
			// Note: AbstractFurnaceBlockEntity does something very similar
			if(stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, prevItem)) {
				if(this.lastRecipeHasOutputItems) {
					// If the focal slot was acting as an output and the output items were taken, then it is no longer an output
					this.lastRecipeHasOutputItems = false;
					this.setChangedNoUpdate();
				} else {
					// If the focal slot was acting as the crafting input, then reset crafting progress
					this.resetCraftingProgress();
					// If the world is loaded, update transient recipe data
					if(this.hasLevel()) {
						this.updateRecipeTransientData(this.getLevel());
					} else {
						this.resetRecipeTransientData();
					}
				}
			}
		}
		if(slot == LIFE_CRYSTAL_SLOT) {
			this.updateLifeCrystalFields();
			this.updateLifeCrystalDrainData();
		}
		if(slot == FUEL_SLOT) {
			this.updateFuelFields();
			// If the fuel item changed, reset burn progress
			if(!ItemStack.isSameItemSameComponents(stack, prevItem) && this.fuelBurnProgress != 0) {
				this.fuelBurnProgress = 0;
				this.setChangedNoUpdate();
			}
		}
	}

	public void setChangedNoUpdate() {
		if(this.lastRecipeHasOutputItems && this.getItem(FOCAL_SLOT).isEmpty()) {
			this.lastRecipeHasOutputItems = false;
		}
		super.setChanged();
	}

	public void markUpdated() {
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}
	
	@Override
	public void setChanged() {
		this.setChangedNoUpdate();
		this.markUpdated();
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
			if(this.lastRecipeHasOutput()) {
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
			if(direction == Direction.UP && this.lastRecipeHasOutput()) {
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
			// This can only be taken by a player
			if(this.lastRecipeRequiresPlayerRetrieval) {
				return false;
			}
			
			// Deny the top face access to the focal slot if it's an output right now
			if(direction == Direction.UP && this.lastRecipeHasOutput()) {
				return false;
			}
			// Deny the bottom face access to the focal slot if it's an input right now
			else if(direction == Direction.DOWN && this.lastRecipeHasOutput()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("fuel_burn_progress", this.fuelBurnProgress);
		tag.putInt("fuel_consumed", this.recipeFuelConsumed);
		tag.putBoolean("has_output_items", this.lastRecipeHasOutputItems);
		tag.putBoolean("requires_player_retrieval", this.lastRecipeRequiresPlayerRetrieval);
		if(this.lastRecipeData.isPresent()) {
			AnimatorRecipeData lastRecipe = this.lastRecipeData.get();
			CompoundTag lastRecipeTag = new CompoundTag(2);
			lastRecipeTag.putString("id", lastRecipe.recipeId().toString());
			lastRecipeTag.put("input", lastRecipe.recipeInput.save(registries));
			tag.put("last_recipe", lastRecipeTag);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items.clear();
		ContainerHelper.loadAllItems(tag, this.items, registries);
		
		this.fuelBurnProgress = tag.getInt("fuel_burn_progress");
		this.recipeFuelConsumed = tag.getInt("fuel_consumed");

		this.lastRecipeHasOutputItems = tag.getBoolean("has_output_items");
		this.lastRecipeRequiresPlayerRetrieval = tag.getBoolean("requires_player_retrieval");
		
		if(tag.contains("last_recipe", Tag.TAG_COMPOUND)) {
			CompoundTag lastRecipeTag = tag.getCompound("last_recipe");
			ResourceLocation id = ResourceLocation.tryParse(lastRecipeTag.getString("id"));
			ItemStack input = ItemStack.parseOptional(registries, lastRecipeTag.getCompound("input"));
			this.lastRecipeData = Optional.of(new AnimatorRecipeData(id, input));
		} else {
			this.lastRecipeData = Optional.empty();
		}
		
		this.updateFuelFields();
		this.updateLifeCrystalFields();
		if(this.hasLevel()) {
			this.updateRecipeTransientData(this.getLevel());
		} else {
			this.resetRecipeTransientData();
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.updateRecipeTransientData(this.getLevel());
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = this.saveCustomOnly(registries);
		tag.putBoolean("running", this.running);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
		super.handleUpdateTag(tag, lookupProvider);
		this.running = tag.getBoolean("running");
	}

	protected static record AnimatorRecipeData(ResourceLocation recipeId, ItemStack recipeInput) {
		
	}
	
	public boolean hasOutputItems() {
		return this.lastRecipeHasOutputItems;
	}
	
	public boolean requiresPlayerRetrieval() {
		return this.lastRecipeRequiresPlayerRetrieval;
	}
	
	/**
	 * Applies all pending retrieval behaviours
	 * @param level
	 * @param pos
	 * @param player
	 * @return true if there are no more retrieval behaviours necessary
	 */
	public boolean processRetrieval(Level level, BlockPos pos, Player player) {
		// There is no previous recipe, so there are no behaviours that need processing
		if(this.lastRecipeData.isEmpty()) {
			this.lastRecipeRequiresPlayerRetrieval = false;
			return true;
		}
		
		AnimatorRecipeData lastRecipe = this.lastRecipeData.get();
		
		SingleRecipeInput recipeInput = new SingleRecipeInput(lastRecipe.recipeInput().copy());
		Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level, lastRecipe.recipeId());
		// There is no previous recipe, so there are no behaviours that need processing
		if(recipe.isEmpty()) {
			this.lastRecipeRequiresPlayerRetrieval = false;
			return true;
		}
		
		boolean canOpenMenu = recipe.get().value().onRetrieved(player, pos, recipeInput);
		
		if(canOpenMenu) {
			this.lastRecipeRequiresPlayerRetrieval = false;
			return true;
		}
		
		return false;
	}
}
