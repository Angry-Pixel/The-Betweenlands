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

public class StraightTrunkPlacer extends TrunkPlacer {
   public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create((p_70261_) -> {
      return trunkPlacerParts(p_70261_).apply(p_70261_, StraightTrunkPlacer::new);
   });

   public StraightTrunkPlacer(int p_70248_, int p_70249_, int p_70250_) {
      super(p_70248_, p_70249_, p_70250_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161859_, BiConsumer<BlockPos, BlockState> p_161860_, Random p_161861_, int p_161862_, BlockPos p_161863_, TreeConfiguration p_161864_) {
      setDirtAt(p_161859_, p_161860_, p_161861_, p_161863_.below(), p_161864_);

      for(int i = 0; i < p_161862_; ++i) {
         placeLog(p_161859_, p_161860_, p_161861_, p_161863_.above(i), p_161864_);
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(p_161863_.above(p_161862_), 0, false));
   }
}