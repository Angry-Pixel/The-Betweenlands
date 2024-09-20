package thebetweenlands.common.item.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

public record DecayFood(int decay, float saturation) {

	public static final Codec<DecayFood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("decay").forGetter(DecayFood::decay),
		Codec.FLOAT.fieldOf("saturation").forGetter(DecayFood::saturation)
	).apply(instance, DecayFood::new));

	public void getDecayFoodTooltip(List<Component> list) {
		list.add(Component.translatable("tooltip.bl.decay_food"));
	}
}
