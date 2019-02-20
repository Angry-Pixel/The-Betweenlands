package thebetweenlands.common.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import thebetweenlands.common.item.BLItemGroups;
import thebetweenlands.common.item.food.ItemRottenFood;
import thebetweenlands.common.item.food.ItemTaintedPotion;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.herblore.ItemDentrothystVial;
import thebetweenlands.common.item.herblore.ItemElixir;
import thebetweenlands.common.item.misc.ItemOctineIngot;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.lib.ModInfo;

@ObjectHolder(ModInfo.ID)
public class ItemRegistry {
	//@ObjectHolder("items_misc")
	//public static final Item ITEMS_MISC = null;

	//@ObjectHolder("items_crushed")
	//public static final Item ITEMS_CRUSHED = null;

	//@ObjectHolder("items_plant_drop")
	//public static final Item ITEMS_PLANT_DROP = null;

	//@ObjectHolder("swamp_talisman")
	//public static final Item SWAMP_TALISMAN = null;

	//@ObjectHolder("weedwood_rowboat")
	//public static final Item WEEDWOOD_ROWBOAT = null;

	//@ObjectHolder("dentrothyst_shard_orange")
	//public static final Item DENTROTHYST_SHARD_ORANGE = null;

	//@ObjectHolder("dentrothyst_shard_green")
	//public static final Item DENTROTHYST_SHARD_GREEN = null;

	//@ObjectHolder("sap_ball")
	//public static final Item SAP_BALL = null;

	//@ObjectHolder("rotten_food")
	//public static final ItemRottenFood ROTTEN_FOOD = null;

	//@ObjectHolder("middle_fruit_bush_seeds")
	//public static final Item MIDDLE_FRUIT_BUSH_SEEDS = null;

	//@ObjectHolder("spores")
	//public static final Item SPORES = null;

	//@ObjectHolder("aspectrus_seeds")
	//public static final Item ASPECTRUS_SEEDS = null;

	//@ObjectHolder("mire_snail_egg")
	//public static final Item MIRE_SNAIL_EGG = null;

	//@ObjectHolder("mire_snail_egg_cooked")
	//public static final Item MIRE_SNAIL_EGG_COOKED = null;

	//@ObjectHolder("angler_meat_raw")
	//public static final Item ANGLER_MEAT_RAW = null;

	//@ObjectHolder("angler_meat_cooked")
	//public static final Item ANGLER_MEAT_COOKED = null;

	//@ObjectHolder("frog_legs_raw")
	//public static final Item FROG_LEGS_RAW = null;

	//@ObjectHolder("frog_legs_cooked")
	//public static final Item FROG_LEGS_COOKED = null;

	//@ObjectHolder("snail_flesh_raw")
	//public static final Item SNAIL_FLESH_RAW = null;

	//@ObjectHolder("snail_flesh_cooked")
	//public static final Item SNAIL_FLESH_COOKED = null;

	//@ObjectHolder("reed_donut")
	//public static final Item REED_DONUT = null;

	//@ObjectHolder("jam_donut")
	//public static final Item JAM_DONUT = null;

	//@ObjectHolder("gerts_donut")
	//public static final Item GERTS_DONUT = null;

	//@ObjectHolder("aspectrus_fruit")
	//public static final Item ASPECTRUS_FRUIT = null;

	//@ObjectHolder("kraken_tentacle")
	//public static final Item KRAKEN_TENTACLE = null;

	//@ObjectHolder("kraken_calamari")
	//public static final Item KRAKEN_CALAMARI = null;

	//@ObjectHolder("middle_fruit")
	//public static final Item MIDDLE_FRUIT = null;

	//@ObjectHolder("mince_pie")
	//public static final Item MINCE_PIE = null;

	//@ObjectHolder("christmas_pudding")
	//public static final Item CHRISTMAS_PUDDING = null;

	//@ObjectHolder("candy_cane")
	//public static final Item CANDY_CANE = null;

