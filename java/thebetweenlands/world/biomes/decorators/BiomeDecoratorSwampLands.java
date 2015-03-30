package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorSwampLands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z);

		helper.generateGiantWeedwoodTree(60);
		helper.generateWeedwoodTree(80);
		helper.generateRubberTree(40);
		helper.generateSapTree(40);
		helper.generateWeedwoodBush(40);
		helper.generateReeds(80);
		helper.generateSwampGrass(80);
		helper.generateNettles(1);
	}
}
