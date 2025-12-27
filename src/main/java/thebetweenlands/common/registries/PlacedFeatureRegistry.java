package thebetweenlands.common.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import thebetweenlands.common.TheBetweenlands;

import java.util.List;

public class PlacedFeatureRegistry {

	//weedwood trees
	public static final ResourceKey<PlacedFeature> WEEDWOOD_TREE_COMMON = makeKey("weedwood_tree_common");
	public static final ResourceKey<PlacedFeature> WEEDWOOD_TREE_UNCOMMON = makeKey("weedwood_tree_uncommon");
	public static final ResourceKey<PlacedFeature> WEEDWOOD_TREE_RARE = makeKey("weedwood_tree_rare");
	public static final ResourceKey<PlacedFeature> WEEDWOOD_TREE_SUPER_RARE = makeKey("weedwood_tree_super_rare");
	public static final ResourceKey<PlacedFeature> ROTTEN_WEEDWOOD_TREE = makeKey("rotten_weedwood_tree");

	//sap trees
	public static final ResourceKey<PlacedFeature> SAP_TREE_COMMON = makeKey("sap_tree_common");
	public static final ResourceKey<PlacedFeature> SAP_TREE_UNCOMMON = makeKey("sap_tree_uncommon");
	public static final ResourceKey<PlacedFeature> SAP_TREE_RARE = makeKey("sap_tree_rare");

	//rubber trees
	public static final ResourceKey<PlacedFeature> RUBBER_TREE = makeKey("rubber_tree");

	//nibbletwig trees
	public static final ResourceKey<PlacedFeature> NIBBLETWIG_TREE = makeKey("nibbletwig_tree");

	//hearthgrove trees
	public static final ResourceKey<PlacedFeature> HEARTHGROVE_TREE = makeKey("hearthgrove_tree");

	public static final ResourceKey<PlacedFeature> GIANT_TREE = makeKey("giant_tree");

	//ores
	public static final ResourceKey<PlacedFeature> SULFUR = makeKey("sulfur");
	public static final ResourceKey<PlacedFeature> SYRMORITE = makeKey("syrmorite");
	public static final ResourceKey<PlacedFeature> BONE_ORE = makeKey("bone_ore");
	public static final ResourceKey<PlacedFeature> OCTINE = makeKey("octine");
	public static final ResourceKey<PlacedFeature> SWAMP_DIRT = makeKey("swamp_dirt");
	public static final ResourceKey<PlacedFeature> LIMESTONE = makeKey("limestone");
	public static final ResourceKey<PlacedFeature> VALONITE = makeKey("valonite");
	public static final ResourceKey<PlacedFeature> SCABYST = makeKey("scabyst");
	public static final ResourceKey<PlacedFeature> LIFE_GEM = makeKey("life_gem");

	public static final ResourceKey<PlacedFeature> AQUA_MIDDLE_GEM = makeKey("aqua_middle_gem");
	public static final ResourceKey<PlacedFeature> CRIMSON_MIDDLE_GEM = makeKey("crimson_middle_gem");
	public static final ResourceKey<PlacedFeature> GREEN_MIDDLE_GEM = makeKey("green_middle_gem");

	//simulacrum
	public static final ResourceKey<PlacedFeature> DEEPMAN_SIMULACRUM = makeKey("deepman_simulacrum");
	public static final ResourceKey<PlacedFeature> LAKE_CAVERN_SIMULACRUM = makeKey("lake_cavern_simulacrum");
	public static final ResourceKey<PlacedFeature> ROOTMAN_SIMULACRUM = makeKey("rootman_simulacrum");

	//cave features
	public static final ResourceKey<PlacedFeature> CAVE_GRASS = makeKey("cave_grass");
	public static final ResourceKey<PlacedFeature> CAVE_HANGERS = makeKey("cave_hangers");
	public static final ResourceKey<PlacedFeature> CAVE_MOSS = makeKey("cave_moss");
	public static final ResourceKey<PlacedFeature> CAVE_POTS = makeKey("cave_pots");
	public static final ResourceKey<PlacedFeature> CAVE_THORNS = makeKey("cave_thorns");
	public static final ResourceKey<PlacedFeature> MOSS_CLUSTER_UNDERGROUND = makeKey("moss_patch_underground");
	public static final ResourceKey<PlacedFeature> LICHEN_CLUSTER_UNDERGROUND = makeKey("lichen_patch_underground");

