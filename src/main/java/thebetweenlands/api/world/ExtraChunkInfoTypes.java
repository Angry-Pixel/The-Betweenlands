package thebetweenlands.api.world;

import thebetweenlands.api.world.biome.BiomeWeights;

/*
 * Extra info an early generator might need
 * TODO Make extensible eventually
 */
public enum ExtraChunkInfoTypes {
	BIOME_WEIGHTS(BiomeWeights.class);

	private final Class<?> datatypeClass;

	ExtraChunkInfoTypes(Class<?> datatypeClass) {
		this.datatypeClass = datatypeClass;
	}

	public Class<?> getDatatypeClass() {
		return this.datatypeClass;
	}
}
