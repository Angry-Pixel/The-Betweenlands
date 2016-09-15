package thebetweenlands.common.world.biome;

import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorDeepWaters;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.feature.DeepWatersFeature;

public class BiomeDeepWaters extends BiomeBetweenlands {

	public BiomeDeepWaters() {
		super(new BiomeProperties("deep_waters").setBaseHeight(108.0F).setHeightVariation(5.0F).setWaterColor(0x1b3944).setTemperature(0.8F).setRainfall(0.9F));
		//this.setWeight(15);
		this.setFogColor(10, 30, 12);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorDeepWaters())
		.addFeature(new DeepWatersFeature())
		.addFeature(new AlgaeFeature())
		.addFeature(new CragSpiresFeature());
	}

}
