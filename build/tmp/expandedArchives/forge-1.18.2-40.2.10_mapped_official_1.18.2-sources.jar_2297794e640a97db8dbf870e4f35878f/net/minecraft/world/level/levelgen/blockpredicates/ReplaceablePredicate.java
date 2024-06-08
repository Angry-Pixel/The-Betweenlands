package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

class ReplaceablePredicate extends StateTestingPredicate {
   public static final Codec<ReplaceablePredicate> CODEC = RecordCodecBuilder.create((p_190529_) -> {
      return stateTestingCodec(p_190529_).apply(p_190529_, ReplaceablePredicate::new);
   });

   public ReplaceablePredicate(Vec3i p_190524_) {
      super(p_190524_);
   }

   protected boolean test(BlockState p_190527_) {
      return p_190527_.getMaterial().isReplaceable();
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.REPLACEABLE;
   }
}