package thebetweenlands.manual.gui.entries;

import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
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
    public static ArrayList<ManualEntry> ENTRIES = new ArrayList<>();

    public static IManualEntryItem[] pickaxes = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodPickaxe, (IManualEntryItem) BLItemRegistry.betweenstonePickaxe, (IManualEntryItem) BLItemRegistry.octinePickaxe, (IManualEntryItem) BLItemRegistry.valonitePickaxe};
    public static IManualEntryItem[] axes = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodAxe, (IManualEntryItem) BLItemRegistry.betweenstoneAxe, (IManualEntryItem) BLItemRegistry.octineAxe, (IManualEntryItem) BLItemRegistry.valoniteAxe};
    public static IManualEntryItem[] shovels = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodShovel, (IManualEntryItem) BLItemRegistry.betweenstoneShovel, (IManualEntryItem) BLItemRegistry.octineShovel, (IManualEntryItem) BLItemRegistry.valoniteShovel};
    public static IManualEntryItem[] swords = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodSword, (IManualEntryItem) BLItemRegistry.betweenstoneSword, (IManualEntryItem) BLItemRegistry.octineSword, (IManualEntryItem) BLItemRegistry.valoniteSword};
    public static IManualEntryItem[] records = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.astatos, (IManualEntryItem) BLItemRegistry.betweenYouAndMe, (IManualEntryItem) BLItemRegistry.christmasOnTheMarsh, (IManualEntryItem) BLItemRegistry.theExplorer, (IManualEntryItem) BLItemRegistry.hagDance, (IManualEntryItem) BLItemRegistry.lonelyFire, (IManualEntryItem) BLItemRegistry.mysteriousRecord, (IManualEntryItem) BLItemRegistry.ancient, (IManualEntryItem) BLItemRegistry.beneathAGreenSky, (IManualEntryItem) BLItemRegistry.dJWightsMixtape, (IManualEntryItem) BLItemRegistry.onwards, (IManualEntryItem) BLItemRegistry.stuckInTheMud, (IManualEntryItem) BLItemRegistry.wanderingWisps, (IManualEntryItem) BLItemRegistry.waterlogged};
    public static IManualEntryItem[] doors = new IManualEntryItem[]{(IManualEntryItem) BLBlockRegistry.doorSyrmorite, (IManualEntryItem) BLBlockRegistry.doorWeedwood};
    public static IManualEntryItem[] arrows = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.anglerToothArrow, (IManualEntryItem) BLItemRegistry.basiliskArrow, (IManualEntryItem) BLItemRegistry.octineArrow, (IManualEntryItem) BLItemRegistry.poisonedAnglerToothArrow};
    public static IManualEntryItem[] buckets = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.weedwoodBucket, (IManualEntryItem) BLItemRegistry.weedwoodBucketTar, (IManualEntryItem) BLItemRegistry.weedwoodBucketWater, (IManualEntryItem) BLItemRegistry.weedwoodBucketStagnantWater, (IManualEntryItem) BLItemRegistry.weedwoodBucketRubber};
    public static IManualEntryItem[] food = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.blackHatMushroomItem, (IManualEntryItem) BLItemRegistry.bulbCappedMushroomItem, (IManualEntryItem) BLItemRegistry.flatheadMushroomItem, (IManualEntryItem) BLItemRegistry.sapBall, (IManualEntryItem) BLItemRegistry.rottenFood, (IManualEntryItem) BLItemRegistry.anglerMeatRaw, (IManualEntryItem) BLItemRegistry.anglerMeatCooked, (IManualEntryItem) BLItemRegistry.frogLegsRaw, (IManualEntryItem) BLItemRegistry.frogLegsCooked, (IManualEntryItem) BLItemRegistry.snailFleshRaw, (IManualEntryItem) BLItemRegistry.snailFleshCooked, (IManualEntryItem) BLItemRegistry.reedDonut, (IManualEntryItem) BLItemRegistry.jamDonut, (IManualEntryItem) BLItemRegistry.gertsDonut, (IManualEntryItem) BLItemRegistry.krakenCalamari, (IManualEntryItem) BLItemRegistry.krakenTentacle, (IManualEntryItem) BLItemRegistry.middleFruit, (IManualEntryItem) BLItemRegistry.mincePie, (IManualEntryItem) BLItemRegistry.weepingBluePetal, (IManualEntryItem) BLItemRegistry.wightsHeart, (IManualEntryItem) BLItemRegistry.yellowDottedFungus, (IManualEntryItem) BLItemRegistry.siltCrabClaw, (IManualEntryItem) BLItemRegistry.crabstick, (IManualEntryItem) BLItemRegistry.nettleSoup, (IManualEntryItem) BLItemRegistry.sludgeJello, (IManualEntryItem) BLItemRegistry.middleFruitJello, (IManualEntryItem) BLItemRegistry.sapJello, (IManualEntryItem) BLItemRegistry.marshmallow, (IManualEntryItem) BLItemRegistry.marshmallowPink, (IManualEntryItem) BLItemRegistry.friedSwampKelp, (IManualEntryItem) BLItemRegistry.forbiddenFig, (IManualEntryItem) BLItemRegistry.candyBlue, (IManualEntryItem) BLItemRegistry.candyRed, (IManualEntryItem) BLItemRegistry.candyYellow};

    //armor
    public static IManualEntryItem[] boneArmor = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.boneBoots, (IManualEntryItem) BLItemRegistry.boneChestplate, (IManualEntryItem) BLItemRegistry.boneHelmet, (IManualEntryItem) BLItemRegistry.boneLeggings};
    public static IManualEntryItem[] legendaryArmor = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.legendaryBoots, (IManualEntryItem) BLItemRegistry.legendaryChestplate, (IManualEntryItem) BLItemRegistry.legendaryHelmet, (IManualEntryItem) BLItemRegistry.legendaryLeggings};
    public static IManualEntryItem[] lurkerArmor = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.lurkerSkinBoots, (IManualEntryItem) BLItemRegistry.lurkerSkinChestplate, (IManualEntryItem) BLItemRegistry.lurkerSkinHelmet, (IManualEntryItem) BLItemRegistry.lurkerSkinLeggings};
    public static IManualEntryItem[] syrmoriteArmor = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.syrmoriteBoots, (IManualEntryItem) BLItemRegistry.syrmoriteChestplate, (IManualEntryItem) BLItemRegistry.syrmoriteHelmet, (IManualEntryItem) BLItemRegistry.syrmoriteLeggings};
    public static IManualEntryItem[] valoniteArmor = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.valoniteBoots, (IManualEntryItem) BLItemRegistry.valoniteChestplate, (IManualEntryItem) BLItemRegistry.valoniteHelmet, (IManualEntryItem) BLItemRegistry.valoniteLeggings};
    public static IManualEntryItem[] rubberBoots = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.rubberBoots, (IManualEntryItem) BLItemRegistry.rubberBootsImproved};


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
        entry = new ManualEntryEntryList("title", manual, itemEntries);

        initEntryList();
    }

    public static void initItemEntries(GuiManualBase manual) {
        itemEntries.clear();
        IManualEntryItem[] itemEntryItem = new IManualEntryItem[]{(IManualEntryItem)BLItemRegistry.itemsGeneric, (IManualEntryItem)BLItemRegistry.itemsGenericCrushed, (IManualEntryItem) BLItemRegistry.weedwoodRowboat, (IManualEntryItem) BLItemRegistry.volarPad, (IManualEntryItem) BLItemRegistry.swampTalisman, (IManualEntryItem) BLItemRegistry.rope, (IManualEntryItem) BLItemRegistry.lifeCrystal, (IManualEntryItem) BLItemRegistry.weedwoodBow, (IManualEntryItem) BLItemRegistry.skullMask, (IManualEntryItem) BLBlockRegistry.weedwoodJukebox, (IManualEntryItem) BLItemRegistry.explorerHat, (IManualEntryItem) BLItemRegistry.ringOfPower, (IManualEntryItem) BLItemRegistry.voodooDoll, (IManualEntryItem) BLItemRegistry.tarminion, (IManualEntryItem) BLItemRegistry.shimmerStone, (IManualEntryItem) BLItemRegistry.angryPebble};
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

        ArrayList<ManualEntryItem> temp = new ArrayList<>();
        while (itemEntries.size() > 0) {
            ManualEntryItem currentFirst = null;
            for (ManualEntryItem entry : itemEntries) {
                if (currentFirst == null)
                    currentFirst = entry;
                else {
                    String entryName = entry.entryName.toLowerCase().replace("<underline>", "").replace("</underline>", "");
                    char[] characters = entryName.toCharArray();
                    String entryNameFirst = currentFirst.entryName.toLowerCase().replace("<underline>", "").replace("</underline>", "");
                    char[] charactersFirst = entryNameFirst.toCharArray();
                    for (int i = 0; 0 < characters.length; i++) {
                        if(charactersFirst.length > i) {
                            if (((Character) characters[i]).compareTo(charactersFirst[i]) > 0) {
                                break;
                            } else if (((Character) characters[i]).compareTo(charactersFirst[i]) < 0) {
                                currentFirst = entry;
                                break;
                            }
                        }
                    }
                }
            }
            itemEntries.remove(currentFirst);
            temp.add(currentFirst);
        }
        itemEntries.clear();
        itemEntries.addAll(temp);
    }

    public static void initEntryList() {
        ENTRIES.addAll(itemEntries);
        try {
            for (Field f : ManualEntryRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof ManualEntry) {
                    ENTRIES.add((ManualEntry) obj);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
