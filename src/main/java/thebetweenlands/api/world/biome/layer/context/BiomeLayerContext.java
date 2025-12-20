package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext.AreaFactoryContextSupplier;

/**
 * The context used by biome layers to generate Areas.
 * <br/>
 * Note: intentionally does not implement {@linkplain AreaFactoryContext<A>} or {@linkplain BiomeLayerRandomContext} because that would cause issues
 * @param <A>
 */
public record BiomeLayerContext<A extends Area>(AreaFactoryContextSupplier<A> areaContext, BiomeLayerRandomContext randomContext) {

	// Swaps out the randomContext for a different one
	public BiomeLayerContext<A> useRandomContext(BiomeLayerRandomContext randomContext) {
		return new BiomeLayerContext<>(this.areaContext, randomContext);
	}

	// Creates a new randomContext from a resolver, and swaps it out
	public BiomeLayerContext<A> useRandomFactory(BiomeLayerRandomContextResolver randomFactory) {
		return new BiomeLayerContext<>(this.areaContext, randomFactory.createContext(this.randomContext().getRandomFactory()));
	}

//	// AreaFactory Context methods
//    public A createResult(PixelTransformer transformer) {
//    	return this.areaContext().createResult(transformer);
//    }
//
//    public A createResult(PixelTransformer transformer, A area) {
//    	return this.areaContext().createResult(transformer, area);
//    }
//
//    public A createResult(PixelTransformer transformer, A first, A second) {
//    	return this.areaContext().createResult(transformer, first, second);
//    }
    
    // Random Context methods
    public RandomSource createRandom(long x, long z) {
    	return this.randomContext().createRandom(x, z);
    }
    
    public ImprovedNoise getBiomeNoise() {
    	return this.randomContext().getBiomeNoise();
    }

    public int random(RandomSource random, int first, int second) {
        return this.randomContext().random(random, first, second);
    }

    public int random(RandomSource random, int first, int second, int third, int fourth) {
        return this.randomContext().random(random, first, second, third, fourth);
    }

    public int random(RandomSource random, int ...numbers) {
        return this.randomContext().random(random, numbers);
    }
}
