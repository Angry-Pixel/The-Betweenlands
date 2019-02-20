package thebetweenlands.common.registries;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import thebetweenlands.common.block.farming.BlockAspectrusCrop;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.block.structure.BlockDruidStone;
import thebetweenlands.common.block.terrain.BlockMud;
import thebetweenlands.common.lib.ModInfo;

@ObjectHolder(ModInfo.ID)
public class BlockRegistry {
	//@ObjectHolder("swamp_water")
	public static final Block SWAMP_WATER = null;

	//@ObjectHolder("stagnant_water")
	public static final Block STAGNANT_WATER = null;

	//@ObjectHolder("tar")
	public static final Block TAR = null;

	//@ObjectHolder("rubber")
	public static final Block RUBBER = null;

	@ObjectHolder("druid_stone_1")
	public static final Block DRUID_STONE_1 = null;

	@ObjectHolder("druid_stone_2")
	public static final Block DRUID_STONE_2 = null;

	@ObjectHolder("druid_stone_3")
	public static final Block DRUID_STONE_3 = null;

	@ObjectHolder("druid_stone_4")
	public static final Block DRUID_STONE_4 = null;

	@ObjectHolder("druid_stone_5")
	public static final Block DRUID_STONE_5 = null;

	@ObjectHolder("druid_stone_6")
	public static final Block DRUID_STONE_6 = null;

	@ObjectHolder("druid_stone_1_active")
	public static final Block DRUID_STONE_1_ACTIVE = null;

	@ObjectHolder("druid_stone_2_active")
	public static final Block DRUID_STONE_2_ACTIVE = null;

	@ObjectHolder("druid_stone_3_active")
	public static final Block DRUID_STONE_3_ACTIVE = null;

	@ObjectHolder("druid_stone_4_active")
	public static final Block DRUID_STONE_4_ACTIVE = null;

	@ObjectHolder("druid_stone_5_active")
	public static final Block DRUID_STONE_5_ACTIVE = null;

	@ObjectHolder("druid_stone_6_active")
	public static final Block DRUID_STONE_6_ACTIVE = null;

	//@ObjectHolder("betweenlands_bedrock")
	public static final Block BETWEENLANDS_BEDROCK = null;

	@ObjectHolder("betweenstone")
	public static final Block BETWEENSTONE = null;

	//@ObjectHolder("generic_stone")
	public static final Block GENERIC_STONE = null;

	//@ObjectHolder("mud")
	public static final Block MUD = null;

	//@ObjectHolder("peat")
	public static final Block PEAT = null;

	//@ObjectHolder("sludgy_dirt")
	public static final Block SLUDGY_DIRT = null;

	//@ObjectHolder("spreading_sludgy_dirt")
	public static final Block SPREADING_SLUDGY_DIRT = null;

	//@ObjectHolder("slimy_dirt")
	public static final Block SLIMY_DIRT = null;

	//@ObjectHolder("slimy_grass")
	public static final Block SLIMY_GRASS = null;

	//@ObjectHolder("cragrock")
	public static final Block CRAGROCK = null;

	//@ObjectHolder("pitstone")
	public static final Block PITSTONE = null;

	//@ObjectHolder("limestone")
	public static final Block LIMESTONE = null;

	//@ObjectHolder("swamp_dirt")
	public static final Block SWAMP_DIRT = null;

	//@ObjectHolder("coarse_swamp_dirt")
	public static final Block COARSE_SWAMP_DIRT = null;

	//@ObjectHolder("swamp_grass")
	public static final Block SWAMP_GRASS = null;

	//@ObjectHolder("wisp")
	public static final Block WISP = null;

	//@ObjectHolder("octine_ore")
	public static final Block OCTINE_ORE = null;

	//@ObjectHolder("valonite_ore")
	public static final Block VALONITE_ORE = null;

	//@ObjectHolder("sulfur_ore")
	public static final Block SULFUR_ORE = null;

	//@ObjectHolder("slimy_bone_ore")
	public static final Block SLIMY_BONE_ORE = null;

	//@ObjectHolder("scabyst_ore")
	public static final Block SCABYST_ORE = null;

	//@ObjectHolder("syrmorite_ore")
	public static final Block SYRMORITE_ORE = null;

	//@ObjectHolder("aqua_middle_gem_ore")
	public static final Block AQUA_MIDDLE_GEM_ORE = null;

	//@ObjectHolder("crimson_middle_gem_ore")
	public static final Block CRIMSON_MIDDLE_GEM_ORE = null;

	//@ObjectHolder("green_middle_gem_ore")
	public static final Block GREEN_MIDDLE_GEM_ORE = null;

	//@ObjectHolder("life_crystal_stalactite")
	public static final Block LIFE_CRYSTAL_STALACTITE = null;

	//@ObjectHolder("stalactite")
	public static final Block STALACTITE = null;

	//@ObjectHolder("silt")
	public static final Block SILT = null;

	//@ObjectHolder("dead_grass")
	public static final Block DEAD_GRASS = null;

	//@ObjectHolder("tar_solid")
	public static final Block TAR_SOLID = null;

	//@ObjectHolder("puddle")
	public static final Block PUDDLE = null;

	//@ObjectHolder("log_weedwood")
	public static final Block LOG_WEEDWOOD = null;

	//@ObjectHolder("log_rotten_bark")
	public static final Block LOG_ROTTEN_BARK = null;

