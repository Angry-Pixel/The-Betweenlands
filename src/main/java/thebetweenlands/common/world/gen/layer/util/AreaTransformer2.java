package thebetweenlands.common.world.gen.layer.util;

public interface AreaTransformer2 extends DimensionTransformer {
    default <A extends Area> AreaFactory<A> run(BigContext<A> context, AreaFactory<A> first, AreaFactory<A> second) {
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
