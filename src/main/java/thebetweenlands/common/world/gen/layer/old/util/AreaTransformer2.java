package thebetweenlands.common.world.gen.layer.old.util;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactoryOld;

public interface AreaTransformer2 extends DimensionTransformer {
    default <A extends Area> AreaFactoryOld<A> run(BigContext<A> context, AreaFactoryOld<A> first, AreaFactoryOld<A> second) {
        return () -> {
            A fa = first.make();
            A sa = second.make();
            return context.createResult((x, z) -> {
                context.initRandom(x, z);
                return this.applyPixel(context, fa, sa, x, z);
            }, fa, sa);
        };
    }

    int applyPixel(Context context, Area first, Area second, int x, int z);
}
