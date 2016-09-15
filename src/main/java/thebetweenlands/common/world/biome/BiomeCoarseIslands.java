package thebetweenlands.common.world.biome;

import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorCoarseIslands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;

public class BiomeCoarseIslands extends BiomeBetweenlands {

	public BiomeCoarseIslands() {
		super(new BiomeProperties("coarse_islands").setBaseHeight(115.0F).setHeightVariation(4.0F).setWaterColor(0x1b3944).setTemperature(0.8F).setRainfall(0.9F));
		//this.setWeight(15);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorCoarseIslands())
		.addFeature(new CoarseIslandsFeature())
		.addFeature(new AlgaeFeature());
	}

}
