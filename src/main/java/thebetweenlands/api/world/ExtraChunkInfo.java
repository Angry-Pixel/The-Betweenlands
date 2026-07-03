package thebetweenlands.api.world;

import java.util.EnumSet;
import java.util.Optional;

import thebetweenlands.api.world.biome.BiomeWeights;
import thebetweenlands.api.world.biome.CarvingMasks;

// TODO make extensible
public record ExtraChunkInfo(EnumSet<ExtraChunkInfoTypes> mask, Optional<BiomeWeights> biomeWeights, Optional<CarvingMasks> carvingMasks) {

	public ExtraChunkInfo() {
		this(EnumSet.noneOf(ExtraChunkInfoTypes.class), Optional.empty(), Optional.empty());
	}

	public ExtraChunkInfo withBiomeWeights(BiomeWeights biomeWeights) {
		EnumSet<ExtraChunkInfoTypes> mask = this.mask().clone();
		mask.add(ExtraChunkInfoTypes.BIOME_WEIGHTS);
		return new ExtraChunkInfo(mask, Optional.of(biomeWeights), this.carvingMasks);
	}

	public ExtraChunkInfo withCarvingMasks(CarvingMasks carvingMasks) {
		EnumSet<ExtraChunkInfoTypes> mask = this.mask().clone();
		mask.add(ExtraChunkInfoTypes.CARVING_MASKS);
		return new ExtraChunkInfo(mask, this.biomeWeights, Optional.of(carvingMasks));
	}
	
}
