package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class AlwaysTrueTest extends RuleTest {
   public static final Codec<AlwaysTrueTest> CODEC = Codec.unit(() -> {
      return AlwaysTrueTest.INSTANCE;
   });
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   private AlwaysTrueTest() {
   }

   public boolean test(BlockState p_73959_, Random p_73960_) {
      return true;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.ALWAYS_TRUE_TEST;
   }
}