	//@ObjectHolder("weeping_blue_petal")
	//public static final Item WEEPING_BLUE_PETAL = null;

	//@ObjectHolder("wight_heart")
	//public static final Item WIGHT_HEART = null;

	//@ObjectHolder("yellow_dotted_fungus")
	//public static final Item YELLOW_DOTTED_FUNGUS = null;

	//@ObjectHolder("silt_crab_claw")
	//public static final Item SILT_CRAB_CLAW = null;

	//@ObjectHolder("crab_stick")
	//public static final Item CRAB_STICK = null;

	//@ObjectHolder("nettle_soup")
	//public static final Item NETTLE_SOUP = null;

	//@ObjectHolder("sludge_jello")
	//public static final Item SLUDGE_JELLO = null;

	//@ObjectHolder("middle_fruit_jello")
	//public static final Item MIDDLE_FRUIT_JELLO = null;

	//@ObjectHolder("sap_jello")
	//public static final Item SAP_JELLO = null;

	//@ObjectHolder("marshmallow")
	//public static final Item MARSHMALLOW = null;

	//@ObjectHolder("marshmallow_pink")
	//public static final Item MARSHMALLOW_PINK = null;

	//@ObjectHolder("flat_head_mushroom_item")
	//public static final Item FLAT_HEAD_MUSHROOM_ITEM = null;

	//@ObjectHolder("black_hat_mushroom_item")
	//public static final Item BLACK_HAT_MUSHROOM_ITEM = null;

	//@ObjectHolder("bulb_capped_mushroom_item")
	//public static final Item BULB_CAPPED_MUSHROOM_ITEM = null;

	//@ObjectHolder("swamp_reed_item")
	//public static final Item SWAMP_REED_ITEM = null;

	//@ObjectHolder("swamp_kelp_item")
	//public static final Item SWAMP_KELP_ITEM = null;

	//@ObjectHolder("fried_swamp_kelp")
	//public static final Item FRIED_SWAMP_KELP = null;

	//@ObjectHolder("forbidden_fig")
	//public static final Item FORBIDDEN_FIG = null;

	//@ObjectHolder("candy_blue")
	//public static final Item CANDY_BLUE = null;

	//@ObjectHolder("candy_red")
	//public static final Item CANDY_RED = null;

	//@ObjectHolder("candy_yellow")
	//public static final Item CANDY_YELLOW = null;

	//@ObjectHolder("chiromaw_wing")
	//public static final Item CHIROMAW_WING = null;

	//@ObjectHolder("tangled_root")
	//public static final Item TANGLED_ROOT = null;

	//@ObjectHolder("mire_scramble")
	//public static final Item MIRE_SCRAMBLE = null;

	//@ObjectHolder("weeping_blue_petal_salad")
	//public static final Item WEEPING_BLUE_PETAL_SALAD = null;

	//@ObjectHolder("nibblestick")
	//public static final Item NIBBLESTICK = null;

	//@ObjectHolder("spirit_fruit")
	//public static final Item SPIRIT_FRUIT = null;

	//@ObjectHolder("bone_helmet")
	//public static final Item BONE_HELMET = null;

	//@ObjectHolder("bone_chestplate")
	//public static final Item BONE_CHESTPLATE = null;

	//@ObjectHolder("bone_leggings")
	//public static final Item BONE_LEGGINGS = null;

	//@ObjectHolder("bone_boots")
	//public static final Item BONE_BOOTS = null;

	//@ObjectHolder("lurker_skin_helmet")
	//public static final Item LURKER_SKIN_HELMET = null;

	//@ObjectHolder("lurker_skin_chestplate")
	//public static final Item LURKER_SKIN_CHESTPLATE = null;

	//@ObjectHolder("lurker_skin_leggings")
	//public static final Item LURKER_SKIN_LEGGINGS = null;

