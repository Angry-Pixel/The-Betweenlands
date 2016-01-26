package thebetweenlands.world.feature.structure;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;

import java.util.Random;

/**
 * Created by Bart on 26/01/2016.
 */
public class WorldGenSmallRuins extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return ark1(world, random, x, y, z);
    }

    public boolean ark1(World world, Random random, int x, int y, int z) {
        int height = 9 + random.nextInt(2);
        int width = 7 + random.nextInt(2);
        if (random.nextBoolean()) {
            for (int xx = x; xx < x + width; xx++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(xx, yy, z) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 2)
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x + 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x + 2, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                    world.setBlock(x + 2, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                    int xx;
                    for (xx = x + 3; xx <= x + 3 + width - 7; xx++) {
                        world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                    }
                    world.setBlock(xx, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 4, 3);
                    world.setBlock(xx, yy + 1, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
            for (int yy = y; yy < y + height - 2; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 4)
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x + width - 2, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 1, 3);
                }

                if (yy == y + height - 4) {
                    world.setBlock(x + width - 1, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 5, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x + width - 1, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x + width, yy, z, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        } else {
            for (int zz = z; zz < z + width; zz++)
                for (int yy = y; yy < y + height; yy++)
                    if (!(world.getBlock(x, yy, zz) == Blocks.air))
                        return false;
            for (int yy = y; yy < y + height; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 2)
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x, yy, z + 1, BLBlockRegistry.betweenstoneBrickStairs, 3, 3);
                    world.setBlock(x, yy, z, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x, yy, z + 2, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                    world.setBlock(x, yy + 1, z + 2, BLBlockRegistry.betweenstoneBrickSlab);
                    int zz;
                    for (zz = z + 3; zz <= z + 3 + width - 7; zz++) {
                        world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickSlab, 9, 3);
                        world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                    }
                    world.setBlock(x, yy, zz, BLBlockRegistry.betweenstoneBrickStairs, 6, 3);
                    world.setBlock(x, yy + 1, zz, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
            for (int yy = y; yy < y + height - 2; yy++) {
                if (yy <= y + height - 6)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneTiles);
                else if (yy <= y + height - 4)
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBricks);
                else {
                    world.setBlock(x, yy, z + width - 2, BLBlockRegistry.betweenstoneBrickStairs, 3, 3);
                }

                if (yy == y + height - 4) {
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickStairs, 7, 3);
                }
                if (yy == y + height - 3) {
                    world.setBlock(x, yy, z + width - 1, BLBlockRegistry.betweenstoneBrickSlab);
                    world.setBlock(x, yy, z + width, BLBlockRegistry.betweenstoneBrickSlab);
                }
            }
        }
        return true;
    }
}
