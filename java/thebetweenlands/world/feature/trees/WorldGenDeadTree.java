package thebetweenlands.world.feature.trees;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockPoisonIvy;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.blocks.tree.BlockTreeFungus;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;

import java.util.Random;

public class WorldGenDeadTree implements IWorldGenerator {
	private BlockBLLog bark = (BlockBLLog) BLBlockRegistry.weedwoodBark;
	private BlockBLLog wood = (BlockBLLog) BLBlockRegistry.weedwood;
	private BlockTreeFungus fungus = (BlockTreeFungus) BLBlockRegistry.treeFungus;
	private BlockPoisonIvy ivy = (BlockPoisonIvy) BLBlockRegistry.poisonIvy;
	
	private int height = -1;
	private int radius = -1;
	private int maxRadius = -1;

	public WorldGenDeadTree() {
	}
	
    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider.dimensionId == ConfigHandler.DIMENSION_ID) {
            generate(world, rand, chunkX * 16, chunkZ * 16);
        }
    }

    private void generate(World world, Random rand, int x, int z) {
    	int newY = 82 + rand.nextInt(5);
        BiomeGenBase biomeBase = world.getBiomeGenForCoords(x, z);
        if(validBiomeForGen(biomeBase)) {
            for(int newX = x - 5; newX <= x + 5; ++newX) { //magic numbers for testing
                for(int newZ = z - 5; newZ <= z + 5; ++newZ) {
                    Block block = world.getBlock(newX, newY, newZ);
                    if(block != null && block == biomeBase.topBlock) {
                        if(rand.nextInt(ConfigHandler.GIANT_TREE_DENSITY) == 0 ) { //config option
                            generateTree(world, rand, x, newY, z);
                            break;
                        }
                    }
                }
            }
        }
    }

	private boolean validBiomeForGen(BiomeGenBase biomeBase) {
		return biomeBase == BLBiomeRegistry.swampLands || biomeBase == BLBiomeRegistry.coarseIslands || biomeBase == BLBiomeRegistry.patchyIslands;
	}

	public boolean generateTree(World world, Random rand, int x, int y, int z) {
		
		radius = rand.nextInt(4) + 8;
		height = rand.nextInt(radius) + radius;
		maxRadius = radius + height / 3;
	
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz))
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			if ((yy - y) % 5 == 0 && radius > 2)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;

					if (Math.round(Math.sqrt(dSq)) == radius && yy < y + height - 1) {
						world.setBlock(x + i, yy, z + j, bark, 0, 2);
						addVines(world, rand, x + i, yy, z + j);
					}

					if (Math.round(Math.sqrt(dSq)) < radius)
						world.setBlock(x + i, yy, z + j, wood);

					if (yy == y + height - 1)
						smashTopUpABit(world, rand, x, yy, z, radius);

					if (Math.round(Math.sqrt(dSq)) == radius && yy == y + height - 1)
						if(rand.nextInt(3) != 0)
							world.setBlock(x + i, yy, z + j, bark, 0, 2);

					if (Math.round(Math.sqrt(dSq)) == radius && yy % 5 == 0 && yy >= y + 6 && yy <= y + height - 4) {
						if(rand.nextInt(32) == 0) {
							int randomDirection = rand.nextInt(8);
							switch (randomDirection) {
							case 0:
								createFungus(world, rand, x - 2 - radius/2, yy, z, rand.nextInt(3) + radius/2);
								break;
							case 1:
								createFungus(world, rand, x + 2 + radius/2, yy, z, rand.nextInt(3) + radius/2);
								break;
							case 2:
								createFungus(world, rand, x, yy, z - 2 - radius/2, rand.nextInt(3) + radius/2);
								break;
							case 3:
								createFungus(world, rand, x, yy, z + 2  + radius/2, rand.nextInt(3) + radius/2);
								break;
							case 4:
								createFungus(world, rand, x - 2 - radius/2, yy, z - 2 - radius/2, rand.nextInt(3) + radius/2);
								break;
							case 5:
								createFungus(world, rand, x + 2  + radius/2, yy, z + 2 + radius/2, rand.nextInt(3) + radius/2);
								break;
							case 6:
								createFungus(world, rand, x + 2 + radius/2, yy, z - 2 - radius/2, rand.nextInt(3) + radius/2);
								break;
							case 7:
								createFungus(world, rand, x - 2 - radius/2, yy, z + 2 + radius/2, rand.nextInt(3) + radius/2);
								break;
							}
						}
					}
				}
			
			if (yy == y + 3) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z, 1, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z, 2, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, rand.nextInt(9) + 6);

				createBranch(world, rand, x + radius - 1, yy - rand.nextInt(3), z + radius, 5, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius + 1, yy - rand.nextInt(3), z - radius, 6, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius, yy - rand.nextInt(3), z + radius - 1, 7, rand.nextInt(9) + 6);
				createBranch(world, rand, x + radius, yy - rand.nextInt(3), z - radius + 1, 8, rand.nextInt(9) + 6);
			}

		}
		return true;
	}

	private void smashTopUpABit(World world, Random rand, int x, int y, int z, int radius) {
		for (int x1 = x - radius; x1 <= x + radius; x1++)
			for (int z1 = z - radius; z1 <= z + radius; z1++)
				for (int y1 = y; y1 > y - radius - 1; y1--) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= radius)
							if (rand.nextInt(3) == 0)
								world.setBlockToAir(x1, y1, z1);
				}

	}

	private void createFungus(World world, Random rand, int x, int y, int z, int radius) {
		for (int yy = y; yy > y - radius; yy -= 2) {
			if (radius > 1)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) <= radius)
						if (world.getBlock(x + i, yy, z + j) == Blocks.air)
							world.setBlock(x + i, yy, z + j, fungus);
				}
		}
	}

	private void createBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {

		for (int i = 0; i <= branchLength; ++i) {

			 if (i >= 5)
				y--;

			if (dir == 1) {
				world.setBlock(x + i, y, z, bark);
				world.setBlock(x + i, y - 1, z, wood);
				world.setBlock(x + i, y - 1, z + 1, bark);
				world.setBlock(x + i, y - 1, z - 1, bark);
				world.setBlock(x + i, y - 2, z, bark);
			}
			if (dir == 2) {
				world.setBlock(x - i, y, z, bark);
				world.setBlock(x - i, y - 1, z, wood);
				world.setBlock(x - i, y - 1, z + 1, bark);
				world.setBlock(x - i, y - 1, z - 1, bark);
				world.setBlock(x - i, y - 2, z, bark);
			}
			if (dir == 3) {
				world.setBlock(x, y, z + i, bark);
				world.setBlock(x, y - 1, z + i, wood);
				world.setBlock(x + 1, y - 1, z + i, bark);
				world.setBlock(x - 1, y - 1, z + i, bark);
				world.setBlock(x, y - 2, z + i, bark);
			}
			if (dir == 4) {
				world.setBlock(x, y, z - i, bark);
				world.setBlock(x, y - 1, z - i, wood);
				world.setBlock(x + 1, y - 1, z - i, bark);
				world.setBlock(x - 1, y - 1, z - i, bark);
				world.setBlock(x, y - 2, z - i, bark);
			}
			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 1, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 2, z + i - 1, bark);
				world.setBlock(x + i - 2, y - 1, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 1, z + i - 2, bark);
			}
			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 1, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 2, z - i + 1, bark);
				world.setBlock(x - i + 2, y - 1, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 1, z - i + 2, bark);
			}
			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 1, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 2, z + i - 1, bark);
				world.setBlock(x - i + 2, y - 1, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 1, z + i - 2, bark);
			}
			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 1, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 2, z - i + 1, bark);
				world.setBlock(x + i - 2, y - 1, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 1, z - i + 2, bark);
			}
		}
	}

	public void addVines(World world, Random rand, int x, int y, int z) {
		int offset = 1;
		switch (rand.nextInt(4)) {
		case 0:
			if (world.isAirBlock(x, y, z - offset))
				world.setBlock(x, y, z - offset, ivy, 1, 2);
			break;
		case 1:
			if (world.isAirBlock(x, y, z + offset))
				world.setBlock(x, y, z + offset, ivy, 4, 2);
			break;
		case 2:
			if (world.isAirBlock(x - offset, y, z))
				world.setBlock(x - offset, y, z, ivy, 8, 2);
			break;
		case 3:
			if (world.isAirBlock(x + offset, y, z))
				world.setBlock(x + offset, y, z, ivy, 2, 2);
			break;
		}
	}

}