	//@ObjectHolder("log_spreading_rotten_bark")
	public static final Block LOG_SPREADING_ROTTEN_BARK = null;

	//@ObjectHolder("log_rubber")
	public static final Block LOG_RUBBER = null;

	//@ObjectHolder("log_hearthgrove")
	public static final Block LOG_HEARTHGROVE = null;

	//@ObjectHolder("log_nibbletwig")
	public static final Block LOG_NIBBLETWIG = null;

	//@ObjectHolder("log_spirit_tree")
	public static final Block LOG_SPIRIT_TREE = null;

	//@ObjectHolder("weedwood")
	public static final Block WEEDWOOD = null;

	//@ObjectHolder("log_sap")
	public static final Block LOG_SAP = null;

	//@ObjectHolder("sapling_weedwood")
	public static final Block SAPLING_WEEDWOOD = null;

	//@ObjectHolder("sapling_sap")
	public static final Block SAPLING_SAP = null;

	//@ObjectHolder("sapling_rubber")
	public static final Block SAPLING_RUBBER = null;

	//@ObjectHolder("sapling_hearthgrove")
	public static final Block SAPLING_HEARTHGROVE = null;

	//@ObjectHolder("sapling_nibbletwig")
	public static final Block SAPLING_NIBBLETWIG = null;

	//@ObjectHolder("sapling_spirit_tree")
	public static final Block SAPLING_SPIRIT_TREE = null;

	//@ObjectHolder("root_pod")
	public static final Block ROOT_POD = null;

	//@ObjectHolder("leaves_weedwood_tree")
	public static final Block LEAVES_WEEDWOOD_TREE = null;

	//@ObjectHolder("leaves_sap_tree")
	public static final Block LEAVES_SAP_TREE = null;

	//@ObjectHolder("leaves_rubber_tree")
	public static final Block LEAVES_RUBBER_TREE = null;

	//@ObjectHolder("leaves_hearthgrove_tree")
	public static final Block LEAVES_HEARTHGROVE_TREE = null;

	//@ObjectHolder("leaves_nibbletwig_tree")
	public static final Block LEAVES_NIBBLETWIG_TREE = null;

	//@ObjectHolder("leaves_spirit_tree_top")
	public static final Block LEAVES_SPIRIT_TREE_TOP = null;

	//@ObjectHolder("leaves_spirit_tree_middle")
	public static final Block LEAVES_SPIRIT_TREE_MIDDLE = null;

	//@ObjectHolder("leaves_spirit_tree_bottom")
	public static final Block LEAVES_SPIRIT_TREE_BOTTOM = null;

	//@ObjectHolder("weedwood_planks")
	public static final Block WEEDWOOD_PLANKS = null;

	//@ObjectHolder("rubber_tree_planks")
	public static final Block RUBBER_TREE_PLANKS = null;

	//@ObjectHolder("giant_root_planks")
	public static final Block GIANT_ROOT_PLANKS = null;

	//@ObjectHolder("hearthgrove_planks")
	public static final Block HEARTHGROVE_PLANKS = null;

	//@ObjectHolder("nibbletwig_planks")
	public static final Block NIBBLETWIG_PLANKS = null;

	//@ObjectHolder("angry_betweenstone")
	public static final Block ANGRY_BETWEENSTONE = null;

	//@ObjectHolder("betweenstone_bricks")
	public static final Block BETWEENSTONE_BRICKS = null;

	//@ObjectHolder("betweenstone_bricks_mirage")
	public static final Block BETWEENSTONE_BRICKS_MIRAGE = null;

	//@ObjectHolder("betweenstone_tiles")
	public static final Block BETWEENSTONE_TILES = null;

	//@ObjectHolder("betweenstone_chiseled")
	public static final Block BETWEENSTONE_CHISELED = null;

	//@ObjectHolder("cragrock_chiseled")
	public static final Block CRAGROCK_CHISELED = null;

	//@ObjectHolder("limestone_chiseled")
	public static final Block LIMESTONE_CHISELED = null;

	//@ObjectHolder("pitstone_chiseled")
	public static final Block PITSTONE_CHISELED = null;

	//@ObjectHolder("scabyst_chiseled_1")
	public static final Block SCABYST_CHISELED_1 = null;

	//@ObjectHolder("scabyst_chiseled_2")
	public static final Block SCABYST_CHISELED_2 = null;

	//@ObjectHolder("scabyst_chiseled_3")
	public static final Block SCABYST_CHISELED_3 = null;

	//@ObjectHolder("scabyst_pitstone_dotted")
	public static final Block SCABYST_PITSTONE_DOTTED = null;

	//@ObjectHolder("scabyst_pitstone_horizontal")
	public static final Block SCABYST_PITSTONE_HORIZONTAL = null;

	//@ObjectHolder("scabyst_bricks")
	public static final Block SCABYST_BRICKS = null;

	//@ObjectHolder("cracked_betweenstone_bricks")
	public static final Block CRACKED_BETWEENSTONE_BRICKS = null;

	//@ObjectHolder("cracked_betweenstone_tiles")
	public static final Block CRACKED_BETWEENSTONE_TILES = null;

	//@ObjectHolder("cracked_limestone_bricks")
	public static final Block CRACKED_LIMESTONE_BRICKS = null;

	//@ObjectHolder("cragrock_bricks")
	public static final Block CRAGROCK_BRICKS = null;

	//@ObjectHolder("cragrock_tiles")
	public static final Block CRAGROCK_TILES = null;

