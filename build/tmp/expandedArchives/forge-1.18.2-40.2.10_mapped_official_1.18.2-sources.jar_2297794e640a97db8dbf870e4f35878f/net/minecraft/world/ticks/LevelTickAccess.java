package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public interface LevelTickAccess<T> extends TickAccess<T> {
   boolean willTickThisTick(BlockPos p_193197_, T p_193198_);
}