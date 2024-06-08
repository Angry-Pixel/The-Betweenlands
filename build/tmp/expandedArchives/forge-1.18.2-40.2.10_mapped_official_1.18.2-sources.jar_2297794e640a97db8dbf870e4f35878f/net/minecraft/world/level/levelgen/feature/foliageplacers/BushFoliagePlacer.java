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

public class BushFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68454_) -> {
      return blobParts(p_68454_).apply(p_68454_, BushFoliagePlacer::new);
   });

   public BushFoliagePlacer(IntProvider p_161370_, IntProvider p_161371_, int p_161372_) {
      super(p_161370_, p_161371_, p_161372_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161374_, BiConsumer<BlockPos, BlockState> p_161375_, Random p_161376_, TreeConfiguration p_161377_, int p_161378_, FoliagePlacer.FoliageAttachment p_161379_, int p_161380_, int p_161381_, int p_161382_) {
      for(int i = p_161382_; i >= p_161382_ - p_161380_; --i) {
         int j = p_161381_ + p_161379_.radiusOffset() - 1 - i;
         this.placeLeavesRow(p_161374_, p_161375_, p_161376_, p_161377_, p_161379_.pos(), j, i, p_161379_.doubleTrunk());
      }

   }

   protected boolean shouldSkipLocation(Random p_68447_, int p_68448_, int p_68449_, int p_68450_, int p_68451_, boolean p_68452_) {
      return p_68448_ == p_68451_ && p_68450_ == p_68451_ && p_68447_.nextInt(2) == 0;
   }
}