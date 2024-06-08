package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class AcaciaFoliagePlacer extends FoliagePlacer {
   public static final Codec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68380_) -> {
      return foliagePlacerParts(p_68380_).apply(p_68380_, AcaciaFoliagePlacer::new);
   });

   public AcaciaFoliagePlacer(IntProvider p_161343_, IntProvider p_161344_) {
      super(p_161343_, p_161344_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161346_, BiConsumer<BlockPos, BlockState> p_161347_, Random p_161348_, TreeConfiguration p_161349_, int p_161350_, FoliagePlacer.FoliageAttachment p_161351_, int p_161352_, int p_161353_, int p_161354_) {
      boolean flag = p_161351_.doubleTrunk();
      BlockPos blockpos = p_161351_.pos().above(p_161354_);
      this.placeLeavesRow(p_161346_, p_161347_, p_161348_, p_161349_, blockpos, p_161353_ + p_161351_.radiusOffset(), -1 - p_161352_, flag);
      this.placeLeavesRow(p_161346_, p_161347_, p_161348_, p_161349_, blockpos, p_161353_ - 1, -p_161352_, flag);
      this.placeLeavesRow(p_161346_, p_161347_, p_161348_, p_161349_, blockpos, p_161353_ + p_161351_.radiusOffset() - 1, 0, flag);
   }

   public int foliageHeight(Random p_68389_, int p_68390_, TreeConfiguration p_68391_) {
      return 0;
   }

   protected boolean shouldSkipLocation(Random p_68382_, int p_68383_, int p_68384_, int p_68385_, int p_68386_, boolean p_68387_) {
      if (p_68384_ == 0) {
         return (p_68383_ > 1 || p_68385_ > 1) && p_68383_ != 0 && p_68385_ != 0;
      } else {
         return p_68383_ == p_68386_ && p_68385_ == p_68386_ && p_68386_ > 0;
      }
   }
}