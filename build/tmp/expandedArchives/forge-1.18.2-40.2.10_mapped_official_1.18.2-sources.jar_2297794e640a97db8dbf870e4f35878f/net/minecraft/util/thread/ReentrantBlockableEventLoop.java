package net.minecraft.util.thread;

public abstract class ReentrantBlockableEventLoop<R extends Runnable> extends BlockableEventLoop<R> {
   private int reentrantCount;

   public ReentrantBlockableEventLoop(String p_18765_) {
      super(p_18765_);
   }

   public boolean scheduleExecutables() {
      return this.runningTask() || super.scheduleExecutables();
   }

   protected boolean runningTask() {
      return this.reentrantCount != 0;
   }

   public void doRunTask(R p_18769_) {
      ++this.reentrantCount;

      try {
         super.doRunTask(p_18769_);
      } finally {
         --this.reentrantCount;
      }

   }
}