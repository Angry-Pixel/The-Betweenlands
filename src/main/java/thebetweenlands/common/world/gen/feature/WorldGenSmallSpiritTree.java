package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockLeavesBetweenlands;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityTamedSpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityWallFace.AnchorChecks;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenSmallSpiritTree extends WorldGenerator {
	private static final ImmutableList<EnumFacing> LEAVES_OFFSETS;

	static {
		ImmutableList.Builder<EnumFacing> builder = ImmutableList.builder();
		builder.add(EnumFacing.HORIZONTALS);
		builder.add(EnumFacing.UP);
		LEAVES_OFFSETS = builder.build();
	}

	private IBlockState log;
	private IBlockState leavesTop;
	private IBlockState leavesMiddle;
	private IBlockState leavesBottom;
	private IBlockState roots;

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		int checkRadius = 4;

		for (int xx = -checkRadius; xx <= checkRadius; xx++) {
			for (int zz = -checkRadius; zz <= checkRadius; zz++) {
				for (int yy = 2; yy < 10; yy++) {
					if (!world.isAirBlock(position.add(xx, yy, zz)) && world.getBlockState(position.add(xx, yy, zz)).isNormalCube()) {
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

		int height = 5 + rand.nextInt(3);

		List<BlockPos> facePositions = new ArrayList<>();

		for(int yo = 0; yo < height; yo++) {
			BlockPos pos = new BlockPos(trunkX, trunkY + yo, trunkZ);
			this.setBlock(world, pos, this.log, false, trunkX, trunkY, trunkZ);
			facePositions.add(pos);
		}

		Map<List<BlockPos>, BlockPos> branches = new HashMap<>();

		BlockPos sideBranch;

		//Generate 4 main branches in the cardinal directions

		if(rand.nextInt(2) == 0) {
			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(world, rand, sideBranch, 0, trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(world, rand, sideBranch, 6, trunkX, trunkY, trunkZ), sideBranch);
		} else {
			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(world, rand, sideBranch, 2, trunkX, trunkY, trunkZ), sideBranch);

			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ + 1);
			branches.put(this.generateSideBranch(world, rand, sideBranch, 4, trunkX, trunkY, trunkZ), sideBranch);
		}

		//Generate 1-2 diagonal branches
		List<Integer> diagonals = new ArrayList<>(Arrays.asList(1, 3, 5, 7));
		int numDiagonals = 1 + rand.nextInt(2);
		for(int i = 0; i < numDiagonals; i++) {
			int dir = diagonals.remove(rand.nextInt(diagonals.size()));
			int[] offset = this.getDirOffset(dir);
			int bx = trunkX + offset[0];
			int bz = trunkZ + offset[1];
			sideBranch = new BlockPos(bx, trunkY + height, bz);
			branches.put(this.generateSideBranch(world, rand, sideBranch, dir, trunkX, trunkY, trunkZ), sideBranch);
		}

		//Generate two tall branches at top of trunk
		sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ);
		branches.put(this.generateTopBranch(world, rand, sideBranch, 1, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ + 1);
		branches.put(this.generateTopBranch(world, rand, sideBranch, 5, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ + 1);
		branches.put(this.generateTopBranch(world, rand, sideBranch, 3, trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ);
		branches.put(this.generateTopBranch(world, rand, sideBranch, 7, trunkX, trunkY, trunkZ), sideBranch);

		for(Entry<List<BlockPos>, BlockPos> branch : branches.entrySet()) {
			this.generateBranchLeaves(world, rand, branch.getValue(), branch.getKey(), trunkX, trunkY, trunkZ);
		}

		//Generate roots

		List<BlockPos> rootBlocks = new ArrayList<>();

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY - 1, trunkZ);
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 0, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX, trunkY - 1, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 2, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY - 1, trunkZ + 1);
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 4, trunkX, trunkY, trunkZ));

		sideBranch = new BlockPos(trunkX + 1, trunkY - 1, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(world, rand, sideBranch, 6, trunkX, trunkY, trunkZ));

		for(BlockPos rootBlock : rootBlocks) {
			if(rand.nextInt(4) == 0 && new Vec3d(rootBlock.getX() + 0.5D, 0, rootBlock.getZ() + 0.5D).squareDistanceTo(trunkX + 1.0D, 0, trunkZ + 1.0D) >= 9 && world.isAirBlock(rootBlock.up())) {
				int rootHeight = 1 + rand.nextInt(3);
				for(int yo = 0; yo < rootHeight; yo++) {
					BlockPos pos = rootBlock.up(1 + yo);
					if(world.isAirBlock(pos)) {
						this.setBlock(world, pos, this.roots, true, trunkX, trunkY, trunkZ);
					} else {
						break;
					}
				}
			}
		}

		EntityTamedSpiritTreeFace face = new EntityTamedSpiritTreeFace(world);
		this.trySpawnFace(world, rand, face, facePositions);

		return true;
	}

	private void trySpawnFace(World world, Random rand, EntitySpiritTreeFace face, List<BlockPos> locations) {
		BlockPos faceAnchor = null;
		EnumFacing faceFacing = null;

		List<BlockPos> randFacePositions = new ArrayList<>();
		randFacePositions.addAll(locations);
		Collections.shuffle(randFacePositions, rand);
		largeFaceLoop: for(BlockPos anchor : randFacePositions) {
			List<EnumFacing> facings = new ArrayList<>();
			facings.addAll(Arrays.asList(EnumFacing.HORIZONTALS));
			Collections.shuffle(facings, rand);
			for(EnumFacing facing : facings) {
				if(face.checkAnchorAt(anchor, facing, EnumFacing.UP, AnchorChecks.ALL) == 0) {
					faceAnchor = anchor;
					faceFacing = facing;
					break largeFaceLoop;
				}
			}
		}

		if(faceAnchor != null && faceFacing != null) {
			face.onInitialSpawn(world.getDifficultyForLocation(faceAnchor), null);
			face.setPositionToAnchor(faceAnchor, faceFacing, EnumFacing.UP);
			world.spawnEntity(face);
		}
	}

	private int[] getDirOffset(int dir) {
		int[] offset = new int[2];
		switch(dir) {
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

	private List<BlockPos> generateRoot(World world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> root = this.generateBranchPositions(rand, start, dir, 1 + rand.nextInt(2), 0.9D, 0.8D, (i, remainingBlocks) -> i < 2 ? 0 : rand.nextInt((i - 2) * 2 + 2) == 0 ? 0 : -1, (i, length) -> true);
		for(BlockPos pos : root) {
			BlockPos rel = pos.subtract(new BlockPos(trunkX, trunkY, trunkZ));
			if(Math.abs(rel.getX()) + Math.abs(rel.getZ()) == 1) {
				root.add(root.indexOf(pos), pos.up());
				break;
			}
		}
		for(BlockPos pos : root) {
			this.setBlock(world, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return root;
	}

	private List<BlockPos> generateSideBranch(World world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> branch = this.generateSideBranchPositions(rand, start, dir);
		for(BlockPos pos : branch) {
			this.setBlock(world, pos, this.log, true, trunkX, trunkY, trunkZ);
		}
		return branch;
	}

	private List<BlockPos> generateSideBranchPositions(Random rand, BlockPos start, int dir) {
		return this.generateBranchPositions(rand, start, dir, 1 + rand.nextInt(2), 0.3D, 0.6D, (i, remainingBlocks) -> i < remainingBlocks / 2 ? 1 : (i >= remainingBlocks - 1 && rand.nextInt(2) == 0 ? -1  : 0), (i, length) -> true);
	}

	private List<BlockPos> generateTopBranch(World world, Random rand, BlockPos start, int dir, int trunkX, int trunkY, int trunkZ) {
		List<BlockPos> branch = this.generateBranchPositions(rand, start, dir, 1 + rand.nextInt(3), 0.1D, 0.2D, (i, remainingBlocks) -> i < remainingBlocks - 1 ? 1 : 0, (i, length) -> i >= length - 1);
		for(BlockPos pos : branch) {
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

		if(offset[0] != 0) {
			rx = offset[0] * directedCurveWeight;
		}

		if(offset[1] != 0) {
			rz = offset[1] * directedCurveWeight;
		}

		branchBlocks.add(branch);

		for(int i = 0; i < remainingBlocks; i++) {
			int xo = rand.nextDouble() < Math.abs(rx) ? (int)Math.signum(rx) : 0;
			int zo = rand.nextDouble() < Math.abs(rz) ? (int)Math.signum(rz) : 0;
			if(zo == 0 && xo == 0 && forceMoveFunction.apply(i, remainingBlocks)) {
				if(rand.nextDouble() * Math.abs(rx) > rand.nextDouble() * Math.abs(rz)) {
					xo = (int)Math.signum(rx);
				} else {
					zo = (int)Math.signum(rz);
				}
			}
			if(Math.abs(xo) == Math.abs(zo) && xo != 0) {
				remainingBlocks -= 0.414D; //sqrt(2)-1
			}
			branch = branch.add(xo, heightFunction.apply(i, remainingBlocks), zo);

			branchBlocks.add(branch);
		}

		return branchBlocks;
	}

	private void generateBranchLeaves(World world, Random rand, BlockPos start, List<BlockPos> branchBlocks, int trunkX, int trunkY, int trunkZ) {
		for(BlockPos branchBlock : branchBlocks) {
			int dist = (int) branchBlock.getDistance(start.getX(), start.getY(), start.getZ());
			if(dist >= 1) {
				for(EnumFacing side : LEAVES_OFFSETS) {
					int leavesLength = 3 + (rand.nextInt(5) == 0 ? rand.nextInt(dist + 1) : rand.nextInt(dist / 2 + 1));
					for(int yo = 0; yo > -leavesLength; yo--) {
						BlockPos leafPos = branchBlock.offset(side).add(0, yo, 0);
						IBlockState state;
						if(yo == 0) {
							state = this.leavesTop;
						} else {
							state = this.leavesMiddle;
						}
						if(world.isAirBlock(leafPos)) {
							if(yo == -leavesLength + 1 || (yo < -1 && !world.isAirBlock(leafPos.down()))) {
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

	protected void setBlock(World world, BlockPos pos, IBlockState state, boolean registerSmallFacePositions, int trunkX, int trunkY, int trunkZ) {
		this.setBlockAndNotifyAdequately(world, pos, state);
	}
}
