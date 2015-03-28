package thebetweenlands.world.biomes.decorators;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class BiomeDecoratorDeepWaters extends BiomeDecoratorBaseBetweenlands {
	
	private final WorldGenerator genTreeWeedwood = new WorldGenWeedWoodTree();
	private final WorldGenerator genTreeSap = new WorldGenSapTree();
	private final WorldGenTallGrass genNettle = new WorldGenTallGrass(BLBlockRegistry.nettle, 1);
	private final WorldGenTallGrass genSwampPlant = new WorldGenTallGrass(BLBlockRegistry.swampPlant, 1);
	private final WorldGenTallGrass genCattail = new WorldGenTallGrass(BLBlockRegistry.catTail, 1);
	
	@Override
	public void decorate() {
		for (attempt = 0; attempt < 4; attempt++) {
			xx = x + offsetXZ();
			yy = 80 + rand.nextInt(45);
			zz = z + offsetXZ();

			if (checkSurface(SurfaceType.WATER, xx, yy, zz))
				BLBlockRegistry.wisp.generateBlock(world, xx, yy, zz);
		}
		
		if(this.rand.nextInt(2) == 0) {
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

		if(this.rand.nextInt(4) == 0) {
			xx = this.x + this.rand.nextInt(16);
			zz = this.z + this.rand.nextInt(16);
			yy = this.world.getHeightValue(xx, zz);
			Block cBlock = world.getBlock(xx, yy+1, zz);
			if(cBlock == BLBlockRegistry.swampWater) {
				if(BLBlockRegistry.waterFlowerStalk.canPlaceBlockAt(world, xx, yy, zz)) {
					BlockWaterFlower.generateFlowerPatch(world, xx, yy, zz, 35, 10);
				}
			}
		}
	}
}
