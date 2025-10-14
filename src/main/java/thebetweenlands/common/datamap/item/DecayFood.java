package thebetweenlands.common.datamap.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DecayFood(int decay, float saturation) {

	public static final Codec<DecayFood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("decay").forGetter(DecayFood::decay),
		Codec.FLOAT.fieldOf("saturation").forGetter(DecayFood::saturation)
	).apply(instance, DecayFood::new));
}
