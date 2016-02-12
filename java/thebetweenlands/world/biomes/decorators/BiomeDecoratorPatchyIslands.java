package thebetweenlands.world.biomes.decorators;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postChunkGen(int pass) {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.populateCave();
		helper.generateWeedwoodTree(40);
		helper.generateSapTree(30);
		helper.generateCattail(10);
		helper.generateTallCattail(25);
		helper.generateHugeMushroom(10);
		helper.generateSwampGrass(50);
		helper.generateSwampTallGrass(100);
		helper.generateHeads(20);
		helper.generateReeds(20);
		helper.generateMireCoral(3.0D);
		helper.generateMossPatch(10);
		helper.generateMilkweed(10);
		helper.generateShoots(10);
		helper.generateCardinalFlower(10);
		helper.generateBlueEyedGrass(10);
		helper.generateBoneset(10);
		helper.generateOneDeep(14, 6, 10, BLBlockRegistry.marshMarigold);
		helper.generateOneDeep(14, 6, 10, BLBlockRegistry.bogBean);
		helper.generateOneDeep(14, 6, 10, BLBlockRegistry.goldenClub);
		helper.generateStagnantWaterPool(5);
	}
}
