package thebetweenlands.world.biomes.decorators.base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import thebetweenlands.world.biomes.base.ChunkDataAccess;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.gen.OreGens;
import thebetweenlands.world.feature.gen.WorldGenMinableBetweenlands;

/**
 *
 * @author The Erebus Team
 *
 */
public class BiomeDecoratorBaseBetweenlands
{
	private int postChunkPopulatePasses = 1;
	private int postChunkGenPasses = 1;
	protected World world;
	protected Random rand;
	protected int x, z;
	protected int xx, yy, zz, attempt;

	public final BiomeDecoratorBaseBetweenlands setPostChunkGenPasses(int passes) {
		this.postChunkGenPasses = passes;
		return this;
	}

	public final BiomeDecoratorBaseBetweenlands setPostChunkPopulatePasses(int passes) {
		this.postChunkPopulatePasses = passes;
		return this;
	}

	public final int getX() {
		return this.x;
	}

	public final int getZ() {
		return this.z;
	}

	public final Random getRNG() {
		return this.rand;
	}

	public final World getWorld() {
		return this.world;
	}

	public final void postChunkPopulate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		this.generateOres();
		for(int i = 0; i < this.postChunkPopulatePasses; i++) {
			this.postChunkPopulate(i);
		}
	}

	public final void postChunkGen(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		for(int i = 0; i < this.postChunkGenPasses; i++) {
			this.postChunkGen(i);
		}
	}

	public final void preChunkProvide(World world, Random rand, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		this.preChunkProvide(world, rand, new ChunkDataAccess(chunkX, chunkZ, blocks, metadata, biomes));
	}

	protected void preChunkProvide(World world, Random rand, ChunkDataAccess dataAccess) { }

	protected void postChunkPopulate(int pass) { }

	protected void postChunkGen(int pass) { }

	protected final int offsetXZ() {
		return rand.nextInt(16) + 8;
	}

	protected boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(world.getBlock(x, y - 1, z)) && world.isAirBlock(x, y, z);
	}

	protected void generateOres() {
		this.generateOre(22, OreGens.SULFUR, 0, 128);
		this.generateOre(10, OreGens.SYRMORITE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, OreGens.OCTINE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);	
		this.generateOre(10, OreGens.SMOOTH_BETWEENSTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, OreGens.SWAMP_DIRT, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(1, OreGens.LIMESTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, OreGens.SMOOTH_PITSTONE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(3, OreGens.VALONITE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(12, OreGens.LIFE_GEM, 0, WorldProviderBetweenlands.CAVE_WATER_HEIGHT);

		//Generate middle gems
		int cycles = 1 + (this.rand.nextBoolean() ? this.rand.nextInt(2) : 0);
		if(this.world.getBiomeGenForCoords(this.x, this.z) == BLBiomeRegistry.sludgePlains) {
			cycles = 5 + this.rand.nextInt(3);
		}
		for(int i = 0; i < cycles; i++) {
			if(this.rand.nextInt(9 / cycles + 1) == 0) {
				int xx = this.x + this.rand.nextInt(16);
				int zz = this.z + this.rand.nextInt(16);
				int yy = this.world.getHeightValue(xx, zz) - 1;
				boolean hasMud = false;
				for(int yo = 0; yo < 16; yo++) {
					int bx = yy + yo;
					if(this.world.getBlock(xx, yy + yo, zz) == BLBlockRegistry.swampWater
							&& this.world.getBlock(xx, yy + yo - 1, zz) == BLBlockRegistry.mud) {
						hasMud = true;
						yy = bx - 1;
					}
				}
				if(hasMud) {
					switch(this.rand.nextInt(3)) {
					case 0:
						this.world.setBlock(xx, yy, zz, BLBlockRegistry.aquaMiddleGemOre);
						break;
					case 1:
						this.world.setBlock(xx, yy, zz, BLBlockRegistry.crimsonMiddleGemOre);
						break;
					case 2:
						this.world.setBlock(xx, yy, zz, BLBlockRegistry.greenMiddleGemOre);
						break;
					}
				}
			}
		}
	}

	protected void generateOre(int tries, WorldGenMinableBetweenlands oreGen, int minY, int maxY) {
		for (int i = 0; i < tries; i++) {
			int xx = this.x + this.rand.nextInt(16);
			int yy = this.rand.nextInt(maxY) + this.rand.nextInt(maxY) + (minY - maxY);
			int zz = this.z + this.rand.nextInt(16);
			oreGen.generate(this.world, this.rand, xx, yy, zz);
		}
	}
}
