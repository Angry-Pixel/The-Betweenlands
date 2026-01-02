package thebetweenlands.common.world.gen.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext;
import thebetweenlands.api.world.biome.layer.PixelTransformer;

public class LazyAreaFactoryContext implements AreaFactoryContext<LazyArea> {

    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;

    public LazyAreaFactoryContext(int maxCache) {
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.maxCache = maxCache;
    }
	
    @Override
    public LazyArea createResult(PixelTransformer pPixelTransformer) {
        return new LazyArea(this.cache, this.maxCache, pPixelTransformer);
    }

    @Override
    public LazyArea createResult(PixelTransformer pPixelTransformer, LazyArea pArea) {
        return new LazyArea(this.cache, Math.min(1024, pArea.getMaxCache() * 4), pPixelTransformer);
    }

    @Override
    public LazyArea createResult(PixelTransformer pTransformer, LazyArea pFirstArea, LazyArea pSecondArea) {
        return new LazyArea(this.cache, Math.min(1024, Math.max(pFirstArea.getMaxCache(), pSecondArea.getMaxCache()) * 4), pTransformer);
    }

}
