package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEngineExecutor extends BlockableEventLoop<Runnable> {
   private Thread thread = this.createThread();
   private volatile boolean shutdown;

   public SoundEngineExecutor() {
      super("Sound executor");
   }

   private Thread createThread() {
      Thread thread = new Thread(this::run);
      thread.setDaemon(true);
      thread.setName("Sound engine");
      thread.start();
      return thread;
   }

   protected Runnable wrapRunnable(Runnable p_120341_) {
      return p_120341_;
   }

   protected boolean shouldRun(Runnable p_120339_) {
      return !this.shutdown;
   }

   protected Thread getRunningThread() {
      return this.thread;
   }

   private void run() {
      while(!this.shutdown) {
         this.managedBlock(() -> {
            return this.shutdown;
         });
      }

   }

   protected void waitForTasks() {
      LockSupport.park("waiting for tasks");
   }

   public void flush() {
      this.shutdown = true;
      this.thread.interrupt();

      try {
         this.thread.join();
      } catch (InterruptedException interruptedexception) {
         Thread.currentThread().interrupt();
      }

      this.dropAllTasks();
      this.shutdown = false;
      this.thread = this.createThread();
   }
}