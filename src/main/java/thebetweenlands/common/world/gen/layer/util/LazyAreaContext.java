package thebetweenlands.common.world.gen.layer.util;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.PixelTransformer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextFactory;
import thebetweenlands.api.world.biome.layer.context.SeedMixer;

public class LazyAreaContext implements BiomeLayerContext<LazyArea> {

	private final LazyAreaContextFactory factory;
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;
    private final long seed;

    protected LazyAreaContext(LazyAreaContextFactory factory, int maxCache, long worldSeed, long mixedSeed) {
    	this.factory = factory;
        this.seed = mixedSeed;
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

	@Override
	public RandomSource createRandom(long pX, long pZ) {
		LinearCongruentialRandomSource randomSource = new LinearCongruentialRandomSource(this.seed);
		randomSource.setSeed(this.seed, this.seed, pX, pZ);
		return randomSource;
	}

	@Override
	public BiomeLayerContextFactory<LazyArea> getContextFactory() {
		return this.factory;
	}

    @Override
    public ImprovedNoise getBiomeNoise() {
        return this.factory.getBiomeNoise();
    }

    public static final class LazyAreaContextFactory implements BiomeLayerContextFactory<LazyArea> {
    	
    	private final int maxCache;
    	private final long worldSeed;
    	private final ImprovedNoise biomeNoise;
    	private final SeedMixer seedMixer;
    	
    	public LazyAreaContextFactory(int maxCache, long worldSeed) {
    		this(maxCache, worldSeed, new LinearCongruentialSeedMixer(worldSeed));
    	}
    	
    	public LazyAreaContextFactory(int maxCache, long worldSeed, SeedMixer seedMixer) {
    		this.maxCache = maxCache;
    		this.worldSeed = worldSeed;
            this.biomeNoise = new ImprovedNoise(new LegacyRandomSource(worldSeed));
            this.seedMixer = seedMixer;
    	}

    	public int getMaxCache() {
    		return this.maxCache;
    	}

    	public long getWorldSeed() {
    		return this.worldSeed;
    	}

    	public ImprovedNoise getBiomeNoise() {
    		return this.biomeNoise;
    	}
    	
		@Override
		public SeedMixer getSeedMixer() {
			return this.seedMixer;
		}
		
		private BiomeLayerContext<LazyArea> createContextFromMixedSeed(long mixedSeed) {
			return new LazyAreaContext(this, this.maxCache, this.worldSeed, mixedSeed);
		}

		@Override
		public BiomeLayerContext<LazyArea> createContext(ResourceLocation location) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(location));
		}

		@Override
		public BiomeLayerContext<LazyArea> createContext(String string) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(string));
		}

		@Override
		public BiomeLayerContext<LazyArea> createContext(long seedModifier) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(seedModifier));
		}
    }
}
