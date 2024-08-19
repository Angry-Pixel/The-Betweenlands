package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.DruidStoneBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsBlockStateProvider extends BlockStateProvider {

	public BetweenlandsBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.druidStone(BlockRegistry.DRUID_STONE_1);
		this.druidStone(BlockRegistry.DRUID_STONE_2);
		this.druidStone(BlockRegistry.DRUID_STONE_3);
		this.druidStone(BlockRegistry.DRUID_STONE_4);
		this.druidStone(BlockRegistry.DRUID_STONE_5);
		this.simpleBlockWithItem(BlockRegistry.DRUID_STONE_6.get(), this.models().getExistingFile(this.blockTexture(Blocks.STONE)));
		this.simpleBlockWithItem(BlockRegistry.BETWEENLANDS_BEDROCK);
		this.simpleBlockWithItem(BlockRegistry.BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.CORRUPT_BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.MUD);
		this.simpleBlockWithItem(BlockRegistry.PEAT);
		this.simpleBlockWithItem(BlockRegistry.SMOULDERING_PEAT);
		//sludgy dirt
		this.simpleBlockWithItem(BlockRegistry.SLIMY_DIRT);
		//slimy grass
		this.simpleBlockWithItem(BlockRegistry.CRAGROCK);
		//mossy cragrock
		this.simpleBlockWithItem(BlockRegistry.PITSTONE);
		this.simpleBlockWithItem(BlockRegistry.LIMESTONE);
		this.simpleBlockWithItem(BlockRegistry.SWAMP_DIRT);
		this.simpleBlockWithItem(BlockRegistry.COARSE_SWAMP_DIRT);
		//swamp grass
		//wisp
		this.simpleBlockWithItem(BlockRegistry.OCTINE_ORE);
		this.simpleBlockWithItem(BlockRegistry.VALONITE_ORE);
		this.simpleBlockWithItem(BlockRegistry.SULFUR_ORE);
		this.simpleBlockWithItem(BlockRegistry.SLIMY_BONE_ORE);
		this.simpleBlockWithItem(BlockRegistry.SCABYST_ORE);
		this.simpleBlockWithItem(BlockRegistry.AQUA_MIDDLE_GEM_ORE);
		this.simpleBlockWithItem(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE);
		this.simpleBlockWithItem(BlockRegistry.GREEN_MIDDLE_GEM_ORE);
		this.simpleBlockWithItem(BlockRegistry.SILT);
		this.simpleBlockWithItem(BlockRegistry.FILTERED_SILT);
		//dead grass (has shaggy models)
		this.simpleBlockWithItem(BlockRegistry.SOLID_TAR);
		//puddle?
		this.simpleBlockWithItem(BlockRegistry.PEARL_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.ANCIENT_REMNANT_BLOCK);
		this.logBlockWithItem(BlockRegistry.WEEDWOOD_LOG); //TODO weedwood has 8 side variants
		this.barkBlockWithItem(BlockRegistry.WEEDWOOD_BARK);
		this.barkBlockWithItem(BlockRegistry.ROTTEN_BARK);
		this.simpleBlockWithItem(BlockRegistry.SPREADING_ROTTEN_BARK.get(), this.models().getExistingFile(this.blockTexture(BlockRegistry.ROTTEN_BARK.get())));
		//rubber tree log
		this.logBlockWithItem(BlockRegistry.HEARTHGROVE_LOG);
		this.axisBlock((RotatedPillarBlock) BlockRegistry.TARRED_HEARTHGROVE_LOG.get(), this.blockTexture(BlockRegistry.TARRED_HEARTHGROVE_LOG.get()).withSuffix("_side"), this.blockTexture(BlockRegistry.HEARTHGROVE_LOG.get()).withSuffix("_end"));
		this.simpleBlockItem(BlockRegistry.TARRED_HEARTHGROVE_LOG);
		this.barkBlockWithItem(BlockRegistry.HEARTHGROVE_BARK);
		this.barkBlockWithItem(BlockRegistry.TARRED_HEARTHGROVE_BARK);
//		this.logBlockWithItem(BlockRegistry.NIBBLETWIG_LOG); TODO nibbletwig has 4 side variants
//		this.barkBlockWithItem(BlockRegistry.NIBBLETWIG_BARK);
		this.axisBlock((RotatedPillarBlock) BlockRegistry.SPIRIT_TREE_LOG.get(), this.blockTexture(BlockRegistry.SPIRIT_TREE_LOG.get()).withSuffix("_side"), this.blockTexture(BlockRegistry.SPIRIT_TREE_LOG.get()).withSuffix("_side"));
		this.simpleBlockItem(BlockRegistry.SPIRIT_TREE_LOG);
		this.barkBlockWithItem(BlockRegistry.SPIRIT_TREE_BARK);
		this.simpleBlockWithItem(BlockRegistry.WEEDWOOD);
		this.logBlockWithItem(BlockRegistry.SAP_LOG);
		this.barkBlockWithItem(BlockRegistry.SAP_BARK);
		this.simpleBlockWithItem(BlockRegistry.WEEDWOOD_LEAVES);
		this.simpleBlockWithItem(BlockRegistry.SAP_LEAVES);
		this.simpleBlockWithItem(BlockRegistry.RUBBER_TREE_LEAVES);
		this.simpleBlockWithItem(BlockRegistry.HEARTHGROVE_LEAVES);
		this.simpleBlockWithItem(BlockRegistry.NIBBLETWIG_LEAVES);
		//spirit tree leaves
		this.simpleBlockWithItem(BlockRegistry.WEEDWOOD_PLANKS);
		this.simpleBlockWithItem(BlockRegistry.RUBBER_TREE_PLANKS);
		this.simpleBlockWithItem(BlockRegistry.GIANT_ROOT_PLANKS);
		this.simpleBlockWithItem(BlockRegistry.HEARTHGROVE_PLANKS);
		this.simpleBlockWithItem(BlockRegistry.NIBBLETWIG_PLANKS);
		this.simpleBlockWithItem(BlockRegistry.ANGRY_BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.BETWEENSTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.MIRAGE_BETWEENSTONE_BRICKS.get(), this.models().getExistingFile(this.blockTexture(BlockRegistry.BETWEENSTONE_BRICKS.get())));
		this.simpleBlockWithItem(BlockRegistry.BETWEENSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_CHISELED_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_CHISELED_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_LIMESTONE);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_PITSTONE);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_SCABYST_1);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_SCABYST_2);
		this.simpleBlockWithItem(BlockRegistry.CHISELED_SCABYST_3);
		//scabyst pitstone
		this.simpleBlockWithItem(BlockRegistry.SCABYST_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_BETWEENSTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_BETWEENSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_LIMESTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.CRAGROCK_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_CRAGROCK_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_CRAGROCK_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.CRAGROCK_TILES);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_CRAGROCK_TILES);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_CRAGROCK_TILES);
		this.simpleBlockWithItem(BlockRegistry.GLOWING_BETWEENSTONE_TILE);
		this.simpleBlockWithItem(BlockRegistry.INACTIVE_GLOWING_SMOOTH_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.GLOWING_SMOOTH_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.LIMESTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.LIMESTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_BETWEENSTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_BETWEENSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_LIMESTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.MUD_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.RUBBER_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.PITSTONE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.PITSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.POLISHED_LIMESTONE);
		this.simpleBlockWithItem(BlockRegistry.SMOOTH_BETWEENSTONE);
		this.simpleBlockWithItem(BlockRegistry.SMOOTH_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.OCTINE_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.SYRMORITE_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.VALONITE_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.SCABYST_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.WEAK_BETWEENSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.WEAK_POLISHED_LIMESTONE);
		this.simpleBlockWithItem(BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES);
		this.simpleBlockWithItem(BlockRegistry.WEAK_SMOOTH_CRAGROCK);
		this.simpleBlockWithItem(BlockRegistry.GREEN_DENTROTHYST);
		this.simpleBlockWithItem(BlockRegistry.ORANGE_DENTROTHYST);
		//pots of chance
		//monster spawner
		//pillars (need custom model. 16 pixels tall, only 14 wide)
		this.stairBlockWithItem(BlockRegistry.CRAGROCK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.PITSTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.BETWEENSTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.BETWEENSTONE_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.MUD_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CRAGROCK_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.LIMESTONE_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.PITSTONE_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.LIMESTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SMOOTH_CRAGROCK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.POLISHED_LIMESTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SCABYST_BRICK_STAIRS);
		this.simpleBlockWithItem(BlockRegistry.SULFUR_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.TEMPLE_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.SMOOTH_PITSTONE);
		this.simpleBlockWithItem(BlockRegistry.MIRE_CORAL_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.DEEP_WATER_CORAL_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.SLIMY_BONE_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.COMPOST_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.POLISHED_GREEN_DENTROTHYST);
		this.simpleBlockWithItem(BlockRegistry.POLISHED_ORANGE_DENTROTHYST);
		this.simpleBlockWithItem(BlockRegistry.SILT_GLASS);
		//all panes
		this.stairBlockWithItem(BlockRegistry.SMOOTH_PITSTONE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SOLID_TAR_STAIRS);
		this.stairBlockWithItem(BlockRegistry.TEMPLE_BRICK_STAIRS);
		//spike trap
		this.woodStairBlockWithItem(BlockRegistry.WEEDWOOD_STAIRS);
		this.woodStairBlockWithItem(BlockRegistry.RUBBER_TREE_STAIRS);
		this.woodStairBlockWithItem(BlockRegistry.GIANT_ROOT_STAIRS);
		this.woodStairBlockWithItem(BlockRegistry.HEARTHGROVE_STAIRS);
		this.woodStairBlockWithItem(BlockRegistry.NIBBLETWIG_STAIRS);
		this.simpleBlockWithItem(BlockRegistry.POSSESSED_BLOCK.get(), this.models().getExistingFile(this.blockTexture(BlockRegistry.BETWEENSTONE_BRICKS.get())));
		//item cage
		//item shelf
		this.simpleBlockWithItem(BlockRegistry.THATCH);
		this.slabBlockWithItem(BlockRegistry.CRAGROCK_SLAB);
		this.slabBlockWithItem(BlockRegistry.PITSTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.BETWEENSTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SMOOTH_PITSTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SOLID_TAR_SLAB);
		this.slabBlockWithItem(BlockRegistry.TEMPLE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.BETWEENSTONE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.MUD_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.CRAGROCK_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.LIMESTONE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.PITSTONE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.LIMESTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SMOOTH_BETWEENSTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SMOOTH_CRAGROCK_SLAB);
		this.slabBlockWithItem(BlockRegistry.POLISHED_LIMESTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB);
		this.slabBlockWithItem(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_SLAB);
		this.slabBlockWithItem(BlockRegistry.SCABYST_BRICK_SLAB);
		this.woodSlabBlockWithItem(BlockRegistry.WEEDWOOD_SLAB);
		this.woodSlabBlockWithItem(BlockRegistry.RUBBER_TREE_SLAB);
		this.woodSlabBlockWithItem(BlockRegistry.GIANT_ROOT_SLAB);
		this.woodSlabBlockWithItem(BlockRegistry.HEARTHGROVE_SLAB);
		this.woodSlabBlockWithItem(BlockRegistry.NIBBLETWIG_SLAB);
		this.slabBlockWithItem(BlockRegistry.MUD_BRICK_SHINGLE_SLAB);
		this.stairBlockWithItem(BlockRegistry.MUD_BRICK_SHINGLE_STAIRS);
		this.wallBlockWithItem(BlockRegistry.MUD_BRICK_SHINGLE_WALL);
		//TODO CTM for filtered silt
