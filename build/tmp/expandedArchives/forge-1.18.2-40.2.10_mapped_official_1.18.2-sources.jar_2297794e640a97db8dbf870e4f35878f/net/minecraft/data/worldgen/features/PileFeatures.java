package net.minecraft.data.worldgen.features;

import net.minecraft.core.Holder;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class PileFeatures {
   public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> PILE_HAY = FeatureUtils.register("pile_hay", Feature.BLOCK_PILE, new BlockPileConfiguration(new RotatedBlockProvider(Blocks.HAY_BLOCK)));
   public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> PILE_MELON = FeatureUtils.register("pile_melon", Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(Blocks.MELON)));
   public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> PILE_SNOW = FeatureUtils.register("pile_snow", Feature.BLOCK_PILE, new BlockPileConfiguration(BlockStateProvider.simple(Blocks.SNOW)));
   public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> PILE_ICE = FeatureUtils.register("pile_ice", Feature.BLOCK_PILE, new BlockPileConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.BLUE_ICE.defaultBlockState(), 1).add(Blocks.PACKED_ICE.defaultBlockState(), 5))));
   public static final Holder<ConfiguredFeature<BlockPileConfiguration, ?>> PILE_PUMPKIN = FeatureUtils.register("pile_pumpkin", Feature.BLOCK_PILE, new BlockPileConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.PUMPKIN.defaultBlockState(), 19).add(Blocks.JACK_O_LANTERN.defaultBlockState(), 1))));
}