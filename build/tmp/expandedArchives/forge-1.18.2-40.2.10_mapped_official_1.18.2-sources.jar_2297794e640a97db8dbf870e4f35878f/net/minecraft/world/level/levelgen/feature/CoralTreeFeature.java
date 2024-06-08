package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature(Codec<NoneFeatureConfiguration> p_65488_) {
      super(p_65488_);
   }

   protected boolean placeFeature(LevelAccessor p_65490_, Random p_65491_, BlockPos p_65492_, BlockState p_65493_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_65492_.mutable();
      int i = p_65491_.nextInt(3) + 1;

      for(int j = 0; j < i; ++j) {
         if (!this.placeCoralBlock(p_65490_, p_65491_, blockpos$mutableblockpos, p_65493_)) {
            return true;
         }

         blockpos$mutableblockpos.move(Direction.UP);
      }

      BlockPos blockpos = blockpos$mutableblockpos.immutable();
      int k = p_65491_.nextInt(3) + 2;
      List<Direction> list = Lists.newArrayList(Direction.Plane.HORIZONTAL);
      Collections.shuffle(list, p_65491_);

      for(Direction direction : list.subList(0, k)) {
         blockpos$mutableblockpos.set(blockpos);
         blockpos$mutableblockpos.move(direction);
         int l = p_65491_.nextInt(5) + 2;
         int i1 = 0;

         for(int j1 = 0; j1 < l && this.placeCoralBlock(p_65490_, p_65491_, blockpos$mutableblockpos, p_65493_); ++j1) {
            ++i1;
            blockpos$mutableblockpos.move(Direction.UP);
            if (j1 == 0 || i1 >= 2 && p_65491_.nextFloat() < 0.25F) {
               blockpos$mutableblockpos.move(direction);
               i1 = 0;
            }
         }
      }

      return true;
   }
}