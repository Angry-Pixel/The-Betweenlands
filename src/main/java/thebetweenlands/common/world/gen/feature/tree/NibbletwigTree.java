package thebetweenlands.common.world.gen.feature.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thebetweenlands.common.registries.BlockRegistry;

public class NibbletwigTree {

	public boolean generate(LevelAccessor accessor, BlockPos pos, RandomSource random) {

		double foldedNormalDist = (Math.abs(random.nextGaussian()) + 0.2D) / 3.0D;
		int height = (int) (Math.pow((1 - Math.min(foldedNormalDist, 1)), 1.5D) * 10) + 5;

		for (int x = -2; x <= 2; x++) {
			for (int y = 2; y <= height; y++) {
				for (int z = -2; z <= 2; z++) {
					if (!accessor.getBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z)).isAir()) {
						return false;
					}
				}
			}
		}


		int bend1 = random.nextInt(3) + 6;
		int bend2 = random.nextInt(2) == 0 ? random.nextInt(3) + 10 : -1;

		int canopy1 = random.nextInt(3) + 3;

		BlockState log = BlockRegistry.NIBBLETWIG_LOG.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
		BlockState leaves = BlockRegistry.NIBBLETWIG_LEAVES.get().defaultBlockState();
		BlockState ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

		int[] xo = new int[height + 1];
		int[] zo = new int[height + 1];
		for (int i = 0; i <= height; i++) {
			int bxo = i == 0 ? 0 : xo[i - 1];
			int bzo = i == 0 ? 0 : zo[i - 1];
			if (i == bend1 || i == bend2) {
				Direction randOffset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
				bxo += randOffset.getStepX();
				bzo += randOffset.getStepZ();
			}
			xo[i] = bxo;
			zo[i] = bzo;
			accessor.setBlock(pos.offset(bxo, i, bzo), log, 2);
		}

		//this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[canopy1] + x, y + canopy1, zo[canopy1] + z, -1, 0, -1, leaves, 3, 1, 3, 0);
		for (Direction offset : Direction.Plane.HORIZONTAL) {
			BlockPos offsetPos = pos.offset(xo[canopy1] + offset.getStepX(), canopy1 + 1, zo[canopy1] + offset.getStepZ());
			if (accessor.isEmptyBlock(offsetPos)) {
				accessor.setBlock(offsetPos, leaves, 2);
			}

			BlockPos droopPos = pos.offset(xo[canopy1] + offset.getStepX() * 2, canopy1, zo[canopy1] + offset.getStepZ() * 2);
			int droopLength = random.nextInt(2) + 2;
			for (int yo = 0; yo < droopLength; yo++) {
				BlockPos droopPosY = droopPos.below(yo);
				if (accessor.isEmptyBlock(droopPosY)) {
					accessor.setBlock(droopPosY, leaves, 2);
				}
			}

			if (random.nextInt(3) == 0) {
				BlockPos ivyPos = pos.offset(xo[canopy1] + offset.getStepX() * 3, canopy1, zo[canopy1] + offset.getStepZ() * 3);
				int ivyLength = random.nextInt(5) + 3;
				for (int yo = 0; yo < ivyLength; yo++) {
					BlockPos ivyPosY = ivyPos.below(yo);
					if (accessor.isEmptyBlock(ivyPosY)) {
						accessor.setBlock(ivyPosY, ivy.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(offset.getOpposite()), true), 2);
					} else {
						break;
					}
				}
			}
		}

//		this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -1, 0, -2, leaves, 3, 2, 5, 0);
//		this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -2, 0, -1, leaves, 5, 2, 3, 0);
//
//		this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, -1, 0, 0, leaves, 3, 1, 1, 0);
//		this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, 0, 0, -1, leaves, 1, 1, 3, 0);
//		this.rotatedCubeVolume(accessor, setPos -> accessor.isEmptyBlock(setPos), xo[height - 1] + x, y + height - 1 + 3, zo[height - 1] + z, 0, 0, 0, leaves, 1, 1, 1, 0);

		boolean[] generatedDroops = new boolean[9 * 9];

		int droops = 4 + random.nextInt(8);
		for (int i = 0; i < droops; i++) {
			int droopXO = random.nextInt(7) - 3;
			int droopZO;
			if (Math.abs(droopXO) <= 1) {
				droopZO = random.nextInt(7) - 3;
			} else {
				droopZO = random.nextInt(3) - 1;
			}

			int droopIndex = droopXO + 3 + 1 + (droopZO + 3 + 1) * 9;
			int droopIndexN = droopIndex - 9;
			int droopIndexS = droopIndex + 9;
			int droopIndexE = droopIndex + 1;
			int droopIndexW = droopIndex - 1;

			if (Math.abs(droopXO) + Math.abs(droopZO) > 1 && !generatedDroops[droopIndex] &&
				!generatedDroops[droopIndexN] && !generatedDroops[droopIndexS] &&
				!generatedDroops[droopIndexE] && !generatedDroops[droopIndexW]) {
				BlockPos droopPos = pos.offset(xo[height - 1] + droopXO, height - 1, zo[height - 1] + droopZO);

				int droopLength = random.nextInt(3) + 2;
				for (int yo = 0; yo < droopLength; yo++) {
					BlockPos droopPosY = droopPos.below(yo);
					if (accessor.isEmptyBlock(droopPosY)) {
						accessor.setBlock(droopPosY, leaves, 2);
					}
				}

				Direction ivyOffset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
				BlockPos ivyPos = pos.offset(xo[height - 1] + droopXO + ivyOffset.getStepX(), height - 1, zo[height - 1] + droopZO + ivyOffset.getStepZ());
				int ivyLength = random.nextInt(9) + 3;
				for (int yo = 0; yo < ivyLength; yo++) {
					BlockPos ivyPosY = ivyPos.below(yo);
					if (accessor.isEmptyBlock(ivyPosY)) {
						accessor.setBlock(ivyPosY, ivy.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(ivyOffset.getOpposite()), true), 2);
					} else {
						break;
					}
				}

				generatedDroops[droopIndex] = true;
			}
		}
		return true;
	}
}