	//@ObjectHolder("lurker_skin_boots")
	//public static final Item LURKER_SKIN_BOOTS = null;

	//@ObjectHolder("syrmorite_helmet")
	//public static final Item SYRMORITE_HELMET = null;

	//@ObjectHolder("syrmorite_chestplate")
	//public static final Item SYRMORITE_CHESTPLATE = null;

	//@ObjectHolder("syrmorite_leggings")
	//public static final Item SYRMORITE_LEGGINGS = null;

	//@ObjectHolder("syrmorite_boots")
	//public static final Item SYRMORITE_BOOTS = null;

	//@ObjectHolder("valonite_helmet")
	//public static final Item VALONITE_HELMET = null;

	//@ObjectHolder("valonite_chestplate")
	//public static final Item VALONITE_CHESTPLATE = null;

	//@ObjectHolder("valonite_leggings")
	//public static final Item VALONITE_LEGGINGS = null;

	//@ObjectHolder("valonite_boots")
	//public static final Item VALONITE_BOOTS = null;

	//@ObjectHolder("rubber_boots")
	//public static final Item RUBBER_BOOTS = null;

	//@ObjectHolder("marsh_runner_boots")
	//public static final Item MARSH_RUNNER_BOOTS = null;

	//@ObjectHolder("skull_mask")
	//public static final Item SKULL_MASK = null;

	//@ObjectHolder("explorers_hat")
	//public static final Item EXPLORERS_HAT = null;

	//@ObjectHolder("spirit_tree_face_large_mask")
	//public static final Item SPIRIT_TREE_FACE_LARGE_MASK = null;

	//@ObjectHolder("spirit_tree_face_small_mask")
	//public static final Item SPIRIT_TREE_FACE_SMALL_MASK = null;

	//@ObjectHolder("spirit_tree_face_small_mask_animated")
	//public static final Item SPIRIT_TREE_FACE_SMALL_MASK_ANIMATED = null;

	//@ObjectHolder("weedwood_sword")
	//public static final Item WEEDWOOD_SWORD = null;

	//@ObjectHolder("weedwood_shovel")
	//public static final Item WEEDWOOD_SHOVEL = null;

	//@ObjectHolder("weedwood_axe")
	//public static final Item WEEDWOOD_AXE = null;

	//@ObjectHolder("weedwood_pickaxe")
	//public static final Item WEEDWOOD_PICKAXE = null;

	//@ObjectHolder("bone_sword")
	//public static final Item BONE_SWORD = null;

	//@ObjectHolder("bone_shovel")
	//public static final Item BONE_SHOVEL = null;

	//@ObjectHolder("bone_axe")
	//public static final Item BONE_AXE = null;

	//@ObjectHolder("bone_pickaxe")
	//public static final Item BONE_PICKAXE = null;

	//@ObjectHolder("octine_sword")
	//public static final Item OCTINE_SWORD = null;

	//@ObjectHolder("octine_shovel")
	//public static final Item OCTINE_SHOVEL = null;

	//@ObjectHolder("octine_axe")
	//public static final Item OCTINE_AXE = null;

	//@ObjectHolder("octine_pickaxe")
	//public static final Item OCTINE_PICKAXE = null;

	//@ObjectHolder("valonite_sword")
	//public static final Item VALONITE_SWORD = null;

	//@ObjectHolder("valonite_shovel")
	//public static final Item VALONITE_SHOVEL = null;

	//@ObjectHolder("valonite_axe")
	//public static final Item VALONITE_AXE = null;

	//@ObjectHolder("valonite_pickaxe")
	//public static final Item VALONITE_PICKAXE = null;

	//@ObjectHolder("octine_shield")
	//public static final Item OCTINE_SHIELD = null;

	//@ObjectHolder("valonite_shield")
	//public static final Item VALONITE_SHIELD = null;

	//@ObjectHolder("weedwood_shield")
	//public static final Item WEEDWOOD_SHIELD = null;

