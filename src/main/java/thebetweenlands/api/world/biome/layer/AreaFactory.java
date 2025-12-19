package thebetweenlands.api.world.biome.layer;

@FunctionalInterface
public interface AreaFactory<A extends Area> {
//    A make(BiomeLayerContext<A> context, BiomeLayerChainState state);
    A make();
}
