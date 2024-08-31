package thebetweenlands.common.herblore.aspect.calculator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectCalculatorType;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectTier;
import thebetweenlands.api.aspect.registry.AspectType;

import java.util.*;
import java.util.random.RandomGeneratorFactory;

public record RandomAspectCalculator(float amountMultiplier, float amountVariation, int rolls, AspectTier tier, List<ResourceKey<AspectType>> setAspectList) implements AspectCalculatorType {

	public static final MapCodec<RandomAspectCalculator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("amount_multiplier").forGetter(RandomAspectCalculator::amountMultiplier),
		Codec.FLOAT.fieldOf("amount_variation").forGetter(RandomAspectCalculator::amountVariation),
		Codec.INT.fieldOf("rolls").forGetter(RandomAspectCalculator::rolls),
		AspectTier.CODEC.fieldOf("tier").forGetter(RandomAspectCalculator::tier),
		ResourceKey.codec(BLRegistries.Keys.ASPECT_TYPES).listOf().optionalFieldOf("possible_aspects", List.of()).forGetter(RandomAspectCalculator::setAspectList)
	).apply(instance, RandomAspectCalculator::new));

	public RandomAspectCalculator(float amountMultiplier, float amountVariation, AspectTier tier) {
		this(amountMultiplier, amountVariation, 1, tier);
	}

	public RandomAspectCalculator(float amountMultiplier, float amountVariation, int rolls, AspectTier tier) {
		this(amountMultiplier, amountVariation, rolls, tier, List.of());
	}

	@Override
	public List<Aspect> getAspects(HolderLookup.Provider provider, LegacyRandomSource random) {
		List<Aspect> aspects = new ArrayList<>();
		List<Holder<AspectType>> availableAspects = new ArrayList<>(provider.lookupOrThrow(BLRegistries.Keys.ASPECT_TYPES).listElements().toList());

		List<Holder<AspectType>> possibleAspects = new ArrayList<>();

		for (Holder<AspectType> type : availableAspects) {
			if (!this.setAspectList().isEmpty() && this.setAspectList().contains(type.getKey())) {
				possibleAspects.add(type);
			} else if (type.value().rollAsCommonAspect() && type.value().tier() == this.tier()) {
				possibleAspects.add(type);
			}
		}

		for (int i = 0; i < this.rolls(); i++) {
			Collections.shuffle(possibleAspects, RandomGeneratorFactory.getDefault().create(random.seed.get()));
			Holder<AspectType> fetchedAspect = possibleAspects.removeFirst();
			float baseAmount = fetchedAspect.value().amount() * this.amountMultiplier;
			aspects.add(new Aspect(fetchedAspect, (int) (baseAmount + baseAmount * this.amountVariation * (random.nextFloat() * 2.0F - 1.0F))));
		}

		return aspects;
	}

	@Override
	public MapCodec<? extends AspectCalculatorType> codec() {
		return CODEC;
	}
}
