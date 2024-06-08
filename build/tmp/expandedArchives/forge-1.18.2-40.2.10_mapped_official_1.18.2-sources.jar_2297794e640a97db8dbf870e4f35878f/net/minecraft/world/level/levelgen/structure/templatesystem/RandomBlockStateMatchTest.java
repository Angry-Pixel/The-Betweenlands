package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockStateMatchTest extends RuleTest {
   public static final Codec<RandomBlockStateMatchTest> CODEC = RecordCodecBuilder.create((p_74287_) -> {
      return p_74287_.group(BlockState.CODEC.fieldOf("block_state").forGetter((p_163770_) -> {
         return p_163770_.blockState;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_163768_) -> {
         return p_163768_.probability;
      })).apply(p_74287_, RandomBlockStateMatchTest::new);
   });
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchTest(BlockState p_74280_, float p_74281_) {
      this.blockState = p_74280_;
      this.probability = p_74281_;
   }

   public boolean test(BlockState p_74284_, Random p_74285_) {
      return p_74284_ == this.blockState && p_74285_.nextFloat() < this.probability;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.RANDOM_BLOCKSTATE_TEST;
   }
}