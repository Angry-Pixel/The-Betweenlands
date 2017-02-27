package thebetweenlands.compat.jei;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeCategory;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeHandler;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeMaker;

@JEIPlugin
public class BetweenlandsJEIPlugin extends BlankModPlugin{
    public static IJeiHelpers jeiHelper;

    @Override
    public void register(IModRegistry registry) {
        jeiHelper = registry.getJeiHelpers();
        registry.addRecipeHandlers(new CompostRecipeHandler());
        registry.addRecipeCategories(new CompostRecipeCategory());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.COMPOST_BIN), ModInfo.ID + ":compost");
        registry.addRecipes(CompostRecipeMaker.getRecipes());

    }
}
