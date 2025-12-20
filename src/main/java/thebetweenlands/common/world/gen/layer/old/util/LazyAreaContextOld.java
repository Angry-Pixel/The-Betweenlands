package thebetweenlands.common.world.gen.layer.old.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.PixelTransformer;
import thebetweenlands.common.world.gen.layer.util.LazyArea;

public class LazyAreaContextOld implements BigContext<LazyArea> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;
    private final ImprovedNoise biomeNoise;
    private final long seedModifier;
    private long seed;

    public LazyAreaContextOld(int pMaxCache, long pSeed, long pSeedModifier) {
        this.seedModifier = mixSeed(pSeed, pSeedModifier);
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
        long i = this.seedModifier;
        i = LinearCongruentialGenerator.next(i, pX);
        i = LinearCongruentialGenerator.next(i, pZ);
        i = LinearCongruentialGenerator.next(i, pX);
        i = LinearCongruentialGenerator.next(i, pZ);
        this.seed = i;
    }

    @Override
    public int nextRandom(int pBound) {
        int i = Math.floorMod(this.seed >> 24, pBound);
        this.seed = LinearCongruentialGenerator.next(this.seed, this.seedModifier);
        return i;
    }

    @Override
    public ImprovedNoise getBiomeNoise() {
        return this.biomeNoise;
    }

    private static long mixSeed(long worldSeed, long seedModifier) {
        long i = LinearCongruentialGenerator.next(seedModifier, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);
        long j = LinearCongruentialGenerator.next(worldSeed, i);
        j = LinearCongruentialGenerator.next(j, i);
        return LinearCongruentialGenerator.next(j, i);
    }
}
