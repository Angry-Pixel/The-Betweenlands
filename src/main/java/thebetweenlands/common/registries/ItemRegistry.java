package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemBoneArmor;
import thebetweenlands.common.item.armor.ItemExplorersHat;
import thebetweenlands.common.item.armor.ItemLurkerSkinArmor;
import thebetweenlands.common.item.armor.ItemMarshRunnerBoots;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.item.armor.ItemSkullMask;
import thebetweenlands.common.item.armor.ItemSpiritTreeFaceMaskLarge;
import thebetweenlands.common.item.armor.ItemSpiritTreeFaceMaskSmall;
import thebetweenlands.common.item.armor.ItemSyrmoriteArmor;
import thebetweenlands.common.item.armor.ItemValoniteArmor;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.item.equipment.ItemRingOfDispersion;
import thebetweenlands.common.item.equipment.ItemRingOfFlight;
import thebetweenlands.common.item.equipment.ItemRingOfPower;
import thebetweenlands.common.item.equipment.ItemRingOfRecruitment;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.item.farming.ItemAspectrusSeeds;
import thebetweenlands.common.item.farming.ItemMiddleFruitBushSeeds;
import thebetweenlands.common.item.farming.ItemPlantTonic;
import thebetweenlands.common.item.farming.ItemSpores;
import thebetweenlands.common.item.farming.ItemSwampKelp;
import thebetweenlands.common.item.farming.ItemSwampReed;
import thebetweenlands.common.item.food.ItemAspectrusFruit;
import thebetweenlands.common.item.food.ItemBLFood;
import thebetweenlands.common.item.food.ItemBlackHatMushroom;
import thebetweenlands.common.item.food.ItemBulbCappedMushroom;
import thebetweenlands.common.item.food.ItemChiromawWing;
import thebetweenlands.common.item.food.ItemFlatHeadMushroom;
import thebetweenlands.common.item.food.ItemForbiddenFig;
import thebetweenlands.common.item.food.ItemGertsDonut;
import thebetweenlands.common.item.food.ItemMarshmallow;
import thebetweenlands.common.item.food.ItemMarshmallowPink;
import thebetweenlands.common.item.food.ItemMireScramble;
import thebetweenlands.common.item.food.ItemMireSnailEgg;
import thebetweenlands.common.item.food.ItemNettleSoup;
import thebetweenlands.common.item.food.ItemNibblestick;
import thebetweenlands.common.item.food.ItemRottenFood;
import thebetweenlands.common.item.food.ItemSapBall;
import thebetweenlands.common.item.food.ItemSapJello;
import thebetweenlands.common.item.food.ItemSpiritFruit;
import thebetweenlands.common.item.food.ItemTaintedPotion;
import thebetweenlands.common.item.food.ItemTangledRoot;
import thebetweenlands.common.item.food.ItemWeepingBluePetal;
import thebetweenlands.common.item.food.ItemWeepingBluePetalSalad;
import thebetweenlands.common.item.food.ItemWightHeart;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemDentrothystFluidVial;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.herblore.ItemElixir;
import thebetweenlands.common.item.herblore.ItemManualHL;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemAmateMap;
import thebetweenlands.common.item.misc.ItemAmuletSlot;
import thebetweenlands.common.item.misc.ItemAngryPebble;
import thebetweenlands.common.item.misc.ItemBLRecord;
import thebetweenlands.common.item.misc.ItemBarkAmulet;
import thebetweenlands.common.item.misc.ItemBoneWayfinder;
import thebetweenlands.common.item.misc.ItemCavingRope;
import thebetweenlands.common.item.misc.ItemChiromawEgg;
import thebetweenlands.common.item.misc.ItemChiromawTame;
import thebetweenlands.common.item.misc.ItemCritters;
import thebetweenlands.common.item.misc.ItemDentrothystShard;
import thebetweenlands.common.item.misc.ItemDoorBetweenlands;
import thebetweenlands.common.item.misc.ItemDraeton;
import thebetweenlands.common.item.misc.ItemEmptyAmateMap;
import thebetweenlands.common.item.misc.ItemGalleryFrame;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.item.misc.ItemGemSinger;
import thebetweenlands.common.item.misc.ItemGlue;
import thebetweenlands.common.item.misc.ItemGrapplingHook;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemLoreScrap;
import thebetweenlands.common.item.misc.ItemLurkerSkinPatch;
import thebetweenlands.common.item.misc.ItemMagicItemMagnet;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.item.misc.ItemMossBed;
import thebetweenlands.common.item.misc.ItemMummyBait;
import thebetweenlands.common.item.misc.ItemOctineIngot;
import thebetweenlands.common.item.misc.ItemPyradFlame;
import thebetweenlands.common.item.misc.ItemRingOfGathering;
import thebetweenlands.common.item.misc.ItemRope;
import thebetweenlands.common.item.misc.ItemRuneDoorKey;
import thebetweenlands.common.item.misc.ItemShimmerStone;
import thebetweenlands.common.item.misc.ItemSpiritTreeFaceMaskSmallAnimated;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.item.misc.ItemTarminion;
import thebetweenlands.common.item.misc.ItemVolarkite;
import thebetweenlands.common.item.misc.ItemWeedwoodRowboat;
import thebetweenlands.common.item.misc.ItemWeedwoodSign;
import thebetweenlands.common.item.misc.LocationDebugItem;
import thebetweenlands.common.item.misc.TestItem;
import thebetweenlands.common.item.misc.TestItemChimp;
import thebetweenlands.common.item.misc.TestItemChimpRuler;
import thebetweenlands.common.item.shields.ItemDentrothystShield;
import thebetweenlands.common.item.shields.ItemLivingWeedwoodShield;
import thebetweenlands.common.item.shields.ItemLurkerSkinShield;
import thebetweenlands.common.item.shields.ItemOctineShield;
import thebetweenlands.common.item.shields.ItemSyrmoriteShield;
import thebetweenlands.common.item.shields.ItemValoniteShield;
import thebetweenlands.common.item.shields.ItemWeedwoodShield;
import thebetweenlands.common.item.tools.ItemAncientBattleAxe;
import thebetweenlands.common.item.tools.ItemAncientGreatsword;
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.item.tools.ItemBucketInfusion;
import thebetweenlands.common.item.tools.ItemGreataxe;
import thebetweenlands.common.item.tools.ItemHagHacker;
import thebetweenlands.common.item.tools.ItemLootSword;
import thebetweenlands.common.item.tools.ItemNet;
import thebetweenlands.common.item.tools.ItemOctineAxe;
import thebetweenlands.common.item.tools.ItemOctinePickaxe;
import thebetweenlands.common.item.tools.ItemOctineShovel;
import thebetweenlands.common.item.tools.ItemOctineSword;
import thebetweenlands.common.item.tools.ItemPestle;
import thebetweenlands.common.item.tools.ItemShockwaveSword;
import thebetweenlands.common.item.tools.ItemSickle;
import thebetweenlands.common.item.tools.ItemSimpleSlingshot;
import thebetweenlands.common.item.tools.ItemSpecificBucket;
import thebetweenlands.common.item.tools.ItemSwiftPick;
import thebetweenlands.common.item.tools.ItemSyrmoriteBucketSolidRubber;
import thebetweenlands.common.item.tools.ItemSyrmoriteShears;
import thebetweenlands.common.item.tools.ItemVoodooDoll;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.item.tools.bow.ItemBLArrow;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.item.tools.bow.ItemPredatorBow;
import thebetweenlands.common.lib.ModInfo;

