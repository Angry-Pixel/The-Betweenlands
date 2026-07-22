package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;

public record TreeSpawnEntry() implements ICustomSpawnEntry {

	public static final MapCodec<TreeSpawnEntry> CODEC = MapCodec.unit(new TreeSpawnEntry());

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.TREE;
	}
}
