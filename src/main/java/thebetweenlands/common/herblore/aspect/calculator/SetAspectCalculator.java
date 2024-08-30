package thebetweenlands.common.herblore.aspect.calculator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectCalculatorType;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;

import java.util.List;

public record SetAspectCalculator(float amountMultiplier, float amountVariation, List<ResourceKey<AspectType>> aspects) implements AspectCalculatorType {

	public static final MapCodec<SetAspectCalculator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("amount_multiplier").forGetter(SetAspectCalculator::amountMultiplier),
		Codec.FLOAT.fieldOf("amount_variation").forGetter(SetAspectCalculator::amountVariation),
		ResourceKey.codec(BLRegistries.Keys.ASPECT_TYPES).listOf().fieldOf("aspects").forGetter(SetAspectCalculator::aspects)
	).apply(instance, SetAspectCalculator::new));

	@Override
	public List<Aspect> getAspects(AspectItem item, HolderLookup.Provider provider, LegacyRandomSource random) {
		return this.aspects().stream().map(key -> {
			Holder<AspectType> type = provider.lookupOrThrow(BLRegistries.Keys.ASPECT_TYPES).getOrThrow(key);
			float baseAmount = type.value().amount() * this.amountMultiplier;
			return new Aspect(type, (int) (baseAmount + baseAmount * this.amountVariation * (random.nextFloat() * 2.0F - 1.0F)));
		}).toList();
	}

	@Override
	public MapCodec<? extends AspectCalculatorType> codec() {
		return CODEC;
	}
}
