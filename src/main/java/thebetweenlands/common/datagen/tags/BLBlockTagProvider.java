package thebetweenlands.common.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class BLBlockTagProvider extends BlockTagsProvider {

	public static final TagKey<Block> PRESENTS = tag("presents");
	public static final TagKey<Block> OCTINE_IGNITES = tag("octine_ignites");
	public static final TagKey<Block> STALKER_IGNORED_LIGHT_SOURCES = tag("stalker_ignored_light_sources");
	public static final TagKey<Block> PEAT_MUMMY_SPAWNABLE = tag("peat_mummy_spawnable");
	public static final TagKey<Block> FILTERED_SILT_GLASS = tag("filtered_silt_glass");
	public static final TagKey<Block> MUD_BRICK_SHINGLES = tag("mud_brick_shingles");
	public static final TagKey<Block> REED_MATS = tag("reed_mats");
	public static final TagKey<Block> SAMITE = tag("samite");
	public static final TagKey<Block> SAMITE_CANVAS_PANELS = tag("samite_canvas_panels");
	public static final TagKey<Block> EMBERLING_HEALS_ON = tag("emberling_heals_on");
	public static final TagKey<Block> HEATS_INFUSER = tag("heats_infuser");
	public static final TagKey<Block> HEATS_SMOKING_RACK = tag("heats_smoking_rack");

	public static final TagKey<Block> INCORRECT_FOR_WEEDWOOD_TOOL = tag("incorrect_for_weedwood_tool");
	public static final TagKey<Block> INCORRECT_FOR_BONE_TOOL = tag("incorrect_for_bone_tool");
	public static final TagKey<Block> INCORRECT_FOR_OCTINE_TOOL = tag("incorrect_for_octine_tool");
	public static final TagKey<Block> INCORRECT_FOR_VALONITE_TOOL = tag("incorrect_for_valonite_tool");

	public static final TagKey<Block> DYED_DULL_LAVENDER = commonTag("dyed/dull_lavender");
	public static final TagKey<Block> DYED_MAROON = commonTag("dyed/maroon");
	public static final TagKey<Block> DYED_SHADOW_GREEN = commonTag("dyed/shadow_green");
	public static final TagKey<Block> DYED_CAMELOT_MAGENTA = commonTag("dyed/camelot_magenta");
	public static final TagKey<Block> DYED_SAFFRON = commonTag("dyed/saffron");
	public static final TagKey<Block> DYED_CARIBBEAN_GREEN = commonTag("dyed/caribbean_green");
	public static final TagKey<Block> DYED_VIVID_TANGERINE = commonTag("dyed/vivid_tangerine");
	public static final TagKey<Block> DYED_CHAMPAGNE = commonTag("dyed/champagne");
	public static final TagKey<Block> DYED_RAISIN_BLACK = commonTag("dyed/raisin_black");
	public static final TagKey<Block> DYED_SUSHI_GREEN = commonTag("dyed/sushi_green");
	public static final TagKey<Block> DYED_ELM_CYAN = commonTag("dyed/elm_cyan");
	public static final TagKey<Block> DYED_CADMIUM_GREEN = commonTag("dyed/cadmium_green");
	public static final TagKey<Block> DYED_LAVENDER_BLUE = commonTag("dyed/lavender_blue");
	public static final TagKey<Block> DYED_BROWN_RUST = commonTag("dyed/brown_rust");
	public static final TagKey<Block> DYED_MIDNIGHT_PURPLE = commonTag("dyed/midnight_purple");
	public static final TagKey<Block> DYED_PEWTER_GREY = commonTag("dyed/pewter_grey");

	public static final TagKey<Block> ORE_BEARING_GROUND_BETWEENSTONE = commonTag("ore_bearing_ground/betweenstone");
	public static final TagKey<Block> ORE_BEARING_GROUND_MUD = commonTag("ore_bearing_ground/mud");
	public static final TagKey<Block> ORE_BEARING_GROUND_PITSTONE = commonTag("ore_bearing_ground/pitstone");

	public static final TagKey<Block> ORES_IN_GROUND_BETWEENSTONE = commonTag("ores_in_ground/betweenstone");
	public static final TagKey<Block> ORES_IN_GROUND_MUD = commonTag("ores_in_ground/mud");
	public static final TagKey<Block> ORES_IN_GROUND_PITSTONE = commonTag("ores_in_ground/pitstone");

	public static final TagKey<Block> ORES_OCTINE = commonTag("ores/octine");
	public static final TagKey<Block> ORES_VALONITE = commonTag("ores/valonite");
	public static final TagKey<Block> ORES_SULFUR = commonTag("ores/sulfur");
	public static final TagKey<Block> ORES_SLIMY_BONE = commonTag("ores/slimy_bone");
	public static final TagKey<Block> ORES_SCABYST = commonTag("ores/scabyst");
	public static final TagKey<Block> ORES_SYRMORITE = commonTag("ores/syrmorite");
	public static final TagKey<Block> ORES_CRIMSON_MIDDLE_GEM = commonTag("ores/crimson_middle_gem");
	public static final TagKey<Block> ORES_GREEN_MIDDLE_GEM = commonTag("ores/green_middle_gem");
	public static final TagKey<Block> ORES_AQUA_MIDDLE_GEM = commonTag("ores/aqua_middle_gem");

	public static final TagKey<Block> STORAGE_BLOCKS_OCTINE = commonTag("storage_blocks/octine");
	public static final TagKey<Block> STORAGE_BLOCKS_VALONITE = commonTag("storage_blocks/valonite");
	public static final TagKey<Block> STORAGE_BLOCKS_SULFUR = commonTag("storage_blocks/sulfur");
	public static final TagKey<Block> STORAGE_BLOCKS_SLIMY_BONE = commonTag("storage_blocks/slimy_bone");
	public static final TagKey<Block> STORAGE_BLOCKS_SCABYST = commonTag("storage_blocks/scabyst");
	public static final TagKey<Block> STORAGE_BLOCKS_SYRMORITE = commonTag("storage_blocks/syrmorite");
	public static final TagKey<Block> STORAGE_BLOCKS_CRIMSON_MIDDLE_GEM = commonTag("storage_blocks/crimson_middle_gem");
	public static final TagKey<Block> STORAGE_BLOCKS_GREEN_MIDDLE_GEM = commonTag("storage_blocks/green_middle_gem");
	public static final TagKey<Block> STORAGE_BLOCKS_AQUA_MIDDLE_GEM = commonTag("storage_blocks/aqua_middle_gem");
	public static final TagKey<Block> STORAGE_BLOCKS_PEARL = commonTag("storage_blocks/pearl");
	public static final TagKey<Block> STORAGE_BLOCKS_ANCIENT_REMNANT = commonTag("storage_blocks/ancient_remnant");
	public static final TagKey<Block> STORAGE_BLOCKS_RUBBER = commonTag("storage_blocks/rubber");
	public static final TagKey<Block> STORAGE_BLOCKS_COMPOST = commonTag("storage_blocks/compost");

	public BLBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(OCTINE_IGNITES).add(BlockRegistry.CAVE_MOSS.get(), BlockRegistry.MOSS.get(), BlockRegistry.LICHEN.get(), BlockRegistry.DEAD_MOSS.get(), BlockRegistry.DEAD_LICHEN.get(), BlockRegistry.THORNS.get());
		this.tag(STALKER_IGNORED_LIGHT_SOURCES).add(BlockRegistry.OCTINE_ORE.get(), BlockRegistry.LIFE_CRYSTAL_STALACTITE.get(), BlockRegistry.MOB_SPAWNER.get());
		this.tag(PEAT_MUMMY_SPAWNABLE).add(BlockRegistry.MUD.get(), BlockRegistry.PEAT.get());
		this.tag(EMBERLING_HEALS_ON).add(BlockRegistry.OCTINE_BLOCK.get(), Blocks.MAGMA_BLOCK);
		this.tag(HEATS_INFUSER).add(BlockRegistry.SMOULDERING_PEAT.get()).addTag(BlockTags.FIRE);
		this.tag(HEATS_SMOKING_RACK).add(BlockRegistry.SMOULDERING_PEAT.get());
		this.tag(INCORRECT_FOR_BONE_TOOL).addTag(BlockTags.INCORRECT_FOR_STONE_TOOL);
		this.tag(INCORRECT_FOR_OCTINE_TOOL).addTag(BlockTags.INCORRECT_FOR_IRON_TOOL);
		this.tag(INCORRECT_FOR_VALONITE_TOOL).addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
		this.tag(INCORRECT_FOR_WEEDWOOD_TOOL).addTag(BlockTags.INCORRECT_FOR_WOODEN_TOOL);

		this.tag(FILTERED_SILT_GLASS).add(BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS.get(), BlockRegistry.MAROON_FILTERED_SILT_GLASS.get(),
			BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS.get(), BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS.get(),
			BlockRegistry.SAFFRON_FILTERED_SILT_GLASS.get(), BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS.get(),
			BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS.get(), BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS.get(),
			BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS.get(), BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS.get(),
			BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS.get(), BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS.get(),
			BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS.get(), BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS.get(),
			BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS.get(), BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS.get());

		this.tag(MUD_BRICK_SHINGLES).add(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES.get());

		this.tag(SAMITE).add(BlockRegistry.DULL_LAVENDER_SAMITE.get(), BlockRegistry.MAROON_SAMITE.get(),
			BlockRegistry.SHADOW_GREEN_SAMITE.get(), BlockRegistry.CAMELOT_MAGENTA_SAMITE.get(),
			BlockRegistry.SAFFRON_SAMITE.get(), BlockRegistry.CARIBBEAN_GREEN_SAMITE.get(),
			BlockRegistry.VIVID_TANGERINE_SAMITE.get(), BlockRegistry.CHAMPAGNE_SAMITE.get(),
			BlockRegistry.RAISIN_BLACK_SAMITE.get(), BlockRegistry.SUSHI_GREEN_SAMITE.get(),
			BlockRegistry.ELM_CYAN_SAMITE.get(), BlockRegistry.CADMIUM_GREEN_SAMITE.get(),
			BlockRegistry.LAVENDER_BLUE_SAMITE.get(), BlockRegistry.BROWN_RUST_SAMITE.get(),
			BlockRegistry.MIDNIGHT_PURPLE_SAMITE.get(), BlockRegistry.PEWTER_GREY_SAMITE.get());

		this.tag(SAMITE_CANVAS_PANELS).add(BlockRegistry.DULL_LAVENDER_SAMITE_CANVAS_PANEL.get(), BlockRegistry.MAROON_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.SHADOW_GREEN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CAMELOT_MAGENTA_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.SAFFRON_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CARIBBEAN_GREEN_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.VIVID_TANGERINE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CHAMPAGNE_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.RAISIN_BLACK_SAMITE_CANVAS_PANEL.get(), BlockRegistry.SUSHI_GREEN_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.ELM_CYAN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CADMIUM_GREEN_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.LAVENDER_BLUE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.BROWN_RUST_SAMITE_CANVAS_PANEL.get(),
			BlockRegistry.MIDNIGHT_PURPLE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.PEWTER_GREY_SAMITE_CANVAS_PANEL.get());

		this.tag(REED_MATS).add(BlockRegistry.DULL_LAVENDER_REED_MAT.get(), BlockRegistry.MAROON_REED_MAT.get(),
			BlockRegistry.SHADOW_GREEN_REED_MAT.get(), BlockRegistry.CAMELOT_MAGENTA_REED_MAT.get(),
			BlockRegistry.SAFFRON_REED_MAT.get(), BlockRegistry.CARIBBEAN_GREEN_REED_MAT.get(),
			BlockRegistry.VIVID_TANGERINE_REED_MAT.get(), BlockRegistry.CHAMPAGNE_REED_MAT.get(),
			BlockRegistry.RAISIN_BLACK_REED_MAT.get(), BlockRegistry.SUSHI_GREEN_REED_MAT.get(),
			BlockRegistry.ELM_CYAN_REED_MAT.get(), BlockRegistry.CADMIUM_GREEN_REED_MAT.get(),
			BlockRegistry.LAVENDER_BLUE_REED_MAT.get(), BlockRegistry.BROWN_RUST_REED_MAT.get(),
			BlockRegistry.MIDNIGHT_PURPLE_REED_MAT.get(), BlockRegistry.PEWTER_GREY_REED_MAT.get());

		this.tag(BlockTags.PLANKS).add(
			BlockRegistry.WEEDWOOD_PLANKS.get(), BlockRegistry.RUBBER_TREE_PLANKS.get(), BlockRegistry.GIANT_ROOT_PLANKS.get(),
			BlockRegistry.HEARTHGROVE_PLANKS.get(), BlockRegistry.NIBBLETWIG_PLANKS.get(), BlockRegistry.ROTTEN_PLANKS.get(),
			BlockRegistry.TREATED_WEEDWOOD_PLANKS.get(), BlockRegistry.TREATED_RUBBER_TREE_PLANKS.get(), BlockRegistry.TREATED_GIANT_ROOT_PLANKS.get(),
			BlockRegistry.TREATED_HEARTHGROVE_PLANKS.get(), BlockRegistry.TREATED_NIBBLETWIG_PLANKS.get(), BlockRegistry.TREATED_ROTTEN_PLANKS.get());

		this.tag(BlockTags.WOODEN_BUTTONS).add(BlockRegistry.WEEDWOOD_BUTTON.get());
		this.tag(BlockTags.STONE_BUTTONS).add(BlockRegistry.BETWEENSTONE_BUTTON.get());
		this.tag(BlockTags.WOODEN_DOORS).add(
			BlockRegistry.WEEDWOOD_DOOR.get(), BlockRegistry.RUBBER_TREE_DOOR.get(), BlockRegistry.GIANT_ROOT_DOOR.get(),
			BlockRegistry.HEARTHGROVE_DOOR.get(), BlockRegistry.NIBBLETWIG_DOOR.get(), BlockRegistry.ROTTEN_DOOR.get(),
			BlockRegistry.TREATED_WEEDWOOD_DOOR.get(), BlockRegistry.TREATED_RUBBER_TREE_DOOR.get(), BlockRegistry.TREATED_GIANT_ROOT_DOOR.get(),
			BlockRegistry.TREATED_HEARTHGROVE_DOOR.get(), BlockRegistry.TREATED_NIBBLETWIG_DOOR.get(), BlockRegistry.TREATED_ROTTEN_DOOR.get());
		this.tag(BlockTags.WOODEN_STAIRS).add(
			BlockRegistry.WEEDWOOD_STAIRS.get(), BlockRegistry.RUBBER_TREE_STAIRS.get(), BlockRegistry.GIANT_ROOT_STAIRS.get(),
			BlockRegistry.HEARTHGROVE_STAIRS.get(), BlockRegistry.NIBBLETWIG_STAIRS.get(), BlockRegistry.ROTTEN_STAIRS.get(),
			BlockRegistry.TREATED_WEEDWOOD_STAIRS.get(), BlockRegistry.TREATED_RUBBER_TREE_STAIRS.get(), BlockRegistry.TREATED_GIANT_ROOT_STAIRS.get(),
			BlockRegistry.TREATED_HEARTHGROVE_STAIRS.get(), BlockRegistry.TREATED_NIBBLETWIG_STAIRS.get(), BlockRegistry.TREATED_ROTTEN_STAIRS.get());
		this.tag(BlockTags.WOODEN_SLABS).add(
			BlockRegistry.WEEDWOOD_SLAB.get(), BlockRegistry.RUBBER_TREE_SLAB.get(), BlockRegistry.GIANT_ROOT_SLAB.get(),
			BlockRegistry.HEARTHGROVE_SLAB.get(), BlockRegistry.NIBBLETWIG_SLAB.get(), BlockRegistry.ROTTEN_SLAB.get(),
			BlockRegistry.TREATED_WEEDWOOD_SLAB.get(), BlockRegistry.TREATED_RUBBER_TREE_SLAB.get(), BlockRegistry.TREATED_GIANT_ROOT_SLAB.get(),
			BlockRegistry.TREATED_HEARTHGROVE_SLAB.get(), BlockRegistry.TREATED_NIBBLETWIG_SLAB.get(), BlockRegistry.TREATED_ROTTEN_SLAB.get());
		this.tag(BlockTags.WOODEN_FENCES).add(
			BlockRegistry.WEEDWOOD_FENCE.get(), BlockRegistry.WEEDWOOD_LOG_FENCE.get(), BlockRegistry.RUBBER_TREE_FENCE.get(), BlockRegistry.GIANT_ROOT_FENCE.get(),
			BlockRegistry.HEARTHGROVE_FENCE.get(), BlockRegistry.NIBBLETWIG_FENCE.get(), BlockRegistry.ROTTEN_FENCE.get(),
			BlockRegistry.TREATED_WEEDWOOD_FENCE.get(), BlockRegistry.TREATED_RUBBER_TREE_FENCE.get(), BlockRegistry.TREATED_GIANT_ROOT_FENCE.get(),
			BlockRegistry.TREATED_HEARTHGROVE_FENCE.get(), BlockRegistry.TREATED_NIBBLETWIG_FENCE.get(), BlockRegistry.TREATED_ROTTEN_FENCE.get());
		this.tag(BlockTags.PRESSURE_PLATES).add(BlockRegistry.SYRMORITE_PRESSURE_PLATE.get());
		this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(BlockRegistry.WEEDWOOD_PRESSURE_PLATE.get());
		this.tag(BlockTags.STONE_PRESSURE_PLATES).add(BlockRegistry.BETWEENSTONE_PRESSURE_PLATE.get());
		this.tag(BlockTags.WOODEN_TRAPDOORS).add(
			BlockRegistry.WEEDWOOD_TRAPDOOR.get(), BlockRegistry.RUBBER_TREE_TRAPDOOR.get(), BlockRegistry.GIANT_ROOT_TRAPDOOR.get(),
			BlockRegistry.HEARTHGROVE_TRAPDOOR.get(), BlockRegistry.NIBBLETWIG_TRAPDOOR.get(), BlockRegistry.ROTTEN_TRAPDOOR.get(),
			BlockRegistry.TREATED_WEEDWOOD_TRAPDOOR.get(), BlockRegistry.TREATED_RUBBER_TREE_TRAPDOOR.get(), BlockRegistry.TREATED_GIANT_ROOT_TRAPDOOR.get(),
			BlockRegistry.TREATED_HEARTHGROVE_TRAPDOOR.get(), BlockRegistry.TREATED_NIBBLETWIG_TRAPDOOR.get(), BlockRegistry.TREATED_ROTTEN_TRAPDOOR.get());
		this.tag(BlockTags.DOORS).add(BlockRegistry.SYRMORITE_DOOR.get(), BlockRegistry.SCABYST_DOOR.get());
		this.tag(BlockTags.SAPLINGS).add(BlockRegistry.WEEDWOOD_SAPLING.get(), BlockRegistry.RUBBER_SAPLING.get(),
			BlockRegistry.HEARTHGROVE_SAPLING.get(), BlockRegistry.NIBBLETWIG_SAPLING.get(), BlockRegistry.SPIRIT_TREE_SAPLING.get());
		this.tag(BlockTags.LOGS_THAT_BURN).add(BlockRegistry.WEEDWOOD_LOG.get(), BlockRegistry.WEEDWOOD_BARK.get(),
			BlockRegistry.HEARTHGROVE_LOG.get(), BlockRegistry.HEARTHGROVE_BARK.get(),
			BlockRegistry.TARRED_HEARTHGROVE_LOG.get(), BlockRegistry.TARRED_HEARTHGROVE_BARK.get(),
			BlockRegistry.SAP_LOG.get(), BlockRegistry.SAP_BARK.get(),
			BlockRegistry.NIBBLETWIG_LOG.get(), BlockRegistry.NIBBLETWIG_BARK.get(), BlockRegistry.RUBBER_LOG.get(),
			BlockRegistry.PORTAL_LOG.get(), BlockRegistry.ROTTEN_BARK.get(), BlockRegistry.SPIRIT_TREE_LOG.get(), BlockRegistry.SPIRIT_TREE_BARK.get());
		this.tag(BlockTags.STAIRS).add(BlockRegistry.CRAGROCK_STAIRS.get(), BlockRegistry.PITSTONE_STAIRS.get(), BlockRegistry.BETWEENSTONE_STAIRS.get(),
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS.get(), BlockRegistry.MUD_BRICK_STAIRS.get(), BlockRegistry.CRAGROCK_BRICK_STAIRS.get(),
			BlockRegistry.LIMESTONE_BRICK_STAIRS.get(), BlockRegistry.PITSTONE_BRICK_STAIRS.get(), BlockRegistry.LIMESTONE_STAIRS.get(),
			BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS.get(), BlockRegistry.SMOOTH_CRAGROCK_STAIRS.get(), BlockRegistry.POLISHED_LIMESTONE_STAIRS.get(),
			BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS.get(), BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS.get(),
			BlockRegistry.SCABYST_BRICK_STAIRS.get(), BlockRegistry.SMOOTH_PITSTONE_STAIRS.get(), BlockRegistry.SOLID_TAR_STAIRS.get(), BlockRegistry.TEMPLE_BRICK_STAIRS.get(),
			BlockRegistry.MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_1.get(), BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_2.get(), BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_3.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4.get(), BlockRegistry.THATCH_ROOF.get(), BlockRegistry.MUD_BRICK_SHINGLE_ROOF.get(), BlockRegistry.COMPACTED_MUD_SLOPE.get());
		this.tag(BlockTags.SLABS).add(BlockRegistry.CRAGROCK_SLAB.get(), BlockRegistry.PITSTONE_SLAB.get(), BlockRegistry.BETWEENSTONE_SLAB.get(),
			BlockRegistry.BETWEENSTONE_BRICK_SLAB.get(), BlockRegistry.MUD_BRICK_SLAB.get(), BlockRegistry.CRAGROCK_BRICK_SLAB.get(),
			BlockRegistry.LIMESTONE_BRICK_SLAB.get(), BlockRegistry.PITSTONE_BRICK_SLAB.get(), BlockRegistry.LIMESTONE_SLAB.get(),
			BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.get(), BlockRegistry.SMOOTH_CRAGROCK_SLAB.get(), BlockRegistry.POLISHED_LIMESTONE_SLAB.get(),
			BlockRegistry.MOSSY_BETWEENSTONE_BRICK_SLAB.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB.get(), BlockRegistry.CRACKED_BETWEENSTONE_BRICK_SLAB.get(),
			BlockRegistry.SCABYST_BRICK_SLAB.get(), BlockRegistry.SMOOTH_PITSTONE_SLAB.get(), BlockRegistry.SOLID_TAR_SLAB.get(), BlockRegistry.TEMPLE_BRICK_SLAB.get(),
			BlockRegistry.MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_SLAB_1.get(), BlockRegistry.SLUDGY_MUD_BRICK_SLAB_2.get(), BlockRegistry.SLUDGY_MUD_BRICK_SLAB_3.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_SLAB_4.get(), BlockRegistry.THATCH_SLAB.get(), BlockRegistry.COMPACTED_MUD_SLAB.get());
		this.tag(BlockTags.WALLS).add(BlockRegistry.CRAGROCK_WALL.get(), BlockRegistry.PITSTONE_WALL.get(), BlockRegistry.BETWEENSTONE_WALL.get(),
			BlockRegistry.BETWEENSTONE_BRICK_WALL.get(), BlockRegistry.MUD_BRICK_WALL.get(), BlockRegistry.CRAGROCK_BRICK_WALL.get(),
			BlockRegistry.LIMESTONE_BRICK_WALL.get(), BlockRegistry.PITSTONE_BRICK_WALL.get(), BlockRegistry.LIMESTONE_WALL.get(),
			BlockRegistry.SMOOTH_BETWEENSTONE_WALL.get(), BlockRegistry.SMOOTH_CRAGROCK_WALL.get(), BlockRegistry.POLISHED_LIMESTONE_WALL.get(),
			BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_WALL.get(), BlockRegistry.CRACKED_BETWEENSTONE_BRICK_WALL.get(),
			BlockRegistry.SCABYST_BRICK_WALL.get(), BlockRegistry.SMOOTH_PITSTONE_WALL.get(), BlockRegistry.SOLID_TAR_WALL.get(),
			BlockRegistry.TEMPLE_BRICK_WALL.get(), BlockRegistry.MUD_BRICK_SHINGLE_WALL.get());
		this.tag(BlockTags.LEAVES).add(BlockRegistry.WEEDWOOD_LEAVES.get(), BlockRegistry.RUBBER_TREE_LEAVES.get(), BlockRegistry.HEARTHGROVE_LEAVES.get(),
			BlockRegistry.SAP_LEAVES.get(), BlockRegistry.NIBBLETWIG_LEAVES.get(), BlockRegistry.TOP_SPIRIT_TREE_LEAVES.get(),
			BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES.get(), BlockRegistry.BOTTOM_SPIRIT_TREE_LEAVES.get());
		this.tag(BlockTags.TRAPDOORS).add(BlockRegistry.SYRMORITE_TRAPDOOR.get(), BlockRegistry.SCABYST_TRAPDOOR.get());
		//TODO flower tags? look into repercussions of using these
		this.tag(BlockTags.BEDS).add(BlockRegistry.MOSS_BED.get());
		this.tag(BlockTags.DIRT).add(BlockRegistry.SLUDGY_DIRT.get(), BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.COARSE_SWAMP_DIRT.get(), BlockRegistry.SLIMY_DIRT.get(),
			BlockRegistry.PURIFIED_SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_GRASS.get(), BlockRegistry.SLIMY_GRASS.get(), BlockRegistry.MUD.get());
		this.tag(BlockTags.ICE).add(BlockRegistry.BLACK_ICE.get());
		this.tag(BlockTags.STANDING_SIGNS).add(BlockRegistry.WEEDWOOD_SIGN.get());
		this.tag(BlockTags.WALL_SIGNS).add(BlockRegistry.WEEDWOOD_WALL_SIGN.get());
		this.tag(BlockTags.DRAGON_IMMUNE).add(BlockRegistry.BETWEENLANDS_BEDROCK.get(), BlockRegistry.MOB_SPAWNER.get(), BlockRegistry.ITEM_CAGE.get(), BlockRegistry.DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.CRAWLER_DUNGEON_DOOR_RUNES.get(), BlockRegistry.MIMIC_DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.SPIKE_TRAP.get(), BlockRegistry.MUD_BRICK_SPIKE_TRAP.get(), BlockRegistry.MUD_TILES_SPIKE_TRAP.get(),
			BlockRegistry.DRUID_ALTAR.get(),
			BlockRegistry.BEAM_ORIGIN.get(), BlockRegistry.BEAM_RELAY.get(),
			BlockRegistry.DECAY_PIT_CONTROL.get(), BlockRegistry.DECAY_PIT_GROUND_CHAIN.get(),
			BlockRegistry.DECAY_PIT_HANGING_CHAIN.get());
		this.tag(BlockTags.WITHER_IMMUNE).add(BlockRegistry.BETWEENLANDS_BEDROCK.get(), BlockRegistry.MOB_SPAWNER.get(), BlockRegistry.ITEM_CAGE.get(), BlockRegistry.DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.CRAWLER_DUNGEON_DOOR_RUNES.get(), BlockRegistry.MIMIC_DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.SPIKE_TRAP.get(), BlockRegistry.MUD_BRICK_SPIKE_TRAP.get(), BlockRegistry.MUD_TILES_SPIKE_TRAP.get(),
			BlockRegistry.DRUID_ALTAR.get(),
			BlockRegistry.BEAM_ORIGIN.get(), BlockRegistry.BEAM_RELAY.get(),
			BlockRegistry.DECAY_PIT_CONTROL.get(), BlockRegistry.DECAY_PIT_GROUND_CHAIN.get(),
			BlockRegistry.DECAY_PIT_HANGING_CHAIN.get());
		this.tag(BlockTags.PORTALS).add(BlockRegistry.PORTAL.get());
		this.tag(BlockTags.BEACON_BASE_BLOCKS).add(BlockRegistry.SYRMORITE_BLOCK.get(), BlockRegistry.VALONITE_BLOCK.get(),
			BlockRegistry.OCTINE_BLOCK.get(), BlockRegistry.PEARL_BLOCK.get(), BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK.get(),
			BlockRegistry.GREEN_MIDDLE_GEM_BLOCK.get(), BlockRegistry.AQUA_MIDDLE_GEM_BLOCK.get(),
			BlockRegistry.ANCIENT_REMNANT_BLOCK.get(), BlockRegistry.SCABYST_BLOCK.get());
		this.tag(BlockTags.WALL_POST_OVERRIDE).add(BlockRegistry.DAMP_TORCH.get(), BlockRegistry.SULFUR_TORCH.get(), BlockRegistry.EXTINGUISHED_SULFUR_TORCH.get());
		this.tag(BlockTags.CLIMBABLE).add(BlockRegistry.WEEDWOOD_LADDER.get(), BlockRegistry.CLIMBABLE_MUD_BRICKS.get(), BlockRegistry.HANGER.get(), BlockRegistry.SEEDED_HANGER.get());
		this.tag(BlockTags.FALL_DAMAGE_RESETTING).add(BlockRegistry.NETTLE.get(), BlockRegistry.FLOWERED_NETTLE.get());
		this.tag(BlockTags.STRIDER_WARM_BLOCKS).add(BlockRegistry.OCTINE_ORE.get(), BlockRegistry.OCTINE_BLOCK.get());
		this.tag(BlockTags.GUARDED_BY_PIGLINS).add(
			BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get(),
			BlockRegistry.MUD_LOOT_POT_1.get(), BlockRegistry.MUD_LOOT_POT_2.get(), BlockRegistry.MUD_LOOT_POT_3.get(),
			BlockRegistry.TAR_LOOT_POT_1.get(), BlockRegistry.TAR_LOOT_POT_2.get(), BlockRegistry.TAR_LOOT_POT_3.get(),
			BlockRegistry.LOOT_URN_1.get(), BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get()); //TODO add weedwood chest
		this.tag(BlockTags.FENCE_GATES).add(
			BlockRegistry.WEEDWOOD_FENCE_GATE.get(), BlockRegistry.WEEDWOOD_LOG_FENCE_GATE.get(), BlockRegistry.RUBBER_TREE_FENCE_GATE.get(), BlockRegistry.GIANT_ROOT_FENCE_GATE.get(),
			BlockRegistry.HEARTHGROVE_FENCE_GATE.get(), BlockRegistry.NIBBLETWIG_FENCE_GATE.get(), BlockRegistry.ROTTEN_FENCE_GATE.get(),
			BlockRegistry.TREATED_WEEDWOOD_FENCE_GATE.get(), BlockRegistry.TREATED_RUBBER_TREE_FENCE_GATE.get(), BlockRegistry.TREATED_GIANT_ROOT_FENCE_GATE.get(),
			BlockRegistry.TREATED_HEARTHGROVE_FENCE_GATE.get(), BlockRegistry.TREATED_NIBBLETWIG_FENCE_GATE.get(), BlockRegistry.TREATED_ROTTEN_FENCE_GATE.get());
		this.tag(BlockTags.INFINIBURN_OVERWORLD).add(BlockRegistry.PEAT.get(), BlockRegistry.SMOULDERING_PEAT.get(), BlockRegistry.BRAZIER.get());
		this.tag(BlockTags.COMBINATION_STEP_SOUND_BLOCKS).addTag(REED_MATS).add(BlockRegistry.SLUDGE.get(), BlockRegistry.SNOW.get(), BlockRegistry.PUDDLE.get(), BlockRegistry.CHIP_PATH.get(), BlockRegistry.REED_MAT.get());
		this.tag(BlockTags.OCCLUDES_VIBRATION_SIGNALS).addTag(SAMITE).addTag(SAMITE_CANVAS_PANELS);
		this.tag(BlockTags.DAMPENS_VIBRATIONS).addTag(SAMITE);
		this.tag(BlockTags.SNOW).add(BlockRegistry.SNOW.get());
		this.tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.WEEDWOOD.get(), BlockRegistry.ITEM_CAGE.get(), BlockRegistry.ITEM_SHELF.get(), BlockRegistry.WEEDWOOD_LADDER.get(),
			BlockRegistry.WEEDWOOD_LEVER.get(), BlockRegistry.WOODEN_SUPPORT_BEAM_1.get(), BlockRegistry.WOODEN_SUPPORT_BEAM_2.get(), BlockRegistry.WOODEN_SUPPORT_BEAM_3.get(),
			BlockRegistry.CARVED_ROTTEN_BARK_1.get(), BlockRegistry.CARVED_ROTTEN_BARK_2.get(), BlockRegistry.CARVED_ROTTEN_BARK_3.get(), BlockRegistry.CARVED_ROTTEN_BARK_4.get(),
			BlockRegistry.CARVED_ROTTEN_BARK_5.get(), BlockRegistry.CARVED_ROTTEN_BARK_6.get(), BlockRegistry.CARVED_ROTTEN_BARK_7.get(), BlockRegistry.CARVED_ROTTEN_BARK_8.get(),
			BlockRegistry.CARVED_ROTTEN_BARK_9.get(), BlockRegistry.CARVED_ROTTEN_BARK_10.get(), BlockRegistry.CARVED_ROTTEN_BARK_11.get(), BlockRegistry.CARVED_ROTTEN_BARK_12.get(),
			BlockRegistry.CARVED_ROTTEN_BARK_13.get(), BlockRegistry.CARVED_ROTTEN_BARK_14.get(), BlockRegistry.CARVED_ROTTEN_BARK_15.get(), BlockRegistry.CARVED_ROTTEN_BARK_16.get(),
			BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get(), BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get(), BlockRegistry.SHELF_FUNGUS.get(), BlockRegistry.ROOT.get(),
			BlockRegistry.GIANT_ROOT.get(), BlockRegistry.HOLLOW_LOG.get(), BlockRegistry.PORTAL_FRAME_BOTTOM_LEFT.get(), BlockRegistry.PORTAL_FRAME_BOTTOM.get(),
			BlockRegistry.PORTAL_FRAME_BOTTOM_RIGHT.get(), BlockRegistry.PORTAL_FRAME_LEFT.get(), BlockRegistry.PORTAL_FRAME_RIGHT.get(), BlockRegistry.PORTAL_FRAME_TOP_LEFT.get(),
			BlockRegistry.PORTAL_FRAME_TOP.get(), BlockRegistry.PORTAL_FRAME_TOP_RIGHT.get(), BlockRegistry.WEEDWOOD_CRAFTING_TABLE.get(), BlockRegistry.COMPOST_BIN.get(),
			BlockRegistry.WEEDWOOD_JUKEBOX.get(), /*TODO chest*/ BlockRegistry.GECKO_CAGE.get(), BlockRegistry.WEEDWOOD_BARREL.get(), BlockRegistry.WALKWAY.get(),
			BlockRegistry.CHIP_PATH.get(), BlockRegistry.REPELLER.get(), BlockRegistry.ROOTMAN_SIMULACRUM_1.get(), BlockRegistry.ROOTMAN_SIMULACRUM_2.get(),
			BlockRegistry.ROOTMAN_SIMULACRUM_3.get(), BlockRegistry.PAPER_LANTERN_1.get(), BlockRegistry.PAPER_LANTERN_2.get(), BlockRegistry.PAPER_LANTERN_3.get(),
			BlockRegistry.FISHING_TACKLE_BOX.get(), BlockRegistry.SMOKING_RACK.get(), BlockRegistry.FISH_TRIMMING_TABLE.get(), BlockRegistry.CRAB_POT.get(),
			BlockRegistry.CRAB_POT_FILTER.get(), BlockRegistry.WATER_FILTER.get(), BlockRegistry.MOTH_HOUSE.get(), BlockRegistry.MOSS_BED.get(), BlockRegistry.PURIFIER.get(),
			BlockRegistry.WEEDWOOD_BUSH.get(), BlockRegistry.BONE_NESTING_BLOCK.get(), BlockRegistry.STICK_NESTING_BLOCK.get(), BlockRegistry.PHEROMONE_INFUSED_WEEDWOOD_BUSH.get(),
			BlockRegistry.MOTH_INFESTED_WEEDWOOD_BUSH.get(), BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH.get(), BlockRegistry.SILK_COCOONED_WEEDWOOD_BUSH.get(),
			BlockRegistry.DECAY_INFESTED_WEEDWOOD_BUSH.get());
		this.tag(BlockTags.MINEABLE_WITH_HOE).add(BlockRegistry.WEEDWOOD_LEAVES.get(), BlockRegistry.RUBBER_TREE_LEAVES.get(), BlockRegistry.HEARTHGROVE_LEAVES.get(),
			BlockRegistry.SAP_LEAVES.get(), BlockRegistry.NIBBLETWIG_LEAVES.get(), BlockRegistry.TOP_SPIRIT_TREE_LEAVES.get(), BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES.get(),
			BlockRegistry.BOTTOM_SPIRIT_TREE_LEAVES.get(), BlockRegistry.COMPOST_BLOCK.get(), BlockRegistry.THATCH.get(), BlockRegistry.THATCH_SLAB.get(), BlockRegistry.THATCH_ROOF.get(),
			BlockRegistry.ALGAE.get(), BlockRegistry.POISON_IVY.get(), BlockRegistry.MOSS.get(), BlockRegistry.DEAD_MOSS.get(), BlockRegistry.LICHEN.get(), BlockRegistry.DEAD_LICHEN.get());
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.DRUID_STONE_1.get(), BlockRegistry.DRUID_STONE_2.get(), BlockRegistry.DRUID_STONE_3.get(),
			BlockRegistry.DRUID_STONE_4.get(), BlockRegistry.DRUID_STONE_5.get(), BlockRegistry.DRUID_STONE_6.get(), BlockRegistry.BETWEENSTONE.get(),
			BlockRegistry.CORRUPT_BETWEENSTONE.get(), BlockRegistry.CRAGROCK.get(), BlockRegistry.MOSSY_CRAGROCK_TOP.get(), BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get(),
			BlockRegistry.PITSTONE.get(), BlockRegistry.LIMESTONE.get(), BlockRegistry.OCTINE_ORE.get(), BlockRegistry.VALONITE_ORE.get(), BlockRegistry.SULFUR_ORE.get(),
			BlockRegistry.SLIMY_BONE_ORE.get(), BlockRegistry.SCABYST_ORE.get(), BlockRegistry.SYRMORITE_ORE.get(), BlockRegistry.LIFE_CRYSTAL_STALACTITE.get(),
			BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE.get(), BlockRegistry.STALACTITE.get(), BlockRegistry.SOLID_TAR.get(), BlockRegistry.PEARL_BLOCK.get(),
			BlockRegistry.ANCIENT_REMNANT_BLOCK.get(), BlockRegistry.ANGRY_BETWEENSTONE.get(), BlockRegistry.BETWEENSTONE_BRICKS.get(), BlockRegistry.MIRAGE_BETWEENSTONE_BRICKS.get(),
			BlockRegistry.BETWEENSTONE_TILES.get(), BlockRegistry.CHISELED_BETWEENSTONE.get(), BlockRegistry.CHISELED_CRAGROCK.get(), BlockRegistry.CRACKED_CHISELED_CRAGROCK.get(),
			BlockRegistry.MOSSY_CHISELED_CRAGROCK.get(), BlockRegistry.CHISELED_LIMESTONE.get(), BlockRegistry.CHISELED_PITSTONE.get(), BlockRegistry.CHISELED_SCABYST_1.get(),
			BlockRegistry.CHISELED_SCABYST_2.get(), BlockRegistry.CHISELED_SCABYST_3.get(), BlockRegistry.HORIZONTAL_SCABYST_PITSTONE.get(), BlockRegistry.DOTTED_SCABYST_PITSTONE.get(),
			BlockRegistry.SCABYST_BRICKS.get(), BlockRegistry.CRACKED_BETWEENSTONE_BRICKS.get(), BlockRegistry.CRACKED_BETWEENSTONE_TILES.get(), BlockRegistry.CRACKED_LIMESTONE_BRICKS.get(),
			BlockRegistry.CRAGROCK_BRICKS.get(), BlockRegistry.CRACKED_CRAGROCK_BRICKS.get(), BlockRegistry.MOSSY_CRAGROCK_BRICKS.get(), BlockRegistry.CRAGROCK_TILES.get(),
			BlockRegistry.CRACKED_CRAGROCK_TILES.get(), BlockRegistry.MOSSY_CRAGROCK_TILES.get(), BlockRegistry.GLOWING_BETWEENSTONE_TILE.get(), BlockRegistry.INACTIVE_GLOWING_SMOOTH_CRAGROCK.get(),
			BlockRegistry.GLOWING_SMOOTH_CRAGROCK.get(), BlockRegistry.LIMESTONE_BRICKS.get(), BlockRegistry.LIMESTONE_TILES.get(), BlockRegistry.MOSSY_BETWEENSTONE_BRICKS.get(),
			BlockRegistry.MOSSY_BETWEENSTONE_TILES.get(), BlockRegistry.MOSSY_LIMESTONE_BRICKS.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE.get(), BlockRegistry.MUD_BRICKS.get(),
			BlockRegistry.MUD_BRICK_SHINGLES.get(), BlockRegistry.PITSTONE_BRICKS.get(), BlockRegistry.PITSTONE_TILES.get(), BlockRegistry.POLISHED_LIMESTONE.get(), BlockRegistry.SMOOTH_BETWEENSTONE.get(),
			BlockRegistry.SMOOTH_CRAGROCK.get(), BlockRegistry.OCTINE_BLOCK.get(), BlockRegistry.SYRMORITE_BLOCK.get(), BlockRegistry.VALONITE_BLOCK.get(), BlockRegistry.SCABYST_BLOCK.get(),
			BlockRegistry.WEAK_BETWEENSTONE_TILES.get(), BlockRegistry.WEAK_POLISHED_LIMESTONE.get(), BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES.get(), BlockRegistry.WEAK_SMOOTH_CRAGROCK.get(),
			BlockRegistry.GREEN_DENTROTHYST.get(), BlockRegistry.ORANGE_DENTROTHYST.get(), BlockRegistry.LOOT_POT_1.get(), BlockRegistry.LOOT_POT_2.get(), BlockRegistry.LOOT_POT_3.get(),
			BlockRegistry.MOB_SPAWNER.get(), BlockRegistry.TEMPLE_PILLAR.get(), BlockRegistry.BETWEENSTONE_PILLAR.get(), BlockRegistry.PITSTONE_PILLAR.get(), BlockRegistry.LIMESTONE_PILLAR.get(),
			BlockRegistry.CRAGROCK_PILLAR.get(), BlockRegistry.TAR_LOOT_POT_1.get(), BlockRegistry.TAR_LOOT_POT_2.get(), BlockRegistry.TAR_LOOT_POT_3.get(), BlockRegistry.CRAGROCK_STAIRS.get(),
			BlockRegistry.PITSTONE_STAIRS.get(), BlockRegistry.BETWEENSTONE_STAIRS.get(), BlockRegistry.BETWEENSTONE_BRICK_STAIRS.get(), BlockRegistry.MUD_BRICK_STAIRS.get(),
			BlockRegistry.CRAGROCK_BRICK_STAIRS.get(), BlockRegistry.LIMESTONE_BRICK_STAIRS.get(), BlockRegistry.PITSTONE_BRICK_STAIRS.get(), BlockRegistry.LIMESTONE_STAIRS.get(),
			BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS.get(), BlockRegistry.SMOOTH_CRAGROCK_STAIRS.get(), BlockRegistry.POLISHED_LIMESTONE_STAIRS.get(),
			BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS.get(), BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS.get(),
			BlockRegistry.SCABYST_BRICK_STAIRS.get(), BlockRegistry.SULFUR_BLOCK.get(), BlockRegistry.TEMPLE_BRICKS.get(), BlockRegistry.SMOOTH_PITSTONE.get(),
			BlockRegistry.MIRE_CORAL_BLOCK.get(), BlockRegistry.DEEP_WATER_CORAL_BLOCK.get(), BlockRegistry.SLIMY_BONE_BLOCK.get(), BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK.get(),
			BlockRegistry.GREEN_MIDDLE_GEM_BLOCK.get(), BlockRegistry.AQUA_MIDDLE_GEM_BLOCK.get(), BlockRegistry.SMOOTH_PITSTONE_STAIRS.get(), BlockRegistry.SOLID_TAR_STAIRS.get(),
			BlockRegistry.TEMPLE_BRICK_STAIRS.get(), BlockRegistry.SPIKE_TRAP.get(), BlockRegistry.POSSESSED_BLOCK.get(), BlockRegistry.CRAGROCK_SLAB.get(), BlockRegistry.PITSTONE_SLAB.get(),
			BlockRegistry.BETWEENSTONE_SLAB.get(), BlockRegistry.SMOOTH_PITSTONE_SLAB.get(), BlockRegistry.SOLID_TAR_SLAB.get(), BlockRegistry.TEMPLE_BRICK_SLAB.get(),
			BlockRegistry.BETWEENSTONE_BRICK_SLAB.get(), BlockRegistry.MUD_BRICK_SLAB.get(), BlockRegistry.CRAGROCK_BRICK_SLAB.get(), BlockRegistry.LIMESTONE_BRICK_SLAB.get(),
			BlockRegistry.LIMESTONE_SLAB.get(), BlockRegistry.SMOOTH_BETWEENSTONE_SLAB.get(), BlockRegistry.SMOOTH_CRAGROCK_SLAB.get(), BlockRegistry.POLISHED_LIMESTONE_SLAB.get(),
			BlockRegistry.PITSTONE_BRICK_SLAB.get(), BlockRegistry.MOSSY_BETWEENSTONE_BRICK_SLAB.get(), BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB.get(),
			BlockRegistry.CRACKED_BETWEENSTONE_BRICK_SLAB.get(), BlockRegistry.MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB.get(),
			BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS.get(), BlockRegistry.SCABYST_BRICK_SLAB.get(),
			BlockRegistry.MUD_LOOT_POT_1.get(), BlockRegistry.MUD_LOOT_POT_2.get(), BlockRegistry.MUD_LOOT_POT_3.get(), BlockRegistry.WORM_PILLAR.get(),
			BlockRegistry.SLUDGY_WORM_PILLAR_1.get(), BlockRegistry.SLUDGY_WORM_PILLAR_2.get(), BlockRegistry.SLUDGY_WORM_PILLAR_3.get(), BlockRegistry.SLUDGY_WORM_PILLAR_4.get(),
			BlockRegistry.SLUDGY_WORM_PILLAR_5.get(), BlockRegistry.WORM_PILLAR_TOP.get(), BlockRegistry.SLUDGY_WORM_PILLAR_TOP_1.get(), BlockRegistry.SLUDGY_WORM_PILLAR_TOP_2.get(),
			BlockRegistry.SLUDGY_WORM_PILLAR_TOP_3.get(), BlockRegistry.SLUDGY_WORM_PILLAR_TOP_4.get(), BlockRegistry.SLUDGY_WORM_PILLAR_TOP_5.get(), BlockRegistry.COMPACTED_MUD.get(),
			BlockRegistry.MUD_TILES.get(), BlockRegistry.DECAYED_MUD_TILES.get(), BlockRegistry.CRACKED_MUD_TILES.get(), BlockRegistry.CRACKED_DECAYED_MUD_TILES.get(),
			BlockRegistry.PUFFSHROOM.get(), BlockRegistry.CARVED_MUD_BRICKS.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_1.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_2.get(),
			BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_3.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_4.get(), BlockRegistry.CARVED_MUD_BRICK_EDGE.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_1.get(),
			BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_2.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_3.get(), BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_4.get(),
			BlockRegistry.SLUDGY_MUD_BRICKS_1.get(), BlockRegistry.SLUDGY_MUD_BRICKS_2.get(), BlockRegistry.SLUDGY_MUD_BRICKS_3.get(), BlockRegistry.SLUDGY_MUD_BRICKS_4.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_1.get(), BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_2.get(), BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_3.get(), BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4.get(),
			BlockRegistry.SLUDGY_MUD_BRICK_SLAB_1.get(), BlockRegistry.SLUDGY_MUD_BRICK_SLAB_2.get(), BlockRegistry.SLUDGY_MUD_BRICK_SLAB_3.get(), BlockRegistry.SLUDGY_MUD_BRICK_SLAB_4.get(),
			BlockRegistry.BEAM_TUBE.get(), BlockRegistry.BEAM_RELAY.get(), BlockRegistry.BEAM_LENS_SUPPORT.get(), BlockRegistry.MUD_BRICK_ALCOVE.get(), BlockRegistry.LOOT_URN_1.get(),
			BlockRegistry.LOOT_URN_2.get(), BlockRegistry.LOOT_URN_3.get(), BlockRegistry.DUNGEON_DOOR_COMBINATION.get(), BlockRegistry.CLIMBABLE_MUD_BRICKS.get(), BlockRegistry.BROKEN_MUD_TILES.get(),
			BlockRegistry.DUNGEON_WALL_CANDLE.get(), BlockRegistry.MUD_BRICK_SPIKE_TRAP.get(), BlockRegistry.MUD_TILES_SPIKE_TRAP.get(), BlockRegistry.COMPACTED_MUD_SLOPE.get(),
			BlockRegistry.COMPACTED_MUD_SLAB.get(), BlockRegistry.COMPACTED_MUD_MIRAGE.get(), BlockRegistry.BRAZIER.get(), BlockRegistry.SULFUR_FURNACE.get(), BlockRegistry.DUAL_SULFUR_FURNACE.get(),
			BlockRegistry.SYRMORITE_HOPPER.get(), BlockRegistry.MUD_FLOWER_POT.get(), BlockRegistry.MUD_FLOWER_POT_CANDLE.get(), BlockRegistry.INFUSER.get(), BlockRegistry.MORTAR.get(),
			BlockRegistry.CENSER.get(), BlockRegistry.SYRMORITE_BARREL.get(), BlockRegistry.ANIMATOR.get(), BlockRegistry.ALEMBIC.get(), BlockRegistry.MUD_BRICK_SHINGLE_ROOF.get(),
			BlockRegistry.WAYSTONE.get(), BlockRegistry.DEEPMAN_SIMULACRUM_1.get(), BlockRegistry.DEEPMAN_SIMULACRUM_2.get(), BlockRegistry.DEEPMAN_SIMULACRUM_3.get(),
			BlockRegistry.LAKE_CAVERN_SIMULACRUM_1.get(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_2.get(), BlockRegistry.LAKE_CAVERN_SIMULACRUM_3.get(), BlockRegistry.OFFERING_TABLE.get(),
			BlockRegistry.WIND_CHIME.get(), BlockRegistry.SILT_GLASS_JAR.get(), BlockRegistry.LYESTONE.get(), BlockRegistry.STEEPING_POT.get(), BlockRegistry.GRUB_HUB.get(),
			BlockRegistry.FILTERED_SILT_GLASS_JAR.get(), BlockRegistry.SYRMORITE_PRESSURE_PLATE.get(), BlockRegistry.SYRMORITE_DOOR.get(), BlockRegistry.SYRMORITE_TRAPDOOR.get(),
			BlockRegistry.BETWEENSTONE_BUTTON.get(), BlockRegistry.BETWEENSTONE_PRESSURE_PLATE.get(), BlockRegistry.SCABYST_DOOR.get(), BlockRegistry.SCABYST_TRAPDOOR.get(), BlockRegistry.BLACK_ICE.get())
			.addTag(MUD_BRICK_SHINGLES);
		this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(BlockRegistry.MUD.get(), BlockRegistry.PEAT.get(), BlockRegistry.SMOULDERING_PEAT.get(), BlockRegistry.SLUDGY_DIRT.get(),
			BlockRegistry.SPREADING_SLUDGY_DIRT.get(), BlockRegistry.SLIMY_DIRT.get(), BlockRegistry.SLIMY_GRASS.get(), BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.COARSE_SWAMP_DIRT.get(),
			BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.AQUA_MIDDLE_GEM_ORE.get(), BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.get(), BlockRegistry.GREEN_MIDDLE_GEM_ORE.get(), BlockRegistry.SILT.get(),
			BlockRegistry.FILTERED_SILT.get(), BlockRegistry.DEAD_GRASS.get(), BlockRegistry.COMPOST_BLOCK.get(), BlockRegistry.PURIFIED_SWAMP_DIRT.get(), BlockRegistry.PURIFIED_DUG_SWAMP_DIRT.get(),
			BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get(), BlockRegistry.DUG_SWAMP_DIRT.get(), BlockRegistry.DUG_SWAMP_GRASS.get(), BlockRegistry.SNOW.get(), BlockRegistry.SLUDGE.get());
		this.tag(BlockTags.NEEDS_IRON_TOOL).add(BlockRegistry.VALONITE_ORE.get(), BlockRegistry.SCABYST_ORE.get(), BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE.get(),
			BlockRegistry.SCABYST_BLOCK.get(), BlockRegistry.VALONITE_BLOCK.get());
		this.tag(BlockTags.NEEDS_STONE_TOOL).add(BlockRegistry.SYRMORITE_ORE.get(), BlockRegistry.SYRMORITE_BLOCK.get(), BlockRegistry.OCTINE_ORE.get(), BlockRegistry.OCTINE_BLOCK.get(),
			BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.get(), BlockRegistry.GREEN_MIDDLE_GEM_ORE.get(), BlockRegistry.AQUA_MIDDLE_GEM_ORE.get(), BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK.get(),
			BlockRegistry.GREEN_MIDDLE_GEM_BLOCK.get(), BlockRegistry.AQUA_MIDDLE_GEM_BLOCK.get());
		this.tag(BlockTags.DEAD_BUSH_MAY_PLACE_ON).add(BlockRegistry.MUD.get(), BlockRegistry.PEAT.get());

		this.tag(Tags.Blocks.DYED).addTags(DYED_DULL_LAVENDER, DYED_MAROON, DYED_SHADOW_GREEN, DYED_CAMELOT_MAGENTA, DYED_SAFFRON,
			DYED_CARIBBEAN_GREEN, DYED_VIVID_TANGERINE, DYED_CHAMPAGNE, DYED_RAISIN_BLACK, DYED_SUSHI_GREEN, DYED_ELM_CYAN,
			DYED_CADMIUM_GREEN, DYED_LAVENDER_BLUE, DYED_BROWN_RUST, DYED_MIDNIGHT_PURPLE, DYED_PEWTER_GREY);

		this.tag(DYED_DULL_LAVENDER).add(BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS.get(), BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.DULL_LAVENDER_SAMITE.get(), BlockRegistry.DULL_LAVENDER_SAMITE_CANVAS_PANEL.get(), BlockRegistry.DULL_LAVENDER_REED_MAT.get(),
			BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_MAROON).add(BlockRegistry.MAROON_FILTERED_SILT_GLASS.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.MAROON_SAMITE.get(), BlockRegistry.MAROON_SAMITE_CANVAS_PANEL.get(), BlockRegistry.MAROON_REED_MAT.get(),
			BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_SHADOW_GREEN).add(BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS.get(), BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.SHADOW_GREEN_SAMITE.get(), BlockRegistry.SHADOW_GREEN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.SHADOW_GREEN_REED_MAT.get(),
			BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_CAMELOT_MAGENTA).add(BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.CAMELOT_MAGENTA_SAMITE.get(), BlockRegistry.CAMELOT_MAGENTA_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CAMELOT_MAGENTA_REED_MAT.get(),
			BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_SAFFRON).add(BlockRegistry.SAFFRON_FILTERED_SILT_GLASS.get(), BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.SAFFRON_SAMITE.get(), BlockRegistry.SAFFRON_SAMITE_CANVAS_PANEL.get(), BlockRegistry.SAFFRON_REED_MAT.get(),
			BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_CARIBBEAN_GREEN).add(BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.CARIBBEAN_GREEN_SAMITE.get(), BlockRegistry.CARIBBEAN_GREEN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CARIBBEAN_GREEN_REED_MAT.get(),
			BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_VIVID_TANGERINE).add(BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS.get(), BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.VIVID_TANGERINE_SAMITE.get(), BlockRegistry.VIVID_TANGERINE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.VIVID_TANGERINE_REED_MAT.get(),
			BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_CHAMPAGNE).add(BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.CHAMPAGNE_SAMITE.get(), BlockRegistry.CHAMPAGNE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CHAMPAGNE_REED_MAT.get(),
			BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_RAISIN_BLACK).add(BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS.get(), BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.RAISIN_BLACK_SAMITE.get(), BlockRegistry.RAISIN_BLACK_SAMITE_CANVAS_PANEL.get(), BlockRegistry.RAISIN_BLACK_REED_MAT.get(),
			BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_SUSHI_GREEN).add(BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.SUSHI_GREEN_SAMITE.get(), BlockRegistry.SUSHI_GREEN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.SUSHI_GREEN_REED_MAT.get(),
			BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_ELM_CYAN).add(BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS.get(), BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.ELM_CYAN_SAMITE.get(), BlockRegistry.ELM_CYAN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.ELM_CYAN_REED_MAT.get(),
			BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_CADMIUM_GREEN).add(BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.CADMIUM_GREEN_SAMITE.get(), BlockRegistry.CADMIUM_GREEN_SAMITE_CANVAS_PANEL.get(), BlockRegistry.CADMIUM_GREEN_REED_MAT.get(),
			BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_LAVENDER_BLUE).add(BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS.get(), BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.LAVENDER_BLUE_SAMITE.get(), BlockRegistry.LAVENDER_BLUE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.LAVENDER_BLUE_REED_MAT.get(),
			BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_BROWN_RUST).add(BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.BROWN_RUST_SAMITE.get(), BlockRegistry.BROWN_RUST_SAMITE_CANVAS_PANEL.get(), BlockRegistry.BROWN_RUST_REED_MAT.get(),
			BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_MIDNIGHT_PURPLE).add(BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS.get(), BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.MIDNIGHT_PURPLE_SAMITE.get(), BlockRegistry.MIDNIGHT_PURPLE_SAMITE_CANVAS_PANEL.get(), BlockRegistry.MIDNIGHT_PURPLE_REED_MAT.get(),
			BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS.get());
		this.tag(DYED_PEWTER_GREY).add(BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES.get(),
			BlockRegistry.PEWTER_GREY_SAMITE.get(), BlockRegistry.PEWTER_GREY_SAMITE_CANVAS_PANEL.get(), BlockRegistry.PEWTER_GREY_REED_MAT.get(),
			BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB.get(), BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS.get());

		this.tag(Tags.Blocks.GLASS_BLOCKS).add(BlockRegistry.POLISHED_GREEN_DENTROTHYST.get(), BlockRegistry.POLISHED_ORANGE_DENTROTHYST.get());
		this.tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS).add(BlockRegistry.SILT_GLASS.get(), BlockRegistry.FILTERED_SILT_GLASS.get());
		this.tag(Tags.Blocks.GLASS_BLOCKS_CHEAP).addTag(FILTERED_SILT_GLASS).add(BlockRegistry.SILT_GLASS.get(), BlockRegistry.FILTERED_SILT_GLASS.get());
		this.tag(Tags.Blocks.GLASS_PANES).add(BlockRegistry.POLISHED_GREEN_DENTROTHYST_PANE.get(), BlockRegistry.POLISHED_ORANGE_DENTROTHYST_PANE.get());
		this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(BlockRegistry.SILT_GLASS_PANE.get(), BlockRegistry.FILTERED_SILT_GLASS_PANE.get());

		this.tag(ORE_BEARING_GROUND_BETWEENSTONE).add(BlockRegistry.BETWEENSTONE.get());
		this.tag(ORE_BEARING_GROUND_MUD).add(BlockRegistry.MUD.get());
		this.tag(ORE_BEARING_GROUND_PITSTONE).add(BlockRegistry.PITSTONE.get());

		this.tag(ORES_IN_GROUND_BETWEENSTONE).add(BlockRegistry.OCTINE_ORE.get(), BlockRegistry.SULFUR_ORE.get(), BlockRegistry.SLIMY_BONE_ORE.get(), BlockRegistry.SYRMORITE_ORE.get());
		this.tag(ORES_IN_GROUND_MUD).add(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.get(), BlockRegistry.GREEN_MIDDLE_GEM_ORE.get(), BlockRegistry.AQUA_MIDDLE_GEM_ORE.get());
		this.tag(ORES_IN_GROUND_PITSTONE).add(BlockRegistry.VALONITE_ORE.get(), BlockRegistry.SCABYST_ORE.get());

		this.tag(Tags.Blocks.ORES).addTags(ORES_OCTINE, ORES_VALONITE, ORES_SULFUR, ORES_SLIMY_BONE, ORES_SCABYST, ORES_SYRMORITE, ORES_CRIMSON_MIDDLE_GEM, ORES_GREEN_MIDDLE_GEM, ORES_AQUA_MIDDLE_GEM);
		this.tag(ORES_OCTINE).add(BlockRegistry.OCTINE_ORE.get());
		this.tag(ORES_VALONITE).add(BlockRegistry.VALONITE_ORE.get());
		this.tag(ORES_SULFUR).add(BlockRegistry.SULFUR_ORE.get());
		this.tag(ORES_SLIMY_BONE).add(BlockRegistry.SLIMY_BONE_ORE.get());
		this.tag(ORES_SCABYST).add(BlockRegistry.SCABYST_ORE.get());
		this.tag(ORES_SYRMORITE).add(BlockRegistry.SYRMORITE_ORE.get());
		this.tag(ORES_CRIMSON_MIDDLE_GEM).add(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.get());
		this.tag(ORES_GREEN_MIDDLE_GEM).add(BlockRegistry.GREEN_MIDDLE_GEM_ORE.get());
		this.tag(ORES_AQUA_MIDDLE_GEM).add(BlockRegistry.AQUA_MIDDLE_GEM_ORE.get());

		this.tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(BlockRegistry.WEEDWOOD_CRAFTING_TABLE.get());
		this.tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(BlockRegistry.SULFUR_FURNACE.get(), BlockRegistry.DUAL_SULFUR_FURNACE.get());

		this.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(BlockRegistry.BETWEENLANDS_BEDROCK.get(),
			BlockRegistry.MOB_SPAWNER.get(), BlockRegistry.ITEM_CAGE.get(), BlockRegistry.DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.CRAWLER_DUNGEON_DOOR_RUNES.get(), BlockRegistry.MIMIC_DUNGEON_DOOR_RUNES.get(),
			BlockRegistry.SPIKE_TRAP.get(), BlockRegistry.MUD_BRICK_SPIKE_TRAP.get(), BlockRegistry.MUD_TILES_SPIKE_TRAP.get(),
			BlockRegistry.DRUID_ALTAR.get(),
			BlockRegistry.BEAM_ORIGIN.get(), BlockRegistry.BEAM_RELAY.get(),
			BlockRegistry.DECAY_PIT_CONTROL.get(), BlockRegistry.DECAY_PIT_GROUND_CHAIN.get(),
			BlockRegistry.DECAY_PIT_HANGING_CHAIN.get());

		//TODO rope tag once reed rope returns
		this.tag(Tags.Blocks.STONES).add(BlockRegistry.BETWEENSTONE.get(), BlockRegistry.CRAGROCK.get(), BlockRegistry.PITSTONE.get());

		this.tag(Tags.Blocks.STORAGE_BLOCKS).addTags(STORAGE_BLOCKS_OCTINE, STORAGE_BLOCKS_VALONITE,
			STORAGE_BLOCKS_SULFUR, STORAGE_BLOCKS_SLIMY_BONE, STORAGE_BLOCKS_SCABYST, STORAGE_BLOCKS_SYRMORITE,
			STORAGE_BLOCKS_CRIMSON_MIDDLE_GEM, STORAGE_BLOCKS_GREEN_MIDDLE_GEM, STORAGE_BLOCKS_AQUA_MIDDLE_GEM,
			STORAGE_BLOCKS_PEARL, STORAGE_BLOCKS_ANCIENT_REMNANT, STORAGE_BLOCKS_RUBBER, STORAGE_BLOCKS_COMPOST);
		this.tag(STORAGE_BLOCKS_OCTINE).add(BlockRegistry.OCTINE_BLOCK.get());
		this.tag(STORAGE_BLOCKS_VALONITE).add(BlockRegistry.VALONITE_BLOCK.get());
		this.tag(STORAGE_BLOCKS_SULFUR).add(BlockRegistry.SULFUR_BLOCK.get());
		this.tag(STORAGE_BLOCKS_SLIMY_BONE).add(BlockRegistry.SLIMY_BONE_BLOCK.get());
		this.tag(STORAGE_BLOCKS_SCABYST).add(BlockRegistry.SCABYST_BLOCK.get());
		this.tag(STORAGE_BLOCKS_SYRMORITE).add(BlockRegistry.SYRMORITE_BLOCK.get());
		this.tag(STORAGE_BLOCKS_CRIMSON_MIDDLE_GEM).add(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK.get());
		this.tag(STORAGE_BLOCKS_GREEN_MIDDLE_GEM).add(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK.get());
		this.tag(STORAGE_BLOCKS_AQUA_MIDDLE_GEM).add(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK.get());
		this.tag(STORAGE_BLOCKS_PEARL).add(BlockRegistry.PEARL_BLOCK.get());
		this.tag(STORAGE_BLOCKS_ANCIENT_REMNANT).add(BlockRegistry.ANCIENT_REMNANT_BLOCK.get());
		this.tag(STORAGE_BLOCKS_RUBBER).add(BlockRegistry.RUBBER_BLOCK.get());
		this.tag(STORAGE_BLOCKS_COMPOST).add(BlockRegistry.COMPOST_BLOCK.get());
	}

	public static TagKey<Block> tag(String tagName) {
		return BlockTags.create(TheBetweenlands.prefix(tagName));
	}

	public static TagKey<Block> commonTag(String tagName) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", tagName));
	}
}
