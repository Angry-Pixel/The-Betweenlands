package thebetweenlands.common.datamap.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public record WaterPlant(float gunkAmount) {
	// gunkAmount: how much gunk is added for moving 1 meter through a gunk plant block's hitbox
	
	public static final Codec<WaterPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.POSITIVE_FLOAT.fieldOf("amount").forGetter(WaterPlant::gunkAmount)
	).apply(instance, WaterPlant::new));
	
}
