package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;

public record SurfaceSpawnEntry() implements ICustomSpawnEntry {
	public static final MapCodec<SurfaceSpawnEntry> CODEC = MapCodec.unit(new SurfaceSpawnEntry());

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.SURFACE;
	}
}
