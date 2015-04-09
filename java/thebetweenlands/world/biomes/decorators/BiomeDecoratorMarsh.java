package thebetweenlands.world.biomes.decorators;

import net.minecraft.init.Blocks;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;

public class BiomeDecoratorMarsh extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		for (int i = 0; i < 10; i++) {
			int px = this.x + offsetXZ();
			int py = 80 + rand.nextInt(14);
			int pz = this.z + offsetXZ();
			if (checkSurface(SurfaceType.PEAT, px, py, pz)) {
				world.setBlock(px, py, pz, Blocks.fire, 0, 3);
			}
		}

		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateGiantWeedwoodTree(1);
		
		helper.generateWeedwoodTree(2);
		helper.generateSwampGrass(30);
		helper.generateSundew(6);
		helper.generateNettles(2);
		helper.generateCattail(2);
		helper.generateWeepingBlue(6);
		helper.generateWisp(2);
	}
}
