package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;

import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.function.Function;

public class DecoratorFunction {

    float count = 1;
    Function<DecoratorPositionProvider, Boolean> lambda;



    public DecoratorFunction(float tries, Function<DecoratorPositionProvider, Boolean> lambda) {
        this.count = tries;
        this.lambda = lambda;
    }

    public DecoratorFunction(Function<DecoratorPositionProvider, Boolean> func) {
        this.lambda = func;
    }

    /**
     * Updates the positions
     * @param world World
     * @param biome Biome
     * @param generator Chunk Generator
     * @param rand Rng
     * @param x X coordinate
     * @param y Y coordinate, use -1 for surface
     * @param z Z coordinate
     */
    public void init(DecoratorPositionProvider feature, WorldGenLevel world, int biome, @Nullable ChunkGeneratorBetweenlands generator, Random rand, int x, int y, int z) {
        feature.init(world, biome, generator, rand, x, y, z);
    }

    public boolean generate(DecoratorPositionProvider feature, Random rand) {
        boolean generated = false;
        float tries = (float) (Math.floor(this.count) + (rand.nextFloat() < (this.count - Math.floor(this.count)) ? 1 : 0));
        for (int i = 0; i < tries; i++) {
            if (feature.biome != feature.generator.betweenlandsBiomeProvider.biomeCache.getBiome(feature.x + 10, feature.z + 10, 0)){
                return false;
            }
            feature.init(feature.world, feature.biome, feature.generator, feature.rand, feature.x, feature.y, feature.z);
                if (this.lambda.apply(feature))
                    return true;
        }
        return generated;
    }
}