	//@ObjectHolder("living_weedwood_shield")
	//public static final Item LIVING_WEEDWOOD_SHIELD = null;

	//@ObjectHolder("syrmorite_shield")
	//public static final Item SYRMORITE_SHIELD = null;

	//@ObjectHolder("bone_shield")
	//public static final Item BONE_SHIELD = null;

	//@ObjectHolder("dentrothyst_shield_green")
	//public static final Item DENTROTHYST_SHIELD_GREEN = null;

	//@ObjectHolder("dentrothyst_shield_green_polished")
	//public static final Item DENTROTHYST_SHIELD_GREEN_POLISHED = null;

	//@ObjectHolder("dentrothyst_shield_orange")
	//public static final Item DENTROTHYST_SHIELD_ORANGE = null;

	//@ObjectHolder("dentrothyst_shield_orange_polished")
	//public static final Item DENTROTHYST_SHIELD_ORANGE_POLISHED = null;

	//@ObjectHolder("lurker_skin_shield")
	//public static final Item LURKER_SKIN_SHIELD = null;

	//@ObjectHolder("manual_hl")
	//public static final Item MANUAL_HL = null;

	//@ObjectHolder("syrmorite_shears")
	//public static final Item SYRMORITE_SHEARS = null;

	//@ObjectHolder("sickle")
	//public static final Item SICKLE = null;

	//@ObjectHolder("shockwave_sword")
	//public static final Item SHOCKWAVE_SWORD = null;

	//@ObjectHolder("angler_tooth_arrow")
	//public static final Item ANGLER_TOOTH_ARROW = null;

	//@ObjectHolder("poisoned_angler_tooth_arrow")
	//public static final Item POISONED_ANGLER_TOOTH_ARROW = null;

	//@ObjectHolder("octine_arrow")
	//public static final Item OCTINE_ARROW = null;

	//@ObjectHolder("basilisk_arrow")
	//public static final Item BASILISK_ARROW = null;

	//@ObjectHolder("weedwood_bow")
	//public static final Item WEEDWOOD_BOW = null;

	//@ObjectHolder("wights_bane")
	//public static final Item WIGHTS_BANE = null;

	//@ObjectHolder("sludge_slicer")
	//public static final Item SLUDGE_SLICER = null;

	//@ObjectHolder("critter_cruncher")
	//public static final Item CRITTER_CRUNCHER = null;

	//@ObjectHolder("hag_hacker")
	//public static final Item HAG_HACKER = null;

	//@ObjectHolder("voodoo_doll")
	//public static final Item VOODOO_DOLL = null;

	//@ObjectHolder("swift_pick")
	//public static final Item SWIFT_PICK = null;

	//@ObjectHolder("bl_bucket")
	//public static final ItemBLBucket BL_BUCKET = null;

	//@ObjectHolder("bl_bucket_rubber")
	//public static final Item BL_BUCKET_RUBBER = null;

	//@ObjectHolder("bl_bucket_infusion")
	//public static final Item BL_BUCKET_INFUSION = null;

	//@ObjectHolder("bl_bucket_plant_tonic")
	//public static final Item BL_BUCKET_PLANT_TONIC = null;

	//@ObjectHolder("syrmorite_bucket_solid_rubber")
	//public static final Item SYRMORITE_BUCKET_SOLID_RUBBER = null;

	//@ObjectHolder("astatos")
	//public static final Item ASTATOS = null;

	//@ObjectHolder("between_you_and_me")
	//public static final Item BETWEEN_YOU_AND_ME = null;

	//@ObjectHolder("christmas_on_the_marsh")
	//public static final Item CHRISTMAS_ON_THE_MARSH = null;

	//@ObjectHolder("the_explorer")
	//public static final Item THE_EXPLORER = null;

	//@ObjectHolder("hag_dance")
	//public static final Item HAG_DANCE = null;

