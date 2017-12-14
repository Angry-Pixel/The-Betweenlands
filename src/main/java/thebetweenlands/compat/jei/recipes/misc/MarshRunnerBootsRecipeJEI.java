package thebetweenlands.compat.jei.recipes.misc;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;

public class MarshRunnerBootsRecipeJEI implements ICraftingRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> vials = NonNullList.create();
        for (int i = 0; i < 2; i++) {
            ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, i);
            ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(vial);
            aspectContainer.add(AspectRegistry.BYRGINAZ, 1000);
            vials.add(vial.copy());
        }

        List<List<ItemStack>> inputLists = new ArrayList<>(9);
        inputLists.add(NonNullList.from(ItemStack.EMPTY, new ItemStack(ItemRegistry.RUBBER_BOOTS)));
        inputLists.add(vials);

        ingredients.setInputLists(ItemStack.class, inputLists);
        ingredients.setOutput(ItemStack.class, new ItemStack(ItemRegistry.MARSH_RUNNER_BOOTS));
    }
}
