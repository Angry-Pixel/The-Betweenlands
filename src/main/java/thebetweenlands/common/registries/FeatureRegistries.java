package thebetweenlands.common.registries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.blocks.OctineOreBlock;
import thebetweenlands.common.features.*;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;
import thebetweenlands.common.world.gen.feature.mapgen.GiantRoots;
import thebetweenlands.common.world.gen.feature.mapgen.GiantRootsConfiguration;
import thebetweenlands.common.world.gen.feature.structure.IdolHead;
import thebetweenlands.common.world.gen.feature.terrain.CragSpires;
import thebetweenlands.common.world.gen.feature.terrain.TerrainFeatureHelperConfiguration;
import thebetweenlands.common.world.gen.feature.tree.*;
import thebetweenlands.common.world.gen.placement.SimplexMaskPlacement;

// New task is to port all decorators to feature types
public class FeatureRegistries {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TheBetweenlands.ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, TheBetweenlands.ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, TheBetweenlands.ID);

	// consts for feature placement
	public static final int LAYER_HEIGHT = 120;
	public static final int CAVE_WATER_HEIGHT = 15;
	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 30;
	public static final int CAVE_START = LAYER_HEIGHT - 10;

	// No config
	public static final Codec<NoneFeatureConfiguration> none_config_codec = NoneFeatureConfiguration.CODEC;

	// Raw feature types
	// Trees
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> WEEDWOOD_TREE_FEATURE = FEATURES.register("weedwood_tree", () -> new WeedwoodTree(FeatureHelperConfiguration.CODEC));
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> SAP_TREE_FEATURE = FEATURES.register("sap_tree", () -> new SapTree(FeatureHelperConfiguration.CODEC));
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> NIBBLETWIG_TREE_FEATURE = FEATURES.register("nibbletwig_tree", () -> new NibbletwigTree(FeatureHelperConfiguration.CODEC));
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> RUBBER_TREE_FEATURE = FEATURES.register("rubber_tree", () -> new RubberTree(FeatureHelperConfiguration.CODEC));
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> HEARTHGROVE_TREE_FEATURE = FEATURES.register("hearthgrove_tree", () -> new HearthgroveTree(FeatureHelperConfiguration.CODEC));


	// Ores
	public static final RegistryObject<Feature<OreConfiguration>> BETWEENLANDS_ORE = FEATURES.register("betweenlands_ore", () -> new BetweenlandsOreFeature(OreConfiguration.CODEC));

	// Terrain
	public static final RegistryObject<Feature<TerrainFeatureHelperConfiguration>> CRAG_SPIRES = FEATURES.register("crag_spires", () -> new CragSpires(TerrainFeatureHelperConfiguration.CODEC));
	public static final RegistryObject<Feature<GiantRootsConfiguration>> GIANT_ROOTS = FEATURES.register("giant_roots", () -> new GiantRoots(GiantRootsConfiguration.CODEC));


	// Plants
	public static final SwampReedCluster SWAPM_REEDS_FEATURE = new SwampReedCluster(VegetationPatchConfiguration.CODEC);

	// Structures
	public static final RegistryObject<Feature<FeatureHelperConfiguration>> IDOL_HEAD_FEATURE = FEATURES.register("idol_head", () -> new IdolHead(FeatureHelperConfiguration.CODEC));

	// Giant roots biome list

	public static class BlockFilters {
		static List<FeatureHelperConfiguration.TargetBlockState> LEAVES_SURFACE = Stream.of(
			FeatureHelperConfiguration.SURFACE, FeatureHelperConfiguration.LEAVES
		).flatMap(List::stream).collect(Collectors.toList());
	}

	// Hardcoded Features
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static class ConfigueredFeatures {
		// Trees
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> WEEDWOOD_TREE = FeatureUtils.register("weedwood_tree", WEEDWOOD_TREE_FEATURE.get(), new FeatureHelperConfiguration(
			BlockFilters.LEAVES_SURFACE
		));
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> SAP_TREE = FeatureUtils.register("sap_tree", SAP_TREE_FEATURE.get(), new FeatureHelperConfiguration(
			BlockFilters.LEAVES_SURFACE
		));
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> NIBBLETWIG_TREE = FeatureUtils.register("nibbletwig_tree", NIBBLETWIG_TREE_FEATURE.get(), new FeatureHelperConfiguration(
			BlockFilters.LEAVES_SURFACE
		));
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> RUBBER_TREE = FeatureUtils.register("rubber_tree", RUBBER_TREE_FEATURE.get(), new FeatureHelperConfiguration(
			BlockFilters.LEAVES_SURFACE
		));
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> HEARTHGROVE_TREE = FeatureUtils.register("hearthgrove_tree", HEARTHGROVE_TREE_FEATURE.get(), new FeatureHelperConfiguration(
			BlockFilters.LEAVES_SURFACE
		));

		// Ores
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> SULFUR = FeatureUtils.register("sulfur", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), BlockRegistry.SULFUR_ORE.get().defaultBlockState())), 14, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> SYRMORITE = FeatureUtils.register("syrmorite", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), BlockRegistry.SYRMORITE_ORE.get().defaultBlockState())), 7, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> BONE_ORE = FeatureUtils.register("bone_ore", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), BlockRegistry.SLIMY_BONE_ORE.get().defaultBlockState())), 11, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> OCTINE = FeatureUtils.register("octine", BETWEENLANDS_ORE.get(), new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), ((OctineOreBlock) BlockRegistry.OCTINE_ORE.get()).FeaturePlacementBlockState())), 14, 0f, true));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> SWAMP_DIRT = FeatureUtils.register("swamp_dirt", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), BlockRegistry.SWAMP_DIRT.get().defaultBlockState())), 25, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> LIMESTONE = FeatureUtils.register("limestone", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE), BlockRegistry.LIMESTONE.get().defaultBlockState())), 64, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> VALONITE = FeatureUtils.register("valonite", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.PITSTONE_ORE_REPLACEABLE), BlockRegistry.VALONITE_ORE.get().defaultBlockState())), 5, 0f));
		public static final Holder<ConfiguredFeature<OreConfiguration, ?>> SCABYST = FeatureUtils.register("scabyst", Feature.ORE, new BetweenlandsOreConfiguration(List.of(
			BetweenlandsOreConfiguration.target(new TagMatchTest(TagRegistry.Blocks.PITSTONE_ORE_REPLACEABLE), BlockRegistry.SCABYST_ORE.get().defaultBlockState())), 6, 0f));

		// Plants
		public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> SWAMP_TALLGRASS_PATCH = FeatureUtils.register("swamp_tallgrass_patch", Feature.RANDOM_PATCH,
			FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.SWAMP_TALLGRASS.get()))));

		// Terrain
		public static final Holder<ConfiguredFeature<TerrainFeatureHelperConfiguration, ?>> DEEP_WATERS_CRAG_SPIRES = FeatureUtils.register("deep_waters_crag_spires", CRAG_SPIRES.get(), new TerrainFeatureHelperConfiguration(
			new ResourceLocation(TheBetweenlands.ID, "betweenlands_deep_waters")
		));
		public static final Holder<ConfiguredFeature<GiantRootsConfiguration, ?>> GIANT_ROOTS = FeatureUtils.register("giant_roots", FeatureRegistries.GIANT_ROOTS.get(), new GiantRootsConfiguration(
			FeatureHelperConfiguration.SURFACE
		));

		// Structures
		public static final Holder<ConfiguredFeature<FeatureHelperConfiguration, ?>> IDOL_HEAD = FeatureUtils.register("idol_head", IDOL_HEAD_FEATURE.get(), new FeatureHelperConfiguration(List.of(new FeatureHelperConfiguration.TargetBlockState(new TagMatchTest(TagRegistry.Blocks.PITSTONE_ORE_REPLACEABLE), BlockRegistry.SCABYST_ORE.get().defaultBlockState()))));

		public static void register(IEventBus eventBus) {
		}

	}

	public static class Util {

		// Placement presets
		public static final PlacementModifier DIRT = BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(BlockTags.DIRT));
		public static final PlacementModifier TREE = PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING);

		// Mask presets
		public static final PlacementModifier GEN_SHORT_MASK = new SimplexMaskPlacement();

		// Heightmaps
		public static final PlacementModifier MOTION_BLOCKING_NO_LEAVES = HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
	}

	public static class PlacedFeatures {

		// Trees
		public static final Holder<PlacedFeature> WEEDWOOD_TREE = PlacementUtils.register("weedwood_tree", ConfigueredFeatures.WEEDWOOD_TREE, List.of(
			InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome(), Util.TREE, Util.GEN_SHORT_MASK
		));
		public static final Holder<PlacedFeature> SPARSE_WEEDWOOD_TREE = PlacementUtils.register("sparse_weedwood_tree", ConfigueredFeatures.WEEDWOOD_TREE, List.of(
			RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome(), Util.TREE, Util.GEN_SHORT_MASK
		));
		public static final Holder<PlacedFeature> RARE_WEEDWOOD_TREE = PlacementUtils.register("rare_weedwood_tree", ConfigueredFeatures.WEEDWOOD_TREE, List.of(
			RarityFilter.onAverageOnceEvery(30), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome(), Util.TREE, Util.GEN_SHORT_MASK
		));
		public static final Holder<PlacedFeature> NIBBLETWIG_TREE = PlacementUtils.register("nibbletwig_tree", ConfigueredFeatures.NIBBLETWIG_TREE, List.of(
			InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome(), Util.TREE, Util.GEN_SHORT_MASK
		));
		public static final Holder<PlacedFeature> SAP_TREE = PlacementUtils.register("sap_tree", ConfigueredFeatures.SAP_TREE, List.of(
			RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome(), Util.TREE
		));
		public static final Holder<PlacedFeature> RUBBER_TREE = PlacementUtils.register("rubber_tree", ConfigueredFeatures.RUBBER_TREE, List.of(
			RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome(), Util.TREE
		));
		public static final Holder<PlacedFeature> HEATHGROVE_TREE = PlacementUtils.register("hearthgrove_tree", ConfigueredFeatures.HEARTHGROVE_TREE, List.of(
			RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome(), Util.TREE, Util.GEN_SHORT_MASK
		));

		// Ores
		public static final Holder<PlacedFeature> SULFUR = PlacementUtils.register("sulfur", ConfigueredFeatures.SULFUR, List.of(CountPlacement.of(22), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT), VerticalAnchor.aboveBottom(128))));
		public static final Holder<PlacedFeature> SYRMORITE = PlacementUtils.register("syrmorite", ConfigueredFeatures.SYRMORITE, List.of(CountPlacement.of(6), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT + 40), VerticalAnchor.aboveBottom(CAVE_START - 5))));
		public static final Holder<PlacedFeature> BONE_ORE = PlacementUtils.register("bone_ore", ConfigueredFeatures.BONE_ORE, List.of(CountPlacement.of(5), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT), VerticalAnchor.aboveBottom(128))));
		public static final Holder<PlacedFeature> OCTINE = PlacementUtils.register("octine", ConfigueredFeatures.OCTINE, List.of(CountPlacement.of(5), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(2, 14)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT), VerticalAnchor.aboveBottom(CAVE_START - 40))));
		public static final Holder<PlacedFeature> SWAMP_DIRT = PlacementUtils.register("swamp_dirt", ConfigueredFeatures.SWAMP_DIRT, List.of(CountPlacement.of(4), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT), VerticalAnchor.aboveBottom(CAVE_START - 15))));
		public static final Holder<PlacedFeature> LIMESTONE = PlacementUtils.register("limestone", ConfigueredFeatures.LIMESTONE, List.of(CountPlacement.of(1), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(PITSTONE_HEIGHT), VerticalAnchor.aboveBottom(CAVE_START - 15))));
		public static final Holder<PlacedFeature> VALONITE = PlacementUtils.register("valonite", ConfigueredFeatures.VALONITE, List.of(CountPlacement.of(1), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(PITSTONE_HEIGHT))));
		public static final Holder<PlacedFeature> SCABYST = PlacementUtils.register("scabyst", ConfigueredFeatures.SCABYST, List.of(CountPlacement.of(3), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(PITSTONE_HEIGHT))));
		public static final Holder<PlacedFeature> IDOL_HEAD = PlacementUtils.register("idol_head", ConfigueredFeatures.IDOL_HEAD, List.of(RarityFilter.onAverageOnceEvery(25), RandomOffsetPlacement.of(UniformInt.of(0, 16), UniformInt.of(0, 16)), HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG), BiomeFilter.biome())); //  .uniform(VerticalAnchor.aboveBottom(120),VerticalAnchor.aboveBottom(130))
		// Plants
		public static final Holder<PlacedFeature> SWAMP_TALLGRASS_PATCH = PlacementUtils.register("swamp_tallgrass", ConfigueredFeatures.SWAMP_TALLGRASS_PATCH, List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome())); //  .uniform(VerticalAnchor.aboveBottom(120),VerticalAnchor.aboveBottom(130))

		// Terrain
		public static final Holder<PlacedFeature> DEEP_WATERS_CRAG_SPIRES = PlacementUtils.register("deep_waters_crag_spires", ConfigueredFeatures.DEEP_WATERS_CRAG_SPIRES, List.of(
			CountPlacement.of(1)
		));
		public static final Holder<PlacedFeature> GIANT_ROOTS = PlacementUtils.register("giant_roots", ConfigueredFeatures.GIANT_ROOTS, List.of(
			CountPlacement.of(1)
		));


		public static void register(IEventBus eventBus) {
			PLACED_FEATURES.register(eventBus);
		}
	}

	public static void register(IEventBus eventBus) {
		FEATURES.register(eventBus);
	}
}
