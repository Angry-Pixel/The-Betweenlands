package thebetweenlands.common.datamap.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public record AnimatorFuel(int fuelBurnTime, int fuelValue) {

	public static final Codec<AnimatorFuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_INT.fieldOf("burn_time").forGetter(AnimatorFuel::fuelBurnTime),
		ExtraCodecs.POSITIVE_INT.fieldOf("fuel_value").forGetter(AnimatorFuel::fuelValue)
	).apply(instance, AnimatorFuel::new));
}
