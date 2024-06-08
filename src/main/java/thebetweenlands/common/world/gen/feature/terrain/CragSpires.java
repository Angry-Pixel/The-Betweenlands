package thebetweenlands.common.world.gen.feature.terrain;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;

import java.util.Random;

public class CragSpires extends TerrainFeatureHelper<TerrainFeatureHelperConfiguration> {
    // TODO: set up mossy crag rock
    private BlockState cragrockDefault;
    private BlockState cragrockMossy1;
    private BlockState cragrockMossy2;

    private NoiseGeneratorPerlin spireNoiseGen;
    private double[] spireNoise = new double[256];

    // We'll just wait until we can call the ChunkGenerator before doing this
    public void initializeGenerators(long seed) {
        Random rng = new Random(seed);
        this.spireNoiseGen = new NoiseGeneratorPerlin(rng, 4);

        this.cragrockDefault = BlockRegistry.CRAGROCK.get().defaultBlockState();
        this.cragrockMossy1 = BlockRegistry.CRAGROCK.get().defaultBlockState();
        this.cragrockMossy2 = BlockRegistry.CRAGROCK.get().defaultBlockState();
    }

    public void generateNoise(int chunkX, int chunkZ) {
        this.spireNoise = this.spireNoiseGen.getRegion(this.spireNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
    }

    public CragSpires(Codec<TerrainFeatureHelperConfiguration> p_65786_) {
        super(p_65786_);
    }

    @Override
    public void generateColumn(FeaturePlaceContext<TerrainFeatureHelperConfiguration> context, WorldGenLevel level, ChunkAccess chunk, ChunkGenerator generator, int x, int z, int tx, int tz, BiomeWeights biomeWeights) {
        // Init
        initializeGenerators(level.getSeed());
        generateNoise(chunk.getPos().x, chunk.getPos().z);

        ChunkGeneratorBetweenlands chunkGenerator = (ChunkGeneratorBetweenlands)generator;

        // Debug tp with static biome seed
        // /tp -231 121 703

        // Generate
        float biomeWeight = biomeWeights.get(x, z);
        double noise = this.spireNoise[x * 16 + z] / 1.5f * biomeWeight + 2.4f;
        int layerHeight = TheBetweenlands.LAYER_HEIGHT;
        if (chunk.getBlockState(new BlockPos(x, layerHeight, z)).getBlock() != chunkGenerator.fillfluid.getBlock()) {
            return;
        }
        int lowestBlock = 0;
        for (int yOff = 0; yOff < layerHeight; yOff++) {
            int y = layerHeight - yOff;
            Block currentBlock = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();
            // layerBlock replaced with set dim fluid
            if (currentBlock != chunkGenerator.fillfluid.getBlock()) {
                lowestBlock = y;
                break;
            }
        }
        // Using chunkGenerator.getSeaLevel() now
        if (generator.getSeaLevel() - lowestBlock < 3) {
            return;
        }
        if (-noise * 12 >= 1) {
            for (int y = lowestBlock; y < layerHeight; y++) {
                chunk.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
            }
            int rockHeight = (int) Math.floor(-noise * 12);
            for (int yOff = 0; yOff < rockHeight; yOff++) {
                int y = layerHeight + yOff;
                if (yOff == rockHeight - 2) {
                    chunk.setBlockState(new BlockPos(x, y, z), this.cragrockMossy2, false);
                } else if (yOff == rockHeight - 1) {
                    chunk.setBlockState(new BlockPos(x, y, z), this.cragrockMossy1, false);
                } else {
                    chunk.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
                }
            }
        } else {
            boolean validSpire = false;
            for (int xo = -4; xo < 4; xo++) {
                for (int zo = -4; zo < 4; zo++) {
                    int nx = x + xo;
                    int nz = z + zo;
                    // IDE told me to use standard library min instead, I don't think a func call is any faster so... ¯\_(ツ)_/¯
                    nx = nx < 0 ? 0 : (nx > 15 ? 15 : nx);
                    nz = nz < 0 ? 0 : (nz > 15 ? 15 : nz);
                    double sNoise = this.spireNoise[nx * 16 + nz] * biomeWeight / 1.5f + 2.4f;
                    if (-sNoise * 12 >= 1) {
                        validSpire = true;
                        break;
                    }
                }
            }
            if (validSpire) {
                int rockHeight = (int) Math.floor(-noise * 12);
                for (int y = lowestBlock; y < layerHeight + rockHeight; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), this.cragrockDefault, false);
                }
            }
        }

        //chunk.setBlockState(new BlockPos(x, 150, z), this.cragrockDefault, false);
    }

}
