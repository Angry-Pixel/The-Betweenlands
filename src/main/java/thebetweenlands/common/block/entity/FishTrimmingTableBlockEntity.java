package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.recipes.TrimmingTableRecipe;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;

public class FishTrimmingTableBlockEntity extends BaseContainerBlockEntity {

	private NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
	@Nullable
	private TrimmingTableRecipe recipe;

	public FishTrimmingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.FISH_TRIMMING_TABLE.get(), pos, state);
	}

	public boolean hasAnadia() {
		ItemStack stack = this.getItem(0);
		return !stack.isEmpty() && stack.is(ItemRegistry.ANADIA) && stack.has(DataComponents.ENTITY_DATA);
	}

	private boolean isAnadiaRotten(Level level) {
		ItemStack stack = this.getItem(0);
		if (stack.has(DataComponents.ENTITY_DATA) && stack.has(DataComponentRegistry.ROT_TIME) && stack.get(DataComponents.ENTITY_DATA).copyTag().getByte("fish_color") != 0) {
			long rottingTime = stack.get(DataComponentRegistry.ROT_TIME);
			return rottingTime - level.getGameTime() <= 0;
		}
		return false;
	}

	public boolean hasChopper() {
		return false; //this.getItem(5).is(ItemRegistry.BONE_AXE);
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

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 6;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		if (slot == 0 && this.getLevel() != null) {
			this.recipe = this.getLevel().getRecipeManager().getRecipeFor(RecipeRegistry.TRIMMING_TABLE_RECIPE.get(), new SingleRecipeInput(stack), this.getLevel()).map(RecipeHolder::value).orElse(null);
		}
	}

	@Nullable
	public Entity getInputEntity(Level level) {
		ItemStack stack = this.getItems().getFirst();
		if(!stack.isEmpty() && stack.getItem() instanceof MobItem && ((MobItem) stack.getItem()).hasEntityData(stack)) {
			return ((MobItem) stack.getItem()).createCapturedEntity(level, 0, 0, 0, stack, false);
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

	public boolean allResultSlotsEmpty() {
		return this.getItems().subList(1, 5).isEmpty();
	}
}
