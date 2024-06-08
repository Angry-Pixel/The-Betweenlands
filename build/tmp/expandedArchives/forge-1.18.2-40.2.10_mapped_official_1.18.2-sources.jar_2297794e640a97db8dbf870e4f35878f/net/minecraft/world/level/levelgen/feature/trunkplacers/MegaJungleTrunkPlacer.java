package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer {
   public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create((p_70206_) -> {
      return trunkPlacerParts(p_70206_).apply(p_70206_, MegaJungleTrunkPlacer::new);
   });

   public MegaJungleTrunkPlacer(int p_70193_, int p_70194_, int p_70195_) {
      super(p_70193_, p_70194_, p_70195_);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161852_, BiConsumer<BlockPos, BlockState> p_161853_, Random p_161854_, int p_161855_, BlockPos p_161856_, TreeConfiguration p_161857_) {
      List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
      list.addAll(super.placeTrunk(p_161852_, p_161853_, p_161854_, p_161855_, p_161856_, p_161857_));

      for(int i = p_161855_ - 2 - p_161854_.nextInt(4); i > p_161855_ / 2; i -= 2 + p_161854_.nextInt(4)) {
         float f = p_161854_.nextFloat() * ((float)Math.PI * 2F);
         int j = 0;
         int k = 0;

         for(int l = 0; l < 5; ++l) {
            j = (int)(1.5F + Mth.cos(f) * (float)l);
            k = (int)(1.5F + Mth.sin(f) * (float)l);
            BlockPos blockpos = p_161856_.offset(j, i - 3 + l / 2, k);
            placeLog(p_161852_, p_161853_, p_161854_, blockpos, p_161857_);
         }

         list.add(new FoliagePlacer.FoliageAttachment(p_161856_.offset(j, i, k), -2, false));
      }

      return list;
   }
}