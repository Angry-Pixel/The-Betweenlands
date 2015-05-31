package thebetweenlands.world.feature.plants;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenMushrooms extends WorldGenerator {
	Block mush;
	int size;

	public WorldGenMushrooms(Block mush, int size) {
		this.mush = mush;
		this.size = size;
	}

	public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int l = 0; l < size; ++l)
        {
            int i = x + rand.nextInt(4) - rand.nextInt(4);
            int j = y + rand.nextInt(3) - rand.nextInt(3);
            int k = z + rand.nextInt(4) - rand.nextInt(4);

            if (world.isAirBlock(i, j, k) && mush.canBlockStay(world, i, j, k))
        		setBlockAndNotifyAdequately(world, i, j, k, mush, 0);
        }

		return true;
	}
}