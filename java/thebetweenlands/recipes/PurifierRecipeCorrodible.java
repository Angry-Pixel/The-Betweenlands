package thebetweenlands.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.utils.CorrodibleItemHelper;

class PurifierRecipeCorrodible extends PurifierRecipe {
	@Override
	public boolean matches(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ICorrodible && CorrodibleItemHelper.getCorrosion(stack) > 0;
	}

	@Override
	public boolean matchesOutput(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = ItemStack.copyItemStack(input);
		output.stackSize = 1;
		output.setTagInfo("Corrosion", new NBTTagInt(0));
		return output;
	}

	@Override
	public ItemStack getInput(ItemStack input) {
		return input;
	}
}
