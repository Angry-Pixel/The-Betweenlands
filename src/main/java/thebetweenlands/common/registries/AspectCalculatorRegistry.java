package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectCalculatorType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.calculator.DoubleGroupAspectCalculator;
import thebetweenlands.common.herblore.aspect.calculator.RandomAspectCalculator;
import thebetweenlands.common.herblore.aspect.calculator.SetAspectCalculator;

public class AspectCalculatorRegistry {

	public static final DeferredRegister<MapCodec<? extends AspectCalculatorType>> CALCULATORS = DeferredRegister.create(BLRegistries.ASPECT_CALCULATOR_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<MapCodec<? extends AspectCalculatorType>, MapCodec<DoubleGroupAspectCalculator>> DOUBLE_GROUP = CALCULATORS.register("double_group", () -> DoubleGroupAspectCalculator.CODEC);
	public static final DeferredHolder<MapCodec<? extends AspectCalculatorType>, MapCodec<RandomAspectCalculator>> RANDOM = CALCULATORS.register("random_aspects", () -> RandomAspectCalculator.CODEC);
	public static final DeferredHolder<MapCodec<? extends AspectCalculatorType>, MapCodec<SetAspectCalculator>> SET = CALCULATORS.register("set_aspects", () -> SetAspectCalculator.CODEC);
}
