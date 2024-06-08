package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

abstract class CombiningPredicate implements BlockPredicate {
   protected final List<BlockPredicate> predicates;

   protected CombiningPredicate(List<BlockPredicate> p_190455_) {
      this.predicates = p_190455_;
   }

   public static <T extends CombiningPredicate> Codec<T> codec(Function<List<BlockPredicate>, T> p_190459_) {
      return RecordCodecBuilder.create((p_190462_) -> {
         return p_190462_.group(BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter((p_190457_) -> {
            return p_190457_.predicates;
         })).apply(p_190462_, p_190459_);
      });
   }
}