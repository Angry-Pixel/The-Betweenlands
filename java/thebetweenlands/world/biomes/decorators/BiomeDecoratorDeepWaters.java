package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorDeepWaters extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateWisp(1);
		helper.generateFlowerPatch(10.0D);
		helper.generateMireCoral(15.0D);
		helper.generateMossPatch(10);
		helper.generateLichenPatch(10);
	}
}
