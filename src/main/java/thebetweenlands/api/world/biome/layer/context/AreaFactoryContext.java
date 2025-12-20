package thebetweenlands.api.world.biome.layer.context;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.PixelTransformer;

public interface AreaFactoryContext<A extends Area> {

    A createResult(PixelTransformer transformer);

    default A createResult(PixelTransformer transformer, A area) {
        return this.createResult(transformer);
    }

    default A createResult(PixelTransformer transformer, A first, A second) {
        return this.createResult(transformer);
    }

}
