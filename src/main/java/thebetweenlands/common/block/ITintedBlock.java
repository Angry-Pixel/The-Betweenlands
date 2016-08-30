package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ITintedBlock {
	int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex);
}
