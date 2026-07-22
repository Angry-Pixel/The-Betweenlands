package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;

public record SkySpawnEntry() implements ICustomSpawnEntry {
	public static final MapCodec<SkySpawnEntry> CODEC = MapCodec.unit(new SkySpawnEntry());

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.SKY;
	}
}
