package thebetweenlands.common.block.entity;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.CrabPotFilterRecipe;
import thebetweenlands.common.block.CrabPotFilterBlock;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.inventory.CrabPotFilterMenu;
import thebetweenlands.common.items.recipe.ItemAndEntityInput;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class CrabPotFilterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	private static final int BAIT_SLOT = 0;
	private static final int INPUT_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;
	private static final int[] RESULT_SLOTS = {OUTPUT_SLOT};
	private static final int[] SIDE_SLOTS = {BAIT_SLOT, INPUT_SLOT};
	private static final int EVENT_RESET_FILTERING_PROGRESS = 80;

	private int baitTime;
	private int baitDuration = 800;
	private int filteringProgress;
	private int filteringTotalTime;

	private int prevFilteringAnimationTicks;
	private int filteringAnimationTicks;

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	protected final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> CrabPotFilterBlockEntity.this.baitTime;
				case 1 -> CrabPotFilterBlockEntity.this.baitDuration;
				case 2 -> CrabPotFilterBlockEntity.this.filteringProgress;
				case 3 -> CrabPotFilterBlockEntity.this.filteringTotalTime;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> CrabPotFilterBlockEntity.this.baitTime = value;
				case 1 -> CrabPotFilterBlockEntity.this.baitDuration = value;
				case 2 -> CrabPotFilterBlockEntity.this.filteringProgress = value;
				case 3 -> CrabPotFilterBlockEntity.this.filteringTotalTime = value;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	};
	private final RecipeManager.CachedCheck<ItemAndEntityInput, CrabPotFilterRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get());

	public CrabPotFilterBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.CRAB_POT_FILTER.get(), pos, blockState);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CrabPotFilterBlockEntity entity) {
		//only perform logic if waterlogged
		if (state.getValue(CrabPotFilterBlock.WATER_TYPE) == SwampWaterLoggable.WaterType.NONE) return;
		if (level.isClientSide()) {
			entity.prevFilteringAnimationTicks = entity.filteringAnimationTicks;

			if (entity.isBaited() && entity.getPottedCrab(level, pos) != null) {
				entity.filteringAnimationTicks = Math.min(entity.filteringAnimationTicks + 1, entity.filteringTotalTime);
			} else {
				entity.prevFilteringAnimationTicks = entity.filteringAnimationTicks = 0;
			}
		} else {
			boolean flag = entity.isBaited();
			boolean flag1 = false;
			if (entity.isBaited()) {
				entity.baitTime--;
			}

			ItemStack fuelStack = entity.getItem(1);
			ItemStack inputStack = entity.getItem(0);
			boolean flag2 = !inputStack.isEmpty();
			boolean flag3 = !fuelStack.isEmpty();
			if (entity.isBaited() || flag3 && flag2) {
				ItemAndEntityInput input = new ItemAndEntityInput(entity.getPottedCrab(level, pos), inputStack);
				RecipeHolder<CrabPotFilterRecipe> recipeholder;
				if (flag2) {
					recipeholder = entity.quickCheck.getRecipeFor(input, level).orElse(null);
				} else {
					recipeholder = null;
				}

				if (!entity.isBaited() && entity.canFilter(level.registryAccess(), input, recipeholder)) {
					entity.baitTime = 800;
					if (entity.isBaited()) {
						flag1 = true;
						if (fuelStack.hasCraftingRemainingItem())
							entity.items.set(1, fuelStack.getCraftingRemainingItem());
						else if (flag3) {
							fuelStack.shrink(1);
							if (fuelStack.isEmpty()) {
								entity.items.set(1, fuelStack.getCraftingRemainingItem());
							}
						}
					}
				}

				if (entity.isBaited() && entity.canFilter(level.registryAccess(), input, recipeholder)) {
					entity.filteringProgress++;
					entity.refreshPotAnimation(level, pos, true);
					if (entity.filteringProgress == entity.filteringTotalTime) {
						entity.filteringProgress = 0;
						entity.filteringTotalTime = entity.getTotalFilterTime(input, level);
						if (entity.filter(level.registryAccess(), input, recipeholder)) {
							level.blockEvent(pos, state.getBlock(), EVENT_RESET_FILTERING_PROGRESS, 0);
						}

						flag1 = true;
					}
				} else {
					entity.filteringProgress = 0;
					level.blockEvent(pos, state.getBlock(), EVENT_RESET_FILTERING_PROGRESS, 0);
					entity.refreshPotAnimation(level, pos, false);
				}
			} else if (!entity.isBaited() && entity.filteringProgress > 0) {
				entity.filteringProgress = Mth.clamp(entity.filteringProgress - 2, 0, entity.filteringTotalTime);
				entity.refreshPotAnimation(level, pos, false);
			}

			if (flag != entity.isBaited()) {
				flag1 = true;
			}

			if (flag1) {
				entity.setChanged();
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == EVENT_RESET_FILTERING_PROGRESS) {
			this.prevFilteringAnimationTicks = this.filteringAnimationTicks = 0;
			return true;
		}
		return super.triggerEvent(id, type);
	}

	public boolean isBaited() {
		return this.baitTime > 0;
	}

	public boolean isProgressing() {
		return this.filteringProgress > 0;
	}

	private boolean canFilter(RegistryAccess registryAccess, ItemAndEntityInput input, @Nullable RecipeHolder<CrabPotFilterRecipe> recipe) {
		if (!this.getItem(0).isEmpty() && recipe != null) {
			ItemStack itemstack = recipe.value().assemble(input, registryAccess);
			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.getItem(2);
				if (itemstack1.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
					return false;
				} else {
					return itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize() || itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private boolean filter(RegistryAccess registryAccess, ItemAndEntityInput recipeInput, @Nullable RecipeHolder<CrabPotFilterRecipe> recipe) {
		if (recipe != null && this.canFilter(registryAccess, recipeInput, recipe)) {
			ItemStack input = this.getItem(0);
			ItemStack recipeResult = recipe.value().assemble(recipeInput, registryAccess);
			ItemStack resultSlot = this.getItem(2);
			if (resultSlot.isEmpty()) {
				this.setItem(2, recipeResult.copy());
			} else if (ItemStack.isSameItemSameComponents(resultSlot, recipeResult)) {
				resultSlot.grow(recipeResult.getCount());
			}

			input.shrink(1);
			return true;
		} else {
			return false;
		}
	}

	private int getTotalFilterTime(ItemAndEntityInput input, Level level) {
		return this.quickCheck.getRecipeFor(input, level).map(recipe -> recipe.value().filterTime()).orElse(200);
	}

	@Nullable
	public EntityType<?> getPottedCrab(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos.above()) instanceof CrabPotBlockEntity pot) {
			if (pot.hasBubblerCrab()) {
				return EntityRegistry.BUBBLER_CRAB.get();
			} else if (pot.hasSiltCrab()) {
				return EntityRegistry.SILT_CRAB.get();
			}
		}
		return null;
	}

	private void refreshPotAnimation(Level level, BlockPos pos, boolean validRecipe) {
		if(level.getBlockEntity(pos.above()) instanceof CrabPotBlockEntity pot && (pot.hasSiltCrab() || pot.hasBubblerCrab())) {
			level.blockEvent(pos.above(), pot.getBlockState().getBlock(), 1, validRecipe ? 1 : 0);
			pot.setChanged();
		}
	}

	public float getFilteringAnimationScaled(int count, float partialTicks) {
		return (this.prevFilteringAnimationTicks + (this.filteringAnimationTicks - this.prevFilteringAnimationTicks) * partialTicks) * count / 200;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack itemstack = this.items.get(slot);
		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
		this.items.set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		if (slot == 0 && !flag) {
			ItemAndEntityInput input = new ItemAndEntityInput(this.getPottedCrab(this.getLevel(), this.getBlockPos()), stack);
			this.filteringTotalTime = this.getTotalFilterTime(input, this.getLevel());
			this.filteringProgress = 0;
			this.setChanged();
		}
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.crab_pot_filter");
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
		return new CrabPotFilterMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return 3;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("bait_progress", this.baitTime);
		tag.putInt("filtering_progress", this.filteringProgress);
		tag.putInt("filtering_total", this.filteringTotalTime);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.baitTime = tag.getInt("bait_progress");
		this.filteringProgress = tag.getInt("filtering_progress");
		this.filteringTotalTime = tag.getInt("filtering_total");
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
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot == 2) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			return stack.is(ItemRegistry.ANADIA_REMAINS);
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? RESULT_SLOTS : SIDE_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return direction == Direction.DOWN && index == OUTPUT_SLOT;
	}
}
