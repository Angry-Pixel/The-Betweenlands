package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

public class MapGenBase
{
    protected int range = 8;
    protected Random rand = new Random();
    protected int worldseed;

    public void generate(int worldseed, int x, int z, ChunkAccess primer)
    {
        int i = this.range;
        this.worldseed = worldseed;
        this.rand.setSeed(worldseed);
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();

        for (int l = x - i; l <= x + i; ++l)
        {
            for (int i1 = z - i; i1 <= z + i; ++i1)
            {
                long j1 = (long)l * j;
                long k1 = (long)i1 * k;
                this.rand.setSeed(j1 ^ k1 ^ worldseed);
                this.recursiveGenerate(l, i1, x, z, primer);
            }
        }
    }

    public static void setupChunkSeed(long p_191068_0_, Random p_191068_2_, int p_191068_3_, int p_191068_4_)
    {
        p_191068_2_.setSeed(p_191068_0_);
        long i = p_191068_2_.nextLong();
        long j = p_191068_2_.nextLong();
        long k = (long)p_191068_3_ * i;
        long l = (long)p_191068_4_ * j;
        p_191068_2_.setSeed(k ^ l ^ p_191068_0_);
    }

    protected void recursiveGenerate(int chunkX, int chunkZ, int originalX, int originalZ, ChunkAccess chunkPrimerIn)
    {
    }
}