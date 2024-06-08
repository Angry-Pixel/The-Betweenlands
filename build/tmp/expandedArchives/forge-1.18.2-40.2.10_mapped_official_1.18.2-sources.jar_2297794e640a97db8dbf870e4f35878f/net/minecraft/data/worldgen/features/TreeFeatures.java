package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.Holder;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BushFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class TreeFeatures {
   public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> CRIMSON_FUNGUS = FeatureUtils.register("crimson_fungus", Feature.HUGE_FUNGUS, new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
   public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> CRIMSON_FUNGUS_PLANTED = FeatureUtils.register("crimson_fungus_planted", Feature.HUGE_FUNGUS, new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
   public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> WARPED_FUNGUS = FeatureUtils.register("warped_fungus", Feature.HUGE_FUNGUS, new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
   public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> WARPED_FUNGUS_PLANTED = FeatureUtils.register("warped_fungus_planted", Feature.HUGE_FUNGUS, new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
   public static final Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> HUGE_BROWN_MUSHROOM = FeatureUtils.register("huge_brown_mushroom", Feature.HUGE_BROWN_MUSHROOM, new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(true)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))), BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))), 3));
   public static final Holder<ConfiguredFeature<HugeMushroomFeatureConfiguration, ?>> HUGE_RED_MUSHROOM = FeatureUtils.register("huge_red_mushroom", Feature.HUGE_RED_MUSHROOM, new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))), BlockStateProvider.simple(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false)).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false))), 2));
   private static final BeehiveDecorator BEEHIVE_0002 = new BeehiveDecorator(0.002F);
   private static final BeehiveDecorator BEEHIVE_002 = new BeehiveDecorator(0.02F);
   private static final BeehiveDecorator BEEHIVE_005 = new BeehiveDecorator(0.05F);
   private static final BeehiveDecorator BEEHIVE = new BeehiveDecorator(1.0F);
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> OAK = FeatureUtils.register("oak", Feature.TREE, createOak().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> DARK_OAK = FeatureUtils.register("dark_oak", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.DARK_OAK_LOG), new DarkOakTrunkPlacer(6, 2, 1), BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES), new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)), new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> BIRCH = FeatureUtils.register("birch", Feature.TREE, createBirch().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> ACACIA = FeatureUtils.register("acacia", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.ACACIA_LOG), new ForkingTrunkPlacer(5, 2, 2), BlockStateProvider.simple(Blocks.ACACIA_LEAVES), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)), new TwoLayersFeatureSize(1, 0, 2))).ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SPRUCE = FeatureUtils.register("spruce", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(5, 2, 1), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> PINE = FeatureUtils.register("pine", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(6, 4, 0), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> JUNGLE_TREE = FeatureUtils.register("jungle_tree", Feature.TREE, createJungleTree().decorators(ImmutableList.of(new CocoaDecorator(0.2F), TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE)).ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> FANCY_OAK = FeatureUtils.register("fancy_oak", Feature.TREE, createFancyOak().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> JUNGLE_TREE_NO_VINE = FeatureUtils.register("jungle_tree_no_vine", Feature.TREE, createJungleTree().ignoreVines().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> MEGA_JUNGLE_TREE = FeatureUtils.register("mega_jungle_tree", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.JUNGLE_LOG), new MegaJungleTrunkPlacer(10, 2, 19), BlockStateProvider.simple(Blocks.JUNGLE_LEAVES), new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> MEGA_SPRUCE = FeatureUtils.register("mega_spruce", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)))).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> MEGA_PINE = FeatureUtils.register("mega_pine", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 7)), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)))).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SUPER_BIRCH_BEES_0002 = FeatureUtils.register("super_birch_bees_0002", Feature.TREE, createSuperBirch().decorators(ImmutableList.of(BEEHIVE_0002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SUPER_BIRCH_BEES = FeatureUtils.register("super_birch_bees", Feature.TREE, createSuperBirch().decorators(ImmutableList.of(BEEHIVE)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SWAMP_OAK = FeatureUtils.register("swamp_oak", Feature.TREE, createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(LeaveVineDecorator.INSTANCE)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> JUNGLE_BUSH = FeatureUtils.register("jungle_bush", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.JUNGLE_LOG), new StraightTrunkPlacer(1, 0, 0), BlockStateProvider.simple(Blocks.OAK_LEAVES), new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2), new TwoLayersFeatureSize(0, 0, 0))).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> AZALEA_TREE = FeatureUtils.register("azalea_tree", Feature.TREE, (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG), new BendingTrunkPlacer(4, 2, 0, 3, UniformInt.of(1, 2)), new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.AZALEA_LEAVES.defaultBlockState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 1)), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50), new TwoLayersFeatureSize(1, 0, 1))).dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT)).forceDirt().build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> OAK_BEES_0002 = FeatureUtils.register("oak_bees_0002", Feature.TREE, createOak().decorators(List.of(BEEHIVE_0002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> OAK_BEES_002 = FeatureUtils.register("oak_bees_002", Feature.TREE, createOak().decorators(List.of(BEEHIVE_002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> OAK_BEES_005 = FeatureUtils.register("oak_bees_005", Feature.TREE, createOak().decorators(List.of(BEEHIVE_005)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> BIRCH_BEES_0002 = FeatureUtils.register("birch_bees_0002", Feature.TREE, createBirch().decorators(List.of(BEEHIVE_0002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> BIRCH_BEES_002 = FeatureUtils.register("birch_bees_002", Feature.TREE, createBirch().decorators(List.of(BEEHIVE_002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> BIRCH_BEES_005 = FeatureUtils.register("birch_bees_005", Feature.TREE, createBirch().decorators(List.of(BEEHIVE_005)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> FANCY_OAK_BEES_0002 = FeatureUtils.register("fancy_oak_bees_0002", Feature.TREE, createFancyOak().decorators(List.of(BEEHIVE_0002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> FANCY_OAK_BEES_002 = FeatureUtils.register("fancy_oak_bees_002", Feature.TREE, createFancyOak().decorators(List.of(BEEHIVE_002)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> FANCY_OAK_BEES_005 = FeatureUtils.register("fancy_oak_bees_005", Feature.TREE, createFancyOak().decorators(List.of(BEEHIVE_005)).build());
   public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> FANCY_OAK_BEES = FeatureUtils.register("fancy_oak_bees", Feature.TREE, createFancyOak().decorators(List.of(BEEHIVE)).build());

   private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block p_195147_, Block p_195148_, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
      return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(p_195147_), new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_), BlockStateProvider.simple(p_195148_), new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
   }

   private static TreeConfiguration.TreeConfigurationBuilder createOak() {
      return createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createBirch() {
      return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 0, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createSuperBirch() {
      return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 6, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createJungleTree() {
      return createStraightBlobTree(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 8, 0, 2);
   }

   private static TreeConfiguration.TreeConfigurationBuilder createFancyOak() {
      return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG), new FancyTrunkPlacer(3, 11, 0), BlockStateProvider.simple(Blocks.OAK_LEAVES), new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines();
   }
}