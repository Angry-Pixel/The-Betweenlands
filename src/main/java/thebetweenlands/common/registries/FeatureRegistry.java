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

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> BARNACLE_CLUSTER = FEATURES.register("barnacle_cluster",
		() -> new BarnacleClusterFeature(BlockPlaceConfiguration.CODEC));
	//Big Bulb Capped Mushroom
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> BLADDERWORT_CLUSTER = FEATURES.register("bladderwort_cluster",
		() -> new BladderwortClusterFeature(NoneFeatureConfiguration.CODEC));
	//Block Replacement Cluster
	//Cave Grass
	//Cave Hangers
	//Cave Moss
	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_POTS = FEATURES.register("cave_pots",
		() -> new CavePotsFeature(NoneFeatureConfiguration.CODEC));
	//Cave Thorns
	//Deepman Simulacrum
	//Double Plant Cluster
	//Fluid Pool
	//Giant Root
	//Lake Cavern Simulacrum
	public static final DeferredHolder<Feature<?>, Feature<ChanceConfiguration>> LYESTONE = FEATURES.register("lyestone",
		() -> new LyestoneFeature(ChanceConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<BlockPlaceConfiguration>> MOSS_CLUSTER = FEATURES.register("moss_cluster",
		() -> new MossClusterFeature(BlockPlaceConfiguration.CODEC));
	//Pebble Cluster
	//Root Pod Roots
	//Rootman Simulacrum
	//Roots Cluster
	//Rotten Logs
	//Rotten Weedwood Tree TODO: Tree feature?
	//Simulacrum
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
