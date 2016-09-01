package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorSludgePlains extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		this.generate(60, DecorationHelper::generateTarPoolSurface);
		this.generate(0.6F, DecorationHelper::generateSpawnerStructure);
		this.generate(15, DecorationHelper::generateSmallHollowLog);
		//this.generate(4, DecorationHelper::generateDeadWeedwoodTree);
		//this.generate(5, DecorationHelper::generateMudStructures);
		this.generate(50, DecorationHelper::generateSludgecreepCluster);
		this.generate(10, DecorationHelper::generateDeadWeedwoodBushCluster);
		this.generate(0.8F, DecorationHelper::generateRootsCluster);
	}
}
