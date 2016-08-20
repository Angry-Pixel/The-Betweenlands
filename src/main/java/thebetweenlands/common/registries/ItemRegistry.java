package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.CaseFormat;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemBLArmor;
import thebetweenlands.common.item.armor.ItemRubberBoots;
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
import thebetweenlands.common.item.food.ItemWeepingBluePetal;
import thebetweenlands.common.item.food.ItemWightHeart;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemManualHL;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemBLRecord;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemSwampReed;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.item.misc.TestItem;
import thebetweenlands.common.item.shields.ItemWeedwoodShield;
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.item.tools.ItemSyrmoriteShears;
import thebetweenlands.common.lib.ModInfo;

public class ItemRegistry {
	//generic
	public static final Item ITEMS_MISC = new ItemMisc().setCreativeTab(BLCreativeTabs.ITEMS);
	public static final Item ITEMS_CRUSHED = new ItemCrushed().setCreativeTab(BLCreativeTabs.HERBLORE);
	public static final Item ITEMS_PLANT_DROP = new ItemPlantDrop().setCreativeTab(BLCreativeTabs.HERBLORE);
	public static final Item SWAMP_TALISMAN = new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
	//food
	public static final Item SAP_BALL = new ItemSapBall();
	public static final ItemRottenFood ROTTEN_FOOD = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
	//public static final Item middleFruitSeeds = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.middleFruitBush, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".middleFruitSeeds");
	//public static final Item spores = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.fungusCrop, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".spores");
	//public static final Item aspectrusCropSeed = new ItemAspectrusCropSeed(0, 0F).setUnlocalizedName(ModInfo.NAME_PREFIX + ".aspectrusSeeds");
	public static final Item ANGLER_MEAT_RAW = new ItemBLFood(4, 0.4F, false);
	public static final Item ANGLER_MEAT_COOKED = new ItemBLFood(8, 0.8F, false);
	public static final Item FROG_LEGS_RAW = new ItemBLFood(3, 0.4F, false);
	public static final Item FROG_LEGS_COOKED = new ItemBLFood(6, 0.8F, false);
	public static final Item SNAIL_FLESH_RAW = new ItemBLFood(3, 0.4F, false);
	public static final Item SNAIL_FLESH_COOKED = new ItemBLFood(6, 0.9F, false);
	public static final Item REED_DONUT = new ItemBLFood(6, 0.6F, false);
	public static final Item JAM_DONUT = new ItemBLFood(10, 0.6F, false);
	public static final Item GERTS_DONUT = new ItemGertsDonut();
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
	public static final Item FRIED_SWAMP_KELP = new ItemBLFood(5, 0.6F, false);
	public static final Item FORBIDDEN_FIG = new ItemForbiddenFig();
	public static final Item CANDY_BLUE = new ItemBLFood(3, 1.0F, false);
	public static final Item CANDY_RED = new ItemBLFood(3, 1.0F, false);
	public static final Item CANDY_YELLOW = new ItemBLFood(3, 1.0F, false);
	public static final Item CHIROMAW_WING = new ItemChiromawWing();
	//armor
	public static final Item SYRMORITE_HELMET = new ItemBLArmor(BLMaterialRegistry.ARMOR_SYRMORITE, 0, EntityEquipmentSlot.HEAD, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item SYRMORITE_CHESTPLATE = new ItemBLArmor(BLMaterialRegistry.ARMOR_SYRMORITE, 1, EntityEquipmentSlot.CHEST, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item SYRMORITE_LEGGINGS = new ItemBLArmor(BLMaterialRegistry.ARMOR_SYRMORITE, 2, EntityEquipmentSlot.LEGS, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item SYRMORITE_BOOTS = new ItemBLArmor(BLMaterialRegistry.ARMOR_SYRMORITE, 3, EntityEquipmentSlot.FEET, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item LURKER_SKIN_HELMET = new ItemBLArmor(BLMaterialRegistry.ARMOR_LURKER_SKIN, 0, EntityEquipmentSlot.HEAD, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item LURKER_SKIN_CHESTPLATE = new ItemBLArmor(BLMaterialRegistry.ARMOR_LURKER_SKIN, 1, EntityEquipmentSlot.CHEST, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item LURKER_SKIN_LEGGINGS = new ItemBLArmor(BLMaterialRegistry.ARMOR_LURKER_SKIN, 2, EntityEquipmentSlot.LEGS, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item LURKER_SKIN_BOOTS = new ItemBLArmor(BLMaterialRegistry.ARMOR_LURKER_SKIN, 3, EntityEquipmentSlot.FEET, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_HELMET = new ItemBLArmor(BLMaterialRegistry.ARMOR_BONE, 0, EntityEquipmentSlot.HEAD, "bone").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_CHESTPLATE = new ItemBLArmor(BLMaterialRegistry.ARMOR_BONE, 1, EntityEquipmentSlot.CHEST, "bone").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_LEGGINGS = new ItemBLArmor(BLMaterialRegistry.ARMOR_BONE, 2, EntityEquipmentSlot.LEGS, "bone").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_BOOTS = new ItemBLArmor(BLMaterialRegistry.ARMOR_BONE, 3, EntityEquipmentSlot.FEET, "bone").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_HELMET = new ItemBLArmor(BLMaterialRegistry.ARMOR_VALONITE, 0, EntityEquipmentSlot.HEAD, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_CHESTPLATE = new ItemBLArmor(BLMaterialRegistry.ARMOR_VALONITE, 1, EntityEquipmentSlot.CHEST, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_LEGGINGS = new ItemBLArmor(BLMaterialRegistry.ARMOR_VALONITE, 2, EntityEquipmentSlot.LEGS, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_BOOTS = new ItemBLArmor(BLMaterialRegistry.ARMOR_VALONITE, 3, EntityEquipmentSlot.FEET, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item RUBBER_BOOTS = new ItemRubberBoots().setCreativeTab(BLCreativeTabs.GEARS);
	//tools
	public static final Item WEEDWOOD_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item WEEDWOOD_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item BONE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_BONE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_SWORD = new ItemBLSword(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_SHOVEL = new ItemBLShovel(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_AXE = new ItemBLAxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item VALONITE_PICKAXE = new ItemBLPickaxe(BLMaterialRegistry.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
	public static final Item OCTINE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_OCTINE);
	public static final Item VALONITE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_VALONITE);
	public static final Item WEEDWOOD_SHIELD = new ItemWeedwoodShield();
	public static final Item SYRMORITE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_SYRMORITE);
	public static final Item BONE_SHIELD = new ItemBLShield(BLMaterialRegistry.TOOL_BONE);
	public static final Item MANUAL_HL = new ItemManualHL();
	public static final Item SYRMORITE_SHEARS = new ItemSyrmoriteShears();


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

	public static final Item TEST_ITEM = new TestItem();

	public final static List<Item> ITEMS = new ArrayList<Item>();

	public void preInit() {
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (field.getType().isAssignableFrom(Item.class)) {
					Item item = (Item) field.get(this);
					registerItem(item, field.getName());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (Item item : this.ITEMS) {
			TheBetweenlands.proxy.registerDefaultItemRenderer(item);
		}
	}

	private void registerItem(Item item, String fieldName) {
		String itemName = fieldName.toLowerCase(Locale.ENGLISH);
		GameRegistry.register(item.setRegistryName(ModInfo.ID, itemName).setUnlocalizedName(ModInfo.NAME_PREFIX + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, itemName)));
		ITEMS.add(item);
	}

	public interface ISubItemsItem {
		Map<Integer, ResourceLocation> getModels();
	}

	public interface ISingleJsonSubItems{
		List<String> getTypes();
	}
}
