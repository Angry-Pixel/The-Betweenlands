package thebetweenlands.common.world.gen.biome.generator;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class BiomeGenerator {

    public int biomeID;
    public boolean noiseGenerated = false;
    public boolean noiseGeneratorsInitialized = false;
    public final BiomeDecorator decorator;

    // empty generator
    public BiomeGenerator(BiomeDecorator decorator) {
        this.decorator = decorator;
    }

    public BiomeGenerator addFeature(DecoratorFeature feature) {
        this.decorator.features.add(feature);
        return this;
    }

    public BiomeGenerator addFeatures(DecoratorFeature... feature) {
        this.decorator.features.addAll(feature.length, Arrays.stream(feature).toList());
        return this;
    }

    public void initializeGenerators(long seed) {
        if (!this.noiseGeneratorsInitialized) {
            this.decorator.features.forEach((feature) -> {
                feature.initializeGenerators(seed, this.biomeID);
            });
            this.noiseGeneratorsInitialized = true;
        }
    }

    /**
     * Resets the noise generators at the next {@link BiomeGenerator#initializeGenerators(long)} call
     */
    public void resetNoiseGenerators() {
        this.noiseGeneratorsInitialized = false;
    }

    public void generateNoise(int chunkZ, int chunkX) {
        if (!this.noiseGenerated) {
            decorator.features.forEach((feature) -> {
                feature.generateNoise(chunkZ, chunkX, this.biomeID);
            });
            this.noiseGenerated = true;
        }
    }

    /**
     * Modifies the terrain with {@link DecoratorFeature} specific features.
     * @param blockX
     * @param blockZ
     * @param inChunkX
     * @param inChunkZ
     * @param baseBlockNoise
     * @param chunkPrimer
     * @param chunkGenerator
     * @param biomesForGeneration
     * @param biomeWeights
     * @param pass
     */
    public final void runBiomeFeatures(int blockX, int blockZ, int inChunkX, int inChunkZ,
                                       double baseBlockNoise, ChunkAccess chunkPrimer,
                                       ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration,
                                       BiomeWeights biomeWeights, EnumGeneratorPass pass) {

        decorator.features.forEach((feature) -> {
            feature.replaceStackBlocks(inChunkX, inChunkZ, baseBlockNoise, chunkPrimer, chunkGenerator, biomesForGeneration, this.biomeID, biomeWeights, pass);
        });
    }

    public void replaceBiomeBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ,
                                   double baseBlockNoise, Random rng, long seed, ChunkAccess chunkPrimer,
                                   ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration,
                                   BiomeWeights biomeWeights) {

        if(!this.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, chunkPrimer, chunkGenerator, biomesForGeneration, biomeWeights, EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS)) {
            return;
        }

        SplittableRandom fastRng = new SplittableRandom(blockX * 341873128712L + blockZ * 132897987541L);

        //Random number for base block patch generation based on the base block noise
        int baseBlockNoiseRN = (int) (baseBlockNoise / 3.0D + 3.0D + fastRng.nextDouble() * 0.25D);

        //Amount of blocks below the surface
        int blocksBelow = -1;
        //Amount of blocks below the first block under the layer
        int blocksBelowLayer = -1;

        /*
        for(int y = 255; y >= 0; --y) {
            //Generate bottom block
            if(y <= this.bottomBlockHeight + this.bottomBlockFuzz && y >= this.bottomBlockHeight && y - this.bottomBlockHeight <= fastRng.nextInt(this.bottomBlockFuzz)) {
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.bottomBlockState);
                continue;
            }

            //Block state of the current x, y, z position
            BlockState currentBlockState = chunkPrimer.getBlockState(inChunkX, y, inChunkZ);

            //Block is either null, air or the layer block
            if(currentBlockState == null || currentBlockState.getMaterial() == Material.AIR ||
                    currentBlockState.getBlock() == chunkGenerator.layerBlock) {
                blocksBelow = -1;
                continue;
            } else {
                blocksBelow++;
            }

            if(currentBlockState.getBlock() != chunkGenerator.baseBlock) {
                continue;
            }

            int baseBlockVariationLayer = (int) (Math.abs(this.baseBlockLayerVariationNoise[inChunkX * 16 + inChunkZ] * 0.7F));
            int layerBlockY = y - baseBlockVariationLayer;
            if(layerBlockY < 0) {
                layerBlockY = 0;
            }

            //Generate base block patch
            if(this.hasBaseBlockPatches && baseBlockNoiseRN <= 0) {
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.getBaseBlockState(layerBlockY));
                continue;
            }

            //Block above current block
            BlockState blockAboveState = chunkPrimer.getBlockState(inChunkX, y + 1, inChunkZ);

            if(blocksBelowLayer >= 0) {
                blocksBelowLayer++;
            }
            if(currentBlockState.getBlock() == chunkGenerator.baseBlock && blockAboveState.getBlock() == chunkGenerator.layerBlock) {
                blocksBelowLayer++;
            }

            if(blocksBelowLayer <= this.underLayerBlockHeight && blocksBelowLayer >= 0) {
                //Generate under layer top block
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.underLayerTopBlockState);
            }  else if(blocksBelow == 0 && currentBlockState.getBlock() == chunkGenerator.baseBlock) {
                //Generate top block
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.biome.topBlock);
            } else if(blocksBelow > 0 && blocksBelow <= this.fillerBlockHeight && currentBlockState.getBlock() == chunkGenerator.baseBlock) {
                //Generate filler block
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.biome.fillerBlock);
            } else if(currentBlockState.getBlock() == chunkGenerator.baseBlock) {
                //Generate base block
                chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.getBaseBlockState(layerBlockY));
            }
        }
         */
        this.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, chunkPrimer, chunkGenerator, biomesForGeneration, biomeWeights, EnumGeneratorPass.POST_REPLACE_BIOME_BLOCKS);
    }

    public boolean replaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ,
                                   double baseBlockNoise, ChunkAccess chunkPrimer,
                                   ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration,
                                   BiomeWeights biomeWeights, EnumGeneratorPass enumGeneratorPass) {



        // default always return true
        return true;
    }

    public void resetNoise() {
        this.noiseGenerated = false;
    }

    public static enum EnumGeneratorPass {
        PRE_REPLACE_BIOME_BLOCKS,
        POST_REPLACE_BIOME_BLOCKS,
        POST_GEN_CAVES
    }
}
