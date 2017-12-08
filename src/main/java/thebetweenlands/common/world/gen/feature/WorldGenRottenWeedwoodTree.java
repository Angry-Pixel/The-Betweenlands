package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockPoisonIvy;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenRottenWeedwoodTree extends WorldGenerator {
	private IBlockState log;
	private IBlockState bark;
	private IBlockState wood;
	private IBlockState ivy;

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		int radius = rand.nextInt(2) + 3;
		int height = rand.nextInt(2) + 15;
		int maxRadius = 9;

		this.log = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState();
		this.bark = BlockRegistry.LOG_ROTTEN_BARK.getDefaultState();
		this.wood = BlockRegistry.WEEDWOOD.getDefaultState();
		this.ivy = BlockRegistry.POISON_IVY.getDefaultState();

		if (!world.isAreaLoaded(pos, maxRadius))
			return false;

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		boolean hasPoisonIvy = rand.nextInt(2) == 0;
		
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++)
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++)
				for (int yy = y + 2; yy < y + height; yy++)
					if (!world.isAirBlock(new BlockPos(xx, yy, zz)) && !world.getBlockState(new BlockPos(xx, yy, zz)).getBlock().isReplaceable(world, new BlockPos(xx, yy, zz)))
						return false;


		for (int yy = y; yy < y + height; ++yy) {
			if (yy % 5 == 0 && radius > 1)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= y + height - 2)
						this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), wood);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == y || Math.round(Math.sqrt(dSq)) == radius && yy <= y + height - 1)
						this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, yy, z + j), bark);
				}

			if (yy == y + height/2 + 2) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z, 1, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z, 2, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, x, yy - rand.nextInt(1), z + radius + 1, 3, false, rand.nextInt(2) + 4, hasPoisonIvy);
				createBranch(world, rand, x, yy - rand.nextInt(1), z - radius - 1, 4, false, rand.nextInt(2) + 4, hasPoisonIvy);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z + radius + 1, 5, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z - radius - 1, 6, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z + radius + 1, 7, false, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z - radius - 1, 8, false, rand.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == y + height/2 + 4) {
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z, 1, 4);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z, 2, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(1), z + radius + 1, 3, 4);
				createSmallBranch(world, rand, x, yy - rand.nextInt(1), z - radius - 1, 4, 4);

				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z + radius + 1, 5, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z - radius - 1, 6, 3);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z + radius + 1, 7, 3);
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z - radius - 1, 8, 3);
			}

			if (yy == y + height/2 + 7) {
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z, 1, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z, 2, 2);
				createSmallBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, 2);
				createSmallBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, 2);

				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z + radius + 1, 5, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z - radius - 1, 6, 2);
				createSmallBranch(world, rand, x - radius - 1, yy - rand.nextInt(1), z + radius + 1, 7, 2);
				createSmallBranch(world, rand, x + radius + 1, yy - rand.nextInt(1), z - radius - 1, 8, 2);
			}

			if (yy == y + 1) {
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(3), z, 1, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(3), z, 2, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x, yy - rand.nextInt(3), z + radius + 1, 3, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x, yy - rand.nextInt(3), z - radius - 1, 4, true, rand.nextInt(2) + 3, hasPoisonIvy);

				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z + radius + 1, 5, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z - radius - 1, 6, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x - radius - 1, yy - rand.nextInt(2), z + radius + 1, 7, true, rand.nextInt(2) + 3, hasPoisonIvy);
				createBranch(world, rand, x + radius + 1, yy - rand.nextInt(2), z - radius - 1, 8, true, rand.nextInt(2) + 3, hasPoisonIvy);
			}
		}
		return true;
	}

	private void createSmallBranch(World world, Random rand, int x, int y, int z, int dir, int branchLength) {
		for (int i = 0; i <= branchLength; ++i) {
			if (dir == 1)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y, z), log);

			if (dir == 2)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x - i, y, z), log);

			if (dir == 3)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + i), log);

			if (dir == 4)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - i), log);

			if (dir == 5)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z + i - 1), log);

			if (dir == 6)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z - i + 1), log);

			if (dir == 7)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z + i - 1), log);

			if (dir == 8)
				this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z - i + 1), log);
		}
	}


	private void createBranch(World world, Random rand, int x, int y, int z, int dir, boolean root, int branchLength, boolean ivy) {
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3) {
				if(!root)
					y++;
				else
					y--;
			}

			if (dir == 1)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y, z), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x + i, y - 1, z, EnumFacing.WEST);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y, z), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i, y - 1, z), log);
				}

			if (dir == 2)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i, y, z), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x - i, y - 1, z, EnumFacing.EAST);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i, y, z), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i, y - 1, z), log);
				}

			if (dir == 3)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + i), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x, y - 1, z + i, EnumFacing.NORTH);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z + i), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z + i), log);
				}

			if (dir == 4)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - i), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x, y - 1, z - i, EnumFacing.SOUTH);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y, z - i), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, y - 1, z - i), log);
				}

			if (dir == 5)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z + i - 1), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x + i - 1, y - 1, z + i - 1, EnumFacing.WEST);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z + i - 1), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y - 1, z + i - 1), log);
				}

			if (dir == 6)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z - i + 1), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x - i + 1, y - 1, z - i + 1, EnumFacing.SOUTH);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z - i + 1), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y - 1, z - i + 1), log);
				}

			if (dir == 7)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z + i - 1), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x - i + 1, y - 1, z + i - 1, EnumFacing.EAST);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y, z + i - 1), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x - i + 1, y - 1, z + i - 1), log);
				}

			if (dir == 8)
				if (!root) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z - i + 1), log);
					if (i <= branchLength && ivy)
						addVines(world, rand, x + i - 1, y - 1, z - i + 1, EnumFacing.NORTH);
				} else {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y, z - i + 1), log);
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + i - 1, y - 1, z - i + 1), log);
				}
		}
	}

	private void addVines(World world, Random rand, int x, int y, int z, EnumFacing... dirs) {
		IBlockState state = this.ivy;
		for(EnumFacing dir : dirs) {
			switch(dir) {
			default:
			case NORTH:
				state = state.withProperty(BlockPoisonIvy.NORTH, true);
				break;
			case SOUTH:
				state = state.withProperty(BlockPoisonIvy.SOUTH, true);
				break;
			case EAST:
				state = state.withProperty(BlockPoisonIvy.EAST, true);
				break;
			case WEST:
				state = state.withProperty(BlockPoisonIvy.WEST, true);
				break;
			case UP:
				state = state.withProperty(BlockPoisonIvy.UP, true);
				break;
			}
		}
		if (BlockRegistry.POISON_IVY.canPlaceBlockOnSide(world, new BlockPos(x, y, z), dirs[0].getOpposite()) && rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = y; yy > y - length; --yy)
				if (world.isAirBlock(new BlockPos(x, yy, z))) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x, yy, z), state);
				} else {
					break;
				}
		}
	}
}
