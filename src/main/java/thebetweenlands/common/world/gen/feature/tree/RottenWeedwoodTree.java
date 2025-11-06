package thebetweenlands.common.world.gen.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

		boolean hasPoisonIvy = random.nextInt(2) == 0;

		for (int xx = -maxRadius; xx <= maxRadius; xx++)
			for (int zz = -maxRadius; zz <= maxRadius; zz++)
				for (int yy = 2; yy < height; yy++)
					if (!level.isEmptyBlock(pos.offset(xx, yy, zz)) && !level.getBlockState(pos.offset(xx, yy, zz)).canBeReplaced()) {
						return false;
					}

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 3)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1 + height - 2)
						level.setBlock(pos.offset(i, yy, j), wood, 2);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						level.setBlock(pos.offset(i, yy, j), bark, 2);
				}

			if (yy == height / 2 + 2) {
				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, false, random.nextInt(2) + 4, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, false, random.nextInt(2) + 4, hasPoisonIvy);

				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, false, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, false, random.nextInt(2) + 3, hasPoisonIvy);
			}

			if (yy == height / 2 + 4) {
				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, 4);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, 4);
				this.createSmallBranch(level, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, 4);
				this.createSmallBranch(level, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, 4);

				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, 3);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, 3);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, 3);
				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, 3);
			}

			if (yy == height / 2 + 7) {
				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, 2);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, 2);
				this.createSmallBranch(level, pos.offset(0, yy - random.nextInt(3), radius + 1), 3, 2);
				this.createSmallBranch(level, pos.offset(0, yy - random.nextInt(3), -radius - 1), 4, 2);

				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, 2);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, 2);
				this.createSmallBranch(level, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, 2);
				this.createSmallBranch(level, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, 2);
			}

			if (yy == 0) {
				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), 0), 1, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), 0), 2, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(0, yy - random.nextInt(2), radius + 1), 3, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(0, yy - random.nextInt(2), -radius - 1), 4, true, random.nextInt(2) + 3, hasPoisonIvy);

				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), radius + 1), 5, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), -radius - 1), 6, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(-radius - 1, yy - random.nextInt(2), radius + 1), 7, true, random.nextInt(2) + 3, hasPoisonIvy);
				this.createBranch(level, random, pos.offset(radius + 1, yy - random.nextInt(2), -radius - 1), 8, true, random.nextInt(2) + 3, hasPoisonIvy);
			}
		}
		return true;
	}

	private void createSmallBranch(LevelAccessor level, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:
					level.setBlock(pos.east(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 2:
					level.setBlock(pos.west(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 3:
					level.setBlock(pos.south(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 4:
					level.setBlock(pos.north(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 5:
					level.setBlock(pos.east(i).above(y).south(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 6:
					level.setBlock(pos.west(i).above(y).north(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
					break;

				case 7:
					level.setBlock(pos.west(i).above(y).south(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;

				case 8:
					level.setBlock(pos.east(i).above(y).north(i), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
					break;
			}
		}
	}

	private void createBranch(LevelAccessor level, RandomSource random, BlockPos pos, int dir, boolean root, int branchLength, boolean ivy) {
		int y = 0;
		boolean branchBend = false;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3) {
				y++;
				branchBend = true;
			}

			switch (dir) {
				case 1:

					if (!root) {
						level.setBlock(pos.east(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.east(i).above(y - 1), Direction.EAST);
					} else {
						level.setBlock(pos.east(i).below(y), bark, 2);
						level.setBlock(pos.east(i).below(y - 1), bark, 2);
					}
					break;

				case 2:
					if (!root) {
						level.setBlock(pos.west(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.west(i).above(y - 1), Direction.WEST);
					} else {
						level.setBlock(pos.west(i).below(y), bark, 2);
						level.setBlock(pos.west(i).below(y - 1), bark, 2);
					}
					break;

				case 3:
					if (!root) {
						level.setBlock(pos.south(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.south(i).above(y - 1), Direction.SOUTH);
					} else {
						level.setBlock(pos.south(i).below(y), bark, 2);
						level.setBlock(pos.south(i).below(y - 1), bark, 2);
					}
					break;

				case 4:
					if (!root) {
						level.setBlock(pos.north(i).above(y), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.north(i).above(y - 1), Direction.NORTH);
					} else {
						level.setBlock(pos.north(i).below(y), bark, 2);
						level.setBlock(pos.north(i).below(y - 1), bark, 2);
					}
					break;

				case 5:
					if (!root) {
						level.setBlock(pos.east(i - 1).above(y).south(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.east(i - 1).above(y - 1).south(i - 1), Direction.EAST);
					} else {
						level.setBlock(pos.east(i - 1).below(y).south(i - 1), bark, 2);
						level.setBlock(pos.east(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 6:
					if (!root) {
						level.setBlock(pos.west(i - 1).above(y).north(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.X), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.west(i - 1).above(y - 1).north(i - 1), Direction.WEST);
					} else {
						level.setBlock(pos.west(i - 1).below(y).north(i - 1), bark, 2);
						level.setBlock(pos.west(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;

				case 7:
					if (!root) {
						level.setBlock(pos.west(i - 1).above(y).south(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.west(i - 1).above(y - 1).south(i - 1), Direction.SOUTH);
					} else {
						level.setBlock(pos.west(i - 1).below(y).south(i - 1), bark, 2);
						level.setBlock(pos.west(i - 1).below(y - 1).south(i - 1), bark, 2);
					}
					break;

				case 8:
					if (!root) {
						level.setBlock(pos.east(i - 1).above(y).north(i - 1), branchBend ? this.log : this.log.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), 2);
						if (i <= branchLength && ivy)
							addVines(level, random, pos.east(i - 1).above(y - 1).north(i - 1), Direction.NORTH);
					} else {
						level.setBlock(pos.east(i - 1).below(y).north(i - 1), bark, 2);
						level.setBlock(pos.east(i - 1).below(y - 1).north(i - 1), bark, 2);
					}
					break;
			}
		}
	}

	private void addVines(LevelAccessor level, RandomSource random, BlockPos pos, Direction facing) {
		if (random.nextInt(4) != 0) {
			int length = random.nextInt(4) + 4;
			for (int yy = 0; yy < length; ++yy)
				if (level.isEmptyBlock(pos.below(yy)))
					level.setBlock(pos.below(yy), BlockRegistry.POISON_IVY.get().defaultBlockState().setValue(VineBlock.getPropertyForFace(facing.getOpposite()), true), 2);
				else
					break;
		}
	}
}