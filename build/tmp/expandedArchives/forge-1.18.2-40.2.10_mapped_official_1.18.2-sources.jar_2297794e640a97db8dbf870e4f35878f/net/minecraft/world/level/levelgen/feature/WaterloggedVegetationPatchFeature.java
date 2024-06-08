package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {
   public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> p_160635_) {
      super(p_160635_);
   }

   protected Set<BlockPos> placeGroundPatch(WorldGenLevel p_160643_, VegetationPatchConfiguration p_160644_, Random p_160645_, BlockPos p_160646_, Predicate<BlockState> p_160647_, int p_160648_, int p_160649_) {
      Set<BlockPos> set = super.placeGroundPatch(p_160643_, p_160644_, p_160645_, p_160646_, p_160647_, p_160648_, p_160649_);
      Set<BlockPos> set1 = new HashSet<>();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(BlockPos blockpos : set) {
         if (!isExposed(p_160643_, set, blockpos, blockpos$mutableblockpos)) {
            set1.add(blockpos);
         }
      }

      for(BlockPos blockpos1 : set1) {
         p_160643_.setBlock(blockpos1, Blocks.WATER.defaultBlockState(), 2);
      }

      return set1;
   }

   private static boolean isExposed(WorldGenLevel p_160656_, Set<BlockPos> p_160657_, BlockPos p_160658_, BlockPos.MutableBlockPos p_160659_) {
      return isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.NORTH) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.EAST) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.SOUTH) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.WEST) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.DOWN);
   }

   private static boolean isExposedDirection(WorldGenLevel p_160651_, BlockPos p_160652_, BlockPos.MutableBlockPos p_160653_, Direction p_160654_) {
      p_160653_.setWithOffset(p_160652_, p_160654_);
      return !p_160651_.getBlockState(p_160653_).isFaceSturdy(p_160651_, p_160653_, p_160654_.getOpposite());
   }

   protected boolean placeVegetation(WorldGenLevel p_160637_, VegetationPatchConfiguration p_160638_, ChunkGenerator p_160639_, Random p_160640_, BlockPos p_160641_) {
      if (super.placeVegetation(p_160637_, p_160638_, p_160639_, p_160640_, p_160641_.below())) {
         BlockState blockstate = p_160637_.getBlockState(p_160641_);
         if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && !blockstate.getValue(BlockStateProperties.WATERLOGGED)) {
            p_160637_.setBlock(p_160641_, blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 2);
         }

         return true;
      } else {
         return false;
      }
   }
}