package thebetweenlands.common.world.gen.util;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;

// TODO this could probably be done better
public class BiomeWeightsCache {
	
	// Technically, one volatile field should be equally safe (and faster) because the data is immutable and only read once.
	// However, the atomic reference makes it very clear that this data may be read on multiple threads at once, 
	//          and is much harder to accidentally make thread-unsafe in future.
	protected final AtomicReference<BiomeWeightsCacheData> weightsCacheReference = new AtomicReference<>();

	public Optional<BiomeWeights> getWeights(ChunkGenerator chunkGenerator, int chunkX, int chunkZ) {
		final BiomeWeightsCacheData cache = this.weightsCacheReference.updateAndGet((cacheData) -> {
			if(cacheData == null || !cacheData.present() || cacheData.generator.get() == null) {
				cacheData = BiomeWeightsCacheData.EMPTY;
			}
			
			if(cacheData.present && cacheData.generator.get() == chunkGenerator && cacheData.chunkX == chunkX && cacheData.chunkZ == chunkZ) {
				return cacheData;
			}
			
			// If we have no valid cached data

			// TODO get biome weights properly
			if(!(chunkGenerator instanceof BetweenlandsChunkGenerator blGenerator)) {
				return cacheData;
			}
			
			BiomeWeights biomeWeights = blGenerator.calculateBiomeWeights(new ChunkPos(chunkX, chunkZ));
			return new BiomeWeightsCacheData(chunkGenerator, chunkX, chunkZ, biomeWeights);
		});
		
		return cache.present() && cache.generator().get() == chunkGenerator ? Optional.of(cache.biomeWeights()) : Optional.empty();
	}
	
	public static record BiomeWeightsCacheData(boolean present, WeakReference<ChunkGenerator> generator, int chunkX, int chunkZ, BiomeWeights biomeWeights) {
		public static final BiomeWeightsCacheData EMPTY = new BiomeWeightsCacheData(false, null, 0, 0, null);
		
		public BiomeWeightsCacheData(ChunkGenerator generator, int chunkX, int chunkZ, BiomeWeights biomeWeights) {
			this(true, new WeakReference<>(generator), chunkX, chunkZ, biomeWeights);
		}

		public BiomeWeightsCacheData() {
			this(false, null, 0, 0, null);
		}
	}
}
