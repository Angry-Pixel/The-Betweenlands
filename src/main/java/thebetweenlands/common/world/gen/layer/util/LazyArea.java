package thebetweenlands.common.world.gen.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.PixelTransformer;

public class LazyArea implements Area {
    private final PixelTransformer transformer;
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;

    public LazyArea(Long2IntLinkedOpenHashMap pCache, int pMaxCache, PixelTransformer pTransformer) {
        this.cache = pCache;
        this.maxCache = pMaxCache;
        this.transformer = pTransformer;
    }

    @Override
    public int get(int pX, int pZ) {
        long i = ChunkPos.asLong(pX, pZ);
        final Long2IntLinkedOpenHashMap cache = this.cache;
        final int maxCache = this.maxCache;
        synchronized(cache) {
            int j = cache.getAndMoveToFirst(i);
            if (j != Integer.MIN_VALUE) {
                return j;
            } else {
                int k = this.transformer.apply(pX, pZ);
                cache.putAndMoveToFirst(i, k);
                if (cache.size() > maxCache) {
                    for(int l = 0; l < maxCache / 16; ++l) {
                        cache.removeLastInt();
                    }
                }

                return k;
            }
        }
    }

    public int getMaxCache() {
        return this.maxCache;
    }
}
