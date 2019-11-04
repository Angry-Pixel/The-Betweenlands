package thebetweenlands.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IDungeonFogBlock {
	public boolean isCreatingDungeonFog(IBlockAccess world, BlockPos pos, IBlockState state);
}
