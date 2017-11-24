package thebetweenlands.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeCategory;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeMaker;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeCategory;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeMaker;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeCategory;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeMaker;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarCategory;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarRecipeMaker;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeCategory;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeMaker;
import thebetweenlands.util.config.ConfigHandler;

@JEIPlugin
public class BetweenlandsJEIPlugin implements IModPlugin{
    public static IJeiHelpers jeiHelper;
    public static IJeiRuntime jeiRuntime;

    @Override
    public void register(IModRegistry registry) {
    	MinecraftForge.EVENT_BUS.register(DynamicJEIRecipeHandler.class);

        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.COMPOST_BIN), ModInfo.ID + ":compost");
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ANIMATOR), ModInfo.ID + ":animator");
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.DRUID_ALTAR), ModInfo.ID + ":druid_altar");
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.MORTAR), ModInfo.ID + ":pestle_and_mortar");
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.PURIFIER), ModInfo.ID + ":purifier");
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH), VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipes(CompostRecipeMaker.getRecipes(), ModInfo.ID + ":compost");
        registry.addRecipes(AnimatorRecipeMaker.getRecipes(), ModInfo.ID + ":animator");
        registry.addRecipes(DruidAltarRecipeMaker.getRecipes(), ModInfo.ID + ":druid_altar");
        registry.addRecipes(PestleAndMortarRecipeMaker.getRecipes(), ModInfo.ID + ":pestle_and_mortar");
        registry.addRecipes(PurifierRecipeMaker.getRecipes(), ModInfo.ID + ":purifier");

        registry.addRecipeClickArea(GuiWeedwoodWorkbench.class, 88, 32, 28, 23, VanillaRecipeCategoryUid.CRAFTING);

        IRecipeTransferRegistry recipeTranferRegistry = registry.getRecipeTransferRegistry();
        recipeTranferRegistry.addRecipeTransferHandler(ContainerWeedwoodWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);

        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        //Hiding blocks/items that don't have to show in JEI
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.MIDDLE_FRUIT_BUSH).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.FUNGUS_CROP).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.WEEDWOOD_DOOR).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.RUBBER_TREE_PLANK_DOOR).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.SYRMORITE_DOOR).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.STANDING_WEEDWOOD_SIGN).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.WALL_WEEDWOOD_SIGN).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.MOSS_BED).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.ASPECT_VIAL_BLOCK).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.ASPECTRUS_CROP.getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SWAMP_KELP));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SWAMP_REED));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SWAMP_REED_UNDERWATER));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SULFUR_FURNACE_ACTIVE));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SULFUR_FURNACE_DUAL_ACTIVE));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.ROPE));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.WEEDWOOD_RUBBER_TAP));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.SYRMORITE_RUBBER_TAP));
        blacklist.addIngredientToBlacklist(new ItemStack(BlockRegistry.TREE_PORTAL));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.GLUE));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.TAINTED_POTION));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.ROTTEN_FOOD));

        if (!ConfigHandler.debug) {
            blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.TEST_ITEM));
            blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.LOCATION_DEBUG));
        }

        //TODO remove when added
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.MANUAL_HL));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.ASPECTRUS_FRUIT));
        blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.ASPECTRUS_SEEDS));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
        jeiHelper = registry.getJeiHelpers();
        registry.addRecipeCategories(new CompostRecipeCategory(), new AnimatorRecipeCategory(), new DruidAltarRecipeCategory(), new PestleAndMortarCategory(), new PurifierRecipeCategory());
    }

}
