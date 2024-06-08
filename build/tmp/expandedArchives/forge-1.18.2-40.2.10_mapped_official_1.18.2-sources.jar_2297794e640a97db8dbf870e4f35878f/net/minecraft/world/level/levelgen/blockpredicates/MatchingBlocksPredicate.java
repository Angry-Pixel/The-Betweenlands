package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

class MatchingBlocksPredicate extends StateTestingPredicate {
   private final HolderSet<Block> blocks;
   public static final Codec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.create((p_190491_) -> {
      return stateTestingCodec(p_190491_).and(RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("blocks").forGetter((p_204693_) -> {
         return p_204693_.blocks;
      })).apply(p_190491_, MatchingBlocksPredicate::new);
   });

   public MatchingBlocksPredicate(Vec3i p_204690_, HolderSet<Block> p_204691_) {
      super(p_204690_);
      this.blocks = p_204691_;
   }

   protected boolean test(BlockState p_190487_) {
      return p_190487_.is(this.blocks);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCKS;
   }
}