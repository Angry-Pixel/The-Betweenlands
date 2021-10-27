package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorSwamplandsClearing extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorSwamplandsClearing(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("swamplandsClearingSpiritTree");
		this.generate(1, DecorationHelper::generateSwamplandsClearingSpiritTree);
		this.endProfilerSection();

		this.startProfilerSection("venusFlyTrapCluster");
		this.generate(3, DecorationHelper::generateVenusFlyTrapCluster);
		this.endProfilerSection();

		this.startProfilerSection("pitcherPlant");
		this.generate(4, DecorationHelper::generatePitcherPlant);
		this.endProfilerSection();

		this.startProfilerSection("swampPlantCluster");
		this.generate(10, DecorationHelper::generateSwampPlantCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampTallgrassCluster");
		this.generate(15, DecorationHelper::generateSwampTallgrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("flatHeadMushroomCluster");
		this.generate(5, DecorationHelper::generateFlatHeadMushroomCluster);
		this.endProfilerSection();

		this.startProfilerSection("blackHatMushroomCluster");
		this.generate(5, DecorationHelper::generateBlackHatMushroomCluster);
		this.endProfilerSection();

		this.startProfilerSection("cattailCluster");
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.endProfilerSection();

		this.startProfilerSection("mossCluster");
		this.generate(5, DecorationHelper::generateMossCluster);
		this.endProfilerSection();
		
        this.startProfilerSection("pebbleClusterLand");
        this.generate(6, DecorationHelper::generatePebbleClusterLand);
        this.endProfilerSection();

        this.startProfilerSection("pebbleClusterWater");
        this.generate(8, DecorationHelper::generatePebbleClusterWater);
        this.endProfilerSection();
	}
}
