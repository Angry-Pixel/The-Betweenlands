package thebetweenlands.world.feature.trees;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockBLHanger;
import thebetweenlands.blocks.tree.BlockBLLog;

import java.util.Random;

/**
 * Created by Bart on 3-6-2015.
 */
public class WorldGenPurpleRainTree extends WorldGenerator {
    private BlockBLLog log = (BlockBLLog) BLBlockRegistry.purpleRainLog;
    private BlockBLHanger hanger = (BlockBLHanger) BLBlockRegistry.purpleHanger;
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return false;
    }
}
