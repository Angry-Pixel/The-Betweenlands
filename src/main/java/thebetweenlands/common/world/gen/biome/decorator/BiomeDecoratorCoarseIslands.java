package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorCoarseIslands extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorCoarseIslands(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("giantTree");
		this.generate(0.1F, DecorationHelper::generateGiantTree);
		this.endProfilerSection();
		
		this.startProfilerSection("cragrockTower");
		this.generate(0.8F, DecorationHelper::generateCragrockTower);
		this.endProfilerSection();
		
		this.startProfilerSection("weedwoodTree");
		this.generate(25, DecorationHelper::generateWeedwoodTree);
		this.endProfilerSection();

		this.startProfilerSection("sapTree");
		this.generate(15, DecorationHelper::generateSapTree);
		this.endProfilerSection();

		this.startProfilerSection("weedwoodBush");
		this.generate(40, DecorationHelper::generateWeedwoodBush);
		this.endProfilerSection();

		this.startProfilerSection("cattailCluster");
		this.generate(10, DecorationHelper::generateCattailCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampTallgrassCluster");
		this.generate(40, DecorationHelper::generateSwampTallgrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("nettlesCluster");
		this.generate(1, DecorationHelper::generateNettlesCluster);
		this.endProfilerSection();

		this.startProfilerSection("volarpad");
		this.generate(18, DecorationHelper::generateVolarpad);
		this.endProfilerSection();

		this.startProfilerSection("swampReedCluster");
		this.generate(15, DecorationHelper::generateSwampReedCluster);
		this.endProfilerSection();

		this.startProfilerSection("mireCoralCluster");
		this.generate(1, DecorationHelper::generateMireCoralCluster);
		this.endProfilerSection();

		this.startProfilerSection("bladderwortCluster");
		this.generate(1, DecorationHelper::generateBladderwortCluster);
		this.endProfilerSection();

		this.startProfilerSection("waterRootsCluster");
		this.generate(1, DecorationHelper::generateWaterRootsCluster);
		this.endProfilerSection();

		this.startProfilerSection("copperIrisCluster");
		this.generate(5, DecorationHelper::generateCopperIrisCluster);
		this.endProfilerSection();

		this.startProfilerSection("blueIrisCluster");
		this.generate(5, DecorationHelper::generateBlueIrisCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampKelpCluster");
		this.generate(4, DecorationHelper::generateSwampKelpCluster);
		this.endProfilerSection();

        this.startProfilerSection("pebbleClusterLand");
        this.generate(4, DecorationHelper::generatePebbleClusterLand);
        this.endProfilerSection();

        this.startProfilerSection("pebbleClusterWater");
        this.generate(10, DecorationHelper::generatePebbleClusterWater);
        this.endProfilerSection();
	}
}
