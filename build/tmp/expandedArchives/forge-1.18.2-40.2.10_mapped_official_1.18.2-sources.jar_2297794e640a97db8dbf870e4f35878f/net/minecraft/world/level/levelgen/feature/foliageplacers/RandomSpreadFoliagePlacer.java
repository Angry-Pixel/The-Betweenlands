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

public class RandomSpreadFoliagePlacer extends FoliagePlacer {
   public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create((p_161522_) -> {
      return foliagePlacerParts(p_161522_).and(p_161522_.group(IntProvider.codec(1, 512).fieldOf("foliage_height").forGetter((p_161537_) -> {
         return p_161537_.foliageHeight;
      }), Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter((p_161524_) -> {
         return p_161524_.leafPlacementAttempts;
      }))).apply(p_161522_, RandomSpreadFoliagePlacer::new);
   });
   private final IntProvider foliageHeight;
   private final int leafPlacementAttempts;

   public RandomSpreadFoliagePlacer(IntProvider p_161506_, IntProvider p_161507_, IntProvider p_161508_, int p_161509_) {
      super(p_161506_, p_161507_);
      this.foliageHeight = p_161508_;
      this.leafPlacementAttempts = p_161509_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161512_, BiConsumer<BlockPos, BlockState> p_161513_, Random p_161514_, TreeConfiguration p_161515_, int p_161516_, FoliagePlacer.FoliageAttachment p_161517_, int p_161518_, int p_161519_, int p_161520_) {
      BlockPos blockpos = p_161517_.pos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();

      for(int i = 0; i < this.leafPlacementAttempts; ++i) {
         blockpos$mutableblockpos.setWithOffset(blockpos, p_161514_.nextInt(p_161519_) - p_161514_.nextInt(p_161519_), p_161514_.nextInt(p_161518_) - p_161514_.nextInt(p_161518_), p_161514_.nextInt(p_161519_) - p_161514_.nextInt(p_161519_));
         tryPlaceLeaf(p_161512_, p_161513_, p_161514_, p_161515_, blockpos$mutableblockpos);
      }

   }

   public int foliageHeight(Random p_161533_, int p_161534_, TreeConfiguration p_161535_) {
      return this.foliageHeight.sample(p_161533_);
   }

   protected boolean shouldSkipLocation(Random p_161526_, int p_161527_, int p_161528_, int p_161529_, int p_161530_, boolean p_161531_) {
      return false;
   }
}