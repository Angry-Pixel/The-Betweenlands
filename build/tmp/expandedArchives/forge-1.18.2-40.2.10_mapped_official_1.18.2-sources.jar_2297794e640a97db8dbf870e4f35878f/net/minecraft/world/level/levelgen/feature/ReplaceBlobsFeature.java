package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ReplaceBlobsFeature extends Feature<ReplaceSphereConfiguration> {
   public ReplaceBlobsFeature(Codec<ReplaceSphereConfiguration> p_66633_) {
      super(p_66633_);
   }

   public boolean place(FeaturePlaceContext<ReplaceSphereConfiguration> p_160214_) {
      ReplaceSphereConfiguration replacesphereconfiguration = p_160214_.config();
      WorldGenLevel worldgenlevel = p_160214_.level();
      Random random = p_160214_.random();
      Block block = replacesphereconfiguration.targetState.getBlock();
      BlockPos blockpos = findTarget(worldgenlevel, p_160214_.origin().mutable().clamp(Direction.Axis.Y, worldgenlevel.getMinBuildHeight() + 1, worldgenlevel.getMaxBuildHeight() - 1), block);
      if (blockpos == null) {
         return false;
      } else {
         int i = replacesphereconfiguration.radius().sample(random);
         int j = replacesphereconfiguration.radius().sample(random);
         int k = replacesphereconfiguration.radius().sample(random);
         int l = Math.max(i, Math.max(j, k));
         boolean flag = false;

         for(BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, i, j, k)) {
            if (blockpos1.distManhattan(blockpos) > l) {
               break;
            }

            BlockState blockstate = worldgenlevel.getBlockState(blockpos1);
            if (blockstate.is(block)) {
               this.setBlock(worldgenlevel, blockpos1, replacesphereconfiguration.replaceState);
               flag = true;
            }
         }

         return flag;
      }
   }

   @Nullable
   private static BlockPos findTarget(LevelAccessor p_66635_, BlockPos.MutableBlockPos p_66636_, Block p_66637_) {
      while(p_66636_.getY() > p_66635_.getMinBuildHeight() + 1) {
         BlockState blockstate = p_66635_.getBlockState(p_66636_);
         if (blockstate.is(p_66637_)) {
            return p_66636_;
         }

         p_66636_.move(Direction.DOWN);
      }

      return null;
   }
}