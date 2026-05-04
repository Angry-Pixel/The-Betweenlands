package thebetweenlands.common.datamap.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.util.ExtraCodecs;

public record GunkPlant(double amount) {
	// amount: how much gunk is added for moving 1 meter through a gunk plant block's hitbox
	
	public static final Codec<GunkPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_DOUBLE.fieldOf("amount").forGetter(GunkPlant::amount)
	).apply(instance, GunkPlant::new));
	
}
