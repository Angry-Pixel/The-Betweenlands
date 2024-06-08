package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;

public interface TickingBlockEntity {
   void tick();

   boolean isRemoved();

   BlockPos getPos();

   String getType();
}