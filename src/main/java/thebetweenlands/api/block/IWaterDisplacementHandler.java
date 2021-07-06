package thebetweenlands.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockSwampWater;

public interface IWaterDisplacementHandler {
	public boolean onWaterDisplacement(World world, BlockPos pos, BlockSwampWater block);
}
