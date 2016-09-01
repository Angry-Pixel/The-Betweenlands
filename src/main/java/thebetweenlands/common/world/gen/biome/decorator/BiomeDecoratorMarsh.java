package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;

public class BiomeDecoratorMarsh extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, ChunkGeneratorBetweenlands generator, Random rand, int x, int z) {
		super.decorate(world, generator, rand, x, z);

		//TODO: Implement missing generators
		//this.generate(70, DecorationHelper::generateGiantWeedwoodTree);
		//this.generate(10, DecorationHelper::generateWightFortress);
		this.generate(1, DecorationHelper::generateWeedwoodTree);
		this.generate(0.5F, DecorationHelper::generateSpawnerStructure);
		//this.generate(2, DecorationHelper::generateSmallRuins);
		this.generate(300, DecorationHelper::generatePhragmites);
		this.generate(5, DecorationHelper::generateSmallHollowLog);
		this.generate(30, DecorationHelper::generateSwampTallgrassCluster);
		this.generate(5, DecorationHelper::generateSundew);
		this.generate(2, DecorationHelper::generateNettlesCluster);
		this.generate(3, DecorationHelper::generateWeepingBlue);
		//this.generate(4, DecorationHelper::generateWisp);
		this.generate(2, DecorationHelper::generateArrowArumCluster);
		this.generate(2, DecorationHelper::generatePickerelWeedCluster);
		this.generate(2, DecorationHelper::generateMarshHibiscusCluster);
		this.generate(2, DecorationHelper::generateMarshMallowCluster);
		this.generate(2, DecorationHelper::generateButtonBushCluster);
		this.generate(90, DecorationHelper::generateSoftRushCluster);
		this.generate(40, DecorationHelper::generateBroomsedge);
		this.generate(5, DecorationHelper::generateBottleBrushGrassCluster);
	}
}