	//@ObjectHolder("lonely_fire")
	//public static final Item LONELY_FIRE = null;

	//@ObjectHolder("mysterious_record")
	//public static final Item MYSTERIOUS_RECORD = null;

	//@ObjectHolder("ancient")
	//public static final Item ANCIENT = null;

	//@ObjectHolder("beneath_a_green_sky")
	//public static final Item BENEATH_A_GREEN_SKY = null;

	//@ObjectHolder("dj_wights_mixtape")
	//public static final Item DJ_WIGHTS_MIXTAPE = null;

	//@ObjectHolder("onwards")
	//public static final Item ONWARDS = null;

	//@ObjectHolder("stuck_in_the_mud")
	//public static final Item STUCK_IN_THE_MUD = null;

	//@ObjectHolder("wandering_wisps")
	//public static final Item WANDERING_WISPS = null;

	//@ObjectHolder("waterlogged")
	//public static final Item WATERLOGGED = null;

	//@ObjectHolder("weedwood_door_item")
	//public static final Item WEEDWOOD_DOOR_ITEM = null;

	//@ObjectHolder("syrmorite_door_item")
	//public static final Item SYRMORITE_DOOR_ITEM = null;

	//@ObjectHolder("rubber_tree_plank_door_item")
	//public static final Item RUBBER_TREE_PLANK_DOOR_ITEM = null;

	//@ObjectHolder("giant_root_plank_door_item")
	//public static final Item GIANT_ROOT_PLANK_DOOR_ITEM = null;

	//@ObjectHolder("hearthgrove_plank_door_item")
	//public static final Item HEARTHGROVE_PLANK_DOOR_ITEM = null;

	//@ObjectHolder("nibbletwig_plank_door_item")
	//public static final Item NIBBLETWIG_PLANK_DOOR_ITEM = null;

	//@ObjectHolder("scabyst_door_item")
	//public static final Item SCABYST_DOOR_ITEM = null;

	//@ObjectHolder("weedwood_sign_item")
	//public static final Item WEEDWOOD_SIGN_ITEM = null;

	//@ObjectHolder("crimson_middle_gem")
	//public static final Item CRIMSON_MIDDLE_GEM = null;

	//@ObjectHolder("aqua_middle_gem")
	//public static final Item AQUA_MIDDLE_GEM = null;

	//@ObjectHolder("green_middle_gem")
	//public static final Item GREEN_MIDDLE_GEM = null;

	//@ObjectHolder("pestle")
	//public static final Item PESTLE = null;

	//@ObjectHolder("life_crystal")
	//public static final Item LIFE_CRYSTAL = null;

	//@ObjectHolder("test_item")
	//public static final Item TEST_ITEM = null;

	//@ObjectHolder("location_debug")
	//public static final Item LOCATION_DEBUG = null;

	//@ObjectHolder("pyrad_flame")
	//public static final Item PYRAD_FLAME = null;

	//@ObjectHolder("net")
	//public static final Item NET = null;

	//@ObjectHolder("gecko")
	//public static final Item GECKO = null;

	//@ObjectHolder("firefly")
	//public static final Item FIREFLY = null;

	//@ObjectHolder("shimmer_stone")
	//public static final Item SHIMMER_STONE = null;

	//@ObjectHolder("tarminion")
	//public static final Item TARMINION = null;

	//@ObjectHolder("moss_bed_item")
	//public static final Item MOSS_BED_ITEM = null;

	//@ObjectHolder("sludge_ball")
	//public static final Item SLUDGE_BALL = null;

	//@ObjectHolder("elixir")
	//public static final ItemElixir ELIXIR = null;

	//@ObjectHolder("dentrothyst_vial")
	//public static final ItemDentrothystVial DENTROTHYST_VIAL = null;

	//@ObjectHolder("aspect_vial")
	//public static final ItemAspectVial ASPECT_VIAL = null;

	//@ObjectHolder("glue")
	//public static final Item GLUE = null;

