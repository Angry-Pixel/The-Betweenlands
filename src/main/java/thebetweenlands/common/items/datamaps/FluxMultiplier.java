package thebetweenlands.common.items.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FluxMultiplier(float multiplyChance, int multiplier) {

	public static final Codec<FluxMultiplier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("multiply_chance").forGetter(FluxMultiplier::multiplyChance),
		Codec.INT.fieldOf("multiplier").forGetter(FluxMultiplier::multiplier)
	).apply(instance, FluxMultiplier::new));
}
