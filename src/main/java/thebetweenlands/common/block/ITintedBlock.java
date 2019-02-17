package thebetweenlands.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface ITintedBlock {
	int getColorMultiplier(IBlockState state, @Nullable IWorldReader worldIn, @Nullable BlockPos pos, int tintIndex);
}