	//@ObjectHolder("glowing_betweenstone_tile")
	public static final Block GLOWING_BETWEENSTONE_TILE = null;

	//@ObjectHolder("inactive_glowing_smooth_cragrock")
	public static final Block INACTIVE_GLOWING_SMOOTH_CRAGROCK = null;

	//@ObjectHolder("glowing_smooth_cragrock")
	public static final Block GLOWING_SMOOTH_CRAGROCK = null;

	//@ObjectHolder("limestone_bricks")
	public static final Block LIMESTONE_BRICKS = null;

	//@ObjectHolder("limestone_tiles")
	public static final Block LIMESTONE_TILES = null;

	//@ObjectHolder("mossy_betweenstone_bricks")
	public static final Block MOSSY_BETWEENSTONE_BRICKS = null;

	//@ObjectHolder("mossy_betweenstone_tiles")
	public static final Block MOSSY_BETWEENSTONE_TILES = null;

	//@ObjectHolder("mossy_limestone_bricks")
	public static final Block MOSSY_LIMESTONE_BRICKS = null;

	//@ObjectHolder("mossy_smooth_betweenstone")
	public static final Block MOSSY_SMOOTH_BETWEENSTONE = null;

	//@ObjectHolder("mud_bricks")
	public static final Block MUD_BRICKS = null;

	//@ObjectHolder("mud_brick_shingles")
	public static final Block MUD_BRICK_SHINGLES = null;

	//@ObjectHolder("rubber_block")
	public static final Block RUBBER_BLOCK = null;

	//@ObjectHolder("pitstone_bricks")
	public static final Block PITSTONE_BRICKS = null;

	//@ObjectHolder("pitstone_tiles")
	public static final Block PITSTONE_TILES = null;

	//@ObjectHolder("polished_limestone")
	public static final Block POLISHED_LIMESTONE = null;

	//@ObjectHolder("smooth_betweenstone")
	public static final Block SMOOTH_BETWEENSTONE = null;

	//@ObjectHolder("smooth_cragrock")
	public static final Block SMOOTH_CRAGROCK = null;

	//@ObjectHolder("octine_block")
	public static final Block OCTINE_BLOCK = null;

	//@ObjectHolder("syrmorite_block")
	public static final Block SYRMORITE_BLOCK = null;

	//@ObjectHolder("valonite_block")
	public static final Block VALONITE_BLOCK = null;

	//@ObjectHolder("scabyst_block")
	public static final Block SCABYST_BLOCK = null;

	//@ObjectHolder("weak_betweenstone_tiles")
	public static final Block WEAK_BETWEENSTONE_TILES = null;

	//@ObjectHolder("weak_polished_limestone")
	public static final Block WEAK_POLISHED_LIMESTONE = null;

	//@ObjectHolder("weak_mossy_betweenstone_tiles")
	public static final Block WEAK_MOSSY_BETWEENSTONE_TILES = null;

	//@ObjectHolder("dentrothyst")
	public static final Block DENTROTHYST = null;

	//@ObjectHolder("loot_pot")
	public static final Block LOOT_POT = null;

	//@ObjectHolder("mob_spawner")
	public static final Block MOB_SPAWNER = null;

	//@ObjectHolder("temple_pillar")
	public static final Block TEMPLE_PILLAR = null;

	//@ObjectHolder("betweenstone_pillar")
	public static final Block BETWEENSTONE_PILLAR = null;

	//@ObjectHolder("pitstone_pillar")
	public static final Block PITSTONE_PILLAR = null;

	//@ObjectHolder("limestone_pillar")
	public static final Block LIMESTONE_PILLAR = null;

	//@ObjectHolder("cragrock_pillar")
	public static final Block CRAGROCK_PILLAR = null;

	//@ObjectHolder("tar_beast_spawner")
	public static final Block TAR_BEAST_SPAWNER = null;

	//@ObjectHolder("tar_loot_pot")
	public static final Block TAR_LOOT_POT = null;

	//@ObjectHolder("cragrock_stairs")
	public static final Block CRAGROCK_STAIRS = null;

	//@ObjectHolder("pitstone_stairs")
	public static final Block PITSTONE_STAIRS = null;

	//@ObjectHolder("betweenstone_stairs")
	public static final Block BETWEENSTONE_STAIRS = null;

	//@ObjectHolder("betweenstone_brick_stairs")
	public static final Block BETWEENSTONE_BRICK_STAIRS = null;

	//@ObjectHolder("mud_brick_stairs")
	public static final Block MUD_BRICK_STAIRS = null;

	//@ObjectHolder("cragrock_brick_stairs")
	public static final Block CRAGROCK_BRICK_STAIRS = null;

	//@ObjectHolder("limestone_brick_stairs")
	public static final Block LIMESTONE_BRICK_STAIRS = null;

	//@ObjectHolder("pitstone_brick_stairs")
	public static final Block PITSTONE_BRICK_STAIRS = null;

	//@ObjectHolder("limestone_stairs")
	public static final Block LIMESTONE_STAIRS = null;

	//@ObjectHolder("smooth_betweenstone_stairs")
	public static final Block SMOOTH_BETWEENSTONE_STAIRS = null;

	//@ObjectHolder("smooth_cragrock_stairs")
	public static final Block SMOOTH_CRAGROCK_STAIRS = null;

	//@ObjectHolder("polished_limestone_stairs")
	public static final Block POLISHED_LIMESTONE_STAIRS = null;

