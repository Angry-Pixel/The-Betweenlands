package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VoidStartPlatformFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockPos PLATFORM_OFFSET = new BlockPos(8, 3, 8);
   private static final ChunkPos PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_OFFSET);
   private static final int PLATFORM_RADIUS = 16;
   private static final int PLATFORM_RADIUS_CHUNKS = 1;

   public VoidStartPlatformFeature(Codec<NoneFeatureConfiguration> p_67354_) {
      super(p_67354_);
   }

   private static int checkerboardDistance(int p_67356_, int p_67357_, int p_67358_, int p_67359_) {
      return Math.max(Math.abs(p_67356_ - p_67358_), Math.abs(p_67357_ - p_67359_));
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160633_) {
      WorldGenLevel worldgenlevel = p_160633_.level();
      ChunkPos chunkpos = new ChunkPos(p_160633_.origin());
      if (checkerboardDistance(chunkpos.x, chunkpos.z, PLATFORM_ORIGIN_CHUNK.x, PLATFORM_ORIGIN_CHUNK.z) > 1) {
         return true;
      } else {
         BlockPos blockpos = PLATFORM_OFFSET.atY(p_160633_.origin().getY() + PLATFORM_OFFSET.getY());
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int i = chunkpos.getMinBlockZ(); i <= chunkpos.getMaxBlockZ(); ++i) {
            for(int j = chunkpos.getMinBlockX(); j <= chunkpos.getMaxBlockX(); ++j) {
               if (checkerboardDistance(blockpos.getX(), blockpos.getZ(), j, i) <= 16) {
                  blockpos$mutableblockpos.set(j, blockpos.getY(), i);
                  if (blockpos$mutableblockpos.equals(blockpos)) {
                     worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                  } else {
                     worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.STONE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}