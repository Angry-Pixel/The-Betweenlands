package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BiomeDecorator extends DecoratorPositionProvider {

    private static final List<String> profiledGenerators = new ArrayList<String>();
    private static boolean decorating;
    private final int biome;

    public final List<DecoratorFeature> features;
    public final List<DecoratorFunction> worldGenerators;

    public BiomeDecorator(List<DecoratorFeature> features, List<DecoratorFunction> worldGenerators, int biomeID) {
        this.worldGenerators = worldGenerators;
        this.features = features;
        this.biome = biomeID;
    }

    public BiomeDecorator(List<DecoratorFeature> features, int biomeID) {
        this.worldGenerators = new ArrayList<DecoratorFunction>();
        this.features = features;
        this.biome = biomeID;
    }

    public BiomeDecorator(int BiomeID) {
        this.worldGenerators = new ArrayList<DecoratorFunction>();
        this.features = new ArrayList<DecoratorFeature>();
        this.biome = BiomeID;
    }

    /**
     * Decorates the specified chunk
     *
     * @param world
     * @param rand
     * @param x
     * @param z
     */
    public final void decorate(WorldGenLevel world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
        this.init(world, this.biome, generator, rand, x, z);

        boolean wasDecorating = decorating;
        decorating = true;

        if (!wasDecorating) {
            //profiledGenerators.clear();
            //this.getProfiler().push(generator.betweenlandsBiomeProvider.BiomeFromID(this.getBiome()).biomeName);//startSection(generator.betweenlandsBiomeProvider.BiomeFromID(this.getBiome()).biomeName);
        }

        this.decorate();

        if (!wasDecorating) {
            //this.getProfiler().endSection();
        }

        if (!wasDecorating) {
            decorating = false;
        }
    }

    /**
     * Modifies the terrain with {@link DecoratorFeature} specific features.
     * @param blockX
     * @param blockZ
     * @param inChunkX
     * @param inChunkZ
     * @param baseBlockNoise
     * @param chunkPrimer
     * @param chunkGenerator
     * @param biomesForGeneration
     * @param biomeWeights
     * @param pass
     */
    public final void runBiomeFeatures(int blockX, int blockZ, int inChunkX, int inChunkZ,
                                       double baseBlockNoise, ChunkAccess chunkPrimer,
                                       ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration,
                                       BiomeWeights biomeWeights, BiomeGenerator.EnumGeneratorPass pass) {
        features.forEach((feature) -> {
            feature.replaceStackBlocks(inChunkX, inChunkZ, baseBlockNoise, chunkPrimer, chunkGenerator, biomesForGeneration, this.biome, biomeWeights, pass);
        });
        this.decorate();
    }

    /**
     * Decorates the terrain
     */
    public void decorate() {
        for (DecoratorFunction gen : this.worldGenerators) {
            gen.generate(this, this.rand); //.generate(this.world.getLevel(),this.rand,new BlockPos(this.x, this.y, this.z));
        }
    }

    /**
     * Tries to generate the specified feature
     *
     * @param tries     How many attempts should be run. If this value is < 1 it is used as a probability
     * @param generator
     * @return
     */
    public boolean generate(float tries, Function<BiomeDecorator, Boolean> generator) {
        boolean generated = false;
        tries = (float)(Math.floor(tries) + (this.getRand().nextFloat() <= (tries - Math.floor(tries)) ? 1 : 0));
        for (int i = 0; i < tries; i++) {
            if (generator.apply(this))
                generated = true;
        }
        return generated;
    }

    /**
     * Tries to generate the specified feature
     *
     * @param generator
     * @return
     */
    public boolean generate(Function<BiomeDecorator, Boolean> generator) {
        return this.generate(1, generator);
    }

    /**
     * Returns the profiler
     *
     * @return
     */
    public ProfilerFiller getProfiler() {
        return this.getWorld().getLevel().getProfiler();
    }
}
