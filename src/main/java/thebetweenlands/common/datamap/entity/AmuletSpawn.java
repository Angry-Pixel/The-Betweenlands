package thebetweenlands.common.datamap.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record AmuletSpawn(int chance) {

	public static final Codec<AmuletSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("chance").forGetter(AmuletSpawn::chance)
	).apply(instance, AmuletSpawn::new));
}
