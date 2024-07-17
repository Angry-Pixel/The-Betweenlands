package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.AspectType;

import javax.annotation.Nullable;

public interface AspectFogBlock {
	@Nullable
	AspectType getAspectFogType(LevelAccessor levelAccessor, BlockPos pos, BlockState state);
}
