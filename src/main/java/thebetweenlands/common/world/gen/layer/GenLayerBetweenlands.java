package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import thebetweenlands.common.registries.BiomeRegistry;

public abstract class GenLayerBetweenlands extends GenLayer {
	public GenLayerBetweenlands(long seed) {
		super(seed);
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
		int biomeSize = getModdedBiomeSize(worldType, (worldType == WorldType.LARGE_BIOMES ? 7 : 5));

		GenLayer genLayer = new GenLayerIsland(1L);
		genLayer = new GenLayerFuzzyZoom(2000L, genLayer);

		genLayer = new GenLayerBetweenlandsBiome(100L, genLayer);
		genLayer = GenLayerZoom.magnify(2000L, genLayer, 1);

		GenLayer spiritTreeLayer = new GenLayerSurrounded(102L, genLayer, BiomeRegistry.SWAMPLANDS, BiomeRegistry.SWAMPLANDS_CLEARING, 1, 1);
		spiritTreeLayer = new GenLayerMask(spiritTreeLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING));
		spiritTreeLayer = GenLayerThinMask.thin(105L, spiritTreeLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), 3, 0.33f, 9);

		genLayer = GenLayerZoom.magnify(2345L, genLayer, biomeSize);
		spiritTreeLayer = GenLayerZoomIncrementMask.magnify(2345L, spiritTreeLayer, biomeSize);

		spiritTreeLayer = new GenLayerCircleMask(103L, spiritTreeLayer, Biome.getIdForBiome(BiomeRegistry.SWAMPLANDS_CLEARING), 10);

		genLayer = new GenLayerMixMask(genLayer, spiritTreeLayer);

		GenLayer indexLayer = new GenLayerVoronoiZoom(10L, genLayer);
		indexLayer.initWorldGenSeed(seed);
		genLayer.initWorldGenSeed(seed);

		return new GenLayer[]{genLayer, indexLayer, genLayer};
	}
}
