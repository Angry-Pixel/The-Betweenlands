package thebetweenlands.world.biomes.decorators;

import net.minecraft.block.Block;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

public class BiomeDecoratorSwampLands extends BiomeDecoratorBaseBetweenlands {
	@Override
	public void postTerrainGen() {
		DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);

		helper.generateGiantWeedwoodTree(60);
		
		helper.generateWeedwoodTree(100);
		
		helper.generateWeedwoodBush(120);
		helper.generateRubberTree(40);
		helper.generateSapTree(40);
		helper.generateRoots(7.0D, 2);
		helper.generateReeds(80);
		helper.generateSwampGrass(60);
		helper.generateCattail(2);
		helper.generateNettles(1);
	}
	
	@Override
	public void postTerrainGen2() {
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
						if(cBlock == BLBlockRegistry.deadGrass || cBlock == BLBlockRegistry.swampGrass) {
							if(this.world.isAirBlock(px, py-yo+1, pz)) {
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
