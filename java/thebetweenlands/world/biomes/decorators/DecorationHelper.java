package thebetweenlands.world.biomes.decorators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.blocks.plants.roots.BlockRoot;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.trees.WorldGenGiantTree;
import thebetweenlands.world.feature.trees.WorldGenRubberTree;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodBush;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class DecorationHelper {
	private final static WorldGenGiantTree GEN_GIANT_TREE = new WorldGenGiantTree();
	private final static WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedWoodTree();
	private final static WorldGenerator GEN_SAP_TREE = new WorldGenSapTree();
	private final static WorldGenerator GEN_RUBBER_TREE = new WorldGenRubberTree();
	private final static WorldGenerator GEN_WEEDWOOD_BUSH = new WorldGenWeedWoodBush();
	private final static WorldGenTallGrass GEN_NETTLE = new WorldGenTallGrass(BLBlockRegistry.nettle, 1);
	private final static WorldGenTallGrass GEN_CATTAIL = new WorldGenTallGrass(BLBlockRegistry.catTail, 1);
	private final static WorldGenTallGrass GEN_SWAMP_PLANT = new WorldGenTallGrass(BLBlockRegistry.swampPlant, 1);

	private final Random rand;
	private final int x, y, z;
	private final World world;

	public DecorationHelper(Random rand, World world, int x, int y, int z) {
		this.rand = rand;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	private final int offsetXZ() {
		return rand.nextInt(16) + 8;
	}

	private final boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(this.world.getBlock(x, y - 1, z)) && this.world.isAirBlock(x, y, z);
	}

	public void generateSwampGrass(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SWAMP_PLANT.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateNettles(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_NETTLE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateCattail(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_CATTAIL.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateWeedwoodBush(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_WEEDWOOD_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateWisp(double probability) {
		if(probability >= 1.0D) {
			for(int i = 0; i < (int)probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.WATER, x, y, z)) {
					BLBlockRegistry.wisp.generateBlock(this.world, x, y, z);
				}
			}
		} else {
			if(this.rand.nextInt((int)(1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.WATER, x, y, z)) {
					BLBlockRegistry.wisp.generateBlock(this.world, x, y, z);
				}
			}
		}
	}

	public void generateGiantWeedwoodTree(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x + 1, y, z + 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, y + 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z + 1))
				GEN_GIANT_TREE.generateTree(this.world, this.rand, x, y, z);
		}
	}

	public void generateWeedwoodTree(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x + 1, y, z + 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, y + 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z + 1))
				GEN_WEEDWOOD_TREE.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateSapTree(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x + 1, y, z + 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, y + 1, y, z - 1)
					&& this.checkSurface(SurfaceType.SWAMP_GRASS, x - 1, y, z + 1))
				GEN_SAP_TREE.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateRubberTree(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
				GEN_RUBBER_TREE.generate(this.world, this.rand, x, y, z);
		}
	}
	
	public void generateWeepingBlue(int attempts) {
		for(int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y, z, BLBlockRegistry.weepingBlue, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.weepingBlue, 8, 2);
			}
		}
	}

	public void generateMireCoral(double probability) {
		if(probability >= 1.0D) {
			for(int i = 0; i < (int)probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if(cBlock == BLBlockRegistry.swampWater) {
					if(BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		} else {
			if(this.rand.nextInt((int)(1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if(cBlock == BLBlockRegistry.swampWater) {
					if(BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		}
	}

	public void generateFlowerPatch(double probability) {
		if(probability >= 1.0D) {
			for(int i = 0; i < (int)probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y+1, z);
				if(cBlock == BLBlockRegistry.swampWater) {
					if(BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		} else {
			if(this.rand.nextInt((int)(1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if(cBlock == BLBlockRegistry.swampWater) {
					if(BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		}
	}
	
	public void generateRoots(double probability) {
		if(probability >= 1.0D) {
			for(int i = 0; i < (int)probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y+1, z);
				Block blockAbove2 = world.getBlock(x, y+2, z);
				if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if(world.rand.nextInt(3) == 0) {
						BlockRoot.generateRootPatch(world, x, y+1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y+1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		} else {
			if(this.rand.nextInt((int)(1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y+1, z);
				Block blockAbove2 = world.getBlock(x, y+2, z);
				if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if(world.rand.nextInt(3) == 0) {
						BlockRoot.generateRootPatch(world, x, y+1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y+1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		}
	}
	
	public void generateReeds(double probability) {
		if(probability >= 1.0D) {
			for(int i = 0; i < (int)probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y+1, z);
				Block blockAbove2 = world.getBlock(x, y+2, z);
				if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y+1, z, 40, 10);
				} else if(block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if(BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y+1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y+1, z, 40, 10);
					}
				}
			}
		} else {
			if(this.rand.nextInt((int)(1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y+1, z);
				Block blockAbove2 = world.getBlock(x, y+2, z);
				if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y+1, z, 40, 10);
				} else if(block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if(BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y+1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y+1, z, 40, 10);
					}
				}
			}
		}
	}
}
