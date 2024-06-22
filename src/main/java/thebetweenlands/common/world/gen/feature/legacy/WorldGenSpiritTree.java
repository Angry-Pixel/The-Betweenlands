package thebetweenlands.common.world.gen.feature.legacy;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public class WorldGenSpiritTree extends WorldGenHelper {

	private static final ImmutableList<Direction> LEAVES_OFFSETS;

	static {
		ImmutableList.Builder<Direction> builder = ImmutableList.builder();
		builder.add(EnumFacing.HORIZONTALS);
		builder.add(Direction.UP);
		LEAVES_OFFSETS = builder.build();
	}

	private BlockState log;
	private BlockState leavesTop;
	private BlockState leavesMiddle;
	private BlockState leavesBottom;
	private BlockState roots;

	@Nullable
	private ILocationGuard guard;

	@Nullable
	private LocationSpiritTree location;

	public WorldGenSpiritTree(@Nullable ILocationGuard guard, @Nullable LocationSpiritTree location) {
		this.guard = guard;
		this.location = location;
	}

	@Override
	public boolean generate(WorldGenLevel world, Random rand, BlockPos position) {
		int checkRadius = 9;

		for (int xx = -checkRadius; xx <= checkRadius; xx++) {
			for (int zz = -checkRadius; zz <= checkRadius; zz++) {
				for (int yy = 3; yy < 16; yy++) {
					if (!world.getBlockState(position.offset(xx, yy, zz)).isAir() && world.getBlockState(position.offset(xx, yy, zz)).isNormalCube()) {
						return false;
					}
				}
			}
		}

		this.log = BlockRegistry.LOG_SPIRIT_TREE.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		this.leavesTop = BlockRegistry.LEAVES_SPIRIT_TREE_TOP.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
		this.leavesMiddle = BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
		this.leavesBottom = BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
		this.roots = BlockRegistry.ROOT.getDefaultState();

		int trunkX = position.getX();
		int trunkY = position.getY();
		int trunkZ = position.getZ();

		int height = 8 + rand.nextInt(4);

		for (int yo = 0; yo < height; yo++) {
			this.generateTrunkCrossSection(world, rand, trunkX, trunkY + yo, trunkZ, this.log, trunkX, trunkY, trunkZ);
			if (this.location != null) {
				this.location.addLargeFacePosition(new BlockPos(trunkX, trunkY + yo, trunkZ));
			}
		}

		Map<List<BlockPos>, BlockPos> branches = new HashMap<>();

		BlockPos sideBranch;

		//Generate 4 main branches in the cardinal directions

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY + height - 3, trunkZ);
		branches.put(this.generateSideBranch(world, rand, sideBranch, 0, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX, trunkY + height - 3, trunkZ + rand.nextInt(2));
		branches.put(this.generateSideBranch(world, rand, sideBranch, 2, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY + height - 3, trunkZ + 1);
		branches.put(this.generateSideBranch(world, rand, sideBranch, 4, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height - 3, trunkZ + rand.nextInt(2));
		branches.put(this.generateSideBranch(world, rand, sideBranch, 6, trunkX, trunkY, trunkZ), sideBranch);

		//Generate 1-3 diagonal branches
		List<Integer> diagonals = new ArrayList<>(Arrays.asList(1, 3, 5, 7));
		int numDiagonals = 1 + rand.nextInt(3);
		for (int i = 0; i < numDiagonals; i++) {
			int dir = diagonals.remove(rand.nextInt(diagonals.size()));
			int[] offset = this.getDirOffset(dir);
			int bx = trunkX + (offset[0] > 0 ? offset[0] * 2 : offset[0]);
			int bz = trunkZ + (offset[1] > 0 ? offset[1] * 2 : offset[1]);
			sideBranch = new BlockPos(bx, trunkY + height, bz);
			branches.put(this.generateSideBranch(world, rand, sideBranch, dir, trunkX, trunkY, trunkZ), sideBranch);
		}

		//Generate two tall branches at top of trunk
		if (rand.nextBoolean()) {
			sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ);
			branches.put(this.generateTopBranch(world, rand, sideBranch, 1, trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ + 1);
			branches.put(this.generateTopBranch(world, rand, sideBranch, 5, trunkX, trunkY, trunkZ), sideBranch);
		} else {
			sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ + 1);
			branches.put(this.generateTopBranch(world, rand, sideBranch, 3, trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ);
			branches.put(this.generateTopBranch(world, rand, sideBranch, 7, trunkX, trunkY, trunkZ), sideBranch);
		}

		for (Map.Entry<List<BlockPos>, BlockPos> branch : branches.entrySet()) {
			this.generateBranchLeaves(world, rand, branch.getValue(), branch.getKey(), trunkX, trunkY, trunkZ);
		}

		//Generate roots

		List<BlockPos> rootBlocks = new ArrayList<>();

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY, trunkZ);
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 0, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX, trunkY, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 2, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY, trunkZ + 1);
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 4, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX + 1, trunkY, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 6, trunkX, trunkY, trunkZ));

		for (BlockPos rootBlock : rootBlocks) {
			if (rand.nextInt(4) == 0 && new Vec3(rootBlock.getX() + 0.5D, 0, rootBlock.getZ() + 0.5D).distanceToSqr(trunkX + 1.0D, 0, trunkZ + 1.0D) >= 9 && world.getBlockState(rootBlock.above()).isAir()) {
				int rootHeight = 1 + rand.nextInt(3);
				for (int yo = 0; yo < rootHeight; yo++) {
					BlockPos pos = rootBlock.above(1 + yo);
					if (world.getBlockState(pos).isAir()) {
						this.setBlock(world, pos, this.roots, true, trunkX, trunkY, trunkZ);
					} else {
						break;
					}
				}
			}
		}

		return true;
	}

	private void generateTrunkCrossSection(WorldGenLevel world, Random rand, int x, int y, int z, BlockState log, int trunkX, int trunkY, int trunkZ) {
		for (int xo = 0; xo < 2; xo++) {
			for (int zo = 0; zo < 2; zo++) {
				this.setBlock(world, new BlockPos(x + xo, y, z + zo), log, false, trunkX, trunkY, trunkZ);
			}
		}
	}

	private int[] getDirOffset(int dir) {
		int[] offset = new int[2];
		switch (dir) {
			case 0: //N
				offset[1] = -1;
				break;
			case 1: //NW
				offset[0] = -1;
				offset[1] = -1;
				break;
			case 2: //W
				offset[0] = -1;
				break;
			case 3: //SW
				offset[0] = -1;
				offset[1] = 1;
				break;
			case 4: //S
				offset[1] = 1;
				break;
			case 5: //SE
				offset[0] = 1;
				offset[1] = 1;
				break;
			case 6: //E
				offset[0] = 1;
				break;
			case 7: //NE
				offset[0] = 1;
				offset[1] = -1;
				break;
		}
		return offset;
	}

	/*private List<BlockPos> generateSideBranch(World world, Random rand, BlockPos start, int dir) {
		List<BlockPos> branchBlocks = new ArrayList<>();
		branchBlocks.addAll(this.generateBranch(world, rand, start, dir, 8 + rand.nextInt(3), 0.3D, 0.6D, (i, length) -> i < length / 2 ? 1 : (i >= length - 1 && rand.nextInt(2) == 0 ? -1  : 0), (i, length) -> true));
		BlockPos end = branchBlocks.get(branchBlocks.size() - 1);
		switch(dir) {
		case 0:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 1));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 7));
			break;
		case 1:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 0));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 2));
			break;
		case 2:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 1));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 3));
			break;
		case 3:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 2));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 4));
			break;
		case 4:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 3));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 5));
			break;
		case 5:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 4));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 6));
			break;
		case 6:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 5));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 7));
			break;
		case 7:
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 6));
			branchBlocks.addAll(this.generateSideBranch2(world, rand, end, 1));
			break;
		}
		return branchBlocks;
	}

	private List<BlockPos> generateSideBranch2(World world, Random rand, BlockPos start, int dir) {
		return this.generateBranch(world, rand, start, dir, 4 + rand.nextInt(3), 0.3D, 0.6D, (i, length) -> i < length / 2 ? 1 : (i >= length - 1 && rand.nextInt(2) == 0 ? -1  : 0), (i, length) -> true);
	}*/

	private List<BlockPos> generateRoot(WorldGenLevel world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> root = this.generateBranchPositions(rand, start, dir, 2 + rand.nextInt(2), 0.9D, 0.8D, (i, remainingBlocks) -> i < 2 ? 0 : rand.nextInt((i - 2) * 2 + 2) == 0 ? 0 : -1, (i, length) -> true);
		for (BlockPos pos : root) {
			BlockPos rel = pos.subtract(start);
			if (Math.abs(rel.getX()) + Math.abs(rel.getY()) + Math.abs(rel.getZ()) >= 1) {
				root.add(root.indexOf(pos), pos.above());
				break;
			}
		}
		BlockPos end = root.get(root.size() - 1);
		switch (dir) {
			case 0:
				root.addAll(this.generateSubRootPositions(world, rand, end, 1));
				root.addAll(this.generateSubRootPositions(world, rand, end, 7));
				break;
			case 1:
				root.addAll(this.generateSubRootPositions(world, rand, end, 0));
				root.addAll(this.generateSubRootPositions(world, rand, end, 2));
				break;
			case 2:
				root.addAll(this.generateSubRootPositions(world, rand, end, 1));
				root.addAll(this.generateSubRootPositions(world, rand, end, 3));
				break;
			case 3:
				root.addAll(this.generateSubRootPositions(world, rand, end, 2));
				root.addAll(this.generateSubRootPositions(world, rand, end, 4));
				break;
			case 4:
				root.addAll(this.generateSubRootPositions(world, rand, end, 3));
				root.addAll(this.generateSubRootPositions(world, rand, end, 5));
				break;
			case 5:
				root.addAll(this.generateSubRootPositions(world, rand, end, 4));
				root.addAll(this.generateSubRootPositions(world, rand, end, 6));
				break;
			case 6:
				root.addAll(this.generateSubRootPositions(world, rand, end, 5));
				root.addAll(this.generateSubRootPositions(world, rand, end, 7));
				break;
			case 7:
				root.addAll(this.generateSubRootPositions(world, rand, end, 6));
				root.addAll(this.generateSubRootPositions(world, rand, end, 1));
				break;
		}
		for (BlockPos pos : root) {
			this.setBlock(world, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return root;
	}

	private List<BlockPos> generateSubRootPositions(WorldGenLevel world, Random rand, BlockPos start, int dir) {
		return this.generateBranchPositions(rand, start, dir, 1 + rand.nextInt(4), 0.5D, 1, (i, remainingBlocks) -> rand.nextInt(i * 2 + 1) == 0 ? 0 : -1, (i, length) -> true);
	}

	private List<BlockPos> generateSideBranch(WorldGenLevel world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> branch = this.generateSideBranchPositions(rand, start, dir);
		if (branch.size() > 3) {
			BlockPos end = branch.get(3);
			switch (dir) {
				case 0:
					branch.addAll(this.generateSideBranchPositions(rand, end, 1));
					branch.addAll(this.generateSideBranchPositions(rand, end, 7));
					break;
				case 1:
					branch.addAll(this.generateSideBranchPositions(rand, end, 0));
					branch.addAll(this.generateSideBranchPositions(rand, end, 2));
					break;
				case 2:
					branch.addAll(this.generateSideBranchPositions(rand, end, 1));
					branch.addAll(this.generateSideBranchPositions(rand, end, 3));
					break;
				case 3:
					branch.addAll(this.generateSideBranchPositions(rand, end, 2));
					branch.addAll(this.generateSideBranchPositions(rand, end, 4));
					break;
				case 4:
					branch.addAll(this.generateSideBranchPositions(rand, end, 3));
					branch.addAll(this.generateSideBranchPositions(rand, end, 5));
					break;
				case 5:
					branch.addAll(this.generateSideBranchPositions(rand, end, 4));
					branch.addAll(this.generateSideBranchPositions(rand, end, 6));
					break;
				case 6:
					branch.addAll(this.generateSideBranchPositions(rand, end, 5));
					branch.addAll(this.generateSideBranchPositions(rand, end, 7));
					break;
				case 7:
					branch.addAll(this.generateSideBranchPositions(rand, end, 6));
					branch.addAll(this.generateSideBranchPositions(rand, end, 1));
					break;
			}
		}
		for (BlockPos pos : branch) {
			this.setBlock(world, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return branch;
	}

	private List<BlockPos> generateSideBranchPositions(Random rand, BlockPos start, int dir) {
		return this.generateBranchPositions(rand, start, dir, 6 + rand.nextInt(5), 0.3D, 0.6D, (i, remainingBlocks) -> i < remainingBlocks / 2 ? 1 : (i >= remainingBlocks - 1 && rand.nextInt(2) == 0 ? -1 : 0), (i, length) -> true);
	}

	private List<BlockPos> generateTopBranch(WorldGenLevel world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> branch = this.generateBranchPositions(rand, start, dir, 5 + rand.nextInt(5), 0.1D, 0.2D, (i, remainingBlocks) -> i < remainingBlocks - 1 ? 1 : 0, (i, length) -> i >= length - 1);
		for (BlockPos pos : branch) {
			this.setBlock(world, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return branch;
	}

	public List<BlockPos> generateBranchPositions(Random rand, BlockPos start, int dir, int length, double defaultCurveWeight, double directedCurveWeight,
												  BiFunction<Integer, Double, Integer> heightFunction, BiFunction<Integer, Double, Boolean> forceMoveFunction) {
		double remainingBlocks = length;

		List<BlockPos> branchBlocks = new ArrayList<>(length);

		BlockPos branch = start;

		double rx = rand.nextDouble() * defaultCurveWeight * 2 - defaultCurveWeight;
		double rz = rand.nextDouble() * defaultCurveWeight * 2 - defaultCurveWeight;

		int[] offset = this.getDirOffset(dir);

		if (offset[0] != 0) {
			rx = offset[0] * directedCurveWeight;
		}

		if (offset[1] != 0) {
			rz = offset[1] * directedCurveWeight;
		}

		branchBlocks.add(branch);

		for (int i = 0; i < remainingBlocks; i++) {
			int xo = rand.nextDouble() < Math.abs(rx) ? (int) Math.signum(rx) : 0;
			int zo = rand.nextDouble() < Math.abs(rz) ? (int) Math.signum(rz) : 0;
			if (zo == 0 && xo == 0 && forceMoveFunction.apply(i, remainingBlocks)) {
				if (rand.nextDouble() * Math.abs(rx) > rand.nextDouble() * Math.abs(rz)) {
					xo = (int) Math.signum(rx);
				} else {
					zo = (int) Math.signum(rz);
				}
			}
			if (Math.abs(xo) == Math.abs(zo) && xo != 0) {
				remainingBlocks -= 0.414D; //sqrt(2)-1
			}
			branch = branch.offset(xo, heightFunction.apply(i, remainingBlocks), zo);

			branchBlocks.add(branch);
		}

		return branchBlocks;
	}

	private void generateBranchLeaves(WorldGenLevel world, Random rand, BlockPos start, List<BlockPos> branchBlocks, int trunkX, int trunkY, int trunkZ) {
		for (BlockPos branchBlock : branchBlocks) {
			int dist = (int) branchBlock.distSqr(new Vec3i(start.getX(), start.getY(), start.getZ()));
			if (dist >= 2) {
				for (Direction side : LEAVES_OFFSETS) {
					int leavesLength = 3 + (rand.nextInt(5) == 0 ? rand.nextInt(dist + 1) : rand.nextInt(dist / 2 + 1));
					for (int yo = 0; yo > -leavesLength; yo--) {
						BlockPos leafPos = branchBlock.offset(side.getNormal()).offset(0, yo, 0);
						BlockState state;
						if (yo == 0) {
							state = this.leavesTop;
						} else {
							state = this.leavesMiddle;
						}
						if (world.getBlockState(leafPos).isAir()) {
							if (yo == -leavesLength + 1 || (yo < -1 && !world.getBlockState(leafPos.below()).isAir())) {
								state = this.leavesBottom;
								this.setBlock(world, leafPos, state, true, trunkX, trunkY, trunkZ);
								break;
							} else {
								this.setBlock(world, leafPos, state, true, trunkX, trunkY, trunkZ);
							}
						} else {
							break;
						}
					}
				}
			}
		}
	}

	protected void setBlock(WorldGenLevel world, BlockPos pos, BlockState state, boolean registerSmallFacePositions, int trunkX, int trunkY, int trunkZ) {
		this.setBlockAndNotifyAdequately(world, pos, state);

		if (this.guard != null) {
			this.guard.setGuarded(world, pos, true);
		}

		if (!(pos.getX() >= trunkX && pos.getZ() >= trunkZ && pos.getX() <= trunkX + 1 && pos.getZ() <= trunkZ + 1) && registerSmallFacePositions && this.location != null && state.getBlock() == BlockRegistry.LOG_SPIRIT_TREE) {
			this.location.addSmallFacePosition(pos);
		}
	}
}
