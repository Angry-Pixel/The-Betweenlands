package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorRaisedIsles extends BiomeDecoratorCoarseIslands {
	public BiomeDecoratorRaisedIsles(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("chiromawMatriarchNest");
		this.generate(3.0F, DecorationHelper::generateChiromawMatriarchNest);
		this.endProfilerSection();
	}
}
