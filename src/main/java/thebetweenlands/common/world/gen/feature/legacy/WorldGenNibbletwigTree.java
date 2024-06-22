package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.blocks.BlockPoisonIvy;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenNibbletwigTree extends WorldGenHelper {

	Direction[] HORIZONTALS = {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};

	@Override
	public boolean generate(Level world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		double foldedNormalDist = (Math.abs(rand.nextGaussian()) + 0.2D) / 3.0D;
		int height = (int) (Math.pow((1 - Math.min(foldedNormalDist, 1)), 1.5D) * 10) + 5;

		if (!this.rotatedCubeCantReplace(world, x, y + 2, z, -2, 0, -2, 5, height, 5, 0)) {
			int bend1 = rand.nextInt(3) + 6;
			int bend2 = rand.nextInt(2) == 0 ? rand.nextInt(3) + 10 : -1;

			int canopy1 = rand.nextInt(3) + 3;

			BlockState log = BlockRegistry.LOG_NIBBLETWIG.get().defaultBlockState();//.withProperty(BlockLogBetweenlands.LOG_AXIS, BlockLog.EnumAxis.NONE);
			BlockState leaves = BlockRegistry.LEAVES_NIBBLETWIG_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
			BlockState ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

			int xo[] = new int[height + 1];
			int zo[] = new int[height + 1];
			for (int i = 0; i <= height; i++) {
				int bxo = i == 0 ? 0 : xo[i - 1];
				int bzo = i == 0 ? 0 : zo[i - 1];
				if (i == bend1 || i == bend2) {
					Direction randOffset = HORIZONTALS[rand.nextInt(HORIZONTALS.length)];
					bxo += randOffset.getStepX();
					bzo += randOffset.getStepZ();
				}
				xo[i] = bxo;
				zo[i] = bzo;
				this.setBlockAndNotifyAdequately(world, pos.offset(bxo, i, bzo), log);
			}

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[canopy1] + x, y + canopy1, zo[canopy1] + z, -1, 0, -1, leaves, 3, 1, 3, 0);
			for (Direction offset : HORIZONTALS) {
				BlockPos offsetPos = pos.offset(xo[canopy1] + offset.getStepX(), canopy1 + 1, zo[canopy1] + offset.getStepZ());
				if (world.getBlockState(offsetPos).getBlock() == Blocks.AIR) {
					this.setBlockAndNotifyAdequately(world, offsetPos, leaves);
				}

				BlockPos droopPos = pos.offset(xo[canopy1] + offset.getStepX() * 2, canopy1, zo[canopy1] + offset.getStepZ() * 2);
				int droopLength = rand.nextInt(2) + 2;
				for (int yo = 0; yo < droopLength; yo++) {
					BlockPos droopPosY = droopPos.below(yo);
					if (world.getBlockState(droopPosY).getBlock() == Blocks.AIR) {
						this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
					}
				}

				if (rand.nextInt(3) == 0) {
					BlockPos ivyPos = pos.offset(xo[canopy1] + offset.getStepX() * 3, canopy1, zo[canopy1] + offset.getStepZ() * 3);
					int ivyLength = rand.nextInt(5) + 3;
					for (int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.below(yo);
						if (world.getBlockState(ivyPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(offset.getOpposite()), true));
						} else {
							break;
						}
					}
				}
			}

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -1, 0, -2, leaves, 3, 2, 5, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -2, 0, -1, leaves, 5, 2, 3, 0);

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, -1, 0, 0, leaves, 3, 1, 1, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, 0, 0, -1, leaves, 1, 1, 3, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 3, zo[height - 1] + z, 0, 0, 0, leaves, 1, 1, 1, 0);

			boolean generatedDroops[] = new boolean[9 * 9];

			int droops = 4 + rand.nextInt(8);
			for (int i = 0; i < droops; i++) {
				int droopXO = rand.nextInt(7) - 3;
				int droopZO = 0;
				if (Math.abs(droopXO) <= 1) {
					droopZO = rand.nextInt(7) - 3;
				} else {
					droopZO = rand.nextInt(3) - 1;
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

					int droopLength = rand.nextInt(3) + 2;
					for (int yo = 0; yo < droopLength; yo++) {
						BlockPos droopPosY = droopPos.below(yo);
						if (world.getBlockState(droopPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
						}
					}

					Direction ivyOffset = HORIZONTALS[rand.nextInt(HORIZONTALS.length)];
					BlockPos ivyPos = pos.offset(xo[height - 1] + droopXO + ivyOffset.getStepX(), height - 1, zo[height - 1] + droopZO + ivyOffset.getStepZ());
					int ivyLength = rand.nextInt(9) + 3;
					for (int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.below(yo);
						if (world.getBlockState(ivyPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(ivyOffset.getOpposite()), true));
						} else {
							break;
						}
					}

					generatedDroops[droopIndex] = true;
				}


			}
			return true;
		}
		return false;
	}

	@Override
	public boolean generate(WorldGenLevel world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		double foldedNormalDist = (Math.abs(rand.nextGaussian()) + 0.2D) / 3.0D;
		int height = (int) (Math.pow((1 - Math.min(foldedNormalDist, 1)), 1.5D) * 10) + 5;

		if (!this.rotatedCubeCantReplace(world, x, y + 2, z, -2, 0, -2, 5, height, 5, 0)) {
			int bend1 = rand.nextInt(3) + 6;
			int bend2 = rand.nextInt(2) == 0 ? rand.nextInt(3) + 10 : -1;

			int canopy1 = rand.nextInt(3) + 3;

			BlockState log = BlockRegistry.LOG_NIBBLETWIG.get().defaultBlockState();//.withProperty(BlockLogBetweenlands.LOG_AXIS, BlockLog.EnumAxis.NONE);
			BlockState leaves = BlockRegistry.LEAVES_NIBBLETWIG_TREE.get().defaultBlockState();//.withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
			BlockState ivy = BlockRegistry.POISON_IVY.get().defaultBlockState();

			int xo[] = new int[height + 1];
			int zo[] = new int[height + 1];
			for (int i = 0; i <= height; i++) {
				int bxo = i == 0 ? 0 : xo[i - 1];
				int bzo = i == 0 ? 0 : zo[i - 1];
				if (i == bend1 || i == bend2) {
					Direction randOffset = HORIZONTALS[rand.nextInt(HORIZONTALS.length)];
					bxo += randOffset.getStepX();
					bzo += randOffset.getStepZ();
				}
				xo[i] = bxo;
				zo[i] = bzo;
				this.setBlockAndNotifyAdequately(world, pos.offset(bxo, i, bzo), log);
			}

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[canopy1] + x, y + canopy1, zo[canopy1] + z, -1, 0, -1, leaves, 3, 1, 3, 0);
			for (Direction offset : HORIZONTALS) {
				BlockPos offsetPos = pos.offset(xo[canopy1] + offset.getStepX(), canopy1 + 1, zo[canopy1] + offset.getStepZ());
				if (world.getBlockState(offsetPos).getBlock() == Blocks.AIR) {
					this.setBlockAndNotifyAdequately(world, offsetPos, leaves);
				}

				BlockPos droopPos = pos.offset(xo[canopy1] + offset.getStepX() * 2, canopy1, zo[canopy1] + offset.getStepZ() * 2);
				int droopLength = rand.nextInt(2) + 2;
				for (int yo = 0; yo < droopLength; yo++) {
					BlockPos droopPosY = droopPos.below(yo);
					if (world.getBlockState(droopPosY).getBlock() == Blocks.AIR) {
						this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
					}
				}

				if (rand.nextInt(3) == 0) {
					BlockPos ivyPos = pos.offset(xo[canopy1] + offset.getStepX() * 3, canopy1, zo[canopy1] + offset.getStepZ() * 3);
					int ivyLength = rand.nextInt(5) + 3;
					for (int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.below(yo);
						if (world.getBlockState(ivyPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(offset.getOpposite()), true));
						} else {
							break;
						}
					}
				}
			}

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -1, 0, -2, leaves, 3, 2, 5, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1, zo[height - 1] + z, -2, 0, -1, leaves, 5, 2, 3, 0);

			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, -1, 0, 0, leaves, 3, 1, 1, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 2, zo[height - 1] + z, 0, 0, -1, leaves, 1, 1, 3, 0);
			this.rotatedCubeVolume(world, setPos -> world.getBlockState(setPos).getBlock() == Blocks.AIR, xo[height - 1] + x, y + height - 1 + 3, zo[height - 1] + z, 0, 0, 0, leaves, 1, 1, 1, 0);

			boolean generatedDroops[] = new boolean[9 * 9];

			int droops = 4 + rand.nextInt(8);
			for (int i = 0; i < droops; i++) {
				int droopXO = rand.nextInt(7) - 3;
				int droopZO = 0;
				if (Math.abs(droopXO) <= 1) {
					droopZO = rand.nextInt(7) - 3;
				} else {
					droopZO = rand.nextInt(3) - 1;
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

					int droopLength = rand.nextInt(3) + 2;
					for (int yo = 0; yo < droopLength; yo++) {
						BlockPos droopPosY = droopPos.below(yo);
						if (world.getBlockState(droopPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
						}
					}

					Direction ivyOffset = HORIZONTALS[rand.nextInt(HORIZONTALS.length)];
					BlockPos ivyPos = pos.offset(xo[height - 1] + droopXO + ivyOffset.getStepX(), height - 1, zo[height - 1] + droopZO + ivyOffset.getStepZ());
					int ivyLength = rand.nextInt(9) + 3;
					for (int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.below(yo);
						if (world.getBlockState(ivyPosY).getBlock() == Blocks.AIR) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.setValue(BlockPoisonIvy.PROPERTY_BY_DIRECTION.get(ivyOffset.getOpposite()), true));
						} else {
							break;
						}
					}

					generatedDroops[droopIndex] = true;
				}


			}
			return true;
		}
		return false;
	}
}
