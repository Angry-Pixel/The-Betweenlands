package thebetweenlands.common.world.biome;

import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorDeepWaters;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CragSpiresFeature;
import thebetweenlands.common.world.gen.biome.feature.DeepWatersFeature;

public class BiomeDeepWaters extends BiomeBetweenlands {

	public BiomeDeepWaters() {
		super(new BiomeProperties("deep_waters").setBaseHeight(108.0F).setHeightVariation(5.0F).setWaterColor(0x1b3944));
		//this.setWeight(15);
		this.getBiomeGenerator().setDecorator(new BiomeDecoratorDeepWaters())
		.addFeature(new DeepWatersFeature())
		.addFeature(new AlgaeFeature())
		.addFeature(new CragSpiresFeature());
	}

}
