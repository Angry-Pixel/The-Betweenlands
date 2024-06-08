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

public class DarkOakFoliagePlacer extends FoliagePlacer {
   public static final Codec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68473_) -> {
      return foliagePlacerParts(p_68473_).apply(p_68473_, DarkOakFoliagePlacer::new);
   });

   public DarkOakFoliagePlacer(IntProvider p_161384_, IntProvider p_161385_) {
      super(p_161384_, p_161385_);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader p_161387_, BiConsumer<BlockPos, BlockState> p_161388_, Random p_161389_, TreeConfiguration p_161390_, int p_161391_, FoliagePlacer.FoliageAttachment p_161392_, int p_161393_, int p_161394_, int p_161395_) {
      BlockPos blockpos = p_161392_.pos().above(p_161395_);
      boolean flag = p_161392_.doubleTrunk();
      if (flag) {
         this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_ + 2, -1, flag);
         this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_ + 3, 0, flag);
         this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_ + 2, 1, flag);
         if (p_161389_.nextBoolean()) {
            this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_, 2, flag);
         }
      } else {
         this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_ + 2, -1, flag);
         this.placeLeavesRow(p_161387_, p_161388_, p_161389_, p_161390_, blockpos, p_161394_ + 1, 0, flag);
      }

   }

   public int foliageHeight(Random p_68482_, int p_68483_, TreeConfiguration p_68484_) {
      return 4;
   }

   protected boolean shouldSkipLocationSigned(Random p_68486_, int p_68487_, int p_68488_, int p_68489_, int p_68490_, boolean p_68491_) {
      return p_68488_ != 0 || !p_68491_ || p_68487_ != -p_68490_ && p_68487_ < p_68490_ || p_68489_ != -p_68490_ && p_68489_ < p_68490_ ? super.shouldSkipLocationSigned(p_68486_, p_68487_, p_68488_, p_68489_, p_68490_, p_68491_) : true;
   }

   protected boolean shouldSkipLocation(Random p_68475_, int p_68476_, int p_68477_, int p_68478_, int p_68479_, boolean p_68480_) {
      if (p_68477_ == -1 && !p_68480_) {
         return p_68476_ == p_68479_ && p_68478_ == p_68479_;
      } else if (p_68477_ == 1) {
         return p_68476_ + p_68478_ > p_68479_ * 2 - 2;
      } else {
         return false;
      }
   }
}