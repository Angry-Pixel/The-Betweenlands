package thebetweenlands.compat.jei.recipes.misc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class MummyBaitRecipeJEI implements ICraftingRecipeWrapper {

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.MUMMY_BAIT;
    }

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

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ItemRegistry.MUMMY_BAIT));
    }
}
