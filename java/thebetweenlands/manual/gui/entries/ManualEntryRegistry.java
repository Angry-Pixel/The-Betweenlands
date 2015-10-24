package thebetweenlands.manual.gui.entries;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemCompost;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.ManualPage;
import thebetweenlands.manual.gui.widgets.*;
import thebetweenlands.recipes.RecipeHandler;

import java.util.ArrayList;

/**
 * Created on 11-8-2015.
 */
public class ManualEntryRegistry {

    //these contain a preferred distance between 2 recipes
    public static int craftingRecipeWidth = CraftingRecipeWidget.width + 5;
    public static int craftingRecipeHeight = CraftingRecipeWidget.height + 5;
    public static int smeltingRecipeWidth = SmeltingRecipeWidget.width + 5;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height + 5;
    public static int compostRecipeWidth = CompostRecipeWidget.width + 5;
    public static int compostRecipeHeight = CompostRecipeWidget.height + 5;
    public static int PestleAndMortarRecipeWidth = PestleAndMortarRecipeWidget.width + 5;
    public static int PestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height + 5;
    public static int PurifierRecipeWidth = PurifierRecipeWidget.width + 5;
    public static int PurifierRecipeHeight = PurifierRecipeWidget.height + 5;

    public static ManualEntry entry1;
    public static ManualEntry entry2;

    public static void init(GuiManualBase manual) {
        ArrayList<IRecipe> recipes = new ArrayList<>();
        recipes.add(RecipeHandler.weedwoodPickAxeRecipe);
        recipes.add(RecipeHandler.betweenstonePickAxeRecipe);
        recipes.add(RecipeHandler.octinePickAxeRecipe);
        recipes.add(RecipeHandler.valonitePickAxeRecipe);
        ArrayList<IRecipe> recipes1 = new ArrayList<>();
        recipes1.add(RecipeHandler.weedwoodAxeRecipe);
        recipes1.add(RecipeHandler.betweenstoneAxeRecipe);
        recipes1.add(RecipeHandler.octineAxeRecipe);
        recipes1.add(RecipeHandler.valoniteAxeRecipe);
        ArrayList<IRecipe> recipes2 = new ArrayList<>();
        recipes2.add(RecipeHandler.weedwoodShovelRecipe);
        recipes2.add(RecipeHandler.betweenstoneShovelRecipe);
        recipes2.add(RecipeHandler.octineShovelRecipe);
        recipes2.add(RecipeHandler.valoniteShovelRecipe);
        ArrayList<IRecipe> recipes3 = new ArrayList<>();
        recipes3.add(RecipeHandler.weedwoodSwordRecipe);
        recipes3.add(RecipeHandler.betweenstoneSwordRecipe);
        recipes3.add(RecipeHandler.octineSwordRecipe);
        recipes3.add(RecipeHandler.valoniteSwordRecipe);
        ArrayList<IRecipe> recipes4 = new ArrayList<>();
        recipes4.add(RecipeHandler.anglerToothArrowRecipe);
        recipes4.add(RecipeHandler.octineArrowRecipe);
        ArrayList<ItemStack> arrows = new ArrayList<>();
        arrows.add(new ItemStack(BLItemRegistry.anglerToothArrow));
        arrows.add(new ItemStack(BLItemRegistry.basiliskArrow));
        arrows.add(new ItemStack(BLItemRegistry.octineArrow));
        arrows.add(new ItemStack(BLItemRegistry.poisonedAnglerToothArrow));

        entry1 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.text.test")), new ManualPage(new CraftingRecipeWidget(manual, recipes, 4, 10), new CraftingRecipeWidget(manual, recipes3, 4, 10 + craftingRecipeHeight), new CraftingRecipeWidget(manual, recipes4, 4, 10 + craftingRecipeHeight * 2)), new ManualPage(new CraftingRecipeWidget(manual, recipes1, 4, 10)), new ManualPage(new CraftingRecipeWidget(manual, recipes2, 4, 10), new SmeltingRecipeWidget(manual, new ItemStack(Blocks.cobblestone), 10, 10 + craftingRecipeHeight), new PurifierRecipeWidget(manual, new ItemStack(BLBlockRegistry.aquaMiddleGemOre), 10, 10 + craftingRecipeHeight + smeltingRecipeHeight)));
        entry2 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.title"), new ItemWidget(manual, (127/2)-24, 77, arrows, 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.description")), new ManualPage(new CraftingRecipeWidget(manual, recipes4, 5, 10)));
    }

}
