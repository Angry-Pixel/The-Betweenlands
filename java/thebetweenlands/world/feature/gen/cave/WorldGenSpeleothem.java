package thebetweenlands.world.feature.gen.cave;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

public class WorldGenSpeleothem extends WorldGenerator {
	private Block stone = BLBlockRegistry.betweenstone;

	private Block speleothem = BLBlockRegistry.stalactite;

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (world.getBlock(x, y, z) == Blocks.air && world.getBlock(x, y + 1, z) == stone) {
			int height = 1;
			while (world.getBlock(x, y - height, z) == Blocks.air) {
				height++;
			}
			if (height < 3) {
				return false;
			}
			boolean hasStalagmite = world.getBlock(x, y - height, z) == stone;
			int length = random.nextInt(height < 11 ? height / 2 : 5) + 1;
			boolean isColumn = length == height / 2;
			for (int dy = 0; dy < length; dy++) {
				world.setBlock(x, y - dy, z, speleothem);
				if (hasStalagmite && (isColumn || dy < length - 1)) {
					world.setBlock(x, y - height + dy + 1, z, speleothem);
				}
			}
			return true;
		}
		return false;
	}
}
