package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorMarsh extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorMarsh(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		//TODO: Implement missing generators
		//this.generate(10, DecorationHelper::generateWightFortress);

		this.startProfilerSection("giantTree");
		this.generate(0.25F, DecorationHelper::generateGiantTree);
		this.endProfilerSection();

		this.startProfilerSection("weedwoodTree");
		this.generate(1, DecorationHelper::generateWeedwoodTree);
		this.endProfilerSection();

		this.startProfilerSection("spawnerStructure");
		this.generate(0.2F, DecorationHelper::generateSpawnerStructure);
		this.endProfilerSection();

		this.startProfilerSection("smallRuinsCluster");
		this.generate(2.5F, DecorationHelper::generateSmallRuinsCluster);
		this.endProfilerSection();

		this.startProfilerSection("phragmites");
		this.generate(300, DecorationHelper::generatePhragmites);
		this.endProfilerSection();

		this.startProfilerSection("smallHollowLog");
		this.generate(5, DecorationHelper::generateSmallHollowLog);
		this.endProfilerSection();

		this.startProfilerSection("swampTallgrassCluster");
		this.generate(30, DecorationHelper::generateSwampTallgrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("sundew");
		this.generate(5, DecorationHelper::generateSundew);
		this.endProfilerSection();

		this.startProfilerSection("nettlesCluster");
		this.generate(2, DecorationHelper::generateNettlesCluster);
		this.endProfilerSection();

		this.startProfilerSection("weepingBlue");
		this.generate(3, DecorationHelper::generateWeepingBlue);
		this.endProfilerSection();

		this.startProfilerSection("wisp");
		this.generate(4, DecorationHelper::generateWisp);
		this.endProfilerSection();

		this.startProfilerSection("arrowArumCluster");
		this.generate(2, DecorationHelper::generateArrowArumCluster);
		this.endProfilerSection();

		this.startProfilerSection("pickerelWeedCluster");
		this.generate(2, DecorationHelper::generatePickerelWeedCluster);
		this.endProfilerSection();

		this.startProfilerSection("marshHibiscusCluster");
		this.generate(2, DecorationHelper::generateMarshHibiscusCluster);
		this.endProfilerSection();

		this.startProfilerSection("marshMallowCluster");
		this.generate(2, DecorationHelper::generateMarshMallowCluster);
		this.endProfilerSection();

		this.startProfilerSection("buttonBushCluster");
		this.generate(2, DecorationHelper::generateButtonBushCluster);
		this.endProfilerSection();

		this.startProfilerSection("softRushCluster");
		this.generate(90, DecorationHelper::generateSoftRushCluster);
		this.endProfilerSection();

		this.startProfilerSection("broomsedge");
		this.generate(40, DecorationHelper::generateBroomsedge);
		this.endProfilerSection();

		this.startProfilerSection("bottleBrushGrassCluster");
		this.generate(5, DecorationHelper::generateBottleBrushGrassCluster);
		this.endProfilerSection();
	}
}
