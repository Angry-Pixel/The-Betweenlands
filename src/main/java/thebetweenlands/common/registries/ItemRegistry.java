package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CaseFormat;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemBoneArmor;
import thebetweenlands.common.item.armor.ItemExplorersHat;
import thebetweenlands.common.item.armor.ItemLurkerSkinArmor;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.item.armor.ItemSkullMask;
import thebetweenlands.common.item.armor.ItemSyrmoriteArmor;
import thebetweenlands.common.item.armor.ItemValoniteArmor;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
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
import thebetweenlands.common.item.food.ItemNettleSoup;
import thebetweenlands.common.item.food.ItemRottenFood;
import thebetweenlands.common.item.food.ItemSapBall;
import thebetweenlands.common.item.food.ItemSapJello;
import thebetweenlands.common.item.food.ItemTaintedPotion;
import thebetweenlands.common.item.food.ItemTangledRoot;
import thebetweenlands.common.item.food.ItemWeepingBluePetal;
import thebetweenlands.common.item.food.ItemWightHeart;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.herblore.ItemManualHL;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemAmuletSlot;
import thebetweenlands.common.item.misc.ItemAngryPebble;
import thebetweenlands.common.item.misc.ItemBLRecord;
import thebetweenlands.common.item.misc.ItemCavingRope;
import thebetweenlands.common.item.misc.ItemDoorBetweenlands;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.item.misc.ItemGlue;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.misc.ItemLoreScrap;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.item.misc.ItemMossBed;
import thebetweenlands.common.item.misc.ItemOctineIngot;
import thebetweenlands.common.item.misc.ItemPyradFlame;
import thebetweenlands.common.item.misc.ItemRope;
import thebetweenlands.common.item.misc.ItemShimmerStone;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.item.misc.ItemTarminion;
import thebetweenlands.common.item.misc.ItemWeedwoodRowboat;
import thebetweenlands.common.item.misc.ItemWeedwoodSign;
import thebetweenlands.common.item.misc.LocationDebugItem;
import thebetweenlands.common.item.misc.TestItem;
import thebetweenlands.common.item.shields.ItemWeedwoodShield;
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLBucketFilled;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.item.tools.ItemLootSword;
import thebetweenlands.common.item.tools.ItemNet;
import thebetweenlands.common.item.tools.ItemPestle;
import thebetweenlands.common.item.tools.ItemShockwaveSword;
import thebetweenlands.common.item.tools.ItemSickle;
import thebetweenlands.common.item.tools.ItemSpecificBucket;
import thebetweenlands.common.item.tools.ItemSwiftPick;
import thebetweenlands.common.item.tools.ItemSyrmoriteBucketEmpty;
import thebetweenlands.common.item.tools.ItemSyrmoriteBucketSolidRubber;
import thebetweenlands.common.item.tools.ItemSyrmoriteShears;
import thebetweenlands.common.item.tools.ItemVoodooDoll;
import thebetweenlands.common.item.tools.ItemWeedwoodBucketEmpty;
import thebetweenlands.common.item.tools.ItemWeedwoodBucketInfusion;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.item.tools.bow.ItemBLArrow;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.config.ConfigHandler;

public class ItemRegistry {
	public final static Set<Item> ITEMS = new HashSet<Item>();

	private ItemRegistry() {

	}

	private static final List<ItemStack> ORES = new ArrayList<ItemStack>();
	private static final List<ItemStack> INGOTS = new ArrayList<ItemStack>();
	//generic
	public static final Item ITEMS_MISC = new ItemMisc().setCreativeTab(BLCreativeTabs.ITEMS);
	public static final Item ITEMS_CRUSHED = new ItemCrushed().setCreativeTab(BLCreativeTabs.HERBLORE);
	public static final Item ITEMS_PLANT_DROP = new ItemPlantDrop().setCreativeTab(BLCreativeTabs.HERBLORE);
	public static final Item SWAMP_TALISMAN = new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
	public static final Item WEEDWOOD_ROWBOAT = new ItemWeedwoodRowboat();
	//food
	public static final Item SAP_BALL = new ItemSapBall();
	public static final ItemRottenFood ROTTEN_FOOD = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
	public static final Item MIDDLE_FRUIT_BUSH_SEEDS = new ItemMiddleFruitBushSeeds();
	public static final Item SPORES = new ItemSpores();
	public static final Item ASPECTRUS_SEEDS = new ItemAspectrusSeeds();
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
	public static final Item KRAKEN_TENTACLE = new ItemBLFood(8, 0.9F, false);
	public static final Item KRAKEN_CALAMARI = new ItemBLFood(14, 1.2F, false);
	public static final Item MIDDLE_FRUIT = new ItemBLFood(6, 0.6F, false);
	public static final Item MINCE_PIE = new ItemBLFood(4, 0.85F, false);
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
	public static final Item CANDY_BLUE = new ItemBLFood(3, 1.0F, false);
	public static final Item CANDY_RED = new ItemBLFood(3, 1.0F, false);
	public static final Item CANDY_YELLOW = new ItemBLFood(3, 1.0F, false);
	public static final Item CHIROMAW_WING = new ItemChiromawWing();
	public static final Item TANGLED_ROOT = new ItemTangledRoot();

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
	public static final Item SKULL_MASK = new ItemSkullMask();
	public static final Item EXPLORERS_HAT = new ItemExplorersHat();

