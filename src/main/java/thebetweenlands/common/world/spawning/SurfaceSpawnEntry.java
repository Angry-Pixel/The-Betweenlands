package thebetweenlands.common.world.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SurfaceSpawnEntry(BaseSpawnProperties base, boolean canSpawnOnWater, boolean canSpawnInWater) implements ICustomSpawnEntry {

	public static final MapCodec<SurfaceSpawnEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(BaseSpawnProperties.CODEC.fieldOf("base").forGetter(SurfaceSpawnEntry::base),
					Codec.BOOL.fieldOf("can_spawn_on_water").forGetter(SurfaceSpawnEntry::canSpawnOnWater),
					Codec.BOOL.fieldOf("can_spawn_in_water").forGetter(SurfaceSpawnEntry::canSpawnInWater))
			.apply(instance, SurfaceSpawnEntry::new));

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.SURFACE;
	}
}
