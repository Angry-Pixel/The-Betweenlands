package thebetweenlands.manual.gui.entries;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.CompostRecipeWidget;
import thebetweenlands.manual.gui.widgets.CraftingRecipeWidget;
import thebetweenlands.manual.gui.widgets.PestleAndMortarRecipeWidget;
import thebetweenlands.manual.gui.widgets.PurifierRecipeWidget;
import thebetweenlands.manual.gui.widgets.SmeltingRecipeWidget;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

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
        ArrayList<IManualEntryItem> recipes = new ArrayList<>();
        recipes.add((IManualEntryItem)BLItemRegistry.weedwoodPickaxe);
        recipes.add((IManualEntryItem)BLItemRegistry.betweenstonePickaxe);
        recipes.add((IManualEntryItem)BLItemRegistry.octinePickaxe);
        recipes.add((IManualEntryItem)BLItemRegistry.valonitePickaxe);
        ArrayList<IManualEntryItem> recipes1 = new ArrayList<>();
        recipes1.add((IManualEntryItem)BLItemRegistry.weedwoodAxe);
        recipes1.add((IManualEntryItem)BLItemRegistry.betweenstoneAxe);
        recipes1.add((IManualEntryItem)BLItemRegistry.octineAxe);
        recipes1.add((IManualEntryItem)BLItemRegistry.valoniteAxe);
        ArrayList<IManualEntryItem> recipes2 = new ArrayList<>();
        recipes2.add((IManualEntryItem)BLItemRegistry.weedwoodShovel);
        recipes2.add((IManualEntryItem)BLItemRegistry.betweenstoneShovel);
        recipes2.add((IManualEntryItem)BLItemRegistry.octineShovel);
        recipes2.add((IManualEntryItem)BLItemRegistry.valoniteShovel);
        ArrayList<IManualEntryItem> recipes3 = new ArrayList<>();
        recipes3.add((IManualEntryItem)BLItemRegistry.weedwoodSword);
        recipes3.add((IManualEntryItem)BLItemRegistry.betweenstoneSword);
        recipes3.add((IManualEntryItem)BLItemRegistry.octineSword);
        recipes3.add((IManualEntryItem)BLItemRegistry.valoniteSword);
        ArrayList<ItemStack> recipes4 = new ArrayList<>();
        recipes4.add(new ItemStack(BLItemRegistry.anglerToothArrow));
        recipes4.add((new ItemStack(BLItemRegistry.octineArrow)));
        ArrayList<ItemStack> gems = new ArrayList<>();
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.AQUA_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.CRIMSON_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.GREEN_MIDDLE_GEM));


        entry1 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.text.test2")));
        //entry2 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.title"), new ItemWidget(manual, (127/2)-24, 77, arrows, 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.description")), new ManualPage(new CraftingRecipeWidget(manual, recipes4, 5, 10)));
        entry2 = new ManualEntryItem(gems, "gems", manual, new PurifierRecipeWidget(manual, gems, 5, 10));
        entry3 = new ManualEntryItem((IManualEntryItem)BLItemRegistry.testItem, manual);
        entry4 = new ManualEntryEntity(new EntityFirefly(manual.mc.theWorld), manual);
        entry5 = new ManualEntryItem(recipes4, "arrow", manual);
        entry6 = new ManualEntryItem("pickaxes", recipes, manual);
        entry7 = new ManualEntryItem("shovels", recipes2, manual);
        entry8 = new ManualEntryItem("swords", recipes3, manual);
    }

}
