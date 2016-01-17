package thebetweenlands.recipes;

import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.herblore.ItemAspectVial;

class PurifierRecipeAspectVial extends PurifierRecipe {
	@Override
	public boolean matches(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemAspectVial && AspectManager.getDynamicAspects(stack).size() >= 1;
	}

	@Override
	public boolean matchesOutput(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack getOutput(ItemStack input) {
		ItemStack output = ItemStack.copyItemStack(input);
		return output.getItem().getContainerItem(output);
	}

	@Override
	public ItemStack getInput(ItemStack input) {
		return input;
	}
}
