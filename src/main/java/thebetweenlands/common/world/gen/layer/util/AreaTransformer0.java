package thebetweenlands.common.world.gen.layer.util;

public interface AreaTransformer0 {
    default <A extends Area> AreaFactory<A> run(BigContext<A> context) {
        return () -> context.createResult((x, z) -> {
            context.initRandom(x, z);
            return this.apply(context, x, z);
        });
    }

    int apply(Context context, int x, int z);
}
