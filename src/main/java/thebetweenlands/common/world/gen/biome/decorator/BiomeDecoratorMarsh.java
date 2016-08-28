package thebetweenlands.common.world.gen.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;

public class BiomeDecoratorMarsh extends BiomeDecoratorBetweenlands {
	@Override
	public void decorate(World world, Random rand, int x, int z) {
		super.decorate(world, rand, x, z);

		this.generate(100, DecorationHelper::generatePhragmites);
	}
}
