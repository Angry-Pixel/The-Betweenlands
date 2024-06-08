package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface GameEventDispatcher {
   GameEventDispatcher NOOP = new GameEventDispatcher() {
      public boolean isEmpty() {
         return true;
      }

      public void register(GameEventListener p_157843_) {
      }

      public void unregister(GameEventListener p_157845_) {
      }

      public void post(GameEvent p_157839_, @Nullable Entity p_157840_, BlockPos p_157841_) {
      }
   };

   boolean isEmpty();

   void register(GameEventListener p_157834_);

   void unregister(GameEventListener p_157835_);

   void post(GameEvent p_157831_, @Nullable Entity p_157832_, BlockPos p_157833_);
}