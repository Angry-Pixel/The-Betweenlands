package thebetweenlands.common.world.biome;

import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorPatchyIslands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.SiltBeachFeature;

public class BiomePatchyIslands extends BiomeBetweenlands {

	public BiomePatchyIslands() {
		super(new BiomeProperties("patchy_islands").setBaseHeight(118.0F).setHeightVariation(4.0F).setWaterColor(0x184220));
		//this.setWeight(20);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorPatchyIslands())
		.addFeature(new SiltBeachFeature())
		.addFeature(new AlgaeFeature());
	}

}
