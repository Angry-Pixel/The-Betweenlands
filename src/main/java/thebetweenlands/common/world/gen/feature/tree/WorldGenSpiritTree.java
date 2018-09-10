package thebetweenlands.common.world.gen.feature.tree;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenSpiritTree extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();

		IBlockState log = BlockRegistry.LOG_SPIRIT_TREE.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);

		int trunkX = x;
		int trunkY = y;
		int trunkZ = z;

		int dirX = 0;
		int dirZ = 0;

		for(int yo = 0; yo < 25; yo++) {
			if(yo >= 4) {
				if((yo - 4) % 3 == 0) {
					dirX = rand.nextInt(3) - 1;
					dirZ = rand.nextInt(3) - 1;
				}
				if(yo % 2 == 0) {
					trunkX += dirX;
				} else {
					trunkZ += dirZ;
				}
				this.generateTrunkCrossSection(world, trunkX, trunkY + yo - 1, trunkZ, log);
			}
			this.generateTrunkCrossSection(world, trunkX, trunkY + yo, trunkZ, log);

			if(yo >= 6) {
				for(int i = 0; i < 2; i++) {
					Vec3d branchDir = new Vec3d((rand.nextDouble() - 0.5D), (rand.nextDouble() * 0.3D + 0.5D) * 2, (rand.nextDouble() - 0.5D));

					int branchStartX = (int)(trunkX + 1.5D + branchDir.normalize().x);
					int branchStartZ = (int)(trunkZ + 1.5D + branchDir.normalize().z);

					this.generateBranch(world, branchStartX, trunkY + yo, branchStartZ, branchDir, log);
				}
			}
		}
		for(int yo = 0; yo < 2; yo++) {
			this.generateThinCrossSection(world, trunkX + 1, trunkY + yo + 25, trunkZ + 1, log);
		}

		return true;
	}

	private void generateTrunkCrossSection(World world, int x, int y, int z, IBlockState log) {
		for(int xo = 0; xo < 4; xo++) {
			for(int zo = 0; zo < 4; zo++) {
				if(xo != 0 && xo != 3 || zo != 0 && zo != 3) {
					this.setBlockAndNotifyAdequately(world, new BlockPos(x + xo, y, z + zo), log);
				}
			}
		}
	}

	private void generateThinCrossSection(World world, int x, int y, int z, IBlockState log) {
		for(int xo = 0; xo < 2; xo++) {
			for(int zo = 0; zo < 2; zo++) {
				this.setBlockAndNotifyAdequately(world, new BlockPos(x + xo, y, z + zo), log);
			}
		}
	}

	private void generateBranch(World world, int x, int y, int z, Vec3d dir, IBlockState log) {
		double branchX = x;
		double branchY = y;
		double branchZ = z;
		BlockPos lastBranchPos = null;
		for(int i = 0; i < 24; i++) {
			BlockPos branchPos = new BlockPos(branchX, branchY, branchZ);
			if(lastBranchPos == null || !branchPos.equals(lastBranchPos)) {
				this.setBlockAndNotifyAdequately(world, branchPos, log);
				this.setBlockAndNotifyAdequately(world, branchPos.up(), log);

				for(int yo = -2; yo <= 2; yo++) {
					for(int xo = -2; xo <= 2; xo++) {
						for(int zo = -2; zo <= 2; zo++) {
							if(Math.sqrt(xo*xo + yo*yo + zo*zo) <= 2) {
								BlockPos leafPos = branchPos.add(xo, yo, zo);
								if(world.isAirBlock(leafPos))
									//this.setBlockAndNotifyAdequately(world, leafPos, BlockRegistry.LEAVES_WEEDWOOD_TREE.getDefaultState());
									this.generateLeaves(world, leafPos);
							}
						}
					}
				}

				lastBranchPos = branchPos;
			}

			Vec3d normal = dir.normalize();
			branchX += normal.x * 0.8D;
			branchY += normal.y * 0.8D;
			branchZ += normal.z * 0.8D;
			dir = new Vec3d(dir.x * 0.9D, dir.y - 0.1, dir.z * 0.9D);
		}
	}

	private void generateLeaves(World world, BlockPos pos) {
		if(world.isAirBlock(pos))
			this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.LEAVES_SPIRIT_TREE_TOP.getDefaultState());
		else return;
		pos = pos.down();
		if(world.isAirBlock(pos))
			this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.LEAVES_SPIRIT_TREE_MIDDLE.getDefaultState());
		else return;
		pos = pos.down();
		if(world.isAirBlock(pos))
			this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.LEAVES_SPIRIT_TREE_BOTTOM.getDefaultState());
	}
}
