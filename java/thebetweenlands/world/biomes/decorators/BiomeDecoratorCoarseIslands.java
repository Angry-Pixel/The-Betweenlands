package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorCoarseIslands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateGiantWeedwoodTree(50);

		helper.generateWeedwoodTree(25);
		helper.generateSapTree(30);
		helper.generateWeedwoodBush(40);
		helper.generateCattail(10);
		helper.generateSwampGrass(30);
		helper.generateNettles(1);
		helper.generateCattail(2);
		helper.generateVolarpad(10);
		helper.generateWeepingBlue(2);
		helper.generateReeds(20);
		helper.generateMireCoral(3.0D);
		helper.generateFlowerPatch(1.0D);
		helper.generateWaterRoots(5.0D);
		helper.generateVolarpad(10);
		helper.generateMossPatch(10);
		helper.generateLichenPatch(10);
	}
}