	//@ObjectHolder("mossy_betweenstone_brick_stairs")
	public static final Block MOSSY_BETWEENSTONE_BRICK_STAIRS = null;

	//@ObjectHolder("mossy_smooth_betweenstone_stairs")
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_STAIRS = null;

	//@ObjectHolder("cracked_betweenstone_brick_stairs")
	public static final Block CRACKED_BETWEENSTONE_BRICK_STAIRS = null;

	//@ObjectHolder("scabyst_brick_stairs")
	public static final Block SCABYST_BRICK_STAIRS = null;

	//@ObjectHolder("sulfur_block")
	public static final Block SULFUR_BLOCK = null;

	//@ObjectHolder("temple_bricks")
	public static final Block TEMPLE_BRICKS = null;

	//@ObjectHolder("smooth_pitstone")
	public static final Block SMOOTH_PITSTONE = null;

	//@ObjectHolder("mire_coral_block")
	public static final Block MIRE_CORAL_BLOCK = null;

	//@ObjectHolder("deep_water_coral_block")
	public static final Block DEEP_WATER_CORAL_BLOCK = null;

	//@ObjectHolder("slimy_bone_block")
	public static final Block SLIMY_BONE_BLOCK = null;

	//@ObjectHolder("aqua_middle_gem_block")
	public static final Block AQUA_MIDDLE_GEM_BLOCK = null;

	//@ObjectHolder("crimson_middle_gem_block")
	public static final Block CRIMSON_MIDDLE_GEM_BLOCK = null;

	//@ObjectHolder("green_middle_gem_block")
	public static final Block GREEN_MIDDLE_GEM_BLOCK = null;

	//@ObjectHolder("compost_block")
	public static final Block COMPOST_BLOCK = null;

	//@ObjectHolder("polished_dentrothyst")
	public static final Block POLISHED_DENTROTHYST = null;

	//@ObjectHolder("silt_glass")
	public static final Block SILT_GLASS = null;

	//@ObjectHolder("silt_glass_pane")
	public static final Block SILT_GLASS_PANE = null;

	//@ObjectHolder("polished_dentrothyst_pane")
	public static final Block POLISHED_DENTROTHYST_PANE = null;

	//@ObjectHolder("amate_paper_pane_1")
	public static final Block AMATE_PAPER_PANE_1 = null;

	//@ObjectHolder("amate_paper_pane_2")
	public static final Block AMATE_PAPER_PANE_2 = null;

	//@ObjectHolder("amate_paper_pane_3")
	public static final Block AMATE_PAPER_PANE_3 = null;

	//@ObjectHolder("smooth_pitstone_stairs")
	public static final Block SMOOTH_PITSTONE_STAIRS = null;

	//@ObjectHolder("tar_solid_stairs")
	public static final Block TAR_SOLID_STAIRS = null;

	//@ObjectHolder("temple_brick_stairs")
	public static final Block TEMPLE_BRICK_STAIRS = null;

	//@ObjectHolder("spike_trap")
	public static final Block SPIKE_TRAP = null;

	//@ObjectHolder("weedwood_plank_stairs")
	public static final Block WEEDWOOD_PLANK_STAIRS = null;

	//@ObjectHolder("rubber_tree_plank_stairs")
	public static final Block RUBBER_TREE_PLANK_STAIRS = null;

	//@ObjectHolder("giant_root_plank_stairs")
	public static final Block GIANT_ROOT_PLANK_STAIRS = null;

	//@ObjectHolder("hearthgrove_plank_stairs")
	public static final Block HEARTHGROVE_PLANK_STAIRS = null;

	//@ObjectHolder("nibbletwig_plank_stairs")
	public static final Block NIBBLETWIG_PLANK_STAIRS = null;

	//@ObjectHolder("possessed_block")
	public static final Block POSSESSED_BLOCK = null;

	//@ObjectHolder("item_cage")
	public static final Block ITEM_CAGE = null;

	//@ObjectHolder("item_shelf")
	public static final Block ITEM_SHELF = null;

	//@ObjectHolder("thatch")
	public static final Block THATCH = null;

	//@ObjectHolder("cragrock_slab")
	public static final Block CRAGROCK_SLAB = null;

	//@ObjectHolder("pitstone_slab")
	public static final Block PITSTONE_SLAB = null;

	//@ObjectHolder("betweenstone_slab")
	public static final Block BETWEENSTONE_SLAB = null;

	//@ObjectHolder("smooth_pitstone_slab")
	public static final Block SMOOTH_PITSTONE_SLAB = null;

	//@ObjectHolder("tar_solid_slab")
	public static final Block TAR_SOLID_SLAB = null;

	//@ObjectHolder("temple_brick_slab")
	public static final Block TEMPLE_BRICK_SLAB = null;

	//@ObjectHolder("betweenstone_brick_slab")
	public static final Block BETWEENSTONE_BRICK_SLAB = null;

	//@ObjectHolder("mud_brick_slab")
	public static final Block MUD_BRICK_SLAB = null;

	//@ObjectHolder("cragrock_brick_slab")
	public static final Block CRAGROCK_BRICK_SLAB = null;

	//@ObjectHolder("limestone_brick_slab")
	public static final Block LIMESTONE_BRICK_SLAB = null;

	//@ObjectHolder("limestone_slab")
	public static final Block LIMESTONE_SLAB = null;

