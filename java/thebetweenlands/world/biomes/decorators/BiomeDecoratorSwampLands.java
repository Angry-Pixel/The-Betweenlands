package thebetweenlands.world.biomes.decorators;

import net.minecraft.block.Block;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorSwampLands extends BiomeDecoratorBaseBetweenlands {
	public BiomeDecoratorSwampLands() {
		this.setPostChunkGenPasses(2);
	}

	@Override
	public void postChunkGen(int pass) {
		if(pass == 0) {
			DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

			helper.generateTarPool(60);

			helper.populateCave();
			helper.generateGiantWeedwoodTree(30);

			helper.generateDeadTree(10);

			helper.generateRottenLog(2);

			helper.generateWeedwoodTree(80);

			helper.generateWeedwoodBush(45);
			helper.generateRubberTree(70);
			helper.generateSapTree(70);
			helper.generateRoots(10.0D, 2);
			helper.generateReeds(600);
			helper.generateSwampPlant(75);
			helper.generateVenusFlyTrap(10);
			helper.generatePitcherPlant(20);
			helper.generateFlatHeadMushrooms(15);
			helper.generateBlackHatMushrooms(15);
			helper.generateVolarpad(2);
			helper.generateCattail(5);
			helper.generateNettles(8);
			helper.generateMossPatch(20);
		} else {
			for(int xo = -16; xo < 32; xo++) {
				for(int zo = -16; zo < 32; zo++) {
					int px = this.x + xo;
					int pz = this.z + zo;
					int py = this.world.getHeightValue(px, pz) - 1;
					if(this.world.getBlock(px, py, pz) == BLBlockRegistry.rubberTreeLeaves
							|| this.world.getBlock(px, py, pz) == BLBlockRegistry.weedwoodLeaves
							|| this.world.getBlock(px, py, pz) == BLBlockRegistry.sapTreeLeaves) {
						int yo = 0;
						for(int i = 0; i < 128; i++) {
							yo++;
							if(py-yo <= 0) {
								break;
							}
							Block cBlock = this.world.getBlock(px, py-yo, pz);
							if(cBlock == BLBlockRegistry.deadGrass || cBlock == BLBlockRegistry.swampGrass ||
									cBlock == BLBlockRegistry.swampDirt || cBlock == BLBlockRegistry.mud ||
									cBlock == BLBlockRegistry.weedwoodLog) {
								if(this.world.isAirBlock(px, py-yo+1, pz) && this.world.rand.nextInt(3) == 0) {
									this.world.setBlock(px, py-yo+1, pz, BLBlockRegistry.fallenLeaves);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
}
