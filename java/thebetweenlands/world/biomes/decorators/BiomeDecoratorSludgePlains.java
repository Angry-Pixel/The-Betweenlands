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

        helper.generateReeds(6);
        helper.generateRottenLog(2);
        helper.generateTarPool(5);
        helper.generateWeedwoodTree(2);
        helper.generateTarPool(30);
    }
}
