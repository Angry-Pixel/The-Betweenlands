package thebetweenlands.common.datagen.recipes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.AspectTypeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BLItemRecipeProvider {

	public static void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.REED_ROPE, 4).pattern("r").pattern("r").pattern("r").define('r', BlockRegistry.SWAMP_REED).unlockedBy("has_reed", has(BlockRegistry.SWAMP_REED)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.WEEDWOOD_STICK, 4).pattern("r").pattern("r").define('r', BlockRegistry.WEEDWOOD_PLANKS).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.WEEDWOOD_BOWL, 4).pattern("r r").pattern(" r ").define('r', BlockRegistry.WEEDWOOD_PLANKS).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.TAR_BEAST_HEART)
			.pattern("ttt").pattern("twt").pattern("ttt").define('t', ItemRegistry.TAR_DRIP).define('w', ItemRegistry.WIGHT_HEART)
			.unlockedBy("has_tar", has(ItemRegistry.TAR_DRIP)).unlockedBy("has_heart", has(ItemRegistry.WIGHT_HEART)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMATE_PAPER, 9).pattern("bbb").define('b', ItemRegistry.DRY_BARK).unlockedBy("has_bark", has(ItemRegistry.DRY_BARK)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.INANIMATE_ANGRY_PEBBLE)
			.requires(ItemRegistry.BETWEENSTONE_PEBBLE).requires(ItemRegistry.CREMAINS).requires(ItemRegistry.SULFUR)
			.unlockedBy("has_pebble", has(ItemRegistry.BETWEENSTONE_PEBBLE)).unlockedBy("has_cremains", has(ItemRegistry.CREMAINS)).unlockedBy("has_sulfur", has(ItemRegistry.SULFUR)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.FABRICATED_SCROLL).requires(ItemRegistry.AMATE_PAPER).requires(ItemRegistry.LOOT_SCRAPS, 3).unlockedBy("has_scraps", has(ItemRegistry.LOOT_SCRAPS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.BETWEENSTONE_PEBBLE, 2).requires(BlockRegistry.BETWEENSTONE).unlockedBy("has_pebble", has(BlockRegistry.BETWEENSTONE)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.ANADIA_REMAINS).requires(ItemRegistry.RAW_ANADIA_MEAT, 2).unlockedBy("has_meat", has(ItemRegistry.RAW_ANADIA_MEAT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.FISHING_FLOAT_AND_HOOK)
			.pattern("ll").pattern("or").define('l', ItemRegistry.LEAF).define('o', ItemRegistry.OCTINE_NUGGET).define('r', ItemRegistry.RUBBER_BALL)
			.unlockedBy("has_nugget", has(ItemRegistry.OCTINE_NUGGET)).unlockedBy("has_rubber", has(ItemRegistry.RUBBER_BALL)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.OLMLETTE_MIXTURE)
			.requires(ItemRegistry.FLATHEAD_MUSHROOM).requires(ItemRegistry.BLACK_HAT_MUSHROOM).requires(ItemRegistry.RAW_OLM_EGG, 2).requires(ItemRegistry.WEEDWOOD_BOWL).requires(ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_eggs", has(ItemRegistry.RAW_OLM_EGG)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SILK_THREAD, 2).requires(ItemRegistry.SILK_COCOON).unlockedBy("has_silk", has(ItemRegistry.SILK_COCOON)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.PHEROMONE_THORAXES).pattern("ii").pattern("ii").define('i', ItemRegistry.PHEROMONE_THORAX).unlockedBy("has_resource", has(ItemRegistry.PHEROMONE_THORAX)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.LEAF, 3).requires(BlockRegistry.FALLEN_LEAVES).unlockedBy("has_leaves", has(BlockRegistry.FALLEN_LEAVES)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SWAMP_TALISMAN).requires(Ingredient.of(ItemRegistry.MOSS_CLUMP, BlockRegistry.MOSS)).requires(ItemRegistry.SLIMY_BONE).requires(ItemRegistry.LIFE_CRYSTAL)
			.unlockedBy("has_moss", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.MOSS_CLUMP, BlockRegistry.MOSS)))
			.unlockedBy("has_bone", has(ItemRegistry.SLIMY_BONE)).unlockedBy("has_crystal", has(ItemRegistry.LIFE_CRYSTAL)).save(output, TheBetweenlands.prefix("craftable_talisman"));
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.WEEDWOOD_ROWBOAT).pattern("i i").pattern("iii").define('i', BlockRegistry.WEEDWOOD_PLANKS).unlockedBy("has_resource", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.FUMIGANT).requires(ItemRegistry.LIMESTONE_FLUX).requires(ItemRegistry.SAP_BALL).requires(ItemRegistry.GROUND_SWAMP_KELP).unlockedBy("has_flux", has(ItemRegistry.LIMESTONE_FLUX)).unlockedBy("has_sap", has(ItemRegistry.SAP_BALL)).unlockedBy("has_kelp", has(ItemRegistry.GROUND_SWAMP_KELP)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.REED_DONUT).pattern(" r ").pattern("r r").pattern(" r ").define('r', ItemRegistry.DRIED_SWAMP_REED).unlockedBy("has_reed", has(ItemRegistry.DRIED_SWAMP_REED)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.JAM_DONUT).requires(ItemRegistry.REED_DONUT).requires(ItemRegistry.MIDDLE_FRUIT).unlockedBy("has_donut", has(ItemRegistry.REED_DONUT)).unlockedBy("has_fruit", has(ItemRegistry.MIDDLE_FRUIT)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.GERTS_DONUT).requires(ItemRegistry.REED_DONUT).requires(ItemRegistry.WIGHT_HEART).requires(Items.SLIME_BALL).unlockedBy("has_donut", has(ItemRegistry.REED_DONUT)).unlockedBy("has_heart", has(ItemRegistry.WIGHT_HEART)).unlockedBy("has_slime", has(Items.SLIME_BALL)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.CRAB_STICK).pattern("  s").pattern(" s ").pattern("s  ").define('s', ItemRegistry.SILT_CRAB_CLAW).unlockedBy("has_claw", has(ItemRegistry.SILT_CRAB_CLAW)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.MIDDLE_FRUIT_JELLO).requires(ItemRegistry.SLUDGE_JELLO).requires(ItemRegistry.MIDDLE_FRUIT).unlockedBy("has_jello", has(ItemRegistry.SLUDGE_JELLO)).unlockedBy("has_fruit", has(ItemRegistry.MIDDLE_FRUIT)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.SAP_JELLO).requires(ItemRegistry.SLUDGE_JELLO).requires(ItemRegistry.SAP_BALL).unlockedBy("has_jello", has(ItemRegistry.SLUDGE_JELLO)).unlockedBy("has_sap", has(ItemRegistry.SAP_BALL)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.MIRE_SCRAMBLE).requires(ItemRegistry.WEEDWOOD_BOWL).requires(ItemRegistry.COOKED_MIRE_SNAIL_EGG).requires(ItemRegistry.YELLOW_DOTTED_FUNGUS).unlockedBy("has_egg", has(ItemRegistry.COOKED_MIRE_SNAIL_EGG)).unlockedBy("has_fungus", has(ItemRegistry.YELLOW_DOTTED_FUNGUS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ItemRegistry.WEEPING_BLUE_PETAL_SALAD).requires(ItemRegistry.WEEDWOOD_BOWL).requires(ItemRegistry.WEEPING_BLUE_PETAL, 6).unlockedBy("has_petal", has(ItemRegistry.WEEPING_BLUE_PETAL)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.NIBBLESTICK, 4).pattern("r").pattern("r").define('r', Ingredient.of(BlockRegistry.NIBBLETWIG_BARK, BlockRegistry.NIBBLETWIG_LOG)).unlockedBy("has_log", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.NIBBLETWIG_BARK, BlockRegistry.NIBBLETWIG_LOG))).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.SUSHI, 3)
			.pattern("kkk").pattern("mmm").pattern("lll").define('k', BlockRegistry.SWAMP_KELP).define('m', ItemRegistry.RAW_ANADIA_MEAT).define('l', ItemRegistry.GROUND_LICHEN)
			.unlockedBy("has_kelp", has(BlockRegistry.SWAMP_KELP)).unlockedBy("has_meat", has(ItemRegistry.RAW_ANADIA_MEAT)).unlockedBy("has_lichen", has(ItemRegistry.GROUND_LICHEN)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.PEARLED_PEAR)
			.pattern("mmm").pattern("mpm").pattern("mmm").define('m', ItemRegistry.MIDDLE_FRUIT).define('p', ItemRegistry.ROCK_SNOT_PEARL)
			.unlockedBy("has_fruit", has(ItemRegistry.MIDDLE_FRUIT)).unlockedBy("has_pearl", has(ItemRegistry.ROCK_SNOT_PEARL)).save(output);
		armorSet(output, ItemRegistry.BONE_HELMET, ItemRegistry.BONE_CHESTPLATE, ItemRegistry.BONE_LEGGINGS, ItemRegistry.BONE_BOOTS, ItemRegistry.SLIMY_BONE);
		armorSet(output, ItemRegistry.LURKER_SKIN_HELMET, ItemRegistry.LURKER_SKIN_CHESTPLATE, ItemRegistry.LURKER_SKIN_LEGGINGS, ItemRegistry.LURKER_SKIN_BOOTS, ItemRegistry.LURKER_SKIN);
		armorSet(output, ItemRegistry.SYRMORITE_HELMET, ItemRegistry.SYRMORITE_CHESTPLATE, ItemRegistry.SYRMORITE_LEGGINGS, ItemRegistry.SYRMORITE_BOOTS, ItemRegistry.SYRMORITE_INGOT);
		armorSet(output, ItemRegistry.VALONITE_HELMET, ItemRegistry.VALONITE_CHESTPLATE, ItemRegistry.VALONITE_LEGGINGS, ItemRegistry.VALONITE_BOOTS, ItemRegistry.VALONITE_SHARD);
		bootsItem(output, ItemRegistry.RUBBER_BOOTS, ItemRegistry.RUBBER_BALL);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMPHIBIOUS_HELMET)
			.pattern("sss").pattern("ele").pattern("gbg")
			.define('s', ItemRegistry.ANADIA_SCALES).define('e', ItemRegistry.ANADIA_EYE).define('l', ItemRegistry.LURKER_SKIN_HELMET)
			.define('g', ItemRegistry.ANADIA_GILLS).define('b', ItemRegistry.ANADIA_BONES)
			.unlockedBy("has_scales", has(ItemRegistry.ANADIA_SCALES)).unlockedBy("has_eye", has(ItemRegistry.ANADIA_EYE))
			.unlockedBy("has_gills", has(ItemRegistry.ANADIA_GILLS)).unlockedBy("has_bones", has(ItemRegistry.ANADIA_BONES)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMPHIBIOUS_CHESTPLATE)
			.pattern("sss").pattern("glg").pattern("fbf")
			.define('s', ItemRegistry.ANADIA_SCALES).define('f', ItemRegistry.ANADIA_FINS).define('l', ItemRegistry.LURKER_SKIN_CHESTPLATE)
			.define('g', ItemRegistry.ANADIA_GILLS).define('b', ItemRegistry.ANADIA_BONES)
			.unlockedBy("has_scales", has(ItemRegistry.ANADIA_SCALES)).unlockedBy("has_fins", has(ItemRegistry.ANADIA_FINS))
			.unlockedBy("has_gills", has(ItemRegistry.ANADIA_GILLS)).unlockedBy("has_bones", has(ItemRegistry.ANADIA_BONES)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMPHIBIOUS_LEGGINGS)
			.pattern("sss").pattern("flf").pattern("fbf")
			.define('s', ItemRegistry.ANADIA_SCALES).define('f', ItemRegistry.ANADIA_FINS)
			.define('l', ItemRegistry.LURKER_SKIN_LEGGINGS).define('b', ItemRegistry.ANADIA_BONES)
			.unlockedBy("has_scales", has(ItemRegistry.ANADIA_SCALES)).unlockedBy("has_fins", has(ItemRegistry.ANADIA_FINS))
			.unlockedBy("has_bones", has(ItemRegistry.ANADIA_BONES)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMPHIBIOUS_BOOTS)
			.pattern("sss").pattern("flf").pattern("sbs")
			.define('s', ItemRegistry.ANADIA_SCALES).define('f', ItemRegistry.ANADIA_FINS)
			.define('l', ItemRegistry.LURKER_SKIN_BOOTS).define('b', ItemRegistry.ANADIA_BONES)
			.unlockedBy("has_scales", has(ItemRegistry.ANADIA_SCALES)).unlockedBy("has_fins", has(ItemRegistry.ANADIA_FINS))
			.unlockedBy("has_bones", has(ItemRegistry.ANADIA_BONES)).save(output);
		//TODO gallery frames
		//TODO silk mask
		toolSet(output, ItemRegistry.WEEDWOOD_SWORD, ItemRegistry.WEEDWOOD_PICKAXE, ItemRegistry.WEEDWOOD_AXE, ItemRegistry.WEEDWOOD_SHOVEL, BlockRegistry.WEEDWOOD_PLANKS);
		toolSet(output, ItemRegistry.BONE_SWORD, ItemRegistry.BONE_PICKAXE, ItemRegistry.BONE_AXE, ItemRegistry.BONE_SHOVEL, ItemRegistry.SLIMY_BONE);
		toolSet(output, ItemRegistry.OCTINE_SWORD, ItemRegistry.OCTINE_PICKAXE, ItemRegistry.OCTINE_AXE, ItemRegistry.OCTINE_SHOVEL, ItemRegistry.OCTINE_INGOT);
		toolSet(output, ItemRegistry.VALONITE_SWORD, ItemRegistry.VALONITE_PICKAXE, ItemRegistry.VALONITE_AXE, ItemRegistry.VALONITE_SHOVEL, ItemRegistry.VALONITE_SHARD);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VALONITE_GREATAXE)
			.pattern(" va").pattern(" sv").pattern("s  ")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('a', ItemRegistry.VALONITE_AXE).define('v', ItemRegistry.VALONITE_SHARD)
			.unlockedBy("has_axe", has(ItemRegistry.VALONITE_AXE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.OCTINE_SHIELD).pattern("iii").pattern("iii").pattern(" i ")
			.define('i', ItemRegistry.OCTINE_INGOT).unlockedBy("has_ingot", has(ItemRegistry.OCTINE_INGOT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.VALONITE_SHIELD).pattern("ipi").pattern("iii").pattern(" i ")
			.define('i', ItemRegistry.VALONITE_SHARD).define('p', BlockRegistry.PITSTONE)
			.unlockedBy("has_ingot", has(ItemRegistry.VALONITE_SHARD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.WEEDWOOD_SHIELD).pattern("iri").pattern("iii").pattern(" i ")
			.define('i', Ingredient.of(BlockRegistry.WEEDWOOD_BARK, BlockRegistry.WEEDWOOD_LOG)).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_wood", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.WEEDWOOD_BARK, BlockRegistry.WEEDWOOD_LOG))).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ItemRegistry.LIVING_WEEDWOOD_SHIELD)
			.requires(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK).requires(ItemRegistry.WEEDWOOD_SHIELD)
			.unlockedBy("has_mask", has(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK)).unlockedBy("has_shield", has(ItemRegistry.WEEDWOOD_SHIELD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.SYRMORITE_SHIELD).pattern("iii").pattern("iii").pattern(" i ")
			.define('i', ItemRegistry.SYRMORITE_INGOT).unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.BONE_SHIELD).pattern("iri").pattern("iii").pattern(" i ")
			.define('i', ItemRegistry.SLIMY_BONE).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_bone", has(ItemRegistry.SLIMY_BONE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.GREEN_DENTROTHYST_SHIELD).pattern("isi").pattern("iii").pattern(" i ")
			.define('i', BlockRegistry.GREEN_DENTROTHYST).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_dentrothyst", has(BlockRegistry.GREEN_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.POLISHED_GREEN_DENTROTHYST_SHIELD).pattern("isi").pattern("iii").pattern(" i ")
			.define('i', BlockRegistry.POLISHED_GREEN_DENTROTHYST).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_dentrothyst", has(BlockRegistry.POLISHED_GREEN_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.ORANGE_DENTROTHYST_SHIELD).pattern("isi").pattern("iii").pattern(" i ")
			.define('i', BlockRegistry.ORANGE_DENTROTHYST).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_dentrothyst", has(BlockRegistry.ORANGE_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.POLISHED_ORANGE_DENTROTHYST_SHIELD).pattern("isi").pattern("iii").pattern(" i ")
			.define('i', BlockRegistry.POLISHED_ORANGE_DENTROTHYST).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_dentrothyst", has(BlockRegistry.POLISHED_ORANGE_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.LURKER_SKIN_SHIELD)
			.pattern("isi").pattern("iii").pattern(" i ").define('i', ItemRegistry.LURKER_SKIN).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.HERBLORE_BOOK)
			.pattern("lll").pattern("ppp").pattern("lll").define('l', ItemRegistry.LURKER_SKIN).define('p', ItemRegistry.AMATE_PAPER)
			.unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.SYRMORITE_SHEARS)
			.pattern(" s").pattern("s ").define('s', ItemRegistry.SYRMORITE_INGOT)
			.unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.SICKLE).pattern(" vv").pattern("v s").pattern("  r")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('v', ItemRegistry.VALONITE_SHARD).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_ingot", has(ItemRegistry.VALONITE_SHARD)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.ANGLER_TOOTH_ARROW, 4).pattern("a").pattern("s").pattern("w")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('w', ItemRegistry.DRAGONFLY_WING).define('a', ItemRegistry.ANGLER_TOOTH)
			.unlockedBy("has_wing", has(ItemRegistry.DRAGONFLY_WING)).unlockedBy("has_tooth", has(ItemRegistry.ANGLER_TOOTH)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.POISONED_ANGLER_TOOTH_ARROW).requires(ItemRegistry.POISON_GLAND).requires(ItemRegistry.ANGLER_TOOTH_ARROW).unlockedBy("has_gland", has(ItemRegistry.POISON_GLAND)).unlockedBy("has_arrow", has(ItemRegistry.ANGLER_TOOTH_ARROW)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.OCTINE_ARROW, 4).pattern("a").pattern("s").pattern("w")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('w', ItemRegistry.DRAGONFLY_WING).define('a', ItemRegistry.OCTINE_INGOT)
			.unlockedBy("has_wing", has(ItemRegistry.DRAGONFLY_WING)).unlockedBy("has_ingot", has(ItemRegistry.OCTINE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.WEEDWOOD_BOW)
			.pattern(" sr").pattern("s r").pattern(" sr").define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.PESTLE)
			.pattern("s").pattern("c").pattern("c").define('s', ItemRegistry.WEEDWOOD_STICK).define('c', BlockRegistry.CRAGROCK)
			.unlockedBy("has_rock", has(BlockRegistry.CRAGROCK)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.NET)
			.pattern("srr").pattern("srr").pattern("s  ").define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.SMALL_LURKER_SKIN_POUCH)
			.pattern("sss").pattern("l l").pattern("lll").define('s', ItemRegistry.SILK_THREAD).define('l', ItemRegistry.LURKER_SKIN)
			.unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).unlockedBy("has_silk", has(ItemRegistry.SILK_THREAD)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.MEDIUM_LURKER_SKIN_POUCH)
			.pattern("lll").pattern("lpl").pattern("lll").define('p', ItemRegistry.SMALL_LURKER_SKIN_POUCH).define('l', ItemRegistry.LURKER_SKIN)
			.unlockedBy("has_pouch", has(ItemRegistry.SMALL_LURKER_SKIN_POUCH)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.LARGE_LURKER_SKIN_POUCH)
			.pattern("lll").pattern("lpl").pattern("lll").define('p', ItemRegistry.MEDIUM_LURKER_SKIN_POUCH).define('l', ItemRegistry.LURKER_SKIN)
			.unlockedBy("has_pouch", has(ItemRegistry.MEDIUM_LURKER_SKIN_POUCH)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.XL_LURKER_SKIN_POUCH)
			.pattern("lll").pattern("lpl").pattern("lll").define('p', ItemRegistry.LARGE_LURKER_SKIN_POUCH).define('l', ItemRegistry.LURKER_SKIN)
			.unlockedBy("has_pouch", has(ItemRegistry.LARGE_LURKER_SKIN_POUCH)).save(output);
		//TODO caving rope
		//TODO grapples
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.VOLARKITE)
			.pattern("vvv").pattern("rsr").pattern(" s ").define('v', Ingredient.of(BlockRegistry.VOLARPAD, ItemRegistry.VOLARPAD_LEAF)).define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_volarpad", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.VOLARPAD, ItemRegistry.VOLARPAD_LEAF))).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemRegistry.SLINGSHOT)
			.pattern("r r").pattern(" s ").define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.WEEDWOOD_FISHING_ROD)
			.pattern("  s").pattern("ist").pattern("sih")
			.define('i', ItemRegistry.SYRMORITE_INGOT).define('t', ItemRegistry.SILK_THREAD)
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('h', ItemRegistry.FISHING_FLOAT_AND_HOOK)
			.unlockedBy("has_thread", has(ItemRegistry.SILK_THREAD)).unlockedBy("has_hook", has(ItemRegistry.FISHING_FLOAT_AND_HOOK))
			.unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);

		//TODO spears
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.WEEDWOOD_BUCKET)
			.pattern(" r ").pattern("w w").pattern(" w ").define('w', BlockRegistry.WEEDWOOD_PLANKS).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.SYRMORITE_BUCKET)
			.pattern(" r ").pattern("w w").pattern(" w ").define('w', ItemRegistry.SYRMORITE_INGOT).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.TARMINION)
			.pattern("ttt").pattern("tht").pattern("ttt").define('t', ItemRegistry.TAR_DRIP).define('h', ItemRegistry.ANIMATED_TAR_BEAST_HEART)
			.unlockedBy("has_tar", has(ItemRegistry.TAR_DRIP)).unlockedBy("has_heart", has(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.GREEN_DENTROTHYST_VIAL)
			.pattern(" r ").pattern("s s").pattern(" s ").define('s', ItemRegistry.GREEN_DENTROTHYST_SHARD).define('r', ItemRegistry.RUBBER_BALL)
			.unlockedBy("has_rubber", has(ItemRegistry.RUBBER_BALL)).unlockedBy("has_shard", has(ItemRegistry.GREEN_DENTROTHYST_SHARD)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemRegistry.ORANGE_DENTROTHYST_VIAL)
			.pattern(" r ").pattern("s s").pattern(" s ").define('s', ItemRegistry.ORANGE_DENTROTHYST_SHARD).define('r', ItemRegistry.RUBBER_BALL)
			.unlockedBy("has_rubber", has(ItemRegistry.RUBBER_BALL)).unlockedBy("has_shard", has(ItemRegistry.ORANGE_DENTROTHYST_SHARD)).save(output);

		//TODO amulets
		//TODO rope
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.MUMMY_BAIT)
			.requires(ItemRegistry.SHIMMER_STONE).requires(ItemRegistry.ANIMATED_TAR_BEAST_HEART)
			.requires(ItemRegistry.SLUDGE_BALL).requires(DataComponentIngredient.of(false, DataComponentMap.builder().set(DataComponentRegistry.ASPECT_CONTENTS, new AspectContents(registries.holderOrThrow(AspectTypeRegistry.ARMANIIS), 1000)).build(), ItemRegistry.GREEN_ASPECT_VIAL.get(), ItemRegistry.ORANGE_ASPECT_VIAL.get()))
			.unlockedBy("has_shimmerstone", has(ItemRegistry.SHIMMER_STONE)).unlockedBy("has_heart", has(ItemRegistry.ANIMATED_TAR_BEAST_HEART))
			.unlockedBy("has_aspect", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.ORANGE_ASPECT_VIAL, ItemRegistry.GREEN_ASPECT_VIAL).hasComponents(DataComponentPredicate.builder().expect(DataComponentRegistry.ASPECT_CONTENTS.get(), new AspectContents(registries.holderOrThrow(AspectTypeRegistry.ARMANIIS), 1000)).build()).build())).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.AMATE_MAP)
			.pattern("ppp").pattern("pgp").pattern("ppp").define('p', ItemRegistry.AMATE_PAPER).define('g', Ingredient.of(ItemRegistry.CRIMSON_MIDDLE_GEM, ItemRegistry.GREEN_MIDDLE_GEM, ItemRegistry.AQUA_MIDDLE_GEM))
			.unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).unlockedBy("has_gem", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.CRIMSON_MIDDLE_GEM, ItemRegistry.GREEN_MIDDLE_GEM, ItemRegistry.AQUA_MIDDLE_GEM))).save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.LURKER_SKIN_PATCH).requires(ItemRegistry.TAR_DRIP).requires(ItemRegistry.LURKER_SKIN).unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).unlockedBy("has_tar", has(ItemRegistry.TAR_DRIP)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.DRAETON_BALLOON)
			.pattern("lll").pattern("rrr").pattern("r r").define('r', ItemRegistry.RUBBER_BALL).define('l', ItemRegistry.LURKER_SKIN)
			.unlockedBy("has_rubber", has(ItemRegistry.RUBBER_BALL)).unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.DRAETON_BURNER)
			.pattern("rrr").pattern("rcr").pattern(" e ").define('r', ItemRegistry.ANCIENT_REMNANT).define('e', ItemRegistry.UNDYING_EMBERS).define('c', BlockRegistry.CENSER)
			.unlockedBy("has_remnant", has(ItemRegistry.ANCIENT_REMNANT)).unlockedBy("has_ember", has(ItemRegistry.UNDYING_EMBERS)).unlockedBy("has_censer", has(BlockRegistry.CENSER)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.DRAETON).pattern("bbb").pattern("rdr").pattern("sws")
			.define('b', ItemRegistry.DRAETON_BALLOON).define('r', ItemRegistry.REED_ROPE).define('d', ItemRegistry.DRAETON_BURNER).define('w', ItemRegistry.WEEDWOOD_ROWBOAT).define('s', ItemRegistry.SYRMORITE_INGOT)
			.unlockedBy("has_balloon", has(ItemRegistry.DRAETON_BALLOON)).unlockedBy("has_burner", has(ItemRegistry.DRAETON_BURNER)).unlockedBy("has_rowboat", has(ItemRegistry.WEEDWOOD_ROWBOAT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.DRAETON_FURNACE_UPGRADE).pattern("wfw").pattern("sis")
			.define('w', BlockRegistry.WEEDWOOD_SLAB).define('f', BlockRegistry.SULFUR_FURNACE).define('s', ItemRegistry.WEEDWOOD_STICK).define('i', ItemRegistry.SYRMORITE_INGOT)
			.unlockedBy("has_furnace", has(BlockRegistry.SULFUR_FURNACE)).unlockedBy("has_draeton", has(ItemRegistry.DRAETON)).save(output);
		//TODO anchor
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.DRAETON_CRAFTING_UPGRADE).pattern("wcw").pattern("sos")
			.define('w', BlockRegistry.WEEDWOOD_SLAB).define('c', BlockRegistry.WEEDWOOD_CRAFTING_TABLE).define('s', ItemRegistry.WEEDWOOD_STICK).define('o', ItemRegistry.OCTINE_INGOT)
			.unlockedBy("has_crafting_table", has(BlockRegistry.WEEDWOOD_CRAFTING_TABLE)).unlockedBy("has_draeton", has(ItemRegistry.DRAETON)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ItemRegistry.WEEDWOOD_ROWBOAT_LANTERN_UPGRADE)
			.pattern("sss").pattern("rfr").pattern("www")
			.define('s', BlockRegistry.WEEDWOOD_SLAB).define('r', ItemRegistry.REED_ROPE)
			.define('f', ItemRegistry.FIREFLY).define('w', BlockRegistry.WEEDWOOD_PLANKS)
			.unlockedBy("has_boat", has(ItemRegistry.WEEDWOOD_ROWBOAT)).unlockedBy("has_firefly", has(ItemRegistry.FIREFLY)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.AMATE_NAME_TAG).requires(ItemRegistry.AMATE_PAPER).requires(ItemRegistry.REED_ROPE).unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.BIOPATHIC_LINKSTONE).pattern(" b ").pattern("bhb").pattern(" c ")
			.define('b', ItemRegistry.ANADIA_BONES).define('h', ItemRegistry.WIGHT_HEART).define('c', BlockRegistry.CRAGROCK)
			.unlockedBy("has_bones", has(ItemRegistry.ANADIA_BONES)).unlockedBy("has_heart", has(ItemRegistry.WIGHT_HEART)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.BIOPATHIC_TRIGGERSTONE).requires(ItemRegistry.BIOPATHIC_LINKSTONE).unlockedBy("has_linkstone", has(ItemRegistry.BIOPATHIC_LINKSTONE)).unlockedBy("has_triggerstone", has(ItemRegistry.BIOPATHIC_TRIGGERSTONE)).save(output, TheBetweenlands.prefix("linkstone_to_triggerstone"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.BIOPATHIC_LINKSTONE).requires(ItemRegistry.BIOPATHIC_TRIGGERSTONE).unlockedBy("has_linkstone", has(ItemRegistry.BIOPATHIC_LINKSTONE)).unlockedBy("has_triggerstone", has(ItemRegistry.BIOPATHIC_TRIGGERSTONE)).save(output, TheBetweenlands.prefix("triggerstone_to_linkstone"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.CHAMPAGNE_ITEM_FRAME).pattern("sss").pattern("rtr").pattern("sss")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE).define('t', ItemRegistry.SILK_THREAD)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_thread", has(ItemRegistry.SILK_THREAD)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.DULL_LAVENDER_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.DULL_LAVENDER_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.MAROON_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.MAROON_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SHADOW_GREEN_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.SHADOW_GREEN_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.CAMELOT_MAGENTA_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.CAMELOT_MAGENTA_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SAFFRON_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.SAFFRON_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.CARIBBEAN_GREEN_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.CARIBBEAN_GREEN_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.VIVID_TANGERINE_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.VIVID_TANGERINE_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.RAISIN_BLACK_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.RAISIN_BLACK_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SUSHI_GREEN_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.SUSHI_GREEN_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.ELM_CYAN_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.ELM_CYAN_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.CADMIUM_GREEN_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.CADMIUM_GREEN_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.LAVENDER_BLUE_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.LAVENDER_BLUE_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.BROWN_RUST_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.BROWN_RUST_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.MIDNIGHT_PURPLE_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.MIDNIGHT_PURPLE_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.PEWTER_GREY_ITEM_FRAME).requires(ItemRegistry.CHAMPAGNE_ITEM_FRAME).requires(ItemRegistry.PEWTER_GREY_DYE).unlockedBy("has_frame", has(ItemRegistry.CHAMPAGNE_ITEM_FRAME)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.PHEROMONE_THORAX, 4).requires(ItemRegistry.PHEROMONE_THORAXES).unlockedBy("has_thorax", has(ItemRegistry.PHEROMONE_THORAXES)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.SILK_BUNDLE).pattern(" r ").pattern("s s").pattern("sss")
			.define('r', ItemRegistry.REED_ROPE).define('s', ItemRegistry.SILK_THREAD)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_thread", has(ItemRegistry.SILK_THREAD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.SILK_FILTER).pattern(" s ").pattern("sts").pattern(" s ")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('t', ItemRegistry.SILK_THREAD)
			.unlockedBy("has_thread", has(ItemRegistry.SILK_THREAD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.MOSS_FILTER).pattern(" s ").pattern("sts").pattern(" s ")
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('t',BlockRegistry.MOSS)
			.unlockedBy("has_moss", has(BlockRegistry.MOSS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.SILKY_PEBBLE).pattern(" s ").pattern("sps").pattern(" s ")
			.define('s', ItemRegistry.SILK_THREAD).define('p', ItemRegistry.ANGRY_PEBBLE)
			.unlockedBy("has_thread", has(ItemRegistry.SILK_THREAD)).unlockedBy("has_pebble", has(ItemRegistry.ANGRY_PEBBLE)).save(output);
	}

	protected static void armorSet(RecipeOutput output, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots, ItemLike material) {
		helmetItem(output, helmet, material);
		chestplateItem(output, chestplate, material);
		leggingsItem(output, leggings, material);
		bootsItem(output, boots, material);
	}

	protected static void toolSet(RecipeOutput output, ItemLike sword, ItemLike pick, ItemLike axe, ItemLike shovel, ItemLike material) {
		swordItem(output, sword, material);
		pickaxeItem(output, pick, material);
		axeItem(output, axe, material);
		shovelItem(output, shovel, material);
	}

	protected static void helmetItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result).pattern("###").pattern("# #").define('#', material).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void chestplateItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result).pattern("# #").pattern("###").pattern("###").define('#', material).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void leggingsItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result).pattern("###").pattern("# #").pattern("# #").define('#', material).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void bootsItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result).pattern("# #").pattern("# #").define('#', material).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void pickaxeItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result).pattern("###").pattern(" X ").pattern(" X ").define('#', material).define('X', ItemRegistry.WEEDWOOD_STICK).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void swordItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result).pattern("#").pattern("#").pattern("X").define('#', material).define('X', ItemRegistry.WEEDWOOD_STICK).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void axeItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result).pattern("##").pattern("#X").pattern(" X").define('#', material).define('X', ItemRegistry.WEEDWOOD_STICK).unlockedBy("has_item", has(material)).save(output);
	}

	protected static void shovelItem(RecipeOutput output, ItemLike result, ItemLike material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result).pattern("#").pattern("X").pattern("X").define('#', material).define('X', ItemRegistry.WEEDWOOD_STICK).unlockedBy("has_item", has(material)).save(output);
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(tag));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
		return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
		return CriteriaTriggers.INVENTORY_CHANGED
			.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
	}
}
