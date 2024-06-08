package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class BaseDiskFeature extends Feature<DiskConfiguration> {
   public BaseDiskFeature(Codec<DiskConfiguration> p_65212_) {
      super(p_65212_);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> p_159448_) {
      DiskConfiguration diskconfiguration = p_159448_.config();
      BlockPos blockpos = p_159448_.origin();
      WorldGenLevel worldgenlevel = p_159448_.level();
      boolean flag = false;
      int i = blockpos.getY();
      int j = i + diskconfiguration.halfHeight();
      int k = i - diskconfiguration.halfHeight() - 1;
      boolean flag1 = diskconfiguration.state().getBlock() instanceof FallingBlock;
      int l = diskconfiguration.radius().sample(p_159448_.random());

      for(int i1 = blockpos.getX() - l; i1 <= blockpos.getX() + l; ++i1) {
         for(int j1 = blockpos.getZ() - l; j1 <= blockpos.getZ() + l; ++j1) {
            int k1 = i1 - blockpos.getX();
            int l1 = j1 - blockpos.getZ();
            if (k1 * k1 + l1 * l1 <= l * l) {
               boolean flag2 = false;

               for(int i2 = j; i2 >= k; --i2) {
                  BlockPos blockpos1 = new BlockPos(i1, i2, j1);
                  BlockState blockstate = worldgenlevel.getBlockState(blockpos1);
                  Block block = blockstate.getBlock();
                  boolean flag3 = false;
                  if (i2 > k) {
                     for(BlockState blockstate1 : diskconfiguration.targets()) {
                        if (blockstate1.is(block)) {
                           worldgenlevel.setBlock(blockpos1, diskconfiguration.state(), 2);
                           this.markAboveForPostProcessing(worldgenlevel, blockpos1);
                           flag = true;
                           flag3 = true;
                           break;
                        }
                     }
                  }

                  if (flag1 && flag2 && blockstate.isAir()) {
                     BlockState blockstate2 = diskconfiguration.state().is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                     worldgenlevel.setBlock(new BlockPos(i1, i2 + 1, j1), blockstate2, 2);
                  }

                  flag2 = flag3;
               }
            }
         }
      }

      return flag;
   }
}