package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.PixelTransformer;

public interface BiomeLayerContext<A extends Area> {
    RandomSource createRandom(long x, long z);
    
    ImprovedNoise getBiomeNoise();

    A createResult(PixelTransformer transformer);

    default A createResult(PixelTransformer transformer, A area) {
        return this.createResult(transformer);
    }

    default A createResult(PixelTransformer transformer, A first, A second) {
        return this.createResult(transformer);
    }

    default int random(RandomSource random, int first, int second) {
        return random.nextInt(2) == 0 ? first : second;
    }

    default int random(RandomSource random, int first, int second, int third, int fourth) {
        return switch (random.nextInt(4)) {
            case 0 -> first;
            case 1 -> second;
            case 2 -> third;
            default -> fourth;
        };
    }
}
