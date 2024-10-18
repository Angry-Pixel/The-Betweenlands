package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class AddWormToRodRecipe extends CustomRecipe {

	public AddWormToRodRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		boolean rod = false;
		int bait = 0;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack checkedStack = input.getItem(i);
			if (!checkedStack.isEmpty()) {
				if (checkedStack.is(ItemRegistry.WEEDWOOD_FISHING_ROD) && !checkedStack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)) {
					rod = true;
				} else if (checkedStack.is(ItemRegistry.TINY_SLUDGE_WORM) || checkedStack.is(ItemRegistry.TINY_SLUDGE_WORM_HELPER)) {
					bait++;
				}
			}
		}
		return rod && bait == 1;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		ItemStack rod = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
			if (!itemstack.isEmpty()) {
				if (itemstack.is(ItemRegistry.WEEDWOOD_FISHING_ROD)) {
					if (rod.isEmpty()) {
						rod = itemstack;
					} else {
						//Only accept 1 rod
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (!rod.isEmpty() && !rod.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)) {
			ItemStack newRod = rod.copy();
			newRod.set(DataComponentRegistry.FISHING_ROD_BAIT, true);
			return newRod;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.FISHING_ROD_WORM.get();
	}
}
