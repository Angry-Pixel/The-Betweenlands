package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

class PurifierRecipeCorrodible extends PurifierRecipe {
	@Override
	public boolean matches(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ICorrodible && CorrosionHelper.getCorrosion(stack) > 0;
	}

	@Override
	public boolean matchesOutput(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = ItemStack.copyItemStack(input);
		output.stackSize = 1;
		CorrosionHelper.setCorrosion(output, 0);
		return output;
	}

	@Override
	public ItemStack getInput(ItemStack input) {
		return input;
	}
}
