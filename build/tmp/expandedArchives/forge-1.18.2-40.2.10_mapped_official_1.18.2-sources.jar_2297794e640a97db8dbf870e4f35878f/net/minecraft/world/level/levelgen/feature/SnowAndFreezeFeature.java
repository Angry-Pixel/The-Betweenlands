package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SnowAndFreezeFeature extends Feature<NoneFeatureConfiguration> {
   public SnowAndFreezeFeature(Codec<NoneFeatureConfiguration> p_66836_) {
      super(p_66836_);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160368_) {
      WorldGenLevel worldgenlevel = p_160368_.level();
      BlockPos blockpos = p_160368_.origin();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = blockpos.getX() + i;
            int l = blockpos.getZ() + j;
            int i1 = worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
            blockpos$mutableblockpos.set(k, i1, l);
            blockpos$mutableblockpos1.set(blockpos$mutableblockpos).move(Direction.DOWN, 1);
            Biome biome = worldgenlevel.getBiome(blockpos$mutableblockpos).value();
            if (biome.shouldFreeze(worldgenlevel, blockpos$mutableblockpos1, false)) {
               worldgenlevel.setBlock(blockpos$mutableblockpos1, Blocks.ICE.defaultBlockState(), 2);
            }

            if (biome.shouldSnow(worldgenlevel, blockpos$mutableblockpos)) {
               worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.SNOW.defaultBlockState(), 2);
               BlockState blockstate = worldgenlevel.getBlockState(blockpos$mutableblockpos1);
               if (blockstate.hasProperty(SnowyDirtBlock.SNOWY)) {
                  worldgenlevel.setBlock(blockpos$mutableblockpos1, blockstate.setValue(SnowyDirtBlock.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}