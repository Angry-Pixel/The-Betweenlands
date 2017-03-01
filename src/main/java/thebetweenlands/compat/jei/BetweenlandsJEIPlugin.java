package thebetweenlands.compat.jei;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeCategory;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeHandler;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeMaker;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeCategory;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeHandler;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeMaker;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarHandler;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeCategory;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeMaker;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarCategory;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarHandler;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarRecipeMaker;

@JEIPlugin
public class BetweenlandsJEIPlugin extends BlankModPlugin{
    public static IJeiHelpers jeiHelper;

    @Override
    public void register(IModRegistry registry) {
        jeiHelper = registry.getJeiHelpers();
        registry.addRecipeHandlers(new CompostRecipeHandler(), new AnimatorRecipeHandler(), new DruidAltarHandler(), new PestleAndMortarHandler());
        registry.addRecipeCategories(new CompostRecipeCategory(), new AnimatorRecipeCategory(), new DruidAltarRecipeCategory(), new PestleAndMortarCategory());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.COMPOST_BIN), ModInfo.ID + ":compost");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.ANIMATOR), ModInfo.ID + ":animator");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.DRUID_ALTAR), ModInfo.ID + ":druid_altar");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.MORTAR), ModInfo.ID + ":pestle_and_mortar");

        registry.addRecipes(CompostRecipeMaker.getRecipes());
        registry.addRecipes(AnimatorRecipeMaker.getRecipes());
        registry.addRecipes(DruidAltarRecipeMaker.getRecipes());
        registry.addRecipes(PestleAndMortarRecipeMaker.getRecipes());
    }
}
