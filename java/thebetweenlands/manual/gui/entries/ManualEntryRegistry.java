package thebetweenlands.manual.gui.entries;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.TestItem;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
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

    public static ManualEntry entry;
    public static ManualEntry entry1;
    public static ManualEntry entry2;
    public static ManualEntry entry3;
    public static ManualEntry entry4;
    public static ManualEntry entry5;
    public static ManualEntry entry6;
    public static ManualEntry entry7;
    public static ManualEntry entry8;
    public static ManualEntry entry9;

    public static void init(GuiManualBase manual) {
        ArrayList<ItemStack> recipes = new ArrayList<>();
        recipes.add(new ItemStack(BLItemRegistry.weedwoodPickaxe));
        recipes.add(new ItemStack(BLItemRegistry.betweenstonePickaxe));
        recipes.add(new ItemStack(BLItemRegistry.octinePickaxe));
        recipes.add(new ItemStack(BLItemRegistry.valonitePickaxe));
        ArrayList<ItemStack> recipes1 = new ArrayList<>();
        recipes1.add(new ItemStack(BLItemRegistry.weedwoodAxe));
        recipes1.add(new ItemStack(BLItemRegistry.betweenstoneAxe));
        recipes1.add(new ItemStack(BLItemRegistry.octineAxe));
        recipes1.add(new ItemStack(BLItemRegistry.valoniteAxe));
        ArrayList<ItemStack> recipes2 = new ArrayList<>();
        recipes2.add(new ItemStack(BLItemRegistry.weedwoodShovel));
        recipes2.add(new ItemStack(BLItemRegistry.betweenstoneShovel));
        recipes2.add(new ItemStack(BLItemRegistry.octineShovel));
        recipes2.add(new ItemStack(BLItemRegistry.valoniteShovel));
        ArrayList<ItemStack> recipes3 = new ArrayList<>();
        recipes3.add(new ItemStack(BLItemRegistry.weedwoodSword));
        recipes3.add(new ItemStack(BLItemRegistry.betweenstoneSword));
        recipes3.add(new ItemStack(BLItemRegistry.octineSword));
        recipes3.add(new ItemStack(BLItemRegistry.valoniteSword));
        ArrayList<ItemStack> recipes4 = new ArrayList<>();
        recipes4.add(new ItemStack(BLItemRegistry.anglerToothArrow));
        recipes4.add((new ItemStack(BLItemRegistry.octineArrow)));
        ArrayList<ItemStack> gems = new ArrayList<>();
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.AQUA_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.CRIMSON_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.GREEN_MIDDLE_GEM));



        entry1 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.text.test")), new ManualPage(new CraftingRecipeWidget(manual, recipes, 4, 10), new CraftingRecipeWidget(manual, recipes3, 4, 10 + craftingRecipeHeight), new CraftingRecipeWidget(manual, recipes4, 4, 10 + craftingRecipeHeight * 2)), new ManualPage(new CraftingRecipeWidget(manual, recipes1, 4, 10)), new ManualPage(new CraftingRecipeWidget(manual, recipes2, 4, 10), new SmeltingRecipeWidget(manual, new ItemStack(Blocks.stone), 10, 10 + craftingRecipeHeight), new PurifierRecipeWidget(manual, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.GREEN_MIDDLE_GEM), 10, 10 + craftingRecipeHeight + smeltingRecipeHeight)));
        //entry2 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.title"), new ItemWidget(manual, (127/2)-24, 77, arrows, 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.description")), new ManualPage(new CraftingRecipeWidget(manual, recipes4, 5, 10)));
        entry2 = new ManualEntryItem(gems, "gems", manual, new PurifierRecipeWidget(manual, gems, 5, 10));
        entry3 = new ManualEntryItem((IManualEntryItem)BLItemRegistry.testItem, manual);
        entry4 = new ManualEntryEntity(new EntityFirefly(manual.mc.theWorld), manual);
        entry5 = new ManualEntryItem((IManualEntryItem)BLItemRegistry.explorerHat, manual);
        entry6 = new ManualEntryItem((IManualEntryItem)BLItemRegistry.voodooDoll, manual);
        entry7 = new ManualEntryItem((IManualEntryItem)BLItemRegistry.ringOfPower, manual);
    }

}
