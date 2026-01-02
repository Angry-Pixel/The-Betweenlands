package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public interface BiomeLayerRandomContext {
	public BiomeLayerRandomFactoryContext getRandomFactory();
	
    RandomSource createRandom(long x, long z);
    
    ImprovedNoise getBiomeNoise();
    
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

    default int random(RandomSource random, int ...numbers) {
        return numbers[random.nextInt(numbers.length)];
    }
}
