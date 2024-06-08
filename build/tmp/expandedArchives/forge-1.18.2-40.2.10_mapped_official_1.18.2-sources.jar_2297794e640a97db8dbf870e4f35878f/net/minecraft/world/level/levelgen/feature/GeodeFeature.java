package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;

public class GeodeFeature extends Feature<GeodeConfiguration> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public GeodeFeature(Codec<GeodeConfiguration> p_159834_) {
      super(p_159834_);
   }

   public boolean place(FeaturePlaceContext<GeodeConfiguration> p_159836_) {
      GeodeConfiguration geodeconfiguration = p_159836_.config();
      Random random = p_159836_.random();
      BlockPos blockpos = p_159836_.origin();
      WorldGenLevel worldgenlevel = p_159836_.level();
      int i = geodeconfiguration.minGenOffset;
      int j = geodeconfiguration.maxGenOffset;
      List<Pair<BlockPos, Integer>> list = Lists.newLinkedList();
      int k = geodeconfiguration.distributionPoints.sample(random);
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(worldgenlevel.getSeed()));
      NormalNoise normalnoise = NormalNoise.create(worldgenrandom, -4, 1.0D);
      List<BlockPos> list1 = Lists.newLinkedList();
      double d0 = (double)k / (double)geodeconfiguration.outerWallDistance.getMaxValue();
      GeodeLayerSettings geodelayersettings = geodeconfiguration.geodeLayerSettings;
      GeodeBlockSettings geodeblocksettings = geodeconfiguration.geodeBlockSettings;
      GeodeCrackSettings geodecracksettings = geodeconfiguration.geodeCrackSettings;
      double d1 = 1.0D / Math.sqrt(geodelayersettings.filling);
      double d2 = 1.0D / Math.sqrt(geodelayersettings.innerLayer + d0);
      double d3 = 1.0D / Math.sqrt(geodelayersettings.middleLayer + d0);
      double d4 = 1.0D / Math.sqrt(geodelayersettings.outerLayer + d0);
      double d5 = 1.0D / Math.sqrt(geodecracksettings.baseCrackSize + random.nextDouble() / 2.0D + (k > 3 ? d0 : 0.0D));
      boolean flag = (double)random.nextFloat() < geodecracksettings.generateCrackChance;
      int l = 0;

      for(int i1 = 0; i1 < k; ++i1) {
         int j1 = geodeconfiguration.outerWallDistance.sample(random);
         int k1 = geodeconfiguration.outerWallDistance.sample(random);
         int l1 = geodeconfiguration.outerWallDistance.sample(random);
         BlockPos blockpos1 = blockpos.offset(j1, k1, l1);
         BlockState blockstate = worldgenlevel.getBlockState(blockpos1);
         if (blockstate.isAir() || blockstate.is(BlockTags.GEODE_INVALID_BLOCKS)) {
            ++l;
            if (l > geodeconfiguration.invalidBlocksThreshold) {
               return false;
            }
         }

         list.add(Pair.of(blockpos1, geodeconfiguration.pointOffset.sample(random)));
      }

      if (flag) {
         int i2 = random.nextInt(4);
         int j2 = k * 2 + 1;
         if (i2 == 0) {
            list1.add(blockpos.offset(j2, 7, 0));
            list1.add(blockpos.offset(j2, 5, 0));
            list1.add(blockpos.offset(j2, 1, 0));
         } else if (i2 == 1) {
            list1.add(blockpos.offset(0, 7, j2));
            list1.add(blockpos.offset(0, 5, j2));
            list1.add(blockpos.offset(0, 1, j2));
         } else if (i2 == 2) {
            list1.add(blockpos.offset(j2, 7, j2));
            list1.add(blockpos.offset(j2, 5, j2));
            list1.add(blockpos.offset(j2, 1, j2));
         } else {
            list1.add(blockpos.offset(0, 7, 0));
            list1.add(blockpos.offset(0, 5, 0));
            list1.add(blockpos.offset(0, 1, 0));
         }
      }

      List<BlockPos> list2 = Lists.newArrayList();
      Predicate<BlockState> predicate = isReplaceable(geodeconfiguration.geodeBlockSettings.cannotReplace);

      for(BlockPos blockpos3 : BlockPos.betweenClosed(blockpos.offset(i, i, i), blockpos.offset(j, j, j))) {
         double d8 = normalnoise.getValue((double)blockpos3.getX(), (double)blockpos3.getY(), (double)blockpos3.getZ()) * geodeconfiguration.noiseMultiplier;
         double d6 = 0.0D;
         double d7 = 0.0D;

         for(Pair<BlockPos, Integer> pair : list) {
            d6 += Mth.fastInvSqrt(blockpos3.distSqr(pair.getFirst()) + (double)pair.getSecond().intValue()) + d8;
         }

         for(BlockPos blockpos6 : list1) {
            d7 += Mth.fastInvSqrt(blockpos3.distSqr(blockpos6) + (double)geodecracksettings.crackPointOffset) + d8;
         }

         if (!(d6 < d4)) {
            if (flag && d7 >= d5 && d6 < d1) {
               this.safeSetBlock(worldgenlevel, blockpos3, Blocks.AIR.defaultBlockState(), predicate);

               for(Direction direction1 : DIRECTIONS) {
                  BlockPos blockpos2 = blockpos3.relative(direction1);
                  FluidState fluidstate = worldgenlevel.getFluidState(blockpos2);
                  if (!fluidstate.isEmpty()) {
                     worldgenlevel.scheduleTick(blockpos2, fluidstate.getType(), 0);
                  }
               }
            } else if (d6 >= d1) {
               this.safeSetBlock(worldgenlevel, blockpos3, geodeblocksettings.fillingProvider.getState(random, blockpos3), predicate);
            } else if (d6 >= d2) {
               boolean flag1 = (double)random.nextFloat() < geodeconfiguration.useAlternateLayer0Chance;
               if (flag1) {
                  this.safeSetBlock(worldgenlevel, blockpos3, geodeblocksettings.alternateInnerLayerProvider.getState(random, blockpos3), predicate);
               } else {
                  this.safeSetBlock(worldgenlevel, blockpos3, geodeblocksettings.innerLayerProvider.getState(random, blockpos3), predicate);
               }

               if ((!geodeconfiguration.placementsRequireLayer0Alternate || flag1) && (double)random.nextFloat() < geodeconfiguration.usePotentialPlacementsChance) {
                  list2.add(blockpos3.immutable());
               }
            } else if (d6 >= d3) {
               this.safeSetBlock(worldgenlevel, blockpos3, geodeblocksettings.middleLayerProvider.getState(random, blockpos3), predicate);
            } else if (d6 >= d4) {
               this.safeSetBlock(worldgenlevel, blockpos3, geodeblocksettings.outerLayerProvider.getState(random, blockpos3), predicate);
            }
         }
      }

      List<BlockState> list3 = geodeblocksettings.innerPlacements;

      for(BlockPos blockpos4 : list2) {
         BlockState blockstate1 = Util.getRandom(list3, random);

         for(Direction direction : DIRECTIONS) {
            if (blockstate1.hasProperty(BlockStateProperties.FACING)) {
               blockstate1 = blockstate1.setValue(BlockStateProperties.FACING, direction);
            }

            BlockPos blockpos5 = blockpos4.relative(direction);
            BlockState blockstate2 = worldgenlevel.getBlockState(blockpos5);
            if (blockstate1.hasProperty(BlockStateProperties.WATERLOGGED)) {
               blockstate1 = blockstate1.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(blockstate2.getFluidState().isSource()));
            }

            if (BuddingAmethystBlock.canClusterGrowAtState(blockstate2)) {
               this.safeSetBlock(worldgenlevel, blockpos5, blockstate1, predicate);
               break;
            }
         }
      }

      return true;
   }
}