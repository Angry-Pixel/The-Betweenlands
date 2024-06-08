package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature(Codec<NoneFeatureConfiguration> p_65452_) {
      super(p_65452_);
   }

   protected boolean placeFeature(LevelAccessor p_65454_, Random p_65455_, BlockPos p_65456_, BlockState p_65457_) {
      int i = p_65455_.nextInt(3) + 3;
      int j = p_65455_.nextInt(3) + 3;
      int k = p_65455_.nextInt(3) + 3;
      int l = p_65455_.nextInt(3) + 1;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_65456_.mutable();

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= i; ++j1) {
            for(int k1 = 0; k1 <= k; ++k1) {
               blockpos$mutableblockpos.set(i1 + p_65456_.getX(), j1 + p_65456_.getY(), k1 + p_65456_.getZ());
               blockpos$mutableblockpos.move(Direction.DOWN, l);
               if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && !(p_65455_.nextFloat() < 0.1F) && !this.placeCoralBlock(p_65454_, p_65455_, blockpos$mutableblockpos, p_65457_)) {
               }
            }
         }
      }

      return true;
   }
}