	//@ObjectHolder("smooth_betweenstone_slab")
	public static final Block SMOOTH_BETWEENSTONE_SLAB = null;

	//@ObjectHolder("smooth_cragrock_slab")
	public static final Block SMOOTH_CRAGROCK_SLAB = null;

	//@ObjectHolder("polished_limestone_slab")
	public static final Block POLISHED_LIMESTONE_SLAB = null;

	//@ObjectHolder("pitstone_brick_slab")
	public static final Block PITSTONE_BRICK_SLAB = null;

	//@ObjectHolder("mossy_betweenstone_brick_slab")
	public static final Block MOSSY_BETWEENSTONE_BRICK_SLAB = null;

	//@ObjectHolder("mossy_smooth_betweenstone_slab")
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_SLAB = null;

	//@ObjectHolder("cracked_betweenstone_brick_slab")
	public static final Block CRACKED_BETWEENSTONE_BRICK_SLAB = null;

	//@ObjectHolder("weedwood_plank_slab")
	public static final Block WEEDWOOD_PLANK_SLAB = null;

	//@ObjectHolder("rubber_tree_plank_slab")
	public static final Block RUBBER_TREE_PLANK_SLAB = null;

	//@ObjectHolder("giant_root_plank_slab")
	public static final Block GIANT_ROOT_PLANK_SLAB = null;

	//@ObjectHolder("hearthgrove_plank_slab")
	public static final Block HEARTHGROVE_PLANK_SLAB = null;

	//@ObjectHolder("nibbletwig_plank_slab")
	public static final Block NIBBLETWIG_PLANK_SLAB = null;

	//@ObjectHolder("mud_brick_shingle_slab")
	public static final Block MUD_BRICK_SHINGLE_SLAB = null;

	//@ObjectHolder("thatch_slab")
	public static final Block THATCH_SLAB = null;

	//@ObjectHolder("scabyst_brick_slab")
	public static final Block SCABYST_BRICK_SLAB = null;

	//@ObjectHolder("pitstone_wall")
	public static final Block PITSTONE_WALL = null;

	//@ObjectHolder("betweenstone_wall")
	public static final Block BETWEENSTONE_WALL = null;

	//@ObjectHolder("tar_solid_wall")
	public static final Block TAR_SOLID_WALL = null;

	//@ObjectHolder("temple_brick_wall")
	public static final Block TEMPLE_BRICK_WALL = null;

	//@ObjectHolder("smooth_pitstone_wall")
	public static final Block SMOOTH_PITSTONE_WALL = null;

	//@ObjectHolder("betweenstone_brick_wall")
	public static final Block BETWEENSTONE_BRICK_WALL = null;

	//@ObjectHolder("mud_brick_wall")
	public static final Block MUD_BRICK_WALL = null;

	//@ObjectHolder("cragrock_wall")
	public static final Block CRAGROCK_WALL = null;

	//@ObjectHolder("cragrock_brick_wall")
	public static final Block CRAGROCK_BRICK_WALL = null;

	//@ObjectHolder("limestone_brick_wall")
	public static final Block LIMESTONE_BRICK_WALL = null;

	//@ObjectHolder("limestone_wall")
	public static final Block LIMESTONE_WALL = null;

	//@ObjectHolder("polished_limestone_wall")
	public static final Block POLISHED_LIMESTONE_WALL = null;

	//@ObjectHolder("pitstone_brick_wall")
	public static final Block PITSTONE_BRICK_WALL = null;

	//@ObjectHolder("smooth_betweenstone_wall")
	public static final Block SMOOTH_BETWEENSTONE_WALL = null;

	//@ObjectHolder("smooth_cragrock_wall")
	public static final Block SMOOTH_CRAGROCK_WALL = null;

	//@ObjectHolder("mossy_betweenstone_brick_wall")
	public static final Block MOSSY_BETWEENSTONE_BRICK_WALL = null;

	//@ObjectHolder("mossy_smooth_betweenstone_wall")
	public static final Block MOSSY_SMOOTH_BETWEENSTONE_WALL = null;

	//@ObjectHolder("cracked_betweenstone_brick_wall")
	public static final Block CRACKED_BETWEENSTONE_BRICK_WALL = null;

	//@ObjectHolder("scabyst_brick_wall")
	public static final Block SCABYST_BRICK_WALL = null;

	//@ObjectHolder("weedwood_plank_fence")
	public static final Block WEEDWOOD_PLANK_FENCE = null;

	//@ObjectHolder("weedwood_log_fence")
	public static final Block WEEDWOOD_LOG_FENCE = null;

	//@ObjectHolder("rubber_tree_plank_fence")
	public static final Block RUBBER_TREE_PLANK_FENCE = null;

	//@ObjectHolder("giant_root_plank_fence")
	public static final Block GIANT_ROOT_PLANK_FENCE = null;

	//@ObjectHolder("hearthgrove_plank_fence")
	public static final Block HEARTHGROVE_PLANK_FENCE = null;

	//@ObjectHolder("nibbletwig_plank_fence")
	public static final Block NIBBLETWIG_PLANK_FENCE = null;

	//@ObjectHolder("weedwood_plank_fence_gate")
	public static final Block WEEDWOOD_PLANK_FENCE_GATE = null;

	//@ObjectHolder("weedwood_log_fence_gate")
	public static final Block WEEDWOOD_LOG_FENCE_GATE = null;

	//@ObjectHolder("rubber_tree_plank_fence_gate")
	public static final Block RUBBER_TREE_PLANK_FENCE_GATE = null;

