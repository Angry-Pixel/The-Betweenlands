package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface RuleTestType<P extends RuleTest> {
   RuleTestType<AlwaysTrueTest> ALWAYS_TRUE_TEST = register("always_true", AlwaysTrueTest.CODEC);
   RuleTestType<BlockMatchTest> BLOCK_TEST = register("block_match", BlockMatchTest.CODEC);
   RuleTestType<BlockStateMatchTest> BLOCKSTATE_TEST = register("blockstate_match", BlockStateMatchTest.CODEC);
   RuleTestType<TagMatchTest> TAG_TEST = register("tag_match", TagMatchTest.CODEC);
   RuleTestType<RandomBlockMatchTest> RANDOM_BLOCK_TEST = register("random_block_match", RandomBlockMatchTest.CODEC);
   RuleTestType<RandomBlockStateMatchTest> RANDOM_BLOCKSTATE_TEST = register("random_blockstate_match", RandomBlockStateMatchTest.CODEC);

   Codec<P> codec();

   static <P extends RuleTest> RuleTestType<P> register(String p_74322_, Codec<P> p_74323_) {
      return Registry.register(Registry.RULE_TEST, p_74322_, () -> {
         return p_74323_;
      });
   }
}