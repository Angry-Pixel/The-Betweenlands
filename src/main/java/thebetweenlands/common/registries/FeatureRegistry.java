package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.*;
import thebetweenlands.common.world.gen.feature.config.*;

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

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
	//Double Plant Cluster
	public static final DeferredHolder<Feature<?>, Feature<PoolConfiguration>> FLUID_POOL = FEATURES.register("fluid_pool",
		() -> new FluidPoolFeature(PoolConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<SimulacrumConfiguration>> LAKE_CAVERN_SIMULACRUM = FEATURES.register("lake_cavern_simulacrum",
		() -> new LakeCavernSimulacrumFeature(SimulacrumConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<ChanceConfiguration>> LYESTONE = FEATURES.register("lyestone",
		() -> new LyestoneFeature(ChanceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> MOSS_CLUSTER = FEATURES.register("moss_cluster",
		() -> new MossClusterFeature(BlockPlaceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<PebbleClusterConfiguration>> PEBBLE_CLUSTER = FEATURES.register("pebble_cluster",
		() -> new PebbleClusterFeature(PebbleClusterConfiguration.CODEC));
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
}
