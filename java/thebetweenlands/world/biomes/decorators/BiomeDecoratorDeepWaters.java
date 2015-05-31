package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorDeepWaters extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postChunkGen(int pass) {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateWisp(1);
		helper.generateFlowerPatch(10.0D);
		helper.generateMireCoral(15.0D);
		helper.generateDeepWaterCoral(15.0D);
		helper.generateLichenPatch(10);
		helper.generateWaterWeeds(6, 6, 30);
	}
}