	//pools
	public static final ResourceKey<PlacedFeature> STAGNANT_WATER_POOL = makeKey("stagnant_water_pool");
	public static final ResourceKey<PlacedFeature> TAR_POOL = makeKey("tar_pool");

	//plants
	public static final ResourceKey<PlacedFeature> SHORT_SWAMP_GRASS_PATCH_COMMON = makeKey("short_swamp_grass_patch_common");
	public static final ResourceKey<PlacedFeature> SHORT_SWAMP_GRASS_PATCH_UNCOMMON = makeKey("short_swamp_grass_patch_uncommon");
	public static final ResourceKey<PlacedFeature> SHORT_SWAMP_GRASS_PATCH_RARE = makeKey("short_swamp_grass_patch_rare");
	public static final ResourceKey<PlacedFeature> SHORT_SWAMP_GRASS_PATCH_MORE_RARE = makeKey("short_swamp_grass_patch_more_rare");
	public static final ResourceKey<PlacedFeature> SHORT_SWAMP_GRASS_PATCH_SUPER_RARE = makeKey("short_swamp_grass_patch_super_rare");
	public static final ResourceKey<PlacedFeature> BARNACLE_PATCH = makeKey("barnacle_patch");
	public static final ResourceKey<PlacedFeature> BLADDERWORT_PATCH = makeKey("bladderwort_patch");
	public static final ResourceKey<PlacedFeature> MOSS_PATCH = makeKey("moss_patch");
	public static final ResourceKey<PlacedFeature> LICHEN_PATCH = makeKey("lichen_patch");
	public static final ResourceKey<PlacedFeature> PEBBLE_PATCH_LAND = makeKey("pebble_patch_land");
	public static final ResourceKey<PlacedFeature> PEBBLE_PATCH_WATER = makeKey("pebble_patch_water");
	public static final ResourceKey<PlacedFeature> BULB_CAPPED_MUSHROOM_PATCH = makeKey("bulb_capped_mushroom_patch");
	public static final ResourceKey<PlacedFeature> NETTLE_PATCH_COMMON = makeKey("nettle_patch_common");
	public static final ResourceKey<PlacedFeature> NETTLE_PATCH_UNCOMMON = makeKey("nettle_patch_uncommon");
	public static final ResourceKey<PlacedFeature> NETTLE_PATCH_RARE = makeKey("nettle_patch_rare");
	public static final ResourceKey<PlacedFeature> ARROW_ARUM_PATCH = makeKey("arrow_arum_patch");
	public static final ResourceKey<PlacedFeature> PICKERELWEED_PATCH = makeKey("pickerelweed_patch");
	public static final ResourceKey<PlacedFeature> MARSH_HIBISCUS_PATCH = makeKey("marsh_hibiscus_patch");
	public static final ResourceKey<PlacedFeature> MARSH_MALLOW_PATCH = makeKey("marsh_mallow_patch");
	public static final ResourceKey<PlacedFeature> BUTTON_BUSH_PATCH = makeKey("button_bush_patch");
	public static final ResourceKey<PlacedFeature> SOFT_RUSH_PATCH = makeKey("soft_rush_patch");
	public static final ResourceKey<PlacedFeature> BROOMSEDGE = makeKey("broomsedge");
	public static final ResourceKey<PlacedFeature> BOTTLE_BRUSH_GRASS_PATCH = makeKey("bottle_brush_grass_patch");
	public static final ResourceKey<PlacedFeature> SWAMP_PLANT_PATCH_COMMON = makeKey("swamp_plant_patch_common");
	public static final ResourceKey<PlacedFeature> SWAMP_PLANT_PATCH_UNCOMMON = makeKey("swamp_plant_patch_uncommon");
	public static final ResourceKey<PlacedFeature> FLAT_HEAD_MUSHROOM_PATCH_COMMON = makeKey("flat_head_mushroom_patch_common");
	public static final ResourceKey<PlacedFeature> FLAT_HEAD_MUSHROOM_PATCH_UNCOMMON = makeKey("flat_head_mushroom_patch_uncommon");
	public static final ResourceKey<PlacedFeature> BLACK_HAT_MUSHROOM_PATCH_COMMON = makeKey("black_hat_mushroom_patch_common");
	public static final ResourceKey<PlacedFeature> BLACK_HAT_MUSHROOM_PATCH_UNCOMMON = makeKey("black_hat_mushroom_patch_uncommon");
	public static final ResourceKey<PlacedFeature> VOLARPAD = makeKey("volarpad");
	public static final ResourceKey<PlacedFeature> TALL_CATTAIL_COMMON = makeKey("tall_cattail_common");
	public static final ResourceKey<PlacedFeature> TALL_CATTAIL_UNCOMMON = makeKey("tall_cattail_uncommon");
	public static final ResourceKey<PlacedFeature> TALL_SWAMP_GRASS = makeKey("tall_swamp_grass");
	public static final ResourceKey<PlacedFeature> CATTAIL_PATCH_COMMON = makeKey("tall_swamp_grass_common");
	public static final ResourceKey<PlacedFeature> CATTAIL_PATCH_UNCOMMON = makeKey("tall_swamp_grass_uncommon");
	public static final ResourceKey<PlacedFeature> VENUS_FLY_TRAP_PATCH_COMMON = makeKey("venus_fly_trap_patch_common");
	public static final ResourceKey<PlacedFeature> VENUS_FLY_TRAP_PATCH_UNCOMMON = makeKey("venus_fly_trap_patch_uncommon");
	public static final ResourceKey<PlacedFeature> PITCHER_PLANT = makeKey("pitcher_plant");
	public static final ResourceKey<PlacedFeature> MIRE_CORAL_PATCH = makeKey("mire_coral_patch");
	public static final ResourceKey<PlacedFeature> DEEP_WATER_CORAL_PATCH = makeKey("deep_water_coral_patch");
	public static final ResourceKey<PlacedFeature> COPPER_IRIS_PATCH = makeKey("copper_iris_patch");
	public static final ResourceKey<PlacedFeature> BLUE_IRIS_PATCH = makeKey("blue_iris_patch");
	public static final ResourceKey<PlacedFeature> MILKWEED_PATCH = makeKey("milkweed_patch");
	public static final ResourceKey<PlacedFeature> SHOOTS_PATCH = makeKey("shoots_patch");
	public static final ResourceKey<PlacedFeature> BLUE_EYED_GRASS_PATCH = makeKey("blue_eyes_grass_patch");
	public static final ResourceKey<PlacedFeature> BONESET_PATCH = makeKey("boneset_patch");
	public static final ResourceKey<PlacedFeature> SLUDGECREEP_PATCH = makeKey("sludgecreep_patch");
	public static final ResourceKey<PlacedFeature> DEAD_WEEDWOOD_BUSH_PATCH = makeKey("dead_weedwood_bush_patch");
	public static final ResourceKey<PlacedFeature> WATER_WEED_PATCH = makeKey("water_weed_patch");
	public static final ResourceKey<PlacedFeature> REED_PATCH = makeKey("reed_patch");
	public static final ResourceKey<PlacedFeature> WATER_ROOTS = makeKey("water_roots");
	public static final ResourceKey<PlacedFeature> ROOTS = makeKey("roots");
	public static final ResourceKey<PlacedFeature> SWAMP_KELP_PATCH = makeKey("swamp_kelp_patch");
	public static final ResourceKey<PlacedFeature> WEEDWOOD_BUSH = makeKey("weedwood_bush");

