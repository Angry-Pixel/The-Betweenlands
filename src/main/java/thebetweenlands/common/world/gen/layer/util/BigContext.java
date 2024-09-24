package thebetweenlands.common.world.gen.layer.util;

public interface BigContext<A extends Area> extends Context {
    void initRandom(long x, long z);

    A createResult(PixelTransformer transformer);

    default A createResult(PixelTransformer transformer, A area) {
        return this.createResult(transformer);
    }

    default A createResult(PixelTransformer transformer, A first, A second) {
        return this.createResult(transformer);
    }

    default int random(int first, int second) {
        return this.nextRandom(2) == 0 ? first : second;
    }

    default int random(int first, int second, int third, int fourth) {
        return switch (this.nextRandom(4)) {
            case 0 -> first;
            case 1 -> second;
            case 2 -> third;
            default -> fourth;
        };
    }
}
