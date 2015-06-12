package thebetweenlands.world.feature.trees;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.BlockBLHanger;
import thebetweenlands.blocks.terrain.BlockBLFallenLeaves;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;

import java.util.Random;

/**
 * Created by Bart on 3-6-2015.
 */
public class WorldGenPurpleRainTree extends WorldGenerator {
    private BlockBLLog log = (BlockBLLog) BLBlockRegistry.purpleRainLog;
    private BlockBLHanger hanger = (BlockBLHanger) BLBlockRegistry.purpleHanger;
    private BlockBLHanger hangerFlowered = (BlockBLHanger) BLBlockRegistry.purpleHangerFlowered;
    private BlockBLLeaves leavesLight = (BlockBLLeaves) BLBlockRegistry.purpleRainLeavesLight;
    private BlockBLLeaves leavesDark = (BlockBLLeaves) BLBlockRegistry.purpleRainLeavesDark;
    private BlockBLFallenLeaves fallenLeaves = (BlockBLFallenLeaves) BLBlockRegistry.purpleFallenLeaves;


    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int radius = random.nextInt(2) + 2;
        int height = random.nextInt(5) + 10;

        for (int yy = y; yy <= y + height; ++yy) {
            tramp(world, x, yy, y, z, height, radius, random);
        }
        return true;
    }

    private void tramp(World world, int x, int y, int acctualY, int z, int height, int radius, Random random) {
        if (y <= acctualY + height - 8) {
            for (int distance = 1; distance <= 2; distance++) {
                world.setBlock(x, y, z, log);
                world.setBlock(x - distance, y, z, log);
                world.setBlock(x + distance, y, z, log);
                world.setBlock(x, y, z - distance, log);
                world.setBlock(x, y, z + distance, log);
                if (distance == 1) {
                    world.setBlock(x - 1, y, z - 1, log);
                    world.setBlock(x + 1, y, z + 1, log);
                    world.setBlock(x - 1, y, z + 1, log);
                    world.setBlock(x + 1, y, z - 1, log);
                } else {
                    world.setBlock(x - distance, y, z - 1, log);
                    world.setBlock(x + distance, y, z + 1, log);
                    world.setBlock(x - distance, y, z + 1, log);
                    world.setBlock(x + distance, y, z - 1, log);
                    world.setBlock(x - 1, y, z - distance, log);
                    world.setBlock(x + 1, y, z + distance, log);
                    world.setBlock(x - 1, y, z + distance, log);
                    world.setBlock(x + 1, y, z - distance, log);
                }
            }
        } else {
            world.setBlock(x, y, z, log);
            world.setBlock(x - 1, y, z, log);
            world.setBlock(x + 1, y, z, log);
            world.setBlock(x, y, z - 1, log);
            world.setBlock(x, y, z + 1, log);
            world.setBlock(x - 1, y, z - 1, log);
            world.setBlock(x + 1, y, z + 1, log);
            world.setBlock(x - 1, y, z + 1, log);
            world.setBlock(x + 1, y, z - 1, log);
        }
        if (y == acctualY + height) {
            addBranch(world, random, x + 2, y, z, 1, 4 + random.nextInt(2));
            addBranch(world, random, x - 2, y, z, 2, 4 + random.nextInt(2));
            addBranch(world, random, x, y, z + 2, 3, 4 + random.nextInt(2));
            addBranch(world, random, x, y, z - 2, 4, 4 + random.nextInt(2));
        }
    }

    private void addHangers(World world, Random rand, int x, int y, int z) {
        if (rand.nextInt(4) == 0) {
            if (rand.nextInt(4) == 0) {
                int length = rand.nextInt(4) + 5;
                for (int yy = y; yy > y - length; --yy)
                    if (world.getBlock(x, yy, z) == Blocks.air)
                        world.setBlock(x, yy, z, hangerFlowered);
                    else
                        break;
            } else {
                int length = rand.nextInt(6) + 2;
                for (int yy = y; yy > y - length; --yy)
                    if (world.getBlock(x, yy, z) == Blocks.air)
                        world.setBlock(x, yy, z, hanger);
                    else
                        break;
            }
        }
    }

    private void addBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
        for (int i = 0; i <= branchLength; i++) {
            if (i >= 1 && rand.nextInt(20) <= 13) {
                y++;
            }
            if (dir == 1) {
                if (rand.nextInt(3) == 0)
                    z++;
                world.setBlock(x + i, y, z, log, 5, 2);
                if (rand.nextInt(5) == 0 || i == branchLength) {
                    world.setBlock(x + i, y + 1, z, log, 5, 2);
                    y++;
                }
                if (i >= branchLength - 2 && rand.nextInt(3) == 0)
                    addSubBranch(world, rand, x + i, y, z, 1 + rand.nextInt(3));
                else if( i == branchLength)
                    addLeaves(world, rand, x + i, y, z);
            }

            if (dir == 2) {
                if (rand.nextInt(3) == 0)
                    z--;
                world.setBlock(x - i, y, z, log, 5, 2);
                if (rand.nextInt(5) == 0 || i == branchLength) {
                    world.setBlock(x - i, y + 1, z, log, 5, 2);
                    y++;
                }
                if (i >= branchLength - 2 && rand.nextInt(3) == 0)
                    addSubBranch(world, rand, x - i, y, z, 1 + rand.nextInt(3));
                else if( i == branchLength)
                    addLeaves(world, rand, x - i, y, z );
            }

            if (dir == 3) {
                if (rand.nextInt(3) == 0)
                    x--;
                world.setBlock(x, y, z + i, log, 9, 2);
                if (rand.nextInt(5) == 0 || i == branchLength) {
                    world.setBlock(x, y + 1, z + i, log, 9, 2);
                    y++;
                }
                if (i >= branchLength - 2 && rand.nextInt(3) == 0)
                    addSubBranch(world, rand, x, y, z + i, 1 + rand.nextInt(3));
                else if( i == branchLength)
                    addLeaves(world, rand, x, y, z + i);
            }

            if (dir == 4) {
                if (rand.nextInt(3) == 0)
                    x++;
                world.setBlock(x, y, z - i, log, 9, 2);
                if (rand.nextInt(5) == 0 || i == branchLength) {
                    world.setBlock(x, y + 1, z - i, log, 9, 2);
                    y++;
                }
                if (i >= branchLength - 2 && rand.nextInt(3) == 0)
                    addSubBranch(world, rand, x, y, z - i, 1 + rand.nextInt(3));
                else if( i == branchLength)
                    addLeaves(world, rand, x, y, z - i);
            }
        }
    }

    private void addSubBranch(World world, Random rand, int x, int y, int z, int branchLength) {
        for (int i = 0; i <= branchLength; i++) {
            y++;
            world.setBlock(x, y, z, log);
            if (i == branchLength && rand.nextInt(5) == 0) {
                world.setBlock(x, y, z - 1, log);
                world.setBlock(x, y + 1, z - 1, log);
                addLeaves(world, rand, x, y + 1, z - 1);
            } else if (i == branchLength && rand.nextInt(5) == 0) {
                world.setBlock(x, y, z + 1, log);
                world.setBlock(x, y + 1, z + 1, log);
                addLeaves(world, rand, x, y + 1, z + 1);
            } else if (i == branchLength && rand.nextInt(5) == 0) {
                world.setBlock(x - 1, y, z, log);
                world.setBlock(x - 1, y + 1, z, log);
                addLeaves(world, rand, x - 1, y + 1, z);
            } else if (i == branchLength && rand.nextInt(5) == 0) {
                world.setBlock(x + 1, y, z, log);
                world.setBlock(x + 1, y + 1, z, log);
                addLeaves(world, rand, x + 1, y + 1, z);
            } else if (i == branchLength){
                addLeaves(world, rand, x, y, z);
            }
        }
    }

    private void addLeaves(World world, Random rand, int x, int y, int z) {

        world.setBlock(x + 1, y, z, leavesLight);
        world.setBlock(x - 1, y, z, leavesLight);
        world.setBlock(x + 1, y, z - 1, leavesLight);
        world.setBlock(x - 1, y, z + 1, leavesLight);
        world.setBlock(x + 1, y, z + 1, leavesLight);
        world.setBlock(x - 1, y, z - 1, leavesLight);
        world.setBlock(x, y, z - 1, leavesLight);
        world.setBlock(x, y, z + 1, leavesLight);


        world.setBlock(x + 2, y, z, leavesLight);
        world.setBlock(x - 2, y, z, leavesLight);
        world.setBlock(x + 1, y, z - 2, leavesLight);
        world.setBlock(x - 1, y, z + 2, leavesLight);
        world.setBlock(x + 1, y, z + 2, leavesLight);
        world.setBlock(x - 1, y, z - 2, leavesLight);
        world.setBlock(x + 2, y, z - 1, leavesLight);
        world.setBlock(x - 2, y, z + 1, leavesLight);
        world.setBlock(x + 2, y, z + 1, leavesLight);
        world.setBlock(x - 2, y, z - 1, leavesLight);
        world.setBlock(x, y, z - 2, leavesLight);
        world.setBlock(x, y, z + 2, leavesLight);

        world.setBlock(x, y + 1, z, leavesDark);
        world.setBlock(x + 1, y + 1, z, leavesDark);
        world.setBlock(x - 1, y + 1, z, leavesDark);
        world.setBlock(x + 1, y + 1, z - 1, leavesDark);
        world.setBlock(x - 1, y + 1, z + 1, leavesDark);
        world.setBlock(x + 1, y + 1, z + 1, leavesDark);
        world.setBlock(x - 1, y + 1, z - 1, leavesDark);
        world.setBlock(x, y + 1, z - 1, leavesDark);
        world.setBlock(x, y + 1, z + 1, leavesDark);


    }
}