	//misc
	public static final ResourceKey<PlacedFeature> ALGAE = makeKey("algae");
	public static final ResourceKey<PlacedFeature> CRAG_SPIRES = makeKey("crag_spires");
	public static final ResourceKey<PlacedFeature> SILT_BEACH = makeKey("silt_beach");
	public static final ResourceKey<PlacedFeature> BIG_BULB_CAPPED_MUSHROOM = makeKey("big_bulb_capped_mushroom");
	public static final ResourceKey<PlacedFeature> SMALL_HOLLOW_LOG = makeKey("small_hollow_log");
	public static final ResourceKey<PlacedFeature> DEAD_TRUNK = makeKey("dead_trunk");
	public static final ResourceKey<PlacedFeature> LYESTONE = makeKey("lyestone");


	public static final ResourceKey<PlacedFeature> TAR_POOL_DUNGEON = makeKey("tar_pool_dungeon");

	private static ResourceKey<PlacedFeature> makeKey(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<PlacedFeature> context) {
		HolderGetter<ConfiguredFeature<?, ?>> featureGetter = context.lookup(Registries.CONFIGURED_FEATURE);

		context.register(SULFUR, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SULFUR), OrePlacements.commonOrePlacement(22, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT), VerticalAnchor.absolute(128)))));
		context.register(SYRMORITE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SYRMORITE), OrePlacements.commonOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT + 40), VerticalAnchor.absolute(TheBetweenlands.CAVE_START - 5)))));
		context.register(BONE_ORE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BONE_ORE), OrePlacements.commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT), VerticalAnchor.absolute(128)))));
		//note: in 1.12 octine has a 4.5F count
		context.register(OCTINE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.OCTINE), OrePlacements.commonOrePlacement(5, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT), VerticalAnchor.absolute(TheBetweenlands.CAVE_START - 40)))));
		context.register(SWAMP_DIRT, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SWAMP_DIRT), OrePlacements.commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT), VerticalAnchor.absolute(TheBetweenlands.CAVE_START - 15)))));
		//note: in 1.12 limestone has a 0.2F count
