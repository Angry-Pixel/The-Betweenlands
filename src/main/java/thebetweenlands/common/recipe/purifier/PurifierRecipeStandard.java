package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;

public class PurifierRecipeStandard extends PurifierRecipe {
	private final ItemStack output;
	private final ItemStack input;

	public PurifierRecipeStandard(ItemStack output, ItemStack input) {
		this.output = output.copy();
		this.input = input.copy();
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		return output.copy();
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		if (stack != null) {
			if (doesInputMatch(input, stack)) {
				stack = null;
				return true;
			}
		}
		return false;
	}

	public ItemStack getInput() {
		return input;
	}
}
