package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class GiantTrunkPlacer extends TrunkPlacer {
   public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create((p_70189_) -> {
      return trunkPlacerParts(p_70189_).apply(p_70189_, GiantTrunkPlacer::new);
   });

   public GiantTrunkPlacer(int p_70165_, int p_70166_, int p_70167_) {
      super(p_70165_, p_70166_, p_70167_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.GIANT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161835_, BiConsumer<BlockPos, BlockState> p_161836_, Random p_161837_, int p_161838_, BlockPos p_161839_, TreeConfiguration p_161840_) {
      BlockPos blockpos = p_161839_.below();
      setDirtAt(p_161835_, p_161836_, p_161837_, blockpos, p_161840_);
      setDirtAt(p_161835_, p_161836_, p_161837_, blockpos.east(), p_161840_);
      setDirtAt(p_161835_, p_161836_, p_161837_, blockpos.south(), p_161840_);
      setDirtAt(p_161835_, p_161836_, p_161837_, blockpos.south().east(), p_161840_);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < p_161838_; ++i) {
         placeLogIfFreeWithOffset(p_161835_, p_161836_, p_161837_, blockpos$mutableblockpos, p_161840_, p_161839_, 0, i, 0);
         if (i < p_161838_ - 1) {
            placeLogIfFreeWithOffset(p_161835_, p_161836_, p_161837_, blockpos$mutableblockpos, p_161840_, p_161839_, 1, i, 0);
            placeLogIfFreeWithOffset(p_161835_, p_161836_, p_161837_, blockpos$mutableblockpos, p_161840_, p_161839_, 1, i, 1);
            placeLogIfFreeWithOffset(p_161835_, p_161836_, p_161837_, blockpos$mutableblockpos, p_161840_, p_161839_, 0, i, 1);
         }
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(p_161839_.above(p_161838_), 0, true));
   }

   private static void placeLogIfFreeWithOffset(LevelSimulatedReader p_161842_, BiConsumer<BlockPos, BlockState> p_161843_, Random p_161844_, BlockPos.MutableBlockPos p_161845_, TreeConfiguration p_161846_, BlockPos p_161847_, int p_161848_, int p_161849_, int p_161850_) {
      p_161845_.setWithOffset(p_161847_, p_161848_, p_161849_, p_161850_);
      placeLogIfFree(p_161842_, p_161843_, p_161844_, p_161845_, p_161846_);
   }
}