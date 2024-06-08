package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;

public class PosAlwaysTrueTest extends PosRuleTest {
   public static final Codec<PosAlwaysTrueTest> CODEC = Codec.unit(() -> {
      return PosAlwaysTrueTest.INSTANCE;
   });
   public static final PosAlwaysTrueTest INSTANCE = new PosAlwaysTrueTest();

   private PosAlwaysTrueTest() {
   }

   public boolean test(BlockPos p_74193_, BlockPos p_74194_, BlockPos p_74195_, Random p_74196_) {
      return true;
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.ALWAYS_TRUE_TEST;
   }
}