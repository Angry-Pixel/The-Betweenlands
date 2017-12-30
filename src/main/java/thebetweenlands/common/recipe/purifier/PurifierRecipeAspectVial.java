package thebetweenlands.common.recipe.purifier;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.herblore.ItemAspectVial;

public class PurifierRecipeAspectVial extends PurifierRecipe {

    @Override
    public ItemStack getOutput(ItemStack input) {
        ItemStack output = input.copy();
        return output.getItem().getContainerItem(output);
    }

    @Override
    public boolean matchesInput(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemAspectVial && ItemAspectContainer.fromItem(stack).getAspects().size() >= 1;
    }
}
