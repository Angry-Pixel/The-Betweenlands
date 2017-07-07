package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.ICorrodible;

class PurifierRecipeCorrodible extends PurifierRecipe {
	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ICorrodible && ((ICorrodible)stack.getItem()).getCorrosion(stack) > 0;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = input.copy();
		output.setCount(1);
		((ICorrodible)output.getItem()).setCorrosion(output, 0);
		return output;
	}
}
