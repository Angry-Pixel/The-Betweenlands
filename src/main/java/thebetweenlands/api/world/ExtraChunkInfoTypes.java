package thebetweenlands.api.world;

import thebetweenlands.api.world.biome.BiomeWeights;
import thebetweenlands.api.world.biome.CarvingMasks;

/*
 * Extra info an early generator might need
 * TODO Make extensible eventually
 */
public enum ExtraChunkInfoTypes {
	BIOME_WEIGHTS(BiomeWeights.class),
	CARVING_MASKS(CarvingMasks.class);

	private final Class<?> datatypeClass;

	ExtraChunkInfoTypes(Class<?> datatypeClass) {
		this.datatypeClass = datatypeClass;
	}

	public Class<?> getDatatypeClass() {
		return this.datatypeClass;
	}
}
