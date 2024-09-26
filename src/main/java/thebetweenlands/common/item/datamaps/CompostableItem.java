package thebetweenlands.common.item.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CompostableItem(int amount, int time) {

	public static final Codec<CompostableItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("amount").forGetter(CompostableItem::amount),
		Codec.INT.fieldOf("time").forGetter(CompostableItem::time)
	).apply(instance, CompostableItem::new));
}
