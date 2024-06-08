package thebetweenlands.common.world.gen.feature.terrain;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.feature.FeatureHelper;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;

// TODO: use static cached biome weights
// A little bit or a hacky way to determine the placed biome (looking into better ways)
public abstract class TerrainFeatureHelper<F extends TerrainFeatureHelperConfiguration> extends FeatureHelper<F> {

    public TerrainFeatureHelper(Codec<F> p_65786_) {
        super(p_65786_);
    }

    // Get placed biome, get chunk origin pos, create biome weights list
    @Override
    public boolean place(FeaturePlaceContext<F> context) {

        BlockPos placePos = context.origin();
        ChunkPos chunkPos = new ChunkPos(placePos);
        WorldGenLevel level = context.level();
        ChunkGenerator gen = context.chunkGenerator();
        ResourceLocation targetBiome = context.config().targetBiome;
        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);

        float[] terrainBiomeWeights = new float[25];
        float[] interpolatedTerrainBiomeWeights = new float[256];

        int heightMapIndex = 0;

        // Get biomes
        for (int heightMapX = 0; heightMapX < 5; ++heightMapX) {
            for (int heightMapZ = 0; heightMapZ < 5; ++heightMapZ) {

                boolean centerBiome = chunk.getNoiseBiome(heightMapX + 5, 0,heightMapZ + 5).is(targetBiome);// this.biomesForGeneration[heightMapX + 5 + (heightMapZ + 5) * 15];

                float nearestOtherBiomeSq = 50;

                // Averages biome height and variation in a 5x5 area and calculates the biome terrain weight from a 11x11 area
                for (int offsetX = -5; offsetX <= 5; ++offsetX) {
                    for (int offsetZ = -5; offsetZ <= 5; ++offsetZ) {
                        boolean nearbyBiome = chunk.getNoiseBiome(heightMapX + 5 + offsetX, 0,heightMapZ + 5 + offsetZ).is(targetBiome);// this.biomesForGeneration[heightMapX + 5 + offsetX + (heightMapZ + 5 + offsetZ) * 15];

                        float distWeighted = (offsetX * offsetX + offsetZ * offsetZ);
                        if (nearbyBiome != centerBiome && distWeighted < nearestOtherBiomeSq) {
                            nearestOtherBiomeSq = distWeighted;
                        }
                    }
                }

                //The 0 point is offset by some blocks so that the lerp doesn't cause problems later on
                terrainBiomeWeights[heightMapIndex] = Mth.clamp(Math.max((nearestOtherBiomeSq - 2) / 46.0F, 0.0F), 0.0F, 1.0F);
            }
        }

        // Interpolate
        for(int z = 0; z < 16; z++) {
            for(int x = 0; x < 16; x++) {
                float fractionZ = (z % 4) / 4.0F;
                float fractionX = (x % 4) / 4.0F;
                int biomeWeightZ = z / 4;
                int biomeWeightX = x / 4;

                float weightXCZC = terrainBiomeWeights[biomeWeightX + biomeWeightZ * 5];
                float weightXNZC = terrainBiomeWeights[biomeWeightX+1 + biomeWeightZ * 5];
                float weightXCZN = terrainBiomeWeights[biomeWeightX + (biomeWeightZ+1) * 5];
                float weightXNZN = terrainBiomeWeights[biomeWeightX+1 + (biomeWeightZ+1) * 5];

                float interpZAxisXC = weightXCZC + (weightXCZN - weightXCZC) * fractionZ;
                float interpZAxisXN = weightXNZC + (weightXNZN - weightXNZC) * fractionZ;
                float currentVal = interpZAxisXC + (interpZAxisXN - interpZAxisXC) * fractionX;

                interpolatedTerrainBiomeWeights[x + z * 16] = currentVal;
            }
        }

        BiomeWeights biomeWeights = new BiomeWeights(interpolatedTerrainBiomeWeights);

        for (int columnX = chunkPos.getMinBlockX(); columnX < chunkPos.getMinBlockX() + 16; columnX++){
            for (int columnZ = chunkPos.getMinBlockZ(); columnZ < chunkPos.getMinBlockZ() + 16; columnZ++){

                if (!level.getBiome(new BlockPos(columnX, 0, columnZ)).is(targetBiome)) {
                    continue;
                }

                // Call generate func
                generateColumn(context, level, chunk, gen, columnX - chunkPos.getMinBlockX(), columnZ - chunkPos.getMinBlockZ(), columnX, columnZ, biomeWeights);
            }
        }
        return true;
    }

    public abstract void generateColumn(FeaturePlaceContext<F> context, WorldGenLevel level, ChunkAccess chunk, ChunkGenerator generator, int x, int z, int tx, int tz, BiomeWeights biomeWeights);
}
