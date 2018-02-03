package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockLeavesBetweenlands;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenRubberTree extends WorldGenerator {
	private IBlockState log;
	private IBlockState leaves;

	private MutableBlockPos checkPos = new MutableBlockPos();

	private void createBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 1) {
				y++;
			}

			if (dir == 1) {
				world.setBlockState(new BlockPos(x + i, y, z), log, 2);
				world.setBlockState(new BlockPos(x + i + 1, y, z), log, 2);
			}

			if (dir == 2) {
				world.setBlockState(new BlockPos(x - i, y, z), log, 2);
				world.setBlockState(new BlockPos(x - i - 1, y, z), log, 2);
			}

			if (dir == 3) {
				world.setBlockState(new BlockPos(x, y, z + i), log, 2);
				world.setBlockState(new BlockPos(x, y, z + i + 1), log, 2);
			}

			if (dir == 4) {
				world.setBlockState(new BlockPos(x, y, z - i), log, 2);
				world.setBlockState(new BlockPos(x, y, z - i - 1), log, 2);
			}

		}
	}

	private void createMainCanopy(World world, Random rand, int x, int y, int z, int maxRadius) {
		for (int x1 = x - maxRadius; x1 <= x + maxRadius; x1++)
			for (int z1 = z - maxRadius; z1 <= z + maxRadius; z1++)
				for (int y1 = y; y1 < y + maxRadius; y1++) {
					double dSq = Math.pow(x1 - x, 2.0D) + Math.pow(z1 - z, 2.0D) + Math.pow(y1 - y, 2.0D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlockState(this.checkPos.setPos(x1, y1, z1)).getBlock() != log.getBlock() && rand.nextInt(5) != 0)
							world.setBlockState(new BlockPos(x1, y1, z1), leaves, 2);
				}
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		int height = rand.nextInt(8) + 8;
		int maxRadius = 4;

		this.log = BlockRegistry.LOG_RUBBER.getDefaultState().withProperty(BlockRubberLog.NATURAL, true);
		this.leaves = BlockRegistry.LEAVES_RUBBER_TREE.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);



		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(checkPos.setPos(xx, yy, zz)) && !world.getBlockState(checkPos.setPos(xx, yy, zz)).getBlock().isReplaceable(world, checkPos.setPos(xx, yy, zz)))
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			world.setBlockState(new BlockPos(x, yy, z), log, 2);

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
