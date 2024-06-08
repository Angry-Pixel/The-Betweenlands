package thebetweenlands.common.world.gen.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.FeatureHelper;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;

import java.util.Random;

public class SapTree extends FeatureHelper<FeatureHelperConfiguration> {

    private BlockState logX, logY, logZ, roots;
    private BlockState  leaves;

    public SapTree(Codec<FeatureHelperConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FeatureHelperConfiguration> context) {

        Random rand = context.random();
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();

        int height = rand.nextInt(3) + 16;
        int maxRadius = 6;

        this.logX = BlockRegistry.SAP_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X);
        this.logY = BlockRegistry.SAP_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y);
        this.logZ = BlockRegistry.SAP_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z);
        this.roots = BlockRegistry.SAP_BARK_LOG.get().defaultBlockState();//.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
        this.leaves = BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);

        for (int xx = - maxRadius; xx <= maxRadius; xx++)
            for (int zz = - maxRadius; zz <= maxRadius; zz++)
                for (int yy = 2; yy < height; yy++)
                    if (!world.getBlockState(pos.offset(xx, yy, zz)).isAir())// && !world.getBlockState(pos.offset(xx, yy, zz)).getBlock().isReplaceable(world, pos.offset(xx, yy, zz)))
                        return false;

        for (int yy = 0; yy < height; ++yy) {
            world.setBlock(pos.offset(0, yy, 0), logY, 2);

            if (yy == 0)
                createRoots(world, pos);

            if (yy == height - 1)
                createLeaves(world, pos.offset(0, yy, 0), true);

            if (yy == height - 8 || yy == height - 12) {
                if (rand.nextBoolean()) {
                    createBranch(world, rand, pos.offset(1, yy - rand.nextInt(2), 0), 1, 2);
                    createBranch(world, rand, pos.offset(- 1, yy - rand.nextInt(2), 0), 2, 2);
                    createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), 1), 3, 2);
                    createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), - 1), 4, 2);
                } else {
                    createBranch(world, rand, pos.offset(2, yy - rand.nextInt(2), 2), 5, 2);
                    createBranch(world, rand, pos.offset(- 2, yy - rand.nextInt(2), - 2), 6, 2);
                    createBranch(world, rand, pos.offset(- 2, yy - rand.nextInt(2), 2), 7, 2);
                    createBranch(world, rand, pos.offset(2, yy - rand.nextInt(2), - 2), 8, 2);
                }
            }

            if (yy == height - 4) {
                if (rand.nextBoolean()) {
                    createBranch(world, rand, pos.offset(1, yy - rand.nextInt(2), 0), 1, 1);
                    createBranch(world, rand, pos.offset(- 1, yy - rand.nextInt(2), 0), 2, 1);
                    createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), 1), 3, 1);
                    createBranch(world, rand, pos.offset(0, yy - rand.nextInt(2), - 1), 4, 1);
                } else {
                    createBranch(world, rand, pos.offset(2, yy - rand.nextInt(2), 2), 5, 1);
                    createBranch(world, rand, pos.offset(- 2, yy - rand.nextInt(2), - 2), 6, 1);
                    createBranch(world, rand, pos.offset(- 2, yy - rand.nextInt(2), 2), 7, 1);
                    createBranch(world, rand, pos.offset(2, yy - rand.nextInt(2), - 2), 8, 1);
                }
            }
        }
        return true;
    }

    private void createBranch(WorldGenLevel world, Random rand, BlockPos pos, int dir, int branchLength) {
        boolean branchBend = false;
        int y = 0;
        for (int i = 0; i <= branchLength; ++i) {

            if (i >= 1) {
                y++;
                branchBend = true;
            }

            if (dir == 1) {
                world.setBlock(pos.offset(i, y, 0), branchBend ? logY : logX, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(i, y, 0), false);
                }
            }

            if (dir == 2) {
                world.setBlock(pos.offset(- i, y, 0), branchBend ? logY : logX, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(- i, y, 0), false);
                }
            }

            if (dir == 3) {
                world.setBlock(pos.offset(0, y, i), branchBend ? logY : logZ, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(0, y, i), false);
                }
            }

            if (dir == 4) {
                world.setBlock(pos.offset(0, y, - i), branchBend ? logY : logZ, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(0, y, - i), false);
                }
            }

            if (dir == 5) {
                world.setBlock(pos.offset(i - 1, y, i - 1),branchBend ? logY : logX, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(i - 1, y, i - 1), false);
                }
            }

            if (dir == 6) {
                world.setBlock(pos.offset(- i + 1, y, - i + 1), branchBend ? logY : logX, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(- i + 1, y, - i + 1), false);
                }
            }

            if (dir == 7) {
                world.setBlock(pos.offset(- i + 1, y, i - 1), branchBend ? logY : logZ, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(- i + 1, y, i - 1), false);
                }
            }

            if (dir == 8) {
                world.setBlock(pos.offset(i - 1, y, - i + 1), branchBend ? logY : logZ, 2);
                if(i == branchLength) {
                    createLeaves(world, pos.offset(i - 1, y, - i + 1), false);
                }
            }
        }
    }

    private void createLeaves(WorldGenLevel world, BlockPos pos, boolean top) {
        world.setBlock(pos.offset(0, 0, 1), leaves, 2);
        world.setBlock(pos.offset(0, 0, - 1), leaves, 2);
        world.setBlock(pos.offset(1, 0, 0), leaves, 2);
        world.setBlock(pos.offset(- 1, 0, 0), leaves, 2);

        if(top)
            world.setBlock(pos.offset(0, 1, 0), leaves, 2);
        else
            world.setBlock(pos.offset(0, - 1, 0), leaves, 2);
    }

    private void createRoots(WorldGenLevel world, BlockPos pos) {
        world.setBlock(pos.offset(0, 0, 1), roots, 2);
        world.setBlock(pos.offset(0, 0, - 1), roots, 2);
        world.setBlock(pos.offset(1, 0, 0), roots, 2);
        world.setBlock(pos.offset(- 1, 0, 0), roots, 2);
    }
}
