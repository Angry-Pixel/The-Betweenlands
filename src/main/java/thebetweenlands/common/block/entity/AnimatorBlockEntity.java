package thebetweenlands.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.audio.AnimatorSoundInstance;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Optional;

public class AnimatorBlockEntity extends BaseContainerBlockEntity {

	public ItemStack itemToAnimate = ItemStack.EMPTY;
	public int fuelBurnProgress, lifeCrystalLife, fuelConsumed = 0, requiredFuelCount = 32, requiredLifeCount = 32;
	public boolean itemAnimated = false;
	private int prevStackSize = 0;
	private ItemStack prevItem = ItemStack.EMPTY;

	private boolean running = false;

	private boolean soundPlaying = false;
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

	public AnimatorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ANIMATOR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AnimatorBlockEntity entity) {
		if (entity.isSlotInUse(0) && entity.isValidFocalItem(level)) {
			entity.itemToAnimate = entity.getItem(0);
			if (!level.isClientSide()) {
				SingleRecipeInput input = new SingleRecipeInput(entity.itemToAnimate);
				Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), input, level);
				if (recipe.isPresent()) {
					entity.requiredFuelCount = recipe.get().value().getRequiredFuel(input);
					entity.requiredLifeCount = recipe.get().value().getRequiredLife(input);
				}
			}
		} else {
			entity.itemToAnimate = ItemStack.EMPTY;
		}
		if (!level.isClientSide()) {
			if (entity.isCrystalInSlot())
				entity.lifeCrystalLife = entity.getCrystalPower();
			if (!entity.isSlotInUse(0) || !entity.isSlotInUse(1) || !entity.isSlotInUse(2)) {
				entity.fuelBurnProgress = 0;
				entity.fuelConsumed = 0;
			}

			if (!entity.itemToAnimate.isEmpty() && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.isValidFocalItem(level)) {
				if (entity.lifeCrystalLife >= entity.requiredLifeCount) {
					entity.fuelBurnProgress++;
					if (entity.fuelBurnProgress >= 42) {
						entity.fuelBurnProgress = 0;
						entity.getItem(2).shrink(1);
						entity.fuelConsumed++;
						entity.setChanged();
					}
					entity.itemAnimated = false;
				}
			}

			if (entity.isSlotInUse(2) && !entity.itemAnimated) {
				if (!entity.isSlotInUse(0) || !entity.isSlotInUse(1)) {
					entity.fuelBurnProgress = 0;
					entity.fuelConsumed = 0;
				}
			}

			if (entity.fuelConsumed >= entity.requiredFuelCount && entity.isSlotInUse(0) && entity.isSlotInUse(1) && !entity.itemAnimated) {
				SingleRecipeInput recipeInput = new SingleRecipeInput(entity.getItem(0));
				Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level);
				if (recipe.isPresent()) {
					ItemStack input = entity.getItem(0).copy();
					ItemStack result = recipe.get().value().onAnimated(level, pos, recipeInput);
					if (result.isEmpty()) result = recipe.get().value().assemble(recipeInput, level.registryAccess());
					if (!result.isEmpty()) {
						entity.setItem(0, result.copy());

						AABB aabb = new AABB(pos).inflate(12);
						for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb, EntitySelector.NO_SPECTATORS)) {
							if (player.distanceToSqr(Vec3.atCenterOf(pos)) <= 144) {
								AdvancementCriterionRegistry.ANIMATE.trigger(input, result.copy(), player);
							}
						}
					}
				}
				entity.getItem(1).setDamageValue(entity.getItem(1).getDamageValue() + entity.requiredLifeCount);
				entity.setChanged();
				entity.itemAnimated = true;
			}
			if (entity.prevStackSize != (entity.isSlotInUse(0) ? entity.getItem(0).getCount() : 0))
				entity.setChanged();
			if (entity.prevItem != (entity.isSlotInUse(0) ? entity.getItem(0) : ItemStack.EMPTY))
				entity.setChanged();
			entity.prevItem = entity.isSlotInUse(0) ? entity.getItem(0) : ItemStack.EMPTY;
			entity.prevStackSize = entity.isSlotInUse(0) ? entity.getItem(0).getCount() : 0;

			boolean shouldBeRunning = entity.isSlotInUse(0) && entity.isCrystalInSlot() && entity.isSulfurInSlot() && entity.fuelConsumed < entity.requiredFuelCount && entity.lifeCrystalLife >= entity.requiredLifeCount && entity.isValidFocalItem(level);
			if (entity.running != shouldBeRunning) {
				entity.running = shouldBeRunning;
				entity.setChanged();
			}
		} else {
			if (entity.isRunning() && !entity.soundPlaying) {
				entity.playAnimatorSound();
				entity.soundPlaying = true;
			} else if (!entity.isRunning()) {
				entity.soundPlaying = false;
			}
		}
	}

	protected void playAnimatorSound() {
		Minecraft.getInstance().getSoundManager().play(new AnimatorSoundInstance(SoundRegistry.ANIMATOR.get(), SoundSource.BLOCKS, this));
	}

	public boolean isCrystalInSlot() {
		return this.isSlotInUse(1) && this.getItem(1).getItem() instanceof LifeCrystalItem && this.getItem(1).getDamageValue() < this.getItem(1).getMaxDamage();
	}

	public int getCrystalPower() {
		if (this.isCrystalInSlot())
			return this.getItem(1).getMaxDamage() - this.getItem(1).getDamageValue();
		return 0;
	}

	public boolean isSulfurInSlot() {
		return this.isSlotInUse(2) && this.getItem(2).is(ItemRegistry.SULFUR);
	}

	public boolean isSlotInUse(int slot) {
		return !this.getItem(slot).isEmpty();
	}

	public boolean isValidFocalItem(Level level) {
		if (!this.getItem(0).isEmpty()) {
			SingleRecipeInput recipeInput = new SingleRecipeInput(this.getItem(0));
			return level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level).isPresent();
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

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 3;
	}
}