	//@ObjectHolder("amulet")
	//public static final Item AMULET = null;

	//@ObjectHolder("amulet_slot")
	//public static final Item AMULET_SLOT = null;

	//@ObjectHolder("lurker_skin_pouch")
	//public static final Item LURKER_SKIN_POUCH = null;

	//@ObjectHolder("caving_rope")
	//public static final Item CAVING_ROPE = null;

	//@ObjectHolder("rope_item")
	//public static final Item ROPE_ITEM = null;

	//@ObjectHolder("ring_of_power")
	//public static final Item RING_OF_POWER = null;

	//@ObjectHolder("ring_of_flight")
	//public static final Item RING_OF_FLIGHT = null;

	//@ObjectHolder("ring_of_recruitment")
	//public static final Item RING_OF_RECRUITMENT = null;

	//@ObjectHolder("ring_of_summoning")
	//public static final Item RING_OF_SUMMONING = null;

	//@ObjectHolder("angry_pebble")
	//public static final Item ANGRY_PEBBLE = null;

	//@ObjectHolder("lore_scrap")
	//public static final Item LORE_SCRAP = null;

	//@ObjectHolder("tainted_potion")
	//public static final ItemTaintedPotion TAINTED_POTION = null;

	//@ObjectHolder("octine_ingot")
	//public static final ItemOctineIngot OCTINE_INGOT = null;

	//@ObjectHolder("mummy_bait")
	//public static final Item MUMMY_BAIT = null;

	//@ObjectHolder("sap_spit")
	//public static final Item SAP_SPIT = null;

	//@ObjectHolder("bark_amulet")
	//public static final Item BARK_AMULET = null;

	//@ObjectHolder("empty_amate_map")
	//public static final Item EMPTY_AMATE_MAP = null;

	//@ObjectHolder("amate_map")
	//public static final Item AMATE_MAP = null;

	//@ObjectHolder("bone_wayfinder")
	//public static final Item BONE_WAYFINDER = null;

	//@ObjectHolder("magic_item_magnet")
	//public static final Item MAGIC_ITEM_MAGNET = null;

	//@ObjectHolder("gem_singer")
	//public static final Item GEM_SINGER = null;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		register(new RegistryHelper<Item>() {
			@Override
			public <F extends Item> F reg(String regName, F obj, @Nullable Consumer<F> callback) {
				obj.setRegistryName(ModInfo.ID, regName);
				registry.register(obj);
				if(callback != null) callback.accept(obj);
				return obj;
			}
		});
	}

	private static void register(RegistryHelper<Item> reg) {
		//TODO 1.13 Register items here
		//The registry helper has an optional callback, e.g.
		//reg.reg("regName", new SomeItem(), i -> {
		//   i.setSomething(...);
		//});
		//which can be used to set some values

		//Register items here

		for(Consumer<RegistryHelper<Item>> blockItem : blockItems) {
			blockItem.accept(reg);
		}
	}

	private static List<Consumer<RegistryHelper<Item>>> blockItems = new ArrayList<>();

	static Consumer<Block> block(ItemGroup group) {
		return block(group, null);
	}

	static Consumer<Block> block() {
		return block((Consumer<Properties>) null);
	}

	static Consumer<Block> block(@Nullable Consumer<Properties> props) {
		return block(BLItemGroups.BLOCKS, null);
	}

	static Consumer<Block> block(ItemGroup group, @Nullable Consumer<Properties> props) {
		return block -> blockItems.add(reg -> {
			Properties itemProps = new Properties().group(group);
			if(props != null) props.accept(itemProps);
			reg.reg(block.getRegistryName().getPath(), new ItemBlock(block, itemProps));
		});
	}

	static Consumer<Block> block(Function<Block, Item> item) {
		return block -> blockItems.add(reg -> reg.reg(block.getRegistryName().getPath(), item.apply(block)));
	}
}
