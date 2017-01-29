package thebetweenlands.common.world.gen.biome.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.OreGens;
import thebetweenlands.util.config.ConfigHandler;

public class BiomeDecoratorBetweenlands extends DecoratorPositionProvider {
	private static final List<String> profiledGenerators = new ArrayList<String>();
	private static boolean decorating;
	private final Biome biome;

	public BiomeDecoratorBetweenlands(Biome biome) {
		this.biome = biome;
	}

	@Override
	public Biome getBiome() {
		return this.biome;
	}

	/**
	 * Decorates the specified chunk
	 * @param world
	 * @param rand
	 * @param x
	 * @param z
	 */
	public final void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		this.init(world, this.biome, generator, rand, x, z);

		boolean wasDecorating = decorating;
		decorating = true;

		if(!wasDecorating) {
			profiledGenerators.clear();
			this.getProfiler().startSection(this.getBiome().getBiomeName());
		}

		this.decorate();

		if(!wasDecorating) {
			this.getProfiler().endSection();
		}

		if(!wasDecorating) {
			decorating = false;
		}
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
		this.generate(120, DecorationHelper::generateUndergroundRuins);
		this.endProfilerSection();

		this.startProfilerSection("undergroundSpawners");
		this.generate(1.0F, DecorationHelper::generateSpawner);
		this.endProfilerSection();
	}

	/**
	 * Generates the default ores
	 */
	protected void generateOres() {
		//TODO: Tweak these values for new terrain height
		this.generateOre(22, 14, OreGens.SULFUR, 0, 128);
		this.generateOre(10, 8, OreGens.SYRMORITE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, 8, OreGens.BONE_ORE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, 8, OreGens.OCTINE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);	
		this.generateOre(4, 12, OreGens.SWAMP_DIRT, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(0.2F, 14, OreGens.LIMESTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(1, 8, OreGens.VALONITE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(3, 8, OreGens.SCABYST, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(80, 8, OreGens.LIFE_GEM, 0, WorldProviderBetweenlands.CAVE_WATER_HEIGHT);

		Random rand = this.getRand();
		World world = this.getWorld();

		//Generate middle gems
		int cycles = 1 + (rand.nextBoolean() ? rand.nextInt(2) : 0);
		for(int i = 0; i < cycles; i++) {
			if(rand.nextInt(9 / cycles + 1) == 0) {
				int xx = this.getX() + this.offsetXZ();
				int zz = this.getZ() + this.offsetXZ();
				int yy = world.getHeight(new BlockPos(xx, 0, zz)).getY() - 1;
				boolean hasMud = false;
				for(int yo = 0; yo < 16; yo++) {
					if(world.getBlockState(new BlockPos(xx, yy + yo, zz)).getBlock() == BlockRegistry.SWAMP_WATER
							&& world.getBlockState(new BlockPos(xx, yy + yo - 1, zz)).getBlock() == BlockRegistry.MUD) {
						hasMud = true;
						yy = yy + yo - 1;
					}
				}
				if(hasMud) {
					switch(rand.nextInt(3)) {
					case 0:
						world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.AQUA_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 1:
						world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 2:
						world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.GREEN_MIDDLE_GEM_ORE.getDefaultState());
						break;
					}
				}
			}
		}
	}

	/**
	 * Attempts to generate an ore vein
	 * @param tries
	 * @param padding
	 * @param oreGen
	 * @param minY
	 * @param maxY
	 */
	protected void generateOre(float tries, int padding, WorldGenerator oreGen, int minY, int maxY) {
		tries = tries >= 1.0F ? tries : (this.getRand().nextFloat() <= tries ? 1 : 0);
		for (int i = 0; i < tries; i++) {
			int xx = this.getX() + this.offsetXZ(padding);
			int yy = minY + this.getRand().nextInt(maxY - minY);
			int zz = this.getZ() + this.offsetXZ(padding);
			oreGen.generate(this.getWorld(), this.getRand(), new BlockPos(xx, yy, zz));
		}
	}

	/**
	 * Tries to generate the specified feature
	 * @param tries How many attempts should be run. If this value is < 1 it is used as a probability
	 * @param generator
	 * @return
	 */
	public boolean generate(float tries, Function<BiomeDecoratorBetweenlands, Boolean> generator) {
		boolean generated = false;
		if(tries < 1.0F) {
			if(this.getRand().nextFloat() <= tries) {
				generated = generator.apply(this);
			}
		} else {
			float remainder = tries % 1.0F;

			if(this.getRand().nextFloat() <= remainder) {
				tries++;
			}

			for(int i = 0; i < tries; i++) {
				if(generator.apply(this))
					generated = true;
			}
		}
		return generated;
	}

	/**
	 * Tries to generate the specified feature
	 * @param generator
	 * @return
	 */
	public boolean generate(Function<BiomeDecoratorBetweenlands, Boolean> generator) {
		return this.generate(1, generator);
	}

	/**
	 * Returns the profiler
	 * @return
	 */
	public Profiler getProfiler() {
		return this.getWorld().theProfiler;
	}

	/**
	 * Returns whether profiling is enabled
	 * @return
	 */
	public boolean isProfilingEnabled() {
		return ConfigHandler.debug;
	}

	/**
	 * Returns whether recursive profiling should be enabled
	 * @return
	 */
	public boolean isRecursiveProfilingEnabled() {
		return false;
	}

	/**
	 * Starts a profiler section if enabled
	 * @param name
	 */
	public void startProfilerSection(String name) {
		if(this.isProfilingEnabled() && this.getProfiler().profilingEnabled && (this.isRecursiveProfilingEnabled() || !profiledGenerators.contains(this.getProfiler().getNameOfLastSection()))) {
			this.getProfiler().startSection(name);

			if(!this.isRecursiveProfilingEnabled()) {
				String section = this.getProfiler().getNameOfLastSection();
				profiledGenerators.add(section);
			}
		}
	}

	/**
	 * Stops a profiler section if enabled
	 */
	public void endProfilerSection() {
		if(this.isProfilingEnabled() && this.getProfiler().profilingEnabled) {
			if(!this.isRecursiveProfilingEnabled()) {
				String section = this.getProfiler().getNameOfLastSection();
				boolean contained = profiledGenerators.remove(section);
				if(!"[UNKNOWN]".equals(section) && contained) {
					this.getProfiler().endSection();
				}
			} else {
				this.getProfiler().endSection();
			}
		}
	}
}
