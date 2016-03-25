package thebetweenlands.world.biomes.decorators;

import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;

/**
 * Created by Bart on 9-10-2015.
 */
public class BiomeDecoratorSludgePlains extends BiomeDecoratorBaseBetweenlands {

    @Override
    public void postChunkGen(int pass) {
        DecorationHelper helper = new DecorationHelper(this.rand, this.world, this.x, this.world.getHeightValue(this.x, this.z), this.z, false);
        helper.populateCave();
        helper.generateTarPool(100);
        helper.generateSmallHollowLog(5);
        helper.generateSmallRuins(2);
        helper.generateSmalWeedwoodTree(4);
		helper.generateSludgecreep(50);
		helper.generateDeadWeedwoodBush(10);
        helper.generateDungeon(1);
        helper.generateRoots(10.0D, 2);
        helper.generateStagnantWaterPool(5);
        helper.generateUnderGroundStructures(100);
    }
}
