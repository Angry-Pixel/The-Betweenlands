package thebetweenlands.common.world.noisegenerators.genlayers;

import thebetweenlands.common.registries.BiomeRegistry;

public class ProviderGenLayerBetweenlands extends ProviderGenLayer {

	public ProviderGenLayerBetweenlands() {
	}

	public GenLayer[] initialize(long seed) {
		final InstancedIntCache cache = new InstancedIntCache();

		// might change this later, just making it stick to default size
		int biomeSize = 4;

		GenLayer genLayer = new GenLayerBetweenlands(cache, 100L);

		genLayer = GenLayerZoomIncrement.magnify(cache, 2000L, genLayer, false, 2);

		GenLayer swamplandsClearingLayer = new GenLayerSurrounded(cache, 102L, genLayer, BiomeRegistry.SWAMPLANDS, BiomeRegistry.SWAMPLANDS_CLEARING, 1, 1);
		swamplandsClearingLayer = new GenLayerMask(cache, swamplandsClearingLayer, BiomeRegistry.SWAMPLANDS_CLEARING.id, BiomeRegistry.SWAMPLANDS_CLEARING.id);
		swamplandsClearingLayer = GenLayerThinMask.thin(cache, 105L, swamplandsClearingLayer, BiomeRegistry.SWAMPLANDS_CLEARING.id, 3, 0.25f, 10);

		genLayer = GenLayerZoomIncrement.magnify(cache, 2345L, genLayer, false, 1);
		swamplandsClearingLayer = GenLayerZoomIncrement.magnify(cache, 2345L, swamplandsClearingLayer, true, 1);
		
		GenLayer sludgePlainsClearingLayer = new GenLayerSurrounded(cache, 351L, genLayer, BiomeRegistry.SLUDGE_PLAINS, BiomeRegistry.SLUDGE_PLAINS_CLEARING, 2, 1);
		sludgePlainsClearingLayer = new GenLayerMask(cache, sludgePlainsClearingLayer, BiomeRegistry.SLUDGE_PLAINS_CLEARING.id, BiomeRegistry.SLUDGE_PLAINS_CLEARING.id);
		sludgePlainsClearingLayer = GenLayerThinMask.thin(cache, 214L, sludgePlainsClearingLayer, BiomeRegistry.SLUDGE_PLAINS_CLEARING.id, 4, 0.15f, 20);
		
		genLayer = GenLayerZoomIncrement.magnify(cache, 2345L, genLayer, false, biomeSize - 1);
		swamplandsClearingLayer = GenLayerZoomIncrement.magnify(cache, 2345L, swamplandsClearingLayer, true, biomeSize - 1);
		sludgePlainsClearingLayer = GenLayerZoomIncrement.magnify(cache, 2345L, sludgePlainsClearingLayer, true, biomeSize - 1 - 2);
		
		sludgePlainsClearingLayer = new GenLayerCircleMask(cache, 103L, sludgePlainsClearingLayer, BiomeRegistry.SLUDGE_PLAINS_CLEARING.id, 3);
		sludgePlainsClearingLayer = GenLayerZoomIncrement.magnify(cache, 2542L, sludgePlainsClearingLayer, false, 2);
		
		swamplandsClearingLayer = new GenLayerCircleMask(cache, 103L, swamplandsClearingLayer, BiomeRegistry.SWAMPLANDS_CLEARING.id, 10);

		genLayer = new GenLayerMixMask(cache, genLayer, swamplandsClearingLayer);
		genLayer = new GenLayerMixMask(cache, genLayer, sludgePlainsClearingLayer);
		
		GenLayer indexLayer = new GenLayerResetCache(cache, new GenLayerVoronoiZoomInstanced(cache, 10L, genLayer));
		genLayer = new GenLayerResetCache(cache, genLayer);

		indexLayer.initWorldGenSeed(seed);
		genLayer.initWorldGenSeed(seed);

		return new GenLayer[]{genLayer, indexLayer, genLayer};
	}
}
