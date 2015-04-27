package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockHanger;
import thebetweenlands.blocks.plants.BlockPoisonIvy;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.blocks.tree.BlockTreeFungus;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenGiantTree implements IWorldGenerator {
	private BlockBLLog log = (BlockBLLog) BLBlockRegistry.weedwoodLog;
	private BlockBLLog bark = (BlockBLLog) BLBlockRegistry.weedwoodBark;
	private BlockBLLog wood = (BlockBLLog) BLBlockRegistry.weedwood;
	private BlockBLLeaves leaves = (BlockBLLeaves) BLBlockRegistry.weedwoodLeaves;
	private BlockTreeFungus fungus = (BlockTreeFungus) BLBlockRegistry.treeFungus;
	private BlockHanger hanger = (BlockHanger) BLBlockRegistry.hanger;
	private BlockPoisonIvy ivy = (BlockPoisonIvy) BLBlockRegistry.poisonIvy;
	
	private int height = -1;
	private int radius = -1;
	private int maxRadius = -1;
	
	public WorldGenGiantTree() {

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
		height = rand.nextInt(radius) + radius * 4;
		maxRadius = radius + height / 3;
	
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(xx, yy, zz) && world.getBlock(xx, yy, zz).isBlockNormalCube())
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			if ((yy - y) % 5 == 0 && radius > 2)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius)
						world.setBlock(x + i, yy, z + j, wood, 0, 2);
					if (Math.round(Math.sqrt(dSq)) == radius || Math.round(Math.sqrt(dSq)) <= radius && yy == y + height - 1)
						world.setBlock(x + i, yy, z + j, bark, 0, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy % 5 == 0 && yy >= y + height/4 && yy <= y + height/4 * 3 - 3) {
						if(rand.nextInt(32) == 0) {
							int randomDirection = rand.nextInt(8);
							switch (randomDirection) {
							case 0:
								createFungus(world, rand, x - 2 - radius/2, yy, z, rand.nextInt(3) + radius);
								break;
							case 1:
								createFungus(world, rand, x + 2 + radius/2, yy, z, rand.nextInt(3) + radius);
								break;
							case 2:
								createFungus(world, rand, x, yy, z - 2 - radius/2, rand.nextInt(3) + radius);
								break;
							case 3:
								createFungus(world, rand, x, yy, z + 2  + radius/2, rand.nextInt(3) + radius);
								break;
							case 4:
								createFungus(world, rand, x - 2 - radius/2, yy, z - 2 - radius/2, rand.nextInt(3) + radius);
								break;
							case 5:
								createFungus(world, rand, x + 2  + radius/2, yy, z + 2 + radius/2, rand.nextInt(3) + radius);
								break;
							case 6:
								createFungus(world, rand, x + 2 + radius/2, yy, z - 2 - radius/2, rand.nextInt(3) + radius);
								break;
							case 7:
								createFungus(world, rand, x - 2 - radius/2, yy, z + 2 + radius/2, rand.nextInt(3) + radius);
								break;
							}
						}
					}
				}
			if (yy == y + height/4 * 3) {
				createBranch(world, rand, x + radius + 1, yy, z, 1, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x - radius - 1, yy, z, 2, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x, yy, z + radius + 1, 3, false, height/3 + rand.nextInt(height/10));
				createBranch(world, rand, x, yy, z - radius - 1, 4, false, height/3 + rand.nextInt(height/10));
			}
			if (yy == y + height/4 * 3 + 3) {
				createBranch(world, rand, x + radius, yy, z + radius, 5, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x - radius - 1, yy, z - radius - 1, 6, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x - radius - 1, yy, z + radius + 1, 7, false, height/4 + rand.nextInt(height/10));
				createBranch(world, rand, x + radius + 1, yy, z - radius - 1, 8, false, height/4 + rand.nextInt(height/10));
			}
			if (yy == y + height/4 * 3 + 6) {
				createBranch(world, rand, x + radius + 1, yy, z, 1, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x - radius - 1, yy, z, 2, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x, yy, z + radius + 1, 3, false, height/5 + rand.nextInt(height/15));
				createBranch(world, rand, x, yy, z - radius - 1, 4, false, height/5 + rand.nextInt(height/15));
			}
			if (yy == y + height/4 * 3 + 9) {
				createBranch(world, rand, x + radius, yy, z + radius, 5, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x - radius - 1, yy, z - radius - 1, 6, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x - radius - 1, yy, z + radius + 1, 7, false, height/6 + rand.nextInt(height/15));
				createBranch(world, rand, x + radius + 1, yy, z - radius - 1, 8, false, height/6 + rand.nextInt(height/15));
			}
			if (yy == y + 3) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z, 1, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z, 2, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, true, rand.nextInt(9) + 6);

				createBranch(world, rand, x + radius - 1, yy - rand.nextInt(3), z + radius, 5, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius + 1, yy - rand.nextInt(3), z - radius, 6, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x - radius, yy - rand.nextInt(3), z + radius - 1, 7, true, rand.nextInt(9) + 6);
				createBranch(world, rand, x + radius, yy - rand.nextInt(3), z - radius + 1, 8, true, rand.nextInt(9) + 6);
			}
		}
		createMainCanopy(world, rand, x, y + height, z, height/3 + rand.nextInt(height/10));
		return true;
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

	private void createSmallBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		int meta = dir;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				meta = 0;
			}

			if (dir == 1) {
				world.setBlock(x + i, y, z, log, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 2) {
				world.setBlock(x - i, y, z, log, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 3) {
				world.setBlock(x, y, z + i, log, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x, y, z + i, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 4) {
				world.setBlock(x, y, z - i, log, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x, y, z - i, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, log, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i - 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, log, meta == 0 ? 0 : 4, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i + 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, log, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x - i + 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, log, meta == 0 ? 0 : 8, 2);
				if (i == branchLength)
					createMainCanopy(world, rand, x + i - 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 4));
			}
		}
	}

	private void createMainCanopy(World world, Random rand, int x, int y, int z, int maxRadius) {
		for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
			for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
				for (int y1 = y; y1 < y + maxRadius; y1 ++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.5D);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && rand.nextInt(4) == 0 && y1 > y)
						if (world.getBlock(x1, y1, z1) != log)
							world.setBlock(x1, y1, z1, log);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlock(x1, y1, z1) != log && world.getBlock(x1, y1, z1) != bark)
							world.setBlock(x1, y1, z1, leaves, 0, 15);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(2) == 0 && y1 == y)
						if (world.getBlock(x1, y1, z1) != log && world.getBlock(x1, y1, z1) == leaves)
							for (int i = 1; i < 1 + rand.nextInt(3); i++) {
								world.setBlock(x1, y1 - i, z1, leaves, 0, 15);
								if(i == 2)
									addHangers(world, rand, x1, y1 - i - 1, z1);
								}
				}
	}

	private void createBranch(World world, Random rand, int x, int y, int z, int dir, boolean root, int branchLength) {

		for (int i = 0; i <= branchLength; ++i) {

			if (!root && i >= 3)
				y++;
			else if (root && i >= 5)
				y--;

			if (dir == 1) {
				world.setBlock(x + i, y, z, bark);
				world.setBlock(x + i, y - 1, z, wood);
				world.setBlock(x + i, y - 1, z + 1, bark);
				world.setBlock(x + i, y - 1, z - 1, bark);
				world.setBlock(x + i, y - 2, z, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x + i, y - 3, z, 2);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + i, y - 1, z + 1, 3, 4);
						createSmallBranch(world, rand, x + i, y - 1, z - 1, 4, 4);
					}
						addVines(world, rand, x + i, y - 3, z, 2);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i, y, z, branchLength / 2 + rand.nextInt(branchLength / 4));
				}
			}
			if (dir == 2) {
				world.setBlock(x - i, y, z, bark);
				world.setBlock(x - i, y - 1, z, wood);
				world.setBlock(x - i, y - 1, z + 1, bark);
				world.setBlock(x - i, y - 1, z - 1, bark);
				world.setBlock(x - i, y - 2, z, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x - i, y - 3, z, 8);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x - i, y - 1, z + 1, 3, 4);
						createSmallBranch(world, rand, x - i, y - 1, z - 1, 4, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x - i, y, z, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 3) {
				world.setBlock(x, y, z + i, bark);
				world.setBlock(x, y - 1, z + i, wood);
				world.setBlock(x + 1, y - 1, z + i, bark);
				world.setBlock(x - 1, y - 1, z + i, bark);
				world.setBlock(x, y - 2, z + i, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x, y - 3, z + i, 4);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + 1, y - 1, z + i, 1, 4);
						createSmallBranch(world, rand, x - 1, y - 1, z + i, 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x, y, z + i, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 4) {
				world.setBlock(x, y, z - i, bark);
				world.setBlock(x, y - 1, z - i, wood);
				world.setBlock(x + 1, y - 1, z - i, bark);
				world.setBlock(x - 1, y - 1, z - i, bark);
				world.setBlock(x, y - 2, z - i, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x, y - 3, z - i, 1);
					if (i == branchLength/2) {
						createSmallBranch(world, rand, x + 1, y - 1, z - i, 1, 4);
						createSmallBranch(world, rand,x - 1, y - 1, z - i, 2, 4);
					}
					if (i == branchLength)
						createMainCanopy(world, rand, x, y, z - i, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 5) {
				world.setBlock(x + i - 1, y, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 1, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 2, z + i - 1, bark);
				world.setBlock(x + i - 2, y - 1, z + i - 1, bark);
				world.setBlock(x + i - 1, y - 1, z + i - 2, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 3, z + i - 1, 2);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i - 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 6) {
				world.setBlock(x - i + 1, y, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 1, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 2, z - i + 1, bark);
				world.setBlock(x - i + 2, y - 1, z - i + 1, bark);
				world.setBlock(x - i + 1, y - 1, z - i + 2, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 3, z - i + 1, 8);
					if (i == branchLength)
						createMainCanopy(world, rand, x - i + 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 7) {
				world.setBlock(x - i + 1, y, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 1, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 2, z + i - 1, bark);
				world.setBlock(x - i + 2, y - 1, z + i - 1, bark);
				world.setBlock(x - i + 1, y - 1, z + i - 2, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x - i + 1, y - 3, z + i - 1, 4);
					if (i == branchLength)
						createMainCanopy(world, rand, x - i + 1, y, z + i - 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
			if (dir == 8) {
				world.setBlock(x + i - 1, y, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 1, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 2, z - i + 1, bark);
				world.setBlock(x + i - 2, y - 1, z - i + 1, bark);
				world.setBlock(x + i - 1, y - 1, z - i + 2, bark);
				if (!root) {
					if (i <= branchLength)
						addVines(world, rand, x + i - 1, y - 3, z - i + 1, 1);
					if (i == branchLength)
						createMainCanopy(world, rand, x + i - 1, y, z - i + 1, branchLength / 2 + rand.nextInt(branchLength / 2));
				}
			}
		}
	}

	public void addVines(World world, Random rand, int x, int y, int z, int meta) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int yy = y; yy > y - length; --yy)
				if (world.getBlock(x, yy, z) == Blocks.air)
					world.setBlock(x, yy, z, ivy, meta, 2);
				else
					break;
		}
	}

	public void addHangers(World world, Random rand, int x, int y, int z) {
		if (rand.nextInt(4) == 0) {
			int length = rand.nextInt(10) + 10;
			for (int yy = y; yy > y - length; --yy)
				if (world.getBlock(x, yy, z) == Blocks.air)
					world.setBlock(x, yy, z, hanger);
				else
					break;
		}
	}

}
