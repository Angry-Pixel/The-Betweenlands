package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.registry.AspectType;

import javax.annotation.Nullable;

public interface AspectFogBlock {
	@Nullable
	Holder<AspectType> getAspectFogType(LevelAccessor levelAccessor, BlockPos pos, BlockState state);
}
