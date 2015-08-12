package thebetweenlands.manual.gui;

import net.minecraft.item.crafting.IRecipe;
import thebetweenlands.manual.gui.widgets.*;
import thebetweenlands.recipes.RecipeHandler;

import java.util.ArrayList;

/**
 * Created by Bart on 11-8-2015.
 */
public class ManualEntryRegistry {
    public static int craftingRecipeWidth = CraftingRecipeWidget.width;
    public static int craftingRecipeHeight = CraftingRecipeWidget.height;
    public static int smeltingRecipeWidth = SmeltingRecipeWidget.width;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height;
    public static int compostRecipeWidth = CompostRecipeWidget.width;
    public static int compostRecipeHeight = CompostRecipeWidget.height;
    public static int PestleAndMortarRecipeWidth = PestleAndMortarRecipeWidget.width;
    public static int PestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height;
    public static int PurifierRecipeWidth = PurifierRecipeWidget.width;
    public static int PurifierRecipeHeight = PurifierRecipeWidget.height;

    public static ManualEntry entry1;

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

        entry1 = new ManualEntry(new ManualPage(new CraftingRecipeWidget(manual, recipes, 4, 10), new CraftingRecipeWidget(manual, recipes3, 4, 10 + craftingRecipeHeight + 5), new CraftingRecipeWidget(manual, recipes4, 4, 10 + craftingRecipeHeight + 10)), new ManualPage(new CraftingRecipeWidget(manual, recipes1, 4, 10)), new ManualPage(new CraftingRecipeWidget(manual, recipes2, 4, 10)));
    }

}
