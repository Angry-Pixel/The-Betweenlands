package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.features.*;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;
import thebetweenlands.common.world.gen.feature.mapgen.GiantRoots;
import thebetweenlands.common.world.gen.feature.mapgen.GiantRootsConfiguration;
import thebetweenlands.common.world.gen.feature.structure.IdolHead;
import thebetweenlands.common.world.gen.feature.terrain.CragSpires;
import thebetweenlands.common.world.gen.feature.terrain.TerrainFeatureHelperConfiguration;
import thebetweenlands.common.world.gen.feature.tree.*;

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

	// Trees
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> WEEDWOOD_TREE = FEATURES.register("weedwood_tree", () -> new WeedwoodTree(FeatureHelperConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> SAP_TREE = FEATURES.register("sap_tree", () -> new SapTree(FeatureHelperConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> NIBBLETWIG_TREE = FEATURES.register("nibbletwig_tree", () -> new NibbletwigTree(FeatureHelperConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> RUBBER_TREE = FEATURES.register("rubber_tree", () -> new RubberTree(FeatureHelperConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> HEARTHGROVE_TREE = FEATURES.register("hearthgrove_tree", () -> new HearthgroveTree(FeatureHelperConfiguration.CODEC));

	// Ores
	public static final DeferredHolder<Feature<?>, Feature<OreConfiguration>> BETWEENLANDS_ORE = FEATURES.register("betweenlands_ore", () -> new BetweenlandsOreFeature(OreConfiguration.CODEC));

	// Terrain
	public static final DeferredHolder<Feature<?>, Feature<TerrainFeatureHelperConfiguration>> CRAG_SPIRES = FEATURES.register("crag_spires", () -> new CragSpires(TerrainFeatureHelperConfiguration.CODEC));
	public static final DeferredHolder<Feature<?>, Feature<GiantRootsConfiguration>> GIANT_ROOTS = FEATURES.register("giant_roots", () -> new GiantRoots(GiantRootsConfiguration.CODEC));

	// Plants
	public static final DeferredHolder<Feature<?>, SwampReedCluster> SWAPM_REEDS = FEATURES.register("swamp_reeds", () -> new SwampReedCluster(VegetationPatchConfiguration.CODEC));

	// Structures
	public static final DeferredHolder<Feature<?>, Feature<FeatureHelperConfiguration>> IDOL_HEAD = FEATURES.register("idol_head", () -> new IdolHead(FeatureHelperConfiguration.CODEC));
}
