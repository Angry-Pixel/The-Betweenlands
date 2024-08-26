package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.CrabPotFilterRecipe;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.Optional;

public class CrabPotFilterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	private static final int EVENT_RESET_FILTERING_PROGRESS = 80;

	protected int maxFilteringTime = 200; // 10 seconds per item for a 64 stack = over 10.6 min IRL

	private int baitProgress = 0;
	private int filteringProgress = 0;
	private final int itemsToFilterCount = 3; // logic here means 1 already in the chamber + this

	private int prevFilteringAnimationTicks;
	private int filteringAnimationTicks;

	private boolean active;

	private static final int BAIT_SLOT = 0;
	private static final int INPUT_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;
	private static  final int[] RESULT_SLOTS = {OUTPUT_SLOT};
	private static  final int[] SIDE_SLOTS = {BAIT_SLOT, INPUT_SLOT};
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

	public CrabPotFilterBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.CRAB_POT_FILTER.get(), pos, blockState);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CrabPotFilterBlockEntity entity) {
		if (level.isClientSide()) {
			entity.prevFilteringAnimationTicks = entity.filteringAnimationTicks;

			if (entity.active && entity.canFilterSlots(level, pos, 1, 2)) {
				entity.filteringAnimationTicks = Math.min(entity.filteringAnimationTicks + 1, entity.maxFilteringTime);
			} else {
				entity.prevFilteringAnimationTicks = entity.filteringAnimationTicks = 0;
			}

			return;
		}

		if (level.getBlockState(pos.above()).is(BlockRegistry.CRAB_POT) && !entity.active && entity.hasCrabInTile(level, pos)) {
			entity.active = true;
			level.sendBlockUpdated(pos, state, state, 2);
		}

		if (level.getBlockState(pos.above()).is(BlockRegistry.CRAB_POT) && entity.hasCrabInTile(level, pos)) {
			entity.checkForAnimation(level, pos);
		}

		if (entity.active && (level.getBlockState(pos.above()).is(BlockRegistry.CRAB_POT) || !entity.hasCrabInTile(level, pos))) {
			entity.active = false;
			level.sendBlockUpdated(pos, state, state, 2);
		}

		if (entity.active) {
			if (entity.hasBait() && entity.canFilterSlots(level, pos, INPUT_SLOT, OUTPUT_SLOT)) {
				if (entity.getBaitProgress() == 0) {
					entity.consumeBait(level, pos, state);
				}

				entity.setBaitProgress(entity.getBaitProgress() + 1);
			}

			if (entity.canFilterSlots(level, pos, INPUT_SLOT, OUTPUT_SLOT)) {
				entity.setSlotProgress(entity.getSlotProgress() + 1);

				if (entity.getSlotProgress() >= entity.maxFilteringTime) {
					entity.filterItem(level, pos, state, INPUT_SLOT, OUTPUT_SLOT);
					level.blockEvent(pos, state.getBlock(), EVENT_RESET_FILTERING_PROGRESS, 0);
				}

				if (entity.getSlotProgress() % 10 == 0) {
					level.sendBlockUpdated(pos, state, state, 2);
				}
			} else {
				if (entity.getSlotProgress() > 0) {
					entity.setSlotProgress(0);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
	}

	public boolean isActive() {
		return this.active;
	}

	private void checkForAnimation(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos.above()) instanceof CrabPotBlockEntity pot && (pot.hasSiltCrab() || pot.hasBubblerCrab())) {
			if (canFilterSlots(level, pos, INPUT_SLOT, OUTPUT_SLOT) && !pot.animate) {
				pot.animate = true;
				level.sendBlockUpdated(pot.getBlockPos(), pot.getBlockState(), pot.getBlockState(), 2);
			}
			if (!this.canFilterSlots(level, pos, INPUT_SLOT, OUTPUT_SLOT) && pot.animate) {
				pot.animate = false;
				level.sendBlockUpdated(pot.getBlockPos(), pot.getBlockState(), pot.getBlockState(), 2);
			}
		}
	}

	private boolean hasCrabInTile(Level level, BlockPos pos) {
		return level.getBlockEntity(pos.above()) instanceof CrabPotBlockEntity pot && (pot.hasSiltCrab() || pot.hasBubblerCrab());
	}

	public ItemStack getRecipeOutput(Level level, BlockPos pos, ItemStack stack, boolean checkAnyIfNoCrabs, boolean checkAny) {
		SingleRecipeInput input = new SingleRecipeInput(stack);
		Optional<RecipeHolder<CrabPotFilterRecipe>> recipeHolder = level.getRecipeManager().getRecipeFor(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get(), input, level);
		if (recipeHolder.isEmpty()) return ItemStack.EMPTY;

		CrabPotBlockEntity pot = (CrabPotBlockEntity) level.getBlockEntity(pos.above());
		if (checkAny || (checkAnyIfNoCrabs && (pot == null || (!pot.hasSiltCrab() && !pot.hasBubblerCrab())))) {
			return recipeHolder.get().value().assemble(input, level.registryAccess());
		} else if (pot != null) {
			if (pot.getItem(0).getItem() instanceof MobItem mob && mob.isCapturedEntity(pot.getItem(0), recipeHolder.get().value().getRequiredFilteringMob())) {
				return recipeHolder.get().value().assemble(input, level.registryAccess());
			}
		}

		return ItemStack.EMPTY;
	}

	private void setSlotProgress(int counter) {
		this.filteringProgress = counter;
	}

	public int getSlotProgress() {
		return this.filteringProgress;
	}

	public void consumeBait(Level level, BlockPos pos, BlockState state) {
		ItemStack baitStack = this.getItem(BAIT_SLOT);
		this.setBaitProgress(0);
		level.sendBlockUpdated(pos, state, state, 2);
		baitStack.shrink(1);
	}

	private boolean canFilterSlots(Level level, BlockPos pos, int input, int output) {
		if (!this.active || !this.hasBait() || this.getItem(input).isEmpty() || (!this.getItem(output).isEmpty() && !ItemStack.isSameItemSameComponents(this.getItem(output), this.getRecipeOutput(level, pos, this.getItem(input), false, false))))
			return false;
		else {
			return !this.getRecipeOutput(level, pos, this.getItem(input), false, false).isEmpty();
		}
	}

	public boolean hasBait() {
		ItemStack baitStack = this.getItem(BAIT_SLOT);
		if (!baitStack.isEmpty())
			return true;
		else return this.getBaitProgress() > 0;
	}

	public void filterItem(Level level, BlockPos pos, BlockState state, int input, int output) {
		if (this.canFilterSlots(level, pos, input, output)) {
			ItemStack itemstack = this.getItem(input);
			ItemStack result = this.getRecipeOutput(level, pos, itemstack, false, false);
			ItemStack itemstack2 = this.getItem(output);
			if (!itemstack2.isEmpty() && ItemStack.isSameItemSameComponents(result, itemstack2)) { // better matching needed here, wip
				itemstack2.grow(1);
				this.setItem(output, itemstack2);
			}
			if (itemstack2.isEmpty())
				this.setItem(output, result.copy());
			this.setSlotProgress(0);
			level.sendBlockUpdated(pos, state, state, 2);
			if (this.getBaitProgress() > this.maxFilteringTime * this.itemsToFilterCount) {
				this.setBaitProgress(0);
				level.sendBlockUpdated(pos, state, state, 2);
			}
			itemstack.shrink(1);
		}
	}

	public void setBaitProgress(int duration) {
		baitProgress = duration;
	}

	public int getBaitProgress() {
		return baitProgress;
	}

	public int getBaitProgressScaled(int count) {
		return this.getBaitProgress() * count / (this.maxFilteringTime * this.itemsToFilterCount);
	}

	public int getFilteringProgressScaled(int count) {
		return this.getSlotProgress() * count / (this.maxFilteringTime);
	}

	public float getFilteringAnimationScaled(int count, float partialTicks) {
		return (this.prevFilteringAnimationTicks + (this.filteringAnimationTicks - this.prevFilteringAnimationTicks) * partialTicks) * count / (this.maxFilteringTime);
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

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 3;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("active", this.active);
		tag.putInt("bait_progress", this.baitProgress);
		tag.putInt("filtering_progress", this.filteringProgress);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.active = tag.getBoolean("active");
		this.baitProgress = tag.getInt("bait_progress");
		this.filteringProgress = tag.getInt("filtering_progress");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if(slot == BAIT_SLOT && stack.is(ItemRegistry.ANADIA_REMAINS))
			return true;
		return slot == INPUT_SLOT && !this.getRecipeOutput(this.getLevel(), this.getBlockPos(), stack, true, false).isEmpty();
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
