package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorDeepWaters extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorDeepWaters(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("wisp");
		this.generate(1, DecorationHelper::generateWisp);
		this.endProfilerSection();

		this.startProfilerSection("underwaterRuins");
		this.generate(0.3F, DecorationHelper::generateUnderwaterRuins);
		this.endProfilerSection();

		this.startProfilerSection("bladderwortCluster");
		this.generate(2, DecorationHelper::generateBladderwortCluster);
		this.endProfilerSection();

		this.startProfilerSection("mireCoralCluster");
		this.generate(5, DecorationHelper::generateMireCoralCluster);
		this.endProfilerSection();

		this.startProfilerSection("deepWaterCoralCluster");
		this.generate(5, DecorationHelper::generateDeepWaterCoralCluster);
		this.endProfilerSection();

		this.startProfilerSection("lichenCluster");
		this.generate(240, DecorationHelper::generateLichenCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampKelpCluster");
		this.generate(5, DecorationHelper::generateSwampKelpCluster);
		this.endProfilerSection();

		this.startProfilerSection("waterWeedsCluster");
		this.generate(25, DecorationHelper::generateWaterWeedsCluster);
		this.endProfilerSection();
		
		this.startProfilerSection("barnacleCluster");
		this.generate(500, DecorationHelper::generateBarnacleCluster);
		this.endProfilerSection();

        this.startProfilerSection("pebbleClusterWater");
        this.generate(50, DecorationHelper::generatePebbleClusterWater);
        this.endProfilerSection();
	}
}
