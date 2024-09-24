package thebetweenlands.common.world.gen.layer.util;

public interface AreaTransformer1 extends DimensionTransformer {
    default <A extends Area> AreaFactory<A> run(BigContext<A> context, AreaFactory<A> factory) {
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
