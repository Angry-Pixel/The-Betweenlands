package net.minecraft.server;

public class TickTask implements Runnable {
   private final int tick;
   private final Runnable runnable;

   public TickTask(int p_136252_, Runnable p_136253_) {
      this.tick = p_136252_;
      this.runnable = p_136253_;
   }

   public int getTick() {
      return this.tick;
   }

   public void run() {
      this.runnable.run();
   }
}