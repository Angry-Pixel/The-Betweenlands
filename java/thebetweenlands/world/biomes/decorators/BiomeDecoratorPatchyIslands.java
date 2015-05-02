package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postChunkGen(int pass) {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateGiantWeedwoodTree(40);
	
		helper.generateWeedwoodTree(40);
		helper.generateSapTree(30);
		helper.generateCattail(10);
		helper.generateTallCattail(25);
		helper.generateSwampGrass(50);
		helper.generateSwampTallGrass(100);
		helper.generateReeds(20);
		helper.generateMireCoral(3.0D);
		helper.generateMossPatch(10);
		helper.generateMilkweed(10);
		helper.generateShoots(10);
		helper.generateCardinalFlower(10);
	}
}
