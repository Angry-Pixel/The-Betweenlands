package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorSwamplands extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorSwamplands(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("deadTrunk");
		this.generate(2.25F, DecorationHelper::generateDeadTrunk);
		this.endProfilerSection();

		this.startProfilerSection("rottenLogs");
		this.generate(2, DecorationHelper::generateRottenLogs);
		this.endProfilerSection();

		this.startProfilerSection("tarPoolSurface");
		this.generate(8, DecorationHelper::generateTarPoolSurface);
		this.endProfilerSection();

		this.startProfilerSection("giantTree");
		this.generate(0.75F, DecorationHelper::generateGiantTree);
		this.endProfilerSection();

		this.startProfilerSection("smallHollowLog");
		this.generate(4, DecorationHelper::generateSmallHollowLog);
		this.endProfilerSection();

		this.startProfilerSection("weedwoodTree");
		this.generate(80, DecorationHelper::generateWeedwoodTree);
		this.endProfilerSection();
		
		this.startProfilerSection("nibbletwigTree");
		this.generate(100, DecorationHelper::generateNibbletwigTree);
		this.endProfilerSection();

		this.startProfilerSection("weedwoodBush");
		this.generate(35, DecorationHelper::generateWeedwoodBush);
		this.endProfilerSection();

		this.startProfilerSection("rubberTree");
		this.generate(4, DecorationHelper::generateRubberTree);
		this.endProfilerSection();

		this.startProfilerSection("sapTree");
		this.generate(8, DecorationHelper::generateSapTree);
		this.endProfilerSection();

		this.startProfilerSection("venusFlyTrapCluster");
		this.generate(3, DecorationHelper::generateVenusFlyTrapCluster);
		this.endProfilerSection();

		this.startProfilerSection("pitcherPlant");
		this.generate(4, DecorationHelper::generatePitcherPlant);
		this.endProfilerSection();
		
		this.startProfilerSection("swampReedCluster");
		this.generate(90, DecorationHelper::generateSwampReedCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampPlantCluster");
		this.generate(26, DecorationHelper::generateSwampPlantCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampTallgrassCluster");
		this.generate(150, DecorationHelper::generateSwampTallgrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampDoubleTallgrassCluster");
		this.generate(120, DecorationHelper::generateSwampDoubleTallgrass);
		this.endProfilerSection();

		this.startProfilerSection("flatHeadMushroomCluster");
		this.generate(12, DecorationHelper::generateFlatHeadMushroomCluster);
		this.endProfilerSection();

		this.startProfilerSection("blackHatMushroomCluster");
		this.generate(12, DecorationHelper::generateBlackHatMushroomCluster);
		this.endProfilerSection();

		this.startProfilerSection("volarpad");
		this.generate(18, DecorationHelper::generateVolarpad);
		this.endProfilerSection();

		this.startProfilerSection("cattailCluster");
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.endProfilerSection();

		this.startProfilerSection("tallCattail");
		this.generate(20, DecorationHelper::generateTallCattail);
		this.endProfilerSection();

		this.startProfilerSection("nettlesCluster");
		this.generate(6, DecorationHelper::generateNettlesCluster);
		this.endProfilerSection();

		this.startProfilerSection("mossCluster");
		this.generate(40, DecorationHelper::generateMossCluster);
		this.endProfilerSection();

		this.startProfilerSection("fallenLeaves");
		this.generate(DecorationHelper::generateFallenLeaves);
		this.endProfilerSection();

        this.startProfilerSection("pebbleClusterLand");
        this.generate(4, DecorationHelper::generatePebbleClusterLand);
        this.endProfilerSection();

        this.startProfilerSection("pebbleClusterWater");
        this.generate(10, DecorationHelper::generatePebbleClusterWater);
        this.endProfilerSection();
	}
}
