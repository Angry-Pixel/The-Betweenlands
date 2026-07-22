package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;

public record CaveSpawnEntry() implements ICustomSpawnEntry {
	public static final MapCodec<CaveSpawnEntry> CODEC = MapCodec.unit(new CaveSpawnEntry());

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.CAVE;
	}
}
