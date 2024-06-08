package thebetweenlands.common.generation.trees;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.blocks.BetweenlandsLogBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class NibbletwigTree {
	// Using WorldGenLevel
	public static boolean generate(WorldGenLevel level, BlockPos blockpos, Random random) {
		
		int height = random.nextInt(2) + 16;
		
		if (!level.isAreaLoaded(blockpos, 2)) {
			return false;
		}
		
		// Verify clear
		for (int x = -1; x <= 1; x++) {
			for (int y = 0; y <= height; y++) {
				for (int z = -1; z <= 1; z++) {
					if (!level.getBlockState(new BlockPos(blockpos.getX() + x, blockpos.getY() + y, blockpos.getZ() + z)).is(Blocks.AIR)) {
						return false;
					}
				}
			}
		}
		
		// Roots
		level.setBlock(blockpos.north(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState(), 0);
		level.setBlock(blockpos.south(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState(), 0);
		level.setBlock(blockpos.east(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState(), 0);
		level.setBlock(blockpos.west(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState(), 0);
		
		// Trunk
		for (int y = 0; y < height; y++) {
			level.setBlock(blockpos.above(y), BlockRegistry.SAP_LOG.get().defaultBlockState(), 0);
		}
		
		// Leaves cover
		level.setBlock(blockpos.above(height), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
		level.setBlock(blockpos.north().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
		level.setBlock(blockpos.south().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
		level.setBlock(blockpos.east().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
		level.setBlock(blockpos.west().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
		
		// Branches
		for (int cluster = 1; cluster < 4; cluster ++) {
			boolean angled = random.nextBoolean();
			for (int angmod = 0; angmod < 4; angmod++) {
				int y = (int) ((height * 0.25) * cluster) - random.nextInt(2);
				int lengthmax = 4;
				if (cluster == 3) {
					lengthmax = 3;
				}
				
				
				for (int length = 1; length < lengthmax; length++) {
					int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 90)) * (length)));
					int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 90)) * (length)));
					
					if (angled) {
						if (angmod == 0) {
							x = x + length;
						}
						else if (angmod == 1) {
							z = z - length;
						}
						else if (angmod == 2) {
							x = x - length;
						}
						else if (angmod == 3) {
							z = z + length;
						}
					}
					
					BlockPos pos = blockpos.north(z).east(x).above(y);
					BlockState topstate = level.getBlockState(pos);
					
					// Checks to ownly break soft blocks
					if (topstate.getDestroySpeed(level, pos) <= 0.5) {
						if (length == 1) {
							if (angmod == 0 || angmod == 2) {
								level.setBlock(pos, BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BetweenlandsLogBlock.AXIS, Axis.Z), 0);
							}
							else {
								level.setBlock(pos, BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BetweenlandsLogBlock.AXIS, Axis.X), 0);
							}
						}
						else {
							level.setBlock(pos, BlockRegistry.SAP_LOG.get().defaultBlockState(), 0);
						}
					}
					
					// Leaves
					if (length == lengthmax-1) {
						if (level.getBlockState(pos.below()).getDestroySpeed(level, pos.below()) <= 0.5) {
							level.setBlock(pos.below(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
						}
						if (level.getBlockState(pos.north()).getDestroySpeed(level, pos.north()) <= 0.5) {
							level.setBlock(pos.north(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
						}
						if (level.getBlockState(pos.south()).getDestroySpeed(level, pos.south()) <= 0.5) {
							level.setBlock(pos.south(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
						}
						if (level.getBlockState(pos.east()).getDestroySpeed(level, pos.east()) <= 0.5) {
							level.setBlock(pos.east(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
						}
						if (level.getBlockState(pos.west()).getDestroySpeed(level, pos.west()) <= 0.5) {
							level.setBlock(pos.west(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState(), 0);
						}
					}
					y++;
				}
			}
		}
		
		return true;
	}
	
	// Using LevelAccessor
	public static boolean generate(Level level, BlockPos blockpos, Random random) {

		int height = random.nextInt(2) + 16;

		if (!level.isAreaLoaded(blockpos, 2)) {
			return false;
		}

		// Verify clear
		for (int x = -1; x <= 1; x++) {
			for (int y = 0; y <= height; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						// uhhh how did i forget it chekcing itself
					}
					else if (level.getBlockState(new BlockPos(blockpos.getX() + x, blockpos.getY() + y, blockpos.getZ() + z)) != Blocks.AIR.defaultBlockState()) {
						return false;
					}
				}
			}
		}

		// Roots
		level.setBlockAndUpdate(blockpos.north(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.south(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.east(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.west(), BlockRegistry.SAP_BARK_LOG.get().defaultBlockState());

		// Trunk
		for (int y = 0; y < height; y++) {
			level.setBlockAndUpdate(blockpos.above(y), BlockRegistry.SAP_LOG.get().defaultBlockState());
		}

		// Leaves cover
		level.setBlockAndUpdate(blockpos.above(height), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.north().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.south().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.east().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
		level.setBlockAndUpdate(blockpos.west().above(height-1), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());

		// Branches
		for (int cluster = 1; cluster < 4; cluster ++) {
			boolean angled = random.nextBoolean();
			for (int angmod = 0; angmod < 4; angmod++) {
				int y = (int) ((height * 0.25) * cluster) - random.nextInt(2);
				int lengthmax = 4;
				if (cluster == 3) {
					lengthmax = 3;
				}


				for (int length = 1; length < lengthmax; length++) {
					int x = (int) Math.round((Math.sin(Math.toRadians((float)angmod * 90)) * (length)));
					int z = (int) Math.round((Math.cos(Math.toRadians((float)angmod * 90)) * (length)));

					if (angled) {
						if (angmod == 0) {
							x = x + length;
						}
						else if (angmod == 1) {
							z = z - length;
						}
						else if (angmod == 2) {
							x = x - length;
						}
						else if (angmod == 3) {
							z = z + length;
						}
					}

					BlockPos pos = blockpos.north(z).east(x).above(y);
					BlockState topstate = level.getBlockState(pos);

					// Checks to ownly break soft blocks
					if (topstate.getDestroySpeed(level, pos) <= 0.5) {
						if (length == 1) {
							if (angmod == 0 || angmod == 2) {
								level.setBlockAndUpdate(pos, BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BetweenlandsLogBlock.AXIS, Axis.Z));
							}
							else {
								level.setBlockAndUpdate(pos, BlockRegistry.SAP_LOG.get().defaultBlockState().setValue(BetweenlandsLogBlock.AXIS, Axis.X));
							}
						}
						else {
							level.setBlockAndUpdate(pos, BlockRegistry.SAP_LOG.get().defaultBlockState());
						}
					}

					// Leaves
					if (length == lengthmax-1) {
						if (level.getBlockState(pos.below()).getDestroySpeed(level, pos.below()) <= 0.5) {
							level.setBlockAndUpdate(pos.below(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
						}
						if (level.getBlockState(pos.north()).getDestroySpeed(level, pos.north()) <= 0.5) {
							level.setBlockAndUpdate(pos.north(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
						}
						if (level.getBlockState(pos.south()).getDestroySpeed(level, pos.south()) <= 0.5) {
							level.setBlockAndUpdate(pos.south(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
						}
						if (level.getBlockState(pos.east()).getDestroySpeed(level, pos.east()) <= 0.5) {
							level.setBlockAndUpdate(pos.east(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
						}
						if (level.getBlockState(pos.west()).getDestroySpeed(level, pos.west()) <= 0.5) {
							level.setBlockAndUpdate(pos.west(), BlockRegistry.LEAVES_SAP_TREE.get().defaultBlockState());
						}
					}
					y++;
				}
			}
		}

		return true;
	}
}
