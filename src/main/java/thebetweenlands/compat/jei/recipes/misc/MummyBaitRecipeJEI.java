package thebetweenlands.compat.jei.recipes.misc;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;

public class MummyBaitRecipeJEI implements ICraftingRecipeWrapper {

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> vials = NonNullList.create();
        for (int i = 0; i < 2; i++) {
            ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, i);
            ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(vial);
            aspectContainer.add(AspectRegistry.ARMANIIS, 1000);
            vials.add(vial.copy());
        }

        List<List<ItemStack>> inputLists = new ArrayList<>(9);
        inputLists.add(NonNullList.from(ItemStack.EMPTY, new ItemStack(ItemRegistry.SHIMMER_STONE)));
        inputLists.add(NonNullList.from(ItemStack.EMPTY, ItemMisc.EnumItemMisc.TAR_BEAST_HEART_ANIMATED.create(1)));
        inputLists.add(NonNullList.from(ItemStack.EMPTY, new ItemStack(ItemRegistry.SLUDGE_BALL)));
        inputLists.add(vials);

        ingredients.setInputLists(ItemStack.class, inputLists);
        ingredients.setOutput(ItemStack.class, new ItemStack(ItemRegistry.MUMMY_BAIT));
    }
}
