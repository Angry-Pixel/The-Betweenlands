package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature extends Feature<NoneFeatureConfiguration> {
   public static final int PODIUM_RADIUS = 4;
   public static final int PODIUM_PILLAR_HEIGHT = 4;
   public static final int RIM_RADIUS = 1;
   public static final float CORNER_ROUNDING = 0.5F;
   public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
   private final boolean active;

   public EndPodiumFeature(boolean p_65718_) {
      super(NoneFeatureConfiguration.CODEC);
      this.active = p_65718_;
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159723_) {
      BlockPos blockpos = p_159723_.origin();
      WorldGenLevel worldgenlevel = p_159723_.level();

      for(BlockPos blockpos1 : BlockPos.betweenClosed(new BlockPos(blockpos.getX() - 4, blockpos.getY() - 1, blockpos.getZ() - 4), new BlockPos(blockpos.getX() + 4, blockpos.getY() + 32, blockpos.getZ() + 4))) {
         boolean flag = blockpos1.closerThan(blockpos, 2.5D);
         if (flag || blockpos1.closerThan(blockpos, 3.5D)) {
            if (blockpos1.getY() < blockpos.getY()) {
               if (flag) {
                  this.setBlock(worldgenlevel, blockpos1, Blocks.BEDROCK.defaultBlockState());
               } else if (blockpos1.getY() < blockpos.getY()) {
                  this.setBlock(worldgenlevel, blockpos1, Blocks.END_STONE.defaultBlockState());
               }
            } else if (blockpos1.getY() > blockpos.getY()) {
               this.setBlock(worldgenlevel, blockpos1, Blocks.AIR.defaultBlockState());
            } else if (!flag) {
               this.setBlock(worldgenlevel, blockpos1, Blocks.BEDROCK.defaultBlockState());
            } else if (this.active) {
               this.setBlock(worldgenlevel, new BlockPos(blockpos1), Blocks.END_PORTAL.defaultBlockState());
            } else {
               this.setBlock(worldgenlevel, new BlockPos(blockpos1), Blocks.AIR.defaultBlockState());
            }
         }
      }

      for(int i = 0; i < 4; ++i) {
         this.setBlock(worldgenlevel, blockpos.above(i), Blocks.BEDROCK.defaultBlockState());
      }

      BlockPos blockpos2 = blockpos.above(2);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         this.setBlock(worldgenlevel, blockpos2.relative(direction), Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction));
      }

      return true;
   }
}