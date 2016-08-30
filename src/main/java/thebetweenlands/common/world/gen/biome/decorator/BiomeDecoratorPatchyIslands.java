package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		this.generate(50, DecorationHelper::generateWeedwoodTree);
		//this.generate(50, DecorationHelper::generateSapTree);
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.generate(40, DecorationHelper::generateTallCattail);
		//this.generate(10, DecorationHelper::generateBigBulbCappedMushroom);
		this.generate(50, DecorationHelper::generateSwampTallgrassCluster);
		this.generate(100, DecorationHelper::generateSwampTallgrassCluster);
		//this.generate(5, DecorationHelper::generateIdolHead);
		this.generate(20, DecorationHelper::generateSwampReedCluster);
		this.generate(0.4F, DecorationHelper::generateMireCoralCluster);
		//this.generate(10, DecorationHelper::generateMossCluster);
		this.generate(10, DecorationHelper::generateMilkweedCluster);
		this.generate(10, DecorationHelper::generateShootsCluster);
		this.generate(10, DecorationHelper::generateCardinalFlower);
		this.generate(10, DecorationHelper::generateBlueEyedGrassCluster);
		this.generate(10, DecorationHelper::generateBonesetCluster);
		this.generate(14, DecorationHelper::generateMarshMarigold);
		this.generate(14, DecorationHelper::generateGoldenClub);
		this.generate(14, DecorationHelper::generateBogBean);
	}
}
