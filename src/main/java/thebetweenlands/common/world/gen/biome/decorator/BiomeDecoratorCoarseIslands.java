package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorCoarseIslands extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		//this.generate(30, DecorationHelper::generateGiantWeedwoodTree);
		this.generate(25, DecorationHelper::generateWeedwoodTree);
		//this.generate(30, DecorationHelper::generateSapTree);
		//this.generate(40, DecorationHelper::generateWeedwoodBush);

		this.generate(10, DecorationHelper::generateCattailCluster);
		this.generate(40, DecorationHelper::generateSwampTallgrassCluster);
		this.generate(1, DecorationHelper::generateNettlesCluster);
		this.generate(18, DecorationHelper::generateVolarpad);
		this.generate(15, DecorationHelper::generateSwampReedCluster);
		this.generate(1, DecorationHelper::generateMireCoralCluster);
		this.generate(1, DecorationHelper::generateBladderwortCluster);
		this.generate(1, DecorationHelper::generateWaterRootsCluster);
		this.generate(5, DecorationHelper::generateCopperIrisCluster);
		this.generate(5, DecorationHelper::generateBlueIrisCluster);
		this.generate(4, DecorationHelper::generateSwampKelpCluster);

		//this.generate(5, DecorationHelper::generateCragrockTower);
	}
}
