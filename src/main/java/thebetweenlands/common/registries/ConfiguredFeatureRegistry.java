package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.config.ChanceConfiguration;

public class ConfiguredFeatureRegistry {

	//trees
	public static final ResourceKey<ConfiguredFeature<?, ?>> WEEDWOOD_TREE = makeKey("weedwood_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ROTTEN_WEEDWOOD_TREE = makeKey("rotten_weedwood_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SAP_TREE = makeKey("sap_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = makeKey("rubber_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> NIBBLETWIG_TREE = makeKey("nibbletwig_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> HEARTHGROVE_TREE = makeKey("hearthgrove_tree");
	public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_TREE = makeKey("giant_tree");

	//ores
	public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR = makeKey("sulfur");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SYRMORITE = makeKey("syrmorite");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BONE_ORE = makeKey("bone_ore");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OCTINE = makeKey("octine");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_DIRT = makeKey("swamp_dirt");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LIMESTONE = makeKey("limestone");
	public static final ResourceKey<ConfiguredFeature<?, ?>> VALONITE = makeKey("valonite");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SCABYST = makeKey("scabyst");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LIFE_GEM = makeKey("life_gem");

	public static final ResourceKey<ConfiguredFeature<?, ?>> AQUA_MIDDLE_GEM = makeKey("aqua_middle_gem");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_MIDDLE_GEM = makeKey("crimson_middle_gem");
	public static final ResourceKey<ConfiguredFeature<?, ?>> GREEN_MIDDLE_GEM = makeKey("green_middle_gem");

	//simulacrum
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEEPMAN_SIMULACRUM = makeKey("deepman_simulacrum");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LAKE_CAVERN_SIMULACRUM = makeKey("lake_cavern_simulacrum");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ROOTMAN_SIMULACRUM = makeKey("rootman_simulacrum");

	//cave features
	public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_GRASS = makeKey("cave_grass");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_HANGERS = makeKey("cave_hangers");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_MOSS = makeKey("cave_moss");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_POTS = makeKey("cave_pots");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_THORNS = makeKey("cave_thorns");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_CLUSTER_UNDERGROUND = makeKey("moss_patch_underground");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LICHEN_CLUSTER_UNDERGROUND = makeKey("lichen_patch_underground");

	//pools
	public static final ResourceKey<ConfiguredFeature<?, ?>> STAGNANT_WATER_POOL = makeKey("stagnant_water_pool");
	public static final ResourceKey<ConfiguredFeature<?, ?>> TAR_POOL = makeKey("tar_pool");

	//plants
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_TALLGRASS_PATCH = makeKey("swamp_tallgrass_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BARNACLE_PATCH = makeKey("barnacle_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BLADDERWORT_PATCH = makeKey("bladderwort_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MOSS_PATCH = makeKey("moss_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LICHEN_PATCH = makeKey("lichen_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_PATCH_LAND = makeKey("pebble_patch_land");
	public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_PATCH_WATER = makeKey("pebble_patch_water");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BULB_CAPPED_MUSHROOM_PATCH = makeKey("bulb_capped_mushroom_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> NETTLE_PATCH = makeKey("nettle_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ARROW_ATUM_PATCH = makeKey("arrow_atum_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> PICKERELWEED_PATCH = makeKey("pickerelweed_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MARSH_HIBISCUS_PATCH = makeKey("marsh_hibiscus_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MARSH_MALLOW_PATCH = makeKey("marsh_mallow_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BUTTON_BUSH_PATCH = makeKey("button_bush_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SOFT_RUSH_PATCH = makeKey("soft_rush_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BOTTLE_BRUSH_GRASS_PATCH = makeKey("bottle_brush_grass_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_PLANT_PATCH = makeKey("swamp_plant_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FLAT_HEAD_MUSHROOM_PATCH = makeKey("flat_head_mushroom_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BLACK_HAT_MUSHROOM_PATCH = makeKey("black_hat_mushroom_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CATTAIL_PATCH = makeKey("cattail_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> VENUS_FLY_TRAP_PATCH = makeKey("venus_fly_trap_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MIRE_CORAL_PATCH = makeKey("mire_coral_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEEP_WATER_CORAL_PATCH = makeKey("deep_water_coral_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_IRIS_PATCH = makeKey("copper_iris_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_IRIS_PATCH = makeKey("blue_iris_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> MILKWEED_PATCH = makeKey("milkweed_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SHOOTS_PATCH = makeKey("shoots_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_EYED_GRASS_PATCH = makeKey("blue_eyes_grass_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BONESET_PATCH = makeKey("boneset_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SLUDGECREEP_PATCH = makeKey("sludgecreep_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_WEEDWOOD_BUSH_PATCH = makeKey("dead_weedwood_bush_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_WEED_PATCH = makeKey("water_weed_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> REED_PATCH = makeKey("reed_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_ROOTS = makeKey("water_roots");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ROOTS = makeKey("roots");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_KELP_PATCH = makeKey("swamp_kelp_patch");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WEEDWOOD_BUSH = makeKey("weedwood_bush");

	//misc
	public static final ResourceKey<ConfiguredFeature<?, ?>> ALGAE = makeKey("algae");
	public static final ResourceKey<ConfiguredFeature<?, ?>> CRAG_SPIRES = makeKey("crag_spires");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SILT_BEACH = makeKey("silt_beach");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BULB_CAPPED_MUSHROOM = makeKey("big_bulb_capped_mushroom");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_HOLLOW_LOG = makeKey("small_hollow_log");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_TRUNK = makeKey("dead_trunk");
	public static final ResourceKey<ConfiguredFeature<?, ?>> LYESTONE = makeKey("lyestone");

	private static ResourceKey<ConfiguredFeature<?, ?>> makeKey(String name) {
		return ResourceKey.create(Registries.CONFIGURED_FEATURE, TheBetweenlands.prefix(name));
	}

	public static final RuleTest BETWEENSTONE_TEST = new BlockStateMatchTest(BlockRegistry.BETWEENSTONE.get().defaultBlockState());
	public static final RuleTest PITSTONE_TEST = new BlockStateMatchTest(BlockRegistry.PITSTONE.get().defaultBlockState());

	public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
		context.register(SULFUR,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.SULFUR_ORE.get().defaultBlockState(), 14)));
		context.register(SYRMORITE,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.SYRMORITE_ORE.get().defaultBlockState(), 7)));
		context.register(BONE_ORE,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.SLIMY_BONE_ORE.get().defaultBlockState(), 11)));
		context.register(OCTINE,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.OCTINE_ORE.get().defaultBlockState(), 6)));
		context.register(SWAMP_DIRT,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.SWAMP_DIRT.get().defaultBlockState(), 25)));
//		context.register(LIMESTONE, TODO: Ore sizes cannot be more than 64. This cannot datagen
//			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(BETWEENSTONE_TEST, BlockRegistry.LIMESTONE.get().defaultBlockState(), 100)));
		context.register(VALONITE,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(PITSTONE_TEST, BlockRegistry.VALONITE_ORE.get().defaultBlockState(), 5)));
		context.register(SCABYST,
			new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(PITSTONE_TEST, BlockRegistry.SCABYST_ORE.get().defaultBlockState(), 6)));
		context.register(LIFE_GEM,
			new ConfiguredFeature<>(FeatureRegistry.LIFE_GEM_ORE.get(), new ChanceConfiguration(35)));
	}
}
