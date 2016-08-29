package thebetweenlands.common.world.biome;

import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorMarsh;
import thebetweenlands.common.world.gen.biome.feature.Marsh1Feature;
import thebetweenlands.common.world.gen.biome.feature.Marsh2Feature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;

public class BiomeMarsh extends BiomeBetweenlands {

	public BiomeMarsh(int type) {
		super(new BiomeProperties("marsh_" + type).setBaseHeight(119F).setHeightVariation(1.1F).setWaterColor(0x485E18));
		//this.setWeight(10);
		this.getBiomeGenerator()
		.addFeature(type == 0 ? new Marsh1Feature() : new Marsh2Feature())
		.addFeature(new PatchFeature(0.03125D * 3.5D, 0.03125D * 3.5D, BlockRegistry.PEAT.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 12.5D, 0.03125D * 12.5D, BlockRegistry.PEAT.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 5.5D, 0.03125D * 5.5D, BlockRegistry.MUD.getDefaultState()))
		.addFeature(new PatchFeature(0.03125D * 8.5D, 0.03125D * 8.5D, BlockRegistry.MUD.getDefaultState()))
		.setDecorator(new BiomeDecoratorMarsh());
	}

}
