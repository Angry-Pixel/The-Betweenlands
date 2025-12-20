package thebetweenlands.common.world.gen.layer.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomFactoryContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContext;
import thebetweenlands.api.world.biome.layer.context.SeedMixer;

public class LinearCongruentialRandomContext implements BiomeLayerRandomContext {

	private final LinearCongruentialRandomFactory factory;
    private final long seed;

    protected LinearCongruentialRandomContext(LinearCongruentialRandomFactory factory, long worldSeed, long mixedSeed) {
    	this.factory = factory;
        this.seed = mixedSeed;
    }
	
	@Override
	public RandomSource createRandom(long pX, long pZ) {
		LinearCongruentialRandomSource randomSource = new LinearCongruentialRandomSource(this.seed);
		randomSource.setSeed(this.seed, this.seed, pX, pZ);
		return randomSource;
	}

	@Override
	public BiomeLayerRandomFactoryContext getRandomFactory() {
		return this.factory;
	}

    @Override
    public ImprovedNoise getBiomeNoise() {
        return this.factory.getBiomeNoise();
    }

    public static final class LinearCongruentialRandomFactory implements BiomeLayerRandomFactoryContext {
    	
    	private final long worldSeed;
    	private final ImprovedNoise biomeNoise;
    	private final SeedMixer seedMixer;
    	
    	public LinearCongruentialRandomFactory(long worldSeed) {
    		this(worldSeed, new LinearCongruentialSeedMixer(worldSeed));
    	}
    	
    	public LinearCongruentialRandomFactory(long worldSeed, SeedMixer seedMixer) {
    		this.worldSeed = worldSeed;
            this.biomeNoise = new ImprovedNoise(new LegacyRandomSource(worldSeed));
            this.seedMixer = seedMixer;
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
		
		private BiomeLayerRandomContext createContextFromMixedSeed(long mixedSeed) {
			return new LinearCongruentialRandomContext(this, this.worldSeed, mixedSeed);
		}

		@Override
		public BiomeLayerRandomContext createContext(ResourceLocation location) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(location));
		}

		@Override
		public BiomeLayerRandomContext createContext(String string) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(string));
		}

		@Override
		public BiomeLayerRandomContext createContext(long seedModifier) {
			return this.createContextFromMixedSeed(this.getSeedMixer().mixSeed(seedModifier));
		}
    }
}