	//@ObjectHolder("giant_root_plank_fence_gate")
	public static final Block GIANT_ROOT_PLANK_FENCE_GATE = null;

	//@ObjectHolder("hearthgrove_plank_fence_gate")
	public static final Block HEARTHGROVE_PLANK_FENCE_GATE = null;

	//@ObjectHolder("nibbletwig_plank_fence_gate")
	public static final Block NIBBLETWIG_PLANK_FENCE_GATE = null;

	//@ObjectHolder("weedwood_plank_pressure_plate")
	public static final Block WEEDWOOD_PLANK_PRESSURE_PLATE = null;

	//@ObjectHolder("betweenstone_pressure_plate")
	public static final Block BETWEENSTONE_PRESSURE_PLATE = null;

	//@ObjectHolder("syrmorite_pressure_plate")
	public static final Block SYRMORITE_PRESSURE_PLATE = null;

	//@ObjectHolder("weedwood_plank_button")
	public static final Block WEEDWOOD_PLANK_BUTTON = null;

	//@ObjectHolder("betweenstone_button")
	public static final Block BETWEENSTONE_BUTTON = null;

	//@ObjectHolder("weedwood_ladder")
	public static final Block WEEDWOOD_LADDER = null;

	//@ObjectHolder("weedwood_lever")
	public static final Block WEEDWOOD_LEVER = null;

	//@ObjectHolder("present")
	public static final Block PRESENT = null;

	//@ObjectHolder("pitcher_plant")
	public static final BlockDoublePlantBL PITCHER_PLANT = null;

	//@ObjectHolder("weeping_blue")
	public static final BlockDoublePlantBL WEEPING_BLUE = null;

	//@ObjectHolder("sundew")
	public static final BlockDoublePlantBL SUNDEW = null;

	//@ObjectHolder("black_hat_mushroom")
	public static final Block BLACK_HAT_MUSHROOM = null;

	//@ObjectHolder("bulb_capped_mushroom")
	public static final Block BULB_CAPPED_MUSHROOM = null;

	//@ObjectHolder("flat_head_mushroom")
	public static final Block FLAT_HEAD_MUSHROOM = null;

	//@ObjectHolder("venus_fly_trap")
	public static final Block VENUS_FLY_TRAP = null;

	//@ObjectHolder("volarpad")
	public static final BlockDoublePlantBL VOLARPAD = null;

	//@ObjectHolder("swamp_plant")
	public static final Block SWAMP_PLANT = null;

	//@ObjectHolder("swamp_kelp")
	public static final Block SWAMP_KELP = null;

	//@ObjectHolder("mire_coral")
	public static final Block MIRE_CORAL = null;

	//@ObjectHolder("deep_water_coral")
	public static final Block DEEP_WATER_CORAL = null;

	//@ObjectHolder("water_weeds")
	public static final Block WATER_WEEDS = null;

	//@ObjectHolder("bulb_capped_mushroom_cap")
	public static final Block BULB_CAPPED_MUSHROOM_CAP = null;

	//@ObjectHolder("bulb_capped_mushroom_stalk")
	public static final Block BULB_CAPPED_MUSHROOM_STALK = null;

	//@ObjectHolder("shelf_fungus")
	public static final Block SHELF_FUNGUS = null;

	//@ObjectHolder("algae")
	public static final Block ALGAE = null;

	//@ObjectHolder("poison_ivy")
	public static final Block POISON_IVY = null;

	//@ObjectHolder("root")
	public static final Block ROOT = null;

	//@ObjectHolder("root_underwater")
	public static final Block ROOT_UNDERWATER = null;

	//@ObjectHolder("giant_root")
	public static final Block GIANT_ROOT = null;

	//@ObjectHolder("arrow_arum")
	public static final Block ARROW_ARUM = null;

	//@ObjectHolder("blue_eyed_grass")
	public static final Block BLUE_EYED_GRASS = null;

	//@ObjectHolder("blue_iris")
	public static final Block BLUE_IRIS = null;

	//@ObjectHolder("boneset")
	public static final Block BONESET = null;

	//@ObjectHolder("bottle_brush_grass")
	public static final Block BOTTLE_BRUSH_GRASS = null;

	//@ObjectHolder("broomsedge")
	public static final BlockDoublePlantBL BROOMSEDGE = null;

	//@ObjectHolder("button_bush")
	public static final Block BUTTON_BUSH = null;

	//@ObjectHolder("cardinal_flower")
	public static final BlockDoublePlantBL CARDINAL_FLOWER = null;

	//@ObjectHolder("cattail")
	public static final Block CATTAIL = null;

	//@ObjectHolder("cave_grass")
	public static final Block CAVE_GRASS = null;

	//@ObjectHolder("copper_iris")
	public static final Block COPPER_IRIS = null;

	//@ObjectHolder("marsh_hibiscus")
	public static final Block MARSH_HIBISCUS = null;

	//@ObjectHolder("marsh_mallow")
	public static final Block MARSH_MALLOW = null;

	//@ObjectHolder("bladderwort_flower")
	public static final Block BLADDERWORT_FLOWER = null;

	//@ObjectHolder("bladderwort_stalk")
	public static final Block BLADDERWORT_STALK = null;

	//@ObjectHolder("bog_bean_flower")
	public static final Block BOG_BEAN_FLOWER = null;

	//@ObjectHolder("bog_bean_stalk")
	public static final Block BOG_BEAN_STALK = null;