public class ItemRegistry {
    public final static Set<Item> ITEMS = new LinkedHashSet<>();
    //generic
    public static final Item ITEMS_MISC = new ItemMisc().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item ITEMS_CRUSHED = new ItemCrushed().setCreativeTab(BLCreativeTabs.HERBLORE);
    public static final Item ITEMS_PLANT_DROP = new ItemPlantDrop().setCreativeTab(BLCreativeTabs.HERBLORE);
    public static final Item SWAMP_TALISMAN = new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item WEEDWOOD_ROWBOAT = new ItemWeedwoodRowboat();
    public static final Item DENTROTHYST_SHARD_ORANGE = new ItemDentrothystShard(EnumDentrothyst.ORANGE);
    public static final Item DENTROTHYST_SHARD_GREEN = new ItemDentrothystShard(EnumDentrothyst.GREEN);
    //food
    public static final Item SAP_BALL = new ItemSapBall();
    public static final ItemRottenFood ROTTEN_FOOD = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
    public static final Item MIDDLE_FRUIT_BUSH_SEEDS = new ItemMiddleFruitBushSeeds();
    public static final Item SPORES = new ItemSpores();
    public static final Item ASPECTRUS_SEEDS = new ItemAspectrusSeeds();
    public static final Item MIRE_SNAIL_EGG = new ItemMireSnailEgg();
    public static final Item MIRE_SNAIL_EGG_COOKED = new ItemBLFood(8, 1, false);
    public static final Item ANGLER_MEAT_RAW = new ItemBLFood(4, 0.4F, false);
    public static final Item ANGLER_MEAT_COOKED = new ItemBLFood(8, 0.8F, false);
    public static final Item FROG_LEGS_RAW = new ItemBLFood(3, 0.4F, false);
    public static final Item FROG_LEGS_COOKED = new ItemBLFood(6, 0.8F, false);
    public static final Item SNAIL_FLESH_RAW = new ItemBLFood(3, 0.4F, false);
    public static final Item SNAIL_FLESH_COOKED = new ItemBLFood(6, 0.9F, false);
    public static final Item REED_DONUT = new ItemBLFood(6, 0.6F, false);
    public static final Item JAM_DONUT = new ItemBLFood(10, 0.6F, false);
    public static final Item GERTS_DONUT = new ItemGertsDonut();
    public static final Item ASPECTRUS_FRUIT = new ItemAspectrusFruit();
    public static final Item PUFFSHROOM_TENDRIL = new ItemBLFood(8, 0.9F, false);
    public static final Item KRAKEN_TENTACLE = new ItemBLFood(8, 0.9F, false);
    public static final Item KRAKEN_CALAMARI = new ItemBLFood(14, 1.2F, false);
    public static final Item MIDDLE_FRUIT = new ItemBLFood(6, 0.6F, false);
    public static final Item MINCE_PIE = new ItemBLFood(8, 1F, false);
    public static final Item CHRISTMAS_PUDDING = new ItemBLFood(6, 0.95F, false);
    public static final Item CANDY_CANE = new ItemBLFood(4, 0.85F, false);
    public static final Item WEEPING_BLUE_PETAL = new ItemWeepingBluePetal();
    public static final Item WIGHT_HEART = new ItemWightHeart();
    public static final Item YELLOW_DOTTED_FUNGUS = new ItemBLFood(8, 0.6F, false);
    public static final Item SILT_CRAB_CLAW = new ItemBLFood(2, 0.6F, false);
    public static final Item CRAB_STICK = new ItemBLFood(6, 0.9F, false);
    public static final Item NETTLE_SOUP = new ItemNettleSoup();
    public static final Item SLUDGE_JELLO = new ItemBLFood(4, 0.9F, false);
    public static final Item MIDDLE_FRUIT_JELLO = new ItemBLFood(10, 1.0F, false);
    public static final Item SAP_JELLO = new ItemSapJello();
    public static final Item MARSHMALLOW = new ItemMarshmallow();
    public static final Item MARSHMALLOW_PINK = new ItemMarshmallowPink();
    public static final Item FLAT_HEAD_MUSHROOM_ITEM = new ItemFlatHeadMushroom();
    public static final Item BLACK_HAT_MUSHROOM_ITEM = new ItemBlackHatMushroom();
    public static final Item BULB_CAPPED_MUSHROOM_ITEM = new ItemBulbCappedMushroom();
    public static final Item SWAMP_REED_ITEM = new ItemSwampReed();
    public static final Item SWAMP_KELP_ITEM = new ItemSwampKelp();
    public static final Item FRIED_SWAMP_KELP = new ItemBLFood(5, 0.6F, false);
    public static final Item FORBIDDEN_FIG = new ItemForbiddenFig();
    public static final Item CANDY_BLUE = new ItemBLFood(4, 1.0F, false);
    public static final Item CANDY_RED = new ItemBLFood(4, 1.0F, false);
    public static final Item CANDY_YELLOW = new ItemBLFood(4, 1.0F, false);
    public static final Item CHIROMAW_WING = new ItemChiromawWing();
    public static final Item TANGLED_ROOT = new ItemTangledRoot();
    public static final Item MIRE_SCRAMBLE = new ItemMireScramble();
    public static final Item WEEPING_BLUE_PETAL_SALAD = new ItemWeepingBluePetalSalad();
    public static final Item NIBBLESTICK = new ItemNibblestick();
    public static final Item SPIRIT_FRUIT = new ItemSpiritFruit();
    public static final Item SUSHI = new ItemBLFood(5, 1.0F, false);
    
