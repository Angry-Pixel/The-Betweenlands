package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AllOfPredicate extends CombiningPredicate {
   public static final Codec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

   public AllOfPredicate(List<BlockPredicate> p_190373_) {
      super(p_190373_);
   }

   public boolean test(WorldGenLevel p_190376_, BlockPos p_190377_) {
      for(BlockPredicate blockpredicate : this.predicates) {
         if (!blockpredicate.test(p_190376_, p_190377_)) {
            return false;
         }
      }

      return true;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ALL_OF;
   }
}