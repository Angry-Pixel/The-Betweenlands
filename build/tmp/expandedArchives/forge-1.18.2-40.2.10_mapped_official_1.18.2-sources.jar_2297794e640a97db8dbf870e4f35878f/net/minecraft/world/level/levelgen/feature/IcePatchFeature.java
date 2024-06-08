package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class IcePatchFeature extends BaseDiskFeature {
   public IcePatchFeature(Codec<DiskConfiguration> p_65989_) {
      super(p_65989_);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> p_159880_) {
      WorldGenLevel worldgenlevel = p_159880_.level();
      ChunkGenerator chunkgenerator = p_159880_.chunkGenerator();
      Random random = p_159880_.random();
      DiskConfiguration diskconfiguration = p_159880_.config();

      BlockPos blockpos;
      for(blockpos = p_159880_.origin(); worldgenlevel.isEmptyBlock(blockpos) && blockpos.getY() > worldgenlevel.getMinBuildHeight() + 2; blockpos = blockpos.below()) {
      }

      return !worldgenlevel.getBlockState(blockpos).is(Blocks.SNOW_BLOCK) ? false : super.place(new FeaturePlaceContext<>(p_159880_.topFeature(), worldgenlevel, p_159880_.chunkGenerator(), p_159880_.random(), blockpos, p_159880_.config()));
   }
}