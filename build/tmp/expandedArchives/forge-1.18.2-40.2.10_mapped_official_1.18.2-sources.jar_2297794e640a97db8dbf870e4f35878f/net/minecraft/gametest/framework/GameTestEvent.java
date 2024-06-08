package net.minecraft.gametest.framework;

import javax.annotation.Nullable;

class GameTestEvent {
   @Nullable
   public final Long expectedDelay;
   public final Runnable assertion;

   private GameTestEvent(@Nullable Long p_177092_, Runnable p_177093_) {
      this.expectedDelay = p_177092_;
      this.assertion = p_177093_;
   }

   static GameTestEvent create(Runnable p_177098_) {
      return new GameTestEvent((Long)null, p_177098_);
   }

   static GameTestEvent create(long p_177095_, Runnable p_177096_) {
      return new GameTestEvent(p_177095_, p_177096_);
   }
}