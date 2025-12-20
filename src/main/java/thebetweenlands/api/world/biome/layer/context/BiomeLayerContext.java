package thebetweenlands.api.world.biome.layer.context;

import thebetweenlands.api.world.biome.layer.Area;

// TODO split context in two: one creates an Area from a PixelTransformer, and the other provides createRandom(...) and getBiomeNoise()
public interface BiomeLayerContext<A extends Area> extends RandomFactoryContext, AreaFactoryContext<A> {
	BiomeLayerContextFactory<A> getContextFactory();
}
