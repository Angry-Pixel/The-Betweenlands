package thebetweenlands.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
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
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeCategory;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeHandler;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeMaker;

@JEIPlugin
public class BetweenlandsJEIPlugin extends BlankModPlugin{
    public static IJeiHelpers jeiHelper;
    public static IJeiRuntime jeiRuntime;

    @Override
    public void register(IModRegistry registry) {
        jeiHelper = registry.getJeiHelpers();
        registry.addRecipeHandlers(new CompostRecipeHandler(), new AnimatorRecipeHandler(), new DruidAltarHandler(), new PestleAndMortarHandler(), new PurifierRecipeHandler());
        registry.addRecipeCategories(new CompostRecipeCategory(), new AnimatorRecipeCategory(), new DruidAltarRecipeCategory(), new PestleAndMortarCategory(), new PurifierRecipeCategory());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.COMPOST_BIN), ModInfo.ID + ":compost");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.ANIMATOR), ModInfo.ID + ":animator");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.DRUID_ALTAR), ModInfo.ID + ":druid_altar");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.MORTAR), ModInfo.ID + ":pestle_and_mortar");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.PURIFIER), ModInfo.ID + ":purifier");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH), VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipes(CompostRecipeMaker.getRecipes());
        registry.addRecipes(AnimatorRecipeMaker.getRecipes());
        registry.addRecipes(DruidAltarRecipeMaker.getRecipes());
        registry.addRecipes(PestleAndMortarRecipeMaker.getRecipes());
        registry.addRecipes(PurifierRecipeMaker.getRecipes());
        IRecipeTransferRegistry recipeTranferRegistry = registry.getRecipeTransferRegistry();

        registry.addRecipeClickArea(GuiWeedwoodWorkbench.class, 88, 32, 28, 23, VanillaRecipeCategoryUid.CRAFTING);

        recipeTranferRegistry.addRecipeTransferHandler(ContainerWeedwoodWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
        System.out.println("runtime available, yay!");
    }
}
