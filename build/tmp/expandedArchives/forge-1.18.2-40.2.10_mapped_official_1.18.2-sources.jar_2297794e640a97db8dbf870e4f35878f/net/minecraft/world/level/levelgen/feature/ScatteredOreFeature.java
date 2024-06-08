package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class ScatteredOreFeature extends Feature<OreConfiguration> {
   private static final int MAX_DIST_FROM_ORIGIN = 7;

   ScatteredOreFeature(Codec<OreConfiguration> p_160304_) {
      super(p_160304_);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> p_160306_) {
      WorldGenLevel worldgenlevel = p_160306_.level();
      Random random = p_160306_.random();
      OreConfiguration oreconfiguration = p_160306_.config();
      BlockPos blockpos = p_160306_.origin();
      int i = random.nextInt(oreconfiguration.size + 1);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = 0; j < i; ++j) {
         this.offsetTargetPos(blockpos$mutableblockpos, random, blockpos, Math.min(j, 7));
         BlockState blockstate = worldgenlevel.getBlockState(blockpos$mutableblockpos);

         for(OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : oreconfiguration.targetStates) {
            if (OreFeature.canPlaceOre(blockstate, worldgenlevel::getBlockState, random, oreconfiguration, oreconfiguration$targetblockstate, blockpos$mutableblockpos)) {
               worldgenlevel.setBlock(blockpos$mutableblockpos, oreconfiguration$targetblockstate.state, 2);
               break;
            }
         }
      }

      return true;
   }

   private void offsetTargetPos(BlockPos.MutableBlockPos p_160308_, Random p_160309_, BlockPos p_160310_, int p_160311_) {
      int i = this.getRandomPlacementInOneAxisRelativeToOrigin(p_160309_, p_160311_);
      int j = this.getRandomPlacementInOneAxisRelativeToOrigin(p_160309_, p_160311_);
      int k = this.getRandomPlacementInOneAxisRelativeToOrigin(p_160309_, p_160311_);
      p_160308_.setWithOffset(p_160310_, i, j, k);
   }

   private int getRandomPlacementInOneAxisRelativeToOrigin(Random p_160313_, int p_160314_) {
      return Math.round((p_160313_.nextFloat() - p_160313_.nextFloat()) * (float)p_160314_);
   }
}