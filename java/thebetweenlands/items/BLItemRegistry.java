package thebetweenlands.items;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import thebetweenlands.entities.mobs.EntityDragonFly;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityLeech;
import thebetweenlands.entities.mobs.EntityMireSnail;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTermite;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.armor.ItemArmorOfLegends;
import thebetweenlands.items.armor.ItemBoneArmor;
import thebetweenlands.items.armor.ItemImprovedRubberBoots;
import thebetweenlands.items.armor.ItemLurkerSkinArmor;
import thebetweenlands.items.armor.ItemRubberBoots;
import thebetweenlands.items.armor.ItemSkullMask;
import thebetweenlands.items.armor.ItemSyrmoriteArmor;
import thebetweenlands.items.armor.ItemValoniteArmor;
import thebetweenlands.items.block.ItemBLDoor;
import thebetweenlands.items.bow.EnumArrowType;
import thebetweenlands.items.bow.ItemBLArrow;
import thebetweenlands.items.bow.ItemWeedwoodBow;
import thebetweenlands.items.crops.ItemAspectrusCropSeed;
import thebetweenlands.items.crops.ItemBLGenericSeed;
import thebetweenlands.items.equipment.ItemAmulet;
import thebetweenlands.items.equipment.ItemLurkerSkinPouch;
import thebetweenlands.items.equipment.ItemRingOfFlight;
import thebetweenlands.items.equipment.ItemRingOfPower;
import thebetweenlands.items.equipment.ItemRingOfRecruitment;
import thebetweenlands.items.equipment.ItemRingOfSummoning;
import thebetweenlands.items.food.ItemBLFood;
import thebetweenlands.items.food.ItemBlackHatMushroom;
import thebetweenlands.items.food.ItemBulbCappedMushroom;
import thebetweenlands.items.food.ItemChiromawWing;
import thebetweenlands.items.food.ItemFlatheadMushroom;
import thebetweenlands.items.food.ItemForbiddenFig;
import thebetweenlands.items.food.ItemGertsDonut;
import thebetweenlands.items.food.ItemMarshmallow;
import thebetweenlands.items.food.ItemMarshmallowPink;
import thebetweenlands.items.food.ItemNettleSoup;
import thebetweenlands.items.food.ItemRottenFood;
import thebetweenlands.items.food.ItemSapBall;
import thebetweenlands.items.food.ItemSapJello;
import thebetweenlands.items.food.ItemWeepingBluePetal;
import thebetweenlands.items.food.ItemWightHeart;
import thebetweenlands.items.herblore.ItemAspectVial;
import thebetweenlands.items.herblore.ItemDentrothystVial;
import thebetweenlands.items.herblore.ItemElixir;
import thebetweenlands.items.herblore.ItemGenericCrushed;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.loot.ItemExplorerHat;
import thebetweenlands.items.loot.ItemLore;
import thebetweenlands.items.loot.ItemVoodooDoll;
import thebetweenlands.items.misc.ItemAmuletSlot;
import thebetweenlands.items.misc.ItemBLRecord;
import thebetweenlands.items.misc.ItemBLSign;
import thebetweenlands.items.misc.ItemCavingRope;
import thebetweenlands.items.misc.ItemGem;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.misc.ItemHLBook;
import thebetweenlands.items.misc.ItemLifeCrystal;
import thebetweenlands.items.misc.ItemManual;
import thebetweenlands.items.misc.ItemMob;
import thebetweenlands.items.misc.ItemMossBed;
import thebetweenlands.items.misc.ItemRope;
import thebetweenlands.items.misc.ItemShockwaveSword;
import thebetweenlands.items.misc.ItemSpawnEggs;
import thebetweenlands.items.misc.ItemSummonMummy;
import thebetweenlands.items.misc.ItemSwampTalisman;
import thebetweenlands.items.misc.ItemTestItem;
import thebetweenlands.items.misc.ItemTestItem2;
import thebetweenlands.items.misc.ItemVolarkite;
import thebetweenlands.items.misc.ItemWeedwoodRowboat;
import thebetweenlands.items.throwable.ItemAngryPebble;
import thebetweenlands.items.throwable.ItemShimmerStone;
import thebetweenlands.items.throwable.ItemTarminion;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemLootSword;
import thebetweenlands.items.tools.ItemNet;
import thebetweenlands.items.tools.ItemPestle;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSickle;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwiftPick;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.items.tools.ItemSyrmoriteShears;
import thebetweenlands.items.tools.ItemWeedwoodBucket;
import thebetweenlands.items.tools.ItemWeedwoodBucketInfusion;
import thebetweenlands.items.tools.ItemWeedwoodBucketRubber;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;


