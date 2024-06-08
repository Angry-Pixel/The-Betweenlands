package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class RandomOffsetPlacement extends PlacementModifier {
   public static final Codec<RandomOffsetPlacement> CODEC = RecordCodecBuilder.create((p_191883_) -> {
      return p_191883_.group(IntProvider.codec(-16, 16).fieldOf("xz_spread").forGetter((p_191894_) -> {
         return p_191894_.xzSpread;
      }), IntProvider.codec(-16, 16).fieldOf("y_spread").forGetter((p_191885_) -> {
         return p_191885_.ySpread;
      })).apply(p_191883_, RandomOffsetPlacement::new);
   });
   private final IntProvider xzSpread;
   private final IntProvider ySpread;

   public static RandomOffsetPlacement of(IntProvider p_191880_, IntProvider p_191881_) {
      return new RandomOffsetPlacement(p_191880_, p_191881_);
   }

   public static RandomOffsetPlacement vertical(IntProvider p_191878_) {
      return new RandomOffsetPlacement(ConstantInt.of(0), p_191878_);
   }

   public static RandomOffsetPlacement horizontal(IntProvider p_191892_) {
      return new RandomOffsetPlacement(p_191892_, ConstantInt.of(0));
   }

   private RandomOffsetPlacement(IntProvider p_191875_, IntProvider p_191876_) {
      this.xzSpread = p_191875_;
      this.ySpread = p_191876_;
   }

   public Stream<BlockPos> getPositions(PlacementContext p_191887_, Random p_191888_, BlockPos p_191889_) {
      int i = p_191889_.getX() + this.xzSpread.sample(p_191888_);
      int j = p_191889_.getY() + this.ySpread.sample(p_191888_);
      int k = p_191889_.getZ() + this.xzSpread.sample(p_191888_);
      return Stream.of(new BlockPos(i, j, k));
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.RANDOM_OFFSET;
   }
}