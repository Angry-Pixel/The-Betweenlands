package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorSwamplands extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		this.generate(60, DecorationHelper::generateTarPool);
		//this.generate(30, DecorationHelper::generateGiantWeedwoodTree);
		//this.generate(10, DecorationHelper::generateDeadTree);
		//this.generate(2, DecorationHelper::generateRottenLogStructure);
		this.generate(4, DecorationHelper::generateSmallHollowLog);
		this.generate(80, DecorationHelper::generateWeedwoodTree);
		//this.generate(45, DecorationHelper::generateWeedwoodBush);
		//this.generate(70, DecorationHelper::generateRubberTree);
		//this.generate(70, DecorationHelper::generateSapTree);
		this.generate(20, DecorationHelper::generateSwampReedCluster);
		this.generate(80, DecorationHelper::generateSwampPlantCluster);
		this.generate(6, DecorationHelper::generateVenusFlyTrap);
		this.generate(10, DecorationHelper::generatePitcherPlant);
		this.generate(2, DecorationHelper::generateFlatHeadMushroomCluster);
		this.generate(2, DecorationHelper::generateBlackHatMushroomCluster);
		this.generate(2, DecorationHelper::generateVolarpad);
		this.generate(5, DecorationHelper::generateCattailCluster);
		this.generate(20, DecorationHelper::generateTallCattail);
		this.generate(8, DecorationHelper::generateNettlesCluster);
		//this.generate(20, DecorationHelper::generateMossCluster);
	}
}
