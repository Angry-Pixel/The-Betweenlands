package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface DungeonFogBlock {
	boolean isCreatingDungeonFog(LevelAccessor levelAccessor, BlockPos pos, BlockState state);
}
