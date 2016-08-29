package thebetweenlands.common.world.biome;

import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorSwamplands;
import thebetweenlands.common.world.gen.biome.feature.AlgaeFeature;
import thebetweenlands.common.world.gen.biome.feature.FlatLandFeature;
import thebetweenlands.common.world.gen.biome.feature.PatchFeature;
import thebetweenlands.common.world.gen.biome.feature.SiltBeachFeature;

public class BiomeSwamplands extends BiomeBetweenlands {

	public BiomeSwamplands() {
		super(new BiomeProperties("swamplands").setBaseHeight(110.0F).setHeightVariation(5F).setWaterColor(0x184220));
		//this.setWeight(25);
		this.getBiomeGenerator().setFillerBlockState(BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState())
		.setTopBlockState(BlockRegistry.DEAD_GRASS.getDefaultState())
		.addFeature(new FlatLandFeature(WorldProviderBetweenlands.LAYER_HEIGHT))
		.addFeature(new PatchFeature(0.03125D * 5.75D, 0.03125D * 5.75D, BlockRegistry.SWAMP_GRASS.getDefaultState()))
		.addFeature(new PatchFeature(0.74D, 0.74D, BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState()))
		.addFeature(new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.getDefaultState(), 1.0D / 1.35D, 1.72D))
		.addFeature(new AlgaeFeature())
		.addFeature(new SiltBeachFeature(0.98F))
		.setDecorator(new BiomeDecoratorSwamplands());
	}

}
