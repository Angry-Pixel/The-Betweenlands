package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;

public record PitstoneCaveSpawnEntry() implements ICustomSpawnEntry {
	public static final MapCodec<PitstoneCaveSpawnEntry> CODEC = MapCodec.unit(new PitstoneCaveSpawnEntry());

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.PITSTONE_CAVE;
	}
}
