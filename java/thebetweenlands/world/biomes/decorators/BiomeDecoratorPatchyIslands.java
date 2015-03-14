package thebetweenlands.world.biomes.decorators;

import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBaseBetweenlands {
	
	private final WorldGenerator genTreeWeedwood = new WorldGenWeedWoodTree();
	private final WorldGenerator genTreeSap = new WorldGenSapTree();
	
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
	}
}
