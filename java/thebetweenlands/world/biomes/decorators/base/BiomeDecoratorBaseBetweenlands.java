package thebetweenlands.world.biomes.decorators.base;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
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
	protected World world;
	protected Random rand;
	protected int x, z;
	protected int xx, yy, zz, attempt;

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

	public final void populate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		this.generateOres();
		this.populate();
	}

	public final void decorate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		this.decorate();
	}
	
	public final void postTerrainGen(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		this.postTerrainGen2();
	}
	
	public final void postTerrainGen2(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;
		this.postTerrainGen2();
	}

	protected void populate() { }
 
	protected void decorate() { }

	protected void postTerrainGen() { }
	protected void postTerrainGen2() { }
	
	protected final int offsetXZ() {
		return rand.nextInt(16) + 8;
	}

	protected boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(world.getBlock(x, y - 1, z)) && world.isAirBlock(x, y, z);
	}

	protected void generateOres() {
		this.generateOre(20, OreGens.SULFUR, 0, 128);
		this.generateOre(20, OreGens.OCTINE, 0, 64);
		this.generateOre(2, OreGens.VALONITE, 0, 32);
		this.generateOre(1, OreGens.LIFE_GEM, 0, 16);
		
		//Generate middle gems
		if(this.rand.nextInt(5) == 0) {
			int xx = this.x + this.rand.nextInt(16);
			int zz = this.z + this.rand.nextInt(16);
			int yy = this.world.getHeightValue(xx, zz);
			boolean hasMud = false;
			if(this.world.getBlock(xx, yy, zz) == BLBlockRegistry.swampWater) {
				while(yy > 0) {
					if(this.world.getBlock(xx, yy, zz) == BLBlockRegistry.mud) {
						hasMud = true;
						break;
					}
					--yy;
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
	
	protected void generateOre(int tries, WorldGenMinableBetweenlands oreGen, int minY, int maxY) {
		for (int i = 0; i < tries; i++) {
			int xx = this.x + this.rand.nextInt(16);
			int yy = this.rand.nextInt(maxY) + this.rand.nextInt(maxY) + (minY - maxY);
			int zz = this.z + this.rand.nextInt(16);
			BiomeGenBaseBetweenlands biome = (BiomeGenBaseBetweenlands)this.getWorld().getBiomeGenForCoords(xx, zz);
			oreGen.prepare(biome.getBaseBlock()).generate(this.world, this.rand, xx, yy, zz);
		}
	}
}
