package thebetweenlands.common.world.gen.feature.mapgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.world.gen.feature.FeatureHelper;

import java.util.Random;

public abstract class MapGenHelper<F extends MapGenHelperConfiguration> extends FeatureHelper<F> {
	public int range = 8;
	public Random rand = new Random();

	public MapGenHelper(Codec<F> p_65786_) {
		super(p_65786_);
	}

	@Override
	public boolean place(FeaturePlaceContext<F> context) {
		WorldGenLevel level = context.level();
		BlockPos pos = context.origin();
		ChunkAccess chunk = level.getChunk(pos);
		ChunkPos chunkPos = chunk.getPos();
		this.generate(context, level, chunkPos.x, chunkPos.z, chunk);
		return true;
	}

	public void generate(FeaturePlaceContext<F> context, WorldGenLevel level, int x, int z, ChunkAccess chunk) {
		int i = this.range;
		this.rand.setSeed(level.getSeed());
		long j = this.rand.nextLong();
		long k = this.rand.nextLong();

		for (int l = x - i; l <= x + i; ++l) {
			for (int i1 = z - i; i1 <= z + i; ++i1) {
				long j1 = (long) l * j;
				long k1 = (long) i1 * k;
				this.rand.setSeed(j1 ^ k1 ^ level.getSeed());
				this.recursiveGenerate(context, level, l, i1, x, z, chunk);
			}
		}
	}

	public static void setupChunkSeed(long seed, Random rand, int x, int z) {
		rand.setSeed(seed);
		long i = rand.nextLong();
		long j = rand.nextLong();
		long k = (long) x * i;
		long l = (long) z * j;
		rand.setSeed(k ^ l ^ seed);
	}

	public void recursiveGenerate(FeaturePlaceContext<F> context, WorldGenLevel level, int chunkX, int chunkZ, int originalX, int originalZ, ChunkAccess chunk) {
	}
}
