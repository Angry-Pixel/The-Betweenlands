package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.structure.BlockTreePortal;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;

public class WorldGenWeedwoodPortalTree extends WorldGenerator {
	private IBlockState bark;
	private IBlockState wood;
	private IBlockState leaves;

	private boolean targetDimSet = false;
	private int targetDim;
	
	public WorldGenWeedwoodPortalTree() {
		super(true);
	}
	
	public WorldGenWeedwoodPortalTree(int targetDim) {
		this.targetDimSet = true;
		this.targetDim = targetDim;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if(pos.getY() > world.getActualHeight() - 20) {
			return false;
		}
		
		int radius = 4;
		int height = 16;
		int maxRadius = 9;

		int checkHeight = 20;

		this.bark = BlockRegistry.LOG_PORTAL.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		this.wood = BlockRegistry.WEEDWOOD.getDefaultState();
		this.leaves = BlockRegistry.LEAVES_WEEDWOOD_TREE.getDefaultState();

		for (int yy = 1; yy < 1 + checkHeight; yy++) {
			int checkRadius = 6;
			if(yy > 11) {
				checkRadius = 10;
			}
			for (int xx = -checkRadius; xx <= checkRadius; xx++) {
				for (int zz = -checkRadius; zz <= checkRadius; zz++) {
					if(xx*xx + zz*zz <= checkRadius*checkRadius) {
						IBlockState blockState = world.getBlockState(pos.add(xx, yy, zz));
						boolean isReplaceable = blockState.getBlock() == Blocks.AIR || blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.PLANTS || 
								blockState.getMaterial() == Material.VINE || blockState.getBlock().isReplaceable(world, pos.add(xx, yy, zz));
						if (!isReplaceable) {
							return false;
						}
					}
				}
			}
		}

		createMainCanopy(world, rand, pos.add(0, height/2 + 4, 0), maxRadius);

		for (int yy = 0; yy < height; ++yy) {
			if (yy % 3 == 0 && radius > 1 && yy > 5)
				--radius;

			for (int i = radius * -1; i <= radius; ++i)
				for (int j = radius * -1; j <= radius; ++j) {
					double dSq = i * i + j * j;
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= 1)
						this.setBlockIfValid(world, pos.add(i, yy, j), wood);
					if (Math.round(Math.sqrt(dSq)) == radius && yy == 0 || Math.round(Math.sqrt(dSq)) == radius && yy <= height - 1)
						this.setBlockIfValid(world, pos.add(i, yy, j), bark);
					if (Math.round(Math.sqrt(dSq)) < radius && yy <= height - 1 && yy > 1 && yy <= 10)
						this.setBlockIfValid(world, pos.add(i, yy, j), Blocks.AIR.getDefaultState());
				}

			if(yy == 4) {
				BlockTreePortal.makePortalX(world, pos.add(radius, yy - 2, 0));
				BlockTreePortal.makePortalX(world, pos.add(- radius, yy - 2, 0));
				BlockTreePortal.makePortalZ(world, pos.add(0, yy - 2, radius));
				BlockTreePortal.makePortalZ(world, pos.add(0, yy - 2, - radius));
			}

			if (yy == height/2 + 2) {
				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), 0), 1, false, rand.nextInt(2) + 4);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), 0), 2, false, rand.nextInt(2) + 4);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), radius + 1), 3, false, rand.nextInt(2) + 4);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), - radius - 1), 4, false, rand.nextInt(2) + 4);

				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), radius + 1), 5, false, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), - radius - 1), 6, false, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), radius + 1), 7, false, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), - radius - 1), 8, false, rand.nextInt(2) + 3);
			}

			if (yy == height/2 + 4) {
				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), 0), 1, 4);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), 0), 2, 4);
				createSmallBranch(world, rand, pos.add(0, yy - rand.nextInt(2), radius + 1), 3, 4);
				createSmallBranch(world, rand, pos.add(0, yy - rand.nextInt(2), - radius - 1), 4, 4);

				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 3);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), - radius - 1), 6, 3);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), radius + 1), 7, 3);
				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), - radius - 1), 8, 3);
			}

			if (yy == height/2 + 7) {
				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), 0), 1, 2);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), 0), 2, 2);
				createSmallBranch(world, rand, pos.add(0, yy - rand.nextInt(3), radius + 1), 3, 2);
				createSmallBranch(world, rand, pos.add(0, yy - rand.nextInt(3), - radius - 1), 4, 2);

				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), radius + 1), 5, 2);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), - radius - 1), 6, 2);
				createSmallBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), radius + 1), 7, 2);
				createSmallBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), - radius - 1), 8, 2);
			}

			if (yy == 0) {
				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), 0), 1, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), 0), 2, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), radius + 1), 3, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(0, yy - rand.nextInt(2), - radius - 1), 4, true, rand.nextInt(2) + 3);

				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), radius + 1), 5, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), - radius - 1), 6, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(- radius - 1, yy - rand.nextInt(2), radius + 1), 7, true, rand.nextInt(2) + 3);
				createBranch(world, rand, pos.add(radius + 1, yy - rand.nextInt(2), - radius - 1), 8, true, rand.nextInt(2) + 3);
			}
		}

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		LocationPortal location = new LocationPortal(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos);
		location.addBounds(new AxisAlignedBB(pos).grow(8, 7, 8).offset(0, 7, 0));
		location.setSeed(rand.nextLong());
		if(this.targetDimSet) {
			location.setTargetDimension(this.targetDim);
		}
		location.setDirty(true);
		location.setVisible(false);
		worldStorage.getLocalStorageHandler().addLocalStorage(location);

		return true;
	}

	private void createSmallBranch(World world, Random rand, BlockPos pos, int dir, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 2)
				y++;

			switch (dir) {
			case 1:
				this.setBlockIfValid(world, pos.east(i).up(y), bark);
				break;

			case 2:
				this.setBlockIfValid(world, pos.west(i).up(y), bark);
				break;

			case 3:
				this.setBlockIfValid(world, pos.south(i).up(y), bark);
				break;

			case 4:
				this.setBlockIfValid(world, pos.north(i).up(y), bark);
				break;

			case 5:
				this.setBlockIfValid(world, pos.east(i).up(y).south(i), bark);
				break;

			case 6:
				this.setBlockIfValid(world, pos.west(i).up(y).north(i), bark);
				break;

			case 7:
				this.setBlockIfValid(world, pos.west(i).up(y).south(i), bark);
				break;

			case 8:
				this.setBlockIfValid(world, pos.east(i).up(y).north(i), bark);
				break;
			}
		}
	}

	private void createMainCanopy(World world, Random rand, BlockPos pos, int maxRadius) {
		for (int x1 = - maxRadius; x1 <= maxRadius; x1++)
			for (int z1 = - maxRadius; z1 <= maxRadius; z1++)
				for (int y1 = 0; y1 < maxRadius; y1++) {
					double dSq = Math.pow(x1, 2.0D) + Math.pow(z1, 2.0D) + Math.pow(y1, 2.5D);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius)
						if (world.getBlockState(pos.add(x1, y1, z1)) != bark && rand.nextInt(5) != 0)
							this.setBlockIfValid(world, pos.add(x1, y1, z1), leaves);
					if (Math.round(Math.sqrt(dSq)) < maxRadius - 1 && rand.nextInt(5) == 0 && y1 > 0)
						if (world.getBlockState(pos.add(x1, y1, z1)) != bark)
							this.setBlockIfValid(world, pos.add(x1, y1, z1), bark);
					if (Math.round(Math.sqrt(dSq)) <= maxRadius && rand.nextInt(3) == 0 && y1 == 0)
						if (world.getBlockState(pos.add(x1, y1, z1)) != bark)
							for (int i = 1; i < 1 + rand.nextInt(3); i++)
								this.setBlockIfValid(world, pos.add(x1, y1 - i, z1), leaves);
				}
	}

	private void createBranch(World world, Random rand, BlockPos pos, int dir, boolean root, int branchLength) {
		int y = 0;
		for (int i = 0; i <= branchLength; ++i) {

			if (i >= 3)
				y++;

			switch (dir) {
			case 1:
				if (!root)
					this.setBlockIfValid(world, pos.east(i).up(y), bark);
				else {
					this.setBlockIfValid(world, pos.east(i).down(y), bark);
					this.setBlockIfValid(world, pos.east(i).down(y - 1), bark);
				}
				break;

			case 2:
				if (!root)
					this.setBlockIfValid(world, pos.west(i).up(y), bark);
				else {
					this.setBlockIfValid(world, pos.west(i).down(y), bark);
					this.setBlockIfValid(world, pos.west(i).down(y - 1), bark);
				}
				break;

			case 3:
				if (!root)
					this.setBlockIfValid(world, pos.south(i).up(y), bark);
				else {
					this.setBlockIfValid(world, pos.south(i).down(y), bark);
					this.setBlockIfValid(world, pos.south(i).down(y - 1), bark);
				}
				break;

			case 4:
				if (!root)
					this.setBlockIfValid(world, pos.north(i).up(y), bark);
				else {
					this.setBlockIfValid(world, pos.north(i).down(y), bark);
					this.setBlockIfValid(world, pos.north(i).down(y - 1), bark);
				}
				break;

			case 5:
				if (!root)
					this.setBlockIfValid(world, pos.east(i - 1).up(y).south(i - 1),
							bark);
				else {
					this.setBlockIfValid(world, pos.east(i - 1).down(y).south(i - 1), bark);
					this.setBlockIfValid(world, pos.east(i - 1).down(y - 1).south(i - 1), bark);
				}
				break;

			case 6:
				if (!root)
					this.setBlockIfValid(world, pos.west(i - 1).up(y).north(i - 1), bark);
				else {
					this.setBlockIfValid(world, pos.west(i - 1).down(y).north(i - 1), bark);
					this.setBlockIfValid(world, pos.west(i - 1).down(y - 1).north(i - 1), bark);
				}
				break;

			case 7:
				if (!root)
					this.setBlockIfValid(world, pos.west(i - 1).up(y).south(i - 1), bark);
				else {
					this.setBlockIfValid(world, pos.west(i - 1).down(y).south(i - 1), bark);
					this.setBlockIfValid(world, pos.west(i - 1).down(y - 1).south(i - 1), bark);
				}
				break;

			case 8:
				if (!root)
					this.setBlockIfValid(world, pos.east(i - 1).up(y).north(i - 1), bark);
				else {
					this.setBlockIfValid(world, pos.east(i - 1).down(y).north(i - 1), bark);
					this.setBlockIfValid(world, pos.east(i - 1).down(y - 1).north(i - 1), bark);
				}
				break;
			}
		}
	}
	
	/**
	 * Sets the block if the block at the specified position is not unbreakable
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	protected boolean setBlockIfValid(World world, BlockPos pos, IBlockState state) {
		if(world.getBlockState(pos).getBlockHardness(world, pos) >= 0) {
			this.setBlockAndNotifyAdequately(world, pos, state);
			return true;
		}
		return false;
	}
}
