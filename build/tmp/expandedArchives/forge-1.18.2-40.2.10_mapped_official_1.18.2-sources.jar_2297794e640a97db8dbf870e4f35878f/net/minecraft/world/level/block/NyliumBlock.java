package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.lighting.LayerLightEngine;

public class NyliumBlock extends Block implements BonemealableBlock {
   public NyliumBlock(BlockBehaviour.Properties p_55057_) {
      super(p_55057_);
   }

   private static boolean canBeNylium(BlockState p_55079_, LevelReader p_55080_, BlockPos p_55081_) {
      BlockPos blockpos = p_55081_.above();
      BlockState blockstate = p_55080_.getBlockState(blockpos);
      int i = LayerLightEngine.getLightBlockInto(p_55080_, p_55079_, p_55081_, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(p_55080_, blockpos));
      return i < p_55080_.getMaxLightLevel();
   }

   public void randomTick(BlockState p_55074_, ServerLevel p_55075_, BlockPos p_55076_, Random p_55077_) {
      if (!canBeNylium(p_55074_, p_55075_, p_55076_)) {
         p_55075_.setBlockAndUpdate(p_55076_, Blocks.NETHERRACK.defaultBlockState());
      }

   }

   public boolean isValidBonemealTarget(BlockGetter p_55064_, BlockPos p_55065_, BlockState p_55066_, boolean p_55067_) {
      return p_55064_.getBlockState(p_55065_.above()).isAir();
   }

   public boolean isBonemealSuccess(Level p_55069_, Random p_55070_, BlockPos p_55071_, BlockState p_55072_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_55059_, Random p_55060_, BlockPos p_55061_, BlockState p_55062_) {
      BlockState blockstate = p_55059_.getBlockState(p_55061_);
      BlockPos blockpos = p_55061_.above();
      ChunkGenerator chunkgenerator = p_55059_.getChunkSource().getGenerator();
      if (blockstate.is(Blocks.CRIMSON_NYLIUM)) {
         NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL.value().place(p_55059_, chunkgenerator, p_55060_, blockpos);
      } else if (blockstate.is(Blocks.WARPED_NYLIUM)) {
         NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL.value().place(p_55059_, chunkgenerator, p_55060_, blockpos);
         NetherFeatures.NETHER_SPROUTS_BONEMEAL.value().place(p_55059_, chunkgenerator, p_55060_, blockpos);
         if (p_55060_.nextInt(8) == 0) {
            NetherFeatures.TWISTING_VINES_BONEMEAL.value().place(p_55059_, chunkgenerator, p_55060_, blockpos);
         }
      }

   }
}