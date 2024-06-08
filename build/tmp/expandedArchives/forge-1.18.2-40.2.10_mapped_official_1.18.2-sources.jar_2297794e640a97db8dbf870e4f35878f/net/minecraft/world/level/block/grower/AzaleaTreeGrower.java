package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AzaleaTreeGrower extends AbstractTreeGrower {
   @Nullable
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204313_, boolean p_204314_) {
      return TreeFeatures.AZALEA_TREE;
   }
}