package thebetweenlands.world.feature.trees;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockBLLog;

public class WorldGenRottenLogs extends WorldGenerator {

	private int length = -1;
	private int baseRadius = -1;
	private byte direction;
	private BlockBLLog log;

	public WorldGenRottenLogs(int length, int baseRadius, byte direction) {
		this.length = length;
		this.baseRadius = baseRadius;
		this.direction = direction;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		this.log = (BlockBLLog) BLBlockRegistry.rottenWeedwoodBark;

		// Trunk N/S
		if (direction == 1) {
			for (int xx = x - baseRadius; baseRadius + x >= xx; xx++)
				for (int zz = z - length; length + z - 1 >= zz; zz++)
					for (int yy = y + 1; yy <= y + baseRadius * 2; yy++)
						if (!world.isAirBlock(xx, yy, zz))
							return false;

			for (int zz = z - length; length + z - 1 >= zz; zz++)
				for (int i = baseRadius * -1; i <= baseRadius; ++i)
					for (int j = baseRadius * -1; j <= baseRadius; ++j) {
						double dSq = i * i + j * j;
						if (Math.round(Math.sqrt(dSq)) == baseRadius) {
							world.setBlock(x + i, y + j + baseRadius, zz, log, 11, 2);
							if (rand.nextInt(12) == 0)
								world.setBlock(x + i, y + j + baseRadius, zz, Blocks.air);
							if (zz == z - length && rand.nextInt(2) == 0 || zz == z + length - 1 && rand.nextInt(2) == 0)
								world.setBlock(x + i, y + j + baseRadius, zz, Blocks.air);
						} else
							world.setBlock(x + i, y + j + baseRadius, zz, Blocks.air);
					}

		} else {

			// Trunk E/W
			for (int xx = x - length; length + x - 1 >= xx; xx++)
				for (int zz = z - baseRadius; baseRadius + z >= zz; zz++)
					for (int yy = y + 1; yy <= y + baseRadius * 2; yy++)
						if (!world.isAirBlock(xx, yy, zz))
							return false;

			for (int xx = x - length; length + x - 1 >= xx; xx++)
				for (int i = baseRadius * -1; i <= baseRadius; ++i)
					for (int j = baseRadius * -1; j <= baseRadius; ++j) {
						double dSq = i * i + j * j;
						if (Math.round(Math.sqrt(dSq)) == baseRadius) {
							world.setBlock(xx, y + j + baseRadius, z + i, log, 7, 2);
							if (rand.nextInt(12) == 0)
								world.setBlock(xx, y + j + baseRadius, z + i, Blocks.air);
							if (xx == x - length && rand.nextInt(2) == 0 || xx == x + length - 1 && rand.nextInt(2) == 0)
								world.setBlock(xx, y + j + baseRadius, z + i, Blocks.air);
						} else
							world.setBlock(xx, y + j + baseRadius, z + i, Blocks.air);
					}
		}
		return true;
	}
}
