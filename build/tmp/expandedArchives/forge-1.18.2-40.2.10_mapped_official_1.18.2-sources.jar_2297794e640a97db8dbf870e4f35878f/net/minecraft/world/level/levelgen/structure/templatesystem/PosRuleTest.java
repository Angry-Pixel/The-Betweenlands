package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;

public abstract class PosRuleTest {
   public static final Codec<PosRuleTest> CODEC = Registry.POS_RULE_TEST.byNameCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);

   public abstract boolean test(BlockPos p_74201_, BlockPos p_74202_, BlockPos p_74203_, Random p_74204_);

   protected abstract PosRuleTestType<?> getType();
}