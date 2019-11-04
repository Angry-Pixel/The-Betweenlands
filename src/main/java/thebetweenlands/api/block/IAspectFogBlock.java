package thebetweenlands.api.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.api.aspect.IAspectType;

public interface IAspectFogBlock {
	@Nullable
	public IAspectType getAspectFogType(IBlockAccess world, BlockPos pos, IBlockState state);
}
