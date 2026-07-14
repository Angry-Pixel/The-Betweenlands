package thebetweenlands.common.world.spawning;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TreeSpawnEntry(BaseSpawnProperties base) implements ICustomSpawnEntry {

	public static final MapCodec<TreeSpawnEntry> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(BaseSpawnProperties.CODEC.fieldOf("base").forGetter(TreeSpawnEntry::base))
					.apply(instance, TreeSpawnEntry::new));

	@Override
	public CustomSpawnType getType() {
		return CustomSpawnType.TREE;
	}
}
