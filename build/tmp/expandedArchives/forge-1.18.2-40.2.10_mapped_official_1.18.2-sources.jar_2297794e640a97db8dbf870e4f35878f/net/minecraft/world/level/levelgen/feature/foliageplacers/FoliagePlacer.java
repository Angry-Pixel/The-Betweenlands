package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public abstract class FoliagePlacer {
   public static final Codec<FoliagePlacer> CODEC = Registry.FOLIAGE_PLACER_TYPES.byNameCodec().dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
   protected final IntProvider radius;
   protected final IntProvider offset;

   protected static <P extends FoliagePlacer> P2<Mu<P>, IntProvider, IntProvider> foliagePlacerParts(Instance<P> p_68574_) {
      return p_68574_.group(IntProvider.codec(0, 16).fieldOf("radius").forGetter((p_161449_) -> {
         return p_161449_.radius;
      }), IntProvider.codec(0, 16).fieldOf("offset").forGetter((p_161447_) -> {
         return p_161447_.offset;
      }));
   }

   public FoliagePlacer(IntProvider p_161411_, IntProvider p_161412_) {
      this.radius = p_161411_;
      this.offset = p_161412_;
   }

   protected abstract FoliagePlacerType<?> type();

   public void createFoliage(LevelSimulatedReader p_161414_, BiConsumer<BlockPos, BlockState> p_161415_, Random p_161416_, TreeConfiguration p_161417_, int p_161418_, FoliagePlacer.FoliageAttachment p_161419_, int p_161420_, int p_161421_) {
      this.createFoliage(p_161414_, p_161415_, p_161416_, p_161417_, p_161418_, p_161419_, p_161420_, p_161421_, this.offset(p_161416_));
   }

   protected abstract void createFoliage(LevelSimulatedReader p_161422_, BiConsumer<BlockPos, BlockState> p_161423_, Random p_161424_, TreeConfiguration p_161425_, int p_161426_, FoliagePlacer.FoliageAttachment p_161427_, int p_161428_, int p_161429_, int p_161430_);

   public abstract int foliageHeight(Random p_68568_, int p_68569_, TreeConfiguration p_68570_);

   public int foliageRadius(Random p_68560_, int p_68561_) {
      return this.radius.sample(p_68560_);
   }

   private int offset(Random p_68559_) {
      return this.offset.sample(p_68559_);
   }

   protected abstract boolean shouldSkipLocation(Random p_68562_, int p_68563_, int p_68564_, int p_68565_, int p_68566_, boolean p_68567_);

   protected boolean shouldSkipLocationSigned(Random p_68575_, int p_68576_, int p_68577_, int p_68578_, int p_68579_, boolean p_68580_) {
      int i;
      int j;
      if (p_68580_) {
         i = Math.min(Math.abs(p_68576_), Math.abs(p_68576_ - 1));
         j = Math.min(Math.abs(p_68578_), Math.abs(p_68578_ - 1));
      } else {
         i = Math.abs(p_68576_);
         j = Math.abs(p_68578_);
      }

      return this.shouldSkipLocation(p_68575_, i, p_68577_, j, p_68579_, p_68580_);
   }

   protected void placeLeavesRow(LevelSimulatedReader p_161438_, BiConsumer<BlockPos, BlockState> p_161439_, Random p_161440_, TreeConfiguration p_161441_, BlockPos p_161442_, int p_161443_, int p_161444_, boolean p_161445_) {
      int i = p_161445_ ? 1 : 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = -p_161443_; j <= p_161443_ + i; ++j) {
         for(int k = -p_161443_; k <= p_161443_ + i; ++k) {
            if (!this.shouldSkipLocationSigned(p_161440_, j, p_161444_, k, p_161443_, p_161445_)) {
               blockpos$mutableblockpos.setWithOffset(p_161442_, j, p_161444_, k);
               tryPlaceLeaf(p_161438_, p_161439_, p_161440_, p_161441_, blockpos$mutableblockpos);
            }
         }
      }

   }

   protected static void tryPlaceLeaf(LevelSimulatedReader p_161432_, BiConsumer<BlockPos, BlockState> p_161433_, Random p_161434_, TreeConfiguration p_161435_, BlockPos p_161436_) {
      if (TreeFeature.validTreePos(p_161432_, p_161436_)) {
         p_161433_.accept(p_161436_, p_161435_.foliageProvider.getState(p_161434_, p_161436_));
      }

   }

   public static final class FoliageAttachment {
      private final BlockPos pos;
      private final int radiusOffset;
      private final boolean doubleTrunk;

      public FoliageAttachment(BlockPos p_68585_, int p_68586_, boolean p_68587_) {
         this.pos = p_68585_;
         this.radiusOffset = p_68586_;
         this.doubleTrunk = p_68587_;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public int radiusOffset() {
         return this.radiusOffset;
      }

      public boolean doubleTrunk() {
         return this.doubleTrunk;
      }
   }
}