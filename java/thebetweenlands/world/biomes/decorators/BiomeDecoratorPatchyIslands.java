package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateGiantWeedwoodTree(40);
	
		helper.generateWeedwoodTree(40);
		helper.generateSapTree(30);
		helper.generateWeedwoodBush(20);
		helper.generateCattail(50);
		helper.generateSwampGrass(30);
		helper.generateNettles(1);
		helper.generateCattail(2);
		helper.generateWeepingBlue(2);
		helper.generateReeds(20);
		helper.generateMireCoral(3.0D);
		helper.generateFlowerPatch(1.0D);
	}
}
