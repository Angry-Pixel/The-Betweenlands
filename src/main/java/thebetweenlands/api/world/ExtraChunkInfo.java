package thebetweenlands.api.world;

import java.util.EnumSet;
import java.util.Optional;

import thebetweenlands.api.world.biome.BiomeWeights;

// TODO make extensible
public record ExtraChunkInfo(EnumSet<ExtraChunkInfoTypes> mask, Optional<BiomeWeights> biomeWeights) {

	public ExtraChunkInfo() {
		this(EnumSet.noneOf(ExtraChunkInfoTypes.class), Optional.empty());
	}
	
	public ExtraChunkInfo withBiomeWeights(BiomeWeights biomeWeights) {
		EnumSet<ExtraChunkInfoTypes> mask = this.mask().clone();
		mask.add(ExtraChunkInfoTypes.BIOME_WEIGHTS);
		return new ExtraChunkInfo(mask, Optional.of(biomeWeights));
	}
	
}
