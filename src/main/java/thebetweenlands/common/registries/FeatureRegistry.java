package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.BarnacleClusterFeature;
import thebetweenlands.common.world.gen.feature.BigBulbCappedMushroomFeature;
import thebetweenlands.common.world.gen.feature.BladderwortClusterFeature;
import thebetweenlands.common.world.gen.feature.BlockReplacementClusterFeature;
import thebetweenlands.common.world.gen.feature.CaveGrassFeature;
import thebetweenlands.common.world.gen.feature.CaveHangersFeature;
import thebetweenlands.common.world.gen.feature.CaveMossFeature;
import thebetweenlands.common.world.gen.feature.CavePotsFeature;
import thebetweenlands.common.world.gen.feature.CaveThornsFeature;
import thebetweenlands.common.world.gen.feature.DeepmanSimulacrumFeature;
import thebetweenlands.common.world.gen.feature.FlatLandFeature;
import thebetweenlands.common.world.gen.feature.FluidPoolFeature;
import thebetweenlands.common.world.gen.feature.LakeCavernSimulacrumFeature;
import thebetweenlands.common.world.gen.feature.LifeGemOreFeature;
import thebetweenlands.common.world.gen.feature.LyestoneFeature;
import thebetweenlands.common.world.gen.feature.MossClusterFeature;
import thebetweenlands.common.world.gen.feature.PebbleClusterFeature;
import thebetweenlands.common.world.gen.feature.PlantClusterFeature;
import thebetweenlands.common.world.gen.feature.RootmanSimulacrumFeature;
import thebetweenlands.common.world.gen.feature.RootsClusterFeature;
import thebetweenlands.common.world.gen.feature.RottenLogFeature;
import thebetweenlands.common.world.gen.feature.SmallHollowLogFeature;
import thebetweenlands.common.world.gen.feature.SpeleothemFeature;
import thebetweenlands.common.world.gen.feature.SwampKelpClusterFeature;
import thebetweenlands.common.world.gen.feature.SwampReedClusterFeature;
import thebetweenlands.common.world.gen.feature.WaterRootsClusterFeature;
import thebetweenlands.common.world.gen.feature.WeedwoodBushFeature;
import thebetweenlands.common.world.gen.feature.config.BlockPlaceConfiguration;
import thebetweenlands.common.world.gen.feature.config.BlockReplacementConfiguration;
import thebetweenlands.common.world.gen.feature.config.ChanceConfiguration;
import thebetweenlands.common.world.gen.feature.config.FlatLandConfiguration;
import thebetweenlands.common.world.gen.feature.config.PebbleClusterConfiguration;
import thebetweenlands.common.world.gen.feature.config.PlantConfiguration;
import thebetweenlands.common.world.gen.feature.config.PoolConfiguration;
import thebetweenlands.common.world.gen.feature.config.RottenLogConfiguration;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;
import thebetweenlands.common.world.gen.feature.structure.UnderwaterRuinsFeature;
import thebetweenlands.common.world.gen.feature.tree.HearthgroveTree;
import thebetweenlands.common.world.gen.feature.tree.NibbletwigTree;
import thebetweenlands.common.world.gen.feature.tree.RottenWeedwoodTree;
import thebetweenlands.common.world.gen.feature.tree.RubberTree;
import thebetweenlands.common.world.gen.feature.tree.SapTree;
import thebetweenlands.common.world.gen.feature.tree.WeedwoodTree;

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

	//trees
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEEDWOOD_TREE = FEATURES.register("weedwood_tree", () -> new WeedwoodTree(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ROTTEN_WEEDWOOD_TREE = FEATURES.register("rotten_weedwood_tree", () -> new RottenWeedwoodTree(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SAP_TREE = FEATURES.register("sap_tree", () -> new SapTree(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> RUBBER_TREE = FEATURES.register("rubber_tree", () -> new RubberTree(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> NIBBLETWIG_TREE = FEATURES.register("nibbletwig_tree", () -> new NibbletwigTree(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> HEARTHGROVE_TREE = FEATURES.register("hearthgrove_tree", () -> new HearthgroveTree(NoneFeatureConfiguration.CODEC));

	// Raw Biome Features
	public static final DeferredHolder<Feature<?>, Feature<FlatLandConfiguration>> FLAT_LAND = FEATURES.register("flat_land", () -> new FlatLandFeature(FlatLandConfiguration.CODEC));
	
	//Biome Features
	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> BARNACLE_CLUSTER = FEATURES.register("barnacle_cluster",
		() -> new BarnacleClusterFeature(BlockPlaceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> BIG_BULB_CAPPED_MUSHROOM = FEATURES.register("big_bulb_capped_mushroom",
		() -> new BigBulbCappedMushroomFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> BLADDERWORT_CLUSTER = FEATURES.register("bladderwort_cluster",
		() -> new BladderwortClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<BlockReplacementConfiguration>> BLOCK_REPLACE_CLUSTER = FEATURES.register("block_replacement_cluster",
		() -> new BlockReplacementClusterFeature(BlockReplacementConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_GRASS = FEATURES.register("cave_grass",
		() -> new CaveGrassFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_HANGERS = FEATURES.register("cave_hangers",
		() -> new CaveHangersFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_MOSS = FEATURES.register("cave_moss",
		() -> new CaveMossFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_POTS = FEATURES.register("cave_pots",
		() -> new CavePotsFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_THORNS = FEATURES.register("cave_thorns",
		() -> new CaveThornsFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<SimulacrumConfiguration>> DEEPMAN_SIMULACRUM = FEATURES.register("deepman_simulacrum",
		() -> new DeepmanSimulacrumFeature(SimulacrumConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<PoolConfiguration>> FLUID_POOL = FEATURES.register("fluid_pool",
		() -> new FluidPoolFeature(PoolConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<SimulacrumConfiguration>> LAKE_CAVERN_SIMULACRUM = FEATURES.register("lake_cavern_simulacrum",
		() -> new LakeCavernSimulacrumFeature(SimulacrumConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<ChanceConfiguration>> LIFE_GEM_ORE = FEATURES.register("life_gem_ore",
		() -> new LifeGemOreFeature(ChanceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<ChanceConfiguration>> LYESTONE = FEATURES.register("lyestone",
		() -> new LyestoneFeature(ChanceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> MOSS_CLUSTER = FEATURES.register("moss_cluster",
		() -> new MossClusterFeature(BlockPlaceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<PebbleClusterConfiguration>> PEBBLE_CLUSTER = FEATURES.register("pebble_cluster",
		() -> new PebbleClusterFeature(PebbleClusterConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<PlantConfiguration>> PLANT_CLUSTER = FEATURES.register("plant_cluster",
		() -> new PlantClusterFeature(PlantConfiguration.CODEC));
	//Root Pod Roots
	public static final DeferredHolder<Feature<?>, Feature<SimulacrumConfiguration>> ROOTMAN_SIMULACRUM = FEATURES.register("rootman_simulacrum",
		() -> new RootmanSimulacrumFeature(SimulacrumConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ROOTS_CLUSTER = FEATURES.register("roots_cluster",
		() -> new RootsClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<RottenLogConfiguration>> ROTTEN_LOG = FEATURES.register("rotten_log",
		() -> new RottenLogFeature(RottenLogConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SMALL_HOLLOW_LOG = FEATURES.register("small_hollow_log",
		() -> new SmallHollowLogFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPELEOTHEM = FEATURES.register("speleothem",
		() -> new SpeleothemFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SWAMP_KELP_CLUSTER = FEATURES.register("swamp_kelp_cluster",
		() -> new SwampKelpClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SWAMP_REED_CLUSTER = FEATURES.register("swamp_reed_cluster",
		() -> new SwampReedClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WATER_ROOTS_CLUSTER = FEATURES.register("water_roots_cluster",
		() -> new WaterRootsClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEEDWOOD_BUSH = FEATURES.register("weedwood_bush",
		() -> new WeedwoodBushFeature(NoneFeatureConfiguration.CODEC));

	//Structure Features
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> UNDERWATER_RUINS = FEATURES.register("underwater_ruins",
		() -> new UnderwaterRuinsFeature(NoneFeatureConfiguration.CODEC));
}
