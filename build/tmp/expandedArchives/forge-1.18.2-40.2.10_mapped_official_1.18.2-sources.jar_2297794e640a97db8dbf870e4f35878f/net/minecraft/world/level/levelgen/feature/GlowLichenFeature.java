package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;

public class GlowLichenFeature extends Feature<GlowLichenConfiguration> {
   public GlowLichenFeature(Codec<GlowLichenConfiguration> p_159838_) {
      super(p_159838_);
   }

   public boolean place(FeaturePlaceContext<GlowLichenConfiguration> p_159847_) {
      WorldGenLevel worldgenlevel = p_159847_.level();
      BlockPos blockpos = p_159847_.origin();
      Random random = p_159847_.random();
      GlowLichenConfiguration glowlichenconfiguration = p_159847_.config();
      if (!isAirOrWater(worldgenlevel.getBlockState(blockpos))) {
         return false;
      } else {
         List<Direction> list = getShuffledDirections(glowlichenconfiguration, random);
         if (placeGlowLichenIfPossible(worldgenlevel, blockpos, worldgenlevel.getBlockState(blockpos), glowlichenconfiguration, random, list)) {
            return true;
         } else {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();

            for(Direction direction : list) {
               blockpos$mutableblockpos.set(blockpos);
               List<Direction> list1 = getShuffledDirectionsExcept(glowlichenconfiguration, random, direction.getOpposite());

               for(int i = 0; i < glowlichenconfiguration.searchRange; ++i) {
                  blockpos$mutableblockpos.setWithOffset(blockpos, direction);
                  BlockState blockstate = worldgenlevel.getBlockState(blockpos$mutableblockpos);
                  if (!isAirOrWater(blockstate) && !blockstate.is(Blocks.GLOW_LICHEN)) {
                     break;
                  }

                  if (placeGlowLichenIfPossible(worldgenlevel, blockpos$mutableblockpos, blockstate, glowlichenconfiguration, random, list1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean placeGlowLichenIfPossible(WorldGenLevel p_159840_, BlockPos p_159841_, BlockState p_159842_, GlowLichenConfiguration p_159843_, Random p_159844_, List<Direction> p_159845_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_159841_.mutable();

      for(Direction direction : p_159845_) {
         BlockState blockstate = p_159840_.getBlockState(blockpos$mutableblockpos.setWithOffset(p_159841_, direction));
         if (blockstate.is(p_159843_.canBePlacedOn)) {
            GlowLichenBlock glowlichenblock = (GlowLichenBlock)Blocks.GLOW_LICHEN;
            BlockState blockstate1 = glowlichenblock.getStateForPlacement(p_159842_, p_159840_, p_159841_, direction);
            if (blockstate1 == null) {
               return false;
            }

            p_159840_.setBlock(p_159841_, blockstate1, 3);
            p_159840_.getChunk(p_159841_).markPosForPostprocessing(p_159841_);
            if (p_159844_.nextFloat() < p_159843_.chanceOfSpreading) {
               glowlichenblock.spreadFromFaceTowardRandomDirection(blockstate1, p_159840_, p_159841_, direction, p_159844_, true);
            }

            return true;
         }
      }

      return false;
   }

   public static List<Direction> getShuffledDirections(GlowLichenConfiguration p_159849_, Random p_159850_) {
      List<Direction> list = Lists.newArrayList(p_159849_.validDirections);
      Collections.shuffle(list, p_159850_);
      return list;
   }

   public static List<Direction> getShuffledDirectionsExcept(GlowLichenConfiguration p_159852_, Random p_159853_, Direction p_159854_) {
      List<Direction> list = p_159852_.validDirections.stream().filter((p_159857_) -> {
         return p_159857_ != p_159854_;
      }).collect(Collectors.toList());
      Collections.shuffle(list, p_159853_);
      return list;
   }

   private static boolean isAirOrWater(BlockState p_159859_) {
      return p_159859_.isAir() || p_159859_.is(Blocks.WATER);
   }
}