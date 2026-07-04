package thebetweenlands.common.datamap.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public record WaterPlant(float movementGunk, float passiveGunk) {
	// movementGunk: how much gunk is added for moving all the way through the block's hitbox
	// movementGunk: how much gunk is added each tick for passively being inside the plant's hitbox
	
	public static final Codec<WaterPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.POSITIVE_FLOAT.fieldOf("movement_gunk").forGetter(WaterPlant::movementGunk),
			ExtraCodecs.POSITIVE_FLOAT.fieldOf("passive_gunk").forGetter(WaterPlant::passiveGunk)
	).apply(instance, WaterPlant::new));
	
}
