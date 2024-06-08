package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204334_, boolean p_204335_) {
      return TreeFeatures.SPRUCE;
   }

   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(Random p_204332_) {
      return p_204332_.nextBoolean() ? TreeFeatures.MEGA_SPRUCE : TreeFeatures.MEGA_PINE;
   }
}