//		context.register(LIMESTONE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.LIMESTONE), OrePlacements.commonOrePlacement(1, HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT), VerticalAnchor.absolute(TheBetweenlands.CAVE_START - 15)))));
		context.register(VALONITE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.VALONITE), OrePlacements.commonOrePlacement(1, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT)))));
		context.register(SCABYST, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SCABYST), OrePlacements.commonOrePlacement(3, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(TheBetweenlands.PITSTONE_HEIGHT)))));
		context.register(LIFE_GEM, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.LIFE_GEM), OrePlacements.commonOrePlacement(70, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(TheBetweenlands.CAVE_WATER_HEIGHT)))));

		context.register(WEEDWOOD_TREE_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.WEEDWOOD_TREE), tree(80)));
		context.register(WEEDWOOD_TREE_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.WEEDWOOD_TREE), tree(50)));
		context.register(WEEDWOOD_TREE_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.WEEDWOOD_TREE), tree(25)));
		context.register(WEEDWOOD_TREE_SUPER_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.WEEDWOOD_TREE), tree(1)));
		context.register(ROTTEN_WEEDWOOD_TREE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.ROTTEN_WEEDWOOD_TREE), tree(4)));

		context.register(SAP_TREE_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SAP_TREE), treeHydrophobic(15)));
		context.register(SAP_TREE_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SAP_TREE), treeHydrophobic(10)));
		context.register(SAP_TREE_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SAP_TREE), treeHydrophobic(8)));

		context.register(RUBBER_TREE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.RUBBER_TREE), treeHydrophobic(4)));

		context.register(NIBBLETWIG_TREE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.NIBBLETWIG_TREE), treeHydrophobic(100)));

		context.register(HEARTHGROVE_TREE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.HEARTHGROVE_TREE), tree(5)));

		context.register(SHORT_SWAMP_GRASS_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SHORT_SWAMP_GRASS_PATCH), patch(150)));
		context.register(SHORT_SWAMP_GRASS_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SHORT_SWAMP_GRASS_PATCH), patch(90)));
		context.register(SHORT_SWAMP_GRASS_PATCH_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SHORT_SWAMP_GRASS_PATCH), patch(40)));
		context.register(SHORT_SWAMP_GRASS_PATCH_MORE_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SHORT_SWAMP_GRASS_PATCH), patch(30)));
		context.register(SHORT_SWAMP_GRASS_PATCH_SUPER_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SHORT_SWAMP_GRASS_PATCH), patch(15)));

		context.register(NETTLE_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.NETTLE_PATCH), patch(6)));
		context.register(NETTLE_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.NETTLE_PATCH), patch(2)));
		context.register(NETTLE_PATCH_RARE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.NETTLE_PATCH), patch(1)));

		context.register(ARROW_ARUM_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.ARROW_ARUM_PATCH), patch(2)));

		context.register(PICKERELWEED_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.PICKERELWEED_PATCH), patch(2)));

		context.register(MARSH_HIBISCUS_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.MARSH_HIBISCUS_PATCH), patch(2)));

		context.register(MARSH_MALLOW_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.MARSH_MALLOW_PATCH), patch(2)));

		context.register(BUTTON_BUSH_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BUTTON_BUSH_PATCH), patch(2)));

		context.register(SOFT_RUSH_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SOFT_RUSH_PATCH), patch(90)));

		context.register(BROOMSEDGE, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BROOMSEDGE), patch(40)));

		context.register(BOTTLE_BRUSH_GRASS_PATCH, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BOTTLE_BRUSH_GRASS_PATCH), patch(5)));

		context.register(SWAMP_PLANT_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SWAMP_PLANT_PATCH), patch(26)));
		context.register(SWAMP_PLANT_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.SWAMP_PLANT_PATCH), patch(10)));

		context.register(VENUS_FLY_TRAP_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.VENUS_FLY_TRAP_PATCH), patch(26)));
		context.register(VENUS_FLY_TRAP_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.VENUS_FLY_TRAP_PATCH), patch(10)));

		context.register(PITCHER_PLANT, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.PITCHER_PLANT), patch(4)));

		context.register(FLAT_HEAD_MUSHROOM_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.FLAT_HEAD_MUSHROOM_PATCH), patch(12)));
		context.register(FLAT_HEAD_MUSHROOM_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.FLAT_HEAD_MUSHROOM_PATCH), patch(5)));

		context.register(BLACK_HAT_MUSHROOM_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BLACK_HAT_MUSHROOM_PATCH), patch(12)));
		context.register(BLACK_HAT_MUSHROOM_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.BLACK_HAT_MUSHROOM_PATCH), patch(5)));

		context.register(VOLARPAD, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.VOLARPAD), patch(18)));

		context.register(TALL_CATTAIL_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.TALL_CATTAIL), patch(50)));
		context.register(TALL_CATTAIL_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.TALL_CATTAIL), patch(20)));

		context.register(TALL_SWAMP_GRASS, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.TALL_SWAMP_GRASS), patch(120)));

		context.register(CATTAIL_PATCH_COMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.CATTAIL_PATCH), patch(10)));
		context.register(CATTAIL_PATCH_UNCOMMON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.CATTAIL_PATCH), patch(5)));

		context.register(TAR_POOL_DUNGEON, new PlacedFeature(featureGetter.getOrThrow(ConfiguredFeatureRegistry.TAR_POOL_DUNGEON), List.of(InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(TheBetweenlands.CAVE_WATER_HEIGHT), VerticalAnchor.absolute(TheBetweenlands.LAYER_HEIGHT)), BiomeFilter.biome())));
	}

	private static List<PlacementModifier> tree(int count) {
		return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(BlockRegistry.WEEDWOOD_SAPLING.get().defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome());
	}

	private static List<PlacementModifier> treeHydrophobic(int count) {
		return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(BlockRegistry.WEEDWOOD_SAPLING.get().defaultBlockState(), BlockPos.ZERO)), BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE), BiomeFilter.biome());
	}

	private static List<PlacementModifier> patch(int count) {
		return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE), BiomeFilter.biome());
	}
}