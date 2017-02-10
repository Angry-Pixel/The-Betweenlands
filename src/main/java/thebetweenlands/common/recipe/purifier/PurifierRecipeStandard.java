package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;

public class PurifierRecipeStandard extends PurifierRecipe {
	private final ItemStack output;
	private final ItemStack input;

	public PurifierRecipeStandard(ItemStack output, ItemStack input) {
		this.output = ItemStack.copyItemStack(output);
		this.input = ItemStack.copyItemStack(input);
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		return ItemStack.copyItemStack(output);
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		if (stack != null) {
			if (areStacksTheSame(input, stack)) {
				stack = null;
				return true;
			}
		}
		return false;
	}
}
