package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorCoarseIslands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postChunkGen(int pass) {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.populateCave();
		helper.generateGiantWeedwoodTree(40);
		//helper.generatePurpleRainTree(40);
        helper.generateWeedwoodTree(25);
		helper.generateSapTree(30);
		helper.generateWeedwoodBush(40);
		helper.generateCattail(10);
		helper.generateSwampGrass(30);
		helper.generateNettles(1);
		helper.generateCattail(2);
		helper.generateVolarpad(5);
		helper.generateReeds(20);
		helper.generateMireCoral(3.0D);
		helper.generateFlowerPatch(1.0D);
		helper.generateWaterRoots(5.0D);
		helper.generateVolarpad(5);
		helper.generateCopperIris(5);
		helper.generateBlueIris(5);
		helper.generateWaterWeeds(6, 6, 30);
		helper.generateStagnantWaterPool(5);
	}
}
