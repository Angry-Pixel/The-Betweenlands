package thebetweenlands.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe;
import thebetweenlands.common.recipe.ShapelessOverrideDummyRecipe.ShapedOverrideDummyRecipe;
import thebetweenlands.common.recipe.misc.*;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeCategory;
import thebetweenlands.compat.jei.recipes.animator.AnimatorRecipeMaker;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeCategory;
import thebetweenlands.compat.jei.recipes.compost.CompostRecipeMaker;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeCategory;
import thebetweenlands.compat.jei.recipes.druid_altar.DruidAltarRecipeMaker;
import thebetweenlands.compat.jei.recipes.misc.*;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarCategory;
import thebetweenlands.compat.jei.recipes.pam.PestleAndMortarRecipeMaker;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeCategory;
import thebetweenlands.compat.jei.recipes.purifier.PurifierRecipeMaker;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class BetweenlandsJEIPlugin implements IModPlugin{
    public static IJeiHelpers jeiHelper;
    public static IJeiRuntime jeiRuntime;
    public static IIngredientRegistry ingredientRegistry;

    @Override
    public void register(IModRegistry registry) {
        ingredientRegistry = registry.getIngredientRegistry();
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

        registry.handleRecipes(ShapelessOverrideDummyRecipe.class, recipe -> new ShapelessOverrideRecipeJEI(jeiHelper, recipe), VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapedOverrideDummyRecipe.class, recipe -> new ShapedOverrideRecipeJEI(jeiHelper, recipe), VanillaRecipeCategoryUid.CRAFTING);
        
        addDynamicRecipes(registry);

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
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.SCABYST_DOOR).getItemBlock()));
        blacklist.addIngredientToBlacklist(new ItemStack(((BlockRegistry.ICustomItemBlock)BlockRegistry.GIANT_ROOT_PLANK_DOOR).getItemBlock()));
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

        if (!BetweenlandsConfig.DEBUG.debug) {
            blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.TEST_ITEM));
            blacklist.addIngredientToBlacklist(new ItemStack(ItemRegistry.LOCATION_DEBUG));
        }
    }

    private void addDynamicRecipes(IModRegistry registry) {
        List<IRecipe> recipes = new ArrayList<>();

        //Change active container to prevent warnings
        Loader loader = Loader.instance();
        ModContainer activeModContainer = loader.activeModContainer();
        ModContainer modContainer = loader.getIndexedModList().get(ModInfo.ID);
        if (modContainer != null) {
            loader.setActiveModContainer(modContainer);
        }

        //MummyBait
        registry.handleRecipes(RecipeMummyBait.class, recipe -> new MummyBaitRecipeJEI(), VanillaRecipeCategoryUid.CRAFTING);

        //Vials
        recipes.add(new ShapelessOreRecipe(null, new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 0), new ItemStack(ItemRegistry.ASPECT_VIAL,  1, 0)).setRegistryName(ModInfo.ID, RecipeRegistry.ASPECT_VIAL.getResourcePath() + "_green"));
        recipes.add(new ShapelessOreRecipe(null, new ItemStack(ItemRegistry.DENTROTHYST_VIAL, 1, 2), new ItemStack(ItemRegistry.ASPECT_VIAL,  1, 1)).setRegistryName(ModInfo.ID, RecipeRegistry.ASPECT_VIAL.getResourcePath() + "_orange"));

        //Plant Tonic
        recipes.add(new ShapelessOreRecipe(null, new ItemStack(ItemRegistry.BL_BUCKET_PLANT_TONIC, 1, 0), ItemRegistry.BL_BUCKET.withFluid(0, FluidRegistry.SWAMP_WATER), ItemRegistry.SAP_BALL).setRegistryName(ModInfo.ID, RecipeRegistry.PLANT_TONIC.getResourcePath() + "_weedwood"));
        recipes.add(new ShapelessOreRecipe(null, new ItemStack(ItemRegistry.BL_BUCKET_PLANT_TONIC, 1, 1), ItemRegistry.BL_BUCKET.withFluid(1, FluidRegistry.SWAMP_WATER), ItemRegistry.SAP_BALL).setRegistryName(ModInfo.ID, RecipeRegistry.PLANT_TONIC.getResourcePath() + "_syrmorite"));

        //Lurker skin
        ItemStack output = new ItemStack(ItemRegistry.LURKER_SKIN_POUCH);
        ItemStack input = new ItemStack(ItemRegistry.LURKER_SKIN_POUCH);
        ItemStack skin = ItemMisc.EnumItemMisc.LURKER_SKIN.create(1);
        for (int i = 0; i < 3; i++) {
            input.setItemDamage(i);
            output.setItemDamage(i+1);
            recipes.add(new ShapedOreRecipe(null, output.copy(), "LLL", "LPL", "LLL", 'L', skin, 'P', input.copy()).setRegistryName(ModInfo.ID, RecipeRegistry.LURKER_POUCH.getResourcePath() + "_" + i));
        }

        //Reset the active container
        loader.setActiveModContainer(activeModContainer);

        //MarshRunnerBoots
        registry.handleRecipes(RecipeMarshRunnerBoots.class, recipe -> new MarshRunnerBootsRecipeJEI(), VanillaRecipeCategoryUid.CRAFTING);

        //Life Crystal
        registry.handleRecipes(RecipesLifeCrystal.class, recipe -> new LifeCrystalRecipeJEI(jeiHelper.getGuiHelper()), VanillaRecipeCategoryUid.CRAFTING);

        //Coating
        CoatingRecipeJEI.setCoatableItems();
        registry.handleRecipes(RecipesCoating.class, recipe -> new CoatingRecipeJEI(jeiHelper.getGuiHelper()), VanillaRecipeCategoryUid.CRAFTING);

        //CircleGems
        CircleGemsRecipeJEI.updateApplicableItems();
        registry.handleRecipes(RecipesCircleGems.class, recipe -> new CircleGemsRecipeJEI(jeiHelper.getGuiHelper()), VanillaRecipeCategoryUid.CRAFTING);

        //Book merging
        registry.handleRecipes(BookMergeRecipe.class, recipe -> new BookMergeRecipeJEI(jeiHelper.getGuiHelper()), VanillaRecipeCategoryUid.CRAFTING);

        //Tarring recipe
        registry.handleRecipes(HearthgroveTarringRecipe.class, recipe -> new TarringRecipeJEI(jeiHelper.getGuiHelper()), VanillaRecipeCategoryUid.CRAFTING);

        registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        //Makes mummy bait and runner boots work better, but vials recipe won't work
        /*subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.ASPECT_VIAL, itemStack -> {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            if (nbtTagCompound == null || nbtTagCompound.hasNoTags()) {
                return itemStack.getItemDamage() + ISubtypeRegistry.ISubtypeInterpreter.NONE;
            }
            return itemStack.getItemDamage() + nbtTagCompound.toString();
        });*/
        subtypeRegistry.useNbtForSubtypes(ItemRegistry.AMULET);
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

    public static void addRecipeName(ResourceLocation registryName, IGuiItemStackGroup guiItemStacks, int ouputIndex) {
        guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == ouputIndex) {
                boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();
                if (showAdvanced) {
                    tooltip.add(TextFormatting.GRAY + registryName.getResourcePath());
                }
            }
        });
    }

}
