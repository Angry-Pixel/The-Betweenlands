package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenRubberTree extends WorldGenHelper {
    public BlockState log;
    public BlockState leaves;

    private BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

    private void createBranch(WorldGenLevel world, Random rand, int x, int y, int z, int dir, int branchLength) {
        for (int i = 0; i <= branchLength; ++i) {

            if (i >= 1) {
                y++;
            }

            if (dir == 1) {
                world.setBlock(new BlockPos(x + i, y, z), log, 2, 2);
                world.setBlock(new BlockPos(x + i + 1, y, z), log, 2, 2);
            }

            if (dir == 2) {
                world.setBlock(new BlockPos(x - i, y, z), log, 2, 2);
                world.setBlock(new BlockPos(x - i - 1, y, z), log, 2, 2);
            }

            if (dir == 3) {
                world.setBlock(new BlockPos(x, y, z + i), log, 2, 2);
                world.setBlock(new BlockPos(x, y, z + i + 1), log, 2, 2);
            }

            if (dir == 4) {
                world.setBlock(new BlockPos(x, y, z - i), log, 2, 2);
                world.setBlock(new BlockPos(x, y, z - i - 1), log, 2, 2);
            }

        }
    }

    private void createMainCanopy(WorldGenLevel world, Random rand, int x, int y, int z, int maxRadius) {
        for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
            for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
                for (int y1 = y; y1 < y + maxRadius; y1++) {
                    double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
                    if (Math.round(Math.sqrt(dSq)) <= maxRadius)
                        if (world.getBlockState(this.checkPos.set(x1, y1, z1)).getBlock() != log.getBlock() && rand.nextInt(5) != 0)
                            world.setBlock(new BlockPos(x1, y1, z1), leaves, 2, 2);
                }
    }

    @Override
    public boolean generate(WorldGenLevel world, Random rand, BlockPos position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        int height = rand.nextInt(8) + 8;
        int maxRadius = 4;

        this.log = BlockRegistry.RUBBER_LOG.get().defaultBlockState();
        this.leaves = BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState();



        for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
            for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
                for (int yy = y + 2; yy < y + height; yy++)
                    if (!world.getBlockState(checkPos.set(xx, yy, zz)).isAir())// && !world.getBlockState(checkPos.set(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.set(xx, yy, zz)))
                        return false;

        for (int yy = y; yy < y + height; ++yy) {
            world.setBlock(new BlockPos(x, yy, z), log, 2, 2);

            if (yy == y + height - 1)
                createMainCanopy(world, rand, x, yy, z, maxRadius);

            if (yy == y + height - 2) {
                createBranch(world, rand, x + 1, yy, z, 1, 1);
                createBranch(world, rand, x- 1, yy, z, 2, 1);
                createBranch(world, rand, x, yy, z + 1, 3, 1);
                createBranch(world, rand, x, yy, z - 1, 4, 1);
            }
        }
        return true;
    }

    private void createBranch(Level world, Random rand, int x, int y, int z, int dir, int branchLength) {
        for (int i = 0; i <= branchLength; ++i) {

            if (i >= 1) {
                y++;
            }

            if (dir == 1) {
                world.setBlock(new BlockPos(x + i, y, z), log, 2);
                world.setBlock(new BlockPos(x + i + 1, y, z), log, 2);
            }

            if (dir == 2) {
                world.setBlock(new BlockPos(x - i, y, z), log, 2);
                world.setBlock(new BlockPos(x - i - 1, y, z), log, 2);
            }

            if (dir == 3) {
                world.setBlock(new BlockPos(x, y, z + i), log, 2);
                world.setBlock(new BlockPos(x, y, z + i + 1), log, 2);
            }

            if (dir == 4) {
                world.setBlock(new BlockPos(x, y, z - i), log, 2);
                world.setBlock(new BlockPos(x, y, z - i - 1), log, 2);
            }

        }
    }

    private void createMainCanopy(Level world, Random rand, int x, int y, int z, int maxRadius) {
        for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
            for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
                for (int y1 = y; y1 < y + maxRadius; y1++) {
                    double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
                    if (Math.round(Math.sqrt(dSq)) <= maxRadius)
                        if (world.getBlockState(this.checkPos.set(x1, y1, z1)).getBlock() != log.getBlock() && rand.nextInt(5) != 0)
                            world.setBlock(new BlockPos(x1, y1, z1), leaves, 2);
                }
    }

    @Override
    public boolean generate(Level world, Random rand, BlockPos position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        int height = rand.nextInt(8) + 8;
        int maxRadius = 4;

        this.log = BlockRegistry.RUBBER_LOG.get().defaultBlockState();
        this.leaves = BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState();



        for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
            for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
                for (int yy = y + 2; yy < y + height; yy++)
                    if (!world.getBlockState(checkPos.set(xx, yy, zz)).isAir())// && !world.getBlockState(checkPos.set(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.set(xx, yy, zz)))
                        return false;

        for (int yy = y; yy < y + height; ++yy) {
            world.setBlock(new BlockPos(x, yy, z), log, 2);

            if (yy == y + height - 1)
                createMainCanopy(world, rand, x, yy, z, maxRadius);

            if (yy == y + height - 2) {
                createBranch(world, rand, x + 1, yy, z, 1, 1);
                createBranch(world, rand, x- 1, yy, z, 2, 1);
                createBranch(world, rand, x, yy, z + 1, 3, 1);
                createBranch(world, rand, x, yy, z - 1, 4, 1);
            }
        }
        return true;
    }
}
