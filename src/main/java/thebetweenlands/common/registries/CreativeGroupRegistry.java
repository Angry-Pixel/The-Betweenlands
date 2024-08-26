package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

//TODO since we have full control of tab placement consider reorganizing the contents. Theyre currently the same as they were in 1.12
public class CreativeGroupRegistry {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheBetweenlands.ID);

	// Blocks tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_BLOCKS = CREATIVE_TABS.register("blocks", CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.thebetweenlands.blocks"))
		.icon(() -> new ItemStack(BlockRegistry.SWAMP_GRASS))
		.displayItems((parameters, output) -> {
			output.accept(BlockRegistry.DRUID_STONE_1);
			output.accept(BlockRegistry.DRUID_STONE_2);
			output.accept(BlockRegistry.DRUID_STONE_3);
			output.accept(BlockRegistry.DRUID_STONE_4);
			output.accept(BlockRegistry.DRUID_STONE_5);
			output.accept(BlockRegistry.DRUID_STONE_6);
			output.accept(BlockRegistry.BETWEENLANDS_BEDROCK);
			output.accept(BlockRegistry.BETWEENSTONE);
			output.accept(BlockRegistry.CORRUPT_BETWEENSTONE);
			output.accept(BlockRegistry.MUD);
			output.accept(BlockRegistry.PEAT);
			output.accept(BlockRegistry.SMOULDERING_PEAT);
			output.accept(BlockRegistry.SLUDGY_DIRT);
//			output.accept(BlockRegistry.SPREADING_SLUDGY_DIRT);
			output.accept(BlockRegistry.SLIMY_DIRT);
			output.accept(BlockRegistry.SLIMY_GRASS);
			output.accept(BlockRegistry.CRAGROCK);
			output.accept(BlockRegistry.MOSSY_CRAGROCK_TOP);
			output.accept(BlockRegistry.MOSSY_CRAGROCK_BOTTOM);
			output.accept(BlockRegistry.PITSTONE);
			output.accept(BlockRegistry.LIMESTONE);
			output.accept(BlockRegistry.SWAMP_DIRT);
			output.accept(BlockRegistry.COARSE_SWAMP_DIRT);
			output.accept(BlockRegistry.SWAMP_GRASS);
			output.accept(BlockRegistry.WISP);
			output.accept(BlockRegistry.OCTINE_ORE);
			output.accept(BlockRegistry.VALONITE_ORE);
			output.accept(BlockRegistry.SULFUR_ORE);
			output.accept(BlockRegistry.SLIMY_BONE_ORE);
			output.accept(BlockRegistry.SCABYST_ORE);
			output.accept(BlockRegistry.SYRMORITE_ORE);
			output.accept(BlockRegistry.AQUA_MIDDLE_GEM_ORE);
			output.accept(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE);
			output.accept(BlockRegistry.GREEN_MIDDLE_GEM_ORE);
			output.accept(BlockRegistry.LIFE_CRYSTAL_STALACTITE);
			output.accept(BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE);
			output.accept(BlockRegistry.STALACTITE);
			output.accept(BlockRegistry.SILT);
			output.accept(BlockRegistry.FILTERED_SILT);
			output.accept(BlockRegistry.DEAD_GRASS);
			output.accept(BlockRegistry.SOLID_TAR);
			output.accept(BlockRegistry.PEARL_BLOCK);
			output.accept(BlockRegistry.ANCIENT_REMNANT_BLOCK);
			output.accept(BlockRegistry.WEEDWOOD_LOG);
			output.accept(BlockRegistry.WEEDWOOD_BARK);
			output.accept(BlockRegistry.ROTTEN_BARK);
//			output.accept(BlockRegistry.SPREADING_ROTTEN_BARK);
			output.accept(BlockRegistry.RUBBER_LOG);
			output.accept(BlockRegistry.HEARTHGROVE_LOG);
			output.accept(BlockRegistry.TARRED_HEARTHGROVE_LOG);
			output.accept(BlockRegistry.HEARTHGROVE_BARK);
			output.accept(BlockRegistry.TARRED_HEARTHGROVE_BARK);
			output.accept(BlockRegistry.NIBBLETWIG_LOG);
			output.accept(BlockRegistry.NIBBLETWIG_BARK);
			output.accept(BlockRegistry.SPIRIT_TREE_LOG);
			output.accept(BlockRegistry.SPIRIT_TREE_BARK);
			output.accept(BlockRegistry.WEEDWOOD);
			output.accept(BlockRegistry.SAP_LOG);
			output.accept(BlockRegistry.SAP_BARK);
			output.accept(BlockRegistry.WEEDWOOD_LEAVES);
			output.accept(BlockRegistry.SAP_LEAVES);
			output.accept(BlockRegistry.RUBBER_TREE_LEAVES);
			output.accept(BlockRegistry.HEARTHGROVE_LEAVES);
			output.accept(BlockRegistry.NIBBLETWIG_LEAVES);
			output.accept(BlockRegistry.TOP_SPIRIT_TREE_LEAVES);
			output.accept(BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES);
			output.accept(BlockRegistry.BOTTOM_SPIRIT_TREE_LEAVES);
			output.accept(BlockRegistry.WEEDWOOD_PLANKS);
			output.accept(BlockRegistry.RUBBER_TREE_PLANKS);
			output.accept(BlockRegistry.GIANT_ROOT_PLANKS);
			output.accept(BlockRegistry.HEARTHGROVE_PLANKS);
			output.accept(BlockRegistry.NIBBLETWIG_PLANKS);
			output.accept(BlockRegistry.ANGRY_BETWEENSTONE);
			output.accept(BlockRegistry.BETWEENSTONE_BRICKS);
			output.accept(BlockRegistry.MIRAGE_BETWEENSTONE_BRICKS);
			output.accept(BlockRegistry.BETWEENSTONE_TILES);
			output.accept(BlockRegistry.CHISELED_BETWEENSTONE);
			output.accept(BlockRegistry.CHISELED_CRAGROCK);
			output.accept(BlockRegistry.CRACKED_CHISELED_CRAGROCK);
			output.accept(BlockRegistry.MOSSY_CHISELED_CRAGROCK);
			output.accept(BlockRegistry.CHISELED_LIMESTONE);
			output.accept(BlockRegistry.CHISELED_PITSTONE);
			output.accept(BlockRegistry.CHISELED_SCABYST_1);
			output.accept(BlockRegistry.CHISELED_SCABYST_2);
			output.accept(BlockRegistry.CHISELED_SCABYST_3);
			output.accept(BlockRegistry.DOTTED_SCABYST_PITSTONE);
			output.accept(BlockRegistry.HORIZONTAL_SCABYST_PITSTONE);
			output.accept(BlockRegistry.SCABYST_BRICKS);
			output.accept(BlockRegistry.CRACKED_BETWEENSTONE_BRICKS);
			output.accept(BlockRegistry.CRACKED_BETWEENSTONE_TILES);
			output.accept(BlockRegistry.CRACKED_LIMESTONE_BRICKS);
			output.accept(BlockRegistry.CRAGROCK_BRICKS);
			output.accept(BlockRegistry.CRACKED_CRAGROCK_BRICKS);
			output.accept(BlockRegistry.MOSSY_CRAGROCK_BRICKS);
			output.accept(BlockRegistry.CRAGROCK_TILES);
			output.accept(BlockRegistry.CRACKED_CRAGROCK_TILES);
			output.accept(BlockRegistry.MOSSY_CRAGROCK_TILES);
			output.accept(BlockRegistry.MUD_BRICKS);
			output.accept(BlockRegistry.MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.RUBBER_BLOCK);
			output.accept(BlockRegistry.PITSTONE_BRICKS);
			output.accept(BlockRegistry.PITSTONE_TILES);
			output.accept(BlockRegistry.POLISHED_LIMESTONE);
			output.accept(BlockRegistry.SMOOTH_BETWEENSTONE);
			output.accept(BlockRegistry.SMOOTH_CRAGROCK);
			output.accept(BlockRegistry.OCTINE_BLOCK);
			output.accept(BlockRegistry.SYRMORITE_BLOCK);
			output.accept(BlockRegistry.VALONITE_BLOCK);
			output.accept(BlockRegistry.SCABYST_BLOCK);
			output.accept(BlockRegistry.WEAK_BETWEENSTONE_TILES);
			output.accept(BlockRegistry.WEAK_POLISHED_LIMESTONE);
			output.accept(BlockRegistry.WEAK_MOSSY_BETWEENSTONE_TILES);
			output.accept(BlockRegistry.WEAK_SMOOTH_CRAGROCK);
			output.accept(BlockRegistry.GREEN_DENTROTHYST);
			output.accept(BlockRegistry.ORANGE_DENTROTHYST);
			output.accept(BlockRegistry.LOOT_POT_1);
			output.accept(BlockRegistry.LOOT_POT_2);
			output.accept(BlockRegistry.LOOT_POT_3);
			output.accept(BlockRegistry.MOB_SPAWNER);
			output.accept(BlockRegistry.TEMPLE_PILLAR);
			output.accept(BlockRegistry.BETWEENSTONE_PILLAR);
			output.accept(BlockRegistry.PITSTONE_PILLAR);
			output.accept(BlockRegistry.LIMESTONE_PILLAR);
			output.accept(BlockRegistry.CRAGROCK_PILLAR);
//			output.accept(BlockRegistry.TAR_BEAST_SPAWNER);
			output.accept(BlockRegistry.TAR_LOOT_POT_1);
			output.accept(BlockRegistry.TAR_LOOT_POT_2);
			output.accept(BlockRegistry.TAR_LOOT_POT_3);
			output.accept(BlockRegistry.CRAGROCK_STAIRS);
			output.accept(BlockRegistry.PITSTONE_STAIRS);
			output.accept(BlockRegistry.BETWEENSTONE_STAIRS);
			output.accept(BlockRegistry.BETWEENSTONE_BRICK_STAIRS);
			output.accept(BlockRegistry.MUD_BRICK_STAIRS);
			output.accept(BlockRegistry.CRAGROCK_BRICK_STAIRS);
			output.accept(BlockRegistry.LIMESTONE_BRICK_STAIRS);
			output.accept(BlockRegistry.PITSTONE_BRICK_STAIRS);
			output.accept(BlockRegistry.LIMESTONE_STAIRS);
			output.accept(BlockRegistry.SMOOTH_BETWEENSTONE_STAIRS);
			output.accept(BlockRegistry.SMOOTH_CRAGROCK_STAIRS);
			output.accept(BlockRegistry.POLISHED_LIMESTONE_STAIRS);
			output.accept(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_STAIRS);
			output.accept(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_STAIRS);
			output.accept(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_STAIRS);
			output.accept(BlockRegistry.SCABYST_BRICK_STAIRS);
			output.accept(BlockRegistry.SULFUR_BLOCK);
			output.accept(BlockRegistry.TEMPLE_BRICKS);
			output.accept(BlockRegistry.SMOOTH_PITSTONE);
			output.accept(BlockRegistry.MIRE_CORAL_BLOCK);
			output.accept(BlockRegistry.DEEP_WATER_CORAL_BLOCK);
			output.accept(BlockRegistry.SLIMY_BONE_BLOCK);
			output.accept(BlockRegistry.AQUA_MIDDLE_GEM_BLOCK);
			output.accept(BlockRegistry.CRIMSON_MIDDLE_GEM_BLOCK);
			output.accept(BlockRegistry.GREEN_MIDDLE_GEM_BLOCK);
			output.accept(BlockRegistry.COMPOST_BLOCK);
			output.accept(BlockRegistry.POLISHED_GREEN_DENTROTHYST);
			output.accept(BlockRegistry.POLISHED_ORANGE_DENTROTHYST);
			output.accept(BlockRegistry.SILT_GLASS);
			output.accept(BlockRegistry.SILT_GLASS_PANE);
			output.accept(BlockRegistry.LATTICE);
			output.accept(BlockRegistry.FINE_LATTICE);
			output.accept(BlockRegistry.FILTERED_SILT_GLASS);
			output.accept(BlockRegistry.FILTERED_SILT_GLASS_PANE);
			output.accept(BlockRegistry.POLISHED_GREEN_DENTROTHYST_PANE);
			output.accept(BlockRegistry.POLISHED_ORANGE_DENTROTHYST_PANE);
			output.accept(BlockRegistry.CONNECTED_AMATE_PAPER_PANE);
			output.accept(BlockRegistry.SQUARED_AMATE_PAPER_PANE);
			output.accept(BlockRegistry.ROUNDED_AMATE_PAPER_PANE);
			output.accept(BlockRegistry.SMOOTH_PITSTONE_STAIRS);
			output.accept(BlockRegistry.SOLID_TAR_STAIRS);
			output.accept(BlockRegistry.TEMPLE_BRICK_STAIRS);
			output.accept(BlockRegistry.SPIKE_TRAP);
			output.accept(BlockRegistry.WEEDWOOD_STAIRS);
			output.accept(BlockRegistry.RUBBER_TREE_STAIRS);
			output.accept(BlockRegistry.GIANT_ROOT_STAIRS);
			output.accept(BlockRegistry.HEARTHGROVE_STAIRS);
			output.accept(BlockRegistry.NIBBLETWIG_STAIRS);
			output.accept(BlockRegistry.POSSESSED_BLOCK);
			output.accept(BlockRegistry.ITEM_CAGE);
			output.accept(BlockRegistry.ITEM_SHELF);
			output.accept(BlockRegistry.THATCH);
			output.accept(BlockRegistry.CRAGROCK_SLAB);
			output.accept(BlockRegistry.PITSTONE_SLAB);
			output.accept(BlockRegistry.BETWEENSTONE_SLAB);
			output.accept(BlockRegistry.SMOOTH_PITSTONE_SLAB);
			output.accept(BlockRegistry.SOLID_TAR_SLAB);
			output.accept(BlockRegistry.TEMPLE_BRICK_SLAB);
			output.accept(BlockRegistry.BETWEENSTONE_BRICK_SLAB);
			output.accept(BlockRegistry.MUD_BRICK_SLAB);
			output.accept(BlockRegistry.CRAGROCK_BRICK_SLAB);
			output.accept(BlockRegistry.LIMESTONE_BRICK_SLAB);
			output.accept(BlockRegistry.LIMESTONE_SLAB);
			output.accept(BlockRegistry.SMOOTH_BETWEENSTONE_SLAB);
			output.accept(BlockRegistry.SMOOTH_CRAGROCK_SLAB);
			output.accept(BlockRegistry.POLISHED_LIMESTONE_SLAB);
			output.accept(BlockRegistry.PITSTONE_BRICK_SLAB);
			output.accept(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_SLAB);
			output.accept(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_SLAB);
			output.accept(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_SLAB);
			output.accept(BlockRegistry.WEEDWOOD_SLAB);
			output.accept(BlockRegistry.RUBBER_TREE_SLAB);
			output.accept(BlockRegistry.GIANT_ROOT_SLAB);
			output.accept(BlockRegistry.HEARTHGROVE_SLAB);
			output.accept(BlockRegistry.NIBBLETWIG_SLAB);
			output.accept(BlockRegistry.MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.MUD_BRICK_SHINGLE_WALL);
			output.accept(BlockRegistry.DULL_LAVENDER_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.MAROON_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.SHADOW_GREEN_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.SAFFRON_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.VIVID_TANGERINE_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.CHAMPAGNE_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.RAISIN_BLACK_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.SUSHI_GREEN_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.ELM_CYAN_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.CADMIUM_GREEN_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.LAVENDER_BLUE_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.BROWN_RUST_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.PEWTER_GREY_FILTERED_SILT_GLASS_BLOCK);
			output.accept(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.MAROON_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLES);
			output.accept(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.MAROON_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_SLAB);
			output.accept(BlockRegistry.DULL_LAVENDER_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.MAROON_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.SHADOW_GREEN_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.SAFFRON_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.VIVID_TANGERINE_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.CHAMPAGNE_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.RAISIN_BLACK_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.SUSHI_GREEN_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.ELM_CYAN_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.CADMIUM_GREEN_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.LAVENDER_BLUE_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.BROWN_RUST_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.PEWTER_GREY_MUD_BRICK_SHINGLE_STAIRS);
			output.accept(BlockRegistry.THATCH_SLAB);
			output.accept(BlockRegistry.SCABYST_BRICK_SLAB);
			output.accept(BlockRegistry.PITSTONE_WALL);
			output.accept(BlockRegistry.BETWEENSTONE_WALL);
			output.accept(BlockRegistry.SOLID_TAR_WALL);
			output.accept(BlockRegistry.TEMPLE_BRICK_WALL);
			output.accept(BlockRegistry.SMOOTH_PITSTONE_WALL);
			output.accept(BlockRegistry.BETWEENSTONE_BRICK_WALL);
			output.accept(BlockRegistry.MUD_BRICK_WALL);
			output.accept(BlockRegistry.CRAGROCK_WALL);
			output.accept(BlockRegistry.CRAGROCK_BRICK_WALL);
			output.accept(BlockRegistry.LIMESTONE_BRICK_WALL);
			output.accept(BlockRegistry.LIMESTONE_WALL);
			output.accept(BlockRegistry.POLISHED_LIMESTONE_WALL);
			output.accept(BlockRegistry.PITSTONE_BRICK_WALL);
			output.accept(BlockRegistry.SMOOTH_BETWEENSTONE_WALL);
			output.accept(BlockRegistry.SMOOTH_CRAGROCK_WALL);
			output.accept(BlockRegistry.MOSSY_BETWEENSTONE_BRICK_WALL);
			output.accept(BlockRegistry.MOSSY_SMOOTH_BETWEENSTONE_WALL);
			output.accept(BlockRegistry.CRACKED_BETWEENSTONE_BRICK_WALL);
			output.accept(BlockRegistry.SCABYST_BRICK_WALL);
			output.accept(BlockRegistry.WEEDWOOD_FENCE);
			output.accept(BlockRegistry.WEEDWOOD_LOG_FENCE);
			output.accept(BlockRegistry.RUBBER_TREE_FENCE);
			output.accept(BlockRegistry.GIANT_ROOT_FENCE);
			output.accept(BlockRegistry.HEARTHGROVE_FENCE);
			output.accept(BlockRegistry.NIBBLETWIG_FENCE);
			output.accept(BlockRegistry.WEEDWOOD_FENCE_GATE);
			output.accept(BlockRegistry.WEEDWOOD_LOG_FENCE_GATE);
			output.accept(BlockRegistry.RUBBER_TREE_FENCE_GATE);
			output.accept(BlockRegistry.GIANT_ROOT_FENCE_GATE);
			output.accept(BlockRegistry.HEARTHGROVE_FENCE_GATE);
			output.accept(BlockRegistry.NIBBLETWIG_FENCE_GATE);
			output.accept(BlockRegistry.WEEDWOOD_PRESSURE_PLATE);
			output.accept(BlockRegistry.BETWEENSTONE_PRESSURE_PLATE);
			output.accept(BlockRegistry.SYRMORITE_PRESSURE_PLATE);
			output.accept(BlockRegistry.WEEDWOOD_BUTTON);
			output.accept(BlockRegistry.BETWEENSTONE_BUTTON);
			output.accept(BlockRegistry.WEEDWOOD_LADDER);
			output.accept(BlockRegistry.WEEDWOOD_LEVER);
			output.accept(BlockRegistry.MUD_LOOT_POT_1);
			output.accept(BlockRegistry.MUD_LOOT_POT_2);
			output.accept(BlockRegistry.MUD_LOOT_POT_3);
			output.accept(BlockRegistry.WORM_PILLAR);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_1);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_2);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_3);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_4);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_5);
			output.accept(BlockRegistry.WORM_PILLAR_TOP);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_1);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_2);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_3);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_4);
			output.accept(BlockRegistry.SLUDGY_WORM_PILLAR_TOP_5);
			output.accept(BlockRegistry.COMPACTED_MUD);
			output.accept(BlockRegistry.MUD_TILES);
			output.accept(BlockRegistry.DECAYED_MUD_TILES);
			output.accept(BlockRegistry.CRACKED_MUD_TILES);
			output.accept(BlockRegistry.CRACKED_DECAYED_MUD_TILES);
			output.accept(BlockRegistry.PUFFSHROOM);
			output.accept(BlockRegistry.CARVED_MUD_BRICKS);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_1);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_2);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_3);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_4);
			output.accept(BlockRegistry.CARVED_MUD_BRICK_EDGE);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_1);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_2);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_3);
			output.accept(BlockRegistry.SLUDGY_CARVED_MUD_BRICK_EDGE_4);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICKS_1);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICKS_2);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICKS_3);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICKS_4);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_1);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_2);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_3);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_STAIRS_4);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_1);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_2);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_3);
			output.accept(BlockRegistry.SLUDGY_MUD_BRICK_SLAB_4);
			output.accept(BlockRegistry.BEAM_RELAY);
			output.accept(BlockRegistry.BEAM_TUBE);
			output.accept(BlockRegistry.BEAM_LENS_SUPPORT);
			output.accept(BlockRegistry.MUD_BRICK_ALCOVE);
			output.accept(BlockRegistry.LOOT_URN_1);
			output.accept(BlockRegistry.LOOT_URN_2);
			output.accept(BlockRegistry.LOOT_URN_3);
			output.accept(BlockRegistry.DUNGEON_DOOR_RUNES);
			output.accept(BlockRegistry.MIMIC_DUNGEON_DOOR_RUNES);
			output.accept(BlockRegistry.CRAWLER_DUNGEON_DOOR_RUNES);
			output.accept(BlockRegistry.DUNGEON_DOOR_COMBINATION);
			output.accept(BlockRegistry.CLIMBABLE_MUD_BRICKS);
			output.accept(BlockRegistry.BROKEN_MUD_TILES);
			output.accept(BlockRegistry.DUNGEON_WALL_CANDLE);
			output.accept(BlockRegistry.WOODEN_SUPPORT_BEAM_1);
			output.accept(BlockRegistry.WOODEN_SUPPORT_BEAM_2);
			output.accept(BlockRegistry.WOODEN_SUPPORT_BEAM_3);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_1);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_2);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_3);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_4);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_5);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_6);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_7);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_8);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_9);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_10);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_11);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_12);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_13);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_14);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_15);
			output.accept(BlockRegistry.CARVED_ROTTEN_BARK_16);
			output.accept(BlockRegistry.MUD_BRICK_SPIKE_TRAP);
			output.accept(BlockRegistry.MUD_TILES_SPIKE_TRAP);
			output.accept(BlockRegistry.COMPACTED_MUD_SLOPE);
			output.accept(BlockRegistry.COMPACTED_MUD_SLAB);
			output.accept(BlockRegistry.COMPACTED_MUD_MIRAGE);
			output.accept(BlockRegistry.ROTTEN_PLANKS);
			output.accept(BlockRegistry.ROTTEN_SLAB);
			output.accept(BlockRegistry.ROTTEN_STAIRS);
			output.accept(BlockRegistry.ROTTEN_FENCE);
			output.accept(BlockRegistry.ROTTEN_FENCE_GATE);
			output.accept(BlockRegistry.BRAZIER);
			output.accept(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP);
			output.accept(BlockRegistry.BULB_CAPPED_MUSHROOM_STALK);
			output.accept(BlockRegistry.SHELF_FUNGUS);
			output.accept(BlockRegistry.ROOT);
			output.accept(BlockRegistry.GIANT_ROOT);
			output.accept(BlockRegistry.HOLLOW_LOG);
			output.accept(BlockRegistry.PURIFIED_SWAMP_DIRT);
			output.accept(BlockRegistry.DUG_SWAMP_DIRT);
			output.accept(BlockRegistry.PURIFIED_DUG_SWAMP_DIRT);
			output.accept(BlockRegistry.DUG_SWAMP_GRASS);
			output.accept(BlockRegistry.PURIFIED_DUG_SWAMP_GRASS);
			output.accept(BlockRegistry.BLACK_ICE);
			output.accept(BlockRegistry.SNOW);
			output.accept(BlockRegistry.PORTAL_LOG);
			output.accept(BlockRegistry.PORTAL_FRAME_TOP_LEFT);
			output.accept(BlockRegistry.PORTAL_FRAME_TOP);
			output.accept(BlockRegistry.PORTAL_FRAME_TOP_RIGHT);
			output.accept(BlockRegistry.PORTAL_FRAME_LEFT);
			output.accept(BlockRegistry.PORTAL_FRAME_RIGHT);
			output.accept(BlockRegistry.PORTAL_FRAME_BOTTOM_LEFT);
			output.accept(BlockRegistry.PORTAL_FRAME_BOTTOM);
			output.accept(BlockRegistry.PORTAL_FRAME_BOTTOM_RIGHT);
			output.accept(BlockRegistry.DRUID_ALTAR);
			output.accept(BlockRegistry.PURIFIER);
			output.accept(BlockRegistry.WEEDWOOD_CRAFTING_TABLE);
			output.accept(BlockRegistry.COMPOST_BIN);
			output.accept(BlockRegistry.WEEDWOOD_JUKEBOX);
			output.accept(BlockRegistry.SULFUR_FURNACE);
			output.accept(BlockRegistry.DUAL_SULFUR_FURNACE);
