package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.corrosion.CorrosionHelper;

class PurifierRecipeCorrodible extends PurifierRecipe {
	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ICorrodible && CorrosionHelper.getCorrosion(stack) > 0;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = ItemStack.copyItemStack(input);
		output.stackSize = 1;
		CorrosionHelper.setCorrosion(output, 0);
		return output;
	}
}
