package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockMatchTest extends RuleTest {
   public static final Codec<RandomBlockMatchTest> CODEC = RecordCodecBuilder.create((p_74270_) -> {
      return p_74270_.group(Registry.BLOCK.byNameCodec().fieldOf("block").forGetter((p_163766_) -> {
         return p_163766_.block;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_163764_) -> {
         return p_163764_.probability;
      })).apply(p_74270_, RandomBlockMatchTest::new);
   });
   private final Block block;
   private final float probability;

   public RandomBlockMatchTest(Block p_74263_, float p_74264_) {
      this.block = p_74263_;
      this.probability = p_74264_;
   }

   public boolean test(BlockState p_74267_, Random p_74268_) {
      return p_74267_.is(this.block) && p_74268_.nextFloat() < this.probability;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.RANDOM_BLOCK_TEST;
   }
}