package thebetweenlands.manual.gui.entries;

import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.PurifierRecipeWidget;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created on 11-8-2015.
 */
public class ManualEntryRegistry {

    public static IManualEntryItem[] pickaxes = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodPickaxe, (IManualEntryItem) BLItemRegistry.betweenstonePickaxe, (IManualEntryItem) BLItemRegistry.octinePickaxe, (IManualEntryItem) BLItemRegistry.valonitePickaxe};
    public static IManualEntryItem[] shovels = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodAxe, (IManualEntryItem) BLItemRegistry.betweenstoneAxe, (IManualEntryItem) BLItemRegistry.octineAxe, (IManualEntryItem) BLItemRegistry.valoniteAxe};
    public static IManualEntryItem[] axes = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodShovel, (IManualEntryItem) BLItemRegistry.betweenstoneShovel, (IManualEntryItem) BLItemRegistry.octineShovel, (IManualEntryItem) BLItemRegistry.valoniteShovel};
    public static IManualEntryItem[] swords = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodSword, (IManualEntryItem) BLItemRegistry.betweenstoneSword, (IManualEntryItem) BLItemRegistry.octineSword, (IManualEntryItem) BLItemRegistry.valoniteSword};
    public static IManualEntryItem[] records = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.astatos, (IManualEntryItem) BLItemRegistry.betweenYouAndMe, (IManualEntryItem) BLItemRegistry.christmasOnTheMarsh, (IManualEntryItem) BLItemRegistry.theExplorer, (IManualEntryItem) BLItemRegistry.hagDance, (IManualEntryItem) BLItemRegistry.lonelyFire, (IManualEntryItem) BLItemRegistry.mysteriousRecord, (IManualEntryItem) BLItemRegistry.ancient, (IManualEntryItem) BLItemRegistry.beneathAGreenSky, (IManualEntryItem) BLItemRegistry.dJWightsMixtape, (IManualEntryItem) BLItemRegistry.onwards, (IManualEntryItem) BLItemRegistry.stuckInTheMud, (IManualEntryItem) BLItemRegistry.wanderingWisps, (IManualEntryItem) BLItemRegistry.waterlogged};
    public static IManualEntryItem[] doors = new IManualEntryItem[]{(IManualEntryItem) BLBlockRegistry.doorSyrmorite, (IManualEntryItem) BLBlockRegistry.doorWeedwood};


    public static ArrayList<ManualEntryItem> itemEntries = new ArrayList<>();


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
        ArrayList<ItemStack> recipes4 = new ArrayList<>();
        recipes4.add(new ItemStack(BLItemRegistry.anglerToothArrow));
        recipes4.add((new ItemStack(BLItemRegistry.octineArrow)));
        ArrayList<ItemStack> gems = new ArrayList<>();
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.AQUA_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.CRIMSON_MIDDLE_GEM));
        gems.add(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.GREEN_MIDDLE_GEM));


        entry1 = new ManualEntry("Title", new ManualPage(new TextWidget(manual, 5, 5, "manual.text.test2")));
        //entry2 = new ManualEntry(new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.title"), new ItemWidget(manual, (127/2)-24, 77, arrows, 3)), new ManualPage(new TextWidget(manual, 5, 5, "manual.arrow.description")), new ManualPage(new CraftingRecipeWidget(manual, recipes4, 5, 10)));
        entry2 = new ManualEntryItem(gems, "gems", manual, new PurifierRecipeWidget(manual, gems, 5, 10));
        entry3 = new ManualEntryItem((IManualEntryItem) BLItemRegistry.testItem, manual);
        entry4 = new ManualEntryEntity(new EntityFirefly(manual.mc.theWorld), manual);
        entry5 = new ManualEntryItem(recipes4, "arrow", manual);

        initItemEntries(manual);
        entry9 = new ManualEntryEntryList("title", manual, itemEntries);
    }

    public static void initItemEntries(GuiManualBase manual) {
        itemEntries.clear();
        IManualEntryItem[] itemEntryItem = new IManualEntryItem[]{ (IManualEntryItem)BLBlockRegistry.weedwoodJukebox, (IManualEntryItem)BLItemRegistry.explorerHat, (IManualEntryItem)BLItemRegistry.ringOfPower, (IManualEntryItem)BLItemRegistry.voodooDoll, (IManualEntryItem)BLItemRegistry.testItem};
        for (IManualEntryItem item : itemEntryItem)
            itemEntries.add(new ManualEntryItem(item, manual));

        try {
            for (Field f : ManualEntryRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof IManualEntryItem[]) {
                    ArrayList<IManualEntryItem> list = new ArrayList<>();
                    Collections.addAll(list, (IManualEntryItem[]) obj);
                    itemEntries.add(new ManualEntryItem(f.getName(), list, manual));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO some way of sorting the entries by name
    }

}
