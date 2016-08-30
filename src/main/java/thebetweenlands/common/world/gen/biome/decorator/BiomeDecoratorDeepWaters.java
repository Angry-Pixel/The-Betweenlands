package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorDeepWaters extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		//this.generate(1, DecorationHelper::generateWisp);
		this.generate(2, DecorationHelper::generateBladderwortCluster);
		this.generate(5, DecorationHelper::generateMireCoralCluster);
		this.generate(5, DecorationHelper::generateDeepWaterCoralCluster);
		//this.generate(10, DecorationHelper::generateLichenCluster);
		this.generate(5, DecorationHelper::generateSwampKelpCluster);
		this.generate(25, DecorationHelper::generateWaterWeedsCluster);
	}
}
