package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockEntityTicker<T extends BlockEntity> {
   void tick(Level p_155253_, BlockPos p_155254_, BlockState p_155255_, T p_155256_);
}