package thebetweenlands.common.world.gen.layer.old.util;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;

public interface AreaTransformer0 {
    default <A extends Area> AreaFactory<A> run(BigContext<A> context) {
        return () -> context.createResult((x, z) -> {
            context.initRandom(x, z);
            return this.apply(context, x, z);
        });
    }

    int apply(Context context, int x, int z);
}