//			output.accept(BlockRegistry.WEEDWOOD_CHEST);
			output.accept(BlockRegistry.SLUDGE);
			output.accept(BlockRegistry.SULFUR_TORCH);
			output.accept(BlockRegistry.EXTINGUISHED_SULFUR_TORCH);
			output.accept(BlockRegistry.WEEDWOOD_TRAPDOOR);
			output.accept(BlockRegistry.RUBBER_TREE_TRAPDOOR);
			output.accept(BlockRegistry.SYRMORITE_TRAPDOOR);
			output.accept(BlockRegistry.GIANT_ROOT_TRAPDOOR);
			output.accept(BlockRegistry.HEARTHGROVE_TRAPDOOR);
			output.accept(BlockRegistry.NIBBLETWIG_TRAPDOOR);
			output.accept(BlockRegistry.ROTTEN_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_TRAPDOOR);
			output.accept(BlockRegistry.TREATED_ROTTEN_TRAPDOOR);
			output.accept(BlockRegistry.SCABYST_TRAPDOOR);
			output.accept(BlockRegistry.SYRMORITE_HOPPER);
			output.accept(BlockRegistry.MUD_FLOWER_POT);
			output.accept(BlockRegistry.MUD_FLOWER_POT_CANDLE);
			output.accept(BlockRegistry.GECKO_CAGE);
			output.accept(BlockRegistry.INFUSER);
			output.accept(BlockRegistry.MORTAR);
			output.accept(BlockRegistry.CENSER);
			output.accept(BlockRegistry.WEEDWOOD_BARREL);
			output.accept(BlockRegistry.SYRMORITE_BARREL);
			output.accept(BlockRegistry.ANIMATOR);
			output.accept(BlockRegistry.ALEMBIC);
			output.accept(BlockRegistry.DAMP_TORCH);
			output.accept(BlockRegistry.WALKWAY);
			output.accept(BlockRegistry.CHIP_PATH);
			output.accept(BlockRegistry.THATCH_ROOF);
			output.accept(BlockRegistry.MUD_BRICK_SHINGLE_ROOF);
			output.accept(BlockRegistry.REPELLER);
			output.accept(BlockRegistry.WAYSTONE);
			output.accept(BlockRegistry.DEEPMAN_SIMULACRUM_1);
			output.accept(BlockRegistry.DEEPMAN_SIMULACRUM_2);
			output.accept(BlockRegistry.DEEPMAN_SIMULACRUM_3);
			output.accept(BlockRegistry.LAKE_CAVERN_SIMULACRUM_1);
			output.accept(BlockRegistry.LAKE_CAVERN_SIMULACRUM_2);
			output.accept(BlockRegistry.LAKE_CAVERN_SIMULACRUM_3);
			output.accept(BlockRegistry.ROOTMAN_SIMULACRUM_1);
			output.accept(BlockRegistry.ROOTMAN_SIMULACRUM_2);
			output.accept(BlockRegistry.ROOTMAN_SIMULACRUM_3);
			output.accept(BlockRegistry.OFFERING_TABLE);
			output.accept(BlockRegistry.WIND_CHIME);
			output.accept(BlockRegistry.PAPER_LANTERN_1);
			output.accept(BlockRegistry.PAPER_LANTERN_2);
			output.accept(BlockRegistry.PAPER_LANTERN_3);
			output.accept(BlockRegistry.SILT_GLASS_LANTERN);
			output.accept(BlockRegistry.FISHING_TACKLE_BOX);
			output.accept(BlockRegistry.SMOKING_RACK);
			output.accept(BlockRegistry.FISH_TRIMMING_TABLE);
			output.accept(BlockRegistry.CRAB_POT);
			output.accept(BlockRegistry.CRAB_POT_FILTER);
			output.accept(BlockRegistry.SILT_GLASS_JAR);
			output.accept(BlockRegistry.GLOWING_GOOP);
			output.accept(BlockRegistry.REED_MAT);
			output.accept(BlockRegistry.LYESTONE);
			output.accept(BlockRegistry.MIST_BRIDGE);
			output.accept(BlockRegistry.SHADOW_WALKER);
			output.accept(BlockRegistry.STEEPING_POT);
			output.accept(BlockRegistry.GRUB_HUB);
			output.accept(BlockRegistry.WATER_FILTER);
			output.accept(BlockRegistry.FILTERED_SILT_GLASS_JAR);
			output.accept(BlockRegistry.MOTH_HOUSE);
			output.accept(BlockRegistry.DULL_LAVENDER_SAMITE);
			output.accept(BlockRegistry.MAROON_SAMITE);
			output.accept(BlockRegistry.SHADOW_GREEN_SAMITE);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_SAMITE);
			output.accept(BlockRegistry.SAFFRON_SAMITE);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_SAMITE);
			output.accept(BlockRegistry.VIVID_TANGERINE_SAMITE);
			output.accept(BlockRegistry.CHAMPAGNE_SAMITE);
			output.accept(BlockRegistry.RAISIN_BLACK_SAMITE);
			output.accept(BlockRegistry.SUSHI_GREEN_SAMITE);
			output.accept(BlockRegistry.ELM_CYAN_SAMITE);
			output.accept(BlockRegistry.CADMIUM_GREEN_SAMITE);
			output.accept(BlockRegistry.LAVENDER_BLUE_SAMITE);
			output.accept(BlockRegistry.BROWN_RUST_SAMITE);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_SAMITE);
			output.accept(BlockRegistry.PEWTER_GREY_SAMITE);
			output.accept(BlockRegistry.DULL_LAVENDER_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.MAROON_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.SHADOW_GREEN_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.SAFFRON_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.VIVID_TANGERINE_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.CHAMPAGNE_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.RAISIN_BLACK_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.SUSHI_GREEN_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.ELM_CYAN_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.CADMIUM_GREEN_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.LAVENDER_BLUE_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.BROWN_RUST_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.PEWTER_GREY_SAMITE_CANVAS_PANEL);
			output.accept(BlockRegistry.DULL_LAVENDER_REED_MAT);
			output.accept(BlockRegistry.MAROON_REED_MAT);
			output.accept(BlockRegistry.SHADOW_GREEN_REED_MAT);
			output.accept(BlockRegistry.CAMELOT_MAGENTA_REED_MAT);
			output.accept(BlockRegistry.SAFFRON_REED_MAT);
			output.accept(BlockRegistry.CARIBBEAN_GREEN_REED_MAT);
			output.accept(BlockRegistry.VIVID_TANGERINE_REED_MAT);
			output.accept(BlockRegistry.CHAMPAGNE_REED_MAT);
			output.accept(BlockRegistry.RAISIN_BLACK_REED_MAT);
			output.accept(BlockRegistry.SUSHI_GREEN_REED_MAT);
			output.accept(BlockRegistry.ELM_CYAN_REED_MAT);
			output.accept(BlockRegistry.CADMIUM_GREEN_REED_MAT);
			output.accept(BlockRegistry.LAVENDER_BLUE_REED_MAT);
			output.accept(BlockRegistry.BROWN_RUST_REED_MAT);
			output.accept(BlockRegistry.MIDNIGHT_PURPLE_REED_MAT);
			output.accept(BlockRegistry.PEWTER_GREY_REED_MAT);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_PLANKS);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_SLAB);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_STAIRS);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_FENCE);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_FENCE_GATE);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_PLANKS);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_SLAB);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_STAIRS);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_FENCE);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_FENCE_GATE);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_PLANKS);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_SLAB);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_STAIRS);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_FENCE);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_FENCE_GATE);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_PLANKS);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_SLAB);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_STAIRS);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_FENCE);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_FENCE_GATE);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_PLANKS);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_SLAB);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_STAIRS);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_FENCE);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_FENCE_GATE);
			output.accept(BlockRegistry.TREATED_ROTTEN_PLANKS);
			output.accept(BlockRegistry.TREATED_ROTTEN_SLAB);
			output.accept(BlockRegistry.TREATED_ROTTEN_STAIRS);
			output.accept(BlockRegistry.TREATED_ROTTEN_FENCE);
			output.accept(BlockRegistry.TREATED_ROTTEN_FENCE_GATE);
			output.accept(BlockRegistry.WEEDWOOD_DOOR);
			output.accept(BlockRegistry.SYRMORITE_DOOR);
			output.accept(BlockRegistry.RUBBER_TREE_DOOR);
			output.accept(BlockRegistry.GIANT_ROOT_DOOR);
			output.accept(BlockRegistry.HEARTHGROVE_DOOR);
			output.accept(BlockRegistry.NIBBLETWIG_DOOR);
			output.accept(BlockRegistry.SCABYST_DOOR);
			output.accept(BlockRegistry.ROTTEN_DOOR);
			output.accept(BlockRegistry.TREATED_WEEDWOOD_DOOR);
			output.accept(BlockRegistry.TREATED_RUBBER_TREE_DOOR);
			output.accept(BlockRegistry.TREATED_GIANT_ROOT_DOOR);
			output.accept(BlockRegistry.TREATED_HEARTHGROVE_DOOR);
			output.accept(BlockRegistry.TREATED_NIBBLETWIG_DOOR);
			output.accept(BlockRegistry.TREATED_ROTTEN_DOOR);
			output.accept(BlockRegistry.WEEDWOOD_SIGN);
			output.accept(BlockRegistry.MOSS_BED);
		})::build);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_ITEMS = CREATIVE_TABS.register("items", () -> CreativeModeTab.builder()
		.icon(ItemRegistry.SWAMP_TALISMAN::toStack)
		.withTabsBefore(BETWEENLANDS_BLOCKS.getKey())
		.title(Component.translatable("itemGroup.thebetweenlands.items"))
		.displayItems((parameters, output) -> {
			//barnacle
			output.accept(ItemRegistry.CRIMSON_SNAIL_SHELL);
			output.accept(ItemRegistry.OCHRE_SNAIL_SHELL);
			output.accept(ItemRegistry.COMPOST);
			output.accept(ItemRegistry.DRAGONFLY_WING);
			output.accept(ItemRegistry.LURKER_SKIN);
			output.accept(ItemRegistry.DRIED_SWAMP_REED);
			output.accept(ItemRegistry.REED_ROPE);
			output.accept(ItemRegistry.MUD_BRICK);
			output.accept(ItemRegistry.SYRMORITE_INGOT);
			output.accept(ItemRegistry.DRY_BARK);
			output.accept(ItemRegistry.SLIMY_BONE);
			output.accept(ItemRegistry.SNAPPER_ROOT);
			output.accept(ItemRegistry.STALKER_EYE);
			output.accept(ItemRegistry.SULFUR);
			output.accept(ItemRegistry.VALONITE_SHARD);
			output.accept(ItemRegistry.WEEDWOOD_STICK);
			output.accept(ItemRegistry.ANGLER_TOOTH);
			output.accept(ItemRegistry.WEEDWOOD_BOWL);
			output.accept(ItemRegistry.RUBBER_BALL);
			output.accept(ItemRegistry.TAR_BEAST_HEART);
			output.accept(ItemRegistry.ANIMATED_TAR_BEAST_HEART);
			output.accept(ItemRegistry.TAR_DRIP);
			output.accept(ItemRegistry.LIMESTONE_FLUX);
			output.accept(ItemRegistry.INANIMATE_TARMINION);
			output.accept(ItemRegistry.POISON_GLAND);
			output.accept(ItemRegistry.AMATE_PAPER);
			output.accept(ItemRegistry.SHOCKWAVE_SWORD_PIECE_1);
			output.accept(ItemRegistry.SHOCKWAVE_SWORD_PIECE_2);
			output.accept(ItemRegistry.SHOCKWAVE_SWORD_PIECE_3);
			output.accept(ItemRegistry.SHOCKWAVE_SWORD_PIECE_4);
			output.accept(ItemRegistry.AMULET_SOCKET);
			output.accept(ItemRegistry.SCABYST);
			output.accept(ItemRegistry.ITEM_SCROLL);
			output.accept(ItemRegistry.SYRMORITE_NUGGET);
			output.accept(ItemRegistry.OCTINE_NUGGET);
			output.accept(ItemRegistry.VALONITE_SPLINTER);
			output.accept(ItemRegistry.CREMAINS);
			output.accept(ItemRegistry.UNDYING_EMBERS);
			output.accept(ItemRegistry.INANIMATE_ANGRY_PEBBLE);
			output.accept(ItemRegistry.ANCIENT_REMNANT);
			output.accept(ItemRegistry.LOOT_SCRAPS);
			output.accept(ItemRegistry.FABRICATED_SCROLL);
			output.accept(BlockRegistry.BETWEENSTONE_PEBBLE);
			output.accept(ItemRegistry.ANADIA_SWIM_BLADDER);
			output.accept(ItemRegistry.ANADIA_EYE);
			output.accept(ItemRegistry.ANADIA_GILLS);
			output.accept(ItemRegistry.ANADIA_SCALES);
			output.accept(ItemRegistry.ANADIA_BONES);
			output.accept(ItemRegistry.ANADIA_REMAINS);
			output.accept(ItemRegistry.ANADIA_FINS);
			output.accept(ItemRegistry.SNOT);
			output.accept(ItemRegistry.URCHIN_SPIKE);
			output.accept(ItemRegistry.FISHING_FLOAT_AND_HOOK);
			output.accept(ItemRegistry.OLMLETTE_MIXTURE);
			output.accept(ItemRegistry.SILK_COCOON);
			output.accept(ItemRegistry.SILK_THREAD);
			output.accept(ItemRegistry.DIRTY_SILK_BUNDLE);
			output.accept(ItemRegistry.PHEROMONE_THORAXES);
			output.accept(ItemRegistry.SWAMP_TALISMAN);
			output.accept(ItemRegistry.SWAMP_TALISMAN_PIECE_1);
			output.accept(ItemRegistry.SWAMP_TALISMAN_PIECE_2);
			output.accept(ItemRegistry.SWAMP_TALISMAN_PIECE_3);
			output.accept(ItemRegistry.SWAMP_TALISMAN_PIECE_4);
			output.accept(ItemRegistry.WEEDWOOD_ROWBOAT);
			output.accept(ItemRegistry.ORANGE_DENTROTHYST_SHARD);
			output.accept(ItemRegistry.GREEN_DENTROTHYST_SHARD);
			output.accept(ItemRegistry.FISH_BAIT);
			output.accept(ItemRegistry.FUMIGANT);
			output.accept(ItemRegistry.SAP_BALL);
			output.accept(ItemRegistry.MIRE_SNAIL_EGG);
			output.accept(ItemRegistry.COOKED_MIRE_SNAIL_EGG);
			output.accept(ItemRegistry.RAW_FROG_LEGS);
			output.accept(ItemRegistry.COOKED_FROG_LEGS);
			output.accept(ItemRegistry.RAW_SNAIL_FLESH);
			output.accept(ItemRegistry.COOKED_SNAIL_FLESH);
			output.accept(ItemRegistry.REED_DONUT);
			output.accept(ItemRegistry.JAM_DONUT);
			output.accept(ItemRegistry.GERTS_DONUT);
			output.accept(ItemRegistry.PUFFSHROOM_TENDRIL);
			output.accept(ItemRegistry.KRAKEN_TENTACLE);
			output.accept(ItemRegistry.KRAKEN_CALAMARI);
			output.accept(ItemRegistry.MIDDLE_FRUIT);
			output.accept(ItemRegistry.MINCE_PIE);
			output.accept(ItemRegistry.CHRISTMAS_PUDDING);
			output.accept(ItemRegistry.CANDY_CANE);
			output.accept(ItemRegistry.WEEPING_BLUE_PETAL);
			output.accept(ItemRegistry.WIGHT_HEART);
			output.accept(ItemRegistry.YELLOW_DOTTED_FUNGUS);
			output.accept(ItemRegistry.SILT_CRAB_CLAW);
			output.accept(ItemRegistry.CRAB_STICK);
			output.accept(ItemRegistry.SLUDGE_JELLO);
			output.accept(ItemRegistry.MIDDLE_FRUIT_JELLO);
			output.accept(ItemRegistry.SAP_JELLO);
			output.accept(ItemRegistry.GREEN_MARSHMALLOW);
			output.accept(ItemRegistry.PINK_MARSHMALLOW);
			output.accept(ItemRegistry.FLATHEAD_MUSHROOM);
			output.accept(ItemRegistry.BLACK_HAT_MUSHROOM);
			output.accept(ItemRegistry.BULB_CAPPED_MUSHROOM);
			output.accept(ItemRegistry.FRIED_SWAMP_KELP);
			output.accept(ItemRegistry.FORBIDDEN_FIG);
			output.accept(ItemRegistry.BLUE_CANDY);
			output.accept(ItemRegistry.RED_CANDY);
			output.accept(ItemRegistry.YELLOW_CANDY);
			output.accept(ItemRegistry.CHIROMAW_WING);
			output.accept(ItemRegistry.TANGLED_ROOT);
			output.accept(ItemRegistry.MIRE_SCRAMBLE);
			output.accept(ItemRegistry.WEEPING_BLUE_PETAL_SALAD);
			output.accept(ItemRegistry.NIBBLESTICK);
			output.accept(ItemRegistry.SPIRIT_FRUIT);
			output.accept(ItemRegistry.SUSHI);
			output.accept(ItemRegistry.ROCK_SNOT_PEARL);
			output.accept(ItemRegistry.PEARLED_PEAR);
			output.accept(ItemRegistry.RAW_ANADIA_MEAT);
			output.accept(ItemRegistry.COOKED_ANADIA_MEAT);
			output.accept(ItemRegistry.SMOKED_ANADIA_MEAT);
			output.accept(ItemRegistry.BARNACLE);
			output.accept(ItemRegistry.COOKED_BARNACLE);
			output.accept(ItemRegistry.SMOKED_BARNACLE);
			output.accept(ItemRegistry.SMOKED_CRAB_STICK);
			output.accept(ItemRegistry.SMOKED_FROG_LEGS);
			output.accept(ItemRegistry.SMOKED_PUFFSHROOM_TENDRIL);
			output.accept(ItemRegistry.SMOKED_SILT_CRAB_CLAW);
			output.accept(ItemRegistry.SMOKED_SNAIL_FLESH);
			output.accept(ItemRegistry.RAW_OLM_EGG);
			output.accept(ItemRegistry.COOKED_OLM_EGG);
			output.accept(ItemRegistry.OLMLETTE);
			output.accept(ItemRegistry.SILK_GRUB);
			//drinkable brews
			//herblore book
			output.accept(ItemRegistry.CRIMSON_MIDDLE_GEM);
			output.accept(ItemRegistry.AQUA_MIDDLE_GEM);
			output.accept(ItemRegistry.GREEN_MIDDLE_GEM);
			//life crystal
			//life crystal fragment
			//pyrad flame
			//dragonfly
			//firefly
			//olm
			//gecko
			//cave fish
			//mire snail
			//sludge worm egg sac
			output.accept(ItemRegistry.TINY_SLUDGE_WORM);
			output.accept(ItemRegistry.TINY_SLUDGE_WORM_HELPER);
			output.accept(ItemRegistry.ANADIA);
			//jellyfish
			output.accept(ItemRegistry.SILT_CRAB);
			output.accept(ItemRegistry.BUBBLER_CRAB);
			//urchin
			//chiromaw egg
			//lightning chiromaw egg
			//chiromaw
			//lightning chiromaw
			output.accept(ItemRegistry.SHIMMER_STONE);
			//tarminion
			//sludge ball
			//rope
			//angry pebble
			output.accept(ItemRegistry.OCTINE_INGOT);
			output.accept(ItemRegistry.SAP_SPIT);
			//shambler tongue
			output.accept(ItemRegistry.RUNE_DOOR_KEY);
			//lurker skin patch
			//draeton balloon
			//draeton burner
			//draeton
			//draeton furnace
			//draeton anchor
			//draeton crafting
			//weedwood rowboat lantern
			output.accept(ItemRegistry.AMATE_NAME_TAG);
			//dyes
			//frames
			//thorax
			output.accept(ItemRegistry.SILK_FILTER);
			output.accept(ItemRegistry.MOSS_FILTER);
			//silky pebble
		})
		.build());

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_GEAR = CREATIVE_TABS.register("gear", () -> CreativeModeTab.builder()
		.withTabsBefore(BETWEENLANDS_ITEMS.getKey())
		.icon(ItemRegistry.ANCIENT_HELMET::toStack) //TODO temp, change to valonite pick when implemented
		.title(Component.translatable("itemGroup.thebetweenlands.gear"))
		.displayItems((parameters, output) -> {
			output.accept(ItemRegistry.BONE_HELMET);
			output.accept(ItemRegistry.BONE_CHESTPLATE);
			output.accept(ItemRegistry.BONE_LEGGINGS);
			output.accept(ItemRegistry.BONE_BOOTS);
			output.accept(ItemRegistry.LURKER_SKIN_HELMET);
			output.accept(ItemRegistry.LURKER_SKIN_CHESTPLATE);
			output.accept(ItemRegistry.LURKER_SKIN_LEGGINGS);
			output.accept(ItemRegistry.LURKER_SKIN_BOOTS);
			output.accept(ItemRegistry.SYRMORITE_HELMET);
			output.accept(ItemRegistry.SYRMORITE_CHESTPLATE);
			output.accept(ItemRegistry.SYRMORITE_LEGGINGS);
			output.accept(ItemRegistry.SYRMORITE_BOOTS);
			output.accept(ItemRegistry.VALONITE_HELMET);
			output.accept(ItemRegistry.VALONITE_CHESTPLATE);
			output.accept(ItemRegistry.VALONITE_LEGGINGS);
			output.accept(ItemRegistry.VALONITE_BOOTS);
			output.accept(ItemRegistry.ANCIENT_HELMET);
			output.accept(ItemRegistry.ANCIENT_CHESTPLATE);
			output.accept(ItemRegistry.ANCIENT_LEGGINGS);
			output.accept(ItemRegistry.ANCIENT_BOOTS);
			//amphibious armor
			output.accept(ItemRegistry.RUBBER_BOOTS);
			output.accept(ItemRegistry.MARSH_RUNNER_BOOTS);
			output.accept(ItemRegistry.WEEDWOOD_SWORD);
			output.accept(ItemRegistry.WEEDWOOD_SHOVEL);
			output.accept(ItemRegistry.WEEDWOOD_AXE);
			output.accept(ItemRegistry.WEEDWOOD_PICKAXE);
			output.accept(ItemRegistry.BONE_SWORD);
			output.accept(ItemRegistry.BONE_SHOVEL);
			output.accept(ItemRegistry.BONE_AXE);
			output.accept(ItemRegistry.BONE_PICKAXE);
			output.accept(ItemRegistry.OCTINE_SWORD);
			output.accept(ItemRegistry.OCTINE_SHOVEL);
			output.accept(ItemRegistry.OCTINE_AXE);
			output.accept(ItemRegistry.OCTINE_PICKAXE);
			output.accept(ItemRegistry.VALONITE_SWORD);
			output.accept(ItemRegistry.VALONITE_SHOVEL);
			output.accept(ItemRegistry.VALONITE_AXE);
			output.accept(ItemRegistry.VALONITE_GREATAXE);
			output.accept(ItemRegistry.VALONITE_PICKAXE);
			//octine shield
			//valonite shield
			//weedwood shield
			//living weedwood shield
			//syrmorite shield
			//bone shield
			//green and orange dentrothyst shields
			//lurker skin shield
			//shears
			//sickle
			//shockwave sword
			//arrows x6
			//chiromaw barb
			//weedwood bow
			//predator bow
			//ancient greatsword
			//ancient battleaxe
			output.accept(ItemRegistry.PESTLE);
			output.accept(ItemRegistry.NET);
			//lurker skin pouches
			//volarkite
			//slingshot
			output.accept(ItemRegistry.WEEDWOOD_FISHING_ROD);
			//weedwood buckets
			//syrmorite buckets
			output.accept(ItemRegistry.ELECTRIC_UPGRADE);
			output.accept(ItemRegistry.GLIDE_UPGRADE);
			output.accept(ItemRegistry.ASCENT_UPGRADE);
			output.accept(ItemRegistry.URCHIN_SPIKE_UPGRADE);
			output.accept(ItemRegistry.FISH_VORTEX_UPGRADE);
			//biopathic triggerstone
			//biopathic linkstone
			output.accept(ItemRegistry.SILK_BUNDLE);
		})
		.build());

	//Special tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_SPECIAL = CREATIVE_TABS.register("specials", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.thebetweenlands.specials"))
		.withTabsBefore(BETWEENLANDS_GEAR.getKey())
		.icon(() -> new ItemStack(ItemRegistry.RECORD_ASTATOS.get()))
		.displayItems((parameters, output) -> {
			output.accept(ItemRegistry.SKULL_MASK);
//			output.accept(ItemRegistry.EXPLORERS_HAT);
//			output.accept(ItemRegistry.LARGE_SPIRIT_TREE_MASK);
//			output.accept(ItemRegistry.SMALL_SPIRIT_TREE_MASK);
//			output.accept(ItemRegistry.SMALL_SPIRIT_TREE_MASK_ANIMATED);
//			output.accept(ItemRegistry.SMALL_GALLERY_FRAME);
//			output.accept(ItemRegistry.LARGE_GALLERY_FRAME);
//			output.accept(ItemRegistry.VERY_LARGE_GALLERY_FRAME);
//			output.accept(ItemRegistry.SILK_MASK);
//			output.accept(ItemRegistry.WIGHTS_BANE);
//			output.accept(ItemRegistry.SLUDGE_SLICER);
//			output.accept(ItemRegistry.CRITTER_CRUNCHER);
//			output.accept(ItemRegistry.HAG_HACKER);
//			output.accept(ItemRegistry.VOODOO_DOLL);
//			output.accept(ItemRegistry.SWIFT_PICK);
//			output.accept(ItemRegistry.CHIROBARB_ERUPTER);
//			output.accept(ItemRegistry.CHIROBARB_SHOCK_ERUPTER);
//			output.accept(ItemRegistry.MIST_STAFF);
//			output.accept(ItemRegistry.SHADOW_STAFF);
			output.accept(ItemRegistry.RECORD_ASTATOS);
			output.accept(ItemRegistry.RECORD_BETWEEN_YOU_AND_ME);
			output.accept(ItemRegistry.RECORD_CHRISTMAS_ON_THE_MARSH);
			output.accept(ItemRegistry.RECORD_THE_EXPLORER);
			output.accept(ItemRegistry.RECORD_HAG_DANCE);
			output.accept(ItemRegistry.RECORD_LONELY_FIRE);
			output.accept(ItemRegistry.MYSTERIOUS_RECORD);
			output.accept(ItemRegistry.RECORD_ANCIENT);
			output.accept(ItemRegistry.RECORD_BENEATH_A_GREEN_SKY);
			output.accept(ItemRegistry.RECORD_DJ_WIGHTS_MIXTAPE);
			output.accept(ItemRegistry.RECORD_ONWARDS);
			output.accept(ItemRegistry.RECORD_STUCK_IN_THE_MUD);
			output.accept(ItemRegistry.RECORD_WANDERING_WISPS);
			output.accept(ItemRegistry.RECORD_WATERLOGGED);
			//bark amulet
			//aqua amulet
			//crimson amulet
			//green amulet
			//amulet slot
			//rings x6
			//lore scraps x10
			//mummy bait
			output.accept(ItemRegistry.BARK_AMULET);
			output.accept(ItemRegistry.AMATE_MAP);
			//bone wayfinder
			//item magnet
			//gem singer
			//rocksnot pod
		}).build());

	// Plants tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_PLANTS = CREATIVE_TABS.register("plants", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.thebetweenlands.plants"))
		.withTabsBefore(BETWEENLANDS_SPECIAL.getKey())
		.icon(() -> new ItemStack(BlockRegistry.MIRE_CORAL))
		.displayItems((parameters, output) -> {
			output.accept(BlockRegistry.WEEDWOOD_SAPLING);
			output.accept(BlockRegistry.SAP_SAPLING);
			output.accept(BlockRegistry.RUBBER_SAPLING);
//			output.accept(BlockRegistry.HEARTHGROVE_SAPLING);
			output.accept(BlockRegistry.NIBBLETWIG_SAPLING);
//			output.accept(BlockRegistry.SPIRIT_TREE_SAPLING);
//			output.accept(ItemRegistry.ROOT_POD);
			output.accept(BlockRegistry.EDGE_SHROOM);
			output.accept(BlockRegistry.EDGE_MOSS);
			output.accept(BlockRegistry.EDGE_LEAF);
			output.accept(BlockRegistry.PITCHER_PLANT);
			output.accept(BlockRegistry.WEEPING_BLUE);
			output.accept(BlockRegistry.SUNDEW);
			output.accept(BlockRegistry.BLACK_HAT_MUSHROOM);
			output.accept(BlockRegistry.BULB_CAPPED_MUSHROOM);
			output.accept(BlockRegistry.FLATHEAD_MUSHROOM);
			output.accept(BlockRegistry.VENUS_FLY_TRAP);
			output.accept(BlockRegistry.VOLARPAD);
			output.accept(BlockRegistry.SWAMP_PLANT);
			output.accept(BlockRegistry.MIRE_CORAL);
			output.accept(BlockRegistry.DEEP_WATER_CORAL);
			output.accept(BlockRegistry.WATER_WEEDS);
			output.accept(BlockRegistry.ALGAE);
			output.accept(BlockRegistry.POISON_IVY);
			output.accept(BlockRegistry.ARROW_ARUM);
			output.accept(BlockRegistry.BLUE_EYED_GRASS);
			output.accept(BlockRegistry.BLUE_IRIS);
			output.accept(BlockRegistry.BONESET);
			output.accept(BlockRegistry.BOTTLE_BRUSH_GRASS);
			output.accept(BlockRegistry.BROOMSEDGE);
			output.accept(BlockRegistry.BUTTON_BUSH);
			output.accept(BlockRegistry.CARDINAL_FLOWER);
			output.accept(BlockRegistry.CATTAIL);
			output.accept(BlockRegistry.CAVE_GRASS);
			output.accept(BlockRegistry.COPPER_IRIS);
			output.accept(BlockRegistry.MARSH_HIBISCUS);
			output.accept(BlockRegistry.MARSH_MALLOW);
			output.accept(BlockRegistry.BLADDERWORT_FLOWER);
			output.accept(BlockRegistry.BLADDERWORT_STALK);
			output.accept(BlockRegistry.BOG_BEAN_FLOWER);
			output.accept(BlockRegistry.GOLDEN_CLUB_FLOWER);
			output.accept(BlockRegistry.MARSH_MARIGOLD_FLOWER);
			output.accept(BlockRegistry.TALL_SWAMP_GRASS);
			output.accept(BlockRegistry.MILKWEED);
			output.accept(BlockRegistry.NETTLE);
			output.accept(BlockRegistry.FLOWERED_NETTLE);
			output.accept(BlockRegistry.PICKERELWEED);
			output.accept(BlockRegistry.PHRAGMITES);
			output.accept(BlockRegistry.SHOOTS);
			output.accept(BlockRegistry.SLUDGECREEP);
			output.accept(BlockRegistry.TALL_SLUDGECREEP);
			output.accept(BlockRegistry.SOFT_RUSH);
			output.accept(BlockRegistry.THORNS);
			output.accept(BlockRegistry.TALL_CATTAIL);
			output.accept(BlockRegistry.SHORT_SWAMP_GRASS);
			output.accept(BlockRegistry.DEAD_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.WEEDWOOD_BUSH);
			output.accept(BlockRegistry.STICK_NESTING_BLOCK);
			output.accept(BlockRegistry.BONE_NESTING_BLOCK);
			output.accept(BlockRegistry.CAVE_MOSS);
			output.accept(BlockRegistry.CRYPTWEED);
			output.accept(BlockRegistry.STRING_ROOTS);
			output.accept(BlockRegistry.PALE_GRASS);
			output.accept(BlockRegistry.ROTBULB);
			output.accept(BlockRegistry.MOSS);
			output.accept(BlockRegistry.DEAD_MOSS);
			output.accept(BlockRegistry.LICHEN);
			output.accept(BlockRegistry.DEAD_LICHEN);
			output.accept(BlockRegistry.HANGER);
			output.accept(BlockRegistry.SEEDED_HANGER);
			output.accept(BlockRegistry.PHEROMONE_INFUSED_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.MOTH_INFESTED_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.SILK_COCOONED_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.DECAY_INFESTED_WEEDWOOD_BUSH);
			output.accept(BlockRegistry.FALLEN_LEAVES);
			output.accept(BlockRegistry.SWAMP_REED);
			output.accept(BlockRegistry.SWAMP_KELP);
		}).build());

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BETWEENLANDS_HERBLORE = CREATIVE_TABS.register("herblore", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup.thebetweenlands.herblore"))
		.withTabsBefore(BETWEENLANDS_PLANTS.getKey())
		.icon(ItemRegistry.GROUND_LEAF::toStack)
		.displayItems((parameters, output) -> {
			output.accept(ItemRegistry.GROUND_LEAF);
			output.accept(ItemRegistry.GROUND_CATTAIL);
			output.accept(ItemRegistry.GROUND_SWAMP_GRASS);
			output.accept(ItemRegistry.GROUND_SHOOTS);
			output.accept(ItemRegistry.GROUND_ARROW_ARUM);
			output.accept(ItemRegistry.GROUND_BUTTON_BUSH);
			output.accept(ItemRegistry.GROUND_MARSH_HIBUSCUS);
			output.accept(ItemRegistry.GROUND_PICKERELWEED);
			output.accept(ItemRegistry.GROUND_SOFT_RUSH);
			output.accept(ItemRegistry.GROUND_MARSH_MALLOW);
			output.accept(ItemRegistry.GROUND_MILKWEED);
			output.accept(ItemRegistry.GROUND_BLUE_IRIS);
			output.accept(ItemRegistry.GROUND_COPPER_IRIS);
			output.accept(ItemRegistry.GROUND_BLUE_EYED_GRASS);
			output.accept(ItemRegistry.GROUND_BONESET);
			output.accept(ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS);
			output.accept(ItemRegistry.GROUND_WEEDWOOD_BARK);
			output.accept(ItemRegistry.GROUND_DRIED_SWAMP_REED);
			output.accept(ItemRegistry.GROUND_ALGAE);
			output.accept(ItemRegistry.GROUND_ANGLER_TOOTH);
			output.accept(ItemRegistry.GROUND_BLACKHAT_MUSHROOM);
			output.accept(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL);
			output.accept(ItemRegistry.GROUND_BOG_BEAN);
			output.accept(ItemRegistry.GROUND_BROOMSEDGE);
			output.accept(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM);
			output.accept(ItemRegistry.GROUND_CARDINAL_FLOWER);
			output.accept(ItemRegistry.GROUND_CAVE_GRASS);
			output.accept(ItemRegistry.GROUND_CAVE_MOSS);
			output.accept(ItemRegistry.GROUND_CRIMSON_MIDDLE_GEM);
			output.accept(ItemRegistry.GROUND_DEEP_WATER_CORAL);
			output.accept(ItemRegistry.GROUND_FLATHEAD_MUSHROOM);
			output.accept(ItemRegistry.GROUND_GOLDEN_CLUB);
			output.accept(ItemRegistry.GROUND_GREEN_MIDDLE_GEM);
			output.accept(ItemRegistry.GROUND_HANGER);
			output.accept(ItemRegistry.GROUND_LICHEN);
			output.accept(ItemRegistry.GROUND_MARSH_MARIGOLD);
			output.accept(ItemRegistry.GROUND_MIRE_CORAL);
			output.accept(ItemRegistry.GROUND_OCHRE_SNAIL_SHELL);
			output.accept(ItemRegistry.GROUND_MOSS);
			output.accept(ItemRegistry.GROUND_NETTLE);
			output.accept(ItemRegistry.GROUND_PHRAGMITES);
			output.accept(ItemRegistry.GROUND_SLUDGECREEP);
			output.accept(ItemRegistry.GROUND_SUNDEW);
			output.accept(ItemRegistry.GROUND_SWAMP_KELP);
			output.accept(ItemRegistry.GROUND_ROOTS);
			output.accept(ItemRegistry.GROUND_AQUA_MIDDLE_GEM);
			output.accept(ItemRegistry.GROUND_PITCHER_PLANT);
			output.accept(ItemRegistry.GROUND_WATER_WEEDS);
			output.accept(ItemRegistry.GROUND_VENUS_FLY_TRAP);
			output.accept(ItemRegistry.GROUND_VOLARPAD);
			output.accept(ItemRegistry.GROUND_THORNS);
			output.accept(ItemRegistry.GROUND_POISON_IVY);
			output.accept(ItemRegistry.GROUND_BLADDERWORT_FLOWER);
			output.accept(ItemRegistry.GROUND_BLADDERWORT_STALK);
			output.accept(ItemRegistry.GROUND_EDGE_SHROOM);
			output.accept(ItemRegistry.GROUND_EDGE_MOSS);
			output.accept(ItemRegistry.GROUND_EDGE_LEAF);
			output.accept(ItemRegistry.GROUND_ROTBULB);
			output.accept(ItemRegistry.GROUND_PALE_GRASS);
			output.accept(ItemRegistry.GROUND_STRING_ROOTS);
			output.accept(ItemRegistry.GROUND_CRYPTWEED);
			output.accept(ItemRegistry.GROUND_BETWEENSTONE_PEBBLE);
			output.accept(ItemRegistry.LEAF);
			output.accept(ItemRegistry.ALGAE_CLUMP);
			output.accept(ItemRegistry.ARROW_ARUM_LEAF);
			output.accept(ItemRegistry.BLUE_EYED_GRASS_FLOWERS);
			output.accept(ItemRegistry.BLUE_IRIS_PETAL);
			output.accept(ItemRegistry.MIRE_CORAL_STALK);
			output.accept(ItemRegistry.DEEP_WATER_CORAL_STALK);
			output.accept(ItemRegistry.BOG_BEAN_FLOWER_DROP);
			output.accept(ItemRegistry.BONESET_FLOWERS);
			output.accept(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES);
			output.accept(ItemRegistry.BROOMSEDGE_LEAVES);
			output.accept(ItemRegistry.BUTTON_BUSH_FLOWERS);
			output.accept(ItemRegistry.CARDINAL_FLOWER_PETALS);
			output.accept(ItemRegistry.CATTAIL_HEAD);
			output.accept(ItemRegistry.CAVE_GRASS_BLADES);
			output.accept(ItemRegistry.COPPER_IRIS_PETALS);
			output.accept(ItemRegistry.GOLDEN_CLUB_FLOWERS);
			output.accept(ItemRegistry.LICHEN_CLUMP);
			output.accept(ItemRegistry.MARSH_HIBISCUS_FLOWER);
			output.accept(ItemRegistry.MARSH_MALLOW_FLOWER);
			output.accept(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP);
			output.accept(ItemRegistry.NETTLE_LEAF);
			output.accept(ItemRegistry.PHRAGMITE_STEMS);
			output.accept(ItemRegistry.PICKERELWEED_FLOWER);
			output.accept(ItemRegistry.SHOOT_LEAVES);
			output.accept(ItemRegistry.SLUDGECREEP_LEAVES);
			output.accept(ItemRegistry.SOFT_RUSH_LEAVES);
			output.accept(ItemRegistry.SUNDEW_HEAD);
			output.accept(ItemRegistry.SWAMP_GRASS_BLADES);
			output.accept(ItemRegistry.CAVE_MOSS_CLUMP);
			output.accept(ItemRegistry.MOSS_CLUMP);
			output.accept(ItemRegistry.MILKWEED_FLOWER);
			output.accept(ItemRegistry.HANGER_DROP);
			output.accept(ItemRegistry.PITCHER_PLANT_TRAP);
			output.accept(ItemRegistry.WATER_WEEDS_DROP);
			output.accept(ItemRegistry.VENUS_FLY_TRAP_HEAD);
			output.accept(ItemRegistry.VOLARPAD_LEAF);
			output.accept(ItemRegistry.THORN_BRANCH);
			output.accept(ItemRegistry.POISON_IVY_LEAF);
			output.accept(ItemRegistry.BLADDERWORT_STALK_DROP);
			output.accept(ItemRegistry.BLADDERWORT_FLOWER_DROP);
			output.accept(ItemRegistry.EDGE_SHROOM_GILLS);
			output.accept(ItemRegistry.EDGE_MOSS_CLUMP);
			output.accept(ItemRegistry.EDGE_LEAF_DROP);
			output.accept(ItemRegistry.ROTBULB_STALK);
			output.accept(ItemRegistry.PALE_GRASS_BLADES);
			output.accept(ItemRegistry.STRING_ROOT_FIBERS);
			output.accept(ItemRegistry.CRYPTWEED_BLADES);
			//aspectrus fruits
			//elixirs
			//aspect vials
		}).build());

	public static void populateTabs(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			EntityRegistry.SPAWN_EGGS.getEntries().forEach(item -> event.accept(new ItemStack(item)));
		}
	}
}
