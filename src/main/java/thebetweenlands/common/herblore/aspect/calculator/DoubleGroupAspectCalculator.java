package thebetweenlands.common.herblore.aspect.calculator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectCalculatorType;
import thebetweenlands.api.aspect.registry.AspectItem;

import java.util.ArrayList;
import java.util.List;

public record DoubleGroupAspectCalculator(AspectCalculatorType firstRoll, AspectCalculatorType secondRoll) implements AspectCalculatorType {

	public static final MapCodec<DoubleGroupAspectCalculator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		AspectCalculatorType.CODEC.fieldOf("first_roll").forGetter(DoubleGroupAspectCalculator::firstRoll),
		AspectCalculatorType.CODEC.fieldOf("second_roll").forGetter(DoubleGroupAspectCalculator::secondRoll)
		).apply(instance, DoubleGroupAspectCalculator::new));

	@Override
	public List<Aspect> getAspects(AspectItem item, HolderLookup.Provider provider, LegacyRandomSource random) {
		List<Aspect> aspects = new ArrayList<>();
		aspects.addAll(this.firstRoll().getAspects(item, provider, random));
		aspects.addAll(this.secondRoll().getAspects(item, provider, random));
		return aspects;
	}

	@Override
	public MapCodec<? extends AspectCalculatorType> codec() {
		return CODEC;
	}
}
