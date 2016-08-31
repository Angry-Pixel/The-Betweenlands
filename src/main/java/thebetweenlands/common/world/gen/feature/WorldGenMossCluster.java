package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockMoss;

public class WorldGenMossCluster extends WorldGenerator {
	private final IBlockState blockState;
	private final Block block;
	private final int offset;
	private final int attempts;

	public WorldGenMossCluster(IBlockState blockState, int offset, int attempts) {
		this.blockState = blockState;
		this.block = blockState.getBlock();
		this.offset = offset;
		this.attempts = attempts;
	}

	public WorldGenMossCluster(IBlockState blockState) {
		this(blockState, 8, 256);
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = false;

		for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
			position = position.down();
		}

		for (int i = 0; i < this.attempts; ++i) {
			BlockPos pos = position.add(rand.nextInt(this.offset) - rand.nextInt(this.offset), rand.nextInt(this.offset/2+1) - rand.nextInt(this.offset/2+1), rand.nextInt(this.offset) - rand.nextInt(this.offset));

			if (worldIn.isAirBlock(pos) && this.block.canPlaceBlockAt(worldIn, pos)) {
				EnumFacing facing = EnumFacing.getFront(rand.nextInt(EnumFacing.VALUES.length));
				EnumFacing.Axis axis = facing.getAxis();
				EnumFacing oppositeFacing = facing.getOpposite();
				boolean isInvalid = false;
				if (axis.isHorizontal() && !worldIn.isSideSolid(pos.offset(oppositeFacing), facing, true)) {
					isInvalid = true;
				} else if (axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(oppositeFacing))) {
					isInvalid = true;
				}
				if (!isInvalid) {
					IBlockState state = this.blockState.withProperty(BlockMoss.FACING, facing);
					this.setBlockAndNotifyAdequately(worldIn, pos, state);
					generated = true;
				}
			}
		}

		return generated;
	}

	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.isSideSolid(worldIn, pos, EnumFacing.UP)) {
			return true;
		} else {
			return false;
		}
	}
}