    //armor
    public static final Item BONE_HELMET = new ItemBoneArmor(EntityEquipmentSlot.HEAD);
    public static final Item BONE_CHESTPLATE = new ItemBoneArmor(EntityEquipmentSlot.CHEST);
    public static final Item BONE_LEGGINGS = new ItemBoneArmor(EntityEquipmentSlot.LEGS);
    public static final Item BONE_BOOTS = new ItemBoneArmor(EntityEquipmentSlot.FEET);
    public static final Item LURKER_SKIN_HELMET = new ItemLurkerSkinArmor(EntityEquipmentSlot.HEAD);
    public static final Item LURKER_SKIN_CHESTPLATE = new ItemLurkerSkinArmor(EntityEquipmentSlot.CHEST);
    public static final Item LURKER_SKIN_LEGGINGS = new ItemLurkerSkinArmor(EntityEquipmentSlot.LEGS);
    public static final Item LURKER_SKIN_BOOTS = new ItemLurkerSkinArmor(EntityEquipmentSlot.FEET);
    public static final Item SYRMORITE_HELMET = new ItemSyrmoriteArmor(EntityEquipmentSlot.HEAD);
    public static final Item SYRMORITE_CHESTPLATE = new ItemSyrmoriteArmor(EntityEquipmentSlot.CHEST);
    public static final Item SYRMORITE_LEGGINGS = new ItemSyrmoriteArmor(EntityEquipmentSlot.LEGS);
    public static final Item SYRMORITE_BOOTS = new ItemSyrmoriteArmor(EntityEquipmentSlot.FEET);
    public static final Item VALONITE_HELMET = new ItemValoniteArmor(EntityEquipmentSlot.HEAD);
    public static final Item VALONITE_CHESTPLATE = new ItemValoniteArmor(EntityEquipmentSlot.CHEST);
    public static final Item VALONITE_LEGGINGS = new ItemValoniteArmor(EntityEquipmentSlot.LEGS);
    public static final Item VALONITE_BOOTS = new ItemValoniteArmor(EntityEquipmentSlot.FEET);
    public static final Item RUBBER_BOOTS = new ItemRubberBoots();
    public static final Item MARSH_RUNNER_BOOTS = new ItemMarshRunnerBoots();
    public static final Item SKULL_MASK = new ItemSkullMask();
    public static final Item EXPLORERS_HAT = new ItemExplorersHat();
    public static final Item SPIRIT_TREE_FACE_LARGE_MASK = new ItemSpiritTreeFaceMaskLarge();
    public static final Item SPIRIT_TREE_FACE_SMALL_MASK = new ItemSpiritTreeFaceMaskSmall();
    public static final Item SPIRIT_TREE_FACE_SMALL_MASK_ANIMATED = new ItemSpiritTreeFaceMaskSmallAnimated();
    public static final Item GALLERY_FRAME_SMALL = new ItemGalleryFrame(EntityGalleryFrame.Type.SMALL);
    public static final Item GALLERY_FRAME_LARGE = new ItemGalleryFrame(EntityGalleryFrame.Type.LARGE);
    public static final Item GALLERY_FRAME_VERY_LARGE = new ItemGalleryFrame(EntityGalleryFrame.Type.VERY_LARGE);
    //TOOLS
    public static final Item WEEDWOOD_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item WEEDWOOD_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item WEEDWOOD_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item WEEDWOOD_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item BONE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item BONE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item BONE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item BONE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item OCTINE_SWORD = new ItemOctineSword();
    public static final Item OCTINE_SHOVEL = new ItemOctineShovel();
    public static final Item OCTINE_AXE = new ItemOctineAxe();
    public static final Item OCTINE_PICKAXE = new ItemOctinePickaxe();
    public static final Item VALONITE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item VALONITE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item VALONITE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item VALONITE_GREATAXE = new ItemGreataxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item VALONITE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item OCTINE_SHIELD = new ItemOctineShield();
    public static final Item VALONITE_SHIELD = new ItemValoniteShield();
    public static final Item WEEDWOOD_SHIELD = new ItemWeedwoodShield();
    public static final Item LIVING_WEEDWOOD_SHIELD = new ItemLivingWeedwoodShield();
    public static final Item SYRMORITE_SHIELD = new ItemSyrmoriteShield();
    public static final Item BONE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_BONE);
    public static final Item DENTROTHYST_SHIELD_GREEN = new ItemDentrothystShield(true);
    public static final Item DENTROTHYST_SHIELD_GREEN_POLISHED = new ItemDentrothystShield(true);
    public static final Item DENTROTHYST_SHIELD_ORANGE = new ItemDentrothystShield(false);
    public static final Item DENTROTHYST_SHIELD_ORANGE_POLISHED = new ItemDentrothystShield(false);
    public static final Item LURKER_SKIN_SHIELD = new ItemLurkerSkinShield();
    public static final Item MANUAL_HL = new ItemManualHL();
    public static final Item SYRMORITE_SHEARS = new ItemSyrmoriteShears();
    public static final Item SICKLE = new ItemSickle();
    public static final Item SHOCKWAVE_SWORD = new ItemShockwaveSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.DEFAULT).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item POISONED_ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.ANGLER_POISON).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item OCTINE_ARROW = new ItemBLArrow(EnumArrowType.OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item BASILISK_ARROW = new ItemBLArrow(EnumArrowType.BASILISK).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item SLUDGE_WORM_ARROW = new ItemBLArrow(EnumArrowType.WORM).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item SHOCK_ARROW = new ItemBLArrow(EnumArrowType.SHOCK).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item CHIROMAW_BARB = new ItemBLArrow(EnumArrowType.CHIROMAW_BARB).setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item WEEDWOOD_BOW = new ItemBLBow().setCreativeTab(BLCreativeTabs.GEARS);
    public static final Item PREDATOR_BOW = new ItemPredatorBow();
    public static final Item WIGHTS_BANE = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityWight.class).setMaxDamage(32);
    public static final Item SLUDGE_SLICER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntitySludge.class, EntitySmollSludge.class).setMaxDamage(32);
    public static final Item CRITTER_CRUNCHER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityBloodSnail.class, EntityDragonFly.class, EntityFirefly.class, EntityLeech.class, EntityMireSnail.class, EntitySporeling.class, EntityTermite.class, EntityChiromaw.class).setMaxDamage(32);
    public static final Item HAG_HACKER = new ItemHagHacker();
    public static final Item VOODOO_DOLL = new ItemVoodooDoll();
    public static final Item SWIFT_PICK = new ItemSwiftPick();
    public static final Item ANCIENT_GREATSWORD = new ItemAncientGreatsword();
    public static final Item ANCIENT_BATTLE_AXE = new ItemAncientBattleAxe();
    public static final Item PESTLE = new ItemPestle();
    public static final Item NET = new ItemNet();
    public static final Item LURKER_SKIN_POUCH = new ItemLurkerSkinPouch();
    public static final Item CAVING_ROPE = new ItemCavingRope();
    public static final Item GRAPPLING_HOOK = new ItemGrapplingHook();
    public static final Item VOLARKITE = new ItemVolarkite();
    public static final Item SIMPLE_SLINGSHOT = new ItemSimpleSlingshot();
    //BUCKETS
    public static final ItemBLBucket BL_BUCKET = new ItemBLBucket();
    public static final Item BL_BUCKET_RUBBER = new ItemSpecificBucket(FluidRegistry.RUBBER);
    public static final Item BL_BUCKET_INFUSION = new ItemBucketInfusion();
    public static final Item BL_BUCKET_PLANT_TONIC = new ItemPlantTonic();
    public static final Item SYRMORITE_BUCKET_SOLID_RUBBER = new ItemSyrmoriteBucketSolidRubber();
    //RECORDS
    public static final Item ASTATOS = new ItemBLRecord(SoundRegistry.ASTATOS);
    public static final Item BETWEEN_YOU_AND_ME = new ItemBLRecord(SoundRegistry.BETWEEN_YOU_AND_ME);
    public static final Item CHRISTMAS_ON_THE_MARSH = new ItemBLRecord(SoundRegistry.CHRISTMAS_ON_THE_MARSH);
    public static final Item THE_EXPLORER = new ItemBLRecord(SoundRegistry.THE_EXPLORER);
    public static final Item HAG_DANCE = new ItemBLRecord(SoundRegistry.HAG_DANCE);
    public static final Item LONELY_FIRE = new ItemBLRecord(SoundRegistry.LONELY_FIRE);
    public static final Item MYSTERIOUS_RECORD = new ItemBLRecord(SoundRegistry._16612);
    public static final Item ANCIENT = new ItemBLRecord(SoundRegistry.ACIENT);
    public static final Item BENEATH_A_GREEN_SKY = new ItemBLRecord(SoundRegistry.BENEATH_A_GREEN_SKY);
    public static final Item DJ_WIGHTS_MIXTAPE = new ItemBLRecord(SoundRegistry.DJ_WIGHTS_MIXTAPE);
    public static final Item ONWARDS = new ItemBLRecord(SoundRegistry.ONWARD);
    public static final Item STUCK_IN_THE_MUD = new ItemBLRecord(SoundRegistry.STUCK_IN_THE_MUD);
    public static final Item WANDERING_WISPS = new ItemBLRecord(SoundRegistry.WANDERING_WISPS);
    public static final Item WATERLOGGED = new ItemBLRecord(SoundRegistry.WATERLOGGED);
    //MISC
    public static final Item WEEDWOOD_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.WEEDWOOD_DOOR;
        }
    };
    public static final Item SYRMORITE_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.SYRMORITE_DOOR;
        }
    };
    public static final Item RUBBER_TREE_PLANK_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.RUBBER_TREE_PLANK_DOOR;
        }
    };
    public static final Item GIANT_ROOT_PLANK_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.GIANT_ROOT_PLANK_DOOR;
        }
    };
    public static final Item HEARTHGROVE_PLANK_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.HEARTHGROVE_PLANK_DOOR;
        }
    };
    public static final Item NIBBLETWIG_PLANK_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.NIBBLETWIG_PLANK_DOOR;
        }
    };
    public static final Item SCABYST_DOOR_ITEM = new ItemDoorBetweenlands() {
        @Override
        public Block getDoorBlock() {
            return BlockRegistry.SCABYST_DOOR;
        }
    };
    public static final Item WEEDWOOD_SIGN_ITEM = new ItemWeedwoodSign();
    public static final Item CRIMSON_MIDDLE_GEM = new ItemGem(CircleGemType.CRIMSON);
    public static final Item AQUA_MIDDLE_GEM = new ItemGem(CircleGemType.AQUA);
    public static final Item GREEN_MIDDLE_GEM = new ItemGem(CircleGemType.GREEN);
    public static final Item LIFE_CRYSTAL = new ItemLifeCrystal();
    public static final Item TEST_ITEM = new TestItem();
    public static final Item TEST_ITEM_CHIMP = new TestItemChimp();
    public static final Item TEST_ITEM_CHIMP_RULER = new TestItemChimpRuler();
    public static final Item LOCATION_DEBUG = new LocationDebugItem().setCreativeTab(null);
    public static final Item PYRAD_FLAME = new ItemPyradFlame();
    public static final ItemMob CRITTER = new ItemCritters();
    public static final ItemMob SLUDGE_WORM_EGG_SAC = new ItemMob(16, EntityTinyWormEggSac.class, null);
    public static final ItemMob CHIROMAW_EGG = new ItemChiromawEgg(false);
    public static final ItemMob CHIROMAW_EGG_LIGHTNING = new ItemChiromawEgg(true);
    public static final ItemMob CHIROMAW_TAME = new ItemChiromawTame(false);
    public static final ItemMob CHIROMAW_TAME_LIGHTNING = new ItemChiromawTame(true);
    public static final Item SHIMMER_STONE = new ItemShimmerStone();
    public static final Item TARMINION = new ItemTarminion();
    public static final Item MOSS_BED_ITEM = new ItemMossBed();
    public static final Item SLUDGE_BALL = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final ItemElixir ELIXIR = new ItemElixir();
    public static final ItemDentrothystVial DENTROTHYST_VIAL = new ItemDentrothystVial();
    public static final ItemDentrothystFluidVial DENTROTHYST_FLUID_VIAL = new ItemDentrothystFluidVial();
    public static final ItemAspectVial ASPECT_VIAL = new ItemAspectVial();
    public static final Item GLUE = new ItemGlue();
    public static final Item AMULET = new ItemAmulet();
    public static final Item AMULET_SLOT = new ItemAmuletSlot();
    public static final Item ROPE_ITEM = new ItemRope();
    public static final Item RING_OF_POWER = new ItemRingOfPower();
    public static final Item RING_OF_FLIGHT = new ItemRingOfFlight();
    public static final Item RING_OF_RECRUITMENT = new ItemRingOfRecruitment();
    public static final Item RING_OF_SUMMONING = new ItemRingOfSummoning();
    public static final Item RING_OF_DISPERSION = new ItemRingOfDispersion();
    public static final ItemRingOfGathering RING_OF_GATHERING = new ItemRingOfGathering();
    public static final Item ANGRY_PEBBLE = new ItemAngryPebble();
    public static final Item LORE_SCRAP = new ItemLoreScrap();
    public static final ItemTaintedPotion TAINTED_POTION = new ItemTaintedPotion();
    public static final ItemOctineIngot OCTINE_INGOT = new ItemOctineIngot();
    public static final Item MUMMY_BAIT = new ItemMummyBait();
    public static final Item SAP_SPIT = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item BARK_AMULET = new ItemBarkAmulet();
    public static final Item EMPTY_AMATE_MAP = new ItemEmptyAmateMap();
    public static final Item AMATE_MAP = new ItemAmateMap();
    public static final Item BONE_WAYFINDER = new ItemBoneWayfinder();
    public static final Item MAGIC_ITEM_MAGNET = new ItemMagicItemMagnet();
    public static final Item GEM_SINGER = new ItemGemSinger();
    public static final Item SHAMBLER_TONGUE = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item RUNE_DOOR_KEY = new ItemRuneDoorKey();
    public static final Item LURKER_SKIN_PATCH = new ItemLurkerSkinPatch();
    public static final Item DRAETON_BALLOON = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item DRAETON_BURNER = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
    public static final Item DRAETON = new ItemDraeton();
    public static final Item DRAETON_UPGRADE_FURNACE = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final Item DRAETON_UPGRADE_ANCHOR = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final Item DRAETON_UPGRADE_CRAFTING = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    public static final Item WEEDWOOD_ROWBOAT_UPGRADE_LANTERN = new Item().setCreativeTab(BLCreativeTabs.ITEMS).setMaxStackSize(1);
    
    private static final List<ItemStack> ORES = new ArrayList<ItemStack>();
    private static final List<ItemStack> INGOTS = new ArrayList<ItemStack>();
    private static final List<ItemStack> NUGGETS = new ArrayList<ItemStack>();
 
    private ItemRegistry() {

    }

    public static void preInit() {
        try {
            for (Field field : ItemRegistry.class.getDeclaredFields()) {
                if (field.get(null) instanceof Item) {
                    Item item = (Item) field.get(null);
                    registerItem(item, field.getName());

                    if (BetweenlandsConfig.DEBUG.debug && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                        if (item.getCreativeTab() == null)
                        	TheBetweenlands.logger.warn(String.format("Item %s doesn't have a creative tab", item.getTranslationKey()));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void registerItem(Item item, String fieldName) {
        ITEMS.add(item);
        String name = fieldName.toLowerCase(Locale.ENGLISH);
        item.setRegistryName(ModInfo.ID, name).setTranslationKey(ModInfo.ID + "." + name);
    }

    private static void registerItemTypes() {
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.OCTINE_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.SYRMORITE_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.SULFUR_ORE)));
        ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.VALONITE_ORE)));
        //ORES.add(new ItemStack(Item.getItemFromBlock(BlockRegistry.LIFE_CRYSTAL_STALACTITE)));

        INGOTS.add(new ItemStack(ItemRegistry.OCTINE_INGOT));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_INGOT.getID()));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SULFUR.getID()));
        INGOTS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SHARD.getID()));
        //INGOTS.add(new ItemStack(LIFE_CRYSTAL));

        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()));
        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()));
        NUGGETS.add(new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()));
    }
    
    private static void registerOreDictionary() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(BlockRegistry.SULFUR_ORE));
		OreDictionary.registerOre("oreSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_ORE));
		OreDictionary.registerOre("oreBone", new ItemStack(BlockRegistry.SLIMY_BONE_ORE));
		OreDictionary.registerOre("oreOctine", new ItemStack(BlockRegistry.OCTINE_ORE));
		OreDictionary.registerOre("oreValonite", new ItemStack(BlockRegistry.VALONITE_ORE));
		OreDictionary.registerOre("oreAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
		OreDictionary.registerOre("oreLifeCrystal", new ItemStack(BlockRegistry.LIFE_CRYSTAL_STALACTITE, 1, BlockLifeCrystalStalactite.EnumLifeCrystalType.ORE.getMetadata()));
		OreDictionary.registerOre("oreScabyst", new ItemStack(BlockRegistry.SCABYST_ORE));

		OreDictionary.registerOre("nuggetSyrmorite", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.SYRMORITE_NUGGET.getID()));
		OreDictionary.registerOre("nuggetOctine", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.OCTINE_NUGGET.getID()));
		OreDictionary.registerOre("nuggetValonite", new ItemStack(ITEMS_MISC, 1, EnumItemMisc.VALONITE_SPLINTER.getID()));

		OreDictionary.registerOre("blockSulfur", new ItemStack(BlockRegistry.SULFUR_BLOCK));
		OreDictionary.registerOre("blockSyrmorite", new ItemStack(BlockRegistry.SYRMORITE_BLOCK));
		OreDictionary.registerOre("blockBone", new ItemStack(BlockRegistry.SLIMY_BONE_BLOCK));
		OreDictionary.registerOre("blockOctine", new ItemStack(BlockRegistry.OCTINE_BLOCK));
		OreDictionary.registerOre("blockValonite", new ItemStack(BlockRegistry.VALONITE_BLOCK));
		OreDictionary.registerOre("blockAquaMiddleGem", new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockGreenMiddleGem", new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK));
		OreDictionary.registerOre("blockCrimsonMiddleGem", new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK));

		OreDictionary.registerOre("blockGlass", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("blockGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS));
		OreDictionary.registerOre("paneGlass", new ItemStack(BlockRegistry.SILT_GLASS_PANE));
		OreDictionary.registerOre("paneGlassColorless", new ItemStack(BlockRegistry.SILT_GLASS_PANE));

		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.SWAMP_DIRT));
		OreDictionary.registerOre("dirt", new ItemStack(BlockRegistry.COARSE_SWAMP_DIRT));

		OreDictionary.registerOre("grass", new ItemStack(BlockRegistry.SWAMP_GRASS));

		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_WEEDWOOD_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SAP_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_RUBBER_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_HEARTHGROVE_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_NIBBLETWIG_TREE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_TOP));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE));
		OreDictionary.registerOre("treeLeaves", new ItemStack(BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM));
		
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_WEEDWOOD));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_SAP));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_RUBBER));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_HEARTHGROVE));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlockRegistry.SAPLING_NIBBLETWIG));

		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM));
		OreDictionary.registerOre("foodMushroom", new ItemStack(ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM));

		OreDictionary.registerOre("ingotSyrmorite", EnumItemMisc.SYRMORITE_INGOT.create(1));
		OreDictionary.registerOre("ingotOctine", new ItemStack(ItemRegistry.OCTINE_INGOT));

		OreDictionary.registerOre("gemValonite", EnumItemMisc.VALONITE_SHARD.create(1));
		OreDictionary.registerOre("gemAquaMiddleGem", new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM));
		OreDictionary.registerOre("gemCrimsonMiddleGem", new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM));
		OreDictionary.registerOre("gemGreenMiddleGem", new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM));
		OreDictionary.registerOre("gemLifeCrystal", new ItemStack(ItemRegistry.LIFE_CRYSTAL, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("gemScabyst", EnumItemMisc.SCABYST.create(1));

		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_WEEDWOOD, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SAP, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_RUBBER, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.GIANT_ROOT, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_HEARTHGROVE, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_NIBBLETWIG, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(BlockRegistry.LOG_SPIRIT_TREE, 1, OreDictionary.WILDCARD_VALUE));

		OreDictionary.registerOre("stickWood", EnumItemMisc.WEEDWOOD_STICK.create(1));
		
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANKS));
		OreDictionary.registerOre("plankWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANKS));
		
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_SLAB));
		OreDictionary.registerOre("slabWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_SLAB));
		
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_FENCE));
		OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_FENCE));
        OreDictionary.registerOre("fenceWood", new ItemStack(BlockRegistry.ROTTEN_PLANK_FENCE));

        OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.WEEDWOOD_LOG_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_FENCE_GATE));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_FENCE_GATE));


		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.WEEDWOOD_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.RUBBER_TREE_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.GIANT_ROOT_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.HEARTHGROVE_PLANK_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.NIBBLETWIG_PLANK_STAIRS));
        OreDictionary.registerOre("stairWood", new ItemStack(BlockRegistry.ROTTEN_PLANK_STAIRS));

        OreDictionary.registerOre("torch", new ItemStack(BlockRegistry.SULFUR_TORCH));

		OreDictionary.registerOre("bone", EnumItemMisc.SLIMY_BONE.create(1));

		OreDictionary.registerOre("cobblestone", new ItemStack(BlockRegistry.BETWEENSTONE));
		OreDictionary.registerOre("stone", new ItemStack(BlockRegistry.SMOOTH_BETWEENSTONE));

		OreDictionary.registerOre("sand", new ItemStack(BlockRegistry.SILT));

		OreDictionary.registerOre("workbench", new ItemStack(BlockRegistry.WEEDWOOD_WORKBENCH));

		OreDictionary.registerOre("chest", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));
		OreDictionary.registerOre("chestWood", new ItemStack(BlockRegistry.WEEDWOOD_CHEST));

		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.POISON_IVY));
		OreDictionary.registerOre("vine", new ItemStack(BlockRegistry.THORNS));

		OreDictionary.registerOre("sugarcane", new ItemStack(ItemRegistry.SWAMP_REED_ITEM));

		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ASTATOS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BETWEEN_YOU_AND_ME));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.CHRISTMAS_ON_THE_MARSH));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.THE_EXPLORER));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.HAG_DANCE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.LONELY_FIRE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.MYSTERIOUS_RECORD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ANCIENT));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.BENEATH_A_GREEN_SKY));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.DJ_WIGHTS_MIXTAPE));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.ONWARDS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.STUCK_IN_THE_MUD));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WANDERING_WISPS));
		OreDictionary.registerOre("record", new ItemStack(ItemRegistry.WATERLOGGED));
	}

    private static boolean containsItem(List<ItemStack> lst, ItemStack stack) {
        for (ItemStack s : lst) {
            if (s.getItem() == stack.getItem() && s.getItemDamage() == stack.getItemDamage())
                return true;
        }
        return false;
    }

    public static boolean isIngotFromOre(ItemStack input, ItemStack output) {
        if (input.isEmpty() || output.isEmpty()) return false;
        return isOre(input) && isIngot(output);
    }

    public static boolean isOre(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(ORES, stack);
    }

    public static boolean isIngot(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(INGOTS, stack);
    }
 
    public static boolean isNugget(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return containsItem(NUGGETS, stack);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        for (Item item : BlockRegistry.ITEM_BLOCKS) {
            registry.register(item);
        }
        for (Item item : ITEMS) {
            registry.register(item);
        }
        registerItemTypes();
        registerOreDictionary();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            TheBetweenlands.proxy.registerDefaultItemRenderer(item);
        }
    }

    public interface IMultipleItemModelDefinition {
    	/**
    	 * A map from item meta values to different item models
    	 * @return
    	 */
    	@SideOnly(Side.CLIENT)
        Map<Integer, ResourceLocation> getModels();
    }

    public interface IBlockStateItemModelDefinition {
    	/**
    	 * A maps from item meta values to blockstate variants
    	 * @return
    	 */
    	@SideOnly(Side.CLIENT)
        Map<Integer, String> getVariants();
    }

    public interface ICustomMeshCallback {

        /**
         * A callback to get a custom mesh definition
         * @return
         */
        @SideOnly(Side.CLIENT)
        ItemMeshDefinition getMeshDefinition();

    }
}
