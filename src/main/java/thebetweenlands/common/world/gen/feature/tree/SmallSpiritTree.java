package thebetweenlands.common.world.gen.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.spirit_tree.SmallTamedSpiritTreeFace;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.*;

public class SmallSpiritTree extends SpiritTree {
	public SmallSpiritTree(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos position = context.origin();
		RandomSource rand = context.random();

		int checkRadius = 4;

		for (int xx = -checkRadius; xx <= checkRadius; xx++) {
			for (int zz = -checkRadius; zz <= checkRadius; zz++) {
				for (int yy = 2; yy < 10; yy++) {
					BlockPos checkPos = position.offset(xx, yy, zz);
					if (!level.isEmptyBlock(checkPos) && level.getBlockState(checkPos).isRedstoneConductor(level, checkPos)) {
						return false;
					}
				}
			}
		}

		int trunkX = position.getX();
		int trunkY = position.getY();
		int trunkZ = position.getZ();

		int height = 5 + rand.nextInt(3);

		List<BlockPos> facePositions = new ArrayList<>();

		for (int yo = 0; yo < height; yo++) {
			BlockPos pos = new BlockPos(trunkX, trunkY + yo, trunkZ);
			this.setBlock(level, pos, this.log, false, trunkX, trunkY, trunkZ);
			facePositions.add(pos);
		}

		Map<List<BlockPos>, BlockPos> branches = new HashMap<>();

		BlockPos sideBranch;

		//Generate 4 main branches in the cardinal directions

		if (rand.nextInt(2) == 0) {
			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(level, rand, sideBranch, 0, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false), sideBranch);

			sideBranch = new BlockPos(trunkX + 1, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(level, rand, sideBranch, 6, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false), sideBranch);
		} else {
			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ);
			branches.put(this.generateSideBranch(level, rand, sideBranch, 2, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false), sideBranch);

			sideBranch = new BlockPos(trunkX, trunkY + height - 2, trunkZ + 1);
			branches.put(this.generateSideBranch(level, rand, sideBranch, 4, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false), sideBranch);
		}

		//Generate 1-2 diagonal branches
		List<Integer> diagonals = new ArrayList<>(Arrays.asList(1, 3, 5, 7));
		int numDiagonals = 1 + rand.nextInt(2);
		for (int i = 0; i < numDiagonals; i++) {
			int dir = diagonals.remove(rand.nextInt(diagonals.size()));
			int[] offset = this.getDirOffset(dir);
			int bx = trunkX + offset[0];
			int bz = trunkZ + offset[1];
			sideBranch = new BlockPos(bx, trunkY + height, bz);
			branches.put(this.generateSideBranch(level, rand, sideBranch, dir, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false), sideBranch);
		}

		//Generate two tall branches at top of trunk
		sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ);
		branches.put(this.generateTopBranch(level, rand, sideBranch, 1, 1 + rand.nextInt(3), trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ + 1);
		branches.put(this.generateTopBranch(level, rand, sideBranch, 5, 1 + rand.nextInt(3), trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX, trunkY + height, trunkZ + 1);
		branches.put(this.generateTopBranch(level, rand, sideBranch, 3, 1 + rand.nextInt(3), trunkX, trunkY, trunkZ), sideBranch);

		sideBranch = new BlockPos(trunkX + 1, trunkY + height, trunkZ);
		branches.put(this.generateTopBranch(level, rand, sideBranch, 7, 1 + rand.nextInt(3), trunkX, trunkY, trunkZ), sideBranch);

		for (Map.Entry<List<BlockPos>, BlockPos> branch : branches.entrySet()) {
			this.generateBranchLeaves(level, rand, branch.getValue(), branch.getKey(), trunkX, trunkY, trunkZ);
		}

		//Generate roots
		List<BlockPos> rootBlocks = new ArrayList<>();

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY - 1, trunkZ);
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 0, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false));

		sideBranch = new BlockPos(trunkX, trunkY - 1, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 2, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false));

		sideBranch = new BlockPos(trunkX + rand.nextInt(2), trunkY - 1, trunkZ + 1);
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 4, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false));

		sideBranch = new BlockPos(trunkX + 1, trunkY - 1, trunkZ + rand.nextInt(2));
		rootBlocks.addAll(this.generateRoot(level, rand, sideBranch, 6, 1 + rand.nextInt(2), trunkX, trunkY, trunkZ, false));

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

		SmallTamedSpiritTreeFace face = new SmallTamedSpiritTreeFace(EntityRegistry.SMALL_TAMED_SPIRIT_TREE_FACE.get(), level.getLevel());
		this.trySpawnFace(level, face, facePositions);

		return true;
	}
}
