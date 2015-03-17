package thebetweenlands.world.biomes.decorators;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class BiomeDecoratorDeepWater extends BiomeDecoratorBaseBetweenlands {
	
	private final WorldGenerator genTreeWeedwood = new WorldGenWeedWoodTree();
	private final WorldGenerator genTreeSap = new WorldGenSapTree();
	private final WorldGenTallGrass genFerns = new WorldGenTallGrass(Blocks.tallgrass, 2);
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

		for (attempt = 0; attempt < 20; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genSwampPlant.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 10; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genNettle.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 10; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(15);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.SWAMP_GRASS, xx, yy, zz))
				genFerns.generate(world, rand, xx, yy, zz);
		}

		for (attempt = 0; attempt < 10; attempt++) {
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
		
		for (attempt = 0; attempt < 1; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(62);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.WATER, xx, yy, zz))
				BLBlockRegistry.wisp.generateBlock(world, xx, yy, zz);
		}
	}
}
