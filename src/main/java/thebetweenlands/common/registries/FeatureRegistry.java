package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.*;
import thebetweenlands.common.world.gen.feature.config.BlockPlaceConfiguration;
import thebetweenlands.common.world.gen.feature.config.ChanceConfiguration;
import thebetweenlands.common.world.gen.feature.config.PebbleClusterConfiguration;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> BARNACLE_CLUSTER = FEATURES.register("barnacle_cluster",
		() -> new BarnacleClusterFeature(BlockPlaceConfiguration.CODEC));
	//Big Bulb Capped Mushroom
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> BLADDERWORT_CLUSTER = FEATURES.register("bladderwort_cluster",
		() -> new BladderwortClusterFeature(NoneFeatureConfiguration.CODEC));
	//Block Replacement Cluster
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
	//Fluid Pool
	//Giant Root
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
	//Roots Cluster
	//Rotten Logs
	//Rotten Weedwood Tree TODO: Tree feature?
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SMALL_HOLLOW_LOG = FEATURES.register("small_hollow_log",
		() -> new SmallHollowLogFeature(NoneFeatureConfiguration.CODEC));
	//Small Spirit Tree
	//Speleothem
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SWAMP_KELP_CLUSTER = FEATURES.register("swamp_kelp_cluster",
		() -> new SwampKelpClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SWAMP_REED_CLUSTER = FEATURES.register("swamp_reed_cluster",
		() -> new SwampReedClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEEDWOOD_BUSH = FEATURES.register("weedwood_bush",
		() -> new WeedwoodBushFeature(NoneFeatureConfiguration.CODEC));
}
