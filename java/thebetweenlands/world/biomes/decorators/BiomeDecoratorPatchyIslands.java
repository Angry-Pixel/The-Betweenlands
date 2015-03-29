package thebetweenlands.world.biomes.decorators;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodBush;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBaseBetweenlands {
	
	private final WorldGenerator genTreeWeedwood = new WorldGenWeedWoodTree();
	private final WorldGenerator genTreeSap = new WorldGenSapTree();
	private final WorldGenerator genBushWeedWood = new WorldGenWeedWoodBush();
	private final WorldGenTallGrass genNettle = new WorldGenTallGrass(BLBlockRegistry.nettle, 1);
	private final WorldGenTallGrass genSwampPlant = new WorldGenTallGrass(BLBlockRegistry.swampPlant, 1);
	private final WorldGenTallGrass genCattail = new WorldGenTallGrass(BLBlockRegistry.catTail, 1);
	
	@Override
	public void decorate() {
		for (attempt = 0; attempt < 120; attempt++) {
			xx = x + rand.nextInt(5) + 12;
			yy = 80 + rand.nextInt(15);
			zz = z + rand.nextInt(5) + 12;

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz) && checkSurface(SurfaceType.SWAMP_GRASS, xx - 2, yy, zz - 2) && checkSurface(SurfaceType.SWAMP_GRASS, xx + 2, yy, zz + 2) && checkSurface(SurfaceType.SWAMP_GRASS, xx + 2, yy, zz - 2) && checkSurface(SurfaceType.SWAMP_GRASS, xx - 2, yy, zz + 2))
			genTreeWeedwood.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 20; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genBushWeedWood.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 50; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz) && checkSurface(SurfaceType.SWAMP_GRASS, xx - 1, yy, zz - 1) && checkSurface(SurfaceType.SWAMP_GRASS, xx + 1, yy, zz + 1) && checkSurface(SurfaceType.SWAMP_GRASS, xx + 1, yy, zz - 1) && checkSurface(SurfaceType.SWAMP_GRASS, xx - 1, yy, zz + 1))
			genTreeSap.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 60; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz) && world.isAirBlock(xx, yy + 1, zz)) {
				world.setBlock(xx, yy, zz, Blocks.double_plant, 3, 2);
				world.setBlock(xx, yy + 1, zz, Blocks.double_plant, 10, 2);
			}
		}

		for (attempt = 0; attempt < 30; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genSwampPlant.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 2; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(20);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genNettle.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 3; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genCattail.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 5; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz) && world.isAirBlock(xx, yy, zz) && world.isAirBlock(xx, yy + 1, zz)) {
				world.setBlock(xx, yy, zz, BLBlockRegistry.weepingBlue, 0, 2);
				world.setBlock(xx, yy + 1, zz, BLBlockRegistry.weepingBlue, 8, 2);
			}
		}
		
		for (attempt = 0; attempt < 50; attempt++) {
			xx = x + offsetXZ();
			yy = 80 - 10 + rand.nextInt(20);
			zz = z + offsetXZ();

			Block block = world.getBlock(xx, yy, zz);
			Block blockAbove = world.getBlock(xx, yy+1, zz);
			Block blockAbove2 = world.getBlock(xx, yy+2, zz);
			if(block == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
				BlockSwampReed.generateReedPatch(world, xx, yy+1, zz, 40, 10);
			} else if(block.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
				if(BLBlockRegistry.swampReed.canPlaceBlockAt(world, xx, yy+1, zz)) {
					BlockSwampReed.generateReedPatch(world, xx, yy+1, zz, 40, 10);
				}
			}
		}
		
		if(this.rand.nextInt(10) == 0) {
			xx = this.x + this.rand.nextInt(16);
			zz = this.z + this.rand.nextInt(16);
			yy = this.world.getHeightValue(xx, zz);
			Block cBlock = world.getBlock(xx, yy, zz);
			if(cBlock == BLBlockRegistry.swampWater) {
				if(BLBlockRegistry.mireCoral.canPlaceBlockAt(world, xx, yy, zz)) {
					world.setBlock(xx, yy, zz, BLBlockRegistry.mireCoral, 0, 3);
				}
			}
		}

		if(this.rand.nextInt(20) == 0) {
			xx = this.x + this.rand.nextInt(16);
			zz = this.z + this.rand.nextInt(16);
			yy = this.world.getHeightValue(xx, zz);
			Block cBlock = world.getBlock(xx, yy+1, zz);
			if(cBlock == BLBlockRegistry.swampWater) {
				if(BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(world, xx, yy, zz)) {
					BlockWaterFlower.generateFlowerPatch(world, xx, yy, zz, 20, 6);
				}
			}
		}
	}
}
