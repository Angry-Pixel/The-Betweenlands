package thebetweenlands.common.generation.trees;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class RubberTree {

	// TODO: fix world gen level varient set correct block state

	public BlockState log = BlockRegistry.RUBBER_LOG.get().defaultBlockState();
	public BlockState leaves = BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState();

	private MutableBlockPos checkPos = new MutableBlockPos();

	// Pass this function a level and it'll generate a sap tree
	// Using WorldGenLevel
	public boolean generate(WorldGenLevel level, BlockPos position, Random rand) {


		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		int height = rand.nextInt(8) + 8;
		int maxRadius = 4;

		// add block propertys
		this.log = BlockRegistry.RUBBER_LOG.get().defaultBlockState();//.withProperty(BlockRubberLog.NATURAL, true);
		this.leaves = BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);


		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!level.isEmptyBlock(checkPos.set(xx, yy, zz)) && !level.getBlockState(checkPos.set(xx, yy, zz)).getMaterial().isReplaceable())
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			level.setBlock(new BlockPos(x, yy, z), log, 2, 2);

			if (yy == y + height - 1)
				createMainCanopy(level, rand, x, yy, z, maxRadius);

			if (yy == y + height - 2) {
				createBranch(level, rand, x + 1, yy, z, 1, 1);
				createBranch(level, rand, x - 1, yy, z, 2, 1);
				createBranch(level, rand, x, yy, z + 1, 3, 1);
				createBranch(level, rand, x, yy, z - 1, 4, 1);
			}
		}
		return true;
	}

	// Using LevelAccessor
	public boolean generate(Level level, BlockPos position, Random rand) {

		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		int height = rand.nextInt(8) + 8;
		int maxRadius = 4;

		// add block propertys
		this.log = BlockRegistry.RUBBER_LOG.get().defaultBlockState();//.withProperty(BlockRubberLog.NATURAL, true);
		this.leaves = BlockRegistry.LEAVES_RUBBER_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);


		// added some modifications to alow this to work with worldgenlevel
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!level.isEmptyBlock(checkPos.set(xx, yy, zz)) && !level.getBlockState(checkPos.set(xx, yy, zz)).getMaterial().isReplaceable())
						return false;

		for (int yy = y; yy < y + height; ++yy) {
			level.setBlock(new BlockPos(x, yy, z), log, 2);

			if (yy == y + height - 1)
				createMainCanopy(level, rand, x, yy, z, maxRadius);

			if (yy == y + height - 2) {
				createBranch(level, rand, x + 1, yy, z, 1, 1);
				createBranch(level, rand, x - 1, yy, z, 2, 1);
				createBranch(level, rand, x, yy, z + 1, 3, 1);
				createBranch(level, rand, x, yy, z - 1, 4, 1);
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
}
