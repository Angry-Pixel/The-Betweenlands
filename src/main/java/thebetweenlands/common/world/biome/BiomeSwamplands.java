package thebetweenlands.common.world.biome;

import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomeSwamplands extends BiomeBetweenlands {

	public BiomeSwamplands() {
		super(new BiomeProperties("swamplands").setBaseHeight(128.0F).setHeightVariation(3.0F).setWaterColor(0x184220));
	}

}
