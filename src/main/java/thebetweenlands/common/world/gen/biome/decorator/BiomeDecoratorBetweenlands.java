package thebetweenlands.common.world.gen.biome.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.OreGens;

public class BiomeDecoratorBetweenlands extends DecoratorPositionProvider {
    private final List<String> profiledGenerators = new ArrayList<String>();
    private boolean decorating;
    private final Biome biome;

    public BiomeDecoratorBetweenlands(Biome biome) {
        this.biome = biome;
    }

    @Override
    public Biome getBiome() {
        return this.biome;
    }

    public final boolean isDecorating() {
    	return decorating;
    }
    
    /**
     * Decorates the specified chunk
     *
     * @param world
     * @param rand
     * @param x
     * @param z
     */
    public final void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
    	if(this.decorating) {
    		throw new RuntimeException("Already Decorating!");
    	}
    	
        this.init(world, this.biome, generator, rand, x, z);

        //TODO re-evaluate the profiling, make sure it's still working and that decorators aren't piling up or anything
//        boolean wasDecorating = decorating;
        decorating = true;

//        if (!wasDecorating) {
            this.profiledGenerators.clear();
            this.getProfiler().startSection(this.getBiome().getRegistryName().getPath());
//        }

        this.decorate();

//        if (!wasDecorating) {
            this.getProfiler().endSection();
//        }

//        if (!wasDecorating) {
            decorating = false;
//        }
    }

    /**
     * Decorates the terrain
     */
    protected void decorate() {
        this.startProfilerSection("ores");
        this.generateOres();
        this.endProfilerSection();

        this.startProfilerSection("caves");
        this.generate(DecorationHelper::populateCaves);
        this.endProfilerSection();

        this.startProfilerSection("stagnantWater");
        this.generate(2, DecorationHelper::generateStagnantWaterPool);
        this.endProfilerSection();

        this.startProfilerSection("undergroundRuins");
        this.generate(200, DecorationHelper::generateUndergroundRuins);
        this.endProfilerSection();

        this.startProfilerSection("undergroundSpawners");
        this.generate(1.0F, DecorationHelper::generateSpawner);
        this.endProfilerSection();

        this.startProfilerSection("tarPoolDungeons");
        this.generate(1.0F, DecorationHelper::generateTarPoolDungeon);
        this.endProfilerSection();
    }

    /**
     * Generates the default ores
     */
    protected void generateOres() {
		this.generateOre(22, 12, OreGens.SULFUR, WorldProviderBetweenlands.PITSTONE_HEIGHT, 128);
		this.generateOre(6, 12, OreGens.SYRMORITE, WorldProviderBetweenlands.PITSTONE_HEIGHT + 40, WorldProviderBetweenlands.CAVE_START - 5);
		this.generateOre(5, 12, OreGens.BONE_ORE, WorldProviderBetweenlands.PITSTONE_HEIGHT, 128);
		this.generateOre(4.5F, 12, OreGens.OCTINE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 40);
		this.generateOre(4, 12, OreGens.SWAMP_DIRT, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(0.2F, 12, OreGens.LIMESTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(1, 12, OreGens.VALONITE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(3, 12, OreGens.SCABYST, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(70, 2, OreGens.LIFE_GEM, 0, WorldProviderBetweenlands.CAVE_WATER_HEIGHT);

        Random rand = this.getRand();
        World world = this.getWorld();

        //Generate middle gems
        int cycles = rand.nextInt(4) == 0 ? (1 + rand.nextInt(3)) : 0;
        gems: for (int i = 0; i < cycles; i++) {
            if (rand.nextInt(9 / cycles + 1) == 0) {
                int xx = this.getX() + this.offsetXZ();
                int zz = this.getZ() + this.offsetXZ();
                int yy = world.getHeight(new BlockPos(xx, 0, zz)).getY() - 1;
                boolean hasMud = false;
                for (int yo = 0; yo < 16; yo++) {
                    if (world.getBlockState(new BlockPos(xx, yy + yo, zz)).getBlock() == BlockRegistry.SWAMP_WATER
                            && world.getBlockState(new BlockPos(xx, yy + yo - 1, zz)).getBlock() == BlockRegistry.MUD) {
                        hasMud = true;
                        yy = yy + yo - 1;
                    }
                }
                if (hasMud) {
                    switch (rand.nextInt(3)) {
                        case 0:
                            world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.AQUA_MIDDLE_GEM_ORE.getDefaultState());
                            break gems;
                        case 1:
                            world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.getDefaultState());
                            break gems;
                        case 2:
                            world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.GREEN_MIDDLE_GEM_ORE.getDefaultState());
                            break gems;
                    }
                }
            }
        }
    }

    /**
     * Attempts to generate an ore vein
     *
     * @param tries
     * @param padding
     * @param oreGen
     * @param minY
     * @param maxY
     */
    protected void generateOre(float tries, int padding, WorldGenerator oreGen, int minY, int maxY) {
        tries = MathHelper.floor(tries) + (this.getRand().nextFloat() <= (tries - MathHelper.floor(tries)) ? 1 : 0);
        for (int i = 0; i < tries; i++) {
            int xx = this.getX() + this.offsetXZ(padding);
            int yy = minY + this.getRand().nextInt(maxY - minY);
            int zz = this.getZ() + this.offsetXZ(padding);
            BlockPos pos = new BlockPos(xx, yy, zz);
            if (this.getWorld().isAreaLoaded(pos, padding)) {
                oreGen.generate(this.getWorld(), this.getRand(), pos);
            }
        }
    }

    /**
     * Tries to generate the specified feature
     *
     * @param tries     How many attempts should be run. If this value is < 1 it is used as a probability
     * @param generator
     * @return
     */
    public boolean generate(float tries, Function<BiomeDecoratorBetweenlands, Boolean> generator) {
        boolean generated = false;
        tries = MathHelper.floor(tries) + (this.getRand().nextFloat() <= (tries - MathHelper.floor(tries)) ? 1 : 0);
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
    public boolean generate(Function<BiomeDecoratorBetweenlands, Boolean> generator) {
        return this.generate(1, generator);
    }

    /**
     * Returns the profiler
     *
     * @return
     */
    public Profiler getProfiler() {
        return this.getWorld().profiler;
    }

    /**
     * Returns whether profiling is enabled
     *
     * @return
     */
    public boolean isProfilingEnabled() {
        return BetweenlandsConfig.DEBUG.debug;
    }

    /**
     * Returns whether recursive profiling should be enabled
     *
     * @return
     */
    public boolean isRecursiveProfilingEnabled() {
        return false;
    }

    /**
     * Starts a profiler section if enabled
     *
     * @param name
     */
    public void startProfilerSection(String name) {
        if (this.isProfilingEnabled() && this.getProfiler().profilingEnabled && (this.isRecursiveProfilingEnabled() || !profiledGenerators.contains(this.getProfiler().getNameOfLastSection()))) {
            this.getProfiler().startSection(name);

            if (!this.isRecursiveProfilingEnabled()) {
                String section = this.getProfiler().getNameOfLastSection();
                profiledGenerators.add(section);
            }
        }
    }

    /**
     * Stops a profiler section if enabled
     */
    public void endProfilerSection() {
        if (this.isProfilingEnabled() && this.getProfiler().profilingEnabled) {
            if (!this.isRecursiveProfilingEnabled()) {
                String section = this.getProfiler().getNameOfLastSection();
                boolean contained = profiledGenerators.remove(section);
                if (!"[UNKNOWN]".equals(section) && contained) {
                    this.getProfiler().endSection();
                }
            } else {
                this.getProfiler().endSection();
            }
        }
    }
}
