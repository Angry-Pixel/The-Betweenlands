package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.plant.BlockPoisonIvy;
import thebetweenlands.common.block.terrain.BlockLeavesBetweenlands;
import thebetweenlands.common.block.terrain.BlockLogBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.WorldGenHelper;

public class WorldGenNibbletwigTree extends WorldGenHelper {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		double foldedNormalDist = (Math.abs(rand.nextGaussian()) + 0.2D) / 3.0D;
		int height = (int)(Math.pow((1 - Math.min(foldedNormalDist, 1)), 1.5D) * 10) + 5;

		if(!this.rotatedCubeCantReplace(world, x, y + 2, z, -2, 0, -2, 5, height, 5, 0)) {
			int bend1 = rand.nextInt(3) + 6;
			int bend2 = rand.nextInt(2) == 0 ? rand.nextInt(3) + 10 : -1;

			int canopy1 = rand.nextInt(3) + 3;

			IBlockState log = BlockRegistry.LOG_NIBBLETWIG.getDefaultState().withProperty(BlockLogBetweenlands.LOG_AXIS, BlockLog.EnumAxis.NONE);
			IBlockState leaves = BlockRegistry.LEAVES_NIBBLETWIG_TREE.getDefaultState().withProperty(BlockLeavesBetweenlands.CHECK_DECAY, false);
			IBlockState ivy = BlockRegistry.POISON_IVY.getDefaultState();

			int xo[] = new int[height+1];
			int zo[] = new int[height+1];
			for(int i = 0; i <= height; i++) {
				int bxo = i == 0 ? 0 : xo[i-1];
				int bzo = i == 0 ? 0 : zo[i-1];
				if(i == bend1 || i == bend2) {
					EnumFacing randOffset = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
					bxo += randOffset.getFrontOffsetX();
					bzo += randOffset.getFrontOffsetZ();
				}
				xo[i] = bxo;
				zo[i] = bzo;
				this.setBlockAndNotifyAdequately(world, pos.add(bxo, i, bzo), log);
			}

			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[canopy1] + x, y + canopy1, zo[canopy1] + z, -1, 0, -1, leaves, 3, 1, 3, 0);
			for(EnumFacing offset : EnumFacing.HORIZONTALS) {
				BlockPos offsetPos = pos.add(xo[canopy1] + offset.getFrontOffsetX(), canopy1 + 1, zo[canopy1] + offset.getFrontOffsetZ());
				if(world.isAirBlock(offsetPos)) {
					this.setBlockAndNotifyAdequately(world, offsetPos, leaves);
				}

				BlockPos droopPos = pos.add(xo[canopy1] + offset.getFrontOffsetX()*2, canopy1, zo[canopy1] + offset.getFrontOffsetZ()*2);
				int droopLength = rand.nextInt(2) + 2;
				for(int yo = 0; yo < droopLength; yo++) {
					BlockPos droopPosY = droopPos.down(yo);
					if(world.isAirBlock(droopPosY)) {
						this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
					}
				}

				if(rand.nextInt(3) == 0) {
					BlockPos ivyPos = pos.add(xo[canopy1] + offset.getFrontOffsetX()*3, canopy1, zo[canopy1] + offset.getFrontOffsetZ()*3);
					int ivyLength = rand.nextInt(5) + 3;
					for(int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.down(yo);
						if(world.isAirBlock(ivyPosY)) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.withProperty(BlockPoisonIvy.getPropertyFor(offset.getOpposite()), true));
						} else {
							break;
						}
					}
				}
			}

			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[height-1] + x, y + height-1, zo[height-1] + z, -1, 0, -2, leaves, 3, 2, 5, 0);
			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[height-1] + x, y + height-1, zo[height-1] + z, -2, 0, -1, leaves, 5, 2, 3, 0);

			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[height-1] + x, y + height-1 + 2, zo[height-1] + z, -1, 0, 0, leaves, 3, 1, 1, 0);
			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[height-1] + x, y + height-1 + 2, zo[height-1] + z, 0, 0, -1, leaves, 1, 1, 3, 0);
			this.rotatedCubeVolume(world, setPos -> world.isAirBlock(setPos), xo[height-1] + x, y + height-1 + 3, zo[height-1] + z, 0, 0, 0, leaves, 1, 1, 1, 0);

			boolean generatedDroops[] = new boolean[9*9];

			int droops = 4 + rand.nextInt(8);
			for(int i = 0; i < droops; i++) {
				int droopXO = rand.nextInt(7) - 3;
				int droopZO = 0;
				if(Math.abs(droopXO) <= 1) {
					droopZO = rand.nextInt(7) - 3;
				} else {
					droopZO = rand.nextInt(3) - 1;
				}

				int droopIndex = droopXO + 3 + 1 + (droopZO + 3 + 1) * 9;
				int droopIndexN = droopIndex - 9;
				int droopIndexS = droopIndex + 9;
				int droopIndexE = droopIndex + 1;
				int droopIndexW = droopIndex - 1;

				if(Math.abs(droopXO) + Math.abs(droopZO) > 1 && !generatedDroops[droopIndex] &&
						!generatedDroops[droopIndexN] && !generatedDroops[droopIndexS] &&
						!generatedDroops[droopIndexE] && !generatedDroops[droopIndexW]) {
					BlockPos droopPos = pos.add(xo[height-1] + droopXO, height-1, zo[height-1] + droopZO);

					int droopLength = rand.nextInt(3) + 2;
					for(int yo = 0; yo < droopLength; yo++) {
						BlockPos droopPosY = droopPos.down(yo);
						if(world.isAirBlock(droopPosY)) {
							this.setBlockAndNotifyAdequately(world, droopPosY, leaves);
						}
					}

					EnumFacing ivyOffset = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
					BlockPos ivyPos = pos.add(xo[height-1] + droopXO + ivyOffset.getFrontOffsetX(), height-1, zo[height-1] + droopZO + ivyOffset.getFrontOffsetZ());
					int ivyLength = rand.nextInt(9) + 3;
					for(int yo = 0; yo < ivyLength; yo++) {
						BlockPos ivyPosY = ivyPos.down(yo);
						if(world.isAirBlock(ivyPosY)) {
							this.setBlockAndNotifyAdequately(world, ivyPosY, ivy.withProperty(BlockPoisonIvy.getPropertyFor(ivyOffset.getOpposite()), true));
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
