package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorPatchyIslands extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		this.generate(50, DecorationHelper::generateWeedwoodTree);
		this.generate(10, DecorationHelper::generateSapTree);
		this.generate(0.15F, DecorationHelper::generateSunkenIdolHead);
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.generate(50, DecorationHelper::generateTallCattail);
		this.generate(8, DecorationHelper::generateBigBulbCappedMushroom);
		this.generate(90, DecorationHelper::generateSwampTallgrassCluster);
		this.generate(20, DecorationHelper::generateSwampReedCluster);
		this.generate(0.4F, DecorationHelper::generateMireCoralCluster);
		this.generate(25, DecorationHelper::generateMossCluster);
		this.generate(1.5F, DecorationHelper::generateMilkweedCluster);
		this.generate(1.5F, DecorationHelper::generateShootsCluster);
		this.generate(1.5F, DecorationHelper::generateCardinalFlower);
		this.generate(1.5F, DecorationHelper::generateBlueEyedGrassCluster);
		this.generate(1.5F, DecorationHelper::generateBonesetCluster);
		this.generate(6, DecorationHelper::generateMarshMarigold);
		this.generate(6, DecorationHelper::generateGoldenClub);
		this.generate(6, DecorationHelper::generateBogBean);
	}
}