	//@ObjectHolder("golden_club_flower")
	public static final Block GOLDEN_CLUB_FLOWER = null;

	//@ObjectHolder("golden_club_stalk")
	public static final Block GOLDEN_CLUB_STALK = null;

	//@ObjectHolder("marsh_marigold_flower")
	public static final Block MARSH_MARIGOLD_FLOWER = null;

	//@ObjectHolder("marsh_marigold_stalk")
	public static final Block MARSH_MARIGOLD_STALK = null;

	//@ObjectHolder("swamp_double_tallgrass")
	public static final BlockDoublePlantBL SWAMP_DOUBLE_TALLGRASS = null;

	//@ObjectHolder("milkweed")
	public static final Block MILKWEED = null;

	//@ObjectHolder("nettle")
	public static final Block NETTLE = null;

	//@ObjectHolder("nettle_flowered")
	public static final Block NETTLE_FLOWERED = null;

	//@ObjectHolder("pickerel_weed")
	public static final Block PICKEREL_WEED = null;

	//@ObjectHolder("phragmites")
	public static final BlockDoublePlantBL PHRAGMITES = null;

	//@ObjectHolder("shoots")
	public static final Block SHOOTS = null;

	//@ObjectHolder("sludgecreep")
	public static final Block SLUDGECREEP = null;

	//@ObjectHolder("soft_rush")
	public static final Block SOFT_RUSH = null;

	//@ObjectHolder("swamp_reed")
	public static final Block SWAMP_REED = null;

	//@ObjectHolder("swamp_reed_underwater")
	public static final Block SWAMP_REED_UNDERWATER = null;

	//@ObjectHolder("thorns")
	public static final Block THORNS = null;

	//@ObjectHolder("tall_cattail")
	public static final BlockDoublePlantBL TALL_CATTAIL = null;

	//@ObjectHolder("swamp_tallgrass")
	public static final Block SWAMP_TALLGRASS = null;

	//@ObjectHolder("dead_weedwood_bush")
	public static final Block DEAD_WEEDWOOD_BUSH = null;

	//@ObjectHolder("weedwood_bush")
	public static final Block WEEDWOOD_BUSH = null;

	//@ObjectHolder("hollow_log")
	public static final Block HOLLOW_LOG = null;

	//@ObjectHolder("cave_moss")
	public static final Block CAVE_MOSS = null;

	//@ObjectHolder("moss")
	public static final Block MOSS = null;

	//@ObjectHolder("lichen")
	public static final Block LICHEN = null;

	//@ObjectHolder("hanger")
	public static final Block HANGER = null;

	//@ObjectHolder("middle_fruit_bush")
	public static final Block MIDDLE_FRUIT_BUSH = null;

	//@ObjectHolder("fungus_crop")
	public static final Block FUNGUS_CROP = null;

	//@ObjectHolder("aspectrus_crop")
	public static final BlockAspectrusCrop ASPECTRUS_CROP = null;

	//@ObjectHolder("purified_swamp_dirt")
	public static final Block PURIFIED_SWAMP_DIRT = null;

	//@ObjectHolder("dug_swamp_dirt")
	public static final Block DUG_SWAMP_DIRT = null;

	//@ObjectHolder("dug_purified_swamp_dirt")
	public static final Block DUG_PURIFIED_SWAMP_DIRT = null;

	//@ObjectHolder("dug_swamp_grass")
	public static final Block DUG_SWAMP_GRASS = null;

	//@ObjectHolder("dug_purified_swamp_grass")
	public static final Block DUG_PURIFIED_SWAMP_GRASS = null;

	//@ObjectHolder("black_ice")
	public static final Block BLACK_ICE = null;

	//@ObjectHolder("snow")
	public static final Block SNOW = null;

	//@ObjectHolder("log_portal")
	public static final Block LOG_PORTAL = null;

	//@ObjectHolder("tree_portal")
	public static final Block TREE_PORTAL = null;

	//@ObjectHolder("portal_frame")
	public static final Block PORTAL_FRAME = null;

	//@ObjectHolder("druid_altar")
	public static final Block DRUID_ALTAR = null;

	//@ObjectHolder("purifier")
	public static final Block PURIFIER = null;

	//@ObjectHolder("weedwood_workbench")
	public static final Block WEEDWOOD_WORKBENCH = null;

	//@ObjectHolder("compost_bin")
	public static final Block COMPOST_BIN = null;

	//@ObjectHolder("weedwood_jukebox")
	public static final Block WEEDWOOD_JUKEBOX = null;

	//@ObjectHolder("sulfur_furnace")
	public static final Block SULFUR_FURNACE = null;

	//@ObjectHolder("sulfur_furnace_active")
	public static final Block SULFUR_FURNACE_ACTIVE = null;

	//@ObjectHolder("sulfur_furnace_dual")
	public static final Block SULFUR_FURNACE_DUAL = null;

	//@ObjectHolder("sulfur_furnace_dual_active")
	public static final Block SULFUR_FURNACE_DUAL_ACTIVE = null;

	//@ObjectHolder("weedwood_chest")
	public static final Block WEEDWOOD_CHEST = null;

	//@ObjectHolder("weedwood_rubber_tap")
	public static final Block WEEDWOOD_RUBBER_TAP = null;

	//@ObjectHolder("syrmorite_rubber_tap")
	public static final Block SYRMORITE_RUBBER_TAP = null;

