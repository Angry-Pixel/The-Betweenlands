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
import thebetweenlands.world.feature.plants.WorldGenMossPatch;
import thebetweenlands.world.feature.plants.WorldGenMushrooms;
import thebetweenlands.world.feature.plants.WorldGenWeedWoodBush;
import thebetweenlands.world.feature.trees.WorldGenGiantTree;
import thebetweenlands.world.feature.trees.WorldGenRubberTree;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class DecorationHelper {
	private final static WorldGenGiantTree GEN_GIANT_TREE = new WorldGenGiantTree();
	private final static WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedWoodTree();
	private final static WorldGenerator GEN_SAP_TREE = new WorldGenSapTree();
	private final static WorldGenerator GEN_RUBBER_TREE = new WorldGenRubberTree();
	private final static WorldGenerator GEN_WEEDWOOD_BUSH = new WorldGenWeedWoodBush();
	private final static WorldGenerator GEN_BLACK_HAT_MUSHROOMS = new WorldGenMushrooms(BLBlockRegistry.blackHatMushroom, 15);
	private final static WorldGenerator GEN_FLAT_HEAD_MUSHROOMS = new WorldGenMushrooms(BLBlockRegistry.flatHeadMushroom, 15);
	private final static WorldGenTallGrass GEN_NETTLE = new WorldGenTallGrass(BLBlockRegistry.nettle, 1);
	private final static WorldGenTallGrass GEN_CATTAIL = new WorldGenTallGrass(BLBlockRegistry.catTail, 1);
	private final static WorldGenTallGrass GEN_SWAMP_TALL_GRASS = new WorldGenTallGrass(BLBlockRegistry.swampTallGrass, 1);
	private final static WorldGenTallGrass GEN_SWAMP_DOUBLE_TALL_GRASS = new WorldGenTallGrass(BLBlockRegistry.doubleSwampTallgrass, 1);
	private final static WorldGenTallGrass GEN_SWAMP_PLANT = new WorldGenTallGrass(BLBlockRegistry.swampPlant, 1);
	private final static WorldGenTallGrass GEN_VENUS_FLY_TRAP = new WorldGenTallGrass(BLBlockRegistry.venusFlyTrap, 0);
	private final static WorldGenTallGrass GEN_VOLARPAD = new WorldGenTallGrass(BLBlockRegistry.volarpad, 0);
	private final static WorldGenTallGrass GEN_SUNDEW = new WorldGenTallGrass(BLBlockRegistry.sundew, 0);
	private final static WorldGenTallGrass GEN_PITCHER_PLANT = new WorldGenTallGrass(BLBlockRegistry.pitcherPlant, 0);
	private final static WorldGenerator GEN_MOSS_PATCH = new WorldGenMossPatch(0);
	private final static WorldGenerator GEN_LICHEN_PATCH = new WorldGenMossPatch(1);
	private final static WorldGenTallGrass GEN_ARROW_ARUM = new WorldGenTallGrass(BLBlockRegistry.arrowArum, 1);
	private final static WorldGenTallGrass GEN_BUTTON_BUSH = new WorldGenTallGrass(BLBlockRegistry.buttonBush, 1);
	private final static WorldGenTallGrass GEN_MARSH_HIBISCUS = new WorldGenTallGrass(BLBlockRegistry.marshHibiscus, 1);
	private final static WorldGenTallGrass GEN_PICKEREL_WEED = new WorldGenTallGrass(BLBlockRegistry.pickerelWeed, 1);
	private final static WorldGenTallGrass GEN_PHRAGMITES = new WorldGenTallGrass(BLBlockRegistry.phragmites, 1);
	private final static WorldGenTallGrass GEN_WEEPING_BLUE = new WorldGenTallGrass(BLBlockRegistry.weepingBlue, 1);
	private final static WorldGenTallGrass GEN_SOFT_RUSH = new WorldGenTallGrass(BLBlockRegistry.softRush, 1);
	private final static WorldGenTallGrass GEN_MARSH_MALLOW = new WorldGenTallGrass(BLBlockRegistry.marshMallow, 1);

	private final Random rand;
	private final int x, y, z;
	private final World world;
	private final boolean centerOffset;

	public DecorationHelper(Random rand, World world, int x, int y, int z, boolean centerOffset) {
		this.rand = rand;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.centerOffset = centerOffset;
	}

	private final int offsetXZ() {
		return rand.nextInt(16) + (this.centerOffset ? 8 : 0);
	}

	private final boolean checkSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(this.world.getBlock(x, y - 1, z)) && this.world.isAirBlock(x, y, z);
	}

	private final boolean checkAboveSurface(SurfaceType surfaceType, int x, int y, int z) {
		return surfaceType.matchBlock(this.world.getBlock(x, y, z));
	}
	
	public void generatePhragmites(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.phragmites, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.phragmites, 8, 2);
			}
		}
	}
	
	public void generateTallCattail(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.tallCattail, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.tallCattail, 8, 2);
			}
		}
	}
	
	public void generateArrowArum(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_ARROW_ARUM.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateMarshMallow(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_MARSH_MALLOW.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateButtonBush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_BUTTON_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateSoftRush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SOFT_RUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateMarshHibiscus(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_MARSH_HIBISCUS.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generatePickerelWeed(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_PICKEREL_WEED.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSwampGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SWAMP_TALL_GRASS.generate(this.world, this.rand, x, y, z);
			}
		}
	}
	
	public void generateSwampTallGrass(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.doubleSwampTallgrass, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.doubleSwampTallgrass, 8, 2);
			}
		}
	}
	

	public void generateSwampPlant(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_SWAMP_PLANT.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateNettles(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_NETTLE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateCattail(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_CATTAIL.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateVenusFlyTrap(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_VENUS_FLY_TRAP.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateVolarpad(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_VOLARPAD.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateSundew(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
			{
				world.setBlock(x, y, z, BLBlockRegistry.sundew, 0, 2);
				world.setBlock(x, y + 1, z, BLBlockRegistry.sundew, 8, 2);
			}
		}
	}

	public void generatePitcherPlant(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
				world.setBlock(x, y, z, BLBlockRegistry.pitcherPlant, 0, 2);
		}
	}

	public void generateWeedwoodBush(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z)) {
				GEN_WEEDWOOD_BUSH.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateBlackHatMushrooms(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_BLACK_HAT_MUSHROOMS.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateFlatHeadMushrooms(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			GEN_FLAT_HEAD_MUSHROOMS.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateWisp(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				if (this.checkSurface(SurfaceType.WATER, x, y, z)) {
					BLBlockRegistry.wisp.generateBlock(this.world, x, y, z);
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
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
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
				GEN_GIANT_TREE.generateTree(this.world, this.rand, x, y, z);
		}
	}

	public void generateWeedwoodTree(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) || this.checkSurface(SurfaceType.DIRT, x, y, z) && this.checkAboveSurface(SurfaceType.WATER, x, y + 1, z))
				GEN_WEEDWOOD_TREE.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateSapTree(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
				GEN_SAP_TREE.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateRubberTree(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
				GEN_RUBBER_TREE.generate(this.world, this.rand, x, y, z);
		}
	}

	public void generateWeepingBlue(int attempts) {
		for (int i = 0; i < attempts; i++) {
			int x = this.x + this.offsetXZ();
			int y = this.y - 8 + this.rand.nextInt(16);
			int z = this.z + this.offsetXZ();
			if (checkSurface(SurfaceType.SWAMP_GRASS, x, y, z) && world.isAirBlock(x, y, z) && world.isAirBlock(x, y, z)) {
				if (this.checkSurface(SurfaceType.SWAMP_GRASS, x, y, z))
					GEN_WEEPING_BLUE.generate(this.world, this.rand, x, y, z);
			}
		}
	}

	public void generateMireCoral(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.mireCoral.canPlaceBlockAt(this.world, x, y, z)) {
						this.world.setBlock(x, y, z, BLBlockRegistry.mireCoral, 0, 3);
					}
				}
			}
		}
	}

	public void generateFlowerPatch(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y + 1, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = this.world.getBlock(x, y, z);
				if (cBlock == BLBlockRegistry.swampWater) {
					if (BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(this.world, x, y, z)) {
						BlockWaterFlower.generateFlowerPatch(this.world, x, y, z, 35, 10);
					}
				}
			}
		}
	}

	public void generateWaterRoots(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if (world.rand.nextInt(3) == 0) {
						BlockRoot.generateWaterRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					if (world.rand.nextInt(3) == 0) {
						BlockRoot.generateWaterRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		}
	}

	public void generateRoots(double probability, int patchProbability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				boolean hasSpace = blockAbove == Blocks.air && blockAbove2 == Blocks.air;
				if ((cBlock == BLBlockRegistry.swampGrass || cBlock == BLBlockRegistry.deadGrass) && hasSpace) {
					if (world.rand.nextInt(patchProbability) == 0) {
						BlockRoot.generateRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block cBlock = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				boolean hasSpace = blockAbove == Blocks.air && blockAbove2 == Blocks.air;
				if ((cBlock == BLBlockRegistry.swampGrass || cBlock == BLBlockRegistry.deadGrass) && hasSpace) {
					if (world.rand.nextInt(patchProbability) == 0) {
						BlockRoot.generateRootPatch(world, x, y + 1, z, 60, 15);
					} else {
						BlockRoot.generateRoot(world, x, y + 1, z, WorldProviderBetweenlands.LAYER_HEIGHT - y + world.rand.nextInt(8) + 1);
					}
				}
			}
		}
	}

	public void generateReeds(double probability) {
		if (probability >= 1.0D) {
			for (int i = 0; i < (int) probability; i++) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
				} else if (block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if (BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y + 1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
					}
				}
			}
		} else {
			if (this.rand.nextInt((int) (1.0D / probability)) == 0) {
				int x = this.x + this.offsetXZ();
				int y = this.y - 8 + this.rand.nextInt(16);
				int z = this.z + this.offsetXZ();
				Block block = world.getBlock(x, y, z);
				Block blockAbove = world.getBlock(x, y + 1, z);
				Block blockAbove2 = world.getBlock(x, y + 2, z);
				if (block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
				} else if (block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if (BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y + 1, z)) {
						BlockSwampReed.generateReedPatch(world, x, y + 1, z, 40, 10);
					}
				}
			}
		}
	}

	public void generateMossPatch(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 30 + rand.nextInt(80);
			int zz = z + offsetXZ();

			if (world.isAirBlock(xx, yy, zz))
				GEN_MOSS_PATCH.generate(world, rand, xx, yy, zz);
		}
	}

	public void generateLichenPatch(int attempt) {
		for (int i = 0; i < attempt; i++) {
			int xx = x + offsetXZ();
			int yy = 30 + rand.nextInt(80);
			int zz = z + offsetXZ();

			if (world.isAirBlock(xx, yy, zz))
				GEN_LICHEN_PATCH.generate(world, rand, xx, yy, zz);
		}
	}
}
