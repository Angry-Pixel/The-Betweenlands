package thebetweenlands.world.biomes.feature;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;

import java.util.Random;

/**
 * Created by Bart on 9-10-2015.
 */
public class SludgePlainsNoiseFeature extends BiomeNoiseFeature {
    private NoiseGeneratorPerlin noiseGen;
    private double[] noise = new double[256];

    @Override
    public void initializeNoiseGen(Random rng, BiomeGenBaseBetweenlands biome) {
        this.noiseGen = new NoiseGeneratorPerlin(rng, 4);
    }

    @Override
    public void generateNoise(int chunkX, int chunkZ,
                              BiomeGenBaseBetweenlands biome) {
        this.noise = this.noiseGen.func_151599_a(this.noise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
    }

    @Override
    public void postReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
                                       byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider,
                                       BiomeGenBase[] chunksForGeneration, Random rng) { }

    @Override
    public void preReplaceStackBlocks(int x, int z, Block[] chunkBlocks,
                                      byte[] chunkMeta, BiomeGenBaseBetweenlands biome, ChunkProviderBetweenlands provider,
                                      BiomeGenBase[] chunksForGeneration, Random rng) {
        int sliceSize = chunkBlocks.length / 256;
        int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
        //Flatten terrain
        int lowestBlock = 0;
        for(int yOff = 0; yOff < layerHeight; yOff++) {
            int y = layerHeight - yOff;
            Block currentBlock = chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)];
            if(currentBlock != provider.layerBlock) {
                lowestBlock = y;
                break;
            }
        }
        int depth = (int)((layerHeight - lowestBlock) / 6.5D);
        for(int y = lowestBlock; y < layerHeight - depth; y++) {
            chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
        }
        double noise = this.noise[x * 16 + z] / 8.0f;
        int height = (int)(layerHeight - (layerHeight - lowestBlock) / 2.5f + noise * (layerHeight - lowestBlock) + 3);
        for(int y = layerHeight - depth; y < height; y++) {
            chunkBlocks[BiomeGenBaseBetweenlands.getBlockArrayIndex(x, y, z, sliceSize)] = provider.baseBlock;
        }
    }
}