	//TOOLS
	public static final Item WEEDWOOD_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_OCTINE) {
		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(stack, target, attacker);
			if (attacker.worldObj.rand.nextInt(CircleGemHelper.getGem(attacker.getHeldItem(attacker.getActiveHand())) == CircleGemType.CRIMSON ? 3 : 4) == 0) {
				target.setFire(10);
			}
			return false;
		}
	}.setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_OCTINE){
		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(stack, target, attacker);
			if (attacker.worldObj.rand.nextInt(4) == 0) {
				target.setFire(10);
			}
			return false;
		}
	}.setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_OCTINE){
		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(stack, target, attacker);
			if (attacker.worldObj.rand.nextInt(4) == 0) {
				target.setFire(10);
			}
			return false;
		}
	}.setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_OCTINE){
		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(stack, target, attacker);
			if (attacker.worldObj.rand.nextInt(4) == 0) {
				target.setFire(10);
			}
			return false;
		}
	}.setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_OCTINE);
	public static final Item VALONITE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_VALONITE);
	public static final Item WEEDWOOD_SHIELD = new ItemWeedwoodShield();
	public static final Item SYRMORITE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_SYRMORITE);
	public static final Item BONE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_BONE);
	public static final Item DENTROTHYST_SHIELD_GREEN = new ItemBLShield(BLMaterialRegistry.TOOL_DENTROTHYST);
	public static final Item DENTROTHYST_SHIELD_GREEN_POLISHED = new ItemBLShield(BLMaterialRegistry.TOOL_DENTROTHYST);
	public static final Item DENTROTHYST_SHIELD_ORANGE = new ItemBLShield(BLMaterialRegistry.TOOL_DENTROTHYST);
	public static final Item DENTROTHYST_SHIELD_ORANGE_POLISHED = new ItemBLShield(BLMaterialRegistry.TOOL_DENTROTHYST);
	public static final Item LURKER_SKIN_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_LURKER_SKIN);
	public static final Item MANUAL_HL = new ItemManualHL();
	public static final Item SYRMORITE_SHEARS = new ItemSyrmoriteShears();
	public static final Item SICKLE = new ItemSickle();
	public static final Item SHOCKWAVE_SWORD = new ItemShockwaveSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.DEFAULT).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item POISONED_ANGLER_TOOTH_ARROW = new ItemBLArrow(EnumArrowType.ANGLER_POISON).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_ARROW = new ItemBLArrow(EnumArrowType.OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BASILISK_ARROW = new ItemBLArrow(EnumArrowType.BASILISK).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_BOW = new ItemBLBow().setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WIGHTS_BANE = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityWight.class).setMaxDamage(32);
	public static final Item SLUDGE_SLICER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntitySludge.class).setMaxDamage(32);
	public static final Item CRITTER_CRUNCHER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntityBloodSnail.class, EntityDragonFly.class, EntityFirefly.class, EntityLeech.class, EntityMireSnail.class, EntitySporeling.class, EntityTermite.class).setMaxDamage(32);
	public static final Item HAG_HACKER = new ItemLootSword(BLMaterialRegistry.TOOL_WEEDWOOD).addInstantKills(EntitySwampHag.class).setMaxDamage(32);
	public static final Item VOODOO_DOLL = new ItemVoodooDoll();
	public static final Item SWIFT_PICK = new ItemSwiftPick();
	
	//BUCKETS
	public static final Item WEEDWOOD_BUCKET = new ItemWeedwoodBucketEmpty();
	public static final ItemBLBucketFilled WEEDWOOD_BUCKET_FILLED = new ItemBLBucketFilled(WEEDWOOD_BUCKET);

	public static final Item WEEDWOOD_BUCKET_RUBBER = new ItemSpecificBucket(WEEDWOOD_BUCKET, FluidRegistry.RUBBER);
	public static final Item WEEDWOOD_BUCKET_INFUSION = new ItemWeedwoodBucketInfusion();

	public static final Item SYRMORITE_BUCKET = new ItemSyrmoriteBucketEmpty();
	public static final ItemBLBucketFilled SYRMORITE_BUCKET_FILLED = new ItemBLBucketFilled(SYRMORITE_BUCKET);

	public static final Item SYRMORITE_BUCKET_RUBBER = new ItemSpecificBucket(SYRMORITE_BUCKET, FluidRegistry.RUBBER);
	public static final Item SYRMORITE_BUCKET_SOLID_RUBBER = new ItemSyrmoriteBucketSolidRubber();

	public static final Item SYRMORITE_BUCKET_PLANT_TONIC = new ItemPlantTonic(new ItemStack(SYRMORITE_BUCKET));
	public static final Item WEEDWOOD_BUCKET_PLANT_TONIC = new ItemPlantTonic(new ItemStack(WEEDWOOD_BUCKET));
	
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
	public static final Item WEEDWOOD_SIGN_ITEM = new ItemWeedwoodSign();
	public static final Item CRIMSON_MIDDLE_GEM = new ItemGem(CircleGemType.CRIMSON);
	public static final Item AQUA_MIDDLE_GEM = new ItemGem(CircleGemType.AQUA);
	public static final Item GREEN_MIDDLE_GEM = new ItemGem(CircleGemType.GREEN);
	public static final Item PESTLE = new ItemPestle();
	public static final Item LIFE_CRYSTAL = new ItemLifeCrystal();
	public static final Item TEST_ITEM = new TestItem();
	public static final Item LOCATION_DEBUG = new LocationDebugItem().setCreativeTab(null);
	public static final Item PYRAD_FLAME = new ItemPyradFlame();
	public static final Item NET = new ItemNet();
	public static final Item GECKO = new ItemMob("gecko");
	public static final Item FIREFLY = new ItemMob("firefly");
	public static final Item SHIMMER_STONE = new ItemShimmerStone();
	public static final Item TARMINION = new ItemTarminion();
	public static final Item MOSS_BED_ITEM = new ItemMossBed();
	public static final Item SLUDGE_BALL = new Item().setCreativeTab(BLCreativeTabs.ITEMS);
	
	public static final ItemDentrothystVial DENTROTHYST_VIAL = new ItemDentrothystVial();
	public static final ItemAspectVial ASPECT_VIAL = new ItemAspectVial();

	public static final Item GLUE = new ItemGlue();
	
	public static final Item AMULET = new ItemAmulet();
	public static final Item AMULET_SLOT = new ItemAmuletSlot();
	public static final Item LURKER_SKIN_POUCH = new ItemLurkerSkinPouch();
	public static final Item CAVING_ROPE = new ItemCavingRope();
	public static final Item ROPE_ITEM = new ItemRope();
	public static final Item RING_OF_POWER = new ItemRingOfPower();
	public static final Item RING_OF_FLIGHT = new ItemRingOfFlight();
	public static final Item RING_OF_RECRUITMENT = new ItemRingOfRecruitment();
	public static final Item RING_OF_SUMMONING = new ItemRingOfSummoning();
	public static final Item ANGRY_PEBBLE = new ItemAngryPebble();
	public static final Item LORE_SCRAP = new ItemLoreScrap();
	public static final ItemTaintedPotion TAINTED_POTION = new ItemTaintedPotion();
	public static final Item OCTINE_INGOT = new ItemOctineIngot();
	
	public static void preInit() {
		try {
			for (Field field : ItemRegistry.class.getDeclaredFields()) {
				if (field.get(null) instanceof Item) {
					Item item = (Item) field.get(null);
					registerItem(item, field.getName());

					if (ConfigHandler.debug && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						if (item.getCreativeTab() == null)
							System.out.println(String.format("Warning: Item %s doesn't have a creative tab", item.getUnlocalizedName()));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		registerProperties();

		for (Item item : ITEMS) {
			TheBetweenlands.proxy.registerDefaultItemRenderer(item);
		}
	}

	private static void registerItem(Item item, String fieldName) {
		String itemName = fieldName.toLowerCase(Locale.ENGLISH);
		GameRegistry.register(item.setRegistryName(ModInfo.ID, itemName).setUnlocalizedName(ModInfo.NAME_PREFIX + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, itemName)));
		ITEMS.add(item);
	}

	public interface ISubItemsItem {
		Map<Integer, ResourceLocation> getModels();
	}

	public interface ISingleJsonSubItems {
		List<String> getTypes();
	}

	private static void registerProperties() {
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
	}

	private static boolean containsItem(List<ItemStack> lst, ItemStack stack) {
		for (ItemStack s : lst) {
			if (s.getItem() == stack.getItem() && s.getItemDamage() == stack.getItemDamage())
				return true;
		}
		return false;
	}

	public static boolean isIngotFromOre(ItemStack input, ItemStack output) {
		if (input == null || output == null) return false;
		return isOre(input) && isIngot(output);
	}

	public static boolean isOre(ItemStack stack) {
		if (stack == null) return false;
		return containsItem(ORES, stack);
	}

	public static boolean isIngot(ItemStack stack) {
		if (stack == null) return false;
		return containsItem(INGOTS, stack);
	}
}
