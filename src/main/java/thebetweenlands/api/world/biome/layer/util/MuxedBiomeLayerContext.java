package thebetweenlands.api.world.biome.layer.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.PixelTransformer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextFactory;
import thebetweenlands.api.world.biome.layer.context.SeedMixer;

public record MuxedBiomeLayerContext<A extends Area, B extends Area>(BiomeLayerContext<A> mainContext, BiomeLayerContext<B> randomContext) implements BiomeLayerContext<A> {
	// From the main context
	@Override
    public A createResult(PixelTransformer transformer) {
		return this.mainContext.createResult(transformer);
	}

	@Override
    public A createResult(PixelTransformer transformer, A area) {
		return this.mainContext.createResult(transformer, area);
    }

	@Override
    public A createResult(PixelTransformer transformer, A first, A second) {
		return this.mainContext.createResult(transformer, first, second);
    }

	// From the random context
	@Override
	public RandomSource createRandom(long x, long z) {
		return this.randomContext.createRandom(x, z);
	}
    
	@Override
	public ImprovedNoise getBiomeNoise() {
		return this.randomContext.getBiomeNoise();
	}
    
	@Override
	public int random(RandomSource random, int first, int second) {
        return this.randomContext.random(random, first, second);
    }

	@Override
	public int random(RandomSource random, int first, int second, int third, int fourth) {
        return this.randomContext.random(random, first, second, third, fourth);
    }

	@Override
	public int random(RandomSource random, int ...numbers) {
        return this.randomContext.random(random, numbers);
    }
	
	// Custom muxed context factory
	@Override
	public BiomeLayerContextFactory<A> getContextFactory() {
		BiomeLayerContext<A> originalMainContext = this.mainContext;
		while(originalMainContext instanceof MuxedBiomeLayerContext<A, ?> muxer) {
			originalMainContext = muxer.mainContext();
		}

		BiomeLayerContext<?> originalRandomContext = this.randomContext;
		while(originalRandomContext instanceof MuxedBiomeLayerContext<?, ?> muxer) {
			originalRandomContext = muxer.randomContext();
		}
		
		return new MuxedBiomeLayerContextFactory<>(this.mainContext.getContextFactory(), this.randomContext.getContextFactory());
	}

	public static record MuxedBiomeLayerContextFactory<A extends Area, B extends Area>(BiomeLayerContextFactory<A> mainContextFactory, BiomeLayerContextFactory<B> randomContextFactory) implements BiomeLayerContextFactory<A> {
		@Override
		public SeedMixer getSeedMixer() {
			return this.randomContextFactory().getSeedMixer();
		}
		
		@Override
		public BiomeLayerContext<A> createContext(long seedModifier) {
			BiomeLayerContext<A> mainContext = this.mainContextFactory.createContext(seedModifier);
			BiomeLayerContext<B> randomContext = this.randomContextFactory.createContext(seedModifier);
			return new MuxedBiomeLayerContext<A, B>(mainContext, randomContext);
		}
		
		@Override
		public BiomeLayerContext<A> createContext(String seedModifier) {
			BiomeLayerContext<A> mainContext = this.mainContextFactory.createContext(seedModifier);
			BiomeLayerContext<B> randomContext = this.randomContextFactory.createContext(seedModifier);
			return new MuxedBiomeLayerContext<A, B>(mainContext, randomContext);
		}

		@Override
		public BiomeLayerContext<A> createContext(ResourceLocation seedModifier) {
			BiomeLayerContext<A> mainContext = this.mainContextFactory.createContext(seedModifier);
			BiomeLayerContext<B> randomContext = this.randomContextFactory.createContext(seedModifier);
			return new MuxedBiomeLayerContext<A, B>(mainContext, randomContext);
		}
	}
}
