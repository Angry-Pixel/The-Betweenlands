package thebetweenlands.api.world.biome.layer;

import java.util.function.Supplier;

public interface AreaFactoryContext<A extends Area> {

    A createResult(PixelTransformer transformer);

    default A createResult(PixelTransformer transformer, A area) {
        return this.createResult(transformer);
    }

    default A createResult(PixelTransformer transformer, A first, A second) {
        return this.createResult(transformer);
    }

    @FunctionalInterface
    public static interface AreaFactoryContextSupplier<A extends Area> extends Supplier<AreaFactoryContext<A>> { }
}
