package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MatchingBlockTagPredicate extends StateTestingPredicate {
   final TagKey<Block> tag;
   public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create((p_204688_) -> {
      return stateTestingCodec(p_204688_).and(TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("tag").forGetter((p_204686_) -> {
         return p_204686_.tag;
      })).apply(p_204688_, MatchingBlockTagPredicate::new);
   });

   protected MatchingBlockTagPredicate(Vec3i p_204683_, TagKey<Block> p_204684_) {
      super(p_204683_);
      this.tag = p_204684_;
   }

   protected boolean test(BlockState p_198343_) {
      return p_198343_.is(this.tag);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCK_TAG;
   }
}