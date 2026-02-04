package thebetweenlands.common.world.gen.feature.tree;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.entity.monster.spirit_tree.AbstractSpiritTreeFace;
import thebetweenlands.common.entity.monster.wall.AbstractWallCreature;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public class SpiritTree extends Feature<NoneFeatureConfiguration> {

	private static final ImmutableList<Direction> LEAVES_OFFSETS = ImmutableList.<Direction>builder()
		.addAll(Direction.Plane.HORIZONTAL.stream().toList())
		.add(Direction.UP)
		.build();


	protected final BlockState log = BlockRegistry.SPIRIT_TREE_BARK.get().defaultBlockState();
	protected final BlockState leavesTop = BlockRegistry.TOP_SPIRIT_TREE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	protected final BlockState leavesMiddle = BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	protected final BlockState leavesBottom = BlockRegistry.BOTTOM_SPIRIT_TREE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	protected final BlockState roots = BlockRegistry.ROOT.get().defaultBlockState();

	@Nullable
	private final ILocationGuard guard;

	@Nullable
	private final LocationSpiritTree location;

	public SpiritTree(Codec<NoneFeatureConfiguration> codec) {
		this(codec, null, null);
	}

	public SpiritTree(Codec<NoneFeatureConfiguration> codec, @Nullable ILocationGuard guard, @Nullable LocationSpiritTree location) {
		super(codec);
		this.guard = guard;
		this.location = location;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos position = context.origin();
		RandomSource rand = context.random();

		int checkRadius = 9;

		for (int xx = -checkRadius; xx <= checkRadius; xx++) {
			for (int zz = -checkRadius; zz <= checkRadius; zz++) {
				for (int yy = 3; yy < 16; yy++) {
					if (!level.isEmptyBlock(position.offset(xx, yy, zz)) && level.getBlockState(position.offset(xx, yy, zz)).isRedstoneConductor(level, position.offset(xx, yy, zz))) {
						return false;
					}
				}
			}
		}

		int trunkX = position.getX();
		int trunkY = position.getY();
		int trunkZ = position.getZ();

		int height = 8 + rand.nextInt(4);

		for (int yo = 0; yo < height; yo++) {
			this.generateTrunkCrossSection(level, trunkX, trunkY + yo, trunkZ, this.log, trunkX, trunkY, trunkZ);
			if (this.location != null) {
				this.location.addLargeFacePosition(new BlockPos(trunkX, trunkY + yo, trunkZ));
			}
		}

		Map<List<BlockPos>, BlockPos> branches = new HashMap<>();

		BlockPos sideBranch;

		//Generate 4 main branches in the cardinal directions

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY + height - 3, trunkZ);
		branches.put(this.generateSideBranch(level, rand, sideBranch, 0, 6 + rand.nextInt(5), trunkX, trunkY, trunkZ, true), sideBranch);

		sideBranch = new BlockPos(trunkX, trunkY + height - 3, trunkZ + rand.nextInt(2));
		branches.put(this.generateSideBranch(level, rand, sideBranch, 2, 6 + rand.nextInt(5), trunkX, trunkY, trunkZ, true), sideBranch);

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY + height - 3, trunkZ + 1);
		branches.put(this.generateSideBranch(level, rand, sideBranch, 4, 6 + rand.nextInt(5), trunkX, trunkY, trunkZ, true), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height - 3, trunkZ + rand.nextInt(2));
		branches.put(this.generateSideBranch(level, rand, sideBranch, 6, 6 + rand.nextInt(5), trunkX, trunkY, trunkZ, true), sideBranch);

		//Generate 1-3 diagonal branches
		List<Integer> diagonals = new ArrayList<>(Arrays.asList(1, 3, 5, 7));
		int numDiagonals = 1 + rand.nextInt(3);
		for (int i = 0; i < numDiagonals; i++) {
			int dir = diagonals.remove(rand.nextInt(diagonals.size()));
			int[] offset = this.getDirOffset(dir);
			int bx = trunkX + (offset[0] > 0 ? offset[0] * 2 : offset[0]);
			int bz = trunkZ + (offset[1] > 0 ? offset[1] * 2 : offset[1]);
			sideBranch = new BlockPos(bx, trunkY + height, bz);
			branches.put(this.generateSideBranch(level, rand, sideBranch, dir, 6 + rand.nextInt(5), trunkX, trunkY, trunkZ, true), sideBranch);
		}

		//Generate two tall branches at top of trunk
		if (rand.nextBoolean()) {
			sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ);
			branches.put(this.generateTopBranch(level, rand, sideBranch, 1, 5 + rand.nextInt(5), trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ + 1);
			branches.put(this.generateTopBranch(level, rand, sideBranch, 5, 5 + rand.nextInt(5), trunkX, trunkY, trunkZ), sideBranch);
		} else {
			sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ + 1);
			branches.put(this.generateTopBranch(level, rand, sideBranch, 3, 5 + rand.nextInt(5), trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ);
			branches.put(this.generateTopBranch(level, rand, sideBranch, 7, 5 + rand.nextInt(5), trunkX, trunkY, trunkZ), sideBranch);
		}

		for (Map.Entry<List<BlockPos>, BlockPos> branch : branches.entrySet()) {
			this.generateBranchLeaves(level, rand, branch.getValue(), branch.getKey(), trunkX, trunkY, trunkZ);
		}

		//Generate roots

		List<BlockPos> rootBlocks = new ArrayList<>();

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY, trunkZ);
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 0, 2 + rand.nextInt(2), trunkX, trunkY, trunkZ, true));

		sideBranch = new BlockPos(trunkX, trunkY, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 2, 2 + rand.nextInt(2), trunkX, trunkY, trunkZ, true));

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY, trunkZ + 1);
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 4, 2 + rand.nextInt(2), trunkX, trunkY, trunkZ, true));

		sideBranch = new BlockPos(trunkX + 1, trunkY, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 6, 2 + rand.nextInt(2), trunkX, trunkY, trunkZ, true));

		for (BlockPos rootBlock : rootBlocks) {
			if (rand.nextInt(4) == 0 && new Vec3(rootBlock.getX() + 0.5D, 0, rootBlock.getZ() + 0.5D).distanceToSqr(trunkX + 1.0D, 0, trunkZ + 1.0D) >= 9 && level.isEmptyBlock(rootBlock.above())) {
				int rootHeight = 1 + rand.nextInt(3);
				for (int yo = 0; yo < rootHeight; yo++) {
					BlockPos pos = rootBlock.above(1 + yo);
					if (level.isEmptyBlock(pos)) {
						this.setBlock(level, pos, this.roots, true, trunkX, trunkY, trunkZ);
					} else {
						break;
					}
				}
			}
		}

		return true;
	}

	private void generateTrunkCrossSection(WorldGenLevel level, int x, int y, int z, BlockState log, int trunkX, int trunkY, int trunkZ) {
		for (int xo = 0; xo < 2; xo++) {
			for (int zo = 0; zo < 2; zo++) {
				this.setBlock(level, new BlockPos(x + xo, y, z + zo), log, false, trunkX, trunkY, trunkZ);
			}
		}
	}

	protected int[] getDirOffset(int dir) {
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

	protected List<BlockPos> generateRoot(WorldGenLevel level, RandomSource rand, BlockPos start, int dir, int length, int trunkX, int trunkY, int trunkZ, boolean genSubroots) {
		List<BlockPos> root = this.generateBranchPositions(rand, start, dir, length, 0.9D, 0.8D, (i, remainingBlocks) -> i < 2 ? 0 : rand.nextInt((i - 2) * 2 + 2) == 0 ? 0 : -1, (i, len) -> true);
		for (BlockPos pos : root) {
			BlockPos rel = pos.subtract(start);
			if (Math.abs(rel.getX()) + Math.abs(rel.getY()) + Math.abs(rel.getZ()) >= 1) {
				root.add(root.indexOf(pos), pos.above());
				break;
			}
		}
		if (genSubroots) {
			BlockPos end = root.getLast();
			switch (dir) {
				case 0:
					root.addAll(this.generateSubRootPositions(rand, end, 1));
					root.addAll(this.generateSubRootPositions(rand, end, 7));
					break;
				case 1:
					root.addAll(this.generateSubRootPositions(rand, end, 0));
					root.addAll(this.generateSubRootPositions(rand, end, 2));
					break;
				case 2:
					root.addAll(this.generateSubRootPositions(rand, end, 1));
					root.addAll(this.generateSubRootPositions(rand, end, 3));
					break;
				case 3:
					root.addAll(this.generateSubRootPositions(rand, end, 2));
					root.addAll(this.generateSubRootPositions(rand, end, 4));
					break;
				case 4:
					root.addAll(this.generateSubRootPositions(rand, end, 3));
					root.addAll(this.generateSubRootPositions(rand, end, 5));
					break;
				case 5:
					root.addAll(this.generateSubRootPositions(rand, end, 4));
					root.addAll(this.generateSubRootPositions(rand, end, 6));
					break;
				case 6:
					root.addAll(this.generateSubRootPositions(rand, end, 5));
					root.addAll(this.generateSubRootPositions(rand, end, 7));
					break;
				case 7:
					root.addAll(this.generateSubRootPositions(rand, end, 6));
					root.addAll(this.generateSubRootPositions(rand, end, 1));
					break;
			}
		}
		for (BlockPos pos : root) {
			this.setBlock(level, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return root;
	}

	protected List<BlockPos> generateSubRootPositions(RandomSource rand, BlockPos start, int dir) {
		return this.generateBranchPositions(rand, start, dir, 1 + rand.nextInt(4), 0.5D, 1, (i, remainingBlocks) -> rand.nextInt(i * 2 + 1) == 0 ? 0 : -1, (i, length) -> true);
	}

	protected List<BlockPos> generateSideBranch(WorldGenLevel level, RandomSource rand, BlockPos start, int dir, int initialLength, int trunkX, int trunkY, int trunkZ, boolean genSubBranches) {
		List<BlockPos> branch = this.generateSideBranchPositions(rand, start, dir, initialLength);
		if (branch.size() > 3 && genSubBranches) {
			BlockPos end = branch.get(3);
			switch (dir) {
				case 0:
					branch.addAll(this.generateSideBranchPositions(rand, end, 1, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 7, 6 + rand.nextInt(5)));
					break;
				case 1:
					branch.addAll(this.generateSideBranchPositions(rand, end, 0, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 2, 6 + rand.nextInt(5)));
					break;
				case 2:
					branch.addAll(this.generateSideBranchPositions(rand, end, 1, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 3, 6 + rand.nextInt(5)));
					break;
				case 3:
					branch.addAll(this.generateSideBranchPositions(rand, end, 2, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 4, 6 + rand.nextInt(5)));
					break;
				case 4:
					branch.addAll(this.generateSideBranchPositions(rand, end, 3, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 5, 6 + rand.nextInt(5)));
					break;
				case 5:
					branch.addAll(this.generateSideBranchPositions(rand, end, 4, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 6, 6 + rand.nextInt(5)));
					break;
				case 6:
					branch.addAll(this.generateSideBranchPositions(rand, end, 5, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 7, 6 + rand.nextInt(5)));
					break;
				case 7:
					branch.addAll(this.generateSideBranchPositions(rand, end, 6, 6 + rand.nextInt(5)));
					branch.addAll(this.generateSideBranchPositions(rand, end, 1, 6 + rand.nextInt(5)));
					break;
			}
		}
		for (BlockPos pos : branch) {
			this.setBlock(level, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return branch;
	}

	protected List<BlockPos> generateSideBranchPositions(RandomSource rand, BlockPos start, int dir, int length) {
		return this.generateBranchPositions(rand, start, dir, length, 0.3D, 0.6D, (i, remainingBlocks) -> i < remainingBlocks / 2 ? 1 : (i >= remainingBlocks - 1 && rand.nextInt(2) == 0 ? -1 : 0), (i, len) -> true);
	}

	protected List<BlockPos> generateTopBranch(WorldGenLevel level, RandomSource rand, BlockPos start, int dir, int length, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> branch = this.generateBranchPositions(rand, start, dir, length, 0.1D, 0.2D, (i, remainingBlocks) -> i < remainingBlocks - 1 ? 1 : 0, (i, len) -> i >= len - 1);
		for (BlockPos pos : branch) {
			this.setBlock(level, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return branch;
	}

	public List<BlockPos> generateBranchPositions(RandomSource rand, BlockPos start, int dir, int length, double defaultCurveWeight, double directedCurveWeight,
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

	protected void generateBranchLeaves(WorldGenLevel level, RandomSource rand, BlockPos start, List<BlockPos> branchBlocks, int trunkX, int trunkY, int trunkZ) {
		for (BlockPos branchBlock : branchBlocks) {
			int dist = (int) Math.sqrt(branchBlock.distToCenterSqr(start.getX(), start.getY(), start.getZ()));
			if (dist >= 2) {
				for (Direction side : LEAVES_OFFSETS) {
					int leavesLength = 3 + (rand.nextInt(5) == 0 ? rand.nextInt(dist + 1) : rand.nextInt(dist / 2 + 1));
					for (int yo = 0; yo > -leavesLength; yo--) {
						BlockPos leafPos = branchBlock.relative(side).offset(0, yo, 0);
						BlockState state;
						if (yo == 0) {
							state = this.leavesTop;
						} else {
							state = this.leavesMiddle;
						}
						if (level.isEmptyBlock(leafPos)) {
							if (yo == -leavesLength + 1 || (yo < -1 && !level.isEmptyBlock(leafPos.below()))) {
								state = this.leavesBottom;
								this.setBlock(level, leafPos, state, true, trunkX, trunkY, trunkZ);
								break;
							} else {
								this.setBlock(level, leafPos, state, true, trunkX, trunkY, trunkZ);
							}
						} else {
							break;
						}
					}
				}
			}
		}
	}

	public void trySpawnFace(WorldGenLevel level, AbstractSpiritTreeFace face, List<BlockPos> locations) {
		BlockPos faceAnchor = null;
		Direction faceFacing = null;

		List<BlockPos> largeFacePositions = new ArrayList<>(locations);
		Collections.shuffle(largeFacePositions);
		largeFaceLoop:
		for (BlockPos anchor : largeFacePositions) {
			List<Direction> facings = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
			Collections.shuffle(facings);
			for (Direction facing : facings) {
				if (face.checkAnchorAt(anchor, facing, Direction.UP, AbstractWallCreature.AnchorChecks.ALL) == 0) {
					faceAnchor = anchor;
					faceFacing = facing;
					break largeFaceLoop;
				}
			}
		}

		if (faceAnchor != null && faceFacing != null) {
			EventHooks.finalizeMobSpawn(face, level.getLevel(), level.getCurrentDifficultyAt(faceAnchor), MobSpawnType.STRUCTURE, null);
			face.setPositionToAnchor(faceAnchor, faceFacing, Direction.UP);
			level.addFreshEntity(face);
		}
	}

	protected void setBlock(WorldGenLevel level, BlockPos pos, BlockState state, boolean registerSmallFacePositions, int trunkX, int trunkY, int trunkZ) {
		this.setBlock(level, pos, state);

		if (this.guard != null) {
			this.guard.setGuarded(level, pos, true);
		}

		if (!(pos.getX() >= trunkX && pos.getZ() >= trunkZ && pos.getX() <= trunkX + 1 && pos.getZ() <= trunkZ + 1) && registerSmallFacePositions && this.location != null && state.is(BlockRegistry.SPIRIT_TREE_BARK)) {
			this.location.addSmallFacePosition(pos);
		}
	}
}
