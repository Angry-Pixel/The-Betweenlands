package thebetweenlands.common.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class GenLayerBetweenlands extends GenLayer {
	public GenLayerBetweenlands(long seed) {
		super(seed);
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
		int biomeSize = getModdedBiomeSize(worldType, (byte) (worldType == WorldType.LARGE_BIOMES ? 7 : 5));

		GenLayer genLayer = new GenLayerIsland(1L);
		genLayer = new GenLayerFuzzyZoom(2000L, genLayer);

		genLayer = new GenLayerBetweenlandsBiome(100L, genLayer);
		genLayer = GenLayerZoom.magnify(2000L, genLayer, 1);

		genLayer = new GenLayerSubBiomes(101L, genLayer);
		genLayer = GenLayerZoom.magnify(2100L, genLayer, biomeSize);

		genLayer = new GenLayerVoronoiZoom(10L, genLayer);
		genLayer.initWorldGenSeed(seed);
		return new GenLayer[]{genLayer, genLayer, genLayer};
	}
}