public class BLItemRegistry {
	public static final List<Item> ITEMS = new ArrayList<Item>();
	// BASIC MATERIALS
	public static final Item testItem = new ItemTestItem().setUnlocalizedName("thebetweenlands.testItem").setTextureName("thebetweenlands:testItem");
	public static final Item testItem2 = new ItemTestItem2().setUnlocalizedName("thebetweenlands.testItem2").setTextureName("thebetweenlands:testItem");
	public static final Item swampTalisman = new ItemSwampTalisman();
	public static final Item itemsGeneric = new ItemGeneric();
	public static final Item itemsGenericCrushed = new ItemGenericCrushed().setUnlocalizedName("thebetweenlands.groundStuff");
	public static final Item itemsGenericPlantDrop = new ItemGenericPlantDrop().setUnlocalizedName("thebetweenlands.plantDrop");
	public static final Item lifeCrystal = new ItemLifeCrystal().setUnlocalizedName("thebetweenlands.lifeCrystal").setTextureName("thebetweenlands:lifeCrystal");
	// WEAPONS TOOLS
	public static final Item weedwoodSword = new ItemSwordBL(BLMaterial.toolWeedWood)
			.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:weedWoodSwordCrimson")
			.setGemTextures(CircleGem.AQUA, "thebetweenlands:weedWoodSwordAqua")
			.setGemTextures(CircleGem.GREEN, "thebetweenlands:weedWoodSwordGreen").setUnlocalizedName("thebetweenlands.weedwoodSword").setTextureName("thebetweenlands:weedWoodSword");
	public static final Item weedwoodPickaxe = new ItemPickaxeBL(BLMaterial.toolWeedWood).setUnlocalizedName("thebetweenlands.weedwoodPickaxe").setTextureName("thebetweenlands:weedWoodPickaxe");
	public static final Item weedwoodAxe = new ItemAxeBL(BLMaterial.toolWeedWood).setUnlocalizedName("thebetweenlands.weedwoodAxe").setTextureName("thebetweenlands:weedWoodAxe");
	public static final Item weedwoodShovel = new ItemSpadeBL(BLMaterial.toolWeedWood).setUnlocalizedName("thebetweenlands.weedwoodShovel").setTextureName("thebetweenlands:weedWoodShovel");
	public static final Item weedwoodBow = new ItemWeedwoodBow().setUnlocalizedName("thebetweenlands.weedwoodBow").setTextureName("thebetweenlands:weedwoodBow");
	public static final Item anglerToothArrow = new ItemBLArrow("anglerToothArrow", EnumArrowType.DEFAULT).setUnlocalizedName("thebetweenlands.anglerToothArrow");
	public static final Item poisonedAnglerToothArrow = new ItemBLArrow("poisonedAnglerToothArrow", EnumArrowType.ANGLER_POISON).setUnlocalizedName("thebetweenlands.poisonedAnglerToothArrow");
	public static final Item octineArrow = new ItemBLArrow("octineArrow", EnumArrowType.OCTINE).setUnlocalizedName("thebetweenlands.octineArrow");
	public static final Item basiliskArrow = new ItemBLArrow("basiliskArrow", EnumArrowType.BASILISK).setUnlocalizedName("thebetweenlands.basiliskArrow");
	public static final Item betweenstoneSword = new ItemSwordBL(BLMaterial.toolBetweenstone)
			.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:betweenstoneSwordCrimson")
			.setGemTextures(CircleGem.AQUA, "thebetweenlands:betweenstoneSwordAqua")
			.setGemTextures(CircleGem.GREEN, "thebetweenlands:betweenstoneSwordGreen").setUnlocalizedName("thebetweenlands.betweenstoneSword").setTextureName("thebetweenlands:betweenstoneSword");
	public static final Item betweenstonePickaxe = new ItemPickaxeBL(BLMaterial.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstonePickaxe").setTextureName("thebetweenlands:betweenstonePickaxe");
	public static final Item betweenstoneAxe = new ItemAxeBL(BLMaterial.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstoneAxe").setTextureName("thebetweenlands:betweenstoneAxe");
	public static final Item betweenstoneShovel = new ItemSpadeBL(BLMaterial.toolBetweenstone).setUnlocalizedName("thebetweenlands.betweenstoneShovel").setTextureName("thebetweenlands:betweenstoneShovel");
	public static final Item octineSword = new ItemSwordBL(BLMaterial.toolOctine)
			.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:octineSwordCrimson")
			.setGemTextures(CircleGem.AQUA, "thebetweenlands:octineSwordAqua")
			.setGemTextures(CircleGem.GREEN, "thebetweenlands:octineSwordGreen").setUnlocalizedName("thebetweenlands.octineSword").setTextureName("thebetweenlands:octineSword");
	public static final Item octinePickaxe = new ItemPickaxeBL(BLMaterial.toolOctine).setUnlocalizedName("thebetweenlands.octinePickaxe").setTextureName("thebetweenlands:octinePickaxe");
	public static final Item octineAxe = new ItemAxeBL(BLMaterial.toolOctine).setUnlocalizedName("thebetweenlands.octineAxe").setTextureName("thebetweenlands:octineAxe");
	public static final Item octineShovel = new ItemSpadeBL(BLMaterial.toolOctine).setUnlocalizedName("thebetweenlands.octineShovel").setTextureName("thebetweenlands:octineShovel");
	public static final Item valoniteSword = new ItemSwordBL(BLMaterial.toolValonite)
			.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:valoniteSwordCrimson")
			.setGemTextures(CircleGem.AQUA, "thebetweenlands:valoniteSwordAqua")
			.setGemTextures(CircleGem.GREEN, "thebetweenlands:valoniteSwordGreen").setUnlocalizedName("thebetweenlands.valoniteSword").setTextureName("thebetweenlands:valoniteSword");
	public static final Item valonitePickaxe = new ItemPickaxeBL(BLMaterial.toolValonite).setUnlocalizedName("thebetweenlands.valonitePickaxe").setTextureName("thebetweenlands:valonitePickaxe");
	public static final Item valoniteAxe = new ItemAxeBL(BLMaterial.toolValonite).setUnlocalizedName("thebetweenlands.valoniteAxe").setTextureName("thebetweenlands:valoniteAxe");
	public static final Item valoniteShovel = new ItemSpadeBL(BLMaterial.toolValonite).setUnlocalizedName("thebetweenlands.valoniteShovel").setTextureName("thebetweenlands:valoniteShovel");
	public static final Item weedwoodBucket = new ItemWeedwoodBucket();
	public static final Item weedwoodBucketTar = new ItemWeedwoodBucket(BLBlockRegistry.tarFluid).setUnlocalizedName("thebetweenlands.weedwoodBucketTar").setTextureName("thebetweenlands:weedwoodBucketTar");
	public static final Item weedwoodBucketWater = new ItemWeedwoodBucket(BLBlockRegistry.swampWater).setUnlocalizedName("thebetweenlands.weedwoodBucketWater").setTextureName("thebetweenlands:weedwoodBucketWater");
	public static final Item weedwoodBucketStagnantWater = new ItemWeedwoodBucket(BLBlockRegistry.stagnantWaterFluid).setUnlocalizedName("thebetweenlands.weedwoodBucketStagnantWater").setTextureName("thebetweenlands:weedwoodBucketStagnantWater");
	public static final Item legendarySword = new ItemSwordBL(BLMaterial.toolOfLegends).setUnlocalizedName("thebetweenlands.legendarySword").setTextureName("thebetweenlands:legendarySword");
	public static final Item sickle = new ItemSickle();
	public static final Item syrmoriteShears = new ItemSyrmoriteShears();
	public static final Item net = new ItemNet();
	public static final Item shockwaveSword = new ItemShockwaveSword(BLMaterial.toolValonite);
	// GEMS
	public static final ItemGem aquaMiddleGem = new ItemGem("aquaMiddleGem", CircleGem.AQUA);
	public static final ItemGem crimsonMiddleGem = new ItemGem("crimsonMiddleGem", CircleGem.CRIMSON);
	public static final ItemGem greenMiddleGem = new ItemGem("greenMiddleGem", CircleGem.GREEN);
	// MISC ARMOUR
	public static final Item lurkerSkinHelmet = new ItemLurkerSkinArmor(0).setUnlocalizedName("thebetweenlands.lurkerSkinHelmet");
	public static final Item lurkerSkinChestplate = new ItemLurkerSkinArmor(1).setUnlocalizedName("thebetweenlands.lurkerSkinChestplate");
	public static final Item lurkerSkinLeggings = new ItemLurkerSkinArmor(2).setUnlocalizedName("thebetweenlands.lurkerSkinLeggings");
	public static final Item lurkerSkinBoots = new ItemLurkerSkinArmor(3).setUnlocalizedName("thebetweenlands.lurkerSkinBoots");
	public static final Item boneHelmet = new ItemBoneArmor(0).setUnlocalizedName("thebetweenlands.boneHelmet");
	public static final Item boneChestplate = new ItemBoneArmor(1).setUnlocalizedName("thebetweenlands.boneChestplate");
	public static final Item boneLeggings = new ItemBoneArmor(2).setUnlocalizedName("thebetweenlands.boneLeggings");
	public static final Item boneBoots = new ItemBoneArmor(3).setUnlocalizedName("thebetweenlands.boneBoots");
	public static final Item syrmoriteHelmet = new ItemSyrmoriteArmor(0).setUnlocalizedName("thebetweenlands.syrmoriteHelmet");
	public static final Item syrmoriteChestplate = new ItemSyrmoriteArmor(1).setUnlocalizedName("thebetweenlands.syrmoriteChestplate");
	public static final Item syrmoriteLeggings = new ItemSyrmoriteArmor(2).setUnlocalizedName("thebetweenlands.syrmoriteLeggings");
	public static final Item syrmoriteBoots = new ItemSyrmoriteArmor(3).setUnlocalizedName("thebetweenlands.syrmoriteBoots");
	public static final Item legendaryHelmet = new ItemArmorOfLegends(0).setMaxDamage(-1).setUnlocalizedName("thebetweenlands.legendaryHelmet").setTextureName("thebetweenlands:legendaryHelmet");
	public static final Item legendaryChestplate = new ItemArmorOfLegends(1).setMaxDamage(-1).setUnlocalizedName("thebetweenlands.legendaryChestplate").setTextureName("thebetweenlands:legendaryChestplate");
	public static final Item legendaryLeggings = new ItemArmorOfLegends(2).setMaxDamage(-1).setUnlocalizedName("thebetweenlands.legendaryLeggings").setTextureName("thebetweenlands:legendaryLeggings");
	public static final Item legendaryBoots = new ItemArmorOfLegends(3).setMaxDamage(-1).setUnlocalizedName("thebetweenlands.legendaryBoots").setTextureName("thebetweenlands:legendaryBoots");
	public static final Item valoniteHelmet = new ItemValoniteArmor(0).setUnlocalizedName("thebetweenlands.valoniteHelmet");
	public static final Item valoniteChestplate = new ItemValoniteArmor(1).setUnlocalizedName("thebetweenlands.valoniteChestplate");
	public static final Item valoniteLeggings = new ItemValoniteArmor(2).setUnlocalizedName("thebetweenlands.valoniteLeggings");
	public static final Item valoniteBoots = new ItemValoniteArmor(3).setUnlocalizedName("thebetweenlands.valoniteBoots");
	public static final Item rubberBoots = new ItemRubberBoots().setUnlocalizedName("thebetweenlands.rubberBoots").setTextureName("thebetweenlands:rubberBoots");
	public static final Item rubberBootsImproved = new ItemImprovedRubberBoots().setUnlocalizedName("thebetweenlands.rubberBootsImproved").setTextureName("thebetweenlands:rubberBoots");
	// CREATIVE
	public static final Item spawnEggs = new ItemSpawnEggs().setUnlocalizedName("thebetweenlands.monsterPlacer").setTextureName("spawn_egg");
	//FOOD
	public static final Item sapBall = new ItemSapBall().setUnlocalizedName("thebetweenlands.sapBall").setTextureName("thebetweenlands:sapBall");
	public static final ItemRottenFood rottenFood = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible().setUnlocalizedName("thebetweenlands.rottenFood").setTextureName("thebetweenlands:rottenFood");
	public static final Item middleFruitSeeds = new ItemBLGenericSeed(BLBlockRegistry.middleFruitBush, BLBlockRegistry.farmedDirt).setUnlocalizedName("thebetweenlands.middleFruitSeeds").setTextureName("thebetweenlands:middleFruitSeeds");
	public static final Item spores = new ItemBLGenericSeed(BLBlockRegistry.fungusCrop, BLBlockRegistry.farmedDirt).setUnlocalizedName("thebetweenlands.spores").setTextureName("thebetweenlands:spores");
	public static final Item aspectrusCropSeed = new ItemAspectrusCropSeed().setUnlocalizedName("thebetweenlands.aspectrusSeeds").setTextureName("thebetweenlands:aspectrusSeeds");
	public static final Item anglerMeatRaw = new ItemBLFood(4, 0.4F, false).setUnlocalizedName("thebetweenlands.anglerMeatRaw").setTextureName("thebetweenlands:anglerFlesh");
	public static final Item anglerMeatCooked = new ItemBLFood(8, 0.8F, false).setUnlocalizedName("thebetweenlands.anglerMeatCooked").setTextureName("thebetweenlands:cookedAnglerFlesh");
	public static final Item frogLegsRaw = new ItemBLFood(3, 0.4F, false).setUnlocalizedName("thebetweenlands.frogLegsRaw").setTextureName("thebetweenlands:frogLegs");
	public static final Item frogLegsCooked = new ItemBLFood(6, 0.8F, false).setUnlocalizedName("thebetweenlands.frogLegsCooked").setTextureName("thebetweenlands:frogLegsCooked");
	public static final Item snailFleshRaw = new ItemBLFood(3, 0.4F, false).setUnlocalizedName("thebetweenlands.snailFleshRaw").setTextureName("thebetweenlands:snailMeat");
	public static final Item snailFleshCooked = new ItemBLFood(6, 0.9F, false).setUnlocalizedName("thebetweenlands.snailFleshCooked").setTextureName("thebetweenlands:cookedSnailMeat");
	public static final Item reedDonut = new ItemBLFood(6, 0.6F, false).setUnlocalizedName("thebetweenlands.reedDonut").setTextureName("thebetweenlands:donut");
	public static final Item jamDonut = new ItemBLFood(10, 0.6F, false).setUnlocalizedName("thebetweenlands.jamDonut").setTextureName("thebetweenlands:jamDonut");
	public static final Item gertsDonut = new ItemGertsDonut().setUnlocalizedName("thebetweenlands.gertsDonut").setTextureName("thebetweenlands:gertsDonut");
	public static final Item krakenTentacle = new ItemBLFood(8, 0.9F, false).setUnlocalizedName("thebetweenlands.krakenTentacle").setTextureName("thebetweenlands:krakenTentacle");
	public static final Item krakenCalamari = new ItemBLFood(14, 1.2F, false).setUnlocalizedName("thebetweenlands.krakenCalamari").setTextureName("thebetweenlands:krakenCalamari");
	public static final Item middleFruit = new ItemBLFood(6, 0.6F, false).setUnlocalizedName("thebetweenlands.middleFruit").setTextureName("thebetweenlands:middleFruit");
	public static final Item mincePie = new ItemBLFood(4, 0.85F, false).setUnlocalizedName("thebetweenlands.mincePie").setTextureName("thebetweenlands:mincePie");
	public static final Item weepingBluePetal = new ItemWeepingBluePetal().setUnlocalizedName("thebetweenlands.weepingBluePetal").setTextureName("thebetweenlands:weepingBluePetal");
	public static final Item wightsHeart = new ItemWightHeart().setUnlocalizedName("thebetweenlands.wightHeart").setTextureName("thebetweenlands:wightHeart");
	public static final Item yellowDottedFungus = new ItemBLFood(8, 0.6F, false).setUnlocalizedName("thebetweenlands.yellowDottedFungus").setTextureName("thebetweenlands:yellowDottedFungus");
	public static final Item siltCrabClaw = new ItemBLFood(2, 0.6F, false).setUnlocalizedName("thebetweenlands.siltCrabClaw").setTextureName("thebetweenlands:siltCrabClaw");
	public static final Item crabstick = new ItemBLFood(6, 0.9F, false).setUnlocalizedName("thebetweenlands.crabstick").setTextureName("thebetweenlands:crabStick");
	public static final Item nettleSoup = new ItemNettleSoup().setUnlocalizedName("thebetweenlands.nettleSoup").setTextureName("thebetweenlands:nettleSoup");
	public static final Item sludgeJello = new ItemBLFood(4, 0.9F, false).setUnlocalizedName("thebetweenlands.sludgeJello").setTextureName("thebetweenlands:sludgeJello");
	public static final Item middleFruitJello = new ItemBLFood(10, 1.0F, false).setUnlocalizedName("thebetweenlands.middleFruitJello").setTextureName("thebetweenlands:middleFruitJello");
	public static final Item sapJello = new ItemSapJello().setUnlocalizedName("thebetweenlands.sapJello").setTextureName("thebetweenlands:sapJello");
	public static final Item marshmallow = new ItemMarshmallow().setUnlocalizedName("thebetweenlands.marshmallow").setTextureName("thebetweenlands:greenMarshmallow");
	public static final Item marshmallowPink = new ItemMarshmallowPink().setUnlocalizedName("thebetweenlands.marshmallowPink").setTextureName("thebetweenlands:pinkMarshmallow");
	public static final Item flatheadMushroomItem = new ItemFlatheadMushroom().setUnlocalizedName("thebetweenlands.flatheadMushroomItem").setTextureName("thebetweenlands:flatheadMushroom");
	public static final Item blackHatMushroomItem = new ItemBlackHatMushroom().setUnlocalizedName("thebetweenlands.blackHatMushroomItem").setTextureName("thebetweenlands:blackHatMushroom");
	public static final Item bulbCappedMushroomItem = new ItemBulbCappedMushroom().setUnlocalizedName("thebetweenlands.bulbCappedMushroomItem").setTextureName("thebetweenlands:bulbCappedMushroom");
	public static final Item friedSwampKelp = new ItemBLFood(5, 0.6F, false).setUnlocalizedName("thebetweenlands.friedSwampKelp").setTextureName("thebetweenlands:friedSwampKelp");
	public static final Item forbiddenFig = new ItemForbiddenFig().setUnlocalizedName("thebetweenlands.forbiddenFig").setTextureName("thebetweenlands:forbiddenFig");
	public static final Item candyBlue = new ItemBLFood(3, 1.0F, false).setUnlocalizedName("thebetweenlands.candyBlue").setTextureName("thebetweenlands:sweetBlue");
	public static final Item candyRed = new ItemBLFood(3, 1.0F, false).setUnlocalizedName("thebetweenlands.candyRed").setTextureName("thebetweenlands:sweetRed");
	public static final Item candyYellow = new ItemBLFood(3, 1.0F, false).setUnlocalizedName("thebetweenlands.candyYellow").setTextureName("thebetweenlands:sweetYellow");
	public static final Item chiromawWing = new ItemChiromawWing();
	//DOORS
	public static final Item doorWeedwood = new ItemBLDoor(BLBlockRegistry.doorWeedwood);
	public static final Item doorRubber = new ItemBLDoor(BLBlockRegistry.doorRubber);
	public static final Item doorSyrmorite = new ItemBLDoor(BLBlockRegistry.doorSyrmorite);
	//MISC
	public static final ItemShimmerStone shimmerStone = new ItemShimmerStone();
	public static final Item angryPebble = new ItemAngryPebble();
	public static final Item scroll = new Item().setUnlocalizedName("thebetweenlands.itemScroll").setTextureName("thebetweenlands:itemScroll").setMaxStackSize(16);
	public static final Item weedwoodBucketRubber = new ItemWeedwoodBucketRubber();
	public static final Item summonMummy = new ItemSummonMummy();
	//public static final Item itemFrame = new ItemBLHangingEntity(EntityBLItemFrame.class).setUnlocalizedName("thebetweenlands.weedwoodItemFrame").setTextureName("thebetweenlands:weedwoodItemFrame").setCreativeTab(BLCreativeTabs.blocks);
	public static final Item mudFlowerPot = new ItemReed(BLBlockRegistry.mudFlowerPot).setUnlocalizedName("thebetweenlands.mudFlowerPotItem").setCreativeTab(BLCreativeTabs.blocks).setTextureName("thebetweenlands:mudBrickFlowerPot");
	public static final Item pestle = new ItemPestle().setUnlocalizedName("thebetweenlands.pestle");
	public static final Item weedwoodBucketInfusion = new ItemWeedwoodBucketInfusion();
	public static final Item manualGuideBook = new ItemManual().setUnlocalizedName("thebetweenlands.manual").setTextureName("thebetweenlands:manual");
	public static final Item manualHL = new ItemHLBook().setUnlocalizedName("thebetweenlands.manualHL").setTextureName("thebetweenlands:manualHL");
	public static final Item tarminion = new ItemTarminion().setUnlocalizedName("thebetweenlands.tarminion");
	public static final Item volarkite = new ItemVolarkite().setMaxDamage(200);
	public static final Item weedwoodRowboat = new ItemWeedwoodRowboat();
	public static final ItemRope rope = new ItemRope();
	public static final Item fireFly = new ItemMob("fireFly");
	public static final Item gecko = new ItemMob("gecko");
	public static final Item cavingRope = new ItemCavingRope().setUnlocalizedName("thebetweenlands.cavingRope").setTextureName("thebetweenlands:cavingRope");
	public static final Item weedwoodSign = new ItemBLSign(BLBlockRegistry.weedwoodWallSign, BLBlockRegistry.weedwoodSignStanding, "weedwood");
	public static final Item mossBed = new ItemMossBed().setUnlocalizedName("thebetweenlands.mossBedItem").setTextureName("thebetweenlands:mossBed");
	public static final Item amulet = new ItemAmulet();
	public static final Item amuletSlot = new ItemAmuletSlot();
	public static final Item lurkerSkinPouch = new ItemLurkerSkinPouch();
	//LOOT
	public static final Item voodooDoll = new ItemVoodooDoll();
	public static final Item explorerHat = new ItemExplorerHat().setUnlocalizedName("thebetweenlands.explorerHat").setTextureName("thebetweenlands:explorersHat");
	public static final Item swiftPick = new ItemSwiftPick(BLMaterial.toolValonite).setUnlocalizedName("thebetweenlands.swiftPickaxe").setTextureName("thebetweenlands:swiftPick");
	public static final Item wightsBane = new ItemLootSword(BLMaterial.toolWeedWood)
			.addInstantKills(EntityWight.class)
			.setMaxDamage(32).setUnlocalizedName("thebetweenlands.wightsBane").setTextureName("thebetweenlands:wightsBane");
	public static final Item sludgeSlicer = new ItemLootSword(BLMaterial.toolWeedWood)
			.addInstantKills(EntitySludge.class)
			.setMaxDamage(32).setUnlocalizedName("thebetweenlands.sludgeSlicer").setTextureName("thebetweenlands:purgerOfTheMire");
	public static final Item critterCruncher = new ItemLootSword(BLMaterial.toolWeedWood)
			.addInstantKills(EntityBloodSnail.class, EntityDragonFly.class, EntityFirefly.class, EntityLeech.class, EntityMireSnail.class, EntitySporeling.class, EntityTermite.class)
			.setMaxDamage(32).setUnlocalizedName("thebetweenlands.critterCruncher").setTextureName("thebetweenlands:critterCruncher");
	public static final Item hagHacker = new ItemLootSword(BLMaterial.toolWeedWood)
			.addInstantKills(EntitySwampHag.class)
			.setMaxDamage(32).setUnlocalizedName("thebetweenlands.hagHacker").setTextureName("thebetweenlands:hagHacker");
	public static final Item lore = new ItemLore();
	public static final Item skullMask = new ItemSkullMask().setUnlocalizedName("thebetweenlands.skullMask").setTextureName("thebetweenlands:skullMask");
	//DISCS
	public static final Item astatos = new ItemBLRecord("Astatos");
	public static final Item betweenYouAndMe = new ItemBLRecord("BetweenYouAndMe");
	public static final Item christmasOnTheMarsh = new ItemBLRecord("ChristmasOnTheMarsh");
	public static final Item theExplorer = new ItemBLRecord("TheExplorer");
	public static final Item hagDance = new ItemBLRecord("HagDance");
	public static final Item lonelyFire = new ItemBLRecord("LonelyFire");
	public static final Item mysteriousRecord = new ItemBLRecord("16612");
	public static final Item ancient = new ItemBLRecord("Ancient");
	public static final Item beneathAGreenSky = new ItemBLRecord("BeneathAGreenSky");
	public static final Item dJWightsMixtape = new ItemBLRecord("DJWightsMixtape");
	public static final Item onwards = new ItemBLRecord("Onwards");
	public static final Item stuckInTheMud = new ItemBLRecord("StuckInTheMud");
	public static final Item wanderingWisps = new ItemBLRecord("WanderingWisps");
	public static final Item waterlogged = new ItemBLRecord("Waterlogged");
	public static final ItemElixir elixir = new ItemElixir();
	public static final ItemDentrothystVial dentrothystVial = new ItemDentrothystVial();
	public static final ItemAspectVial aspectVial = new ItemAspectVial();
	// EQUIPMENT
	public static final Item ringOfPower = new ItemRingOfPower();
	public static final Item ringOfRecruitment = new ItemRingOfRecruitment();
	public static final Item ringOfFlight = new ItemRingOfFlight();
	public static final Item ringOfSummoning = new ItemRingOfSummoning();
	private static final List<ItemStack> ORES = new ArrayList<ItemStack>();
	private static final List<ItemStack> INGOTS = new ArrayList<ItemStack>();

	public static void init() {
		initCreativeTabs();
		registerItems();
		registerProperties();
	}

	private static void initCreativeTabs() {
		BLCreativeTabs.items.setTab(lore, net, rope, weedwoodRowboat, volarkite, swampTalisman, itemsGeneric, sapBall, rottenFood, flatheadMushroomItem,
				blackHatMushroomItem, bulbCappedMushroomItem, anglerMeatRaw, anglerMeatCooked, frogLegsRaw, frogLegsCooked, snailFleshRaw,
				snailFleshCooked, friedSwampKelp, reedDonut, jamDonut, /*krakenTentacle, krakenCalamari,*/ middleFruit, /*mincePie,*/ weepingBluePetal,
				wightsHeart, yellowDottedFungus, siltCrabClaw, crabstick, nettleSoup, sludgeJello, middleFruitJello, sapJello, marshmallow, 
				marshmallowPink, weedwoodBucket, weedwoodBucketStagnantWater, weedwoodBucketWater, weedwoodBucketTar, lifeCrystal, gertsDonut, 
				forbiddenFig, candyBlue, candyRed, candyYellow, cavingRope);
		BLCreativeTabs.specials.setTab(aquaMiddleGem, crimsonMiddleGem, greenMiddleGem, gecko, fireFly, shimmerStone, skullMask, 
				spawnEggs, angryPebble, scroll, voodooDoll, ringOfPower, ringOfRecruitment, ringOfFlight, ringOfSummoning, swiftPick, wightsBane, sludgeSlicer, critterCruncher, hagHacker, manualHL, tarminion,
				astatos, betweenYouAndMe, theExplorer, christmasOnTheMarsh, ancient, beneathAGreenSky, dJWightsMixtape, onwards, stuckInTheMud, 
				wanderingWisps, waterlogged, hagDance, lonelyFire, mysteriousRecord, amulet, amuletSlot);
		BLCreativeTabs.gears.setTab(weedwoodSword, weedwoodPickaxe, weedwoodAxe, weedwoodShovel, betweenstoneSword, betweenstonePickaxe, 
				betweenstoneAxe, betweenstoneShovel, octineSword, octinePickaxe, octineAxe, octineShovel, valoniteSword, valonitePickaxe, 
				valoniteAxe, valoniteShovel);
		BLCreativeTabs.gears.setTab(shockwaveSword, /*legendarySword, legendaryBoots, legendaryChestplate, legendaryHelmet, legendaryLeggings,*/ lurkerSkinHelmet,
				lurkerSkinChestplate, lurkerSkinLeggings, lurkerSkinBoots, boneHelmet, boneChestplate, boneLeggings, boneBoots, syrmoriteHelmet, 
				syrmoriteChestplate, syrmoriteLeggings, syrmoriteBoots, valoniteHelmet, valoniteChestplate, valoniteLeggings, valoniteBoots, 
				weedwoodBow, anglerToothArrow, poisonedAnglerToothArrow, octineArrow, basiliskArrow, explorerHat, rubberBoots, 
				rubberBootsImproved, lurkerSkinPouch);
		BLCreativeTabs.plants.setTab(middleFruitSeeds, spores);
		BLCreativeTabs.herbLore.setTab(pestle, itemsGenericCrushed, itemsGenericPlantDrop, weedwoodBucketInfusion, elixir, dentrothystVial, 
				aspectVial);

		if(ConfigHandler.DEBUG) {
			BLCreativeTabs.specials.setTab(testItem, testItem2);
		}
	}

	private static void registerItems() {
		try {
			for (Field f : BLItemRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Item) registerItem((Item) obj);
				else if (obj instanceof Item[])
					for (Item item : (Item[]) obj)
						registerItem(item);
			}
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerItem(Item item) {
		ITEMS.add(item);
		String name = item.getUnlocalizedName();
		String[] strings = name.split("\\.");
		GameRegistry.registerItem(item, strings[strings.length - 1]);
		if (!StatCollector.canTranslate(item.getUnlocalizedName() + ".name")) {
			TheBetweenlands.unlocalizedNames.add(item.getUnlocalizedName() + ".name");
		}
	}

	private static void registerProperties() {
		GameRegistry.registerFuelHandler(new IFuelHandler() {
			@Override
			public int getBurnTime(ItemStack fuel) {
				return 0;
				//add fuels here
			}
		});

		ORES.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.octineOre)));
		ORES.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.syrmoriteOre)));
		ORES.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.sulfurOre)));
		ORES.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.valoniteOre)));
		ORES.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.lifeCrystalOre)));

		INGOTS.add(new ItemStack(itemsGeneric, 1, EnumItemGeneric.OCTINE_INGOT.id));
		INGOTS.add(new ItemStack(itemsGeneric, 1, EnumItemGeneric.SYRMORITE_INGOT.id));
		INGOTS.add(new ItemStack(itemsGeneric, 1, EnumItemGeneric.SULFUR.id));
		INGOTS.add(new ItemStack(itemsGeneric, 1, EnumItemGeneric.VALONITE_SHARD.id));
		INGOTS.add(new ItemStack(lifeCrystal));
	}

	private static boolean containsItem(List<ItemStack> lst, ItemStack stack) {
		for(ItemStack s : lst) {
			if(s.getItem() == stack.getItem() && s.getItemDamage() == stack.getItemDamage())
				return true;
		}
		return false;
	}

	public static boolean isIngotFromOre(ItemStack input, ItemStack output) {
		if(input == null || output == null) return false;
		return isOre(input) && isIngot(output);
	}

	public static boolean isOre(ItemStack stack) {
		if(stack == null) return false;
		return containsItem(ORES, stack);
	}

	public static boolean isIngot(ItemStack stack) {
		if(stack == null) return false;
		return containsItem(INGOTS, stack);
	}
}
