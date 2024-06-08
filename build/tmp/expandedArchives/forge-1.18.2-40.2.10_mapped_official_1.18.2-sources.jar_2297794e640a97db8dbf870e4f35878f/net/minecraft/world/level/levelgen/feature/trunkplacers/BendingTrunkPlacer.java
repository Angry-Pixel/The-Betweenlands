package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {
   public static final Codec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.create((p_161786_) -> {
      return trunkPlacerParts(p_161786_).and(p_161786_.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", 1).forGetter((p_161788_) -> {
         return p_161788_.minHeightForLeaves;
      }), IntProvider.codec(1, 64).fieldOf("bend_length").forGetter((p_161784_) -> {
         return p_161784_.bendLength;
      }))).apply(p_161786_, BendingTrunkPlacer::new);
   });
   private final int minHeightForLeaves;
   private final IntProvider bendLength;

   public BendingTrunkPlacer(int p_161770_, int p_161771_, int p_161772_, int p_161773_, IntProvider p_161774_) {
      super(p_161770_, p_161771_, p_161772_);
      this.minHeightForLeaves = p_161773_;
      this.bendLength = p_161774_;
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.BENDING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161777_, BiConsumer<BlockPos, BlockState> p_161778_, Random p_161779_, int p_161780_, BlockPos p_161781_, TreeConfiguration p_161782_) {
      Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_161779_);
      int i = p_161780_ - 1;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_161781_.mutable();
      BlockPos blockpos = blockpos$mutableblockpos.below();
      setDirtAt(p_161777_, p_161778_, p_161779_, blockpos, p_161782_);
      List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();

      for(int j = 0; j <= i; ++j) {
         if (j + 1 >= i + p_161779_.nextInt(2)) {
            blockpos$mutableblockpos.move(direction);
         }

         if (TreeFeature.validTreePos(p_161777_, blockpos$mutableblockpos)) {
            placeLog(p_161777_, p_161778_, p_161779_, blockpos$mutableblockpos, p_161782_);
         }

         if (j >= this.minHeightForLeaves) {
            list.add(new FoliagePlacer.FoliageAttachment(blockpos$mutableblockpos.immutable(), 0, false));
         }

         blockpos$mutableblockpos.move(Direction.UP);
      }

      int l = this.bendLength.sample(p_161779_);

      for(int k = 0; k <= l; ++k) {
         if (TreeFeature.validTreePos(p_161777_, blockpos$mutableblockpos)) {
            placeLog(p_161777_, p_161778_, p_161779_, blockpos$mutableblockpos, p_161782_);
         }

         list.add(new FoliagePlacer.FoliageAttachment(blockpos$mutableblockpos.immutable(), 0, false));
         blockpos$mutableblockpos.move(direction);
      }

      return list;
   }
}