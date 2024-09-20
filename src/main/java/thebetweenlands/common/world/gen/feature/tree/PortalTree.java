package thebetweenlands.common.world.gen.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.TreePortalBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

import javax.annotation.Nullable;
import java.util.UUID;

public class PortalTree {

	private static final BlockState BARK = BlockRegistry.PORTAL_LOG.get().defaultBlockState();
	private static final BlockState WOOD = BlockRegistry.WEEDWOOD.get().defaultBlockState();
	private static final BlockState LEAVES = BlockRegistry.WEEDWOOD_LEAVES.get().defaultBlockState();

	public static boolean place(Level level, RandomSource rand, BlockPos pos, @Nullable ResourceKey<Level> dimension) {
		if (pos.getY() > level.getHeight() - 20) {
			return false;
		}

		int radius = 4;
		int height = 16;
		int maxRadius = 9;

		int checkHeight = 20;

		for (int yy = 1; yy < 1 + checkHeight; yy++) {
			int checkRadius = 6;
			if (yy > 11) {
				checkRadius = 10;
			}
			for (int xx = -checkRadius; xx <= checkRadius; xx++) {
				for (int zz = -checkRadius; zz <= checkRadius; zz++) {
					if (xx * xx + zz * zz <= checkRadius * checkRadius) {
						BlockState blockState = level.getBlockState(pos.offset(xx, yy, zz));
						boolean isReplaceable = blockState.isAir() || blockState.is(BlockTags.LEAVES) || blockState.canBeReplaced();
						if (!isReplaceable) {
							return false;
						}
					}
				}
			}
		}

		createMainCanopy(level, rand, pos.offset(0, height / 2 + 4, 0), maxRadius);

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 5)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1)
						level.setBlock(pos.offset(i, yy, j), WOOD, 18);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						level.setBlock(pos.offset(i, yy, j), BARK, 18);
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= height - 1 && yy > 1 && yy <= 10)
						level.setBlock(pos.offset(i, yy, j), Blocks.AIR.defaultBlockState(), 18);
				}

			if (yy == 4) {
				TreePortalBlock.makePortal(level, pos.offset(radius, yy - 2, 0), Direction.WEST);
				TreePortalBlock.makePortal(level, pos.offset(-radius, yy - 2, 0), Direction.WEST);
				TreePortalBlock.makePortal(level, pos.offset(0, yy - 2, radius), Direction.NORTH);
				TreePortalBlock.makePortal(level, pos.offset(0, yy - 2, -radius), Direction.NORTH);
			}

			if (yy == height / 2 + 2) {
				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, false, rand.nextInt(2) + 4);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, false, rand.nextInt(2) + 4);
				createBranch(level, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, false, rand.nextInt(2) + 4);
				createBranch(level, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, false, rand.nextInt(2) + 4);

				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, false, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, false, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, false, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, false, rand.nextInt(2) + 3);
			}

			if (yy == height / 2 + 4) {
				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 4);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 4);
				createSmallBranch(level, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, 4);
				createSmallBranch(level, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, 4);

				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 3);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 3);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 3);
				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 3);
			}

			if (yy == height / 2 + 7) {
				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, 2);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, 2);
				createSmallBranch(level, pos.offset(0, yy - rand.nextInt(3), radius + 1), 3, 2);
				createSmallBranch(level, pos.offset(0, yy - rand.nextInt(3), -radius - 1), 4, 2);

				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 2);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, 2);
				createSmallBranch(level, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, 2);
				createSmallBranch(level, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, 2);
			}

			if (yy == 0) {
				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), 0), 1, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), 0), 2, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(0, yy - rand.nextInt(2), radius + 1), 3, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(0, yy - rand.nextInt(2), -radius - 1), 4, true, rand.nextInt(2) + 3);

				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), radius + 1), 5, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), -radius - 1), 6, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(-radius - 1, yy - rand.nextInt(2), radius + 1), 7, true, rand.nextInt(2) + 3);
				createBranch(level, rand, pos.offset(radius + 1, yy - rand.nextInt(2), -radius - 1), 8, true, rand.nextInt(2) + 3);
			}
		}

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
		if (worldStorage != null) {
			LocationPortal location = new LocationPortal(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos);
			location.addBounds(new AABB(pos).inflate(8, 7, 8).move(0, 7, 0));
			location.setSeed(rand.nextLong());
			if (dimension != null) {
				location.setTargetDimension(dimension);
			}
			location.setDirty(true);
			location.setVisible(false);
//			worldStorage.getLocalStorageHandler().addLocalStorage(location);
		}
		return true;
	}

	private static void createSmallBranch(Level level, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2)
				y++;

			switch (dir) {
				case 1 -> level.setBlock(pos.east(i).above(y), BARK, 18);
				case 2 -> level.setBlock(pos.west(i).above(y), BARK, 18);
				case 3 -> level.setBlock(pos.south(i).above(y), BARK, 18);
				case 4 -> level.setBlock(pos.north(i).above(y), BARK, 18);
				case 5 -> level.setBlock(pos.east(i).above(y).south(i), BARK, 18);
				case 6 -> level.setBlock(pos.west(i).above(y).north(i), BARK, 18);
				case 7 -> level.setBlock(pos.west(i).above(y).south(i), BARK, 18);
				case 8 -> level.setBlock(pos.east(i).above(y).north(i), BARK, 18);
			}
		}
	}

	private static void createMainCanopy(Level level, RandomSource rand, BlockPos pos, int maxRadius) {
		for (int x1 = -maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = -maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (level.getBlockState(pos.offset(x1, y1, z1)) != BARK && rand.nextInt(5) != 0)
							level.setBlock(pos.offset(x1, y1, z1), LEAVES, 18);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && rand.nextInt(5) == 0 && y1 > 0)
						if (level.getBlockState(pos.offset(x1, y1, z1)) != BARK)
							level.setBlock(pos.offset(x1, y1, z1), BARK, 18);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == 0)
						if (level.getBlockState(pos.offset(x1, y1, z1)) != BARK)
							for (int i = 1; i < 1 + rand.nextInt(3); i++)
								level.setBlock(pos.offset(x1, y1 - i, z1), LEAVES, 18);
				}
	}

	private static void createBranch(Level level, RandomSource rand, BlockPos pos, int dir, boolean root, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3)
				y++;

			switch (dir) {
				case 1:
					if (!root)
						level.setBlock(pos.east(i).above(y), BARK, 18);
					else {
						level.setBlock(pos.east(i).below(y), BARK, 18);
						level.setBlock(pos.east(i).below(y - 1), BARK, 18);
					}
					break;

				case 2:
					if (!root)
						level.setBlock(pos.west(i).above(y), BARK, 18);
					else {
						level.setBlock(pos.west(i).below(y), BARK, 18);
						level.setBlock(pos.west(i).below(y - 1), BARK, 18);
					}
					break;

				case 3:
					if (!root)
						level.setBlock(pos.south(i).above(y), BARK, 18);
					else {
						level.setBlock(pos.south(i).below(y), BARK, 18);
						level.setBlock(pos.south(i).below(y - 1), BARK, 18);
					}
					break;

				case 4:
					if (!root)
						level.setBlock(pos.north(i).above(y), BARK, 18);
					else {
						level.setBlock(pos.north(i).below(y), BARK, 18);
						level.setBlock(pos.north(i).below(y - 1), BARK, 18);
					}
					break;

				case 5:
					if (!root)
						level.setBlock(pos.east(i - 1).above(y).south(i - 1), BARK, 18);
					else {
						level.setBlock(pos.east(i - 1).below(y).south(i - 1), BARK, 18);
						level.setBlock(pos.east(i - 1).below(y - 1).south(i - 1), BARK, 18);
					}
					break;

				case 6:
					if (!root)
						level.setBlock(pos.west(i - 1).above(y).north(i - 1), BARK, 18);
					else {
						level.setBlock(pos.west(i - 1).below(y).north(i - 1), BARK, 18);
						level.setBlock(pos.west(i - 1).below(y - 1).north(i - 1), BARK, 18);
					}
					break;

				case 7:
					if (!root)
						level.setBlock(pos.west(i - 1).above(y).south(i - 1), BARK, 18);
					else {
						level.setBlock(pos.west(i - 1).below(y).south(i - 1), BARK, 18);
						level.setBlock(pos.west(i - 1).below(y - 1).south(i - 1), BARK, 18);
					}
					break;

				case 8:
					if (!root)
						level.setBlock(pos.east(i - 1).above(y).north(i - 1), BARK, 18);
					else {
						level.setBlock(pos.east(i - 1).below(y).north(i - 1), BARK, 18);
						level.setBlock(pos.east(i - 1).below(y - 1).north(i - 1), BARK, 18);
					}
					break;
			}
		}
	}
}
