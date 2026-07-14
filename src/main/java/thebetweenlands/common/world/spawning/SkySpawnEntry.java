package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SkySpawnEntry(BaseSpawnProperties base) implements ICustomSpawnEntry {

	public static final MapCodec<SkySpawnEntry> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(BaseSpawnProperties.CODEC.fieldOf("base").forGetter(SkySpawnEntry::base))
					.apply(instance, SkySpawnEntry::new));

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.SKY;
	}
}
