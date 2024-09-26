package thebetweenlands.common.item.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;

public record CompostableItem(int time, int amount) {

	public static final Codec<CompostableItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("time").forGetter(CompostableItem::time),
		Codec.INT.fieldOf("amount").forGetter(CompostableItem::amount)

		).apply(instance, CompostableItem::new));
}
