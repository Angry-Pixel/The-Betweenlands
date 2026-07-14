package thebetweenlands.common.world.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PitstoneCaveSpawnEntry(BaseSpawnProperties base, boolean canSpawnOnWater, boolean canSpawnInWater, boolean constantWeight) implements ICustomSpawnEntry {

	public static final MapCodec<PitstoneCaveSpawnEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(BaseSpawnProperties.CODEC.fieldOf("base").forGetter(PitstoneCaveSpawnEntry::base),
					Codec.BOOL.fieldOf("can_spawn_on_water").forGetter(PitstoneCaveSpawnEntry::canSpawnOnWater),
					Codec.BOOL.fieldOf("can_spawn_in_water").forGetter(PitstoneCaveSpawnEntry::canSpawnInWater),
					Codec.BOOL.fieldOf("constant_weight").forGetter(PitstoneCaveSpawnEntry::constantWeight))
			.apply(instance, PitstoneCaveSpawnEntry::new));

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.PITSTONE_CAVE;
	}
}
