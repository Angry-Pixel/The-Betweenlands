package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class DarkOakTrunkPlacer extends TrunkPlacer {
   public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create((p_70090_) -> {
      return trunkPlacerParts(p_70090_).apply(p_70090_, DarkOakTrunkPlacer::new);
   });

   public DarkOakTrunkPlacer(int p_70077_, int p_70078_, int p_70079_) {
      super(p_70077_, p_70078_, p_70079_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161790_, BiConsumer<BlockPos, BlockState> p_161791_, Random p_161792_, int p_161793_, BlockPos p_161794_, TreeConfiguration p_161795_) {
      List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
      BlockPos blockpos = p_161794_.below();
      setDirtAt(p_161790_, p_161791_, p_161792_, blockpos, p_161795_);
      setDirtAt(p_161790_, p_161791_, p_161792_, blockpos.east(), p_161795_);
      setDirtAt(p_161790_, p_161791_, p_161792_, blockpos.south(), p_161795_);
      setDirtAt(p_161790_, p_161791_, p_161792_, blockpos.south().east(), p_161795_);
      Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_161792_);
      int i = p_161793_ - p_161792_.nextInt(4);
      int j = 2 - p_161792_.nextInt(3);
      int k = p_161794_.getX();
      int l = p_161794_.getY();
      int i1 = p_161794_.getZ();
      int j1 = k;
      int k1 = i1;
      int l1 = l + p_161793_ - 1;

      for(int i2 = 0; i2 < p_161793_; ++i2) {
         if (i2 >= i && j > 0) {
            j1 += direction.getStepX();
            k1 += direction.getStepZ();
            --j;
         }

         int j2 = l + i2;
         BlockPos blockpos1 = new BlockPos(j1, j2, k1);
         if (TreeFeature.isAirOrLeaves(p_161790_, blockpos1)) {
            placeLog(p_161790_, p_161791_, p_161792_, blockpos1, p_161795_);
            placeLog(p_161790_, p_161791_, p_161792_, blockpos1.east(), p_161795_);
            placeLog(p_161790_, p_161791_, p_161792_, blockpos1.south(), p_161795_);
            placeLog(p_161790_, p_161791_, p_161792_, blockpos1.east().south(), p_161795_);
         }
      }

      list.add(new FoliagePlacer.FoliageAttachment(new BlockPos(j1, l1, k1), 0, true));

      for(int l2 = -1; l2 <= 2; ++l2) {
         for(int i3 = -1; i3 <= 2; ++i3) {
            if ((l2 < 0 || l2 > 1 || i3 < 0 || i3 > 1) && p_161792_.nextInt(3) <= 0) {
               int j3 = p_161792_.nextInt(3) + 2;

               for(int k2 = 0; k2 < j3; ++k2) {
                  placeLog(p_161790_, p_161791_, p_161792_, new BlockPos(k + l2, l1 - k2 - 1, i1 + i3), p_161795_);
               }

               list.add(new FoliagePlacer.FoliageAttachment(new BlockPos(j1 + l2, l1, k1 + i3), 0, false));
            }
         }
      }

      return list;
   }
}