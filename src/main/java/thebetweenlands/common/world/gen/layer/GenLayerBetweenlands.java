package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import thebetweenlands.common.registries.BiomeRegistry;

public abstract class GenLayerBetweenlands extends GenLayer {
	protected final InstancedIntCache cache;

	public GenLayerBetweenlands(InstancedIntCache cache, long seed) {
		super(seed);
		this.cache = cache;
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
		int biomeSize = getModdedBiomeSize(worldType, (worldType == WorldType.LARGE_BIOMES ? 6 : 4));

		final InstancedIntCache cache = new InstancedIntCache();

		GenLayer genLayer = new GenLayerBetweenlandsBiome(cache, 100L);

		genLayer = GenLayerZoomIncrement.magnify(cache, 2000L, genLayer, false, 2);

		GenLayer swamplandsClearingLayer = new GenLayerSurrounded(cache, 102L, genLayer, BiomeRegistry.SWAMPLANDS, BiomeRegistry.SWAMPLANDS_CLEARING, 1, 1);
		swamplandsClearingLayer = new GenLayerMask(cache, swamplandsClearingLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING));
		swamplandsClearingLayer = GenLayerThinMask.thin(cache, 105L, swamplandsClearingLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), 3, 0.25f, 10);

		genLayer = GenLayerZoomIncrement.magnify(cache, 2345L, genLayer, false, biomeSize);
		swamplandsClearingLayer = GenLayerZoomIncrement.magnify(cache, 2345L, swamplandsClearingLayer, true, biomeSize);

		swamplandsClearingLayer = new GenLayerCircleMask(cache, 103L, swamplandsClearingLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), 10);

		genLayer = new GenLayerMixMask(cache, genLayer, swamplandsClearingLayer);

		GenLayer indexLayer = new GenLayerResetCache(cache, new GenLayerVoronoiZoomInstanced(cache, 10L, genLayer));
		genLayer = new GenLayerResetCache(cache, genLayer);

		indexLayer.initWorldGenSeed(seed);
		genLayer.initWorldGenSeed(seed);

		return new GenLayer[]{genLayer, indexLayer, genLayer};
	}
}