	//@ObjectHolder("sludge")
	public static final BlockSludge SLUDGE = null;

	//@ObjectHolder("fallen_leaves")
	public static final Block FALLEN_LEAVES = null;

	//@ObjectHolder("energy_barrier")
	public static final Block ENERGY_BARRIER = null;

	//@ObjectHolder("weedwood_door")
	public static final Block WEEDWOOD_DOOR = null;

	//@ObjectHolder("rubber_tree_plank_door")
	public static final Block RUBBER_TREE_PLANK_DOOR = null;

	//@ObjectHolder("giant_root_plank_door")
	public static final Block GIANT_ROOT_PLANK_DOOR = null;

	//@ObjectHolder("hearthgrove_plank_door")
	public static final Block HEARTHGROVE_PLANK_DOOR = null;

	//@ObjectHolder("nibbletwig_plank_door")
	public static final Block NIBBLETWIG_PLANK_DOOR = null;

	//@ObjectHolder("syrmorite_door")
	public static final Block SYRMORITE_DOOR = null;

	//@ObjectHolder("scabyst_door")
	public static final Block SCABYST_DOOR = null;

	//@ObjectHolder("standing_weedwood_sign")
	public static final Block STANDING_WEEDWOOD_SIGN = null;

	//@ObjectHolder("wall_weedwood_sign")
	public static final Block WALL_WEEDWOOD_SIGN = null;

	//@ObjectHolder("sulfur_torch")
	public static final Block SULFUR_TORCH = null;

	//@ObjectHolder("weedwood_trapdoor")
	public static final Block WEEDWOOD_TRAPDOOR = null;

	//@ObjectHolder("rubber_tree_plank_trapdoor")
	public static final Block RUBBER_TREE_PLANK_TRAPDOOR = null;

	//@ObjectHolder("syrmorite_trapdoor")
	public static final Block SYRMORITE_TRAPDOOR = null;

	//@ObjectHolder("giant_root_plank_trapdoor")
	public static final Block GIANT_ROOT_PLANK_TRAPDOOR = null;

	//@ObjectHolder("hearthgrove_plank_trapdoor")
	public static final Block HEARTHGROVE_PLANK_TRAPDOOR = null;

	//@ObjectHolder("nibbletwig_plank_trapdoor")
	public static final Block NIBBLETWIG_PLANK_TRAPDOOR = null;

	//@ObjectHolder("scabyst_trapdoor")
	public static final Block SCABYST_TRAPDOOR = null;

	//@ObjectHolder("syrmorite_hopper")
	public static final Block SYRMORITE_HOPPER = null;

	//@ObjectHolder("mud_flower_pot")
	public static final Block MUD_FLOWER_POT = null;

	//@ObjectHolder("mud_flower_pot_candle")
	public static final Block MUD_FLOWER_POT_CANDLE = null;

	//@ObjectHolder("gecko_cage")
	public static final Block GECKO_CAGE = null;

	//@ObjectHolder("infuser")
	public static final Block INFUSER = null;

	//@ObjectHolder("aspect_vial_block")
	public static final Block ASPECT_VIAL_BLOCK = null;

	//@ObjectHolder("mortar")
	public static final Block MORTAR = null;

	//@ObjectHolder("animator")
	public static final Block ANIMATOR = null;

	//@ObjectHolder("alembic")
	public static final Block ALEMBIC = null;

	//@ObjectHolder("moss_bed")
	public static final Block MOSS_BED = null;

	//@ObjectHolder("rope")
	public static final Block ROPE = null;

	//@ObjectHolder("damp_torch")
	public static final Block DAMP_TORCH = null;

	//@ObjectHolder("walkway")
	public static final Block WALKWAY = null;

	//@ObjectHolder("wood_chip_path")
	public static final Block WOOD_CHIP_PATH = null;

	//@ObjectHolder("thatch_roof")
	public static final Block THATCH_ROOF = null;

	//@ObjectHolder("mud_brick_roof")
	public static final Block MUD_BRICK_ROOF = null;

	//@ObjectHolder("repeller")
	public static final Block REPELLER = null;

	//@ObjectHolder("waystone")
	public static final Block WAYSTONE = null;

	//@ObjectHolder("caving_rope_light")
	public static final Block CAVING_ROPE_LIGHT = null;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();

		register(new RegistryHelper<Block>() {
			@Override
			public <F extends Block> F reg(String regName, F obj, @Nullable  Consumer<F> callback) {
				obj.setRegistryName(ModInfo.ID, regName);
				registry.register(obj);
				if(callback != null) callback.accept(obj);
				return obj;
			}
		});
	}

	private static void register(RegistryHelper<Block> reg) {
		//TODO 1.13 Register blocks here
		//The registry helper has  an optional callback, e.g.
		//reg.reg("regName", new SomeBlock(), b -> {
		//   b.setSomething(...);
		//});
		//which can be used to set some values

		reg.reg("betweenstone", new Block(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5F, 10.0F)), ItemRegistry.block());
		reg.reg("druid_stone_1", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_2", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_3", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_4", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_5", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_6", new BlockDruidStone(false), ItemRegistry.block());
		reg.reg("druid_stone_1_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("druid_stone_2_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("druid_stone_3_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("druid_stone_4_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("druid_stone_5_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("druid_stone_6_active", new BlockDruidStone(true), ItemRegistry.block());
		reg.reg("mud", new BlockMud(), ItemRegistry.block());
	}
}

