package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class FancyFoliagePlacer extends BlobFoliagePlacer {
   public static final Codec<FancyFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68518_) -> {
      return blobParts(p_68518_).apply(p_68518_, FancyFoliagePlacer::new);
   });

   public FancyFoliagePlacer(IntProvider p_161397_, IntProvider p_161398_, int p_161399_) {
      super(p_161397_, p_161398_, p_161399_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.FANCY_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161401_, BiConsumer<BlockPos, BlockState> p_161402_, Random p_161403_, TreeConfiguration p_161404_, int p_161405_, FoliagePlacer.FoliageAttachment p_161406_, int p_161407_, int p_161408_, int p_161409_) {
      for(int i = p_161409_; i >= p_161409_ - p_161407_; --i) {
         int j = p_161408_ + (i != p_161409_ && i != p_161409_ - p_161407_ ? 1 : 0);
         this.placeLeavesRow(p_161401_, p_161402_, p_161403_, p_161404_, p_161406_.pos(), j, i, p_161406_.doubleTrunk());
      }

   }

   protected boolean shouldSkipLocation(Random p_68511_, int p_68512_, int p_68513_, int p_68514_, int p_68515_, boolean p_68516_) {
      return Mth.square((float)p_68512_ + 0.5F) + Mth.square((float)p_68514_ + 0.5F) > (float)(p_68515_ * p_68515_);
   }
}