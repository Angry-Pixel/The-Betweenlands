package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface GameEventListener {
   PositionSource getListenerSource();

   int getListenerRadius();

   boolean handleGameEvent(Level p_157846_, GameEvent p_157847_, @Nullable Entity p_157848_, BlockPos p_157849_);
}