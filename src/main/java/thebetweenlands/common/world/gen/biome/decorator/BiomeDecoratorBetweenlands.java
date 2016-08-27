package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.OreGens;

public class BiomeDecoratorBetweenlands {
	protected World world;
	protected int x, z;
	protected Random rand;

	public final int offsetXZ() {
		return this.rand.nextInt(16) + 8;
	}

	public void decorate(World world, Random rand, int x, int z) {
		this.x = x;
		this.z = z;
		this.rand = rand;
		this.world = world;

		//TODO: Implement ore gen
		//this.generateOres();
	}

	protected void generateOres() {
		this.generateOre(22, OreGens.SULFUR, 0, 128);
		this.generateOre(10, OreGens.SYRMORITE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, OreGens.BONE_ORE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(10, OreGens.OCTINE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);	
		this.generateOre(10, OreGens.SWAMP_DIRT, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(1, OreGens.LIMESTONE, WorldProviderBetweenlands.PITSTONE_HEIGHT, WorldProviderBetweenlands.CAVE_START - 15);
		this.generateOre(3, OreGens.VALONITE, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(4, OreGens.SCABYST, 0, WorldProviderBetweenlands.PITSTONE_HEIGHT);
		this.generateOre(200, OreGens.LIFE_GEM, 0, WorldProviderBetweenlands.CAVE_WATER_HEIGHT);

		//Generate middle gems
		int cycles = 1 + (this.rand.nextBoolean() ? this.rand.nextInt(2) : 0);
		if(this.world.getBiomeGenForCoords(new BlockPos(this.x, 0, this.z)) == BiomeRegistry.SLUDGE_PLAINS) {
			cycles = 5 + this.rand.nextInt(3);
		}
		for(int i = 0; i < cycles; i++) {
			if(this.rand.nextInt(9 / cycles + 1) == 0) {
				int xx = this.x + this.offsetXZ();
				int zz = this.z + this.offsetXZ();
				int yy = this.world.getHeight(new BlockPos(xx, 0, zz)).getY() - 1;
				boolean hasMud = false;
				for(int yo = 0; yo < 16; yo++) {
					int bx = yy + yo;
					if(this.world.getBlockState(new BlockPos(xx, yy + yo, zz)).getBlock() == BlockRegistry.SWAMP_WATER
							&& this.world.getBlockState(new BlockPos(xx, yy + yo - 1, zz)).getBlock() == BlockRegistry.MUD) {
						hasMud = true;
						yy = bx - 1;
					}
				}
				if(hasMud) {
					switch(this.rand.nextInt(3)) {
					case 0:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.AQUA_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 1:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.getDefaultState());
						break;
					case 2:
						this.world.setBlockState(new BlockPos(xx, yy, zz), BlockRegistry.GREEN_MIDDLE_GEM_ORE.getDefaultState());
						break;
					}
				}
			}
		}
	}

	protected void generateOre(int tries, WorldGenerator oreGen, int minY, int maxY) {
		for (int i = 0; i < tries; i++) {
			int xx = this.x + this.offsetXZ();
			int yy = this.rand.nextInt(maxY) + this.rand.nextInt(maxY) + (minY - maxY);
			int zz = this.z + this.offsetXZ();
			oreGen.generate(this.world, this.rand, new BlockPos(xx, yy, zz));
		}
	}
}
