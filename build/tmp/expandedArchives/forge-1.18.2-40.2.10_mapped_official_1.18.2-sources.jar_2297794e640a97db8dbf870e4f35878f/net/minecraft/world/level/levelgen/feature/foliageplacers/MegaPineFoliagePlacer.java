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

public class MegaPineFoliagePlacer extends FoliagePlacer {
   public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68664_) -> {
      return foliagePlacerParts(p_68664_).and(IntProvider.codec(0, 24).fieldOf("crown_height").forGetter((p_161484_) -> {
         return p_161484_.crownHeight;
      })).apply(p_68664_, MegaPineFoliagePlacer::new);
   });
   private final IntProvider crownHeight;

   public MegaPineFoliagePlacer(IntProvider p_161470_, IntProvider p_161471_, IntProvider p_161472_) {
      super(p_161470_, p_161471_);
      this.crownHeight = p_161472_;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161474_, BiConsumer<BlockPos, BlockState> p_161475_, Random p_161476_, TreeConfiguration p_161477_, int p_161478_, FoliagePlacer.FoliageAttachment p_161479_, int p_161480_, int p_161481_, int p_161482_) {
      BlockPos blockpos = p_161479_.pos();
      int i = 0;

      for(int j = blockpos.getY() - p_161480_ + p_161482_; j <= blockpos.getY() + p_161482_; ++j) {
         int k = blockpos.getY() - j;
         int l = p_161481_ + p_161479_.radiusOffset() + Mth.floor((float)k / (float)p_161480_ * 3.5F);
         int i1;
         if (k > 0 && l == i && (j & 1) == 0) {
            i1 = l + 1;
         } else {
            i1 = l;
         }

         this.placeLeavesRow(p_161474_, p_161475_, p_161476_, p_161477_, new BlockPos(blockpos.getX(), j, blockpos.getZ()), i1, 0, p_161479_.doubleTrunk());
         i = l;
      }

   }

   public int foliageHeight(Random p_68673_, int p_68674_, TreeConfiguration p_68675_) {
      return this.crownHeight.sample(p_68673_);
   }

   protected boolean shouldSkipLocation(Random p_68666_, int p_68667_, int p_68668_, int p_68669_, int p_68670_, boolean p_68671_) {
      if (p_68667_ + p_68669_ >= 7) {
         return true;
      } else {
         return p_68667_ * p_68667_ + p_68669_ * p_68669_ > p_68670_ * p_68670_;
      }
   }
}