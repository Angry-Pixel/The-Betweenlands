package thebetweenlands.manual;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bart on 22/11/2015.
 */
public class ManualEntryRegistry {

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



    public static ArrayList<Page> itemPages = new ArrayList<>();
    //public static ArrayList<ManualEntry> entryLists = new ArrayList<>();
    //public static ArrayList<ManualEntryMachines> machines = new ArrayList<>();

    public static ManualCategory itemsCategory;

    public static void init(){
        initItemEntries();
    }

    public static void initItemEntries() {
        IManualEntryItem[] itemEntryItem = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.itemsGeneric, (IManualEntryItem) BLItemRegistry.itemsGenericCrushed, (IManualEntryItem) BLItemRegistry.weedwoodRowboat, (IManualEntryItem) BLItemRegistry.volarPad, (IManualEntryItem) BLItemRegistry.swampTalisman, (IManualEntryItem) BLItemRegistry.rope, (IManualEntryItem) BLItemRegistry.lifeCrystal, (IManualEntryItem) BLItemRegistry.weedwoodBow, (IManualEntryItem) BLItemRegistry.skullMask, (IManualEntryItem) BLBlockRegistry.weedwoodJukebox, (IManualEntryItem) BLItemRegistry.explorerHat, (IManualEntryItem) BLItemRegistry.ringOfPower, (IManualEntryItem) BLItemRegistry.voodooDoll, (IManualEntryItem) BLItemRegistry.tarminion, (IManualEntryItem) BLItemRegistry.shimmerStone, (IManualEntryItem) BLItemRegistry.angryPebble};

        itemPages.clear();
        for (IManualEntryItem item : itemEntryItem)
            itemPages.addAll(PageCreators.pageCreatorItems(item));
        try {
            for (Field f : ManualEntryRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof IManualEntryItem[]) {
                    ArrayList<IManualEntryItem> list = new ArrayList<>();
                    Collections.addAll(list, (IManualEntryItem[]) obj);
                    itemPages.addAll(PageCreators.pageCreatorItems(f.getName(), list));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Page> temp = new ArrayList<>();
        while (itemPages.size() > 0) {
            Page currentFirst = null;
            for (Page page : itemPages) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase().replace("<underline>", "").replace("</underline>", "");
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase().replace("<underline>", "").replace("</underline>", "");
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if(charactersFirst.length > i) {
                            if (((Character) characters[i]).compareTo(charactersFirst[i]) > 0) {
                                break;
                            } else if (((Character) characters[i]).compareTo(charactersFirst[i]) < 0) {
                                currentFirst = page;
                                break;
                            }
                        }
                    }
                }
            }
            itemPages.remove(currentFirst);
            temp.add(currentFirst);
        }
        itemPages.clear();
        itemPages.addAll(temp);

        itemsCategory = new ManualCategory(itemPages);
    }
}
