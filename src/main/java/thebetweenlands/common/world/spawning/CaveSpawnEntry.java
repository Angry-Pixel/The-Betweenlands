package thebetweenlands.common.world.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CaveSpawnEntry(BaseSpawnProperties base, boolean canSpawnOnWater, boolean canSpawnInWater, boolean constantWeight) implements ICustomSpawnEntry {

	public static final MapCodec<CaveSpawnEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(BaseSpawnProperties.CODEC.fieldOf("base").forGetter(CaveSpawnEntry::base),
					Codec.BOOL.fieldOf("can_spawn_on_water").forGetter(CaveSpawnEntry::canSpawnOnWater),
					Codec.BOOL.fieldOf("can_spawn_in_water").forGetter(CaveSpawnEntry::canSpawnInWater),
					Codec.BOOL.fieldOf("constant_weight").forGetter(CaveSpawnEntry::constantWeight))
			.apply(instance, CaveSpawnEntry::new));

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.CAVE;
	}
}
