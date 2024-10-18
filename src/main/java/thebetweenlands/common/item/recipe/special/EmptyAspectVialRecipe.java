package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.item.herblore.AspectVialItem;
import thebetweenlands.common.registries.RecipeRegistry;

public class EmptyAspectVialRecipe extends CustomRecipe {

	public EmptyAspectVialRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack vial = null;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof AspectVialItem) {
					if(vial != null) {
						return false;
					}
					vial = stack;
				} else {
					return false;
				}
			}
		}
		return vial != null;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		ItemStack vial = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof AspectVialItem) {
					vial = stack;
				}
			}
		}
		return new ItemStack(vial.getCraftingRemainingItem().getItem(), vial.getCount());
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return NonNullList.withSize(input.size(), ItemStack.EMPTY);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.EMPTY_ASPECT_VIAL.get();
	}
}