//		this.simpleBlockWithItem(BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.MAROON_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.SAFFRON_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS_BLOCK);
//		this.simpleBlockWithItem(BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS_BLOCK);
		this.simpleBlockWithItem(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.MAROON_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES);
		this.simpleBlockWithItem(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES);
		this.slabBlockWithItem(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB);
		this.slabBlockWithItem(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB);
		this.stairBlockWithItem(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS);
		this.stairBlockWithItem(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS);
		this.slabBlockWithItem(BlockRegistry.THATCH_SLAB);
		this.slabBlockWithItem(BlockRegistry.SCABYST_BRICK_SLAB);
		this.wallBlockWithItem(BlockRegistry.CRAGROCK_WALL);
		this.wallBlockWithItem(BlockRegistry.PITSTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.BETWEENSTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.SMOOTH_PITSTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.SOLID_TAR_WALL);
		this.wallBlockWithItem(BlockRegistry.TEMPLE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.BETWEENSTONE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.MUD_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.CRAGROCK_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.LIMESTONE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.PITSTONE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.LIMESTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.SMOOTH_BETWEENSTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.SMOOTH_CRAGROCK_WALL);
		this.wallBlockWithItem(BlockRegistry.POLISHED_LIMESTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_WALL);
		this.wallBlockWithItem(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_WALL);
		this.wallBlockWithItem(BlockRegistry.SCABYST_BRICK_WALL);
		//fence (gate)s
		//pressure plates
		//buttons
		//ladder
		//lever
		//pots
		this.simpleBlockWithItem(BlockRegistry.WORM_PILLAR);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_1);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_2);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_3);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_4);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_5);
		this.simpleBlockWithItem(BlockRegistry.WORM_PILLAR_TOP);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_1);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_2);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_3);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_4);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_5);
		this.simpleBlockWithItem(BlockRegistry.COMPACTED_MUD);
		this.simpleBlockWithItem(BlockRegistry.MUD_TILES);
