package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.world.biome.Biome;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBetweenlands {
	public BiomeDecoratorPatchyIslands(Biome biome) {
		super(biome);
	}

	@Override
	public void decorate() {
		super.decorate();

		this.startProfilerSection("hearthgroveTree");
		this.generate(5, DecorationHelper::generateHearthgroveTree);
		this.endProfilerSection();
		
		this.startProfilerSection("weedwoodTree");
		this.generate(50, DecorationHelper::generateWeedwoodTree);
		this.endProfilerSection();
		
		this.startProfilerSection("sapTree");
		this.generate(10, DecorationHelper::generateSapTree);
		this.endProfilerSection();

		this.startProfilerSection("sunkenIdolHead");
		this.generate(0.3F, DecorationHelper::generateSunkenIdolHead);
		this.endProfilerSection();

		this.startProfilerSection("cattailCluster");
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.endProfilerSection();

		this.startProfilerSection("tallCattail");
		this.generate(50, DecorationHelper::generateTallCattail);
		this.endProfilerSection();

		this.startProfilerSection("bigBulbCappedMushroom");
		this.generate(8, DecorationHelper::generateBigBulbCappedMushroom);
		this.endProfilerSection();

		this.startProfilerSection("swampTallgrassCluster");
		this.generate(90, DecorationHelper::generateSwampTallgrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("swampReedCluster");
		this.generate(20, DecorationHelper::generateSwampReedCluster);
		this.endProfilerSection();

		this.startProfilerSection("mireCoralCluster");
		this.generate(0.4F, DecorationHelper::generateMireCoralCluster);
		this.endProfilerSection();

		this.startProfilerSection("mossCluster");
		this.generate(25, DecorationHelper::generateMossCluster);
		this.endProfilerSection();

		this.startProfilerSection("milkweedCluster");
		this.generate(1.5F, DecorationHelper::generateMilkweedCluster);
		this.endProfilerSection();

		this.startProfilerSection("shootsCluster");
		this.generate(1.5F, DecorationHelper::generateShootsCluster);
		this.endProfilerSection();

		this.startProfilerSection("cardinalFlower");
		this.generate(1.5F, DecorationHelper::generateCardinalFlower);
		this.endProfilerSection();

		this.startProfilerSection("blueEyedGrassCluster");
		this.generate(1.5F, DecorationHelper::generateBlueEyedGrassCluster);
		this.endProfilerSection();

		this.startProfilerSection("bonesetCluster");
		this.generate(1.5F, DecorationHelper::generateBonesetCluster);
		this.endProfilerSection();

		this.startProfilerSection("marshMarigold");
		this.generate(6, DecorationHelper::generateMarshMarigold);
		this.endProfilerSection();

		this.startProfilerSection("goldenClub");
		this.generate(6, DecorationHelper::generateGoldenClub);
		this.endProfilerSection();

		this.startProfilerSection("bogBean");
		this.generate(6, DecorationHelper::generateBogBean);
		this.endProfilerSection();

        this.startProfilerSection("pebbleClusterLand");
        this.generate(4, DecorationHelper::generatePebbleClusterLand);
        this.endProfilerSection();

        this.startProfilerSection("pebbleClusterWater");
        this.generate(10, DecorationHelper::generatePebbleClusterWater);
        this.endProfilerSection();
	}
}
