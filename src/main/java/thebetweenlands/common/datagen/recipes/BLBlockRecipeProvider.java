package thebetweenlands.common.datagen.recipes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BLBlockRecipeProvider {

	public static void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COARSE_SWAMP_DIRT, 2).requires(BlockRegistry.SWAMP_DIRT).requires(BlockRegistry.SILT)
			.unlockedBy("has_dirt", has(BlockRegistry.SWAMP_DIRT)).unlockedBy("has_silt", has(BlockRegistry.SILT)).save(output);

		resourceBlock(output, BlockRegistry.PEARL_BLOCK, ItemRegistry.ROCK_SNOT_PEARL, null);
		resourceBlock(output, BlockRegistry.ANCIENT_REMNANT_BLOCK, ItemRegistry.ANCIENT_REMNANT, null);

		woodAndPlanks(output, BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK, BlockRegistry.WEEDWOOD_PLANKS, BlockRegistry.WEEDWOOD);
		woodAndPlanks(output, BlockRegistry.NIBBLETWIG_LOG, BlockRegistry.NIBBLETWIG_BARK, BlockRegistry.NIBBLETWIG_PLANKS);
		woodAndPlanks(output, BlockRegistry.HEARTHGROVE_LOG, BlockRegistry.HEARTHGROVE_BARK, BlockRegistry.HEARTHGROVE_PLANKS, BlockRegistry.TARRED_HEARTHGROVE_BARK, BlockRegistry.TARRED_HEARTHGROVE_LOG);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.RUBBER_TREE_PLANKS, 4).requires(Ingredient.of(BlockRegistry.RUBBER_LOG)).unlockedBy("has_block", has(BlockRegistry.RUBBER_LOG)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.GIANT_ROOT_PLANKS, 4).requires(Ingredient.of(BlockRegistry.GIANT_ROOT)).unlockedBy("has_block", has(BlockRegistry.GIANT_ROOT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SAP_BARK, 3).pattern("ii").pattern("ii").define('i', BlockRegistry.SAP_LOG).unlockedBy("has_resource", has(BlockRegistry.SAP_LOG)).save(output);

		brickBlock(output, BlockRegistry.BETWEENSTONE_BRICKS, BlockRegistry.BETWEENSTONE);
		brickBlock(output, BlockRegistry.BETWEENSTONE_TILES, BlockRegistry.SMOOTH_BETWEENSTONE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_BETWEENSTONE).pattern("i").pattern("i").define('i', BlockRegistry.BETWEENSTONE_BRICK_SLAB).unlockedBy("has_block", has(BlockRegistry.BETWEENSTONE_BRICK_SLAB)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_CRAGROCK).pattern("i").pattern("i").define('i', BlockRegistry.CRAGROCK_BRICK_SLAB).unlockedBy("has_block", has(BlockRegistry.CRAGROCK_BRICK_SLAB)).save(output);
		mossyBlock(output, BlockRegistry.MOSSY_CHISELED_CRAGROCK, BlockRegistry.CHISELED_CRAGROCK);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_LIMESTONE).pattern("i").pattern("i").define('i', BlockRegistry.LIMESTONE_BRICK_SLAB).unlockedBy("has_block", has(BlockRegistry.LIMESTONE_BRICK_SLAB)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_PITSTONE).pattern("i").pattern("i").define('i', BlockRegistry.PITSTONE_BRICK_SLAB).unlockedBy("has_block", has(BlockRegistry.PITSTONE_BRICK_SLAB)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_SCABYST_1).pattern("i").pattern("i").define('i', BlockRegistry.SCABYST_BRICK_SLAB).unlockedBy("has_block", has(BlockRegistry.SCABYST_BRICK_SLAB)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_SCABYST_2).requires(BlockRegistry.CHISELED_SCABYST_1).unlockedBy("has_scabyst", has(BlockRegistry.SCABYST_BRICK_SLAB)).save(output, TheBetweenlands.prefix("chiseled_scabyst_1_to_2"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_SCABYST_3).requires(BlockRegistry.CHISELED_SCABYST_2).unlockedBy("has_scabyst", has(BlockRegistry.SCABYST_BRICK_SLAB)).save(output, TheBetweenlands.prefix("chiseled_scabyst_2_to_3"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHISELED_SCABYST_1).requires(BlockRegistry.CHISELED_SCABYST_3).unlockedBy("has_scabyst", has(BlockRegistry.SCABYST_BRICK_SLAB)).save(output, TheBetweenlands.prefix("chiseled_scabyst_3_to_1"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.DOTTED_SCABYST_PITSTONE, 4)
			.pattern("bbb").pattern("bib").pattern("bbb")
			.define('b', BlockRegistry.PITSTONE_BRICKS).define('i', ItemRegistry.SCABYST)
			.unlockedBy("has_pitstone", has(BlockRegistry.PITSTONE_BRICKS))
			.unlockedBy("has_scabyst", has(ItemRegistry.SCABYST))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.HORIZONTAL_SCABYST_PITSTONE, 4)
			.pattern("bbb").pattern("iii").pattern("bbb")
			.define('b', BlockRegistry.PITSTONE_BRICKS).define('i', ItemRegistry.SCABYST)
			.unlockedBy("has_pitstone", has(BlockRegistry.PITSTONE_BRICKS))
			.unlockedBy("has_scabyst", has(ItemRegistry.SCABYST))
			.save(output);
		brickBlock(output, BlockRegistry.SCABYST_BRICKS, ItemRegistry.SCABYST);
		brickBlock(output, BlockRegistry.CRAGROCK_BRICKS, BlockRegistry.CRAGROCK);
		mossyBlock(output, BlockRegistry.MOSSY_CRAGROCK_BRICKS, BlockRegistry.CRAGROCK_BRICKS);
		brickBlock(output, BlockRegistry.CRAGROCK_TILES, BlockRegistry.SMOOTH_CRAGROCK);
		mossyBlock(output, BlockRegistry.MOSSY_CRAGROCK_TILES, BlockRegistry.CRAGROCK_TILES);
		brickBlock(output, BlockRegistry.LIMESTONE_BRICKS, BlockRegistry.LIMESTONE);
		brickBlock(output, BlockRegistry.LIMESTONE_TILES, BlockRegistry.POLISHED_LIMESTONE);
		mossyBlock(output, BlockRegistry.MOSSY_BETWEENSTONE_BRICKS, BlockRegistry.BETWEENSTONE_BRICKS);
		mossyBlock(output, BlockRegistry.MOSSY_BETWEENSTONE_TILES, BlockRegistry.BETWEENSTONE_TILES);
		mossyBlock(output, BlockRegistry.MOSSY_LIMESTONE_BRICKS, BlockRegistry.LIMESTONE_BRICKS);
		mossyBlock(output, BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE, BlockRegistry.SMOOTH_BETWEENSTONE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.MUD_BRICKS).pattern("ii").pattern("ii").define('i', ItemRegistry.MUD_BRICK).unlockedBy("has_resource", has(ItemRegistry.MUD_BRICK)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.MUD_BRICK_SHINGLES, 2).pattern("iii").pattern("iii").pattern("iii").define('i', ItemRegistry.MUD_BRICK).unlockedBy("has_resource", has(ItemRegistry.MUD_BRICK)).save(output);
		resourceBlock(output, BlockRegistry.RUBBER_BLOCK, ItemRegistry.RUBBER_BALL, null);
		brickBlock(output, BlockRegistry.PITSTONE_BRICKS, BlockRegistry.PITSTONE);
		brickBlock(output, BlockRegistry.PITSTONE_TILES, BlockRegistry.SMOOTH_PITSTONE);
		resourceBlock(output, BlockRegistry.OCTINE_BLOCK, ItemRegistry.OCTINE_INGOT, ItemRegistry.OCTINE_NUGGET);
		resourceBlock(output, BlockRegistry.SYRMORITE_BLOCK, ItemRegistry.SYRMORITE_INGOT, ItemRegistry.SYRMORITE_NUGGET);
		resourceBlock(output, BlockRegistry.VALONITE_BLOCK, ItemRegistry.VALONITE_SHARD, ItemRegistry.VALONITE_SPLINTER);
		resourceBlock(output, BlockRegistry.SCABYST_BLOCK, ItemRegistry.SCABYST, null);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.GREEN_DENTROTHYST).pattern("ii").pattern("ii").define('i', ItemRegistry.GREEN_DENTROTHYST_SHARD).unlockedBy("has_resource", has(ItemRegistry.GREEN_DENTROTHYST_SHARD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ORANGE_DENTROTHYST).pattern("ii").pattern("ii").define('i', ItemRegistry.ORANGE_DENTROTHYST_SHARD).unlockedBy("has_resource", has(ItemRegistry.ORANGE_DENTROTHYST_SHARD)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.BETWEENSTONE_PILLAR, 2).pattern("i").pattern("i").define('i', BlockRegistry.SMOOTH_BETWEENSTONE).unlockedBy("has_block", has(BlockRegistry.SMOOTH_BETWEENSTONE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.PITSTONE_PILLAR, 2).pattern("i").pattern("i").define('i', BlockRegistry.SMOOTH_PITSTONE).unlockedBy("has_block", has(BlockRegistry.SMOOTH_PITSTONE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.LIMESTONE_PILLAR, 2).pattern("i").pattern("i").define('i', BlockRegistry.POLISHED_LIMESTONE).unlockedBy("has_block", has(BlockRegistry.POLISHED_LIMESTONE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRAGROCK_PILLAR, 2).pattern("i").pattern("i").define('i', BlockRegistry.SMOOTH_CRAGROCK).unlockedBy("has_block", has(BlockRegistry.SMOOTH_CRAGROCK)).save(output);
		stairsSlabAndWall(output, BlockRegistry.CRAGROCK_STAIRS, BlockRegistry.CRAGROCK_SLAB, BlockRegistry.CRAGROCK_WALL, BlockRegistry.CRAGROCK);
		stairsSlabAndWall(output, BlockRegistry.PITSTONE_STAIRS, BlockRegistry.PITSTONE_SLAB, BlockRegistry.PITSTONE_WALL, BlockRegistry.PITSTONE);
		stairsSlabAndWall(output, BlockRegistry.BETWEENSTONE_STAIRS, BlockRegistry.BETWEENSTONE_SLAB, BlockRegistry.BETWEENSTONE_WALL, BlockRegistry.BETWEENSTONE);
		stairsSlabAndWall(output, BlockRegistry.BETWEENSTONE_BRICK_STAIRS, BlockRegistry.BETWEENSTONE_BRICK_SLAB, BlockRegistry.BETWEENSTONE_BRICK_WALL, BlockRegistry.BETWEENSTONE_BRICKS);
		wall(output, BlockRegistry.MUD_BRICK_WALL, BlockRegistry.MUD_BRICKS);
		stairsSlabAndWall(output, BlockRegistry.CRAGROCK_BRICK_STAIRS, BlockRegistry.CRAGROCK_BRICK_SLAB, BlockRegistry.CRAGROCK_BRICK_WALL, BlockRegistry.CRAGROCK_BRICKS);
		stairsSlabAndWall(output, BlockRegistry.LIMESTONE_BRICK_STAIRS, BlockRegistry.LIMESTONE_BRICK_SLAB, BlockRegistry.LIMESTONE_BRICK_WALL, BlockRegistry.LIMESTONE_BRICKS);
		stairsSlabAndWall(output, BlockRegistry.PITSTONE_BRICK_STAIRS, BlockRegistry.PITSTONE_BRICK_SLAB, BlockRegistry.PITSTONE_BRICK_WALL, BlockRegistry.PITSTONE_BRICKS);
		stairsSlabAndWall(output, BlockRegistry.LIMESTONE_STAIRS, BlockRegistry.LIMESTONE_SLAB, BlockRegistry.LIMESTONE_WALL, BlockRegistry.LIMESTONE);
		stairsSlabAndWall(output, BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS, BlockRegistry.SMOOTH_BETWEENSTONE_SLAB, BlockRegistry.SMOOTH_BETWEENSTONE_WALL, BlockRegistry.SMOOTH_BETWEENSTONE);
		stairsSlabAndWall(output, BlockRegistry.SMOOTH_CRAGROCK_STAIRS, BlockRegistry.SMOOTH_CRAGROCK_SLAB, BlockRegistry.SMOOTH_CRAGROCK_WALL, BlockRegistry.SMOOTH_CRAGROCK);
		stairsSlabAndWall(output, BlockRegistry.POLISHED_LIMESTONE_STAIRS, BlockRegistry.POLISHED_LIMESTONE_SLAB, BlockRegistry.POLISHED_LIMESTONE_WALL, BlockRegistry.POLISHED_LIMESTONE);
		stairsSlabAndWall(output, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_SLAB, BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL, BlockRegistry.MOSSY_BETWEENSTONE_BRICKS);
		stairsSlabAndWall(output, BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS, BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB, BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_WALL, BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE);
		stairsSlabAndWall(output, BlockRegistry.SCABYST_BRICK_STAIRS, BlockRegistry.SCABYST_BRICK_SLAB, BlockRegistry.SCABYST_BRICK_WALL, BlockRegistry.SCABYST_BRICKS);
		resourceBlock(output, BlockRegistry.SULFUR_BLOCK, ItemRegistry.SULFUR, null);
		brickBlock(output, BlockRegistry.MIRE_CORAL_BLOCK, ItemRegistry.MIRE_CORAL_STALK);
		brickBlock(output, BlockRegistry.DEEP_WATER_CORAL_BLOCK, ItemRegistry.DEEP_WATER_CORAL_STALK);
		resourceBlock(output, BlockRegistry.SLIMY_BONE_BLOCK, ItemRegistry.SLIMY_BONE, null);
		resourceBlock(output, BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK, ItemRegistry.CRIMSON_MIDDLE_GEM, null);
		resourceBlock(output, BlockRegistry.GREEN_MIDDLE_GEM_BLOCK, ItemRegistry.GREEN_MIDDLE_GEM, null);
		resourceBlock(output, BlockRegistry.AQUA_MIDDLE_GEM_BLOCK, ItemRegistry.AQUA_MIDDLE_GEM, null);
		resourceBlock(output, BlockRegistry.COMPOST_BLOCK, ItemRegistry.COMPOST, null);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SILT_GLASS_PANE, 16)
			.pattern("iii").pattern("iii").define('i', BlockRegistry.SILT_GLASS)
			.unlockedBy("has_resource", has(BlockRegistry.SILT_GLASS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FILTERED_SILT_GLASS_PANE, 16)
			.pattern("iii").pattern("iii").define('i', BlockRegistry.FILTERED_SILT_GLASS)
			.unlockedBy("has_resource", has(BlockRegistry.FILTERED_SILT_GLASS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_GREEN_DENTROTHYST_PANE, 16)
			.pattern("iii").pattern("iii").define('i', BlockRegistry.POLISHED_GREEN_DENTROTHYST)
			.unlockedBy("has_resource", has(BlockRegistry.POLISHED_GREEN_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_ORANGE_DENTROTHYST_PANE, 16)
			.pattern("iii").pattern("iii").define('i', BlockRegistry.POLISHED_ORANGE_DENTROTHYST)
			.unlockedBy("has_resource", has(BlockRegistry.POLISHED_ORANGE_DENTROTHYST)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.LATTICE, 3)
			.pattern("i i").pattern(" i ").pattern("i i").define('i', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_resource", has(ItemRegistry.WEEDWOOD_STICK)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FINE_LATTICE)
			.pattern("ii").pattern("ii").define('i', BlockRegistry.LATTICE)
			.unlockedBy("has_resource", has(BlockRegistry.LATTICE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CONNECTED_AMATE_PAPER_PANE)
			.pattern(" s ").pattern("sps").pattern(" s ").define('s', ItemRegistry.WEEDWOOD_STICK).define('p', ItemRegistry.AMATE_PAPER)
			.unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SQUARED_AMATE_PAPER_PANE).requires(BlockRegistry.CONNECTED_AMATE_PAPER_PANE).unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).save(output, TheBetweenlands.prefix("paper_pane_1_to_2"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ROUNDED_AMATE_PAPER_PANE).requires(BlockRegistry.SQUARED_AMATE_PAPER_PANE).unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).save(output, TheBetweenlands.prefix("paper_pane_2_to_3"));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CONNECTED_AMATE_PAPER_PANE).requires(BlockRegistry.ROUNDED_AMATE_PAPER_PANE).unlockedBy("has_paper", has(ItemRegistry.AMATE_PAPER)).save(output, TheBetweenlands.prefix("paper_pane_3_to_1"));
		stairsSlabAndWall(output, BlockRegistry.SMOOTH_PITSTONE_STAIRS, BlockRegistry.SMOOTH_PITSTONE_SLAB, BlockRegistry.SMOOTH_PITSTONE_WALL, BlockRegistry.SMOOTH_PITSTONE);
		stairsSlabAndWall(output, BlockRegistry.SOLID_TAR_STAIRS, BlockRegistry.SOLID_TAR_SLAB, BlockRegistry.SOLID_TAR_WALL, BlockRegistry.SOLID_TAR);
		stairsSlabAndWall(output, BlockRegistry.TEMPLE_BRICK_STAIRS, BlockRegistry.TEMPLE_BRICK_SLAB, BlockRegistry.TEMPLE_BRICK_WALL, BlockRegistry.TEMPLE_BRICKS);
		stairsAndSlab(output, BlockRegistry.THATCH_ROOF, BlockRegistry.THATCH_SLAB, BlockRegistry.THATCH);
		stairs(output, BlockRegistry.MUD_BRICK_SHINGLE_ROOF, ItemRegistry.MUD_BRICK);
		stairs(output, BlockRegistry.WEEDWOOD_STAIRS, BlockRegistry.WEEDWOOD_PLANKS);
		stairs(output, BlockRegistry.RUBBER_TREE_STAIRS, BlockRegistry.RUBBER_TREE_PLANKS);
		stairs(output, BlockRegistry.GIANT_ROOT_STAIRS, BlockRegistry.GIANT_ROOT_PLANKS);
		stairs(output, BlockRegistry.HEARTHGROVE_STAIRS, BlockRegistry.HEARTHGROVE_PLANKS);
		stairs(output, BlockRegistry.NIBBLETWIG_STAIRS, BlockRegistry.NIBBLETWIG_PLANKS);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ITEM_SHELF, 3)
			.pattern("sss").pattern("   ").pattern("sss").define('s', BlockRegistry.WEEDWOOD_SLAB)
			.unlockedBy("has_slab", has(BlockRegistry.WEEDWOOD_SLAB)).save(output);
		brickBlock(output, BlockRegistry.THATCH, ItemRegistry.DRIED_SWAMP_REED);
		slab(output, BlockRegistry.WEEDWOOD_SLAB, BlockRegistry.WEEDWOOD_PLANKS);
		slab(output, BlockRegistry.RUBBER_TREE_SLAB, BlockRegistry.RUBBER_TREE_PLANKS);
		slab(output, BlockRegistry.GIANT_ROOT_SLAB, BlockRegistry.GIANT_ROOT_PLANKS);
		slab(output, BlockRegistry.HEARTHGROVE_SLAB, BlockRegistry.HEARTHGROVE_PLANKS);
		slab(output, BlockRegistry.NIBBLETWIG_SLAB, BlockRegistry.NIBBLETWIG_PLANKS);
		stairsSlabAndWall(output, BlockRegistry.MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.MUD_BRICK_SHINGLE_SLAB, BlockRegistry.MUD_BRICK_SHINGLE_WALL, BlockRegistry.MUD_BRICK_SHINGLES);
		dying(output, BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.DULL_LAVENDER_DYE);
		dying(output, BlockRegistry.MAROON_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.MAROON_DYE);
		dying(output, BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.SHADOW_GREEN_DYE);
		dying(output, BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.CAMELOT_MAGENTA_DYE);
		dying(output, BlockRegistry.SAFFRON_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.SAFFRON_DYE);
		dying(output, BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.CARIBBEAN_GREEN_DYE);
		dying(output, BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.VIVID_TANGERINE_DYE);
		dying(output, BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.CHAMPAGNE_DYE);
		dying(output, BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.RAISIN_BLACK_DYE);
		dying(output, BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.SUSHI_GREEN_DYE);
		dying(output, BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.ELM_CYAN_DYE);
		dying(output, BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.CADMIUM_GREEN_DYE);
		dying(output, BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.LAVENDER_BLUE_DYE);
		dying(output, BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.BROWN_RUST_DYE);
		dying(output, BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.MIDNIGHT_PURPLE_DYE);
		dying(output, BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS, ItemRegistry.PEWTER_GREY_DYE);
		dying(output, BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.DULL_LAVENDER_DYE);
		dying(output, BlockRegistry.MAROON_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.MAROON_DYE);
		dying(output, BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.SHADOW_GREEN_DYE);
		dying(output, BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.CAMELOT_MAGENTA_DYE);
		dying(output, BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.SAFFRON_DYE);
		dying(output, BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.CARIBBEAN_GREEN_DYE);
		dying(output, BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.VIVID_TANGERINE_DYE);
		dying(output, BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.CHAMPAGNE_DYE);
		dying(output, BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.RAISIN_BLACK_DYE);
		dying(output, BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.SUSHI_GREEN_DYE);
		dying(output, BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.ELM_CYAN_DYE);
		dying(output, BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.CADMIUM_GREEN_DYE);
		dying(output, BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.LAVENDER_BLUE_DYE);
		dying(output, BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.BROWN_RUST_DYE);
		dying(output, BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.MIDNIGHT_PURPLE_DYE);
		dying(output, BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES, ItemRegistry.PEWTER_GREY_DYE);
		stairsAndSlab(output, BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.MAROON_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES);
		stairsAndSlab(output, BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS, BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB, BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEEDWOOD_LOG_FENCE, 3)
			.pattern("psp").pattern("psp").define('p', Ingredient.of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK)).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_resource", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK))).save(output);
		fence(output, BlockRegistry.WEEDWOOD_FENCE, BlockRegistry.WEEDWOOD_PLANKS);
		fence(output, BlockRegistry.RUBBER_TREE_FENCE, BlockRegistry.RUBBER_TREE_PLANKS);
		fence(output, BlockRegistry.GIANT_ROOT_FENCE, BlockRegistry.GIANT_ROOT_PLANKS);
		fence(output, BlockRegistry.HEARTHGROVE_FENCE, BlockRegistry.HEARTHGROVE_PLANKS);
		fence(output, BlockRegistry.NIBBLETWIG_FENCE, BlockRegistry.NIBBLETWIG_PLANKS);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEEDWOOD_LOG_FENCE_GATE)
			.pattern("sps").pattern("sps").define('p', Ingredient.of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK)).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_resource", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK))).save(output);
		gate(output, BlockRegistry.WEEDWOOD_FENCE_GATE, BlockRegistry.WEEDWOOD_PLANKS);
		gate(output, BlockRegistry.RUBBER_TREE_FENCE_GATE, BlockRegistry.RUBBER_TREE_PLANKS);
		gate(output, BlockRegistry.GIANT_ROOT_FENCE_GATE, BlockRegistry.GIANT_ROOT_PLANKS);
		gate(output, BlockRegistry.HEARTHGROVE_FENCE_GATE, BlockRegistry.HEARTHGROVE_PLANKS);
		gate(output, BlockRegistry.NIBBLETWIG_FENCE_GATE, BlockRegistry.NIBBLETWIG_PLANKS);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.WEEDWOOD_PRESSURE_PLATE).pattern("ii").define('i', BlockRegistry.WEEDWOOD_PLANKS).unlockedBy("has_resource", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.SYRMORITE_PRESSURE_PLATE).pattern("ii").define('i', ItemRegistry.SYRMORITE_INGOT).unlockedBy("has_resource", has(ItemRegistry.SYRMORITE_INGOT)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.BETWEENSTONE_PRESSURE_PLATE).pattern("ii").define('i', BlockRegistry.BETWEENSTONE).unlockedBy("has_resource", has(BlockRegistry.BETWEENSTONE)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BlockRegistry.WEEDWOOD_BUTTON).requires(BlockRegistry.WEEDWOOD_PLANKS).unlockedBy("has_resource", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BlockRegistry.BETWEENSTONE_BUTTON).requires(BlockRegistry.BETWEENSTONE).unlockedBy("has_resource", has(BlockRegistry.BETWEENSTONE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.WEEDWOOD_LADDER, 3)
			.pattern("r r").pattern("sss").pattern("r r").define('r', ItemRegistry.REED_ROPE).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BlockRegistry.WEEDWOOD_LEVER)
			.pattern("s").pattern("w").define('w', Ingredient.of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK)).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_logs", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK))).save(output);

		sludgeSet(output, BlockRegistry.WORM_PILLAR, BlockRegistry.WORM_PILLAR_TOP, BlockRegistry.CARVED_MUD_BRICKS, BlockRegistry.CARVED_MUD_BRICK_EDGE, BlockRegistry.MUD_BRICKS, BlockRegistry.MUD_BRICK_STAIRS, BlockRegistry.MUD_BRICK_SLAB, null);
		sludgeSet(output, BlockRegistry.SLUDGY_WORM_PILLAR_1, BlockRegistry.SLUDGY_WORM_PILLAR_TOP_1, BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_1, BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_1, BlockRegistry.SLUDGY_MUD_BRICKS_1, BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_1, BlockRegistry.SLUDGY_MUD_BRICK_SLAB_1, BlockRegistry.MUD_BRICKS);
		sludgeSet(output, BlockRegistry.SLUDGY_WORM_PILLAR_2, BlockRegistry.SLUDGY_WORM_PILLAR_TOP_2, BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_2, BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_2, BlockRegistry.SLUDGY_MUD_BRICKS_2, BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_2, BlockRegistry.SLUDGY_MUD_BRICK_SLAB_2, BlockRegistry.SLUDGY_MUD_BRICKS_1);
		sludgeSet(output, BlockRegistry.SLUDGY_WORM_PILLAR_3, BlockRegistry.SLUDGY_WORM_PILLAR_TOP_3, BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_3, BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_3, BlockRegistry.SLUDGY_MUD_BRICKS_3, BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_3, BlockRegistry.SLUDGY_MUD_BRICK_SLAB_3, BlockRegistry.SLUDGY_MUD_BRICKS_2);
		sludgeSet(output, BlockRegistry.SLUDGY_WORM_PILLAR_4, BlockRegistry.SLUDGY_WORM_PILLAR_TOP_4, BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_4, BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_4, BlockRegistry.SLUDGY_MUD_BRICKS_4, BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4, BlockRegistry.SLUDGY_MUD_BRICK_SLAB_4, BlockRegistry.SLUDGY_MUD_BRICKS_3);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COMPACTED_MUD).pattern("ii").pattern("ii").define('i', BlockRegistry.MUD).unlockedBy("has_resource", has(BlockRegistry.MUD)).save(output);
		brickBlock(output, BlockRegistry.MUD_TILES, BlockRegistry.MUD_BRICKS);
		stairsAndSlab(output, BlockRegistry.COMPACTED_MUD_SLOPE, BlockRegistry.COMPACTED_MUD_SLAB, BlockRegistry.COMPACTED_MUD);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ROTTEN_PLANKS, 4).requires(BlockRegistry.ROTTEN_BARK).unlockedBy("has_block", has(BlockRegistry.ROTTEN_BARK)).save(output);
		stairsAndSlab(output, BlockRegistry.ROTTEN_STAIRS, BlockRegistry.ROTTEN_SLAB, BlockRegistry.ROTTEN_PLANKS);
		fence(output, BlockRegistry.ROTTEN_FENCE, BlockRegistry.ROTTEN_PLANKS);
		gate(output, BlockRegistry.ROTTEN_FENCE_GATE, BlockRegistry.ROTTEN_PLANKS);
		brickBlock(output, BlockRegistry.BULB_CAPPED_MUSHROOM_CAP, ItemRegistry.BULB_CAPPED_MUSHROOM);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.PURIFIER)
			.pattern("p p").pattern("ppp").pattern("ooo").define('p', BlockRegistry.WEEDWOOD_PLANKS).define('o', ItemRegistry.OCTINE_INGOT)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).unlockedBy("has_ingot", has(ItemRegistry.OCTINE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEEDWOOD_CRAFTING_TABLE)
			.pattern("pp").pattern("pp").define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COMPOST_BIN)
			.pattern("sps").pattern("p p").pattern("p p").define('p', BlockRegistry.WEEDWOOD_PLANKS).define('s', ItemRegistry.SYRMORITE_INGOT)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEEDWOOD_JUKEBOX)
			.pattern("ppp").pattern("pvp").pattern("ppp").define('p', BlockRegistry.WEEDWOOD_PLANKS).define('v', ItemRegistry.VALONITE_SHARD)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).unlockedBy("has_ingot", has(ItemRegistry.VALONITE_SHARD)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SULFUR_FURNACE)
			.pattern("ppp").pattern("p p").pattern("ppp").define('p', BlockRegistry.BETWEENSTONE)
			.unlockedBy("has_stone", has(BlockRegistry.BETWEENSTONE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.DUAL_SULFUR_FURNACE)
			.pattern("ppp").pattern("pfp").pattern("ppp").define('p', BlockRegistry.BETWEENSTONE).define('f', BlockRegistry.SULFUR_FURNACE)
			.unlockedBy("has_stone", has(BlockRegistry.BETWEENSTONE)).unlockedBy("has_furnace", has(BlockRegistry.SULFUR_FURNACE)).save(output);

		//TODO weedwood chest

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.SULFUR_TORCH, 4)
			.pattern("s").pattern("r").define('s', ItemRegistry.SULFUR).define('r', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_sulfur", has(ItemRegistry.SULFUR)).save(output);

		doorAndTrapdoor(output, BlockRegistry.WEEDWOOD_DOOR, BlockRegistry.WEEDWOOD_TRAPDOOR, BlockRegistry.WEEDWOOD_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.RUBBER_TREE_DOOR, BlockRegistry.RUBBER_TREE_TRAPDOOR, BlockRegistry.RUBBER_TREE_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.GIANT_ROOT_DOOR, BlockRegistry.GIANT_ROOT_TRAPDOOR, BlockRegistry.GIANT_ROOT_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.HEARTHGROVE_DOOR, BlockRegistry.HEARTHGROVE_TRAPDOOR, BlockRegistry.HEARTHGROVE_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.NIBBLETWIG_DOOR, BlockRegistry.NIBBLETWIG_TRAPDOOR, BlockRegistry.NIBBLETWIG_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.ROTTEN_DOOR, BlockRegistry.ROTTEN_TRAPDOOR, BlockRegistry.ROTTEN_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.SYRMORITE_DOOR, BlockRegistry.SYRMORITE_TRAPDOOR, ItemRegistry.SYRMORITE_INGOT);
		doorAndTrapdoor(output, BlockRegistry.TREATED_WEEDWOOD_DOOR, BlockRegistry.TREATED_WEEDWOOD_TRAPDOOR, BlockRegistry.TREATED_WEEDWOOD_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.TREATED_RUBBER_TREE_DOOR, BlockRegistry.TREATED_RUBBER_TREE_TRAPDOOR, BlockRegistry.TREATED_RUBBER_TREE_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.TREATED_GIANT_ROOT_DOOR, BlockRegistry.TREATED_GIANT_ROOT_TRAPDOOR, BlockRegistry.TREATED_GIANT_ROOT_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.TREATED_HEARTHGROVE_DOOR, BlockRegistry.TREATED_HEARTHGROVE_TRAPDOOR, BlockRegistry.TREATED_HEARTHGROVE_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.TREATED_NIBBLETWIG_DOOR, BlockRegistry.TREATED_NIBBLETWIG_TRAPDOOR, BlockRegistry.TREATED_NIBBLETWIG_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.TREATED_ROTTEN_DOOR, BlockRegistry.TREATED_ROTTEN_TRAPDOOR, BlockRegistry.TREATED_ROTTEN_PLANKS);
		doorAndTrapdoor(output, BlockRegistry.SCABYST_DOOR, BlockRegistry.SCABYST_TRAPDOOR, ItemRegistry.SCABYST);
		//TODO syrmorite hopper
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.MUD_FLOWER_POT)
			.pattern("b b").pattern(" b ").define('b', ItemRegistry.MUD_BRICK)
			.unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK)).save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BlockRegistry.MUD_FLOWER_POT_CANDLE)
			.requires(BlockRegistry.MUD_FLOWER_POT).requires(BlockRegistry.SULFUR_TORCH)
			.unlockedBy("has_pot", has(BlockRegistry.MUD_FLOWER_POT)).unlockedBy("has_torch", has(BlockRegistry.SULFUR_TORCH)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.GECKO_CAGE)
			.pattern("isi").pattern("rrr").pattern("isi").define('s', BlockRegistry.WEEDWOOD_SLAB).define('i', ItemRegistry.SYRMORITE_INGOT).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_slab", has(BlockRegistry.WEEDWOOD_SLAB)).unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.INFUSER)
			.pattern("i i").pattern("ipi").pattern("sis").define('s', ItemRegistry.WEEDWOOD_STICK).define('i', ItemRegistry.OCTINE_INGOT).define('p', ItemRegistry.PESTLE)
			.unlockedBy("has_ingot", has(ItemRegistry.OCTINE_INGOT)).unlockedBy("has_pestle", has(ItemRegistry.PESTLE)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.MORTAR)
			.pattern("c c").pattern("ccc").pattern("s s").define('s', ItemRegistry.WEEDWOOD_STICK).define('c', BlockRegistry.CRAGROCK)
			.unlockedBy("has_rock", has(BlockRegistry.CRAGROCK)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CENSER)
			.pattern("rrr").pattern("rcr").pattern("bbb").define('b', BlockRegistry.MUD_BRICKS).define('c', ItemRegistry.CREMAINS).define('r', ItemRegistry.ANCIENT_REMNANT)
			.unlockedBy("has_bricks", has(BlockRegistry.MUD_BRICKS)).unlockedBy("has_cremains", has(ItemRegistry.CREMAINS)).unlockedBy("has_remnant", has(ItemRegistry.ANCIENT_REMNANT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEEDWOOD_BARREL)
			.pattern("sds").pattern("ibi").pattern("ppp")
			.define('s', BlockRegistry.WEEDWOOD_SLAB).define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.define('i', ItemRegistry.SYRMORITE_INGOT).define('b', ItemRegistry.WEEDWOOD_BUCKET).define('d', ItemRegistry.TAR_DRIP)
			.unlockedBy("has_slab", has(BlockRegistry.WEEDWOOD_SLAB)).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS))
			.unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).unlockedBy("has_bucket", has(ItemRegistry.WEEDWOOD_BUCKET)).unlockedBy("has_drip", has(ItemRegistry.TAR_DRIP)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SYRMORITE_BARREL)
			.pattern("iii").pattern("ibi").pattern("iii").define('b', BlockRegistry.WEEDWOOD_BARREL).define('i', ItemRegistry.SYRMORITE_INGOT)
			.unlockedBy("has_barrel", has(BlockRegistry.WEEDWOOD_BARREL)).unlockedBy("has_ingot", has(ItemRegistry.SYRMORITE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ANIMATOR)
			.pattern("ppp").pattern("shs").pattern("bbb")
			.define('b', BlockRegistry.BETWEENSTONE).define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.define('s', ItemRegistry.WEEDWOOD_STICK).define('h', ItemRegistry.WIGHT_HEART)
			.unlockedBy("has_stone", has(BlockRegistry.BETWEENSTONE)).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS))
			.unlockedBy("has_heart", has(ItemRegistry.WIGHT_HEART)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ALEMBIC)
			.pattern(" i ").pattern(" gv").pattern("cic")
			.define('g', BlockRegistry.GREEN_DENTROTHYST).define('c', BlockRegistry.CRAGROCK)
			.define('v', ItemRegistry.GREEN_DENTROTHYST_VIAL).define('i', ItemRegistry.OCTINE_INGOT)
			.unlockedBy("has_stone", has(BlockRegistry.CRAGROCK)).unlockedBy("has_dentrothyst", has(BlockRegistry.GREEN_DENTROTHYST))
			.unlockedBy("has_vial", has(ItemRegistry.GREEN_DENTROTHYST_VIAL)).unlockedBy("has_ingot", has(ItemRegistry.OCTINE_INGOT)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WALKWAY, 2)
			.pattern("ppp").pattern("s s").define('p', BlockRegistry.WEEDWOOD_PLANKS).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHIP_PATH, 4)
			.pattern("ppp").define('p', ItemRegistry.GROUND_WEEDWOOD_BARK)
			.unlockedBy("has_bark", has(ItemRegistry.GROUND_WEEDWOOD_BARK)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.REPELLER)
			.pattern("sv").pattern("s ").pattern("c ").define('c', BlockRegistry.CRAGROCK).define('v', ItemRegistry.GREEN_DENTROTHYST_VIAL).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_stone", has(BlockRegistry.CRAGROCK)).unlockedBy("has_vial", has(ItemRegistry.GREEN_DENTROTHYST_VIAL)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WIND_CHIME)
			.pattern(" r ").pattern("ara").pattern("asa")
			.define('r', ItemRegistry.REED_ROPE).define('a', ItemRegistry.ANCIENT_REMNANT).define('s', ItemRegistry.ORANGE_DENTROTHYST_SHARD)
			.unlockedBy("has_remnant", has(ItemRegistry.ANCIENT_REMNANT)).unlockedBy("has_shard", has(ItemRegistry.ORANGE_DENTROTHYST_SHARD)).save(output);

		//TODO lanterns (need firefly item)
		//TODO fishing tackle box

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMOKING_RACK)
			.pattern("srs").pattern("sts").pattern("s s").define('s', ItemRegistry.WEEDWOOD_STICK).define('t', ItemRegistry.ANGLER_TOOTH).define('r', ItemRegistry.REED_ROPE)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_tooth", has(ItemRegistry.ANGLER_TOOTH)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FISH_TRIMMING_TABLE)
			.pattern("s").pattern("l").define('s', BlockRegistry.SMOOTH_BETWEENSTONE_SLAB).define('l', BlockRegistry.WEEDWOOD_LOG)
			.unlockedBy("has_stone", has(BlockRegistry.SMOOTH_BETWEENSTONE_SLAB))
			.unlockedBy("has_fish", has(ItemRegistry.ANADIA)).unlockedBy("has_crab", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.BUBBLER_CRAB, ItemRegistry.SILT_CRAB))) //bonus: unlock after a fish or crab is caught
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRAB_POT)
			.pattern("sss").pattern("r r").pattern("ppp")
			.define('r', ItemRegistry.REED_ROPE).define('s', ItemRegistry.WEEDWOOD_STICK).define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS))
			.unlockedBy("has_remains", has(ItemRegistry.ANADIA_REMAINS)) //bonus: unlock when remains are obtained
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRAB_POT_FILTER)
			.pattern("ppp").pattern("r r").pattern("ppp")
			.define('r', ItemRegistry.REED_ROPE).define('p', BlockRegistry.ROTTEN_PLANKS)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_planks", has(BlockRegistry.ROTTEN_PLANKS))
			.unlockedBy("has_remains", has(ItemRegistry.ANADIA_REMAINS)).unlockedBy("has_crab", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.BUBBLER_CRAB, ItemRegistry.SILT_CRAB))) //bonus: unlock when remains are obtained or crab is caught
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SILT_GLASS_JAR)
			.pattern(" g ").pattern("g g").pattern("ggg").define('g', BlockRegistry.SILT_GLASS).unlockedBy("has_glass", has(BlockRegistry.SILT_GLASS))
			.unlockedBy("has_worm", has(ItemRegistry.TINY_SLUDGE_WORM)) //bonus: unlock when a worm is caught
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BlockRegistry.GLOWING_GOOP, 4)
			.requires(ItemRegistry.SLUDGE_BALL).requires(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM)
			.unlockedBy("has_sludge", has(ItemRegistry.SLUDGE_BALL)).unlockedBy("has_mushroom", has(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.REED_MAT).pattern("ggg").define('g', ItemRegistry.DRIED_SWAMP_REED).unlockedBy("has_reed", has(ItemRegistry.DRIED_SWAMP_REED)).save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BlockRegistry.REED_MAT).requires(BLItemTagProvider.REED_MATS).unlockedBy("has_mat", has(BLItemTagProvider.REED_MATS)).save(output, TheBetweenlands.prefix("undye_reed_mat"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STEEPING_POT)
			.pattern("brb").pattern("sbs").pattern("ccc")
			.define('r', ItemRegistry.REED_ROPE).define('s', ItemRegistry.SLIMY_BONE)
			.define('b', ItemRegistry.MUD_BRICK).define('c', BlockRegistry.SMOOTH_CRAGROCK_SLAB)
			.unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_slabs", has(BlockRegistry.SMOOTH_CRAGROCK_SLAB))
			.unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK)).unlockedBy("has_bone", has(ItemRegistry.SLIMY_BONE))
			.unlockedBy("has_bundle", has(ItemRegistry.SILK_BUNDLE)) //bonus: unlock when bundle is obtained
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.GRUB_HUB)
			.pattern("pmp").pattern("mjm").pattern(" m ")
			.define('m', ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK).define('j', BlockRegistry.FILTERED_SILT_GLASS_JAR).define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.unlockedBy("has_mask", has(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK))
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).unlockedBy("has_jar", has(BlockRegistry.FILTERED_SILT_GLASS_JAR))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WATER_FILTER)
			.pattern("l l").pattern("sls").pattern("rpr")
			.define('l', ItemRegistry.LURKER_SKIN).define('s', ItemRegistry.WEEDWOOD_STICK).define('r', ItemRegistry.REED_ROPE).define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.unlockedBy("has_skin", has(ItemRegistry.LURKER_SKIN)).unlockedBy("has_rope", has(ItemRegistry.REED_ROPE)).unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FILTERED_SILT_GLASS_JAR).pattern(" g ").pattern("g g").pattern("ggg").define('g', BlockRegistry.FILTERED_SILT_GLASS).unlockedBy("has_glass", has(BlockRegistry.FILTERED_SILT_GLASS)).save(output);
		//TODO moth house

		dying(output, BlockRegistry.DULL_LAVENDER_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.DULL_LAVENDER_DYE);
		dying(output, BlockRegistry.MAROON_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.MAROON_DYE);
		dying(output, BlockRegistry.SHADOW_GREEN_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.SHADOW_GREEN_DYE);
		dying(output, BlockRegistry.CAMELOT_MAGENTA_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.CAMELOT_MAGENTA_DYE);
		dying(output, BlockRegistry.SAFFRON_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.SAFFRON_DYE);
		dying(output, BlockRegistry.CARIBBEAN_GREEN_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.CARIBBEAN_GREEN_DYE);
		dying(output, BlockRegistry.VIVID_TANGERINE_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.VIVID_TANGERINE_DYE);
		dying(output, BlockRegistry.CHAMPAGNE_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.CHAMPAGNE_DYE);
		dying(output, BlockRegistry.RAISIN_BLACK_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.RAISIN_BLACK_DYE);
		dying(output, BlockRegistry.SUSHI_GREEN_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.SUSHI_GREEN_DYE);
		dying(output, BlockRegistry.ELM_CYAN_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.ELM_CYAN_DYE);
		dying(output, BlockRegistry.CADMIUM_GREEN_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.CADMIUM_GREEN_DYE);
		dying(output, BlockRegistry.LAVENDER_BLUE_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.LAVENDER_BLUE_DYE);
		dying(output, BlockRegistry.BROWN_RUST_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.BROWN_RUST_DYE);
		dying(output, BlockRegistry.MIDNIGHT_PURPLE_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.MIDNIGHT_PURPLE_DYE);
		dying(output, BlockRegistry.PEWTER_GREY_SAMITE, BLItemTagProvider.SAMITE, ItemRegistry.PEWTER_GREY_DYE);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CHAMPAGNE_SAMITE)
			.pattern("iii").pattern("iii").pattern("iii")
			.define('i', ItemRegistry.SILK_THREAD)
			.unlockedBy("has_resource", has(ItemRegistry.SILK_THREAD))
			.save(output, TheBetweenlands.prefix("samite_from_thread"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.SILK_THREAD, 9)
			.requires(BlockRegistry.CHAMPAGNE_SAMITE)
			.unlockedBy("has_resource", has(BlockRegistry.CHAMPAGNE_SAMITE))
			.save(output, TheBetweenlands.prefix("thread_from_samite"));
		dying(output, BlockRegistry.DULL_LAVENDER_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.DULL_LAVENDER_DYE);
		dying(output, BlockRegistry.MAROON_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.MAROON_DYE);
		dying(output, BlockRegistry.SHADOW_GREEN_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.SHADOW_GREEN_DYE);
		dying(output, BlockRegistry.CAMELOT_MAGENTA_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.CAMELOT_MAGENTA_DYE);
		dying(output, BlockRegistry.SAFFRON_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.SAFFRON_DYE);
		dying(output, BlockRegistry.CARIBBEAN_GREEN_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.CARIBBEAN_GREEN_DYE);
		dying(output, BlockRegistry.VIVID_TANGERINE_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.VIVID_TANGERINE_DYE);
		dying(output, BlockRegistry.CHAMPAGNE_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.CHAMPAGNE_DYE);
		dying(output, BlockRegistry.RAISIN_BLACK_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.RAISIN_BLACK_DYE);
		dying(output, BlockRegistry.SUSHI_GREEN_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.SUSHI_GREEN_DYE);
		dying(output, BlockRegistry.ELM_CYAN_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.ELM_CYAN_DYE);
		dying(output, BlockRegistry.CADMIUM_GREEN_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.CADMIUM_GREEN_DYE);
		dying(output, BlockRegistry.LAVENDER_BLUE_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.LAVENDER_BLUE_DYE);
		dying(output, BlockRegistry.BROWN_RUST_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.BROWN_RUST_DYE);
		dying(output, BlockRegistry.MIDNIGHT_PURPLE_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.MIDNIGHT_PURPLE_DYE);
		dying(output, BlockRegistry.PEWTER_GREY_REED_MAT, BlockRegistry.REED_MAT, ItemRegistry.PEWTER_GREY_DYE);

		panel(output, BlockRegistry.DULL_LAVENDER_SAMITE, BlockRegistry.DULL_LAVENDER_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.MAROON_SAMITE, BlockRegistry.MAROON_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.SHADOW_GREEN_SAMITE, BlockRegistry.SHADOW_GREEN_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.CAMELOT_MAGENTA_SAMITE, BlockRegistry.CAMELOT_MAGENTA_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.SAFFRON_SAMITE, BlockRegistry.SAFFRON_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.CARIBBEAN_GREEN_SAMITE, BlockRegistry.CARIBBEAN_GREEN_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.VIVID_TANGERINE_SAMITE, BlockRegistry.VIVID_TANGERINE_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.CHAMPAGNE_SAMITE, BlockRegistry.CHAMPAGNE_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.RAISIN_BLACK_SAMITE, BlockRegistry.RAISIN_BLACK_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.SUSHI_GREEN_SAMITE, BlockRegistry.SUSHI_GREEN_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.ELM_CYAN_SAMITE, BlockRegistry.ELM_CYAN_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.CADMIUM_GREEN_SAMITE, BlockRegistry.CADMIUM_GREEN_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.LAVENDER_BLUE_SAMITE, BlockRegistry.LAVENDER_BLUE_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.BROWN_RUST_SAMITE, BlockRegistry.BROWN_RUST_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.MIDNIGHT_PURPLE_SAMITE, BlockRegistry.MIDNIGHT_PURPLE_SAMITE_CANVAS_PANEL);
		panel(output, BlockRegistry.PEWTER_GREY_SAMITE, BlockRegistry.PEWTER_GREY_SAMITE_CANVAS_PANEL);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_WEEDWOOD_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_WEEDWOOD_STAIRS, BlockRegistry.TREATED_WEEDWOOD_SLAB, BlockRegistry.TREATED_WEEDWOOD_PLANKS);
		fence(output, BlockRegistry.TREATED_WEEDWOOD_FENCE, BlockRegistry.TREATED_WEEDWOOD_PLANKS);
		gate(output, BlockRegistry.TREATED_WEEDWOOD_FENCE_GATE, BlockRegistry.TREATED_WEEDWOOD_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_RUBBER_TREE_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.RUBBER_TREE_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.RUBBER_TREE_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_RUBBER_TREE_STAIRS, BlockRegistry.TREATED_RUBBER_TREE_SLAB, BlockRegistry.TREATED_RUBBER_TREE_PLANKS);
		fence(output, BlockRegistry.TREATED_RUBBER_TREE_FENCE, BlockRegistry.TREATED_RUBBER_TREE_PLANKS);
		gate(output, BlockRegistry.TREATED_RUBBER_TREE_FENCE_GATE, BlockRegistry.TREATED_RUBBER_TREE_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_GIANT_ROOT_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.GIANT_ROOT_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.GIANT_ROOT_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_GIANT_ROOT_STAIRS, BlockRegistry.TREATED_GIANT_ROOT_SLAB, BlockRegistry.TREATED_GIANT_ROOT_PLANKS);
		fence(output, BlockRegistry.TREATED_GIANT_ROOT_FENCE, BlockRegistry.TREATED_GIANT_ROOT_PLANKS);
		gate(output, BlockRegistry.TREATED_GIANT_ROOT_FENCE_GATE, BlockRegistry.TREATED_GIANT_ROOT_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_HEARTHGROVE_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.HEARTHGROVE_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.HEARTHGROVE_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_HEARTHGROVE_STAIRS, BlockRegistry.TREATED_HEARTHGROVE_SLAB, BlockRegistry.TREATED_HEARTHGROVE_PLANKS);
		fence(output, BlockRegistry.TREATED_HEARTHGROVE_FENCE, BlockRegistry.TREATED_HEARTHGROVE_PLANKS);
		gate(output, BlockRegistry.TREATED_HEARTHGROVE_FENCE_GATE, BlockRegistry.TREATED_HEARTHGROVE_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_NIBBLETWIG_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.NIBBLETWIG_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.NIBBLETWIG_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_NIBBLETWIG_STAIRS, BlockRegistry.TREATED_NIBBLETWIG_SLAB, BlockRegistry.TREATED_NIBBLETWIG_PLANKS);
		fence(output, BlockRegistry.TREATED_NIBBLETWIG_FENCE, BlockRegistry.TREATED_NIBBLETWIG_PLANKS);
		gate(output, BlockRegistry.TREATED_NIBBLETWIG_FENCE_GATE, BlockRegistry.TREATED_NIBBLETWIG_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.TREATED_ROTTEN_PLANKS, 8)
			.pattern("ppp").pattern("pbp").pattern("ppp")
			.define('p', BlockRegistry.ROTTEN_PLANKS)
			.define('b', DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.FISH_OIL_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get(), ItemRegistry.SYRMORITE_BUCKET.get()))
			.unlockedBy("has_planks", has(BlockRegistry.ROTTEN_PLANKS)).save(output);

		stairsAndSlab(output, BlockRegistry.TREATED_ROTTEN_STAIRS, BlockRegistry.TREATED_ROTTEN_SLAB, BlockRegistry.TREATED_ROTTEN_PLANKS);
		fence(output, BlockRegistry.TREATED_ROTTEN_FENCE, BlockRegistry.TREATED_ROTTEN_PLANKS);
		gate(output, BlockRegistry.TREATED_ROTTEN_FENCE_GATE, BlockRegistry.TREATED_ROTTEN_PLANKS);

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.WEEDWOOD_SIGN, 3)
			.pattern("ppp").pattern("ppp").pattern(" s ")
			.define('p', BlockRegistry.WEEDWOOD_PLANKS)
			.define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BlockRegistry.MOSS_BED).pattern("mmm").pattern("ppp")
			.define('p', BlockRegistry.WEEDWOOD_PLANKS).define('m', Ingredient.of(ItemRegistry.MOSS_CLUMP, ItemRegistry.CAVE_MOSS_CLUMP, BlockRegistry.CAVE_MOSS, BlockRegistry.MOSS))
			.unlockedBy("has_planks", has(BlockRegistry.WEEDWOOD_PLANKS)).unlockedBy("has_moss", inventoryTrigger(ItemPredicate.Builder.item().of(ItemRegistry.MOSS_CLUMP, ItemRegistry.CAVE_MOSS_CLUMP, BlockRegistry.CAVE_MOSS, BlockRegistry.MOSS))).save(output);
	}

	private static void resourceBlock(RecipeOutput output, ItemLike block, ItemLike ingredient, @Nullable ItemLike nugget) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
			.pattern("iii").pattern("iii").pattern("iii")
			.define('i', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingredient, 9)
			.requires(block)
			.unlockedBy("has_resource", has(block))
			.save(output, TheBetweenlands.prefix(BuiltInRegistries.ITEM.getKey(ingredient.asItem()).getPath() + "_from_block"));

		if (nugget != null) {
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingredient)
				.pattern("iii").pattern("iii").pattern("iii")
				.define('i', nugget)
				.unlockedBy("has_resource", has(nugget))
				.save(output, TheBetweenlands.prefix(BuiltInRegistries.ITEM.getKey(ingredient.asItem()).getPath() + "_from_nuggets"));

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
				.requires(ingredient)
				.unlockedBy("has_resource", has(ingredient))
				.save(output, TheBetweenlands.prefix(BuiltInRegistries.ITEM.getKey(nugget.asItem()).getPath() + "_from_ingot"));
		}
	}

	private static void woodAndPlanks(RecipeOutput output, ItemLike log, ItemLike wood, ItemLike planks, ItemLike... otherPlankIngredients) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
			.pattern("ii")
			.pattern("ii")
			.define('i', log)
			.unlockedBy("has_resource", has(log))
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
			.requires(Ingredient.of(Stream.concat(Arrays.stream(otherPlankIngredients), Stream.of(log, wood)).map(ItemStack::new)))
			.unlockedBy("has_others", inventoryTrigger(ItemPredicate.Builder.item().of(otherPlankIngredients)))
			.unlockedBy("has_base", inventoryTrigger(ItemPredicate.Builder.item().of(log, wood)))
			.save(output);
	}

	private static void mossyBlock(RecipeOutput output, ItemLike mossyBlock, ItemLike block) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyBlock)
			.requires(block).requires(Ingredient.of(BlockRegistry.MOSS, BlockRegistry.CAVE_MOSS, ItemRegistry.MOSS_CLUMP, ItemRegistry.CAVE_MOSS_CLUMP))
			.unlockedBy("has_moss", inventoryTrigger(ItemPredicate.Builder.item().of(BlockRegistry.MOSS, BlockRegistry.CAVE_MOSS, ItemRegistry.MOSS_CLUMP, ItemRegistry.CAVE_MOSS_CLUMP)))
			.unlockedBy("has_block", has(block))
			.save(output);
	}

	private static void brickBlock(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 4)
			.pattern("ii")
			.pattern("ii")
			.define('i', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void stairs(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 4)
			.pattern("i  ")
			.pattern("ii ")
			.pattern("iii")
			.define('i', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void wall(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
			.pattern("iii")
			.pattern("iii")
			.define('i', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void slab(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
			.pattern("iii")
			.define('i', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void fence(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 3)
			.pattern("psp")
			.pattern("psp")
			.define('p', ingredient)
			.define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void gate(RecipeOutput output, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
			.pattern("sps")
			.pattern("sps")
			.define('p', ingredient)
			.define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void doorAndTrapdoor(RecipeOutput output, ItemLike door, ItemLike trapdoor, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, door, 3)
			.pattern("pp")
			.pattern("pp")
			.pattern("pp")
			.define('p', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, trapdoor, 2)
			.pattern("ppp")
			.pattern("ppp")
			.define('p', ingredient)
			.unlockedBy("has_resource", has(ingredient))
			.save(output);
	}

	private static void stairsAndSlab(RecipeOutput output, ItemLike stairs, ItemLike slab, ItemLike ingredient) {
		stairs(output, stairs, ingredient);
		slab(output, slab, ingredient);
	}

	private static void stairsSlabAndWall(RecipeOutput output, ItemLike stairs, ItemLike slab, ItemLike wall, ItemLike ingredient) {
		stairs(output, stairs, ingredient);
		slab(output, slab, ingredient);
		wall(output, wall, ingredient);
	}

	private static void dying(RecipeOutput output, ItemLike dyedBlock, ItemLike block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, dyedBlock, 8)
			.pattern("iii").pattern("idi").pattern("iii")
			.define('i', block).define('d', ingredient)
			.unlockedBy("has_block", has(block))
			.unlockedBy("has_dye", has(ingredient))
			.save(output);
	}

	private static void dying(RecipeOutput output, ItemLike dyedBlock, TagKey<Item> block, ItemLike ingredient) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, dyedBlock, 8)
			.pattern("iii").pattern("idi").pattern("iii")
			.define('i', block).define('d', ingredient)
			.unlockedBy("has_block", has(block))
			.unlockedBy("has_dye", has(ingredient))
			.save(output);
	}

	private static void panel(RecipeOutput output, ItemLike samite, ItemLike panel) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, panel, 4)
			.pattern(" s ").pattern("sis").pattern(" s ")
			.define('i', samite).define('s', ItemRegistry.WEEDWOOD_STICK)
			.unlockedBy("has_block", has(samite))
			.save(output);
	}

	private static void sludgeSet(RecipeOutput output, ItemLike pillar, ItemLike pillarCap, ItemLike carved, ItemLike edge, ItemLike bricks, ItemLike stairs, ItemLike slab, @Nullable ItemLike previousStageBricks) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillar, 2)
			.pattern("i").pattern("i")
			.define('i', bricks)
			.unlockedBy("has_block", has(bricks))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillarCap, 4)
			.pattern("iii").pattern("ibi").pattern("iii")
			.define('i', bricks)
			.define('b', ItemRegistry.MUD_BRICK)
			.unlockedBy("has_block", has(bricks))
			.unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK))
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, carved, 4)
			.pattern("iii").pattern("bbb").pattern("iii")
			.define('i', bricks)
			.define('b', ItemRegistry.MUD_BRICK)
			.unlockedBy("has_block", has(bricks))
			.unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK))
			.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, edge).requires(carved).unlockedBy("has_block", has(bricks)).unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK))
			.save(output, TheBetweenlands.prefix(BuiltInRegistries.ITEM.getKey(carved.asItem()).getPath() + "_to_edge"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, carved).requires(edge).unlockedBy("has_block", has(bricks)).unlockedBy("has_brick", has(ItemRegistry.MUD_BRICK))
			.save(output, TheBetweenlands.prefix(BuiltInRegistries.ITEM.getKey(edge.asItem()).getPath() + "_to_carved"));

		if (previousStageBricks != null) {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, bricks)
				.requires(previousStageBricks).requires(ItemRegistry.SLUDGE_BALL)
				.unlockedBy("has_block", has(bricks))
				.unlockedBy("has_sludge", has(ItemRegistry.SLUDGE_BALL))
				.save(output);
		}
		stairs(output, stairs, bricks);
		slab(output, slab, bricks);
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
