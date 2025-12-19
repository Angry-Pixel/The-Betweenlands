package thebetweenlands.api.world.biome.layer;

@FunctionalInterface
public interface PixelTransformer {
    int apply(int x, int z);
}
