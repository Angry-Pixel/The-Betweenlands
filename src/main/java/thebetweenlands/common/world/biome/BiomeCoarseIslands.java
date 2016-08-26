package thebetweenlands.common.world.biome;

import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;

public class BiomeCoarseIslands extends BiomeBetweenlands {

	public BiomeCoarseIslands() {
		super(new BiomeProperties("coarse_islands").setBaseHeight(115.0F).setHeightVariation(4.0F).setWaterColor(0x1b3944));
		//this.setWeight(15);
		this.getBiomeGenerator()
		.addFeature(new CoarseIslandsFeature())
		.addFeature(new AlgaeFeature());
	}

}
