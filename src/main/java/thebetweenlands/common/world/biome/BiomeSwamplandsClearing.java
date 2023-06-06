package thebetweenlands.common.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecoratorSwamplandsClearing;

public class BiomeSwamplandsClearing extends BiomeBetweenlands {
	public BiomeSwamplandsClearing() {
		super(new ResourceLocation(ModInfo.ID, "swamplands_clearing"), 
				new BiomeProperties("Swamplands Clearing")
				.setBaseHeight(WorldProviderBetweenlands.LAYER_HEIGHT + 2)
				.setHeightVariation(1F)
				.setWaterColor(0x184220)
				.setTemperature(0.8F)
				.setRainfall(0.9F));

		this.setWeight(0);
		this.getBiomeGenerator().setFillerBlockState(BlockRegistry.SWAMP_DIRT.getDefaultState())
		.setTopBlockState(BlockRegistry.SWAMP_GRASS.getDefaultState()).setDecoratorFactory(generateDecoratorFactory(BiomeDecoratorSwamplandsClearing.class));
		this.setFoliageColors(-1, 0x52AF5A);
	}

	@Override
	public void addTypes() {
		BiomeDictionary.addTypes(this, Type.SWAMP, Type.DENSE, Type.FOREST, Type.WET, Type.WATER);
	}
}
