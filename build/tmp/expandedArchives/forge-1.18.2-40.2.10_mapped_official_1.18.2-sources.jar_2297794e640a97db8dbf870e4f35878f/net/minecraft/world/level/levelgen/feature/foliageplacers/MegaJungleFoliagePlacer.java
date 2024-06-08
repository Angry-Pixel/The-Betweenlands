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

public class MegaJungleFoliagePlacer extends FoliagePlacer {
   public static final Codec<MegaJungleFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68630_) -> {
      return foliagePlacerParts(p_68630_).and(Codec.intRange(0, 16).fieldOf("height").forGetter((p_161468_) -> {
         return p_161468_.height;
      })).apply(p_68630_, MegaJungleFoliagePlacer::new);
   });
   protected final int height;

   public MegaJungleFoliagePlacer(IntProvider p_161454_, IntProvider p_161455_, int p_161456_) {
      super(p_161454_, p_161455_);
      this.height = p_161456_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_JUNGLE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161458_, BiConsumer<BlockPos, BlockState> p_161459_, Random p_161460_, TreeConfiguration p_161461_, int p_161462_, FoliagePlacer.FoliageAttachment p_161463_, int p_161464_, int p_161465_, int p_161466_) {
      int i = p_161463_.doubleTrunk() ? p_161464_ : 1 + p_161460_.nextInt(2);

      for(int j = p_161466_; j >= p_161466_ - i; --j) {
         int k = p_161465_ + p_161463_.radiusOffset() + 1 - j;
         this.placeLeavesRow(p_161458_, p_161459_, p_161460_, p_161461_, p_161463_.pos(), k, j, p_161463_.doubleTrunk());
      }

   }

   public int foliageHeight(Random p_68639_, int p_68640_, TreeConfiguration p_68641_) {
      return this.height;
   }

   protected boolean shouldSkipLocation(Random p_68632_, int p_68633_, int p_68634_, int p_68635_, int p_68636_, boolean p_68637_) {
      if (p_68633_ + p_68635_ >= 7) {
         return true;
      } else {
         return p_68633_ * p_68633_ + p_68635_ * p_68635_ > p_68636_ * p_68636_;
      }
   }
}