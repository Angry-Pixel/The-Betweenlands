package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface PosRuleTestType<P extends PosRuleTest> {
   PosRuleTestType<PosAlwaysTrueTest> ALWAYS_TRUE_TEST = register("always_true", PosAlwaysTrueTest.CODEC);
   PosRuleTestType<LinearPosTest> LINEAR_POS_TEST = register("linear_pos", LinearPosTest.CODEC);
   PosRuleTestType<AxisAlignedLinearPosTest> AXIS_ALIGNED_LINEAR_POS_TEST = register("axis_aligned_linear_pos", AxisAlignedLinearPosTest.CODEC);

   Codec<P> codec();

   static <P extends PosRuleTest> PosRuleTestType<P> register(String p_74212_, Codec<P> p_74213_) {
      return Registry.register(Registry.POS_RULE_TEST, p_74212_, () -> {
         return p_74213_;
      });
   }
}