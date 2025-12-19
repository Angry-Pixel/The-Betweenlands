package thebetweenlands.api.world.biome.layer;

@FunctionalInterface
public interface AreaFactoryOld<A extends Area> {
    A make();
}
