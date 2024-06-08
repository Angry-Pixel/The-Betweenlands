package thebetweenlands.common.biomes.feature;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.world.gen.BiomeWeights;

import java.util.Random;

/**
 * Generates block patches at the surface of the terrain
 */
public class PatchFeature  {
    //private NoiseGeneratorPerlin mudNoiseGen;
    /*
    private double[] mudNoise = new double[256];
    private double scaleX, scaleY;
    private double mult = 1.0D / 1.6D;
    private double offset = 1.5D;
    private BlockState block;

    public PatchFeature(double scaleX, double scaleY, BlockState block) {
        super();
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.block = block;
    }

    public PatchFeature(double scaleX, double scaleY, BlockState block, double mult, double offset) {
        super();
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.block = block;
        this.mult = mult;
        this.offset = offset;
    }

    @Override
    public void initializeGenerators(long seed, Biome biome) {
        Random rng = new Random(seed);
        this.mudNoiseGen = new NoiseGeneratorPerlin(rng, 4);
    }

    @Override
    public void generateNoise(int chunkX, int chunkZ, Biome biome) {
        this.mudNoise = this.mudNoiseGen.getRegion(this.mudNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, this.scaleX, this.scaleY, 1.0D);
    }

    @Override
    public void replaceStackBlocks(int x, int z, double baseBlockNoise,
                                   ChunkPrimer chunkPrimer, ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration,
                                   Biome biome, BiomeWeights biomeWeights, EnumGeneratorPass pass) {
        if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
            float biomeWeight = biomeWeights.get(x, z);
            if(this.mudNoise[x * 16 + z] * this.mult * biomeWeight + this.offset <= 0) {
                int y = WorldProviderBetweenlands.LAYER_HEIGHT + 20;
                for(int yo = 0; yo < WorldProviderBetweenlands.LAYER_HEIGHT + 20; yo++) {
                    Block currentBlock = chunkPrimer.getBlockState(x, y - yo, z).getBlock();
                    Block blockAbove = chunkPrimer.getBlockState(x, y - yo + 1, z).getBlock();
                    if(currentBlock == biome.topBlock.getBlock() && (blockAbove == Blocks.AIR || blockAbove == null)) {
                        chunkPrimer.setBlockState(x, y - yo, z, this.block);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean place(FeaturePlaceContext p_159749_) {
        return false;
    }
    */
}
