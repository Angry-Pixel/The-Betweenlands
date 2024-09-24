package thebetweenlands.common.world.gen.warp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TerrainPoint(short weight, float depth, float scale) {
	public static final Codec<TerrainPoint> CODEC = RecordCodecBuilder.create((instance) ->
		instance.group(
			Codec.SHORT.fieldOf("weight").forGetter((object) -> object.weight),
			Codec.FLOAT.fieldOf("depth").forGetter((object) -> object.depth),
			Codec.FLOAT.fieldOf("scale").forGetter((object) -> object.scale)
		).apply(instance, TerrainPoint::new));
}
