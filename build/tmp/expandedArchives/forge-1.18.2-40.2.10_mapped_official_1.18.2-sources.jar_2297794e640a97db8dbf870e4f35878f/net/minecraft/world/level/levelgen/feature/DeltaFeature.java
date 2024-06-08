package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;

public class DeltaFeature extends Feature<DeltaFeatureConfiguration> {
   private static final ImmutableList<Block> CANNOT_REPLACE = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final double RIM_SPAWN_CHANCE = 0.9D;

   public DeltaFeature(Codec<DeltaFeatureConfiguration> p_65550_) {
      super(p_65550_);
   }

   public boolean place(FeaturePlaceContext<DeltaFeatureConfiguration> p_159548_) {
      boolean flag = false;
      Random random = p_159548_.random();
      WorldGenLevel worldgenlevel = p_159548_.level();
      DeltaFeatureConfiguration deltafeatureconfiguration = p_159548_.config();
      BlockPos blockpos = p_159548_.origin();
      boolean flag1 = random.nextDouble() < 0.9D;
      int i = flag1 ? deltafeatureconfiguration.rimSize().sample(random) : 0;
      int j = flag1 ? deltafeatureconfiguration.rimSize().sample(random) : 0;
      boolean flag2 = flag1 && i != 0 && j != 0;
      int k = deltafeatureconfiguration.size().sample(random);
      int l = deltafeatureconfiguration.size().sample(random);
      int i1 = Math.max(k, l);

      for(BlockPos blockpos1 : BlockPos.withinManhattan(blockpos, k, 0, l)) {
         if (blockpos1.distManhattan(blockpos) > i1) {
            break;
         }

         if (isClear(worldgenlevel, blockpos1, deltafeatureconfiguration)) {
            if (flag2) {
               flag = true;
               this.setBlock(worldgenlevel, blockpos1, deltafeatureconfiguration.rim());
            }

            BlockPos blockpos2 = blockpos1.offset(i, 0, j);
            if (isClear(worldgenlevel, blockpos2, deltafeatureconfiguration)) {
               flag = true;
               this.setBlock(worldgenlevel, blockpos2, deltafeatureconfiguration.contents());
            }
         }
      }

      return flag;
   }

   private static boolean isClear(LevelAccessor p_65552_, BlockPos p_65553_, DeltaFeatureConfiguration p_65554_) {
      BlockState blockstate = p_65552_.getBlockState(p_65553_);
      if (blockstate.is(p_65554_.contents().getBlock())) {
         return false;
      } else if (CANNOT_REPLACE.contains(blockstate.getBlock())) {
         return false;
      } else {
         for(Direction direction : DIRECTIONS) {
            boolean flag = p_65552_.getBlockState(p_65553_.relative(direction)).isAir();
            if (flag && direction != Direction.UP || !flag && direction == Direction.UP) {
               return false;
            }
         }

         return true;
      }
   }
}