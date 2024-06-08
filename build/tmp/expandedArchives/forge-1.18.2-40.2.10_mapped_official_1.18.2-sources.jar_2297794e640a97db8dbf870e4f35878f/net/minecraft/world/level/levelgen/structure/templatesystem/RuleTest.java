package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
   public static final Codec<RuleTest> CODEC = Registry.RULE_TEST.byNameCodec().dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);

   public abstract boolean test(BlockState p_74310_, Random p_74311_);

   protected abstract RuleTestType<?> getType();
}