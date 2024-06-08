package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMatchTest extends RuleTest {
   public static final Codec<TagMatchTest> CODEC = TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("tag").xmap(TagMatchTest::new, (p_205065_) -> {
      return p_205065_.tag;
   }).codec();
   private final TagKey<Block> tag;

   public TagMatchTest(TagKey<Block> p_205063_) {
      this.tag = p_205063_;
   }

   public boolean test(BlockState p_74697_, Random p_74698_) {
      return p_74697_.is(this.tag);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.TAG_TEST;
   }
}