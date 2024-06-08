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

public class SpruceFoliagePlacer extends FoliagePlacer {
   public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68735_) -> {
      return foliagePlacerParts(p_68735_).and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter((p_161553_) -> {
         return p_161553_.trunkHeight;
      })).apply(p_68735_, SpruceFoliagePlacer::new);
   });
   private final IntProvider trunkHeight;

   public SpruceFoliagePlacer(IntProvider p_161539_, IntProvider p_161540_, IntProvider p_161541_) {
      super(p_161539_, p_161540_);
      this.trunkHeight = p_161541_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161543_, BiConsumer<BlockPos, BlockState> p_161544_, Random p_161545_, TreeConfiguration p_161546_, int p_161547_, FoliagePlacer.FoliageAttachment p_161548_, int p_161549_, int p_161550_, int p_161551_) {
      BlockPos blockpos = p_161548_.pos();
      int i = p_161545_.nextInt(2);
      int j = 1;
      int k = 0;

      for(int l = p_161551_; l >= -p_161549_; --l) {
         this.placeLeavesRow(p_161543_, p_161544_, p_161545_, p_161546_, blockpos, i, l, p_161548_.doubleTrunk());
         if (i >= j) {
            i = k;
            k = 1;
            j = Math.min(j + 1, p_161550_ + p_161548_.radiusOffset());
         } else {
            ++i;
         }
      }

   }

   public int foliageHeight(Random p_68744_, int p_68745_, TreeConfiguration p_68746_) {
      return Math.max(4, p_68745_ - this.trunkHeight.sample(p_68744_));
   }

   protected boolean shouldSkipLocation(Random p_68737_, int p_68738_, int p_68739_, int p_68740_, int p_68741_, boolean p_68742_) {
      return p_68738_ == p_68741_ && p_68740_ == p_68741_ && p_68741_ > 0;
   }
}