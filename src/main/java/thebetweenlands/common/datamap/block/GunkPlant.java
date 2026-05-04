package thebetweenlands.common.datamap.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public record GunkPlant(float amount) {
	// amount: how much gunk is added for moving 1 meter through a gunk plant block's hitbox
	
	public static final Codec<GunkPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_FLOAT.fieldOf("amount").forGetter(GunkPlant::amount)
	).apply(instance, GunkPlant::new));
	
}
