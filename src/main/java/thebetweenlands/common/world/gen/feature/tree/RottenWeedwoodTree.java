package thebetweenlands.common.world.gen.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;

public class RottenWeedwoodTree extends Feature<NoneFeatureConfiguration> {

	private final BlockState log = BlockRegistry.ROTTEN_BARK.get().defaultBlockState();
	private final BlockState bark = BlockRegistry.ROTTEN_BARK.get().defaultBlockState();
	private final BlockState wood = BlockRegistry.WEEDWOOD.get().defaultBlockState();
	private final BlockState ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

	public RottenWeedwoodTree(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return this.generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(LevelAccessor level, RandomSource random, BlockPos pos) {
		int radius = random.nextInt(2) + 3;
		int height = random.nextInt(2) + 15;
		int maxRadius = 9;

		if (!level.isAreaLoaded(pos, maxRadius))
			return false;

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		boolean hasPoisonIvy = random.nextInt(2) == 0;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!level.isEmptyBlock(pos.offset(xx, yy, zz)) && !level.getBlockState(pos.offset(xx, yy, zz)).canBeReplaced()) {
						return false;
					}

		for (int yy = y; yy < y + height; ++yy) {
			if (yy % 5 == 0 && radius > 1)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= y + height - 2)
						this.setBlock(level, new BlockPos(x + i, yy, z + j), wood);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == y || Math.round(Math.sqrt(dSq)) == radius && yy <= y + height - 1)
						this.setBlock(level, new BlockPos(x + i, yy, z + j), bark);
				}

			if (yy == y + height/2 + 2) {
				createBranch(level, random, x + radius + 1, yy - random.nextInt(1), z, 1, false, random.nextInt(2) + 4, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(1), z, 2, false, random.nextInt(2) + 4, hasPoisonIvy);
				createBranch(level, random, x, yy - random.nextInt(1), z + radius + 1, 3, false, random.nextInt(2) + 4, hasPoisonIvy);
				createBranch(level, random, x, yy - random.nextInt(1), z - radius - 1, 4, false, random.nextInt(2) + 4, hasPoisonIvy);

				createBranch(level, random, x + radius + 1, yy - random.nextInt(1), z + radius + 1, 5, false, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(1), z - radius - 1, 6, false, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(1), z + radius + 1, 7, false, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x + radius + 1, yy - random.nextInt(1), z - radius - 1, 8, false, random.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == y + height/2 + 4) {
				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(1), z, 1, 4);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(1), z, 2, 4);
				createSmallBranch(level, random, x, yy - random.nextInt(1), z + radius + 1, 3, 4);
				createSmallBranch(level, random, x, yy - random.nextInt(1), z - radius - 1, 4, 4);

				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(1), z + radius + 1, 5, 3);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(1), z - radius - 1, 6, 3);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(1), z + radius + 1, 7, 3);
				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(1), z - radius - 1, 8, 3);
			}

			if (yy == y + height/2 + 7) {
				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(2), z, 1, 2);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(2), z, 2, 2);
				createSmallBranch(level, random, x, yy - random.nextInt(3), z + radius + 1, 3, 2);
				createSmallBranch(level, random, x, yy - random.nextInt(3), z - radius - 1, 4, 2);

				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(1), z + radius + 1, 5, 2);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(1), z - radius - 1, 6, 2);
				createSmallBranch(level, random, x - radius - 1, yy - random.nextInt(1), z + radius + 1, 7, 2);
				createSmallBranch(level, random, x + radius + 1, yy - random.nextInt(1), z - radius - 1, 8, 2);
			}

			if (yy == y + 1) {
				createBranch(level, random, x + radius + 1, yy - random.nextInt(3), z, 1, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(3), z, 2, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x, yy - random.nextInt(3), z + radius + 1, 3, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x, yy - random.nextInt(3), z - radius - 1, 4, true, random.nextInt(2) + 3, hasPoisonIvy);

				createBranch(level, random, x + radius + 1, yy - random.nextInt(2), z + radius + 1, 5, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(2), z - radius - 1, 6, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x - radius - 1, yy - random.nextInt(2), z + radius + 1, 7, true, random.nextInt(2) + 3, hasPoisonIvy);
				createBranch(level, random, x + radius + 1, yy - random.nextInt(2), z - radius - 1, 8, true, random.nextInt(2) + 3, hasPoisonIvy);
			}
		}
		return true;
	}

	private void createSmallBranch(LevelAccessor world, RandomSource random, int x, int y, int z, int dir, int branchLength) {
		for (int i = 0; i <= branchLength; ++i) {
			if (dir == 1)
				this.setBlock(world, new BlockPos(x + i, y, z), log);

			if (dir == 2)
				this.setBlock(world, new BlockPos(x - i, y, z), log);

			if (dir == 3)
				this.setBlock(world, new BlockPos(x, y, z + i), log);

			if (dir == 4)
				this.setBlock(world, new BlockPos(x, y, z - i), log);

			if (dir == 5)
				this.setBlock(world, new BlockPos(x + i - 1, y, z + i - 1), log);

			if (dir == 6)
				this.setBlock(world, new BlockPos(x - i + 1, y, z - i + 1), log);

			if (dir == 7)
				this.setBlock(world, new BlockPos(x - i + 1, y, z + i - 1), log);

			if (dir == 8)
				this.setBlock(world, new BlockPos(x + i - 1, y, z - i + 1), log);
		}
	}


	private void createBranch(LevelAccessor level, RandomSource random, int x, int y, int z, int dir, boolean root, int branchLength, boolean ivy) {
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3) {
				if(!root)
					y++;
				else
					y--;
			}

			if (dir == 1)
				if (!root) {
					this.setBlock(level, new BlockPos(x + i, y, z), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x + i, y - 1, z, Direction.WEST);
				} else {
					this.setBlock(level, new BlockPos(x + i, y, z), log);
					this.setBlock(level, new BlockPos(x + i, y - 1, z), log);
				}

			if (dir == 2)
				if (!root) {
					this.setBlock(level, new BlockPos(x - i, y, z), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x - i, y - 1, z, Direction.EAST);
				} else {
					this.setBlock(level, new BlockPos(x - i, y, z), log);
					this.setBlock(level, new BlockPos(x - i, y - 1, z), log);
				}

			if (dir == 3)
				if (!root) {
					this.setBlock(level, new BlockPos(x, y, z + i), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x, y - 1, z + i, Direction.NORTH);
				} else {
					this.setBlock(level, new BlockPos(x, y, z + i), log);
					this.setBlock(level, new BlockPos(x, y - 1, z + i), log);
				}

			if (dir == 4)
				if (!root) {
					this.setBlock(level, new BlockPos(x, y, z - i), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x, y - 1, z - i, Direction.SOUTH);
				} else {
					this.setBlock(level, new BlockPos(x, y, z - i), log);
					this.setBlock(level, new BlockPos(x, y - 1, z - i), log);
				}

			if (dir == 5)
				if (!root) {
					this.setBlock(level, new BlockPos(x + i - 1, y, z + i - 1), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x + i - 1, y - 1, z + i - 1, Direction.WEST);
				} else {
					this.setBlock(level, new BlockPos(x + i - 1, y, z + i - 1), log);
					this.setBlock(level, new BlockPos(x + i - 1, y - 1, z + i - 1), log);
				}

			if (dir == 6)
				if (!root) {
					this.setBlock(level, new BlockPos(x - i + 1, y, z - i + 1), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x - i + 1, y - 1, z - i + 1, Direction.SOUTH);
				} else {
					this.setBlock(level, new BlockPos(x - i + 1, y, z - i + 1), log);
					this.setBlock(level, new BlockPos(x - i + 1, y - 1, z - i + 1), log);
				}

			if (dir == 7)
				if (!root) {
					this.setBlock(level, new BlockPos(x - i + 1, y, z + i - 1), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x - i + 1, y - 1, z + i - 1, Direction.EAST);
				} else {
					this.setBlock(level, new BlockPos(x - i + 1, y, z + i - 1), log);
					this.setBlock(level, new BlockPos(x - i + 1, y - 1, z + i - 1), log);
				}

			if (dir == 8)
				if (!root) {
					this.setBlock(level, new BlockPos(x + i - 1, y, z - i + 1), log);
					if (i <= branchLength && ivy)
						addVines(level, random, x + i - 1, y - 1, z - i + 1, Direction.NORTH);
				} else {
					this.setBlock(level, new BlockPos(x + i - 1, y, z - i + 1), log);
					this.setBlock(level, new BlockPos(x + i - 1, y - 1, z - i + 1), log);
				}
		}
	}

	private void addVines(LevelAccessor level, RandomSource rand, int x, int y, int z, Direction... dirs) {
		BlockState state = this.ivy;
		for(Direction dir : dirs) {
			state = switch (dir) {
				default -> state.setValue(VineBlock.getPropertyForFace(Direction.NORTH), true);
				case SOUTH -> state.setValue(VineBlock.getPropertyForFace(Direction.SOUTH), true);
				case EAST -> state.setValue(VineBlock.getPropertyForFace(Direction.EAST), true);
				case WEST -> state.setValue(VineBlock.getPropertyForFace(Direction.WEST), true);
				case UP -> state.setValue(VineBlock.getPropertyForFace(Direction.UP), true);
			};
		}
		if (BlockRegistry.POISON_IVY.get().defaultBlockState().isFaceSturdy(level, new BlockPos(x, y, z), dirs[0].getOpposite()) && rand.nextInt(4) != 0) {
			int length = rand.nextInt(4) + 4;
			for (int yy = y; yy > y - length; --yy)
				if (level.getBlockState(new BlockPos(x, yy, z)).isAir()) {
					this.setBlock(level, new BlockPos(x, yy, z), state);
				} else {
					break;
				}
		}
	}
}