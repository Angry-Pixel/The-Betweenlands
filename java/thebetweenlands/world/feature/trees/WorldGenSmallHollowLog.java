package thebetweenlands.world.feature.trees;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

import java.util.Random;

/**
 * Created by Bart on 8-8-2015.
 */
public class WorldGenSmallHollowLog extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int len = rand.nextInt(3) + 3;
        int offsetX = rand.nextInt(2), offsetZ = 1 - offsetX;

        for (int a = 0; a < len; a++)
            if (!world.isAirBlock(x + offsetX * a, y, z + offsetZ * a) || world.isAirBlock(x + offsetX * a, y - 1, z + offsetZ * a))
                return false;
        for (int a = 0; a < len; a++)
            world.setBlock(x + offsetX * a, y, z + offsetZ * a, BLBlockRegistry.hollowLog, offsetX == 0 ? rand.nextInt(4) : rand.nextInt(4) + 4, 2);
        return true;
    }
}
