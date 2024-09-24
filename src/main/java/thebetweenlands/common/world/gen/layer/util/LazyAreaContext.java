package thebetweenlands.common.world.gen.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public class LazyAreaContext implements BigContext<LazyArea> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;
    private final ImprovedNoise biomeNoise;
    private final long seed;
    private long rval;

    public LazyAreaContext(int pMaxCache, long pSeed, long pSeedModifier) {
        this.seed = mixSeed(pSeed, pSeedModifier);
        this.biomeNoise = new ImprovedNoise(new LegacyRandomSource(pSeed));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.maxCache = pMaxCache;
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

    @Override
    public void initRandom(long pX, long pZ) {
        long i = this.seed;
        i = LinearCongruentialGenerator.next(i, pX);
        i = LinearCongruentialGenerator.next(i, pZ);
        i = LinearCongruentialGenerator.next(i, pX);
        i = LinearCongruentialGenerator.next(i, pZ);
        this.rval = i;
    }

    @Override
    public int nextRandom(int pBound) {
        int i = Math.floorMod(this.rval >> 24, pBound);
        this.rval = LinearCongruentialGenerator.next(this.rval, this.seed);
        return i;
    }

    @Override
    public ImprovedNoise getBiomeNoise() {
        return this.biomeNoise;
    }

    private static long mixSeed(long pLeft, long pRight) {
        long i = LinearCongruentialGenerator.next(pRight, pRight);
        i = LinearCongruentialGenerator.next(i, pRight);
        i = LinearCongruentialGenerator.next(i, pRight);
        long j = LinearCongruentialGenerator.next(pLeft, i);
        j = LinearCongruentialGenerator.next(j, i);
        return LinearCongruentialGenerator.next(j, i);
    }
}
