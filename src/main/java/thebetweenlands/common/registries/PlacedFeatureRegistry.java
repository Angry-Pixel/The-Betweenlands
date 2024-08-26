package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thebetweenlands.common.TheBetweenlands;

public class PlacedFeatureRegistry {

	//trees
	public static final ResourceKey<PlacedFeature> WEEDWOOD_TREE = makeKey("weedwood_tree");
	public static final ResourceKey<PlacedFeature> ROTTEN_WEEDWOOD_TREE = makeKey("rotten_weedwood_tree");
	public static final ResourceKey<PlacedFeature> SAP_TREE = makeKey("sap_tree");
	public static final ResourceKey<PlacedFeature> RUBBER_TREE = makeKey("rubber_tree");
	public static final ResourceKey<PlacedFeature> NIBBLETWIG_TREE = makeKey("nibbletwig_tree");
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
	public static final ResourceKey<PlacedFeature> SWAMP_TALLGRASS_PATCH = makeKey("swamp_tallgrass_patch");
	public static final ResourceKey<PlacedFeature> BARNACLE_PATCH = makeKey("barnacle_patch");
	public static final ResourceKey<PlacedFeature> BLADDERWORT_PATCH = makeKey("bladderwort_patch");
	public static final ResourceKey<PlacedFeature> MOSS_PATCH = makeKey("moss_patch");
	public static final ResourceKey<PlacedFeature> LICHEN_PATCH = makeKey("lichen_patch");
	public static final ResourceKey<PlacedFeature> PEBBLE_PATCH_LAND = makeKey("pebble_patch_land");
	public static final ResourceKey<PlacedFeature> PEBBLE_PATCH_WATER = makeKey("pebble_patch_water");
	public static final ResourceKey<PlacedFeature> BULB_CAPPED_MUSHROOM_PATCH = makeKey("bulb_capped_mushroom_patch");
	public static final ResourceKey<PlacedFeature> NETTLE_PATCH = makeKey("nettle_patch");
	public static final ResourceKey<PlacedFeature> ARROW_ATUM_PATCH = makeKey("arrow_atum_patch");
	public static final ResourceKey<PlacedFeature> PICKERELWEED_PATCH = makeKey("pickerelweed_patch");
	public static final ResourceKey<PlacedFeature> MARSH_HIBISCUS_PATCH = makeKey("marsh_hibiscus_patch");
	public static final ResourceKey<PlacedFeature> MARSH_MALLOW_PATCH = makeKey("marsh_mallow_patch");
	public static final ResourceKey<PlacedFeature> BUTTON_BUSH_PATCH = makeKey("button_bush_patch");
	public static final ResourceKey<PlacedFeature> SOFT_RUSH_PATCH = makeKey("soft_rush_patch");
	public static final ResourceKey<PlacedFeature> BOTTLE_BRUSH_GRASS_PATCH = makeKey("bottle_brush_grass_patch");
	public static final ResourceKey<PlacedFeature> SWAMP_PLANT_PATCH = makeKey("swamp_plant_patch");
	public static final ResourceKey<PlacedFeature> FLAT_HEAD_MUSHROOM_PATCH = makeKey("flat_head_mushroom_patch");
	public static final ResourceKey<PlacedFeature> BLACK_HAT_MUSHROOM_PATCH = makeKey("black_hat_mushroom_patch");
	public static final ResourceKey<PlacedFeature> CATTAIL_PATCH = makeKey("cattail_patch");
	public static final ResourceKey<PlacedFeature> VENUS_FLY_TRAP_PATCH = makeKey("venus_fly_trap_patch");
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

	private static ResourceKey<PlacedFeature> makeKey(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<PlacedFeature> context) {

	}
}