//		this.simpleBlockWithItem(BlockRegistry.DECAYED_MUD_TILES);
		this.simpleBlockWithItem(BlockRegistry.CRACKED_MUD_TILES);
//		this.simpleBlockWithItem(BlockRegistry.CRACKED_DECAYED_MUD_TILES);
		//puffshroom
		this.simpleBlockWithItem(BlockRegistry.CARVED_MUD_BRICKS);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_1);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_2);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_3);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_4);
		this.simpleBlockWithItem(BlockRegistry.CARVED_MUD_BRICK_EDGE);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_1);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_2);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_3);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_4);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICKS_1);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICKS_2);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICKS_3);
		this.simpleBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICKS_4);
		this.stairBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_1, "s");
		this.stairBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_2, "s");
		this.stairBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_3, "s");
		this.stairBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4, "s");
		this.slabBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_1, "s");
		this.slabBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_2, "s");
		this.slabBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_3, "s");
		this.slabBlockWithItem(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_4, "s");

		this.simpleBlock(BlockRegistry.SWAMP_WATER.get(), this.models().getBuilder("swamp_water").texture("particle", this.modLoc("fluid/swamp_water_still")));
	}

	private void druidStone(DeferredBlock<Block> stone) {
		ModelFile inactive = this.models().orientable(stone.getId().toString(), this.blockTexture(Blocks.STONE), this.blockTexture(stone.get()), this.blockTexture(Blocks.STONE));
		ModelFile active = this.models().orientable(stone.getId() + "_active", this.blockTexture(Blocks.STONE), this.blockTexture(stone.get()), this.blockTexture(Blocks.STONE));

		this.getVariantBuilder(stone.get()).forAllStates(state -> ConfiguredModel.builder().rotationY((int) state.getValue(DruidStoneBlock.FACING).toYRot()).modelFile(state.getValue(DruidStoneBlock.ACTIVE) ? active : inactive).build());
		this.simpleBlockItem(stone);
	}

	public void logBlockWithItem(DeferredBlock<Block> block) {
		this.axisBlock((RotatedPillarBlock) block.get());
		this.simpleBlockItem(block);
	}

	public void barkBlockWithItem(DeferredBlock<Block> block) {
		ResourceLocation tex = ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("bark", "log_side"));
		this.axisBlock((RotatedPillarBlock) block.get(), this.models().cubeColumn(block.getId().toString(), tex, tex), this.models().cubeColumnHorizontal(block.getId() + "_horizontal", tex, tex));
		this.simpleBlockItem(block);
	}

	public void slabBlockWithItem(DeferredBlock<Block> block) {
		ResourceLocation tex = ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("_slab", "") + (block.getId().getPath().contains("brick") ? "s" : ""));
		this.slabBlock((SlabBlock) block.get(), tex, tex);
		this.simpleBlockItem(block);
	}

	public void slabBlockWithItem(DeferredBlock<Block> block, String replacement) {
		ResourceLocation tex = ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("_slab", replacement));
		this.slabBlock((SlabBlock) block.get(), tex, tex);
		this.simpleBlockItem(block);
	}

	public void woodSlabBlockWithItem(DeferredBlock<Block> block) {
		ResourceLocation tex = ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("slab", "planks"));
		this.slabBlock((SlabBlock) block.get(), tex, tex);
		this.simpleBlockItem(block);
	}

	public void stairBlockWithItem(DeferredBlock<Block> block, String replacement) {
		this.stairsBlock((StairBlock) block.get(), ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("_stairs", replacement)));
		this.simpleBlockItem(block);
	}

	public void stairBlockWithItem(DeferredBlock<Block> block) {
		this.stairsBlock((StairBlock) block.get(), ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("_stairs", "") + (block.getId().getPath().contains("brick") ? "s" : "")));
		this.simpleBlockItem(block);
	}

	public void woodStairBlockWithItem(DeferredBlock<Block> block) {
		this.stairsBlock((StairBlock) block.get(), ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("stairs", "planks")));
		this.simpleBlockItem(block);
	}

	public void wallBlockWithItem(DeferredBlock<Block> block) {
		ResourceLocation tex = ResourceLocation.parse(this.blockTexture(block.get()).toString().replace("_wall", "") + (block.getId().getPath().contains("brick") ? "s" : ""));
		this.wallBlock((WallBlock) block.get(), tex);
		this.itemModels().wallInventory(block.getId().toString(), tex);
	}

	public void simpleBlockWithItem(DeferredBlock<Block> block) {
		this.simpleBlock(block.get());
		this.simpleBlockItem(block);
	}

	public void simpleBlockItem(DeferredBlock<Block> block) {
		this.itemModels().withExistingParent(block.getId().toString(), this.modLoc("block/" + block.getId().getPath()));
	}
}
