package thebetweenlands.manual;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 22/11/2015.
 */
public class GuideBookEntryRegistry {

    public static ArrayList<ManualCategory> CATEGORIES = new ArrayList<>();

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
    public static ArrayList<Page> entityPages = new ArrayList<>();
    public static ArrayList<Page> machines = new ArrayList<>();

    public static Item manualType = BLItemRegistry.manualGuideBook;

    public static ManualCategory itemsCategory;
    public static ManualCategory machineCategory;
    public static ManualCategory entitiesCategory;
    public static ManualCategory category4;
    public static ManualCategory category5;
    public static ManualCategory category6;
    public static ManualCategory category7;

    /**
     * initializes the Guide Book
     */
    public static void init() {
        initItemEntries();
        initMachineEntries();
        initEnityEntries();
        ArrayList<Page> page = new ArrayList<>();
        page.addAll(PageCreators.TextPages(16, 10, "manual.wip.page", "WIP", false, manualType));
        category4 = new ManualCategory(page, 4, manualType, "4");
        category5 = new ManualCategory(page, 5, manualType, "5");
        category6 = new ManualCategory(page, 6, manualType, "6");
        category7 = new ManualCategory(page, 7, manualType, "7");
    }

    /**
     * initializes the item pages
     */
    public static void initItemEntries() {
        IManualEntryItem[] itemEntryItem = new IManualEntryItem[]{(IManualEntryItem) BLItemRegistry.itemsGeneric, (IManualEntryItem) BLItemRegistry.itemsGenericCrushed, (IManualEntryItem) BLItemRegistry.weedwoodRowboat, (IManualEntryItem) BLItemRegistry.volarkite, (IManualEntryItem) BLItemRegistry.swampTalisman, (IManualEntryItem) BLItemRegistry.rope, (IManualEntryItem) BLItemRegistry.lifeCrystal, (IManualEntryItem) BLItemRegistry.weedwoodBow, (IManualEntryItem) BLItemRegistry.skullMask, (IManualEntryItem) BLBlockRegistry.weedwoodJukebox, (IManualEntryItem) BLItemRegistry.explorerHat, (IManualEntryItem) BLItemRegistry.ringOfPower, (IManualEntryItem) BLItemRegistry.voodooDoll, (IManualEntryItem) BLItemRegistry.tarminion, (IManualEntryItem) BLItemRegistry.shimmerStone, (IManualEntryItem) BLItemRegistry.angryPebble};

        itemPages.clear();
        for (IManualEntryItem item : itemEntryItem)
            itemPages.addAll(PageCreators.pageCreatorItems(item, false, manualType));
        try {
            for (Field f : GuideBookEntryRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof IManualEntryItem[]) {
                    ArrayList<IManualEntryItem> list = new ArrayList<>();
                    Collections.addAll(list, (IManualEntryItem[]) obj);
                    itemPages.addAll(PageCreators.pageCreatorItems(f.getName(), list, false, manualType));
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
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (charactersFirst.length > i) {
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

        itemsCategory = new ManualCategory(itemPages, 1, manualType, "itemCategory");
        CATEGORIES.add(itemsCategory);
    }

    /**
     * initializes the mechanic pages
     */
    public static void initMachineEntries() {
        machines.clear();
        machines.addAll(PageCreators.pageCreatorMachines("pestleAndMortar", new ItemStack(BLBlockRegistry.pestleAndMortar), "thebetweenlands:textures/gui/manual/pamGridExplanation.png", 106, 69, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("purifier", new ItemStack(BLBlockRegistry.purifier), "thebetweenlands:textures/gui/manual/purifierGridExplanation.png", 82, 58, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("sulfurFurnace", new ItemStack(BLBlockRegistry.furnaceBL), "thebetweenlands:textures/gui/manual/furnaceGridExplanation.png", 82, 54, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("druidAltar", new ItemStack(BLBlockRegistry.druidAltar), "thebetweenlands:textures/gui/manual/druidAltarGridExplanation.png", 74, 74, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("compostBin", new ItemStack(BLBlockRegistry.compostBin), "thebetweenlands:textures/gui/manual/compostExplanation.png", 89, 58, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("rubberTab", new ItemStack(BLBlockRegistry.rubberTreeLog), "thebetweenlands:textures/gui/manual/rubberTabExplanation.png", 89, 58, false, manualType));
        machines.addAll(PageCreators.pageCreatorMachines("animator", new ItemStack(BLBlockRegistry.animator), "thebetweenlands:textures/gui/manual/animatorGridExplanation.png", 108, 67, false, manualType));

        ArrayList<Page> temp = new ArrayList<>();
        while (machines.size() > 0) {
            Page currentFirst = null;
            for (Page page : machines) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (charactersFirst.length > i) {
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
            machines.remove(currentFirst);
            temp.add(currentFirst);
        }
        machines.clear();
        machines.addAll(temp);

        machineCategory = new ManualCategory(machines, 2, manualType, "machineCategory");
        CATEGORIES.add(machineCategory);
    }

    /**
     * initializes the entity pages
     */
    public static void initEnityEntries() {
        IManualEntryEntity[] manualEntryEntities = new IManualEntryEntity[]{new IManualEntryEntity("angler", 110, 128, 10, -1), new IManualEntryEntity("berserkerGuardian", 110, 128, -1, -1), new IManualEntryEntity("blindCaveFish", 110, 128, -1, -1), new IManualEntryEntity("bloodSnail", 110, 128, -1, -1), new IManualEntryEntity("darkDruid", 110, 128, -1, -1), new IManualEntryEntity("dragonFly", 110, 128, -1, -1), new IManualEntryEntity("firefly", 110, 128, -1, -1), new IManualEntryEntity("gecko", 110, 128, -1, -1), new IManualEntryEntity("giantToad", 110, 128, -1, -1), new IManualEntryEntity("leech", 110, 128, -1, -1), new IManualEntryEntity("lurker", 110, 128, -1, -1), new IManualEntryEntity("meleeGuardian", 110, 128, -1, -1), new IManualEntryEntity("mireSnail", 110, 128, -1, -1), new IManualEntryEntity("mireSnailEgg", 110, 128, -1, -1), new IManualEntryEntity("peatMummy", 110, 128, -1, -1), new IManualEntryEntity("siltCrab", 110, 128, -1, -1), new IManualEntryEntity("sludge", 110, 128, -1, -1), new IManualEntryEntity("sporeling", 110, 128, -1, -1), new IManualEntryEntity("swampHag", 110, 128, -1, -1), new IManualEntryEntity("tarBeast", 110, 128, -1, -1), new IManualEntryEntity("tarminion", 110, 128, -1, -1), new IManualEntryEntity("termite", 110, 128, -1, -1), new IManualEntryEntity("wight", 110, 128, -1, -1)};

        entityPages.clear();
        for (IManualEntryEntity entity : manualEntryEntities) {
            entityPages.addAll(PageCreators.pageCreatorEntities(entity, true, manualType));
        }
        ArrayList<Page> temp = new ArrayList<>();
        while (entityPages.size() > 0) {
            Page currentFirst = null;
            for (Page page : entityPages) {
                if (currentFirst == null)
                    currentFirst = page;
                else {
                    String pageName = page.pageName.toLowerCase();
                    char[] characters = pageName.toCharArray();
                    String pageNameFirst = currentFirst.pageName.toLowerCase();
                    char[] charactersFirst = pageNameFirst.toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (charactersFirst.length > i) {
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
            entityPages.remove(currentFirst);
            temp.add(currentFirst);
        }
        entityPages.clear();
        entityPages.addAll(temp);

        entitiesCategory = new ManualCategory(entityPages, 3, manualType, "entitiesCategory");
        CATEGORIES.add(entitiesCategory);
    }
}
