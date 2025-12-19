package thebetweenlands.common.world.gen.layer.old.util;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactoryOld;

public interface AreaTransformer1 extends DimensionTransformer {
    default <A extends Area> AreaFactoryOld<A> run(BigContext<A> context, AreaFactoryOld<A> factory) {
        return () -> {
            A area = factory.make();
            return context.createResult((x, z) -> {
                context.initRandom(x, z);
                return this.apply(context, area, x, z);
            }, area);
        };
    }

    int apply(BigContext<?> context, Area area, int x, int z);
}
