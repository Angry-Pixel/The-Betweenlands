package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.farming.BlockBarnacle_1_2;

public class WorldGenBarnacleCluster extends WorldGenerator {
	private final IBlockState blockState;
	private final Block block;
	private final int offset;
	private final int attempts;

	public WorldGenBarnacleCluster(IBlockState blockState, int offset, int attempts) {
		this.blockState = blockState;
		this.block = blockState.getBlock();
		this.offset = offset;
		this.attempts = attempts;
	}

	public WorldGenBarnacleCluster(IBlockState blockState) {
		this(blockState, 8, 256);
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = false;

		for (IBlockState iblockstate = worldIn.getBlockState(position);
				(iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
			position = position.down();
		}

		PooledMutableBlockPos mutablePos = PooledMutableBlockPos.retain();
		for (int i = 0; i < this.attempts; ++i) {
			mutablePos.setPos(position.getX() + rand.nextInt(this.offset) - rand.nextInt(this.offset), position.getY() + rand.nextInt(this.offset/2+1) - rand.nextInt(this.offset/2+1), position.getZ() + rand.nextInt(this.offset) - rand.nextInt(this.offset));

			if (worldIn.isAreaLoaded(mutablePos, 1) && worldIn.getBlockState(mutablePos).getMaterial() == Material.WATER && this.block.canPlaceBlockAt(worldIn, mutablePos)) {
				EnumFacing facing = EnumFacing.byIndex(rand.nextInt(EnumFacing.VALUES.length));
				EnumFacing.Axis axis = facing.getAxis();
				EnumFacing oppositeFacing = facing.getOpposite();
				boolean isInvalid = false;
				if (axis.isHorizontal() && !worldIn.isSideSolid(mutablePos.offset(oppositeFacing), facing, true)) {
					isInvalid = true;
				} else if (axis.isVertical() && !this.canPlaceOn(worldIn, mutablePos.offset(oppositeFacing))) {
					isInvalid = true;
				}
				if (!isInvalid) {
					IBlockState state = this.blockState.withProperty(BlockBarnacle_1_2.BARNACLE_TYPE_EARLY, BlockBarnacle_1_2.getTypeFromFacing(oppositeFacing));
					this.setBlockAndNotifyAdequately(worldIn, mutablePos.toImmutable(), state);
					generated = true;
				}
			}
		}
		mutablePos.release();

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